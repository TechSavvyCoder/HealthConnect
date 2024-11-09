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

public class ConsultationAdapter extends RecyclerView.Adapter<ConsultationAdapter.ConsultationViewHolder>{

    Context context;
    ArrayList<String> con_id, con_datetime, con_type, con_diagnosis, con_treatment, con_desc;
    MyDatabaseHelper myDB;

    public ConsultationAdapter(Context context, ArrayList<String> con_id, ArrayList<String> con_datetime, ArrayList<String> con_type,
                               ArrayList<String> con_diagnosis, ArrayList<String> con_treatment, ArrayList<String> con_desc) {
        this.context = context;
        this.con_id = con_id;
        this.con_datetime = con_datetime;
        this.con_type = con_type;
        this.con_diagnosis = con_diagnosis;
        this.con_treatment = con_treatment;
        this.con_desc = con_desc;
        this.myDB = new MyDatabaseHelper(context);  // Initialize database helper here
    }

    @NonNull
    @Override
    public ConsultationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_consultation, parent, false);
        return new ConsultationAdapter.ConsultationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ConsultationAdapter.ConsultationViewHolder holder, int position) {
        MyDatabaseHelper db = new MyDatabaseHelper(context);
        String app_dateTime = db.getAppointmentInfoByID(con_datetime.get(position), "appointment_dateTime");

        Log.d("ConsultationAdapter", "Binding data for position: " + position);
        Log.d("ConsultationAdapter", "Data - Type: " + con_type.get(position));

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

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return con_id.size();
    }

    public static class ConsultationViewHolder extends RecyclerView.ViewHolder {
        TextView tvConDateTime, tvConType, tvConDiagnosis, tvConTreatment, tvConDesc;
        Button btnDelete;

        public ConsultationViewHolder(View itemView) {
            super(itemView);
            tvConDateTime = itemView.findViewById(R.id.tvConDateTime);
            tvConType = itemView.findViewById(R.id.tvConType);
            tvConDiagnosis = itemView.findViewById(R.id.tvConDiagnosis);
            tvConTreatment = itemView.findViewById(R.id.tvConTreatment);
            tvConDesc = itemView.findViewById(R.id.tvConDesc);

            btnDelete = itemView.findViewById(R.id.btnDelete);
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

            // Optionally, you can show a toast message
            Toast.makeText(context, "Entry has been deleted successfully", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Failed to delete this entry", Toast.LENGTH_SHORT).show();
        }
    }
}
