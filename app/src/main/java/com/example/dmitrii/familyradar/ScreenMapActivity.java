package com.example.dmitrii.familyradar;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import com.example.dmitrii.familyradar.databinding.ActivityScreenMapBinding;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;

public class ScreenMapActivity extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener, OnMapReadyCallback, LocationListener {

    private Toolbar toolbar;
    private ActivityScreenMapBinding binding;
    private GoogleApiClient googleApiClient;
    private GoogleSignInOptions googleSignInOptions;
    private MapView mapView;
    private GoogleMap gmap;
    private LocationManager locationManager;
    private Bundle mapViewBundle;

    private static final long MIN_TIME = 400;
    private static final float MIN_DISTANCE = 1000;

    private static final String MAP_VIEW_BUNDLE_KEY = "google_maps_key";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen_map);

        //Getting an instance
        binding = DataBindingUtil.setContentView(this, R.layout.activity_screen_map);

        googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions)
                .build();

        //Call the toolbar
        setSupportActionBar(binding.toolbar);
        binding.toolbar.setTitle(getResources().getString(R.string.mapTitle));

        // Save key
        mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAP_VIEW_BUNDLE_KEY);
        }

        // Get map
        mapView = binding.mapV;
        mapView.onCreate(mapViewBundle);
        mapView.getMapAsync(this);

    }

//    @Override
//    public void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
//
//        Bundle mapViewBundle = outState.getBundle(MAP_VIEW_BUNDLE_KEY);
//        if (mapViewBundle == null) {
//            mapViewBundle = new Bundle();
//            outState.putBundle(MAP_VIEW_BUNDLE_KEY, mapViewBundle);
//        }
//        mapView.onSaveInstanceState(mapViewBundle);
//    }

    // Life cycle Activity
    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    protected void onPause() {
        mapView.onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        mapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    // Map preparation
    @Override
    public void onMapReady(GoogleMap googleMap) {
        gmap = googleMap;
        gmap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        UiSettings settings = gmap.getUiSettings();
        settings.setMyLocationButtonEnabled(true);
        settings.setZoomControlsEnabled(true);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME, MIN_DISTANCE, this);
        gmap.setMyLocationEnabled(true);
    }

    // Work with location
    @Override
    public void onLocationChanged(Location location) {
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 17);
        gmap.animateCamera(cameraUpdate);
        locationManager.removeUpdates(this);
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

    // Method of transition to another activity
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.social:

                Intent intent = new Intent(this, ScreenFamilyActivity.class);
                startActivity(intent);
                return true;

            //Sign out of google account
            case R.id.exitButton:
                logOut();
                Toast.makeText(getApplicationContext(), R.string.you_left_the_account, Toast.LENGTH_SHORT).show();
                return true;

            case R.id.myProfile:
                Intent goMyProfile = new Intent(this, ProfileActivity.class);
                startActivity(goMyProfile);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //break connection
    private void logOut() {

        Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {
                if (status.isSuccess()) {
                    goLogInScreen();
                } else {
                    Toast.makeText(getApplicationContext(), R.string.not_close_session, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //go to another activity
    private void goLogInScreen() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    // Menu
    @Override
    public boolean onCreateOptionsMenu(Menu mMenu) {
        Menu menu = binding.toolbar.getMenu();
        getMenuInflater().inflate(R.menu.menu, menu);
        menu.findItem(R.id.delete).setVisible(false);
        menu.findItem(R.id.social).setVisible(true);
        menu.findItem(R.id.checkSymbol).setVisible(false);
        return true;
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

}
