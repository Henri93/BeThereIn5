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
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.View;

import java.util.HashMap;

public class MainActivity extends FragmentActivity implements android.app.ActionBar.TabListener {

    private ViewPager viewPager;
    private PagerAdapter mAdapter;
    private String[] tabs = {"Contacts", "New"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);


        Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/Exo-Regular.otf");


        // Initilization
        viewPager = (ViewPager) findViewById(R.id.pager);

        if (Build.VERSION.SDK_INT >= 21) {
            ActionBar actionBar = getActionBar();
            actionBar.setStackedBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.MidLight)));
            actionBar.setHomeButtonEnabled(false);
            actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
            // Adding Tabs
            for (String tab_name : tabs) {
                actionBar.addTab(actionBar.newTab().setText(tab_name)
                        .setTabListener(this));
            }
        }else{
           //android.support.v7.app.ActionBar actionBar2 = getSupportActionBar();
           // actionBar2.setStackedBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.MidLight)));
            //actionBar2.setHomeButtonEnabled(false);
            //No Tabs for < 21
        }
        mAdapter = new PagerAdapter(getSupportFragmentManager());

        viewPager.setAdapter(mAdapter);


    }


    @Override
    public void onTabSelected(android.app.ActionBar.Tab tab, android.app.FragmentTransaction ft) {
        viewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(android.app.ActionBar.Tab tab, android.app.FragmentTransaction ft) {

    }

    @Override
    public void onTabReselected(android.app.ActionBar.Tab tab, android.app.FragmentTransaction ft) {

    }


    /**
     * Returns a HashMap with all user's contacts
     *
     * */
    public HashMap<String, String> populateContacts(ContentResolver cr) {
        HashMap<String, String> contacts = new HashMap<String, String>();
        Cursor phones = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
        while (phones.moveToNext()) {
            String contact = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            contacts.put(phoneNumber, contact);
        }
        return contacts;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        return true;
    }

}
