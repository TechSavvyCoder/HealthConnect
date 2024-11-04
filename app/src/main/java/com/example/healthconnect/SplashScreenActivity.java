package com.example.healthconnect;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Timer;
import java.util.TimerTask;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                finish(); // end the life cycle of the SplashActivity by closing the activity
                startActivity(new Intent(SplashScreenActivity.this, MainActivity.class));
            }
        };

        Timer opening = new Timer();
        opening.schedule(task, 3000);
    }
}