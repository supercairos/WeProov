package com.weproov.app.logic.controllers;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import com.weproov.app.MyApplication;
import com.weproov.app.logic.providers.BusProvider;
import com.weproov.app.models.User;
import com.weproov.app.models.events.LoginSuccessEvent;
import com.weproov.app.models.events.NetworkErrorEvent;
import com.weproov.app.models.events.RegisterSuccessEvent;
import com.weproov.app.models.exceptions.NetworkException;
import com.weproov.app.models.wrappers.ParseFile;
import com.weproov.app.models.wrappers.ParseFileResponse;
import com.weproov.app.models.wrappers.ParseRegisterResponse;
import com.weproov.app.utils.AccountUtils;
import com.weproov.app.utils.Dog;
import com.weproov.app.utils.connections.TypedUri;
import com.weproov.app.utils.constants.AccountConstants;
import com.weproov.app.utils.constants.AuthenticatorConstants;
import retrofit.RetrofitError;

public final class UsersTask {

	private static final User.IUserService SERVICE = User.getService();
	private static final BusProvider.MainThreadBus BUS = BusProvider.getInstance();

	public static void login(final String email, final String password) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					User user = SERVICE.login(email, password);
					user.password = password;
					Dog.d("User found : %s", user.toString());
					save(user);
					BUS.post(new LoginSuccessEvent());
				} catch (NetworkException | RetrofitError error) {
					Dog.e(error, "Got an error while login :(");
					BUS.post(new NetworkErrorEvent(error));
				}
			}
		}).start();
	}

	public static void register(final User user) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					if (user.picture != null) {
						ContentResolver contentResolver = MyApplication.getAppContext().getContentResolver();
						TypedUri file = new TypedUri(contentResolver, user.picture);
						ParseFileResponse server = SERVICE.upload(file.fileName(), file);
						user.parsePictureFile = new ParseFile(server.name, server.url);
					}

					ParseRegisterResponse server = SERVICE.register(user);
					user.token = server.token;
					Dog.d("User found : %s", server.toString());
					save(user);
					BUS.post(new RegisterSuccessEvent());
				} catch (NetworkException | RetrofitError error) {
					Dog.e(error, "Got an error while registering :(");
					BUS.post(new NetworkErrorEvent(error));
				}
			}
		}).start();
	}

	private void copyPictureToCache(Uri picture) {

	}

	@SuppressWarnings("deprecation")
	public static void save(User user) {
		AccountManager accountManager = AccountManager.get(MyApplication.getAppContext());
		Account[] accounts = accountManager.getAccountsByType(AuthenticatorConstants.ACCOUNT_TYPE);
		for (Account account : accounts) {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
				accountManager.removeAccountExplicitly(account);
			} else {
				accountManager.removeAccount(account, null, null);
			}
		}

		Account myAccount = new Account(user.email, AuthenticatorConstants.ACCOUNT_TYPE);

		Bundle data = new Bundle();
		data.putString(AccountConstants.KEY_FIRST_NAME, user.firstname);
		data.putString(AccountConstants.KEY_LAST_NAME, user.lastname);
		data.putString(AccountConstants.KEY_PROFILE_PICTURE, user.parsePictureFile != null ? user.parsePictureFile.url : null);

		accountManager.addAccountExplicitly(myAccount, user.password, data);
		accountManager.setAuthToken(myAccount, AuthenticatorConstants.AUTH_TOKEN_TYPE_FULL, user.token);

		AccountUtils.setSyncable(true);
	}
}
