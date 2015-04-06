package com.weproov.app.ui.fragments;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import butterknife.InjectView;
import com.weproov.app.R;
import com.weproov.app.ui.ifaces.CommandIface;
import com.weproov.app.ui.views.FingerPaintView;
import com.weproov.app.ui.views.FourThirdView;
import com.weproov.app.utils.PicassoUtils;

import java.util.ArrayList;
import java.util.List;

public class CommentFragment extends TunnelFragment implements CommandIface.OnClickListener {

    /**
     * Arguments key *
     */
    private static final String KEY_COMMENT_PICTURE_PATH = "comment_picture_path";

    @InjectView(R.id.comment_image_view)
    ImageView mImageView;
    @InjectView(R.id.comment_finger_paint)
    FingerPaintView mFingerPaint;
    @InjectView(R.id.comment_list_view)
    ListView mListView;
    @InjectView(R.id.picture_container)
    FourThirdView mPictureContainer;

    /**
     * Footer Views *
     */
    EditText mAddCommentText;
    ImageView mAddCommentImageView;
    View mFooter;

    private String mRawPicturePath = null;

    private final List<String> mComments = new ArrayList<String>();
    private ArrayAdapter<String> mAdapter;

    /**
     * The picture *
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
            mRawPicturePath = getArguments().getString(KEY_COMMENT_PICTURE_PATH);
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
        PicassoUtils.PICASSO.load("file://" + mRawPicturePath).fit().into(mImageView);
    }

    public void onAddCommentClicked() {
        mComments.add(mAddCommentText.getEditableText().toString());
        mAdapter.notifyDataSetChanged();
        mAddCommentText.getEditableText().clear();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setCommmandListener(this);
    }

    @Override
    public void onPositiveButtonClicked(Button b) {
        getTunnel().next();
    }

    @Override
    public void onNegativeButtonClicked(Button b) {
        // Goto dashboard
    }
}
