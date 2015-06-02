package com.weproov.app;

import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.os.StrictMode;
import android.support.multidex.MultiDex;
import com.activeandroid.ActiveAndroid;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;
import com.weproov.app.utils.Dog;

public class MyApplication extends Application {

	public static String PACKAGE_NAME;

	private static RefWatcher sRefWatcher;
	private static Context sContext;

	@Override
	public void onCreate() {
		super.onCreate();
		PACKAGE_NAME = getPackageName();

		ActiveAndroid.initialize(this);
		ActiveAndroid.setLoggingEnabled(true);

		Dog.bury(new Dog.DebugBone());

		enabledStrictMode();
		sRefWatcher = LeakCanary.install(this);
	}

	protected void attachBaseContext(Context base) {
		super.attachBaseContext(base);
		MultiDex.install(this);
		sContext = this;
	}

	private void enabledStrictMode() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
			StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder() //
					.detectAll() //
					.penaltyLog() //
					.build());
		}
	}

	@Override
	public void onTerminate() {
		super.onTerminate();
		ActiveAndroid.dispose();
	}

	public static Context getAppContext() {
		return sContext;
	}

	public static RefWatcher getRefWatcher() {
		return sRefWatcher;
	}
}
