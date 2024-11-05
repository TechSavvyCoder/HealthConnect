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

public class MyDatabaseHelper extends SQLiteOpenHelper {

    private Context context;
    private static final String DATABASE_NAME = "HealthConnect.db";
    private static final int DATABASE_VERSION = 1;

    private static final String USER_TABLE_NAME = "user";
    private static final String USER_COLUMN_ID = "user_id";
    private static final String USER_COLUMN_USERNAME = "user_name";
    private static final String USER_COLUMN_USERPASS = "user_pass";
    private static final String USER_COLUMN_USERROLE = "user_role";
    private static final String USER_COLUMN_EMAIL= "user_email";
    private static final String USER_COLUMN_FIRSTNAME = "user_firstName";
    private static final String USER_COLUMN_LASTNAME = "user_lastName";
    private static final String USER_COLUMN_DATEOFBIRTH = "date_of_birth";
    private static final String USER_COLUMN_DATECREATED = "date_created";
    private static final String USER_COLUMN_DATEUPDATED = "date_updated";

    private static final String PATIENT_TABLE = "patient";
    private static final String PATIENT_COLUMN_ID = "patient_id";
    private static final String PATIENT_COLUMN_USERID = "user_id";
    private static final String PATIENT_COLUMN_MEDICALHISTORY = "medical_history";
    private static final String PATIENT_COLUMN_DATECREATED = "date_created";
    private static final String PATIENT_COLUMN_DATEUPDATED = "date_updated";

    public MyDatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
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
                        USER_COLUMN_DATECREATED + " TEXT, " +
                        USER_COLUMN_DATEUPDATED + " TEXT " +
                        " );";
        db.execSQL(query_user);

        String query_patient =
                "CREATE TABLE " + PATIENT_TABLE + " ( " +
                        PATIENT_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        PATIENT_COLUMN_USERID + " TEXT NOT NULL, " +
                        PATIENT_COLUMN_MEDICALHISTORY + " TEXT NOT NULL, " +
                        PATIENT_COLUMN_DATECREATED + " TEXT, " +
                        PATIENT_COLUMN_DATEUPDATED + " TEXT " +
                        " );";
        db.execSQL(query_patient);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + USER_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + PATIENT_TABLE);
    }

    // Function to add new user
    void addUser(String user_name, String user_pass, String user_role, String user_email, String user_firstname, String user_lastname, String date_of_birth, String date_created){

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
        if(result == -1){
            Toast.makeText(context, "User Registration Failed!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Successfully Registered!", Toast.LENGTH_SHORT).show();
        }
    }

    // Function to check user credentials
    public boolean checkUserCredentials(String user_name, String user_pass, Context context) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + USER_TABLE_NAME + " WHERE " + USER_COLUMN_USERNAME + " =? AND " + USER_COLUMN_USERPASS + " =?";
        Cursor cursor = db.rawQuery(query, new String[]{user_name, user_pass});

        if (cursor != null && cursor.moveToFirst()) {
            // Extract user details from the cursor
            String user_id = cursor.getString(cursor.getColumnIndex(USER_COLUMN_ID));
            String username = cursor.getString(cursor.getColumnIndex(USER_COLUMN_USERNAME));
            String password = cursor.getString(cursor.getColumnIndex(USER_COLUMN_USERPASS));
            String user_role = cursor.getString(cursor.getColumnIndex(USER_COLUMN_USERROLE));
            String user_email = cursor.getString(cursor.getColumnIndex(USER_COLUMN_EMAIL));
            String user_firstname = cursor.getString(cursor.getColumnIndex(USER_COLUMN_FIRSTNAME));
            String user_lastname = cursor.getString(cursor.getColumnIndex(USER_COLUMN_LASTNAME));
            String user_dob = cursor.getString(cursor.getColumnIndex(USER_COLUMN_DATEOFBIRTH));

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
}
