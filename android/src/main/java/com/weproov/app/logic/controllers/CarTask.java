package com.weproov.app.logic.controllers;

import com.weproov.app.MyApplication;
import com.weproov.app.R;
import com.weproov.app.models.CarInfo;
import com.weproov.app.models.PictureItem;
import com.weproov.app.models.WeProov;
import com.weproov.app.models.exceptions.NetworkException;
import com.weproov.app.models.wrappers.parse.*;
import retrofit.RetrofitError;

public final class CarTask {

	private static final CarInfo.ICarService SERVICE = CarInfo.getService();

	public static void upload(WeProov parent, CarInfo item) throws NetworkException, RetrofitError {
		item.prepare();
		if (item.vehicleDocumentation != null) {
			PicturesTask.upload(new PictureItem(
					parent,
					item.vehicleDocumentation,
					PictureItem.TYPE_JUSTIF,
					item.vehicleDocumentation.getLastPathSegment(),
					MyApplication.getAppContext().getString(R.string.vehicle_documentation),
					0
			));
		}
		ParseObjectResponse response = SERVICE.upload(item);
		item.setServerId(response.id);
		item.save();
	}

	public static CarInfo download(String objectId) throws NetworkException, RetrofitError {
		ParseQueryWrapper<CarInfo> response = SERVICE.download(new ParseObjectIdQuery(objectId));
		if (response != null && response.results != null && response.results.size() > 0) {
			return response.results.get(0);
		}

		return null;
	}
}
