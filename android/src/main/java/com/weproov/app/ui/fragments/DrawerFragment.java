package com.weproov.app.ui.fragments;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import butterknife.InjectView;
import com.squareup.picasso.RequestCreator;
import com.weproov.app.R;
import com.weproov.app.models.NavItem;
import com.weproov.app.ui.adapter.NavigationAdapter;
import com.weproov.app.utils.AccountUtils;
import com.weproov.app.utils.PicassoUtils;
import com.weproov.app.utils.constants.AccountConstants;

public class DrawerFragment extends BaseFragment {

	private OnNavigationInteractionListener mListener;
	private NavigationAdapter mAdapter;

	@InjectView(R.id.drawer_list)
	ListView mDrawerList;

	@InjectView(R.id.drawer_title)
	TextView mDrawerTitle;

	@InjectView(R.id.drawer_subtitle)
	TextView mDrawerSubtitle;

	@InjectView(R.id.drawer_image)
	ImageView mDrawerImageView;

	private AccountManager mAccountManager;

	public DrawerFragment() {
		// Required empty public constructor
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mAccountManager = (AccountManager) getActivity().getSystemService(Context.ACCOUNT_SERVICE);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_navigation, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		// set up the drawer's list view with items and click listener
		mAdapter = new NavigationAdapter(getActivity(), NavItem.getNavItems());
		mDrawerList.setAdapter(mAdapter);
		mDrawerList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

		// Check first item;
		mDrawerList.setItemChecked(0, true);

		Account account = AccountUtils.getAccount();
		if (account != null) {
			String firstName = mAccountManager.getUserData(account, AccountConstants.KEY_FIRST_NAME);
			String lastName = mAccountManager.getUserData(account, AccountConstants.KEY_LAST_NAME);
			String url = mAccountManager.getUserData(account, AccountConstants.KEY_PROFILE_PICTURE);
			String email = account.name;

			mDrawerTitle.setText(firstName + " " + lastName);
			mDrawerSubtitle.setText(email);
			if (!TextUtils.isEmpty(url)) {
				RequestCreator requestCreator = PicassoUtils.PICASSO.load(url).fit().centerCrop().error(R.drawable.no_icon_profile);
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
					requestCreator.placeholder(getResources().getDrawable(R.drawable.progress_large, getActivity().getTheme()));
				} else {
					requestCreator.placeholder(R.drawable.progress_large);
				}

				requestCreator.into(mDrawerImageView);
			}
		}


	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mListener = (OnNavigationInteractionListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement OnNavigationInteractionListener");
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mListener = null;
	}

	public interface OnNavigationInteractionListener {
		void onNavItemSelected(NavItem item);
	}

	/* The click listner for ListView in the navigation drawer */
	private class DrawerItemClickListener implements ListView.OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			mListener.onNavItemSelected(mAdapter.getItem(position));
			mDrawerList.setItemChecked(position, true);
		}
	}

}
