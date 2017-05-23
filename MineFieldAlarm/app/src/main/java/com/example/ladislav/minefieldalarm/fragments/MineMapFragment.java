package com.example.ladislav.minefieldalarm.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ladislav.minefieldalarm.R;
import com.example.ladislav.minefieldalarm.model.MineField;
import com.example.ladislav.minefieldalarm.model.MineFieldTable;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

/**
 * Fragment that is used as part of MainActivity.
 * It receives location updates from LocationTrackerService
 * and sets the marker on GoogleMap along with geofences.
 */

public class MineMapFragment extends Fragment {

    private static final String TAG = "MineFieldAlarm";
    private static final String LOCATION_CHANGE_FILTER = "UserLocationChange";

    private List<MineField> mineFields;
    private SupportMapFragment mapFragment;
    private GoogleMap googleMap;
    private LocationReceiver receiver;
    private Marker userPositionMarker;
    private CameraUpdate cameraUpdate;

    private boolean markerUpdated;
    //TODO on pause and destroy maybe unregister receiver
    // TODO when marker is updated once, do not move to it every time (it's annoying)

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        setRetainInstance(true);
        markerUpdated = false;
        mineFields = MineFieldTable.getInstance().getMineFields();
        receiver = new LocationReceiver(this);
        LocalBroadcastManager.getInstance(this.getContext())
                .registerReceiver(receiver, new IntentFilter(LOCATION_CHANGE_FILTER));

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_map, container, false);

        mapFragment = SupportMapFragment.newInstance();
        FragmentTransaction fragmentTransaction = getChildFragmentManager()
                .beginTransaction();
        fragmentTransaction.add(R.id.map_container, mapFragment);
        fragmentTransaction.commit();
        return root;
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this.getContext())
                .unregisterReceiver(receiver);
    }

    @Override
    public void onResume() {
        super.onResume();
        markerUpdated = false;
        if (mapFragment != null) {
            mapFragment.getMapAsync(new OnMapReadyCallback() {

                @Override
                public void onMapReady(GoogleMap googleMap) {
                    MineMapFragment.this.googleMap = googleMap;
                    displayMineFields();
                }
            });
        }

        LocalBroadcastManager.getInstance(this.getContext())
                .registerReceiver(receiver, new IntentFilter("UserLocationChange"));

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

    private class LocationReceiver extends BroadcastReceiver {

        MineMapFragment fragment;

        public LocationReceiver(Fragment fragment) {
            this.fragment = (MineMapFragment) fragment;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, "MineMapFragment: New location received !");

            Bundle bundle = intent.getExtras();
            double latitude = bundle.getDouble("latitude");
            double longitude = bundle.getDouble("longitude");

            updateMarker(latitude, longitude);
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
