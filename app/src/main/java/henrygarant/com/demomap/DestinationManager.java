package henrygarant.com.demomap;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

public class DestinationManager extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        String destination = intent.getStringExtra("destination");
        Log.d("ON RECIEVE: ", "Destination: " + (destination));
        Log.d("ON RECIEVE: ", "Distance: " + isWithin5Minutes(context));
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.be_there_in_5_round)
                        .setContentTitle("Alert")
                        .setContentText("Distance: " + isWithin5Minutes(context));

        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(1, mBuilder.build());

    }

    private int isWithin5Minutes(Context context)
    {
        //TODO Check if gps is enabled
        //TODO Get Speed for S=d/time
        //LocationManager locationManager = (LocationManager)context.getSystemService(context.LOCATION_SERVICE);
        //Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        //LatLng locationLatLng = new LatLng(location.getLatitude(), location.getLongitude());
        return CalculationByDistance(makeDestinationLatLng(context, "3301 Solly Ave, Philadelphia"), makeDestinationLatLng(context, "3030 Magee Ave, Philadelphia"));

    }

    public int CalculationByDistance(LatLng StartP, LatLng EndP) {

        Location origin = new Location("Origin");
                origin.setLatitude(StartP.latitude);
                origin.setLongitude(StartP.longitude);

        Location dest = new Location("Destination");
            dest.setLatitude(EndP.latitude);
            dest.setLongitude(EndP.longitude);

        return (int)origin.distanceTo(dest);
    }

    private LatLng makeDestinationLatLng(Context context, String destination) {
        Geocoder coder = new Geocoder(context);
        List<Address> address;
        Address location = null;

        try {
            address = coder.getFromLocationName(destination, 1);
            if (address == null) {
                //TODO HANDLE NO LOCATION EVENT
                return null;
            }
            location = address.get(0);
            return new LatLng(location.getLatitude(), location.getLongitude());
        }catch (Exception e){

        }
        return null;
    }
}
