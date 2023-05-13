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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        identityBox = (EditText) findViewById(R.id.identityBox);
        passwordBox = (EditText) findViewById(R.id.passwordBox);
        logInButton = (Button) findViewById(R.id.logInButton);

        logInButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String cnp = identityBox.getText().toString();
                String password = passwordBox.getText().toString();

                //replace debugText with toasts
                if(cnp.equals("") || password.equals("")) {
                    Toast.makeText(LoginActivity.this, "Please fill all the boxes.", Toast.LENGTH_SHORT) .show();
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
                                Toast.makeText(LoginActivity.this, "User couldn't be found.", Toast.LENGTH_SHORT) .show();
                            }
                        }

                        @Override
                        public void onFailure(Call<LogInResponse> call, Throwable t) {
                            Toast.makeText(LoginActivity.this, "An error has occured. Try again later.", Toast.LENGTH_SHORT) .show();
                        }
                    });
                }

                password = null;
                passwordBox.setText("");
            }
        });
    }
}