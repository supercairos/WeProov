package com.weproov.app.ui.fragments;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import butterknife.InjectView;
import butterknife.OnClick;
import com.weproov.app.BuildConfig;
import com.weproov.app.R;
import com.weproov.app.models.CarInfo;
import com.weproov.app.ui.adapter.PlateAutocompleteAdapter;
import com.weproov.app.ui.ifaces.CommandIface;
import com.weproov.app.utils.PicassoUtils;
import com.weproov.app.utils.PixelUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class CarInfoFragment extends TunnelFragment implements CommandIface.OnClickListener {

	private static final int SELECT_VEHICLE_DOC_REQUEST_CODE = 999;

	private Uri mOutputFileUri;
	private Uri mVehicleDocumentationUri;

	private String[] mMillageTypeServer;
	private String[] mCarTypeServer;

	private PlateAutocompleteAdapter mAutocompleteAdapter;
	@InjectView(R.id.edit_car_plate_number)
	AutoCompleteTextView mPlateNumber;
	@InjectView(R.id.edit_car_plate_number_error)
	TextView mPlateNumberError;

	@InjectView(R.id.edit_car_brand)
	EditText mBrand;
	@InjectView(R.id.edit_car_brand_error)
	TextView mBrandError;

	@InjectView(R.id.edit_car_model)
	EditText mModel;
	@InjectView(R.id.edit_car_model_error)
	TextView mModelError;

	@InjectView(R.id.edit_car_millage)
	EditText mMillage;
	@InjectView(R.id.edit_car_millage_error)
	TextView mMillageError;

	@InjectView(R.id.spinner_car_millage_type)
	Spinner mMillageType;

	@InjectView(R.id.edit_car_color)
	EditText mColor;
	@InjectView(R.id.edit_car_color_error)
	TextView mColorError;

	@InjectView(R.id.seekbar_car_gas_level)
	SeekBar mGasLevel;

	@InjectView(R.id.spinner_car_type)
	Spinner mCarType;

	@InjectView(R.id.vehicle_documentation_picture)
	ImageView mVehicleDocumentationPicture;
	@InjectView(R.id.vehicle_documentation_text)
	TextView mVehicleDocumentationText;


	public static CarInfoFragment newInstance(CarInfo info) {
		CarInfoFragment fragment = new CarInfoFragment();
		Bundle bundle = new Bundle();
		bundle.putParcelable(TunnelFragment.KEY_CAR_INFO, info);
		fragment.setArguments(bundle);
		return fragment;
	}

	public CarInfoFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_car_info, container, false);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mCarTypeServer = getResources().getStringArray(R.array.car_type_server);
		mMillageTypeServer = getResources().getStringArray(R.array.millage_type_server);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setCommmandListener(this);

		CarInfo info = null;
		if (savedInstanceState != null) {
			info = savedInstanceState.getParcelable(TunnelFragment.KEY_RENTER_INFO);
		} else if (getArguments() != null) {
			info = getArguments().getParcelable(TunnelFragment.KEY_RENTER_INFO);
		}

		if (info != null) {
			mPlateNumber.setText(info.plate);
			mBrand.setText(info.brand);
			mModel.setText(info.model);
			mMillage.setText(String.valueOf(info.millage));
			mMillageType.setSelection(getServerIndex(mMillageTypeServer, info.millage_type));
			mColor.setText(info.color);

			mGasLevel.setProgress(info.gas_level);
			mCarType.setSelection(getServerIndex(mCarTypeServer, info.millage_type));

			int size = (int) PixelUtils.convertDpToPixel(100);
			mVehicleDocumentationUri = info.vehicle_documentation;
			PicassoUtils.PICASSO.load(mVehicleDocumentationUri).centerInside().resize(size, size).placeholder(R.drawable.card1).into(mVehicleDocumentationPicture);
		}

		mAutocompleteAdapter = new PlateAutocompleteAdapter(getActivity());
		mPlateNumber.setAdapter(mAutocompleteAdapter);
		mPlateNumber.setThreshold(1);
		mPlateNumber.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				CarInfo info = mAutocompleteAdapter.getItem(position);
				mBrand.setText(info.brand);
				mModel.setText(info.model);
				mMillageType.setSelection(getServerIndex(mMillageTypeServer, info.millage_type));
				mColor.setText(info.color);
				mCarType.setSelection(getServerIndex(mCarTypeServer, info.millage_type));

				int size = (int) PixelUtils.convertDpToPixel(100);
				mVehicleDocumentationUri = info.vehicle_documentation;
				PicassoUtils.PICASSO.load(mVehicleDocumentationUri).centerInside().resize(size, size).placeholder(R.drawable.card1).into(mVehicleDocumentationPicture);
			}
		});
		mPlateNumber.setFilters(new InputFilter[] {new InputFilter.AllCaps(), new InputFilter.LengthFilter(9)});
		mPlateNumber.addTextChangedListener(new TextWatcher() {

			boolean isDeletion = false;

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				isDeletion = count > after;
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				if (!isDeletion && (s.length() == 2 || s.length() == 6)) {
					s.append("-");
				}
			}
		});
	}

	private int getServerIndex(String[] serverValues, String serverVal) {
		for (int i = 0; i < serverValues.length; i++) {
			String val = serverValues[i];
			if (serverVal.equals(val)) {
				return i;
			}
		}

		return 0;
	}

	private CarInfo getCarInfo() {
		CarInfo info = new CarInfo();
		try {
			info.plate = mPlateNumber.getEditableText().toString().toUpperCase();
			info.brand = mBrand.getEditableText().toString();
			info.model = mModel.getEditableText().toString();
			info.color = mColor.getEditableText().toString();

			try {
				info.millage = Float.parseFloat(mMillage.getEditableText().toString().replaceAll("[^\\d.,-]", ""));
			} catch (NumberFormatException e) {
				info.millage = -1;
			}
			info.millage_type = mMillageTypeServer[mMillageType.getSelectedItemPosition()];

			info.gas_level = mGasLevel.getProgress();
			info.car_type = mCarTypeServer[mCarType.getSelectedItemPosition()];

			info.vehicle_documentation = mVehicleDocumentationUri;

		} catch (NullPointerException e) {
			// Ignore
		}
		return info;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putParcelable(TunnelFragment.KEY_CAR_INFO, getCarInfo());
		super.onSaveInstanceState(outState);
	}

	@Override
	public void onPositiveButtonClicked(Button b) {

		CarInfo info = getCarInfo();
		boolean valid;
		if (TextUtils.isEmpty(info.plate) || info.plate.length() != 9) {
			mPlateNumberError.setVisibility(View.VISIBLE);
			valid = false;
		} else {
			mPlateNumberError.setVisibility(View.INVISIBLE);
			valid = true;
		}

		if (TextUtils.isEmpty(info.brand)) {
			mBrandError.setVisibility(View.VISIBLE);
			valid = false;
		} else {
			mBrandError.setVisibility(View.INVISIBLE);
			valid &= true;
		}

		if (TextUtils.isEmpty(info.model)) {
			mModelError.setVisibility(View.VISIBLE);
			valid = false;
		} else {
			mModelError.setVisibility(View.INVISIBLE);
			valid &= true;
		}

		if (TextUtils.isEmpty(info.color)) {
			mColorError.setVisibility(View.VISIBLE);
			valid = false;
		} else {
			mColorError.setVisibility(View.INVISIBLE);
			valid &= true;
		}

		if (info.millage < 0) {
			mMillageError.setVisibility(View.VISIBLE);
			valid = false;
		} else {
			mMillageError.setVisibility(View.INVISIBLE);
			valid &= true;
		}

		if (mVehicleDocumentationUri == null) {
			mVehicleDocumentationText.setTextColor(Color.RED);
			valid = false;
		} else {
			mVehicleDocumentationText.setTextColor(Color.BLACK);
			valid &= true;
		}

		if (valid || BuildConfig.DEBUG) {
			Bundle out = new Bundle();
			out.putParcelable(TunnelFragment.KEY_CAR_INFO, info);
			getTunnel().next(out);
		}
	}


	@Override
	public void onNegativeButtonClicked(Button b) {

	}

	@OnClick(R.id.vehicle_documentation)
	public void onDrivingLicenceClicked() {
		startImageIntent(SELECT_VEHICLE_DOC_REQUEST_CODE);
	}

	private void startImageIntent(int requestCode) {
		final File file = new File(getActivity().getCacheDir(), "renter_" + System.currentTimeMillis() + ".jpg");
		mOutputFileUri = Uri.fromFile(file);

		// Camera.
		final List<Intent> cameraIntents = new ArrayList<>();
		final Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
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

		// Chooser of filesystem options.
		final Intent chooserIntent = Intent.createChooser(galleryIntent, getString(R.string.picker_select_source));

		// Add the camera options.
		chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, cameraIntents.toArray(new Parcelable[cameraIntents.size()]));
		startActivityForResult(chooserIntent, requestCode);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == SELECT_VEHICLE_DOC_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
			if (data == null || (data.getAction() != null && data.getAction().equals(android.provider.MediaStore.ACTION_IMAGE_CAPTURE))) {
				mVehicleDocumentationUri = mOutputFileUri;
			} else {
				mVehicleDocumentationUri = data.getData();
			}

			int size = (int) PixelUtils.convertDpToPixel(100);
			PicassoUtils.PICASSO.load(mVehicleDocumentationUri).centerInside().resize(size, size).into(mVehicleDocumentationPicture);

			Log.d("Test", "Found picture : " + mVehicleDocumentationUri);
		}
	}
}
