package xhinliang.github.io.mdpreference;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceScreen;
import android.view.View;

import io.github.xhinliang.mdpreference.ListPreference;
import io.github.xhinliang.mdpreference.PreferenceFragment;


/**
 * Created by xhinliang on 16-2-3.
 * xhinliang@gmail.com
 */
public class SettingsFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getPreferenceManager().setSharedPreferencesName(getString(R.string.app_name));
        addPreferencesFromResource(R.xml.preference_settings);
    }

}