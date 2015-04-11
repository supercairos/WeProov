package com.weproov.app.logic.controllers;

import android.util.Log;
import android.webkit.MimeTypeMap;
import com.weproov.app.models.PictureItem;
import com.weproov.app.models.events.LoginErrorEvent;
import com.weproov.app.models.events.LoginSuccessEvent;
import com.weproov.app.models.exceptions.LoginException;
import com.weproov.app.logic.providers.BusProvider;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedFile;

import java.io.File;

public final class PicturesTask {

	private static final PictureItem.IPictureService SERVICE = PictureItem.getService();
	private static final BusProvider.MainThreadBus BUS = BusProvider.getInstance();

	public static void upload(PictureItem item) {
		try {
			File file = new File(item.path.getPath());
			Response response = SERVICE.upload(item, new TypedFile(getMimeType(file), file));
			Log.d("Test", "Response found : " + response);

			BUS.post(new LoginSuccessEvent());
		} catch (LoginException | RetrofitError error) {
			Log.e("Test", "Got an error while login :(", error);
			BUS.post(new LoginErrorEvent());
		}
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
