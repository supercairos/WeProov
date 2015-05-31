package com.weproov.app.logic.services;

import android.accounts.Account;
import android.app.Service;
import android.content.*;
import android.os.Bundle;
import android.os.IBinder;
import com.activeandroid.query.Select;
import com.weproov.app.logic.controllers.SlackTask;
import com.weproov.app.models.Feedback;
import com.weproov.app.models.exceptions.NetworkException;
import com.weproov.app.utils.Dog;
import retrofit.RetrofitError;

import java.util.List;

/**
 * Define a Service that returns an IBinder for the
 * sync adapter class, allowing the sync adapter framework to call
 * onPerformSync().
 */
public class DebugSyncService extends Service {

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

		public SyncAdapter(Context context, boolean autoInitialize, boolean allowParallelSyncs) {
			super(context, autoInitialize, allowParallelSyncs);
		}

		public SyncAdapter(Context context, boolean autoInitialize) {
			this(context, autoInitialize, false);
		}

		@Override
		public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
			try {
				List<Feedback> feedbacks = (new Select()).all().from(Feedback.class).execute();
				for (Feedback feedback : feedbacks) {
					if (SlackTask.send(feedback)) {
						// It's uploaded, delete it;
						feedback.delete();
					}
				}
			} catch (NetworkException e) {
				Dog.e(e, "LoginException");
				syncResult.stats.numAuthExceptions++;
			} catch (final RetrofitError e) {
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
	}
}
