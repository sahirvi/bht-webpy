package com.example.test_project.views;

import android.content.res.ColorStateList;
import android.hardware.Sensor;
import android.media.metrics.LogSessionId;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.test_project.R;
import com.example.test_project.api.BloomCall;
import com.example.test_project.model.SensorModel;
import com.example.test_project.model.ZoneModel;
import com.example.test_project.viewHelper.UserSessionManager;
import com.example.test_project.viewModel.SensorViewModel;
import com.example.test_project.viewModel.ZoneDataViewModel;

import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Retrofit;

public class CreateZoneNextFragment extends Fragment {
    View view;

    private static final String TAG = "Create Zone Next Fragment";


    int hub_Id, zone_Id;

    FrameLayout contentstart;
    AppCompatButton btn_sensor_next;
    TextView btn_sensor_cancel;
    //ImageView create_zone_sensor_back;
    TextView connect_sensor_pending;

    ImageView create_zone_next_image;
    TextView sensor_next_title;
    TextView create_zone_next_title;
    TextView sensor_next_text;
    RelativeLayout sensor_next_layout;
    TextView sensor_next_link;
    ImageView sensor_image;

    RelativeLayout footer_flower;


    TimerTask task;
    Timer timer;

    int sensor_zone_id;

    ZoneDataViewModel zoneDataViewModel;

    UserSessionManager session;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_addzone_next, container, false);



        //UserSession

        session = new UserSessionManager(getContext());

        //Bundle
        Bundle bundle = this.getArguments();

        if (bundle != null){
            hub_Id = bundle.getInt("hubId");
            zone_Id = bundle.getInt("zoneId");
            Log.d(TAG, "Next zone: " + zone_Id + " hub: " + hub_Id);
        }



        //init views
        btn_sensor_next = view.findViewById(R.id.btn_sensor_next);
        btn_sensor_cancel = view.findViewById(R.id.btn_sensor_cancel);
        //create_zone_sensor_back = view.findViewById(R.id.create_zone_sensor_back);
        connect_sensor_pending = view.findViewById(R.id.connect_sensor_pending);
        create_zone_next_image = view.findViewById(R.id.create_zone_next_image);
        create_zone_next_title = view.findViewById(R.id.create_zone_next_title);
        sensor_next_title = view.findViewById(R.id.sensor_next_title);
        sensor_next_text = view.findViewById(R.id.sensor_next_text);
        sensor_next_layout = view.findViewById(R.id.sensor_next_layout);
//        sensor_next_link = view.findViewById(R.id.sensor_next_link);
        //sensor_image = view.findViewById(R.id.sensor_image);
        footer_flower = getActivity().findViewById(R.id.footer_flower);




        //Hide Parent Content
        contentstart = getActivity().findViewById(R.id.scroll_layout_dashboard);
        contentstart.setVisibility(View.INVISIBLE);
        //sensor_image.setVisibility(View.GONE);
        footer_flower.setVisibility(View.GONE);


        zoneDataViewModel = new ViewModelProvider(this).get(ZoneDataViewModel.class);



        getSensor();
        //connectSensor();


        task = new TimerTask() {
            @Override
            public void run() {
                Log.d(TAG, "get Sensor every second");
                zoneDataViewModel.getOneSensorCall(getContext(), zone_Id, hub_Id);
            }
        };

        timer = new Timer();
        long delay = 0;
        long intervalPeriod = 1 * 1000;
        timer.scheduleAtFixedRate(task, delay,
                intervalPeriod);

        //Next Button
        btn_sensor_next.setOnClickListener(v -> {
            contentstart.setVisibility(View.VISIBLE);
            footer_flower.setVisibility(View.VISIBLE);

            getParentFragmentManager().beginTransaction().remove(this).commit();
            task.cancel();
        });

        create_zone_next_title.setText("Gießzone " + zone_Id + " einrichten");


        //Abbrechen Link
        btn_sensor_cancel.setOnClickListener(v -> {
            contentstart.setVisibility(View.VISIBLE);
            footer_flower.setVisibility(View.VISIBLE);
            getParentFragmentManager().beginTransaction().remove(this).commit();
            task.cancel();
        });

//        // Back Button
//        create_zone_sensor_back.setOnClickListener(v -> {
//            contentstart.setVisibility(View.VISIBLE);
//            getParentFragmentManager().beginTransaction().remove(this).commit();
//            task.cancel();
//        });


        return view;
    }

    private void createZone() {

        zoneDataViewModel.postZoneDataObserver().observe(getViewLifecycleOwner(), new Observer<ZoneModel>() {
            @Override
            public void onChanged(ZoneModel zoneModel) {
                if (zoneModel != null){
                    Log.d(TAG, zoneModel.getCreatedAt());
                    task.cancel();
                    changeUI();
                }
                else{
                    Log.d(TAG, "zoneModel is empty");
                    getSensor();
                }
            }
        });

        zoneDataViewModel.createZone(getContext(), hub_Id, zone_Id);
    }

    private void changeUI() {
        sensor_next_layout.setVisibility(View.GONE);
        create_zone_next_image.setImageResource(R.drawable.visual_bloom_box_happy_looking_down);
        sensor_next_title.setText("Sensor " + zone_Id + " verbunden!");
        btn_sensor_next.setClickable(true);
        btn_sensor_next.setBackground(getResources().getDrawable(R.drawable.custom_button));
//        btn_sensor_next.setBackgroundColor(getResources().getColor(R.color.dark_blue));
        btn_sensor_next.setTextColor(getResources().getColor(R.color.white));
        //sensor_image.setVisibility(View.VISIBLE);
        sensor_next_text.setText(R.string.sensor_created_text);
    }

    private void connectSensor() {
        //Todo SensorModel add Constructor for Update
        Log.d(TAG, "in Connect Sensor");
        zoneDataViewModel.getAddedSensorObserver().observe(getActivity(), new Observer<SensorModel>() {
            @Override
            public void onChanged(SensorModel sensorModel) {
                createZone();
                Log.d(TAG, "Sensor created");
            }
        });

        zoneDataViewModel.addSensorCall(getContext(), zone_Id, zone_Id, hub_Id);
    }

    private void getSensor() {
        Log.d(TAG, "in Connect Sensor " + zone_Id + "hub: " + hub_Id);
        zoneDataViewModel.getSensorObserver().observe(getViewLifecycleOwner(), new Observer<SensorModel>() {
            @Override
            public void onChanged(SensorModel sensorModel) {

                if (sensorModel != null){
                    Log.d(TAG, "GET SENSOR:  " + sensorModel.getZoneId());
                    sensor_zone_id = sensorModel.getZoneId();
                    //task.cancel();
                    //createZone();
                    openSuccessFragment();
                }
            }
        });

    }

    private void openSuccessFragment() {
        CreateZoneSuccessFragment nextFragment = new CreateZoneSuccessFragment();

        Bundle bundle3 = new Bundle();
        bundle3.putInt("zoneId", sensor_zone_id);
        bundle3.putInt("hubId", hub_Id);
        nextFragment.setArguments(bundle3);

        getParentFragmentManager().beginTransaction().remove(this).commit();
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.layout_dashboard, nextFragment, "findThisFragment")
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onPause() {
        super.onPause();
        task.cancel();
        timer.cancel();
        contentstart.setVisibility(View.VISIBLE);
    }

    @Override
    public void onStop() {
        super.onStop();
        task.cancel();
        timer.cancel();
        contentstart.setVisibility(View.VISIBLE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        task.cancel();
        timer.cancel();
        contentstart.setVisibility(View.VISIBLE);
    }

    @Override
    public void onDetach() {
        task.cancel();
        timer.cancel();
        super.onDetach();
    }
}