package amirz.globalsqueeze.settings;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.widget.Toast;

import amirz.globalsqueeze.MonitorActivity;
import amirz.globalsqueeze.R;
import amirz.globalsqueeze.SqueezeService;
import amirz.globalsqueeze.Utilities;
import amirz.library.settings.Tunable;

public class SettingsActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SqueezeService.startForeground(this);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .replace(android.R.id.content, new SettingsFragment())
                    .commit();
        }
    }

    public static class SettingsFragment extends PreferenceFragment
            implements Preference.OnPreferenceChangeListener {
        private Activity mContext;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            mContext = getActivity();

            getPreferenceManager().setSharedPreferencesName(mContext.getPackageName());
            addPreferencesFromResource(R.xml.preferences);

            findPreference(getString(R.string.pref_monitor_fft))
                    .setOnPreferenceClickListener(new OnFFTPreference());

            findPreference(getString(R.string.pref_reset_prefs))
                    .setOnPreferenceClickListener(new OnResetPreferences());
        }

        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            return true;
        }

        public class OnFFTPreference implements Preference.OnPreferenceClickListener {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                startActivity(new Intent(mContext, MonitorActivity.class));
                return true;
            }
        }

        public class OnResetPreferences implements Preference.OnPreferenceClickListener {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                SharedPreferences prefs = Utilities.prefs(mContext);
                SharedPreferences.Editor editor = prefs.edit();
                editor.clear();
                editor.apply();

                Tunable.applyAll(prefs, getResources());

                Toast.makeText(mContext, R.string.on_reset_pref, Toast.LENGTH_SHORT).show();

                mContext.finish();
                startActivity(mContext.getIntent());

                return true;
            }
        }
    }
}
