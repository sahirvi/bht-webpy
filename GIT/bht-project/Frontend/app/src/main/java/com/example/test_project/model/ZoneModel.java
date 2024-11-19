package com.example.test_project.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * ZoneModel schema from Webserver in GSON
 **/
public class ZoneModel {

    @SerializedName("_id")
    @Expose
    private String id;

    @SerializedName("hub_id")
    @Expose
    private Integer hubId;

    @SerializedName("zone_id")
    @Expose
    private Integer zoneId;

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("is_dry")
    @Expose
    private Boolean isDry;

    @SerializedName("is_pending")
    @Expose
    private Boolean isPending;

    @SerializedName("is_watering")
    @Expose
    private Boolean isWatering;

    @SerializedName("watering_timestamp")
    @Expose
    private String wateringTimestamp;

    @SerializedName("last_watered")
    @Expose
    private String lastWatered;

    @SerializedName("createdAt")
    @Expose
    private String createdAt;

    @SerializedName("updatedAt")
    @Expose
    private String updatedAt;

    public ZoneModel(Boolean isPending) {
        this.isPending = isPending;
    }

    public ZoneModel(String name) {
        this.name = name;
    }

    public ZoneModel(int hub_id, int zone_id){
        this.hubId = hub_id;
        this.zoneId = zone_id;
        this.name = "Zone " + zone_id;
        this.isDry = false;
        this.isPending = false;
        this.isWatering = false;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getIsDry() {
        return isDry;
    }

    public void setIsDry(Boolean isDry) {
        this.isDry = isDry;
    }

    public Boolean getIsPending() {
        return isPending;
    }

    public void setIsPending(Boolean isPending) {
        this.isPending = isPending;
    }

    public Boolean getIsWatering() {
        return isWatering;
    }

    public void setIsWatering(Boolean isWatering) {
        this.isWatering = isWatering;
    }

    public String getWateringTimestamp() {
        return wateringTimestamp;
    }

    public void setWateringTimestamp(String wateringTimestamp) {
        this.wateringTimestamp = wateringTimestamp;
    }

    public String getLastWatered() {
        return lastWatered;
    }

    public void setLastWatered(String lastWatered) {
        this.lastWatered = lastWatered;
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
