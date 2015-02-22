package com.weproov.app.utils.connections;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import com.squareup.okhttp.Cache;
import com.squareup.okhttp.OkHttpClient;
import com.weproov.app.MyApplication;
import com.weproov.app.models.exceptions.LoginException;
import com.weproov.app.models.exceptions.NetworkException;
import com.weproov.app.utils.AccountUtils;
import retrofit.ErrorHandler;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.OkClient;
import retrofit.client.Response;
import retrofit.converter.GsonConverter;

import java.util.concurrent.TimeUnit;

import static com.weproov.app.utils.GsonUtils.GSON;

public class Connection {

    public static final long TIMEOUT = 60;
    public static final String PROTOCOL = "http";
    public static final String HOST = "192.168.0.14";
    public static final int PORT = 1337;

    public static final String URL = createUrl();
    public static final OkHttpClient HTTP_CLIENT = setupOkHttp();
    public static final RestAdapter ADAPTER = new RestAdapter.Builder()
            .setEndpoint(URL)
            .setLogLevel(RestAdapter.LogLevel.FULL)
            .setConverter(new GsonConverter(GSON))
            .setClient(new OkClient(HTTP_CLIENT))
            .setRequestInterceptor(new MyRequestInterceptor())
            .setErrorHandler(new MyErrorHandler())
            .build();

    private static String createUrl() {
        return PROTOCOL + "://" + HOST + ":" + PORT;
    }

    private static OkHttpClient setupOkHttp() {
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.setConnectTimeout(TIMEOUT, TimeUnit.SECONDS);

        Context ctx = MyApplication.getAppContext();
        if (ctx != null) {
            Cache cache = new Cache(ctx.getCacheDir(), 10 * 1024 * 1024); // 10 MiB
            okHttpClient.setCache(cache);
        }

        return okHttpClient;
    }

    private static final class MyRequestInterceptor implements RequestInterceptor {

        @Override
        public void intercept(RequestFacade request) {
            request.addHeader("User-Agent", "WeProov-Client {Android-" + Build.VERSION.SDK_INT + "}");
            request.addHeader("Cache-Control", "public, max-stale=" + String.valueOf(60 * 60 * 3)); // tolerate 3 hours stale
            String s = AccountUtils.peekToken();
            if (!TextUtils.isEmpty(s)) {
                request.addHeader("Authorization", "Bearer " + s);
            }
        }
    }

    private static final class MyErrorHandler implements ErrorHandler {
        @Override
        public Throwable handleError(RetrofitError cause) {
            Response r = cause.getResponse();
            if (r != null && r.getStatus() == 401) {
                switch (r.getStatus()) {
                    case 401:
                        return new LoginException();
                    default:
                        return new NetworkException();
                }

            }

            return cause;
        }
    }
}
