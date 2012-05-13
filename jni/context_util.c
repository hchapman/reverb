/*******************************************************************************
*Copyright (c) 2012 Harrison Chapman.
*
*This file is part of Reverb.
*
*    Reverb is free software: you can redistribute it and/or modify
*    it under the terms of the GNU General Public License as published by
*    the Free Software Foundation, either version 2 of the License, or
*    (at your option) any later version.
*
*    Reverb is distributed in the hope that it will be useful,
*    but WITHOUT ANY WARRANTY; without even the implied warranty of
*    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
*    GNU General Public License for more details.
*
*    You should have received a copy of the GNU General Public License
*    along with Reverb.  If not, see <http://www.gnu.org/licenses/>.
*
*Contributors:
*    Harrison Chapman - initial API and implementation
*******************************************************************************/

#include "context_util.h"

extern jclass jcls_context;

jni_pa_cb_info_t *new_cbinfo(JNIEnv *jenv, jobject jcontext, jobject jcb,
		pa_threaded_mainloop *m, void *to_free) {
	jni_pa_cb_info_t *cbinfo = (jni_pa_cb_info_t*)malloc(sizeof(jni_pa_cb_info_t));
	cbinfo->cb_runnable = get_cb_globalref(jenv, jcontext, jcb);
	cbinfo->m = m;
	cbinfo->to_free = to_free;

	return cbinfo;
}

/*
 * Get a new global reference, saving knowledge of it in the context (for freeing)
 *
 */
jobject get_cb_globalref(JNIEnv *jenv, jobject c, jobject ref) {
	jobject global = (*jenv)->NewGlobalRef(jenv, ref);
	LOGD("About to get method id to store our ptr");
	jclass cls = (*jenv)->GetObjectClass(jenv, ref);
	LOGD("About to get method id to store our ptr 2");
	jfieldID mid = (*jenv)->GetMethodID(jenv, cls, "storeGlobal", "(Lcom/harrcharr/reverb/pulse/PulseContext;J)V");

	if (mid == NULL)
		return; // We're in trouble

	(*jenv)->CallVoidMethod(jenv, ref, mid, c, (jlong)global);
	LOGD("Ptr stored");

	return global;
}

void del_cb_globalref(JNIEnv *jenv, jobject gref) {
	jclass cls = (*jenv)->GetObjectClass(jenv, gref);
	jfieldID mid = (*jenv)->GetMethodID(jenv, cls, "unstoreGlobal", "()V");

	if (mid != NULL)
		return; // We're probably already in a memory leak situation

	(*jenv)->CallVoidMethod(jenv, gref, mid);

	(*jenv)->DeleteGlobalRef(jenv, gref);
}

void call_subscription_run(pa_subscription_event_type_t t, uint32_t idx, jobject runnable) {
	JNIEnv *jenv;
	jclass cls;
	jmethodID mid;
	jenv_status_t status;

	if ((status = get_jnienv(&jenv)) == JENV_UNSUCCESSFUL) {
		return;
	}

	if ((cls = (*jenv)->GetObjectClass(jenv, runnable))) {
		// For a SubscriptionCallback, our parameters are (int event, int idx)
		if ((mid = (*jenv)->GetMethodID(jenv, cls, "run", "(II)V"))) {
			// Run the actual Java callback method
			(*jenv)->CallVoidMethod(jenv, runnable, mid, (jint)t, (jint)idx);
		}
	}

	detach_jnienv(status);
}

void context_subscription_cb(pa_context* c, pa_subscription_event_type_t t,
		uint32_t idx, void *userdata) {
	jni_pa_event_cbs_t *cbs = (jni_pa_event_cbs_t *)userdata;

    switch (t & PA_SUBSCRIPTION_EVENT_FACILITY_MASK) {
        case PA_SUBSCRIPTION_EVENT_SINK:
//            if ((t & PA_SUBSCRIPTION_EVENT_TYPE_MASK) == PA_SUBSCRIPTION_EVENT_REMOVE)
//                w->removeSink(index);
//            else {
//                pa_operation *o;
//                if (!(o = pa_context_get_sink_info_by_index(c, index, sink_cb, w))) {
//                    show_error(_("pa_context_get_sink_info_by_index() failed"));
//                    return;
//                }
//                pa_operation_unref(o);
//            }
            break;

        case PA_SUBSCRIPTION_EVENT_SOURCE:
        	break;

        case PA_SUBSCRIPTION_EVENT_SINK_INPUT:
        	LOGD("Remove enum is %d", PA_SUBSCRIPTION_EVENT_REMOVE);

        	if (cbs->sink_input_cbo != NULL)
        		call_subscription_run(t & PA_SUBSCRIPTION_EVENT_TYPE_MASK,
        				idx, cbs->sink_input_cbo);
            break;

        case PA_SUBSCRIPTION_EVENT_SOURCE_OUTPUT:
            break;

        case PA_SUBSCRIPTION_EVENT_CLIENT:
            break;

        case PA_SUBSCRIPTION_EVENT_SERVER:
            break;

        case PA_SUBSCRIPTION_EVENT_CARD:
            break;
    }
}

void context_state_cb(pa_context* c, void* userdata) {

	JNIEnv *env;
	jclass cls;
	jmethodID mid;
	jenv_status_t status;

	if ((status = get_jnienv(&env)) == JENV_UNSUCCESSFUL) {
		return;
	}

	jni_pa_cb_info_t *cbdata = (jni_pa_cb_info_t*)userdata;

	if ((cls = (*env)->GetObjectClass(env, cbdata->cb_runnable))) {
		if ((mid = (*env)->GetMethodID(env, cls, "run", "()V"))) {
			// Run the actual Java callback method
			(*env)->CallVoidMethod(env, cbdata->cb_runnable, mid);
		}
	}

	detach_jnienv(status);
}

void sink_info_cb(pa_context* c, const pa_sink_info *i,
		int eol, void *userdata) {
	JNIEnv *env;
	jclass cls;
	jmethodID mid;
	jenv_status_t status;

	if ((status = get_jnienv(&env)) == JENV_UNSUCCESSFUL) {
		return;
	}

	jni_pa_cb_info_t *cbdata = (jni_pa_cb_info_t*)userdata;

    pa_threaded_mainloop *m = cbdata->m;
    assert(m);

	if (eol < 0) {
		LOGE("Error returned from a sink info query");
	    pa_threaded_mainloop_signal(m, 0);
	    del_cb_globalref(env, cbdata->cb_runnable);
	    free(cbdata);
	    detach_jnienv(status);
	    return;
	}

	if (eol > 0) {
		pa_threaded_mainloop_signal(m, 0);
		del_cb_globalref(env, cbdata->cb_runnable);
	    free(cbdata);
	    detach_jnienv(status);
	    return;
	}

	if ((cls = (*env)->GetObjectClass(env, cbdata->cb_runnable))) {
		if ((mid = (*env)->GetMethodID(env, cls, "run", "(IJ)V"))) {
			// Run the actual Java callback method
			(*env)->CallVoidMethod(env, cbdata->cb_runnable, mid, (jint)i->index, (jlong)i);
		}
	}

	detach_jnienv(status);
	pa_threaded_mainloop_signal(m, 0);

}

void sink_input_info_cb(pa_context* c, const pa_sink_input_info *i,
		int eol, void *userdata) {
	JNIEnv *env;
	jclass cls;
	jmethodID mid;
	jenv_status_t status;

	LOGD("In sink input info callback");

	if ((status = get_jnienv(&env)) == JENV_UNSUCCESSFUL) {
		return;
	}

	jni_pa_cb_info_t *cbdata = (jni_pa_cb_info_t*)userdata;

    pa_threaded_mainloop *m = cbdata->m;
    assert(m);

	if (eol < 0) {
		LOGE("Error returned from a sink info query");
	    pa_threaded_mainloop_signal(m, 0);
	    del_cb_globalref(env, cbdata->cb_runnable);
	    free(cbdata);
	    detach_jnienv(status);
	    return;
	}

	if (eol > 0) {
		LOGD("About to delete our runnable");
		pa_threaded_mainloop_signal(m, 0);
		del_cb_globalref(env, cbdata->cb_runnable);
	    free(cbdata);
	    detach_jnienv(status);
	    return;
	}

	LOGD("C index %d", 	i->index);

	if ((cls = (*env)->GetObjectClass(env, cbdata->cb_runnable))) {
		if ((mid = (*env)->GetMethodID(env, cls, "run", "(IJ)V"))) {
			// Run the actual Java callback method
			(*env)->CallVoidMethod(env, cbdata->cb_runnable, mid, (jint)(i->index), (jlong)i);
		}
	}

	detach_jnienv(status);
	pa_threaded_mainloop_signal(m, 0);

}

void client_info_cb(pa_context* c, const pa_sink_info *i,
		int eol, void *userdata) {
	JNIEnv *env;
	jclass cls;
	jmethodID mid;
	jenv_status_t status;

	if ((status = get_jnienv(&env)) == JENV_UNSUCCESSFUL) {
		return;
	}

	jni_pa_cb_info_t *cbdata = (jni_pa_cb_info_t*)userdata;

    pa_threaded_mainloop *m = cbdata->m;
    assert(m);

	if (eol < 0) {
		LOGE("Error returned from a client info query");
	    pa_threaded_mainloop_signal(m, 0);
	    del_cb_globalref(env, cbdata->cb_runnable);
	    free(cbdata);
	    detach_jnienv(status);
	    return;
	}

	if (eol > 0) {
		pa_threaded_mainloop_signal(m, 0);
		del_cb_globalref(env, cbdata->cb_runnable);
	    free(cbdata);
	    detach_jnienv(status);
	    return;
	}

	if ((cls = (*env)->GetObjectClass(env, cbdata->cb_runnable))) {
		if ((mid = (*env)->GetMethodID(env, cls, "run", "(IJ)V"))) {
			// Run the actual Java callback method
			(*env)->CallVoidMethod(env, cbdata->cb_runnable, mid, (jint)i->index, (jlong)i);
		}
	}

	detach_jnienv(status);
	pa_threaded_mainloop_signal(m, 0);

}

void success_cb(pa_context* c, int success, void *userdata) {
	LOGD("freeing");
	JNIEnv *env;
	jclass cls;
	jmethodID mid;
	jenv_status_t status;

	jni_pa_cb_info_t *cbdata = (jni_pa_cb_info_t*)userdata;

	if(cbdata->to_free != NULL) {
		LOGD("freeing %d", cbdata->to_free);
		free(cbdata->to_free);
		cbdata->to_free = NULL;
	}

	if (cbdata->cb_runnable == NULL) {
		free(cbdata);
		return;
	}

	if ((status = get_jnienv(&env)) == JENV_UNSUCCESSFUL) {
		return;
	}

    pa_threaded_mainloop *m = cbdata->m;
    assert(m);

	if ((cls = (*env)->GetObjectClass(env, cbdata->cb_runnable))) {
		if ((mid = (*env)->GetMethodID(env, cls, "run", "(IJ)V"))) {
			// Run the actual Java callback method
			//(*env)->CallVoidMethod(env, cbdata->cb_runnable, mid, (jint)i->index, (jlong)i);
		}
	}

	detach_jnienv(status);
	pa_threaded_mainloop_signal(m, 0);

	del_cb_globalref(env, cbdata->cb_runnable);
	free(cbdata);
}
