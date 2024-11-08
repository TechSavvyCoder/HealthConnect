package com.example.healthconnect;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AppointmentAdapter extends RecyclerView.Adapter<AppointmentAdapter.AppointmentViewHolder> {

    Context context;
    ArrayList<String> app_id, patient_id, doctor_id, app_datetime, app_desc, app_status;

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
    }

    @Override
    public AppointmentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_appointment, parent, false);
        return new AppointmentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AppointmentViewHolder holder, int position) {
        holder.tv_appDateTime.setText(app_datetime.get(position));
        holder.tv_appStatusValue.setText(app_status.get(position));
        holder.tv_appDesc.setText(app_desc.get(position));
    }

    @Override
    public int getItemCount() {
        return app_id.size();
    }

    public static class AppointmentViewHolder extends RecyclerView.ViewHolder {
        TextView tv_appDateTime, tv_appStatusValue, tv_appDesc;

        public AppointmentViewHolder(View itemView) {
            super(itemView);
            tv_appDateTime = itemView.findViewById(R.id.tvAppDateTime);
            tv_appStatusValue = itemView.findViewById(R.id.tvAppStatusValue);
            tv_appDesc = itemView.findViewById(R.id.tvAppDesc);
        }
    }
}
