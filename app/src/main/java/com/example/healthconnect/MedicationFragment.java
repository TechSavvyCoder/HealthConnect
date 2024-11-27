package com.example.healthconnect;

import android.database.Cursor;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class MedicationFragment extends Fragment {

    private SessionManager sessionManager;
    String loggedInUserRole, loggedID;
    private RecyclerView recyclerView;
    private MedicationAdapter medicationAdapter;

    MyDatabaseHelper myDB;
    ArrayList<String> med_id, med_dateConsulted, med_dosage, med_frequency,  med_duration, med_desc;
    TextView noMedicationsMessage;

    // Variables to store patient info
    String patient_id_from_bundle;

    View view;

    public MedicationFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        sessionManager = new SessionManager(getContext());
        loggedInUserRole = sessionManager.getUserRole();
        loggedID = sessionManager.getUserId();

        if (loggedInUserRole.equals("Doctor")) {
            view = inflater.inflate(R.layout.fragment_medication, container, false);

            myDB = new MyDatabaseHelper(getContext());
            med_id = new ArrayList<>();
            med_dateConsulted = new ArrayList<>();
            med_dosage = new ArrayList<>();
            med_frequency = new ArrayList<>();
            med_duration = new ArrayList<>();
            med_desc = new ArrayList<>();

            noMedicationsMessage = view.findViewById(R.id.noMedicationsMessage);
            recyclerView = view.findViewById(R.id.rvMedicationList);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

            // Retrieve patient info from the arguments
            if (getArguments() != null) {
                patient_id_from_bundle = getArguments().getString("patient_id");
            }

            storeDataInArray(loggedID, patient_id_from_bundle);

            medicationAdapter = new MedicationAdapter(getContext(), med_id, med_dateConsulted, med_dosage, med_frequency, med_duration, med_desc, loggedID, patient_id_from_bundle);
            recyclerView.setAdapter(medicationAdapter);
        } else {
            view = inflater.inflate(R.layout.fragment_medication, container, false);

            myDB = new MyDatabaseHelper(getContext());
            med_id = new ArrayList<>();
            med_dateConsulted = new ArrayList<>();
            med_dosage = new ArrayList<>();
            med_frequency = new ArrayList<>();
            med_duration = new ArrayList<>();
            med_desc = new ArrayList<>();

            noMedicationsMessage = view.findViewById(R.id.noMedicationsMessage);
            recyclerView = view.findViewById(R.id.rvMedicationList);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

            // Retrieve patient info from the arguments
            if (getArguments() != null) {
                patient_id_from_bundle = getArguments().getString("doctor_id");
            }

            storeDataInArray(patient_id_from_bundle, loggedID);

            medicationAdapter = new MedicationAdapter(getContext(), med_id, med_dateConsulted, med_dosage, med_frequency, med_duration, med_desc, patient_id_from_bundle, loggedID);
            recyclerView.setAdapter(medicationAdapter);

        }

        return view;
    }

    void storeDataInArray(String loggedDoctorID, String currPatientID) {
        Cursor cursor = myDB.getAllMedications(loggedDoctorID, currPatientID);

        if(cursor.getCount() == 0){
            noMedicationsMessage.setVisibility(View.VISIBLE);
        } else {
            noMedicationsMessage.setVisibility(View.GONE);
            while(cursor.moveToNext()){
                med_id.add(cursor.getString(0));
                med_dateConsulted.add(cursor.getString(1));
                med_desc.add(cursor.getString(2));
                med_dosage.add(cursor.getString(3));
                med_frequency.add(cursor.getString(4));
                med_duration.add(cursor.getString(5));

            }
        }
    }
}