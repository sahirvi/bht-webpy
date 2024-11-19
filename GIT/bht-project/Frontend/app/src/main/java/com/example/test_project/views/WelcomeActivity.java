package com.example.test_project.views;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.test_project.R;
import com.example.test_project.viewHelper.UserSessionManager;

/**
 * Activity of the Welcome Screen
 */

public class WelcomeActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        new UserSessionManager(this);

        // get elements of the layout
        Button loginButton = findViewById(R.id.loginButton);
        TextView signupLink = findViewById(R.id.loginLink);

        // adds a Sign-Up Fragment
        signupLink.setOnClickListener(v -> addFragment(new SignUpFragment()));

        // adds a Login Fragment
        loginButton.setOnClickListener(v -> addFragment(new LoginFragment()));
    }

    /**
     * Adds a Fragment on top of the Welcome Activity
     *
     * @param fragment Which Fragment to add
     */
    private void addFragment(Fragment fragment) {

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.welcomeLayout, fragment, "findThisFragment")
                .addToBackStack(null)
                .commit();
    }
}
