package com.stuffsels.muzei.deviantart;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import com.stuffsels.muzei.deviantart.helpers.PreferenceHelper;

public class SettingsActivity extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    private SharedPreferences prefs;
    private PrefsFragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.registerOnSharedPreferenceChangeListener(this);

        fragment = new PrefsFragment();


        getFragmentManager().beginTransaction().replace(android.R.id.content,
                fragment).commit();
    }

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (fragment.isAdded()) {
            PreferenceHelper.updateSummaries(fragment, sharedPreferences, key);
            if (key.equals(PreferenceHelper.PREF_REFRESHTIME) || key.equals(PreferenceHelper.PREF_QUERY) || key.equals(PreferenceHelper.PREF_MODE)) {
                startService(new Intent(SettingsActivity.this, DeviantARTSource.class).setAction(DeviantARTSource.ACTION_FORCE_UPDATE));
            }
        }
    }

}
