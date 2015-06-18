package com.weproov.app.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.weproov.app.models.Country;
import com.weproov.app.utils.PixelUtils;

import java.util.ArrayList;
import java.util.Arrays;

public class CountrySpinnerAdapter extends ArrayAdapter<Country> {

	private final LayoutInflater mInflater;

	public CountrySpinnerAdapter(Context context) {
		super(context, 0, new ArrayList<>(Arrays.asList(Country.COUNTRIES)));
		mInflater = LayoutInflater.from(context);
	}

	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {
		final TextView tv;
		if (convertView != null) {
			tv = (TextView) convertView;
		} else {
			tv = (TextView) mInflater.inflate(android.R.layout.simple_dropdown_item_1line, parent, false);
		}

		Country country = getItem(position);
		tv.setText(country.name);
		tv.setCompoundDrawablePadding((int) PixelUtils.convertDpToPixel(8));
		tv.setCompoundDrawablesWithIntrinsicBounds(country.res, 0, 0, 0);
		return tv;
	}

	@Override
	public View getView(final int position, final View convertView, final ViewGroup parent) {
		final ImageView tv;
		if (convertView != null) {
			tv = (ImageView) convertView;
		} else {
			tv = new ImageView(getContext());
		}

		Country country = getItem(position);
		tv.setImageResource(country.res);
		return tv;
	}
}
