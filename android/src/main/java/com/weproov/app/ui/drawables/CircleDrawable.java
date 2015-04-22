package com.weproov.app.ui.drawables;

import android.graphics.*;
import android.graphics.drawable.Drawable;
import android.util.Log;

public class CircleDrawable extends Drawable {

	private final RectF mRect = new RectF();

	private final Paint mBitmapPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
	private final Paint mColorPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
	private float mPadding;

	public CircleDrawable(Bitmap bitmap) {
		mBitmapPaint.setShader(new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));
		mColorPaint.setColor(Color.argb(255, 231, 231, 231));
	}

	@Override
	protected void onBoundsChange(Rect bounds) {
		super.onBoundsChange(bounds);
		mRect.set(0, 0, bounds.width(), bounds.height());
		mPadding = Math.min((float) bounds.width(), (float) bounds.height()) * (3f / 100f);

		Log.d("Test", "Padding is : " + mPadding);
	}

	@Override
	public void draw(Canvas canvas) {
		canvas.drawCircle(mRect.centerX(), mRect.centerY(), Math.min(mRect.centerX(), mRect.centerY()), mColorPaint);
		canvas.drawCircle(mRect.centerX(), mRect.centerY(), Math.min(mRect.centerX(), mRect.centerY()) - mPadding, mBitmapPaint);
	}

	@Override
	public int getOpacity() {
		return PixelFormat.TRANSLUCENT;
	}

	@Override
	public void setAlpha(int alpha) {
		mBitmapPaint.setAlpha(alpha);
	}

	@Override
	public void setColorFilter(ColorFilter cf) {
		mBitmapPaint.setColorFilter(cf);
	}
}
