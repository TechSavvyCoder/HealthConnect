package com.example.healthconnect;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private SessionManager sessionManager;
    TextView welcomeTextView;

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
            // Retrieve session data
            String userFirstName = sessionManager.getUserFirstName();
            String userLastName = sessionManager.getUserLastName();
            String fullName = userFirstName + " " + userLastName;

            welcomeTextView = findViewById(R.id.test);

            // Display user details (e.g., show in Toast, TextView, etc.)
            Toast.makeText(this, "Welcome, " + fullName, Toast.LENGTH_SHORT).show();
            welcomeTextView.setText("Welcome, " + fullName);
        } else {
            // Redirect to LoginActivity if no session is active
            Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(loginIntent);
            finish();
        }
    }
}