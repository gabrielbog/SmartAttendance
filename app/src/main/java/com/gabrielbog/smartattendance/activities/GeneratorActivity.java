package com.gabrielbog.smartattendance.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.WindowManager;
import android.widget.ImageView;

import com.gabrielbog.smartattendance.R;
import com.gabrielbog.smartattendance.models.LogInResponse;
import com.gabrielbog.smartattendance.models.QrCodeResponse;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

public class GeneratorActivity extends AppCompatActivity {

    // UI Elements

    private ImageView qrCodeView;
    QRGEncoder qrgEncoder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generator);

        Intent i = getIntent();
        QrCodeResponse response = (QrCodeResponse) i.getSerializableExtra("response");

        qrCodeView = findViewById(R.id.qrCodeView);

        WindowManager manager = (WindowManager) getSystemService(WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);

        int width = point.x;
        int height = point.y;

        int qrDimension = 0;
        if (width < height) {
            qrDimension = width * 3 / 4;
        }
        else {
            qrDimension = height * 3 / 4;
        }

        qrgEncoder = new QRGEncoder(response.getQrString(), null, QRGContents.Type.TEXT, qrDimension); //replace shown qr code with unique code
        Bitmap bitmap = qrgEncoder.getBitmap();
        qrCodeView.setImageBitmap(bitmap);
    }
}