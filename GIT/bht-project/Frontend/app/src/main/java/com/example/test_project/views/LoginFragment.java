package com.example.test_project.views;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.test_project.R;
import com.example.test_project.api.BloomCall;
import com.example.test_project.model.HubModel;
import com.example.test_project.viewHelper.AuthInterceptor;
import com.example.test_project.viewHelper.UnsafeOkHttpClient;
import com.example.test_project.viewHelper.UserSessionManager;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Objects;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Fragment of Login
 */

public class LoginFragment extends Fragment {
    private BloomCall bloomCall;
    private UserSessionManager session;
    private String token;

    // elements from layout
    private TextInputEditText emailInput;
    private TextInputEditText passwordInput;
    private TextView errorLoginText;

    private String email;
    private String password;
    private String basicAuthentication;

    private static final String TAG = "LoginFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        // UserSession
        session = new UserSessionManager(requireContext());

        //session.logoutUser();

        OkHttpClient okHttpClient = UnsafeOkHttpClient.getUnsafeOkHttpClient();

        // Client to make requests to Server-URL
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://217.160.45.110:443/")
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // Instance of retrofit with BloomCall
        bloomCall = retrofit.create(BloomCall.class);

        // init default elements
        emailInput = view.findViewById(R.id.loginEmailInput);
        passwordInput = view.findViewById(R.id.loginPasswordInput);
        errorLoginText = view.findViewById(R.id.errorLogin);
        errorLoginText.setVisibility(View.INVISIBLE);
        Button loginButton = view.findViewById(R.id.loginButton);
        ImageView backButton = view.findViewById(R.id.backButton);
        TextView registerLink = view.findViewById(R.id.signupLink);

        // change color of login button to dark blue if all fields are filled
        passwordInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!TextUtils.isEmpty(Objects.requireNonNull(emailInput.getText()).toString()) && !TextUtils.isEmpty(Objects.requireNonNull(passwordInput.getText()).toString())) {
                    loginButton.setBackground(ContextCompat.getDrawable(requireActivity(), R.drawable.custom_button));
                    int white = ContextCompat.getColor(requireActivity(), R.color.white);
                    loginButton.setTextColor(white);
                }
            }
        });

        // if login button clicked login user on server
        loginButton.setOnClickListener(v -> {
            email = Objects.requireNonNull(emailInput.getText()).toString();
            password = Objects.requireNonNull(passwordInput.getText()).toString();
            createBasicAuthentication();
            loginUser();
        });

        // if back button clicked go back
        backButton.setOnClickListener(v -> getParentFragmentManager().popBackStack());

        // if register link clicked go to Register Fragment
        registerLink.setOnClickListener(v -> requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.welcomeLayout, new SignUpFragment(), "findThisFragment")
                .addToBackStack(null)
                .commit());

        return view;
    }

    /**
     * generates base64 Authentication String with Credentials
     */
    private void createBasicAuthentication() {
        String credentials = email + ":" + password;
        basicAuthentication = "Basic " + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
        Log.d(TAG, "Basic Authentication is: " + basicAuthentication);
    }

    /**
     * Login User via BloomCall and Retrofit
     */
    private void loginUser() {

        Call<Void> call = bloomCall.login(basicAuthentication);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    token = response.headers().get("Authorization");
                    session.createUserLoginSession(token);
                    //Log.d(TAG, "LOGIN TOKEN: " + token);

                    checkIfUserHasHub(token);


                    //openDashboardActivity(token);

                    Log.d(TAG, "Successful: " + response.message());
                    Log.d(TAG, "HEADER: " + token);
                } else {
                    Log.d(TAG, "Error: " + response.code());

                    // if error show Error Message and change Color of Inputs
                    int red = ContextCompat.getColor(requireActivity(), R.color.red);
                    errorLoginText.setVisibility(View.VISIBLE);
                    emailInput.setTextColor(red);
                    passwordInput.setTextColor(red);
                    errorLoginText.setText(R.string.login_error);
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                Log.e(TAG, "onFailure: " + t.getMessage());
                t.printStackTrace();
            }
        });
    }

    private void checkIfUserHasHub(String token) {
        OkHttpClient okHttpClient = UnsafeOkHttpClient.getUnsafeOkHttpClient();
        // to send request with token
        OkHttpClient updatedClient = okHttpClient
                .newBuilder()
                .addInterceptor(new AuthInterceptor(getContext()))
                .build();

        // Client to make requests to Server-URL
        Retrofit retrofit2 = new Retrofit.Builder()
                .baseUrl("https://217.160.45.110:443/")
                .client(updatedClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        BloomCall bloomCall2 = retrofit2.create(BloomCall.class);

        Call<HubModel> call = bloomCall2.getHubByUser();

        call.enqueue(new Callback<HubModel>() {
            @Override
            public void onResponse(@NonNull Call<HubModel> call, @NonNull Response<HubModel> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        Log.d(TAG, "HUB ID: " + response.body().getHubId() + "");
                        openDashboardActivity(token);
                    }
                    //openHubRegistrationInfo(token);
                } else {
                    openHubRegistrationInfo(token);
                }
            }

            @Override
            public void onFailure(@NonNull Call<HubModel> call, @NonNull Throwable t) {

            }
        });

    }

    private void openHubRegistrationInfo(String token) {
        // pass bundle with email to next fragment
        Bundle bundle = new Bundle();
        bundle.putString("email", email);
        bundle.putString("token", token);
        session.createUserLoginSession(token);

        HubRegInfoFragment hubRegInfoFragment = new HubRegInfoFragment();
        hubRegInfoFragment.setArguments(bundle);
        getParentFragmentManager().beginTransaction().remove(this).commit();

        // Replace Layout of Activity with hubRegFragment
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.welcomeLayout, hubRegInfoFragment, "findThisFragment")
                .addToBackStack(null)
                .commit();
    }

    /**
     * opens Dashboard Activity
     */
    private void openDashboardActivity(String token) {
        Handler handler = new Handler();
        Runnable runnable = () -> {
            Intent intent = new Intent(getActivity(), DashboardActivity.class);
            intent.putExtra("Token", token);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            // Add new Flag to start new Activity
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            requireActivity().finish();
        };
        handler.postDelayed(runnable, 1000);
    }
}
