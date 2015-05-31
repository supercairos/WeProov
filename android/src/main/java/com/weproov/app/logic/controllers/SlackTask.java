package com.weproov.app.logic.controllers;

import android.accounts.Account;
import android.accounts.AccountManager;
import com.weproov.app.MyApplication;
import com.weproov.app.models.Feedback;
import com.weproov.app.models.exceptions.NetworkException;
import com.weproov.app.models.wrappers.slack.SlackMessage;
import com.weproov.app.utils.AccountUtils;
import com.weproov.app.utils.constants.AccountConstants;
import com.weproov.app.utils.constants.AuthenticatorConstants;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class SlackTask {

	private static final Feedback.IFeedbackService SERVICE = Feedback.getService();

	public static boolean send(Feedback item) throws NetworkException, RetrofitError {
		Account account = AccountUtils.getAccount();
		AccountManager accountManager = AccountManager.get(MyApplication.getAppContext());
		if (account != null) {
			String username = accountManager.getUserData(account, AccountConstants.KEY_FIRST_NAME) + " " + accountManager.getUserData(account, AccountConstants.KEY_LAST_NAME);
			String icon = accountManager.getUserData(account, AccountConstants.KEY_PROFILE_PICTURE);

			Response response = SERVICE.send(new SlackMessage(username, icon, item.title, item.message));
			return response.getStatus() > 199 && response.getStatus() < 300;
		}
		return false;
	}

	public static void save(final Feedback item) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				item.save();
				AccountUtils.startSync(AuthenticatorConstants.ACCOUNT_PROVIDER_DEBUG);
			}
		});
	}

}
