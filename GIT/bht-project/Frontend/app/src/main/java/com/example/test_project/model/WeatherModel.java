package com.example.test_project.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


import java.util.List;

//JSON model (POJO)

public class WeatherModel {

    @SerializedName("name")
    @Expose
    public String name;

    @SerializedName("main")
    Main main;

    @SerializedName("weather")
    @Expose
    public List<Weather> weather = null;

    public Main getMain() {
        return main;
    }

    public void setMain(Main main) {
        this.main = main;
    }

    public List<Weather> getWeather() {
        return weather;
    }

    public void setWeather(List<Weather> weather) {
        this.weather = weather;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
