package com.weproov.app.utils;

import android.webkit.MimeTypeMap;

import java.io.File;

public final class MimeUtils {

	public static String getMimeType(File file) {
		return getMimeType(file.getAbsolutePath());
	}

	// url = file path or whatever suitable URL you want.
	public static String getMimeType(String url) {
		String type = null;
		String extension = MimeTypeMap.getFileExtensionFromUrl(url);
		if (extension != null) {
			MimeTypeMap mime = MimeTypeMap.getSingleton();
			type = mime.getMimeTypeFromExtension(extension);
		}
		return type;
	}
}
