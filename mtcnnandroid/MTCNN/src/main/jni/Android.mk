LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)
OPENCV_INSTALL_MODULES := on
OPENCV_CAMERA_MODULES := on
include D:/Tools/OpenCV-android-sdk/sdk/native/jni/OpenCV.mk

LOCAL_MODULE    := mtcnn
LOCAL_SRC_FILES := mtcnnDetect.cpp mtcnn.cpp network.cpp pBox.cpp
LOCAL_STATIC_LIBRARIES := openblas_prebuilt
LOCAL_LDLIBS +=  -llog -ldl
LOCAL_ALLOW_UNDEFINED_SYMBOLS := true
LOCAL_CFLAGS += -std=c++11
LOCAL_CPP_FEATURES := rtti exceptions

include $(BUILD_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE    := openblas_prebuilt
LOCAL_SRC_FILES := D:/Tools/openblas-android/lib/libopenblas.so

include $(PREBUILT_SHARED_LIBRARY)