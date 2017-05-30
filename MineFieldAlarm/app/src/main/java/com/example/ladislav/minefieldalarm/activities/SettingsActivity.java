package com.example.ladislav.minefieldalarm.activities;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.example.ladislav.minefieldalarm.R;
import com.example.ladislav.minefieldalarm.services.LocationTrackerService;

public class SettingsActivity extends AppCompatActivity {
    private static final String TAG = "MineFieldAlarm";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);
        Switch mySwitch = (Switch) findViewById(R.id.switch1);
        // new implementation ***
        mySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Log.i(TAG, "Settings: starting LocationTrackerService");
                    if (!LocationTrackerService.isRunning()) {
                        startService(new Intent(getParent(), LocationTrackerService.class));
                    }
                } else {
                    if (LocationTrackerService.isRunning()) {
                        stopService(new Intent(getParent(), LocationTrackerService.class));
                    }
                }
            }
        });
    }

}
