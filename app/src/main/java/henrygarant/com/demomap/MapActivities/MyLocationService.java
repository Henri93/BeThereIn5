package henrygarant.com.demomap.MapActivities;

import android.app.AlarmManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.Location;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import henrygarant.com.demomap.GcmServices.GcmSender;
import henrygarant.com.demomap.MapsActivity;
import henrygarant.com.demomap.SQLiteHandler;


public class MyLocationService extends Service {

    //TODO MAKE SURE DOESNT END

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("MYLOCATIONSERVICE", "Service started");
        Toast.makeText(this, "LOCATION SERVICE UPDATE", Toast.LENGTH_SHORT);
        DestinationManager dm = new DestinationManager();
        Location location = dm.getLocation(getApplicationContext());
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        LatLng myLatLng = new LatLng(latitude, longitude);
        //send the gcm message with location data
        GcmSender sender = new GcmSender(this);
        if (MapsActivity.phoneTo == null || MapsActivity.phoneTo.equals("")) {
            Log.d("MYLOCATIONSERVICE", "No Target!");
            Toast.makeText(getApplicationContext(), "Sorry, unable to connect at this time.", Toast.LENGTH_LONG);
            abort();
        } else {
            SQLiteHandler db = new SQLiteHandler(this);
            Log.d("SERVICE MYPHONE: ", db.getUserDetails().get("phone").toString());
            Log.d("SERVICE PHONETO: ", MapsActivity.phoneTo);
            sender.sendGcmMessage(db.getUserDetails().get("phone").toString(), MapsActivity.phoneTo, myLatLng.toString());
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
