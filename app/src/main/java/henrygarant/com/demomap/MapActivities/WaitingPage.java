package henrygarant.com.demomap.MapActivities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.ContextThemeWrapper;

import java.util.Timer;
import java.util.TimerTask;

import henrygarant.com.demomap.GcmServices.GcmSender;
import henrygarant.com.demomap.MainActivity;
import henrygarant.com.demomap.MapsActivity;
import henrygarant.com.demomap.R;
import henrygarant.com.demomap.SQLiteHandler;

public class WaitingPage extends FragmentActivity {

    private ProgressDialog pDialog;
    private String number;
    private String sender;
    private boolean fromAccept;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.waiting_page);

        Intent intent = getIntent();
        number = intent.getStringExtra("phoneto");
        sender = intent.getStringExtra("sender");
        fromAccept = intent.getBooleanExtra("fromaccept", false);


        if (sender == null || sender.equals("")) {
            sender = "Unknown";
        }

        if (fromAccept && (number != null && !number.equals(""))) {
            //Came from notification
            new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.ProcessDialog))
                    .setTitle("Be There In 5")
                    .setMessage("Are you sure you want to ride with " + sender + "?")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Intent myIntent = new Intent(getBaseContext(), MapsActivity.class);

                            //phone number of whom sent the location
                            myIntent.putExtra("phonefrom", number);

                            startActivity(myIntent);

                        }
                    })
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Intent myIntent = new Intent(getBaseContext(), MainActivity.class);
                            myIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(myIntent);

                        }
                    })
                    .setIcon(R.mipmap.icon)
                    .show();
        } else {
            //Came from user click aka ContactAdapter class or SenderActivity
            sendGCMAccept(number);
            Intent myIntent = new Intent(getBaseContext(), MapsActivity.class);
            myIntent.putExtra("phonefrom", number);
            startActivity(myIntent);
        }
    }



    private void sendGCMAccept(final String number) {
        // Progress dialog
        pDialog = new ProgressDialog(this, R.style.ProcessDialog);
        //set cancelable in some other manner
        pDialog.setCancelable(false);
        pDialog.setMessage("Sending Request...");
        showDialog();

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        pDialog.setMessage("Waiting for Acceptance...");
                        GcmSender gcmSender = new GcmSender(getApplicationContext());
                        SQLiteHandler db = new SQLiteHandler(getApplicationContext());
                        Log.d("USER: ", db.getUserDetails().toString());
                        gcmSender.sendGcmAccept(db.getUserDetails().get("phone"), number, "1", "0");
                    }
                });
            }
        }, 4 * 1000);
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }
}
