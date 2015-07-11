package com.weproov.app.utils;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.Nullable;

import java.io.File;
import java.io.FileDescriptor;
import java.io.InputStream;

public final class BitmapUtils {

	public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {

			final int halfHeight = height / 2;
			final int halfWidth = width / 2;

			// Calculate the largest inSampleSize value that is a power of 2 and keeps both
			// height and width larger than the requested height and width.
			while ((halfHeight / inSampleSize) > reqHeight && (halfWidth / inSampleSize) > reqWidth) {
				inSampleSize *= 2;
			}
		}

		return inSampleSize;
	}

	public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId, int reqWidth, int reqHeight) {

		// First decode with inJustDecodeBounds=true to check dimensions
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		options.inPurgeable = true;
		BitmapFactory.decodeResource(res, resId, options);

		if (options.outHeight > 0 && options.outWidth > 0) {
			// Calculate inSampleSize
			options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

			// Decode bitmap with inSampleSize set
			options.inJustDecodeBounds = false;
			return BitmapFactory.decodeResource(res, resId, options);
		} else {
			return null;
		}
	}

	public static Bitmap decodeSampledBitmapFromFile(File file, int reqWidth, int reqHeight) {

		// First decode with inJustDecodeBounds=true to check dimensions
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		options.inPurgeable = true;
		BitmapFactory.decodeFile(file.getAbsolutePath(), options);

		if (options.outHeight > 0 && options.outWidth > 0) {
			// Calculate inSampleSize
			options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

			// Decode bitmap with inSampleSize set
			options.inJustDecodeBounds = false;
			return BitmapFactory.decodeFile(file.getAbsolutePath(), options);
		} else {
			return null;
		}
	}

	public static Bitmap decodeSampledBitmapFromInputStream(InputStream stream, int reqWidth, int reqHeight) {

		// First decode with inJustDecodeBounds=true to check dimensions
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		options.inPurgeable = true;
		BitmapFactory.decodeStream(stream, null, options);

		if (options.outHeight > 0 && options.outWidth > 0) {
			// Calculate inSampleSize
			options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

			// Decode bitmap with inSampleSize set
			options.inJustDecodeBounds = false;
			return BitmapFactory.decodeStream(stream, null, options);
		} else {
			return null;
		}
	}

	public static Bitmap decodeSampledBitmapFromFileDescriptor(FileDescriptor stream, int reqWidth, int reqHeight) {

		// First decode with inJustDecodeBounds=true to check dimensions
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		options.inPurgeable = true;
		BitmapFactory.decodeFileDescriptor(stream, null, options);

		if (options.outHeight > 0 && options.outWidth > 0) {
			// Calculate inSampleSize
			options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

			// Decode bitmap with inSampleSize set
			options.inJustDecodeBounds = false;
			return BitmapFactory.decodeFileDescriptor(stream, null, options);
		} else {
			return null;
		}
	}

	// This computes a sample size which makes the longer side at least
	// minSideLength long. If that's not possible, return 1.
	public static int computeSampleSizeLarger(int w, int h, int minSideLength) {
		int initialSize = Math.max(w / minSideLength, h / minSideLength);
		if (initialSize <= 1) return 1;
		return initialSize <= 8
				? MathUtils.prevPowerOf2(initialSize)
				: initialSize / 8 * 8;
	}

	// Find the min x that 1 / x >= scale
	public static int computeSampleSizeLarger(float scale) {
		int initialSize = (int) Math.floor(1d / scale);
		if (initialSize <= 1) return 1;
		return initialSize <= 8
				? MathUtils.prevPowerOf2(initialSize)
				: initialSize / 8 * 8;
	}

	@Nullable
	public static Bitmap removeTransparent(@Nullable Bitmap source) {
		if(source == null) {
			return null;
		}

		int minX = source.getWidth();
		int minY = source.getHeight();
		int maxX = -1;
		int maxY = -1;
		for (int y = 0; y < source.getHeight(); y++) {
			for (int x = 0; x < source.getWidth(); x++) {
				int alpha = (source.getPixel(x, y) >> 24) & 255;
				if (alpha > 0) {
					if (x < minX)
						minX = x;
					if (x > maxX)
						maxX = x;
					if (y < minY)
						minY = y;
					if (y > maxY)
						maxY = y;
				}
			}
		}

		if ((maxX < minX) || (maxY < minY)) {
			return null; // Bitmap is entirely transparent
		}

		// crop bitmap to non-transparent area and return:
		return Bitmap.createBitmap(source, minX, minY, (maxX - minX) + 1, (maxY - minY) + 1);
	}
}
