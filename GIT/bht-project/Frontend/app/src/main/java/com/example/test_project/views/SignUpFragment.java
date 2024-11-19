package com.example.test_project.views;

import android.os.Bundle;
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
import com.example.test_project.model.UserModel;
import com.example.test_project.viewHelper.UnsafeOkHttpClient;
import com.example.test_project.viewHelper.UserSessionManager;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Fragment to Sign Up
 */
public class SignUpFragment extends Fragment {

    private BloomCall bloomCall;
    private UserSessionManager session;
    private String token;

    // elements from layout
    private TextInputEditText emailInput;
    private TextInputEditText passwordInput;
    private TextInputEditText passwordConfirmInput;
    private TextView errorSignup;

    private String email;
    private String password;
    private String passwordConfirm;
    private String passwordEqual;
    private String basicAuthentication;

    private static final String TAG = "SignUpFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sign, container, false);

        //UserSession
        session = new UserSessionManager(requireContext());

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
        emailInput = view.findViewById(R.id.signupEmailInput);
        passwordInput = view.findViewById(R.id.signupPasswordInput);
        passwordConfirmInput = view.findViewById(R.id.signupPasswordConfirmInput);
        errorSignup = view.findViewById(R.id.errorSignup);
        errorSignup.setVisibility(View.INVISIBLE);
        Button signUpButton = view.findViewById(R.id.signUpButton);

        // change color of signup button to dark blue if all fields are filled
        passwordConfirmInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!TextUtils.isEmpty(Objects.requireNonNull(emailInput.getText()).toString()) && !TextUtils.isEmpty(Objects.requireNonNull(passwordInput.getText()).toString()) && !TextUtils.isEmpty(Objects.requireNonNull(passwordConfirmInput.getText()).toString())) {
                    signUpButton.setBackground(ContextCompat.getDrawable(requireActivity(), R.drawable.custom_button));
                    int white = ContextCompat.getColor(requireActivity(), R.color.white);
                    signUpButton.setTextColor(white);
                }
            }
        });

        // if signup button clicked register user on server
        signUpButton.setOnClickListener(v -> {
            email = Objects.requireNonNull(emailInput.getText()).toString();
            password = Objects.requireNonNull(passwordInput.getText()).toString();
            passwordConfirm = Objects.requireNonNull(passwordConfirmInput.getText()).toString();
            createBasicAuthentication();
            registerUser();
        });

        // if close button clicked go back
        ImageView closeButton = view.findViewById(R.id.closeButton);
        closeButton.setOnClickListener(v -> getParentFragmentManager().popBackStack());

        // if login link clicked go to Login Fragment
        TextView loginLink = view.findViewById(R.id.loginLink);
        loginLink.setOnClickListener(v -> requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.welcomeLayout, new LoginFragment(), "findThisFragment")
                .addToBackStack(null)
                .commit());

        return view;
    }

    /**
     * Method to validate Password
     */
    public static boolean isValidPassword(final String password) {

        Pattern pattern;
        Matcher matcher;
        final String PASSWORD_PATTERN = ("^" +
                "(?=.*[@#$%^&+=])" +     // at least 1 special character
                "(?=\\S+$)" +            // no white spaces
                ".{4,}" +                // at least 4 characters
                "$");

        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);

        return matcher.matches();
    }

    /**
     * Method to register an user via BloomCall and retrofit
     */
    private void registerUser() {
        int red = ContextCompat.getColor(requireActivity(), R.color.red);
        // if password not equals other password show error message and change color of inputs
        if (!password.equals(passwordConfirm)) {
            errorSignup.setVisibility(View.VISIBLE);
            errorSignup.setText(R.string.error_signup_2);
            passwordInput.setTextColor(red);
            passwordConfirmInput.setTextColor(red);
        } else {
            passwordEqual = password;
        }

        // if password not equals pattern show error message and change color of inputs
        if (password.length() < 8 && !isValidPassword(password)) {
            errorSignup.setVisibility(View.VISIBLE);
            errorSignup.setText(R.string.error_signup_1);
            passwordInput.setTextColor(red);
            passwordConfirmInput.setTextColor(red);
        } else {

            Call<UserModel> call = bloomCall.register(new UserModel(email, passwordEqual));

            call.enqueue(new Callback<UserModel>() {
                @Override
                public void onResponse(@NonNull Call<UserModel> call, @NonNull Response<UserModel> response) {
                    if (response.isSuccessful()) {
                        Log.d(TAG, "Successful: " + response.message());
                        loginAfterSignup();
                    } else {
                        Log.d(TAG, "Error: " + response.code());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<UserModel> call, @NonNull Throwable t) {
                    Log.e(TAG, "onFailure: " + t.getMessage());
                    t.printStackTrace();
                }
            });
        }
    }

    /**
     * Method to login an user directly after he signed Up via BloomCall to get the token
     */
    private void loginAfterSignup() {

        Call<Void> call = bloomCall.login(basicAuthentication);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    token = response.headers().get("Authorization");
                    session.createUserLoginSession(token);

                    openHubRegistrationInfo();

                    Log.d(TAG, "Successful: " + response.message());
                    Log.d(TAG, "HEADER: " + token);
                } else {
                    Log.d(TAG, "Error: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                Log.e(TAG, "onFailure: " + t.getMessage());
                t.printStackTrace();
            }
        });
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
     * opens Hub Registration Info Fragment
     */
    private void openHubRegistrationInfo() {
        // pass bundle with email to next fragment
        Bundle bundle = new Bundle();
        bundle.putString("email", email);

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
}