package com.gabrielbog.smartattendance.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import com.gabrielbog.smartattendance.R;
import com.gabrielbog.smartattendance.models.LogInResponse;

public class MainActivity extends AppCompatActivity {

    //UI Elements
    private TextView greetText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent i = getIntent();
        LogInResponse response = (LogInResponse) i.getSerializableExtra("response");

        greetText = (TextView) findViewById(R.id.greetText);
        greetText.setText("Welcome, " + response.getFirstName());
    }
}