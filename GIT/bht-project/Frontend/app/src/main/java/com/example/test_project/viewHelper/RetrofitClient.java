package com.example.test_project.viewHelper;


import android.util.Log;

import androidx.annotation.NonNull;

import com.example.test_project.viewHelper.UnsafeOkHttpClient;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.GsonBuildConfig;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Shared Retrofit Client builder
 */

public class RetrofitClient {

    private static Retrofit instance;
    private static OkHttpClient okHttpClient;


    private static String BASE_URL = "https://217.160.45.110:443/";

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
