package com.example.amy.googlemaps;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.common.stats.StatsEvent;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private LocationManager locationManager;
    private boolean isGPSenabled = false;
    private boolean isNetworkEnabled = false;
    private boolean canGetLocation = false;
    private static final long MIN_TIME_BW_UPDATES = 1000 * 15;
    private static final float MIN_DISTANCE_CHANGE_FOR_UPDATES = 5.0f;
    private Location myLocation;
    private static final float MY_LOC_ZOOM_FACTOR = 17.0f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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

        LatLng sanfran = new LatLng(37.77, -122.43);
        mMap.addMarker(new MarkerOptions().position(sanfran).title("Born here"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sanfran));

        /*if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d("MyMappsApp", "Failed Permission check 1");
            Log.d("MyMappsApp", Integer.toString(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)));
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 2);
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            Log.d("MyMapsApp", "Failed Permission check 2");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 2);
        }
        mMap.setMyLocationEnabled(true);*/
    }

    public void trackMe(View v){
        
    }

    public void getLocation() {
        try {
            locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

            //get GPS status
            isGPSenabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            if (isGPSenabled) Log.d("MyMaps", "getLocation: GPS is enabled");

            //get network status
            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            if (isNetworkEnabled) Log.d("MyMaps", "getLocation: Network is enabled");

            if (!isGPSenabled && !isNetworkEnabled) {
                Log.d("MyMaps", "getLocation: No Provider is enabled");
            } else {

                canGetLocation = true;
                if (isGPSenabled) {
                    Log.d("MyMaps", "getLocation: GPS Enabled - requesting location updates");
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        Log.d("MyMappsApp", "Failed Permission check 1");
                        Log.d("MyMappsApp", Integer.toString(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)));
                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 2);
                    }
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) !=
                            PackageManager.PERMISSION_GRANTED) {
                        Log.d("MyMapsApp", "Failed Permission check 2");
                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 2);
                    }
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES,
                            locationListenerGps);
                    Log.d("MyMaps", "getLocation: GPS update request success");
                    Toast.makeText(this, "Using GPS", Toast.LENGTH_SHORT);
                }
                if (isNetworkEnabled) {
                    Log.d("MyMaps", "getLocation: Network Enabled - requesting location updates");
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES,
                            locationListenerNetwork);
                    Log.d("MyMaps", "getLocation: Network update request success");
                    Toast.makeText(this, "Using GPS", Toast.LENGTH_SHORT);
                }
            }

        } catch (Exception e) {
            Log.d("MyMaps", "getLocation: Caught an exeption in getLocation");
            e.printStackTrace();
        }
    }

    public void setView(View v) {
        if (mMap.getMapType() == GoogleMap.MAP_TYPE_SATELLITE)
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        else
            mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
    }

    public void dropAmarker(String provider) {
        LatLng userLocation = null;
        if (locationManager != null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                return;
            }
            myLocation = locationManager.getLastKnownLocation(provider);
        }

        if (myLocation == null) {
            //display a message in Log.d and/or Toast
        } else {
            userLocation = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());

            //display message in Log.d and/or Toast
            CameraUpdate update = CameraUpdateFactory.newLatLngZoom(userLocation, MY_LOC_ZOOM_FACTOR);
            //Add a shape for your marker
            if(provider == LocationManager.GPS_PROVIDER)
            {
                Circle circle = mMap.addCircle(new CircleOptions()
                        .center(userLocation)
                        .radius(1)
                        .strokeColor(Color.RED)
                        .strokeWidth(2)
                        .fillColor(Color.RED));

                mMap.animateCamera(update);
            } else {
                Circle circle = mMap.addCircle(new CircleOptions()
                        .center(userLocation)
                        .radius(1)
                        .strokeColor(Color.GREEN)
                        .strokeWidth(2)
                        .fillColor(Color.GREEN));
            }
        }
    }

    LocationListener locationListenerGps = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            //output message in Log.d and Toast
            Log.d("MyMaps", "onLocationChanged: GPS location changed");
            Toast.makeText(getApplicationContext(), "GPS location changed", Toast.LENGTH_SHORT);
            //drop a marker on the map(create a method called dropAmarker)
            dropAmarker(location.getProvider());
            //disable network updates (see LocationManager API to remove updates) if gps is available
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                locationManager.removeUpdates(locationListenerNetwork);
            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.d("MyMaps", "onLocationChanged: GPS Status changed");
            Toast.makeText(getApplicationContext(), "GPS Status changed", Toast.LENGTH_SHORT);
            switch (status) {
                case LocationProvider.AVAILABLE:
                    Log.d("MyMaps", "onLocationChanged: GPS Status changed");
                    Toast.makeText(getApplicationContext(), "GPS Status changed", Toast.LENGTH_SHORT);
                    break;
                case LocationProvider.OUT_OF_SERVICE:
                    Log.d("MyMaps", "onLocationChanged: GPS Status changed");
                    if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat
                            .checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        return;
                    }
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES,
                            locationListenerGps);
                    break;
                case LocationProvider.TEMPORARILY_UNAVAILABLE:
                    Log.d("MyMaps", "onLocationChanged: GPS Status changed");
                    if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat
                            .checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        return;
                    }
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES,
                            locationListenerGps);
                    break;
                default:
                    //request updates from NETWORK_PROVIDER
                    if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat
                            .checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        return;
                    }
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES,
                            locationListenerGps);
            }
        }
        @Override
        public void onProviderEnabled(String provider) {
        }
        @Override
        public void onProviderDisabled(String provider) {
        }
    };

    LocationListener locationListenerNetwork = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            //output message in Log.d and Toast
            //drop a marker on the map(create a method called dropAmarker)
            //relaunch request for network location updates (requestLocationUpdates(NETWORK_PROVIDER)
            Log.d("MyMaps", "onLocationChanged: Network location changed");
            Toast.makeText(getApplicationContext(), "Network location changed", Toast.LENGTH_SHORT);
            dropAmarker(location.getProvider());
            if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat
                    .checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                return;
            }
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    MIN_TIME_BW_UPDATES,
                    MIN_DISTANCE_CHANGE_FOR_UPDATES,
                    locationListenerGps);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            //output message in Log.d and/or Toast
            Log.d("MyMaps", "onStatusChanged: Network status changed");
            Toast.makeText(getApplicationContext(), "Network status changed", Toast.LENGTH_SHORT);
        }
        @Override
        public void onProviderEnabled(String provider) {}
        @Override
        public void onProviderDisabled(String provider) {}
    };

}