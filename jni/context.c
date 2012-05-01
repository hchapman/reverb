#include <jni.h>
#include <pulse/pulseaudio.h>

#include "jni_core.h"
#include "context.h"
#include "logging.h"

extern jclass jcls_context;

// A structure holding (global) references to runnables, per event type
typedef struct jni_pa_event_cbs {
	jobject sink_input_cbo;
	jobject sink_cbo;
} jni_pa_event_cbs_t ;

void call_subscription_run(pa_subscription_event_type_t t, uint32_t idx, jobject runnable) {
	JNIEnv *env;
	jclass cls;
	jmethodID mid;
	jenv_status_t status;

	if ((status = get_jnienv(&env)) == JENV_UNSUCCESSFUL) {
		return;
	}

	if ((cls = (*env)->GetObjectClass(env, runnable))) {
		// For a SubscriptionCallback, our parameters are (int event, int idx)
		if ((mid = (*env)->GetMethodID(env, cls, "run", "(II)V"))) {
			// Run the actual Java callback method
			(*env)->CallVoidMethod(env, runnable, mid, (jint)t, (jint)idx);
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
	jmethodID mid;
	jenv_status_t status;

	if ((status = get_jnienv(&env)) == JENV_UNSUCCESSFUL) {
		return;
	}

	if (jcls_context) {
		if ((mid = (*env)->GetStaticMethodID(env, jcls_context,
				"statusChanged", "(JI)V"))) {
			// Run the actual Java callback method
			(*env)->CallStaticVoidMethod(env, jcls_context, mid, (jlong)c,
					(jint)pa_context_get_state(c));
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
	    (*env)->DeleteGlobalRef(env, cbdata->cb_runnable);
	    free(cbdata);
	    detach_jnienv(status);
	    return;
	}

	if (eol > 0) {
		pa_threaded_mainloop_signal(m, 0);
	    (*env)->DeleteGlobalRef(env, cbdata->cb_runnable);
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

void sink_input_info_cb(pa_context* c, const pa_sink_info *i,
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
	    (*env)->DeleteGlobalRef(env, cbdata->cb_runnable);
	    free(cbdata);
	    detach_jnienv(status);
	    return;
	}

	if (eol > 0) {
		pa_threaded_mainloop_signal(m, 0);
	    (*env)->DeleteGlobalRef(env, cbdata->cb_runnable);
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
	    (*env)->DeleteGlobalRef(env, cbdata->cb_runnable);
	    free(cbdata);
	    detach_jnienv(status);
	    return;
	}

	if (eol > 0) {
		pa_threaded_mainloop_signal(m, 0);
	    (*env)->DeleteGlobalRef(env, cbdata->cb_runnable);
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

JNIEXPORT jlong JNICALL
Java_com_harrcharr_reverb_pulse_PulseContext_JNICreate(
		JNIEnv *jenv, jclass jcls, pa_threaded_mainloop *m) {
	dlog(0, "%d", m);
	pa_mainloop_api *api = pa_threaded_mainloop_get_api(m);
	pa_context *c = pa_context_new(api, "primary");

	pa_context_set_state_callback(c, context_state_cb, m);

	dlog(0, "hello from c!");
	dlog(0, "%d", c);

	return c;
//	context_ptr_to_jobject(jenv, jobj, c);
}

JNIEXPORT jint JNICALL
Java_com_harrcharr_reverb_pulse_PulseContext_JNIConnect(
		JNIEnv *jenv, jclass jcls, jlong ptr_c, jstring server) {
//	pa_threaded_mainloop *m = (pa_threaded_mainloop*)ptr_mainloop;
	pa_context *c = (pa_context *)ptr_c;

	dlog(0, "%d", c);

    char buf[128];
    const jbyte *srv;
    srv = (*jenv)->GetStringUTFChars(jenv, server, NULL);
    if (srv == NULL) {
        return NULL; /* OutOfMemoryError already thrown */
    }
    printf("%s", srv);
	int result = pa_context_connect(c, srv, PA_CONTEXT_NOFAIL, NULL);
    (*jenv)->ReleaseStringUTFChars(jenv, server, srv);
    /* We assume here that the user does not type more than
     * 127 characters */
    scanf("%s", buf);

	dlog(0, "connection result %d", result);
	dlog(0, pa_context_get_server(c));

	while(pa_context_is_pending(c) != 0) {
//		dlog(0, "%i", pa_context_get_state(c));
	}

	while(pa_context_get_state(c) != PA_CONTEXT_READY) {
//		dlog(0, "%i", pa_context_get_state(c));
	}

	dlog(0, "holy shit");
	dlog(0, "%d", pa_context_get_state(c));

	return result;
}

JNIEXPORT void JNICALL
Java_com_harrcharr_reverb_pulse_PulseContext_JNIGetSinkInfoByIndex(
		JNIEnv *jenv, jclass jcls, jlong c_ptr, jlong m_ptr, jint idx,
		jobject runnable) {
	pa_context *c = (pa_context *)c_ptr;
	pa_threaded_mainloop *m = (pa_threaded_mainloop *)m_ptr;
	pa_threaded_mainloop_lock(m);

	pa_operation *o;
	dlog(0, "About to get sink info %d", m);

	jni_pa_cb_info_t *cbinfo = (jni_pa_cb_info_t*)malloc(sizeof(jni_pa_cb_info_t));
	cbinfo->cb_runnable = (*jenv)->NewGlobalRef(jenv, runnable);
	cbinfo->m = m;
	o = pa_context_get_sink_info_by_index(c, (int)idx, sink_info_cb, cbinfo);
	assert(o);
	dlog(0, "Sink info call is a go!");
//	while (pa_operation_get_state(o) == PA_OPERATION_RUNNING) {
//		dlog(0, "Waiting for the mainloop in sink info!");
//		pa_threaded_mainloop_wait(m);
//	}
	dlog(0, "Mainloop is done waiting");
	pa_operation_unref(o);
	pa_threaded_mainloop_unlock(m);
}

JNIEXPORT void JNICALL
Java_com_harrcharr_reverb_pulse_PulseContext_JNIGetSinkInputInfoList(
		JNIEnv *jenv, jclass jcls, jlong c_ptr, jlong m_ptr,
		jobject runnable) {
	pa_context *c = (pa_context *)c_ptr;
	pa_threaded_mainloop *m = (pa_threaded_mainloop *)m_ptr;
	pa_threaded_mainloop_lock(m);

	pa_operation *o;
	dlog(0, "About to get sink info %d", m);

	jni_pa_cb_info_t *cbinfo = (jni_pa_cb_info_t*)malloc(sizeof(jni_pa_cb_info_t));
	cbinfo->cb_runnable = (*jenv)->NewGlobalRef(jenv, runnable);
	cbinfo->m = m;
	o = pa_context_get_sink_input_info_list(c, sink_input_info_cb, cbinfo);
	assert(o);
	dlog(0, "Sink info call is a go!");
//	while (pa_operation_get_state(o) == PA_OPERATION_RUNNING) {
//		dlog(0, "Waiting for the mainloop in sink info!");
//		pa_threaded_mainloop_wait(m);
//	}
	dlog(0, "Mainloop is done waiting");
	pa_operation_unref(o);
	pa_threaded_mainloop_unlock(m);
}

JNIEXPORT void JNICALL
Java_com_harrcharr_reverb_pulse_PulseContext_JNIGetSinkInputInfo(
		JNIEnv *jenv, jclass jcls, jlong c_ptr, jlong m_ptr, jint idx,
		jobject runnable) {
	pa_context *c = (pa_context *)c_ptr;
	pa_threaded_mainloop *m = (pa_threaded_mainloop *)m_ptr;
	pa_threaded_mainloop_lock(m);

	pa_operation *o;
	dlog(0, "About to get sink info %d", m);

	jni_pa_cb_info_t *cbinfo = (jni_pa_cb_info_t*)malloc(sizeof(jni_pa_cb_info_t));
	cbinfo->cb_runnable = (*jenv)->NewGlobalRef(jenv, runnable);
	cbinfo->m = m;
	o = pa_context_get_sink_input_info(c, (int)idx, sink_input_info_cb, cbinfo);
	assert(o);
	dlog(0, "Sink info call is a go!");
//	while (pa_operation_get_state(o) == PA_OPERATION_RUNNING) {
//		dlog(0, "Waiting for the mainloop in sink info!");
//		pa_threaded_mainloop_wait(m);
//	}
	dlog(0, "Mainloop is done waiting");
	pa_operation_unref(o);
	pa_threaded_mainloop_unlock(m);
}


JNIEXPORT void JNICALL
Java_com_harrcharr_reverb_pulse_PulseContext_JNISetSinkInputMuteByIndex(
		JNIEnv *jenv, jclass jcls, jlong c_ptr, jlong m_ptr, jint idx, jboolean mute,
		jobject runnable) {
	pa_context *c = (pa_context *)c_ptr;
	pa_threaded_mainloop *m = (pa_threaded_mainloop *)m_ptr;
	pa_threaded_mainloop_lock(m);

	pa_operation *o;
	dlog(0, "About to get sink mute %d", m);
	o = pa_context_set_sink_input_mute(c, (uint32_t)idx, (int)mute, NULL, m);
	assert(o);
	dlog(0, "Sink mute call is a go!");
//	while (pa_operation_get_state(o) == PA_OPERATION_RUNNING) {
//		pa_threaded_mainloop_wait(m);
//	}
	dlog(0, "Mainloop is done waiting");
	pa_operation_unref(o);
	pa_threaded_mainloop_unlock(m);
}

JNIEXPORT void JNICALL
Java_com_harrcharr_reverb_pulse_PulseContext_JNIGetClientInfo(
		JNIEnv *jenv, jclass jcls, jlong c_ptr, jlong m_ptr, jint idx,
		jobject runnable) {
	pa_context *c = (pa_context *)c_ptr;
	pa_threaded_mainloop *m = (pa_threaded_mainloop *)m_ptr;
	pa_threaded_mainloop_lock(m);

	pa_operation *o;
	dlog(0, "About to get sink info %d", m);

	jni_pa_cb_info_t *cbinfo = (jni_pa_cb_info_t*)malloc(sizeof(jni_pa_cb_info_t));
	cbinfo->cb_runnable = (*jenv)->NewGlobalRef(jenv, runnable);
	cbinfo->m = m;
	o = pa_context_get_client_info(c, (int)idx, client_info_cb, cbinfo);
	assert(o);
	dlog(0, "Sink info call is a go!");
//	while (pa_operation_get_state(o) == PA_OPERATION_RUNNING) {
//		dlog(0, "Waiting for the mainloop in sink info!");
//		pa_threaded_mainloop_wait(m);
//	}
	dlog(0, "Mainloop is done waiting");
	pa_operation_unref(o);
	pa_threaded_mainloop_unlock(m);
}

JNIEXPORT void JNICALL
Java_com_harrcharr_reverb_pulse_PulseContext_JNIGetClientInfoList(
		JNIEnv *jenv, jclass jcls, jlong c_ptr, jlong m_ptr,
		jobject runnable) {
	pa_context *c = (pa_context *)c_ptr;
	pa_threaded_mainloop *m = (pa_threaded_mainloop *)m_ptr;
	pa_threaded_mainloop_lock(m);

	pa_operation *o;
	dlog(0, "About to get sink info %d", m);

	jni_pa_cb_info_t *cbinfo = (jni_pa_cb_info_t*)malloc(sizeof(jni_pa_cb_info_t));
	cbinfo->cb_runnable = (*jenv)->NewGlobalRef(jenv, runnable);
	cbinfo->m = m;
	o = pa_context_get_client_info_list(c, client_info_cb, cbinfo);
	assert(o);
	dlog(0, "Sink info call is a go!");
//	while (pa_operation_get_state(o) == PA_OPERATION_RUNNING) {
//		dlog(0, "Waiting for the mainloop in sink info!");
//		pa_threaded_mainloop_wait(m);
//	}
	dlog(0, "Mainloop is done waiting");
	pa_operation_unref(o);
	pa_threaded_mainloop_unlock(m);
}

JNIEXPORT void JNICALL
Java_com_harrcharr_reverb_pulse_PulseContext_JNISetSinkMuteByIndex(
		JNIEnv *jenv, jclass jcls, jlong c_ptr, jlong m_ptr, jint idx, jboolean mute) {
	pa_context *c = (pa_context *)c_ptr;
	pa_threaded_mainloop *m = (pa_threaded_mainloop *)m_ptr;
	pa_threaded_mainloop_lock(m);

	pa_operation *o;
	dlog(0, "About to get sink mute %d", m);
	o = pa_context_set_sink_mute_by_index(c, (uint32_t)idx, (int)mute, NULL, m);
	assert(o);
	dlog(0, "Sink mute call is a go!");
//	while (pa_operation_get_state(o) == PA_OPERATION_RUNNING) {
//		pa_threaded_mainloop_wait(m);
//	}
	dlog(0, "Mainloop is done waiting");
	pa_operation_unref(o);
	pa_threaded_mainloop_unlock(m);
}

JNIEXPORT jlong JNICALL
Java_com_harrcharr_reverb_pulse_PulseContext_JNISubscribe(
		JNIEnv *jenv, jclass jcls, jlong c_ptr, jlong m_ptr) {
	pa_context *c = (pa_context *)c_ptr;
	pa_threaded_mainloop *m = (pa_threaded_mainloop *)m_ptr;
	pa_threaded_mainloop_lock(m);

	pa_operation *o;
	o = pa_context_subscribe(c, PA_SUBSCRIPTION_MASK_ALL, NULL, NULL);
	assert(o);

	pa_operation_unref(o);
	pa_threaded_mainloop_unlock(m);

	jni_pa_event_cbs_t *cbs = (jni_pa_event_cbs_t *)malloc(sizeof(jni_pa_event_cbs_t));
	cbs->sink_input_cbo = NULL;
	pa_context_set_subscribe_callback(c, context_subscription_cb, cbs);

	return (jlong)cbs;
}

JNIEXPORT void JNICALL
Java_com_harrcharr_reverb_pulse_PulseContext_JNISubscribeSinkInput(
		JNIEnv *jenv, jclass jcls, jlong c_ptr, jlong cbo_ptr, jobject runnable) {
	pa_context *c = (pa_context *)c_ptr;
	jni_pa_event_cbs_t *cbs = (jni_pa_event_cbs_t *)cbo_ptr;

	if (cbs->sink_input_cbo != NULL) {
		(*jenv)->DeleteGlobalRef(jenv, cbs->sink_input_cbo);
	}
	cbs->sink_input_cbo = (*jenv)->NewGlobalRef(jenv, runnable);
}
