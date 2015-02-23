package com.weproov.app.ui;

import android.support.v7.app.ActionBarActivity;
import com.weproov.app.models.providers.BusProvider;

public abstract class BaseActivity extends ActionBarActivity {

    @Override
    protected void onStart() {
        super.onStart();
        BusProvider.getInstance().register(this);
    }
    @Override
    protected void onStop() {
        BusProvider.getInstance().unregister(this);
        super.onStop();
    }
}
