package com.example.test_project.viewHelper;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.annotation.NonNull;

import java.io.IOException;

import okhttp3.Credentials;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;


public class AuthInterceptor implements Interceptor {
    //private String authToken;
    private UserSessionManager userSession;
    /**
     * Token is sent with every request in the header.
     * First used after Login and Registration.
     * @param
     */
//    public AuthInterceptor(String authToken){
//        this.authToken = authToken;
//    }

    /**
     * Token called from current UserSession
     * @param context
     */
    public AuthInterceptor(Context context){
        this.userSession = new UserSessionManager(context);
        //this.authToken = this.userSession.getToken();
    }


    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request request = chain.request();

        Request.Builder builder = request.newBuilder()
                .header("Authorization", this.userSession.getToken());

        Request newRequest = builder.build();
        return chain.proceed(newRequest);
    }

}
