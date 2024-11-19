package com.example.test_project.api;

import com.example.test_project.model.MeasurementModel;
import com.example.test_project.model.HubModel;
import com.example.test_project.model.HubRegModel;
import com.example.test_project.model.SensorModel;
import com.example.test_project.model.UserModel;
import com.example.test_project.model.ZoneModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Call Interface for HTTPS Request to use in RetrofitClient
 **/
public interface BloomCall {

    /**
     * Hub
     *
     * @param hub
     * @return
     **/

    @Headers({"Content-Type: application/json"})
    @POST("hub/addHub")
    Call<HubModel> addHub(@Body HubModel hub);

    @GET("hub/getAllHubs")
    Call<List<HubModel>> getAllHubs();

    @GET("hub/getHub/{hub_id}")
    Call<HubModel> getHub(@Path("hub_id") Integer hubId);

    @GET("hub/getHubByUser/")
    Call<HubModel> getHubByUser();

    @Headers({"Content-Type: application/json"})
    @PUT("hub/updateHub/{hub_id}")
    Call<HubModel> updateHub(
            @Path("hub_id") Integer hubId,
            @Body HubModel newHub);

    @DELETE("hub/deleteHub/{hub_id}")
    Call<HubModel> deleteHub(@Path("hub_id") Integer hubId);


    /**
     * Measurement
     *
     * @return
     */

    @GET("sensor/getAllCurrentSensorData")
    Call<List<MeasurementModel>> getAllSensorData();

    /**
     * Sensor
     *
     * @param sensor
     * @return
     */

    @Headers({"Content-Type: application/json"})
    @POST("sensor/addSensor")
    Call<SensorModel> addSensor(@Body SensorModel sensor);

    @GET("sensor//getAllSensorsByHubId/{hub_id}")
    Call<List<SensorModel>> getAllSensorsByHubID(@Path("hub_id") Integer hubId);

    @GET("sensor/getSensor")
    Call<SensorModel> getSensor(@Query("hub_id") Integer hubId,
                                @Query("zone_id") Integer zoneId);

    @GET("sensor/getAllCurrentSensorData")
    Call<List<SensorModel>> getAllCurrentSensorData();

    @Headers({"Content-Type: application/json"})
    @PUT("sensor/updateSensor")
    Call<SensorModel> updateSensor(
            @Query("hub_id") Integer hubId,
            @Query("zone_id") Integer zoneId,
            @Body SensorModel newSensor);

    @DELETE("sensor/deleteSensor")
    Call<SensorModel> deleteSensor(
            @Query("hub_id") Integer hubId,
            @Query("zone_id") Integer zoneId);


    /**
     * UserLogin
     *
     * @param basicAuth
     * @return
     */
    @POST("user/login")
    Call<Void> login(@Header("Authorization") String basicAuth);

    /**
     * User
     *
     * @param user
     * @return
     */

    @Headers({"Content-Type: application/json"})
    @POST("user/register")
    Call<UserModel> register(@Body UserModel user);

    @GET("user/getUser/{email}")
    Call<UserModel> getUser(@Path("email") Integer email);

    @Headers({"Content-Type: application/json"})
    @PUT("user/updateUser/{email}")
    Call<UserModel> updateUser(
            @Path("email") Integer email,
            @Body UserModel newUser);

    @DELETE("user/deleteUser/{email}")
    Call<UserModel> deleteUser(
            @Path("email") Integer email);

    /**
     * PostUserKey for HubRegistration
     */

    @Headers({"Content-Type: application/json"})
    @POST("user/postUserKey")
    Call<Void> postUserKey(
            @Body HubRegModel data);


    /**
     * Zone
     *
     * @param zone
     * @return
     */

    @Headers({"Content-Type: application/json"})
    @POST("zone/addZone")
    Call<ZoneModel> addZone(@Body ZoneModel zone);

    @GET("zone/getAllZones")
    Call<List<ZoneModel>> getAllZones();

    @GET("zone/getAllZonesbyHubId/{hub_id}")
    Call<List<ZoneModel>> getAllZonesbyHub(@Path("hub_id") Integer hubId);

    @GET("zone/getZone")
    Call<ZoneModel> getZone(@Query("hub_id") Integer hubId,
                            @Query("zone_id") Integer zoneId);

    @GET("zone/getAllPendingZones/{hub_id}")
    Call<List<ZoneModel>> getAllPendingZones(
            @Path("hub_id") Integer hubId);

    @Headers({"Content-Type: application/json"})
    @PUT("zone/updateZone")
    Call<ZoneModel> updateZone(
            @Query("hub_id") Integer hubId,
            @Query("zone_id") Integer zoneId,
            @Body ZoneModel newZone);

    @DELETE("zone/deleteZone")
    Call<ZoneModel> deleteZone(@Query("hub_id") Integer hubId,
                               @Query("zone_id") Integer zoneId);

}
