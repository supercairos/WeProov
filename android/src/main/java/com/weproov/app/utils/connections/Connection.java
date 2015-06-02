package com.weproov.app.utils.connections;

import android.content.Context;
import com.squareup.okhttp.*;
import com.weproov.app.MyApplication;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

class Connection {

	private static final long TIMEOUT = 60;

	protected static OkHttpClient setupOkHttp() {
		OkHttpClient okHttpClient = new OkHttpClient();
		okHttpClient.setConnectTimeout(TIMEOUT, TimeUnit.SECONDS);

		okHttpClient.setConnectionSpecs(Collections.singletonList(ConnectionSpec.MODERN_TLS));
		Context ctx = MyApplication.getAppContext();
		if (ctx != null) {
			Cache cache = new Cache(ctx.getCacheDir(), 10 * 1024 * 1024); // 10 MiB
			okHttpClient.setCache(cache);
		}

		return okHttpClient;
	}
}
