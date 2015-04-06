package com.weproov.app.ui.fragments;

import android.app.Activity;
import android.os.Bundle;

/**
 * Created by Romain on 06/04/2015.
 */
public class TunnelFragment extends BaseFragment {

	/**
	 * Fragment data out
	 */
	public static final String KEY_COMMENT_PICTURE_PATH = "key_comment_picture_path";
	public static final String KEY_RENTER_INFO = "key_renter_info";
	public static final String KEY_CAR_INFO = "key_car_info";
	public static final String KEY_PICTURE_ITEM = "key_picture_item";

	public interface Tunnel {
		void next();

		void next(Bundle data);
	}

	private Tunnel mListener;

	public Tunnel getTunnel() {
		return mListener;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mListener = (Tunnel) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement Tunnelface");
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mListener = null;
	}
}
