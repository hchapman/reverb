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

#include <jni.h>
#include <pulse/pulseaudio.h>

#include "jni_core.h"
#include "context.h"
#include "logging.h"

#include "context_util.h"

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

    if (result < 0) {
    	// An error occurred during server connection
    	throw_exception(jenv, "java/lang/Exception", pa_strerror(pa_context_errno(c)));
    }
}

JNIEXPORT void JNICALL
Java_com_harrcharr_reverb_pulse_PulseContext_disconnect(
		JNIEnv *jenv, jobject jobj) {
	pa_context *c = (pa_context *)get_obj_ptr(jenv, jobj);
	pa_context_disconnect(c);
	pa_context_unref(c);
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
Java_com_harrcharr_reverb_pulse_PulseContext_getSinkInfoByIndex(
		JNIEnv *jenv, jobject jcontext, jint idx, jobject runnable) {
	context_synchronized_info_call(
			jenv, jcontext, runnable,
			&pa_context_get_sink_info_by_index, (uint32_t)idx,
			info_cb);
}

JNIEXPORT void JNICALL
Java_com_harrcharr_reverb_pulse_PulseContext_getSinkInputInfoList(
		JNIEnv *jenv, jobject jcontext, jobject runnable) {
	context_synchronized_info_list_call(
			jenv, jcontext, runnable,
			&pa_context_get_sink_input_info_list,
			info_cb);
}

JNIEXPORT void JNICALL
Java_com_harrcharr_reverb_pulse_PulseContext_getSinkInputInfo(
		JNIEnv *jenv, jobject jcontext, jint idx,
		jobject runnable) {
	context_synchronized_info_call(
			jenv, jcontext, runnable,
			&pa_context_get_sink_input_info, (uint32_t)idx,
			info_cb);
}


JNIEXPORT void JNICALL
Java_com_harrcharr_reverb_pulse_PulseContext_setSinkInputMuteByIndex(
		JNIEnv *jenv, jobject jcontext, jint idx, jboolean mute,
		jobject runnable) {
	context_synchronized_mute_call(
			jenv, jcontext, runnable,
			&pa_context_set_sink_input_mute, (uint32_t)idx,
			(int) mute, success_cb);
}

JNIEXPORT void JNICALL
Java_com_harrcharr_reverb_pulse_PulseContext_setSinkInputVolumeByIndex(
		JNIEnv *jenv, jobject jcontext,
		jint idx, jintArray volumes,
		jobject runnable) {
	context_synchronized_volume_call(
			jenv, jcontext, runnable,
			&pa_context_set_sink_input_volume, (uint32_t)idx,
			volumes, success_cb);
}

JNIEXPORT void JNICALL
Java_com_harrcharr_reverb_pulse_PulseContext_JNIGetClientInfo(
		JNIEnv *jenv, jobject jcontext, jint idx, jobject runnable) {
	context_synchronized_info_call(
			jenv, jcontext, runnable,
			&pa_context_get_client_info, (uint32_t)idx,
			info_cb);
}

JNIEXPORT void JNICALL
Java_com_harrcharr_reverb_pulse_PulseContext_JNIGetClientInfoList(
		JNIEnv *jenv, jobject jcontext, jobject runnable) {
	context_synchronized_info_list_call(
			jenv, jcontext, runnable,
			&pa_context_get_client_info_list,
			info_cb);
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
		JNIEnv *jenv, jobject cobj, jlong cbo_ptr, jobject runnable) {
	pa_context *c = (pa_context *)get_obj_ptr(jenv, cobj);
	jni_pa_event_cbs_t *cbs = (jni_pa_event_cbs_t *)get_pointer_field(jenv, cobj, "mSubCbPtr");

	if (cbs->sink_input_cbo != NULL) {
		del_cb_globalref(jenv, runnable);
	}
	if (runnable != NULL) {
		cbs->sink_input_cbo = get_cb_globalref(jenv, cobj, runnable);
	}
}
