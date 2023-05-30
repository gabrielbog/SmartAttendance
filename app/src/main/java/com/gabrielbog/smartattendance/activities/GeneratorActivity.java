package com.gabrielbog.smartattendance.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.gabrielbog.smartattendance.R;
import com.gabrielbog.smartattendance.models.LogInCreditentials;
import com.gabrielbog.smartattendance.models.responses.QrCodeResponse;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

public class GeneratorActivity extends AppCompatActivity {

    // UI Elements
    private TextView subjectText;
    private TextView professorText;
    private ImageView qrCodeView;
    private QRGEncoder qrgEncoder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generator);

        Intent i = getIntent();
        QrCodeResponse response = (QrCodeResponse) i.getSerializableExtra("response");
        LogInCreditentials logInCreditentials = LogInCreditentials.getInstance();

        subjectText = (TextView) findViewById(R.id.subjectText);
        subjectText.setText("Professor: " + logInCreditentials.getLogInResponse().getLastName() + " " + logInCreditentials.getLogInResponse().getFirstName());
        professorText = (TextView) findViewById(R.id.professorText);
        professorText.setText("Subject: " + response.getAdditionalString());
        qrCodeView = (ImageView) findViewById(R.id.qrCodeView);

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

        qrgEncoder = new QRGEncoder(response.getQrString(), null, QRGContents.Type.TEXT, qrDimension); //replace shown qr code with unique code
        Bitmap bitmap = qrgEncoder.getBitmap();
        qrCodeView.setImageBitmap(bitmap);
    }
}