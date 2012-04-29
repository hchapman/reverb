#include "jni_core.h"
#include "logging.h"

#include <pulse/pulseaudio.h>

JNIEXPORT jint JNICALL
Java_com_harrcharr_reverb_pulse_Volume_getMax(
		JNIEnv *jenv, jobject jobj) {
	pa_cvolume *v = (pa_cvolume*)get_obj_ptr(jenv, jobj);
	return pa_cvolume_max(v);
}

JNIEXPORT void JNICALL
Java_com_harrcharr_reverb_pulse_Volume_free(
		JNIEnv *jenv, jobject jobj) {
}
