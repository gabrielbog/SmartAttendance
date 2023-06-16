package com.gabrielbog.smartattendance.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.gabrielbog.smartattendance.R;
import com.gabrielbog.smartattendance.models.LogInCreditentials;
import com.gabrielbog.smartattendance.models.responses.LogInResponse;
import com.gabrielbog.smartattendance.network.RetrofitInterface;
import com.gabrielbog.smartattendance.network.RetrofitService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    //UI Elements
    private LinearLayout loginLayout;
    private LinearLayout loginLoadingLayout;
    private EditText identityBox;
    private EditText passwordBox;
    private Button logInButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        loginLayout = (LinearLayout) findViewById(R.id.loginLayout);
        loginLoadingLayout = (LinearLayout) findViewById(R.id.loginLoadingLayout);
        loginLoadingLayout.setVisibility(View.GONE);
        identityBox = (EditText) findViewById(R.id.identityBox);
        passwordBox = (EditText) findViewById(R.id.passwordBox);
        logInButton = (Button) findViewById(R.id.logInButton);

        logInButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE); //hide keyboard
                imm.hideSoftInputFromWindow(loginLayout.getWindowToken(), 0);

                String cnp = identityBox.getText().toString();
                String password = passwordBox.getText().toString();

                if(cnp.equals("") || password.equals("")) {
                    Toast.makeText(LoginActivity.this, "Please fill all the boxes.", Toast.LENGTH_SHORT) .show();
                }
                else {
                    showLoadingScreen();
                    Call<LogInResponse> logInCall = RetrofitService.getInstance().create(RetrofitInterface.class).checkUserExistence(cnp, password);
                    logInCall.enqueue(new Callback<LogInResponse>() {
                        @Override
                        public void onResponse(Call<LogInResponse> call, Response<LogInResponse> response) {

                            if(response.body().getCode() == 1) {
                                hideLoadingScreen();
                                LogInCreditentials.createInstance(response.body()); //this just creates the instance, will be used to get the data nicer in the other activities
                                Intent i = new Intent(LoginActivity.this, MainActivity.class);
                                finish();
                                startActivity(i);
                            }
                            else {
                                hideLoadingScreen();
                                Toast.makeText(LoginActivity.this, "User couldn't be found.", Toast.LENGTH_SHORT) .show();
                            }
                        }

                        @Override
                        public void onFailure(Call<LogInResponse> call, Throwable t) {
                            hideLoadingScreen();
                            Toast.makeText(LoginActivity.this, "An error has occured. Try again later.", Toast.LENGTH_SHORT) .show();
                        }
                    });
                }

                password = null;
                passwordBox.setText("");
            }
        });
    }

    public void showLoadingScreen() {
        loginLayout.setAlpha(0.3f); //make background uninteractable
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        loginLoadingLayout.setVisibility(View.VISIBLE);
        disableFocus();
    }

    public void hideLoadingScreen() {
        loginLayout.setAlpha(1f); //restore background
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        loginLoadingLayout.setVisibility(View.GONE);
        enableFocus();
    }

    public void enableFocus() {
        identityBox.setFocusableInTouchMode(true);
        identityBox.setFocusable(true);
        passwordBox.setFocusableInTouchMode(true);
        passwordBox.setFocusable(true);
        logInButton.setFocusableInTouchMode(true);
        logInButton.setFocusable(true);
    }

    public void disableFocus() {
        identityBox.setFocusableInTouchMode(false);
        identityBox.setFocusable(false);
        passwordBox.setFocusableInTouchMode(false);
        passwordBox.setFocusable(false);
        logInButton.setFocusableInTouchMode(false);
        logInButton.setFocusable(false);
    }
}