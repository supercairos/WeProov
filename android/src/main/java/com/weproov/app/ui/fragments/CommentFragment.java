package com.weproov.app.ui.fragments;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import butterknife.InjectView;
import com.weproov.app.R;
import com.weproov.app.models.PictureItem;
import com.weproov.app.ui.ifaces.CommandIface;
import com.weproov.app.ui.views.FingerPaintView;
import com.weproov.app.utils.PicassoUtils;

public class CommentFragment extends TunnelFragment implements CommandIface.OnClickListener {

	/**
	 * Arguments key
	 */
	private static final String KEY_COMMENT_PICTURE_PATH = "comment_picture_path";

	@InjectView(R.id.comment_image_view)
	ImageView mImageView;
	@InjectView(R.id.comment_finger_paint)
	FingerPaintView mFingerPaint;

	private Uri mRawPicturePath = null;

	/**
	 * The picture
	 */
	private Bitmap mBitmap;


	public static CommentFragment newInstance(String picturePath) {
		Bundle bundle = new Bundle();
		bundle.putString(KEY_COMMENT_PICTURE_PATH, picturePath);

		CommentFragment commentFragment = new CommentFragment();
		commentFragment.setArguments(bundle);
		return commentFragment;
	}

	public CommentFragment() {
	}


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getArguments() != null) {
			mRawPicturePath = Uri.parse("file://" + getArguments().getString(KEY_COMMENT_PICTURE_PATH));
			Log.d("Test", "Found picture = " + mRawPicturePath);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_comment, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		PicassoUtils.PICASSO.load(mRawPicturePath).fit().centerInside().into(mImageView);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setCommmandListener(this);
	}

	@Override
	public void onPositiveButtonClicked(Button b) {
		Bundle bundle = new Bundle();

		PictureItem item = new PictureItem();
		item.path = mRawPicturePath;
		item.comment = "Test";

		Log.d("Test", "Serializing item " + item);

		bundle.putParcelable(TunnelFragment.KEY_PICTURE_ITEM, item);
		getTunnel().next(bundle);
	}

	@Override
	public void onNegativeButtonClicked(Button b) {
		// Goto dashboard
	}
}
