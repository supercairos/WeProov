package com.weproov.app.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.squareup.otto.Subscribe;
import com.weproov.app.R;
import com.weproov.app.logic.controllers.UsersTask;
import com.weproov.app.models.User;
import com.weproov.app.models.events.RegisterErrorEvent;
import com.weproov.app.models.events.RegisterSuccessEvent;
import com.weproov.app.utils.validators.EmailValidator;
import com.weproov.app.utils.validators.PasswordValidator;

public class RegisterActivity extends BaseActivity {

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

    @InjectView(R.id.button_positive)
    Button mPositiveButton;

    @InjectView(R.id.button_negative)
    Button mNegativeButton;

    ProgressDialog mDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.inject(this);
        setSupportActionBar(mActionBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mPositiveButton.setText(R.string.register);
        mNegativeButton.setText(android.R.string.cancel);
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

    @OnClick(R.id.button_negative)
    public void onButtonBackClicked() {
        NavUtils.navigateUpFromSameTask(this);
    }

    @OnClick(R.id.button_positive)
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
            mDialog = ProgressDialog.show(this, "Register", "Please wait...", true);
            mDialog.show();

            UsersTask.register(new User(email, password, firstname, lastname));
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mDialog != null) {
            mDialog.dismiss();
        }
    }

    @Subscribe
    public void onRegisterSuccess(RegisterSuccessEvent event) {
        mDialog.dismiss();
        gotoMain();
    }

    @Subscribe
    public void onRegisterError(RegisterErrorEvent event) {
        mDialog.dismiss();
        Toast.makeText(this, "Error while registering", Toast.LENGTH_SHORT).show();
    }


    private void gotoMain() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
