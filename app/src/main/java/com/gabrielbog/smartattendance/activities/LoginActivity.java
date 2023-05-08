package com.gabrielbog.smartattendance.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.gabrielbog.smartattendance.R;
import com.gabrielbog.smartattendance.models.LogInResponse;
import com.gabrielbog.smartattendance.models.User;
import com.gabrielbog.smartattendance.network.RetrofitInterface;
import com.gabrielbog.smartattendance.network.RetrofitService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Field;

public class LoginActivity extends AppCompatActivity {

    //UI Elements
    private EditText identityBox;
    private EditText passwordBox;
    private Button logInButton;
    private TextView debugText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        identityBox = (EditText) findViewById(R.id.identityBox);
        passwordBox = (EditText) findViewById(R.id.passwordBox);
        logInButton = (Button) findViewById(R.id.logInButton);
        debugText = (TextView) findViewById(R.id.debugText);

        logInButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String cnp = identityBox.getText().toString();
                String password = passwordBox.getText().toString();

                //replace debugText with toasts
                if(cnp.equals("") || password.equals("")) {
                    debugText.setText("Please fill all boxes");
                }
                else {
                    Call<LogInResponse> logInCall = RetrofitService.getInstance().create(RetrofitInterface.class).getUserByCnpAndPassword(cnp, password);
                    logInCall.enqueue(new Callback<LogInResponse>() {
                        @Override
                        public void onResponse(Call<LogInResponse> call, Response<LogInResponse> response) {
                            if(response.body().getCode() == 1) {
                                Intent i = new Intent(LoginActivity.this, MainActivity.class);
                                i.putExtra("response", response.body());
                                finish();
                                startActivity(i);
                            }
                            else {
                                debugText.setText("Not found");
                            }
                        }

                        @Override
                        public void onFailure(Call<LogInResponse> call, Throwable t) {
                            debugText.setText("Try again later");
                        }
                    });
                }

                password = null;
                passwordBox.setText("");
            }
        });
    }
}