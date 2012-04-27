/*
 * logging.h
 *
 *  Created on: Apr 27, 2012
 *      Author: Harrison Chapman
 */

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
