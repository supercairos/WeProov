package com.weproov.app.ui.fragments.dialogs;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import butterknife.ButterKnife;


public abstract class BaseDialogFragment extends DialogFragment {

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);
    }

}
