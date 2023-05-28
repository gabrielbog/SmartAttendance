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
import com.gabrielbog.smartattendance.models.QrCodeResponse;
import com.gabrielbog.smartattendance.network.RetrofitInterface;
import com.gabrielbog.smartattendance.network.RetrofitService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
        LogInResponse logInResponse = (LogInResponse) i.getSerializableExtra("response");

        //create a static class so that it can't be edited by possible exploiters

        greetText = (TextView) findViewById(R.id.greetText);
        greetText.setText("Welcome, " + logInResponse.getFirstName());

        qrButton = (Button) findViewById(R.id.qrButton);
        if(logInResponse.getIsAdmin() == 0) //student
            qrButton.setText("Scan QR Code");
        else
            qrButton.setText("Generate QR Code");

        qrButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(logInResponse.getIsAdmin() == 0) { //student

                    if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
                        ActivityCompat.requestPermissions(MainActivity.this, new String[] { Manifest.permission.CAMERA }, CAMERA_PERMISSION_CODE);
                    }
                    else {
                        Intent i = new Intent(MainActivity.this, ScannerActivity.class);
                        i.putExtra("logInId", logInResponse.getId());
                        startActivity(i);
                    }
                }
                else if(logInResponse.getIsAdmin() == 1) { //professor

                    //disable button functionality

                    Call<QrCodeResponse> qrCodeCall = RetrofitService.getInstance().create(RetrofitInterface.class).generateQrCode(logInResponse.getId());
                    qrCodeCall.enqueue(new Callback<QrCodeResponse>() {
                        @Override
                        public void onResponse(Call<QrCodeResponse> call, Response<QrCodeResponse> response) {

                            if(response.body().getCode() == 2) {
                                Intent i = new Intent(MainActivity.this, GeneratorActivity.class);
                                i.putExtra("response", response.body());
                                startActivity(i);
                            }
                            else if(response.body().getCode() == 3) {
                                //add more else cases when a professor doesn't have schedule at the time of the request
                                Toast.makeText(MainActivity.this, response.body().getQrString(), Toast.LENGTH_SHORT) .show();
                            }
                            else {
                                //add more else cases when a professor doesn't have schedule at the time of the request
                                Toast.makeText(MainActivity.this, "Invalid request.", Toast.LENGTH_SHORT) .show();
                            }
                        }

                        @Override
                        public void onFailure(Call<QrCodeResponse> call, Throwable t) {
                            Toast.makeText(MainActivity.this, "An error has occured. Try again later.", Toast.LENGTH_SHORT) .show();
                        }
                    });

                    //enable button functionality
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