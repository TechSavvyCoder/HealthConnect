package com.example.healthconnect;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.SyncStateContract;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class MyDatabaseHelper extends SQLiteOpenHelper {

    private Context context;
    private static final String DATABASE_NAME = "HealthConnect.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_NAME = "user";
    private static final String COLUMN_ID = "user_id";
    private static final String COLUMN_USERNAME = "user_name";
    private static final String COLUMN_USERPASS = "user_pass";
    private static final String COLUMN_USERROLE = "user_role";
    private static final String COLUMN_EMAIL= "user_email";
    private static final String COLUMN_FIRSTNAME = "user_firstName";
    private static final String COLUMN_LASTNAME = "user_lastName";
    private static final String COLUMN_DATEOFBIRTH = "date_of_birth";
    private static final String COLUMN_DATECREATED = "date_created";
    private static final String COLUMN_DATEUPDATED = "date_updated";

    public MyDatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query =
                "CREATE TABLE " + TABLE_NAME + " ( " +
                        COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        COLUMN_USERNAME + " TEXT NOT NULL, " +
                        COLUMN_USERPASS + " TEXT NOT NULL, " +
                        COLUMN_USERROLE + " TEXT, " +
                        COLUMN_EMAIL + " TEXT, " +
                        COLUMN_FIRSTNAME + " TEXT, " +
                        COLUMN_LASTNAME + " TEXT, " +
                        COLUMN_DATEOFBIRTH + " TEXT, " +
                        COLUMN_DATECREATED + " TEXT, " +
                        COLUMN_DATEUPDATED + " TEXT " +
                        " );";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
    }

    // Function to add new user
    void addUser(String user_name, String user_pass, String user_role, String user_email, String user_firstname, String user_lastname, String date_of_birth, String date_created){

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_USERNAME, user_name);
        cv.put(COLUMN_USERPASS, user_pass);
        cv.put(COLUMN_USERROLE, user_role);
        cv.put(COLUMN_EMAIL, user_email);
        cv.put(COLUMN_FIRSTNAME, user_firstname);
        cv.put(COLUMN_LASTNAME, user_lastname);
        cv.put(COLUMN_DATEOFBIRTH, date_of_birth);
        cv.put(COLUMN_DATECREATED, date_created);

        long result = db.insert(TABLE_NAME, null, cv);
        if(result == -1){
            Toast.makeText(context, "User Registration Failed!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Successfully Registered!", Toast.LENGTH_SHORT).show();
        }
    }

    // Function to check user credentials
    public boolean checkUserCredentials(String user_name, String user_pass) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_USERNAME + " =? AND " + COLUMN_USERPASS + " =?";
        Cursor cursor = db.rawQuery(query, new String[]{user_name, user_pass});

        if (cursor != null && cursor.getCount() > 0) {
            cursor.close();
            return true;
        } else {
            if (cursor != null) {
                cursor.close();
            }
            return false;
        }
    }
}
