package com.weproov.app.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import butterknife.InjectView;
import butterknife.OnClick;
import com.weproov.app.R;
import com.weproov.app.ui.views.FingerPaintView;

/**
 * Created by Romain on 30/01/2015.
 */
public class SignatureFragment extends BaseFragment {


    @InjectView(R.id.finger_paint_view)
    FingerPaintView mFingerPaintView;

    @InjectView(R.id.button_clear)
    Button mClearButton;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_signature, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @OnClick(R.id.button_clear)
    public void onClearClicked() {
        mFingerPaintView.clear();
    }
}
