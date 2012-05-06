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

void sink_input_info_cb(pa_context* c, const pa_sink_input_info *i,
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

	(*env)->DeleteGlobalRef(env, cbdata->cb_runnable);
	free(cbdata);
}

JNIEXPORT jlong JNICALL
Java_com_harrcharr_reverb_pulse_PulseContext_JNICreate(
		JNIEnv *jenv, jclass jcls, pa_threaded_mainloop *m) {
	dlog(0, "%d", m);
	pa_mainloop_api *api = pa_threaded_mainloop_get_api(m);
	pa_context *c = pa_context_new(api, "primary");

	return c;
}

JNIEXPORT jint JNICALL
Java_com_harrcharr_reverb_pulse_PulseContext_connect(
		JNIEnv *jenv, jobject jobj, jstring server) {
	pa_context *c = (pa_context *)get_obj_ptr(jenv, jobj);

    const jbyte *srv;
    srv = (*jenv)->GetStringUTFChars(jenv, server, NULL);
    if (srv == NULL) {
        return NULL; /* OutOfMemoryError already thrown */
    }

	int result = pa_context_connect(c, srv, PA_CONTEXT_NOFAIL, NULL);
    (*jenv)->ReleaseStringUTFChars(jenv, server, srv);

	return result;
}

JNIEXPORT void JNICALL
Java_com_harrcharr_reverb_pulse_PulseContext_disconnect(
		JNIEnv *jenv, jobject jobj) {
	pa_context *c = (pa_context *)get_obj_ptr(jenv, jobj);
	pa_context_disconnect(c);
}

JNIEXPORT jint JNICALL
Java_com_harrcharr_reverb_pulse_PulseContext_getStatus(
		JNIEnv *jenv, jobject jobj) {
	pa_context *c = (pa_context *)get_obj_ptr(jenv, jobj);
	return pa_context_get_state(c);
}

JNIEXPORT void JNICALL
Java_com_harrcharr_reverb_pulse_PulseContext_setStateCallback(
		JNIEnv *jenv, jobject jobj, jobject runnable) {
	pa_context *c = (pa_context *)get_obj_ptr(jenv, jobj);

	jni_pa_cb_info_t *cbinfo = (jni_pa_cb_info_t*)malloc(sizeof(jni_pa_cb_info_t));
	cbinfo->cb_runnable = (*jenv)->NewGlobalRef(jenv, runnable);
	cbinfo->m = NULL;
	cbinfo->to_free = NULL;

	pa_context_set_state_callback(c, context_state_cb, cbinfo);
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
	cbinfo->to_free = NULL;
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
	cbinfo->to_free = NULL;
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
	cbinfo->to_free = NULL;
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
Java_com_harrcharr_reverb_pulse_PulseContext_JNISetSinkInputVolumeByIndex(
		JNIEnv *jenv, jclass jcls, jlong c_ptr, jlong m_ptr, jint idx, jintArray volumes,
		jobject runnable) {
	pa_context *c = (pa_context *)c_ptr;
	pa_threaded_mainloop *m = (pa_threaded_mainloop *)m_ptr;
	pa_threaded_mainloop_lock(m);
	pa_cvolume *v = (pa_cvolume *)malloc(sizeof(pa_cvolume));
	pa_cvolume_init(v);
	pa_cvolume_set(v, 2, PA_VOLUME_NORM);
	char *s = malloc(sizeof(char[500]));
	LOGD(pa_cvolume_snprint(s, 500, v));
	LOGD("%d... %d", v->values, v->values[0]);
	(*jenv)->GetIntArrayRegion(jenv, volumes, 0, (*jenv)->GetArrayLength(jenv, volumes), &(v->values));

	pa_operation *o;
	dlog(0, "About to set sink volume %d, len %d", v, (*jenv)->GetArrayLength(jenv, volumes));
	LOGD("Volume is %d", pa_cvolume_valid(v));
	LOGD(pa_cvolume_snprint(s, 500, v));
	LOGD("%d... %d", v->values, v->values[0]);

	jni_pa_cb_info_t *cbinfo = (jni_pa_cb_info_t*)malloc(sizeof(jni_pa_cb_info_t));
	if (runnable != NULL) {
		cbinfo->cb_runnable = (*jenv)->NewGlobalRef(jenv, runnable);
	} else {
		cbinfo->cb_runnable = NULL;
	}
	cbinfo->m = m;
	cbinfo->to_free = v;
	dlog(0, "Sink volume info is prepared! Want to free %d", cbinfo->to_free);
	o = pa_context_set_sink_input_volume(c, (uint32_t)idx, v, success_cb, cbinfo);
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
