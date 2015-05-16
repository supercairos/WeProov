package com.weproov.app.ui.views;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.*;
import android.os.Build;
import com.weproov.app.ui.glrender.BasicTexture;
import com.weproov.app.ui.glrender.BitmapTexture;
import com.weproov.app.utils.BitmapUtils;
import com.weproov.app.utils.Dog;

import java.io.IOException;

@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
public class BitmapRegionTileSource implements TiledImageRenderer.TileSource {
	private static final String TAG = "BitmapRegionTileSource";
	private static final boolean REUSE_BITMAP = Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN;
	private static final int GL_SIZE_LIMIT = 2048;
	// This must be no larger than half the size of the GL_SIZE_LIMIT
	// due to decodePreview being allowed to be up to 2x the size of the target
	private static final int MAX_PREVIEW_SIZE = 1024;
	BitmapRegionDecoder mDecoder;
	int mWidth;
	int mHeight;
	int mTileSize;
	private BasicTexture mPreview;
	private final int mRotation;
	// For use only by getTile
	private Rect mWantRegion = new Rect();
	private Rect mOverlapRegion = new Rect();
	private BitmapFactory.Options mOptions;
	private Canvas mCanvas;

	public BitmapRegionTileSource(Context context, String path, int previewSize, int rotation) {
		mTileSize = TiledImageRenderer.suggestedTileSize(context);
		mRotation = rotation;
		try {
			mDecoder = BitmapRegionDecoder.newInstance(path, true);
			mWidth = mDecoder.getWidth();
			mHeight = mDecoder.getHeight();
		} catch (IOException e) {
			Dog.w( "ctor failed", e);
		}
		mOptions = new BitmapFactory.Options();
		mOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;
		mOptions.inPreferQualityOverSpeed = true;
		mOptions.inTempStorage = new byte[16 * 1024];
		if (previewSize != 0) {
			previewSize = Math.min(previewSize, MAX_PREVIEW_SIZE);
			// Although this is the same size as the Bitmap that is likely already
			// loaded, the lifecycle is different and interactions are on a different
			// thread. Thus to simplify, this source will decode its own bitmap.
			Bitmap preview = decodePreview(path, previewSize);
			if (preview.getWidth() <= GL_SIZE_LIMIT && preview.getHeight() <= GL_SIZE_LIMIT) {
				mPreview = new BitmapTexture(preview);
			} else {
				Dog.w(String.format(
						"Failed to create preview of apropriate size! "
								+ " in: %dx%d, out: %dx%d",
						mWidth, mHeight,
						preview.getWidth(), preview.getHeight()));
			}
		}
	}

	@Override
	public int getTileSize() {
		return mTileSize;
	}

	@Override
	public int getImageWidth() {
		return mWidth;
	}

	@Override
	public int getImageHeight() {
		return mHeight;
	}

	@Override
	public BasicTexture getPreview() {
		return mPreview;
	}

	@Override
	public int getRotation() {
		return mRotation;
	}

	@Override
	public Bitmap getTile(int level, int x, int y, Bitmap bitmap) {
		int tileSize = getTileSize();
		if (!REUSE_BITMAP) {
			return getTileWithoutReusingBitmap(level, x, y, tileSize);
		}
		int t = tileSize << level;
		mWantRegion.set(x, y, x + t, y + t);
		if (bitmap == null) {
			bitmap = Bitmap.createBitmap(tileSize, tileSize, Bitmap.Config.ARGB_8888);
		}
		mOptions.inSampleSize = (1 << level);
		mOptions.inBitmap = bitmap;
		try {
			bitmap = mDecoder.decodeRegion(mWantRegion, mOptions);
		} finally {
			if (mOptions.inBitmap != bitmap && mOptions.inBitmap != null) {
				mOptions.inBitmap = null;
			}
		}
		if (bitmap == null) {
			Dog.w( "fail in decoding region");
		}
		return bitmap;
	}

	private Bitmap getTileWithoutReusingBitmap(
			int level, int x, int y, int tileSize) {
		int t = tileSize << level;
		mWantRegion.set(x, y, x + t, y + t);
		mOverlapRegion.set(0, 0, mWidth, mHeight);
		mOptions.inSampleSize = (1 << level);
		Bitmap bitmap = mDecoder.decodeRegion(mOverlapRegion, mOptions);
		if (bitmap == null) {
			Dog.w( "fail in decoding region");
		}
		if (mWantRegion.equals(mOverlapRegion)) {
			return bitmap;
		}
		Bitmap result = Bitmap.createBitmap(tileSize, tileSize, Bitmap.Config.ARGB_8888);
		if (mCanvas == null) {
			mCanvas = new Canvas();
		}
		mCanvas.setBitmap(result);
		mCanvas.drawBitmap(bitmap,
				(mOverlapRegion.left - mWantRegion.left) >> level,
				(mOverlapRegion.top - mWantRegion.top) >> level, null);
		mCanvas.setBitmap(null);
		return result;
	}

	/**
	 * Note that the returned bitmap may have a long edge that's longer
	 * than the targetSize, but it will always be less than 2x the targetSize
	 */
	private Bitmap decodePreview(String file, int targetSize) {
		float scale = (float) targetSize / Math.max(mWidth, mHeight);
		mOptions.inSampleSize = BitmapUtils.computeSampleSizeLarger(scale);
		mOptions.inJustDecodeBounds = false;
		Bitmap result = BitmapFactory.decodeFile(file, mOptions);
		if (result == null) {
			return null;
		}
		// We need to resize down if the decoder does not support inSampleSize
		// or didn't support the specified inSampleSize (some decoders only do powers of 2)
		scale = (float) targetSize / (float) (Math.max(result.getWidth(), result.getHeight()));
		if (scale <= 0.5) {
			result = resizeBitmapByScale(result, scale, true);
		}

		return ensureGLCompatibleBitmap(result);
	}

	private static Bitmap ensureGLCompatibleBitmap(Bitmap bitmap) {
		if (bitmap == null || bitmap.getConfig() != null) {
			return bitmap;
		}
		Bitmap newBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, false);
		bitmap.recycle();
		return newBitmap;
	}

	public static Bitmap resizeBitmapByScale(Bitmap bitmap, float scale, boolean recycle) {
		int width = Math.round(bitmap.getWidth() * scale);
		int height = Math.round(bitmap.getHeight() * scale);
		if (width == bitmap.getWidth()
				&& height == bitmap.getHeight()) return bitmap;
		Bitmap target = Bitmap.createBitmap(width, height, getConfig(bitmap));
		Canvas canvas = new Canvas(target);
		canvas.scale(scale, scale);
		Paint paint = new Paint(Paint.FILTER_BITMAP_FLAG | Paint.DITHER_FLAG);
		canvas.drawBitmap(bitmap, 0, 0, paint);
		if (recycle) bitmap.recycle();
		return target;
	}

	private static Bitmap.Config getConfig(Bitmap bitmap) {
		Bitmap.Config config = bitmap.getConfig();
		if (config == null) {
			config = Bitmap.Config.ARGB_8888;
		}
		return config;
	}
}
