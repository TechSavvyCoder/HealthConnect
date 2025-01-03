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
    String loggedInUserRole, loggedID;
    private RecyclerView recyclerView;
    private AppointmentAdapter appointmentAdapter;

    MyDatabaseHelper myDB;
    ArrayList<String> app_id, patient_id, doctor_id, app_datetime, app_desc, app_status;
    TextView noAppointmentsMessage;

    // Variables to store patient info
    String patient_id_from_bundle;

    View view;

    public AppointmentFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        sessionManager = new SessionManager(getContext());
        loggedInUserRole = sessionManager.getUserRole();
        loggedID = sessionManager.getUserId();

        if (loggedInUserRole.equals("Doctor")) {
            view = inflater.inflate(R.layout.fragment_appointment, container, false);

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

            // Retrieve patient info from the arguments
            if (getArguments() != null) {
                patient_id_from_bundle = getArguments().getString("patient_id");
            }

            storeDataInArray(loggedID, patient_id_from_bundle);

            appointmentAdapter = new AppointmentAdapter(getContext(), app_id, patient_id, doctor_id, app_datetime, app_desc, app_status);
            recyclerView.setAdapter(appointmentAdapter);
        } else {
            view = inflater.inflate(R.layout.fragment_appointment, container, false);

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

            // Retrieve patient info from the arguments
            if (getArguments() != null) {
                patient_id_from_bundle = getArguments().getString("doctor_id");
            }

            storeDataInArray(patient_id_from_bundle, loggedID);

            appointmentAdapter = new AppointmentAdapter(getContext(), app_id, patient_id, doctor_id, app_datetime, app_desc, app_status);
            recyclerView.setAdapter(appointmentAdapter);
        }

        return view;
    }

    void storeDataInArray(String loggedDoctorID, String currPatientID) {
        Cursor cursor = myDB.getAllAppointments(loggedDoctorID, currPatientID);
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
