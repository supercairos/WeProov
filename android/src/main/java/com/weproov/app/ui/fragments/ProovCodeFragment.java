package com.weproov.app.ui.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.InjectView;
import com.squareup.otto.Subscribe;
import com.weproov.app.R;
import com.weproov.app.logic.controllers.ProovCodeTask;
import com.weproov.app.models.ProovCode;
import com.weproov.app.models.events.ProovCodeEvent;
import com.weproov.app.models.events.ProovCodeFailureEvent;
import com.weproov.app.ui.ifaces.CommandIface;

public class ProovCodeFragment extends TunnelFragment implements CommandIface.OnClickListener {

    @InjectView(R.id.content_fragment_root_view)
    LinearLayout mRootLayout;

    @InjectView(R.id.proov_code_layout)
    TextInputLayout mProovCodeLayout;

    @InjectView(R.id.proov_code)
    EditText mProovCodeEdit;

    private ProgressDialog mDialog;

    public static ProovCodeFragment newInstance() {
        return new ProovCodeFragment();
    }

    public ProovCodeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_proov_code, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mProovCodeLayout.setErrorEnabled(true);
        mProovCodeEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
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
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setCommmandListener(this);
        getPositiveButton().setText(R.string.button_start);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mDialog != null) {
            mDialog.dismiss();
        }
    }

    @Subscribe
    public void onProovCodeSucceded(ProovCodeEvent event) {
        ProovCode code = event.getProovCode();
        if(code != null) {
            Bundle b = new Bundle();
            b.putParcelable(KEY_PROOV_CODE, code);
            getTunnel().next(b);
        } else {
            onProovCodeFailed(new ProovCodeFailureEvent());
        }
    }

    @Subscribe
    public void onProovCodeFailed(ProovCodeFailureEvent event) {
        mDialog.dismiss();
        mProovCodeLayout.setError(getString(R.string.proov_code_error));
        Snackbar.make(mRootLayout, R.string.proov_code_error_snack, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void onPositiveButtonClicked(Button b) {
        String code = mProovCodeEdit.getEditableText().toString();
        ProovCodeTask.getAsync(code);
        mDialog = ProgressDialog.show(getActivity(), getString(R.string.proov_code_dialog_title), getString(R.string.proov_code_dialog_message));
    }

    @Override
    public void onNegativeButtonClicked(Button b) {

    }
}
