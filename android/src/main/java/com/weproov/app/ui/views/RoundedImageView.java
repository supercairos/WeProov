package com.weproov.app.ui.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;
import com.weproov.app.ui.drawables.CircleDrawable;

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
		if(drawable instanceof BitmapDrawable) {
			super.setImageDrawable(new CircleDrawable(((BitmapDrawable) drawable).getBitmap()));
		} else {
			super.setImageDrawable(drawable);
		}
	}

	@Override
	public void setImageBitmap(Bitmap bm) {
		super.setImageDrawable(new CircleDrawable(bm));
	}
}
