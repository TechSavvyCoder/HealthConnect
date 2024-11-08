package com.example.healthconnect;

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
    String loggedDoctorID;
    private RecyclerView recyclerView;
    private ConsultationAdapter consultationAdapter;

    MyDatabaseHelper myDB;
    ArrayList<String> con_id, patient_id, doctor_id, con_datetime, con_desc, con_status;
    TextView noConsultationsMessage;

    // Variables to store patient info
    String patient_id_from_bundle;

    public ConsultationFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        sessionManager = new SessionManager(getContext());
        loggedDoctorID = sessionManager.getUserId();

        View view = inflater.inflate(R.layout.fragment_consultation, container, false);

        myDB = new MyDatabaseHelper(getContext());
        con_id = new ArrayList<>();
        patient_id = new ArrayList<>();
        doctor_id = new ArrayList<>();
        con_datetime = new ArrayList<>();
        con_desc = new ArrayList<>();
        con_status = new ArrayList<>();

        noConsultationsMessage = view.findViewById(R.id.noConsultationsMessage);
        recyclerView = view.findViewById(R.id.rvConsultationList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Retrieve patient info from the arguments
        if (getArguments() != null) {
            patient_id_from_bundle = getArguments().getString("patient_id");
        }

        storeDataInArray(loggedDoctorID, patient_id_from_bundle);

        consultationAdapter = new ConsultationAdapter(getContext(), con_id, patient_id, doctor_id, con_datetime, con_desc, con_status);
        recyclerView.setAdapter(consultationAdapter);

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
                patient_id.add(cursor.getString(1));
                doctor_id.add(cursor.getString(2));
                con_datetime.add(cursor.getString(3));
                con_desc.add(cursor.getString(4));
                con_status.add(cursor.getString(5));
            }
        }
    }
}