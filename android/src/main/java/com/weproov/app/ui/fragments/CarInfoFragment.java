package com.weproov.app.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import butterknife.InjectView;
import butterknife.OnClick;
import com.weproov.app.R;
import com.weproov.app.ui.ifaces.Tunnelface;


public class CarInfoFragment extends BaseFragment {

    private String[] mCarType;

    @InjectView(R.id.car_type_spinner)
    Spinner mSpinner;

    public CarInfoFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mCarType = getResources().getStringArray(R.array.car_type);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_car_info, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, mCarType);
        mSpinner.setAdapter(adapter);
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
