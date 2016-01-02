package henrygarant.com.demomap;


import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import com.github.nkzawa.socketio.client.IO;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends FragmentActivity{

    private Button searchButton;
    private AutoCompleteTextView addressBar;
    private ArrayAdapter<String> adapter;
    private HashMap<String, String> contacts;

    private static final String SERVER_ADDRESS = "http://192.168.1.4:3000";
    private com.github.nkzawa.socketio.client.Socket mSocket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        getActionBar().setHomeButtonEnabled(true);

        Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/Exo-Regular.otf");

        searchButton = (Button) findViewById(R.id.searchButton);
        addressBar = (AutoCompleteTextView) findViewById(R.id.addressBar);

        contacts = new HashMap<String, String>();

        populateContacts(this.getContentResolver());

        addressBar.setThreshold(2);

        adapter = new ContactAdapter(this, new ArrayList<String>(contacts.values()));
        addressBar.setAdapter(adapter);

        searchButton.setTypeface(tf, Typeface.BOLD);
        addressBar.setTypeface(tf);


        //temp code
        try {
            mSocket = IO.socket(SERVER_ADDRESS);
            mSocket.connect();
            mSocket.emit("message", "Android Says Hello");
        } catch (URISyntaxException e) {
            Log.d("ERROR", "Unable to Connect to Socket");
        }


    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mSocket.disconnect();
    }


    public void calculateLocation(View view)
    {
        new AlertDialog.Builder(this)
                .setTitle("Set Notification")
                .setMessage("Do you want to be reminded when they are within 5 minutes?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int whichButton){
                        //TODO SET REMINDER HERE

                        String address = addressBar.getText().toString().trim();
                        moveToActivity(MapsActivity.class, address);
                    }})
                .setNegativeButton(android.R.string.no, null).show();
    }

    private void moveToActivity(Class newClass, String destination){
        Intent intent = new Intent(this, newClass);
        intent.putExtra("destination", destination);
        startActivity(intent);
    }

    public void populateContacts(ContentResolver cr) {
        Cursor phones = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
        while (phones.moveToNext()) {
            String contact = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            contacts.put(phoneNumber, contact);
        }
    }
}
