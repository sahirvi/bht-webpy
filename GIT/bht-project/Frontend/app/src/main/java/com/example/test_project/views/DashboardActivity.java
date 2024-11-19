package com.example.test_project.views;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.test_project.R;
import com.example.test_project.api.WeatherService;
import com.example.test_project.model.HubModel;
import com.example.test_project.model.Weather;
import com.example.test_project.model.WeatherModel;
import com.example.test_project.model.ZoneModel;
import com.example.test_project.viewHelper.DryZoneRecyclerAdapter;
import com.example.test_project.viewHelper.UserSessionManager;
import com.example.test_project.viewHelper.WeatherClient;
import com.example.test_project.viewHelper.ZoneRecyclerAdapter;
import com.example.test_project.viewModel.ZoneDataViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class DashboardActivity extends AppCompatActivity implements ZoneRecyclerAdapter.ItemClickListener, LocationListener{

    private static final String TAG = "Dashboard Activity";

    RecyclerView recyclerView;
    LinearLayoutManager layoutManager;
    ZoneRecyclerAdapter adapter;
    int zoneCount;

    RecyclerView dRecyclerView;
    LinearLayoutManager dLayoutManager;
    DryZoneRecyclerAdapter dAdapter;


    SwitchCompat autoSwitch;
    Boolean switchStatus;

    TextView autowater_message;
    TextView autowater_text;
    TextView humidity;
    TextView temperature;
    TextView city_name;
    RelativeLayout autowater_card;
    RelativeLayout dash_warn_waterfill_card;
    RelativeLayout no_zones;
    ImageButton addButton;
    ImageView humidity_icon;
    ImageView weather_icon;
    LinearLayout dash_zone_item_container;
    ImageView footer_img;


    ZoneDataViewModel zoneDataViewModel;
    List<ZoneModel> zoneDataList;

    int hub_Id;

    UserSessionManager session;

    LocationManager locationManager;
    Location location;
    double latitude, longitude;
    int mHumidity;
    double mTemperature;
    String city;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        //Logs

        Log.d(TAG, "onCreate: in Dashboard activity ");


        //Session
        session = new UserSessionManager(getApplicationContext());
        if(session.checkLogin())
            finish();

        //MVVM
        zoneDataViewModel = new ViewModelProvider(this).get(ZoneDataViewModel.class);


        //Inits
        autoSwitch = findViewById(R.id.autowater_switch);
        autowater_message = findViewById(R.id.autowater_message);
        autowater_text = findViewById(R.id.autowater_text);
        autowater_card = findViewById(R.id.autowater_card);
        dash_zone_item_container = findViewById(R.id.dash_zone_item_container);
        addButton = findViewById(R.id.add_icon);
        dash_warn_waterfill_card = findViewById(R.id.dash_warn_waterfill_card);
        footer_img = findViewById(R.id.footer_img);
        no_zones = findViewById(R.id.no_zones);
        humidity = findViewById(R.id.humidity);
        temperature = findViewById(R.id.temperature);
        humidity_icon = findViewById(R.id.humidity_icon);
//        city_name = findViewById(R.id.city_name);
//        weather_icon = findViewById(R.id.weather_icon);


        // Watertank
        dash_warn_waterfill_card.setVisibility(View.GONE);

        autoSwitch.setChecked(session.getSwitchStatus());

        //Weather from Session
        mTemperature = session.getTemp();
        mHumidity = session.getHumidity();
//        city = session.getCityName();

        Log.d(TAG, "CITY: " + city);
        double celsius = mTemperature - 273.15;
        String formatted_celsius = String.format("%.1f", celsius);
        humidity.setText(mHumidity+"%");
        temperature.setText(formatted_celsius+"°C");
//        city_name.setText(city);



        // Actions

        addZone();
        changeAutoOrManual();
        getUserHub();

        Handler handler = new Handler();
        Runnable runnable = () -> {
            getLocation();
        };
        handler.postDelayed(runnable, 10000);


        Handler handler2 = new Handler();

        //Request Hub Call every 10 seconds
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                zoneDataViewModel.makeHubCall(getApplicationContext());

                Runnable runnable2 = () -> {
                    weatherCall();
                };
                handler2.postDelayed(runnable2, 15000);
            }

        };

        Timer timer = new Timer();
        long delay = 0;
        long intervalPeriod = 1 * 3000;
        timer.scheduleAtFixedRate(task, delay,
                intervalPeriod);


    }

    private void getLocation() {
        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 101);
        }

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);

        if (locationManager != null) {
            location = locationManager
                    .getLastKnownLocation(LocationManager.GPS_PROVIDER);
        }

    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        session.saveLocation(longitude, latitude);

    }


//    @Override
//    protected void onStop() {
//        super.onStop();
//        session.logoutUser();
//        Intent intent = new Intent(this, WelcomeActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//
//        // Add new Flag to start new Activity
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        startActivity(intent);
//    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        session.logoutUser();
        Intent intent = new Intent(this, WelcomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // Add new Flag to start new Activity
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    /**
     * OnClickListener for adding a new Zone
     */
    private void addZone() {
        addButton.setOnClickListener(e -> {

            Bundle bundle = new Bundle();

            bundle.putInt("zoneCount", zoneCount);
            bundle.putInt("hubId", hub_Id);

            CreateZoneFragment createZoneFragment = new CreateZoneFragment();
            createZoneFragment.setArguments(bundle);

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.layout_dashboard, createZoneFragment)
                    .addToBackStack(null)
                    .commit();
        });
    }


    /**
     * MVVM zoneDataViewModel sends Hub Data.
     * Sets the hubID globally
     * and checks if Bucket is empty.
     * Updates Hub when Setting is changed to
     * Manual or Automatic watering.
     */

    private void getUserHub() {
        zoneDataViewModel.getHubDataObserver().observe(this, new Observer<HubModel>() {
            @Override
            public void onChanged(HubModel hubModel) {
                //checkHeader();
                if (hubModel != null){
                    //checkHeader();

                    hub_Id = hubModel.getHubId();
                    //Wassertank leer
                    if(hubModel.getBucketEmpty() && hubModel.getBucketEmpty() != null) {
                        dash_warn_waterfill_card.setVisibility(View.VISIBLE);
                    }
                    else {
                        dash_warn_waterfill_card.setVisibility(View.GONE);
                    }

                    //Hub auto or manual

                    //ToDo make UI remember switchStatus for next Login with UserSessionManager
                    if (hubModel.getAuto()){
                        switchStatus = true;
                        session.setSwitchStatus(switchStatus);
                        if (hubModel.getAuto() != null){
                            autoSwitch.setChecked(hubModel.getAuto());
                        }else{
                            autoSwitch.setChecked(session.getSwitchStatus());
                        }
                    }
                    else{
                        switchStatus = false;
                        session.setSwitchStatus(switchStatus);

                        if (hubModel.getAuto() != null){
                            autoSwitch.setChecked(hubModel.getAuto());
                        }else{
                            autoSwitch.setChecked(session.getSwitchStatus());
                        }
                    }

                    if (hubModel.getAuto() != null){
                        updateAuto(hubModel.getAuto());
                    }
                    else{
                        updateAuto(session.getSwitchStatus());
                    }
                    //changeUIAuto(hubModel.getAuto());

                    //After hub Data is fetched, get all Zones for this hub
                    getZoneObserverData();

                }
            }
        });

    }


    /**
     * OnCheckedListener for Switch
     * when Switch is true, is automatic
     * if false, then manual
     */
    private void changeAutoOrManual() {

        autoSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //API CALL to update hub on server
                updateAuto(isChecked);
                switchStatus = isChecked;
                session.setSwitchStatus(isChecked);

                //change UI
                changeUIAuto(isChecked);
            }
        });
    }


    /**
     * Update Hub on Server when Switch is changed
     * @param isChecked
     */
    private void updateAuto(boolean isChecked) {

        zoneDataViewModel.getHubUpdateDataObserver().observe(this, new Observer<HubModel>() {
            @Override
            public void onChanged(HubModel hubModel) {
                if (hubModel != null){
                    switchStatus = hubModel.getAuto();
                    session.setSwitchStatus(switchStatus);
                    changeUIAuto(session.getSwitchStatus());
                }
                else{
                    session.setSwitchStatus(switchStatus);
                    changeUIAuto(session.getSwitchStatus());
                }
            }
        });

        zoneDataViewModel.makeHubUpdate(this, hub_Id, isChecked);
    }




    //Get Zones for Hub
    private void getZoneObserverData() {

        //RecyclerView for Zones
        recyclerView = findViewById(R.id.recyclerViewDashboard);
        layoutManager = new GridLayoutManager(this, 2);
        layoutManager.setOrientation(RecyclerView.VERTICAL);

        //layoutManager der RecyclerView hinzufügen
        recyclerView.setLayoutManager(layoutManager);

        adapter = new ZoneRecyclerAdapter(this, this, switchStatus);
        recyclerView.setAdapter(adapter);

        //RecyclerView for Dry Zone Warnings

        dRecyclerView = findViewById(R.id.recyclerViewDashboardDry);
        dLayoutManager = new LinearLayoutManager(this);
        dLayoutManager.setOrientation(RecyclerView.VERTICAL);

        //layoutManager der RecyclerView hinzufügen
        dRecyclerView.setLayoutManager(dLayoutManager);
        dAdapter = new DryZoneRecyclerAdapter(this, new DryZoneRecyclerAdapter.ItemClickListener() {
            @Override
            public void onItemClick(int position) {
                int zone_id = zoneDataList.get(position).getZoneId();
                Intent intent = new Intent(getApplicationContext(), ZoneDetailActivity.class);
                intent.putExtra("auto_or_manual", switchStatus);
                intent.putExtra("zone_id", zone_id);
                intent.putExtra("hub_id", hub_Id);
                startActivity(intent);
            }
        });
        dRecyclerView.setAdapter(dAdapter);

        //Observe zoneData coming in async
        zoneDataViewModel.getZoneDataObserver().observe(this, new Observer<List<ZoneModel>>() {
            @Override
            public void onChanged(List<ZoneModel> zoneModels) {
                if (zoneModels != null){
                    zoneDataList = zoneModels;
                    List<ZoneModel> distinctZoneDataList = zoneModels.stream().distinct().collect(Collectors.toList());

                    zoneCount = zoneDataList.size();
                    if (zoneDataList.size() == 0){
                        no_zones.setVisibility(View.VISIBLE);
                    }
                    else{
                        no_zones.setVisibility(View.GONE);
                    }
                    adapter.setData(distinctZoneDataList);

                    addButtonorNot(zoneCount);

                    List<ZoneModel> dryZones;

                    if (switchStatus == false){
                        for (ZoneModel d : zoneDataList){
                            if (d.getIsDry() == true){
                                dryZones = new ArrayList<>();
                                dryZones.add(d);
                                dAdapter.setData(dryZones);
                            }
                        }
                    }


                }
            }
        });

        //make the Api Calls
        zoneDataViewModel.makeZonesCall(this, hub_Id);

        if (zoneCount == 0){
            no_zones.setVisibility(View.VISIBLE);
        }
        else{
            no_zones.setVisibility(View.GONE);
        }

    }

    /**
     * Inits when UI is changed
     * @param auto
     */

    private void changeUIAuto(Boolean auto) {

        if (auto){
            autowater_card.setBackground(getResources().getDrawable(R.drawable.custom_autowater));
            autowater_message.setText(getResources().getText(R.string.dash_auto_water_title));
            autowater_text.setText(getResources().getText(R.string.dash_auto_water_text));
            footer_img.setImageResource(R.drawable.visual_footer_dashboard_auto);
            addButton.setColorFilter(getResources().getColor(R.color.dark_blue));

            if (recyclerView != null){
                for (int i = 0; i < zoneCount; i++){
                    recyclerView.getLayoutManager().getChildAt(i).findViewById(R.id.dash_zone_item_container).setBackground(getResources().getDrawable(R.drawable.autowater));
                }
            }
        }
        else{
            autowater_card.setBackground(getResources().getDrawable(R.drawable.custom_manualwater));
            autowater_message.setText(getResources().getText(R.string.dash_manual_watering_title));
            autowater_text.setText(getResources().getText(R.string.dash_manual_watering_text));
            footer_img.setImageResource(R.drawable.visual_footer_dashboard_manual);
            addButton.setColorFilter(getResources().getColor(R.color.dark_green));

            if (recyclerView != null){
                for (int i = 0; i < zoneCount; i++){
                    recyclerView.getLayoutManager().getChildAt(i).findViewById(R.id.dash_zone_item_container).setBackground(getResources().getDrawable(R.drawable.manualwater));
                }
            }
        }
    }

    /**
     * Weather Call
     * Get Temperature and Humidity of Location that is set in the settings
     */

    private void weatherCall(){
        Retrofit retrofit = WeatherClient.getInstance();

        // Instance of retrofit with BloomCall
        WeatherService weatherCall = retrofit.create(WeatherService.class);
        String api_key ="8e2b1a39043fbcaaa31e31e2d6b584e6";
        Call<WeatherModel> call = weatherCall.getWeatherData(latitude, longitude, api_key);

        call.enqueue(new Callback<WeatherModel>() {
            @Override
            public void onResponse(@NonNull Call<WeatherModel> call, @NonNull Response<WeatherModel> response) {

                // put the response.body in zone
                if (response.isSuccessful()) {
//                    mTemperature = response.body().getMain().getTemp();
//                    mHumidity = response.body().getMain().getHumidity();

                    session.saveWeather(response.body());

                    mTemperature = session.getTemp();
                    mHumidity = session.getHumidity();

                    Log.d(TAG, "sTemperature" + mTemperature);

                    double celsius = mTemperature - 273.15;
                    String formatted_celsius = String.format("%.1f", celsius);
                    humidity.setText(mHumidity+"%");
                    temperature.setText(formatted_celsius+"°C");
//                    city_name.setText(session.getCityName());
                } else {
                    Log.d(TAG, "onResponse, but still failed: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<WeatherModel> call, @NonNull Throwable t) {
                Log.d(TAG, "Error: " + t.getMessage());
            }
        });
    }


    //If more then 3 Zones, Remove the Add Button
    private void addButtonorNot(int zoneCount) {
        if (zoneCount > 3){
            addButton.setVisibility(View.GONE);
        }
    }


    //OnClickListener for RecyclerView ViewHolders
    @Override
    public void onItemClick(int position) {
        int zone_id = zoneDataList.get(position).getZoneId();
        Log.d(TAG, "onItemClick: " + zoneDataList.get(position).getZoneId());
        //Toast.makeText(this, "" + zoneDataList.get(position).getZoneId(), Toast.LENGTH_SHORT).show();

        //go To ZoneDetail
        Intent intent = new Intent(this, ZoneDetailActivity.class);
        intent.putExtra("auto_or_manual", switchStatus);
        intent.putExtra("zone_id", zone_id);
        intent.putExtra("hub_id", hub_Id);
        startActivity(intent);
    }

}