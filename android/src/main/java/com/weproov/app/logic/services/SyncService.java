package com.weproov.app.logic.services;

import android.accounts.Account;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.*;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import com.activeandroid.query.Select;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.weproov.app.R;
import com.weproov.app.logic.controllers.CarTask;
import com.weproov.app.logic.controllers.PicturesTask;
import com.weproov.app.logic.controllers.WeProovTask;
import com.weproov.app.models.PictureItem;
import com.weproov.app.models.WeProov;
import com.weproov.app.models.exceptions.NetworkException;
import com.weproov.app.ui.LandingActivity;
import com.weproov.app.utils.CryptoUtils;
import com.weproov.app.utils.Dog;
import com.weproov.app.utils.connections.Connection;
import retrofit.RetrofitError;

import java.io.IOException;
import java.util.List;

/**
 * Define a Service that returns an IBinder for the
 * sync adapter class, allowing the sync adapter framework to call
 * onPerformSync().
 */
public class SyncService extends Service {

	// Storage for an instance of the sync adapter
	private static SyncAdapter sSyncAdapter = null;
	// Object to use as a thread-safe lock
	private static final Object sSyncAdapterLock = new Object();

	/*
	 * Instantiate the sync adapter object.
	 */
	@Override
	public void onCreate() {
		/*
		 * Create the sync adapter as a singleton.
         * Set the sync adapter as syncable
         * Disallow parallel syncs
         */
		synchronized (sSyncAdapterLock) {
			if (sSyncAdapter == null) {
				sSyncAdapter = new SyncAdapter(getApplicationContext(), true);
			}
		}
	}

	/**
	 * Return an object that allows the system to invoke
	 * the sync adapter.
	 */
	@Override
	public IBinder onBind(Intent intent) {
		/*
		 * Get the object that allows external processes
         * to call onPerformSync(). The object is created
         * in the base class code when the SyncAdapter
         * constructors call super()
         */
		return sSyncAdapter.getSyncAdapterBinder();
	}

	private static class SyncAdapter extends AbstractThreadedSyncAdapter {

		private static final int DOWNLOAD_NOTIFICATION_ID = 2;
		private final NotificationManager mNotifyManager;
		private NotificationCompat.Builder mBuilder;

		/**
		 * Set up the sync adapter. This form of the
		 * constructor maintains compatibility with Android 3.0
		 * and later platform versions
		 */
		public SyncAdapter(Context context, boolean autoInitialize, boolean allowParallelSyncs) {
			super(context, autoInitialize, allowParallelSyncs);
			/*
			 * If your app uses a content resolver, get an instance of it
			 * from the incoming Context
			 */
			mNotifyManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		}

		public SyncAdapter(Context context, boolean autoInitialize) {
			super(context, autoInitialize);
			mNotifyManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		}

		@Override
		public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {

			mBuilder = new NotificationCompat.Builder(getContext())
					.setSmallIcon(R.drawable.ic_notification)
					.setProgress(0, 0, true)
					.setContentTitle(getContext().getString(R.string.notification_picture_download_title))
					.setContentIntent(PendingIntent.getActivity(getContext(), 0, new Intent(getContext(), LandingActivity.class), 0))
					.setContentText(getContext().getString(R.string.notification_picture_download_start));

			Notification notification;
			try {
				notification = mBuilder.build();
				notification.flags |= Notification.FLAG_ONGOING_EVENT;
				mNotifyManager.notify(DOWNLOAD_NOTIFICATION_ID, notification);

				List<WeProov> items = (new Select()).all().from(WeProov.class).where("server_id IS NULL").execute();

				Dog.d("Sync started ... ");

				int size = items.size();
				for (int i = 0; i < size; i++) {
					WeProov proov = items.get(i);
					Dog.d("Syncing item >> %s", proov);
					mBuilder.setProgress(size, i, false);
					mBuilder.setContentText(getContext().getString(R.string.notification_uploading_item_number, proov.car.plate));
					notification = mBuilder.build();
					notification.flags |= Notification.FLAG_ONGOING_EVENT;

					mNotifyManager.notify(DOWNLOAD_NOTIFICATION_ID, notification);

					List<PictureItem> pictures = proov.getPictures();
					int count = pictures.size();
					for (PictureItem pictureItem : pictures) {
						// Upload picture
						mBuilder.setProgress(count, i, false);
						mBuilder.setContentText(getContext().getString(R.string.notification_uploading_photo_number, i, proov.car.plate));
						notification = mBuilder.build();
						notification.flags |= Notification.FLAG_ONGOING_EVENT;

						mNotifyManager.notify(DOWNLOAD_NOTIFICATION_ID, notification);
						PicturesTask.upload(pictureItem);
					}

					mBuilder.setProgress(size, i, false);
					mBuilder.setContentText(getContext().getString(R.string.notification_finishing_upload, proov.car.plate));
					notification = mBuilder.build();
					notification.flags |= Notification.FLAG_ONGOING_EVENT;

					mNotifyManager.notify(DOWNLOAD_NOTIFICATION_ID, notification);

					CarTask.upload(proov.car);
					WeProovTask.upload(proov);
					String url = getWeProovUrl(proov);
					Dog.d("Generating document using URL %s", url);
					Request request = new Request.Builder()
							.url(url)
							.build();

					OkHttpClient client = Connection.HTTP_CLIENT;
					Response response = client.newCall(request).execute();
					if (!response.isSuccessful()) {
						throw new IOException("Unexpected code " + response);
					}
				}

				Dog.d("Sync finished ... ");

				// When the loop is finished, updates the notification
				mBuilder.setContentText(getContext().getString(R.string.notification_picture_download_end))
						// Removes the progress bar
						.setProgress(0, 0, false);

				notification = mBuilder.build();
				notification.flags |= Notification.FLAG_AUTO_CANCEL;
				mNotifyManager.notify(DOWNLOAD_NOTIFICATION_ID, notification);
			} catch (IOException e) {
				onError();
				Dog.e(e, "IOException");
				syncResult.stats.numIoExceptions++;
			} catch (NetworkException e) {
				onError();
				Dog.e(e, "LoginException");
				syncResult.stats.numAuthExceptions++;
			} catch (final RetrofitError e) {
				onError();
				Dog.e(e, "RetrofitError");
				if (e.getKind() == RetrofitError.Kind.CONVERSION) {
					Dog.e("RetrofitError kind RetrofitError.Kind.CONVERSION");
					syncResult.stats.numParseExceptions++;
				} else if (e.getKind() == RetrofitError.Kind.HTTP) {
					Dog.e("RetrofitError kind RetrofitError.Kind.HTTP");
					syncResult.stats.numAuthExceptions++;
				} else if (e.getKind() == RetrofitError.Kind.NETWORK) {
					Dog.e("RetrofitError kind RetrofitError.Kind.NETWORK");
					syncResult.stats.numIoExceptions++;
				}
			}

		}

		private void onError() {
			// When the loop is finished, updates the notification
			mBuilder.setContentText(getContext().getString(R.string.notification_upload_failed))
					// Removes the progress bar
					.setProgress(0, 0, false);

			Notification notification = mBuilder.build();
			notification.flags |= Notification.FLAG_AUTO_CANCEL;
			mNotifyManager.notify(DOWNLOAD_NOTIFICATION_ID, notification);
		}

		private String getWeProovUrl(WeProov proov) {
			String objectId = proov.proovCode;
			String hash = CryptoUtils.sha1(CryptoUtils.sha1(objectId) + "@wePr00v!@");
			return "http://weproov.com/visu/temp/create.php?weproov=" + objectId + "&hash=" + hash;
		}
	}

}
