package com.weproov.app.ui.fragments;

import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import butterknife.InjectView;
import com.weproov.app.R;
import com.weproov.app.models.PictureItem;
import com.weproov.app.ui.ifaces.CommandIface;
import com.weproov.app.ui.views.BitmapRegionTileSource;
import com.weproov.app.ui.views.TiledImageView;
import com.weproov.app.utils.Dog;

public class CommentFragment extends TunnelFragment implements CommandIface.OnClickListener {

	/**
	 * Arguments key
	 */
	private static final String KEY_COMMENT_PICTURE_PATH = "comment_picture_path";

	@InjectView(R.id.comment_image_view)
	TiledImageView mImageView;

	@InjectView(R.id.comment_edit_text)
	EditText mEditText;

	private Uri mRawPicturePath = null;

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
			Dog.d("Found picture = %s", mRawPicturePath);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_comment, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		String path = getArguments().getString(KEY_COMMENT_PICTURE_PATH);
		if (!TextUtils.isEmpty(path)) {
			mImageView.setTileSource(new BitmapRegionTileSource(getActivity(), getArguments().getString(KEY_COMMENT_PICTURE_PATH), 0, 0), null);
		}
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setCommmandListener(this);
	}

	@Override
	public void onResume() {
		super.onResume();
		mImageView.onResume();
	}

	@Override
	public void onPause() {
		mImageView.onPause();
		super.onPause();
	}

	@Override
	public void onDestroy() {
		if(mImageView != null) {
			mImageView.destroy();
		} else {
			Dog.d("onDestroy > WTF, ImageView was null :(");
		}
		super.onDestroy();
	}

	@Override
	public void onPositiveButtonClicked(Button b) {
		Bundle bundle = new Bundle();

		PictureItem item = new PictureItem();
		item.path = mRawPicturePath;
		item.comment = mEditText.getEditableText().toString();

		bundle.putParcelable(TunnelFragment.KEY_PICTURE_ITEM, item);
		getTunnel().next(bundle);
	}

	@Override
	public void onNegativeButtonClicked(Button b) {

	}
}
