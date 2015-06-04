package com.weproov.app.utils.connections;

import android.os.Build;
import android.text.TextUtils;
import com.google.gson.JsonSyntaxException;
import com.weproov.app.BuildConfig;
import com.weproov.app.models.exceptions.NetworkException;
import com.weproov.app.utils.AccountUtils;
import com.weproov.app.utils.Dog;
import com.weproov.app.utils.GsonUtils;
import retrofit.ErrorHandler;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.OkClient;
import retrofit.client.Response;
import retrofit.converter.GsonConverter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import static com.weproov.app.utils.GsonUtils.GSON;

public class ParseConnection extends Connection{

	public static final String PROTOCOL = "https"; // http
	public static final String HOST = "api.parse.com"; // weproov.com
	public static final int PORT = 443; // 1337

	private static final String PARSE_APP_ID_KEY = "O5FeshCbuIh6tg8UFMAtyk26jDltZrTVzSVPCPu1";
	private static final String PARSE_REST_KEY = "97dm5I11O1JChm3V82RHx2yqS144FLyif6LEml11";

	public static final String URL = createUrl();

	public static final RestAdapter ADAPTER = new RestAdapter.Builder()
			.setEndpoint(URL)
			.setLogLevel(BuildConfig.DEBUG ? RestAdapter.LogLevel.FULL : RestAdapter.LogLevel.BASIC)
			.setConverter(new GsonConverter(GSON))
			.setClient(new OkClient(HTTP_CLIENT))
			.setRequestInterceptor(new MyRequestInterceptor())
			.setErrorHandler(new MyErrorHandler())
			.build();

	private static String createUrl() {
		return PROTOCOL + "://" + HOST + ":" + PORT + "/1";
	}

	private static final class MyRequestInterceptor implements RequestInterceptor {

		@Override
		public void intercept(RequestFacade request) {
			request.addHeader("X-Parse-Application-Id", PARSE_APP_ID_KEY);
			request.addHeader("X-Parse-Client-Key", PARSE_REST_KEY);
			request.addHeader("X-Parse-Revocable-Session", "1");
			request.addHeader("User-Agent", "WeProov-Client {Android-" + Build.VERSION.SDK_INT + "}");
			request.addHeader("Cache-Control", "public, max-stale=" + String.valueOf(60 * 60 * 3)); // tolerate 3 hours stale
			String s = AccountUtils.peekToken();
			if (!TextUtils.isEmpty(s)) {
				// request.addHeader("Authorization", "Bearer " + s);
				request.addHeader("X-Parse-Session-Token", s);
			}
		}
	}

	private static final class MyErrorHandler implements ErrorHandler {

		@Override
		public Throwable handleError(RetrofitError cause) {
			Response r = cause.getResponse();
			if (r != null) {

				//Try to get response body
				BufferedReader reader;
				StringBuilder sb = new StringBuilder();
				try {
					reader = new BufferedReader(new InputStreamReader(r.getBody().in()));
					String line;
					while ((line = reader.readLine()) != null) {
						sb.append(line);
					}

					NetworkException exception = GsonUtils.GSON.fromJson(sb.toString(), NetworkException.class);
					exception.setStatus(r.getStatus());

					return exception;
				} catch (IOException e) {
					Dog.e(e, "IOException");
				} catch (JsonSyntaxException e) {
					Dog.e(e, "JsonSyntaxException");
				}
			}

			return cause;
		}
	}
}
