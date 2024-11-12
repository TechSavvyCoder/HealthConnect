package com.example.healthconnect;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class Doctor_PatientProfile extends AppCompatActivity {

    // Session
    String loggedInUserRole, loggedInUserID;

    String intent_user_id;
    MyDatabaseHelper db;

    String user_name, user_fName, user_lName, user_email, user_DOB;
    TextView tv_userName, tv_userFullName, tv_userEmail, tv_userDOB, tv_userDesc;

    private TabLayout tabLayout;
    FloatingActionButton btnAdd;

    String selectedDate;
    String selectedAppointmentID;

    String selectedConsultationID;
    String consultationID;

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
            tv_userDesc = findViewById(R.id.patientDescription);

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
            btnAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Show the dialog when FAB is clicked
                    showOptionsDialog();
                }
            });

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
        editTextDateOfBirth.setText(user_DOB); // Assuming user_DOB contains the date of birth
        editTextDescription.setText(""); // You might want to populate this if you have a description for the patient

        // Handle the DatePicker for Date of Birth field
        editTextDateOfBirth.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(Doctor_PatientProfile.this,
                    (view, selectedYear, selectedMonth, selectedDay) -> {
                        // Format the month and day with leading zeros if needed
                        String formattedDate = String.format("%04d/%02d/%02d", selectedYear, selectedMonth + 1, selectedDay);
                        editTextDateOfBirth.setText(formattedDate); // Set selected date as DOB
                    }, year, month, dayOfMonth);
            datePickerDialog.show();
        });

        // Create the AlertDialog and set up the Save button to handle user input
        new AlertDialog.Builder(Doctor_PatientProfile.this)
                .setTitle("Edit Patient Information")
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
                        Toast.makeText(Doctor_PatientProfile.this, "All fields must be filled!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Update the database with the new information
                    Boolean isUpdated = db.updatePatientInfo(intent_user_id, updatedUsername, updatedEmail, updatedFirstName, updatedLastName, updatedDateOfBirth, updatedDescription, formattedDate);

                    if (isUpdated) {
                        tv_userFullName.setText(updatedFirstName + " " + updatedLastName);
                        tv_userEmail.setText(updatedEmail);
                        tv_userDesc.setText(updatedDescription);

                        Toast.makeText(Doctor_PatientProfile.this, updatedFirstName + " " + updatedLastName + "'s information has been updated successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(Doctor_PatientProfile.this, "Failed to update patient information. Please try again.", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }


    private void showOptionsDialog() {
        String[] options = {"Consultation", "Medication", "Appointment"};

        new AlertDialog.Builder(Doctor_PatientProfile.this)
                .setTitle("Add new:")
                .setItems(options, (dialog, which) -> {
                    switch (which) {
                        case 0:
                            showAddConsultationDialog();
                            break;
                        case 1:
                            showAddMedicationDialog();
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

    private void showAddConsultationDialog() {
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.doctor_add_patient_consultation, null);

        // Get the spinner for appointment selection
        Spinner spinnerAppointment = dialogView.findViewById(R.id.spinnerAppointment);

        // Retrieve the appointments from the database
        db = new MyDatabaseHelper(Doctor_PatientProfile.this);
        ArrayList<HashMap<String, String>> appointments = db.getAppointmentsForPatient(loggedInUserID, intent_user_id);

        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault());
        SimpleDateFormat outputFormat = new SimpleDateFormat("MMM dd, yyyy 'at' h:mm a", Locale.getDefault());

        ArrayList<String> new_appointments = new ArrayList<>();
        final HashMap<String, String> appointmentMap = new HashMap<>();

        for (HashMap<String, String> appointment : appointments) {
            String dateTime = appointment.get("dateTime");
            String id = appointment.get("id");
            try {
                // Parse the appointment string to a Date object
                Date date = inputFormat.parse(dateTime);
                // Format the Date object to the desired format
                if (date != null) {
                    String formattedDate = outputFormat.format(date);
                    new_appointments.add(formattedDate);
                    appointmentMap.put(formattedDate, id); // Associate formatted date with ID
                } else {
                    new_appointments.add("No appointment");
                    appointmentMap.put("No appointment", id);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // Create an ArrayAdapter to populate the spinner with appointments
        ArrayAdapter<String> adapter = new ArrayAdapter<>(Doctor_PatientProfile.this,
                android.R.layout.simple_spinner_dropdown_item, new_appointments);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerAppointment.setAdapter(adapter);

        // Get the selected ID when an item is selected
        spinnerAppointment.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedDate = (String) parent.getItemAtPosition(position);
                selectedAppointmentID = appointmentMap.get(selectedDate);
                // Use the selectedAppointmentID as needed
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Handle case when no item is selected
            }
        });

        // Get the spinner for appointment selection
        Spinner spinnerType = dialogView.findViewById(R.id.spinnerType);

        // Create a predefined list of consultation types
        ArrayList<String> consultations = new ArrayList<>();
        consultations.add("Initial Consultation");
        consultations.add("Follow-up Consultation");
        consultations.add("Emergency Consultation");
        consultations.add("Specialist Consultation");
        consultations.add("Telemedicine Consultation");
        consultations.add("Second Opinion Consultation");
        consultations.add("Pre-Surgery Consultation");
        consultations.add("Post-Surgery Consultation");
        consultations.add("Nutrition Consultation");
        consultations.add("Vaccination Consultation");

        // Create an ArrayAdapter to populate the spinner with the consultation types
        ArrayAdapter<String> con_adapter = new ArrayAdapter<>(Doctor_PatientProfile.this,
                android.R.layout.simple_spinner_item, consultations);
        con_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Set the adapter to the spinner
        spinnerType.setAdapter(con_adapter);

        new AlertDialog.Builder(Doctor_PatientProfile.this)
                .setTitle("Add New Consultation")
                .setView(dialogView)
                .setPositiveButton("Submit", (dialog, which) -> {
                    EditText con_Diagnosis = dialogView.findViewById(R.id.txtDiagnosis);
                    EditText con_Treatment = dialogView.findViewById(R.id.txtTreatment);
                    EditText con_Desc = dialogView.findViewById(R.id.txtDesc);

                    String selectedAppointment = selectedAppointmentID;
                    String consultationType = spinnerType.getSelectedItem().toString();
                    String consultationDiagnosis = con_Diagnosis.getText().toString();
                    String consultationTreatment = con_Treatment.getText().toString();
                    String consultationDescription = con_Desc.getText().toString();

                    if(!selectedAppointment.isEmpty() && !consultationType.isEmpty() && !consultationDiagnosis.trim().isEmpty() && !consultationTreatment.trim().isEmpty()  && !consultationDescription.trim().isEmpty()) {

                        Date currentDate = new Date();
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                        String formattedDate = sdf.format(currentDate);

                        String result = db.addConsulation(selectedAppointment, consultationType, consultationDiagnosis, consultationTreatment, consultationDescription, formattedDate);

                        if ("success".equals(result)) {
                            // Consultation added successfully
                            Toast.makeText(Doctor_PatientProfile.this, "Entry added successfully!", Toast.LENGTH_SHORT).show();

                            ConsultationFragment consultationFragment = new ConsultationFragment();
                            loadFragment(consultationFragment);
                        } else {
                            // Consultation addition failed
                            Toast.makeText(Doctor_PatientProfile.this, "Failed to add entry. Please try again.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "Fields cannot be empty", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    private void showAddMedicationDialog() {
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.doctor_add_patient_medication, null);

        db = new MyDatabaseHelper(Doctor_PatientProfile.this);
        ArrayList<HashMap<String, String>> consultations = db.getConsultationsForPatient(loggedInUserID, intent_user_id);

        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault());
        SimpleDateFormat outputFormat = new SimpleDateFormat("MMM dd, yyyy 'at' h:mm a", Locale.getDefault());

        ArrayList<String> new_consultations = new ArrayList<>();
        final HashMap<String, String> consultationMap = new HashMap<>();

        for (HashMap<String, String> consultation : consultations) {
            String consultationDesc = consultation.get("consultationDesc");
            String appointmentDateTime = consultation.get("appointmentDateTime");
            String id = consultation.get("consultation_id");
            try {
                // Parse the appointment string to a Date object
                Date date = inputFormat.parse(appointmentDateTime);
                // Format the Date object to the desired format
                if (date != null) {
                    String formattedDate = outputFormat.format(date);
                    new_consultations.add(consultationDesc + " - " +formattedDate);
                    consultationMap.put(formattedDate, id); // Associate formatted date with ID
                } else {
                    new_consultations.add("No consultation");
                    consultationMap.put("No consultation", id);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // Set up the Spinner with the labels
        Spinner spinnerConsultation = dialogView.findViewById(R.id.spinnerConsultation);
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new_consultations);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerConsultation.setAdapter(spinnerAdapter);

        // Handle item selection
        spinnerConsultation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                selectedConsultationID = new_consultations.get(position);
                consultationID = consultations.get(position).get("consultation_id");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Handle no selection
            }
        });


        new AlertDialog.Builder(Doctor_PatientProfile.this)
                .setTitle("Add New Medication")
                .setView(dialogView)
                .setPositiveButton("Submit", (dialog, which) -> {
                    EditText med_Desc = dialogView.findViewById(R.id.txtDesc);
                    EditText med_Dosage = dialogView.findViewById(R.id.txtDosage);
                    EditText med_Frequency = dialogView.findViewById(R.id.txtFrequency);
                    EditText med_Duration = dialogView.findViewById(R.id.txtDuration);

                    String selectedConsultation = consultationID;
                    String medicationDescription = med_Desc.getText().toString();
                    String medicationDosage = med_Dosage.getText().toString();
                    String medicationFrequency = med_Frequency.getText().toString();
                    String medicationDuration = med_Duration.getText().toString();

                    if(!medicationDescription.trim().isEmpty() && !medicationDosage.trim().isEmpty()  && !medicationFrequency.trim().isEmpty() && !medicationDuration.trim().isEmpty()) {

                        Date currentDate = new Date();
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                        String formattedDate = sdf.format(currentDate);

                        String result = db.addMedication(selectedConsultation, medicationDescription, medicationDosage, medicationFrequency, medicationDuration, formattedDate);

                        if ("success".equals(result)) {
                            // Consultation added successfully
                            Toast.makeText(Doctor_PatientProfile.this, "Entry added successfully!", Toast.LENGTH_SHORT).show();

                            MedicationFragment medicationFragment = new MedicationFragment();
                            loadFragment(medicationFragment);
                        } else {
                            // Consultation addition failed
                            Toast.makeText(Doctor_PatientProfile.this, "Failed to add entry. Please try again.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "Fields cannot be empty", Toast.LENGTH_SHORT).show();
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
                        // Format the month and day with leading zeros if needed
                        String formattedDate = String.format("%04d/%02d/%02d", selectedYear, selectedMonth + 1, selectedDay);
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

                    if(!appointmentDateText.trim().isEmpty() && !appointmentTimeText.trim().isEmpty()  && !appointmentDescText.trim().isEmpty()){
                        Date currentDate = new Date();
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                        String formattedDate = sdf.format(currentDate);

                        String result = db.addAppointment(intent_user_id, loggedInUserID, appointmentDateText + " " + appointmentTimeText, appointmentDescText, "Pending", formattedDate);

                        if ("success".equals(result)) {
                            // Consultation added successfully
                            Toast.makeText(Doctor_PatientProfile.this, "Entry added successfully!", Toast.LENGTH_SHORT).show();

                            AppointmentFragment appointmentFragment = new AppointmentFragment();
                            loadFragment(appointmentFragment);
                        } else {
                            // Consultation addition failed
                            Toast.makeText(Doctor_PatientProfile.this, "Failed to add entry. Please try again.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "Fields cannot be empty", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }
}
