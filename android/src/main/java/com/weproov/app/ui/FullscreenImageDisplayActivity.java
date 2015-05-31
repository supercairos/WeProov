package com.weproov.app.ui;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
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
