package com.weproov.app.logic.controllers;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.util.Log;
import com.squareup.okhttp.Credentials;
import com.weproov.app.MyApplication;
import com.weproov.app.models.User;
import com.weproov.app.models.events.LoginErrorEvent;
import com.weproov.app.models.events.LoginSuccessEvent;
import com.weproov.app.models.events.RegisterErrorEvent;
import com.weproov.app.models.events.RegisterSuccessEvent;
import com.weproov.app.models.exceptions.LoginException;
import com.weproov.app.models.exceptions.NetworkException;
import com.weproov.app.models.providers.BusProvider;
import com.weproov.app.utils.constants.AccountConstants;
import com.weproov.app.utils.constants.AuthenticatorConstants;
import retrofit.RetrofitError;

public class UsersTask {

    private static final User.IUserService SERVICE = User.getService();
    private static final BusProvider.MainThreadBus BUS = BusProvider.getInstance();

    public static void login(final String email, final String password) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    User user = SERVICE.login(Credentials.basic(email, password));
                    Log.d("Test", "User found : " + user.toString());
                    save(user, password);
                    BUS.post(new LoginSuccessEvent());
                } catch (LoginException | RetrofitError error) {
                    Log.e("Test", "Got an error while login :(", error);
                    BUS.post(new LoginErrorEvent());
                }
            }
        }).start();
    }

    public static void register(final User user) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    User server = SERVICE.register(user);
                    Log.d("Test", "User found : " + server.toString());
                    save(server, user.password);
                    BUS.post(new RegisterSuccessEvent());
                } catch (NetworkException | RetrofitError error) {
                    Log.e("Test", "Got an error while registering :(", error);
                    BUS.post(new RegisterErrorEvent());
                }
            }
        }).start();
    }

    public static void save(User user, String password) {
        AccountManager accountManager = AccountManager.get(MyApplication.getAppContext());
        Account[] accounts = accountManager.getAccountsByType(AuthenticatorConstants.ACCOUNT_TYPE);
        for (Account account : accounts) {
            accountManager.removeAccount(account, null, null);
        }

        Account myAccount = new Account(user.email, AuthenticatorConstants.ACCOUNT_TYPE);
        accountManager.addAccountExplicitly(myAccount, password, null);
        accountManager.setAuthToken(myAccount, AuthenticatorConstants.AUTH_TOKEN_TYPE_FULL, user.token);
        accountManager.setUserData(myAccount, AccountConstants.KEY_FIRST_NAME, user.firstname);
        accountManager.setUserData(myAccount, AccountConstants.KEY_LAST_NAME, user.lastname);
    }
}
