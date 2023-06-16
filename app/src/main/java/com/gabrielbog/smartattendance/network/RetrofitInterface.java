package com.gabrielbog.smartattendance.network;

import com.gabrielbog.smartattendance.models.responses.LogInResponse;
import com.gabrielbog.smartattendance.models.responses.ProfessorGrupsResponse;
import com.gabrielbog.smartattendance.models.responses.QrCodeResponse;
import com.gabrielbog.smartattendance.models.responses.ScheduleCalendarResponse;
import com.gabrielbog.smartattendance.models.responses.StudentAttendanceResponse;
import com.gabrielbog.smartattendance.models.responses.SubjectListResponse;

import java.sql.Date;

import retrofit2.Call;
import retrofit2.http.Path;
import retrofit2.http.GET;

public interface RetrofitInterface {

    @GET("/api/checkUserExistence/{cnp}&{password}")
    Call<LogInResponse> checkUserExistence(@Path("cnp") String cnp, @Path("password") String password);

    @GET("/api/generateQrCode/{id}")
    Call<QrCodeResponse> generateQrCode(@Path("id") int id);

    @GET("/api/refreshQrCode/{id}&{code}")
    Call<QrCodeResponse> refreshQrCode(@Path("id") int id, @Path("code") String code);

    @GET("/api/scanQrCode/{id}&{code}")
    Call<QrCodeResponse> scanQrCode(@Path("id") int id, @Path("code") String code);

    @GET("/api/getSubjectList/{id}")
    Call<SubjectListResponse> getSubjectList(@Path("id") int id);

    @GET("/api/getSubjectAttendanceList/{studentId}&{subjectId}")
    Call<StudentAttendanceResponse> getSubjectAttendanceList(@Path("studentId") int studentId, @Path("subjectId") int subjectId);

    @GET("/api/getScheduleCalendar/{professorId}&{subjectId}")
    Call<ScheduleCalendarResponse> getScheduleCalendar(@Path("professorId") int professorId, @Path("subjectId") int subjectId);

    @GET("/api/getAttendingStudentsList/{scanDate}&{scheduleId}")
    Call<StudentAttendanceResponse> getAttendingStudentsList(@Path("scanDate") Date scanDate, @Path("scheduleId") int scheduleId);

    @GET("/api/getProfessorGrups/{professorId}&{subjectId}")
    Call<ProfessorGrupsResponse> getProfessorGrups(@Path("professorId") int professorId, @Path("subjectId") int subjectId);

    @GET("/api/getTotalAttendingStudentsList/{professorId}&{subjectId}&{grup}")
    Call<StudentAttendanceResponse> getTotalAttendingStudentsList(@Path("professorId") int professorId, @Path("subjectId") int subjectId, @Path("grup") int grup);
}
