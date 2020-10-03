package lk.hndit.quickeats.activity.admin;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import lk.hndit.quickeats.R;
import lk.hndit.quickeats.util.GpsTracker;

public class TrackingOrderAdmin extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Intent intent;
    private double latitude;
    private double longitude;
    private Marker MYmarker;
    private boolean firstLoad = true;
    private ProgressDialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracking_order_admin);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        dialog = new ProgressDialog(this);
        if(firstLoad){
            dialog.setMessage("Getting Map data...");
            dialog.show();
        }

        intent = getIntent();
        latitude = intent.getDoubleExtra("latitude", 0);
        longitude = intent.getDoubleExtra("longitude",0);




        LocationManager locationManager = (LocationManager)
                getSystemService(Context.LOCATION_SERVICE);


        try {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 101);
            }
        } catch (Exception e){
            e.printStackTrace();
        }




        android.location.LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {



//                Toast.makeText(getBaseContext(), "Location changed: Lat: " + location.getLatitude() + " Lng: "
//                                + location.getLongitude(), Toast.LENGTH_SHORT).show();


                String longitude = "Longitude: " + location.getLongitude();
                Log.v("TAG", longitude);
                String latitude = "Latitude: " + location.getLatitude();
                Log.v("TAG", latitude);

                if(MYmarker != null){
                    MYmarker.remove();
                }
                final LatLng yourL = new LatLng(location.getLatitude(),location.getLongitude());
                MYmarker = mMap.addMarker(new MarkerOptions().position(yourL).title("Your Location"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(yourL));
                //googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(t, 17.0f));
                //googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(t, 17));



                /*------- To get city name from coordinates -------- */

                String cityName = "";
                Geocoder gcd = new Geocoder(getBaseContext(), Locale.getDefault());
                List<Address> addresses;
                try {
                    addresses = gcd.getFromLocation(location.getLatitude(),
                            location.getLongitude(), 1);
                    if (addresses.size() > 0) {
                        System.out.println(addresses.get(0).getLocality());
                        cityName = addresses.get(0).getLocality();

                        String address = addresses.get(0).getAddressLine(0);
                        String city = addresses.get(0).getLocality();
                        String postalCode = addresses.get(0).getPostalCode();

                        //Toast.makeText(TrackingOrderAdmin.this, "address : "+address+", "+city+", "+postalCode+". ", Toast.LENGTH_LONG).show();

                        Log.d("TAG", "========================== address : "+address+", "+city+", "+postalCode+". ");
                    }
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
                String s = longitude + "\n" + latitude + "\n\nMy Current City is: "
                        + cityName;




                if(firstLoad){
                    dialog.dismiss();
                    firstLoad = false;
                }


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
        };

        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER, 5000, 10, locationListener);


        GpsTracker gpsTracker = new GpsTracker(TrackingOrderAdmin.this);

        if(gpsTracker.canGetLocation()){

        }else {
            gpsTracker.showSettingsAlert();
        }




    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        final LatLng distL = new LatLng(latitude, longitude);
        mMap.addMarker(new MarkerOptions().position(distL).title("Customer's Location "));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(distL));
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(distL, 17.0f));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(distL, 16));
    }
}
