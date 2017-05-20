package com.example.ladislav.minefieldalarm;

import com.google.android.gms.location.Geofence;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Singleton class.
 * Serves as storage to all known minefields and their exact location.
 */

// TODO read data from file that holds location data of mine fields and store it in map

public class MineFieldTable {

    private List<MineField> mineFields;

    private static MineFieldTable instance = new MineFieldTable();

    private MineFieldTable() {
        // 60.008557, 30.391760 Dormitory, "Grazhdyanski Prospekt 30"
        // 60.006726, 30.372550 In front of main Politeh building
        // 59.999755, 30.364346 Park across 9k.
        mineFields = new ArrayList<>();
        mineFields.add(new MineField("First", 60.008557, 30.391760, 100, Geofence.GEOFENCE_TRANSITION_ENTER));
        mineFields.add(new MineField("Second", 59.999755, 30.364346, 100, Geofence.GEOFENCE_TRANSITION_ENTER));
        mineFields.add(new MineField("Third", 60.006726, 30.372550, 100, Geofence.GEOFENCE_TRANSITION_ENTER));
    }

    public List<MineField> getMineFields() {
        return Collections.unmodifiableList(mineFields);
    }

    public static MineFieldTable getInstance() {
        return instance;
    }

}
