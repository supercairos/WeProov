package com.weproov.app.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.weproov.app.R;
import com.weproov.app.models.NavItem;

import java.util.List;

public class NavigationAdapter extends ArrayAdapter<NavItem> {


    public NavigationAdapter(Context context, List<NavItem> services) {
        super(context, 0, services);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final NavItem service = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_navigation, parent, false);
        }

        // Lookup view for data population
        final TextView label = (TextView) convertView.findViewById(android.R.id.text1);
        final ImageView icon = (ImageView) convertView.findViewById(android.R.id.icon);

        label.setText(service.getLabel());
        icon.setImageResource(service.getIconResId());

        return convertView;
    }

}
