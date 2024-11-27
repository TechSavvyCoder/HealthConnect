package com.example.healthconnect;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
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
    MyDatabaseHelper dbHelper;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sessionManager = new SessionManager(this);
        loggedInUserID = sessionManager.getUserId();
        loggedInUserName = sessionManager.getUserFirstName();
        dbHelper = new MyDatabaseHelper(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        handler.removeCallbacks(sendUpdates);
        handler.postDelayed(sendUpdates, 1000);
        return START_STICKY;
    }

    private Runnable sendUpdates = new Runnable() {
        public void run() {
            matchTime();
            handler.postDelayed(this, 50000);
        }
    };

    public void matchTime() {
        // Get the current date
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());

        String currentDate = dateFormat.format(calendar.getTime());

        calendar.add(Calendar.DAY_OF_YEAR, 1);
        String targetDate = dateFormat.format(calendar.getTime());

        List<Appointment> appointments = dbHelper.getAppointmentsForNotification(loggedInUserID, "Pending");
        for (Appointment appointment : appointments) {
            int appointmentId = appointment.getId();
            String appointmentDate = appointment.getDate();

            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm");
            SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy/MM/dd");

            try {
                Date date = inputFormat.parse(appointmentDate);
                appointmentDate = outputFormat.format(date);
            } catch (ParseException e) {
                Log.e("DateUtils", "Error parsing date: " + e.getMessage());
            }

            if (appointmentDate.equals(targetDate)) {
                createNotification(appointmentDate);
                dbHelper.updateNotificationStatus(appointmentId, true);
            }
        }
    }

    private void createNotification(String appointmentDate) {
        String channelId = "AppointmentIDNotifications";

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), channelId)
                .setSmallIcon(R.mipmap.ic_launcher_foreground)
                .setContentTitle("Appointment Reminder")
                .setContentText("You have an appointment coming up")
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = notificationManager.getNotificationChannel(channelId);
            if (notificationChannel == null) {
                int importance = NotificationManager.IMPORTANCE_HIGH;
                notificationChannel = new NotificationChannel(channelId, "Appointment Reminders", importance);
                notificationChannel.setLightColor(Color.BLUE);
                notificationManager.createNotificationChannel(notificationChannel);
            }
        }

        notificationManager.notify(0, builder.build());
    }
}
