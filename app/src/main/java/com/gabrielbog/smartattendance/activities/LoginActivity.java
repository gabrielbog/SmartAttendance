package com.gabrielbog.smartattendance.activities;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.gabrielbog.smartattendance.R;

public class LoginActivity extends AppCompatActivity {

    //UI Elements
    private Button logInButton;
    private TextView debugText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Button logInButton = (Button) findViewById(R.id.logInButton);
        TextView debugText = (TextView) findViewById(R.id.debugText);

        logInButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                debugText.setText("Button is working");
            }
        });
    }
}