package com.gabrielbog.smartattendance.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.gabrielbog.smartattendance.R;
import com.gabrielbog.smartattendance.models.LogInCreditentials;
import com.gabrielbog.smartattendance.models.Subject;
import com.gabrielbog.smartattendance.models.responses.QrCodeResponse;
import com.gabrielbog.smartattendance.models.responses.SubjectListResponse;
import com.gabrielbog.smartattendance.network.RetrofitInterface;
import com.gabrielbog.smartattendance.network.RetrofitService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StudentAttendanceActivity extends AppCompatActivity {

    //UI Elements
    private RelativeLayout studentAttendanceLayout;
    private LinearLayout studentAttendanceLoadingLayout;
    private Spinner studentSubjectSpinner;

    //Lists
    private List<Subject> subjectList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_attendance);

        LogInCreditentials logInCreditentials = LogInCreditentials.getInstance();

        studentAttendanceLayout = (RelativeLayout) findViewById(R.id.studentAttendanceLayout);
        studentAttendanceLoadingLayout = (LinearLayout) findViewById(R.id.studentAttendanceLoadingLayout);
        studentSubjectSpinner = (Spinner) findViewById(R.id.studentSubjectSpinner);

        subjectList = new ArrayList<>();

        showLoadingScreen();
        Call<SubjectListResponse> subjectListResponseCall = RetrofitService.getInstance().create(RetrofitInterface.class).getSubjectList(logInCreditentials.getLogInResponse().getId());
        subjectListResponseCall.enqueue(new Callback<SubjectListResponse>() {
            @Override
            public void onResponse(Call<SubjectListResponse> call, Response<SubjectListResponse> response) {
                if(response.body().getCode() == 1) {
                    hideLoadingScreen();
                    setSubjectList(response.body().getSubjectList());
                    ArrayAdapter<Subject> adapter = new ArrayAdapter<Subject>(StudentAttendanceActivity.this, android.R.layout.simple_spinner_item, subjectList);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    studentSubjectSpinner.setAdapter(adapter);
                }
                else {
                    hideLoadingScreen();
                    Toast.makeText(StudentAttendanceActivity.this, "Server Error", Toast.LENGTH_SHORT) .show(); //change
                }
            }

            @Override
            public void onFailure(Call<SubjectListResponse> call, Throwable t) {
                hideLoadingScreen();
                Toast.makeText(StudentAttendanceActivity.this, "An error has occured. Try again later.", Toast.LENGTH_SHORT) .show();
            }
        });

        studentSubjectSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(StudentAttendanceActivity.this, subjectList.get(i).toString(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    public void showLoadingScreen() {
        studentAttendanceLayout.setAlpha(0.3f); //make background uninteractable
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        studentAttendanceLoadingLayout.setVisibility(View.VISIBLE);
        disableFocus();
    }

    public void hideLoadingScreen() {
        studentAttendanceLayout.setAlpha(1f); //restore background
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        studentAttendanceLoadingLayout.setVisibility(View.GONE);
        enableFocus();
    }

    public void enableFocus() {
        //focus
    }

    public void disableFocus() {
        //unfocus
    }

    public void setSubjectList(List<Subject> subjectList) {
        this.subjectList = subjectList;
    }
}