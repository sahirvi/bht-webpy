package com.example.test_project.viewModel;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.test_project.api.BloomCall;
import com.example.test_project.model.SensorModel;
import com.example.test_project.viewHelper.AuthInterceptor;
import com.example.test_project.viewHelper.UnsafeOkHttpClient;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * ViewModel to GET one Sensor
 */

public class SensorViewModel extends ViewModel {

    private MutableLiveData<SensorModel> sensor;
    private MutableLiveData<String> header;
    private Integer hubId;
    private Integer zoneId;
    private static final String TAG = "SensorViewModel";
    BloomCall bloomCall;


    public SensorViewModel(Integer hubId, Integer zoneId) {
        sensor = new MutableLiveData<>();
        header = new MutableLiveData<>();
        this.hubId = hubId;
        this.zoneId = zoneId;
    }

    public MutableLiveData<SensorModel> getSensorObserver() {
        return sensor;
    }

    public MutableLiveData<String> getAuthHeader() {
        return header;
    }



    private void retrofitCall(Context context) {
        //OkHttp
        OkHttpClient okHttpClient = UnsafeOkHttpClient.getUnsafeOkHttpClient();

        OkHttpClient updatedClient = okHttpClient
                .newBuilder()
                .addInterceptor(new AuthInterceptor(context))
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://217.160.45.110:443/")
                .client(updatedClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        bloomCall = retrofit.create(BloomCall.class);
    }




    public void makeApiCall(Context context) {

        OkHttpClient okHttpClient = UnsafeOkHttpClient.getUnsafeOkHttpClient();

        // to send request with token
        OkHttpClient updatedClient = okHttpClient
                .newBuilder()
                .addInterceptor(new AuthInterceptor(context))
                .build();

        //Todo add Runtime for Request ca. 1 min

        // Client to make requests to Server-URL
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://217.160.45.110:443/")
                .client(updatedClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // Instance of retrofit with BloomCall
        BloomCall bloomCall = retrofit.create(BloomCall.class);
        Call<SensorModel> call = bloomCall.getSensor(hubId, zoneId);


        // asynchronous GET request of a sensor by hub_id and zone_id
        call.enqueue(new Callback<SensorModel>() {
            @Override
            public void onResponse(@NonNull Call<SensorModel> call, @NonNull Response<SensorModel> response) {

                // put header (refreshToken) in variable
                if (response.headers().get("Authorization") != null) {
                    Log.d(TAG, "New Authorization Header" + response.headers().get("Authorization"));
                    header.postValue(response.headers().get("Authorization"));
                }

                // put the response.body in zone
                if (response.isSuccessful()) {
                    Log.d(TAG, "Zone: " + response.headers().get("Authorization"));
                    sensor.postValue(response.body());
                } else {
                    sensor.postValue(null);
                }
            }

            @Override
            public void onFailure(@NonNull Call<SensorModel> call, @NonNull Throwable t) {
                Log.d(TAG, "Error: " + t.getMessage());
                sensor.postValue(null);
            }
        });
    }
}
