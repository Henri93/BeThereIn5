package henrygarant.com.demomap;

import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

public class MapsActivity extends FragmentActivity {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private final int MILE_RADIUS = 1;
    private int CIRCLE_COLOR =  Color.argb(100, 30, 136, 229);
    private String destination;
    private LatLng destinationLatLng = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        CIRCLE_COLOR =  getResources().getColor(R.color.Map_Color);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        destination = intent.getStringExtra("destination");
        makeDestinationLatLng(destination);
        setUpMapIfNeeded();
    }

    private void makeDestinationLatLng(String destination) {
        Geocoder coder = new Geocoder(this);
        List<Address> address;
        Address location = null;

        try {
            address = coder.getFromLocationName(destination, 1);
            if (address == null) {
                //TODO HANDLE NO LOCATION EVENT
                return;
            }
            location = address.get(0);
            destinationLatLng = new LatLng(location.getLatitude(), location.getLongitude());
        }catch (Exception e){

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
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


        //DestinationManager locManager = (DestinationManager)getSystemService(Context.LOCATION_SERVICE);
        //Location currentLocation = locManager.getLastKnownLocation(DestinationManager.GPS_PROVIDER);
        // LatLng currentLatLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());


        mMap.addMarker(new MarkerOptions()
                .position(destinationLatLng)
                .snippet("Your destination")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                .title("Destination"));

        // Instantiates a new CircleOptions object and defines the center and radius
        CircleOptions circleOptions = new CircleOptions()
                .center(destinationLatLng)
                .fillColor(CIRCLE_COLOR)
                .strokeColor(CIRCLE_COLOR)
                .radius(1609 * MILE_RADIUS);  //convert miles to meters

        mMap.addCircle(circleOptions);

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                destinationLatLng, 13));
    }
}
