package com.example.android.popularmovies;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;


public class PreferencesFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener{


    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
       addPreferencesFromResource(R.xml.preferences);

       //register the preference change listener
       getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

       //getting the summaries of the preferences
       setSummaries() ;
    }

    //set the summary of the getting movies preferences
    private void setSummaries() {

        //getting the preference
        Preference pref = getPreferenceScreen().findPreference(getString(R.string.sort_prefs_key)) ;

        if (pref instanceof ListPreference){
            ListPreference lPref = (ListPreference) pref ;
            String value = lPref.getValue() ;
            int prefIndex = lPref.findIndexOfValue(value);
            if (prefIndex >= 0){
                lPref.setSummary( lPref.getEntries()[prefIndex]);

            }
        }
    }

    //if the user change the preference reset the summaries to the new value
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        setSummaries();
    }


    //unregister the preference change listener
    @Override
    public void onDestroy() {
        super.onDestroy();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }
}
