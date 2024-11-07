package com.example.healthconnect;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

public class Doctor_PatientProfile extends AppCompatActivity {

    String intent_user_id;
    MyDatabaseHelper db;

    String user_name, user_fName, user_lName, user_email, user_DOB;
    TextView tv_userName, tv_userFullName, tv_userEmail, tv_userDOB;

    private TabLayout tabLayout;
    FloatingActionButton btnAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_patient_profile);
        getPatientInfo();

        tv_userFullName = (TextView) findViewById(R.id.patientName);
        tv_userEmail = (TextView) findViewById(R.id.patientEmail);

        tv_userFullName.setText(user_fName + " " + user_lName);
        tv_userEmail.setText(user_email);

        // Initialize TabLayout
        tabLayout = findViewById(R.id.tabLayout);

        // Add tabs
        tabLayout.addTab(tabLayout.newTab().setText("Consultations"));
        tabLayout.addTab(tabLayout.newTab().setText("Medications"));
        tabLayout.addTab(tabLayout.newTab().setText("Appointments"));

        // Set default fragment
        loadFragment(new ConsultationFragment());

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                // Based on the selected tab, load the respective fragment
                switch (tab.getPosition()) {
                    case 0:
                        loadFragment(new ConsultationFragment());
                        break;
                    case 1:
                        loadFragment(new MedicationFragment());
                        break;
                    case 2:
                        loadFragment(new AppointmentFragment());
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        btnAdd = findViewById(R.id.btnAdd);

        // Set up the FAB click listener
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Show the dialog when FAB is clicked
                showOptionsDialog();
            }
        });
    }

    void getPatientInfo(){
        db = new MyDatabaseHelper(this);
        intent_user_id = getIntent().getStringExtra("user_id");

        user_name = db.getUserNameById(intent_user_id);
        user_fName = db.getUserInfo(intent_user_id, "user_firstName");
        user_lName = db.getUserInfo(intent_user_id, "user_lastName");
        user_email = db.getUserInfo(intent_user_id, "user_email");
        user_DOB = db.getUserInfo(intent_user_id, "date_of_birth");
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);  // R.id.fragment_container is the container in your layout
        transaction.addToBackStack(null);  // Optional: Add to back stack for fragment back navigation
        transaction.commit();
    }

    // Method to show the options dialog
    private void showOptionsDialog() {
        // Create the options for the dialog
        String[] options = {"Consultation", "Medication", "Appointment"};

        // Create the dialog with options
        new AlertDialog.Builder(Doctor_PatientProfile.this)
                .setTitle("Select an action")
                .setItems(options, (dialog, which) -> {
                    // Handle the option clicked
                    switch (which) {
                        case 0:
                            // Add new consultation
                            Toast.makeText(Doctor_PatientProfile.this, "Add new consultation clicked", Toast.LENGTH_SHORT).show();
                            break;
                        case 1:
                            // Add new medication
                            Toast.makeText(Doctor_PatientProfile.this, "Add new medication clicked", Toast.LENGTH_SHORT).show();
                            break;
                        case 2:
                            // Add new appointment
                            Toast.makeText(Doctor_PatientProfile.this, "Add new appointment clicked", Toast.LENGTH_SHORT).show();
                            break;
                    }
                })
                .setNegativeButton("Cancel", null) // Optionally add a cancel button
                .create()
                .show();

    }
}