package com.example.test_project.viewModel;

import android.content.Context;
import android.util.Log;
import android.widget.CompoundButton;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.test_project.api.BloomCall;
import com.example.test_project.model.HubModel;
import com.example.test_project.model.MeasurementModel;
import com.example.test_project.model.SensorModel;
import com.example.test_project.model.ZoneModel;
import com.example.test_project.viewHelper.AuthInterceptor;
import com.example.test_project.viewHelper.UnsafeOkHttpClient;
import com.example.test_project.viewHelper.UserSessionManager;

import java.io.IOException;
import java.util.List;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ZoneDataViewModel extends ViewModel {

    private static final String TAG = "ZoneDataViewModel";

    BloomCall bloomCall;

    private MutableLiveData<List<ZoneModel>> zoneData;
    private MutableLiveData<HubModel> hubData;
    private MutableLiveData<HubModel> hubUpdateData;
    private MutableLiveData<String> headerData;
    private MutableLiveData<ZoneModel> addedZoneData;
    private MutableLiveData<List<SensorModel>> foundSensorData;
    private  MutableLiveData<SensorModel> addedSensorData;
    private  MutableLiveData<SensorModel> sensorData;

    Retrofit retrofit;
    OkHttpClient okHttpClient;

    HubModel hubModel;
    UserSessionManager session;


    public ZoneDataViewModel() {
        super();
        zoneData = new MutableLiveData<>();
        hubData = new MutableLiveData<>();
        hubUpdateData = new MutableLiveData<>();
        headerData = new MutableLiveData<>();
        addedZoneData = new MutableLiveData<>();
        foundSensorData = new MutableLiveData<>();
        sensorData = new MutableLiveData<>();
        addedSensorData = new MutableLiveData<>();


    }

    public MutableLiveData<List<ZoneModel>> getZoneDataObserver() { return zoneData; }
    public MutableLiveData<HubModel> getHubDataObserver(){
        return hubData;
    }
    public MutableLiveData<HubModel> getHubUpdateDataObserver(){ return hubUpdateData; }
    public MutableLiveData<String> getAuthHeader(){
        return headerData;
    }
    public MutableLiveData<ZoneModel> postZoneDataObserver() { return addedZoneData; }
    public MutableLiveData<List<SensorModel>> getFoundSensorObserver(){return foundSensorData;}
    public MutableLiveData<SensorModel> getAddedSensorObserver(){return addedSensorData;}
    public MutableLiveData<SensorModel> getSensorObserver(){return sensorData;}



    //To change auto or manual watering with switch

    public void makeHubUpdate(Context context, Integer hub_id, boolean isChecked){
        retrofitCall(context);

        Log.d(TAG, "updateAuto: " + isChecked + ", HUB ID: " + hub_id);
        HubModel hubModel = new HubModel(isChecked);

        Call<HubModel> updateAutoHub = bloomCall.updateHub(hub_id, hubModel);
        updateAutoHub.enqueue(new Callback<HubModel>() {
            @Override
            public void onResponse(Call<HubModel> call, Response<HubModel> response) {
                hubUpdateData.postValue(response.body());
                if (response.headers().get("Authorization") != null){
                    Log.d(TAG, "VM NEW HEADER hubupdate: " + response.headers().get("Authorization"));
                    checkResponseHeader(response.headers().get("Authorization"));
                }
                checkResponseHeader(response.headers().get("Authorization"));
                Log.d(TAG, "VM NO NEW HEADER YET hubupdate");
            }

            @Override
            public void onFailure(Call<HubModel> call, Throwable t) {
                hubUpdateData.postValue(null);
                Log.d(TAG, "onFailure hub: " + t.getMessage());
            }
        });
    }

    //to get the hub of user

    public void makeHubCall(Context context){

        retrofitCall(context);
        Log.d(TAG, "makeHubCall");

        Call<HubModel> callHub = bloomCall.getHubByUser();

        callHub.enqueue(new Callback<HubModel>() {
            @Override
            public void onResponse(Call<HubModel> call, Response<HubModel> response) {
                if (response.headers().get("Authorization") != null){
                    session = new UserSessionManager(context);
                    Log.d(TAG, "VM NEW HEADER hub: " + response.headers().get("Authorization"));
                    session.logoutUser();
                    session.createUserLoginSession(response.headers().get("Authorization"));
                }
                if (response.isSuccessful()){
                    hubData.postValue(response.body());
                    Log.d(TAG, "VM NO NEW HEADER YET hub");
                }
                else{
                    hubData.postValue(null);
                }
            }
            @Override
            public void onFailure(Call<HubModel> call, Throwable t) {
                hubData.postValue(null);
                Log.d(TAG, "onFailure hub: " + t.getMessage());
            }
        });
    }

    public void getSensorCall(Context context, int zone_id, int hub_id){

        retrofitCall(context);
        Call<List<SensorModel>> call = bloomCall.getAllSensorsByHubID(hub_id);
        call.enqueue(new Callback<List<SensorModel>>() {
            @Override
            public void onResponse(Call<List<SensorModel>> call, Response<List<SensorModel>> response) {
                if (response.isSuccessful()){
                    foundSensorData.postValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<SensorModel>> call, Throwable t) {
                foundSensorData.postValue(null);
            }
        });
    }

    public void getOneSensorCall(Context context, int zone_id, int hub_id){
        retrofitCall(context);
        Call<SensorModel> call = bloomCall.getSensor(hub_id, zone_id);

        call.enqueue(new Callback<SensorModel>() {
            @Override
            public void onResponse(Call<SensorModel> call, Response<SensorModel> response) {
                if (response.isSuccessful()) {
                    sensorData.postValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<SensorModel> call, Throwable t) {
                sensorData.postValue(null);
            }
        });

    }

    public void addSensorCall(Context context, int sensor_id, int zone_id, int hub_id){
        Log.d(TAG, "Sensor: " + sensor_id + " Zone: " + zone_id + " Hub: " + hub_id);
        SensorModel sensorModel = new SensorModel(sensor_id, zone_id, hub_id);

        retrofitCall(context);
        Call<SensorModel> call = bloomCall.addSensor(sensorModel);
        call.enqueue(new Callback<SensorModel>() {
            @Override
            public void onResponse(Call<SensorModel> call, Response<SensorModel> response) {
                if (response.isSuccessful()){
                    addedSensorData.postValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<SensorModel> call, Throwable t) {
                addedSensorData.postValue(null);
            }
        });

    }


    //to get all Zones for Hub

    public void makeZonesCall(Context context, int hubId){
        retrofitCall(context);

        Call<List<ZoneModel>> callHub = bloomCall.getAllZonesbyHub(hubId);

        callHub.enqueue(new Callback<List<ZoneModel>>() {
            @Override
            public void onResponse(Call<List<ZoneModel>> call, Response<List<ZoneModel>> response) {
                //checkResponseHeader(response.headers().get("Authorization"));
                if (response.isSuccessful()){
                    if (response.headers().get("Authorization") != null){
                        session = new UserSessionManager(context);
                        Log.d(TAG, "VM NEW HEADER zone: " + response.headers().get("Authorization"));
                        session.logoutUser();
                        session.createUserLoginSession(response.headers().get("Authorization"));
                        Log.d(TAG, "oder here?");
                        //checkResponseHeader(response.headers().get("Authorization"));
                    }
                    Log.d(TAG, "VM NO NEW HEADER YET zonecall");;
                    //Dispatch to main Thread
                    zoneData.postValue(response.body());
                }
                else{
                    zoneData.postValue(null);
                }
            }

            @Override
            public void onFailure(Call<List<ZoneModel>> call, Throwable t) {
                zoneData.postValue(null);
                Log.d(TAG, "onFailure list of zones: " + t.getMessage());
            }
        });
    }

    public void createZone(Context context, int hubId, int zoneId){
        retrofitCall(context);

        ZoneModel zoneModel = new ZoneModel(hubId, zoneId);

        Call<ZoneModel> createNewZone = bloomCall.addZone(zoneModel);
        createNewZone.enqueue(new Callback<ZoneModel>() {
            @Override
            public void onResponse(Call<ZoneModel> call, Response<ZoneModel> response) {
                if (response.isSuccessful()){
                    Log.d(TAG, "BODY ZONEDATA: " + response.body());
                    addedZoneData.postValue(response.body());

                    if (response.headers().get("Authorization") != null){
                        Log.d(TAG, "VM NEW HEADER hubupdate: " + response.headers().get("Authorization"));
                        checkResponseHeader(response.headers().get("Authorization"));
                    }
                }
                addedZoneData.postValue(null);
                Log.d(TAG, "VM NO NEW HEADER YET hubupdate");
            }

            @Override
            public void onFailure(Call<ZoneModel> call, Throwable t) {
                hubUpdateData.postValue(null);
                Log.d(TAG, "onFailure hub: " + t.getMessage());
            }
        });
    }


    //Retrofit API Call

    private void retrofitCall(Context context) {
        //OkHttp
        okHttpClient = UnsafeOkHttpClient.getUnsafeOkHttpClient();

        OkHttpClient updatedClient = okHttpClient
                .newBuilder()
                .addInterceptor(new AuthInterceptor(context))
                .build();

        retrofit = new Retrofit.Builder()
                .baseUrl("https://217.160.45.110:443/")
                .client(updatedClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        bloomCall = retrofit.create(BloomCall.class);
    }

    private void checkResponseHeader(String header) {
        if (header != null){
            Log.d(TAG, "REFRESH TOKEN: " + header);
            headerData.postValue(header);
        }
    }

}