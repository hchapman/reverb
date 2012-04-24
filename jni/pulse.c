#include <jni.h>
#include <stdlib.h>
#include <string.h>
#include <android/log.h>
#include <unistd.h>

#include <sys/socket.h>
#include <errno.h>

#include <pulse/pulseaudio.h>

static void sink_info_cb(pa_context* c, const pa_sink_info *i,
		int eol, void *userdata) {
	dlog(0, "Sup bro", NULL);
	dlog(0, i->description);
    pa_threaded_mainloop *ma;

    ma = userdata;
    assert(ma);

    pa_threaded_mainloop_signal(ma, 0);
}

JNIEXPORT jlong JNICALL Java_com_harrcharr_reverb_pulse_Mainloop_JNINew(
		JNIEnv *jenv, jclass jcls) {
	pa_threaded_mainloop *m = pa_threaded_mainloop_new();

	return m;
}

JNIEXPORT void JNICALL Java_com_harrcharr_reverb_pulse_Mainloop_JNIStart(
		JNIEnv *jenv, jclass jcls, jlong ptr_m) {
	pa_threaded_mainloop_start((pa_threaded_mainloop*)ptr_m);
}

JNIEXPORT jlong JNICALL Java_com_harrcharr_reverb_pulse_Context_JNICreate(
		JNIEnv *jenv, jclass jcls, pa_threaded_mainloop *m) {
	dlog(0, "%d", m);
	pa_mainloop_api *api = pa_threaded_mainloop_get_api(m);
	pa_context *c = pa_context_new(api, "primary");

	dlog(0, "hello from c!");

	dlog(0, "%d", c);

	return c;
}

JNIEXPORT jint JNICALL Java_com_harrcharr_reverb_pulse_Context_JNIConnect(
		JNIEnv *jenv, jclass jcls, jlong ptr_context, jstring server) {
//	pa_threaded_mainloop *m = (pa_threaded_mainloop*)ptr_mainloop;
	pa_context *c = (pa_context*)ptr_context;

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
		dlog(0, "%i", pa_context_get_state(c));
	}

	dlog(0, "holy shit");
	dlog(0, "%d", pa_context_get_state(c));

	return result;
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
//        pa_threaded_mainloop_wait(m);
//    }
//    dlog(0, "op done", NULL);
//
//    pa_operation_unref(o);
//    dlog(0, "unref'd", NULL);
//    pa_threaded_mainloop_unlock(m);
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

JNIEXPORT jint JNICALL JNI_OnLoad(
                JavaVM *jvm, void *reserved) {
        (void)jvm; (void)reserved;
        return JNI_VERSION_1_6;
}
