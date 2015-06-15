package com.weproov.app.ui.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import butterknife.InjectView;
import butterknife.OnClick;
import com.weproov.app.R;
import com.weproov.app.models.CarInfo;
import com.weproov.app.models.ClientInfo;
import com.weproov.app.models.PictureItem;
import com.weproov.app.models.WeProov;
import com.weproov.app.ui.FullscreenImageDisplayActivity;
import com.weproov.app.ui.ifaces.CommandIface;
import com.weproov.app.utils.PicassoUtils;

import java.util.List;

public class SummaryFragment extends TunnelFragment implements CommandIface.OnClickListener {

	private WeProov mWeProov;

	private static final String KEY_WEPROOV = "key_weproov";

	private LayoutInflater mLayoutInflater;

	@InjectView(R.id.content)
	ScrollView mContentView;

	@InjectView(R.id.loader)
	LinearLayout mLoaderView;

	@InjectView(R.id.summary_client_display_name)
	TextView mClientDisplayName;
	@InjectView(R.id.summary_client_email)
	TextView mClientEmail;
	@InjectView(R.id.summary_client_company)
	TextView mClientCompany;
	@InjectView(R.id.summary_client_id)
	ImageView mClientIdCard;
	@InjectView(R.id.summary_client_driving_licence)
	ImageView mClientDrivingLicence;

	@InjectView(R.id.summary_car_info_plate)
	TextView mCarPlate;
	@InjectView(R.id.summary_car_info_brand_model)
	TextView mCarBrandModel;
	@InjectView(R.id.summary_car_info_color)
	TextView mCarColor;
	@InjectView(R.id.summary_car_info_mileage)
	TextView mCarMileage;
	@InjectView(R.id.summary_car_info_gas_tank)
	TextView mCarGasTank;
	@InjectView(R.id.summary_car_info_document)
	ImageView mCarDocument;

	@InjectView(R.id.summary_pictures)
	LinearLayout mPicturesLayout;

	@InjectView(R.id.summary_client_signature)
	ImageView mClientSignature;

	@InjectView(R.id.summary_renter_signature)
	ImageView mRenterSignature;

	public static SummaryFragment newInstance(WeProov weProov) {
		Bundle bundle = new Bundle();
		bundle.putParcelable(KEY_WEPROOV, weProov);

		SummaryFragment fragment = new SummaryFragment();
		fragment.setArguments(bundle);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (savedInstanceState != null && savedInstanceState.getParcelable(KEY_WEPROOV) != null) {
			mWeProov = savedInstanceState.getParcelable(KEY_WEPROOV);
		} else if (getArguments() != null) {
			mWeProov = getArguments().getParcelable(KEY_WEPROOV);
		}

		mLayoutInflater = LayoutInflater.from(getActivity());

		if (getTunnel() != null) {
			setCommmandListener(this);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_summary, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		setWeProov(mWeProov);
	}

	public void setWeProov(WeProov proov) {
		mWeProov = proov;
		if (mWeProov != null) {
			bindData(mWeProov);
		} else {
			mLoaderView.setVisibility(View.VISIBLE);
			mContentView.setVisibility(View.GONE);
		}
	}

	private void bindData(WeProov proov) {
		mLoaderView.setVisibility(View.GONE);
		mContentView.setVisibility(View.VISIBLE);

		ClientInfo client = proov.client;
		mClientDisplayName.setText(client.firstname + " " + client.lastname);
		mClientEmail.setText(client.email);
		mClientCompany.setText(client.company);
		PicassoUtils.PICASSO.load(client.id_card).fit().centerCrop().into(mClientIdCard);
		PicassoUtils.PICASSO.load(client.driving_licence).fit().centerCrop().into(mClientDrivingLicence);

		CarInfo car = proov.car;
		mCarPlate.setText(car.plate);
		mCarBrandModel.setText(car.brand + " " + car.model);
		mCarColor.setText(car.car_type + ", " + car.color);
		mCarMileage.setText(String.valueOf(car.millage) + car.millage_type);
		mCarGasTank.setText(String.valueOf(car.gas_level) + "%");
		PicassoUtils.PICASSO.load(car.vehicle_documentation).fit().centerCrop().into(mCarDocument);

		List<PictureItem> items = proov.getPictures();
		for (final PictureItem item : items) {
			View v = mLayoutInflater.inflate(R.layout.item_summary_pictures, mPicturesLayout, false);
			ImageView imageView = (ImageView) v.findViewById(R.id.summary_image);
			PicassoUtils.PICASSO.load(item.path).fit().centerCrop().into(imageView);
			v.findViewById(R.id.summary_comment).setVisibility(TextUtils.isEmpty(item.comment) ? View.GONE : View.VISIBLE);
			v.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent i = new Intent(getActivity(), FullscreenImageDisplayActivity.class);
					i.setData(item.path);
					startActivity(i);
				}
			});

			// :p
			mPicturesLayout.addView(v, mPicturesLayout.getChildCount() - 1);
		}

		PicassoUtils.PICASSO.load(proov.renterSignature).fit().centerCrop().into(mRenterSignature);
		PicassoUtils.PICASSO.load(proov.clientSignature).fit().centerCrop().into(mClientSignature);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putParcelable(KEY_WEPROOV, mWeProov);
	}

	@OnClick(R.id.edit_client_info)
	protected void onEditClientInfoClicked() {

	}

	@OnClick(R.id.edit_car_info)
	protected void onEditCarInfoClicked() {

	}

	@Override
	public void onPositiveButtonClicked(Button b) {
		new AlertDialog.Builder(getActivity())
				.setTitle(R.string.confirm_weproov_title)
				.setMessage(R.string.confirm_weproov_message)
				.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						getTunnel().next();
					}
				})
				.setNegativeButton(android.R.string.cancel, null)
				.show();
	}

	@Override
	public void onNegativeButtonClicked(Button b) {

	}
}
