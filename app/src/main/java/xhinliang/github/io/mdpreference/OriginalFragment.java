package xhinliang.github.io.mdpreference;

import android.os.Bundle;
import android.preference.PreferenceFragment;

/**
 * Created by xhinliang on 16-3-11.
 * original
 */
public class OriginalFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getPreferenceManager().setSharedPreferencesName(getString(R.string.app_name));
        addPreferencesFromResource(R.xml.preference_original);
    }
}
