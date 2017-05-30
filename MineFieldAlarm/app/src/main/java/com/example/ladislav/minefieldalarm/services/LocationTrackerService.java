package com.example.ladislav.minefieldalarm.services;

import android.Manifest;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.example.ladislav.minefieldalarm.model.MineField;
import com.example.ladislav.minefieldalarm.model.MineFieldTable;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Background user location tracker. Uses Google API client to track and update user location,
 * Compares location coordinates to mine field coordinates and dynamically makes geofences.
 * On geofence enter, it turns on alarm and re-starts main activity showing location and minefield !
 */

public class LocationTrackerService extends Service
        implements GoogleApiClient.ConnectionCallbacks,
        LocationListener,
        ResultCallback<Status>,
        GoogleApiClient.OnConnectionFailedListener {

    private static String TAG = "MineFieldAlarm";

    private static boolean isRunning;
    public static final int UPDATE_INTERVAL = 60000;
    public static final int FASTEST_UPDATE_INTERVAL = 1000;
    private Set<Geofence> closestFields;
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private PendingIntent pendingIntent;


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "LocationTrackerService onStartCommand");

        buildGoogleApiClient();
        createLocationRequest();
        googleApiClient.connect();
        isRunning = true;

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isRunning = false;
    }

    @Override
    public void onCreate() {
        Log.i(TAG, "LocationTrackerService onCreate");
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i(TAG, "Connected to GoogleApiClient, starting location updates... ");

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // start dialog and ask for permission
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(
                googleApiClient, locationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Connection suspended");

        googleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i(TAG, "Connection failed: " + connectionResult.getErrorCode());
    }


    @Override
    public void onLocationChanged(Location location) {
        Log.i(TAG, "LocationTrackerService: Location changed to: " + location.toString());

        if (googleApiClient.isConnected()) {
            updateGeofences(location);
            notifyMapFragment(location);
        }
    }

    public void notifyMapFragment(Location location) {
        Log.i(TAG, "LocationTrackerService: Notifying map fragment for location update.");

        Intent lbcIntent = new Intent("UserLocationChange");

        lbcIntent.putExtra("latitude", location.getLatitude());
        lbcIntent.putExtra("longitude", location.getLongitude());

        LocalBroadcastManager.getInstance(this).sendBroadcast(lbcIntent);
    }

    /**
     * Updates geofences that needs to be tracked based on
     * distance between user and mine fields.
     * It adds all the minefield in are of 5 kilometers in perimeter
     * to active geofences.
     * If geofence number passes 100, it calculates 100 nearest and
     * adds them. Method is being called on every location change.
     *
     * @param location used to get users latitude and longitude
     */

    private void updateGeofences(Location location) {
        Log.d(TAG, "LocationTrackerService: Updating geofences");

        // note: reimplemented update geofences - needs testing and refactor probably :)
        double userLatitude = location.getLatitude();
        double userLongitude = location.getLongitude();

        List<Geofence> newGeofences = new ArrayList<>();
        GeofencingRequest.Builder geofencingRequestBuilder = new GeofencingRequest.Builder();
        pendingIntent = requestPendingIntent();

        if (closestFields == null || closestFields.isEmpty()) {
            closestFields = MineFieldTable.getInstance().getClosestFieldsTo(userLatitude, userLongitude);
            for (Geofence mineField : closestFields) {
                geofencingRequestBuilder.addGeofence(mineField);
            }

        } else {
            Set<Geofence> tempClosest = MineFieldTable.getInstance().getClosestFieldsTo(userLatitude, userLongitude);
            for (Geofence g : tempClosest) {
                if (!closestFields.contains(g)) {
                    newGeofences.add(g);
                    closestFields.add(g);
                }
            }
            for (Geofence mineField : newGeofences) {
                geofencingRequestBuilder.addGeofence(mineField);
            }

        }

        Log.i(TAG, "LocationTrackerService: building Geofencing request ");
        GeofencingRequest geofencingRequest = geofencingRequestBuilder.build();

        if (closestFields != null && closestFields.size() > 99) {
            Log.i(TAG, "LocationTrackerService: removing geofences.");
            LocationServices.GeofencingApi.removeGeofences(googleApiClient,
                    requestPendingIntent()).setResultCallback(this);
        }


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG, "LocationTrackerService: location permission NOT granted ");
            // start dialog and ask for permission
            return;
        }
        Log.i(TAG, "LocationTrackerService: adding geofences. ");
        LocationServices.GeofencingApi.addGeofences(googleApiClient,
                geofencingRequest, pendingIntent).setResultCallback(this);
    }

    private PendingIntent requestPendingIntent() {
        Log.i(TAG, "LocationTrackerService: requesting pending intent.");
        if (pendingIntent != null) {
            return pendingIntent;
        }

        Intent intent = new Intent(this, GeofenceTransitionsIntentService.class);
        return PendingIntent.getService(this, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

    }

    @Override   //result callback
    public void onResult(@NonNull Status status) {
        Log.i(TAG, "GeofencingStatus:" + status.getStatusMessage());
    }

    /**
     * Helper method that uses GoogleApiClient Builder to instantiate
     * the client.
     */

    private synchronized void buildGoogleApiClient() {
        Log.i(TAG, "Building GoogleApiClient");
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
    }

    /**
     * Helper method that instantiates LocationRequest
     * and sets update interval and location accuracy.
     */

    private void createLocationRequest() {
        Log.i(TAG, "LocationTrackerService: Creating location request.");
        locationRequest = new LocationRequest();
        locationRequest.setInterval(UPDATE_INTERVAL);
        locationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    public static boolean isRunning() {
        return isRunning;
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
