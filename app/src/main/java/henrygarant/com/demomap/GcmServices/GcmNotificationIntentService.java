package henrygarant.com.demomap.GcmServices;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import henrygarant.com.demomap.Config;
import henrygarant.com.demomap.MainActivity;
import henrygarant.com.demomap.MapActivities.WaitingPage;
import henrygarant.com.demomap.MapsActivity;
import henrygarant.com.demomap.R;

public class GcmNotificationIntentService extends IntentService {

    public static final int NOTIFICATION_ID = 1;
    public static final String TAG = "GCMNotificationIntentService";
    NotificationCompat.Builder builder;
    private NotificationManager mNotificationManager;
    private String sender;

    public GcmNotificationIntentService() {
        super("GcmIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);

        String messageType = gcm.getMessageType(intent);

        if (!extras.isEmpty()) {
            if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR
                    .equals(messageType)) {
                Log.d("Send error: ", extras.toString());
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED
                    .equals(messageType)) {
                Log.d("Deleted messages on server: ", extras.toString());
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE
                    .equals(messageType)) {

                for (int i = 0; i < 3; i++) {
                    Log.i(TAG,
                            "Working... " + (i + 1) + "/5 @ "
                                    + SystemClock.elapsedRealtime());
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                    }
                }
                Log.i(TAG, "Completed work @ " + SystemClock.elapsedRealtime());
                if (extras.get(Config.MESSAGE_KEY) == null) {
                    //GCM ACCEPT REQUEST
                    if (extras.get(Config.ACCEPT_START_KEY).toString().equals("1") && extras.get(Config.ACCEPT_END_KEY).toString().equals("0")) {
                        Log.d("NOTIFICATIONINTENTSERVICE: ", extras.toString());
                        sender = extras.get("sender").toString();
                        //MapsActivity.sender = sender;
                        //MapsActivity.phoneTo = extras.get("phonefrom").toString();
                        sendNotification("Ride Request From " + sender, extras.get("phonefrom").toString());
                    }
                    //GCM CANCEL REQUEST
                    else if (extras.get(Config.ACCEPT_START_KEY).toString().equals("0") && extras.get(Config.ACCEPT_END_KEY).toString().equals("1")) {
                        Log.d("CANCEL REQUEST:", "canceling");
                        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                        am.cancel(MapsActivity.getAlarmPendingIntent());
                        startActivity(new Intent(this, MainActivity.class));
                        Toast.makeText(this, extras.get(Config.SENDER_KEY).toString() + " canceled connection.", Toast.LENGTH_LONG).show();
                    } else {
                        Log.d("NOTIFICATIONINTENTSERVICE: ", "Error parsing gcm message");
                    }
                }else{
                    //GCM LOCATION DATA
                    Intent location_intent = new Intent();
                    Log.d("GCM LOCTION UPDATE: ", extras.toString());

                    //the other persons location
                    location_intent.putExtra("target", extras.get(Config.MESSAGE_KEY).toString());
                    //phone number of whom sent the location
                    location_intent.putExtra("phonefrom", extras.get(Config.PHONEFROM_KEY).toString());
                    //name of whom sent the message
                    location_intent.putExtra("sender", extras.get(Config.SENDER_KEY).toString());

                    location_intent.setAction(MapsActivity.MAP_BROADCAST);
                    Log.d("GCM LOCTION UPDATE: ", "sent broadcast.");
                    sendBroadcast(location_intent);
                }
                Log.i(TAG, "Received: " + extras.toString());
            }
        }
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    private void sendNotification(String msg, String phonefrom) {
        Log.d(TAG, "Preparing to send notification...: " + msg);
        mNotificationManager = (NotificationManager) this
                .getSystemService(Context.NOTIFICATION_SERVICE);

        Intent intent = new Intent(this, WaitingPage.class);

        intent.putExtra("sender", sender);
        intent.putExtra("phoneto", phonefrom);
        intent.putExtra("fromaccept", true);

        //Class to open when user clicks notification
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                intent, PendingIntent.FLAG_CANCEL_CURRENT);


        //TODO ADD CONFIRM DIALOG IN NOTIFICATION

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                this).setSmallIcon(R.drawable.notification_icon_small)
                .setContentTitle("Be There In 5")
                .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                .setContentText(msg);

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
        Log.d(TAG, "Notification sent successfully.");
    }
}
