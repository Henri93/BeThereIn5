package henrygarant.com.demomap;


import android.app.ActionBar;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.View;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;

public class MainActivity extends FragmentActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);


        if (Build.VERSION.SDK_INT >= 21) {
            ActionBar actionBar = getActionBar();
            actionBar.setStackedBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.MidLight)));
            actionBar.setHomeButtonEnabled(false);
        }else{
           //android.support.v7.app.ActionBar actionBar2 = getSupportActionBar();
           // actionBar2.setStackedBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.MidLight)));
            //actionBar2.setHomeButtonEnabled(false);
            //No Tabs for < 21
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        return true;
    }

}
