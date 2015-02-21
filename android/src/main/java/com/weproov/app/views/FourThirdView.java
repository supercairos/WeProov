package com.weproov.app.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

public class FourThirdView extends RelativeLayout {

    public FourThirdView(Context context) {
        super(context);
    }

    public FourThirdView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FourThirdView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int size;
        if (widthMode == MeasureSpec.EXACTLY && widthSize > 0) {
            size = widthSize;
        } else if (heightMode == MeasureSpec.EXACTLY && heightSize > 0) {
            size = heightSize;
        } else {
            size = widthSize < heightSize ? widthSize : heightSize;
        }

        int finaWidthlMeasureSpec = MeasureSpec.makeMeasureSpec(widthSize > heightSize ? (size * 4) / 3 : size, MeasureSpec.EXACTLY);
        int finaHeightlMeasureSpec = MeasureSpec.makeMeasureSpec(heightSize > widthSize ? (size * 4) / 3 : size, MeasureSpec.EXACTLY);
        super.onMeasure(finaWidthlMeasureSpec, finaHeightlMeasureSpec);
    }
}
