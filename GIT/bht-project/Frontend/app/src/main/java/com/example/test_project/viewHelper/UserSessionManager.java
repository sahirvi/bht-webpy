package com.example.test_project.viewHelper;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Base64;
import android.util.Log;

import com.example.test_project.model.WeatherModel;
import com.example.test_project.views.WelcomeActivity;

import java.io.UnsupportedEncodingException;

public class UserSessionManager {
    SharedPreferences preferences;
    Context context;
    SharedPreferences.Editor editor;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Sharedpref file name
    private static final String PREFER_NAME = "SessionToken";

    // All Shared Preferences Keys
    private static final String IS_USER_LOGIN = "IsUserLoggedIn";

    // Token
    public static final String KEY_TOKEN= "token";

    //TTL
    public static final String TTL = "ttl";

    //SwitchStatus
    public static final String IS_AUTO = "auto";

    //Location
    public static final String LONG = "longitude";
    public static final String LAT = "latitude";

    //Weather
    public static final String NAME = "name";
    public static final String HUMIDITY = "humidity";
    public static final String TEMP = "temp";


    /**
     * Constructor
     * @param context
     */
    public UserSessionManager(Context context){
        this.context = context;
        preferences = context.getSharedPreferences(PREFER_NAME, PRIVATE_MODE);
        editor = preferences.edit();
    }

    /**
     * Create the login session, save the token in the SharedPrefs
     * @param token
     */
    public void createUserLoginSession(String token){
        // Storing login value as TRUE
        //getTTL(token);
        editor.putBoolean(IS_USER_LOGIN, true);
        editor.putString(KEY_TOKEN, token);
        editor.commit();
    }

    private void getTTL(String token) {
        try{
            String[] split = token.split("\\.");
            Log.d("JWT_DECODED", "Header: " + getJson(split[0]));
            Log.d("JWT_DECODED", "Body: " + getJson(split[1]));
        }
        catch (UnsupportedEncodingException e){
            Log.d("Errorrrr", "error parsing split");
        }

    }

    private static String getJson(String strEncoded) throws UnsupportedEncodingException {
        byte[] decodedBytes = Base64.decode(strEncoded, Base64.URL_SAFE);
        return new String(decodedBytes, "UTF-8");
    }


    /**
     * Check login method will check user login status
     * If false it will redirect user to login page
     * Else do anything
     * */
    public boolean checkLogin(){
        // Check login status
        if(!this.isUserLoggedIn()){
            Intent intent = new Intent(context, WelcomeActivity.class);

            // Closing all the activities from stack
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            // Add new flag to start new Activity
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);

            return true;
        }
        return false;
    }

    /**
     * check if User is currently logged in
     * @return whether true or false
     */
    public boolean isUserLoggedIn() {
        return preferences.getBoolean(IS_USER_LOGIN, false);
    }

    /**
     * Get Token from SharedPreferences
     * @return
     */
    public String getToken(){
        return preferences.getString(KEY_TOKEN, "");
    }

    /**
     * Clear session details
     * */
    public void logoutUser(){

        // Clearing all user data from Shared Preferences
        editor.clear();
        editor.commit();

    }

    public void setSwitchStatus(Boolean auto){
        editor.putBoolean(IS_AUTO, auto);
        editor.commit();
    }

    public void saveLocation(double longitude, double latitude){
        editor.putFloat(LONG, (float) longitude);
        editor.putFloat(LAT, (float) latitude);
        editor.commit();
    }

    public void saveWeather(WeatherModel weather){
        editor.putString(NAME, weather.getName());
        editor.putInt(HUMIDITY, weather.getMain().getHumidity());
        editor.putFloat(TEMP, (float) weather.getMain().getTemp().doubleValue());
        editor.commit();
    }

    public boolean getSwitchStatus(){
        return preferences.getBoolean(IS_AUTO, true);
    }

    public double getLongitude(){
        return preferences.getFloat(LONG, 0.0f);
    }

    public double getLatitude(){
        return preferences.getFloat(LAT, 0.1f);
    }

    public String getCityName(){
        return preferences.getString(NAME, "");
    }

    public double getTemp(){
        return preferences.getFloat(TEMP, 0.1f);
    }

    public int getHumidity(){
        return preferences.getInt(HUMIDITY, 0);
    }
}
