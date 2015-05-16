/**
 * Copyright 2013 Romain Guy
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.weproov.app.ui.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import com.weproov.app.R;
import com.weproov.app.utils.PixelUtils;
import com.weproov.app.utils.SvgHelper;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"ForLoopReplaceableByForEach", "UnusedDeclaration"})
public class WeproovLogoView extends View {
	private static final String LOG_TAG = "IntroView";

	public static final int TRACE_TIME = 2000;
	private static final int TRACE_TIME_PER_GLYPH = 1200;
	private static final int FILL_START = 1500;
	public static final int FILL_TIME = 1500;

	public static final int STATE_NOT_STARTED = 0;
	public static final int STATE_TRACE_STARTED = 1;
	public static final int STATE_FILL_STARTED = 2;
	public static final int STATE_FINISHED = 3;

	private static final float MARKER_SIZE = PixelUtils.convertDpToPixel(16);


	private static final Interpolator INTERPOLATOR = new DecelerateInterpolator();

	private long mStartTime;
	private int mState = STATE_NOT_STARTED;
	private OnStateChangeListener mOnStateChangeListener;

	private final Paint mStrokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
	private final Paint mPointPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
	private final Paint mFillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

	private final SvgHelper mSvg = new SvgHelper();
	private int mSvgResource;

	private final Object mSvgLock = new Object();
	private List<SvgHelper.SvgPath> mPaths = new ArrayList<>(0);
	private Thread mLoader;

	public interface OnStateChangeListener {
		void onStateChange(int state);
	}

	public WeproovLogoView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public WeproovLogoView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.WeproovLogoView, defStyle, 0);
		try {
			if (a != null) {

				float width = a.getFloat(R.styleable.WeproovLogoView_strokeWidth, 1.0f);
				int strokeColor = a.getColor(R.styleable.WeproovLogoView_strokeColor, 0xff000000);

				mStrokePaint.setStrokeWidth(width);
				mStrokePaint.setColor(Color.argb(128, Color.red(strokeColor), Color.green(strokeColor), Color.blue(strokeColor)));

				mPointPaint.setStrokeWidth(width);
				mPointPaint.setColor(strokeColor);

				int fillColor = a.getColor(R.styleable.WeproovLogoView_fillColor, 0xff000000);

				mFillPaint.setColor(fillColor);

				setSvgResource(a.getResourceId(R.styleable.WeproovLogoView_resource, 0));
			}
		} finally {
			if (a != null) a.recycle();
		}

		init();
	}

	private void init() {
		mStrokePaint.setStyle(Paint.Style.STROKE);
		mPointPaint.setStyle(Paint.Style.STROKE);
		mFillPaint.setStyle(Paint.Style.FILL);

		// Note: using a software layer here is an optimization. This view works with
		// hardware accelerated rendering but every time a path is modified (when the
		// dash path effect is modified), the graphics pipeline will rasterize the path
		// again in a new texture. Since we are dealing with dozens of paths, it is much
		// more efficient to rasterize the entire view into a single re-usable texture
		// instead. Ideally this should be toggled using a heuristic based on the number
		// and or dimensions of paths to render.
		// Note that PathDashPathEffects can lead to clipping issues with hardware rendering.
		setLayerType(LAYER_TYPE_SOFTWARE, null);
	}

	public void setSvgResource(int resource) {
		if (mSvgResource == 0) {
			mSvgResource = resource;
		}
	}

	@SuppressLint("DrawAllocation")
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (mState == STATE_NOT_STARTED || mPaths == null || mPaths.size() == 0) {
			return;
		}

		long t = System.currentTimeMillis() - mStartTime;

		synchronized (mSvgLock) {
			canvas.save();

			canvas.translate(getPaddingLeft(), getPaddingTop() - getPaddingBottom());
			final int count = mPaths.size();

			for (int i = 0; i < count; i++) {
				SvgHelper.SvgPath svgPath = mPaths.get(i);

				float phase = constrain(0, 1, (t - (TRACE_TIME - TRACE_TIME_PER_GLYPH) * i * 1f / mPaths.size()) * 1f / TRACE_TIME_PER_GLYPH);
				float distance = INTERPOLATOR.getInterpolation(phase) * svgPath.length;

				mStrokePaint.setPathEffect(new DashPathEffect(new float[]{distance, svgPath.length}, 0));
				canvas.drawPath(svgPath.path, mStrokePaint);

				mPointPaint.setPathEffect(new DashPathEffect(new float[]{0, distance, phase > 0 ? MARKER_SIZE : 0, svgPath.length}, 0));
				canvas.drawPath(svgPath.path, mPointPaint);
			}

			if (t > FILL_START) {
				if (mState < STATE_FILL_STARTED) {
					changeState(STATE_FILL_STARTED);
				}

				// If after fill start, draw fill
				float phase = constrain(0, 1, (t - FILL_START) * 1f / FILL_TIME);
				mFillPaint.setARGB((int) (phase * 255), 255, 255, 255);
				for (int i = 0; i < count; i++) {
					SvgHelper.SvgPath svgPath = mPaths.get(i);
					canvas.drawPath(svgPath.path, mFillPaint);
				}
			}

			canvas.restore();
		}


		if (t < FILL_START + FILL_TIME) {
			// draw next frame if animation isn't finished
			postInvalidateOnAnimation();
		} else {
			changeState(STATE_FINISHED);
		}
	}

	public static float constrain(float min, float max, float v) {
		return Math.max(min, Math.min(max, v));
	}

	@Override
	protected void onSizeChanged(final int w, final int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);

		if (mLoader != null) {
			try {
				mLoader.join();
			} catch (InterruptedException e) {
				Log.e(LOG_TAG, "Unexpected error", e);
			}
		}

		mLoader = new Thread(new Runnable() {
			@Override
			public void run() {
				mSvg.load(getContext(), mSvgResource);
				synchronized (mSvgLock) {
					mPaths = mSvg.getPathsForViewport(
							w - getPaddingLeft() - getPaddingRight(),
							h - getPaddingTop() - getPaddingBottom()
					);

					if (mState == STATE_TRACE_STARTED) {
						// Restart it;
						start();
					}
				}
			}
		}, "SVG Loader");
		mLoader.start();
	}

	public void start() {
		mStartTime = System.currentTimeMillis();
		changeState(STATE_TRACE_STARTED);
		postInvalidateOnAnimation();
	}

	public void reset() {
		mStartTime = 0;
		mPaths = null;
		changeState(STATE_NOT_STARTED);
		postInvalidateOnAnimation();
	}

	private void changeState(int state) {
		if (mState == state) {
			return;
		}

		mState = state;
		if (mOnStateChangeListener != null) {
			mOnStateChangeListener.onStateChange(state);
		}
	}

	public void setOnStateChangeListenerListener(OnStateChangeListener listener) {
		mOnStateChangeListener = listener;
	}
}
