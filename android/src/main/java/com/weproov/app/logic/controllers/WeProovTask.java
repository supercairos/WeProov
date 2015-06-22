package com.weproov.app.logic.controllers;

import com.weproov.app.models.CarInfo;
import com.weproov.app.models.WeProov;
import com.weproov.app.models.exceptions.NetworkException;
import com.weproov.app.models.wrappers.parse.ParseObjectResponse;

import retrofit.RetrofitError;

public class WeProovTask {

    private static final WeProov.IWeProovService SERVICE = WeProov.getService();

    public static void upload(WeProov item) throws NetworkException, RetrofitError {
        item.prepare();
        ParseObjectResponse response = SERVICE.upload(item);
        item.setServerId(response.id);
        item.save();
    }

}
