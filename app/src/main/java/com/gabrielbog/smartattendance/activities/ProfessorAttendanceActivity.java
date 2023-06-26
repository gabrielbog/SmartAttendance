package com.gabrielbog.smartattendance.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.gabrielbog.smartattendance.BuildConfig;
import com.gabrielbog.smartattendance.R;
import com.gabrielbog.smartattendance.constants.Constants;
import com.gabrielbog.smartattendance.models.LogInCreditentials;
import com.gabrielbog.smartattendance.models.ProfessorGrups;
import com.gabrielbog.smartattendance.models.ScheduleCalendar;
import com.gabrielbog.smartattendance.models.StudentAttendance;
import com.gabrielbog.smartattendance.models.Subject;
import com.gabrielbog.smartattendance.models.responses.ProfessorGrupsResponse;
import com.gabrielbog.smartattendance.models.responses.ScheduleCalendarResponse;
import com.gabrielbog.smartattendance.models.responses.StudentAttendanceResponse;
import com.gabrielbog.smartattendance.models.responses.SubjectListResponse;
import com.gabrielbog.smartattendance.network.RetrofitInterface;
import com.gabrielbog.smartattendance.network.RetrofitService;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfessorAttendanceActivity extends AppCompatActivity {

    //Permission Code
    private static final int EXTERNAL_STORAGE_PERMISSION_CODE = 101;

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
    private List<StudentAttendance> totalAttendanceList;
    private List<ProfessorGrups> professorGrupsList;

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
        professorAttendanceListView.setVisibility(View.GONE);
        professorButtonLayout = (LinearLayout) findViewById(R.id.professorButtonLayout);
        professorButtonLayout.setVisibility(View.GONE);
        currentDateExcelButton = (Button) findViewById(R.id.currentDateExcelButton);
        currentDateExcelButton.setText("Generate table for current list");
        totalExcelButton = (Button) findViewById(R.id.totalExcelButton);
        totalExcelButton.setText("Generate table for all schedules");

        subjectList = new ArrayList<>();
        scheduleCalendarList = new ArrayList<>();
        attendanceList = new ArrayList<>();
        totalAttendanceList = new ArrayList<>();
        professorGrupsList = new ArrayList<>();

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
                professorDateSpinner.setVisibility(View.GONE);
                professorAttendanceListView.setVisibility(View.GONE);
                professorButtonLayout.setVisibility(View.GONE);
                if(subjectList.size() > 0) { //only do this if the list isn't empty
                    if(i > 0) {
                        showLoadingScreen();
                        Call<ScheduleCalendarResponse> scheduleCalendarResponseCall = RetrofitService.getInstance().create(RetrofitInterface.class).getScheduleCalendar(logInCreditentials.getLogInResponse().getId(), subjectList.get(i).getId());
                        scheduleCalendarResponseCall.enqueue(new Callback<ScheduleCalendarResponse>() {
                            @Override
                            public void onResponse(Call<ScheduleCalendarResponse> call, Response<ScheduleCalendarResponse> response) {
                                if(response.body().getCode() == 1) {
                                    hideLoadingScreen();
                                    setScheduleCalendarList(response.body().getScheduleCalendarList());
                                    ArrayAdapter<ScheduleCalendar> scheduleCalendarArrayAdapter = new ArrayAdapter<ScheduleCalendar>(ProfessorAttendanceActivity.this, android.R.layout.simple_list_item_1, scheduleCalendarList) {
                                        @Override
                                        public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                                            View view = super.getDropDownView(position, convertView, parent);
                                            TextView textView = (TextView) view;
                                            int[] colorValues = {
                                                    Color.WHITE,
                                                    Color.LTGRAY,
                                                    Color.rgb(153, 153, 153), //dark gray
                                                    Color.LTGRAY
                                            };
                                            int bgColor = colorValues[position % colorValues.length];
                                            textView.setBackgroundColor(bgColor);
                                            return view;
                                        }
                                    };
                                    scheduleCalendarArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                    professorDateSpinner.setAdapter(scheduleCalendarArrayAdapter);
                                    professorDateSpinner.setVisibility(View.VISIBLE);
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
                    else {

                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        professorDateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                professorAttendanceListView.setVisibility(View.GONE);
                professorButtonLayout.setVisibility(View.GONE);
                if(scheduleCalendarList.size() > 0) { //only do this if the list isn't emptys
                    if (i > 0) {
                        showLoadingScreen();
                        Call<StudentAttendanceResponse> studentAttendanceResponseCall = RetrofitService.getInstance().create(RetrofitInterface.class).getAttendingStudentsList(scheduleCalendarList.get(i).getDate(), scheduleCalendarList.get(i).getId());
                        studentAttendanceResponseCall.enqueue(new Callback<StudentAttendanceResponse>() {
                            @Override
                            public void onResponse(Call<StudentAttendanceResponse> call, Response<StudentAttendanceResponse> response) {
                                if(response.body().getCode() == 1) {
                                    hideLoadingScreen();
                                    setAttendanceList(response.body().getStudentAttendanceList());
                                    ArrayAdapter<StudentAttendance> studentAttendanceArrayAdapter = new ArrayAdapter<StudentAttendance>(ProfessorAttendanceActivity.this, android.R.layout.simple_list_item_1, attendanceList);
                                    studentAttendanceArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                    professorAttendanceListView.setAdapter(studentAttendanceArrayAdapter);
                                    professorAttendanceListView.setVisibility(View.VISIBLE);
                                    professorButtonLayout.setVisibility(View.VISIBLE);
                                }
                            }

                            @Override
                            public void onFailure(Call<StudentAttendanceResponse> call, Throwable t) {
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

        currentDateExcelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(ProfessorAttendanceActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED
                || ContextCompat.checkSelfPermission(ProfessorAttendanceActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                    ActivityCompat.requestPermissions(ProfessorAttendanceActivity.this, new String[] { Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE }, EXTERNAL_STORAGE_PERMISSION_CODE);
                }
                else {
                    generateExcelForAttendanceList();
                }
            }
        });

        totalExcelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(ProfessorAttendanceActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                    ActivityCompat.requestPermissions(ProfessorAttendanceActivity.this, new String[] { Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE }, EXTERNAL_STORAGE_PERMISSION_CODE);
                }
                else {
                    Subject subject = (Subject) professorSubjectSpinner.getSelectedItem();
                    if(subject.getType().equals("course")) {
                        generateExcelForTotalAttendanceList(logInCreditentials.getLogInResponse().getId(), subject.getId(), 0);
                    }
                    else {
                        showLoadingScreen();
                        Call<ProfessorGrupsResponse> professorGrupsResponseCall = RetrofitService.getInstance().create(RetrofitInterface.class).getProfessorGrups(logInCreditentials.getLogInResponse().getId(), subject.getId());
                        professorGrupsResponseCall.enqueue(new Callback<ProfessorGrupsResponse>() {
                            @Override
                            public void onResponse(Call<ProfessorGrupsResponse> call, Response<ProfessorGrupsResponse> response) {
                                hideLoadingScreen();
                                setProfessorGrupsList(response.body().getProfessorGrupsList());
                                showGrupAlertBox(logInCreditentials.getLogInResponse().getId(), subject.getId());
                            }

                            @Override
                            public void onFailure(Call<ProfessorGrupsResponse> call, Throwable t) {
                                hideLoadingScreen();
                                Toast.makeText(ProfessorAttendanceActivity.this, "An error has occured. Try again later.", Toast.LENGTH_SHORT) .show();
                            }
                        });
                    }
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == EXTERNAL_STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(ProfessorAttendanceActivity.this, "Please select one of the options again.", Toast.LENGTH_SHORT) .show();
            }
            else {
                Toast.makeText(ProfessorAttendanceActivity.this, "External storage permissions are required to generate tables.", Toast.LENGTH_SHORT) .show();
            }
        }
    }

    public void showGrupAlertBox(int professorId, int subjectId) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ProfessorAttendanceActivity.this);
        alertDialogBuilder.setTitle("Select a group");

        Spinner spinner = new Spinner(ProfessorAttendanceActivity.this);
        ArrayAdapter<ProfessorGrups> professorGrupsArrayAdapter = new ArrayAdapter<ProfessorGrups>(this, android.R.layout.simple_spinner_item, professorGrupsList);
        professorGrupsArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(professorGrupsArrayAdapter);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 50);
        layoutParams.setMargins(480, 0, 480, 0);
        spinner.setLayoutParams(layoutParams);
        alertDialogBuilder.setView(spinner);

        alertDialogBuilder.setPositiveButton("Generate", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                ProfessorGrups element = (ProfessorGrups) spinner.getSelectedItem();
                generateExcelForTotalAttendanceList(professorId, subjectId, element.getGrup());
            }
        });

        alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public void generateExcelForAttendanceList() {

        Subject subject = (Subject) professorSubjectSpinner.getSelectedItem();
        ScheduleCalendar selectedDate = (ScheduleCalendar) professorDateSpinner.getSelectedItem();

        HSSFWorkbook workbook = new HSSFWorkbook();
        Cell cell = null;

        Sheet sheet = workbook.createSheet("Attendance List");
        Row row = sheet.createRow(0);

        cell = row.createCell(0);
        cell.setCellValue("Name");
        cell = row.createCell(1);
        cell.setCellValue("Date");
        cell = row.createCell(2);
        cell.setCellValue("State");

        sheet.setColumnWidth(0, 50 * 120);
        sheet.setColumnWidth(1, 50 * 80);
        sheet.setColumnWidth(2, 50 * 80);

        int i = 1;
        for(StudentAttendance studentAttendance : attendanceList) {

            Row currentRow = sheet.createRow(i);

            cell = currentRow.createCell(0);
            cell.setCellValue(studentAttendance.getLastName() + " " + studentAttendance.getFirstName());
            cell = currentRow.createCell(1);
            cell.setCellValue(selectedDate.getDate().toString());
            cell = currentRow.createCell(2);
            cell.setCellValue(studentAttendance.getState());

            ++i;
        }

        boolean rootFolderResult;
        String rootFolderPath = null;
        if(Build.VERSION.SDK_INT >= 26) { //for android 10 onwards
            rootFolderPath = getExternalFilesDir(null) + File.separator + Constants.BASE_FOLDER_NAME + File.separator + Constants.TABLES_FOLDER_NAME;
        }
        else {
            rootFolderPath = Environment.getExternalStorageDirectory() + File.separator + Constants.BASE_FOLDER_NAME + File.separator + Constants.TABLES_FOLDER_NAME;
        }

        File rootFolder = new File(rootFolderPath);
        if(!rootFolder.exists()) {
            rootFolderResult = rootFolder.mkdirs();
        }
        else {
            rootFolderResult = true;
        }

        if(rootFolderResult == true) {

            String filePath = null;
            String timeStart = selectedDate.getTimeStart().toString();
            timeStart = timeStart.replaceAll(":", ".");
            String timeStop = selectedDate.getTimeStop().toString();
            timeStop = timeStop.replaceAll(":", ".");
            String groupName = null;
            if(selectedDate.getGrup() == 0) {
                groupName = "All Groups";
            }
            else {
                groupName = "Group " + selectedDate.getGrup();
            }

            if(Build.VERSION.SDK_INT >= 26) { //for android 10 onwards
                filePath = getExternalFilesDir(null) + File.separator + Constants.BASE_FOLDER_NAME + File.separator + Constants.TABLES_FOLDER_NAME + File.separator + selectedDate.getDate().toString() + " - " + timeStart + "-" + timeStop + " - " + subject.getName() + " - " + groupName + ".xls";
            }
            else {
                filePath = Environment.getExternalStorageDirectory() + File.separator + Constants.BASE_FOLDER_NAME + File.separator + Constants.TABLES_FOLDER_NAME + File.separator + selectedDate.getDate().toString() + " - " + timeStart + "-" + timeStop + " - " + subject.getName() + " - " + groupName + ".xls";
            }

            System.out.println(filePath);

            try {
                FileOutputStream outputStream = new FileOutputStream(filePath);
                workbook.write(outputStream);

                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW); //start excel right from the app
                    Uri uri = FileProvider.getUriForFile(ProfessorAttendanceActivity.this, ProfessorAttendanceActivity.this.getPackageName() + ".provider", new File(filePath));
                    intent.setDataAndType(uri,"application/vnd.ms-excel");
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    startActivity(intent);
                }
                catch (ActivityNotFoundException e) {
                    Toast.makeText(ProfessorAttendanceActivity.this, "The file has been saved in /" + Constants.BASE_FOLDER_NAME + "/" + Constants.TABLES_FOLDER_NAME + "/.", Toast.LENGTH_SHORT) .show();
                }
            }
            catch (IOException e) {
                Toast.makeText(ProfessorAttendanceActivity.this, "Couldn't create the table file.", Toast.LENGTH_SHORT) .show();
            }
        }
        else {
            Toast.makeText(ProfessorAttendanceActivity.this, "An error has occured. Please try again.", Toast.LENGTH_SHORT) .show();
        }

    }

    public void generateExcelForTotalAttendanceList(int professorId, int subjectId, int grup) {
        showLoadingScreen();
        Call<StudentAttendanceResponse> studentAttendanceResponseCall = RetrofitService.getInstance().create(RetrofitInterface.class).getTotalAttendingStudentsList(professorId, subjectId, grup);
        studentAttendanceResponseCall.enqueue(new Callback<StudentAttendanceResponse>() {
            @Override
            public void onResponse(Call<StudentAttendanceResponse> call, Response<StudentAttendanceResponse> response) {
                hideLoadingScreen();
                setTotalAttendanceList(response.body().getStudentAttendanceList());

                Subject chosenSubject = null;
                for(Subject subject : subjectList) {
                    if(subject.getId() == subjectId) {
                        chosenSubject = subject;
                        break;
                    }
                }

                HSSFWorkbook workbook = new HSSFWorkbook();
                Cell cell = null;

                Sheet sheet = workbook.createSheet("Attendance List");
                Row row = sheet.createRow(0);

                cell = row.createCell(0);
                cell.setCellValue("Name");
                cell = row.createCell(1);
                cell.setCellValue("Date");
                cell = row.createCell(2);
                cell.setCellValue("State");

                sheet.setColumnWidth(0, 50 * 120);
                sheet.setColumnWidth(1, 50 * 200);
                sheet.setColumnWidth(2, 50 * 80);

                int i = 1;
                Date previousDate = totalAttendanceList.get(0).getDate();
                for(StudentAttendance studentAttendance : totalAttendanceList) {

                    if(!previousDate.equals(studentAttendance.getDate())) {
                        previousDate = studentAttendance.getDate();
                        ++i; //make a space between days for consistency
                    }

                    Row currentRow = sheet.createRow(i);

                    cell = currentRow.createCell(0);
                    cell.setCellValue(studentAttendance.getLastName() + " " + studentAttendance.getFirstName());
                    cell = currentRow.createCell(1);
                    cell.setCellValue(studentAttendance.getDate().toString() + " " + studentAttendance.getTimeStart() + "-" + studentAttendance.getTimeStop());
                    cell = currentRow.createCell(2);
                    cell.setCellValue(studentAttendance.getState());

                    ++i;
                }

                boolean rootFolderResult;
                String rootFolderPath = null;
                if(Build.VERSION.SDK_INT >= 29) {
                    rootFolderPath = getExternalFilesDir(null) + File.separator + Constants.BASE_FOLDER_NAME + File.separator + Constants.TABLES_FOLDER_NAME;
                }
                else {
                    rootFolderPath = Environment.getExternalStorageDirectory() + File.separator + Constants.BASE_FOLDER_NAME + File.separator + Constants.TABLES_FOLDER_NAME;
                }

                File rootFolder = new File(rootFolderPath);
                if(!rootFolder.exists()) {
                    rootFolderResult = rootFolder.mkdirs();
                }
                else {
                    rootFolderResult = true;
                }

                if(rootFolderResult == true) {

                    String filePath = null;
                    String groupName = null;

                    if(grup == 0) {
                        groupName = "All Groups";
                    }
                    else {
                        groupName = "Group " + grup;
                    }

                    if(Build.VERSION.SDK_INT >= 26) { //for android 10 onwards
                        filePath = getExternalFilesDir(null) + File.separator + Constants.BASE_FOLDER_NAME + File.separator + Constants.TABLES_FOLDER_NAME + File.separator + "Total Attendance - " + chosenSubject.getName() + " - " + groupName + ".xls";
                    }
                    else {
                        filePath = Environment.getExternalStorageDirectory() + File.separator + Constants.BASE_FOLDER_NAME + File.separator + Constants.TABLES_FOLDER_NAME + File.separator + "Total Attendance - " + chosenSubject.getName() + " - " + groupName + ".xls";
                    }

                    try {
                        FileOutputStream outputStream = new FileOutputStream(filePath);
                        workbook.write(outputStream);

                        try {
                            Intent intent = new Intent(Intent.ACTION_VIEW); //start excel right from the app
                            Uri uri = FileProvider.getUriForFile(ProfessorAttendanceActivity.this, ProfessorAttendanceActivity.this.getPackageName() + ".provider", new File(filePath));
                            intent.setDataAndType(uri,"application/vnd.ms-excel");
                            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            startActivity(intent);
                        }
                        catch (ActivityNotFoundException e) {
                            Toast.makeText(ProfessorAttendanceActivity.this, "The file has been saved in /" + Constants.BASE_FOLDER_NAME + "/" + Constants.TABLES_FOLDER_NAME + "/.", Toast.LENGTH_SHORT) .show();
                        }
                    }
                    catch (IOException e) {
                        Toast.makeText(ProfessorAttendanceActivity.this, "Couldn't create the table file.", Toast.LENGTH_SHORT) .show();
                    }
                }
                else {
                    Toast.makeText(ProfessorAttendanceActivity.this, "An error has occured. Please try again.", Toast.LENGTH_SHORT) .show();
                }
            }

            @Override
            public void onFailure(Call<StudentAttendanceResponse> call, Throwable t) {
                hideLoadingScreen();
                Toast.makeText(ProfessorAttendanceActivity.this, "An error has occured. Try again later.", Toast.LENGTH_SHORT) .show();
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

    public void setTotalAttendanceList(List<StudentAttendance> totalAttendanceList) {
        this.totalAttendanceList = totalAttendanceList;
    }

    public void setProfessorGrupsList(List<ProfessorGrups> professorGrupsList) {
        this.professorGrupsList = professorGrupsList;
    }
}