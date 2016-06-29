package henrygarant.com.demomap.MapActivities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.ContextThemeWrapper;

import java.util.Timer;
import java.util.TimerTask;

import henrygarant.com.demomap.GcmServices.GcmSender;
import henrygarant.com.demomap.MainActivity;
import henrygarant.com.demomap.R;
import henrygarant.com.demomap.SQLiteHandler;

public class WaitingPage extends FragmentActivity {

    private ProgressDialog pDialog;
    private String number;
    private String sender;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.waiting_page);

        Intent intent = getIntent();
        number = getIntent().getStringExtra("phoneto");
        sender = intent.getStringExtra("sender");

        if (sender == null || sender.equals("")) {
            sender = "Unknown";
        }

        if (number == null || number.equals("")) {
            //Came from notification

            //--testing--this was where contextthemewrapper is
            new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.AppTheme))
                    //TODO STYLE THIS MOTHER FUCKER
                    .setTitle("Be There In 5")
                    .setMessage("Are you sure you want to ride with " + sender + "?")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            //send to MapsActivity
                            //TODO make MapsActivity send periodic updates
                            //TODO set place to recieve location updates, which sends user to MapsActivity
                        }
                    })
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            //TODO FIX THIS EXIT it is sending the request again
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        }
                    })
                    .setIcon(R.mipmap.icon)
                    .show();
        } else {
            //Came from user click aka ContactAdapter class
            sendGCMAccept(number);
        }
    }



    private void sendGCMAccept(final String number) {
        // Progress dialog
        //TODO STYLE THIS MOFO
        pDialog = new ProgressDialog(this, R.style.ProgressDialogTheme);
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
                        gcmSender.sendGcmAccept(db.getUserDetails().get("phone"), number);
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
