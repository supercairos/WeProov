package com.weproov.app.ui.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import com.weproov.app.R;
import com.weproov.app.ui.fragments.DocumentListFragment;

import java.util.List;

public class DocumentPageAdapter extends FragmentPagerAdapter {

	private FragmentManager mFragmentManager;
	private Context mContext;

	public void setQuery(String query) {
		List<Fragment> fragments = mFragmentManager.getFragments();
		for (Fragment fragment : fragments) {
			if (fragment instanceof DocumentListFragment) {
				((DocumentListFragment) fragment).setQuery(query);
			}
		}
	}

	public DocumentPageAdapter(FragmentManager fm, Context context) {
		super(fm);
		mFragmentManager = fm;
		mContext = context;
	}

	@Override
	public Fragment getItem(int position) {
		switch (position) {
			case 1:
				return DocumentListFragment.newInstance(DocumentListFragment.MODE_ALL);
			case 0:
			default:
				return DocumentListFragment.newInstance(DocumentListFragment.MODE_ONLY_CHECKOUT);
		}
	}

	@Override
	public int getCount() {
		return 2;
	}

	@Override
	public CharSequence getPageTitle(int position) {
		switch (position) {
			case 1:
				return mContext.getString(R.string.title_document_all);
			case 0:
			default:
				return mContext.getString(R.string.title_document_checkout);
		}
	}
}
