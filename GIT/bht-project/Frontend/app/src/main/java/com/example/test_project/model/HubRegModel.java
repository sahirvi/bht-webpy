package com.example.test_project.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * HubRegModel schema from Webserver in GSON
 **/
public class HubRegModel {

    @SerializedName("user_key")
    @Expose
    private Integer userKey;

    @SerializedName("email")
    @Expose
    private String email;

    public HubRegModel(Integer userKey, String email) {
        this.userKey = userKey;
        this.email = email;
    }

    public Integer getUserKey() {
        return userKey;
    }

    public void setUserKey(Integer userKey) {
        this.userKey = userKey;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

}