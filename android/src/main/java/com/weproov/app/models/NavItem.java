package com.weproov.app.models;

import com.weproov.app.MyApplication;
import com.weproov.app.R;

import java.util.ArrayList;
import java.util.List;

public class NavItem {

    public static final int NAV_DASHBOARD = 0;
    public static final int NAV_WEPROOV = 1;
    public static final int NAV_MY_DOCUMENTS = 2;
    public static final int NAV_LOGOUT = 3;
    public static final int NAV_ABOUT = 4;


    public int id;
    private int labelResId;
    private int iconResId;

    public NavItem(int id, int labelResId, int iconResId) {
        this.id = id;
        this.labelResId = labelResId;
        this.iconResId = iconResId;
    }

    public String getLabel() {
        return MyApplication.getAppContext().getString(labelResId);
    }

    public int getIconResId() {
        return iconResId;
    }

    private static final List<NavItem> ARRAY = new ArrayList<>();

    public static List<NavItem> getNavItems() {

        if (ARRAY.size() == 0) {
            // Fill it;
            ARRAY.add(new NavItem(NAV_DASHBOARD, R.string.nav_dashboard, R.drawable.ic_action_about));
            ARRAY.add(new NavItem(NAV_WEPROOV, R.string.nav_weproov, R.drawable.ic_action_send_now));
            ARRAY.add(new NavItem(NAV_MY_DOCUMENTS, R.string.nav_my_documents, R.drawable.ic_action_view_as_list));
            ARRAY.add(new NavItem(NAV_LOGOUT, R.string.nav_logout, R.drawable.ic_action_accounts));
            ARRAY.add(new NavItem(NAV_ABOUT, R.string.nav_about, R.drawable.ic_action_about));
        }

        return ARRAY;
    }
}
