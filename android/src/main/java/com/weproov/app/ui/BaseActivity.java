package com.weproov.app.ui;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.LinearLayout;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.Optional;
import com.weproov.app.R;
import com.weproov.app.logic.providers.BusProvider;
import com.weproov.app.ui.ifaces.CommandIface;
import com.weproov.app.utils.debug.ViewServer;

public abstract class BaseActivity extends AppCompatActivity implements CommandIface {

	@InjectView(R.id.content_root_view)
	ViewGroup mRootView;

	@Optional
	@InjectView(R.id.bottom_frame_layout)
	LinearLayout mBottomCommands;

	@Optional
	@InjectView(android.R.id.button2)
	Button mPositiveButton;

	@Optional
	@InjectView(android.R.id.button1)
	Button mNegativeButton;

	CommandIface.OnClickListener mCommandListener;

	private boolean mKeyboardShown;

	@Override
	public void setCommandListener(OnClickListener listener) {
		mCommandListener = listener;
		if (mBottomCommands != null) {
			mBottomCommands.setVisibility((mCommandListener != null && !mKeyboardShown) ? View.VISIBLE : View.GONE);
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
		super.onCreate(savedInstanceState, persistentState);
	}

	@Override
	public void setContentView(int layoutResID) {
		super.setContentView(layoutResID);
		ButterKnife.inject(this);
	}

	@Override
	public void setContentView(View view) {
		super.setContentView(view);
		ButterKnife.inject(this);
	}

	@Override
	public void setContentView(View view, ViewGroup.LayoutParams params) {
		super.setContentView(view, params);
		ButterKnife.inject(this);
	}

	public Button getPositiveButton() {
		return mPositiveButton;
	}

	public Button getNegativeButton() {
		return mNegativeButton;
	}

	@Optional
	@OnClick(android.R.id.button2)
	void onPositiveButtonClicked(Button b) {
		// Empty
		if (mCommandListener != null) {
			mCommandListener.onPositiveButtonClicked(b);
		}
	}

	@Optional
	@OnClick(android.R.id.button1)
	void onNegativeButtonClicked(Button b) {
		if (mCommandListener != null) {
			mCommandListener.onNegativeButtonClicked(b);
		}
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);

		mRootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {
				int heightDiff = mRootView.getRootView().getHeight() - mRootView.getHeight();
				onKeyboardVisibilityChanged(heightDiff > 300);// if more than 300 pixels, its probably a keyboard...
			}
		});
		ViewServer.get(this).addWindow(this);
	}

	@Override
	protected void onStart() {
		super.onStart();
		BusProvider.getInstance().register(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		ViewServer.get(this).setFocusedWindow(this);
	}

	@Override
	protected void onStop() {
		BusProvider.getInstance().unregister(this);
		super.onStop();
	}

	public void onDestroy() {
		super.onDestroy();
		ViewServer.get(this).removeWindow(this);
	}

	protected boolean isKeyboardShown() {
		return mKeyboardShown;
	}

	protected void onKeyboardVisibilityChanged(boolean shown) {
		// empty
		mKeyboardShown = shown;
		if (mBottomCommands != null) {
			mBottomCommands.setVisibility((mCommandListener != null && !mKeyboardShown) ? View.VISIBLE : View.GONE);
		}
	}
}
