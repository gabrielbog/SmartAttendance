package com.gabrielbog.smartattendance.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.budiyev.android.codescanner.ScanMode;
import com.gabrielbog.smartattendance.R;
import com.gabrielbog.smartattendance.models.LogInResponse;
import com.gabrielbog.smartattendance.models.QrCodeResponse;
import com.gabrielbog.smartattendance.network.RetrofitInterface;
import com.gabrielbog.smartattendance.network.RetrofitService;
import com.google.zxing.Result;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ScannerActivity extends AppCompatActivity {

    //Hardware Elements
    private Vibrator vibrator;

    //UI Elements
    private CodeScannerView scannerView;
    private TextView hintText;
    private CodeScanner codeScanner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);

        Intent i = getIntent();
        int logInId = i.getIntExtra("logInId", 0);

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        scannerView = findViewById(R.id.scannerView);
        hintText = findViewById(R.id.hintText);

        hintText.setText(String.valueOf(logInId));

        codeScanner = new CodeScanner(this, scannerView);
        codeScanner.setAutoFocusEnabled(true);
        codeScanner.setFormats(CodeScanner.ALL_FORMATS);
        //codeScanner.setScanMode(ScanMode.CONTINUOUS);
        codeScanner.startPreview();

        codeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull final Result result) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        if(result.getText().length() == 64) {
                            Call<QrCodeResponse> qrCodeCall = RetrofitService.getInstance().create(RetrofitInterface.class).scanQrCode(logInId, result.getText());
                            qrCodeCall.enqueue(new Callback<QrCodeResponse>() {
                                @Override
                                public void onResponse(Call<QrCodeResponse> call, Response<QrCodeResponse> response) {
                                    hintText.setText(response.body().getQrString());
                                }

                                @Override
                                public void onFailure(Call<QrCodeResponse> call, Throwable t) {
                                    hintText.setText("Try again later.");
                                }
                            });
                        }
                        else {
                            hintText.setText("Invalid QR code."); //done here so the server isn't overloaded with requests
                        }

                        if(Build.VERSION.SDK_INT >= 26) {
                            vibrator.vibrate(VibrationEffect.createOneShot(250, VibrationEffect.DEFAULT_AMPLITUDE));
                        }
                        else {
                            vibrator.vibrate(250);
                        }
                    }
                });
            }
        });
    }
}