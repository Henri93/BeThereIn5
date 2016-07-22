package henrygarant.com.demomap.MapActivities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.provider.Settings;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

import henrygarant.com.demomap.MainActivity;

public class DestinationManager {

    private Location mLastLocation;

    public float isWithin5Minutes(Context context, LatLng destination) {
        boolean isWithin5 = false;
        //assume 15mph or 6.7056 m/s if current speed is 0
        float speed = (float) 6.7056;
        if (getSpeed(context) != 0) {
            speed = getSpeed(context);
        }
        int distance = CalculationByDistance(context, destination);
        //distance/speed to get seconds
        float time = distance / speed;
        if (time <= 60 * 5) {
            isWithin5 = true;
        }
        //return isWithin5;
        return time;
    }

    public int CalculationByDistance(Context context, LatLng EndP) {

        Location origin = new Location("Origin");
        origin.setLatitude(getLocation(context).getLatitude());
        origin.setLongitude(getLocation(context).getLongitude());

        Location dest = new Location("Destination");
        dest.setLatitude(EndP.latitude);
        dest.setLongitude(EndP.longitude);

        //returns distance in meters
        return (int) origin.distanceTo(dest);
    }

    public LatLng convertStringToLatLng(String stringToConvert) {
        try {
            int index = stringToConvert.indexOf(",");
            String lat = stringToConvert.substring((stringToConvert.indexOf("(") + 1), index).trim();
            String lng = stringToConvert.substring(index + 1, stringToConvert.indexOf(")")).trim();
            double lati = Double.parseDouble(lat);
            double lngi = Double.parseDouble(lng);
            LatLng stringToConvertLoc = new LatLng(lati, lngi);
            return stringToConvertLoc;
        } catch (Exception e) {
            return null;
        }
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

    public float getSpeed(Context context) {
        //meters per second
        //TODO test this
        return getLocation(context).getSpeed();
    }

}
