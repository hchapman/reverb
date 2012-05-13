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

LOCAL_PATH := $(call my-dir)
ROOT_PATH := $(LOCAL_PATH)

include $(call all-subdir-makefiles)
include $(CLEAR_VARS)

LOCAL_PATH = $(ROOT_PATH)
LOCAL_CFLAGS := -Wall -Wextra

LOCAL_MODULE := pulse_interface

LOCAL_LDLIBS := -L$(SYSROOT)/usr/lib -llog
LOCAL_SHARED_LIBRARIES := libjson libpulse
LOCAL_SRC_FILES := jni_core.c logging.c pulse.c context.c mainloop.c wrap_struct.c jni_util.c
include $(BUILD_SHARED_LIBRARY)

