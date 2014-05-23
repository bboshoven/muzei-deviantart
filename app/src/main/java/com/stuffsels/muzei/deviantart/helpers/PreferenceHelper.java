package com.stuffsels.muzei.deviantart.helpers;

import android.content.SharedPreferences;
import android.preference.Preference;
import android.preference.PreferenceFragment;

public class PreferenceHelper {
    public static final String PREF_QUERY = "pref_query";
    public static final String PREF_NFSW = "pref_nfsw";
    public static final String PREF_WIFIONLY = "pref_wifionly";
    public static final String PREF_REFRESHTIME = "pref_refreshtime";
    public static final String PREF_NROFDEVIATIONS = "pref_nrofdeviations";

    private SharedPreferences preferences;
    public PreferenceHelper(SharedPreferences prefs){
        this.preferences = prefs;
    }

    public Integer getRefreshTime() {
        return Integer.valueOf(preferences.getString(PREF_REFRESHTIME, "180"));
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
