package henrygarant.com.demomap.MapActivities;

import android.app.AlarmManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.Location;
import android.location.LocationManager;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import henrygarant.com.demomap.GcmServices.GcmSender;
import henrygarant.com.demomap.MapsActivity;


public class MyLocationService extends Service {


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("MYLOCATIONSERVICE", "Service started");
        Toast.makeText(this, "LOCATION SERVICE UPDATE", Toast.LENGTH_SHORT);
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        LatLng myLatLng = new LatLng(latitude, longitude);
        //send the gcm message with location data
        GcmSender sender = new GcmSender(this);
        if (MapsActivity.phoneTo == null || MapsActivity.phoneTo.equals("")) {
            Log.d("MYLOCATIONSERVICE", "No Target!");
            Toast.makeText(getBaseContext(), "Sorry, unable to connect at this time.", Toast.LENGTH_LONG);
            abort();
        } else {
            sender.sendGcmMessage(MapsActivity.phoneTo, myLatLng.toString());
        }
        return START_NOT_STICKY;
    }

    @Override
    public boolean bindService(Intent service, ServiceConnection conn, int flags) {
        return super.bindService(service, conn, flags);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("MYLOCATIONSERVICE", "Service destroyed");
    }

    private void abort() {
        onDestroy();
        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        am.cancel(MapsActivity.getAlarmPendingIntent());
    }
}
