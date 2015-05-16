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

package com.weproov.app.utils;

import android.content.Context;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

// This class aggregates three gesture detectors: GestureDetector,
// ScaleGestureDetector, and DownUpDetector.
public class GestureRecognizer {

	public interface Listener {
//		boolean onSingleTapUp(float x, float y);

		boolean onDoubleTap(float x, float y);

		boolean onScroll(float dx, float dy, float totalX, float totalY);

//		boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY);

//		boolean onScaleBegin(float focusX, float focusY);

		boolean onScale(float focusX, float focusY, float scale);

//		void onScaleEnd();
	}

	private final GestureDetector mGestureDetector;
	private final ScaleGestureDetector mScaleDetector;

	private final Listener mListener;

	public GestureRecognizer(Context context, Listener listener) {
		mListener = listener;
		mGestureDetector = new GestureDetector(context, new MyGestureListener());
		mScaleDetector = new ScaleGestureDetector(context, new MyScaleListener());
	}

	public boolean onTouchEvent(MotionEvent event) {
		boolean res = mScaleDetector.onTouchEvent(event);
		if (!mScaleDetector.isInProgress()) {
			res = mGestureDetector.onTouchEvent(event);
		}

		return res;
	}

	private class MyGestureListener extends GestureDetector.SimpleOnGestureListener {

		@Override
		public boolean onDown(MotionEvent e) {
			return true;
		}

//		@Override
//		public boolean onSingleTapUp(MotionEvent e) {
//			Log.d("Test", "onSingleTapUp");
//			return mListener.onSingleTapUp(e.getX(), e.getY());
//		}

		@Override
		public boolean onDoubleTap(MotionEvent e) {
			Dog.d( "onDoubleTap");
			return mListener.onDoubleTap(e.getX(), e.getY());
		}

		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2, float dx, float dy) {
			Dog.d( "onScroll");
			return mListener.onScroll(dx, dy, e2.getX() - e1.getX(), e2.getY() - e1.getY());
		}

//		@Override
//		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
//			Log.d("Test", "onFling");
//			return mListener.onFling(e1, e2, velocityX, velocityY);
//		}
	}

	private class MyScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {

//		@Override
//		public boolean onScaleBegin(ScaleGestureDetector detector) {
//			Log.d("Test", "onScaleBegin");
//			return mListener.onScaleBegin(detector.getFocusX(), detector.getFocusY());
//		}

		@Override
		public boolean onScale(ScaleGestureDetector detector) {
			Dog.d( "onScale");
			return mListener.onScale(detector.getFocusX(), detector.getFocusY(), detector.getScaleFactor());
		}

//		@Override
//		public void onScaleEnd(ScaleGestureDetector detector) {
//			Log.d("Test", "onScaleEnd");
//			mListener.onScaleEnd();
//		}
	}
}
