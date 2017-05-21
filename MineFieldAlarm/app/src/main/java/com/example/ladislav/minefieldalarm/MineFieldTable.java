package com.example.ladislav.minefieldalarm;

import com.google.android.gms.location.Geofence;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Singleton class.
 * Serves as storage to all known minefields and their exact location.
 */

// TODO read data from file that holds location data of mine fields

public class MineFieldTable {

    public static final double R = 6372.8;
    private static final double CHECK_PERIMETER = 5.0;

    private List<MineField> mineFields;

    private static MineFieldTable instance = new MineFieldTable();

    private MineFieldTable() {
        mineFields = new ArrayList<>();

        mineFields.add(new MineField("Obshaga", 60.008557, 30.391760, 100));
        mineFields.add(new MineField("Glavno", 59.999755, 30.364346, 100));
        mineFields.add(new MineField("9k", 60.006726, 30.372550, 100));
    }

    public List<MineField> getMineFields() {
        return Collections.unmodifiableList(mineFields);
    }

    public static MineFieldTable getInstance() {
        return instance;
    }

    public List<MineField> getClosestFieldsTo(double latitude, double longitude) {

        List<MineField> closestFields = new ArrayList<>();

        for (MineField mineField : mineFields) {
            if (distanceBetween(latitude, longitude,
                    mineField.getLatitude(), mineField.getLongitude()) <= CHECK_PERIMETER) {
                closestFields.add(mineField);
            }
        }

        if (closestFields.size() > 99) {
            //TODO sort all by distance and remove the furthest
        }

        return Collections.unmodifiableList(closestFields);
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
    private static double distanceBetween(double lat1, double lon1, double lat2, double lon2) {

        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.sin(dLon / 2) *
                Math.sin(dLon / 2) * Math.cos(lat1) * Math.cos(lat2);
        double c = 2 * Math.asin(Math.sqrt(a));
        return R * c;

    }

}
