package com.example.test_project.viewHelper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.IOException;

import okhttp3.Authenticator;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;

public class TokenAuthenticator implements Authenticator {

    @Nullable
    @Override
    public synchronized Request authenticate(@Nullable Route route, @NonNull Response response) throws IOException {
        boolean refreshResult = refreshToken();
        return null;
        
    }

    private boolean refreshToken() {
        return false;
    }
}
