package com.weproov.app.ui;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.inputmethod.EditorInfo;
import android.widget.*;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.squareup.otto.Subscribe;
import com.weproov.app.BuildConfig;
import com.weproov.app.R;
import com.weproov.app.logic.controllers.ProfileLoader;
import com.weproov.app.logic.controllers.UsersTask;
import com.weproov.app.models.User;
import com.weproov.app.models.UserProfile;
import com.weproov.app.models.events.LoginSuccessEvent;
import com.weproov.app.models.events.NetworkErrorEvent;
import com.weproov.app.models.exceptions.NetworkException;
import com.weproov.app.ui.ifaces.CommandIface;
import com.weproov.app.ui.views.WeproovLogoView;
import com.weproov.app.utils.AccountUtils;
import com.weproov.app.utils.CameraUtils;
import com.weproov.app.utils.Dog;
import com.weproov.app.utils.PixelUtils;
import retrofit.RetrofitError;

public class LandingActivity extends BaseActivity implements CommandIface.OnClickListener {

	private static final float TRANSLATION_Y = PixelUtils.convertDpToPixel(96);
	private static final int LOADER_ID = 1337;

	private Handler mHandler = new Handler();

	@InjectView(R.id.background)
	ImageView mBackground;

	@InjectView(R.id.background_blur)
	ImageView mBackgroundBlur;

	@InjectView(R.id.weproov_animated_logo)
	WeproovLogoView mLogoView;

	@InjectView(R.id.login_group)
	LinearLayout mLoginGroup;

	@InjectView(R.id.edit_email)
	EditText mEmail;

	@InjectView(R.id.edit_password)
	EditText mPassword;

	ProgressDialog mDialog;
	private ProfileLoader mLoader;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (!CameraUtils.checkCameraHardware(this)) {
			Toast.makeText(this, R.string.camera_is_required, Toast.LENGTH_SHORT).show();
			finish();
		}

		String token = AccountUtils.peekToken();
		if (!TextUtils.isEmpty(token)) {
			Dog.d("Auto login : %s", token);
			gotoMain();
		}

		setContentView(R.layout.activity_landing);
		ButterKnife.inject(this);

		getNegativeButton().setText(R.string.register);
		getPositiveButton().setText(R.string.login);

		mPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				boolean handled = false;
				if (actionId == EditorInfo.IME_ACTION_DONE) {
					onPositiveButtonClicked(null);
					handled = true;
				}
				return handled;
			}
		});

		mLogoView.setOnStateChangeListenerListener(new WeproovLogoView.OnStateChangeListener() {
			@Override
			public void onStateChange(int state) {
				if (state == WeproovLogoView.STATE_FILL_STARTED) {
//					mSubtitleView.setAlpha(0);
//					mSubtitleView.setVisibility(View.VISIBLE);
//					mSubtitleView.setTranslationY(-mSubtitleView.getHeight());

					// Bug in older versions where set.setInterpolator didn't work
					AnimatorSet set = new AnimatorSet();
					Interpolator interpolator = new DecelerateInterpolator();
					ObjectAnimator a1 = ObjectAnimator.ofFloat(mLogoView, View.TRANSLATION_Y, 0);
//					ObjectAnimator a2 = ObjectAnimator.ofFloat(mSubtitleView, View.TRANSLATION_Y, 0);
//					ObjectAnimator a3 = ObjectAnimator.ofFloat(mSubtitleView, View.ALPHA, 1);

					ObjectAnimator a4 = ObjectAnimator.ofFloat(mBackgroundBlur, View.ALPHA, 1);
					ObjectAnimator a5 = ObjectAnimator.ofFloat(mBackground, View.ALPHA, 0);

					ObjectAnimator a6 = ObjectAnimator.ofFloat(mLoginGroup, View.TRANSLATION_Y, 0);
					ObjectAnimator a7 = ObjectAnimator.ofFloat(mLoginGroup, View.ALPHA, 1);

					a1.setInterpolator(interpolator);
//					a2.setInterpolator(interpolator);
					a6.setInterpolator(interpolator);

					set.setDuration(500).playTogether(a1, a4, a5, a6, a7);
					set.addListener(new Animator.AnimatorListener() {
						@Override
						public void onAnimationStart(Animator animation) {
							mBackground.setVisibility(View.VISIBLE);
							mBackground.setAlpha(1.0f);

							mBackgroundBlur.setVisibility(View.VISIBLE);
							mBackgroundBlur.setAlpha(0.0f);

							mLoginGroup.setVisibility(View.VISIBLE);
							mLoginGroup.setAlpha(0.0f);
						}

						@Override
						public void onAnimationEnd(Animator animation) {
							mBackground.setVisibility(View.GONE);
						}

						@Override
						public void onAnimationCancel(Animator animation) {

						}

						@Override
						public void onAnimationRepeat(Animator animation) {

						}
					});
					set.start();
				}
			}
		});

		setCommandListener(this);

		mLoader = new ProfileLoader(this) {
			@Override
			protected void onProfileLoaded(UserProfile profile) {
				mEmail.setText(profile.email);
			}
		};
		getSupportLoaderManager().initLoader(LOADER_ID, null, mLoader);
	}

	@Override
	protected void onStart() {
		super.onStart();
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				start();
			}
		}, 500);
	}

	private void start() {
		mLogoView.setTranslationY(TRANSLATION_Y);
		mLogoView.start();

		mBackground.setVisibility(View.VISIBLE);
		mBackground.setAlpha(1.0f);
		mBackgroundBlur.setVisibility(View.GONE);

		mLoginGroup.setVisibility(View.GONE);
		mLoginGroup.setTranslationY(TRANSLATION_Y);
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onStop() {
		super.onPause();
		if (mDialog != null) {
			mDialog.dismiss();
		}
	}

	@Override
	public void onPositiveButtonClicked(Button b) {
		mDialog = ProgressDialog.show(this, "Login", "Please wait...", true);
		mDialog.show();

		String email = mEmail.getEditableText().toString().trim();
		String password = mPassword.getEditableText().toString().trim();

		if (!BuildConfig.DEBUG || (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password))) {
			UsersTask.login(email, password);
		} else {
			UsersTask.save(new User("super.cairos@gmail.com", "password", "Romain", "Caire", null));
			gotoMain();
		}
	}

	@Override
	public void onNegativeButtonClicked(Button b) {
		startActivity(new Intent(this, RegisterActivity.class));
	}

	@Subscribe
	public void onLoginSuccess(LoginSuccessEvent event) {
		mDialog.dismiss();
		gotoMain();
	}

	@Subscribe
	public void onRegisterError(NetworkErrorEvent event) {
		mDialog.dismiss();

		Throwable throwable = event.throwable;
		String message;
		if (throwable instanceof NetworkException) {
			switch (((NetworkException) throwable).getCode()) {
				case NetworkException.OBJECT_NOT_FOUND:
					message = "This account doesn't exit or the password is wrong";
					break;
				default:
					message = throwable.getMessage();
					break;
			}
		} else if (throwable instanceof RetrofitError) {
			message = "Retrofit Error : " + ((RetrofitError) throwable).getKind();
		} else {
			// Network error...
			message = "WTF ??";
		}

		new AlertDialog.Builder(this)
				.setPositiveButton(android.R.string.ok, null)
				.setMessage(message)
				.setTitle(android.R.string.untitled)
				.show();
	}

	private void gotoMain() {
		Intent intent = new Intent(this, MainActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
		finish();
	}

	@Override
	protected void onKeyboardVisibilityChanged(boolean shown) {
		super.onKeyboardVisibilityChanged(shown);
		if (mLogoView.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
			ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) mLogoView.getLayoutParams();
			int top = (int) (shown ? PixelUtils.convertDpToPixel(5) : PixelUtils.convertDpToPixel(40));
			p.setMargins(p.leftMargin, top, p.rightMargin, p.bottomMargin);
			mLogoView.requestLayout();
		}
	}
}
