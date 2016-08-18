package henrygarant.com.demomap.MapActivities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.Location;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import henrygarant.com.demomap.Config;
import henrygarant.com.demomap.GcmServices.GcmSender;
import henrygarant.com.demomap.MapsActivity;
import henrygarant.com.demomap.MyNotificationManager;
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

        ConnectionManager connectionManager = new ConnectionManager(this);

        phoneTo = intent.getStringExtra("phoneto");
        distance = intent.getIntExtra("distance", 666);
        sender = intent.getStringExtra("sender");
        isConnected = intent.getBooleanExtra("connected", false);

        if (intent.getAction().equals(Config.ACTION_STOP)) {
            abort();
            isConnected = false;
            connectionManager.setConnected(false);
            MapsActivity.updateUI("Connection Stopped", 0);
            //send gcm cancel
            if (phoneTo != null) {
                GcmSender gcmSender = new GcmSender(this);
                SQLiteHandler db = new SQLiteHandler(this);
                gcmSender.sendGcmAccept(db.getUserDetails().get("phone").toString(), phoneTo, "0", "1");
            }
            //remove notification
            Intent serviceIntent = new Intent(this, MyNotificationManager.class);
            serviceIntent.setAction(Config.NOTIF_STOP);
            serviceIntent.putExtra("phoneto", phoneTo);
            serviceIntent.putExtra("finished", true);
            startService(serviceIntent);
        } else {
            DestinationManager dm = new DestinationManager();
            Location location = dm.getLocation(getApplicationContext());
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            LatLng myLatLng = new LatLng(latitude, longitude);
            //send the gcm message with location data
            GcmSender gcmSender = new GcmSender(this);
            SQLiteHandler db = new SQLiteHandler(this);

            Intent serviceIntent = new Intent(this, MyNotificationManager.class);
            serviceIntent.setAction(Config.NOTIF_STICKY);
            serviceIntent.putExtra("distance", distance);
            serviceIntent.putExtra("sender", sender);
            serviceIntent.putExtra("phoneto", phoneTo);
            serviceIntent.putExtra("finished", false);
            serviceIntent.putExtra("connected", isConnected);
            startService(serviceIntent);

            if (phoneTo != null) {
                Log.d("LOCATION SERVICE: phoneTo-", phoneTo);
                gcmSender.sendGcmMessage(db.getUserDetails().get("phone").toString(), phoneTo, myLatLng.toString());
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
        Intent serviceIntent = new Intent(this, MyLocationService.class);
        serviceIntent.setAction(Config.ACTION_START);
        serviceIntent.putExtra("phoneto", phoneTo);
        serviceIntent.putExtra("distance", distance);
        serviceIntent.putExtra("sender", sender);
        serviceIntent.putExtra("connected", isConnected);

        PendingIntent alarmPendingIntent = PendingIntent.getService(this, 0, serviceIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarm_manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarm_manager.cancel(alarmPendingIntent);
    }

}
