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

    private MineFieldTable instance = new MineFieldTable();

    private MineFieldTable() {
        mineFields = new ArrayList<>();
        mineFields.add(new MineField("First", 10.0, 10.0, 100, Geofence.GEOFENCE_TRANSITION_ENTER));
        mineFields.add(new MineField("Second", 10.0, 10.0, 100, Geofence.GEOFENCE_TRANSITION_ENTER));
        mineFields.add(new MineField("Third", 10.0, 10.0, 100, Geofence.GEOFENCE_TRANSITION_ENTER));
    }

    public List<MineField> getMineFields() {
        return Collections.unmodifiableList(mineFields);
    }

    public MineFieldTable getInstance() {
        return instance;
    }

}
