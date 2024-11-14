package com.example.healthconnect;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class LoginActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "OnboardingProcedures";
    private static final String KEY_IS_FIRST_RUN = "isFirstRun";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_USER_ROLE = "userRole";

    private EditText txtEmail, txtPass;
    private Button btnLogin;
    private MyDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        txtEmail = (EditText) findViewById(R.id.txtEmail);
        txtPass = (EditText) findViewById(R.id.txtPass);
        btnLogin = findViewById(R.id.onboarding3_login);

        dbHelper = new MyDatabaseHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = txtEmail.getText().toString();
                String password = txtPass.getText().toString();

                // Check the credentials against the database
                if (dbHelper.checkUserCredentials(username, password, LoginActivity.this)) {
                    // Initialize SharedPreferences to set login status
                    SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();

                    // Initialize the session manager
                    SessionManager sessionManager = new SessionManager(LoginActivity.this);
                    String loggedInUserRole = sessionManager.getUserRole();

                    // User exists, proceed to Home Activity
                    if(loggedInUserRole.equals("Doctor")) {
                        editor.putBoolean(KEY_IS_LOGGED_IN, true);
                        editor.putString(KEY_USER_ROLE, "Doctor");
                        editor.apply();

                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);  // This will clear the activity stack
                        startActivity(intent);
                        finish();
                    } else if(loggedInUserRole.equals("Patient")) {
                        editor.putBoolean(KEY_IS_LOGGED_IN, true);
                        editor.putString(KEY_USER_ROLE, "Patient");
                        editor.apply();

                        Intent intent = new Intent(LoginActivity.this, Patient_Profile.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);  // This will clear the activity stack
                        startActivity(intent);
                        finish();
                    } else {
                        editor.putBoolean(KEY_IS_LOGGED_IN, false);
                        editor.putString(KEY_USER_ROLE, "");
                        editor.apply();

                        Intent intent = new Intent(LoginActivity.this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);  // This will clear the activity stack
                        startActivity(intent);
                        finish();
                    }
                } else {
                    // User doesn't exist, show a toast
                    Toast.makeText(LoginActivity.this, "User not found or incorrect password!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}