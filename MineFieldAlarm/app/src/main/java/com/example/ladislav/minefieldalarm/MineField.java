package com.example.ladislav.minefieldalarm;

import android.text.format.DateUtils;

import com.google.android.gms.location.Geofence;

/**
 * This simple class that represents actual mine field with getters, setters
 * and support for converting it to Geofence
 */

public class MineField {

    private static final long GEOFENCE_EXPIRATION_IN_HOURS = 12;
    public static final long GEOFENCE_EXPIRATION_IN_MILLISECONDS = GEOFENCE_EXPIRATION_IN_HOURS
            * DateUtils.HOUR_IN_MILLIS;
    private static final int GEOFENCE_LOITERING_DELAY = 5000;

    private final String ID;
    private final double latitude;
    private final double longitude;
    private final float radius; // for now only radius border for simplicity
    private int transitionType; // probably final value: on enter only ???

    public MineField(String ID, double latitude, double longitude, float radius, int transitionType) {
        this.ID = ID;
        this.latitude = latitude;
        this.longitude = longitude;
        this.radius = radius;
        this.transitionType = transitionType;

    }

    public Geofence toGeofence() {
        return new Geofence.Builder().setRequestId(ID)
                .setTransitionTypes(transitionType)
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

    public int getTransitionType() {
        return transitionType;
    }

    public void setTransitionType(int transitionType) {
        this.transitionType = transitionType;
    }

}
