package com.gabrielbog.smartattendance.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.gabrielbog.smartattendance.R;
import com.gabrielbog.smartattendance.models.LogInCreditentials;
import com.gabrielbog.smartattendance.models.ScheduleCalendar;
import com.gabrielbog.smartattendance.models.StudentAttendance;
import com.gabrielbog.smartattendance.models.Subject;
import com.gabrielbog.smartattendance.models.responses.ScheduleCalendarResponse;
import com.gabrielbog.smartattendance.models.responses.StudentAttendanceResponse;
import com.gabrielbog.smartattendance.models.responses.SubjectListResponse;
import com.gabrielbog.smartattendance.network.RetrofitInterface;
import com.gabrielbog.smartattendance.network.RetrofitService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfessorAttendanceActivity extends AppCompatActivity {

    //UI Elements
    private RelativeLayout professorAttendanceLayout;
    private LinearLayout professorAttendanceLoadingLayout;
    private Spinner professorSubjectSpinner;
    private Spinner professorDateSpinner;
    private ListView professorAttendanceListView;
    private LinearLayout professorButtonLayout;
    private Button currentDateExcelButton;
    private Button totalExcelButton;

    //Lists
    private List<Subject> subjectList;
    private List<ScheduleCalendar> scheduleCalendarList;
    private List<StudentAttendance> attendanceList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_professor_attendance);

        LogInCreditentials logInCreditentials = LogInCreditentials.getInstance();

        professorAttendanceLayout = (RelativeLayout) findViewById(R.id.professorAttendanceLayout);
        professorAttendanceLoadingLayout = (LinearLayout) findViewById(R.id.professorAttendanceLoadingLayout);
        professorSubjectSpinner = (Spinner) findViewById(R.id.professorSubjectSpinner);
        professorDateSpinner = (Spinner) findViewById(R.id.professorDateSpinner);
        professorDateSpinner.setVisibility(View.GONE);
        professorAttendanceListView = (ListView) findViewById(R.id.professorAttendanceListView);
        professorButtonLayout = (LinearLayout) findViewById(R.id.professorButtonLayout);
        professorButtonLayout.setVisibility(View.GONE);
        currentDateExcelButton = (Button) findViewById(R.id.currentDateExcelButton);
        totalExcelButton = (Button) findViewById(R.id.totalExcelButton);

        showLoadingScreen();
        Call<SubjectListResponse> subjectListResponseCall = RetrofitService.getInstance().create(RetrofitInterface.class).getSubjectList(logInCreditentials.getLogInResponse().getId());
        subjectListResponseCall.enqueue(new Callback<SubjectListResponse>() {
            @Override
            public void onResponse(Call<SubjectListResponse> call, Response<SubjectListResponse> response) {
                if(response.body().getCode() == 1) {
                    hideLoadingScreen();
                    setSubjectList(response.body().getSubjectList());
                    ArrayAdapter<Subject> subjectArrayAdapter = new ArrayAdapter<Subject>(ProfessorAttendanceActivity.this, android.R.layout.simple_spinner_item, subjectList);
                    subjectArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    professorSubjectSpinner.setAdapter(subjectArrayAdapter);
                }
                else {
                    hideLoadingScreen();
                    Toast.makeText(ProfessorAttendanceActivity.this, "Server Error", Toast.LENGTH_SHORT) .show(); //change
                }
            }

            @Override
            public void onFailure(Call<SubjectListResponse> call, Throwable t) {
                hideLoadingScreen();
                Toast.makeText(ProfessorAttendanceActivity.this, "An error has occured. Try again later.", Toast.LENGTH_SHORT) .show();
            }
        });

        professorSubjectSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(subjectList.size() > 0) { //only do this if the list isn't empty
                    if(i > 0) {
                        //reset date spinner and attendance list
                        showLoadingScreen();
                        Call<ScheduleCalendarResponse> scheduleCalendarResponseCall = RetrofitService.getInstance().create(RetrofitInterface.class).getScheduleCalendar(logInCreditentials.getLogInResponse().getId(), subjectList.get(i).getId());
                        scheduleCalendarResponseCall.enqueue(new Callback<ScheduleCalendarResponse>() {
                            @Override
                            public void onResponse(Call<ScheduleCalendarResponse> call, Response<ScheduleCalendarResponse> response) {
                                if(response.body().getCode() == 1) {
                                    hideLoadingScreen();
                                    setScheduleCalendarList(response.body().getScheduleCalendarList());
                                    ArrayAdapter<ScheduleCalendar> scheduleCalendarArrayAdapter = new ArrayAdapter<ScheduleCalendar>(ProfessorAttendanceActivity.this, android.R.layout.simple_list_item_1, scheduleCalendarList);
                                    scheduleCalendarArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                    professorDateSpinner.setAdapter(scheduleCalendarArrayAdapter);
                                }
                            }

                            @Override
                            public void onFailure(Call<ScheduleCalendarResponse> call, Throwable t) {
                                hideLoadingScreen();
                                System.out.println(t.getMessage());
                                Toast.makeText(ProfessorAttendanceActivity.this, "An error has occured. Try again later.", Toast.LENGTH_SHORT) .show();
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
        professorAttendanceLayout.setAlpha(0.3f); //make background uninteractable
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        professorAttendanceLoadingLayout.setVisibility(View.VISIBLE);
        disableFocus();
    }

    public void hideLoadingScreen() {
        professorAttendanceLayout.setAlpha(1f); //restore background
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        professorAttendanceLoadingLayout.setVisibility(View.GONE);
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

    public void setScheduleCalendarList(List<ScheduleCalendar> scheduleCalendarList) {
        scheduleCalendarList.add(0, new ScheduleCalendar(-1, null, null, null, 0));
        this.scheduleCalendarList = scheduleCalendarList;
    }

    public void setAttendanceList(List<StudentAttendance> attendanceList) {
        this.attendanceList = attendanceList;
    }
}