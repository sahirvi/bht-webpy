package com.example.test_project.viewHelper;


import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Shared Retrofit Client builder
 */

public class WeatherClient {

    private static Retrofit instance;
    private static OkHttpClient okHttpClient;


    private static String BASE_URL = "https://api.openweathermap.org/";

    public static Retrofit getInstance(){

        okHttpClient = UnsafeOkHttpClient.getUnsafeOkHttpClient();

        if (instance == null)
            instance = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build();
        return instance;
    }

}
