package com.example.florra_a;

import android.app.Application;
import androidx.work.Constraints;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import com.example.florra_a.network.NotificationReceiver;
import com.example.florra_a.network.NotificationWorker;
import com.example.florra_a.network.RetrofitClient;
import java.util.concurrent.TimeUnit;

public class FlorraApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // Initialize RetrofitClient with application context on app startup
        RetrofitClient.init(this);
        
        startNotificationService();
        scheduleNotificationCheck();
    }

    private void startNotificationService() {
        android.content.Intent serviceIntent = new android.content.Intent(this, com.example.florra_a.network.NotificationService.class);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent);
        } else {
            startService(serviceIntent);
        }
    }

    private void scheduleNotificationCheck() {
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        PeriodicWorkRequest notificationWorkRequest =
                new PeriodicWorkRequest.Builder(NotificationWorker.class, 15, TimeUnit.MINUTES)
                        .setConstraints(constraints)
                        .addTag("NotificationCheck")
                        .build();

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
                "NotificationCheck",
                androidx.work.ExistingPeriodicWorkPolicy.KEEP,
                notificationWorkRequest
        );

        scheduleAlarmManager();
    }

    private void scheduleAlarmManager() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, NotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this, 0, intent, 
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        long interval = 5 * 60 * 1000; // 5 minutes
        long triggerTime = System.currentTimeMillis() + interval;

        if (alarmManager != null) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
            } else {
                alarmManager.set(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
            }
        }
    }
}
