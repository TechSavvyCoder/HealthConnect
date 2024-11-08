package com.example.healthconnect;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class Doctor_PatientListActivity extends AppCompatActivity {

    private SessionManager sessionManager;
    String loggedInUserID, loggedInUserName;

    RecyclerView rvPatientList;
    FloatingActionButton btnAdd, btnSignOut;

    MyDatabaseHelper myDB;
    ArrayList<String> user_id, user_name, user_email, user_firstName, user_lastName, user_DOB;
    PatientAdapter patientAdapter;

    private SearchView searchPatient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_patient_list);

        // Hide the ActionBar
        getSupportActionBar().hide();

        // Initialize the session manager
        sessionManager = new SessionManager(this);

        // Check if there is an active session
        if (sessionManager.isSessionActive()) {
            loggedInUserID = sessionManager.getUserId();
            loggedInUserName = sessionManager.getUserFirstName() + " " + sessionManager.getUserLastName();

            TextView tvLoggedUser= findViewById(R.id.tvLoggedUser);
            tvLoggedUser.setText("Hi, " + loggedInUserName + "!");

            searchPatient = findViewById(R.id.searchView);
            searchPatient.clearFocus();
            searchPatient.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    filterList(newText);
                    return false;
                }
            });

            rvPatientList = (RecyclerView) findViewById(R.id.rvPatientList);
            signOut();

            btnAdd = (FloatingActionButton) findViewById(R.id.btnAdd);
            btnAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Doctor_PatientListActivity.this, Doctor_AddPatientActivity.class);
                    startActivity(intent);
                }
            });

            myDB = new MyDatabaseHelper(Doctor_PatientListActivity.this);
            user_id = new ArrayList<>();
            user_name = new ArrayList<>();
            user_email = new ArrayList<>();
            user_firstName = new ArrayList<>();
            user_lastName = new ArrayList<>();
            user_DOB = new ArrayList<>();

            storeDataInArray(loggedInUserID);

            patientAdapter = new PatientAdapter(Doctor_PatientListActivity.this, user_id, user_firstName, user_lastName, user_email);
            rvPatientList.setAdapter(patientAdapter);
            rvPatientList.setLayoutManager(new LinearLayoutManager(Doctor_PatientListActivity.this));
        } else {
            // Redirect to LoginActivity if no session is active
            Intent loginIntent = new Intent(Doctor_PatientListActivity.this, LoginActivity.class);
            startActivity(loginIntent);
            finish();
        }
    }

    private void filterList(String newText) {
        // Clear the existing list before adding filtered data
        user_id.clear();
        user_name.clear();
        user_email.clear();
        user_firstName.clear();
        user_lastName.clear();
        user_DOB.clear();

        // Query the database for matching patients
        Cursor cursor = myDB.searchPatient(newText);
        if(cursor.getCount() == 0){
            // No matching patient found
        } else {
            while(cursor.moveToNext()){
                user_id.add(cursor.getString(0));
                user_name.add(cursor.getString(1));
                user_email.add(cursor.getString(4));
                user_firstName.add(cursor.getString(5));
                user_lastName.add(cursor.getString(6));
                user_DOB.add(cursor.getString(7));
            }
        }

        // Notify the adapter that the data has changed
        patientAdapter.notifyDataSetChanged();
    }

    public void storeDataInArray(String loggedInUserID) {
        Cursor cursor = myDB.getAllPatient(loggedInUserID);
        if(cursor.getCount() == 0){
            // empty list
        } else {
            while(cursor.moveToNext()){
                user_id.add(cursor.getString(0));
                user_name.add(cursor.getString(1));
                user_email.add(cursor.getString(4));
                user_firstName.add(cursor.getString(5));
                user_lastName.add(cursor.getString(6));
                user_DOB.add(cursor.getString(7));
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

                // Redirect to LoginActivity
                Intent intent = new Intent(Doctor_PatientListActivity.this, OnboardingStep3Activity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);  // Clear activity stack
                startActivity(intent);
                finish();
            }
        });
    }
}