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

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends FragmentActivity implements android.app.ActionBar.TabListener {

    private ViewPager viewPager;
    private PagerAdapter mAdapter;
    private android.app.ActionBar actionBar;
    private String[] tabs = { "New", "Recent" };
    //GCM VARS

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        //Start GCM Tinkering

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

    public void sendGcmMessage(View v){
        Toast.makeText(getApplicationContext(), "Send Message", Toast.LENGTH_SHORT).show();
        //TODO Connect to Message Page and Push the information into form and send
        sendGcmMessage();
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

    private void sendGcmMessage() {
        new AsyncTask() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try {
                    Bundle data = new Bundle();
                    data.putString("my_message", "Hello World");
                    data.putString("my_action","SAY_HELLO");
                    String id = Integer.toString(msgId.incrementAndGet());
                    gcm.send(SENDER_ID + "@gcm.googleapis.com", id, data);
                    msg = "Sent message";
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                mDisplay.append(msg + "\n");
            }
        }.execute(null, null, null);
    }
}
