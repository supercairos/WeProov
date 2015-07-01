package com.weproov.app.ui.views;

import android.content.Context;
import android.graphics.*;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import com.weproov.app.utils.PixelUtils;

public class FingerPaintView extends View {

	private static final float STROKE_SIZE = PixelUtils.convertDpToPixel(5);

	private Bitmap mBitmap;
	private Canvas mCanvas;
	private Path mPath;
	private final Paint mBitmapPaint = new Paint(Paint.DITHER_FLAG);
	private final Paint mFingerPaint = new Paint(Paint.DITHER_FLAG | Paint.ANTI_ALIAS_FLAG);

	public FingerPaintView(Context context) {
		super(context);
		init();
	}

	public FingerPaintView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public FingerPaintView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public Bitmap getBitmap() {
		return mBitmap;
	}

	private void init() {
		mBitmap = Bitmap.createBitmap(320, 480, Bitmap.Config.ARGB_8888);
		mCanvas = new Canvas(mBitmap);
		mPath = new Path();

		mFingerPaint.setColor(Color.WHITE);
		mFingerPaint.setStyle(Paint.Style.STROKE);
		mFingerPaint.setStrokeJoin(Paint.Join.ROUND);
		mFingerPaint.setStrokeCap(Paint.Cap.ROUND);
		mFingerPaint.setStrokeWidth(STROKE_SIZE);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);

		if (h != oldh && w != oldw) {
			Bitmap newBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
			mCanvas = new Canvas(newBitmap);
			mCanvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);

			mBitmap.recycle();
			mBitmap = newBitmap;
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// canvas.drawColor(0xFFAAAAAA);
		canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
		canvas.drawPath(mPath, mFingerPaint);
	}

	private float mX, mY;
	private static final float TOUCH_TOLERANCE = 4;

	private void touch_start(float x, float y) {
		mPath.reset();
		mPath.moveTo(x, y);
		mX = x;
		mY = y;
	}

	private void touch_move(float x, float y) {
		float dx = Math.abs(x - mX);
		float dy = Math.abs(y - mY);
		if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
			mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
			mX = x;
			mY = y;
		}
	}

	private void touch_up() {
		mPath.lineTo(mX, mY);
		// commit the path to our offscreen
		mCanvas.drawPath(mPath, mFingerPaint);
		// kill this so we don't double draw
		mPath.reset();
	}

	@Override
	public boolean onTouchEvent(@NonNull MotionEvent event) {
		float x = event.getX();
		float y = event.getY();
		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				touch_start(x, y);
				invalidate();
				break;
			case MotionEvent.ACTION_MOVE:
				touch_move(x, y);
				invalidate();
				break;
			case MotionEvent.ACTION_UP:
				touch_up();
				invalidate();
				break;
		}
		return true;
	}

	public void clear() {
		mCanvas.drawColor(0x00FFFFFF, PorterDuff.Mode.CLEAR);
		invalidate();
	}
}
