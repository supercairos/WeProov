package com.weproov.app.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.weproov.app.models.Country;
import com.weproov.app.utils.PixelUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CountryAutocompleteAdapter extends ArrayAdapter<Country> implements Filterable {

	private final LayoutInflater mInflater;

	public CountryAutocompleteAdapter(Context context) {
		super(context, 0, new ArrayList<>(Arrays.asList(Country.COUNTRIES)));
		mInflater = LayoutInflater.from(context);
	}

	@Override
	public View getView(final int position, final View convertView, final ViewGroup parent) {
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
	public Filter getFilter() {
		return new Filter() {
			@Override
			protected FilterResults performFiltering(CharSequence constraint) {
				List<Country> ok = new ArrayList<>();
				if (constraint != null) {
					if (constraint.length() == 1 && constraint.charAt(0) == '+') {
						ok.addAll(Arrays.asList(Country.COUNTRIES));
					} else if (constraint.length() > 1 && constraint.length() < 5) {
						if (constraint.charAt(0) == '+') {
							String s = constraint.subSequence(1, constraint.length()).toString();
							for (Country country : Country.COUNTRIES) {
								if (String.valueOf(country.phone).startsWith(s)) {
									ok.add(country);
								}
							}
						}
					}
				}

				FilterResults results = new FilterResults();
				results.count = ok.size();
				results.values = ok;

				return results;
			}

			@Override
			protected void publishResults(CharSequence constraint, FilterResults results) {
				clear();
				if (results.values != null && results.count > 0) {
					addAll((List<Country>) results.values);
				}
			}
		};
	}
}
