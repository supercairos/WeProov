package com.weproov.app.ui.fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import com.weproov.app.R;
import com.weproov.app.ui.ifaces.CommandIface;
import com.weproov.app.utils.PrefUtils;

public class WeproovWelcomeFragment extends TunnelFragment implements CommandIface.OnClickListener {

	public static final String PREF_PASS_HELP = "pref_pass_help";
	private CheckBox mCheckBox;

	public WeproovWelcomeFragment() {
		// Required empty public constructor
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_weproov_welcome, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		mCheckBox = (CheckBox) view.findViewById(R.id.welcome_to_weproov_checkbox);
		mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				PrefUtils.putBoolean(PREF_PASS_HELP, isChecked);
			}
		});
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setCommmandListener(this);
	}

	@Override
	public void onPositiveButtonClicked(Button b) {
		getTunnel().next();
	}

	@Override
	public void onNegativeButtonClicked(Button b) {

	}
}
