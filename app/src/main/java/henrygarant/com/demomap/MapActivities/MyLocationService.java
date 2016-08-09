package henrygarant.com.demomap.MapActivities;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.Location;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;

import com.google.android.gms.maps.model.LatLng;

import henrygarant.com.demomap.Config;
import henrygarant.com.demomap.GcmServices.GcmSender;
import henrygarant.com.demomap.MapsActivity;
import henrygarant.com.demomap.R;
import henrygarant.com.demomap.SQLiteHandler;


public class MyLocationService extends Service {

    private String phoneTo;
    private int distance;
    private String sender;
    private boolean isConnected;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent.getAction().equals(Config.ACTION_STOP)) {
            abort();
            AlarmManager alarm_manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            alarm_manager.cancel(PendingIntent.getService(this, 0, new Intent(this, MyLocationService.class), PendingIntent.FLAG_UPDATE_CURRENT));
            isConnected = false;
            //send gcm cancel
            GcmSender gcmSender = new GcmSender(this);
            SQLiteHandler db = new SQLiteHandler(this);
            gcmSender.sendGcmAccept(db.getUserDetails().get("phone").toString(), phoneTo, "0", "1");
        } else {

            phoneTo = intent.getStringExtra("phoneto");
            distance = intent.getIntExtra("distance", 666);
            sender = intent.getStringExtra("sender");
            isConnected = intent.getBooleanExtra("connected", false);

            DestinationManager dm = new DestinationManager();
            Location location = dm.getLocation(getApplicationContext());
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            LatLng myLatLng = new LatLng(latitude, longitude);
            //send the gcm message with location data
            GcmSender sender = new GcmSender(this);
            SQLiteHandler db = new SQLiteHandler(this);
            startNotification();
            if (phoneTo != null) {
                Log.d("LOCATION SERVICE: phoneTo-", phoneTo);
                sender.sendGcmMessage(db.getUserDetails().get("phone").toString(), phoneTo, myLatLng.toString());
            } else {
                Log.d("LOCATION SERVICE: phoneTo-", "null");
            }
        }
        return START_NOT_STICKY;
    }

    @Override
    public boolean bindService(Intent service, ServiceConnection conn, int flags) {
        return super.bindService(service, conn, flags);
    }

    @Override
    public void onDestroy() {
        abort();
    }

    private void abort() {
        stopSelf();
        stopForeground(true);
    }

    private void startNotification() {

        Intent i = new Intent(this, MapsActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pi = PendingIntent.getActivity(this, 0, i, 0);

        Intent nextIntent = new Intent(this, MyLocationService.class);
        nextIntent.setAction(Config.ACTION_STOP);
        PendingIntent stopIntent = PendingIntent.getService(this, 0,
                nextIntent, 0);

        RemoteViews views = new RemoteViews(getPackageName(),
                R.layout.notification);
        views.setOnClickPendingIntent(R.id.notif_stop, stopIntent);

        if (sender != null && !sender.equals("Unknown") && distance != 0) {
            views.setTextViewText(R.id.notif_status, "Connected");
            views.setTextViewText(R.id.notif_info, sender + " is " + distance + "m away.");
        } else {
            if (!isConnected && (sender == null || sender.equals("Unknown"))) {
                views.setTextViewText(R.id.notif_status, "Waiting for Ride Acceptance");
                views.setTextViewText(R.id.notif_info, "");
            } else {
                views.setTextViewText(R.id.notif_status, "Connection Lost");
                views.setTextViewText(R.id.notif_info, "");
            }
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(
                getApplicationContext());
        Notification note = builder.setContentIntent(pi)
                .setSmallIcon(R.drawable.notification_icon_small).setTicker("Ride Status").setWhen(System.currentTimeMillis())
                .setAutoCancel(false).setContentTitle("Be There In 5")
                .setOngoing(true)
                .setContent(views).build();

        startForeground(1337, builder.build());
    }
}
