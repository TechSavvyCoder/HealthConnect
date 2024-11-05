package com.example.healthconnect;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class OnboardingStep1Activity extends AppCompatActivity {

    private ImageButton btnNext;
    private TextView txtSkip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding_step1);

        btnNext = (ImageButton) findViewById(R.id.img_onboardingBtnNext);
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(OnboardingStep1Activity.this, OnboardingStep2Activity.class));
                finish();
            }
        });

        txtSkip = (TextView) findViewById(R.id.tvSkip);
        txtSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(OnboardingStep1Activity.this, OnboardingStep3Activity.class));
                finish();
            }
        });

        // Hide the ActionBar
        getSupportActionBar().hide();
    }
}