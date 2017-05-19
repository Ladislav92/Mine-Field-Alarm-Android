package com.example.ladislav.minefieldalarm;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

/**
 * Background user location tracker. Uses Google API client to track and update user location,
 * Compares location coordinates to mine field coordinates and dynamically makes geofences.
 * On geofence enter, it turns on alarm and re-starts main activity showing location and minefield !.
 */

// TODO Track and update location,
// TODO compare it to MineFieldTable locations,
// TODO find 100 closest to current location and add to geofence
// TODO On geofence enter send notification, turn alarm and start activity ?!
// TODO send message to MapFragment about location and geofences

public class LocationTrackerService extends Service
        implements GoogleApiClient.ConnectionCallbacks,
        LocationListener,
        ResultCallback<Status>,
        GoogleApiClient.OnConnectionFailedListener {

    private static String TAG = "LOCATION_TRACKER";
    public static final int UPDATE_INTERVAL = 10000;
    public static final int FASTEST_UPDATE_INTERVAL = 1000;

    private MineFieldTable mineFields; // all mine fields

    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;

    @Override   //service
    public int onStartCommand(Intent intent, int flags, int startId) {
        buildGoogleApiClient();
        googleApiClient.connect();
        return START_STICKY;
    }

    @Override   //service
    public void onCreate() {
        mineFields.getInstance();
    }

    @Override   //google api client
    public void onConnected(@Nullable Bundle bundle) {
        Log.i(TAG, "Connected to GoogleApiClient, starting location updates... ");

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling !  !  !
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(
                googleApiClient, locationRequest, this);
    }


    @Override   //google api client
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Connection suspended");
    }

    @Override   //onConnectionFailedListener
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i(TAG, "Connection failed: " + connectionResult.getErrorCode());
    }


    @Override   //location request
    public void onLocationChanged(Location location) {
        Log.i(TAG, "Location changed to: " + location.toString());
        //TODO update geofences
    }

    @Override   //result callback
    public void onResult(@NonNull Status status) {

    }


    protected synchronized void buildGoogleApiClient() {
        Log.i(TAG, "Building GoogleApiClient");
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
        createLocationRequest();
    }

    protected void createLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(UPDATE_INTERVAL);
        locationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Nullable
    @Override    // service
    public IBinder onBind(Intent intent) {
        return null;
    }
}
