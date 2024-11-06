package com.example.healthconnect;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class PatientAdapter extends RecyclerView.Adapter<PatientAdapter.MyViewHolder> {

    Context context;
    ArrayList userID, userFName, userLName, userEmail;

    PatientAdapter(Context context, ArrayList userID, ArrayList userFName, ArrayList userLName, ArrayList userEmail){
        this.context = context;
        this.userID = userID;
        this.userFName = userFName;
        this.userLName = userLName;
        this.userEmail = userEmail;
    }

    @NonNull
    @Override
    public PatientAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.patient_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PatientAdapter.MyViewHolder holder, int position) {
        holder.tv_userName.setText(String.valueOf(userFName.get(position)) + " " + String.valueOf(userLName.get(position)));
        holder.tv_userEmail.setText(String.valueOf(userEmail.get(position)));

    }

    @Override
    public int getItemCount() {
        return userID.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tv_userName, tv_userEmail;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_userName = itemView.findViewById(R.id.tvUserName);
            tv_userEmail = itemView.findViewById(R.id.tvEmail);
        }
    }
}
