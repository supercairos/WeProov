package com.weproov.app.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.weproov.app.R;
import com.weproov.app.utils.Constants;
import com.weproov.app.utils.EmailValidator;
import com.weproov.app.utils.PasswordValidator;
import com.weproov.app.utils.PrefUtils;

import java.lang.ref.WeakReference;

public class RegisterActivity extends ActionBarActivity {

    @InjectView(R.id.edit_firstname)
    EditText mFirstName;
    @InjectView(R.id.edit_firstname_error)
    TextView mFirstNameError;

    @InjectView(R.id.edit_lastname)
    EditText mLastName;
    @InjectView(R.id.edit_lastname_error)
    TextView mLastNameError;

    @InjectView(R.id.edit_email)
    EditText mEmail;
    @InjectView(R.id.edit_email_error)
    TextView mEmailError;

    @InjectView(R.id.edit_password)
    EditText mPassword;
    @InjectView(R.id.edit_password_error)
    TextView mPasswordError;

    @InjectView(R.id.action_bar)
    Toolbar mActionBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.inject(this);
        setSupportActionBar(mActionBar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.button_back)
    public void onButtonBackClicked() {
        NavUtils.navigateUpFromSameTask(this);
    }

    @OnClick(R.id.button_register)
    public void onButtonRegisterClicked() {
        String firstname = mFirstName.getEditableText().toString();
        String lastname = mLastName.getEditableText().toString();
        String email = mEmail.getEditableText().toString();
        String password = mPassword.getEditableText().toString();

        boolean isEverythingOk = true;
        if (!EmailValidator.getInstance().validate(email)) {
            mEmailError.setVisibility(View.VISIBLE);
            mEmail.requestFocus();
            isEverythingOk = false;
        } else {
            mEmailError.setVisibility(View.INVISIBLE);
        }

        if (!PasswordValidator.isAcceptablePassword(password)) {
            mPasswordError.setVisibility(View.VISIBLE);
            mPassword.requestFocus();
            isEverythingOk = false;
        } else {
            mPasswordError.setVisibility(View.INVISIBLE);
        }

        if (TextUtils.isEmpty(lastname)) {
            mLastNameError.setVisibility(View.VISIBLE);
            mLastName.requestFocus();
            isEverythingOk = false;
        } else {
            mLastNameError.setVisibility(View.INVISIBLE);
        }

        if (TextUtils.isEmpty(firstname)) {
            mFirstNameError.setVisibility(View.VISIBLE);
            mFirstName.requestFocus();
            isEverythingOk = false;
        } else {
            mFirstNameError.setVisibility(View.INVISIBLE);
        }

        if (isEverythingOk) {
            // Register
            (new FakeRegistration(this, firstname, lastname, email, password)).execute();
        }

    }

    public static void login(Activity ctx, String displayName, String email){
        PrefUtils.putString(Constants.KEY_DISPLAY_NAME, displayName);
        PrefUtils.putString(Constants.KEY_EMAIL, email);
        Intent intent = new Intent(ctx, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        ctx.startActivity(intent);
        ctx.finish();
    }

    private static class FakeRegistration extends AsyncTask<Void, Void, Void> {

        WeakReference<Activity> mContext;
        ProgressDialog mDialog;

        private String mFirstname;
        private String mLastname;
        private String mEmail;
        private String mPassword;

        public FakeRegistration(Activity ctx, String firstname, String lastname, String email, String password) {
            mContext = new WeakReference<>(ctx);

            mFirstname = firstname;
            mLastname = lastname;
            mEmail = email;
            mPassword = password;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            mDialog = ProgressDialog.show(mContext.get(), "Register", "Please wait...", true);
            mDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            Activity context = mContext.get();
            if (context != null) {
                mDialog.dismiss();
                login(context, mFirstname + " " + mLastname, mEmail);
            }
        }
    }
}
