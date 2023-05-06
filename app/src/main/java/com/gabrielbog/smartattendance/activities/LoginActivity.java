package com.gabrielbog.smartattendance.activities;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.gabrielbog.smartattendance.R;
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

                //debugText.setText(cnp + " " + password);

                Call<Integer> logInCall = RetrofitService.getInstance().create(RetrofitInterface.class).getUserByCnpAndPassword(cnp, password);
                logInCall.enqueue(new Callback<Integer>() {
                    @Override
                    public void onResponse(Call<Integer> call, Response<Integer> response) {
                        if(response.body() == 1) {
                            debugText.setText("Works");
                        }
                        else {
                            debugText.setText("Not found");
                        }
                    }

                    @Override
                    public void onFailure(Call<Integer> call, Throwable t) {
                        debugText.setText(t.getMessage());
                    }
                });
            }
        });
    }
}