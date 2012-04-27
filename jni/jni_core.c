#include "jni_core.h"
#include "logging.h"

JavaVM *g_vm;
jclass jclsContext;

const char *kContextPath =
		"com/harrcharr/reverb/pulse/Context";

jenv_status_t get_jnienv(JNIEnv **env) {
	dlog(0, "about to get jnienv");
	int status = (*g_vm)->GetEnv(g_vm, (void **) env, JNI_VERSION_1_6);
	if(status < 0){
		dlog(0, "We have to attach.");
		// We're running from a C thread, so attach to Java
		status = (*g_vm)->AttachCurrentThread(g_vm, env, NULL);
		if(status < 0) {
			// Failure of some sort
			return JENV_UNSUCCESSFUL;
		}
		return JENV_ATTACHED;
	}
	return JENV_UNATTACHED;
}

void detach_jnienv(jenv_status_t status) {
	if (status == JENV_ATTACHED)
		(*g_vm)->DetachCurrentThread(g_vm);
}

void init_class_helper(JNIEnv *env,
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
	init_class_helper(env, kContextPath, &jclsContext);

    return JNI_VERSION_1_6;
}
