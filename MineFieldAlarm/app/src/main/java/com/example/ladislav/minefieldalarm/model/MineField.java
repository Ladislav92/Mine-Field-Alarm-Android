package com.example.ladislav.minefieldalarm.model;

import android.text.format.DateUtils;

import com.google.android.gms.location.Geofence;

/**
 * This simple class that represents actual mine field with getters, setters
 * and method for converting it to Geofence
 */

public class MineField {

    public static final double R = 6372.8;

    private static final long GEOFENCE_EXPIRATION_IN_HOURS = 12;
    public static final long GEOFENCE_EXPIRATION_IN_MILLISECONDS = GEOFENCE_EXPIRATION_IN_HOURS
            * DateUtils.HOUR_IN_MILLIS;
    private static final int GEOFENCE_LOITERING_DELAY = 20000;

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

    /**
     * Calculates distance between 2 locations (user and minefield in my case)
     * Uses Haversine formula for calculation:
     * <p>
     * "The haversine formula determines the great-circle
     * distance between two points on a sphere given their longitudes and latitudes.
     * Important in navigation, it is a special case of a more general formula in spherical
     * trigonometry, the law of haversines, that relates the sides and angles of spherical triangles."
     *
     * @return double distance in kilometers
     */
    public double distanceFrom(double lat, double lon) {

        double minefieldLat = latitude;
        double minefieldLon = longitude;

        double dLat = Math.toRadians(minefieldLat - lat);
        double dLon = Math.toRadians(minefieldLon - lon);
        lat = Math.toRadians(lat);
        minefieldLat = Math.toRadians(minefieldLat);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.sin(dLon / 2) *
                Math.sin(dLon / 2) * Math.cos(lat) * Math.cos(minefieldLat);
        double c = 2 * Math.asin(Math.sqrt(a));

        return R * c;
    }
}







