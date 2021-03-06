package henrygarant.com.demomap;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
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

import java.util.Timer;
import java.util.TimerTask;

import henrygarant.com.demomap.GcmServices.GcmSender;
import henrygarant.com.demomap.MapActivities.ConnectionManager;
import henrygarant.com.demomap.MapActivities.DestinationManager;

public class MapsActivity extends ActionBarActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    public static String MAP_BROADCAST = "henryrgarant.com.demomap.MAP_UPDATE";
    private String phoneTo;
    private String sender;
    private int distance;
    private PendingIntent alarmPendingIntent;
    private final double MILE_RADIUS = .5;
    private String updatedLocation;
    private Intent serviceIntent;
    private GoogleMap mMap;
    private int CIRCLE_COLOR =  Color.argb(100, 30, 136, 229);
    private LatLng myLocation;
    public static TextView targetName;
    public static TextView distanceTextView;
    private GoogleApiClient mGoogleApiClient;
    private DestinationManager destinationManager;
    private ConnectionManager connectionManager = new ConnectionManager(this);

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("MAPSACTIVITY: ", "Location Broadcast Received");

            //the location of the other person
            updatedLocation = intent.getStringExtra("target");
            //phone number of person sending location
            phoneTo = intent.getStringExtra("phonefrom");
            //the name of person sending location
            sender = intent.getStringExtra("sender");

            DestinationManager dm = new DestinationManager();

            distance = dm.CalculationByDistance(getApplicationContext(), dm.convertStringToLatLng(updatedLocation));

            updateUI(sender, distance);

            updateMap(updatedLocation);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        CIRCLE_COLOR = getResources().getColor(R.color.Map_Color);

        destinationManager = new DestinationManager();

        //make sure location is enabled
        destinationManager.checkLocationAvailable(this);

        Intent intent = getIntent();

        phoneTo = intent.getStringExtra("phonefrom");
        sender = intent.getStringExtra("sender");

        targetName = (TextView) findViewById(R.id.targetNameTextView);
        distanceTextView = (TextView) findViewById(R.id.distanceTextView);

        updateUI("Connecting...", 0);

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        try {
            setUpMapIfNeeded();

        } catch (Exception e) {
            Log.e("MapsActivity Exception", e.toString());
        }


    }

    @Override
    public void onLocationChanged(Location location) {
        Toast.makeText(MapsActivity.this, "LOCATION CHANGED", Toast.LENGTH_SHORT).show();
    }

    public static void updateUI(String name, int distance) {
        try {
        targetName.setText(name);
        distanceTextView.setText("Distance: " + distance + " m");
        } catch (Exception e) {
            e.printStackTrace();
        }
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

        Location location = destinationManager.getLocation(this);

        myLocation = new LatLng(location.getLatitude(), location.getLongitude());

        updateMap("");
    }

    private void updateMap(String target) {
        if (mMap != null) {
            mMap.clear();
            mMap.addMarker(new MarkerOptions()
                    .position(myLocation)
                    .snippet("My Location")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                    .title("Me"));

            if (!target.equals("")) {

                LatLng targetLoc = new DestinationManager().convertStringToLatLng(target);

                mMap.addMarker(new MarkerOptions()
                        .position(targetLoc)
                        .snippet("Target")
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.car))
                        .title("Target"));
            }

            // Instantiates a new CircleOptions object and defines the center and radius
            CircleOptions circleOptions = new CircleOptions()
                    .center(myLocation)
                    .fillColor(CIRCLE_COLOR)
                    .strokeColor(CIRCLE_COLOR)
                    .radius(1609 * MILE_RADIUS);  //convert miles to meters

            mMap.addCircle(circleOptions);

            //TODO ONLY ZOOM FIRST TIME

            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                    myLocation, 14));
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d("Location Updates: ", "Connected");
        Toast.makeText(this, "Location Updates Started", Toast.LENGTH_LONG).show();
        //startLocationUpdates();
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

    @Override
    protected void onStop() {
        super.onStop();
        //finish();
    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
    }

    @Override
    public void onResume() {
        super.onResume();
        setUpMapIfNeeded();
        updateUI(connectionManager.sender, connectionManager.distance);
        IntentFilter filter = new IntentFilter();
        filter.addAction(MapsActivity.MAP_BROADCAST);
        registerReceiver(broadcastReceiver, filter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void getDirectionsButtonClick(View v) {
        if (destinationManager.convertStringToLatLng(updatedLocation) != null) {
            LatLng tempLatLng = destinationManager.convertStringToLatLng(updatedLocation);
            Uri gmmIntentUri = Uri.parse("google.navigation:q=" + tempLatLng.latitude + "," + tempLatLng.longitude);
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            startActivity(mapIntent);
        } else {
            final LinearLayout lin = (LinearLayout) findViewById(R.id.mapsLayout);
            final TextView dirTextView = (TextView) findViewById(R.id.getDirectionsTextView);
            final Animation shake = AnimationUtils.loadAnimation(getBaseContext(), R.anim.shake);
            dirTextView.setText("Wait to connect!");
            lin.startAnimation(shake);
            Timer directionTimer = new Timer();
            directionTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dirTextView.setText("Get Directions");
                        }

                    });
                }

            }, 2000);

        }
    }

    public void cancelButtonClick(View v) {
        if (connectionManager.isConnected()) {
            GcmSender gcmSender = new GcmSender(this);
            SQLiteHandler db = new SQLiteHandler(this);
            gcmSender.sendGcmAccept(db.getUserDetails().get("phone").toString(), phoneTo, "0", "1");
        }
        destroyConnection();
    }

    public void destroyConnection() {
        connectionManager.destroyConnection();
    }
}
