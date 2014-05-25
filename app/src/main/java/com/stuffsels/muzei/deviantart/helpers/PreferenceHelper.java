package com.stuffsels.muzei.deviantart.helpers;

import android.content.SharedPreferences;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import com.stuffsels.muzei.deviantart.R;

public class PreferenceHelper {
    public static final String PREF_QUERY = "pref_query";
    public static final String PREF_NFSW = "pref_nfsw";
    public static final String PREF_WIFIONLY = "pref_wifionly";
    public static final String PREF_REFRESHTIME = "pref_refreshtime";
    public static final String PREF_NROFDEVIATIONS = "pref_nrofdeviations";
    public static final String PREF_MODE = "pref_mode";

    private SharedPreferences preferences;
    public PreferenceHelper(SharedPreferences prefs){
        this.preferences = prefs;
    }

    public Integer getRefreshTime() {
        return Integer.valueOf(preferences.getString(PREF_REFRESHTIME, "180"));
    }

    public String getMode() {
        return preferences.getString(PREF_MODE, "search");
    }

    public Integer getNrOfDeviations() {
        return Integer.valueOf(preferences.getString(PREF_NROFDEVIATIONS, "30"));
    }

    public Boolean getWifiOnly() {
        return preferences.getBoolean(PREF_WIFIONLY, true);
    }

    public Boolean getNfsw() {
        return preferences.getBoolean(PREF_NFSW, false);
    }

    public String getQuery() {
        return preferences.getString(PREF_QUERY, "in:photography");
    }

    public static void updateSummaries(PreferenceFragment fragment, SharedPreferences sharedPreferences, String key) {
        PreferenceHelper helper = new PreferenceHelper(sharedPreferences);
        /*

public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
    Preference pref = findPreference(key);

    if (pref instanceof ListPreference) {
        ListPreference listPref = (ListPreference) pref;
        pref.setSummary(listPref.getEntry());
    }
}
         */

        if (key == null || key.equals(PreferenceHelper.PREF_MODE)) {
            Preference pref = fragment.findPreference(PreferenceHelper.PREF_MODE);
            if (pref != null) {
                CharSequence[] modeValues = fragment.getResources().getTextArray(R.array.prefs_mode_values);
                CharSequence[] modeNames = fragment.getResources().getTextArray(R.array.prefs_mode_entries);
                String mode = helper.getMode();
                String name = mode;
                Integer index = 0;
                for(CharSequence val : modeValues) {
                    if (mode.equals(val.toString())) {
                        name = modeNames[index].toString();
                    }
                    index++;
                }
                pref.setSummary(name);
            }
        }
        if (key == null || key.equals(PreferenceHelper.PREF_REFRESHTIME)) {
            Preference pref = fragment.findPreference(PreferenceHelper.PREF_REFRESHTIME);
            if (pref != null)
                pref.setSummary(String.valueOf(helper.getRefreshTime()));
        }
        if (key == null || key.equals(PreferenceHelper.PREF_NROFDEVIATIONS)) {
            Preference pref = fragment.findPreference(PreferenceHelper.PREF_NROFDEVIATIONS);
            if (pref != null)
                pref.setSummary(String.valueOf(helper.getNrOfDeviations()));
        }
        if (key == null || key.equals(PreferenceHelper.PREF_QUERY)) {
            Preference pref = fragment.findPreference(PreferenceHelper.PREF_QUERY);
            if (pref!= null)
                pref.setSummary(helper.getQuery());
        }
    }

}
