package com.example.ladislav.minefieldalarm.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.ladislav.minefieldalarm.R;

public class OptionsActivity extends AppCompatActivity {

    //TODO think about FragmentActivity?

    // TODO set MainActivity as parent activity
    // TODO add change language options
    // TODO add start/stop service option

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);
    }
}
