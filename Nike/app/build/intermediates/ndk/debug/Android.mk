LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)

LOCAL_MODULE := MyLib
LOCAL_SRC_FILES := \
	/Users/stevenstewart/AndroidStudioProjects/Nike/app/src/main/jni/native_code.c \

LOCAL_C_INCLUDES += /Users/stevenstewart/AndroidStudioProjects/Nike/app/src/main/jni
LOCAL_C_INCLUDES += /Users/stevenstewart/AndroidStudioProjects/Nike/app/src/debug/jni

include $(BUILD_SHARED_LIBRARY)
