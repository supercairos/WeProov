package com.weproov.app.ui;

import android.accounts.Account;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SyncStatusObserver;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import butterknife.InjectView;
import com.weproov.app.R;
import com.weproov.app.logic.services.GcmRegisterService;
import com.weproov.app.ui.fragments.dialogs.BugReportDialogFragment;
import com.weproov.app.ui.fragments.dialogs.LogoutDialogFragment;
import com.weproov.app.utils.*;
import com.weproov.app.utils.constants.AuthenticatorConstants;
import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;

import java.lang.ref.WeakReference;


public class MainActivity extends DrawerActivity {

	@InjectView(R.id.action_bar)
	Toolbar mActionBar;

	@InjectView(R.id.floating_action_button)
	FloatingActionButton mFloatingActionButton;

	@InjectView(R.id.sync_progress)
	SmoothProgressBar mSyncProgress;

	private Object mSyncObserverHandle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		setSupportActionBar(mActionBar);
		checkHardware();

		// enable ActionBar app icon to behave as action to toggle nav drawer
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);

		mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(MainActivity.this, WeproovActivity.class));
			}
		});
	}

	@Override
	public boolean onNavItemSelected(MenuItem item) {
		DialogFragment fragment;
		switch (item.getItemId()) {
			case R.id.navigation_account_logout:
				fragment = new LogoutDialogFragment();
				break; // Keep the return here
			case R.id.navigation_weproov_new:
				mDrawerLayout.closeDrawer(mDrawerNavigation);
				startActivity(new Intent(this, WeproovActivity.class));
				return true; // Keep the return here
			case R.id.navigation_dashboard:
				mDrawerLayout.closeDrawer(mDrawerNavigation);
				return true; // Keep the return here
			case R.id.navigation_weproov_list:
				mDrawerLayout.closeDrawer(mDrawerNavigation);
				startActivity(new Intent(this, ReportListActivity.class));
				return true; // Keep the return here
			case R.id.navigation_account_about:
				mDrawerLayout.closeDrawer(mDrawerNavigation);
				startActivity(new Intent(this, AboutActivity.class));
				return true; // Keep the return here
			default:
				return false;
		}

		FragmentsUtils.showDialog(this, fragment);
		mDrawerLayout.closeDrawer(mDrawerNavigation);

		return true;
	}

	private void checkHardware() {
		if (!CameraUtils.checkCameraHardware(this)) {
			Toast.makeText(this, R.string.camera_is_required, Toast.LENGTH_SHORT).show();
			finish();
		}

		if (PlayServicesUtils.checkPlayServices(this)) {
			if (TextUtils.isEmpty(PlayServicesUtils.getRegistrationId(this))) {
				startService(new Intent(this, GcmRegisterService.class));
			}
		} else {
			Dog.i("No valid Google Play Services APK found.");
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
		final Account account = AccountUtils.getAccount();
		boolean isRunning = false;
		if (account != null) {
			// https://github.com/square/leakcanary/issues/86
			mSyncObserverHandle = ContentResolver.addStatusChangeListener(ContentResolver.SYNC_OBSERVER_TYPE_ACTIVE | ContentResolver.SYNC_OBSERVER_TYPE_PENDING, new MySyncObserver(mSyncProgress));
			isRunning = ContentResolver.isSyncActive(account, AuthenticatorConstants.ACCOUNT_PROVIDER) || ContentResolver.isSyncPending(account, AuthenticatorConstants.ACCOUNT_PROVIDER);
		}
		mSyncProgress.setVisibility(isRunning ? View.VISIBLE : View.GONE);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_main_activity, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		//noinspection SimplifiableIfStatement
		if (id == R.id.action_report_bug) {
			FragmentsUtils.showDialog(this, new BugReportDialogFragment());
			return true;
		} else if (id == R.id.action_settings) {
			startActivity(new Intent(this, SettingsActivity.class));
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onStop() {
		ContentResolver.removeStatusChangeListener(mSyncObserverHandle);
		super.onStop();
	}


	private static class MySyncObserver implements SyncStatusObserver {

		private final Handler mMainThreadHandler;
		private final WeakReference<View> mView;

		public MySyncObserver(View view) {
			this.mView = new WeakReference<>(view);
			this.mMainThreadHandler = new Handler(Looper.getMainLooper());
		}

		@Override
		public void onStatusChanged(final int which) {
			final Account account = AccountUtils.getAccount();
			final View view = mView.get();
			if (account != null && view != null) {
				if (ContentResolver.isSyncActive(account, AuthenticatorConstants.ACCOUNT_PROVIDER)) {
					mMainThreadHandler.post(new Runnable() {
						@Override
						public void run() {
							view.setVisibility(View.VISIBLE);
						}
					});
				} else if (ContentResolver.isSyncPending(account, AuthenticatorConstants.ACCOUNT_PROVIDER)) {
					mMainThreadHandler.post(new Runnable() {
						@Override
						public void run() {
							view.setVisibility(View.VISIBLE);
						}
					});
				} else {
					mMainThreadHandler.post(new Runnable() {
						@Override
						public void run() {
							view.setVisibility(View.VISIBLE);
						}
					});
				}
			}
		}
	}
}
