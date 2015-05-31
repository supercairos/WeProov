package com.weproov.app.ui.fragments;


import android.os.Bundle;
import android.preference.PreferenceFragment;
import com.weproov.app.R;

public class SettingsFragment extends PreferenceFragment {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Load the preferences from an XML resource
		addPreferencesFromResource(R.xml.sync_preferences);
	}
}
