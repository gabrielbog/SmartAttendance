package com.gabrielbog.smartattendance.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.gabrielbog.smartattendance.R;
import com.gabrielbog.smartattendance.models.LogInCreditentials;
import com.gabrielbog.smartattendance.models.responses.QrCodeResponse;
import com.gabrielbog.smartattendance.network.RetrofitInterface;
import com.gabrielbog.smartattendance.network.RetrofitService;
import com.google.zxing.Result;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ScannerActivity extends AppCompatActivity {

    //Variables
    private int buttonState;

    //Hardware Elements
    private Vibrator vibrator;

    //UI Elements
    private RelativeLayout scannerLayout;
    private LinearLayout scannerLoadingLayout;
    private CodeScannerView scannerView;
    private TextView hintText;
    private Button resultButton;
    private CodeScanner codeScanner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);

        LogInCreditentials logInCreditentials = LogInCreditentials.getInstance();

        buttonState = 0;
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        scannerLayout = (RelativeLayout) findViewById(R.id.scannerLayout);
        scannerLoadingLayout = (LinearLayout) findViewById(R.id.scannerLoadingLayout);
        scannerLoadingLayout.setVisibility(View.GONE);
        scannerView = findViewById(R.id.scannerView);
        hintText = findViewById(R.id.hintText);
        hintText.setText("Please scan the QR Code provided by the professor.");
        resultButton = (Button) findViewById(R.id.resultButton);
        resultButton.setVisibility(View.GONE);

        codeScanner = new CodeScanner(this, scannerView);
        codeScanner.setAutoFocusEnabled(true);
        codeScanner.setFormats(CodeScanner.ALL_FORMATS);
        codeScanner.startPreview();

        codeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull final Result result) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        if(result.getText().length() == 64) {

                            showLoadingScreen();
                            Call<QrCodeResponse> qrCodeCall = RetrofitService.getInstance().create(RetrofitInterface.class).scanQrCode(logInCreditentials.getLogInResponse().getId(), result.getText());
                            qrCodeCall.enqueue(new Callback<QrCodeResponse>() {
                                @Override
                                public void onResponse(Call<QrCodeResponse> call, Response<QrCodeResponse> response) {
                                    hideLoadingScreen();
                                    resultButton.setVisibility(View.VISIBLE);
                                    hintText.setText(response.body().getQrString());

                                    int buttonState = response.body().getCode();
                                    setButtonState(buttonState);
                                    if(buttonState == 2) {
                                        resultButton.setText("Return to the main page");
                                    }
                                    else {
                                        resultButton.setText("Scan again");
                                    }
                                }

                                @Override
                                public void onFailure(Call<QrCodeResponse> call, Throwable t) {
                                    hideLoadingScreen();
                                    resultButton.setVisibility(View.VISIBLE);
                                    hintText.setText("Try again later.");

                                    setButtonState(0);
                                    resultButton.setText("Scan again");
                                }
                            });
                        }
                        else {
                            hideLoadingScreen();
                            resultButton.setVisibility(View.VISIBLE);
                            hintText.setText("Invalid QR code."); //done here so the server isn't overloaded with requests

                            setButtonState(0);
                            resultButton.setText("Scan again");
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

        resultButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(buttonState == 2) {
                    finish();
                }
                else {
                    hintText.setText("Please scan the QR Code provided by the professor.");
                    codeScanner.startPreview();
                    resultButton.setVisibility(View.GONE);
                }
            }
        });
    }

    public void setButtonState(int buttonState) {
        this.buttonState = buttonState;
    }

    public void showLoadingScreen() {
        scannerLayout.setAlpha(0.3f); //make background uninteractable
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        scannerLoadingLayout.setVisibility(View.VISIBLE);
        disableFocus();
    }

    public void hideLoadingScreen() {
        scannerLayout.setAlpha(1f); //restore background
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        scannerLoadingLayout.setVisibility(View.GONE);
        enableFocus();
    }

    public void enableFocus() {
        resultButton.setFocusableInTouchMode(true);
        resultButton.setFocusable(true);
    }

    public void disableFocus() {
        resultButton.setFocusableInTouchMode(false);
        resultButton.setFocusable(false);
    }
}