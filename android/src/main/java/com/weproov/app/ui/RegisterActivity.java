package com.weproov.app.ui;

import android.annotation.SuppressLint;
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
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.NavUtils;
import android.support.v7.widget.Toolbar;
import android.telephony.PhoneNumberUtils;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.*;

import butterknife.InjectView;
import butterknife.OnClick;

import com.squareup.otto.Subscribe;
import com.weproov.app.R;
import com.weproov.app.logic.controllers.ProfileLoader;
import com.weproov.app.logic.controllers.UsersTask;
import com.weproov.app.models.Country;
import com.weproov.app.models.User;
import com.weproov.app.models.UserProfile;
import com.weproov.app.models.events.NetworkErrorEvent;
import com.weproov.app.models.events.RegisterSuccessEvent;
import com.weproov.app.ui.adapter.CountryAutocompleteAdapter;
import com.weproov.app.ui.adapter.CountrySpinnerAdapter;
import com.weproov.app.ui.adapter.CustomPhoneNumberFormattingTextWatcher;
import com.weproov.app.ui.ifaces.CommandIface;
import com.weproov.app.utils.Dog;
import com.weproov.app.utils.PicassoUtils;
import com.weproov.app.utils.validators.EmailValidator;
import com.weproov.app.utils.validators.PasswordValidator;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RegisterActivity extends BaseActivity implements CommandIface.OnClickListener {

    private static final int SELECT_PROFILE_REQUEST_CODE = 1337;
    private static final int LOADER_ID = 1338;

    @InjectView(R.id.profile_picture)
    ImageView mProfilePicture;

    @InjectView(R.id.edit_first_name)
    EditText mFirstName;
    @InjectView(R.id.edit_first_name_layout)
    TextInputLayout mFirstNameLayout;

    @InjectView(R.id.edit_last_name)
    EditText mLastName;
    @InjectView(R.id.edit_last_name_layout)
    TextInputLayout mLastNameLayout;

    @InjectView(R.id.edit_email)
    EditText mEmail;
    @InjectView(R.id.edit_email_layout)
    TextInputLayout mEmailLayout;

    @InjectView(R.id.edit_password)
    EditText mPassword;
    @InjectView(R.id.edit_password_layout)
    TextInputLayout mPasswordLayout;

    @InjectView(R.id.licence_checkbox)
    CheckBox mCheckBox;

    @InjectView(R.id.action_bar)
    Toolbar mActionBar;

    @InjectView(R.id.spinner_country)
    Spinner mCountrySpinner;
    @InjectView(R.id.edit_phone)
    AutoCompleteTextView mPhone;
    @InjectView(R.id.edit_phone_layout)
    TextInputLayout mPhoneLayout;

    ProgressDialog mDialog;

    private Uri mProfilePictureUri;
    private Uri mOutputFileUri;

    private ProfileLoader mLoader;

    private CountryAutocompleteAdapter mAutocompleteAdapter;
    private CountrySpinnerAdapter mSpinnerAdapter;

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

        mSpinnerAdapter = new CountrySpinnerAdapter(this);
        mCountrySpinner.setAdapter(mSpinnerAdapter);

        mCountrySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Country country = (Country) parent.getItemAtPosition(position);
                String ind = "+" + String.valueOf(country.phone);
                mPhone.setText(ind);
                mPhone.setSelection(ind.length());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        mCountrySpinner.setSelection(1);

        mAutocompleteAdapter = new CountryAutocompleteAdapter(this);
        mPhone.setAdapter(mAutocompleteAdapter);
        mPhone.setThreshold(1);
        mPhone.setText("+33");
        mPhone.addTextChangedListener(new CustomPhoneNumberFormattingTextWatcher());
        mPhone.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Country country = (Country) parent.getItemAtPosition(position);
                int positionSpinner = mSpinnerAdapter.getPosition(country);
                if (positionSpinner >= 0) {
                    mCountrySpinner.setSelection(positionSpinner);
                }
                String ind = "+" + String.valueOf(country.phone);
                mPhone.setText(ind);
                mPhone.setSelection(ind.length());
            }
        });

        setCommandListener(this);

        mLoader = new ProfileLoader(this) {
            @Override
            protected void onProfileLoaded(UserProfile profile) {
                mEmail.setText(profile.email);
                mFirstName.setText(profile.givenName);
                mLastName.setText(profile.familyName);
                mPhone.setText(profile.phoneNumber);

                if (mProfilePictureUri == null) {
                    mProfilePictureUri = profile.photo;
                    if (profile.photo != null) {
                        PicassoUtils.PICASSO.load(mProfilePictureUri).centerCrop().fit().into(mProfilePicture);
                    }
                }
            }
        };
        getSupportLoaderManager().initLoader(LOADER_ID, null, mLoader);
        findViewById(R.id.edit_email).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, LandingActivity.class));
            }
        });
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
        String phone = mPhone.getEditableText().toString().replaceAll("[^0-9+]*", "");
        String password = mPassword.getEditableText().toString();

        boolean isEverythingOk = true;
        if (!EmailValidator.getInstance().validate(email)) {
            mEmailLayout.setError(getString(R.string.error_email));
            mEmail.requestFocus();
            isEverythingOk = false;
        } else {
            mEmailLayout.setError(null);
        }

        if (!PasswordValidator.isAcceptablePassword(password)) {
            mPasswordLayout.setError(getString(R.string.error_password));
            mPassword.requestFocus();
            isEverythingOk = false;
        } else {
            mPasswordLayout.setError(null);
        }

        if (TextUtils.isEmpty(last_name)) {
            mLastNameLayout.setError(getString(R.string.error_last_name));
            mLastName.requestFocus();
            isEverythingOk = false;
        } else {
            mLastNameLayout.setError(null);
        }

        if (TextUtils.isEmpty(first_name)) {
            mFirstNameLayout.setError(getString(R.string.error_first_name));
            mFirstName.requestFocus();
            isEverythingOk = false;
        } else {
            mFirstNameLayout.setError(null);
        }

        if (!PhoneNumberUtils.isGlobalPhoneNumber(phone)) {
            mPhoneLayout.setError(getString(R.string.error_phone));
            mPhone.requestFocus();
            isEverythingOk = false;
        } else {
            mPhoneLayout.setError(null);
        }

        if (!mCheckBox.isChecked()) {
            mCheckBox.setTextColor(getResources().getColor(R.color.error_color));
            isEverythingOk = false;
        } else {
            mCheckBox.setTextColor(getResources().getColor(android.R.color.white));
        }

        if (isEverythingOk) {
            // Register
            mDialog = ProgressDialog.show(this, "Register", "Please wait...", true);
            mDialog.show();

            UsersTask.register(new User(email, phone, password, first_name, last_name, mProfilePictureUri));
        }
    }

    @OnClick(R.id.profile_picture)
    public void onProfilePictureClicked() {
        startImageIntent(SELECT_PROFILE_REQUEST_CODE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mDialog != null) {
            mDialog.dismiss();
        }
    }

    @Subscribe
    public void onRegisterSuccess(RegisterSuccessEvent event) {
        if (mDialog != null) {
            mDialog.dismiss();
        }
        gotoMain();
    }

    @Subscribe
    public void onRegisterError(NetworkErrorEvent event) {
        if (mDialog != null) {
            mDialog.dismiss();
        }

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
        try {
            mOutputFileUri = Uri.fromFile(createImageFile());

            // Camera.
            final List<Intent> cameraIntents = new ArrayList<>();
            final Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
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
        } catch (IOException e) {
            Toast.makeText(this, "Error creating picture", Toast.LENGTH_LONG).show();
            Dog.e(e, "IOException");
        }
    }

    @SuppressLint("SimpleDateFormat")
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "renter_" + timeStamp;
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

        return File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir     /* directory */
        );
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SELECT_PROFILE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            if (data == null || (data.getAction() != null && data.getAction().equals(android.provider.MediaStore.ACTION_IMAGE_CAPTURE))) {
                mProfilePictureUri = mOutputFileUri;
            } else {
                Uri uri = data.getData();
                if (!mOutputFileUri.equals(uri)) {
                    new File(mOutputFileUri.getPath()).delete();
                }

                mProfilePictureUri = data.getData();
            }

            PicassoUtils.PICASSO.load(mProfilePictureUri).centerCrop().fit().into(mProfilePicture);
            Dog.d("Found picture : %s", mProfilePictureUri);
        }
    }
}
