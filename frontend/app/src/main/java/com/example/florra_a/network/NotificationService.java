package com.example.florra_a.network;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import com.example.florra_a.R;
import com.example.florra_a.utils.NotificationHelper;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationService extends Service {

    private static final String CHANNEL_ID = "florra_service_channel";
    private static final int SERVICE_NOTIFICATION_ID = 999;
    private static final String PREFS_NAME = "FlorraPrefs";
    private static final String KEY_LAST_NOTIF_ID = "last_notification_id";
    
    private Handler handler = new Handler();
    private Runnable pollRunnable;
    private static final long POLL_INTERVAL = 120000; // 2 minutes

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Florra is active")
                .setContentText("Checking for new updates in background...")
                .setSmallIcon(R.drawable.a)
                .setPriority(NotificationCompat.PRIORITY_MIN)
                .build();

        startForeground(SERVICE_NOTIFICATION_ID, notification);
        
        startPolling();
        
        return START_STICKY;
    }

    private void startPolling() {
        pollRunnable = new Runnable() {
            @Override
            public void run() {
                checkNotifications();
                handler.postDelayed(this, POLL_INTERVAL);
            }
        };
        handler.post(pollRunnable);
    }

    private void checkNotifications() {
        // Reschedule the alarm for next check
        scheduleNextAlarm();

        ApiService apiService = RetrofitClient.getApiService();
        apiService.getNotifications().enqueue(new Callback<List<com.example.florra_a.models.Notification>>() {
            @Override
            public void onResponse(Call<List<com.example.florra_a.models.Notification>> call, 
                                   Response<List<com.example.florra_a.models.Notification>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<com.example.florra_a.models.Notification> notifications = response.body();
                    SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
                    int lastId = prefs.getInt(KEY_LAST_NOTIF_ID, -1);
                    int maxId = lastId;

                    for (com.example.florra_a.models.Notification notif : notifications) {
                        if (notif.getId() > lastId) {
                            NotificationHelper.showNotification(
                                NotificationService.this, 
                                notif.getTitle(), 
                                notif.getMessage()
                            );
                            if (notif.getId() > maxId) maxId = notif.getId();
                        }
                    }

                    if (maxId > lastId) {
                        prefs.edit().putInt(KEY_LAST_NOTIF_ID, maxId).apply();
                    }
                }
            }

            @Override
            public void onFailure(Call<List<com.example.florra_a.models.Notification>> call, Throwable t) {
                Log.e("NotificationService", "Poll failed: " + t.getMessage());
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (pollRunnable != null) {
            handler.removeCallbacks(pollRunnable);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Florra Background Service",
                    NotificationManager.IMPORTANCE_LOW
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(serviceChannel);
            }
        }
    }

    private void scheduleNextAlarm() {
        android.app.AlarmManager alarmManager = (android.app.AlarmManager) getSystemService(Context.ALARM_SERVICE);
        android.content.Intent intent = new android.content.Intent(this, NotificationReceiver.class);
        android.app.PendingIntent pendingIntent = android.app.PendingIntent.getBroadcast(
                this, 0, intent, 
                android.app.PendingIntent.FLAG_UPDATE_CURRENT | android.app.PendingIntent.FLAG_IMMUTABLE
        );

        long interval = 5 * 60 * 1000; // 5 minutes
        long triggerTime = System.currentTimeMillis() + interval;

        if (alarmManager != null) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                alarmManager.setAndAllowWhileIdle(android.app.AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
            } else {
                alarmManager.set(android.app.AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
            }
        }
    }
}
