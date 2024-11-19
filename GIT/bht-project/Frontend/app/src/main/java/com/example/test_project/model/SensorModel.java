package com.example.test_project.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * SensorSchema from Webserver in GSON
 **/
public class SensorModel {

    @SerializedName("_id")
    @Expose
    private String _id;

//    @SerializedName("idd")
//    @Expose
//    private Integer id;

    @SerializedName("hub_id")
    @Expose
    private Integer hubId;

    @SerializedName("zone_id")
    @Expose
    private Integer zoneId;

    @SerializedName("battery")
    @Expose
    private double battery;

    @SerializedName("moisture_value")
    @Expose
    private double moistureValue;

    @SerializedName("createdAt")
    @Expose
    private String createdAt;

    @SerializedName("updatedAt")
    @Expose
    private String updatedAt;

    public SensorModel(int sensor_id, int zone_id, int hub_id){
        //this.id = sensor_id;
        this.zoneId = zone_id;
        this.hubId = hub_id;
        this.battery = 1;
        this.moistureValue = 1;
    }

    public String get_Id() {
        return _id;
    }

    public void set_Id(String _id) {
        this._id = _id;
    }

//    public Integer getId() {
//        return id;
//    }

//    public void setId(Integer idd) {
//        this.id = idd;
//    }

    public Integer getHubId() {
        return hubId;
    }

    public void setHubId(Integer hubId) {
        this.hubId = hubId;
    }

    public Integer getZoneId() {
        return zoneId;
    }

    public void setZoneId(Integer zoneId) {
        this.zoneId = zoneId;
    }

    public double getBattery() {
        return battery;
    }

    public void setBattery(Integer battery) {
        this.battery = battery;
    }

    public double getMoistureValue() {
        return moistureValue;
    }

    public void setMoistureValue(Integer moistureValue) {
        this.moistureValue = moistureValue;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

}
