package org.abondar.experimental.sunshine;


import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.*;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import org.abondar.experimental.sunshine.data.WeatherContract;
import org.abondar.experimental.sunshine.sync.SunshineSyncAdapter;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
    }

    @Override
    public Intent getParentActivityIntent() {
        return super.getParentActivityIntent().addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    }

    public static class SunshinePreferencesFragment extends PreferenceFragment
            implements Preference.OnPreferenceChangeListener, SharedPreferences.OnSharedPreferenceChangeListener {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings_main);

            Preference location = findPreference(getString(R.string.settings_location_key));
            bindPrefSummaryToVal(location);

            Preference units = findPreference(getString(R.string.settings_units_key));
            bindPrefSummaryToVal(units);

        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if (key.equals("location")) {
                Utility.resetLocationStatus(getActivity());
                SunshineSyncAdapter.syncImmediately(getActivity());
            } else if (key.equals("units")) {
                getActivity().getContentResolver().notifyChange(WeatherContract.WeatherEntry.CONTENT_URI, null);
            }
        }

        @Override
        public void onResume() {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
            preferences.registerOnSharedPreferenceChangeListener(this);
            super.onPause();
        }

        @Override
        public void onPause() {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
            preferences.unregisterOnSharedPreferenceChangeListener(this);
            super.onPause();
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

        private void bindPrefSummaryToVal(Preference preference) {
            preference.setOnPreferenceChangeListener(this);
            setPreferenceSummary(preference,PreferenceManager
            .getDefaultSharedPreferences(preference.getContext())
            .getString(preference.getKey(),""));

        }

        private void setPreferenceSummary(Preference preference, Object value) {
            String stringValue = value.toString();
            String key = preference.getKey();

            if (preference instanceof ListPreference) {
                ListPreference listPreference = (ListPreference) preference;
                int prefIndex = listPreference.findIndexOfValue(stringValue);
                if (prefIndex >= 0) {
                    preference.setSummary(listPreference.getEntries()[prefIndex]);
                }
            } else if (key.equals(getString(R.string.settings_location_key))) {
                @SunshineSyncAdapter.LocationStatus int status = Utility.getLocationStatus(getContext());
                switch (status) {
                    case SunshineSyncAdapter.LOCATION_STATUS_OK:
                        preference.setSummary(stringValue);
                        break;
                    case SunshineSyncAdapter.LOCATION_STATUS_UNKNOWN:
                        preference.setSummary(getString(R.string.settings_location_unknown_description, value.toString()));
                        break;
                    case SunshineSyncAdapter.LOCATION_STATUS_INVALID:
                        preference.setSummary(getString(R.string.settings_location_error_description, value.toString()));
                        break;
                    default:
                        preference.setSummary(stringValue);
                }
            } else {
                preference.setSummary(stringValue);
            }

        }
    }
}

