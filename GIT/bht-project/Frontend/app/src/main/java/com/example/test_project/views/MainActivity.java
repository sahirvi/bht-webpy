package com.example.test_project.views;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.widget.ImageView;

import com.example.test_project.R;
import com.example.test_project.viewHelper.UserSessionManager;
import com.airbnb.lottie.LottieAnimationView;

/**
 * Activity that shows splash screen after being invoked.
 * */

public class MainActivity extends AppCompatActivity {


    private Handler handler;
    private Runnable runnable;

    private UserSessionManager session;
    private ImageView appName;
    private ImageView splashImage;
    private LottieAnimationView lottieAnimationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        session = new UserSessionManager(getApplicationContext());

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        appName = findViewById(R.id.app_name_splash);
        //splashImage = findViewById(R.id.splash_bg_img);
        lottieAnimationView = findViewById(R.id.lottie);

//        splashImage.animate().translationY(-2500).setDuration(1000).setStartDelay(5000);
//        appName.animate().translationY(2000).setDuration(1000).setStartDelay(5000);
//        lottieAnimationView.animate().translationY(1500).setDuration(1000).setStartDelay(5000);

        /*
         * Handler runs Runnable after 3000 ms
         * Intent starts new activity
         */

        //If token exists, go to personal page immediately
        //Todo: Currently when token run out at start, not Authorized in HomeActivity

//        if (session.isUserLoggedIn() == true){
//            openHomeActivity();
//        }
//        else{
//            openStartActivity();
//        }

        //Todo beim Demonstrieren sofort zum Login / Signup

        openOnBoardingActivity();

    }



    /**
     * opens Dashboard Activity
     */
    private void openDashboardActivity() {
        handler = new Handler();
        runnable = () -> {
            Intent intent = new Intent(MainActivity.this, DashboardActivity.class);
            startActivity(intent);
            finish();
        };
        handler.postDelayed(runnable, 100);
    }

    /**
     * opens onBoarding Activity
     */
    private void openOnBoardingActivity() {
        handler = new Handler();
        runnable = () -> {
            Intent intent = new Intent(MainActivity.this, OnBoardingActivity.class);
            startActivity(intent);
            finish();
        };
        handler.postDelayed(runnable, 2000);
    }

}