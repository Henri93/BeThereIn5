package henrygarant.com.demomap.MapActivities;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

import henrygarant.com.demomap.MainActivity;
import henrygarant.com.demomap.R;

public class DestinationManager extends BroadcastReceiver {

    private Location mLastLocation;

    @Override
    public void onReceive(Context context, Intent intent) {
        String destination = intent.getStringExtra("destination");
        Log.d("ON RECIEVE: ", "Destination: " + (destination));
        Log.d("ON RECIEVE: ", "Distance: " + isWithin5Minutes(context, destination));
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.notification_icon_small)
                        .setLargeIcon(((BitmapDrawable) context.getResources().getDrawable(R.drawable.notification_icon_large)).getBitmap())
                        .setContentTitle("Alert")
                        .setContentText("Distance: " + isWithin5Minutes(context, destination));

        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(1, mBuilder.build());

    }

    private int isWithin5Minutes(Context context, String destination) {
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

    public LatLng convertStringToLatLng(String stringToConvert) {
        int index = stringToConvert.indexOf(",");
        String lat = stringToConvert.substring((stringToConvert.indexOf("(") + 1), index).trim();
        String lng = stringToConvert.substring(index + 1, stringToConvert.indexOf(")")).trim();
        double lati = Double.parseDouble(lat);
        double lngi = Double.parseDouble(lng);
        LatLng stringToConvertLoc = new LatLng(lati, lngi);
        return stringToConvertLoc;
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

    public void checkLocationAvailable(final Context context) {
        LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
        }

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
        }

        if (!gps_enabled && !network_enabled) {
            // notify user
            AlertDialog.Builder dialog = new AlertDialog.Builder(context);
            dialog.setMessage("Please Enalbe Location Services");
            dialog.setPositiveButton("Location Settings", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    context.startActivity(myIntent);
                }
            });
            dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    Intent myIntent = new Intent(context, MainActivity.class);
                    context.startActivity(myIntent);
                }
            });
            dialog.show();
        }
    }

}
