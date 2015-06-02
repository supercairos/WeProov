package com.weproov.app.ui;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.InjectView;
import com.squareup.picasso.RequestCreator;
import com.weproov.app.R;
import com.weproov.app.utils.AccountUtils;
import com.weproov.app.utils.PicassoUtils;
import com.weproov.app.utils.constants.AccountConstants;

public abstract class DrawerActivity extends BaseActivity {

	@InjectView(R.id.content_root_view)
	DrawerLayout mDrawerLayout;

	@InjectView(R.id.left_frame)
	NavigationView mDrawerNavigation;

	@InjectView(R.id.drawer_title)
	TextView mDrawerTitle;

	@InjectView(R.id.drawer_subtitle)
	TextView mDrawerSubtitle;

	@InjectView(R.id.drawer_image)
	ImageView mDrawerImageView;

	private ActionBarDrawerToggle mDrawerToggle;

	private LayoutInflater mInflater;
	private AccountManager mAccountManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mAccountManager = AccountManager.get(this);
	}

	@Override
	public void setContentView(int layoutResID) {
		setContentView(mInflater.inflate(layoutResID, null));
	}

	@Override
	public void setContentView(View view) {
		setContentView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
	}

	@Override
	public void setContentView(View view, ViewGroup.LayoutParams params) {
		ViewGroup v = (ViewGroup) mInflater.inflate(R.layout.activity_drawer_layout, null);
		ViewGroup content = (ViewGroup) v.findViewById(R.id.root_drawer_layout_content);
		content.addView(view, params);

		super.setContentView(v);

		setupDrawer();
	}

	private void setupDrawer() {
		// set a custom shadow that overlays the main content when the drawer opens
		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

		// ActionBarDrawerToggle ties together the the proper interactions
		// between the sliding drawer and the action bar app icon
		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open, R.string.drawer_close) {

			@Override
			public void onDrawerClosed(View drawerView) {
				super.onDrawerClosed(drawerView);
				invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
			}

			@Override
			public void onDrawerOpened(View drawerView) {
				super.onDrawerOpened(drawerView);
				invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
			}
		};

		mDrawerLayout.setDrawerListener(mDrawerToggle);
		mDrawerNavigation.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
			@Override
			public boolean onNavigationItemSelected(MenuItem menuItem) {
				return onNavItemSelected(menuItem);
			}
		});

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
					requestCreator.placeholder(getResources().getDrawable(R.drawable.progress_large, getTheme()));
				} else {
					requestCreator.placeholder(R.drawable.progress_large);
				}

				requestCreator.into(mDrawerImageView);
			}
		}
	}

	/**
	 * When using the ActionBarDrawerToggle, you must call it during onPostCreate() and onConfigurationChanged()...
	 */
	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		if (mDrawerToggle != null) {
			// Sync the toggle state after onRestoreInstanceState has occurred.
			mDrawerToggle.syncState();
		}
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return (mDrawerToggle != null && mDrawerToggle.onOptionsItemSelected(item)) || super.onOptionsItemSelected(item);
	}


	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		if (mDrawerToggle != null) {
			// Pass any configuration change to the drawer toggls
			mDrawerToggle.onConfigurationChanged(newConfig);
		}
	}

	@Override
	public void onBackPressed() {
		if (mDrawerLayout != null && mDrawerLayout.isDrawerOpen(mDrawerNavigation)) {
			mDrawerLayout.closeDrawer(mDrawerNavigation);
		} else {
			super.onBackPressed();
		}
	}

	public abstract boolean onNavItemSelected(MenuItem item);
}
