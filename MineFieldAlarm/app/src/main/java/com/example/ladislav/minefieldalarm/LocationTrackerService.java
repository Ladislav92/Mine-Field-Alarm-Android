package com.example.ladislav.minefieldalarm;

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
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Background user location tracker. Uses Google API client to track and update user location,
 * Compares location coordinates to mine field coordinates and dynamically makes geofences.
 * On geofence enter, it turns on alarm and re-starts main activity showing location and minefield !
 */

// TODO compare it to MineFieldTable locations,
// TODO find 100 closest to current location and add to geofence
// TODO On geofence enter send notification, turn alarm and start activity ?!
// TODO send message to MapFragment about location and geofences

//TODO Think of IntentService

public class LocationTrackerService extends Service
        implements GoogleApiClient.ConnectionCallbacks,
        LocationListener,
        ResultCallback<Status>,
        GoogleApiClient.OnConnectionFailedListener {

    private static String TAG = "LOCATION_TRACKER";

    public static final double R = 6372.8;
    private static final double CHECK_PERIMETER = 5.0;
    public static final int UPDATE_INTERVAL = 10000;
    public static final int FASTEST_UPDATE_INTERVAL = 1000;

    private MineFieldTable mineFields; // all mine fields
    private List<MineField> closestFields;
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private PendingIntent pendingIntent;

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

        googleApiClient.connect();
    }

    @Override   //onConnectionFailedListener
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i(TAG, "Connection failed: " + connectionResult.getErrorCode());
    }

    @Override   //location request
    public void onLocationChanged(Location location) {
        Log.i(TAG, "Location changed to: " + location.toString());

        updateGeofences(location);
    }

    private void updateGeofences(Location location) {
        Log.d(TAG, "Updating geofences");

        double userLatitude = location.getLatitude();
        double userLongitude = location.getLongitude();

        pendingIntent = requestPendingIntent();

        if (!closestFields.isEmpty()) {
            LocationServices.GeofencingApi.removeGeofences(googleApiClient,
                    pendingIntent).setResultCallback(this);
        }

        closestFields = new ArrayList<>();

        for (MineField mineField : mineFields.getMineFields()) {
            if (distanceBetween(userLatitude, userLongitude,
                    mineField.getLatitude(), mineField.getLongitude()) <= CHECK_PERIMETER) {
                closestFields.add(mineField);
            }
        }

        if (closestFields.size() > 99) {
            //TODO sort all by distance and remove the furthest

        }

        GeofencingRequest.Builder geofencingRequestBuilder = new GeofencingRequest.Builder();

        for (MineField mineField : closestFields) {
            geofencingRequestBuilder.addGeofence(mineField.toGeofence());
        }

        GeofencingRequest geofencingRequest = geofencingRequestBuilder.build();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.GeofencingApi.addGeofences(googleApiClient,
                geofencingRequest, pendingIntent).setResultCallback(this);
    }

    private double distanceBetween(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.sin(dLon / 2) *
                Math.sin(dLon / 2) * Math.cos(lat1) * Math.cos(lat2);
        double c = 2 * Math.asin(Math.sqrt(a));
        return R * c;
    }


    private PendingIntent requestPendingIntent() {

        if (null != pendingIntent) {

            return pendingIntent;
        } else {

            Intent intent = new Intent(this, GeofenceTransitionsIntentService.class);
            return PendingIntent.getService(this, 0, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT);

        }
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
