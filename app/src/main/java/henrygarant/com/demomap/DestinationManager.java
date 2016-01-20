package henrygarant.com.demomap;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

public class DestinationManager extends BroadcastReceiver implements LocationListener {

    private Location mLastLocation;

    @Override
    public void onReceive(Context context, Intent intent) {
        String destination = intent.getStringExtra("destination");
        Log.d("ON RECIEVE: ", "Destination: " + (destination));
        Log.d("ON RECIEVE: ", "Distance: " + isWithin5Minutes(context, destination));
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.notification_icon))
                        .setContentTitle("Alert")
                        .setContentText("Distance: " + isWithin5Minutes(context, destination));

        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(1, mBuilder.build());

    }

    private int isWithin5Minutes(Context context, String destination) {
        //TODO Check if gps is enabled
        //TODO Get Speed for S=d/time
        //null object here if location is off
        Location mLocation = getLocation(context);
        Log.d("DESTINATION MANAGER: ", "Current LL: " + mLocation.toString());
        return CalculationByDistance(new LatLng(mLocation.getLatitude(), mLocation.getLongitude()), makeDestinationLatLng(context, destination));

    }

    public int CalculationByDistance(LatLng StartP, LatLng EndP) {

        Location origin = new Location("Origin");
        origin.setLatitude(StartP.latitude);
        origin.setLongitude(StartP.longitude);

        Location dest = new Location("Destination");
        dest.setLatitude(EndP.latitude);
        dest.setLongitude(EndP.longitude);

        //returns distance in meters
        return (int) origin.distanceTo(dest);
    }

    public LatLng makeDestinationLatLng(Context context, String destination) {
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
        } catch (Exception e) {

        }
        return null;
    }


    public Location getLocation(Context context) {
        int MIN_TIME_BW_UPDATES = 6000;
        int MIN_DISTANCE_CHANGE_FOR_UPDATES = 100;
        try {
            LocationManager locationManager = (LocationManager) context
                    .getSystemService(context.LOCATION_SERVICE);

            // Getting GPS status
            boolean isGPSEnabled = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);

            // Getting network status
            boolean isNetworkEnabled = locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled && !isNetworkEnabled) {
                // No network provider is enabled
            } else {
                if (isNetworkEnabled) {
                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    Log.d("Network", "Network");
                    if (locationManager != null) {
                        mLastLocation = locationManager
                                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (mLastLocation != null) {
                            double latitude = mLastLocation.getLatitude();
                            double longitude = mLastLocation.getLongitude();
                        }
                    }
                }
                // If GPS enabled, get latitude/longitude using GPS Services
                if (isGPSEnabled) {
                    if (mLastLocation == null) {
                        locationManager.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                        Log.d("GPS Enabled", "GPS Enabled");
                        if (locationManager != null) {
                            mLastLocation = locationManager
                                    .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (mLastLocation != null) {
                                double latitude = mLastLocation.getLatitude();
                                double longitude = mLastLocation.getLongitude();
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return mLastLocation;
    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
