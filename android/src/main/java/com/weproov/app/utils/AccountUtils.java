package com.weproov.app.utils;


import android.accounts.*;
import android.app.Activity;
import android.content.ContentResolver;
import android.os.Build;
import android.os.Bundle;
import com.weproov.app.MyApplication;
import com.weproov.app.utils.constants.AccountConstants;
import com.weproov.app.utils.constants.AuthenticatorConstants;

import java.io.IOException;

public final class AccountUtils {

	public static Account getAccount() {
		AccountManager accountManager = AccountManager.get(MyApplication.getAppContext());
		Account[] accounts = accountManager.getAccountsByType(AuthenticatorConstants.ACCOUNT_TYPE);
		if (accounts.length > 0) {
			return accounts[0];
		} else {
			return null;
		}
	}

	public static String getDisplayName() {
		AccountManager accountManager = AccountManager.get(MyApplication.getAppContext());
		Account account = getAccount();
		if (account != null) {
			return accountManager.getUserData(account, AccountConstants.KEY_FIRST_NAME) + " " + accountManager.getUserData(account, AccountConstants.KEY_LAST_NAME);
		} else {
			return null;
		}
	}

	public static String getToken() throws IOException {
		try {
			AccountManager accountManager = AccountManager.get(MyApplication.getAppContext());
			Account[] accounts = accountManager.getAccountsByType(AuthenticatorConstants.ACCOUNT_TYPE);
			if (accounts.length > 0) {
				return accountManager.blockingGetAuthToken(accounts[0], AuthenticatorConstants.AUTH_TOKEN_TYPE_FULL, true);
			} else {
				return "";
			}
		} catch (AuthenticatorException | OperationCanceledException e) {
			Dog.e( e, "Error");
			return "";
		}
	}

	public static String peekToken() {
		AccountManager accountManager = AccountManager.get(MyApplication.getAppContext());
		Account[] accounts = accountManager.getAccountsByType(AuthenticatorConstants.ACCOUNT_TYPE);
		if (accounts.length > 0) {
			return accountManager.peekAuthToken(accounts[0], AuthenticatorConstants.AUTH_TOKEN_TYPE_FULL);
		} else {
			return "";
		}
	}

	public static void setSyncable(String provider, boolean enabled) {
		Dog.d( "Start setSyncable");
		Account account = getAccount();
		if (account != null) {
			ContentResolver.setIsSyncable(account, provider, enabled ? 1 : -1);
		} else {
			Dog.e( "Account was null while starting sync...");
		}
	}

	public static void startSync(String provider) {
		Dog.d( "Start sync");
		/*
		 * Signal the framework to run your sync adapter. Assume that
		 * app initialization has already created the account.
		 */
		Account account = getAccount();
		if (account != null) {
			Bundle bundle = new Bundle();
			bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
			bundle.putBoolean(ContentResolver.SYNC_EXTRAS_IGNORE_BACKOFF, true);
			bundle.putBoolean(ContentResolver.SYNC_EXTRAS_IGNORE_SETTINGS, true);
			bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);

			ContentResolver.requestSync(account, provider, bundle);
		} else {
			Dog.e( "Account was null while starting sync...");
		}
	}

	@SuppressWarnings("deprecation")
	public static void removeAccount(final Activity ctx, final AccountRemovedCallback callback) {
		Dog.d( "Loging out");
		AccountManager accountManager = AccountManager.get(ctx);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
			accountManager.removeAccount(AccountUtils.getAccount(), ctx, new AccountManagerCallback<Bundle>() {
				@Override
				public void run(AccountManagerFuture<Bundle> future) {
					try {
						Bundle bundle = future.getResult();
						if (bundle.getBoolean(AccountManager.KEY_BOOLEAN_RESULT, false)) {
							callback.onSuccess();
						}
					} catch (OperationCanceledException | IOException | AuthenticatorException e) {
						Dog.e(e, "Failed to delete the account... :(");
						callback.onFailure();
					}
				}
			}, null);
		} else {
			accountManager.removeAccount(AccountUtils.getAccount(), new AccountManagerCallback<Boolean>() {
				@Override
				public void run(AccountManagerFuture<Boolean> future) {
					try {
						if (future.getResult()) {
							callback.onSuccess();
						}
					} catch (OperationCanceledException | IOException | AuthenticatorException e) {
						Dog.e(e, "Failed to delete the account... :(");
						callback.onFailure();
					}
				}
			}, null);
		}
	}

	public interface AccountRemovedCallback {
		void onSuccess();

		void onFailure();
	}
}
