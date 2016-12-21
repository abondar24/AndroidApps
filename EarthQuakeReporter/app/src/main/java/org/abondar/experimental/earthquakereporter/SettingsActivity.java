package org.abondar.experimental.earthquakereporter;

import android.content.SharedPreferences;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
    }

    public static class EarthquakePreferenceFragment extends PreferenceFragment
            implements Preference.OnPreferenceChangeListener {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings_main);

            Preference minMag = findPreference(getString(R.string.settings_min_magnitude_key));
            bindPrefSummaryToVal(minMag);

            Preference orderBy = findPreference(getString(R.string.settings_order_by_key));
            bindPrefSummaryToVal(orderBy);
        }

        private void bindPrefSummaryToVal(Preference preference) {
            preference.setOnPreferenceChangeListener(this);
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(preference.getContext());
            String prefString = preferences.getString(preference.getKey(), "");
            onPreferenceChange(preference, prefString);
        }


        @Override
        public boolean onPreferenceChange(Preference preference, Object val) {
            String stringVal = val.toString();

            if (preference instanceof ListPreference) {
                ListPreference listPreference = (ListPreference) preference;
                int prefIndex = listPreference.findIndexOfValue(stringVal);
                if (prefIndex >= 0) {
                    CharSequence[] labels = listPreference.getEntries();
                    preference.setSummary(labels[prefIndex]);
                }
            } else {
                preference.setSummary(stringVal);

            }

            return true;
        }
    }
}
