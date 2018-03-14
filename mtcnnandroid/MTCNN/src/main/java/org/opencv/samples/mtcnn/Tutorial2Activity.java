package org.opencv.samples.mtcnn;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.WindowManager;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Tutorial2Activity extends Activity implements CvCameraViewListener2 {
    private static final String    TAG = "OCVSample::Activity";
    private static final String    RUN = "Program run process";


    private Mat                    mRgba;
    //private Mat                    mIntermediateMat;
    private Mat                    image;

    private CameraBridgeViewBase   mOpenCvCameraView;



    private BaseLoaderCallback  mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i(TAG, "OpenCV loaded successfully");

                    // Load native library after(!) OpenCV initialization
                    //System.loadLibrary("mixed_sample");

                    mOpenCvCameraView.enableView();
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };

    public Tutorial2Activity() {
        Log.i(TAG, "Instantiated new " + this.getClass());
    }

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "called onCreate");
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.tutorial2_surface_view);

        mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.tutorial2_activity_surface_view);
        mOpenCvCameraView.setVisibility(CameraBridgeViewBase.VISIBLE);
        mOpenCvCameraView.enableFpsMeter();
        mOpenCvCameraView.setCvCameraViewListener(this);
        mOpenCvCameraView.setCameraIndex(CameraBridgeViewBase.CAMERA_ID_FRONT);//front camera
    }


    @Override
    public void onPause()
    {
        super.onPause();
        Log.i(RUN,"on Pause");
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
        File faceInfo = new File("mnt/sdcard/mtcnn/FaceRect.txt");
        try {
            new upLoadTask(faceInfo).startClient();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }



    @Override
    public void onResume()
    {
        Log.i("socket","onResume");
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            //OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
        if (checkFiles()) {
            Log.i("socket","下载模型");
            Intent intent = new Intent(Tutorial2Activity.this,DownLoadActivity.class);
            startActivity(intent);
        }

    }

    public void onDestroy() {
        super.onDestroy();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    public void onCameraViewStarted(int width, int height) {
        mRgba = new Mat();
        //Log.i(RUN,mRgba.type()+"");
        //mRgba = new Mat(height, width, CvType.CV_8UC4);
        //mIntermediateMat = new Mat(height, width, CvType.CV_8UC4);
    }

    public void onCameraViewStopped() {
        mRgba.release();
        //mIntermediateMat.release();
    }

    //private int i = 1;
    public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
        //get start time
        // input frame has RGBA format
        mRgba = inputFrame.rgba();

        Log.i(RUN,"cols: " + mRgba.cols() + " rows: " + mRgba.rows());

        Log.i(RUN,"Excute start.");

        //Log.i(RUN,mRgba.getNativeObjAddr() + "");
        mtcnnDetect(mRgba.getNativeObjAddr());
//        if (1 == i) {
//            Mat save = new Mat();
//            Imgproc.cvtColor(mRgba,save,Imgproc.COLOR_BGR2RGB);
//            String filename = Environment.getExternalStorageDirectory() + File.separator + "testfile.jpg";
//            Imgcodecs.imwrite(filename,save);
//            i = 2;
//        }

        //get end time
        Log.i(RUN,"Excute done.");

        return mRgba;
    }

    public native void mtcnnDetect(long matAddr);

    public boolean checkFiles() {
        File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "mtcnn");
        Log.i("socket","check files");
        if (!dir.exists()) {
            Log.i("socket","建立文件夹");
            dir.mkdirs();
            return true;
        }
        String[] files = {"Pnet.txt","Rnet.txt","Onet.txt"};
        for (String file : files) {
            File filePath = new File(dir.getAbsolutePath() + File.separator + file);
            if (!filePath.exists() || filePath.length() == 0) {
                Log.i("socket","文件不存在，重新下载模型文件");
                return true;
            }
        }
        return false;
    }

static {
        System.loadLibrary("openblas");
        Log.i(RUN,"Load openblas.so done.");
        System.loadLibrary("opencv_java3");
        Log.i(RUN,"Load opencv_java3.so done");
        System.loadLibrary("mtcnn");
        Log.i(RUN,"Load mtcnn.so done.");
    }

}
