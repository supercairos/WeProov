package com.weproov.app.ui.fragments.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import com.weproov.app.R;

public class AboutDialogFragment extends BaseDialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View view = inflater.inflate(R.layout.fragment_about, null);

        return new AlertDialog.Builder(getActivity())
                .setIcon(R.drawable.ic_action_about)
                .setTitle(R.string.title_about_us)
                .setView(view)
                .setNeutralButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {

                            }
                        }
                )

                .create();
    }
}

