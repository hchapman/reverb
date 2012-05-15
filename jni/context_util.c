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

#include "logging.h"
#include "context_util.h"

extern jclass jcls_context;

jni_pa_cb_info_t *new_cbinfo(JNIEnv *jenv, jobject jcontext, jobject jcb,
		pa_threaded_mainloop *m, void *to_free) {
	jni_pa_cb_info_t *cbinfo = (jni_pa_cb_info_t*)malloc(sizeof(jni_pa_cb_info_t));
	if (jcb != NULL) {
		cbinfo->cb_runnable = get_cb_globalref(jenv, jcontext, jcb);
	} else {
		cbinfo->cb_runnable = NULL;
	}
	cbinfo->m = m;
	cbinfo->to_free = to_free;

	return cbinfo;
}

jni_pa_event_cbs_t *new_event_cbs() {
	jni_pa_event_cbs_t *cbs = (jni_pa_event_cbs_t *)malloc(sizeof(jni_pa_event_cbs_t));
	cbs->sink_input_cbo = NULL;
	cbs->sink_cbo = NULL;
	cbs->source_output_cbo = NULL;
	cbs->source_cbo = NULL;

	return cbs;
}

jni_pa_state_cbs_t *new_state_cbs() {
	jni_pa_state_cbs_t *cbs = (jni_pa_state_cbs_t *)malloc(sizeof(jni_pa_state_cbs_t));
	cbs->unconnected_cbo = NULL;
	cbs->connecting_cbo = NULL;
	cbs->authorizing_cbo = NULL;
	cbs->setting_name_cbo = NULL;
	cbs->ready_cbo = NULL;
	cbs->failed_cbo = NULL;
	cbs->terminated_cbo = NULL;

	return cbs;
}

pa_context *get_context_ptr(JNIEnv *jenv, jobject jcontext) {
	jfieldID fid = (*jenv)->GetFieldID(jenv, jcls_context, "mPointer", "J");
	if (fid == NULL)
		return;

	return (*jenv)->GetLongField(jenv, jcontext, fid);
}

pa_threaded_mainloop *get_mainloop_ptr(JNIEnv *jenv, jobject jcontext) {
	jmethodID mid = (*jenv)->GetMethodID(jenv, jcls_context, "getMainloopPointer", "()J");
	if (mid == NULL) {
		LOGE("There was an error getting the mainloop pointer method ID");
		return NULL;
	}

	return (*jenv)->CallLongMethod(jenv, jcontext, mid);
}

jni_pa_event_cbs_t *get_event_cbs_ptr(JNIEnv *jenv, jobject jcontext) {
	jmethodID mid = (*jenv)->GetMethodID(jenv, jcls_context, "getEventCbsPointer", "()J");
	if (mid == NULL) {
		LOGE("There was an error getting the event pointer method ID");
		return NULL;
	}

	return (*jenv)->CallLongMethod(jenv, jcontext, mid);
}

jni_pa_state_cbs_t *get_state_cbs_ptr(JNIEnv *jenv, jobject jcontext) {
	jmethodID mid = (*jenv)->GetMethodID(jenv, jcls_context, "getStateCbsPointer", "()J");
	if (mid == NULL) {
		LOGE("There was an error getting the state pointer method ID");
		return NULL;
	}

	return (*jenv)->CallLongMethod(jenv, jcontext, mid);
}

void set_event_cbs_ptr(JNIEnv *jenv, jobject jcontext, jni_pa_event_cbs_t *cbs) {
	jmethodID mid = (*jenv)->GetMethodID(jenv, jcls_context, "setEventCbsPointer", "(J)V");
	if (mid == NULL) {
		LOGE("There was an error getting the event cb pointer method ID");
		return;
	}

	(*jenv)->CallVoidMethod(jenv, jcontext, mid, (jlong)cbs);
}

void set_state_cbs_ptr(JNIEnv *jenv, jobject jcontext, jni_pa_state_cbs_t *cbs) {
	jmethodID mid = (*jenv)->GetMethodID(jenv, jcls_context, "setStateCbsPointer", "(J)V");
	if (mid == NULL) {
		LOGE("There was an error getting the state cb pointer method ID");
		return;
	}

	(*jenv)->CallVoidMethod(jenv, jcontext, mid, (jlong)cbs);
}

void set_cb_context(JNIEnv *jenv, jobject jcb, jobject jcontext) {
	jclass jcls = (*jenv)->GetObjectClass(jenv, jcb);
	jmethodID mid = (*jenv)->GetMethodID(jenv, jcls,
			"setContext", "(Lcom/harrcharr/reverb/pulse/PulseContext;)V");
	if (mid == NULL) {
		LOGE("There was an error getting the context set method ID");
		return;
	}

	(*jenv)->CallVoidMethod(jenv, jcb, mid, jcontext);
}

void context_synchronized_info_call(
		JNIEnv *jenv, jobject jcontext, jobject jcb,
		pa_context_get_info_t get_info, uint32_t idx,
		void (*cb)) {
	set_cb_context(jenv, jcb, jcontext);

	LOGD("NATIVE: sync_info_call - start");
	pa_context *c = get_context_ptr(jenv, jcontext);
	assert(c);
	pa_threaded_mainloop *m = get_mainloop_ptr(jenv, jcontext);
	assert(m);

	pa_threaded_mainloop_lock(m);

	pa_operation *o;
	LOGD("NATIVE: sync_info_call - pointers ready");

	jni_pa_cb_info_t *cbinfo = new_cbinfo(jenv, jcontext, jcb, m, NULL);

	o = get_info(c, idx, cb, cbinfo);
	assert(o);

	pa_operation_unref(o);
	pa_threaded_mainloop_unlock(m);
}

void context_synchronized_info_list_call(
		JNIEnv *jenv, jobject jcontext, jobject jcb,
		pa_context_get_info_list_t get_info_list,
		void (*cb)) {
	set_cb_context(jenv, jcb, jcontext);

	LOGD("NATIVE: sync_info_call - start");
	pa_context *c = get_context_ptr(jenv, jcontext);
	assert(c);
	pa_threaded_mainloop *m = get_mainloop_ptr(jenv, jcontext);
	assert(m);

	pa_threaded_mainloop_lock(m);

	pa_operation *o;
	LOGD("NATIVE: sync_info_call - pointers ready");

	jni_pa_cb_info_t *cbinfo = new_cbinfo(jenv, jcontext, jcb, m, NULL);

	o = get_info_list(c, cb, cbinfo);
	assert(o);

	pa_operation_unref(o);
	pa_threaded_mainloop_unlock(m);
}

void context_synchronized_mute_call(
		JNIEnv *jenv, jobject jcontext, jobject jcb,
		pa_context_set_mute_t set_mute, uint32_t idx,
		int mute, void (*cb)) {
	LOGD("NATIVE: sync_mute_call - start");
	pa_context *c = get_context_ptr(jenv, jcontext);
	assert(c);
	pa_threaded_mainloop *m = get_mainloop_ptr(jenv, jcontext);
	assert(m);

	pa_threaded_mainloop_lock(m);

	pa_operation *o;
	LOGD("NATIVE: sync_mute_call - pointers ready");

	jni_pa_cb_info_t *cbinfo = new_cbinfo(jenv, jcontext, jcb, m, NULL);

	o = set_mute(c, idx, mute, cb, cbinfo);
	assert(o);

	pa_operation_unref(o);
	pa_threaded_mainloop_unlock(m);
}

void context_synchronized_volume_call(
		JNIEnv *jenv, jobject jcontext, jobject jcb,
		pa_context_set_volume_t set_volume, uint32_t idx,
		jintArray volumes, void (*cb)) {
	LOGD("NATIVE: sync_mute_call - start");
	pa_context *c = get_context_ptr(jenv, jcontext);
	assert(c);
	pa_threaded_mainloop *m = get_mainloop_ptr(jenv, jcontext);
	assert(m);

	pa_threaded_mainloop_lock(m);

	pa_cvolume *v = (pa_cvolume *)malloc(sizeof(pa_cvolume));
	pa_cvolume_init(v);
	pa_cvolume_set(v, 2, PA_VOLUME_NORM);

	(*jenv)->GetIntArrayRegion(jenv, volumes, 0,
			(*jenv)->GetArrayLength(jenv, volumes), &(v->values));

	pa_operation *o;

	LOGD("NATIVE: sync_mute_call - pointers ready");

	jni_pa_cb_info_t *cbinfo = new_cbinfo(jenv, jcontext, jcb, m, v);

	o = set_volume(c, idx, v, cb, cbinfo);
	assert(o);

	pa_operation_unref(o);
	pa_threaded_mainloop_unlock(m);
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

void call_state_run(jobject runnable) {
	JNIEnv *jenv;
	jclass cls;
	jmethodID mid;
	jenv_status_t status;

	if ((status = get_jnienv(&jenv)) == JENV_UNSUCCESSFUL) {
		return;
	}

	if ((cls = (*jenv)->GetObjectClass(jenv, runnable))) {
		// For a SubscriptionCallback, our parameters are (int event, int idx)
		if ((mid = (*jenv)->GetMethodID(jenv, cls, "run", "()V"))) {
			// Run the actual Java callback method
			(*jenv)->CallVoidMethod(jenv, runnable, mid);
		}
	}

	detach_jnienv(status);
}

void context_state_cb(pa_context* c, void *userdata) {
	jni_pa_state_cbs_t *cbs = (jni_pa_state_cbs_t *)userdata;

    switch (pa_context_get_state(c)) {
        case PA_CONTEXT_UNCONNECTED:
        	if (cbs->unconnected_cbo != NULL)
        		call_state_run(cbs->unconnected_cbo);
            break;

        case PA_CONTEXT_CONNECTING:
        	if (cbs->connecting_cbo != NULL)
        		call_state_run(cbs->connecting_cbo);
        	break;

        case PA_CONTEXT_AUTHORIZING:
        	if (cbs->authorizing_cbo != NULL)
        		call_state_run(cbs->authorizing_cbo);
        	break;

        case PA_CONTEXT_SETTING_NAME:
        	if (cbs->setting_name_cbo != NULL)
        		call_state_run(cbs->setting_name_cbo);
            break;

        case PA_CONTEXT_READY:
        	if (cbs->ready_cbo != NULL)
        		call_state_run(cbs->ready_cbo);
            break;

        case PA_CONTEXT_FAILED:
        	if (cbs->failed_cbo != NULL)
        		call_state_run(cbs->failed_cbo);
            break;

        case PA_CONTEXT_TERMINATED:
        	if (cbs->terminated_cbo != NULL)
        		call_state_run(cbs->terminated_cbo);
            break;
    }
}

void info_cb(pa_context* c, const void *i,
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


	if ((cls = (*env)->GetObjectClass(env, cbdata->cb_runnable))) {
		if ((mid = (*env)->GetMethodID(env, cls, "run", "(J)V"))) {
			// Run the actual Java callback method
			(*env)->CallVoidMethod(env, cbdata->cb_runnable, mid, (jlong)i);
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
