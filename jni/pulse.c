#include <jni.h>
#include <stdlib.h>
#include <string.h>
#include <android/log.h>
#include <unistd.h>

#include <sys/socket.h>
#include <errno.h>

#include <pulse/pulseaudio.h>

static JavaVM *g_vm;
static jclass jclsContext;

const char *kContextPath =
		"com/harrcharr/reverb/pulse/Context";

typedef struct jni_pa_cb_info {
	jobject cb_runnable;        	// Object with the run() command
	char *cb_runnable_type;        	// Type of runnable (so we know what to search for
	pa_threaded_mainloop *m;       	// pa_mainloop for signaling
} jni_pa_cb_info_t ;

static void context_state_cb(pa_context* c, void* userdata) {
	JNIEnv *env;
	int status;
	char isAttached = 0;

	status = (*g_vm)->GetEnv(g_vm, (void **) &env, JNI_VERSION_1_6);
	dlog(0, "status %d", status);
	if(status < 0){
		dlog(0, "ATTACHIN'");
		status = (*g_vm)->AttachCurrentThread(g_vm, &env, NULL);
		if(status < 0) {
			return;
		}
		isAttached = 1;
	}

	jclass cls = jclsContext;
	if (cls == 0) {
		if(isAttached == 1) {
			dlog(0, "detaching");
			(*g_vm)->DetachCurrentThread(g_vm);
		}
		return;
	}
	jmethodID mid = (*env)->GetStaticMethodID(env, cls,
			"statusChanged", "(JI)V");
	if (mid == 0) {
		if(isAttached == 1) {
			dlog(0, "detaching");
			(*g_vm)->DetachCurrentThread(g_vm);
		}
		return;
	}

	// Run the actual Java callback method
	(*env)->CallStaticVoidMethod(env, cls, mid, (jlong)c,
			(jint)pa_context_get_state(c));

	if(isAttached == 1) {
		dlog(0, "detaching");
		(*g_vm)->DetachCurrentThread(g_vm);
	}
}

void sink_info_cb(pa_context* c, const pa_sink_info *i,
		int eol, void *userdata) {
    pa_threaded_mainloop *m = ((jni_pa_cb_info*)userdata)->m;
    assert(m);

	if (eol < 0) {
		dlog(0, "Apparently this is an error");

	    pa_threaded_mainloop_signal(m, 0);
	    return;
	}

	if (eol > 0) {

	    pa_threaded_mainloop_signal(m, 0);
	    return;
	}

	dlog(0, "Sup bro", NULL);
	dlog(0, "%d eol %d", i, eol);

	dlog(0, i->description);
	dlog(0, "Pointer to sink info at begin of cb %d", i);

    pa_threaded_mainloop_signal(m, 0);

	JNIEnv *env;
	int status;
	char isAttached = 0;

	status = (*g_vm)->GetEnv(g_vm, (void **) &env, JNI_VERSION_1_6);
	dlog(0, "status %d", status);
	if(status < 0){
		dlog(0, "ATTACHIN'");
		status = (*g_vm)->AttachCurrentThread(g_vm, &env, NULL);
		if(status < 0) {
			return;
		}
		isAttached = 1;
	}

	jclass cls = jclsContext;
	if (cls == 0) {
		if(isAttached == 1) {
			dlog(0, "detaching");
			(*g_vm)->DetachCurrentThread(g_vm);
		}
		return;
	}
	jmethodID mid = (*env)->GetStaticMethodID(env, cls,
			"gotSinkInfo", "(JJL/com/harrcharr/reverb/pulse/Context$SinkInfoCallback;)V");
	if (mid == 0) {
		if(isAttached == 1) {
			dlog(0, "detaching");
			(*g_vm)->DetachCurrentThread(g_vm);
		}
		return;
	}

	// Run the actual Java callback method
	(*env)->CallStaticVoidMethod(env, cls, mid, (jlong)c, (jlong)i);

	if(isAttached == 1) {
		dlog(0, "detaching");
		(*g_vm)->DetachCurrentThread(g_vm);
	}

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
		JNIEnv *jenv, jclass jcls, jlong c_ptr, jlong m_ptr, jint idx) {
	pa_context *c = (pa_context *)c_ptr;
	pa_threaded_mainloop *m = (pa_threaded_mainloop *)m_ptr;
	pa_threaded_mainloop_lock(m);

	pa_operation *o;
	dlog(0, "About to get sink info %d", m);
	jni_pa_cb_info_t
	o = pa_context_get_sink_info_by_index(c, (int)idx, sink_info_cb, m);
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

//    pa_operation_unref(o);
    dlog(0, "unref'd", NULL);
    pa_threaded_mainloop_unlock(m);
	dlog(0, "meh", NULL);


	return (jint)result;
}

void dlog(int level, const char *fmt, ...) {
	va_list args;

	va_start(args, fmt);
	__android_log_vprint(ANDROID_LOG_DEBUG, "Reverb", fmt, args);
	va_end(args);
}

void initClassHelper(JNIEnv *env,
		const char *path, jclass *clsptr) {
	jclass cls = (*env)->FindClass(env, path);
	(*clsptr) = (*env)->NewGlobalRef(env, cls);
}

JNIEXPORT jint JNICALL JNI_OnLoad(
                JavaVM *jvm, void *reserved) {
	(void)reserved;
	JNIEnv *env;

	g_vm = jvm;
	if ((*jvm)->GetEnv(jvm, (void**) &env, JNI_VERSION_1_6) != JNI_OK) {
		return -1;
	}
	initClassHelper(env, kContextPath, &jclsContext);

    return JNI_VERSION_1_6;
}
