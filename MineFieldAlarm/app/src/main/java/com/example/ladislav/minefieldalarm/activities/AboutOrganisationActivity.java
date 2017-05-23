package com.example.ladislav.minefieldalarm.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.ladislav.minefieldalarm.R;

public class AboutOrganisationActivity extends AppCompatActivity {

    //TODO think about FragmentActivity?

    // TODO set MainActivity as parent activity
    // TODO add few words about UDAS
    // TODO add contact form, e-mail form maybe (with permission to use email and open an app)?

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_organisation);
    }
}
