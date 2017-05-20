package com.example.ladislav.minefieldalarm;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MineFieldAlarm";

    //TODO Add a menu
    //TODO Add about app activity / fragment
    //TODO Add about "UDAS" organisation activity / fragment
    //TODO Add contact us activity / fragment

    //TODO manage life cycle of activity

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "MainActivity onCreate started");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setFragment(new MineMapFragment());

        // TODO start service to run in the foreground
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
