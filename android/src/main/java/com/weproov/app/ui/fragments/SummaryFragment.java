package com.weproov.app.ui.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import butterknife.InjectView;
import com.weproov.app.R;
import com.weproov.app.models.WeProov;
import com.weproov.app.ui.ifaces.CommandIface;

public class SummaryFragment extends TunnelFragment implements CommandIface.OnClickListener {

	private WeProov mWeProov;

	private static final String KEY_WEPROOV = "key_weproov";
	private String mName;

	public static SummaryFragment newInstance(WeProov weProov) {
		Bundle bundle = new Bundle();
		bundle.putParcelable(KEY_WEPROOV, weProov);

		SummaryFragment fragment = new SummaryFragment();
		fragment.setArguments(bundle);
		return fragment;
	}

	public SummaryFragment() {
		super();
	}

	@InjectView(R.id.signature_text_view)
	TextView mTextView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getArguments() != null) {
			mWeProov = getArguments().getParcelable(KEY_WEPROOV);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_summary, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		setCommmandListener(this);
	}

	@Override
	public void onPositiveButtonClicked(Button b) {
		new AlertDialog.Builder(getActivity())
				.setTitle("Confirm")
				.setMessage("By pressing OK, you agree that all information are correct.")
				.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						getTunnel().next();
					}
				})
				.setNegativeButton(android.R.string.cancel, null);
	}

	@Override
	public void onNegativeButtonClicked(Button b) {

	}
}
