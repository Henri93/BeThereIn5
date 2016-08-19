package henrygarant.com.demomap.MapActivities;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import henrygarant.com.demomap.Config;
import henrygarant.com.demomap.MainActivity;
import henrygarant.com.demomap.MyNotificationManager;

public class ConnectionManager {

    private Context context;
    public static int distance = 0;
    public static String sender = "Waiting for Ride Acceptance";

    public ConnectionManager(Context context) {
        this.context = context;
    }

    public void setConnected(boolean connected) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean("isConnected", connected);
        editor.commit();
    }

    public boolean isConnected() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        return sharedPref.getBoolean("isConnected", false);
    }

    public void destroyConnection() {
        setConnected(false);
        distance = 0;
        sender = "Waiting for Ride Acceptance";

        //stop alarm
        Intent serviceIntent = new Intent(context, MyLocationService.class);
        serviceIntent.setAction(Config.ACTION_START);
        PendingIntent alarmPendingIntent = PendingIntent.getService(context, 0, serviceIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarm_manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarm_manager.cancel(alarmPendingIntent);

        //clear notification
        Log.d("CONNECTIONMANAGER: ", "clear notifiation");
        clearNotification();

        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public void clearNotification() {
        Intent serviceIntent = new Intent(context, MyNotificationManager.class);
        serviceIntent.setAction(Config.NOTIF_STOP);
        serviceIntent.putExtra("finished", true);
        context.startService(serviceIntent);
    }
}
