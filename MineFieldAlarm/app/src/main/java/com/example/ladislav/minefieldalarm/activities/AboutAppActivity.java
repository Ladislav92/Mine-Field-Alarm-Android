package com.example.ladislav.minefieldalarm.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.ladislav.minefieldalarm.R;

public class AboutAppActivity extends AppCompatActivity {

    // TODO set MainActivity as parent activity
    // TODO Add few words, some disclaimer (maybe disclaimer as dialog when app is opened

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
    }
}
