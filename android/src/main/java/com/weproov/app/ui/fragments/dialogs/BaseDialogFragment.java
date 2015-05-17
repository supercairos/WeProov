package com.weproov.app.ui.fragments.dialogs;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import butterknife.ButterKnife;
import com.squareup.leakcanary.RefWatcher;
import com.weproov.app.MyApplication;


public abstract class BaseDialogFragment extends DialogFragment {

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		ButterKnife.inject(this, view);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		RefWatcher refWatcher = MyApplication.getRefWatcher();
		refWatcher.watch(this);
	}

}
