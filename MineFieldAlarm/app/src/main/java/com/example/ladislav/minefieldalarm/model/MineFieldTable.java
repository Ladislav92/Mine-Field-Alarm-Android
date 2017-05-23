package com.example.ladislav.minefieldalarm.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Singleton class.
 * Serves as storage to all known minefields and their exact location.
 */

// TODO read data from file that holds location data of mine fields

public class MineFieldTable {

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
            if (mineField.distanceFrom(latitude, longitude) <= CHECK_PERIMETER) {
                closestFields.add(mineField);
            }
        }

        if (closestFields.size() > 99) {
            //TODO sort all by distance and remove the furthest
        }

        return Collections.unmodifiableList(closestFields);
    }

}
