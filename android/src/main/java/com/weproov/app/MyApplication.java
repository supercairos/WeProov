package com.weproov.app;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;
import com.activeandroid.ActiveAndroid;

public class MyApplication extends Application {

    private static Context sContext;

    @Override
    public void onCreate() {
        super.onCreate();

        sContext = this;
        ActiveAndroid.initialize(this);
        MultiDex.install(this);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        ActiveAndroid.dispose();
    }

    public static Context getAppContext() {
        return sContext;
    }
}
