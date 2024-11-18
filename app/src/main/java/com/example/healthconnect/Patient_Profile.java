package com.example.healthconnect;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Patient_Profile extends AppCompatActivity {

    // Session
    private SessionManager sessionManager;
    String loggedInUserRole, loggedInUserID;

    private static final String PREFS_NAME = "OnboardingProcedures";
    private static final String KEY_IS_FIRST_RUN = "isFirstRun";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_USER_ROLE = "userRole";

    MyDatabaseHelper db;
    String user_name, user_fName, user_lName, user_email, user_DOB, user_desc, user_docID;
    FloatingActionButton btnSignOut;

    TextView tv_userFullName, tv_userEmail, tv_userDesc;
    private TabLayout tabLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_profile);

        // Hide the ActionBar
        getSupportActionBar().hide();

        // Initialize the session manager
        sessionManager = new SessionManager(Patient_Profile.this);
        loggedInUserRole = sessionManager.getUserRole();
        loggedInUserID = sessionManager.getUserId();

        // Check if there is an active session
        if (sessionManager.isSessionActive()) {
            // User exists, proceed to Home Activity
            if (loggedInUserRole.equals("Patient")) {
                getPatientInfo();

                tv_userFullName = findViewById(R.id.patientName);
                tv_userEmail = findViewById(R.id.patientEmail);
                tv_userDesc = findViewById(R.id.patientDescription);

                tv_userFullName.setText(user_fName + " " + user_lName);
                tv_userEmail.setText(user_email);
                tv_userDesc.setText(user_desc);

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

                signOut();

                LinearLayout editLayout = findViewById(R.id.editProfileLayout);
                TextView txtEditBtn = findViewById(R.id.txtEditBtn);
                ImageView editIcon = findViewById(R.id.editIcon);

                View.OnClickListener editClickListener = v -> {
                    showEditPatientInfoDialog();
                };

                txtEditBtn.setOnClickListener(editClickListener);
                editIcon.setOnClickListener(editClickListener);
                editLayout.setOnClickListener(editClickListener);

            } else {
                Intent intent = new Intent(Patient_Profile.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);  // This will clear the activity stack
                startActivity(intent);
                finish();
            }
        } else {
            Intent intent = new Intent(Patient_Profile.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);  // This will clear the activity stack
            startActivity(intent);
            finish();
        }
    }

    void getPatientInfo(){
        db = new MyDatabaseHelper(this);
        user_name = db.getUserNameById(loggedInUserID);
        user_fName = db.getUserInfo(loggedInUserID, "user_firstName");
        user_lName = db.getUserInfo(loggedInUserID, "user_lastName");
        user_email = db.getUserInfo(loggedInUserID, "user_email");
        user_DOB = db.getUserInfo(loggedInUserID, "date_of_birth");
        user_desc = db.getUserInfo(loggedInUserID, "user_desc");
        user_docID = db.getUserInfo(loggedInUserID, "doctor_id");
    }

    private void loadFragment(Fragment fragment) {
        // Prepare the bundle to send data to the fragment
        Bundle bundle = new Bundle();
        bundle.putString("doctor_id", user_docID);

        fragment.setArguments(bundle);  // Attach the bundle to the fragment

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
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
                Intent intent = new Intent(Patient_Profile.this, OnboardingStep3Activity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);  // Clear activity stack
                startActivity(intent);
                finish();
            }
        });
    }

    // Method to show the options dialog
    private void showEditPatientInfoDialog() {
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.doctor_edit_patient_profile, null);

        // Initialize the TextInputEditText views
        TextInputEditText editTextUsername = dialogView.findViewById(R.id.editTextUsername);
        TextInputEditText editTextEmail = dialogView.findViewById(R.id.editTextEmail);
        TextInputEditText editTextFirstName = dialogView.findViewById(R.id.editTextFirstName);
        TextInputEditText editTextLastName = dialogView.findViewById(R.id.editTextLastName);
        TextInputEditText editTextDateOfBirth = dialogView.findViewById(R.id.editTextDateOfBirth);
        TextInputEditText editTextDescription = dialogView.findViewById(R.id.editTextDescription);

        // Set the current patient data into the fields
        editTextUsername.setText(user_name); // Assuming user_name contains the username
        editTextEmail.setText(user_email);
        editTextFirstName.setText(user_fName);
        editTextLastName.setText(user_lName);
        editTextDateOfBirth.setText(user_DOB);
        editTextDescription.setText(user_desc);

        // Handle the DatePicker for Date of Birth field
        editTextDateOfBirth.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(Patient_Profile.this,
                    (view, selectedYear, selectedMonth, selectedDay) -> {
                        // Format the month and day with leading zeros if needed
                        String formattedDate = String.format("%04d/%02d/%02d", selectedYear, selectedMonth + 1, selectedDay);
                        editTextDateOfBirth.setText(formattedDate); // Set selected date as DOB
                    }, year, month, dayOfMonth);
            datePickerDialog.show();
        });

        // Create the AlertDialog and set up the Save button to handle user input
        new AlertDialog.Builder(Patient_Profile.this)
                .setTitle("Edit Profile Information")
                .setView(dialogView)
                .setPositiveButton("Save", (dialog, which) -> {
                    Date currentDate = new Date();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                    String formattedDate = sdf.format(currentDate);

                    // Retrieve the updated information
                    String updatedUsername = editTextUsername.getText().toString();
                    String updatedEmail = editTextEmail.getText().toString();
                    String updatedFirstName = editTextFirstName.getText().toString();
                    String updatedLastName = editTextLastName.getText().toString();
                    String updatedDateOfBirth = editTextDateOfBirth.getText().toString();
                    String updatedDescription = editTextDescription.getText().toString();

                    // Validate the input fields
                    if (updatedUsername.isEmpty() || updatedEmail.isEmpty() || updatedFirstName.isEmpty() ||
                            updatedLastName.isEmpty() || updatedDateOfBirth.isEmpty()) {
                        Toast.makeText(Patient_Profile.this, "All fields must be filled!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Update the database with the new information
                    Boolean isUpdated = db.updatePatientInfo(loggedInUserID, updatedUsername, updatedEmail, updatedFirstName, updatedLastName, updatedDateOfBirth, updatedDescription, formattedDate);

                    if (isUpdated) {
                        tv_userFullName.setText(updatedFirstName + " " + updatedLastName);
                        tv_userEmail.setText(updatedEmail);
                        tv_userDesc.setText(updatedDescription);

                        Toast.makeText(Patient_Profile.this, "Your information has been updated successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(Patient_Profile.this, "Failed to update your information. Please try again.", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }
}