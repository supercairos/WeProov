package com.weproov.app.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import butterknife.InjectView;
import com.weproov.app.R;
import com.weproov.app.ui.adapter.DocumentPageAdapter;
import com.weproov.app.ui.fragments.dialogs.BugReportDialogFragment;
import com.weproov.app.ui.fragments.dialogs.LogoutDialogFragment;
import com.weproov.app.utils.FragmentsUtils;

public class ReportListActivity extends DrawerActivity {

	@InjectView(R.id.action_bar)
	Toolbar mActionBar;

	@InjectView(R.id.document_view_pager)
	ViewPager mViewPager;

	@InjectView(R.id.tab_layout)
	TabLayout mTabLayout;

	private PagerAdapter mPagerAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_reports);
		setSupportActionBar(mActionBar);

		// enable ActionBar app icon to behave as action to toggle nav drawer
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);

		setTitle(getString(R.string.title_activity_my_reports));

		mPagerAdapter = new DocumentPageAdapter(getSupportFragmentManager(), this);
		mViewPager.setAdapter(mPagerAdapter);
		mViewPager.setPageTransformer(true, new ZoomOutPageTransformer());
		mTabLayout.setupWithViewPager(mViewPager);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_main_activity, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		//noinspection SimplifiableIfStatement
		if (id == R.id.action_report_bug) {
			FragmentsUtils.showDialog(this, new BugReportDialogFragment());
			return true;
		} else if (id == R.id.action_settings) {
			startActivity(new Intent(this, SettingsActivity.class));
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onNavItemSelected(MenuItem item) {
		DialogFragment fragment;
		switch (item.getItemId()) {
			case R.id.navigation_account_logout:
				fragment = new LogoutDialogFragment();
				break; // Keep the return here
			case R.id.navigation_weproov_new:
				mDrawerLayout.closeDrawer(mDrawerNavigation);
				Intent intent = new Intent(this, WeproovActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				return true; // Keep the return here
			case R.id.navigation_dashboard:
				mDrawerLayout.closeDrawer(mDrawerNavigation);
				Intent main = new Intent(this, MainActivity.class);
				main.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(main);
				return true; // Keep the return here
			case R.id.navigation_weproov_list:
				mDrawerLayout.closeDrawer(mDrawerNavigation);
				return true; // Keep the return here
			case R.id.navigation_account_about:
				mDrawerLayout.closeDrawer(mDrawerNavigation);
				startActivity(new Intent(this, AboutActivity.class));
				return true; // Keep the return here
			default:
				return false;
		}

		FragmentsUtils.showDialog(this, fragment);
		mDrawerLayout.closeDrawer(mDrawerNavigation);
		return true;
	}

	private class ZoomOutPageTransformer implements ViewPager.PageTransformer {
		private static final float MIN_SCALE = 0.85f;
		private static final float MIN_ALPHA = 0.5f;

		public void transformPage(View view, float position) {
			int pageWidth = view.getWidth();
			int pageHeight = view.getHeight();

			if (position < -1) { // [-Infinity,-1)
				// This page is way off-screen to the left.
				view.setAlpha(0);

			} else if (position <= 1) { // [-1,1]
				// Modify the default slide transition to shrink the page as well
				float scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position));
				float vertMargin = pageHeight * (1 - scaleFactor) / 2;
				float horzMargin = pageWidth * (1 - scaleFactor) / 2;
				if (position < 0) {
					view.setTranslationX(horzMargin - vertMargin / 2);
				} else {
					view.setTranslationX(-horzMargin + vertMargin / 2);
				}

				// Scale the page down (between MIN_SCALE and 1)
				view.setScaleX(scaleFactor);
				view.setScaleY(scaleFactor);

				// Fade the page relative to its size.
				view.setAlpha(MIN_ALPHA +
						(scaleFactor - MIN_SCALE) /
								(1 - MIN_SCALE) * (1 - MIN_ALPHA));

			} else { // (1,+Infinity]
				// This page is way off-screen to the right.
				view.setAlpha(0);
			}
		}
	}
}
