package com.weproov.app.ui;

import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import butterknife.InjectView;
import com.activeandroid.content.ContentProvider;
import com.weproov.app.R;
import com.weproov.app.models.WeProov;
import com.weproov.app.ui.fragments.SummaryFragment;

public class DocumentDisplayActivity extends BaseActivity implements LoaderManager.LoaderCallbacks<Cursor> {

	public static final String EXTRA_WEPROOV_ID = "extra_weproov_id";
	private static final int LOADER_ID = 1569;

	@InjectView(R.id.action_bar)
	Toolbar mActionBar;

	@InjectView(R.id.collapsing_toolbar)
	CollapsingToolbarLayout mCollapsingToolbarLayout;

	private long mWeProovId;
	private SummaryFragment mSummaryFragment;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_document_display);
		setSupportActionBar(mActionBar);

		//noinspection ConstantConditions
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		mWeProovId = getIntent().getLongExtra(EXTRA_WEPROOV_ID, -1);
		mCollapsingToolbarLayout.setTitle(getString(R.string.app_name));
		mSummaryFragment = (SummaryFragment) getSupportFragmentManager().findFragmentById(R.id.summary_fragment);
	}

	@Override
	protected void onStart() {
		super.onStart();
		getSupportLoaderManager().restartLoader(LOADER_ID, null, this);
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

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		return new CursorLoader(this, ContentProvider.createUri(WeProov.class, mWeProovId), null, null, null, null);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		if (data != null) {
			try {
				if (data.moveToFirst()) {
					final WeProov proov = new WeProov();
					proov.loadFromCursor(data);
					mCollapsingToolbarLayout.setTitle(proov.car.brand + " " + proov.car.model);
					mSummaryFragment.setWeProov(proov);
				}
			} finally {
				data.close();
			}
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {

	}
}
