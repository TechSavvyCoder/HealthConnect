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
    private static final String KEY_USER_ROLE = "userRole";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        // Initialize the database helper
        dbHelper = new MyDatabaseHelper(this);
//        resetSession();
//        resetDatabase();

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                isDemo(true); // set to true if for demo purposes
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

    private void resetSession() {
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        editor.putBoolean(KEY_IS_FIRST_RUN, true);
        editor.putBoolean(KEY_IS_LOGGED_IN, false);
        editor.putString(KEY_USER_ROLE, "");
        editor.apply();
    }

    private void isDemo(boolean isDemo) {
        if(!isDemo){
            // SharedPreferences instance
            SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

            boolean isFirstRun = preferences.getBoolean(KEY_IS_FIRST_RUN, true);
            boolean isLoggedIn = preferences.getBoolean(KEY_IS_LOGGED_IN, false);
            String userRole = preferences.getString(KEY_USER_ROLE, "");

            if (isFirstRun) {
                // If it's the first run, show Onboarding Step 1
                startActivity(new Intent(SplashScreenActivity.this, OnboardingStep1Activity.class));

                // Update shared preferences to indicate that onboarding has been completed
                preferences.edit().putBoolean(KEY_IS_FIRST_RUN, false).apply();
            } else {
                if (isLoggedIn) {
                    if(userRole.equals("Doctor")){
                        startActivity(new Intent(SplashScreenActivity.this, MainActivity.class));
                    } else if(userRole.equals("Patient")){
                        startActivity(new Intent(SplashScreenActivity.this, Patient_Profile.class));
                    }
                } else {
                    // If not first run and user is not logged in, show Onboarding Step 3
                    startActivity(new Intent(SplashScreenActivity.this, OnboardingStep3Activity.class));
                }
            }
            finish();
        } else {
            startActivity(new Intent(SplashScreenActivity.this, OnboardingStep1Activity.class));
        }
    }
}