package com.mridang.betamax.activities;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

import com.mridang.betamax.R;

/*
 * This class is the activity which contains the preferences
 */
@SuppressWarnings("deprecation")
public class WidgetSettings extends PreferenceActivity {

    /*
     * @see android.preference.PreferenceActivity#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        getActionBar().setIcon(R.drawable.ic_dashclock);
        getActionBar().setDisplayHomeAsUpEnabled(true);

    }

    /*
     * A preference value change listener that updates the preference's summary 
     * to reflect its new value.
     */
    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {

        @Override
        public boolean onPreferenceChange(Preference prePreference, Object objValue) {

            if (prePreference instanceof ListPreference) {

            	ListPreference listPreference = (ListPreference) prePreference;
                int index = listPreference.findIndexOfValue(objValue.toString());
                prePreference.setSummary(index >= 0 ? listPreference.getEntries()[index] : null);

            } else {

            	prePreference.setSummary(objValue.toString());

            }

            return true;

        }

    };

    /*
     * Binds a preference's summary to its value. More specifically, when the 
     * preference's value is changed, its summary is updated to reflect the value.
     */
    private static void bindPreferenceSummaryToValue(Preference prePreference) {

        prePreference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        sBindPreferenceSummaryToValueListener.onPreferenceChange(prePreference,
                PreferenceManager
                .getDefaultSharedPreferences(prePreference.getContext())
                .getString(prePreference.getKey(), ""));

    }

    /*
     * @see android.app.Activity#onPostCreate(android.os.Bundle)
     */
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {

        super.onPostCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        bindPreferenceSummaryToValue(findPreference("username"));
        bindPreferenceSummaryToValue(findPreference("password"));
        bindPreferenceSummaryToValue(findPreference("provider"));

    }

}