package com.weproov.app.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.squareup.otto.Subscribe;
import com.weproov.app.R;
import com.weproov.app.logic.controllers.UsersTask;
import com.weproov.app.models.events.LoginErrorEvent;
import com.weproov.app.models.events.LoginSuccessEvent;
import com.weproov.app.utils.AccountUtils;

public class LandingActivity extends BaseActivity {

    @InjectView(R.id.edit_email)
    EditText mEmail;

    @InjectView(R.id.edit_password)
    EditText mPassword;

    ProgressDialog mDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);
        ButterKnife.inject(this);

        String token = AccountUtils.peekToken();
        if(!TextUtils.isEmpty(token)){
            Log.d("Test", "Autologin : " + token);
        }
    }


    @OnClick(R.id.button_login)
    public void onButtonLoginClicked(Button button) {
        mDialog = ProgressDialog.show(this, "Login", "Please wait...", true);
        mDialog.show();

        String email = mEmail.getEditableText().toString().trim();
        String password = mPassword.getEditableText().toString().trim();

        UsersTask.login(email, password);
    }

    @OnClick(R.id.button_register)
    public void onButtonRegisterClicked(Button button) {
        startActivity(new Intent(this, RegisterActivity.class));
    }

    @Subscribe
    public void onLoginSuccess(LoginSuccessEvent event){
        mDialog.dismiss();
        gotoMain();
    }

    @Subscribe
    public void onLoginError(LoginErrorEvent event){
        mDialog.dismiss();
        Toast.makeText(this, "Error while login", Toast.LENGTH_SHORT);
    }

    private void gotoMain() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
