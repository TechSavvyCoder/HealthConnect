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
    private static final String USER_COLUMN_DATECREATED = "date_created";
    private static final String USER_COLUMN_DATEUPDATED = "date_updated";

    // Patient Table
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

    public Cursor getAllPatient() {
        String query = "SELECT * FROM " + USER_TABLE_NAME + " " +
                "WHERE user_role = 'Patient'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        if(db != null){
            cursor = db.rawQuery(query, null);
        }
        return cursor;
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

    public String getUserInfo(String userId, String tableName) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(
                USER_TABLE_NAME,
                new String[]{tableName},
                USER_COLUMN_USERROLE + " = ? AND " + USER_COLUMN_ID + " = ?",
                new String[]{"Patient", userId},
                null,
                null,
                null
        );

        if (cursor != null && cursor.moveToFirst()) {
            int col_index = cursor.getColumnIndex(tableName);
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

}
