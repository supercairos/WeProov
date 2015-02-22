package com.weproov.app.ui.views;

import android.content.Context;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;
import java.util.List;

public class CameraPreviewView extends SurfaceView implements SurfaceHolder.Callback {

    private static final String TAG = "CameraPreview";
    private static final double ASPECT_TOLERANCE = 0.1;

    private SurfaceHolder mHolder;
    private Camera mCamera;

    public CameraPreviewView(Context context, Camera camera) {
        super(context);
        mCamera = camera;

        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        mHolder = getHolder();
        mHolder.addCallback(this);
        // deprecated setting, but required on Android versions prior to 3.0
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        setZOrderOnTop(false);
    }

    public void surfaceCreated(SurfaceHolder holder) {
        // The Surface has been created, now tell the camera where to draw the preview.
        try {
            mCamera.setPreviewDisplay(holder);
            mCamera.startPreview();
        } catch (IOException e) {
            Log.d(TAG, "Error setting camera preview: " + e.getMessage());
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        // empty. Take care of releasing the Camera preview in your activity.
    }

    private Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, int width, int height) {
        double targetRatio = (double) width / height;
        double minimumDifference = Double.MAX_VALUE;
        Camera.Size optimalSize = null;

        // Try to find a size that matches the aspect ratio and size.
        for (Camera.Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) {
                continue;
            }
            if (Math.abs(size.height - height) < minimumDifference) {
                optimalSize = size;
                minimumDifference = Math.abs(size.height - height);
            }
        }

        // Cannot find one that matches the aspect ratio, ignore the requirement.
        if (optimalSize == null) {
            minimumDifference = Double.MAX_VALUE;
            for (Camera.Size size : sizes) {
                if (Math.abs(size.height - height) < minimumDifference) {
                    optimalSize = size;
                    minimumDifference = Math.abs(size.height - height);
                }
            }
        }

        return optimalSize;
    }


    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        // If your preview can change or rotate, take care of those events here.
        // Make sure to stop the preview before resizing or reformatting it.

        if (mHolder.getSurface() == null) {
            // preview surface does not exist
            return;
        }

        // stop preview before making changes
        try {
            mCamera.stopPreview();
        } catch (Exception e) {
            // ignore: tried to stop a non-existent preview
        }


        // set preview size and make any resize, rotate or
        // reformatting changes here
        Camera.Parameters parameters = mCamera.getParameters();
        Camera.Size previewSize = getOptimalPreviewSize(parameters.getSupportedPreviewSizes(), h, w);

        Log.d("Test", "Selected size = [" + previewSize.width + "," + previewSize.height + "]");

        parameters.setPreviewSize(previewSize.width, previewSize.height);
        mCamera.setParameters(parameters);

        // start preview with new settings
        try {
            mCamera.setPreviewDisplay(mHolder);
            mCamera.startPreview();

        } catch (Exception e) {
            Log.d(TAG, "Error starting camera preview: " + e.getMessage());
        }
    }
}
