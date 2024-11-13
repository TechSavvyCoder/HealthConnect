package com.example.healthconnect;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private SessionManager sessionManager;
    String loggedInUserID, loggedInUserName;
    FloatingActionButton btnSignOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Hide the ActionBar
        getSupportActionBar().hide();

        // Initialize the session manager
        sessionManager = new SessionManager(this);

        // Check if there is an active session
        if (sessionManager.isSessionActive()) {
            loggedInUserID = sessionManager.getUserId();
            loggedInUserName = sessionManager.getUserFirstName();

            TextView tvLoggedUser= findViewById(R.id.tvLoggedUser);
            tvLoggedUser.setText("Hi, " + loggedInUserName + "!");

            signOut();

            // Reference to the 'Patients' button
            View buttonPatients = findViewById(R.id.btn_patients);
            buttonPatients.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(MainActivity.this, Doctor_PatientListActivity.class));
                }
            });

            // Reference to the 'Appointments' button
            View buttonAppointments = findViewById(R.id.button_appointments);
            buttonAppointments.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(MainActivity.this, "Appointments button clicked!", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            // Redirect to LoginActivity if no session is active
            Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(loginIntent);
            finish();
        }
    }


    public void signOut() {
        btnSignOut = (FloatingActionButton) findViewById(R.id.btnSignOut);
        btnSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Clear the current user session
                sessionManager.clearSession();

                // Redirect to LoginActivity
                Intent intent = new Intent(MainActivity.this, OnboardingStep3Activity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);  // Clear activity stack
                startActivity(intent);
                finish();
            }
        });
    }
}