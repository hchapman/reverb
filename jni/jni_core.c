#-------------------------------------------------------------------------------
# Copyright (c) 2012 Harrison Chapman.
# 
# This file is part of Reverb.
# 
#     Reverb is free software: you can redistribute it and/or modify
#     it under the terms of the GNU General Public License as published by
#     the Free Software Foundation, either version 2 of the License, or
#     (at your option) any later version.
# 
#     Reverb is distributed in the hope that it will be useful,
#     but WITHOUT ANY WARRANTY; without even the implied warranty of
#     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
#     GNU General Public License for more details.
# 
#     You should have received a copy of the GNU General Public License
#     along with Reverb.  If not, see <http://www.gnu.org/licenses/>.
# 
# Contributors:
#     Harrison Chapman - initial API and implementation
#-------------------------------------------------------------------------------
#include "jni_core.h"
#include "logging.h"

JavaVM *g_vm;
jclass jcls_context;
jclass jcls_volume;

const char *k_context_path =
		"com/harrcharr/reverb/pulse/PulseContext";
const char *k_volume_path =
		"com/harrcharr/reverb/pulse/Volume";

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

void *get_obj_ptr(JNIEnv *env, jobject obj) {
	jclass cls = (*env)->GetObjectClass(env, obj);
	jfieldID fid = (*env)->GetFieldID(env, cls, "mPointer", "J");
	if (fid == NULL)
		return;

	return (*env)->GetLongField(env, obj, fid);
}

void *get_pointer_field(JNIEnv *env, jobject obj, char *fname) {
	jclass cls = (*env)->GetObjectClass(env, obj);
	jfieldID fid = (*env)->GetFieldID(env, cls, fname, "J");
	if (fid == NULL)
		return;

	return (*env)->GetLongField(env, obj, fid);
}

long get_long_field(JNIEnv *env, jobject obj, char *fname) {
	jclass cls = (*env)->GetObjectClass(env, obj);
	jfieldID fid = (*env)->GetFieldID(env, cls, fname, "J");
	if (fid == NULL)
		return;

	return (*env)->GetLongField(env, obj, fid);
}

char get_char_field(JNIEnv *env, jobject obj, char *field) {
	jclass cls = (*env)->GetObjectClass(env, obj);
	jfieldID fid = (*env)->GetFieldID(env, cls, field, "C");
	if (fid == NULL)
		return NULL;

	return (*env)->GetCharField(env, obj, fid);
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
	init_class_helper(env, k_context_path, &jcls_context);
	init_class_helper(env, k_volume_path, &jcls_volume);

    return JNI_VERSION_1_6;
}
