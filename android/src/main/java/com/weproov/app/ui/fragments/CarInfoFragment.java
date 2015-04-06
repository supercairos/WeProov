package com.weproov.app.ui.fragments;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import butterknife.InjectView;
import butterknife.OnClick;
import com.weproov.app.R;
import com.weproov.app.ui.ifaces.CommandIface;
import com.weproov.app.utils.PicassoUtils;
import com.weproov.app.utils.PixelUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class CarInfoFragment extends TunnelFragment implements CommandIface.OnClickListener {

	private static final int SELECT_VEHICULE_DOC_REQUEST_CODE = 999;

	@InjectView(R.id.car_type_spinner)
	Spinner mCarType;

	@InjectView(R.id.millage_type_spinner)
	Spinner mMillageType;

	private Uri mOutputFileUri;
	private Uri mVehicleDocumentationUri;

	@InjectView(R.id.vehicle_documentation_picture)
	ImageView mVehicleDocumentation;

	public CarInfoFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_car_info, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setCommmandListener(this);
	}

	@Override
	public void onPositiveButtonClicked(Button b) {
		Log.d("Test", "Millage type is : " + mMillageType.getSelectedItem());
		Log.d("Test", "Car type is : " + mCarType.getSelectedItem());

		Bundle bundle = new Bundle();
		getTunnel().next(bundle);
	}


	@Override
	public void onNegativeButtonClicked(Button b) {

	}

	@OnClick(R.id.vehicle_documentation)
	public void onDrivingLicenceClicked() {
		startImageIntent(SELECT_VEHICULE_DOC_REQUEST_CODE);
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
		if (requestCode == SELECT_VEHICULE_DOC_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
			if (data == null || (data.getAction() != null && data.getAction().equals(android.provider.MediaStore.ACTION_IMAGE_CAPTURE))) {
				mVehicleDocumentationUri = mOutputFileUri;
			} else {
				mVehicleDocumentationUri = data.getData();
			}

			int size = (int) PixelUtils.convertDpToPixel(100, getActivity());
			PicassoUtils.PICASSO.load(mVehicleDocumentationUri).centerInside().resize(size, size).into(mVehicleDocumentation);

			Log.d("Test", "Found picture : " + mVehicleDocumentationUri);
		}
	}
}
