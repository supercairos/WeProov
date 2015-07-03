package com.weproov.app.ui.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class FocusView extends View {

	private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
	private Handler mHander = new Handler();
	private List<RectF> mRects = new ArrayList<>();

	public void addRect(final RectF rect) {
		mRects.add(rect);
		mHander.postDelayed(new Runnable() {
			@Override
			public void run() {
				mRects.remove(rect);
				invalidate();
			}
		}, 2000);
		invalidate();
	}

	public FocusView(Context context) {
		super(context);
	}

	public FocusView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public FocusView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		for(RectF rect: mRects) {
			canvas.drawRect(rect, mPaint);
		}
	}
}
