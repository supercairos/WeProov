package com.weproov.app.ui.drawables;

import android.graphics.*;
import android.graphics.drawable.Drawable;

public class CircleDrawable extends Drawable {

    private final float mCornerRadius;
    private final RectF mRect = new RectF();

    private final BitmapShader mBitmapShader;
    private final Paint mPaint;

    public CircleDrawable(Bitmap bitmap) {
        mCornerRadius = bitmap.getHeight() > bitmap.getWidth() ? bitmap.getWidth() / 2 : bitmap.getHeight() / 2;

        mBitmapShader = new BitmapShader(bitmap,
                Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setShader(mBitmapShader);
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);
        mRect.set(0, 0, bounds.width(), bounds.height());
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawCircle(mRect.centerX(), mRect.centerY(), mCornerRadius, mPaint);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    @Override
    public void setAlpha(int alpha) {
        mPaint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        mPaint.setColorFilter(cf);
    }
}
