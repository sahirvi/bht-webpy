package com.example.test_project.viewModel;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.test_project.api.BloomCall;
import com.example.test_project.model.SensorModel;
import com.example.test_project.model.ZoneModel;
import com.example.test_project.viewHelper.AuthInterceptor;
import com.example.test_project.viewHelper.UnsafeOkHttpClient;
import com.example.test_project.viewHelper.UserSessionManager;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * ViewModel to GET one Zone
 */

public class ZoneDetailViewModel extends ViewModel {

    private MutableLiveData<ZoneModel> zone;
    private MutableLiveData<SensorModel> sensor;
    private MutableLiveData<String> headerData;
    private static final String TAG = "ZoneViewModel";
    private BloomCall bloomCall;
    private UserSessionManager session;

    public ZoneDetailViewModel() {
        super();
        zone = new MutableLiveData<>();
        sensor = new MutableLiveData<>();
        headerData = new MutableLiveData<>();
    }

    public MutableLiveData<ZoneModel> getZoneObserver() {
        return zone;
    }

    public MutableLiveData<SensorModel> getSensorObserver() {
        return sensor;
    }

    public MutableLiveData<String> getAuthHeader() {
        return headerData;
    }

    /**
     * Gets a zone
     * Sends hubId and zoneId as query parameter and gets zone in response body
     */
    public void getZone(Context context, Integer hubId, Integer zoneId) {

        retrofitCall(context);

        Call<ZoneModel> call = bloomCall.getZone(hubId, zoneId);

        // asynchronous GET request of a zone by hub_id and zone_id
        call.enqueue(new Callback<ZoneModel>() {
            @Override
            public void onResponse(@NonNull Call<ZoneModel> call, @NonNull Response<ZoneModel> response) {
                // check if new header because of refresh token
                if (response.headers().get("Authorization") != null) {
                    session = new UserSessionManager(context);
                    Log.d(TAG, "ZoneDetailViewModel: " + response.headers().get("Authorization"));
                    session.logoutUser();
                    session.createUserLoginSession(response.headers().get("Authorization"));
                }
                // put the response.body in zone
                if (response.isSuccessful()) {
                    zone.postValue(response.body());
                } else {
                    zone.postValue(null);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ZoneModel> call, @NonNull Throwable t) {
                Log.e(TAG, "onFailure: " + t.getMessage());
                zone.postValue(null);
            }
        });
    }

    /**
     * Gets a sensor
     * Sends hubId and zoneId as query parameter and gets sensor in response body
     */
    public void getSensor(Context context, Integer hubId, Integer zoneId) {

        retrofitCall(context);

        Call<SensorModel> call = bloomCall.getSensor(hubId, zoneId);

        // asynchronous GET request of a sensor by hub_id and zone_id
        call.enqueue(new Callback<SensorModel>() {
            @Override
            public void onResponse(@NonNull Call<SensorModel> call, @NonNull Response<SensorModel> response) {
                // check if new header because of refresh token
                if (response.headers().get("Authorization") != null) {
                    session = new UserSessionManager(context);
                    Log.d(TAG, "ZoneDetailViewModel: " + response.headers().get("Authorization"));
                    session.logoutUser();
                    session.createUserLoginSession(response.headers().get("Authorization"));
                }
                // put the response.body in sensor
                if (response.isSuccessful()) {
                    sensor.postValue(response.body());
                } else {
                    sensor.postValue(null);
                }
            }

            @Override
            public void onFailure(@NonNull Call<SensorModel> call, @NonNull Throwable t) {
                Log.e(TAG, "onFailure: " + t.getMessage());
                sensor.postValue(null);
            }
        });
    }

    /**
     * Updates is_pending with hub_id and zone_id
     * Sends ZoneModel(is_pending) as body and hub_id and zone_id as query parameter.
     */
    public void updateIsPending(Context context, Integer hubId, Integer zoneId, boolean is_pending) {

        ZoneModel zoneModel = new ZoneModel(is_pending);

        retrofitCall(context);

        Call<ZoneModel> call = bloomCall.updateZone(hubId, zoneId, zoneModel);

        // asynchronous GET request of a zone by hub_id and zone_id
        call.enqueue(new Callback<ZoneModel>() {
            @Override
            public void onResponse(@NonNull Call<ZoneModel> call, @NonNull Response<ZoneModel> response) {
                // check if new header because of refresh token
                if (response.headers().get("Authorization") != null) {
                    session = new UserSessionManager(context);
                    Log.d(TAG, "ZoneDetailViewModel: " + response.headers().get("Authorization"));
                    session.logoutUser();
                    session.createUserLoginSession(response.headers().get("Authorization"));
                }
                // put the response.body in zone
                if (response.isSuccessful()) {
                    zone.postValue(response.body());
                } else {
                    zone.postValue(null);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ZoneModel> call, @NonNull Throwable t) {
                Log.e(TAG, "onFailure: " + t.getMessage());
                zone.postValue(null);
            }
        });
    }

    /**
     * Updates name with hub_id and zone_id
     * Sends ZoneModel(name) as body and hub_id and zone_id as query parameter.
     */
    public void updateName(Context context, Integer hubId, Integer zoneId, String name) {

        ZoneModel zoneModel = new ZoneModel(name);

        retrofitCall(context);

        Call<ZoneModel> call = bloomCall.updateZone(hubId, zoneId, zoneModel);

        // asynchronous GET request of a zone by hub_id and zone_id
        call.enqueue(new Callback<ZoneModel>() {
            @Override
            public void onResponse(@NonNull Call<ZoneModel> call, @NonNull Response<ZoneModel> response) {
                // check if new header because of refresh token
                if (response.headers().get("Authorization") != null) {
                    session = new UserSessionManager(context);
                    Log.d(TAG, "ZoneDetailViewModel: " + response.headers().get("Authorization"));
                    session.logoutUser();
                    session.createUserLoginSession(response.headers().get("Authorization"));
                }
                // put the response.body in zone
                if (response.isSuccessful()) {
                    zone.postValue(response.body());
                } else {
                    zone.postValue(null);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ZoneModel> call, @NonNull Throwable t) {
                Log.e(TAG, "onFailure: " + t.getMessage());
                zone.postValue(null);
            }
        });
    }

    /**
     * Retrofit call to use BloomCall with updatedClient
     */
    private void retrofitCall(Context context) {

        OkHttpClient okHttpClient = UnsafeOkHttpClient.getUnsafeOkHttpClient();

        // to send request with token
        OkHttpClient updatedClient = okHttpClient
                .newBuilder()
                .addInterceptor(new AuthInterceptor(context))
                .build();

        // Client to make requests to Server-URL
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://217.160.45.110:443/")
                .client(updatedClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // Instance of retrofit with BloomCall
        bloomCall = retrofit.create(BloomCall.class);
    }
}
