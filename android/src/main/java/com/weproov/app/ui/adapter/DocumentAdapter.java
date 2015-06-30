package com.weproov.app.ui.adapter;

import android.content.Context;
import android.os.Build;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.weproov.app.R;
import com.weproov.app.models.CarInfo;
import com.weproov.app.models.ClientInfo;
import com.weproov.app.models.PictureItem;
import com.weproov.app.models.WeProov;
import com.weproov.app.utils.PicassoUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DocumentAdapter extends ArrayAdapter<WeProov> {

	private static final String PATTERN;
	private String mQuery;
	private int mAccentColor;

	public void setQuery(String query) {
		mQuery = query;
	}

	static {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
			PATTERN = DateFormat.getBestDateTimePattern(Locale.getDefault(), "MMMMddyyyyHHmm");
		} else {
			PATTERN = "MM/dd/yyyy HH:mm";
		}
	}

	private static final SimpleDateFormat sDateFormatter = new SimpleDateFormat(PATTERN, Locale.getDefault());

	static class ViewHolder {
		public ImageView image;
		public TextView car;
		public TextView client;
		public TextView date;
	}

	public DocumentAdapter(Context context, List<WeProov> proovs) {
		super(context, 0, proovs);
		mAccentColor = context.getResources().getColor(R.color.accent);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			LayoutInflater inflater = LayoutInflater.from(getContext());
			convertView = inflater.inflate(R.layout.item_document, parent, false);
			ViewHolder holder = new ViewHolder();
			holder.image = (ImageView) convertView.findViewById(R.id.document_picture);
			holder.car = (TextView) convertView.findViewById(R.id.document_car_info);
			holder.client = (TextView) convertView.findViewById(R.id.document_client_info);
			holder.date = (TextView) convertView.findViewById(R.id.document_date);
			convertView.setTag(R.id.holder, holder);
		}

		ViewHolder holder = (ViewHolder) convertView.getTag(R.id.holder);
		WeProov proov = getItem(position);

		CarInfo carInfo = proov.car;
		holder.car.setText(TextUtils.concat(setHighlight(carInfo.model), " ", setHighlight(carInfo.brand), " (", setHighlight(carInfo.plate), ")"));

		ClientInfo clientInfo = proov.client;
		holder.client.setText(TextUtils.concat(setHighlight(clientInfo.firstname), " ", setHighlight(clientInfo.lastname), " (", setHighlight(clientInfo.company), ")"));

		holder.date.setText(sDateFormatter.format(new Date(proov.getDate())));

		// TODO : Here don't load picture from database in UI Thread
		List<PictureItem> pictures = proov.getPictures();
		if (pictures != null && pictures.size() > 0) {
			PicassoUtils.PICASSO.load(pictures.get(0).path).fit().centerCrop().into(holder.image);
		}

		return convertView;
	}

	private CharSequence setHighlight(String text) {
		if (!TextUtils.isEmpty(mQuery)) {
			int index = text.indexOf(mQuery);
			if (index >= 0) {
				Spannable sequence = new SpannableString(text);
				sequence.setSpan(new ForegroundColorSpan(mAccentColor), index, index + mQuery.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
				return sequence;
			} else {
				return text;
			}
		} else {
			return text;
		}
	}
}