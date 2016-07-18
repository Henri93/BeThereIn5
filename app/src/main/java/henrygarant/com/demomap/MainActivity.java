package henrygarant.com.demomap;


import android.app.ActionBar;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;

public class MainActivity extends ActionBarActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        if (Build.VERSION.SDK_INT < 21) {
            ActionBar actionBar = getActionBar();
            actionBar.setDisplayUseLogoEnabled(true);
            actionBar.setIcon(R.mipmap.icon);
            actionBar.setHomeButtonEnabled(false);
        }else{
            android.support.v7.app.ActionBar actionBar2 = getSupportActionBar();
            actionBar2.setDisplayUseLogoEnabled(true);
            actionBar2.setIcon(R.mipmap.icon);
            actionBar2.setHomeButtonEnabled(false);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        return true;
    }

}
