package com.example.healthconnect;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Timer;
import java.util.TimerTask;

public class SplashScreenActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "OnboardingProcedures";
    private static final String KEY_IS_FIRST_RUN = "isFirstRun";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

//        // Check if it's the first run
//        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
//        boolean isFirstRun = preferences.getBoolean(KEY_IS_FIRST_RUN, true);
//
//        // Set up a delay if it's the first run
//        new Handler().postDelayed(() -> {
//            if(isFirstRun){
//                startActivity(new Intent(SplashScreenActivity.this, OnboardingStep1Activity.class));
//                preferences.edit().putBoolean(KEY_IS_FIRST_RUN, false).apply();
//            } else {
//                startActivity(new Intent(SplashScreenActivity.this, OnboardingStep3Activity.class));
//            }
//            finish();
//        }, 3000);


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
}