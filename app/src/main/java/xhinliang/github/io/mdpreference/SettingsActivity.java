package xhinliang.github.io.mdpreference;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;

public class SettingsActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return new SettingsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences preferences = getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE);
        preferences.getString("key_fff", "");
    }

}
