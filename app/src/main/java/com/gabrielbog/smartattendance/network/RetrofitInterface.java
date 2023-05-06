package com.gabrielbog.smartattendance.network;

import com.gabrielbog.smartattendance.models.User;
import retrofit2.Call;
import retrofit2.http.Path;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface RetrofitInterface {

    @GET("/api/getUserByCnpAndPassword/{cnp}&{password}")
    Call<Integer> getUserByCnpAndPassword(@Path("cnp") String cnp, @Path("password") String password);
}
