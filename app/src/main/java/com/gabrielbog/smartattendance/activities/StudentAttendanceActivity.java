package com.gabrielbog.smartattendance.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.gabrielbog.smartattendance.R;
import com.gabrielbog.smartattendance.models.LogInCreditentials;
import com.gabrielbog.smartattendance.models.StudentAttendance;
import com.gabrielbog.smartattendance.models.Subject;
import com.gabrielbog.smartattendance.models.responses.QrCodeResponse;
import com.gabrielbog.smartattendance.models.responses.StudentAttendanceResponse;
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
    private ListView studentAttendanceListView;

    //Lists
    private List<Subject> subjectList;
    private List<StudentAttendance> attendanceList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_attendance);

        LogInCreditentials logInCreditentials = LogInCreditentials.getInstance();

        studentAttendanceLayout = (RelativeLayout) findViewById(R.id.studentAttendanceLayout);
        studentAttendanceLoadingLayout = (LinearLayout) findViewById(R.id.studentAttendanceLoadingLayout);
        studentSubjectSpinner = (Spinner) findViewById(R.id.studentSubjectSpinner);
        studentAttendanceListView = (ListView) findViewById(R.id.studentAttendanceListView);

        subjectList = new ArrayList<>();
        attendanceList = new ArrayList<>();

        showLoadingScreen();
        Call<SubjectListResponse> subjectListResponseCall = RetrofitService.getInstance().create(RetrofitInterface.class).getSubjectList(logInCreditentials.getLogInResponse().getId());
        subjectListResponseCall.enqueue(new Callback<SubjectListResponse>() {
            @Override
            public void onResponse(Call<SubjectListResponse> call, Response<SubjectListResponse> response) {
                if(response.body().getCode() == 1) {
                    hideLoadingScreen();
                    setSubjectList(response.body().getSubjectList());
                    ArrayAdapter<Subject> subjectArrayAdapter = new ArrayAdapter<Subject>(StudentAttendanceActivity.this, android.R.layout.simple_spinner_item, subjectList);
                    subjectArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    studentSubjectSpinner.setAdapter(subjectArrayAdapter);
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

                if(subjectList.size() > 0) { //only do this if the list isn't empty
                    if(i > 0) {
                        showLoadingScreen();
                        Call<StudentAttendanceResponse> studentAttendanceResponseCall = RetrofitService.getInstance().create(RetrofitInterface.class).getSubjectAttendanceList(logInCreditentials.getLogInResponse().getId(), subjectList.get(i).getId());
                        studentAttendanceResponseCall.enqueue(new Callback<StudentAttendanceResponse>() {
                            @Override
                            public void onResponse(Call<StudentAttendanceResponse> call, Response<StudentAttendanceResponse> response) {
                                if(response.body().getCode() == 1) {
                                    hideLoadingScreen();
                                    setAttendanceList(response.body().getStudentAttendanceList());
                                    ArrayAdapter<StudentAttendance> studentAttendanceArrayAdapter = new ArrayAdapter<StudentAttendance>(StudentAttendanceActivity.this, android.R.layout.simple_list_item_1, attendanceList);
                                    studentAttendanceArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                    studentAttendanceListView.setAdapter(studentAttendanceArrayAdapter);
                                }
                            }

                            @Override
                            public void onFailure(Call<StudentAttendanceResponse> call, Throwable t) {
                                hideLoadingScreen();
                                System.out.println(t.getMessage());
                                Toast.makeText(StudentAttendanceActivity.this, "An error has occured. Try again later.", Toast.LENGTH_SHORT) .show();
                            }
                        });
                    }
                }
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
        subjectList.add(0, new Subject(-1, "Select a subject", 0, 0, "", 0, 0, 0));
        this.subjectList = subjectList;
    }

    public void setAttendanceList(List<StudentAttendance> attendanceList) {
        this.attendanceList = attendanceList;
    }
}