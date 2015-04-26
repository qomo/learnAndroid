package com.example.liaozongmeng.livecapturegast;

import android.app.Activity;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;


public class LiveCaptureGast extends Activity {

    private static final String TAG = "LiveCaptureActivity";
    Camera mCamera;
    private int mDefaultCameraId;
    private Preview mPreview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_capture_gast);
        mPreview = (Preview) findViewById(R.id.preview1);

        // Find the total number of cameras available
        int nCameras;
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.GINGERBREAD) {
            nCameras = Camera.getNumberOfCameras();
        } else {
            nCameras = 1;
            mDefaultCameraId = 0;
        }
        // Find the ID of the default camera if there is more than 1
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        for (int i = 1; i < nCameras; i++)
        {
            Camera.getCameraInfo(i, cameraInfo);
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK)
            {
                mDefaultCameraId = i;
            }
        }
        // test for no camera facing back
        if (mDefaultCameraId == -1)
        {
            // test for no cameras
            if (nCameras > 0)
            {
                mDefaultCameraId = 0;
            } else
            {
                // nothing can be done; tell the user then exit
                Toast toast = Toast.makeText(getApplicationContext(),
                        R.string.no_cameras, Toast.LENGTH_LONG);
                toast.show();
                finish();
            }
        }
    }


    @Override
    protected void onResume()
    {
        super.onResume();
        Log.d(TAG, "CAMERA: opening camera: " + mDefaultCameraId);
        mCamera = Camera.open(mDefaultCameraId);
        mPreview.setCamera(mCamera);
    }

    @Override
    protected void onPause()
    {
        super.onPause();

        // Because the Camera object is a shared resource, it's very
        // important to release it when the activity is paused.
        if (mCamera != null)
        {
            mPreview.setCamera(null);
            mCamera.release();
            mCamera = null;
        }
    }
}
