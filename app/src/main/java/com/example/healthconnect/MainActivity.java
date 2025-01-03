package com.example.healthconnect;

import android.Manifest;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private SessionManager sessionManager;
    String loggedInUserID, loggedInUserName;
    MyDatabaseHelper dbHelper = new MyDatabaseHelper(this);

    private static final String PREFS_NAME = "OnboardingProcedures";
    private static final String KEY_IS_FIRST_RUN = "isFirstRun";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_USER_ROLE = "userRole";

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

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
                if(ContextCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_DENIED){
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.POST_NOTIFICATIONS}, 101);
                }
            }

            Intent serviceIntent = new Intent(getApplicationContext(), Notification.class);
            startService(serviceIntent);

            signOut();

            // Reference to the 'Patients' button
            View buttonPatients = findViewById(R.id.btn_patients);
            buttonPatients.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(MainActivity.this, Doctor_PatientListActivity.class));
                }
            });

            // Reference to the buttons
            View buttonAppointments = findViewById(R.id.button_appointments);
            buttonAppointments.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(MainActivity.this, "Coming soon...", Toast.LENGTH_SHORT).show();
                }
            });

            View buttonCompleted = findViewById(R.id.btn_completed);
            buttonCompleted.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(MainActivity.this, "Coming soon...", Toast.LENGTH_SHORT).show();
                }
            });

            View buttonCancelled = findViewById(R.id.btn_canceled);
            buttonCancelled.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(MainActivity.this, "Coming soon...", Toast.LENGTH_SHORT).show();
                }
            });

            View buttonNoShow = findViewById(R.id.btn_no_show);
            buttonNoShow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(MainActivity.this, "Coming soon...", Toast.LENGTH_SHORT).show();
                }
            });

            TextView txtBadgePatients = findViewById(R.id.badge_patients);
            TextView txtBadgePendingApp = findViewById(R.id.badge_appointments);
            TextView txtBadgeCompletedApp = findViewById(R.id.badge_completed);
            TextView txtBadgeCanceledApp = findViewById(R.id.badge_canceled);
            TextView txtBadgeNoShow = findViewById(R.id.badge_no_show);

            txtBadgePatients.setText(String.valueOf(dbHelper.countAllPatients(loggedInUserID)));
            txtBadgePendingApp.setText(String.valueOf(dbHelper.countAllAppointmentsByStatus(loggedInUserID, "Pending")));
            txtBadgeCompletedApp.setText(String.valueOf(dbHelper.countAllAppointmentsByStatus(loggedInUserID, "Completed")));
            txtBadgeCanceledApp.setText(String.valueOf(dbHelper.countAllAppointmentsByStatus(loggedInUserID, "Canceled")));
            txtBadgeNoShow.setText(String.valueOf(dbHelper.countAllAppointmentsByStatus(loggedInUserID, "No Show")));

        } else {
            // Redirect to LoginActivity if no session is active
            Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(loginIntent);
            finish();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 101) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted
                Log.e("Notification", "__Notification granted");
            } else {
                // Permission denied
                Log.e("Notification", "__Notification denied");
            }
        }
    }

    public void signOut() {
        btnSignOut = (FloatingActionButton) findViewById(R.id.btnSignOut);
        btnSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Clear the current user session
                sessionManager.clearSession();

                // Initialize SharedPreferences to set login status
                SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();

                editor.putBoolean(KEY_IS_LOGGED_IN, false);
                editor.putString(KEY_USER_ROLE, "");
                editor.apply();

                // Redirect to LoginActivity
                Intent intent = new Intent(MainActivity.this, OnboardingStep3Activity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);  // Clear activity stack
                startActivity(intent);
                finish();
            }
        });
    }
}