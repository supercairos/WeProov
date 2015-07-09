package com.weproov.app.utils;

import android.graphics.Rect;
import android.graphics.RectF;

public class ConvertUtils {

	public static void rectFToRect(RectF rectF, Rect rect) {
		rect.left = Math.round(rectF.left);
		rect.top = Math.round(rectF.top);
		rect.right = Math.round(rectF.right);
		rect.bottom = Math.round(rectF.bottom);
	}
}
