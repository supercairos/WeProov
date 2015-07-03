package com.weproov.app.logic.services.gcm;

import android.accounts.AccountManager;
import android.app.IntentService;
import android.content.Intent;
import com.google.android.gms.gcm.GcmPubSub;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.weproov.app.MyApplication;
import com.weproov.app.models.User;
import com.weproov.app.models.exceptions.NetworkException;
import com.weproov.app.models.wrappers.parse.ParseGcmResponse;
import com.weproov.app.utils.AccountUtils;
import com.weproov.app.utils.Dog;
import com.weproov.app.utils.PlayServicesUtils;
import com.weproov.app.utils.constants.AccountConstants;
import com.weproov.app.utils.constants.GcmConstants;
import retrofit.RetrofitError;

import java.io.IOException;

public class RegistrationIntentService extends IntentService {

	private static final String TAG = "RegIntentService";
	private static final String[] TOPICS = {
			"global"
	};

	public RegistrationIntentService() {
		super(TAG);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		try {
			// In the (unlikely) event that multiple refresh operations occur simultaneously,
			// ensure that they are processed sequentially.
			synchronized (TAG) {
				// [START register_for_gcm]
				// Initially this call goes out to the network to retrieve the token, subsequent calls
				// are local.
				// [START get_token]
				InstanceID instanceID = InstanceID.getInstance(this);
				String token = instanceID.getToken(GcmConstants.SENDER_ID, GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
				// [END get_token]
				Dog.i("GCM Registration Token: " + token);

				// TODO: Implement this method to send any registration to your app's servers.
				sendRegistrationToServer(token);

				// Subscribe to topic channels
				subscribeTopics(token);

				// You should store a boolean that indicates whether the generated token has been
				// sent to your server. If the boolean is false, send the token to your server,
				// otherwise your server should have already received the token.
				PlayServicesUtils.storeRegistrationId(this, token);
				// [END register_for_gcm]
			}
		} catch (Exception e) {
			Dog.d("Failed to complete token refresh", e);
			// If an exception happens while fetching the new token or updating our registration data
			// on a third-party server, this ensures that we'll attempt the update at a later time.
			PlayServicesUtils.forgetRegistrationId(this);
		}
	}

	/**
	 * Persist registration to third-party servers.
	 * <p/>
	 * Modify this method to associate the user's GCM registration token with any server-side account
	 * maintained by your application.
	 *
	 * @param regid The new token.
	 */
	private void sendRegistrationToServer(String regid) {
		Dog.d("Got token : %s", regid);
		try {
			String userId = AccountManager.get(MyApplication.getAppContext()).getUserData(AccountUtils.getAccount(), AccountConstants.KEY_SERVER_ID);
			User.getService().registerGcm(new ParseGcmResponse(regid, userId));
		} catch (NetworkException | RetrofitError error) {
			Dog.e(error, "Error !");
		}
	}

	/**
	 * Subscribe to any GCM topics of interest, as defined by the TOPICS constant.
	 *
	 * @param token GCM token
	 * @throws IOException if unable to reach the GCM PubSub service
	 */
	// [START subscribe_topics]
	private void subscribeTopics(String token) throws IOException {
		for (String topic : TOPICS) {
			GcmPubSub pubSub = GcmPubSub.getInstance(this);
			pubSub.subscribe(token, "/topics/" + topic, null);
		}
	}
	// [END subscribe_topics]
}