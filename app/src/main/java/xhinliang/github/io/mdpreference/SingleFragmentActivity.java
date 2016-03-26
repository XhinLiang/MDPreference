package xhinliang.github.io.mdpreference;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;


/**
 * Created by xhinliang on 15-8-30.
 * xhinliang@gmail.com
 * 托管单个Fragment的抽象Activity
 */
public abstract class SingleFragmentActivity extends FragmentActivity {


    protected abstract Fragment createFragment();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_fragment);
        FragmentManager manager = getFragmentManager();
        Fragment fragment = manager.findFragmentById(R.id.fragment_main);
        if (fragment == null) {
            fragment = createFragment();
            manager.beginTransaction()
                    .add(R.id.fragment_main, fragment)
                    .commit();
        }
    }

}