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
#include <string.h>
#include <unistd.h>

#include <sys/socket.h>
#include <errno.h>

#include <pulse/pulseaudio.h>

#include "jni_core.h"
#include "pulse.h"
#include "logging.h"

extern JavaVM *g_vm;

extern jclass jcls_volume;

inline void set_field_string(JNIEnv *jenv,
		jobject jobj, jclass cls,
		char *fname, char *data) {
	jfieldID fid = (*jenv)->GetFieldID(jenv, cls, fname, "Ljava/lang/String;");
	if (fid == NULL) {
		LOGE("Unable to find field %s", fname);
		return; // Field not found
	}
	jstring jstr = (*jenv)->NewStringUTF(jenv, data);
	if (jstr == NULL) {
		return; // OOM
	}
	(*jenv)->SetObjectField(jenv, jobj, fid, jstr);
}

inline void set_field_int(JNIEnv *jenv,
		jobject jobj, jclass cls,
		char *fname, int data) {
	jfieldID fid = (*jenv)->GetFieldID(jenv, cls, fname, "I");
	if (fid == NULL) {
		LOGE("Unable to find field %s", fname);
		return; // Field not found
	}

	(*jenv)->SetIntField(jenv, jobj, fid, data);
}

inline void set_field_long(JNIEnv *jenv,
		jobject jobj, jclass cls,
		char *fname, long data) {
	jfieldID fid = (*jenv)->GetFieldID(jenv, cls, fname, "J");
	if (fid == NULL) {
		LOGE("Unable to find field %s", fname);
		return; // Field not found
	}

	(*jenv)->SetLongField(jenv, jobj, fid, data);
}

inline void set_field_volume(JNIEnv *jenv,
		jobject jobj, jclass cls,
		char *fname, pa_cvolume* v) {
	jfieldID fid = (*jenv)->GetFieldID(jenv, cls, fname,
			"Lcom/harrcharr/reverb/pulse/Volume;");
	if (fid == NULL) {
		LOGE("Unable to find field %s", fname);
		return; // Field not found
	}

	jintArray vols;
	vols = (*jenv)->NewIntArray(jenv, v->channels);
	if (vols == NULL) {
		return; /* oom */
	}
	(*jenv)->SetIntArrayRegion(
			jenv, vols, 0, v->channels, v->values);

	jobject data;
	jmethodID init = (*jenv)->GetMethodID(jenv, jcls_volume,
			"<init>", "([I)V");
	data = (*jenv)->NewObject(jenv, jcls_volume,
			init, vols);

	(*jenv)->SetObjectField(jenv, jobj, fid, data);
}

//void set_field_proplist(JNIEnv *jenv,
//		jobject jobj, jclass cls,
//		char *fname, pa_proplist* p) {
//	jfieldID fid = (*jenv)->GetFieldID(jenv, cls, fname,
//			"Ljava/nio/ByteBuffer;");
//	if (fid == NULL) {
//		LOGE("Unable to find field %s", fname);
//		return; // Field not found
//	}
//
//	jobject bb = (*jenv)->NewDirectByteBuffer(jenv, p, sizeof(pa_proplist));
//	(*jenv)->SetObjectField(jenv, jobj, fid, bb);
//}

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
	set_field_string(jenv, jobj, cls, "sName", i->name);
	set_field_string(jenv, jobj, cls, "sDescription", i->description);
}

JNIEXPORT void JNICALL
Java_com_harrcharr_reverb_pulse_SinkInput_JNIPopulateStruct(
		JNIEnv *jenv, jobject jobj, jlong i_ptr) {
	pa_sink_input_info *i = (pa_sink_input_info*)i_ptr;
	jstring jstr;
	jfieldID fid;

	LOGI("Sink input index # %d", i->index);
	LOGI("Name of sink input, %s", i->name);

	LOGD("The volume of %s is %d", i->name, (i->volume).values[0]);

	jclass cls = (*jenv)->GetObjectClass(jenv, jobj);
	set_field_string(jenv, jobj, cls, "mName", i->name);
	set_field_int(jenv, jobj, cls, "mIndex", i->index);
	set_field_volume(jenv, jobj, cls, "mVolume", &(i->volume));

	// Set important proplist values, should they exist.
	pa_proplist *p = i->proplist;
	if(pa_proplist_contains(p, PA_PROP_APPLICATION_NAME))
		set_field_string(jenv, jobj, cls, "mAppName",
				pa_proplist_gets(p, PA_PROP_APPLICATION_NAME));
	LOGD(pa_proplist_to_string(i->proplist));
//	set_field_proplist(jenv, jobj, cls, "mProplist", i->proplist);
}

JNIEXPORT void JNICALL
Java_com_harrcharr_reverb_pulse_ClientInfo_JNIPopulateStruct(
		JNIEnv *jenv, jobject jobj, jlong i_ptr) {
	pa_client_info *i = (pa_client_info*)i_ptr;
	jstring jstr;
	jfieldID fid;

	LOGI("Name of client, %s", i->name);

	jclass cls = (*jenv)->GetObjectClass(jenv, jobj);
	set_field_string(jenv, jobj, cls, "sName", i->name);
}

JNIEXPORT jstring JNICALL
Java_com_harrcharr_reverb_pulse_PulseNode_getProps(
		JNIEnv *jenv, jobject jobj, jstring key) {
	pa_proplist *p = (pa_proplist *)get_long_field(jenv, jobj, "mProplist");

//	LOGD(pa_proplist_gets(p, key));
	return key;
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



