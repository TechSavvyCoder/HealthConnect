package com.example.healthconnect;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ConsultationAdapter extends RecyclerView.Adapter<ConsultationAdapter.ConsultationViewHolder>{

    Context context;
    ArrayList<String> con_id, con_datetime, con_type, con_diagnosis, con_treatment, con_desc;
    MyDatabaseHelper myDB;
    String loggedInUserID;
    String intent_user_id;

    public ConsultationAdapter(Context context, ArrayList<String> con_id, ArrayList<String> con_datetime, ArrayList<String> con_type,
                               ArrayList<String> con_diagnosis, ArrayList<String> con_treatment, ArrayList<String> con_desc,
                               String loggedInUserID, String intent_user_id) {
        this.context = context;
        this.con_id = con_id;
        this.con_datetime = con_datetime;
        this.con_type = con_type;
        this.con_diagnosis = con_diagnosis;
        this.con_treatment = con_treatment;
        this.con_desc = con_desc;
        this.myDB = new MyDatabaseHelper(context);
        this.loggedInUserID = loggedInUserID;
        this.intent_user_id = intent_user_id;
    }

    @NonNull
    @Override
    public ConsultationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_consultation, parent, false);
        return new ConsultationAdapter.ConsultationViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(@NonNull ConsultationAdapter.ConsultationViewHolder holder, int position) {
        MyDatabaseHelper db = new MyDatabaseHelper(context);
        String app_dateTime = db.getAppointmentInfoByID(con_datetime.get(position), "appointment_dateTime");

        String outputDateString;

        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault());
            Date date = inputFormat.parse(app_dateTime);

            SimpleDateFormat outputFormat = new SimpleDateFormat("MMM dd, yyyy 'at' h:mm a", Locale.getDefault());
            outputDateString = outputFormat.format(date);

            holder.tvConDateTime.setText("Appointment Date: " + outputDateString);
            holder.tvConType.setText("Consultation Type: " + con_type.get(position));
            holder.tvConDiagnosis.setText("Diagnosis: " + con_diagnosis.get(position));
            holder.tvConTreatment.setText("Treatment: " + con_treatment.get(position));
            holder.tvConDesc.setText(con_desc.get(position));

            // Set OnClickListener for the Delete button
            holder.btnDelete.setOnClickListener(v -> showDeleteConfirmationDialog(position, outputDateString, con_desc.get(position)));
            holder.btnEdit.setOnClickListener(v -> showEditConsultationDialog(position));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return con_id.size();
    }

    public static class ConsultationViewHolder extends RecyclerView.ViewHolder {
        Context context;
        TextView tvConDateTime, tvConType, tvConDiagnosis, tvConTreatment, tvConDesc;
        Button btnEdit, btnDelete;

        public ConsultationViewHolder(View itemView, Context context) {
            super(itemView);
            this.context = context;
            tvConDateTime = itemView.findViewById(R.id.tvConDateTime);
            tvConType = itemView.findViewById(R.id.tvConType);
            tvConDiagnosis = itemView.findViewById(R.id.tvConDiagnosis);
            tvConTreatment = itemView.findViewById(R.id.tvConTreatment);
            tvConDesc = itemView.findViewById(R.id.tvConDesc);

            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);

            SessionManager sessionManager = new SessionManager(context);
            String loggedInUserRole = sessionManager.getUserRole();

            if (loggedInUserRole.equals("Patient")) {
                btnEdit.setVisibility(View.GONE);
                btnDelete.setVisibility(View.GONE);
            }
        }
    }

    // Show confirmation dialog before deletion
    private void showDeleteConfirmationDialog(int position, String con_date, String con_desc) {
        new android.app.AlertDialog.Builder(context)
                .setTitle("Delete entry?")
                .setMessage("This will delete your consultation on " + con_date + "\n\nDescription: " + con_desc)
                .setCancelable(false)
                .setPositiveButton("Yes, delete", (dialog, id) -> {
                    deleteAppointment(position);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    // Method to delete the appointment from the database
    private void deleteAppointment(int position) {
        // Get the appointment ID from the data
        String consultationId = con_id.get(position);

        // Delete the appointment from the database
        boolean isDeleted = myDB.deleteEntry("consultation", "consultation_id", consultationId);

        if (isDeleted) {
            // Remove the item from the list and notify the adapter
            con_id.remove(position);
            con_datetime.remove(position);
            con_type.remove(position);
            con_diagnosis.remove(position);
            con_treatment.remove(position);
            con_desc.remove(position);
            notifyItemRemoved(position);

            Toast.makeText(context, "Entry has been deleted successfully", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Failed to delete this entry", Toast.LENGTH_SHORT).show();
        }
    }

    // Show the Edit Appointment Dialog
    private void showEditConsultationDialog(int position) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.doctor_add_patient_consultation, null);

        // Find the views from the dialog
        EditText editTextDiagnosis = dialogView.findViewById(R.id.txtDiagnosis);
        EditText editTxtTreatment = dialogView.findViewById(R.id.txtTreatment);
        EditText editTextDesc = dialogView.findViewById(R.id.txtDesc);

        // Spinners for Appointment and Consultation Type
        Spinner spinnerAppointment = dialogView.findViewById(R.id.spinnerAppointment);
        Spinner spinnerType = dialogView.findViewById(R.id.spinnerType);

        // Set the existing data into the EditText fields
        editTextDiagnosis.setText(con_diagnosis.get(position)); // Set Date
        editTxtTreatment.setText(con_treatment.get(position)); // Set Time
        editTextDesc.setText(con_desc.get(position)); // Set Description

        // Retrieve and set the appointments into the spinner
        ArrayList<HashMap<String, String>> appointments = myDB.getAppointmentsForPatient(loggedInUserID, intent_user_id);

        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault());
        SimpleDateFormat outputFormat = new SimpleDateFormat("MMM dd, yyyy 'at' h:mm a", Locale.getDefault());

        ArrayList<String> new_appointments = new ArrayList<>();
        final HashMap<String, String> appointmentMap = new HashMap<>();

        for (HashMap<String, String> appointment : appointments) {
            String dateTime = appointment.get("dateTime");
            String id = appointment.get("id");
            try {
                Date date = inputFormat.parse(dateTime);
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

        // Set adapter for the Appointment spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context,
                android.R.layout.simple_spinner_dropdown_item, new_appointments);
        spinnerAppointment.setAdapter(adapter);

        // Set the pre-selected appointment
        String currentAppointment = con_datetime.get(position);

        String currentAppointmentFormatted = null;
        for (Map.Entry<String, String> entry : appointmentMap.entrySet()) {
            if (entry.getValue().equals(currentAppointment)) {
                currentAppointmentFormatted = entry.getKey();
                break;
            }
        }

        // If a matching appointment is found, set it as selected in the spinner
        if (currentAppointmentFormatted != null) {
            int appointmentPosition = new_appointments.indexOf(currentAppointmentFormatted);
            if (appointmentPosition >= 0) {
                spinnerAppointment.setSelection(appointmentPosition);
            }
        }

        // Set the predefined consultation types
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

        // Set adapter for the Consultation Type spinner
        ArrayAdapter<String> con_adapter = new ArrayAdapter<>(context,
                android.R.layout.simple_spinner_item, consultations);
        spinnerType.setAdapter(con_adapter);

        // Set the pre-selected consultation type
        String currentType = con_type.get(position); // Get current type for this consultation
        int typePosition = consultations.indexOf(currentType);
        if (typePosition >= 0) {
            spinnerType.setSelection(typePosition);
        }

        // Create and show the Edit Appointment dialog
        new AlertDialog.Builder(context)
                .setTitle("Edit Appointment")
                .setView(dialogView)
                .setPositiveButton("Save", (dialog, which) -> {
                    // Get the selected spinner values
                    String selectedAppointment = (String) spinnerAppointment.getSelectedItem();
                    String selectedAppointmentID = appointmentMap.get(selectedAppointment);

                    String selectedConsultationType = (String) spinnerType.getSelectedItem();

                    String new_diagnosis = editTextDiagnosis.getText().toString();
                    String new_treatment = editTxtTreatment.getText().toString();
                    String new_desc = editTextDesc.getText().toString();

                    if (!new_diagnosis.isEmpty() && !new_treatment.isEmpty() && !new_desc.isEmpty()) {
                        Date currentDate = new Date();
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                        String formattedDate = sdf.format(currentDate);

                        // Update the appointment in the database
                        boolean isUpdated = myDB.updateConsultation(con_id.get(position), selectedAppointmentID, selectedConsultationType, new_diagnosis, new_treatment, new_desc, formattedDate);

                        if (isUpdated) {
                            // Update the UI with the new data
                            con_datetime.set(position, selectedAppointmentID);
                            con_type.set(position, selectedConsultationType);
                            con_diagnosis.set(position, new_diagnosis);
                            con_treatment.set(position, new_treatment);
                            con_desc.set(position, new_desc);

                            // Notify the adapter about the change
                            notifyItemChanged(position);

                            Toast.makeText(context, "Consultation updated successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, "Failed to update appointment", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(context, "All fields must be filled out", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }
}
