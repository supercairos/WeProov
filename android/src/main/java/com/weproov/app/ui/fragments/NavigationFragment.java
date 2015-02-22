package com.weproov.app.ui.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import butterknife.InjectView;
import com.weproov.app.R;
import com.weproov.app.ui.adapter.NavigationAdapter;
import com.weproov.app.models.NavItem;
import com.weproov.app.utils.constants.Constants;
import com.weproov.app.utils.PrefUtils;

public class NavigationFragment extends BaseFragment {

    private OnNavigationInteractionListener mListener;
    private NavigationAdapter mAdapter;

    @InjectView(R.id.drawer_list)
    ListView mDrawerList;

    @InjectView(R.id.drawer_title)
    TextView mDrawerTitle;

    @InjectView(R.id.drawer_subtitle)
    TextView mDrawerSubtitle;

    public NavigationFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_navigation, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // set up the drawer's list view with items and click listener
        mAdapter = new NavigationAdapter(getActivity(), NavItem.getNavItems());
        mDrawerList.setAdapter(mAdapter);
        mDrawerList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        // Check first item;
        mDrawerList.setItemChecked(0, true);

        String username = PrefUtils.getString(Constants.KEY_DISPLAY_NAME, "");
        String email = PrefUtils.getString(Constants.KEY_EMAIL, "");

        mDrawerTitle.setText(username);
        mDrawerSubtitle.setText(email);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnNavigationInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnNavigationInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnNavigationInteractionListener {
        public void onNavItemSelected(NavItem item);
    }

    /* The click listner for ListView in the navigation drawer */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            mListener.onNavItemSelected(mAdapter.getItem(position));
            mDrawerList.setItemChecked(position, true);
        }
    }

}
