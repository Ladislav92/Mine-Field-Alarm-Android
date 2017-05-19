package com.example.ladislav.minefieldalarm;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.google.android.gms.common.api.GoogleApiClient;

/**
 * Background user location tracker. Uses Google API client to track and update user location,
 * Compares location coordinates to mine field coordinates and dynamically makes geofences.
 * On geofence enter, it turns on alarm and re-starts main activity showing location and minefield !.
 */

// TODO Track and update location,
// TODO compare it to MineFieldTable locations,
// TODO find 100 closest to current location and add to geofence
// TODO On geofence enter send notification, turn alarm and start activity ?!
// TODO send message to MapFragment about location and geofences

public class LocationTrackerService extends Service {

    private MineFieldTable mineFields;
    private GoogleApiClient googleApiClient;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
