package henrygarant.com.demomap.MapActivities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
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

    private String number;
    private String sender;
    private boolean fromAccept;
    private boolean error;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.waiting_page);

        Intent intent = getIntent();
        error = intent.getBooleanExtra("error", false);
        number = intent.getStringExtra("phoneto");
        sender = intent.getStringExtra("sender");
        fromAccept = intent.getBooleanExtra("fromaccept", false);


        if (sender == null || sender.equals("")) {
            sender = "Unknown";
        }

        if (error) {
            new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.ProcessDialog))
                    .setTitle("Be There In 5")
                    .setMessage("Would you like to invite this person to join Be There In 5?")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            //send join request
                            try {
                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("sms:" + number));
                                intent.putExtra("sms_body", "You're invited to beta test a new hands free driving app, BeThereIn5. Join at betherein5.net!");
                                startActivity(intent);
                            } catch (Exception e) {
                                Intent myIntent = new Intent(getBaseContext(), MapsActivity.class);
                                startActivity(myIntent);
                            }

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
        } else if (fromAccept && (number != null && !number.equals(""))) {
            //Came from notification
            new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.ProcessDialog))
                    .setTitle("Be There In 5")
                    .setMessage("Are you sure you want to ride with " + sender + "?")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Intent myIntent = new Intent(getBaseContext(), MapsActivity.class);

                            //phone number of whom sent the location
                            myIntent.putExtra("phonefrom", number);
                            myIntent.putExtra("sender", sender);

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
            myIntent.putExtra("sender", sender);
            startActivity(myIntent);
        }
    }


    private void sendGCMAccept(final String number) {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        GcmSender gcmSender = new GcmSender(getApplicationContext());
                        SQLiteHandler db = new SQLiteHandler(getApplicationContext());
                        Log.d("USER: ", db.getUserDetails().toString());
                        gcmSender.sendGcmAccept(db.getUserDetails().get("phone"), number, "1", "0");
                    }
                });
            }
        }, 4 * 1000);
    }
}
