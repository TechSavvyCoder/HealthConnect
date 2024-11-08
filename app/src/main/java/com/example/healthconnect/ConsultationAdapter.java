package com.example.healthconnect;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class ConsultationAdapter extends RecyclerView.Adapter<ConsultationAdapter.ConsultationViewHolder>{

    Context context;
    ArrayList<String> con_id, patient_id, doctor_id, con_datetime, con_desc, con_status;

    public ConsultationAdapter(Context context, ArrayList<String> con_id, ArrayList<String> patient_id,
                              ArrayList<String> doctor_id, ArrayList<String> con_datetime, ArrayList<String> con_desc,
                              ArrayList<String> con_status) {
        this.context = context;
        this.con_id = con_id;
        this.patient_id = patient_id;
        this.doctor_id = doctor_id;
        this.con_datetime = con_datetime;
        this.con_desc = con_desc;
        this.con_status = con_status;
    }

    @NonNull
    @Override
    public ConsultationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_consultation, parent, false);
        return new ConsultationAdapter.ConsultationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ConsultationAdapter.ConsultationViewHolder holder, int position) {
        String inputDateString = con_datetime.get(position);
        String outputDateString;

        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault());
            Date date = inputFormat.parse(inputDateString);

            SimpleDateFormat outputFormat = new SimpleDateFormat("MMM dd, yyyy 'at' h:mm a", Locale.getDefault());
            outputDateString = outputFormat.format(date);

            holder.tv_appDateTime.setText(outputDateString);
            holder.tv_appStatusValue.setText("Status: " + con_status.get(position));
            holder.tv_appDesc.setText(con_desc.get(position));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return con_id.size();
    }

    public static class ConsultationViewHolder extends RecyclerView.ViewHolder {
        TextView tv_appDateTime, tv_appStatusValue, tv_appDesc;

        public ConsultationViewHolder(View itemView) {
            super(itemView);
            tv_appDateTime = itemView.findViewById(R.id.tvAppDateTime);
            tv_appStatusValue = itemView.findViewById(R.id.tvAppStatusValue);
            tv_appDesc = itemView.findViewById(R.id.tvAppDesc);
        }
    }
}
