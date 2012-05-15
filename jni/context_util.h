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

#ifndef __PA_CONTEXT_UTIL_H
#define __PA_CONTEXT_UTIL_H

#include <jni.h>
#include <pulse/pulseaudio.h>

#include "jni_core.h"

// A structure holding (global) references to runnables, per event type
typedef struct jni_pa_event_cbs {
	jobject sink_input_cbo;
	jobject sink_cbo;
} jni_pa_event_cbs_t;

typedef pa_operation *(*pa_context_get_info_t)(
		pa_context *c, uint32_t idx, void (*cb), void *userdata);
typedef pa_operation *(*pa_context_get_info_list_t)(
		pa_context *c, void (*cb), void *userdata);
typedef pa_operation *(*pa_context_set_mute_t)(
		pa_context *c, uint32_t idx, int mute, void (*cb), void *userdata);
typedef pa_operation *(*pa_context_set_volume_t)(
		pa_context *c, uint32_t idx, pa_cvolume *vol, void (*cb), void *userdata);

void context_synchronized_info_call(
		JNIEnv *jenv, jobject jcontext, jobject jcb,
		pa_context_get_info_t get_info, uint32_t idx,
		void (*cb));
void context_synchronized_info_list_call(
		JNIEnv *jenv, jobject jcontext, jobject jcb,
		pa_context_get_info_list_t get_info_list,
		void (*cb));

void context_synchronized_mute_call(
		JNIEnv *jenv, jobject jcontext, jobject jcb,
		pa_context_set_mute_t set_mute, uint32_t idx, int mute,
		void (*cb));
void context_synchronized_volume_call(
		JNIEnv *jenv, jobject jcontext, jobject jcb,
		pa_context_set_volume_t set_volume, uint32_t idx, jintArray volumes,
		void (*cb));

jni_pa_cb_info_t *new_cbinfo(JNIEnv *jenv, jobject jcontext, jobject jcb,
		pa_threaded_mainloop *m, void *to_free);

pa_context *get_context_ptr(JNIEnv *jenv, jobject jcontext);
pa_threaded_mainloop *get_mainloop_ptr(JNIEnv *jenv, jobject jcontext);

// For dealing with callback global references
jobject get_cb_globalref(JNIEnv *jenv, jobject c, jobject ref);
void del_cb_globalref(JNIEnv *jenv, jobject gref);

// Actual callback functions passed to pulseaudio.
void call_subscription_run(pa_subscription_event_type_t t, uint32_t idx, jobject runnable);
void context_subscription_cb(pa_context* c, pa_subscription_event_type_t t,
		uint32_t idx, void *userdata);
void context_state_cb(pa_context* c, void* userdata);
void info_cb(pa_context* c, const void *i,
		int eol, void *userdata);
void success_cb(pa_context* c, int success, void *userdata);

#endif

