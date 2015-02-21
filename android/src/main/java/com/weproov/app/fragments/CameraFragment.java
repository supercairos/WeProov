package com.weproov.app.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.InjectView;
import butterknife.OnClick;
import com.weproov.app.R;
import com.weproov.app.ifaces.ActionBarIface;
import com.weproov.app.views.CameraPreviewView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class CameraFragment extends BaseFragment {

    private static final int CAM_ID = 0;
    private static final String TAG = "CameraFragment";

    @InjectView(R.id.root_view)
    ViewGroup mRootView;

    @InjectView(R.id.btn_set_flash)
    ImageView mBtnSetFlash;

    @InjectView(R.id.btn_camera)
    View mBtnCamera;

    @InjectView(R.id.saving_picture_textview)
    TextView mSavingPicture;

    @InjectView(R.id.ic_camera)
    ImageView mIcCamera;

    private CameraPreviewView mPreview;
    private ActionBarIface mActionBarListener;
    private Camera mCamera;
    private MyPictureCallback mPictureCallback = new MyPictureCallback();

    private class BytesWrapper {

        public byte[] bytes;

        public BytesWrapper(byte[] bytes) {
            this.bytes = bytes;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_camera, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        (new BootCameraTask()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActionBarListener = (ActionBarIface) activity;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mActionBarListener.hideActionBar();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        releaseCamera();
    }

    /**
     * A safe way to get an instance of the Camera object.
     */
    public Camera getCameraInstance() {
        Camera c = null;
        try {
            c = Camera.open(CAM_ID); // attempt to get a Camera instance
        } catch (Exception e) {
            // Camera is not available (in use or does not exist)
            Log.v(TAG, "Error", e);
        }

        return c; // returns null if camera is unavailable
    }

    private void setCameraDisplayOrientation() {
        if (mCamera == null) {
            Log.d(TAG, "setCameraDisplayOrientation - camera null");
            return;
        }

        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(CAM_ID, info);

        WindowManager winManager = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
        int rotation = winManager.getDefaultDisplay().getRotation();

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

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }

        Camera.Parameters params = mCamera.getParameters();
        params.setRotation(result);
        mCamera.setParameters(params);
        mCamera.setDisplayOrientation(result);
    }

    private void setFlashMode() {
        Camera.Parameters parameters = mCamera.getParameters();
        String mode = parameters.getFlashMode();
        if (Camera.Parameters.FLASH_MODE_AUTO.equals(mode)) {
            mBtnSetFlash.setImageResource(R.drawable.ic_flash_auto_holo_light);
        } else if (Camera.Parameters.FLASH_MODE_ON.equals(mode)) {
            mBtnSetFlash.setImageResource(R.drawable.ic_flash_on_holo_light);
        } else if (Camera.Parameters.FLASH_MODE_OFF.equals(mode)) {
            mBtnSetFlash.setImageResource(R.drawable.ic_flash_off_holo_light);
        }
    }

    private void releaseCamera() {
        if (mCamera != null) {
            mCamera.release();        // release the camera for other applications
            mCamera = null;
        }
    }


    @OnClick(R.id.btn_camera)
    public void onCameraButtonClicked() {
        mCamera.takePicture(null, null, mPictureCallback);
    }

    @OnClick(R.id.btn_set_flash)
    public void onCameraFlashClicked() {
        Camera.Parameters parameters = mCamera.getParameters();
        String mode = parameters.getFlashMode();
        if (Camera.Parameters.FLASH_MODE_AUTO.equals(mode)) {
            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_ON);
            mBtnSetFlash.setImageResource(R.drawable.ic_flash_on_holo_light);
        } else if (Camera.Parameters.FLASH_MODE_ON.equals(mode)) {
            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            mBtnSetFlash.setImageResource(R.drawable.ic_flash_off_holo_light);
        } else if (Camera.Parameters.FLASH_MODE_OFF.equals(mode)) {
            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
            mBtnSetFlash.setImageResource(R.drawable.ic_flash_auto_holo_light);
        }

        mCamera.setParameters(parameters);
    }

    public static void lockOrientation(Activity activity) {
        Display display = ((WindowManager) activity.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        int rotation = display.getRotation();
        int tempOrientation = activity.getResources().getConfiguration().orientation;
        int orientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED;
        switch (tempOrientation) {
            case Configuration.ORIENTATION_LANDSCAPE:
                if (rotation == Surface.ROTATION_0 || rotation == Surface.ROTATION_90)
                    orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                else
                    orientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
                break;
            case Configuration.ORIENTATION_PORTRAIT:
                if (rotation == Surface.ROTATION_0 || rotation == Surface.ROTATION_270)
                    orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                else
                    orientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
        }

        activity.setRequestedOrientation(orientation);
    }

    private class MyPictureCallback implements Camera.PictureCallback {

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            mBtnCamera.setEnabled(false);
            (new SavePictureTask()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    private class BootCameraTask extends AsyncTask<Void, Void, Camera> {

        @Override
        protected Camera doInBackground(Void... params) {
            return getCameraInstance();
        }

        @Override
        protected void onPostExecute(Camera camera) {
            super.onPostExecute(camera);
            if (camera != null) {
                mCamera = camera;
                mPreview = new CameraPreviewView(getActivity(), mCamera);
                setCameraDisplayOrientation();
                setFlashMode();
                mRootView.removeViewAt(0);
                mRootView.addView(mPreview, 0);
                mBtnSetFlash.setVisibility(View.VISIBLE);
            }
        }
    }

    private class SavePictureTask extends AsyncTask<BytesWrapper, Void, File> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            lockOrientation(getActivity());
            mSavingPicture.setVisibility(View.VISIBLE);
            mIcCamera.setVisibility(View.GONE);
        }

        @Override
        protected File doInBackground(BytesWrapper... params) {
            // Save picture to file
            File file = new File(getActivity().getCacheDir(), "pic_" + System.currentTimeMillis() + ".jpg");
            try {
                FileOutputStream f = new FileOutputStream(file);
                f.write(params[0].bytes);
            } catch (IOException e) {
                Log.e("Test", "IOEx", e);
            }

            return file;
        }

        @Override
        protected void onPostExecute(File s) {
            super.onPostExecute(s);
            // Move to edit
            if (s != null && s.exists()) {
                //

            }
        }
    }
}
