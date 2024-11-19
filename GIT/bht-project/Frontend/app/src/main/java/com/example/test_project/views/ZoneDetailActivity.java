package com.example.test_project.views;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.test_project.R;
import com.example.test_project.model.SensorModel;
import com.example.test_project.model.ZoneModel;
import com.example.test_project.viewHelper.UserSessionManager;
import com.example.test_project.viewModel.ZoneDetailViewModel;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Activity of ZoneDetail with ZoneData and SensorData
 */

public class ZoneDetailActivity extends AppCompatActivity {

    private ZoneModel zone;
    private SensorModel sensor;
    private ZoneDetailViewModel zoneDetailViewModel;

    private Integer hubId;
    private Integer zoneId;
    private String zone_name;
    private Boolean auto;
    private static final String TAG = "ZoneDetail";
    private double batteryLimit; // battery max limit
    private double sixtyMinutes; // to display text in min / h
    private double mvLimit; // moisture value max limit

    // default elements for Zone Detail
    private Button waterButton;
    private Button waterButtonStop;
    private TextView zoneName;
    private TextView mvCircleText;
    private ImageView mvCircle;
    private CardView backCard;
    private ImageView backgroundImage;
    private ImageView detailFooter;

    // edit name elements
    private ImageView editIcon;

    // text elements to change
    private TextView wateringTitle;
    private TextView wateringText;
    private TextView moistureValueText;

    // battery
    private ImageView batteryIcon;
    private ImageView batteryIconAlert;
    private TextView batteryTextAlert;

    // is_dry
    private CardView isDryWarnCard;

    // timer elements
    private TextView timer;
    private ImageView timerCircle;

    private long timeLeftInMillis = 10000;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zone_detail);

        Log.d(TAG, "onCreate");

        // get hub_id and zone_id from previous Zone Activity
        Intent intent = getIntent();
        Bundle b = intent.getExtras();
        hubId = (Integer) b.get("hub_id");
        zoneId = (Integer) b.get("zone_id");
        auto = (Boolean) b.get("auto_or_manual");
        Log.d(TAG, "hub ID: " + hubId + "; zone ID: " + zoneId);

        UserSessionManager session = new UserSessionManager(getApplicationContext());
        if (session.checkLogin())
            finish();

        // if back button clicked go back
        ImageView backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> super.onBackPressed());

        //MVVM
        zoneDetailViewModel = new ViewModelProvider(this).get(ZoneDetailViewModel.class);

        // Request Zone and Sensor every 10 seconds
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                zoneDetailViewModel.getZone(getApplicationContext(), hubId, zoneId);
                zoneDetailViewModel.getSensor(getApplicationContext(), hubId, zoneId);
            }
        };

        Timer timer = new Timer();
        long delay = 0;
        long intervalPeriod = 3000;
        timer.scheduleAtFixedRate(task, delay,
                intervalPeriod);

        getZone();
        getSensor();
        layoutDefault();
        wateringEvent();
    }

    /**
     * Observer to change the layout, if getZone in ViewModel gets an different zone
     */
    private void getZone() {
        zoneDetailViewModel.getZoneObserver().observe(this, zoneModel -> {
            if (zoneModel != null) {
                Log.d(TAG, "Zone received: " + zoneModel);
                zone = zoneModel;
                layoutZone();
            } else {
                Log.d(TAG, "No ZoneModel!");
            }
        });
    }

    /**
     * Observer to change the layout, if getSensor in ViewModel gets an different sensor
     */
    private void getSensor() {
        zoneDetailViewModel.getSensorObserver().observe(this, sensorModel -> {
            if (sensorModel != null) {
                Log.d(TAG, "Sensor received: " + sensorModel);
                sensor = sensorModel;
                layoutSensor();
            } else {
                Log.d(TAG, "No SensorModel!");
            }
        });
    }

    /**
     * Updates is_pending with ViewModel
     * changes the layout if is_pending is different
     */
    private void updateIsPending(boolean is_pending) {

        zoneDetailViewModel.getZoneObserver().observe(this, zoneModel -> {
            if (zoneModel != null) {
                zone = zoneModel;
                layoutZone();
            } else {
                Log.d(TAG, "No ZoneModel!");
            }
        });

        zoneDetailViewModel.updateIsPending(this, hubId, zoneId, is_pending);
    }

    /**
     * Default Layout Elements (not dynamical)
     */
    private void layoutDefault() {

        // default elements in Zone Detail
        zoneName = findViewById(R.id.zoneName);
        zoneName.setVisibility(View.VISIBLE);
        // element for delete
        TextView deleteZoneText = findViewById(R.id.deleteZone);
        deleteZoneText.setVisibility(View.VISIBLE);
        mvCircle = findViewById(R.id.mvCircle);
        mvCircle.setVisibility(View.VISIBLE);
        mvCircleText = findViewById(R.id.mvCircleText);
        mvCircleText.setVisibility(View.VISIBLE);
        backCard = findViewById(R.id.cardView);
        backgroundImage = findViewById(R.id.backgroundImage);
        detailFooter = findViewById(R.id.detailFooter);

        // edit name elements
        editIcon = findViewById(R.id.editIcon);
        editIcon.setVisibility(View.VISIBLE);

        // Getting the waterButtons
        waterButton = findViewById(R.id.waterButton);
        waterButton.setVisibility(View.VISIBLE);
        waterButtonStop = findViewById(R.id.waterButtonStop);
        waterButtonStop.setVisibility(View.INVISIBLE);

        // text elements to change
        wateringTitle = findViewById(R.id.watering);
        wateringText = findViewById(R.id.lastWatered);
        moistureValueText = findViewById(R.id.lastMeasurement);
        sixtyMinutes = 60;
        mvLimit = 2;

        // is dry
        isDryWarnCard = findViewById(R.id.isDryCard);
        isDryWarnCard.setVisibility(View.INVISIBLE);

        // battery
        batteryIcon = findViewById(R.id.batteryIcon);
        batteryIcon.setVisibility(View.VISIBLE);
        batteryIconAlert = findViewById(R.id.batteryIconAlert);
        batteryIconAlert.setVisibility(View.INVISIBLE);
        batteryTextAlert = findViewById(R.id.batteryAlertText);
        batteryTextAlert.setVisibility(View.INVISIBLE);
        batteryLimit = 0.1;

        // timer elements
        timer = findViewById(R.id.timer);
        timer.setVisibility(View.INVISIBLE);
        timerCircle = findViewById(R.id.timerCircle);
        timerCircle.setVisibility(View.INVISIBLE);

        // if auto is on
        auto();

        // if Edit Icon pressed, open Edit Name Fragment
        editIcon.setOnClickListener(v -> {
            Log.d(TAG, "Edit Icon clicked");
            Bundle bundle = new Bundle();
            bundle.putInt("hub_id", hubId);
            bundle.putInt("zone_id", zoneId);
            bundle.putString("zone_name", zone_name);
            bundle.putBoolean("auto", auto);

            EditNameFragment editNameFragment = new EditNameFragment();
            editNameFragment.setArguments(bundle);

            // Replace Layout of Activity with EditNameFragment
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.constraintLayout, editNameFragment, "findThisFragment")
                    .addToBackStack(null)
                    .commit();
        });

        // if delete Zone Text is clicked open Delete Zone Fragment
        deleteZoneText.setOnClickListener(v -> {
            Log.d(TAG, "Delete Zone clicked");
            Bundle bundle = new Bundle();
            bundle.putInt("hub_id", hubId);
            bundle.putInt("zone_id", zoneId);
            bundle.putString("zone_name", zone_name);
            bundle.putBoolean("auto", auto);

            ZoneDeleteFragment zoneDeleteFragment = new ZoneDeleteFragment();
            zoneDeleteFragment.setArguments(bundle);

            // Replace Layout of Activity with ZoneDeleteFragment
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.constraintLayout, zoneDeleteFragment, "findThisFragment")
                    .addToBackStack(null)
                    .commit();
        });

    }

    /**
     * if auto is true, change the layout and design
     */
    private void auto() {
        // auto Watering
        if (auto) {
            int darkBlue = ContextCompat.getColor(this, R.color.dark_blue);
            int lightBlue = ContextCompat.getColor(this, R.color.light_blue);

            // change the color of layout items to lightBlue or darkBlue if auto is on
            backCard.setCardBackgroundColor(lightBlue);
            mvCircle.setImageResource(R.drawable.visual_dial_high_auto);
            timerCircle.setImageResource(R.drawable.visual_dial_high_auto);
            wateringTitle.setText(R.string.zone_auto_title);
            editIcon.setColorFilter(darkBlue);
            batteryIcon.setColorFilter(darkBlue);
            waterButton.setVisibility(View.INVISIBLE);
            detailFooter.setImageResource(R.drawable.visual_footer_detail_auto);
        }
    }

    /**
     * function to handle if the Water Button / Water Stop Button is clicked
     */
    private void wateringEvent() {

        // if Button pressed, change is_pending to true (starting Watering Event)
        waterButton.setOnClickListener(v -> {
            Log.d(TAG, "WaterButton clicked");
            updateIsPending(true);
            waterButton.setVisibility(View.INVISIBLE);
            waterButtonStop.setVisibility(View.VISIBLE);
        });

        // if Button pressed, change is_pending to false (stopping Watering Event)
        waterButtonStop.setOnClickListener(v -> {
            Log.d(TAG, "WaterButtonStop clicked");
            updateIsPending(false);
            waterButtonStop.setVisibility(View.INVISIBLE);
            waterButton.setVisibility(View.VISIBLE);
            // making the waterButtonStop invisible to show the waterButton
        });
    }

    /**
     * Layout elements of a zone that changes dynamical
     */
    private void layoutZone() {
        zoneName.setText(zone.getName());
        zone_name = zone.getName();

        if (zoneId == 1) {
            backgroundImage.setImageResource(R.drawable.visual_zone_1);
        } else if (zoneId == 2) {
            backgroundImage.setImageResource(R.drawable.visual_zone_2);
        } else if (zoneId == 3) {
            backgroundImage.setImageResource(R.drawable.visual_zone_3);
        } else if (zoneId == 4) {
            backgroundImage.setImageResource(R.drawable.visual_zone_4);
        }

        // elapsed time between lastWatered and now
        String lastWatered = zone.getLastWatered();
        // format to JavaScript Date.now format
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        LocalDateTime lastWateredTime = LocalDateTime.parse(lastWatered, formatter);

        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);

        // duration between now and lastWatered
        Duration duration = Duration.between(lastWateredTime, now);

        // duration in hours
        long hours = duration.toHours();

        // duration in minutes
        long minutes = duration.toMinutes();

        Log.d(TAG, "lastWatered: " + lastWateredTime);
        Log.d(TAG, "now: " + now);
        Log.d(TAG, "duration: " + duration);
        Log.d(TAG, "hours: " + hours);
        Log.d(TAG, "minutes: " + minutes);

        // case for under five minutes
        if (minutes <= 5) {
            wateringText.setText(R.string.last_watered_now);
        }

        // if minutes is under 60 min, then show in minutes
        else if (minutes < sixtyMinutes) {
            wateringText.setText(getString(R.string.last_watered_minute, minutes, "n"));
        }

        // case for one hour
        else if (hours == 1) {
            wateringText.setText(getString(R.string.last_watered_hour, hours, ""));
        }
        // else show in hours
        else {
            wateringText.setText(getString(R.string.last_watered_hour, hours, "n"));
        }

        if (zone.getIsDry()) {
            isDryWarnCard.setVisibility(View.VISIBLE);
        }
        else {
            isDryWarnCard.setVisibility(View.INVISIBLE);
        }

        // making the waterButton invisible to show the waterButtonStop and start the timer, if is_watering is true
        if (zone.getIsWatering()) {
            wateringText.setText(R.string.zone_watering_text);
            waterButton.setVisibility(View.INVISIBLE);
            waterButtonStop.setVisibility(View.VISIBLE);
            startTimer();
        }

        // making the timer invisible, if is_watering is false
        if (!zone.getIsWatering()) {
            timer.setVisibility(View.INVISIBLE);
            timerCircle.setVisibility(View.INVISIBLE);
        }

        // making the waterButtonStop invisible to show the waterButton, if is_pending is false (manual)
        if (!zone.getIsPending() && !auto){
            waterButtonStop.setVisibility(View.INVISIBLE);
            waterButton.setVisibility(View.VISIBLE);
        }

        // making the waterButtonStop invisible, if is_pending is false (auto)
        if (!zone.getIsPending() && auto){
            waterButtonStop.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * Layout elements of a sensor that changes dynamical
     */
    private void layoutSensor() {

        // elapsed time between updatedAt and now
        String updatedAt = sensor.getUpdatedAt();
        // format to JavaScript Date.now format
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        LocalDateTime lastWateredTime = LocalDateTime.parse(updatedAt, formatter);

        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);

        // duration between now and lastWatered
        Duration duration = Duration.between(lastWateredTime, now);

        // duration in minutes
        long minutes = duration.toMinutes();
        // duration in minutes
        long hours = duration.toHours();

        Log.d(TAG, "updatedAt: " + updatedAt);
        Log.d(TAG, "now: " + now);
        Log.d(TAG, "duration: " + duration);
        Log.d(TAG, "minutes: " + minutes);

        // case for under 5 minutes
        if (minutes <= 5) {
            moistureValueText.setText(R.string.last_measured_now);
        }

        // if minutes is under 60 min, then show in minutes
        else if (minutes < sixtyMinutes) {
            moistureValueText.setText(getString(R.string.last_measured_minute, minutes, "n"));
        }

        // case for one hour
        else if (hours == 1) {
            moistureValueText.setText(getString(R.string.last_measured_hour, hours, ""));
        }

        // else show in hours
        else {
            moistureValueText.setText(getString(R.string.last_measured_hour, hours, "n"));
        }

        // if lastUpdated is 2h ago, make the font red
        if (hours > mvLimit) {
            int color = ContextCompat.getColor(this, R.color.red);
            moistureValueText.setTextColor(color);
            moistureValueText.setTypeface(Typeface.DEFAULT_BOLD);
        }
        else {
            int color = ContextCompat.getColor(this, R.color.black);
            moistureValueText.setTextColor(color);
            moistureValueText.setTypeface(Typeface.DEFAULT);
        }

        // show batteryAlert, if batteryLimit is reached
        if (sensor.getBattery() <= batteryLimit) {
            Log.d(TAG, "Battery: " + sensor.getBattery());
            batteryIcon.setVisibility(View.INVISIBLE);
            batteryIconAlert.setVisibility(View.VISIBLE);
            batteryTextAlert.setVisibility(View.VISIBLE);
        }

        double percent = Math.floor(sensor.getMoistureValue() * 100);
        mvCircleText.setText((int) percent + "%");

        Log.d(TAG, "Percent: " + percent);

        if (percent <= 50) {
            mvCircle.setImageResource(R.drawable.visual_dial_mid_manual);
        }

        if (auto && percent <= 50) {
            mvCircle.setImageResource(R.drawable.visual_dial_mid_auto);
        }

        if (percent <= 20) {
            mvCircle.setImageResource(R.drawable.visual_dial_low);
        }

        if (percent > 50) {
            mvCircle.setImageResource(R.drawable.visual_dial_high_manual);
        }

        if (auto && percent > 50) {
            mvCircle.setImageResource(R.drawable.visual_dial_high_auto);
        }
    }

    /**
     * Timer that takes the Watering Stamp from the server to start a 5 min timer
     */
    private void startTimer() {
        timer.setVisibility(View.VISIBLE);
        timerCircle.setVisibility(View.VISIBLE);
        timer.setText("");

        // get the wateringTimeStamp
        String wateringTimeStamp = zone.getWateringTimestamp();

        // format to JavaScript Date.now format
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        LocalDateTime lastWateredTime = LocalDateTime.parse(wateringTimeStamp, formatter);

        // add five minutes to the wateringTimeStamp
        LocalDateTime minutesLater = lastWateredTime.plusMinutes(1);
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);

        // duration between now and five minutes after wateringTimeStamp
        Duration duration = Duration.between(now, minutesLater);

        // duration in milliseconds

        // set timeLeftInMillis
        timeLeftInMillis = duration.toMillis();

        new CountDownTimer(timeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                updateCountDownText();
            }

            @Override
            public void onFinish() {
                timer.setVisibility(View.INVISIBLE);
                timerCircle.setVisibility(View.INVISIBLE);
            }
        }.start();
//        boolean timerRunning = true;
    }

    // updates the Countdown Text
    private void updateCountDownText() {
        int minutes = (int) (timeLeftInMillis / 1000) / 60;
        int seconds = (int) (timeLeftInMillis / 1000) % 60;
        String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
        timer.setText(timeLeftFormatted);
    }
}
