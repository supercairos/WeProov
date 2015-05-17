package com.weproov.app.ui.views;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Choreographer;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import com.weproov.app.ui.glrender.BasicTexture;
import com.weproov.app.ui.glrender.GLES20Canvas;
import com.weproov.app.utils.Dog;
import com.weproov.app.utils.GestureRecognizer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class TiledImageView extends FrameLayout implements GestureRecognizer.Listener {

	private static final boolean USE_TEXTURE_VIEW = false;
	private static final boolean USE_CHOREOGRAPHER = Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN;

	private final GestureRecognizer mGestureRecognizer;

	private BlockingGLTextureView mTextureView;
	private GLSurfaceView mGLSurfaceView;

	private boolean mInvalPending = false;
	private Choreographer.FrameCallback mFrameCallback;

	@Override
	public boolean onDoubleTap(float x, float y) {
		if (mRenderer.scale < 1) {
			mRenderer.centerY = (int) (getInvertedScaleY() * y);
			mRenderer.centerX = (int) (getInvertedScaleX() * x);
			mRenderer.scale = mRenderer.scale * 1.1f;
		} else {
			updateScaleIfNecessaryLocked(mRenderer);
		}

		invalidate();

		Dog.d("Double tap : %s", mRenderer);
		return true;
	}

	@Override
	public boolean onScroll(float dx, float dy, float totalX, float totalY) {
		mRenderer.centerY = mRenderer.centerY + (int) (getInvertedScaleY() * dy);
		mRenderer.centerX = mRenderer.centerX + (int) (getInvertedScaleX() * dx);
		invalidate();

		Dog.d("onScroll : %s", mRenderer);
		return true;
	}

	@Override
	public boolean onScale(float focusX, float focusY, float scale) {
		mRenderer.centerY = (int) (getInvertedScaleY() * focusY);
		mRenderer.centerX = (int) (getInvertedScaleX() * focusX);
		mRenderer.scale = mRenderer.scale * scale;
		invalidate();

		Dog.d("onScale : %d %d %d", focusX, focusY, scale);
		Dog.d("onScale : %s", mRenderer);
		return true;
	}

	private static class ImageRendererWrapper {
		// Guarded by locks
		float scale;
		int centerX, centerY;
		int rotation;
		TiledImageRenderer.TileSource source;
		Runnable isReadyCallback;
		// GL thread only
		TiledImageRenderer image;

		@Override
		public String toString() {
			return "ImageRendererWrapper{" +
					"scale=" + scale +
					", centerX=" + centerX +
					", centerY=" + centerY +
					", rotation=" + rotation +
					'}';
		}
	}

	private float[] mValues = new float[9];

	// -------------------------
	// Guarded by mLock
	// -------------------------
	private final Object mLock = new Object();
	private ImageRendererWrapper mRenderer;

	public TiledImageView(Context context) {
		this(context, null);
	}

	public TiledImageView(Context context, AttributeSet attrs) {
		super(context, attrs);

		mRenderer = new ImageRendererWrapper();
		mRenderer.image = new TiledImageRenderer(this);
		View view;
		if (USE_TEXTURE_VIEW) {
			mTextureView = new BlockingGLTextureView(context);
			mTextureView.setRenderer(new TileRenderer());
			view = mTextureView;
		} else {
			mGLSurfaceView = new GLSurfaceView(context);
			mGLSurfaceView.setEGLContextClientVersion(2);
			mGLSurfaceView.setRenderer(new TileRenderer());
			mGLSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
			view = mGLSurfaceView;
		}

		addView(view, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

		//setTileSource(new ColoredTiles());

		mGestureRecognizer = new GestureRecognizer(getContext(), this);
	}

	public void destroy() {
		if (USE_TEXTURE_VIEW) {
			mTextureView.destroy();
		} else {
			mGLSurfaceView.queueEvent(mFreeTextures);
		}
	}

	private Runnable mFreeTextures = new Runnable() {
		@Override
		public void run() {
			mRenderer.image.freeTextures();
		}
	};

	public void onPause() {
		if (!USE_TEXTURE_VIEW) {
			mGLSurfaceView.onPause();
		}
	}

	public void onResume() {
		if (!USE_TEXTURE_VIEW) {
			mGLSurfaceView.onResume();
		}
	}

	public void setTileSource(TiledImageRenderer.TileSource source, Runnable isReadyCallback) {
		synchronized (mLock) {
			mRenderer.source = source;
			mRenderer.isReadyCallback = isReadyCallback;
			mRenderer.centerX = source != null ? source.getImageWidth() / 2 : 0;
			mRenderer.centerY = source != null ? source.getImageHeight() / 2 : 0;
			mRenderer.rotation = source != null ? source.getRotation() : 0;
			mRenderer.scale = 0;
			updateScaleIfNecessaryLocked(mRenderer);
		}
		invalidate();
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		synchronized (mLock) {
			updateScaleIfNecessaryLocked(mRenderer);
		}
	}

	private float getInvertedScaleX() {
		return 1 / mRenderer.scale;
	}

	private float getInvertedScaleY() {
		return 1 / mRenderer.scale;
	}

	private void updateScaleIfNecessaryLocked(ImageRendererWrapper renderer) {
		if (renderer == null || renderer.source == null
				|| renderer.scale > 0 || getWidth() == 0) {
			return;
		}
		renderer.scale = Math.min(
				(float) getWidth() / (float) renderer.source.getImageWidth(),
				(float) getHeight() / (float) renderer.source.getImageHeight()
		);
	}

	@Override
	protected void dispatchDraw(Canvas canvas) {
		if (USE_TEXTURE_VIEW) {
			mTextureView.render();
		}
		super.dispatchDraw(canvas);
	}

	@Override
	public void invalidate() {
		if (USE_TEXTURE_VIEW) {
			super.invalidate();
			mTextureView.invalidate();
		} else {
			if (USE_CHOREOGRAPHER) {
				invalOnVsync();
			} else {
				mGLSurfaceView.requestRender();
			}
		}
	}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	private void invalOnVsync() {
		if (!mInvalPending) {
			mInvalPending = true;
			if (mFrameCallback == null) {
				mFrameCallback = new Choreographer.FrameCallback() {
					@Override
					public void doFrame(long frameTimeNanos) {
						mInvalPending = false;
						mGLSurfaceView.requestRender();
					}
				};
			}
			Choreographer.getInstance().postFrameCallback(mFrameCallback);
		}
	}

	private RectF mTempRectF = new RectF();

	public void positionFromMatrix(Matrix matrix) {
		if (mRenderer.source != null) {
			final int rotation = mRenderer.source.getRotation();

			final boolean swap = !(rotation % 180 == 0);
			final int width = swap ? mRenderer.source.getImageHeight() : mRenderer.source.getImageWidth();
			final int height = swap ? mRenderer.source.getImageWidth() : mRenderer.source.getImageHeight();

			mTempRectF.set(0, 0, width, height);
			matrix.mapRect(mTempRectF);
			matrix.getValues(mValues);
			int cx = width / 2;
			int cy = height / 2;
			float scale = mValues[Matrix.MSCALE_X];

			int xoffset = Math.round((getWidth() - mTempRectF.width()) / 2 / scale);
			int yoffset = Math.round((getHeight() - mTempRectF.height()) / 2 / scale);

			if (rotation == 90 || rotation == 180) {
				cx += (mTempRectF.left / scale) - xoffset;
			} else {
				cx -= (mTempRectF.left / scale) - xoffset;
			}

			if (rotation == 180 || rotation == 270) {
				cy += (mTempRectF.top / scale) - yoffset;
			} else {
				cy -= (mTempRectF.top / scale) - yoffset;
			}

			mRenderer.scale = scale;
			mRenderer.centerX = swap ? cy : cx;
			mRenderer.centerY = swap ? cx : cy;
			invalidate();
		}
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		return true;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		Dog.d(" onTouchEvent()");
		return mGestureRecognizer.onTouchEvent(event);
	}

	private class TileRenderer implements GLSurfaceView.Renderer {
		private GLES20Canvas mCanvas;

		@Override
		public void onSurfaceCreated(GL10 gl, EGLConfig config) {
			mCanvas = new GLES20Canvas();
			BasicTexture.invalidateAllTextures();
			mRenderer.image.setModel(mRenderer.source, mRenderer.rotation);
		}

		@Override
		public void onSurfaceChanged(GL10 gl, int width, int height) {
			mCanvas.setSize(width, height);
			mRenderer.image.setViewSize(width, height);
		}

		@Override
		public void onDrawFrame(GL10 gl) {
			mCanvas.clearBuffer();
			Runnable readyCallback;
			synchronized (mLock) {
				readyCallback = mRenderer.isReadyCallback;
				mRenderer.image.setModel(mRenderer.source, mRenderer.rotation);
				mRenderer.image.setPosition(mRenderer.centerX, mRenderer.centerY,
						mRenderer.scale);
			}

			boolean complete = mRenderer.image.draw(mCanvas);
			if (complete && readyCallback != null) {
				synchronized (mLock) {
					// Make sure we don't trample on a newly set callback/source
					// if it changed while we were rendering
					if (mRenderer.isReadyCallback == readyCallback) {
						mRenderer.isReadyCallback = null;
					}
				}

				post(readyCallback);
			}
		}
	}
}
