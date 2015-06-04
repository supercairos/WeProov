package com.weproov.app.ui.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;
import com.weproov.app.R;
import com.weproov.app.models.WeProov;
import com.weproov.app.utils.Dog;

public class DocumentAdapter extends CursorAdapter {

	static class ViewHolder {
		public TextView text;
	}

	public DocumentAdapter(Context context, Cursor c) {
		super(context, c, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		LayoutInflater inflater = LayoutInflater.from(context);
		View view = inflater.inflate(R.layout.item_document, parent, false);
		ViewHolder holder = new ViewHolder();
		holder.text = (TextView) view.findViewById(R.id.info_text);
		view.setTag(R.id.holder, holder);

		return view;
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		ViewHolder holder = (ViewHolder) view.getTag(R.id.holder);
		WeProov proov = new WeProov();
		proov.loadFromCursor(cursor);
		Dog.d("Proov = %s", proov);
		holder.text.setText("Test");
	}
}