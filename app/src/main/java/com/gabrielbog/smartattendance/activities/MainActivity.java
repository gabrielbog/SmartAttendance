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
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;

import com.gabrielbog.smartattendance.R;
import com.gabrielbog.smartattendance.models.LogInCreditentials;
import com.gabrielbog.smartattendance.models.responses.QrCodeResponse;
import com.gabrielbog.smartattendance.network.RetrofitInterface;
import com.gabrielbog.smartattendance.network.RetrofitService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    //Permission Code
    private static final int CAMERA_PERMISSION_CODE = 100;

    //UI Elements
    private LinearLayout mainLayout;
    private LinearLayout mainLoadingLayout;
    private TextView greetText;
    private Button qrButton;
    private Button attendanceButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LogInCreditentials logInCreditentials = LogInCreditentials.getInstance();

        mainLayout = (LinearLayout) findViewById(R.id.mainLayout);
        mainLoadingLayout = (LinearLayout) findViewById(R.id.mainLoadingLayout);
        mainLoadingLayout.setVisibility(View.GONE);
        greetText = (TextView) findViewById(R.id.greetText);
        greetText.setText("Welcome, " + logInCreditentials.getLogInResponse().getFirstName());
        qrButton = (Button) findViewById(R.id.qrButton);
        attendanceButton = (Button) findViewById(R.id.attendanceButton);
        if(logInCreditentials.getLogInResponse().getIsAdmin() == 0) {
            //student
            qrButton.setText("Scan QR Code");
            attendanceButton.setText("View Subject Attendance");
        }
        else {
            qrButton.setText("Generate QR Code");
            attendanceButton.setText("View Student Attendance");
        }

        qrButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(logInCreditentials.getLogInResponse().getIsAdmin() == 0) { //student

                    if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
                        ActivityCompat.requestPermissions(MainActivity.this, new String[] { Manifest.permission.CAMERA }, CAMERA_PERMISSION_CODE);
                    }
                    else {
                        Intent i = new Intent(MainActivity.this, ScannerActivity.class);
                        startActivity(i);
                    }
                }
                else if(logInCreditentials.getLogInResponse().getIsAdmin() == 1) { //professor

                    showLoadingScreen();
                    Call<QrCodeResponse> qrCodeCall = RetrofitService.getInstance().create(RetrofitInterface.class).generateQrCode(logInCreditentials.getLogInResponse().getId());
                    qrCodeCall.enqueue(new Callback<QrCodeResponse>() {
                        @Override
                        public void onResponse(Call<QrCodeResponse> call, Response<QrCodeResponse> response) {

                            if(response.body().getCode() == 2) {
                                hideLoadingScreen();
                                Intent i = new Intent(MainActivity.this, GeneratorActivity.class);
                                i.putExtra("response", response.body());
                                startActivity(i);
                            }
                            else if(response.body().getCode() == 3) {
                                hideLoadingScreen();
                                Toast.makeText(MainActivity.this, response.body().getQrString(), Toast.LENGTH_SHORT) .show();
                            }
                            else {
                                hideLoadingScreen();
                                Toast.makeText(MainActivity.this, "Invalid request.", Toast.LENGTH_SHORT) .show();
                            }
                        }

                        @Override
                        public void onFailure(Call<QrCodeResponse> call, Throwable t) {
                            hideLoadingScreen();
                            Toast.makeText(MainActivity.this, "An error has occured. Try again later.", Toast.LENGTH_SHORT) .show();
                        }
                    });
                }
                else { //in case of possible exploits
                    Toast.makeText(MainActivity.this, "Invalid server answer.", Toast.LENGTH_SHORT) .show();
                }
            }
        });

        attendanceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(logInCreditentials.getLogInResponse().getIsAdmin() == 0) { //student
                    Intent i = new Intent(MainActivity.this, StudentAttendanceActivity.class);
                    startActivity(i);
                }
                else if(logInCreditentials.getLogInResponse().getIsAdmin() == 1) { //professor
                    Intent i = new Intent(MainActivity.this, ProfessorAttendanceActivity.class);
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

    public void showLoadingScreen() {
        mainLayout.setAlpha(0.3f); //make background uninteractable
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        mainLoadingLayout.setVisibility(View.VISIBLE);
        disableFocus();
    }

    public void hideLoadingScreen() {
        mainLayout.setAlpha(1f); //restore background
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        mainLoadingLayout.setVisibility(View.GONE);
        enableFocus();
    }

    public void enableFocus() {
        qrButton.setFocusableInTouchMode(true);
        qrButton.setFocusable(true);
        attendanceButton.setFocusableInTouchMode(true);
        attendanceButton.setFocusable(true);
    }

    public void disableFocus() {
        qrButton.setFocusableInTouchMode(false);
        qrButton.setFocusable(false);
        attendanceButton.setFocusableInTouchMode(false);
        attendanceButton.setFocusable(false);
    }
}