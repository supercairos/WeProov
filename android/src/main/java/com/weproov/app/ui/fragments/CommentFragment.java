package com.weproov.app.ui.fragments;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import butterknife.InjectView;
import butterknife.OnClick;
import com.weproov.app.R;
import com.weproov.app.ui.MainActivity;
import com.weproov.app.ui.views.FingerPaintView;

import java.util.ArrayList;
import java.util.List;

public class CommentFragment extends BaseFragment {

    @InjectView(R.id.comment_image_view)
    ImageView mImageView;
    @InjectView(R.id.comment_finger_paint)
    FingerPaintView mFingerPaint;
    @InjectView(R.id.comment_list_view)
    ListView mListView;
    EditText mFooter;

    private String mRawPicturePath = null;

    private final List<String> mComments = new ArrayList<String>();
    private final ArrayAdapter<String> mAdapter = new ArrayAdapter<>(getActivity(), R.layout.item_comment, mComments);

    public static CommentFragment newInstance(String picturePath) {
        Bundle bundle = new Bundle();
        bundle.putString(MainActivity.KEY_COMMENT_PICTURE_PATH, picturePath);

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
            mRawPicturePath = getArguments().getString(MainActivity.KEY_COMMENT_PICTURE_PATH);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mFooter = (EditText) inflater.inflate(R.layout.footer_comment, null);
        return inflater.inflate(R.layout.fragment_comment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mListView.setAdapter(mAdapter);
        mListView.addFooterView(mFooter);

        Bitmap bmp = BitmapFactory.decodeFile(MainActivity.KEY_COMMENT_PICTURE_PATH);
        mImageView.setImageBitmap(bmp);
    }

    @OnClick(R.id.add_comment)
    public void onAddCommentClicked() {
        mComments.add(mFooter.getEditableText().toString());
        mAdapter.notifyDataSetChanged();
    }
}
