#include <jni.h>
#include <string.h>
#include <unistd.h>

#include <sys/socket.h>
#include <errno.h>

#include <pulse/pulseaudio.h>

#include "jni_core.h"
#include "pulse.h"
#include "logging.h"

extern JavaVM *g_vm;
extern jclass jclsContext;

static void context_state_cb(pa_context* c, void* userdata) {
	JNIEnv *env;
	jmethodID mid;
	jenv_status_t status;

	if ((status = get_jnienv(&env)) == JENV_UNSUCCESSFUL) {
		return;
	}

	if (mid = (*env)->GetStaticMethodID(env, jclsContext,
			"statusChanged", "(JI)V")) {
		// Run the actual Java callback method
		(*env)->CallStaticVoidMethod(env, jclsContext, mid, (jlong)c,
				(jint)pa_context_get_state(c));
	}

	detach_jnienv(status);
}

void sink_info_cb(pa_context* c, const pa_sink_info *i,
		int eol, void *userdata) {
	jni_pa_cb_info_t *cbdata = (jni_pa_cb_info_t*)userdata;
    pa_threaded_mainloop *m = cbdata->m;
    assert(m);

	if (eol < 0) {
		dlog(0, "Apparently this is an error");
	    pa_threaded_mainloop_signal(m, 0);
	    return;
	}

	JNIEnv *env;
	int status;
	char isAttached = 0;

	status = (*g_vm)->GetEnv(g_vm, (void **) &env, JNI_VERSION_1_6);
	if(status < 0){
		status = (*g_vm)->AttachCurrentThread(g_vm, &env, NULL);
		if(status < 0) {
			return;
		}
		isAttached = 1;
	}

	if (eol > 0) {
	    dlog(0, "We're about to free %d", cbdata);
		pa_threaded_mainloop_signal(m, 0);
	    (*env)->DeleteGlobalRef(env, cbdata->cb_runnable);
	    dlog(0, "Even closer to freein' %d", cbdata);
	    free(cbdata);
	    cbdata = NULL;
		if(isAttached == 1) {
			dlog(0, "detaching");
			(*g_vm)->DetachCurrentThread(g_vm);
		}
	    return;
	}

	jclass cls = (*env)->GetObjectClass(env, cbdata->cb_runnable);
	if (cls == 0) {
		if(isAttached == 1) {
			dlog(0, "detaching");
			(*g_vm)->DetachCurrentThread(g_vm);
		}
		return;
	}
	jmethodID mid = (*env)->GetMethodID(env, cls, "run", "(J)V");
	if (mid == 0) {
		if(isAttached == 1) {
			dlog(0, "detaching");
			(*g_vm)->DetachCurrentThread(g_vm);
		}
		return;
	}

	// Run the actual Java callback method
	(*env)->CallVoidMethod(env, cbdata->cb_runnable, mid, (jlong)i);

	if(isAttached == 1) {
		dlog(0, "detaching");
		(*g_vm)->DetachCurrentThread(g_vm);
	}

	pa_threaded_mainloop_signal(m, 0);

}

inline void setFieldStringHelper(JNIEnv *jenv,
		jobject jobj, jclass cls,
		char *fname, char *data) {
	jfieldID fid = (*jenv)->GetFieldID(jenv, cls, fname, "Ljava/lang/String;");
	if (fid == NULL) {
		return; // Field not found
	}
	jstring jstr = (*jenv)->NewStringUTF(jenv, data);
	if (jstr == NULL) {
		return; // OOM
	}
	(*jenv)->SetObjectField(jenv, jobj, fid, jstr);
}

JNIEXPORT void JNICALL
Java_com_harrcharr_reverb_pulse_SinkInfo_JNIPopulateStruct(
		JNIEnv *jenv, jobject jobj, jlong i_ptr) {
	pa_sink_info *i = (pa_sink_info*)i_ptr;
	jstring jstr;
	jfieldID fid;
	dlog(0, "About to populate structure i ptr %d", i);
	dlog(0, i->description);
	dlog(0, "I'm getting a little closer");
	jclass cls = (*jenv)->GetObjectClass(jenv, jobj);
	setFieldStringHelper(jenv, jobj, cls, "sName", i->name);
	setFieldStringHelper(jenv, jobj, cls, "sDescription", i->description);
}

JNIEXPORT jlong JNICALL
Java_com_harrcharr_reverb_pulse_Mainloop_JNINew(
		JNIEnv *jenv, jclass jcls) {
	pa_threaded_mainloop *m = pa_threaded_mainloop_new();

	return m;
}

JNIEXPORT void JNICALL
Java_com_harrcharr_reverb_pulse_Mainloop_JNIStart(
		JNIEnv *jenv, jclass jcls, jlong ptr_m) {
	pa_threaded_mainloop_start((pa_threaded_mainloop*)ptr_m);
}

JNIEXPORT jlong JNICALL
Java_com_harrcharr_reverb_pulse_Context_JNICreate(
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
Java_com_harrcharr_reverb_pulse_Context_JNIConnect(
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
Java_com_harrcharr_reverb_pulse_Context_JNIGetSinkInfoByIndex(
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
	while (pa_operation_get_state(o) == PA_OPERATION_RUNNING) {
		dlog(0, "Waiting for the mainloop in sink info!");
		pa_threaded_mainloop_wait(m);
	}
	dlog(0, "Mainloop is done waiting");
	pa_operation_unref(o);
	pa_threaded_mainloop_unlock(m);
}

JNIEXPORT void JNICALL
Java_com_harrcharr_reverb_pulse_Context_JNISetSinkMuteByIndex(
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

//	dlog(0, "started", NULL);
//    pa_threaded_mainloop_lock(m);
//    dlog(0, "locked", NULL);
//
//    pa_operation *o;
//
//	o = pa_context_get_sink_info_by_index(c, 0,	sink_info_cb, m);
//    assert(o);
//
//	dlog(0, "op running", NULL);
//	dlog(0, "%d", o);
//
//    while (pa_operation_get_state(o) == PA_OPERATION_RUNNING)
//    {
//    	dlog(0, "waiting", NULL);
//
//    }
//    dlog(0, "op done", NULL);
//
//
//    dlog(0, "unref'd", NULL);
//
//	dlog(0, "meh", NULL);
//
//	return result;
//}

JNIEXPORT jint JNICALL Java_com_harrcharr_reverb_pulse_Pulse_JNIDoStuff(
		JNIEnv *jenv, jclass jcls) {
	pa_threaded_mainloop *m;
	pa_operation *o;
	m = pa_threaded_mainloop_new();
	pa_threaded_mainloop_start(m);

	pa_threaded_mainloop_lock(m);

	pa_mainloop_api* api = pa_threaded_mainloop_get_api(m);
	pa_context* c = pa_context_new(api, "context");

	int result = pa_context_connect(
			c, "192.168.0.9", PA_CONTEXT_NOFAIL, NULL);

	dlog(0, "connection result %d", result);
	dlog(0, pa_context_get_server(c));

	while(pa_context_is_pending(c) != 0)
		dlog(0, "%i", pa_context_get_state(c));
		pa_threaded_mainloop_wait(m);

	pa_threaded_mainloop_unlock(m);

	dlog(0, "started", NULL);
    pa_threaded_mainloop_lock(m);
    dlog(0, "locked", NULL);

	o = pa_context_get_sink_info_by_index(c, 0,	sink_info_cb, m);
    assert(o);

	dlog(0, "op running", NULL);

    while (pa_operation_get_state(o) == PA_OPERATION_RUNNING)
    {
    	dlog(0, "waiting", NULL);
        pa_threaded_mainloop_wait(m);
    }
    dlog(0, "op done", NULL);

    pa_operation_unref(o);
    dlog(0, "unref'd", NULL);
    pa_threaded_mainloop_unlock(m);
	dlog(0, "meh", NULL);


	return (jint)result;
}




