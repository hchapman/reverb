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

JNIEXPORT void JNICALL
Java_com_harrcharr_reverb_pulse_SinkInputInfo_JNIPopulateStruct(
		JNIEnv *jenv, jobject jobj, jlong i_ptr) {
	pa_sink_input_info *i = (pa_sink_input_info*)i_ptr;
	jstring jstr;
	jfieldID fid;

	LOGI("Sink input index # %d", i->index);
	LOGI("Name of sink input, %s", i->name);

	jclass cls = (*jenv)->GetObjectClass(jenv, jobj);
	setFieldStringHelper(jenv, jobj, cls, "sName", i->name);
}

JNIEXPORT void JNICALL
Java_com_harrcharr_reverb_pulse_ClientInfo_JNIPopulateStruct(
		JNIEnv *jenv, jobject jobj, jlong i_ptr) {
	pa_client_info *i = (pa_client_info*)i_ptr;
	jstring jstr;
	jfieldID fid;

	LOGI("Name of client, %s", i->name);

	jclass cls = (*jenv)->GetObjectClass(jenv, jobj);
	setFieldStringHelper(jenv, jobj, cls, "sName", i->name);
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



