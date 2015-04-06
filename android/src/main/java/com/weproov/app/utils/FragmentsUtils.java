package com.weproov.app.utils;


import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;

public final class FragmentsUtils {

	public static void replace(ActionBarActivity attachTo, Fragment fragment, int rootView, String tag, boolean addToBackstack, int animIn, int animOut) {

		if (fragment == null || attachTo == null) {
			throw new RuntimeException("New fragment or Activity is null");
		}

		FragmentManager fragmentManager = attachTo.getSupportFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		// fragmentTransaction.setCustomAnimations(animIn, animOut);
		if (!TextUtils.isEmpty(tag)) {
			fragmentTransaction.replace(rootView, fragment, tag);
		} else {
			fragmentTransaction.replace(rootView, fragment);
		}

		if (addToBackstack) {
			fragmentTransaction.addToBackStack(tag);
		}
		fragmentTransaction.commit();
	}

	public static void replace(ActionBarActivity attachTo, Fragment fragment, int rootView) {
		replace(attachTo, fragment, rootView, "tag", true, 0, 0);
	}

	public static void showDialog(ActionBarActivity attachTo, DialogFragment fragment) {
		FragmentTransaction ft = attachTo.getSupportFragmentManager().beginTransaction();
		Fragment prev = attachTo.getSupportFragmentManager().findFragmentByTag("dialog");
		if (prev != null) {
			ft.remove(prev);
		}

		ft.addToBackStack(null);

		// Create and show the dialog.
		fragment.show(ft, "dialog");
	}

	public static void clearBackStack(FragmentManager fm) {
		fm.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
	}
}