package com.weproov.app.ui;

import android.accounts.Account;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.SyncStatusObserver;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import butterknife.InjectView;
import com.weproov.app.R;
import com.weproov.app.models.PictureItem;
import com.weproov.app.models.WeProov;
import com.weproov.app.ui.fragments.*;
import com.weproov.app.ui.fragments.dialogs.BugReportDialogFragment;
import com.weproov.app.ui.ifaces.ActionBarIface;
import com.weproov.app.utils.AccountUtils;
import com.weproov.app.utils.Dog;
import com.weproov.app.utils.FragmentsUtils;
import com.weproov.app.utils.PrefUtils;
import com.weproov.app.utils.constants.AuthenticatorConstants;
import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;

import java.lang.ref.WeakReference;

public class WeproovActivity extends BaseActivity implements ActionBarIface, TunnelFragment.Tunnel {

	private static final String KEY_WE_PROOV_OBJECT = "key_we_proov_object";

	@InjectView(R.id.action_bar)
	Toolbar mActionBar;

	@InjectView(R.id.sync_progress)
	SmoothProgressBar mSyncProgress;

	private Object mSyncObserverHandle;

	private int mWeProovStep;
	private Fragment mCurrentFragment;

	private int[] mOverlayDrawableArray;
	private String[] mOverlaySubtitleArray;

	private WeProov mCurrentWeProov = new WeProov();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_weproov);
		setSupportActionBar(mActionBar);

		// enable ActionBar app icon to behave as action to toggle nav drawer
		//noinspection ConstantConditions
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);

		if (savedInstanceState != null) {
			mCurrentWeProov = savedInstanceState.getParcelable(KEY_WE_PROOV_OBJECT);
			Dog.d("Found weproov object : %s", mCurrentWeProov);
		} else {
			if (PrefUtils.getBoolean(WeproovWelcomeFragment.PREF_PASS_HELP, false)) {
				FragmentsUtils.replace(this, new ClientFragment(), R.id.content_fragment, "tag", false, 0, 0);
			} else {
				FragmentsUtils.replace(this, new WeproovWelcomeFragment(), R.id.content_fragment, "tag", false, 0, 0);
			}
		}


		TypedArray ids = getResources().obtainTypedArray(R.array.camera_overlay);
		mOverlayDrawableArray = new int[ids.length()];
		for (int i = 0; i < ids.length(); i++) {
			mOverlayDrawableArray[i] = ids.getResourceId(i, -1);
		}
		ids.recycle();

		mOverlaySubtitleArray = getResources().getStringArray(R.array.camera_overlay_subtitle);
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
	protected void onStop() {
		ContentResolver.removeStatusChangeListener(mSyncObserverHandle);
		super.onStop();
	}


	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putParcelable(KEY_WE_PROOV_OBJECT, mCurrentWeProov);
		super.onSaveInstanceState(outState);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_weproov, menu);
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
		} else if (id == android.R.id.home) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(R.string.exit_weproov_message)
					.setTitle(R.string.exit_weproov_title)
					.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							finish();
						}
					})
					.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							// User cancelled the dialog
						}
					}).show();

			return true;
		}

		return super.onOptionsItemSelected(item);
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
		// Restore
		mCurrentFragment = getSupportFragmentManager().findFragmentByTag("tag");
		if (WeproovWelcomeFragment.class.equals(mCurrentFragment.getClass())) {
			mCurrentFragment = new ClientFragment();
		} else if (ClientFragment.class.equals(mCurrentFragment.getClass())) {
			// Need to go to CarInfoFragment;
			if (data != null) {
				mCurrentWeProov.client = data.getParcelable(TunnelFragment.KEY_RENTER_INFO);
			}
			Dog.d("Got Renter info : %s", mCurrentWeProov.client);

			mCurrentFragment = new CarInfoFragment();
		} else if (CarInfoFragment.class.equals(mCurrentFragment.getClass())) {
			// Need to go to CarInfoFragment;
			if (data != null) {
				mCurrentWeProov.car = data.getParcelable(TunnelFragment.KEY_CAR_INFO);
			}


			Dog.d("Got Car info : %s", mCurrentWeProov.car);
			mWeProovStep = 0;
			mCurrentFragment = CameraFragment.newInstance(mOverlayDrawableArray[mWeProovStep], mOverlaySubtitleArray[mWeProovStep]);
		} else if (CameraFragment.class.equals(mCurrentFragment.getClass())) {
			// Need to go to comment
			String path = null;
			if (data != null) {
				path = data.getString(TunnelFragment.KEY_COMMENT_PICTURE_PATH);
			}

			// FragmentsUtils.clearBackStack(getSupportFragmentManager());
			mCurrentFragment = CommentFragment.newInstance(path);
		} else if (CommentFragment.class.equals(mCurrentFragment.getClass())) {
			PictureItem item = null;
			if (data != null) {
				item = data.getParcelable(TunnelFragment.KEY_PICTURE_ITEM);
			}

			Dog.d("Got picture = %s", item);

			mCurrentWeProov.addPicture(item);
			// Goto next drawable
			if (++mWeProovStep == mOverlayDrawableArray.length) {
				String name = getString(R.string.signature_renter);
				if (mCurrentWeProov.client != null) {
					name = mCurrentWeProov.client.firstname + " " + mCurrentWeProov.client.lastname;
				}

				mWeProovStep = 0;
				mCurrentFragment = SignatureFragment.newInstance(name);
			} else {
				// Progress with camera
				mCurrentFragment = CameraFragment.newInstance(mOverlayDrawableArray[mWeProovStep], mWeProovStep < mOverlaySubtitleArray.length ? mOverlaySubtitleArray[mWeProovStep] : "");
			}
		} else if (SignatureFragment.class.equals(mCurrentFragment.getClass()) && mWeProovStep == 0) {
			mCurrentWeProov.clientSignature = data.getParcelable(TunnelFragment.KEY_SIGNATURE_ITEM);
			mCurrentFragment = SignatureFragment.newInstance(AccountUtils.getDisplayName());
			mWeProovStep++;
		} else if (SignatureFragment.class.equals(mCurrentFragment.getClass()) && mWeProovStep == 1) {
			mCurrentWeProov.renterSignature = data.getParcelable(TunnelFragment.KEY_SIGNATURE_ITEM);
			mCurrentFragment = SummaryFragment.newInstance(mCurrentWeProov);
		} else {
			mCurrentWeProov.doSave();
			finish();
			return;
			// throw new IllegalStateException("Called from a non nextable fragment");
		}

		setCommandListener(null);
		FragmentsUtils.replace(this, mCurrentFragment, R.id.content_fragment);
		// Clear state
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
