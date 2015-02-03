package com.paulzin.justnote.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import com.parse.ParseUser;
import com.paulzin.justnote.R;

public class SettingsFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.fragment_preference);

        Preference logoutButton = findPreference("logout");
        logoutButton.setSummary(ParseUser.getCurrentUser().getUsername());
        logoutButton.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference arg0) {
                getActivity().setResult(Activity.RESULT_OK);
                getActivity().finish();
                return true;
            }
        });
    }
}
