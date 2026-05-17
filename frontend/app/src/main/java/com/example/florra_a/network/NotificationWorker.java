package com.example.florra_a.network;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import com.example.florra_a.models.Notification;
import com.example.florra_a.utils.NotificationHelper;
import java.util.List;
import retrofit2.Response;

public class NotificationWorker extends Worker {

    private static final String PREFS_NAME = "FlorraPrefs";
    private static final String KEY_LAST_NOTIF_ID = "last_notification_id";

    public NotificationWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        try {
            ApiService apiService = RetrofitClient.getApiService();
            Response<List<Notification>> response = apiService.getNotifications().execute();

            if (response.isSuccessful() && response.body() != null) {
                List<Notification> notifications = response.body();
                SharedPreferences prefs = getApplicationContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
                int lastId = prefs.getInt(KEY_LAST_NOTIF_ID, -1);
                int maxId = lastId;

                for (Notification notif : notifications) {
                    if (notif.getId() > lastId) {
                        // Show notification for each new one found
                        NotificationHelper.showNotification(
                            getApplicationContext(), 
                            notif.getTitle(), 
                            notif.getMessage()
                        );
                        if (notif.getId() > maxId) maxId = notif.getId();
                    }
                }

                if (maxId > lastId) {
                    prefs.edit().putInt(KEY_LAST_NOTIF_ID, maxId).apply();
                }
            }          return Result.success();
        } catch (Exception e) {
            e.printStackTrace();
            return Result.retry();
        }
    }
}
