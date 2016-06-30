package henrygarant.com.demomap;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;

import java.util.Calendar;

import henrygarant.com.demomap.MapActivities.MyLocationService;

public class MapsActivity extends FragmentActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private final int MILE_RADIUS = 1;
    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private int CIRCLE_COLOR =  Color.argb(100, 30, 136, 229);
    private String destination;
    private TextView target;
    private GoogleApiClient mGoogleApiClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        target = (TextView) findViewById(R.id.target);

        Intent intent = getIntent();

        CIRCLE_COLOR = getResources().getColor(R.color.Map_Color);


        destination = intent.getStringExtra("destination");
        //DestinationManager destinationManager = new DestinationManager();
        //destinationLatLng = destinationManager.makeDestinationLatLng(this, destination);

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();

        }


        try {

            //TODO SEND THIS LOCATION NOT MAKE IT TARGET
            //updateUI();
            setUpMapIfNeeded();

            Intent newIntent = new Intent(this, MyLocationService.class);
            PendingIntent pi = PendingIntent.getService(this, 0, newIntent, 0);
            AlarmManager alarm_manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            alarm_manager.setRepeating(AlarmManager.RTC, Calendar.getInstance().getTimeInMillis(), 60000, pi);


        } catch (Exception e) {
            Log.e("MapsActivity Exception", e.toString());
        }


    }


    @Override
    public void onLocationChanged(Location location) {
        Log.d("MAPSACTIVITY: ", "Location Changed");
        updateUI();
    }

    private void updateUI() {
        target.setText("Target: " + destination);
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

        /*mMap.addMarker(new MarkerOptions()
                .position(destinationLatLng)
                .snippet("My Location")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                .title("Me"));

        // Instantiates a new CircleOptions object and defines the center and radius
        CircleOptions circleOptions = new CircleOptions()
                .center(destinationLatLng)
                .fillColor(CIRCLE_COLOR)
                .strokeColor(CIRCLE_COLOR)
                .radius(1609 * MILE_RADIUS);  //convert miles to meters

        mMap.addCircle(circleOptions);

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                destinationLatLng, 13));
                */
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
    }


}
