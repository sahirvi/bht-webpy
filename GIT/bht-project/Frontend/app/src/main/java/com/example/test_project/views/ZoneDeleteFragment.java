package com.example.test_project.views;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.test_project.R;
import com.example.test_project.api.BloomCall;
import com.example.test_project.model.SensorModel;
import com.example.test_project.model.ZoneModel;
import com.example.test_project.viewHelper.AuthInterceptor;
import com.example.test_project.viewHelper.UnsafeOkHttpClient;
import com.example.test_project.viewHelper.UserSessionManager;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Fragment to open delete Zone PopUp
 */
public class ZoneDeleteFragment extends Fragment {

    private BloomCall bloomCall;

    private Integer hubId;
    private Integer zoneId;
    private UserSessionManager session;

    private static final String TAG = "ZoneDeleteFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_zone_delete, container, false);

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

        // get hub_id, zone_id and zone_name from ZoneDetail
        Bundle bundle = this.getArguments();
        assert bundle != null;
        hubId = (Integer) bundle.get("hub_id");
        zoneId = (Integer) bundle.get("zone_id");
        String zone_name = (String) bundle.get("zone_name");
        Boolean auto = (Boolean) bundle.get("auto");

        Log.d(TAG, "hub ID: " + hubId + "; zone ID: " + zoneId + "; zoneName: " + zone_name);

        // get the layout elements
        TextView zoneName = view.findViewById(R.id.zoneName);
        zoneName.setText(zone_name);
        TextView cancel = view.findViewById(R.id.cancel);
        Button deleteButton = view.findViewById(R.id.deleteButton);
        CardView cardViewDelete = view.findViewById(R.id.cardViewDelete);
        ImageView deleteFooter = view.findViewById(R.id.deleteFooter);
        TextView deleteZoneTitle = view.findViewById(R.id.deleteZoneTitle);

        deleteZoneTitle.setText(getString(R.string.zonedelete_warning_title, zoneId));

        if (auto) {
            int lightBlue = ContextCompat.getColor(requireContext(), R.color.light_blue);

            // change the color of background card to lightBlue if auto is on
            cardViewDelete.setCardBackgroundColor(lightBlue);
            deleteFooter.setImageResource(R.drawable.visual_footer_detail_auto);
        }

        // if delete Button pressed, delete Zone and Sensor
        deleteButton.setOnClickListener(v -> {
            Log.d(TAG, "DeleteButton clicked");
            deleteZoneAndSensor();
        });

        // if cancel Text is clicked change the View
        cancel.setOnClickListener(v -> {
            Log.d(TAG, "Cancel Delete Zone clicked");
            getParentFragmentManager().popBackStack();
        });
        // Inflate the layout for this fragment
        return view;
    }

    /**
     * Deletes a zone and a sensor with hub_id and zone_id
     */

    private void deleteZoneAndSensor() {

        Call<ZoneModel> callZone = bloomCall.deleteZone(hubId, zoneId);
        callZone.enqueue(new Callback<ZoneModel>() {
            @Override
            public void onResponse(@NonNull Call<ZoneModel> call, @NonNull Response<ZoneModel> response) {
                // check if new header because of refresh token
                if (response.headers().get("Authorization") != null) {
                    session = new UserSessionManager(requireContext());
                    Log.d(TAG, "ZoneDetailViewModel: " + response.headers().get("Authorization"));
                    session.logoutUser();
                    session.createUserLoginSession(response.headers().get("Authorization"));
                }
                if (response.isSuccessful()) {
                    Log.d(TAG, "deleted zone: " + response.body());
                    openDashBoardActivity();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ZoneModel> call, @NonNull Throwable t) {
                Log.e(TAG, "deleting zone failed: " + t.getMessage());
                t.printStackTrace();
            }
        });

        Call<SensorModel> callSensor = bloomCall.deleteSensor(hubId, zoneId);
        callSensor.enqueue(new Callback<SensorModel>() {
            @Override
            public void onResponse(@NonNull Call<SensorModel> call, @NonNull Response<SensorModel> response) {
                // check if new header because of refresh token
                if (response.headers().get("Authorization") != null) {
                    session = new UserSessionManager(requireContext());
                    Log.d(TAG, "ZoneDetailViewModel: " + response.headers().get("Authorization"));
                    session.logoutUser();
                    session.createUserLoginSession(response.headers().get("Authorization"));
                }
                if (response.isSuccessful()) {
                    Log.e(TAG, "deleted sensor: " + response.body());
                }
            }

            @Override
            public void onFailure(@NonNull Call<SensorModel> call, @NonNull Throwable t) {
                Log.e(TAG, "deleting sensor failed: " + t.getMessage());
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
        handler.postDelayed(runnable, 500);
    }

}