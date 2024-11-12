package com.example.healthconnect;

import android.content.Context;
import android.database.Cursor;
import android.nfc.Tag;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class ConsultationFragment extends Fragment {

    private SessionManager sessionManager;
    String loggedInUserRole, loggedID;
    private RecyclerView recyclerView;
    private ConsultationAdapter consultationAdapter;

    MyDatabaseHelper myDB;
    ArrayList<String> con_id, con_datetime, con_type, con_diagnosis, con_treatment, con_desc;
    TextView noConsultationsMessage;

    // Variables to store patient info
    String id_from_bundle;

    View view;

    public ConsultationFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        sessionManager = new SessionManager(getContext());
        loggedInUserRole = sessionManager.getUserRole();
        loggedID = sessionManager.getUserId();

        if (loggedInUserRole.equals("Doctor")) {
            view = inflater.inflate(R.layout.fragment_consultation, container, false);

            myDB = new MyDatabaseHelper(getContext());
            con_id = new ArrayList<>();
            con_datetime = new ArrayList<>();
            con_type = new ArrayList<>();
            con_diagnosis = new ArrayList<>();
            con_treatment = new ArrayList<>();
            con_desc = new ArrayList<>();

            noConsultationsMessage = view.findViewById(R.id.noConsultationsMessage);
            recyclerView = view.findViewById(R.id.rvConsultationList);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

            // Retrieve patient info from the arguments
            if (getArguments() != null) {
                id_from_bundle = getArguments().getString("patient_id");
            }

            storeDataInArray(loggedID, id_from_bundle);

            consultationAdapter = new ConsultationAdapter(getContext(), con_id, con_datetime, con_type, con_diagnosis, con_treatment, con_desc, loggedID, id_from_bundle);
            recyclerView.setAdapter(consultationAdapter);
        } else {
            view = inflater.inflate(R.layout.fragment_consultation, container, false);

            myDB = new MyDatabaseHelper(getContext());
            con_id = new ArrayList<>();
            con_datetime = new ArrayList<>();
            con_type = new ArrayList<>();
            con_diagnosis = new ArrayList<>();
            con_treatment = new ArrayList<>();
            con_desc = new ArrayList<>();

            noConsultationsMessage = view.findViewById(R.id.noConsultationsMessage);
            recyclerView = view.findViewById(R.id.rvConsultationList);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

            // Retrieve patient info from the arguments
            if (getArguments() != null) {
                id_from_bundle = getArguments().getString("doctor_id");
            }

            storeDataInArray(id_from_bundle, loggedID);

            consultationAdapter = new ConsultationAdapter(getContext(), con_id, con_datetime, con_type, con_diagnosis, con_treatment, con_desc, id_from_bundle, loggedID);
            recyclerView.setAdapter(consultationAdapter);
        }

        return view;
    }

    void storeDataInArray(String loggedDoctorID, String currPatientID) {

        Cursor cursor = myDB.getAllConsultations(loggedDoctorID, currPatientID);

        if(cursor.getCount() == 0){
            noConsultationsMessage.setVisibility(View.VISIBLE);
        } else {
            noConsultationsMessage.setVisibility(View.GONE);
            while(cursor.moveToNext()){
                con_id.add(cursor.getString(0));
                con_datetime.add(cursor.getString(1));
                con_type.add(cursor.getString(2));
                con_diagnosis.add(cursor.getString(3));
                con_treatment.add(cursor.getString(4));
                con_desc.add(cursor.getString(5));
            }
        }
    }
}