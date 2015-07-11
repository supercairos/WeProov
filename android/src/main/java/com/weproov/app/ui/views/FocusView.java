package com.weproov.app.ui.views;

/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.content.Context;
import android.content.res.Resources;
import android.graphics.*;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import com.weproov.app.R;

public class FocusView extends View {


	public FocusView(Context context) {
		super(context);
		init(context);
	}

	public FocusView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public FocusView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	// Sometimes continuous autofocus starts and stops several times quickly.
	// These states are used to make sure the animation is run for at least some
	// time.
	private volatile int mState;
	private ScaleAnimation mAnimation = new ScaleAnimation();
	private static final int STATE_IDLE = 0;
	private static final int STATE_FOCUSING = 1;
	private static final int STATE_FINISHING = 2;

	private Runnable mDisappear = new Disappear();
	private Animation.AnimationListener mEndAction = new EndAction();

	private static final int SCALING_UP_TIME = 600;
	private static final int SCALING_DOWN_TIME = 100;
	private static final int DISAPPEAR_TIMEOUT = 200;

	private static final int SUCCESS_COLOR = Color.GREEN;
	private static final int FAIL_COLOR = Color.RED;

	private Paint mFocusPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

	private int mFocusX;
	private int mFocusY;

	private int mCenterX;
	private int mCenterY;

	private int mDialAngle = 0;
	private RectF mCircle = new RectF();
	private RectF mDial = new RectF();
	private Point mPoint1 = new Point();
	private Point mPoint2 = new Point();
	private int mStartAnimationAngle;

	private boolean mFocused;

	private int mCircleSize;
	private int mInnerOffset;
	private int mOuterStroke;
	private int mInnerStroke;

	private volatile boolean mFocusCancelled;

	private void init(Context ctx) {
		Resources res = ctx.getResources();
		mFocusPaint.setColor(Color.WHITE);
		mFocusPaint.setStyle(Paint.Style.STROKE);

		mCircleSize = res.getDimensionPixelSize(R.dimen.focus_circle_size);
		mInnerOffset = res.getDimensionPixelSize(R.dimen.focus_inner_offset);
		mOuterStroke = res.getDimensionPixelSize(R.dimen.focus_outer_stroke);
		mInnerStroke = res.getDimensionPixelSize(R.dimen.focus_inner_stroke);

		mState = STATE_IDLE;
	}

	@Override
	public void onDraw(Canvas canvas) {
		int state = canvas.save();
		drawFocus(canvas);
		canvas.restoreToCount(state);
	}

	public void setFocus(int x, int y) {
		mFocusX = x;
		mFocusY = y;
		setCircle(mFocusX, mFocusY);
	}

	public int getSize() {
		return 2 * mCircleSize;
	}

	private int getRandomRange() {
		return (int) (-60 + 120 * Math.random());
	}

	@Override
	public void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
		mCenterX = (r - l) / 2;
		mCenterY = (b - t) / 2;
		mFocusX = mCenterX;
		mFocusY = mCenterY;
		setCircle(mFocusX, mFocusY);
	}

	private void setCircle(int cx, int cy) {
		mCircle.set(cx - mCircleSize, cy - mCircleSize, cx + mCircleSize, cy + mCircleSize);
		mDial.set(cx - mCircleSize + mInnerOffset, cy - mCircleSize + mInnerOffset, cx + mCircleSize - mInnerOffset, cy + mCircleSize - mInnerOffset);
	}

	public void drawFocus(Canvas canvas) {
		mFocusPaint.setStrokeWidth(mOuterStroke);
		canvas.drawCircle((float) mFocusX, (float) mFocusY, (float) mCircleSize, mFocusPaint);

		int color = mFocusPaint.getColor();
		if (mState == STATE_FINISHING) {
			mFocusPaint.setColor(mFocused ? SUCCESS_COLOR : FAIL_COLOR);
		}

		mFocusPaint.setStrokeWidth(mInnerStroke);
		drawLine(canvas, mDialAngle, mFocusPaint);
		drawLine(canvas, mDialAngle + 45, mFocusPaint);
		drawLine(canvas, mDialAngle + 180, mFocusPaint);
		drawLine(canvas, mDialAngle + 225, mFocusPaint);

		canvas.save();

		// rotate the arc instead of its offset to better use framework's shape caching
		canvas.rotate(mDialAngle, mFocusX, mFocusY);
		canvas.drawArc(mDial, 0, 45, false, mFocusPaint);
		canvas.drawArc(mDial, 180, 45, false, mFocusPaint);
		canvas.restore();

		mFocusPaint.setColor(color);
	}

	private void drawLine(Canvas canvas, int angle, Paint p) {
		convertCart(angle, mCircleSize - mInnerOffset, mPoint1);
		convertCart(angle, mCircleSize - mInnerOffset + mInnerOffset / 3, mPoint2);
		canvas.drawLine(mPoint1.x + mFocusX, mPoint1.y + mFocusY, mPoint2.x + mFocusX, mPoint2.y + mFocusY, p);
	}

	private static void convertCart(int angle, int radius, Point out) {
		double a = 2 * Math.PI * (angle % 360) / 360;
		out.x = (int) (radius * Math.cos(a) + 0.5);
		out.y = (int) (radius * Math.sin(a) + 0.5);
	}

	public void showStart() {
		cancelFocus();
		mStartAnimationAngle = 67;
		int range = getRandomRange();
		startAnimation(SCALING_UP_TIME, false, mStartAnimationAngle, mStartAnimationAngle + range);
		mState = STATE_FOCUSING;
	}

	public void showSuccess(boolean timeout) {
		if (mState == STATE_FOCUSING) {
			startAnimation(SCALING_DOWN_TIME, timeout, mStartAnimationAngle);
			mState = STATE_FINISHING;
			mFocused = true;
		}
	}

	public void showFail(boolean timeout) {
		if (mState == STATE_FOCUSING) {
			startAnimation(SCALING_DOWN_TIME, timeout, mStartAnimationAngle);
			mState = STATE_FINISHING;
			mFocused = false;
		}
	}

	private void cancelFocus() {
		mFocusCancelled = true;
		removeCallbacks(mDisappear);
		if (mAnimation != null) {
			mAnimation.cancel();
		}
		mFocusCancelled = false;
		mFocused = false;
		mState = STATE_IDLE;
	}

	public void clear() {
		cancelFocus();
		post(mDisappear);
	}

	private void startAnimation(long duration, boolean timeout, float toScale) {
		startAnimation(duration, timeout, mDialAngle, toScale);
	}

	private void startAnimation(long duration, boolean timeout, float fromScale, float toScale) {
		mAnimation.reset();
		mAnimation.setDuration(duration);
		mAnimation.setScale(fromScale, toScale);
		mAnimation.setAnimationListener(timeout ? mEndAction : null);
		startAnimation(mAnimation);
		invalidate();
	}

	private class EndAction implements Animation.AnimationListener {
		@Override
		public void onAnimationEnd(Animation animation) {
			// Keep the focus indicator for some time.
			if (!mFocusCancelled) {
				postDelayed(mDisappear, DISAPPEAR_TIMEOUT);
			}
		}

		@Override
		public void onAnimationRepeat(Animation animation) {
		}

		@Override
		public void onAnimationStart(Animation animation) {
		}
	}

	private class Disappear implements Runnable {
		@Override
		public void run() {
			mFocusX = mCenterX;
			mFocusY = mCenterY;
			mState = STATE_IDLE;
			setCircle(mFocusX, mFocusY);
			mFocused = false;
		}
	}

	private class ScaleAnimation extends Animation {
		private float mFrom = 1f;
		private float mTo = 1f;

		public ScaleAnimation() {
			setFillAfter(true);
		}

		public void setScale(float from, float to) {
			mFrom = from;
			mTo = to;
		}

		@Override
		protected void applyTransformation(float interpolatedTime, Transformation t) {
			mDialAngle = (int) (mFrom + (mTo - mFrom) * interpolatedTime);
		}
	}
}

