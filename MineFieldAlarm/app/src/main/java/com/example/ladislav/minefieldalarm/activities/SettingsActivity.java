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

    }

    public void switchTrackingService(View view){
//FIXME
//        Switch mySwitch = (Switch)findViewById(R.id.switch1);
//        mySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                // do something, the isChecked will be
//                // true if the switch is in the On position
//                if (isChecked) {
//                    Log.i(TAG, "Settings: starting LocationTrackerService");
//                    startService(new Intent(getParent(), LocationTrackerService.class));
//                } else if (!isChecked) {
//                    stopService(new Intent(getParent(), LocationTrackerService.class));
//                }
//            }
//        });
    }
}
