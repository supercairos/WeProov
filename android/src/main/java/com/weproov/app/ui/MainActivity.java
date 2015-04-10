package com.weproov.app.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.weproov.app.R;
import com.weproov.app.logic.services.GcmRegisterService;
import com.weproov.app.models.*;
import com.weproov.app.ui.fragments.*;
import com.weproov.app.ui.fragments.dialogs.AboutDialogFragment;
import com.weproov.app.ui.fragments.dialogs.SignatureDialogFragment;
import com.weproov.app.ui.ifaces.ActionBarIface;
import com.weproov.app.utils.*;
import com.weproov.app.utils.constants.Constants;


public class MainActivity extends BaseActivity implements DrawerFragment.OnNavigationInteractionListener, ActionBarIface, TunnelFragment.Tunnel {

	private static final String KEY_WE_PROOV_OBJECT = "key_we_proov_object";
	@InjectView(R.id.action_bar)
	Toolbar mActionBar;

	@InjectView(R.id.content_root_view)
	DrawerLayout mDrawerLayout;

	@InjectView(R.id.left_frame)
	FrameLayout mDrawerNavigation;

	private ActionBarDrawerToggle mDrawerToggle;

	private Fragment mCurrentFragment;

	private int mWeProovStep;
	private int[] mOverlayDrawableArray;
	private String[] mOverlaySubtitleArray;

	private WeProov mCurrentWeProov;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (!CameraUtils.checkCameraHardware(this)) {
			Toast.makeText(this, R.string.camera_is_required, Toast.LENGTH_SHORT).show();
			finish();
		}

		setContentView(R.layout.activity_main);
		ButterKnife.inject(this);
		setSupportActionBar(mActionBar);

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

		// enable ActionBar app icon to behave as action to toggle nav drawer
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);

		if (savedInstanceState == null) {
			onNavItemSelected(NavItem.getNavItems().get(0));
		} else {
			mCurrentWeProov = savedInstanceState.getParcelable(KEY_WE_PROOV_OBJECT);
			Log.d("Test", "Found weproov object : " + mCurrentWeProov);
		}

		if (PlayServicesUtils.checkPlayServices(this)) {
			if (TextUtils.isEmpty(PlayServicesUtils.getRegistrationId(this))) {
				startService(new Intent(this, GcmRegisterService.class));
			}
		} else {
			Log.i("Test", "No valid Google Play Services APK found.");
		}

		TypedArray ids = getResources().obtainTypedArray(R.array.camera_overlay);
		mOverlayDrawableArray = new int[ids.length()];
		for (int i = 0; i < ids.length(); i++) {
			mOverlayDrawableArray[i] = ids.getResourceId(i, -1);
		}
		ids.recycle();

		mOverlaySubtitleArray = getResources().getStringArray(R.array.camera_overlay_subtitle);
	}

	/**
	 * When using the ActionBarDrawerToggle, you must call it during onPostCreate() and onConfigurationChanged()...
	 */
	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerToggle.syncState();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (mDrawerToggle != null && mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Pass any configuration change to the drawer toggls
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	private static void logout(Activity ctx) {
		Log.d("Test", "Loging out");
		PrefUtils.remove(Constants.KEY_DISPLAY_NAME);
		PrefUtils.remove(Constants.KEY_EMAIL);
		Intent intent = new Intent(ctx, LandingActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		ctx.startActivity(intent);
		ctx.finish();
	}

	@Override
	public void onNavItemSelected(NavItem item) {
		switch (item.id) {
			case NavItem.NAV_LOGOUT:
				// Handle Logout;
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setMessage("Are you sure you want to logout ?")
						.setTitle("Logout")
						.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								logout(MainActivity.this);
							}
						})
						.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								// User cancelled the dialog
							}
						});
				builder.show();
				return; // Keep the return here
			case NavItem.NAV_WEPROOV:
				mCurrentWeProov = new WeProov();
				mCurrentFragment = new RenterFragment();
				break;
			case NavItem.NAV_DASHBOARD:
				mCurrentFragment = new DashboardFragment();
				break;
			case NavItem.NAV_MY_DOCUMENTS:
				mCurrentFragment = new SignatureDialogFragment();
				break;
			case NavItem.NAV_ABOUT:
				mCurrentFragment = new AboutDialogFragment();
				break;
			default:
				throw new IllegalArgumentException();
		}


		if (mCurrentFragment instanceof DialogFragment) {
			FragmentsUtils.showDialog(this, (DialogFragment) mCurrentFragment);
		} else {
			FragmentsUtils.replace(this, mCurrentFragment, R.id.content_fragment);
			setTitle(item.getLabel());
		}
		mDrawerLayout.closeDrawer(mDrawerNavigation);
	}

	@Override
	public void onBackPressed() {
		onNavItemSelected(NavItem.getNavItems().get(0));
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		onSaveInstanceState(outState, null);
	}

	@Override
	public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
		outState.putParcelable(KEY_WE_PROOV_OBJECT, mCurrentWeProov);
		super.onSaveInstanceState(outState, outPersistentState);
	}

	public boolean isActionBarShowing() {
		return mActionBar.getVisibility() == View.VISIBLE;
	}

	public void showActionBar() {
		if (mActionBar != null) {
			mActionBar.setVisibility(View.VISIBLE);
		}
	}

	public void hideActionBar() {
		if (mActionBar != null) {
			mActionBar.setVisibility(View.GONE);
		}
	}

	@Override
	public void next() {
		next(null);
	}

	@Override
	public void next(Bundle data) {
		Log.d("Test", "Next clicked (b = " + data + ")");
		if (mCurrentFragment == null) {
			// Restore
			mCurrentFragment = getSupportFragmentManager().findFragmentByTag("tag");
		}

		if (RenterFragment.class.equals(mCurrentFragment.getClass())) {
			// Need to go to CarInfoFragment;
			RenterInfo info = null;
			if (data != null) {
				info = data.getParcelable(TunnelFragment.KEY_RENTER_INFO);
			}

			mCurrentWeProov.renter = info;
			Log.d("Test", "Got Renter info : " + info);

			mCurrentFragment = new CarInfoFragment();
		} else if (CarInfoFragment.class.equals(mCurrentFragment.getClass())) {
			// Need to go to Camera and reset counter;

			// Need to go to CarInfoFragment;
			CarInfo info = null;
			if (data != null) {
				info = data.getParcelable(TunnelFragment.KEY_CAR_INFO);
			}

			mCurrentWeProov.car = info;
			Log.d("Test", "Got Car info : " + info);
			mWeProovStep = 0;
			mCurrentFragment = CameraFragment.newInstance(mOverlayDrawableArray[mWeProovStep], mOverlaySubtitleArray[mWeProovStep]);
		} else if (CameraFragment.class.equals(mCurrentFragment.getClass())) {
			// Need to go to comment
			String path = null;
			if (data != null) {
				path = data.getString(TunnelFragment.KEY_COMMENT_PICTURE_PATH);
			}

			FragmentsUtils.clearBackStack(getSupportFragmentManager());
			mCurrentFragment = CommentFragment.newInstance(path);
		} else if (CommentFragment.class.equals(mCurrentFragment.getClass())) {
			PictureItem item = null;
			if (data != null) {
				item = data.getParcelable(TunnelFragment.KEY_PICTURE_ITEM);
			}

			Log.d("Test", "Got picture = " + item);

			mCurrentWeProov.addPicture(item);
			// Goto next drawable
			if (++mWeProovStep == mOverlayDrawableArray.length) {
				// We are at the end. Show last fragment;
				mCurrentWeProov.save();
				mCurrentWeProov.savePictures();
				AccountUtils.startSync();

				mCurrentFragment = new DashboardFragment();
			} else {
				// Progress with camera
				mCurrentFragment = CameraFragment.newInstance(mOverlayDrawableArray[mWeProovStep], mWeProovStep < mOverlaySubtitleArray.length ? mOverlaySubtitleArray[mWeProovStep] : "");
			}
		} else {
			throw new IllegalStateException("Called from a non nextable fragment");
		}

		setCommandListener(null);
		FragmentsUtils.replace(this, mCurrentFragment, R.id.content_fragment);
		// Clear state
	}
}
