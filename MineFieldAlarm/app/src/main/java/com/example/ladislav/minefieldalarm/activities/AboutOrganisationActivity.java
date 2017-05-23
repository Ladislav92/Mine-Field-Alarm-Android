package com.example.ladislav.minefieldalarm.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.ladislav.minefieldalarm.R;

public class AboutOrganisationActivity extends AppCompatActivity {

    // TODO maybe read about from text file and add to text view programmatically
    // TODO add few words about UDAS organisation
    // TODO add contact form, e-mail form maybe (with permission to use email and open an app)? (if time)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_organisation);
    }
}
