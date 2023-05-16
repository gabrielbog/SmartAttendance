package com.gabrielbog.smartattendance.network;

import com.gabrielbog.smartattendance.models.LogInResponse;
import com.gabrielbog.smartattendance.models.QrCodeResponse;
import com.gabrielbog.smartattendance.models.User;
import retrofit2.Call;
import retrofit2.http.Path;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface RetrofitInterface {

    @GET("/api/getUserByCnpAndPassword/{cnp}&{password}")
    Call<LogInResponse> getUserByCnpAndPassword(@Path("cnp") String cnp, @Path("password") String password);

    @GET("/api/generateQrCode/{id}")
    Call<QrCodeResponse> generateQrCode(@Path("id") int id);

    @GET("/api/scanQrCode/{id}&{code}")
    Call<QrCodeResponse> scanQrCode(@Path("id") int id, @Path("code") String code);
}
