package com.weproov.app.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.OnClick;
import com.weproov.app.R;
import com.weproov.app.ui.ifaces.Tunnelface;

public class RenterFragment extends BaseFragment {

    public RenterFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_renter_info, container, false);
    }

    @OnClick(R.id.button_positive)
    public void onOkClicked() {
        ((Tunnelface) getActivity()).next();
    }

    @OnClick(R.id.button_negative)
    public void onCancelClicked() {
        // Goto dashboard
    }
}
