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
