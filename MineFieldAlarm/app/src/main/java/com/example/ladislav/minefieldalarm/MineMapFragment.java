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


/**
 * Fragment that holds GoogleMap, receives location updates and shows them on it
 * And draws geofences on the map. It is used as part of MainActivity
 */

public class MineMapFragment extends Fragment {

    protected SupportMapFragment mapFragment;
    protected GoogleMap map;

    // TODO add broadcast receiver to receive map updates from service !
    // TODO show user location (add map marker)
    // TODO show geofences on map

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
