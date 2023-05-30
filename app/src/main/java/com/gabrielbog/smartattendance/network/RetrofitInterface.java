package com.gabrielbog.smartattendance.network;

import com.gabrielbog.smartattendance.models.responses.LogInResponse;
import com.gabrielbog.smartattendance.models.responses.QrCodeResponse;
import com.gabrielbog.smartattendance.models.responses.StudentAttendanceResponse;
import com.gabrielbog.smartattendance.models.responses.SubjectListResponse;

import retrofit2.Call;
import retrofit2.http.Path;
import retrofit2.http.GET;

public interface RetrofitInterface {

    @GET("/api/getUserByCnpAndPassword/{cnp}&{password}")
    Call<LogInResponse> getUserByCnpAndPassword(@Path("cnp") String cnp, @Path("password") String password);

    @GET("/api/generateQrCode/{id}")
    Call<QrCodeResponse> generateQrCode(@Path("id") int id);

    @GET("/api/scanQrCode/{id}&{code}")
    Call<QrCodeResponse> scanQrCode(@Path("id") int id, @Path("code") String code);

    @GET("/api/getSubjectList/{id}")
    Call<SubjectListResponse> getSubjectList(@Path("id") int id);

    @GET("/api/getSubjectAttendanceList/{studentId}&{subjectId}")
    Call<StudentAttendanceResponse> getSubjectAttendanceList(@Path("studentId") int studentId, @Path("subjectId") int subjectId);
}
