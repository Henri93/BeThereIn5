package henrygarant.com.demomap.GcmServices;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import henrygarant.com.demomap.Config;
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
                sendNotification("Send error: " + extras.toString());
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED
                    .equals(messageType)) {
                sendNotification("Deleted messages on server: "
                        + extras.toString());
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
                //sendNotification(extras.get(Config.MESSAGE_KEY).toString());
                if (extras.get(Config.MESSAGE_KEY) == null) {
                    //GCM ACCEPT REQUEST
                    if (extras.get(Config.ACCEPT_START_KEY).toString().equals("1") && extras.get(Config.ACCEPT_END_KEY).toString().equals("0")) {
                        sender = extras.get("sender").toString();
                        MapsActivity.sender = sender;
                        MapsActivity.phoneTo = extras.get("phonefrom").toString();
                        sendNotification("Ride Request From " + sender);
                    } else {
                        Log.d("NOTIFICATIONINTENTSERVICE: ", "Error parsing gcm message");
                    }
                }else{
                    //GCM LOCATION DATA
                    Intent location_intent = new Intent();
                    Log.d("GCM LOCTION UPDATE: ", extras.toString());
                    location_intent.putExtra("target", extras.get(Config.MESSAGE_KEY).toString());
                    location_intent.putExtra("phonefrom", extras.get(Config.PHONEFROM_KEY).toString());
                    location_intent.putExtra("sender", extras.get("sender").toString());
                    location_intent.setAction(MapsActivity.MAP_BROADCAST);
                    Log.d("GCM LOCTION UPDATE: ", "sent broadcast.");
                    sendBroadcast(location_intent);
                }
                Log.i(TAG, "Received: " + extras.toString());
            }
        }
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    private void sendNotification(String msg) {
        Log.d(TAG, "Preparing to send notification...: " + msg);
        mNotificationManager = (NotificationManager) this
                .getSystemService(Context.NOTIFICATION_SERVICE);

        Intent intent = new Intent(this, WaitingPage.class);

        intent.putExtra("sender", sender);

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
