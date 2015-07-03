package com.weproov.app;

import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.os.StrictMode;
import android.support.multidex.MultiDex;
import android.text.TextUtils;
import android.util.Log;
import com.activeandroid.ActiveAndroid;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.StandardExceptionParser;
import com.google.android.gms.analytics.Tracker;
import com.weproov.app.utils.Dog;

public class MyApplication extends Application {

	public static GoogleAnalytics sAnalytics;
	public static Tracker sTracker;

	public static String PACKAGE_NAME;

//	private static RefWatcher sRefWatcher;
	private static Context sContext;

	@Override
	public void onCreate() {
		super.onCreate();
		PACKAGE_NAME = getPackageName();

		ActiveAndroid.initialize(this);
		ActiveAndroid.setLoggingEnabled(true);

		Dog.bury(new Dog.DebugBone());


		if (BuildConfig.DEBUG) {
			// enabledStrictMode();
		}
//		sRefWatcher = LeakCanary.install(this);

		sAnalytics = GoogleAnalytics.getInstance(this);
		sAnalytics.setLocalDispatchPeriod(1800);
		// Dog.bury(new GoogleAnalyticsTree());
		sTracker = sAnalytics.newTracker("UA-64080930-1");
		// sTracker.enableExceptionReporting(true);
		sTracker.enableAdvertisingIdCollection(true);
		sTracker.enableAutoActivityTracking(true);
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
					.penaltyFlashScreen() //
					.penaltyLog()
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

//	public static RefWatcher getRefWatcher() {
//		return sRefWatcher;
//	}

	/**
	 * A tree which logs important information for crash reporting.
	 */
	private static class GoogleAnalyticsTree extends Dog.Bone {
		@Override
		protected void log(int priority, String tag, String message, Throwable t) {
			if (priority == Log.VERBOSE || priority == Log.DEBUG) {
				return;
			}

			if (t != null) {
				// Build and send exception.
				sTracker.send(new HitBuilders.ExceptionBuilder()
						.setDescription(new StandardExceptionParser(sContext, null).getDescription(Thread.currentThread().getName(), t))
						.setFatal(priority >= Log.ERROR)
						.build());
			} else if (!TextUtils.isEmpty(message)) {
				sTracker.send(new HitBuilders.ExceptionBuilder()
						.setDescription(tag + ":" + message)
						.setFatal(priority >= Log.ERROR)
						.build());
			}
		}
	}
}
