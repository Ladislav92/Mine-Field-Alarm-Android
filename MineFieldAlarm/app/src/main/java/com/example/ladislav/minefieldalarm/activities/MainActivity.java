package com.example.ladislav.minefieldalarm.activities;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.example.ladislav.minefieldalarm.services.LocationTrackerService;
import com.example.ladislav.minefieldalarm.fragments.MineMapFragment;
import com.example.ladislav.minefieldalarm.R;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MineFieldAlarm";

    //TODO Add a menu
    //TODO Add settings with language change and turn on/off tracking service?

    //TODO manage life cycle of this activity better

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "MainActivity onCreate started");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setFragment(new MineMapFragment());

        // TODO start service to run in the foreground
        // TODO Make static instance of LocationTrackerService for access from OptionsActivity?
        Log.i(TAG, "MainActivity: starting LocationTrackerService");
        startService(new Intent(this, LocationTrackerService.class));
    }

    protected void setFragment(Fragment fragment) {
        Log.i(TAG, "MainActivity: settingFragment");
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction =
                fragmentManager.beginTransaction();
        fragmentTransaction.replace(android.R.id.content, fragment);
        fragmentTransaction.commit();

    }

}
