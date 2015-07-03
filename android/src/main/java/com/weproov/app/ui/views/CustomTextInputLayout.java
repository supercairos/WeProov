package com.weproov.app.ui.views;

import android.content.Context;
import android.support.design.widget.TextInputLayout;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.EditText;

public class CustomTextInputLayout extends TextInputLayout {

	public CustomTextInputLayout(Context context) {
		super(context);
	}

	public CustomTextInputLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public void addView(final View child, int index, ViewGroup.LayoutParams params) {
		if (child instanceof EditText) {
			// TextInputLayout updates mCollapsingTextHelper bounds on onLayout. but Edit text is not layouted.
			child.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

				@Override
				public void onGlobalLayout() {
					onLayout(false, getLeft(), getTop(), getRight(), getBottom());
					child.getViewTreeObserver().removeOnGlobalLayoutListener(this);
				}
			});
		}

		super.addView(child, index, params);
	}
}
