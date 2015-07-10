package com.weproov.app.ui.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.media.MediaActionSound;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.*;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.InjectView;
import butterknife.OnClick;
import com.weproov.app.R;
import com.weproov.app.logic.controllers.FocusOverlayManager;
import com.weproov.app.ui.ifaces.ActionBarIface;
import com.weproov.app.ui.views.CameraPreviewView;
import com.weproov.app.utils.CameraUtils;
import com.weproov.app.utils.Dog;
import com.weproov.app.utils.OrientationUtils;
import com.weproov.app.utils.PicassoUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class CameraFragment extends TunnelFragment implements Camera.AutoFocusCallback, Camera.AutoFocusMoveCallback,Camera.PictureCallback {

	/**
	 * Arguments keys *,
	 */
	public static final String KEY_OVERLAY_PICTURE_SUBTITLE = "key_overlay_picture_subtitle";
	public static final String KEY_OVERLAY_PICTURE = "key_overlay_picture";
	public static final String KEY_OVERLAY_MINI_PICTURE = "key_overlay_mini_picture";

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

	@InjectView(R.id.camera_overlay_mini)
	ImageView mOverlayMini;

	@InjectView(R.id.camera_overlay_subtitle)
	TextView mOverlaySubtitle;

	@InjectView(R.id.camera_preview)
	CameraPreviewView mPreview;

	private int mOverlayMiniResourceId;
	private int mOverlayResourceId;
	private String mOverlaySubtitleString;


	private ActionBarIface mActionBarListener;
	private Camera mCamera;
	private final MediaActionSound mMedia = new MediaActionSound();

	private FocusOverlayManager mFocusOverlayManager;
	private GestureDetector.SimpleOnGestureListener mDetector = new GestureDetector.SimpleOnGestureListener() {
		@Override
		public boolean onSingleTapUp(MotionEvent e) {
			return mFocusOverlayManager.onSingleTapUp((int) e.getX(), (int) e.getY());
		}
	};

	private static class BytesWrapper {

		private byte[] bytes;

		public BytesWrapper(byte[] bytes) {
			this.bytes = bytes;
		}
	}

	public static CameraFragment newInstance(int overlay, int overlayMini, String subtitle) {

		Bundle bundle = new Bundle();
		bundle.putInt(KEY_OVERLAY_PICTURE, overlay);
		bundle.putInt(KEY_OVERLAY_MINI_PICTURE, overlayMini);
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
			mOverlayMiniResourceId = getArguments().getInt(KEY_OVERLAY_MINI_PICTURE, 0);
			mOverlaySubtitleString = getArguments().getString(KEY_OVERLAY_PICTURE_SUBTITLE);
		}

		mMedia.load(MediaActionSound.FOCUS_COMPLETE);
		mMedia.load(MediaActionSound.SHUTTER_CLICK);
		Dog.d("Got arguments : %d %d %s", mOverlayResourceId, mOverlayMiniResourceId, mOverlaySubtitleString);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// Prevent software keyboard or voice search from showing up.
		if (keyCode == KeyEvent.KEYCODE_SEARCH || keyCode == KeyEvent.KEYCODE_MENU) {
			if (event.isLongPress()) return true;
		}

		if (keyCode == KeyEvent.KEYCODE_MENU) {
			return true;
		}

		return super.onKeyDown(keyCode, event);
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
		OrientationUtils.unlockOrientation(getActivity());
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
			Dog.d("setCameraDisplayOrientation - camera null");
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

		Dog.d("Result = %s", result);

		Camera.Parameters params = camera.getParameters();
		params.setRotation(result);
		camera.setParameters(params);
		camera.setDisplayOrientation(result);

	}

	private static void setCameraFocusMode(Camera camera) {
		// get Camera parameters
		Camera.Parameters params = camera.getParameters();
		params.setMeteringAreas(null);
		params.setFocusAreas(null);
		List<String> focusModes = params.getSupportedFocusModes();
		if (focusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
			params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
			camera.setParameters(params);
		} else if (focusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)) {
			params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
			camera.setParameters(params);
		} else if (focusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
			params.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
			camera.setParameters(params);
		}
	}

	private static void setCameraJPEGQuality(Camera camera) {
		// get Camera parameters
		Camera.Parameters params = camera.getParameters();
		params.setJpegQuality(100);
		params.setPictureFormat(ImageFormat.JPEG);
		camera.setParameters(params);
	}

	private static void setCameraPictureSize(Camera camera) {
		// get Camera parameters
		Camera.Parameters params = camera.getParameters();
		Camera.Size size = CameraUtils.getBestPictureSize(params.getSupportedPictureSizes());
		params.setPictureSize(size.width, size.height);
		camera.setParameters(params);
	}

	private static void setCameraFlashMode(Camera camera) {
		// get Camera parameters
		Camera.Parameters params = camera.getParameters();
		List<String> modes = params.getSupportedFlashModes();
		if (modes != null && modes.contains(Camera.Parameters.FLASH_MODE_AUTO)) {
			params.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
		}
	}

	private static void setCameraSceneMode(Camera camera) {
		// get Camera parameters
		Camera.Parameters params = camera.getParameters();
		List<String> modes = params.getSupportedSceneModes();
		if (modes != null) {
			if (modes.contains(Camera.Parameters.SCENE_MODE_LANDSCAPE)) {
				params.setSceneMode(Camera.Parameters.SCENE_MODE_LANDSCAPE);
			} else if (modes.contains(Camera.Parameters.SCENE_MODE_AUTO)) {
				params.setSceneMode(Camera.Parameters.SCENE_MODE_AUTO);
			}
		}
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
		} else {
			// No flash on device
			mBtnSetFlash.setVisibility(View.GONE);
		}
	}

	private void releaseCamera() {
		mPreview.setCamera(null);
		if (mCamera != null) {
			mCamera.stopPreview();
			mCamera.release();        // release the camera for other applications
            mFocusOverlayManager.onCameraReleased();
			mCamera = null;
		}
	}


	@OnClick(R.id.btn_camera)
	public void onCameraButtonClicked() {
		try {
			if (mCamera != null) {
				mCamera.takePicture(null, null, this);
			}
		} catch (Exception e) {
			// For some reason, the picture could not be taken.
			Toast.makeText(getActivity(), R.string.error_taking_picture, Toast.LENGTH_LONG).show();
			Dog.e(e, "Test");
		}
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

	@Override
	public void onAutoFocus(boolean success, Camera camera) {
		if (success) {
			mMedia.play(MediaActionSound.FOCUS_COMPLETE);
			camera.cancelAutoFocus();
			setCameraFocusMode(camera);
		}
		mFocusOverlayManager.onAutoFocus(success, false);
	}

	@Override
	public void onAutoFocusMoving(boolean start, Camera camera) {
		mFocusOverlayManager.onAutoFocusMoving(start);
	}

	@Override
	public void onPictureTaken(byte[] data, Camera camera) {
		(new SavePictureTask()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new BytesWrapper(data));
	}

	private class BootCameraTask extends AsyncTask<Void, Void, Camera> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			mBtnSetFlash.setVisibility(View.GONE);
			mBtnCamera.setEnabled(false);
			mOverlaySubtitle.setVisibility(View.GONE);
			mOverlay.setVisibility(View.GONE);
			mOverlayMini.setVisibility(View.GONE);
		}

		@Override
		protected Camera doInBackground(Void... params) {
			int id = CameraUtils.getDefaultBackFacingCameraId();
			Camera camera = CameraUtils.getCameraInstance(id);
			setCameraDisplayOrientation(getActivity(), camera, id);
			setCameraFocusMode(camera);
			setCameraPictureSize(camera);
			setCameraSceneMode(camera);
			setCameraJPEGQuality(camera);
			setCameraFlashMode(camera);

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
					mOverlay.setVisibility(View.VISIBLE);
					PicassoUtils.PICASSO.load(mOverlayResourceId).fit().centerInside().into(mOverlay);
				}

				if (mOverlayMiniResourceId > 0) {
					mOverlayMini.setVisibility(View.VISIBLE);
					PicassoUtils.PICASSO.load(mOverlayMiniResourceId).fit().centerInside().into(mOverlayMini);
				}

				if (!TextUtils.isEmpty(mOverlaySubtitleString)) {
					mOverlaySubtitle.setText(mOverlaySubtitleString);
					mOverlaySubtitle.setVisibility(View.VISIBLE);
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
			mOverlayMini.setVisibility(View.GONE);
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
					Dog.e(e, "IOEx");
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
			mMedia.play(MediaActionSound.SHUTTER_CLICK);
			mDialog.dismiss();
			// Move to edit
			if (s != null && s.exists()) {
				CameraUtils.sendMediaScannerBroadcast(getActivity().getApplicationContext(), s);
				Bundle bundle = new Bundle();
				bundle.putString(TunnelFragment.KEY_COMMENT_PICTURE_PATH, s.getAbsolutePath());
				Dog.d("File is = %s", s.getAbsolutePath());
				OrientationUtils.unlockOrientation(getActivity());
				getTunnel().next(bundle);
			} else {
				Toast.makeText(getActivity().getApplicationContext(), R.string.error_taking_picture, Toast.LENGTH_LONG).show();
				mIcCamera.setVisibility(View.VISIBLE);
				mBtnSetFlash.setVisibility(View.VISIBLE);
				mOverlay.setVisibility(View.VISIBLE);
				mOverlayMini.setVisibility(View.VISIBLE);
				mSavingPicture.setVisibility(View.GONE);
				mBtnCamera.setEnabled(true);
			}
		}
	}
}
