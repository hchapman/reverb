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
# LiveStreams/jni/librtmp/Android.mk
LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE := libjson

LOCAL_SRC_FILES := lib/libjson.so
LOCAL_EXPORT_C_INCLUDES := $(LOCAL_PATH)/include

include $(PREBUILT_SHARED_LIBRARY)
