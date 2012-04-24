# LiveStreams/jni/librtmp/Android.mk
LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE := libjson

LOCAL_SRC_FILES := lib/libjson.so
LOCAL_EXPORT_C_INCLUDES := $(LOCAL_PATH)/include

include $(PREBUILT_SHARED_LIBRARY)
