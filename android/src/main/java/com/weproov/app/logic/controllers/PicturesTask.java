package com.weproov.app.logic.controllers;

import android.content.ContentResolver;
import android.net.Uri;
import com.weproov.app.MyApplication;
import com.weproov.app.models.PictureItem;
import com.weproov.app.models.exceptions.NetworkException;
import com.weproov.app.models.wrappers.parse.*;
import com.weproov.app.utils.Dog;
import com.weproov.app.utils.connections.TypedUri;
import retrofit.RetrofitError;

import java.util.List;

public final class PicturesTask {

	private static final PictureItem.IPictureService SERVICE = PictureItem.getService();

	public static boolean upload(PictureItem item) throws NetworkException, RetrofitError {
		ContentResolver contentResolver = MyApplication.getAppContext().getContentResolver();
		TypedUri file = new TypedUri(contentResolver, item.path);
		ParseFileResponse server = SERVICE.upload(file.fileName(), file);
		if (server != null) {
			item.prepare(new ParseFile(server.name, server.url));
			ParseObjectResponse response = SERVICE.post(item);
			item.setServerId(response.id);
			item.save();

			return true;
		}

		return false;
	}

	public static List<PictureItem> downloadAll(String serverId) throws NetworkException, RetrofitError {
		ParseQueryWrapper<PictureItem> response = SERVICE.downloadAll(new ParseWeProovIdPointerQuery(new ParsePointer(serverId, "weproov")));
		if (response != null && response.results != null) {
			Dog.e("Result : " + response.results);
			for(PictureItem item: response.results) {
				if(item.parsePictureFile != null) {
					item.path = Uri.parse(item.parsePictureFile.url);
				}
			}
			return response.results;
		}

		return null;
	}
}
