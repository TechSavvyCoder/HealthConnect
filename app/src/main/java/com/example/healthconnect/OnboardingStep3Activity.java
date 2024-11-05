package com.example.healthconnect;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class OnboardingStep3Activity extends AppCompatActivity {

    private Button btnLogin;
    private ImageButton btnSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding_step3);

        btnLogin = (Button) findViewById(R.id.onboarding3_login);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(OnboardingStep3Activity.this, LoginActivity.class));
            }
        });

        btnSignUp = (ImageButton) findViewById(R.id.onboarding3_signup);
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(OnboardingStep3Activity.this, SignUpActivity.class));
            }
        });

        // Hide the ActionBar
        getSupportActionBar().hide();
    }
}