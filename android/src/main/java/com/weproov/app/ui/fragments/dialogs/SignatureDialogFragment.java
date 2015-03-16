package com.weproov.app.ui.fragments.dialogs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.InjectView;
import com.weproov.app.R;
import com.weproov.app.ui.views.FingerPaintView;

/**
 * Created by Romain on 30/01/2015.
 */
public class SignatureDialogFragment extends BaseDialogFragment {


    @InjectView(R.id.finger_paint_view)
    FingerPaintView mFingerPaintView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_signature, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }


}
