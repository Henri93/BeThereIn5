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
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.util.HashMap;

public class MainActivity extends FragmentActivity implements android.app.ActionBar.TabListener {

    private ViewPager viewPager;
    private PagerAdapter mAdapter;
    private android.app.ActionBar actionBar;
    private String[] tabs = { "New", "Recent" };
    //GCM VARS
    ShareExternalServer appUtil;
    String regId;
    AsyncTask shareRegidTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        //Start GCM Tinkering
        appUtil = new ShareExternalServer();
        regId = getIntent().getStringExtra("regId");
        Log.d("MainActivity", "regId: " + regId);

        final Context context = this;
        shareRegidTask = new AsyncTask() {

            @Override
            protected String doInBackground(Object[] params) {
                String result = appUtil.shareRegIdWithAppServer(context, regId);
                return result;
            }

            protected void onPostExecute(String result) {
                shareRegidTask = null;
                Toast.makeText(getApplicationContext(), result,
                        Toast.LENGTH_LONG).show();
            }

        };
        shareRegidTask.execute(null, null, null);

        //End GCM Tinkering

        Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/Exo-Regular.otf");


        // Initilization
        viewPager = (ViewPager) findViewById(R.id.pager);
        actionBar = getActionBar();
        actionBar.setStackedBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.BlackGloss)));
        mAdapter = new PagerAdapter(getSupportFragmentManager());

        viewPager.setAdapter(mAdapter);
        actionBar.setHomeButtonEnabled(false);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Adding Tabs
        for (String tab_name : tabs) {
            actionBar.addTab(actionBar.newTab().setText(tab_name)
                    .setTabListener(this));
        }
    }


    public void calculateLocation(View view)
    {
        new AlertDialog.Builder(this)
                .setTitle("Set Notification")
                .setMessage("Do you want to be reminded when you are within 5 minutes?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int whichButton){
                        //TODO CLEAR ANY POSSIBLE ALARMS
                         //setPersistentCheck(addressBar.getText().toString());
                         //moveToActivity(MapsActivity.class, addressBar.getText().toString());

                    }})
                .setNegativeButton(android.R.string.no, null).show();
    }

    private void setPersistentCheck(String destination) {
        AlarmManager alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, DestinationManager.class);
        intent.putExtra("destination", destination);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
        //60000 is one minute
        //6000 for testing purposes
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 6000, pendingIntent);
    }

    private void moveToActivity(Class newClass, String destination){
        Intent intent = new Intent(this, newClass);
        intent.putExtra("destination", destination);
        startActivity(intent);
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
    public void onTabSelected(android.app.ActionBar.Tab tab, android.app.FragmentTransaction ft) {
        viewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(android.app.ActionBar.Tab tab, android.app.FragmentTransaction ft) {

    }

    @Override
    public void onTabReselected(android.app.ActionBar.Tab tab, android.app.FragmentTransaction ft) {

    }
}
