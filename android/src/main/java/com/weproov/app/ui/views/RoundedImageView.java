package com.weproov.app.ui.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.util.AttributeSet;
import android.widget.ImageView;

public class RoundedImageView extends ImageView {


	public RoundedImageView(Context context) {
		super(context);
	}

	public RoundedImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public RoundedImageView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	@Override
	public void setImageDrawable(Drawable drawable) {
		if (drawable instanceof BitmapDrawable) {
			Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
			super.setImageDrawable(getDrawable(bitmap));
		} else {
			super.setImageDrawable(drawable);
		}
	}

	@Override
	public void setImageBitmap(Bitmap bm) {
		super.setImageDrawable(getDrawable(bm));
	}

	private RoundedBitmapDrawable getDrawable(Bitmap bitmap) {
		RoundedBitmapDrawable dr = RoundedBitmapDrawableFactory.create(getResources(), bitmap);
		dr.setCornerRadius(Math.max(bitmap.getWidth(), bitmap.getHeight()) / 2.0f);

		return dr;
	}
}
