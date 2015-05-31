package com.weproov.app.ui;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import butterknife.InjectView;
import com.weproov.app.R;
import com.weproov.app.ui.fragments.SettingsFragment;

public class SettingsActivity extends BaseActivity {

	@InjectView(R.id.action_bar)
	Toolbar mActionBar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		setSupportActionBar(mActionBar);

		getSupportActionBar().setHomeButtonEnabled(true);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		if (savedInstanceState == null) {
			// Display the fragment as the main content.
			getFragmentManager().beginTransaction()
					.add(R.id.content_fragment, new SettingsFragment())
					.commit();
		}
	}
}