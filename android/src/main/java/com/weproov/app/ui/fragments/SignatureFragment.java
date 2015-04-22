package com.weproov.app.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import butterknife.InjectView;
import com.weproov.app.R;
import com.weproov.app.ui.ifaces.CommandIface;

public class SignatureFragment extends TunnelFragment implements CommandIface.OnClickListener {

	private static final String KEY_PERSON_NAME = "key_person_name";
	private String mName;

	public static SignatureFragment newInstance(String name) {
		Bundle bundle = new Bundle();
		bundle.putString(KEY_PERSON_NAME, name);

		SignatureFragment signatureFragment = new SignatureFragment();
		signatureFragment.setArguments(bundle);
		return signatureFragment;
	}

	public SignatureFragment() {
		super();
	}

	@InjectView(R.id.signature_text_view)
	TextView mTextView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getArguments() != null) {
			mName = getArguments().getString(KEY_PERSON_NAME);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_signature, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		setCommmandListener(this);
		mTextView.setText(getString(R.string.signature_of, mName));
	}

	@Override
	public void onPositiveButtonClicked(Button b) {
		getTunnel().next();
	}

	@Override
	public void onNegativeButtonClicked(Button b) {

	}
}
