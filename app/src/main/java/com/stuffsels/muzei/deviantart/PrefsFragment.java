package com.stuffsels.muzei.deviantart;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import com.stuffsels.muzei.deviantart.helpers.PreferenceHelper;

public class PrefsFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PreferenceManager.setDefaultValues(getActivity(),
                R.xml.preferences, false);

        addPreferencesFromResource(R.xml.preferences);

        PreferenceHelper.updateSummaries(this, PreferenceManager.getDefaultSharedPreferences(getActivity()), null);
    }
}
