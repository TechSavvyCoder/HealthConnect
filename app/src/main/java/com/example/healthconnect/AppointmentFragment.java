package com.example.healthconnect;

import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.widget.TextView;

import java.util.ArrayList;

public class AppointmentFragment extends Fragment {

    private SessionManager sessionManager;
    String loggedDoctorID;
    private RecyclerView recyclerView;
    private AppointmentAdapter appointmentAdapter;

    MyDatabaseHelper myDB;
    ArrayList<String> app_id, patient_id, doctor_id, app_datetime, app_desc, app_status;
    TextView noAppointmentsMessage;

    public AppointmentFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        sessionManager = new SessionManager(getContext());
        loggedDoctorID = sessionManager.getUserId();

        View view = inflater.inflate(R.layout.fragment_appointment, container, false);

        myDB = new MyDatabaseHelper(getContext());
        app_id = new ArrayList<>();
        patient_id = new ArrayList<>();
        doctor_id = new ArrayList<>();
        app_datetime = new ArrayList<>();
        app_desc = new ArrayList<>();
        app_status = new ArrayList<>();

        noAppointmentsMessage = view.findViewById(R.id.noAppointmentsMessage);
        recyclerView = view.findViewById(R.id.rvAppointmentList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        storeDataInArray(loggedDoctorID);

        appointmentAdapter = new AppointmentAdapter(getContext(), app_id, patient_id, doctor_id, app_datetime, app_desc, app_status);
        recyclerView.setAdapter(appointmentAdapter);

        return view;
    }

    void storeDataInArray(String loggedDoctorID) {
        Cursor cursor = myDB.getAllAppointments(loggedDoctorID);
        if(cursor.getCount() == 0){
            noAppointmentsMessage.setVisibility(View.VISIBLE);
        } else {
            noAppointmentsMessage.setVisibility(View.GONE);
            while(cursor.moveToNext()){
                app_id.add(cursor.getString(0));
                patient_id.add(cursor.getString(1));
                doctor_id.add(cursor.getString(2));
                app_datetime.add(cursor.getString(3));
                app_desc.add(cursor.getString(4));
                app_status.add(cursor.getString(5));
            }
        }
    }
}
