package com.weproov.app.ui.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.*;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnLongClick;
import com.weproov.app.R;
import com.weproov.app.ui.ifaces.ActionBarIface;
import com.weproov.app.ui.views.CameraPreviewView;
import com.weproov.app.utils.CameraUtils;
import com.weproov.app.utils.OrientationUtils;
import com.weproov.app.utils.PicassoUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class CameraFragment extends TunnelFragment {

	private static final String TAG = "CameraFragment";

	/**
	 * Arguments keys *
	 */
	public static final String KEY_OVERLAY_PICTURE_SUBTITLE = "key_overlay_picture_subtitle";
	public static final String KEY_OVERLAY_PICTURE = "key_overlay_picture";

	@InjectView(R.id.loading_camera)
	TextView mLoadingCamera;

	@InjectView(R.id.btn_set_flash)
	ImageView mBtnSetFlash;

	@InjectView(R.id.btn_camera)
	View mBtnCamera;

	@InjectView(R.id.saving_picture_textview)
	TextView mSavingPicture;

	@InjectView(R.id.ic_camera)
	ImageView mIcCamera;

	@InjectView(R.id.camera_overlay)
	ImageView mOverlay;

	@InjectView(R.id.camera_overlay_subtitle)
	TextView mOverlaySubtitle;

	@InjectView(R.id.camera_preview)
	CameraPreviewView mPreview;

	private int mOverlayResourceId;
	private String mOverlaySubtitleString;


	private ActionBarIface mActionBarListener;
	private Camera mCamera;
	private MyPictureCallback mPictureCallback = new MyPictureCallback();


	private static class BytesWrapper {

		private byte[] bytes;

		public BytesWrapper(byte[] bytes) {
			this.bytes = bytes;
		}
	}

	public static CameraFragment newInstance(int overlay, String subtitle) {

		Bundle bundle = new Bundle();
		bundle.putInt(KEY_OVERLAY_PICTURE, overlay);
		bundle.putString(KEY_OVERLAY_PICTURE_SUBTITLE, subtitle);

		CameraFragment frag = new CameraFragment();
		frag.setArguments(bundle);
		return frag;
	}

	public CameraFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (getArguments() != null) {
			mOverlayResourceId = getArguments().getInt(KEY_OVERLAY_PICTURE, 0);
			mOverlaySubtitleString = getArguments().getString(KEY_OVERLAY_PICTURE_SUBTITLE);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_camera, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		mBtnCamera.setEnabled(false);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mActionBarListener = (ActionBarIface) activity;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setCommmandListener(null);
	}

	@Override
	public void onStart() {
		super.onStart();
		(new BootCameraTask()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		mActionBarListener.hideActionBar();
	}

	@Override
	public void onStop() {
		super.onStop();
		releaseCamera();
		mActionBarListener.showActionBar();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	private static void setCameraDisplayOrientation(Context context, Camera camera, int id) {
		if (camera == null) {
			Log.d(TAG, "setCameraDisplayOrientation - camera null");
			return;
		}

		Camera.CameraInfo info = new Camera.CameraInfo();
		Camera.getCameraInfo(id, info);

		int rotation = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getRotation();
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

		Camera.Parameters params = camera.getParameters();
		params.setRotation(result);
		camera.setParameters(params);
		camera.setDisplayOrientation(result);
	}

	private static void setCameraFocusMode(Camera camera) {
		// get Camera parameters
		Camera.Parameters params = camera.getParameters();
		List<String> focusModes = params.getSupportedFocusModes();
		if (focusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
			params.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
			camera.setParameters(params);
		}
	}

	private void setCameraBestPictureSize(Camera camera) {
		// get Camera parameters
		Camera.Parameters params = camera.getParameters();
		Camera.Size size = CameraUtils.getBestPictureSize(params.getSupportedPictureSizes());
		params.setPictureSize(size.width, size.height);
		camera.setParameters(params);
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
		mPreview.setCamera(null);
		if (mCamera != null) {
			mCamera.stopPreview();
			mCamera.release();        // release the camera for other applications
			mCamera = null;
		}
	}


	@OnClick(R.id.btn_camera)
	public void onCameraButtonClicked() {
		try {
			if (mCamera != null) {
				mCamera.takePicture(null, null, mPictureCallback);
			}
		} catch (Exception e) {
			// For some reason, the picture could not be taken.
			Toast.makeText(getActivity(), R.string.error_taking_picture, Toast.LENGTH_LONG).show();
			Log.e("Test", "Test", e);
		}
	}

	@OnLongClick(R.id.btn_camera)
	public boolean onCameraButtonLongClicked() {
		try {
			if (mCamera != null) {
				mCamera.autoFocus(new Camera.AutoFocusCallback() {
					@Override
					public void onAutoFocus(boolean success, Camera camera) {
						if (success) {
							camera.takePicture(null, null, mPictureCallback);
						}
					}
				});

				return true;
			}
		} catch (Exception e) {
			// For some reason, the picture could not be taken.
			Toast.makeText(getActivity(), R.string.error_taking_picture, Toast.LENGTH_LONG).show();
			Log.e("Test", "Test", e);
		}

		return false;
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


	private class MyPictureCallback implements Camera.PictureCallback {

		@Override
		public void onPictureTaken(byte[] data, Camera camera) {
			mBtnCamera.setEnabled(false);
			(new SavePictureTask()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new BytesWrapper(data));
		}
	}

	private class BootCameraTask extends AsyncTask<Void, Void, Camera> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			mIcCamera.setVisibility(View.GONE);
			mBtnSetFlash.setVisibility(View.GONE);
			mBtnCamera.setEnabled(false);
			mOverlaySubtitle.setVisibility(View.GONE);
			mOverlay.setVisibility(View.GONE);
		}

		@Override
		protected Camera doInBackground(Void... params) {
			int id = CameraUtils.getDefaultBackFacingCameraId();
			Camera camera = CameraUtils.getCameraInstance(id);
			setCameraDisplayOrientation(getActivity(), camera, id);
			setCameraFocusMode(camera);
			setCameraBestPictureSize(camera);
			return camera;
		}

		@Override
		protected void onPostExecute(Camera camera) {
			super.onPostExecute(camera);
			if (camera != null) {
				mLoadingCamera.setVisibility(View.GONE);
				mPreview.setVisibility(View.VISIBLE);

				mCamera = camera;
				mPreview.setCamera(camera);
				setFlashMode();
				mBtnSetFlash.setVisibility(View.VISIBLE);
				if (mOverlayResourceId > 0) {
					PicassoUtils.PICASSO.load(mOverlayResourceId).fit().centerInside().into(mOverlay);
					mOverlay.setVisibility(View.VISIBLE);
				}

				if (!TextUtils.isEmpty(mOverlaySubtitleString)) {
					mOverlaySubtitle.setText(mOverlaySubtitleString);
					mOverlaySubtitle.setVisibility(View.VISIBLE);
				} else {
					mOverlaySubtitle.setVisibility(View.GONE);
				}

				mBtnCamera.setEnabled(true);
			}
		}
	}


	private class SavePictureTask extends AsyncTask<BytesWrapper, Void, File> {

		ProgressDialog mDialog;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			OrientationUtils.lockOrientation(getActivity());
			mSavingPicture.setVisibility(View.VISIBLE);
			mIcCamera.setVisibility(View.GONE);
			mBtnSetFlash.setVisibility(View.GONE);
			mOverlay.setVisibility(View.GONE);
			mBtnCamera.setEnabled(false);
			mDialog = ProgressDialog.show(getActivity(), "Saving picture", "Please wait...", true);
		}

		@Override
		protected File doInBackground(BytesWrapper... params) {
			// Save picture to file
			File file = CameraUtils.getOutputMediaFile(CameraUtils.MEDIA_TYPE_IMAGE);
			if (file != null) {
				FileOutputStream f = null;
				try {
					f = new FileOutputStream(file);
					f.write(params[0].bytes);
				} catch (IOException e) {
					Log.e("Test", "IOEx", e);
				} finally {
					if (f != null) {
						try {
							f.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}

			return file;
		}

		@Override
		protected void onPostExecute(File s) {
			super.onPostExecute(s);
			mDialog.dismiss();
			// Move to edit
			if (s != null && s.exists()) {
				CameraUtils.sendMediaScannerBroadcast(getActivity(), s);
				Bundle bundle = new Bundle();
				bundle.putString(TunnelFragment.KEY_COMMENT_PICTURE_PATH, s.getAbsolutePath());
				Log.d("Test", "File is = " + s.getAbsolutePath());
				OrientationUtils.unlockOrientation(getActivity());
				getTunnel().next(bundle);
			} else {
				Toast.makeText(getActivity(), R.string.error_taking_picture, Toast.LENGTH_LONG).show();
				mIcCamera.setVisibility(View.VISIBLE);
				mBtnSetFlash.setVisibility(View.VISIBLE);
				mOverlay.setVisibility(View.VISIBLE);
				mSavingPicture.setVisibility(View.GONE);
				mBtnCamera.setEnabled(true);
			}
		}
	}
}
