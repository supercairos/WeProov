package com.weproov.app.ui.ifaces;

import android.widget.Button;

public interface CommandIface {

	interface OnClickListener {
		void onPositiveButtonClicked(Button b);
		void onNegativeButtonClicked(Button b);
	}

	void setCommandListener(CommandIface.OnClickListener listener);

	Button getNegativeButton();
	Button getPositiveButton();
}
