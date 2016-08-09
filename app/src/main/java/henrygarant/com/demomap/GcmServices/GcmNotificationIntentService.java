package henrygarant.com.demomap.GcmServices;

import android.app.ActivityManager;
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

import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.util.List;

import henrygarant.com.demomap.Config;
import henrygarant.com.demomap.MainActivity;
import henrygarant.com.demomap.MapActivities.MyLocationService;
import henrygarant.com.demomap.MapActivities.WaitingPage;
import henrygarant.com.demomap.MapsActivity;
import henrygarant.com.demomap.MyNotificationManager;

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
        final Bundle extras = intent.getExtras();
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
                }
                Log.i(TAG, "Completed work @ " + SystemClock.elapsedRealtime());
                if (extras.get(Config.ERROR_KEY) != null) {
                    //GCM ERROR SENDING
                    Log.d("GCM NOTIF INTENT: ", "ERROR");
                    stopUpdate();
                    Intent myIntent = new Intent(this, WaitingPage.class);
                    myIntent.putExtra("error", true);
                    myIntent.putExtra("phoneto", extras.get("phoneto").toString());
                    startActivity(myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                } else if (extras.get(Config.MESSAGE_KEY) == null) {
                    //GCM ACCEPT REQUEST
                    if (extras.get(Config.ACCEPT_START_KEY).toString().equals("1") && extras.get(Config.ACCEPT_END_KEY).toString().equals("0")) {
                        sender = extras.get("sender").toString();

                        Intent serviceIntent = new Intent(this, MyNotificationManager.class);
                        serviceIntent.setAction(Config.NOTIF_ACCEPT);
                        serviceIntent.putExtra("sender", sender);
                        serviceIntent.putExtra("phoneto", extras.get("phonefrom").toString());
                        startService(serviceIntent);
                    }
                    //GCM CANCEL REQUEST
                    else if (extras.get(Config.ACCEPT_START_KEY).toString().equals("0") && extras.get(Config.ACCEPT_END_KEY).toString().equals("1")) {
                        Log.d("CANCEL REQUEST:", "canceling");
                        sender = extras.get("sender").toString();
                        stopUpdate();
                        MapsActivity.updateUI("Connection ended by " + sender, 0);
                        //TODO UPDATE NOTIFICATION
                        if (isAppOnForeground(this)) {
                            startActivity(new Intent(this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                        }
                    } else {
                        Log.d("NOTIFICATIONINTENTSERVICE: ", "Error parsing gcm message");
                    }
                } else {
                    //GCM MESSAGE LOCATION DATA
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


    private void stopUpdate() {
        Intent serviceIntent = new Intent(this, MyLocationService.class);
        serviceIntent.setAction(Config.ACTION_START);

        PendingIntent alarmPendingIntent = PendingIntent.getService(this, 0, serviceIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarm_manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarm_manager.cancel(alarmPendingIntent);
    }

    private boolean isAppOnForeground(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        if (appProcesses == null) {
            return false;
        }
        final String packageName = context.getPackageName();
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND && appProcess.processName.equals(packageName)) {
                return true;
            }
        }
        return false;
    }

}
