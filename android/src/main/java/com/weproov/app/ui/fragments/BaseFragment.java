package com.weproov.app.ui.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.Button;
import butterknife.ButterKnife;
import com.weproov.app.ui.ifaces.CommandIface;


abstract class BaseFragment extends Fragment {

	CommandIface mListener;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mListener = (CommandIface) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement CommandIface");
		}
	}

	@Override
	public void onViewCreated(final View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		ButterKnife.inject(this, view);
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mListener = null;
	}

	protected final Button getPositiveButton() {
		return mListener.getPositiveButton();
	}

	protected final Button getNegativeButton() {
		return mListener.getNegativeButton();
	}

	protected final void setCommmandListener(CommandIface.OnClickListener listener) {
		mListener.setCommandListener(listener);
	}
}
