package com.weproov.app.utils.connections;

import android.content.ContentResolver;
import android.net.Uri;
import android.text.TextUtils;
import android.webkit.MimeTypeMap;
import com.weproov.app.utils.Dog;
import retrofit.mime.TypedOutput;

import java.io.*;

public class TypedUri implements TypedOutput {

	private final Uri mUri;
	private final ContentResolver mContentResolver;

	/**
	 * Constructs a new typed file.
	 *
	 * @throws NullPointerException if file or mimeType is null
	 */
	public TypedUri(ContentResolver resolver, Uri uri) {
		if (resolver == null) {
			throw new NullPointerException("resolver");
		}
		if (uri == null) {
			throw new NullPointerException("uri");
		}

		mContentResolver = resolver;
		mUri = uri;
	}

	/**
	 * Original filename.
	 * <p/>
	 * Used only for multipart requests, may be null.
	 */
	@Override
	public String fileName() {
		return cleanFilename(mUri.getLastPathSegment());
	}

	/**
	 * Returns the mime type.
	 */
	@Override
	public String mimeType() {
		String mime = mContentResolver.getType(mUri);
		if (!TextUtils.isEmpty(mime)) {
			return mime;
		}

		String extension = MimeTypeMap.getFileExtensionFromUrl(mUri.toString());
		if (!TextUtils.isEmpty(extension)) {
			mime = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
		} else {
			mime = "text/plain";
		}

		Dog.d("Found mimetype : %s", mime);

		return mime;
	}

	/**
	 * Length in bytes. Returns {@code -1} if length is unknown.
	 */
	@Override
	public long length() {
		return -1;
	}

	/**
	 * Writes these bytes to the given output stream.
	 *
	 * @param out
	 */
	@Override
	public void writeTo(OutputStream out) throws IOException {
		byte[] buffer = new byte[1024]; // Adjust if you want
		InputStream input = new BufferedInputStream(getInputStream(mUri), 1024);

		int bytesRead;
		while ((bytesRead = input.read(buffer)) != -1) {
			out.write(buffer, 0, bytesRead);
		}
	}

	private InputStream getInputStream(Uri uri) throws FileNotFoundException {
		String scheme = uri.getScheme();
		if (ContentResolver.SCHEME_ANDROID_RESOURCE.equals(scheme) || ContentResolver.SCHEME_CONTENT.equals(scheme) || ContentResolver.SCHEME_FILE.equals(scheme)) {
			return mContentResolver.openInputStream(uri);
		} else {
			throw new UnsupportedOperationException();
		}
	}

	private static String cleanFilename(String name) {
		return name.replaceAll("[^a-zA-Z0-9._]", "_");
	}
}
