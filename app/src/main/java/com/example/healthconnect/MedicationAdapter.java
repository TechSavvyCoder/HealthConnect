package com.example.healthconnect;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class MedicationAdapter extends RecyclerView.Adapter<MedicationAdapter.MedicationViewHolder> {

    Context context;
    ArrayList<String> med_id, med_dateConsulted, med_dosage, med_frequency, med_duration, med_desc;
    MyDatabaseHelper myDB;

    public MedicationAdapter(Context context, ArrayList<String> con_id, ArrayList<String> med_dateConsulted, ArrayList<String> med_dosage,
                             ArrayList<String> med_frequency, ArrayList<String> med_duration, ArrayList<String> med_desc) {
        this.context = context;
        this.med_id = con_id;
        this.med_dateConsulted = med_dateConsulted;
        this.med_dosage = med_dosage;
        this.med_frequency = med_frequency;
        this.med_duration = med_duration;
        this.med_desc = med_desc;
        this.myDB = new MyDatabaseHelper(context);
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
}
