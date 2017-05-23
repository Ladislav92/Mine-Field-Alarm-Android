package com.example.ladislav.minefieldalarm.activities;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.ladislav.minefieldalarm.services.LocationTrackerService;
import com.example.ladislav.minefieldalarm.fragments.MineMapFragment;
import com.example.ladislav.minefieldalarm.R;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MineFieldAlarm";
    private Toolbar toolbar;

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
        // TODO Make static instance of LocationTrackerService for access from SettingsActivity?
        Log.i(TAG, "MainActivity: starting LocationTrackerService");
        if (!isMyServiceRunning(LocationTrackerService.class)) {
            startService(new Intent(this, LocationTrackerService.class));
        }
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }


    public void startActivity(Class<?> activity) {
        Intent intent = new Intent(this, activity);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_about_app:
                startActivity(AboutAppActivity.class);
                return true;

            case R.id.action_about_org:
                startActivity(AboutOrganisationActivity.class);
                return true;

            case R.id.action_settings:
                startActivity(SettingsActivity.class);
                return true;
            default:

                return super.onOptionsItemSelected(item);

        }
    }


    protected void setFragment(Fragment fragment) {
        Log.i(TAG, "MainActivity: settingFragment");
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction =
                fragmentManager.beginTransaction();
        fragmentTransaction.add(android.R.id.content, fragment);
        fragmentTransaction.commit();

    }


}
