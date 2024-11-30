package com.example.healthconnect;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.SyncStateContract;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MyDatabaseHelper extends SQLiteOpenHelper {

    private Context context;
    private static final String DATABASE_NAME = "HealthConnect.db";
    private static final int DATABASE_VERSION = 1;

    // User Table
    private static final String USER_TABLE_NAME = "user";
    private static final String USER_COLUMN_ID = "user_id";
    private static final String USER_COLUMN_USERNAME = "user_name";
    private static final String USER_COLUMN_USERPASS = "user_pass";
    private static final String USER_COLUMN_USERROLE = "user_role";
    private static final String USER_COLUMN_EMAIL= "user_email";
    private static final String USER_COLUMN_FIRSTNAME = "user_firstName";
    private static final String USER_COLUMN_LASTNAME = "user_lastName";
    private static final String USER_COLUMN_DATEOFBIRTH = "date_of_birth";
    private static final String USER_COLUMN_DESC = "user_desc";
    private static final String USER_COLUMN_DOCTORID = "doctor_id";
    private static final String USER_COLUMN_DATECREATED = "date_created";
    private static final String USER_COLUMN_DATEUPDATED = "date_updated";

    // Appointment Table
    private static final String APPOINTMENT_TABLE = "appointment";
    private static final String APPOINTMENT_COLUMN_ID = "appointment_id";
    private static final String APPOINTMENT_COLUMN_PATIENTID = "patient_id";
    private static final String APPOINTMENT_COLUMN_DOCTORID = "doctor_id";
    private static final String APPOINTMENT_COLUMN_DATETIME = "appointment_dateTime";
    private static final String APPOINTMENT_COLUMN_STATUS = "appointment_status";
    private static final String APPOINTMENT_COLUMN_DESC = "appointment_desc";
    private static final String APPOINTMENT_COLUMN_ISNOTIFIED = "is_notified";
    private static final String APPOINTMENT_COLUMN_DATECREATED = "date_created";
    private static final String APPOINTMENT_COLUMN_DATEUPDATED = "date_updated";

    // Consultation Table
    private static final String CONSULTATION_TABLE = "consultation";
    private static final String CONSULTATION_COLUMN_ID = "consultation_id";
    private static final String CONSULTATION_COLUMN_APPOINTMENTID = "appointment_id";
    private static final String CONSULTATION_COLUMN_TYPE = "consultation_type";
    private static final String CONSULTATION_COLUMN_DIAGNOSIS = "consultation_diagnosis";
    private static final String CONSULTATION_COLUMN_TREATMENT = "consultation_treatment";
    private static final String CONSULTATION_COLUMN_DESC = "consultation_desc";
    private static final String CONSULTATION_COLUMN_DATECREATED = "date_created";
    private static final String CONSULTATION_COLUMN_DATEUPDATED = "date_updated";

    // Medication Table
    private static final String MEDICATION_TABLE = "medication";
    private static final String MEDICATION_COLUMN_ID = "medication_id";
    private static final String MEDICATION_COLUMN_CONSULTATIONID = "consultation_id";
    private static final String MEDICATION_COLUMN_DESC = "medication_desc";
    private static final String MEDICATION_COLUMN_DOSAGE = "medication_dosage";
    private static final String MEDICATION_COLUMN_FREQUENCY = "medication_frequency";
    private static final String MEDICATION_COLUMN_DURATION = "medication_duration";
    private static final String MEDICATION_COLUMN_DATECREATED = "date_created";
    private static final String MEDICATION_COLUMN_DATEUPDATED = "date_updated";

    public MyDatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;


    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query_user =
                "CREATE TABLE " + USER_TABLE_NAME + " ( " +
                        USER_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        USER_COLUMN_USERNAME + " TEXT NOT NULL, " +
                        USER_COLUMN_USERPASS + " TEXT NOT NULL, " +
                        USER_COLUMN_USERROLE + " TEXT, " +
                        USER_COLUMN_EMAIL + " TEXT, " +
                        USER_COLUMN_FIRSTNAME + " TEXT, " +
                        USER_COLUMN_LASTNAME + " TEXT, " +
                        USER_COLUMN_DATEOFBIRTH + " TEXT, " +
                        USER_COLUMN_DESC + " TEXT, " +
                        USER_COLUMN_DOCTORID + " INTEGER, " +
                        USER_COLUMN_DATECREATED + " TEXT, " +
                        USER_COLUMN_DATEUPDATED + " TEXT " +
                        " );";
        db.execSQL(query_user);

        String query_appointment =
                "CREATE TABLE " + APPOINTMENT_TABLE + " ( " +
                        APPOINTMENT_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        APPOINTMENT_COLUMN_PATIENTID + " TEXT NOT NULL, " +
                        APPOINTMENT_COLUMN_DOCTORID + " TEXT NOT NULL, " +
                        APPOINTMENT_COLUMN_DATETIME + " TEXT NOT NULL, " +
                        APPOINTMENT_COLUMN_DESC + " TEXT, " +
                        APPOINTMENT_COLUMN_STATUS + " TEXT NOT NULL, " +
                        APPOINTMENT_COLUMN_ISNOTIFIED + " INTEGER NOT NULL, " +
                        APPOINTMENT_COLUMN_DATECREATED + " TEXT, " +
                        APPOINTMENT_COLUMN_DATEUPDATED + " TEXT " +
                        " );";
        db.execSQL(query_appointment);

        String query_consultation =
                "CREATE TABLE " + CONSULTATION_TABLE + " ( " +
                        CONSULTATION_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        CONSULTATION_COLUMN_APPOINTMENTID + " TEXT NOT NULL, " +
                        CONSULTATION_COLUMN_TYPE + " TEXT, " +
                        CONSULTATION_COLUMN_DIAGNOSIS + " TEXT, " +
                        CONSULTATION_COLUMN_TREATMENT + " TEXT, " +
                        CONSULTATION_COLUMN_DESC + " TEXT, " +
                        CONSULTATION_COLUMN_DATECREATED + " TEXT, " +
                        CONSULTATION_COLUMN_DATEUPDATED + " TEXT " +
                        " );";
        db.execSQL(query_consultation);

        String query_medication =
                "CREATE TABLE " + MEDICATION_TABLE + " ( " +
                        MEDICATION_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        MEDICATION_COLUMN_CONSULTATIONID + " TEXT NOT NULL, " +
                        MEDICATION_COLUMN_DESC + " TEXT, " +
                        MEDICATION_COLUMN_DOSAGE + " TEXT, " +
                        MEDICATION_COLUMN_FREQUENCY + " TEXT, " +
                        MEDICATION_COLUMN_DURATION + " TEXT, " +
                        MEDICATION_COLUMN_DATECREATED + " TEXT, " +
                        MEDICATION_COLUMN_DATEUPDATED + " TEXT " +
                        " );";
        db.execSQL(query_medication);
    }


    public void copyDatabase() throws IOException {
        try {
            InputStream inputStream = context.getAssets().open(DATABASE_NAME); // Open database from assets
            String outFileName = context.getDatabasePath(DATABASE_NAME).getPath(); // Target path in internal storage

            File file = new File(outFileName);
            if (file.exists()) return; // Skip if the database already exists

            OutputStream outputStream = new FileOutputStream(outFileName);

            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }

            outputStream.flush();
            outputStream.close();
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + USER_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + APPOINTMENT_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + CONSULTATION_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + MEDICATION_TABLE);
        onCreate(db);
    }

    // Method to delete the database and recreate the tables
    public void resetDatabase() {
        // Recreate the database and tables
        SQLiteDatabase db = this.getWritableDatabase();

        db.execSQL("DROP TABLE IF EXISTS " + USER_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + APPOINTMENT_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + CONSULTATION_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + MEDICATION_TABLE);

        onCreate(db);
        db.close();
    }

    public void makeThisTable() {
        // Recreate the database and tables
        SQLiteDatabase db = this.getWritableDatabase();
        resetDatabase();
        String query_user =
                "CREATE TABLE " + USER_TABLE_NAME + " ( " +
                        USER_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        USER_COLUMN_USERNAME + " TEXT NOT NULL, " +
                        USER_COLUMN_USERPASS + " TEXT NOT NULL, " +
                        USER_COLUMN_USERROLE + " TEXT, " +
                        USER_COLUMN_EMAIL + " TEXT, " +
                        USER_COLUMN_FIRSTNAME + " TEXT, " +
                        USER_COLUMN_LASTNAME + " TEXT, " +
                        USER_COLUMN_DATEOFBIRTH + " TEXT, " +
                        USER_COLUMN_DESC + " TEXT, " +
                        USER_COLUMN_DOCTORID + " INTEGER, " +
                        USER_COLUMN_DATECREATED + " TEXT, " +
                        USER_COLUMN_DATEUPDATED + " TEXT " +
                        " );";
        db.execSQL(query_user);

        String query_appointment =
                "CREATE TABLE " + APPOINTMENT_TABLE + " ( " +
                        APPOINTMENT_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        APPOINTMENT_COLUMN_PATIENTID + " TEXT NOT NULL, " +
                        APPOINTMENT_COLUMN_DOCTORID + " TEXT NOT NULL, " +
                        APPOINTMENT_COLUMN_DATETIME + " TEXT NOT NULL, " +
                        APPOINTMENT_COLUMN_DESC + " TEXT, " +
                        APPOINTMENT_COLUMN_STATUS + " TEXT NOT NULL, " +
                        APPOINTMENT_COLUMN_ISNOTIFIED + " INTEGER NOT NULL, " +
                        APPOINTMENT_COLUMN_DATECREATED + " TEXT, " +
                        APPOINTMENT_COLUMN_DATEUPDATED + " TEXT " +
                        " );";
        db.execSQL(query_appointment);

        String query_consultation =
                "CREATE TABLE " + CONSULTATION_TABLE + " ( " +
                        CONSULTATION_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        CONSULTATION_COLUMN_APPOINTMENTID + " TEXT NOT NULL, " +
                        CONSULTATION_COLUMN_TYPE + " TEXT, " +
                        CONSULTATION_COLUMN_DIAGNOSIS + " TEXT, " +
                        CONSULTATION_COLUMN_TREATMENT + " TEXT, " +
                        CONSULTATION_COLUMN_DESC + " TEXT, " +
                        CONSULTATION_COLUMN_DATECREATED + " TEXT, " +
                        CONSULTATION_COLUMN_DATEUPDATED + " TEXT " +
                        " );";
        db.execSQL(query_consultation);

        String query_medication =
                "CREATE TABLE " + MEDICATION_TABLE + " ( " +
                        MEDICATION_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        MEDICATION_COLUMN_CONSULTATIONID + " TEXT NOT NULL, " +
                        MEDICATION_COLUMN_DESC + " TEXT, " +
                        MEDICATION_COLUMN_DOSAGE + " TEXT, " +
                        MEDICATION_COLUMN_FREQUENCY + " TEXT, " +
                        MEDICATION_COLUMN_DURATION + " TEXT, " +
                        MEDICATION_COLUMN_DATECREATED + " TEXT, " +
                        MEDICATION_COLUMN_DATEUPDATED + " TEXT " +
                        " );";
        db.execSQL(query_medication);
    }

    // Function to CREATE new entry
    public String addUser(String user_name, String user_pass, String user_role, String user_email, String user_firstname, String user_lastname, String date_of_birth, String date_created){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(USER_COLUMN_USERNAME, user_name);
        cv.put(USER_COLUMN_USERPASS, user_pass);
        cv.put(USER_COLUMN_USERROLE, user_role);
        cv.put(USER_COLUMN_EMAIL, user_email);
        cv.put(USER_COLUMN_FIRSTNAME, user_firstname);
        cv.put(USER_COLUMN_LASTNAME, user_lastname);
        cv.put(USER_COLUMN_DATEOFBIRTH, date_of_birth);
        cv.put(USER_COLUMN_DATECREATED, date_created);

        long result = db.insert(USER_TABLE_NAME, null, cv);

        if (result != -1) {
            return "success";
        } else {
            return "failed";
        }
    }

    public String addPatientPerDoctor(String user_name, String user_pass, String user_role, String user_email, String user_firstname, String user_lastname, String date_of_birth, String doctor_id, String date_created){

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(USER_COLUMN_USERNAME, user_name);
        cv.put(USER_COLUMN_USERPASS, user_pass);
        cv.put(USER_COLUMN_USERROLE, user_role);
        cv.put(USER_COLUMN_EMAIL, user_email);
        cv.put(USER_COLUMN_FIRSTNAME, user_firstname);
        cv.put(USER_COLUMN_LASTNAME, user_lastname);
        cv.put(USER_COLUMN_DATEOFBIRTH, date_of_birth);
        cv.put(USER_COLUMN_DOCTORID, doctor_id);
        cv.put(USER_COLUMN_DATECREATED, date_created);

        long result = db.insert(USER_TABLE_NAME, null, cv);

        if (result != -1) {
            return "success";
        } else {
            return "failed";
        }
    }

    public String addAppointment(String patient_id, String doctor_id, String app_datetime, String app_desc, String app_status, String date_created) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(APPOINTMENT_COLUMN_PATIENTID, patient_id);
        cv.put(APPOINTMENT_COLUMN_DOCTORID, doctor_id);
        cv.put(APPOINTMENT_COLUMN_DATETIME, app_datetime);
        cv.put(APPOINTMENT_COLUMN_DESC, app_desc);
        cv.put(APPOINTMENT_COLUMN_STATUS, app_status);
        cv.put(APPOINTMENT_COLUMN_ISNOTIFIED, "0");
        cv.put(APPOINTMENT_COLUMN_DATECREATED, date_created);

        long result = db.insert(APPOINTMENT_TABLE, null, cv);

        if (result != -1) {
            return "success";
        } else {
            return "failed";
        }
    }

    public String addConsulation(String app_id, String con_type, String con_diagnosis, String con_treatment, String cont_desc, String date_created) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(CONSULTATION_COLUMN_APPOINTMENTID, app_id);
        cv.put(CONSULTATION_COLUMN_TYPE, con_type);
        cv.put(CONSULTATION_COLUMN_DIAGNOSIS, con_diagnosis);
        cv.put(CONSULTATION_COLUMN_TREATMENT, con_treatment);
        cv.put(CONSULTATION_COLUMN_DESC, cont_desc);
        cv.put(CONSULTATION_COLUMN_DATECREATED, date_created);

        long result = db.insert(CONSULTATION_TABLE, null, cv);

        if (result != -1) {
            return "success";
        } else {
            return "failed";
        }
    }

    public String addMedication(String con_id, String med_desc, String med_dosage, String med_frequency, String med_duration, String date_created) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(MEDICATION_COLUMN_CONSULTATIONID, con_id);
        cv.put(MEDICATION_COLUMN_DESC, med_desc);
        cv.put(MEDICATION_COLUMN_DOSAGE, med_dosage);
        cv.put(MEDICATION_COLUMN_FREQUENCY, med_frequency);
        cv.put(MEDICATION_COLUMN_DURATION, med_duration);
        cv.put(MEDICATION_COLUMN_DATECREATED, date_created);

        long result = db.insert(MEDICATION_TABLE, null, cv);

        if (result != -1) {
            return "success";
        } else {
            return "failed";
        }
    }

    // Function to check user credentials
    public boolean checkUserCredentials(String user_name, String user_pass, Context context) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + USER_TABLE_NAME + " WHERE " + USER_COLUMN_USERNAME + " =? AND " + USER_COLUMN_USERPASS + " =?";
        Cursor cursor = db.rawQuery(query, new String[]{user_name, user_pass});

        if (cursor != null && cursor.moveToFirst()) {
            // Extract user details from the cursor
            int int_userID = cursor.getColumnIndex(USER_COLUMN_ID);
            int int_userName = cursor.getColumnIndex(USER_COLUMN_USERNAME);
            int int_userPass = cursor.getColumnIndex(USER_COLUMN_USERPASS);
            int int_userRole = cursor.getColumnIndex(USER_COLUMN_USERROLE);
            int int_userEmail = cursor.getColumnIndex(USER_COLUMN_EMAIL);
            int int_userFName = cursor.getColumnIndex(USER_COLUMN_FIRSTNAME);
            int int_userLName = cursor.getColumnIndex(USER_COLUMN_LASTNAME);
            int int_userDOB = cursor.getColumnIndex(USER_COLUMN_DATEOFBIRTH);

            String user_id = cursor.getString(int_userID);
            String username = cursor.getString(int_userName);
            String password = cursor.getString(int_userPass);
            String user_role = cursor.getString(int_userRole);
            String user_email = cursor.getString(int_userEmail);
            String user_firstname = cursor.getString(int_userFName);
            String user_lastname = cursor.getString(int_userLName);
            String user_dob = cursor.getString(int_userDOB);

            cursor.close();

            // Save user session details in SharedPreferences
            SessionManager sessionManager = new SessionManager(context);
            sessionManager.saveUserSession(user_id, username, user_role, user_email, user_firstname, user_lastname, user_dob);

            return true; // Login successful
        } else {
            if (cursor != null) {
                cursor.close();
            }
            return false; // Invalid credentials
        }
    }

    // Function to RETRIEVE entries
    public Cursor getAllPatient(String doctor_ID) {
        String query = "SELECT * FROM " + USER_TABLE_NAME + " " +
                "WHERE user_role = 'Patient' AND " + USER_COLUMN_DOCTORID + " = " + doctor_ID +
                " ORDER BY " + USER_COLUMN_FIRSTNAME + " ASC ";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        if(db != null){
            cursor = db.rawQuery(query, null);
        }
        return cursor;
    }

    public int countAllPatients(String doctor_ID){
        String query = "SELECT count(*) FROM " + USER_TABLE_NAME +
                " WHERE " + USER_COLUMN_DOCTORID + " = ? ";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        int result = 0;

        if (db != null) {
            cursor = db.rawQuery(query, new String[]{doctor_ID});
            if (cursor != null && cursor.moveToFirst()) {
                result = cursor.getInt(0); // Get the count from the first column
            }
        }
        return result;
    }

    public List<Appointment> getAppointmentsForNotification(String doctor_ID, String status) {
        List<Appointment> appointments = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + APPOINTMENT_COLUMN_ID + ", " + APPOINTMENT_COLUMN_DATETIME +
                " FROM " + APPOINTMENT_TABLE +
                " WHERE " + APPOINTMENT_COLUMN_DOCTORID + " = ? " +
                " AND " + APPOINTMENT_COLUMN_STATUS + " = ? " +
                " AND " + APPOINTMENT_COLUMN_ISNOTIFIED + " = 0 " + // Only fetch unnotified appointments
                " ORDER BY " + APPOINTMENT_COLUMN_DATETIME + " ASC";
        Cursor cursor = db.rawQuery(query, new String[]{doctor_ID, status});

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(0);
                String datetime = cursor.getString(1);
                appointments.add(new Appointment(id, datetime));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return appointments;
    }


    public void updateNotificationStatus(int appointmentId, boolean isNotified) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(APPOINTMENT_COLUMN_ISNOTIFIED, isNotified ? 1 : 0);

        db.update(APPOINTMENT_TABLE, values, APPOINTMENT_COLUMN_ID + " = ?", new String[]{String.valueOf(appointmentId)});
    }

    public Cursor getAllAppointments(String doctor_ID, String curr_Patient_ID) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + APPOINTMENT_TABLE +
                " WHERE " + APPOINTMENT_COLUMN_PATIENTID + " = ? " +
                " AND " + APPOINTMENT_COLUMN_DOCTORID + " = ? " +
                " ORDER BY " + APPOINTMENT_COLUMN_DATETIME + " ASC";

        Cursor cursor = null;

        try {
            cursor = db.rawQuery(query, new String[]{curr_Patient_ID, doctor_ID});
        } catch (Exception e) {
            e.printStackTrace();
        }

        return cursor;
    }

    public ArrayList<HashMap<String, String>> getAppointmentsForPatient(String doctor_ID, String curr_Patient_ID) {
        ArrayList<HashMap<String, String>> appointments = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // Query to get appointments related to the given patient ID
        String query = "SELECT * FROM " + APPOINTMENT_TABLE +
                " WHERE " + APPOINTMENT_COLUMN_PATIENTID + " = ? " +
                " AND " + APPOINTMENT_COLUMN_DOCTORID + " = ? " +
                " ORDER BY " + APPOINTMENT_COLUMN_DATETIME + " ASC";
        Cursor cursor = db.rawQuery(query, new String[]{curr_Patient_ID, doctor_ID});

        if (cursor != null && cursor.moveToFirst()) {
            do {
                int idIndex = cursor.getColumnIndex("appointment_id"); // Assuming 'appointment_id' is the ID column
                int dateIndex = cursor.getColumnIndex("appointment_dateTime");
                String appointmentID = cursor.getString(idIndex);
                String appointmentDateTime = cursor.getString(dateIndex);

                HashMap<String, String> appointment = new HashMap<>();
                appointment.put("id", appointmentID);
                appointment.put("dateTime", appointmentDateTime);
                appointments.add(appointment);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return appointments;
    }

    public Cursor getAllConsultations(String doctor_ID, String curr_Patient_ID) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + CONSULTATION_TABLE + " c "+
                " JOIN " + APPOINTMENT_TABLE + " a " +
                " ON c." + CONSULTATION_COLUMN_APPOINTMENTID + " = a." + APPOINTMENT_COLUMN_ID +
                " WHERE a." +  APPOINTMENT_COLUMN_DOCTORID + " = ? " +
                " AND a." +  APPOINTMENT_COLUMN_PATIENTID + " = ? ";

        Cursor cursor = null;

        try {
            cursor = db.rawQuery(query, new String[]{doctor_ID, curr_Patient_ID});
        } catch (Exception e) {
            e.printStackTrace();
        }

        return cursor;
    }

    public ArrayList<HashMap<String, String>> getConsultationsForPatient(String doctor_ID, String curr_Patient_ID) {
        ArrayList<HashMap<String, String>> consultations = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // Query to get consultation description and associated appointment dateTime for the given patient ID
        String query = "SELECT c.consultation_id, c.consultation_desc, a.appointment_dateTime " +
                "FROM " + CONSULTATION_TABLE + " c " +
                "INNER JOIN " + APPOINTMENT_TABLE + " a ON c.appointment_id = a.appointment_id " +
                "WHERE a.patient_id = ? AND a.doctor_id = ? " +
                "ORDER BY a.appointment_dateTime ASC";
        Cursor cursor = db.rawQuery(query, new String[]{curr_Patient_ID, doctor_ID});

        if (cursor != null && cursor.moveToFirst()) {
            do {
                // Get columns
                int idIndex = cursor.getColumnIndex("consultation_id");
                int descIndex = cursor.getColumnIndex("consultation_desc");
                int dateIndex = cursor.getColumnIndex("appointment_dateTime");

                String consultationID = cursor.getString(idIndex);
                String consultationDesc = cursor.getString(descIndex);
                String appointmentDateTime = cursor.getString(dateIndex);

                // Create a HashMap to store consultation details
                HashMap<String, String> consultation = new HashMap<>();
                consultation.put("consultation_id", consultationID); // Store consultation ID
                consultation.put("consultationDesc", consultationDesc);
                consultation.put("appointmentDateTime", appointmentDateTime);

                consultations.add(consultation);
            } while (cursor.moveToNext());

            cursor.close();
        }
        db.close();
        return consultations; // Return the list of consultations
    }

    public Cursor getAllMedications(String doctor_ID, String curr_Patient_ID) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + MEDICATION_TABLE + " m " +
                "JOIN " + CONSULTATION_TABLE + " c ON m.consultation_id = c.consultation_id " +
                "JOIN " + APPOINTMENT_TABLE + " a ON c.appointment_id = a.appointment_id " +
                "WHERE a." + APPOINTMENT_COLUMN_DOCTORID + " = ? " +
                "AND a." + APPOINTMENT_COLUMN_PATIENTID + " = ? ";

        Cursor cursor = null;

        try {
            cursor = db.rawQuery(query, new String[]{doctor_ID, curr_Patient_ID});
        } catch (Exception e) {
            e.printStackTrace();
        }

        return cursor;
    }


    public String getAppointmentInfoByID(String app_id, String colname) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(
                APPOINTMENT_TABLE,
                new String[]{colname},
                APPOINTMENT_COLUMN_ID + " = ?",
                new String[]{app_id},
                null,
                null,
                null
        );

        if (cursor != null && cursor.moveToFirst()) {
            int col_index = cursor.getColumnIndex(colname);
            String col_value = cursor.getString(col_index);

            cursor.close();
            return col_value;
        } else {
            return null;
        }
    }

    public int countAllAppointmentsByStatus(String doctor_ID, String status){
        String query = "SELECT count(*) FROM " + APPOINTMENT_TABLE +
                " WHERE doctor_id = ? AND " + APPOINTMENT_COLUMN_STATUS + " = ?";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        int result = 0;

        if (db != null) {
            cursor = db.rawQuery(query, new String[]{doctor_ID, status});
            if (cursor != null && cursor.moveToFirst()) {
                result = cursor.getInt(0); // Get the count from the first column
            }
        }
        return result;
    }

    public Cursor getAllAppointmentsByStatus(String doctor_ID, String status){
        String query = "SELECT * FROM " + APPOINTMENT_TABLE + " " +
                "WHERE doctor_id = " + doctor_ID + " AND " + APPOINTMENT_COLUMN_STATUS + " = " + status;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        if(db != null){
            cursor = db.rawQuery(query, null);
        }
        return cursor;
    }

    public String getConsultationInfoByID(String med_id, String colname) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(
                CONSULTATION_TABLE,
                new String[]{colname},
                CONSULTATION_COLUMN_ID + " = ?",
                new String[]{med_id},
                null,
                null,
                null
        );

        if (cursor != null && cursor.moveToFirst()) {
            int col_index = cursor.getColumnIndex(colname);
            String col_value = cursor.getString(col_index);

            cursor.close();
            return col_value;
        } else {
            return null;
        }
    }

    public String getUserNameById(String userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(
                USER_TABLE_NAME,                                                // Table name
                new String[]{USER_COLUMN_USERNAME},                             // null means all columns (SELECT *)
                USER_COLUMN_USERROLE + " = ? AND " + USER_COLUMN_ID + " = ?",   // WHERE clause with both filters
                new String[]{"Patient", userId},                // Values for the placeholders in the WHERE clause
                null,   // No GROUP BY
                null,   // No HAVING
                null    // No ORDER BY
        );

        if (cursor != null && cursor.moveToFirst()) {
            int int_userName = cursor.getColumnIndex(USER_COLUMN_USERNAME);
            String username = cursor.getString(int_userName);

            cursor.close();
            return username;
        } else {
            return null;
        }
    }

    public String getUserInfo(String userId, String colname) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(
                USER_TABLE_NAME,
                new String[]{colname},
                USER_COLUMN_USERROLE + " = ? AND " + USER_COLUMN_ID + " = ?",
                new String[]{"Patient", userId},
                null,
                null,
                null
        );

        if (cursor != null && cursor.moveToFirst()) {
            int col_index = cursor.getColumnIndex(colname);
            String col_value = cursor.getString(col_index);

            cursor.close();
            return col_value;
        } else {
            return null;
        }
    }

    public Cursor searchPatient(String searchText) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + USER_TABLE_NAME + " " +
                "WHERE (user_firstName LIKE ? " +
                "OR user_lastName LIKE ? " +
                "OR user_email LIKE ? ) " +
                "AND user_role == ? ";
        String[] selectionArgs = new String[]{
                "%" + searchText + "%",
                "%" + searchText + "%",
                "%" + searchText + "%",
                "Patient"
        };
        return db.rawQuery(query, selectionArgs);
    }


    // Function to DELETE entries
    public boolean deleteEntry(String table_name, String col_name, String col_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsDeleted = db.delete(table_name, col_name + " = ?", new String[]{col_id});
        return rowsDeleted > 0;
    }

    // Function to UPDATE entries
    public boolean updatePatientInfo(String user_id, String new_username, String new_email, String new_firstName, String new_lastName, String new_dob, String new_desc, String date_updated){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(USER_COLUMN_USERNAME, new_username);  // Update dateTime
        contentValues.put(USER_COLUMN_EMAIL, new_email);          // Update description
        contentValues.put(USER_COLUMN_FIRSTNAME, new_firstName); // Update the status field
        contentValues.put(USER_COLUMN_LASTNAME, new_lastName);
        contentValues.put(USER_COLUMN_DATEOFBIRTH, new_dob);
        contentValues.put(USER_COLUMN_DESC, new_desc);
        contentValues.put(USER_COLUMN_DATEUPDATED, date_updated);

        // Define the condition to update the row (i.e., matching the appointment_id)
        String selection = USER_COLUMN_ID + " = ?";
        String[] selectionArgs = { user_id };

        // Perform the update and return whether it was successful
        int rowsUpdated = db.update(USER_TABLE_NAME, contentValues, selection, selectionArgs);

        db.close();

        return rowsUpdated > 0;
    }

    public boolean updateAppointment(String app_id, String newDateTime, String newDesc, String newStatus, String date_updated) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(APPOINTMENT_COLUMN_DATETIME, newDateTime);  // Update dateTime
        contentValues.put(APPOINTMENT_COLUMN_DESC, newDesc);          // Update description
        contentValues.put(APPOINTMENT_COLUMN_STATUS, newStatus); // Update the status field
        contentValues.put(APPOINTMENT_COLUMN_DATEUPDATED, date_updated); // Update the date_updated field

        // Define the condition to update the row (i.e., matching the appointment_id)
        String selection = APPOINTMENT_COLUMN_ID + " = ?";
        String[] selectionArgs = { app_id };

        // Perform the update and return whether it was successful
        int rowsUpdated = db.update(APPOINTMENT_TABLE, contentValues, selection, selectionArgs);

        db.close();

        return rowsUpdated > 0;
    }

    public boolean updateConsultation(String con_id, String new_appointment, String new_type, String new_diagnosis, String new_treatment, String new_desc, String date_updated) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(CONSULTATION_COLUMN_APPOINTMENTID, new_appointment);
        contentValues.put(CONSULTATION_COLUMN_TYPE, new_type);
        contentValues.put(CONSULTATION_COLUMN_DIAGNOSIS, new_diagnosis);
        contentValues.put(CONSULTATION_COLUMN_TREATMENT, new_treatment);
        contentValues.put(CONSULTATION_COLUMN_DESC, new_desc);
        contentValues.put(CONSULTATION_COLUMN_DATEUPDATED, date_updated);

        // Define the condition to update the row (i.e., matching the appointment_id)
        String selection = CONSULTATION_COLUMN_ID + " = ?";
        String[] selectionArgs = { con_id };

        // Perform the update and return whether it was successful
        int rowsUpdated = db.update(CONSULTATION_TABLE, contentValues, selection, selectionArgs);

        db.close();

        return rowsUpdated > 0;
    }

    public boolean updateMedication(String med_id, String new_desc, String new_dosage, String new_frequency, String new_duration, String date_updated) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(MEDICATION_COLUMN_DESC, new_desc);
        contentValues.put(MEDICATION_COLUMN_DOSAGE, new_dosage);
        contentValues.put(MEDICATION_COLUMN_FREQUENCY, new_frequency);
        contentValues.put(MEDICATION_COLUMN_DURATION, new_duration);
        contentValues.put(MEDICATION_COLUMN_DATEUPDATED, date_updated);

        // Define the condition to update the row (i.e., matching the medication_id)
        String selection = MEDICATION_COLUMN_ID + " = ?";
        String[] selectionArgs = { med_id };

        // Perform the update and return whether it was successful
        int rowsUpdated = db.update(MEDICATION_TABLE, contentValues, selection, selectionArgs);

        db.close();

        return rowsUpdated > 0;
    }
}
