package com.timen4.ronnny.timemovies;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

/**
 * Created by :luore
 * Date: 2017/2/16
 */
public class SettingActivity extends PreferenceActivity implements Preference.OnPreferenceChangeListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Add 'general' preferences,defined in the XML file
        addPreferencesFromResource(R.xml.pref_general);

        //For all preferences,attach an OnPreferenceChangeListener so the UI summary
        //can be updated when preference changes.
        bindPreferenceSummaryToBalue(findPreference(getString(R.string.pre_sort_key)));
    }

    private void bindPreferenceSummaryToBalue(Preference preference){
        //set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(this);

        //Trigger the listener immediately with the preference's
        //current value
        onPreferenceChange(preference,
                PreferenceManager
                .getDefaultSharedPreferences(preference.getContext())
                .getString(preference.getKey(),""));

    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object value) {
        String stringValue = value.toString();

        if (preference instanceof ListPreference){
            //For list preferences,look up the correct display value in
            //the preference's 'entries' list (since they have separate label/values)
            ListPreference listPreference= (ListPreference) preference;
            int prefIndex = listPreference.findIndexOfValue(stringValue);
            if (prefIndex>=0){
                preference.setSummary(listPreference.getEntries()[prefIndex]);
            }
        }else{
            //For other preferences,set the summary to the value's simple string representation
            preference.setSummary(stringValue);
        }
        return true;
    }
}
