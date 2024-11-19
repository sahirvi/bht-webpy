package com.example.test_project.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * MeasurementSchema (Sensor) from Webserver in GSON
 **/
public class MeasurementModel {
    @SerializedName("_id")
    @Expose
    private String _id;

    @SerializedName("idd")
    @Expose
    private Integer id;

    @SerializedName("moisture_value")
    @Expose
    private Integer moistureValue;

    @SerializedName("createdAt")
    @Expose
    private String createdAt;

    @SerializedName("updatedAt")
    @Expose
    private String updatedAt;


    public String get_Id() {
        return _id;
    }

    public void set_Id(String _id) {
        this._id = _id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getMoistureValue() {
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
