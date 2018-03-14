//
// Created by lenovo on 2018/1/8.
//
#include <jni.h>
#include <android/log.h>
#include "mtcnn.h"
#include "org_opencv_samples_mtcnn_Tutorial2Activity.h"

#define RUN "Program run process"
ofstream faceROI(modeldir + "/FaceRect.txt",ios::out|ios::app);

#ifdef __cplusplus
extern "C" {
#endif

JNIEXPORT void JNICALL Java_org_opencv_samples_mtcnn_Tutorial2Activity_mtcnnDetect
  (JNIEnv *, jobject, jlong matAddr) {
    long mat = (long)matAddr;
    //__android_log_print(ANDROID_LOG_INFO,"MTCNN","THE ADDRESS: %ld",mat);
    Mat &img = *(Mat *) mat;
    //cv::flip(img,img,1);
    //Mat img = origin.clone();
    //rotate image
    cv::transpose(img,img);
    cv::flip(img,img,0);

    //__android_log_print(ANDROID_LOG_INFO,RUN,"START PROCESSING");
    int row = img.rows;
    int col = img.cols;
    __android_log_print(ANDROID_LOG_INFO,RUN,"rows: %d cols: %d",row,col);
    //cv::resize(img,img,cv::Size(640,480),0,0);
    //img.convertTo(img,16);
    cv::cvtColor(img,img,cv::COLOR_RGBA2RGB);
    //__android_log_print(ANDROID_LOG_INFO,RUN,"CVTYPE: %d",img.type());
    if (!img.data) {
        __android_log_print(ANDROID_LOG_INFO,RUN,"NO DATA");
        return;
    }
    static mtcnn find(row,col);
    //__android_log_print(ANDROID_LOG_INFO,RUN,"CREATE FINDOBJECT");
    std::vector<FaceInfo> fds;
    //__android_log_print(ANDROID_LOG_INFO,RUN,"CREATE FACEINFO");
    find.Detect(img, fds);
    //rotate back
    cv::transpose(img,img);


    faceROI << "::";

    __android_log_print(ANDROID_LOG_INFO,RUN,"PROCESS DONE");
}
#ifdef __cplusplus
}
#endif