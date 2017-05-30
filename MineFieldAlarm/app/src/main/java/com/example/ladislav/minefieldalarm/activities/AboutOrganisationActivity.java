package com.example.ladislav.minefieldalarm.activities;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.ladislav.minefieldalarm.R;

public class AboutOrganisationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_organisation);

    }

    public void sendEmail(View view) {

        EditText message = (EditText)findViewById(R.id.message);
        EditText subject = (EditText)findViewById(R.id.emailSubject);

        Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto","email@email.com", null));
        intent.putExtra(Intent.EXTRA_SUBJECT, subject.toString());
        intent.putExtra(Intent.EXTRA_TEXT, message.toString());
        startActivity(Intent.createChooser(intent, "Choose an Email client :"));
    }
}

