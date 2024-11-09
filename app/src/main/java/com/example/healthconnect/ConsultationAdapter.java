package com.example.healthconnect;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

    public ConsultationAdapter(Context context, ArrayList<String> con_id, ArrayList<String> con_datetime, ArrayList<String> con_type,
                               ArrayList<String> con_diagnosis, ArrayList<String> con_treatment, ArrayList<String> con_desc) {
        this.context = context;
        this.con_id = con_id;
        this.con_datetime = con_datetime;
        this.con_type = con_type;
        this.con_diagnosis = con_diagnosis;
        this.con_treatment = con_treatment;
        this.con_desc = con_desc;
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

            holder.tvConDateTime.setText(outputDateString);
            holder.tvConType.setText("Consultation Type: " + con_type.get(position));
            holder.tvConDiagnosis.setText("Diagnosis: " + con_diagnosis.get(position));
            holder.tvConTreatment.setText("Treatment: " + con_treatment.get(position));
            holder.tvConDesc.setText(con_desc.get(position));

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


        public ConsultationViewHolder(View itemView) {
            super(itemView);
            tvConDateTime = itemView.findViewById(R.id.tvConDateTime);
            tvConType = itemView.findViewById(R.id.tvConType);
            tvConDiagnosis = itemView.findViewById(R.id.tvConDiagnosis);
            tvConTreatment = itemView.findViewById(R.id.tvConTreatment);
            tvConDesc = itemView.findViewById(R.id.tvConDesc);
        }
    }
}
