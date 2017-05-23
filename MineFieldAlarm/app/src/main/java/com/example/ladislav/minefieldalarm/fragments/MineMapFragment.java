package com.example.ladislav.minefieldalarm.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ladislav.minefieldalarm.R;
import com.example.ladislav.minefieldalarm.model.MineField;
import com.example.ladislav.minefieldalarm.model.MineFieldTable;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
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

    private List<MineField> mineFields;
    private SupportMapFragment mapFragment;
    private GoogleMap googleMap;
    private LocationReceiver receiver;
    private Marker userPositionMarker;

    //TODO on pause and destroy maybe unregister receiver
    // TODO when marker is updated once, do not move to it every time (it's annoying)

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mineFields = MineFieldTable.getInstance().getMineFields();

        LocalBroadcastManager lbc = LocalBroadcastManager.getInstance(this.getContext());
        receiver = new LocationReceiver(this);
        lbc.registerReceiver(receiver, new IntentFilter("UserLocationChange"));

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
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mapFragment != null) {
            mapFragment.getMapAsync(new OnMapReadyCallback() {

                @Override
                public void onMapReady(GoogleMap googleMap) {
                    MineMapFragment.this.googleMap = googleMap;
                    MineMapFragment.this.googleMap.animateCamera(CameraUpdateFactory.zoomTo(15));
                    displayMineFields();

                }
            });
        }
    }

    private void displayMineFields() {

        for (MineField mineField : mineFields) {

            CircleOptions circleOptions = new CircleOptions()
                    .center(new LatLng(mineField.getLatitude(), mineField.getLongitude()))
                    .radius(mineField.getRadius()).strokeColor(Color.RED)
                    .strokeWidth(2).fillColor(0x500000ff);

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
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        }

        private void updateMarker(Double latitude, Double longitude) {
            if (userPositionMarker == null) {
                createMarker(latitude, longitude);
            }

            LatLng latLng = new LatLng(latitude, longitude);
            userPositionMarker.setPosition(latLng);
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            MineMapFragment.this.googleMap.animateCamera(CameraUpdateFactory.zoomTo(15));
        }
    }
}
