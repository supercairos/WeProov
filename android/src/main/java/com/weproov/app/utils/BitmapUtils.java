package com.weproov.app.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.*;
import android.os.Environment;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.RenderScript.Priority;
import android.renderscript.ScriptIntrinsicBlur;
import android.util.Log;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public final class BitmapUtils {

	public static Bitmap blur(Context context, int radius, Bitmap bitmapOriginal) {
		RenderScript rs = RenderScript.create(context);
		rs.setPriority(Priority.NORMAL);

		final Allocation input = Allocation.createFromBitmap(rs, bitmapOriginal);
		final Allocation output = Allocation.createTyped(rs, input.getType());
		final ScriptIntrinsicBlur script = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
		script.setRadius(radius);
		script.setInput(input);
		script.forEach(output);

		// Copy blur to out
		output.copyTo(bitmapOriginal);
		rs.finish();
		rs.destroy();
		rs = null;

		return bitmapOriginal;
	}

	public static Bitmap scaleCenterCrop(Bitmap source, int newHeight, int newWidth) {
		int sourceWidth = source.getWidth();
		int sourceHeight = source.getHeight();

		// Compute the scaling factors to fit the new height and width, respectively.
		// To cover the final image, the final scaling will be the bigger
		// of these two.
		float xScale = (float) newWidth / sourceWidth;
		float yScale = (float) newHeight / sourceHeight;
		float scale = Math.max(xScale, yScale);

		// Now get the size of the source bitmap when scaled
		float scaledWidth = scale * sourceWidth;
		float scaledHeight = scale * sourceHeight;

		// Let's find out the upper left coordinates if the scaled bitmap
		// should be centered in the new size give by the parameters
		float left = (newWidth - scaledWidth) / 2;
		float top = (newHeight - scaledHeight) / 2;

		// The target rectangle for the new, scaled version of the source bitmap will now
		// be
		RectF targetRect = new RectF(left, top, left + scaledWidth, top + scaledHeight);

		// Finally, we create a new bitmap of the specified size and draw our new,
		// scaled bitmap onto it.
		Bitmap dest = Bitmap.createBitmap(newWidth, newHeight, source.getConfig());
		Canvas canvas = new Canvas(dest);
		canvas.drawBitmap(source, null, targetRect, null);

		source.recycle();
		source = null;

		return dest;
	}

	public static void writeToExt(Bitmap bmp, String name) {
		String file_path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/tests";
		try {
			File dir = new File(file_path);
			if (!dir.exists()) {
				dir.mkdirs();
			}

			File file = new File(dir, name + "_" + System.currentTimeMillis() + ".png");
			FileOutputStream fOut;

			fOut = new FileOutputStream(file);

			bmp.compress(Bitmap.CompressFormat.PNG, 100, fOut);
			fOut.flush();
			fOut.close();
		} catch (IOException e) {
			Log.d("Test", "Ex", e);
		}

	}

	// Make sure input images are very small!
	public static float calculateDarkness(Bitmap bitmap) {
		if (bitmap == null) {
			return 0;
		}
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		int totalLum = 0;
		int n = 0;
		int x, y, color;
		for (y = 0; y < height; y++) {
			for (x = 0; x < width; x++) {
				++n;
				color = bitmap.getPixel(x, y);
				totalLum += (0.21f * Color.red(color) + 0.71f * Color.green(color) + 0.07f * Color.blue(color));
			}
		}
		return (totalLum / n) / 256f;
	}

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

	public static String getScaleTypeString(boolean isFitted) {
		if (isFitted) {
			return "fit";
		} else {
			return "crop";
		}
	}

	public static String getOrientationString(int height, int width) {
		if (height > width) {
			return "port";
		} else {
			return "land";
		}
	}

	public static Bitmap decodeSampledBitmapFromUrl(String src, int reqWidth, int reqHeight) {
		HttpURLConnection connection = null;
		InputStream stream = null;
		try {
			URL url = new URL(src);
			connection = (HttpURLConnection) url.openConnection();
			connection.setDoInput(true);
			connection.connect();
			stream = connection.getInputStream();
			return decodeSampledBitmapFromInputStream(stream, reqWidth, reqHeight);
		} catch (IOException e) {
			return null;
		} finally {
			try {
				if (stream != null) {
					stream.close();
				}

				if (connection != null) {
					connection.disconnect();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	// This computes a sample size which makes the longer side at least
	// minSideLength long. If that's not possible, return 1.
	public static int computeSampleSizeLarger(int w, int h,
											  int minSideLength) {
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
}
