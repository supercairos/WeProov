package com.weproov.app.ui.fragments.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import butterknife.InjectView;
import com.weproov.app.R;
import com.weproov.app.ui.views.FingerPaintView;

public class SignatureDialogFragment extends BaseDialogFragment {

    @InjectView(R.id.finger_paint_view)
    FingerPaintView mFingerPaintView;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        LayoutInflater inflater = LayoutInflater.from(getActivity());
        mFingerPaintView = (FingerPaintView) inflater.inflate(R.layout.fragment_signature, null);

        return new AlertDialog.Builder(getActivity())
                .setIcon(R.drawable.ic_action_about)
                .setTitle(R.string.title_signature_dialog)

                .setView(mFingerPaintView)
                .setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {

                            }
                        }
                )

                .setNegativeButton(android.R.string.cancel,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {

                            }
                        }
                )

                .create();
    }
}
