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
import android.util.Log;
import com.activeandroid.query.Select;
import com.weproov.app.R;
import com.weproov.app.logic.controllers.PicturesTask;
import com.weproov.app.models.PictureItem;

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
					.setContentIntent(PendingIntent.getActivity(this, 0, new Intent(this, MyActivity.class), 0))
					.setContentText(getContext().getString(R.string.notification_picture_download_start));

			Notification notification = mBuilder.build();
			notification.flags |= Notification.FLAG_ONGOING_EVENT;
			mNotifyManager.notify(DOWNLOAD_NOTIFICATION_ID, notification);

			List<PictureItem> items = (new Select()).all().from(PictureItem.class).where("uploaded = ?", false).execute();

			Log.d("Test", "Sync started ... ");

			int size = items.size();
			for (int i = 0; i < size; i++) {
				PictureItem item = items.get(i);
				Log.d("Test", "Syncing item >> " + item);
				mBuilder.setProgress(size, i, false);
				mNotifyManager.notify(DOWNLOAD_NOTIFICATION_ID, mBuilder.build());

				// Upload picture
				PicturesTask.upload(item);

				item.uploaded = true;
				item.save();
			}

			// When the loop is finished, updates the notification
			mBuilder.setContentText(getContext().getString(R.string.notification_picture_download_end))
					// Removes the progress bar
					.setProgress(0, 0, false);

			notification = mBuilder.build();
			notification.flags |= Notification.FLAG_AUTO_CANCEL;
			mNotifyManager.notify(DOWNLOAD_NOTIFICATION_ID, notification);
		}
	}

}
