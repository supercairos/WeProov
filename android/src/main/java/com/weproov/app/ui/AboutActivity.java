package com.weproov.app.ui;

import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import butterknife.InjectView;
import com.weproov.app.R;
import com.weproov.app.ui.fragments.dialogs.BugReportDialogFragment;
import com.weproov.app.utils.FragmentsUtils;

public class AboutActivity extends BaseActivity {

	@InjectView(R.id.action_bar)
	Toolbar mActionBar;

	@InjectView(R.id.collapsing_toolbar)
	CollapsingToolbarLayout mCollapsingToolbarLayout;

	@InjectView(R.id.floating_action_button)
	FloatingActionButton mFloatingActionButton;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);
		setSupportActionBar(mActionBar);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		mCollapsingToolbarLayout.setTitle(getString(R.string.app_name));

		mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				FragmentsUtils.showDialog(AboutActivity.this, new BugReportDialogFragment());
			}
		});
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				finish();
				return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
