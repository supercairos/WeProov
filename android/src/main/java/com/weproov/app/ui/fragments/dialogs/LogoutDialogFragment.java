package com.weproov.app.ui.fragments.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.widget.Toast;
import com.activeandroid.content.ContentProvider;
import com.weproov.app.MyApplication;
import com.weproov.app.R;
import com.weproov.app.models.CarInfo;
import com.weproov.app.models.ClientInfo;
import com.weproov.app.models.PictureItem;
import com.weproov.app.models.WeProov;
import com.weproov.app.ui.LandingActivity;
import com.weproov.app.utils.AccountUtils;
import com.weproov.app.utils.Dog;
import com.weproov.app.utils.PlayServicesUtils;

import java.io.File;

public class LogoutDialogFragment extends BaseDialogFragment {

	@NonNull
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// Handle Logout;
		final Context context = getActivity().getApplicationContext();
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		return builder.setMessage(R.string.logout_message)
				.setTitle(R.string.logout_title)
				.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						AccountUtils.removeAccount(getActivity(), new AccountUtils.AccountRemovedCallback() {
							@Override
							public void onSuccess() {
								Intent intent = new Intent(context, LandingActivity.class);
								intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
								// FIXME : ...
								intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
								context.startActivity(intent);
								new CleanupThread().start();
							}

							@Override
							public void onFailure() {
								Toast.makeText(getActivity(), "Could not remove this account", Toast.LENGTH_LONG).show();
							}
						});
					}
				})
				.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						// User cancelled the dialog
					}
				})
				.create();
	}

	private static class CleanupThread extends Thread {
		ContentResolver mContentResolver;

		public CleanupThread() {
			super("CleanupThread");
			this.mContentResolver = MyApplication.getAppContext().getContentResolver();
		}

		@Override
		public void run() {
			Dog.d("Cleaning up the shit this user made MOFO!");
			// Keep order because of foreign keys
			mContentResolver.delete(ContentProvider.createUri(PictureItem.class, null), null, null);
			mContentResolver.delete(ContentProvider.createUri(WeProov.class, null), null, null);
			mContentResolver.delete(ContentProvider.createUri(CarInfo.class, null), null, null);
			mContentResolver.delete(ContentProvider.createUri(ClientInfo.class, null), null, null);

			PlayServicesUtils.forgetRegistrationId(MyApplication.getAppContext());
			trimCache(MyApplication.getAppContext());
		}

		public static void trimCache(Context context) {
			File dir = context.getCacheDir();
			if (dir != null && dir.isDirectory()) {
				String[] children = dir.list();
				for (String child : children) {
					File f = new File(dir, child);
					if (f.isDirectory()) {
						deleteDir(new File(dir, child));
					} else if (f.isFile()) {
						if (!f.delete()) {
							Dog.e("Could not delete file : %s", f);
						}
					}

				}
			}
		}

		public static boolean deleteDir(File dir) {
			if (dir != null && dir.isDirectory()) {
				String[] children = dir.list();
				for (String child : children) {
					boolean success = deleteDir(new File(dir, child));
					if (!success) {
						return false;
					}
				}
			}

			// The directory is now empty so delete it
			return dir != null && dir.delete();
		}
	}
}
