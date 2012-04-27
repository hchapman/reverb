LOCAL_PATH := $(call my-dir)
ROOT_PATH := $(LOCAL_PATH)

include $(call all-subdir-makefiles)
include $(CLEAR_VARS)

LOCAL_PATH = $(ROOT_PATH)
LOCAL_CFLAGS := -Wall -Wextra

LOCAL_MODULE := pulse_interface

LOCAL_LDLIBS := -L$(SYSROOT)/usr/lib -llog
LOCAL_SHARED_LIBRARIES := libjson libpulse
LOCAL_SRC_FILES := jni_core.c logging.c pulse.c context.c mainloop.c wrap_struct.c
include $(BUILD_SHARED_LIBRARY)

