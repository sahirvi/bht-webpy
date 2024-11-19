package com.example.test_project.views;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.test_project.R;
import com.example.test_project.api.BloomCall;
import com.example.test_project.model.HubRegModel;
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
 * Fragment of Hub Registration
 */
public class HubRegFragment extends Fragment {

    private BloomCall bloomCall;

    // elements from layout
    private TextInputEditText userKeyInput;
    private TextView errorKeyEntry;

    private Integer userKey;
    private String email;

    private static final String TAG = "HubRegActivity";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_hubreg, container, false);

        //UserSession
        new UserSessionManager(requireContext());


        OkHttpClient okHttpClient = UnsafeOkHttpClient.getUnsafeOkHttpClient();

        // to send request with token
        OkHttpClient updatedClient = okHttpClient
                .newBuilder()
                .addInterceptor(new AuthInterceptor(getContext()))
                .build();

        // Client to make requests to Server-URL
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://217.160.45.110:443/")
                .client(updatedClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // Instance of retrofit with BloomCall
        bloomCall = retrofit.create(BloomCall.class);

        // init default elements
        userKeyInput = view.findViewById(R.id.userKeyInput);
        errorKeyEntry = view.findViewById(R.id.errorKeyEntry);
        errorKeyEntry.setVisibility(View.INVISIBLE);
        Button userKeyButton = view.findViewById(R.id.userKeyButton);

        // get email from previous Fragment
        Bundle bundle = this.getArguments();
        assert bundle != null;
        email = bundle.getString("email");

        // change color of user key button to blue if field is filled
        userKeyInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!TextUtils.isEmpty(Objects.requireNonNull(userKeyInput.getText()).toString())) {
                    userKeyButton.setBackground(ContextCompat.getDrawable(requireActivity(), R.drawable.custom_button));
                    int white = ContextCompat.getColor(requireActivity(), R.color.white);
                    userKeyButton.setTextColor(white);
                }
            }
        });

        // if userKey button clicked finish the hub registration on server
        userKeyButton.setOnClickListener(v -> {
            String userKeyS = Objects.requireNonNull(userKeyInput.getText()).toString();
            userKey = Integer.parseInt(userKeyS);
            postUserKey();
        });

        return view;
    }

    /**
     * HubRegistration Call, if successful login the user with email and password in basicAuth
     */
    private void postUserKey() {

        Call<Void> call = bloomCall.postUserKey(new HubRegModel(userKey, email));

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "Successful: " + response.message());
                    openDashBoardActivity();
                } else {
                    Log.d(TAG, "Error: " + response.code());

                    // if error show Error Message and change Color of Input
                    int red = ContextCompat.getColor(requireActivity(), R.color.red);
                    errorKeyEntry.setVisibility(View.VISIBLE);
                    userKeyInput.setTextColor(red);
                    errorKeyEntry.setText(R.string.entry_wrong_code);
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
     * opens Dashboard Activity
     */
    private void openDashBoardActivity() {
        Handler handler = new Handler();
        // Add new Flag to start new Activity
        Runnable runnable = () -> {
            Intent intent = new Intent(getActivity(), DashboardActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            // Add new Flag to start new Activity
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            requireActivity().finish();
        };
        handler.postDelayed(runnable, 1000);
    }
}
