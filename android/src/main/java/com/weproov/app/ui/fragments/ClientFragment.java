package com.weproov.app.ui.fragments;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.design.widget.TextInputLayout;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import butterknife.InjectView;
import butterknife.OnClick;
import com.squareup.picasso.RequestCreator;
import com.weproov.app.BuildConfig;
import com.weproov.app.R;
import com.weproov.app.models.ClientInfo;
import com.weproov.app.ui.adapter.RenterAutocompleteAdapter;
import com.weproov.app.ui.ifaces.CommandIface;
import com.weproov.app.utils.Dog;
import com.weproov.app.utils.PathUtils;
import com.weproov.app.utils.PicassoUtils;
import com.weproov.app.utils.validators.EmailValidator;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ClientFragment extends TunnelFragment implements CommandIface.OnClickListener {

	private static final int SELECT_ID_CARD_REQUEST_CODE = 999;
	private static final int SELECT_DRIVING_LICENCE_REQUEST_CODE = 666;

	/**
	 * For Camera URI return intent *
	 */
	private Uri mOutputFileUri;

	private Uri mDrivingLicenceUri;
	private Uri mIdCardUri;

	@InjectView(R.id.edit_first_name)
	AutoCompleteTextView mFirstName;
	@InjectView(R.id.edit_first_name_layout)
	TextInputLayout mFirstNameLayout;
	@SuppressWarnings("FieldCanBeLocal")
	private RenterAutocompleteAdapter mFirstnameAutocompleteAdapter;

	@InjectView(R.id.edit_last_name)
	AutoCompleteTextView mLastName;
	@InjectView(R.id.edit_last_name_layout)
	TextInputLayout mLastNameLayout;
	@SuppressWarnings("FieldCanBeLocal")
	private RenterAutocompleteAdapter mLastnameAutocompleteAdapter;

	@InjectView(R.id.edit_email)
	EditText mEmail;
	@InjectView(R.id.edit_email_layout)
	TextInputLayout mEmailLayout;

	@InjectView(R.id.edit_company)
	EditText mCompany;
	@InjectView(R.id.edit_company_layout)
	TextInputLayout mCompanyLayout;

	@InjectView(R.id.edit_phone)
	EditText mPhone;
	@InjectView(R.id.edit_phone_layout)
	TextInputLayout mPhoneLayout;

	@InjectView(R.id.identity_card_picture)
	ImageView mIdCardPicture;
	@InjectView(R.id.identity_card_text)
	TextView mIdCardText;

	@InjectView(R.id.driving_licence_picture)
	ImageView mDrivingLicencePicture;
	@InjectView(R.id.driving_licence_text)
	TextView mDrivingLicenceText;

	private EmailValidator mEmailValidator = EmailValidator.getInstance();


	@SuppressWarnings("unused")
	public static ClientFragment newInstance(ClientInfo info) {
		ClientFragment fragment = new ClientFragment();
		Bundle bundle = new Bundle();
		bundle.putParcelable(TunnelFragment.KEY_RENTER_INFO, info);
		fragment.setArguments(bundle);

		return fragment;
	}

	public ClientFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_client_info, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setCommmandListener(this);
		ClientInfo info = null;
		if (savedInstanceState != null) {
			info = savedInstanceState.getParcelable(TunnelFragment.KEY_RENTER_INFO);
		} else if (getArguments() != null) {
			info = getArguments().getParcelable(TunnelFragment.KEY_RENTER_INFO);
		}

		if (info != null) {
			setRenterInfo(info);
		}

		mFirstnameAutocompleteAdapter = new RenterAutocompleteAdapter(getActivity(), RenterAutocompleteAdapter.TYPE_FIRST_NAME);
		mFirstName.setAdapter(mFirstnameAutocompleteAdapter);
		mFirstName.setThreshold(1);
		mFirstName.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				setRenterInfo(((RenterAutocompleteAdapter) parent.getAdapter()).getItem(position));
			}
		});

		mLastnameAutocompleteAdapter = new RenterAutocompleteAdapter(getActivity(), RenterAutocompleteAdapter.TYPE_LAST_NAME);
		mLastName.setAdapter(mLastnameAutocompleteAdapter);
		mLastName.setThreshold(1);
		mLastName.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				setRenterInfo(((RenterAutocompleteAdapter) parent.getAdapter()).getItem(position));
			}
		});

		mPhone.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
	}

	private void setRenterInfo(ClientInfo info) {
		mFirstName.setText(info.firstname);
		mLastName.setText(info.lastname);
		mEmail.setText(info.email);
		mCompany.setText(info.company);
		mPhone.setText(info.phone);

		mIdCardUri = info.id_card;
		mDrivingLicenceUri = info.driving_licence;
		PicassoUtils.PICASSO.load(mIdCardUri).centerCrop().fit().placeholder(R.drawable.card1).into(mIdCardPicture);
		PicassoUtils.PICASSO.load(mDrivingLicenceUri).centerCrop().fit().placeholder(R.drawable.card1).into(mDrivingLicencePicture);
	}

	private ClientInfo getRenterInfo() {
		ClientInfo info = new ClientInfo();
		if (mFirstName != null) {
			info.firstname = mFirstName.getEditableText().toString();
		}

		if (mLastName != null) {
			info.lastname = mLastName.getEditableText().toString();
		}

		if (mEmail != null) {
			info.email = mEmail.getEditableText().toString();
		}

		if (mCompany != null) {
			info.company = mCompany.getEditableText().toString();
		}

		if (mPhone != null) {
			info.phone = mPhone.getEditableText().toString();
		}

		info.id_card = mIdCardUri;
		info.driving_licence = mDrivingLicenceUri;

		return info;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putParcelable(TunnelFragment.KEY_RENTER_INFO, getRenterInfo());
		super.onSaveInstanceState(outState);
	}

	@OnClick(R.id.driving_licence)
	public void onDrivingLicenceClicked() {
		startImageIntent(SELECT_DRIVING_LICENCE_REQUEST_CODE);
	}

	@OnClick(R.id.identity_card)
	public void onIdCardClicked() {
		startImageIntent(SELECT_ID_CARD_REQUEST_CODE);
	}

	@Override
	public void onPositiveButtonClicked(Button b) {

		ClientInfo info = getRenterInfo();

		boolean valid;
		if (TextUtils.isEmpty(info.firstname)) {
			mFirstNameLayout.setError(getString(R.string.error_first_name));
			valid = false;
		} else {
			mFirstNameLayout.setError(null);
			valid = true;
		}

		if (TextUtils.isEmpty(info.lastname)) {
			mLastNameLayout.setError(getString(R.string.error_last_name));
			valid = false;
		} else {
			mLastNameLayout.setError(null);
			valid &= true;
		}

		if (TextUtils.isEmpty(info.email) || !mEmailValidator.validate(info.email)) {
			mEmailLayout.setError(getString(R.string.error_email));
			valid = false;
		} else {
			mEmailLayout.setError(null);
			valid &= true;
		}

		if (TextUtils.isEmpty(info.company)) {
			mCompanyLayout.setError(getString(R.string.error_company));
			valid = false;
		} else {
			mCompanyLayout.setError(null);
			valid &= true;
		}

		if (TextUtils.isEmpty(info.phone)) {
			mPhoneLayout.setError(getString(R.string.error_phone));
			valid = false;
		} else {
			mPhoneLayout.setError(null);
			valid &= true;
		}

		if (mIdCardUri == null) {
			mIdCardText.setTextColor(Color.RED);
			valid = false;
		} else {
			mIdCardText.setTextColor(Color.BLACK);
			valid &= true;
		}

		if (mDrivingLicenceUri == null) {
			mDrivingLicenceText.setTextColor(Color.RED);
			valid = false;
		} else {
			mDrivingLicenceText.setTextColor(Color.BLACK);
			valid &= true;
		}

		if (valid || BuildConfig.DEBUG) {
			Bundle out = new Bundle();
			out.putParcelable(TunnelFragment.KEY_RENTER_INFO, info);
			getTunnel().next(out);
		}
	}

	@Override
	public void onNegativeButtonClicked(Button b) {
		// Goto dashboard
	}


	private void startImageIntent(int requestCode) {
		try {
			mOutputFileUri = Uri.fromFile(createImageFile());

			// Camera.
			final List<Intent> cameraIntents = new ArrayList<>();
			final Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			final PackageManager packageManager = getActivity().getPackageManager();
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
			if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
				galleryIntent.setAction(Intent.ACTION_OPEN_DOCUMENT);
				galleryIntent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
				galleryIntent.addCategory(Intent.CATEGORY_OPENABLE);
			}else{
				galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
			}

			galleryIntent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
			galleryIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
			// Chooser of filesystem options.
			final Intent chooserIntent = Intent.createChooser(galleryIntent, getString(R.string.picker_select_source));

			// Add the camera options.
			chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, cameraIntents.toArray(new Parcelable[cameraIntents.size()]));
			startActivityForResult(chooserIntent, requestCode);
		} catch (IOException e) {
			Toast.makeText(getActivity(), "Error creating picture", Toast.LENGTH_LONG).show();
			Dog.e(e, "IOException");
		}
	}

	private File createImageFile() throws IOException {
		// Create an image file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
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
		if ((requestCode == SELECT_ID_CARD_REQUEST_CODE || requestCode == SELECT_DRIVING_LICENCE_REQUEST_CODE) && resultCode == Activity.RESULT_OK) {
			Uri selectedImageUri = null;
			if (data == null || (data.getAction() != null && MediaStore.ACTION_IMAGE_CAPTURE.equals(data.getAction()))) {
				selectedImageUri = mOutputFileUri;
			} else {
				Uri uri = data.getData();
				if (!uri.equals(mOutputFileUri) && mOutputFileUri != null) {
					//noinspection ResultOfMethodCallIgnored
					new File(mOutputFileUri.getPath()).delete();
				}

				if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
					int takeFlags = data.getFlags();
					takeFlags &= (Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
					// Check for the freshest data.
					getActivity().getContentResolver().takePersistableUriPermission(uri, takeFlags);
				}

				String path = PathUtils.getPath(getActivity(), data.getData());
				if(path != null) {
					selectedImageUri = Uri.fromFile(new File(path));
				}
			}

			RequestCreator requestCreator = PicassoUtils.PICASSO.load(selectedImageUri).centerCrop().fit();
			switch (requestCode) {
				case SELECT_ID_CARD_REQUEST_CODE:
					mIdCardUri = selectedImageUri;
					requestCreator.into(mIdCardPicture);
					break;
				case SELECT_DRIVING_LICENCE_REQUEST_CODE:
					mDrivingLicenceUri = selectedImageUri;
					requestCreator.into(mDrivingLicencePicture);
					break;
				default:
			}

			Dog.d("Found picture : %s", selectedImageUri);
		}
	}
}
