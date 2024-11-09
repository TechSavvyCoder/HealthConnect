package com.example.healthconnect;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class AppointmentAdapter extends RecyclerView.Adapter<AppointmentAdapter.AppointmentViewHolder> {

    Context context;
    ArrayList<String> app_id, patient_id, doctor_id, app_datetime, app_desc, app_status;
    MyDatabaseHelper myDB;

    public AppointmentAdapter(Context context, ArrayList<String> app_id, ArrayList<String> patient_id,
                              ArrayList<String> doctor_id, ArrayList<String> app_datetime, ArrayList<String> app_desc,
                              ArrayList<String> app_status) {
        this.context = context;
        this.app_id = app_id;
        this.patient_id = patient_id;
        this.doctor_id = doctor_id;
        this.app_datetime = app_datetime;
        this.app_desc = app_desc;
        this.app_status = app_status;
        this.myDB = new MyDatabaseHelper(context);  // Initialize database helper here
    }

    @Override
    public AppointmentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_appointment, parent, false);
        return new AppointmentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AppointmentViewHolder holder, int position) {
        String inputDateString = app_datetime.get(position);
        String outputDateString;

        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault());
            Date date = inputFormat.parse(inputDateString);

            SimpleDateFormat outputFormat = new SimpleDateFormat("MMM dd, yyyy 'at' h:mm a", Locale.getDefault());
            outputDateString = outputFormat.format(date);

            holder.tv_appDateTime.setText(outputDateString);
            holder.tv_appStatusValue.setText("Status: " + app_status.get(position));
            holder.tv_appDesc.setText(app_desc.get(position));

            // Set OnClickListener for the Delete button
            holder.btnDelete.setOnClickListener(v -> showDeleteConfirmationDialog(position, outputDateString, app_desc.get(position)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return app_id.size();
    }

    public static class AppointmentViewHolder extends RecyclerView.ViewHolder {
        TextView tv_appDateTime, tv_appStatusValue, tv_appDesc;
        Button btnDelete;

        public AppointmentViewHolder(View itemView) {
            super(itemView);
            tv_appDateTime = itemView.findViewById(R.id.tvAppDateTime);
            tv_appStatusValue = itemView.findViewById(R.id.tvAppStatusValue);
            tv_appDesc = itemView.findViewById(R.id.tvAppDesc);
            btnDelete = itemView.findViewById(R.id.btnDelete);  // Button reference
        }
    }

    // Show confirmation dialog before deletion
    private void showDeleteConfirmationDialog(int position, String app_date, String app_desc) {
        new android.app.AlertDialog.Builder(context)
                .setTitle("Delete appointment?")
                .setMessage("This will delete your appointment on " + app_date + "\n\nDescription: " + app_desc)
                .setCancelable(false)
                .setPositiveButton("yes, delete", (dialog, id) -> {
                    deleteAppointment(position);
                })
                .setNegativeButton("Cancel", null)
                .show();

    }

    // Method to delete the appointment from the database
    private void deleteAppointment(int position) {
        // Get the appointment ID from the data
        String appointmentId = app_id.get(position);

        // Delete the appointment from the database
        boolean isDeleted = myDB.deleteEntry("appointment", "appointment_id", appointmentId);

        if (isDeleted) {
            // Remove the item from the list and notify the adapter
            app_id.remove(position);
            patient_id.remove(position);
            doctor_id.remove(position);
            app_datetime.remove(position);
            app_desc.remove(position);
            app_status.remove(position);
            notifyItemRemoved(position);

            // Optionally, you can show a toast message
            Toast.makeText(context, "Appointment deleted successfully", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Failed to delete appointment", Toast.LENGTH_SHORT).show();
        }
    }
}
