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
import java.util.List;

/**
 * Background user location tracker. Uses Google API client to track and update user location,
 * Compares location coordinates to mine field coordinates and dynamically makes geofences.
 * On geofence enter, it turns on alarm and re-starts main activity showing location and minefield !
 */

// TODO On geofence enter send notification, turn alarm and start activity ?
// TODO send message to MapFragment about location and geofences to display them when opened


// TODO Manage life cycle of service: make option to restart itself if killed !

public class LocationTrackerService extends Service
        implements GoogleApiClient.ConnectionCallbacks,
        LocationListener,
        ResultCallback<Status>,
        GoogleApiClient.OnConnectionFailedListener {

    private static String TAG = "MineFieldAlarm";

    public static final double R = 6372.8;
    private static final double CHECK_PERIMETER = 5.0;
    public static final int UPDATE_INTERVAL = 10000;
    public static final int FASTEST_UPDATE_INTERVAL = 1000;

    private MineFieldTable mineFields;
    private List<MineField> closestFields;

    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private PendingIntent pendingIntent;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "LocationTrackerService onStartCommand");

        buildGoogleApiClient();
        createLocationRequest();
        googleApiClient.connect();

        return START_STICKY;
    }

    @Override
    public void onCreate() {
        Log.i(TAG, "LocationTrackerService onCreate");
        mineFields = MineFieldTable.getInstance();
    }

    @Override
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

        if (location != null) {
            updateGeofences(location);
        }
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

        double userLatitude = location.getLatitude();
        double userLongitude = location.getLongitude();

        if (closestFields != null && !closestFields.isEmpty()) {
            Log.i(TAG, "LocationTrackerService: removing geofences.");
            LocationServices.GeofencingApi.removeGeofences(googleApiClient,
                    requestPendingIntent()).setResultCallback(this);
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

        pendingIntent = requestPendingIntent();

        GeofencingRequest.Builder geofencingRequestBuilder = new GeofencingRequest.Builder();


        for (MineField mineField : closestFields) {
            geofencingRequestBuilder.addGeofence(mineField.toGeofence());
        }

        Log.i(TAG, "LocationTrackerService: building Geofencing request ");
        GeofencingRequest geofencingRequest = geofencingRequestBuilder.build();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG, "LocationTrackerService: location permission NOT granted ");
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Log.i(TAG, "LocationTrackerService: adding geofences. ");
        LocationServices.GeofencingApi.addGeofences(googleApiClient,
                geofencingRequest, pendingIntent).setResultCallback(this);
    }

    /**
     * Calculates distance between 2 locations (user and minefield in my case)
     * Uses Haversine formula for calculation:
     * <p>
     * "The haversine formula determines the great-circle
     * distance between two points on a sphere given their longitudes and latitudes.
     * Important in navigation, it is a special case of a more general formula in spherical
     * trigonometry, the law of haversines, that relates the sides and angles of spherical triangles."
     *
     * @param lat1 latitude of first location
     * @param lon1 longitude of first location
     * @param lat2 latitude of second location
     * @param lon2 longitude of second location
     * @return double distance in kilometers
     */
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
        Log.i(TAG, "LocationTrackerService: requesting pending intent.");
        if (null != pendingIntent) {
            return pendingIntent;
        }

        Intent intent = new Intent(this, GeofenceTransitionsIntentService.class);
        return PendingIntent.getService(this, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

    }

    // TODO implement me ! ! !
    @Override   //result callback
    public void onResult(@NonNull Status status) {

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

    /**
     * Method that has to be overridden.
     *
     * @param intent
     * @return null
     */

    @Nullable
    @Override    // service
    public IBinder onBind(Intent intent) {
        return null;
    }
}
