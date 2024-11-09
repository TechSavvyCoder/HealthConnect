package com.example.healthconnect;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Timer;
import java.util.TimerTask;

public class SplashScreenActivity extends AppCompatActivity {

    MyDatabaseHelper dbHelper;
    private static final String PREFS_NAME = "OnboardingProcedures";
    private static final String KEY_IS_FIRST_RUN = "isFirstRun";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        // SharedPreferences instance
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        // Check if it's the first run
        boolean isFirstRun = preferences.getBoolean(KEY_IS_FIRST_RUN, true);
        boolean isLoggedIn = preferences.getBoolean(KEY_IS_LOGGED_IN, false); // Check if the user is logged in

        // Initialize the database helper
        dbHelper = new MyDatabaseHelper(this);
        // resetDatabase();
        // dbHelper.makeThisTable();

        // Set up a delay before transitioning to the next activity
//        new Handler().postDelayed(() -> {
//            if (isFirstRun) {
//                // If it's the first run, show Onboarding Step 1
//                startActivity(new Intent(SplashScreenActivity.this, OnboardingStep1Activity.class));
//
//                // Update shared preferences to indicate that onboarding has been completed
//                preferences.edit().putBoolean(KEY_IS_FIRST_RUN, false).apply();
//            } else {
//                if (isLoggedIn) {
//                    // If not first run and user is logged in, go to MainActivity
//                    startActivity(new Intent(SplashScreenActivity.this, MainActivity.class));
//                } else {
//                    // If not first run and user is not logged in, show Onboarding Step 3
//                    startActivity(new Intent(SplashScreenActivity.this, OnboardingStep3Activity.class));
//                }
//            }
//            finish(); // Close the SplashScreenActivity
//        }, 3000); // 3 seconds delay for splash screen


        // Temp Timer (To show the onboarding process)
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                finish(); // end the life cycle of the SplashActivity by closing the activity
                startActivity(new Intent(SplashScreenActivity.this, OnboardingStep1Activity.class));
            }
        };

        Timer opening = new Timer();
        opening.schedule(task, 3000);

        // Hide the ActionBar
        getSupportActionBar().hide();
    }

    private void resetDatabase() {
        dbHelper.resetDatabase();
    }
}