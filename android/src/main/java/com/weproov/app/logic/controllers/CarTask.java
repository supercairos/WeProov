package com.weproov.app.logic.controllers;

import com.weproov.app.models.CarInfo;
import com.weproov.app.models.exceptions.NetworkException;
import com.weproov.app.models.wrappers.parse.ParseObjectResponse;

import retrofit.RetrofitError;

public final class CarTask {

    private static final CarInfo.ICarService SERVICE = CarInfo.getService();

    public static void upload(CarInfo item) throws NetworkException, RetrofitError {
        item.prepare();
        ParseObjectResponse response = SERVICE.upload(item);
        item.setServerId(response.id);
        item.save();
    }
}
