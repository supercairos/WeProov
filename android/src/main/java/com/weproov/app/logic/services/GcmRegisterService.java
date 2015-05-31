package com.weproov.app.logic.services;

import android.app.IntentService;
import android.content.Intent;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.weproov.app.models.User;
import com.weproov.app.models.exceptions.NetworkException;
import com.weproov.app.models.wrappers.parse.ParseGcmResponse;
import com.weproov.app.utils.Dog;
import com.weproov.app.utils.PlayServicesUtils;
import com.weproov.app.utils.constants.GcmConstants;
import retrofit.RetrofitError;

import java.io.IOException;

public class GcmRegisterService extends IntentService {

	private GoogleCloudMessaging mGcm;

	/**
	 * Creates an IntentService.  Invoked by your subclass's constructor.
	 * Used to name the worker thread, important only for debugging.
	 */
	public GcmRegisterService() {
		super("GcmRegisterService");
	}

	@Override
	public void onCreate() {
		super.onCreate();
		mGcm = GoogleCloudMessaging.getInstance(this);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		try {
			String regid = mGcm.register(GcmConstants.SENDER_ID);
			Dog.d("Device registered, registration ID=%s", regid);

			// You should send the registration ID to your server over HTTP, so it
			// can use GCM/HTTP or CCS to send messages to your app.
			sendRegistrationIdToBackend(regid);

			// Persist the regID - no need to register again.
			PlayServicesUtils.storeRegistrationId(this, regid);
		} catch (IOException ex) {
			Dog.d(ex, "Error : %s", ex.getMessage());
			// If there is an error, don't just keep trying to register.
			// Require the user to click a button again, or perform
			// exponential back-off.
		}
	}

	private void sendRegistrationIdToBackend(String regid) {
		// TODO : Implement this method;

		Dog.d("Got token : %s", regid);
		try {
			User.getService().registerGcm(new ParseGcmResponse(regid));
		} catch (NetworkException | RetrofitError error) {
			Dog.e(error, "Error !");
		}
	}
}
