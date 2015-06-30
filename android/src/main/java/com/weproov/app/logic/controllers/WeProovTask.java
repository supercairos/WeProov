package com.weproov.app.logic.controllers;

import android.accounts.AccountManager;
import com.weproov.app.MyApplication;
import com.weproov.app.R;
import com.weproov.app.models.ClientInfo;
import com.weproov.app.models.PictureItem;
import com.weproov.app.models.WeProov;
import com.weproov.app.models.exceptions.NetworkException;
import com.weproov.app.models.wrappers.parse.ParseCreatedByPointerQuery;
import com.weproov.app.models.wrappers.parse.ParseObjectResponse;
import com.weproov.app.models.wrappers.parse.ParsePointer;
import com.weproov.app.models.wrappers.parse.ParseQueryWrapper;
import com.weproov.app.utils.AccountUtils;
import com.weproov.app.utils.Dog;
import com.weproov.app.utils.constants.AccountConstants;
import retrofit.RetrofitError;

import java.util.ArrayList;
import java.util.List;

public class WeProovTask {

	private static final WeProov.IWeProovService SERVICE = WeProov.getService();

	public static void upload(WeProov item) throws NetworkException, RetrofitError {
		item.prepare();
		if (item.client != null) {
			ClientInfo info = item.client;
			if (info.driving_licence != null) {
				PicturesTask.upload(new PictureItem(
						item,
						info.driving_licence,
						PictureItem.TYPE_JUSTIF,
						info.driving_licence.getLastPathSegment(),
						MyApplication.getAppContext().getString(R.string.driving_licence),
						0
				));
			}
			if (info.id_card != null) {
				PicturesTask.upload(new PictureItem(
						item,
						info.id_card,
						PictureItem.TYPE_JUSTIF,
						info.id_card.getLastPathSegment(),
						MyApplication.getAppContext().getString(R.string.identity_card),
						0
				));
			}
		}

		if (item.clientSignature != null) {
			PicturesTask.upload(new PictureItem(
					item,
					item.clientSignature,
					PictureItem.TYPE_JUSTIF,
					item.clientSignature.getLastPathSegment(),
					MyApplication.getAppContext().getString(R.string.client_signature),
					1000
			));
		} else {
			Dog.e("Client signature was null");
		}

		if (item.renterSignature != null) {
			PicturesTask.upload(new PictureItem(
					item,
					item.renterSignature,
					PictureItem.TYPE_JUSTIF,
					item.renterSignature.getLastPathSegment(),
					MyApplication.getAppContext().getString(R.string.renter_signature),
					1001
			));
		} else {
			Dog.e("Renter signature was null");
		}

		ParseObjectResponse response = SERVICE.upload(item);
		item.setServerId(response.id);
		item.save();
	}

	public static List<WeProov> downloadAll() throws NetworkException, RetrofitError {
		String userId = AccountManager.get(MyApplication.getAppContext()).getUserData(AccountUtils.getAccount(), AccountConstants.KEY_SERVER_ID);
		ParseQueryWrapper<WeProov> query = SERVICE.download(new ParseCreatedByPointerQuery(new ParsePointer(userId, "_User")));
		if (query != null) {
			for (WeProov result : query.results) {
				result.car = CarTask.download(result.parseCarPointer.objectId);
				result.addPictures(PicturesTask.downloadAll(result.parseProovCodePointer.objectId));
				result.proovCode = result.parseProovCodePointer.objectId;
			}

			return query.results;
		}

		return new ArrayList<>(0);
	}
}
