package com.weproov.app.ui.fragments;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
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
import com.weproov.app.ui.ifaces.Tunnelface;
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

    /** Footer Views **/
    EditText mAddCommentText;
    ImageView mAddCommentImageView;
    View mFooter;

    private String mRawPicturePath = null;

    private final List<String> mComments = new ArrayList<String>();
    private ArrayAdapter<String> mAdapter;



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
            Log.d("Test", "Found picture = " + mRawPicturePath);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mFooter = inflater.inflate(R.layout.footer_comment, null);
        mAddCommentImageView = (ImageView) mFooter.findViewById(R.id.add_comment_image);
        mAddCommentText = (EditText) mFooter.findViewById(R.id.add_comment_text);
        return inflater.inflate(R.layout.fragment_comment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAdapter = new ArrayAdapter<>(getActivity(), R.layout.item_comment, mComments);
        mListView.setAdapter(mAdapter);
        mListView.addFooterView(mFooter);
        mListView.setSelection(mAdapter.getCount());
        mAddCommentImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onAddCommentClicked();
            }
        });

        Bitmap bmp = BitmapFactory.decodeFile(mRawPicturePath);
        mImageView.setImageBitmap(bmp);
    }

    public void onAddCommentClicked() {
        mComments.add(mAddCommentText.getEditableText().toString());
        mAdapter.notifyDataSetChanged();
        mAddCommentText.getEditableText().clear();
    }

    @OnClick(R.id.button_positive)
    public void onOkClicked() {
        ((Tunnelface) getActivity()).next();
    }

    @OnClick(R.id.button_negative)
    public void onCancelClicked() {
        // Goto dashboard
    }
}
