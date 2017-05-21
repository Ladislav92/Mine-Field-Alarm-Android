package com.example.ladislav.minefieldalarm;

import android.text.format.DateUtils;

import com.google.android.gms.location.Geofence;

/**
 * This simple class that represents actual mine field with getters, setters
 * and method for converting it to Geofence
 */

//TODO Think of parceable so it can be sent trough intent?

public class MineField {

    private static final long GEOFENCE_EXPIRATION_IN_HOURS = 12;
    public static final long GEOFENCE_EXPIRATION_IN_MILLISECONDS = GEOFENCE_EXPIRATION_IN_HOURS
            * DateUtils.HOUR_IN_MILLIS;
    private static final int GEOFENCE_LOITERING_DELAY = 5000;

    private final String ID;
    private final double latitude;
    private final double longitude;
    private final float radius; // for now only radius border for simplicity

    public MineField(String ID, double latitude, double longitude, float radius) {
        this.ID = ID;
        this.latitude = latitude;
        this.longitude = longitude;
        this.radius = radius;

    }

    public Geofence toGeofence() {
        return new Geofence.Builder().setRequestId(ID)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                .setCircularRegion(latitude, longitude, radius)
                .setExpirationDuration(GEOFENCE_EXPIRATION_IN_MILLISECONDS)
                .setLoiteringDelay(GEOFENCE_LOITERING_DELAY).build();
    }

    public String getID() {
        return ID;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public float getRadius() {
        return radius;
    }

}
