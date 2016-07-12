package henrygarant.com.demomap.MapActivities;

import android.app.Service;
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
        //TODO GET NUMBER
        if (MapsActivity.phoneTo == null || MapsActivity.phoneTo.equals("")) {
            sender.sendGcmMessage("(215) 331-7408", myLatLng.toString());
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
}
