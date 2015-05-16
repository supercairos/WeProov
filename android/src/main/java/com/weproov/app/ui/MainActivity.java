package com.weproov.app.ui;

import android.accounts.Account;
import android.animation.Animator;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SyncStatusObserver;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.FrameLayout;
import android.widget.Toast;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.weproov.app.R;
import com.weproov.app.logic.services.GcmRegisterService;
import com.weproov.app.models.NavItem;
import com.weproov.app.ui.fragments.DashboardFragment;
import com.weproov.app.ui.fragments.DrawerFragment;
import com.weproov.app.ui.fragments.dialogs.AboutDialogFragment;
import com.weproov.app.ui.fragments.dialogs.SignatureDialogFragment;
import com.weproov.app.utils.*;
import com.weproov.app.utils.constants.AuthenticatorConstants;
import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;


public class MainActivity extends BaseActivity implements DrawerFragment.OnNavigationInteractionListener {

	@InjectView(R.id.action_bar)
	Toolbar mActionBar;

	@InjectView(R.id.content_root_view)
	DrawerLayout mDrawerLayout;

	@InjectView(R.id.left_frame)
	FrameLayout mDrawerNavigation;

	@InjectView(R.id.floating_action_button_container)
	FrameLayout mFloatingActionButton;

	@InjectView(R.id.sync_progress)
	SmoothProgressBar mSyncProgress;

	private ActionBarDrawerToggle mDrawerToggle;

	private Object mSyncObserverHandle;

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
			FragmentsUtils.replace(this, new DashboardFragment(), R.id.content_fragment, "tag", false, 0, 0);
		}

		if (PlayServicesUtils.checkPlayServices(this)) {
			if (TextUtils.isEmpty(PlayServicesUtils.getRegistrationId(this))) {
				startService(new Intent(this, GcmRegisterService.class));
			}
		} else {
			Dog.i("No valid Google Play Services APK found.");
		}


		mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// get the center for the clipping circle
				int cx = (mFloatingActionButton.getLeft() + mFloatingActionButton.getRight()) / 2;
				int cy = (mFloatingActionButton.getTop() + mFloatingActionButton.getBottom()) / 2;

				// get the final radius for the clipping circle
				int finalRadius = Math.max(mFloatingActionButton.getWidth(), mFloatingActionButton.getHeight());

				// create the animator for this view (the start radius is zero)
				final Animator anim = null;
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
					ViewAnimationUtils.createCircularReveal(mDrawerLayout, cx, cy, 0, finalRadius);
				}

				anim.start();
				startActivity(new Intent(MainActivity.this, WeproovActivity.class));
			}
		});
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
	protected void onStart() {
		super.onStart();
		final Account account = AccountUtils.getAccount();
		if (account != null) {
			mSyncObserverHandle = ContentResolver.addStatusChangeListener(ContentResolver.SYNC_OBSERVER_TYPE_ACTIVE | ContentResolver.SYNC_OBSERVER_TYPE_PENDING, new SyncStatusObserver() {
						@Override
						public void onStatusChanged(final int which) {
							if (ContentResolver.isSyncActive(account, AuthenticatorConstants.ACCOUNT_PROVIDER)) {
								mSyncProgress.setVisibility(View.VISIBLE);
							} else if (ContentResolver.isSyncPending(account, AuthenticatorConstants.ACCOUNT_PROVIDER)) {
								mSyncProgress.setVisibility(View.VISIBLE);
							} else {
								mSyncProgress.setVisibility(View.GONE);
							}
						}
					}
			);
		}
	}

	@Override
	protected void onStop() {
		ContentResolver.removeStatusChangeListener(mSyncObserverHandle);
		super.onStop();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return mDrawerToggle != null && mDrawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Pass any configuration change to the drawer toggls
		mDrawerToggle.onConfigurationChanged(newConfig);
	}


	@Override
	public void onNavItemSelected(NavItem item) {
		Fragment fragment = null;
		switch (item.id) {
			case NavItem.NAV_LOGOUT:
				// Handle Logout;
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setMessage("Are you sure you want to logout ?")
						.setTitle("Logout")
						.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								AccountUtils.removeAccount(MainActivity.this, new AccountUtils.AccountRemovedCallback() {
									@Override
									public void onSuccess() {
										Intent intent = new Intent(MainActivity.this, LandingActivity.class);
										intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
										MainActivity.this.startActivity(intent);
										MainActivity.this.finish();
									}

									@Override
									public void onFailure() {
										Toast.makeText(MainActivity.this, "Could not remove this account", Toast.LENGTH_LONG).show();
									}
								});
							}
						})
						.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								// User cancelled the dialog
							}
						})
						.show();

				return; // Keep the return here
			case NavItem.NAV_WEPROOV:
				startActivity(new Intent(this, WeproovActivity.class));
				return; // Keep the return here
			case NavItem.NAV_DASHBOARD:
				fragment = new DashboardFragment();
				break;
			case NavItem.NAV_MY_DOCUMENTS:
				fragment = new SignatureDialogFragment();
				break;
			case NavItem.NAV_ABOUT:
				fragment = new AboutDialogFragment();
				break;
			default:
				throw new IllegalArgumentException();
		}


		if (fragment instanceof DialogFragment) {
			FragmentsUtils.showDialog(this, (DialogFragment) fragment);
		} else {
			FragmentsUtils.replace(this, fragment, R.id.content_fragment);
			setTitle(item.getLabel());
		}
		mDrawerLayout.closeDrawer(mDrawerNavigation);
	}

	@Override
	public void onBackPressed() {
		if (mDrawerLayout.isDrawerOpen(mDrawerNavigation)) {
			mDrawerLayout.closeDrawer(mDrawerNavigation);
		} else {
			super.onBackPressed();
		}
	}
}
