package com.weproov.app.utils.connections;

import android.content.Context;
import com.squareup.okhttp.Cache;
import com.squareup.okhttp.ConnectionSpec;
import com.squareup.okhttp.OkHttpClient;
import com.weproov.app.MyApplication;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

public class Connection {

	public static final OkHttpClient HTTP_CLIENT = setupOkHttp();
	private static final long TIMEOUT = 120;

	protected static OkHttpClient setupOkHttp() {
		OkHttpClient okHttpClient = new OkHttpClient();
		okHttpClient.setConnectTimeout(TIMEOUT, TimeUnit.SECONDS);
		// okHttpClient.setRetryOnConnectionFailure(true);

		// Remove cleartext in the future
		okHttpClient.setConnectionSpecs(Arrays.asList(ConnectionSpec.MODERN_TLS, ConnectionSpec.CLEARTEXT));
		Context ctx = MyApplication.getAppContext();
		if (ctx != null) {
			Cache cache = new Cache(ctx.getCacheDir(), 10 * 1024 * 1024); // 10 MiB
			okHttpClient.setCache(cache);
		}

		return okHttpClient;
	}
}
