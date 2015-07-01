package com.weproov.app.ui.fragments;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.InjectView;
import com.weproov.app.R;
import com.weproov.app.ui.ifaces.CommandIface;
import com.weproov.app.ui.views.FingerPaintView;
import com.weproov.app.utils.BitmapUtils;
import com.weproov.app.utils.CameraUtils;
import com.weproov.app.utils.Dog;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class SignatureFragment extends TunnelFragment implements CommandIface.OnClickListener {

	private static final String KEY_PERSON_NAME = "key_person_name";

	@InjectView(R.id.finger_paint_view)
	FingerPaintView mFingerPaint;

	@InjectView(R.id.signature_text_view)
	TextView mTextView;

	private String mName;

	public static SignatureFragment newInstance(String name) {
		Bundle bundle = new Bundle();
		bundle.putString(KEY_PERSON_NAME, name);

		SignatureFragment signatureFragment = new SignatureFragment();
		signatureFragment.setArguments(bundle);
		return signatureFragment;
	}

	public SignatureFragment() {
		super();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getArguments() != null) {
			mName = getArguments().getString(KEY_PERSON_NAME);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_signature, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		setCommmandListener(this);
		getNegativeButton().setVisibility(View.VISIBLE);
		getNegativeButton().setText(R.string.clear_signature);

		mTextView.setText(getString(R.string.signature_of, mName));
	}

	@Override
	public void onPositiveButtonClicked(Button b) {
		File f = save();
		if (f != null) {
			Bundle bundle = new Bundle();
			bundle.putParcelable(TunnelFragment.KEY_SIGNATURE_ITEM, Uri.fromFile(f));
			getTunnel().next(bundle);
		} else {
			Toast.makeText(getActivity(), R.string.error_saving_signature, Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public void onNegativeButtonClicked(Button b) {
		mFingerPaint.clear();
	}

	private File save() {
		File f = CameraUtils.getOutputMediaFile(CameraUtils.MEDIA_TYPE_IMAGE);

		Bitmap bitmap = mFingerPaint.getBitmap();
		bitmap = BitmapUtils.removeTransparent(bitmap);
		if (bitmap != null) {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			bitmap.compress(Bitmap.CompressFormat.PNG, 0 /* ignored */, bos);

			FileOutputStream fos = null;
			try {
				fos = new FileOutputStream(f);
				fos.write(bos.toByteArray());
				fos.flush();
			} catch (IOException e) {
				Dog.d(e, "IO");
				return null;
			} finally {
				if (fos != null) {
					try {
						fos.close();
					} catch (IOException e) {
						Dog.d(e, "IO");
					}
				}
			}

			CameraUtils.sendMediaScannerBroadcast(getActivity(), f);
			return f;
		}

		return null;
	}
}
