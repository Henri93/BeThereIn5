package henrygarant.com.demomap;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Calendar;

import henrygarant.com.demomap.MapActivities.DestinationManager;
import henrygarant.com.demomap.MapActivities.MyLocationService;

public class MapsActivity extends ActionBarActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    public static String MAP_BROADCAST = "henryrgarant.com.demomap.MAP_UPDATE";
    public static String phoneTo;
    public static String sender;
    private static PendingIntent alarmPendingIntent;
    private final int MILE_RADIUS = 1;
    private String updatedLocation;
    private Intent serviceIntent;
    private GoogleMap mMap;
    private int CIRCLE_COLOR =  Color.argb(100, 30, 136, 229);
    private String destination;
    private LatLng myLocation;
    private TextView targetName;
    private TextView targetLoc;
    private TextView distanceTextView;
    private GoogleApiClient mGoogleApiClient;
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("MAPSACTIVITY: ", "Location Broadcast Received");
            updatedLocation = intent.getStringExtra("target");
            //here the target becomes the person sending the data
            phoneTo = intent.getStringExtra("phonefrom");
            sender = intent.getStringExtra("sender");
            DestinationManager dm = new DestinationManager();
            updateUI(sender, updatedLocation, dm.CalculationByDistance(myLocation, dm.convertStringToLatLng(updatedLocation)));
            updateMap(updatedLocation);
        }
    };

    public static PendingIntent getAlarmPendingIntent() {
        return alarmPendingIntent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
//        getActionBar().setDisplayHomeAsUpEnabled(true);

        //make sure location is enabled
        checkLocationAvailable();

        Intent intent = getIntent();

        phoneTo = intent.getStringExtra("phonefrom");

        targetName = (TextView) findViewById(R.id.targetNameTextView);
        targetLoc = (TextView) findViewById(R.id.targetLocTextView);
        distanceTextView = (TextView) findViewById(R.id.distanceTextView);

        updateUI("Loading...", "Connecting...", 0);

        CIRCLE_COLOR = getResources().getColor(R.color.Map_Color);


        destination = intent.getStringExtra("destination");
        // destinationManager = new DestinationManager();
        //destinationLatLng = destinationManager.makeDestinationLatLng(this, destination);

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }


        try {
            setUpMapIfNeeded();

            serviceIntent = new Intent(this, MyLocationService.class);
            alarmPendingIntent = PendingIntent.getService(this, 0, serviceIntent, 0);
            AlarmManager alarm_manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            alarm_manager.setRepeating(AlarmManager.RTC, Calendar.getInstance().getTimeInMillis(), 60000, alarmPendingIntent);

        } catch (Exception e) {
            Log.e("MapsActivity Exception", e.toString());
        }


    }

    @Override
    public void onLocationChanged(Location location) {
        Toast.makeText(MapsActivity.this, "LOCATION CHANGED", Toast.LENGTH_SHORT).show();
    }

    private void updateUI(String name, String myLocation, int distance) {
        targetName.setText(name);
        targetLoc.setText(myLocation);
        distanceTextView.setText("Distance: " + distance + " m");
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera.
     *
     */
    private void setUpMap() {

        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        mMap.setMyLocationEnabled(true);

        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        myLocation = new LatLng(location.getLatitude(), location.getLongitude());
        Toast.makeText(MapsActivity.this, "LOCATION: " + myLocation.toString(), Toast.LENGTH_SHORT).show();
        updateMap(myLocation.toString());
    }

    private void updateMap(String target) {

        if (mMap != null) {
            mMap.clear();
            mMap.addMarker(new MarkerOptions()
                    .position(myLocation)
                    .snippet("My Location")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                    .title("Me"));

            LatLng targetLoc = new DestinationManager().convertStringToLatLng(target);

            mMap.addMarker(new MarkerOptions()
                    .position(targetLoc)
                    .snippet("Target")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                    .title("Target"));

            // Instantiates a new CircleOptions object and defines the center and radius
            CircleOptions circleOptions = new CircleOptions()
                    .center(myLocation)
                    .fillColor(CIRCLE_COLOR)
                    .strokeColor(CIRCLE_COLOR)
                    .radius(1609 * MILE_RADIUS);  //convert miles to meters

            mMap.addCircle(circleOptions);

            //TODO ONLY ZOOM FIRST TIME

            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                    myLocation, 13));
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d("Location Updates: ", "Connected");
        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    protected void startLocationUpdates() {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);//10000
        mLocationRequest.setFastestInterval(500);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();

        }

        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //stopLocationUpdates();
        unregisterReceiver(broadcastReceiver);
        //TODO save instance state from https://developer.android.com/training/location/receive-location-updates.html#save-state
    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
    }

    @Override
    public void onResume() {
        super.onResume();
        setUpMapIfNeeded();
        IntentFilter filter = new IntentFilter();
        filter.addAction(MapsActivity.MAP_BROADCAST);
        registerReceiver(broadcastReceiver, filter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(serviceIntent);
        AlarmManager alarm_manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarm_manager.cancel(alarmPendingIntent);

    }

    private void checkLocationAvailable() {
        LocationManager lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
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
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setMessage("Please Enalbe Location Services");
            dialog.setPositiveButton("Location Settings", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(myIntent);
                }
            });
            dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    Intent myIntent = new Intent(MapsActivity.this, MainActivity.class);
                    startActivity(myIntent);
                }
            });
            dialog.show();
        }
    }


}
