package com.weproov.app.ui.fragments;


import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.InjectView;
import butterknife.OnLongClick;
import com.weproov.app.R;
import com.weproov.app.utils.AccountUtils;
import com.weproov.app.utils.constants.AccountConstants;

public class DashboardFragment extends BaseFragment {

	@InjectView(R.id.text_welcome)
	TextView mWelcomeText;

	private AccountManager mAccountManager;

	public DashboardFragment() {
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setCommmandListener(null);
    }

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mAccountManager = (AccountManager) getActivity().getSystemService(Context.ACCOUNT_SERVICE);
	}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dashboard, container, false);
    }

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		String firstName = "Romain";
		String lastName = "Caire";

		Account account = AccountUtils.getAccount();
		if(account != null) {
			firstName = mAccountManager.getUserData(account, AccountConstants.KEY_FIRST_NAME);
			lastName = mAccountManager.getUserData(account, AccountConstants.KEY_LAST_NAME);
		}

		mWelcomeText.setText(getString(R.string.welcome, firstName + " " + lastName));
	}

	@OnLongClick(R.id.profile_picture)
    boolean onDebugSyncClicked() {
        AccountUtils.startSync();
		return true;
    }
}
