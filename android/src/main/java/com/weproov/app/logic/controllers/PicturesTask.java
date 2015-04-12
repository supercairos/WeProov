package com.weproov.app.logic.controllers;

import android.util.Log;
import android.webkit.MimeTypeMap;
import com.weproov.app.logic.providers.BusProvider;
import com.weproov.app.models.PictureItem;
import com.weproov.app.models.exceptions.LoginException;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedFile;

import java.io.File;

public final class PicturesTask {

	private static final PictureItem.IPictureService SERVICE = PictureItem.getService();
	private static final BusProvider.MainThreadBus BUS = BusProvider.getInstance();

	public static boolean upload(PictureItem item) throws LoginException, RetrofitError{
			File file = new File(item.path.getPath());
			Response response = SERVICE.upload(item, new TypedFile(getMimeType(file), file));
			Log.d("Test", "Response found : " + response);
			return response.getStatus() > 199 && response.getStatus() < 300;

	}

	private static String getMimeType(File file) {
		return getMimeType(file.getAbsolutePath());
	}

	// url = file path or whatever suitable URL you want.
	private static String getMimeType(String url) {
		String type = null;
		String extension = MimeTypeMap.getFileExtensionFromUrl(url);
		if (extension != null) {
			MimeTypeMap mime = MimeTypeMap.getSingleton();
			type = mime.getMimeTypeFromExtension(extension);
		}
		return type;
	}
}
