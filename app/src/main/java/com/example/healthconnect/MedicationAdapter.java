package com.example.healthconnect;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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

public class MedicationAdapter extends RecyclerView.Adapter<MedicationAdapter.MedicationViewHolder> {

    Context context;
    ArrayList<String> med_id, med_dateConsulted, med_dosage, med_frequency, med_duration, med_desc;
    MyDatabaseHelper myDB;
    String loggedInUserID;
    String intent_user_id;

    public MedicationAdapter(Context context, ArrayList<String> con_id, ArrayList<String> med_dateConsulted, ArrayList<String> med_dosage,
                             ArrayList<String> med_frequency, ArrayList<String> med_duration, ArrayList<String> med_desc,
                             String loggedInUserID, String intent_user_id) {
        this.context = context;
        this.med_id = con_id;
        this.med_dateConsulted = med_dateConsulted;
        this.med_dosage = med_dosage;
        this.med_frequency = med_frequency;
        this.med_duration = med_duration;
        this.med_desc = med_desc;
        this.myDB = new MyDatabaseHelper(context);
        this.loggedInUserID = loggedInUserID;
        this.intent_user_id = intent_user_id;
    }

    @NonNull
    @Override
    public MedicationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_medication, parent, false);
        return new MedicationAdapter.MedicationViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(@NonNull MedicationAdapter.MedicationViewHolder holder, int position) {
        MyDatabaseHelper db = new MyDatabaseHelper(context);
        String get_colFromConsultation = db.getConsultationInfoByID(med_dateConsulted.get(position), "appointment_id");
        String con_dateTime = db.getAppointmentInfoByID(get_colFromConsultation, "appointment_dateTime");

        String outputDateString;

        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault());
            Date date = inputFormat.parse(con_dateTime);

            SimpleDateFormat outputFormat = new SimpleDateFormat("MMM dd, yyyy 'at' h:mm a", Locale.getDefault());
            outputDateString = outputFormat.format(date);

            holder.tvMedDateConsulted.setText("Date Consulted: "+ outputDateString);
            holder.tvMedDosage.setText("Dosage: " + med_dosage.get(position));
            holder.tvMedFrequency.setText("Frequency: " + med_frequency.get(position));
            holder.tvMedDuration.setText("Duration" + med_duration.get(position));
            holder.tvMedDesc.setText(med_desc.get(position));

            // Set OnClickListener for the Delete button
            holder.btnDelete.setOnClickListener(v -> showDeleteConfirmationDialog(position, outputDateString, med_desc.get(position)));
            holder.btnEdit.setOnClickListener(v -> showEditMedicationDialog(position));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return med_id.size();
    }

    public static class MedicationViewHolder extends RecyclerView.ViewHolder {
        TextView tvMedDateConsulted, tvMedDesc, tvMedDosage, tvMedFrequency, tvMedDuration;
        Button btnEdit, btnDelete;

        public MedicationViewHolder(View itemView, Context context) {
            super(itemView);
            tvMedDateConsulted = itemView.findViewById(R.id.tvMedDateConsulted);
            tvMedDosage = itemView.findViewById(R.id.tvMedDosage);
            tvMedFrequency = itemView.findViewById(R.id.tvMedFrequency);
            tvMedDuration = itemView.findViewById(R.id.tvMedDuration);
            tvMedDesc = itemView.findViewById(R.id.tvMedDesc);

            btnDelete = itemView.findViewById(R.id.btnDelete);
            btnEdit = itemView.findViewById(R.id.btnEdit);

            SessionManager sessionManager = new SessionManager(context);
            String loggedInUserRole = sessionManager.getUserRole();

            if (loggedInUserRole.equals("Patient")) {
                btnEdit.setVisibility(View.GONE);
                btnDelete.setVisibility(View.GONE);
            }
        }
    }

    // Show confirmation dialog before deletion
    private void showDeleteConfirmationDialog(int position, String med_date, String med_desc) {
        new android.app.AlertDialog.Builder(context)
                .setTitle("Delete entry?")
                .setMessage("This will delete your medication consulted on " + med_date + "\n\nDescription: " + med_desc)
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
        String medicationId = med_id.get(position);

        // Delete the appointment from the database
        boolean isDeleted = myDB.deleteEntry("medication", "medication_id", medicationId);

        if (isDeleted) {
            // Remove the item from the list and notify the adapter
            med_id.remove(position);
            med_dateConsulted.remove(position);
            med_dosage.remove(position);
            med_frequency.remove(position);
            med_duration.remove(position);
            med_desc.remove(position);
            notifyItemRemoved(position);

            // Optionally, you can show a toast message
            Toast.makeText(context, "Entry has been deleted successfully", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Failed to delete this entry", Toast.LENGTH_SHORT).show();
        }
    }

    // Show the Edit Medication Dialog
    private void showEditMedicationDialog(int position) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.doctor_add_patient_medication, null);

        // Find the views from the dialog
        EditText editTextDesc = dialogView.findViewById(R.id.txtDesc);
        EditText editTextDosage = dialogView.findViewById(R.id.txtDosage);
        EditText editTextFrequency = dialogView.findViewById(R.id.txtFrequency);
        EditText editTextDuration = dialogView.findViewById(R.id.txtDuration);

        // Spinners for Appointment and Consultation Type
        Spinner spinnerConsultation = dialogView.findViewById(R.id.spinnerConsultation);
        spinnerConsultation.setVisibility(View.GONE);

        // Set the existing data into the EditText fields
        editTextDesc.setText(med_desc.get(position)); // Set Desc
        editTextDosage.setText(med_dosage.get(position)); // Set Dosage
        editTextFrequency.setText(med_frequency.get(position)); // Set Dosage
        editTextDuration.setText(med_duration.get(position)); // Set Dosage

        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault());
        SimpleDateFormat outputFormat = new SimpleDateFormat("MMM dd, yyyy 'at' h:mm a", Locale.getDefault());

        // Create and show the Edit Consultation dialog
        new AlertDialog.Builder(context)
                .setTitle("Edit Medication")
                .setView(dialogView)
                .setPositiveButton("Save", (dialog, which) -> {
                    String new_desc = editTextDesc.getText().toString();
                    String new_dosage = editTextDosage.getText().toString();
                    String new_frequency = editTextFrequency.getText().toString();
                    String new_duration = editTextDuration.getText().toString();

                    if (!new_desc.isEmpty() && !new_dosage.isEmpty() && !new_frequency.isEmpty() && !new_duration.isEmpty()) {
                        Date currentDate = new Date();
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                        String formattedDate = sdf.format(currentDate);

                        // Update the medication in the database
                        boolean isUpdated = myDB.updateMedication(med_id.get(position), new_desc, new_dosage, new_frequency, new_duration, formattedDate);

                        if (isUpdated) {
                            // Update the UI with the new data
                            med_desc.set(position, new_desc);
                            med_dosage.set(position, new_dosage);
                            med_frequency.set(position, new_frequency);
                            med_duration.set(position, new_duration);

                            // Notify the adapter about the change
                            notifyItemChanged(position);

                            Toast.makeText(context, "Medication updated successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, "Failed to update medication", Toast.LENGTH_SHORT).show();
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
