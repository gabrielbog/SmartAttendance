package com.gabrielbog.smartattendance.network;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitService {
    private static Retrofit retrofit = null;

    private static Retrofit createInstance() {
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd")
                .registerTypeAdapter(Time.class, new JsonDeserializer<Time>() {
                    public Time deserialize(JsonElement json, Type typeofT, JsonDeserializationContext context) throws JsonParseException {
                        LocalTime localTime = LocalTime.parse(json.getAsString());
                        Time time = new Time(localTime.getHour(), localTime.getMinute(), localTime.getSecond()); //can't return Time.valueOf(localTime) for some reason, so this must be done instead
                        return time;
                    }
                })
                .create();

        Retrofit retrofitInstance = new Retrofit.Builder()
                .baseUrl("http://192.168.1.2:8080/")
                .addConverterFactory(GsonConverterFactory.create(gson))
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
