package com.weproov.app.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.v4.app.NavUtils;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.InjectView;
import butterknife.OnClick;
import com.squareup.otto.Subscribe;
import com.weproov.app.R;
import com.weproov.app.logic.controllers.ProfileLoader;
import com.weproov.app.logic.controllers.UsersTask;
import com.weproov.app.models.User;
import com.weproov.app.models.UserProfile;
import com.weproov.app.models.events.NetworkErrorEvent;
import com.weproov.app.models.events.RegisterSuccessEvent;
import com.weproov.app.ui.ifaces.CommandIface;
import com.weproov.app.utils.Dog;
import com.weproov.app.utils.PicassoUtils;
import com.weproov.app.utils.validators.EmailValidator;
import com.weproov.app.utils.validators.PasswordValidator;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class RegisterActivity extends BaseActivity implements CommandIface.OnClickListener {

	private static final int SELECT_PROFILE_REQUEST_CODE = 1337;
	private static final int LOADER_ID = 1338;

	@InjectView(R.id.profile_picture)
	ImageView mProfilePicture;

	@InjectView(R.id.edit_first_name)
	EditText mFirstName;
	@InjectView(R.id.edit_first_name_error)
	TextView mFirstNameError;

	@InjectView(R.id.edit_last_name)
	EditText mLastName;
	@InjectView(R.id.edit_last_name_error)
	TextView mLastNameError;

	@InjectView(R.id.edit_email)
	EditText mEmail;
	@InjectView(R.id.edit_email_error)
	TextView mEmailError;

	@InjectView(R.id.edit_password)
	EditText mPassword;
	@InjectView(R.id.edit_password_error)
	TextView mPasswordError;

	@InjectView(R.id.action_bar)
	Toolbar mActionBar;

	ProgressDialog mDialog;

	private Uri mProfilePictureUri;
	private Uri mOutputFileUri;

	private ProfileLoader mLoader;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);

		setSupportActionBar(mActionBar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		getPositiveButton().setText(R.string.register);
		getNegativeButton().setText(android.R.string.cancel);

		mPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				boolean handled = false;
				if (actionId == EditorInfo.IME_ACTION_DONE) {
					onPositiveButtonClicked(null);
					handled = true;
				}
				return handled;
			}
		});

		setCommandListener(this);

		mLoader = new ProfileLoader(this) {
			@Override
			protected void onProfileLoaded(UserProfile profile) {
				mEmail.setText(profile.email);
				mFirstName.setText(profile.givenName);
				mLastName.setText(profile.familyName);

				if (mProfilePictureUri == null) {
					mProfilePictureUri = profile.photo;
					if (profile.photo != null) {
						PicassoUtils.PICASSO.load(mProfilePictureUri).centerCrop().fit().into(mProfilePicture);
					}
				}
			}
		};
		getSupportLoaderManager().initLoader(LOADER_ID, null, mLoader);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			// Respond to the action bar's Up/Home button
			case android.R.id.home:
				NavUtils.navigateUpFromSameTask(this);
				return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onNegativeButtonClicked(Button b) {
		NavUtils.navigateUpFromSameTask(this);
	}

	@Override
	public void onPositiveButtonClicked(Button b) {
		String first_name = mFirstName.getEditableText().toString();
		String last_name = mLastName.getEditableText().toString();
		String email = mEmail.getEditableText().toString();
		String password = mPassword.getEditableText().toString();

		boolean isEverythingOk = true;
		if (!EmailValidator.getInstance().validate(email)) {
			mEmailError.setVisibility(View.VISIBLE);
			mEmail.requestFocus();
			isEverythingOk = false;
		} else {
			mEmailError.setVisibility(View.INVISIBLE);
		}

		if (!PasswordValidator.isAcceptablePassword(password)) {
			mPasswordError.setVisibility(View.VISIBLE);
			mPassword.requestFocus();
			isEverythingOk = false;
		} else {
			mPasswordError.setVisibility(View.INVISIBLE);
		}

		if (TextUtils.isEmpty(last_name)) {
			mLastNameError.setVisibility(View.VISIBLE);
			mLastName.requestFocus();
			isEverythingOk = false;
		} else {
			mLastNameError.setVisibility(View.INVISIBLE);
		}

		if (TextUtils.isEmpty(first_name)) {
			mFirstNameError.setVisibility(View.VISIBLE);
			mFirstName.requestFocus();
			isEverythingOk = false;
		} else {
			mFirstNameError.setVisibility(View.INVISIBLE);
		}

		if (isEverythingOk) {
			// Register
			mDialog = ProgressDialog.show(this, "Register", "Please wait...", true);
			mDialog.show();

			UsersTask.register(new User(email, password, first_name, last_name, mProfilePictureUri));
		}
	}

	@OnClick(R.id.profile_picture)
	public void onProfilePictureClicked() {
		startImageIntent(SELECT_PROFILE_REQUEST_CODE);
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (mDialog != null) {
			mDialog.dismiss();
		}
	}

	@Subscribe
	public void onRegisterSuccess(RegisterSuccessEvent event) {
		mDialog.dismiss();
		gotoMain();
	}

	@Subscribe
	public void onRegisterError(NetworkErrorEvent event) {
		mDialog.dismiss();

		Throwable throwable = event.throwable;
		new AlertDialog.Builder(this)
				.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {

					}
				})
				.setMessage(throwable.getMessage())
				.setTitle(android.R.string.untitled)
				.show();
	}


	private void gotoMain() {
		Intent intent = new Intent(this, MainActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
	}

	private void startImageIntent(int requestCode) {
		final File file = new File(getCacheDir(), "renter_" + System.currentTimeMillis() + ".jpg");
		mOutputFileUri = Uri.fromFile(file);

		// Camera.
		final List<Intent> cameraIntents = new ArrayList<>();
		final Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
		final PackageManager packageManager = getPackageManager();
		final List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, PackageManager.MATCH_DEFAULT_ONLY);
		for (ResolveInfo res : listCam) {
			final String packageName = res.activityInfo.packageName;
			final Intent intent = new Intent(captureIntent);
			intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
			intent.setPackage(packageName);
			intent.putExtra(MediaStore.EXTRA_OUTPUT, mOutputFileUri);
			cameraIntents.add(intent);
		}

		// Filesystem.
		final Intent galleryIntent = new Intent();
		galleryIntent.setType("image/*");
		galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

		// Chooser of filesystem options.
		final Intent chooserIntent = Intent.createChooser(galleryIntent, getString(R.string.picker_select_source));

		// Add the camera options.
		chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, cameraIntents.toArray(new Parcelable[cameraIntents.size()]));
		startActivityForResult(chooserIntent, requestCode);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == SELECT_PROFILE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
			if (data == null || (data.getAction() != null && data.getAction().equals(android.provider.MediaStore.ACTION_IMAGE_CAPTURE))) {
				mProfilePictureUri = mOutputFileUri;
			} else {
				mProfilePictureUri = data.getData();
			}

			PicassoUtils.PICASSO.load(mProfilePictureUri).centerCrop().fit().into(mProfilePicture);
			Dog.d("Found picture : %s", mProfilePictureUri);
		}
	}
}
