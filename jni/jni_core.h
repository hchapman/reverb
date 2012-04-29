#ifndef __PA_JNI_CORE_H
#define __PA_JNI_CORE_H

#include <jni.h>
#include <pulse/pulseaudio.h>

typedef struct jni_pa_cb_info {
	jobject cb_runnable;        	// Object with the run() command
	pa_threaded_mainloop *m;       	// pa_mainloop for signaling
} jni_pa_cb_info_t ;


typedef enum {
	JENV_UNSUCCESSFUL,
	JENV_ATTACHED,
	JENV_UNATTACHED
} jenv_status_t ;

/*
 * Get the JNIenv, attaching to a thread if necessary.
 * @returns: status of the Java environment connection
 */
jenv_status_t get_jnienv(JNIEnv **jenv);
void detach_jnienv(jenv_status_t status);

void *get_obj_ptr(JNIEnv *env, jobject obj);

#endif
