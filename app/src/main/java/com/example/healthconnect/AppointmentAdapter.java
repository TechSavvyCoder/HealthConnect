package com.example.healthconnect;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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
        this.myDB = new MyDatabaseHelper(context);
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

            // Get the status value
            String status = app_status.get(position);

            // Set the status text
            holder.tv_appStatusValue.setText(status);

            // Change the font color based on the status
            if (status.equals("Pending")) {
                holder.tv_appStatusValue.setTextColor(Color.BLACK); // Black for Pending
            } else if (status.equals("Completed")) {
                holder.tv_appStatusValue.setTextColor(Color.parseColor("#008000")); // Green for Completed
            } else if (status.equals("Canceled")) {
                holder.tv_appStatusValue.setTextColor(Color.RED); // Red for Canceled
            } else if (status.equals("No Show")) {
                holder.tv_appStatusValue.setTextColor(Color.parseColor("#b48900")); // Gray for No Show
            }

            holder.tv_appDateTime.setText(outputDateString);
            holder.tv_appStatusValue.setText(app_status.get(position));
            holder.tv_appDesc.setText(app_desc.get(position));

            // Set OnClickListener for the Delete button
            holder.btnDelete.setOnClickListener(v -> showDeleteConfirmationDialog(position, outputDateString, app_desc.get(position)));
            holder.btnEdit.setOnClickListener(v -> showEditAppointmentDialog(position));
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
        Button btnEdit, btnDelete;

        public AppointmentViewHolder(View itemView) {
            super(itemView);
            tv_appDateTime = itemView.findViewById(R.id.tvAppDateTime);
            tv_appStatusValue = itemView.findViewById(R.id.tvAppStatusValue);
            tv_appDesc = itemView.findViewById(R.id.tvAppDesc);

            btnDelete = itemView.findViewById(R.id.btnDelete);
            btnEdit = itemView.findViewById(R.id.btnEdit);
        }
    }

    // Show confirmation dialog before deletion
    private void showDeleteConfirmationDialog(int position, String app_date, String app_desc) {
        new android.app.AlertDialog.Builder(context)
                .setTitle("Delete appointment?")
                .setMessage("This will delete your appointment on " + app_date + "\n\nDescription: " + app_desc)
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



    // Show the Edit Appointment Dialog
    private void showEditAppointmentDialog(int position) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.doctor_edit_patient_appointment, null);

        // Find the views from the dialog
        EditText editTextDate = dialogView.findViewById(R.id.editTextAppointmentDate);
        EditText editTextTime = dialogView.findViewById(R.id.editTextAppointmentTime);
        EditText editTextDesc = dialogView.findViewById(R.id.editTextAppointmentDescription);
        Spinner spinnerStatus = dialogView.findViewById(R.id.spinnerStatus);

        // Set the existing data into the EditText fields
        String currentDateTime = app_datetime.get(position);
        String[] dateTimeParts = currentDateTime.split(" ");
        editTextDate.setText(dateTimeParts[0]); // Set Date
        editTextTime.setText(dateTimeParts[1]); // Set Time
        editTextDesc.setText(app_desc.get(position)); // Set Description

        // Set the current status in the spinner
        ArrayAdapter<CharSequence> statusAdapter = ArrayAdapter.createFromResource(context,
                R.array.appointment_status_array, android.R.layout.simple_spinner_item);
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStatus.setAdapter(statusAdapter);

        // Set selected status based on the current data
        String currentStatus = app_status.get(position);
        int statusPosition = ((ArrayAdapter) spinnerStatus.getAdapter()).getPosition(currentStatus);
        spinnerStatus.setSelection(statusPosition);

        // Date Picker for Appointment Date
        editTextDate.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(context,
                    (view, selectedYear, selectedMonth, selectedDay) -> {
                        String formattedDate = String.format("%04d/%02d/%02d", selectedYear, selectedMonth + 1, selectedDay);
                        editTextDate.setText(formattedDate);
                    }, year, month, dayOfMonth);
            datePickerDialog.show();
        });

        // Time Picker for Appointment Time
        editTextTime.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);

            TimePickerDialog timePickerDialog = new TimePickerDialog(context,
                    (view, selectedHour, selectedMinute) -> {
                        String formattedTime = String.format("%02d:%02d", selectedHour, selectedMinute);
                        editTextTime.setText(formattedTime);
                    }, hour, minute, true);
            timePickerDialog.show();
        });

        // Create and show the Edit Appointment dialog
        new AlertDialog.Builder(context)
                .setTitle("Edit Appointment")
                .setView(dialogView)
                .setPositiveButton("Save", (dialog, which) -> {
                    String new_date = editTextDate.getText().toString();
                    String new_time = editTextTime.getText().toString();
                    String new_desc = editTextDesc.getText().toString();
                    String new_status = spinnerStatus.getSelectedItem().toString();

                    if (!new_date.isEmpty() && !new_time.isEmpty() && !new_desc.isEmpty()) {
                        Date currentDate = new Date();
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                        String formattedDate = sdf.format(currentDate);

                        String new_dateTime = new_date + " " + new_time;

                        // Update the appointment in the database
                        boolean isUpdated = myDB.updateAppointment(app_id.get(position), new_dateTime, new_desc, new_status, formattedDate);

                        if (isUpdated) {
                            // Update the UI with the new data
                            app_datetime.set(position, new_dateTime);
                            app_desc.set(position, new_desc);
                            app_status.set(position, new_status);

                            // Notify the adapter about the change
                            notifyItemChanged(position);

                            Toast.makeText(context, "Appointment updated successfully", Toast.LENGTH_SHORT).show();
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
