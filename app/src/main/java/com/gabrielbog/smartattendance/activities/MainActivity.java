package com.gabrielbog.smartattendance.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;

import com.gabrielbog.smartattendance.R;
import com.gabrielbog.smartattendance.models.LogInResponse;

public class MainActivity extends AppCompatActivity {

    //Permission Code
    private static final int CAMERA_PERMISSION_CODE = 100;

    //UI Elements
    private TextView greetText;
    private Button qrButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent i = getIntent();
        LogInResponse response = (LogInResponse) i.getSerializableExtra("response");

        greetText = (TextView) findViewById(R.id.greetText);
        greetText.setText("Welcome, " + response.getFirstName());

        qrButton = (Button) findViewById(R.id.qrButton);
        if(response.getIsAdmin() == 0) //student
            qrButton.setText("Scan QR Code");
        else
            qrButton.setText("Generate QR Code");

        qrButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(response.getIsAdmin() == 0) { //student

                    if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
                        ActivityCompat.requestPermissions(MainActivity.this, new String[] { Manifest.permission.CAMERA }, CAMERA_PERMISSION_CODE);
                    }
                    else {
                        Intent i = new Intent(MainActivity.this, ScannerActivity.class);
                        startActivity(i);
                    }
                }
                else if(response.getIsAdmin() == 1) { //professor

                    //send request to server, get unique code, start activity and send code as extra
                    Intent i = new Intent(MainActivity.this, GeneratorActivity.class);
                    startActivity(i);
                }
                else { //in case of possible exploits
                    Toast.makeText(MainActivity.this, "Invalid server answer.", Toast.LENGTH_SHORT) .show();
                }
            }
        });
    }

    //e
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent i = new Intent(MainActivity.this, ScannerActivity.class);
                startActivity(i);
            }
            else {
                Toast.makeText(MainActivity.this, "Camera permission is required to scan attendace codes.", Toast.LENGTH_SHORT) .show();
            }
        }
    }
}