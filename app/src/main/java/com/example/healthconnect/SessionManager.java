package com.example.healthconnect;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private Context context;

    // Shared Preferences file name
    private static final String PREF_NAME = "UserSession";

    // Key names for user data in SharedPreferences
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_USER_ROLE = "user_role";
    private static final String KEY_USER_EMAIL = "user_email";
    private static final String KEY_USER_FIRSTNAME = "user_firstname";
    private static final String KEY_USER_LASTNAME = "user_lastname";
    private static final String KEY_USER_DOB = "user_dob";

    public SessionManager(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    // Save user session data
    public void saveUserSession(String userId, String username, String userRole, String userEmail, String userFirstName, String userLastName, String userDOB) {
        editor.putString(KEY_USER_ID, userId);
        editor.putString(KEY_USERNAME, username);
        editor.putString(KEY_USER_ROLE, userRole);
        editor.putString(KEY_USER_EMAIL, userEmail);
        editor.putString(KEY_USER_FIRSTNAME, userFirstName);
        editor.putString(KEY_USER_LASTNAME, userLastName);
        editor.putString(KEY_USER_DOB, userDOB);
        editor.apply();  // commit changes
    }

    // Retrieve user session data
    public String getUserId() {
        return sharedPreferences.getString(KEY_USER_ID, "");
    }

    public String getUsername() {
        return sharedPreferences.getString(KEY_USERNAME, "");
    }

    public String getUserRole() {
        return sharedPreferences.getString(KEY_USER_ROLE, "");
    }

    public String getUserEmail() {
        return sharedPreferences.getString(KEY_USER_EMAIL, "");
    }

    public String getUserFirstName() {
        return sharedPreferences.getString(KEY_USER_FIRSTNAME, "Guest");
    }

    public String getUserLastName() {
        return sharedPreferences.getString(KEY_USER_LASTNAME, "");
    }

    public String getUserDOB() {
        return sharedPreferences.getString(KEY_USER_DOB, "");
    }

    // Check if a session is active (i.e., if user_id exists)
    public boolean isSessionActive() {
        return !getUserId().isEmpty();
    }

    // Clear the session (e.g., when logging out)
    public void clearSession() {
        editor.clear();
        editor.apply();
    }
}
