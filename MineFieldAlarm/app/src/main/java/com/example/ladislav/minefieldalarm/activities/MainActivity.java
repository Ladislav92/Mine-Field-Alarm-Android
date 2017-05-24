package com.example.ladislav.minefieldalarm.activities;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.ladislav.minefieldalarm.model.MineField;
import com.example.ladislav.minefieldalarm.model.MineFieldTable;
import com.example.ladislav.minefieldalarm.services.LocationTrackerService;
import com.example.ladislav.minefieldalarm.R;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String LOCATION_CHANGE_FILTER = "UserLocationChange";
    public static final String TAG = "MineFieldAlarm";
    private GoogleMap googleMap;

    private LocationReceiver receiver;
    private Marker userPositionMarker;
    private CameraUpdate cameraUpdate;
    private List<MineField> mineFields;

    private double lastLatitude;
    private double lastLongitude;
    private boolean markerUpdated;

    //TODO manage life cycle of this activity better
    //TODO add floating action button

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "MainActivity onCreate started");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_map);

        if (savedInstanceState != null) {
            lastLatitude = savedInstanceState.getDouble("latitude");
            lastLongitude = savedInstanceState.getDouble("longitude");

        }
        setupMapIfNeeded();
        markerUpdated = false;
        mineFields = MineFieldTable.getInstance().getMineFields();


        receiver = new LocationReceiver();
        LocalBroadcastManager.getInstance(this)
                .registerReceiver(receiver, new IntentFilter(LOCATION_CHANGE_FILTER));


        // TODO start service to run in the foreground
        // TODO Make static instance of LocationTrackerService for access from SettingsActivity?
        Log.i(TAG, "MainActivity: starting LocationTrackerService");
        if (!isMyServiceRunning(LocationTrackerService.class)) {
            startService(new Intent(this, LocationTrackerService.class));
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putDouble("latitude", lastLatitude);
        outState.putDouble("longitude", lastLongitude);
    }

    @Override
    protected void onPause() {
        super.onPause();

        LocalBroadcastManager.getInstance(this)
                .unregisterReceiver(receiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this)
                .registerReceiver(receiver, new IntentFilter(LOCATION_CHANGE_FILTER));
        setupMapIfNeeded();

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        displayMineFields();
        // TODO change it to last known!
    }

    private void setupMapIfNeeded() {
        if (googleMap == null) {
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
        }
    }

    private void displayMineFields() {

        for (MineField mineField : mineFields) {

            CircleOptions circleOptions = new CircleOptions()
                    .center(new LatLng(mineField.getLatitude(), mineField.getLongitude()))
                    .radius(mineField.getRadius()).strokeColor(Color.RED)
                    .strokeWidth(2).fillColor(Color.rgb(226, 203, 29));

            googleMap.addCircle(circleOptions);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_about_app:
                startActivity(AboutAppActivity.class);
                return true;

            case R.id.action_about_org:
                startActivity(AboutOrganisationActivity.class);
                return true;

            case R.id.action_settings:
                startActivity(SettingsActivity.class);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public void startActivity(Class<?> activity) {
        Intent intent = new Intent(this, activity);
        startActivity(intent);
    }

    private class LocationReceiver extends BroadcastReceiver {


        public LocationReceiver() {

        }

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, "MineMapFragment: New location received !");

            Bundle bundle = intent.getExtras();
            lastLatitude = bundle.getDouble("latitude");
            lastLongitude = bundle.getDouble("longitude");

            updateMarker(lastLatitude, lastLongitude);
        }

        private void createMarker(Double latitude, Double longitude) {
            LatLng latLng = new LatLng(latitude, longitude);
            userPositionMarker = googleMap.addMarker(new MarkerOptions().position(latLng));
            userPositionMarker.setIcon((BitmapDescriptorFactory.fromResource(R.mipmap.ic_location_3)));
            userPositionMarker.setTitle("My Location");
            cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 15);
            googleMap.animateCamera(cameraUpdate);

        }

        private void updateMarker(Double latitude, Double longitude) {
            if (userPositionMarker == null) {
                createMarker(latitude, longitude);
            }
            LatLng latLng = new LatLng(latitude, longitude);
            userPositionMarker.setPosition(latLng);

            if (!markerUpdated) {
                cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 15);
                googleMap.animateCamera(cameraUpdate);
                markerUpdated = true;
            }
        }
    }

}
