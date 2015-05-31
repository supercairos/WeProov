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

		ConnectionSpec spec = new ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
				.tlsVersions(TlsVersion.TLS_1_2)
				.cipherSuites(CipherSuite.TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA)
				.build();

		okHttpClient.setConnectionSpecs(Collections.singletonList(spec));

		Context ctx = MyApplication.getAppContext();
		if (ctx != null) {
			Cache cache = new Cache(ctx.getCacheDir(), 10 * 1024 * 1024); // 10 MiB
			okHttpClient.setCache(cache);
		}

		return okHttpClient;
	}
}
