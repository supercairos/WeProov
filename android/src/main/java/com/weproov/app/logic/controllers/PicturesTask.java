package com.weproov.app.logic.controllers;

import android.content.ContentResolver;

import com.weproov.app.MyApplication;
import com.weproov.app.models.PictureItem;
import com.weproov.app.models.exceptions.NetworkException;
import com.weproov.app.models.wrappers.parse.ParseFile;
import com.weproov.app.models.wrappers.parse.ParseFileResponse;
import com.weproov.app.models.wrappers.parse.ParseObjectResponse;
import com.weproov.app.utils.Dog;
import com.weproov.app.utils.MimeUtils;
import com.weproov.app.utils.connections.TypedUri;

import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedFile;

import java.io.File;

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
}
