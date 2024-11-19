package com.example.test_project.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * HubModel schema from Webserver in GSON
 **/
public class HubModel {

    @SerializedName("hub_id")
    @Expose
    private Integer hubId;

    @SerializedName("user")
    @Expose
    private String user;

    @SerializedName("bucket_empty")
    @Expose
    private Boolean bucketEmpty;

    @SerializedName("outlet_count")
    @Expose
    private Integer outletCount;

    @SerializedName("factory_key")
    @Expose
    private String factoryKey;

    @SerializedName("auto")
    @Expose
    private Boolean auto;

    @SerializedName("user_key")
    @Expose
    private Integer userKey;

    @SerializedName("_id")
    @Expose
    private String id;

    @SerializedName("createdAt")
    @Expose
    private String createdAt;

    @SerializedName("updatedAt")
    @Expose
    private String updatedAt;

    public HubModel(Boolean auto){
        this.auto = auto;
    }

    public Integer getHubId() {
        return hubId;
    }

    public void setHubId(Integer hubId) {
        this.hubId = hubId;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public Boolean getBucketEmpty() {
        return bucketEmpty;
    }

    public void setBucketEmpty(Boolean bucketEmpty) {
        this.bucketEmpty = bucketEmpty;
    }

    public Integer getOutletCount() {
        return outletCount;
    }

    public void setOutletCount(Integer outletCount) {
        this.outletCount = outletCount;
    }

    public String getFactoryKey() {
        return factoryKey;
    }

    public void setFactoryKey(String factoryKey) {
        this.factoryKey = factoryKey;
    }

    public Boolean getAuto() {
        return auto;
    }

    public void setAuto(Boolean auto) {
        this.auto = auto;
    }

    public Integer getUserKey() {
        return userKey;
    }

    public void setUserKey(Integer userKey) {
        this.userKey = userKey;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
