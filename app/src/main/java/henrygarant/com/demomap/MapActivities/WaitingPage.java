package henrygarant.com.demomap.MapActivities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;

import java.util.Timer;
import java.util.TimerTask;

import henrygarant.com.demomap.GcmServices.GcmSender;
import henrygarant.com.demomap.SQLiteHandler;

public class WaitingPage extends Activity {

    private ProgressDialog pDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final String number = getIntent().getStringExtra("phoneto");
        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(true);
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
                        //TODO CHANGE MY PHONE NUMBER AND CHECK IF THIS LINE WORKS
                        //gcmSender.sendGcmAccept(db.getUserDetails().get("phone"), number);
                        gcmSender.sendGcmAccept("(215) 331-7408", number);
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
