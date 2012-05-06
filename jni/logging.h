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

#ifndef __PA_JNI_LOGGING_H
#define __PA_JNI_LOGGING_H

#include <stdlib.h>
#include <android/log.h>

#define LOGV(...) __android_log_print(ANDROID_LOG_VERBOSE, "ReverbJNI", __VA_ARGS__)
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG  , "ReverbJNI", __VA_ARGS__)
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO   , "ReverbJNI", __VA_ARGS__)
#define LOGW(...) __android_log_print(ANDROID_LOG_WARN   , "ReverbJNI", __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR  , "ReverbJNI", __VA_ARGS__)

void dlog(int level, const char *fmt, ...);

#endif /* __PA_JNI_LOGGING_H */
