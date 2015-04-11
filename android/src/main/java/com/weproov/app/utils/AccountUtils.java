package com.weproov.app.utils;


import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.content.ContentResolver;
import android.os.Bundle;
import android.util.Log;
import com.weproov.app.MyApplication;
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
			Log.e("Test", "Error " + e, e);
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

	public static void startSync() {
		Log.d("Test", "Start sync");
		/*
		 * Signal the framework to run your sync adapter. Assume that
		 * app initialization has already created the account.
		 */
		Account account = getAccount();
		if (account != null) {
			ContentResolver.requestSync(account, AuthenticatorConstants.ACCOUNT_TYPE, new Bundle());
		} else {
			Log.e("Test", "Account was null while starting sync...");
		}
	}
}
