package com.example.healthconnect;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.text.format.DateUtils;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Notification extends Service {

    private SessionManager sessionManager;
    String loggedInUserID, loggedInUserName;

    Handler handler = new Handler();
    MyDatabaseHelper dbHelper; // Assuming you have a DBHelper class to manage SQLite

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        // Initialize the session manager
        sessionManager = new SessionManager(this);
        loggedInUserID = sessionManager.getUserId();
        loggedInUserName = sessionManager.getUserFirstName();
        dbHelper = new MyDatabaseHelper(this); // Initialize your database helper
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        handler.removeCallbacks(sendUpdates);
        handler.postDelayed(sendUpdates, 1000); // 1 second
        return START_STICKY;
    }

    private Runnable sendUpdates = new Runnable() {
        public void run() {
            matchTime();
            handler.postDelayed(this, 50000); // 50-second notifier
        }
    };

    public void matchTime() {
        // Get the current date
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());

        String currentDate = dateFormat.format(calendar.getTime());

        // Add 1 day to current date
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        String targetDate = dateFormat.format(calendar.getTime());

        // Fetch appointments from database

        createNotification("2024-11-23");

        List<String> appointmentDates = dbHelper.getAllAppointmentsPerDoctor(loggedInUserID); // Retrieve dates as a list
//        for (String appointmentDate : appointmentDates) {
//
//            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm"); // Input with time
//            SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy/MM/dd");
//
//            try {
//                // Parse the input date
//                Date date = inputFormat.parse(appointmentDate);
//                // Format it to the desired output
//                appointmentDate = outputFormat.format(date);
//            } catch (ParseException e) {
//                Log.e("DateUtils", "Error parsing date: " + e.getMessage());
//            }
//
//            Log.d("MatchTime", "AppointmentDate++" + appointmentDate);
//            Log.d("MatchTime", "TargetDate++" + targetDate);
//            if (appointmentDate.equals(targetDate)) {
//                // Schedule a notification
//                createNotification(appointmentDate);
//
//                Log.d("MatchTime", "___Checking for appointments...");
//                Log.d("MatchTime", "___Notification created for: " + appointmentDate);
//            }
//        }
    }

    private void createNotification(String appointmentDate) {
        String channelId = "AppointmentNotifications";

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (notificationManager == null) {
            Log.e("Notification", "___NotificationManager is null");
            return;
        } else {
            Log.e("Notification", "___NotificationManager is not null");
        }

        // Create Notification Channel (For Android 8.0 and above)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "Appointment Reminders",
                    NotificationManager.IMPORTANCE_HIGH
            );
            notificationManager.createNotificationChannel(channel);
            Log.d("NotificationTest", "___Notification Channel Created");

        }

        // Build the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.mipmap.ic_launcher_foreground)
                .setContentTitle("Appointment Reminder")
                .setContentText("You have an appointment scheduled on " + appointmentDate)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        // Show the notification
        Log.d("NotificationTest", "___Preparing to show notification...");
        int notificationId = 1;  // Use a constant ID for simplicity
        notificationManager.notify(notificationId, builder.build());
        Log.d("NotificationTest", "___Notification should now be visible.");

    }
}
