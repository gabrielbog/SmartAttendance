package com.gabrielbog.smartattendance.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TextView;

import com.gabrielbog.smartattendance.R;
import com.gabrielbog.smartattendance.models.LogInCreditentials;
import com.gabrielbog.smartattendance.models.responses.QrCodeResponse;
import com.gabrielbog.smartattendance.network.RetrofitInterface;
import com.gabrielbog.smartattendance.network.RetrofitService;

import org.apache.poi.sl.usermodel.Line;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GeneratorActivity extends AppCompatActivity {

    //Variables
    private LogInCreditentials logInCreditentials;
    private QrCodeResponse qrCodeResponse;

    //Timer Variables
    private CountDownTimer countDownTimer;
    private long duration;

    //UI Elements
    private LinearLayout generatorLayout;
    private LinearLayout generatorLoadingLayout;
    private TextView subjectText;
    private TextView professorText;
    private TextView grupText;
    private TextView availabilityText;
    private TextView timerText;
    private Space generatorSpace1;
    private Space generatorSpace2;
    private ImageView qrCodeView;
    private QRGEncoder qrgEncoder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generator);


        Intent i = getIntent();
        qrCodeResponse = (QrCodeResponse) i.getSerializableExtra("response");
        logInCreditentials = LogInCreditentials.getInstance();

        duration = qrCodeResponse.getDuration();

        generatorLayout = (LinearLayout) findViewById(R.id.generatorLayout);
        generatorLoadingLayout = (LinearLayout) findViewById(R.id.generatorLoadingLayout);
        generatorLoadingLayout.setVisibility(View.GONE);
        subjectText = (TextView) findViewById(R.id.subjectText);
        subjectText.setText("Professor: " + logInCreditentials.getLogInResponse().getLastName() + " " + logInCreditentials.getLogInResponse().getFirstName());
        professorText = (TextView) findViewById(R.id.professorText);
        professorText.setText("Subject: " + qrCodeResponse.getSubjectString());
        grupText = (TextView) findViewById(R.id.grupText);
        if(qrCodeResponse.getGrup() != 0) {
            grupText.setText("Group: " + qrCodeResponse.getGrup());
        }
        else {
            grupText.setText("All Groups");
        }
        availabilityText = (TextView) findViewById(R.id.availabilityText);
        timerText = (TextView) findViewById(R.id.timerText);
        generatorSpace1 = (Space) findViewById(R.id.generatorSpace1);
        generatorSpace2 = (Space) findViewById(R.id.generatorSpace2);
        qrCodeView = (ImageView) findViewById(R.id.qrCodeView);

        setQrCodeImage(qrCodeResponse.getQrString());
        startCountdownTimer();
    }

    public void setQrCodeImage(String code) {

        WindowManager manager = (WindowManager) getSystemService(WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);

        int width = point.x;
        int height = point.y;

        int qrDimension = 0;
        if (width < height) {
            qrDimension = width;
        }
        else {
            qrDimension = height;
        }

        qrgEncoder = new QRGEncoder(code, null, QRGContents.Type.TEXT, qrDimension);
        Bitmap bitmap = qrgEncoder.getBitmap(0);
        qrCodeView.setImageBitmap(bitmap);
    }

    public void startCountdownTimer() {
        countDownTimer = new CountDownTimer(duration, 1000) {
            @Override
            public void onTick(long durationLeft) {
                duration = durationLeft;
                updateTimerText();
            }

            @Override
            public void onFinish() {
                showLoadingScreen();
                Call<QrCodeResponse> qrCodeCall = RetrofitService.getInstance().create(RetrofitInterface.class).refreshQrCode(logInCreditentials.getLogInResponse().getId(), qrCodeResponse.getQrString());
                qrCodeCall.enqueue(new Callback<QrCodeResponse>() {
                    @Override
                    public void onResponse(Call<QrCodeResponse> call, Response<QrCodeResponse> response) {
                        if(response.body().getCode() == 2) {
                            hideLoadingScreen();
                            duration = response.body().getDuration();
                            updateTimerText();
                            setQrCodeImage(response.body().getQrString());
                            startCountdownTimer();
                        }
                        else {
                            hideLoadingScreen();
                            disableQrElements(response.body().getQrString());
                        }
                    }

                    @Override
                    public void onFailure(Call<QrCodeResponse> call, Throwable t) {
                        hideLoadingScreen();
                        disableQrElements("An error has occured. Please try again later.");
                    }
                });
            }
        }.start();
    }

    public void updateTimerText() {

        int minutes = (int) (duration / 1000) / 60;
        int seconds = (int) (duration / 1000) % 60;
        String timeLeft = String.format("%d:%02d", minutes, seconds);
        timerText.setText(timeLeft);
    }

    public void disableQrElements(String message) {
        subjectText.setText(message);
        subjectText.setTextColor(Color.RED);
        professorText.setVisibility(View.GONE);
        grupText.setVisibility(View.GONE);
        availabilityText.setVisibility(View.GONE);
        timerText.setVisibility(View.GONE);
        generatorSpace1.setVisibility(View.GONE);
        generatorSpace2.setVisibility(View.GONE);
        qrCodeView.setVisibility(View.GONE);
    }

    public void showLoadingScreen() {
        generatorLayout.setAlpha(0.3f); //make background uninteractable
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        generatorLoadingLayout.setVisibility(View.VISIBLE);
        disableFocus();
    }

    public void hideLoadingScreen() {
        generatorLayout.setAlpha(1f); //restore background
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        generatorLoadingLayout.setVisibility(View.GONE);
        enableFocus();
    }

    public void enableFocus() {
        //focus
    }

    public void disableFocus() {
        //unfocus
    }
}