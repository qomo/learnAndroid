package com.example.liaozongmeng.livecapturegast;

import android.app.Activity;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Surface;
import android.widget.Toast;


public class LiveCaptureGast extends Activity {

    private static final String TAG = "LiveCaptureActivity";
    Camera mCamera;
    private int mDefaultCameraId;
    private Preview mPreview;
    private ImageCameraView mImageCameraView;

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
        setCameraDisplayOrientation();
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

    /**
     * Calculate the camera display orientation so the camera image matches
     * the user's expectation base on how the display is turn.
     * The rotation required is based on the orientation intrinsic to the
     * camera (CameraInfo.orientation), minus any rotation the display has
     * gone through due to being (think of the display as doing the work
     * necessary to orient the camera). The camera orientation is reversed
     * if the camera is facing the user since the image is mirrored.
     */
    public void setCameraDisplayOrientation() {
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        Camera.getCameraInfo(mDefaultCameraId, cameraInfo);
        int rotation = getWindowManager().getDefaultDisplay().getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }

        int desiredRotation = (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) ? (360 - cameraInfo.orientation) : cameraInfo.orientation;
        int nRotation = (desiredRotation - degrees + 360) % 360;
        mCamera.setDisplayOrientation(nRotation);
//        if (mImageCameraView != null) {
//            mImageCameraView.setCameraDisplayCharacteristics(cameraInfo.facing,
//                    nRotation);        }
    }

//    /**
//     * The image camera view tracks the current camera orientation as set by the
//     * setCameraDisplayOrientation method.
//     * @param imageCameraView
//     */
//    public void setImageCameraView(ImageCameraView imageCameraView) {
//        mImageCameraView = imageCameraView;
//    }
}
