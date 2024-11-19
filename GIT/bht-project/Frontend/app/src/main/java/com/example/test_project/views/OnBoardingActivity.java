package com.example.test_project.views;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.test_project.R;

/**
 * Activity of the OnBoarding Screens
 */

public class OnBoardingActivity extends AppCompatActivity {
    private Button onBoardingButton1;
    private Button onBoardingButton2;
    private Button onBoardingButton3;
    private TextView onBoardingText;
    private ImageView dot1;
    private ImageView dot2;
    private ImageView dot3;
    private ImageView onBoardingBackground;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

        // elements from layout
        onBoardingButton1 = findViewById(R.id.continueButton1);
        onBoardingButton2 = findViewById(R.id.continueButton2);
        onBoardingButton2.setVisibility(View.INVISIBLE);
        onBoardingButton3 = findViewById(R.id.continueButton3);
        onBoardingButton3.setVisibility(View.INVISIBLE);
        onBoardingText = findViewById(R.id.onBoardingText);
        dot1 = findViewById(R.id.dot1);
        dot2 = findViewById(R.id.dot2);
        dot3 = findViewById(R.id.dot3);
        onBoardingBackground = findViewById(R.id.onBoardingBackground);

        int darkBlue = ContextCompat.getColor(this, R.color.dark_blue);
        int lightBlue = ContextCompat.getColor(this, R.color.light_blue);

        dot1.setColorFilter(darkBlue);

        // if button clicked change onBoardingText
        onBoardingButton1.setOnClickListener(v -> {
            onBoardingButton1.setVisibility(View.INVISIBLE);
            onBoardingButton2.setVisibility(View.VISIBLE);
            onBoardingText.setText(R.string.onboarding2);
            dot2.setColorFilter(darkBlue);
            dot1.setColorFilter(lightBlue);
            onBoardingBackground.setImageResource(R.drawable.visual_background_onboarding_2);
        });

        // if button clicked change onBoardingText
        onBoardingButton2.setOnClickListener(v -> {
            onBoardingButton2.setVisibility(View.INVISIBLE);
            onBoardingButton3.setVisibility(View.VISIBLE);
            onBoardingText.setText(R.string.onboarding3);
            dot3.setColorFilter(darkBlue);
            dot2.setColorFilter(lightBlue);
            onBoardingBackground.setImageResource(R.drawable.visual_background_onboarding_3);
        });

        // if button clicked open StartActivity
        onBoardingButton3.setOnClickListener(v -> openWelcomeActivity());
    }

    /**
     * function to start StartActivity
     */
    private void openWelcomeActivity() {
        Handler handler = new Handler();
        Runnable runnable = () -> {
            Intent intent = new Intent(OnBoardingActivity.this, WelcomeActivity.class);
            startActivity(intent);
            finish();
        };
        handler.postDelayed(runnable, 100);
    }
}
