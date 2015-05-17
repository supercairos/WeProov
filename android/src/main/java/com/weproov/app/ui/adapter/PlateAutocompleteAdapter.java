package com.weproov.app.ui.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import com.activeandroid.query.Select;
import com.weproov.app.models.CarInfo;
import com.weproov.app.utils.Dog;

import java.util.Collection;
import java.util.List;

public class PlateAutocompleteAdapter extends ArrayAdapter<CarInfo> implements Filterable {

	private LayoutInflater mInflater;

	public PlateAutocompleteAdapter(final Context context) {
		super(context, -1);
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

		tv.setText(getItem(position).plate);
		return tv;
	}

	@Override
	public Filter getFilter() {
		return new Filter() {
			@Override
			protected FilterResults performFiltering(final CharSequence constraint) {
				List<CarInfo> infos = null;
				if (constraint != null) {
					infos = new Select().from(CarInfo.class).where("plate LIKE ?", constraint + "%").groupBy("plate COLLATE NOCASE").orderBy("plate DESC").execute();
				}

				Dog.d("Found values => %s", infos);

				final FilterResults filterResults = new FilterResults();
				filterResults.values = infos;
				filterResults.count = infos != null ? infos.size() : 0;

				return filterResults;
			}

			@SuppressWarnings("unchecked")
			@Override
			protected void publishResults(final CharSequence contraint, final FilterResults results) {
				clear();
				if (results.values != null) {
					addAll((Collection<? extends CarInfo>) results.values);
				}

				if (results.count > 0) {
					notifyDataSetChanged();
				} else {
					notifyDataSetInvalidated();
				}
			}

			@Override
			public CharSequence convertResultToString(final Object resultValue) {
				return resultValue == null ? "" : ((CarInfo) resultValue).plate;
			}
		};
	}
}