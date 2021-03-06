package com.weproov.app.utils;


import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;
import com.weproov.app.BuildConfig;
import com.weproov.app.MyApplication;
import com.weproov.app.utils.connections.Connection;

public final class PicassoUtils {

	public static final Picasso PICASSO = new Picasso.Builder(MyApplication.getAppContext())
			.loggingEnabled(BuildConfig.DEBUG)
			.indicatorsEnabled(BuildConfig.DEBUG)
			.downloader(new OkHttpDownloader(Connection.HTTP_CLIENT))
			.build();


}
