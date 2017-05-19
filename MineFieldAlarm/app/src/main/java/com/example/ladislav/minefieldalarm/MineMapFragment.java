package com.example.ladislav.minefieldalarm;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Marker;


/**
 * Fragment that is used as part of MainActivity.
 * It receives location updates from LocationTrackerService
 * and sets the marker on GoogleMap along with geofences.
 */

// TODO add broadcast receiver to receive map updates from LocationTrackerService !
// TODO show user location
// TODO show geofences

public class MineMapFragment extends Fragment {

    private SupportMapFragment mapFragment;
    private GoogleMap map;
    private Marker locationMarker;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_map, container, false);

        mapFragment = SupportMapFragment.newInstance();
        FragmentTransaction fragmentTransaction = getChildFragmentManager()
                .beginTransaction();
        fragmentTransaction.add(R.id.map_container, mapFragment);
        fragmentTransaction.commit();

        return root;
    }
}
