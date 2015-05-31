package com.weproov.app.utils.connections;

import com.google.gson.JsonSyntaxException;
import com.squareup.okhttp.OkHttpClient;
import com.weproov.app.BuildConfig;
import com.weproov.app.models.wrappers.slack.SlackNetworkError;
import com.weproov.app.utils.Dog;
import com.weproov.app.utils.GsonUtils;
import retrofit.ErrorHandler;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.OkClient;
import retrofit.client.Response;
import retrofit.converter.GsonConverter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import static com.weproov.app.utils.GsonUtils.GSON;

public class SlackConnection extends Connection {

	public static final OkHttpClient HTTP_CLIENT = setupOkHttp();
	private static final String URL = "https://hooks.slack.com/services/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";

	public static final RestAdapter ADAPTER = new RestAdapter.Builder()
			.setEndpoint(URL)
			.setLogLevel(BuildConfig.DEBUG ? RestAdapter.LogLevel.FULL : RestAdapter.LogLevel.BASIC)
			.setConverter(new GsonConverter(GSON))
			.setClient(new OkClient(HTTP_CLIENT))
			.setErrorHandler(new MyErrorHandler())
			.build();

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

					return GsonUtils.GSON.fromJson(sb.toString(), SlackNetworkError.class);
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
