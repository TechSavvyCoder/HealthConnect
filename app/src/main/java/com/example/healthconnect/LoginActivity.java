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

    private EditText txtEmail, txtPass;
    private Button btnLogin;
    private MyDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize UI components
        txtEmail = (EditText) findViewById(R.id.txtEmail);
        txtPass = (EditText) findViewById(R.id.txtPass);
        btnLogin = findViewById(R.id.onboarding3_login);

        dbHelper = new MyDatabaseHelper(this);  // Initialize MyDatabaseHelper
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get the username and password input from the user
                String username = txtEmail.getText().toString();
                String password = txtPass.getText().toString();

                // Check the credentials against the database
                if (dbHelper.checkUserCredentials(username, password, LoginActivity.this)) {
                    // Initialize the session manager
                    SessionManager sessionManager = new SessionManager(LoginActivity.this);
                    String loggedInUserRole = sessionManager.getUserRole();

                    // User exists, proceed to Home Activity
                    if(loggedInUserRole.equals("Doctor")) {
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);  // This will clear the activity stack
                        startActivity(intent);
                        finish();
                    } else if(loggedInUserRole.equals("Patient")) {
                        Intent intent = new Intent(LoginActivity.this, Patient_Profile.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);  // This will clear the activity stack
                        startActivity(intent);
                        finish();
                    } else {
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