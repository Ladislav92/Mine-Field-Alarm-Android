package com.example.ladislav.minefieldalarm.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import com.example.ladislav.minefieldalarm.R;
import com.example.ladislav.minefieldalarm.model.MapStateManager;
import com.example.ladislav.minefieldalarm.model.MineField;
import com.example.ladislav.minefieldalarm.model.MineFieldTable;
import com.example.ladislav.minefieldalarm.services.LocationTrackerService;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;

import java.util.List;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = "MineFieldAlarm";
    private static final String LOCATION_CHANGE_FILTER = "UserLocationChange";
    private static final String USER_LOCATION_TEXT = "My location";

    private GoogleMap googleMap;
    private LocationReceiver receiver;
    private Marker userPositionMarker;
    private LatLng lastLatLng;

    private List<MineField> mineFields;

    private boolean cameraMoved;
    private boolean moveRequested;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupMapIfNeeded();
        setFloatingActionButton();

        cameraMoved = false;
        mineFields = MineFieldTable.getInstance().getMineFields();
        receiver = new LocationReceiver();
        LocalBroadcastManager.getInstance(this)
                .registerReceiver(receiver, new IntentFilter(LOCATION_CHANGE_FILTER));

        // TODO start service to run in the foreground
        // TODO Make static instance of LocationTrackerService for access from SettingsActivity?
        if (!LocationTrackerService.isRunning()) {
            Log.i(TAG, "MainActivity: starting LocationTrackerService");
            startService(new Intent(this, LocationTrackerService.class));
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        MapStateManager mgr = new MapStateManager(this);
        mgr.saveMapState(googleMap);
        LocalBroadcastManager.getInstance(this)
                .unregisterReceiver(receiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        cameraMoved = false;
        LocalBroadcastManager.getInstance(this)
                .registerReceiver(receiver, new IntentFilter(LOCATION_CHANGE_FILTER));
        setupMapIfNeeded();

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;

        MapStateManager mgr = new MapStateManager(this);
        CameraPosition position = mgr.getSavedCameraPosition();
        if (position != null) {
            CameraUpdate update = CameraUpdateFactory.newCameraPosition(position);
            googleMap.moveCamera(update);
            googleMap.setMapType(mgr.getSavedMapType());
        }
        displayMineFields();
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_about_app:
                startAnotherActivity(AboutAppActivity.class);
                return true;

            case R.id.action_about_org:
                startAnotherActivity(AboutOrganisationActivity.class);
                return true;

            case R.id.action_settings:
                startAnotherActivity(SettingsActivity.class);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void createMarker(LatLng latLng, String title) {
        userPositionMarker = googleMap.addMarker(new MarkerOptions().position(latLng));
        userPositionMarker.setIcon((BitmapDescriptorFactory.fromResource(R.drawable.ic_location_3)));
        userPositionMarker.setTitle(title);
    }

    private void updateMarker(LatLng latLng) {

        if (userPositionMarker == null) {
            createMarker(latLng, USER_LOCATION_TEXT);
        }
        userPositionMarker.setPosition(latLng);

        if (!cameraMoved || moveRequested) {
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 15);
            googleMap.animateCamera(cameraUpdate);
            cameraMoved = true;
            moveRequested = false;
        }
    }

    private class LocationReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, "MineMapFragment: New location received !");

            Bundle bundle = intent.getExtras();
            double lastLatitude = bundle.getDouble("latitude");
            double lastLongitude = bundle.getDouble("longitude");
            lastLatLng = new LatLng(lastLatitude, lastLongitude);
            updateMarker(lastLatLng);
        }
    }

    private void setFloatingActionButton() {
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setImageResource(R.drawable.ic_my_location_black_24dp);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(),
                        "Requesting the position", Toast.LENGTH_LONG).show();
                if (lastLatLng != null) {
                    updateMarker(lastLatLng);
                }
                moveRequested = true;
            }
        });
    }


    private void startAnotherActivity(Class<?> activity) {
        Intent intent = new Intent(this, activity);
        startActivity(intent);
    }
}
