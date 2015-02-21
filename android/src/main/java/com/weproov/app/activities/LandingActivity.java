package com.weproov.app.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.weproov.app.R;
import com.weproov.app.utils.Constants;
import com.weproov.app.utils.PrefUtils;

import java.lang.ref.WeakReference;

public class LandingActivity extends ActionBarActivity {

    @InjectView(R.id.edit_email)
    EditText mEmail;

    @InjectView(R.id.edit_password)
    EditText mPassword;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);
        ButterKnife.inject(this);

        String username = PrefUtils.getString(Constants.KEY_DISPLAY_NAME, "");
        if(!TextUtils.isEmpty(username)){
            Log.d("Test", "Autologin : " + username);
            login(this);
        }
    }


    @OnClick(R.id.button_login)
    public void onButtonLoginClicked(Button button) {
        (new FakeLogin(this)).execute();
    }

    @OnClick(R.id.button_register)
    public void onButtonRegisterClicked(Button button) {
        startActivity(new Intent(this, RegisterActivity.class));
    }


    public static void login(Activity ctx){
        PrefUtils.putString(Constants.KEY_DISPLAY_NAME, "Romain Caire");
        PrefUtils.putString(Constants.KEY_EMAIL, "super.cairos@gmail.com");
        Intent intent = new Intent(ctx, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        ctx.startActivity(intent);
        ctx.finish();
    }

    private static class FakeLogin extends AsyncTask<Void, Void, Void> {

        WeakReference<Activity> mContext;
        ProgressDialog mDialog;

        public FakeLogin(Activity ctx) {
            mContext = new WeakReference<>(ctx);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            mDialog = ProgressDialog.show(mContext.get(), "Login", "Please wait...", true);
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
                login(context);
            }
        }
    }
}
