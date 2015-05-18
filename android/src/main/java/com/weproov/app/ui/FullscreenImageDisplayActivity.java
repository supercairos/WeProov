package com.weproov.app.ui;

import android.os.Bundle;
import butterknife.InjectView;
import com.weproov.app.R;
import com.weproov.app.ui.views.BitmapRegionTileSource;
import com.weproov.app.ui.views.TiledImageView;

public class FullscreenImageDisplayActivity extends BaseActivity {

	private static final String PATH = "file:///storage/emulated/0/Pictures/WeProov/IMG_20150518_161410.jpg";

	@InjectView(R.id.tiled_image_view)
	TiledImageView mImageView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fullscreen_image_display);

		mImageView.setTileSource(new BitmapRegionTileSource(this, PATH, 0, 0), null);
	}

	@Override
	public void onDestroy() {
		mImageView.destroy();
		super.onDestroy();
	}

}
