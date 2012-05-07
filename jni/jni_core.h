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

#ifndef __PA_JNI_CORE_H
#define __PA_JNI_CORE_H

#include <jni.h>
#include <pulse/pulseaudio.h>

typedef struct jni_pa_cb_info {
	jobject cb_runnable;        	// Object with the run() command
	pa_threaded_mainloop *m;       	// pa_mainloop for signaling
	void *to_free;                  // an object to free on success
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
long get_long_field(JNIEnv *env, jobject obj, char *fname);

void throw_exception(JNIEnv *env, const char *name, const char *msg);

#endif
