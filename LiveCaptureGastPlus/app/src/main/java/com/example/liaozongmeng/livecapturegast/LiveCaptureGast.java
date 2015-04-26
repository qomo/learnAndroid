package com.example.liaozongmeng.livecapturegast;

import android.app.Activity;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Surface;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.List;


public class LiveCaptureGast extends Activity {

    private static final String TAG = "LiveCaptureActivity";
    private Button mButtonFlash, mButtonFocus, mButtonSwitch,
            mButtonWhiteBalance, mButtonZoom;
    private List<String> mlszFocusModes, mlszFlashModes, mlszWhiteBalanceModes;
    private int mnFlashMode, mnFocusMode, mnWhiteBalanceMode;
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


        mButtonFlash = (Button) findViewById(R.id.buttonFlash);
        mButtonFlash.setOnClickListener(mButtonClickListener);
        mButtonFocus = (Button) findViewById(R.id.buttonFocus);
        mButtonFocus.setOnClickListener(mButtonClickListener);
        mButtonSwitch = (Button) findViewById(R.id.buttonSwitchCamera);
        mButtonSwitch.setOnClickListener(mButtonClickListener);
        mButtonWhiteBalance = (Button) findViewById(R.id.buttonWhiteBalance);
        mButtonWhiteBalance.setOnClickListener(mButtonClickListener);
        mButtonZoom = (Button) findViewById(R.id.buttonZoom);
        mButtonZoom.setOnClickListener(mButtonClickListener);
    }


    @Override
    protected void onResume()
    {
        super.onResume();
        Log.d(TAG, "CAMERA: opening camera: " + mDefaultCameraId);
        mCamera = Camera.open(mDefaultCameraId);
        setCameraDisplayOrientation();
        mPreview.setCamera(mCamera);
        switchCameraUI();
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
     * switchCamera initializes the UI labels for the currently selected camera
     */
    private void switchCameraUI()
    {
//        mPreview.switchCamera(mCamera);
        mButtonSwitch.setText(getText(R.string.camera) + " " + mDefaultCameraId);
        Camera.Parameters cameraParameters = mCamera.getParameters();
        mlszFlashModes = cameraParameters.getSupportedFlashModes();
        mlszFocusModes = cameraParameters.getSupportedFocusModes();
        mlszWhiteBalanceModes = cameraParameters.getSupportedWhiteBalance();
        mButtonFlash.setEnabled(mlszFlashModes != null
                && mlszFlashModes.size() > 0);
        mButtonZoom.setEnabled(cameraParameters.isZoomSupported()
                && cameraParameters.getMaxZoom() > 0);
        mnFlashMode = 0;
        mnFocusMode = 0;
        mnWhiteBalanceMode = 0;
        setCameraLabels(cameraParameters);
    }



    /**
     * set the button labels based on the current camera parameters
     *
     * @param cameraParameters
     *            : the curren camera parameters object
     */
    void setCameraLabels(Camera.Parameters cameraParameters)
    {
        if (mlszFlashModes != null)
        {
            mButtonFlash.setText(getText(R.string.flash) + " "
                    + cameraParameters.getFlashMode());
        } else
        {
            mButtonFlash.setText(getText(R.string.flash));
            mButtonZoom.setEnabled(false);
        }
        if (mlszFocusModes != null)
        {
            mButtonFocus.setText(getText(R.string.focus) + " "
                    + cameraParameters.getFocusMode());
        } else
        {
            mButtonFocus.setText(getText(R.string.focus));
            mButtonFocus.setEnabled(false);
        }
        if (mlszWhiteBalanceModes != null)
        {
            mButtonWhiteBalance.setText(getText(R.string.whiteBalance) + " "
                    + cameraParameters.getWhiteBalance());
        } else
        {
            mButtonWhiteBalance.setText(getText(R.string.whiteBalance));
            mButtonWhiteBalance.setEnabled(false);
        }
        mButtonZoom.setText(getText(R.string.zoom) + " "
                + cameraParameters.getZoom());

    }

    private View.OnClickListener mButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Camera.Parameters cameraParameters = mCamera.getParameters();
            if (v == mButtonFlash)
            {
                mnFlashMode = (mnFlashMode + 1) % mlszFlashModes.size();
                cameraParameters.setFocusMode(mlszFlashModes.get(mnFlashMode));
            } else if (v == mButtonFocus)
            {
                mnFocusMode = (mnFocusMode + 1) % mlszFocusModes.size();
                cameraParameters.setFocusMode(mlszFocusModes.get(mnFocusMode));
//            } else if (v == mButtonSwitch)
//            {
//                mCamera.stopPreview();
//                advanceCamera();
//                switchCameraUI();
//                // mCamera.startPreview();
//                // reset camera parameters
//                cameraParameters = mCamera.getParameters();
            } else if (v == mButtonWhiteBalance)
            {
                mnWhiteBalanceMode = (mnWhiteBalanceMode + 1)
                        % mlszWhiteBalanceModes.size();
                cameraParameters.setFocusMode(mlszWhiteBalanceModes
                        .get(mnWhiteBalanceMode));
            } else if (v == mButtonZoom)
            {
                cameraParameters.setZoom((cameraParameters.getZoom() + 9)
                        % cameraParameters.getMaxZoom() + 1);
            }

            // stop camera preview because changing some parameters caused a
            // RuntimeException if it is running
            mCamera.stopPreview();
            try {
                mCamera.setParameters(cameraParameters);
            } catch (RuntimeException rx) {
                String szError = getApplicationContext().getString(R.string.set_parameters_failed) + rx.toString();
                Toast t = Toast.makeText(getApplicationContext(), szError, Toast.LENGTH_SHORT);
                t.show();
                // the camera parameter change failed. Reset current value
                cameraParameters = mCamera.getParameters();
            }
            mCamera.startPreview();
            setCameraLabels(cameraParameters);
        }
    };

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


}
