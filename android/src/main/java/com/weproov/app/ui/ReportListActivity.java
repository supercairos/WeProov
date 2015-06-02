package com.weproov.app.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import butterknife.InjectView;
import com.weproov.app.R;
import com.weproov.app.ui.fragments.dialogs.AboutDialogFragment;
import com.weproov.app.ui.fragments.dialogs.BugReportDialogFragment;
import com.weproov.app.ui.fragments.dialogs.LogoutDialogFragment;
import com.weproov.app.utils.FragmentsUtils;

public class ReportListActivity extends DrawerActivity {

	@InjectView(R.id.action_bar)
	Toolbar mActionBar;

//	@InjectView(R.id.collapsing_actionbar_layout)
//	CollapsingToolbarLayout mCollapsingToolbarLayout;

	@InjectView(R.id.tab_layout)
	TabLayout mTabLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_reports);
		setSupportActionBar(mActionBar);

		// enable ActionBar app icon to behave as action to toggle nav drawer
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);

		setTitle(getString(R.string.title_activity_my_reports));

		mTabLayout.addTab(mTabLayout.newTab().setText("All"));
		mTabLayout.addTab(mTabLayout.newTab().setText("Checkout"));
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
				fragment = new AboutDialogFragment();
				break;
			default:
				return false;
		}

		FragmentsUtils.showDialog(this, fragment);
		mDrawerLayout.closeDrawer(mDrawerNavigation);
		return true;
	}
}
