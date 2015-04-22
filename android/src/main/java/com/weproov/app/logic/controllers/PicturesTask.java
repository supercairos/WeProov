package com.weproov.app.logic.controllers;

import android.util.Log;
import com.weproov.app.models.PictureItem;
import com.weproov.app.models.exceptions.NetworkException;
import com.weproov.app.utils.MimeUtils;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedFile;

import java.io.File;

public final class PicturesTask {

	private static final PictureItem.IPictureService SERVICE = PictureItem.getService();

	public static boolean upload(PictureItem item) throws NetworkException, RetrofitError{
			File file = new File(item.path.getPath());
			Response response = SERVICE.upload(item, new TypedFile(MimeUtils.getMimeType(file), file));
			Log.d("Test", "Response found : " + response);
			return response.getStatus() > 199 && response.getStatus() < 300;

	}
}
