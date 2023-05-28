package com.gabrielbog.smartattendance.network;

import com.google.gson.Gson;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitService {
    private static Retrofit retrofit = null;

    private static Retrofit createInstance() {
        Retrofit retrofitInstance = new Retrofit.Builder()
                .baseUrl("http://192.168.1.13:8080/")
                .addConverterFactory(GsonConverterFactory.create(new Gson()))
                .build();
        return retrofitInstance;
    }

    public static Retrofit getInstance() {
        if(retrofit == null) {
            retrofit = createInstance();
        }
        return retrofit;
    }
}
