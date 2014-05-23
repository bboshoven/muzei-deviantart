package com.stuffsels.muzei.deviantart;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.*;
import android.os.Build;
import com.stuffsels.muzei.deviantart.helpers.PreferenceHelper;

import java.util.List;
import java.util.prefs.PreferenceChangeListener;

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
        PreferenceHelper.updateSummaries(fragment, sharedPreferences, key);
        if (key.equals(PreferenceHelper.PREF_REFRESHTIME) || key.equals(PreferenceHelper.PREF_QUERY)) {
            startService(new Intent(SettingsActivity.this, DeviantARTSource.class).setAction(DeviantARTSource.ACTION_FORCE_UPDATE));
        }
    }

}
