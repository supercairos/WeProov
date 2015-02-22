package com.weproov.app.logic.services;

import android.accounts.*;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;
import com.squareup.okhttp.Credentials;
import com.weproov.app.models.User;
import com.weproov.app.models.exceptions.LoginException;
import com.weproov.app.ui.LandingActivity;
import com.weproov.app.ui.RegisterActivity;
import com.weproov.app.utils.constants.AuthenticatorConstants;

public class AuthenticatorService extends Service {

    private Authenticator mAuthenticator;

    @Override
    public void onCreate() {
        // Create a new authenticator object
        mAuthenticator = new Authenticator(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        if (intent.getAction().equals(android.accounts.AccountManager.ACTION_AUTHENTICATOR_INTENT)) {
            return mAuthenticator.getIBinder();
        }

        return null;
    }

    private static class Authenticator extends AbstractAccountAuthenticator {

        private final Context mContext;
        private String TAG = Authenticator.class.getSimpleName();
        private final AccountManager mAccountManager;

        public Authenticator(Context context) {
            super(context);
            mContext = context;
            mAccountManager = AccountManager.get(mContext);
        }

        @Override
        public Bundle addAccount(AccountAuthenticatorResponse response, String accountType, String authTokenType, String[] requiredFeatures, Bundle options) {
            Log.d("IBetYa", TAG + "> addAccount");

            final Intent intent = new Intent(mContext, RegisterActivity.class);
            intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);

            final Bundle bundle = new Bundle();
            bundle.putParcelable(AccountManager.KEY_INTENT, intent);
            return bundle;
        }

        @Override
        public Bundle getAuthToken(AccountAuthenticatorResponse response, Account account, String authTokenType, Bundle options) throws NetworkErrorException {
            Log.v("IBetYa", TAG + "> getAuthToken (" + authTokenType + ")");

            // If the caller requested an authToken type we don't support, then
            // return an error
            if (!authTokenType.equals(AuthenticatorConstants.AUTH_TOKEN_TYPE_FULL)) {
                final Bundle result = new Bundle();
                result.putString(AccountManager.KEY_ERROR_MESSAGE, "invalid authTokenType");
                return result;
            }

            // Extract the username and password from the Account Manager
            final String password = mAccountManager.getPassword(account);

            // Lets give another try to authenticate the user
            if (password != null) {
                Log.d("IBetYa", TAG + "> re-authenticating with the existing password");
                try {
                    User user = User.getService().login(Credentials.basic(account.name, password));
                    if (!TextUtils.isEmpty(user.token)) {
                        final Bundle result = new Bundle();
                        result.putString(AccountManager.KEY_ACCOUNT_NAME, account.name);
                        result.putString(AccountManager.KEY_ACCOUNT_TYPE, account.type);
                        result.putString(AccountManager.KEY_AUTHTOKEN, user.token);
                        return result;
                    }
                } catch (LoginException e) {
                    // Do nothing
                }
            }

            // If we get here, then we couldn't access the user's password - so we
            // need to ask the user to re-login in our app;
            final Intent intent = new Intent(mContext, LandingActivity.class);
            intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);
            intent.putExtra(AuthenticatorConstants.KEY_ACCOUNT_AUTHENTICATOR_FAILED, true);
            // TODO: Show toast to tell user why he needs to re-login

            final Bundle bundle = new Bundle();
            bundle.putParcelable(AccountManager.KEY_INTENT, intent);
            return bundle;
        }

        @Override
        public String getAuthTokenLabel(String authTokenType) {
            return null;
        }

        @Override
        public Bundle hasFeatures(AccountAuthenticatorResponse response,
                                  Account account, String[] features) {
            final Bundle result = new Bundle();
            result.putBoolean(AccountManager.KEY_BOOLEAN_RESULT, false);
            return result;
        }

        @Override
        public Bundle editProperties(AccountAuthenticatorResponse response, String accountType) {
            return null;
        }

        @Override
        public Bundle confirmCredentials(AccountAuthenticatorResponse response, Account account, Bundle options) {
            return null;
        }

        @Override
        public Bundle updateCredentials(AccountAuthenticatorResponse response, Account account, String authTokenType, Bundle options) {
            return null;
        }
    }
}
