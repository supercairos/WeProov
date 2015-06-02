package com.weproov.app.ui;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.weproov.app.R;
import com.weproov.app.ui.views.BitmapRegionTileSource;
import com.weproov.app.ui.views.TiledImageView;

public class FullscreenImageDisplayActivity extends BaseActivity {

	@InjectView(R.id.tiled_image_view)
	TiledImageView mImageView;

	@InjectView(R.id.action_bar)
	Toolbar mActionBar;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fullscreen_image_display);
		ButterKnife.inject(this);
		setSupportActionBar(mActionBar);

		// enable ActionBar app icon to behave as action to toggle nav drawer
		//noinspection ConstantConditions
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		// getSupportActionBar().setHomeButtonEnabled(true);

		String path = getIntent().getData().getPath();
		mImageView.setTileSource(new BitmapRegionTileSource(this, path, 0, 0), null);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		//noinspection SimplifiableIfStatement
		if (id == android.R.id.home) {
			finish();
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onResume() {
		super.onResume();
		mImageView.onResume();
	}

	@Override
	protected void onPause() {
		mImageView.onPause();
		super.onPause();
	}

	@Override
	public void onDestroy() {
		mImageView.destroy();
		super.onDestroy();
	}

}
