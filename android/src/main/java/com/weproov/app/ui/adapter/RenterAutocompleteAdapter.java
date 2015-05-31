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
import com.weproov.app.models.ClientInfo;
import com.weproov.app.utils.Dog;

import java.util.Collection;
import java.util.List;

public class RenterAutocompleteAdapter extends ArrayAdapter<ClientInfo> implements Filterable {

	public static final int TYPE_FIRST_NAME = 0;
	public static final int TYPE_LAST_NAME = 1;

	private LayoutInflater mInflater;

	private int mType = TYPE_FIRST_NAME;

	public int getType() {
		return mType;
	}

	public RenterAutocompleteAdapter(final Context context, int type) {
		super(context, -1);
		mInflater = LayoutInflater.from(context);
		mType = type;
	}

	@Override
	public View getView(final int position, final View convertView, final ViewGroup parent) {
		final TextView tv;
		if (convertView != null) {
			tv = (TextView) convertView;
		} else {
			tv = (TextView) mInflater.inflate(android.R.layout.simple_dropdown_item_1line, parent, false);
		}

		ClientInfo info = getItem(position);
		tv.setText(info.firstname + " " + info.lastname);
		return tv;
	}

	@Override
	public Filter getFilter() {
		return new Filter() {
			@Override
			protected FilterResults performFiltering(final CharSequence constraint) {
				List<CarInfo> infos = null;
				if (constraint != null) {
					infos = new Select().from(ClientInfo.class).where((getType() == TYPE_FIRST_NAME ? "firstname" : "lastname")+" LIKE ?", constraint + "%").groupBy("firstname COLLATE NOCASE, lastname COLLATE NOCASE").orderBy("firstname DESC").execute();
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
					addAll((Collection<? extends ClientInfo>) results.values);
				}

				if (results.count > 0) {
					notifyDataSetChanged();
				} else {
					notifyDataSetInvalidated();
				}
			}

			@Override
			public CharSequence convertResultToString(final Object resultValue) {
				ClientInfo info = ((ClientInfo) resultValue);
				return resultValue == null ? "" : info.firstname + " " + info.lastname;
			}
		};
	}
}