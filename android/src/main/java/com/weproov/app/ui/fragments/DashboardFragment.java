package com.weproov.app.ui.fragments;


import android.accounts.Account;
import android.accounts.AccountManager;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.InjectView;
import butterknife.OnLongClick;
import com.squareup.picasso.RequestCreator;
import com.weproov.app.R;
import com.weproov.app.utils.AccountUtils;
import com.weproov.app.utils.OrientationUtils;
import com.weproov.app.utils.PicassoUtils;
import com.weproov.app.utils.constants.AccountConstants;

public class DashboardFragment extends BaseFragment {

	@InjectView(R.id.text_welcome)
	TextView mWelcomeText;

	@InjectView(R.id.profile_picture)
	ImageView mImageView;

	private AccountManager mAccountManager;

	public DashboardFragment() {
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setCommmandListener(null);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mAccountManager = (AccountManager) getActivity().getSystemService(Context.ACCOUNT_SERVICE);
	}

	@Override
	public void onStart() {
		super.onStart();
		getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_dashboard, container, false);
	}

	@SuppressLint("NewApi")
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		Account account = AccountUtils.getAccount();
		if (account != null) {
			String firstName = mAccountManager.getUserData(account, AccountConstants.KEY_FIRST_NAME);
			String lastName = mAccountManager.getUserData(account, AccountConstants.KEY_LAST_NAME);
			String url = mAccountManager.getUserData(account, AccountConstants.KEY_PROFILE_PICTURE);
			if (!TextUtils.isEmpty(url)) {
				RequestCreator requestCreator = PicassoUtils.PICASSO.load(url).fit().centerCrop().error(R.drawable.no_icon_profile);
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
					requestCreator.placeholder(getResources().getDrawable(R.drawable.progress_large, getActivity().getTheme()));
				} else {
					requestCreator.placeholder(R.drawable.progress_large);
				}

				requestCreator.into(mImageView);
			}

			mWelcomeText.setText(getString(R.string.welcome, firstName + " " + lastName));
		}
	}

	@Override
	public void onStop() {
		super.onStop();
		OrientationUtils.unlockOrientation(getActivity());
	}

	@OnLongClick(R.id.profile_picture)
	boolean onDebugSyncClicked() {
		AccountUtils.startSync();
		return true;
	}
}
