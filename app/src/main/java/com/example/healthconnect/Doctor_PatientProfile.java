package com.example.healthconnect;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Doctor_PatientProfile extends AppCompatActivity {

    // Session
    String loggedInUserRole, loggedInUserID;

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

        // Initialize the session manager
        SessionManager sessionManager = new SessionManager(Doctor_PatientProfile.this);
        loggedInUserRole = sessionManager.getUserRole();
        loggedInUserID = sessionManager.getUserId();

        // User exists, proceed to Home Activity
        if(loggedInUserRole.equals("Doctor")){
            getPatientInfo();

            tv_userFullName = findViewById(R.id.patientName);
            tv_userEmail = findViewById(R.id.patientEmail);

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
                public void onTabUnselected(TabLayout.Tab tab) {}

                @Override
                public void onTabReselected(TabLayout.Tab tab) {}
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
        } else {
            Intent intent = new Intent(Doctor_PatientProfile.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);  // This will clear the activity stack
            startActivity(intent);
            finish();
        }
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
        // Prepare the bundle to send data to the fragment
        Bundle bundle = new Bundle();
        bundle.putString("patient_id", intent_user_id);

        fragment.setArguments(bundle);  // Attach the bundle to the fragment

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);  // Optional: Add to back stack for fragment back navigation
        transaction.commit();
    }

    // Method to show the options dialog
    private void showOptionsDialog() {
        String[] options = {"Consultation", "Medication", "Appointment"};

        new AlertDialog.Builder(Doctor_PatientProfile.this)
                .setTitle("Add new:")
                .setItems(options, (dialog, which) -> {
                    switch (which) {
                        case 0:
                            Toast.makeText(Doctor_PatientProfile.this, "Add new consultation clicked", Toast.LENGTH_SHORT).show();
                            break;
                        case 1:
                            Toast.makeText(Doctor_PatientProfile.this, "Add new medication clicked", Toast.LENGTH_SHORT).show();
                            break;
                        case 2:
                            showAddAppointmentDialog();
                            break;
                    }
                })
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    private void showAddAppointmentDialog() {
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.doctor_add_patient_appointment, null);

        EditText editTextDate = dialogView.findViewById(R.id.editTextAppointmentDate);
        EditText editTextTime = dialogView.findViewById(R.id.editTextAppointmentTime);

        editTextDate.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(Doctor_PatientProfile.this,
                    (view, selectedYear, selectedMonth, selectedDay) -> {
                        String formattedDate = selectedYear + "/" + (selectedMonth + 1) + "/" + selectedDay;
                        editTextDate.setText(formattedDate);
                    }, year, month, dayOfMonth);
            datePickerDialog.show();
        });

        editTextTime.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);

            TimePickerDialog timePickerDialog = new TimePickerDialog(Doctor_PatientProfile.this,
                    (view, selectedHour, selectedMinute) -> {
                        String formattedTime = String.format("%02d:%02d", selectedHour, selectedMinute);
                        editTextTime.setText(formattedTime);
                    }, hour, minute, true);
            timePickerDialog.show();
        });

        new AlertDialog.Builder(Doctor_PatientProfile.this)
                .setTitle("Add New Appointment")
                .setView(dialogView)
                .setPositiveButton("Submit", (dialog, which) -> {
                    EditText appointmentDate = dialogView.findViewById(R.id.editTextAppointmentDate);
                    EditText appointmentTime = dialogView.findViewById(R.id.editTextAppointmentTime);
                    EditText appointmentDesc = dialogView.findViewById(R.id.editTextAppointmentDescription);

                    String appointmentDateText = appointmentDate.getText().toString();
                    String appointmentTimeText = appointmentTime.getText().toString();
                    String appointmentDescText = appointmentDesc.getText().toString();

                    Date currentDate = new Date();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                    String formattedDate = sdf.format(currentDate);

                    Toast.makeText(Doctor_PatientProfile.this,
                            "Appointment added for " + intent_user_id + " on " + appointmentDateText + " at " + appointmentTimeText,
                            Toast.LENGTH_SHORT).show();

                    db.addAppointment(intent_user_id, loggedInUserID, appointmentDateText + " " + appointmentTimeText, appointmentDescText, "Pending", formattedDate);

                    AppointmentFragment appointmentFragment = new AppointmentFragment();
                    loadFragment(appointmentFragment);
                })
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }
}
