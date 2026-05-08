package com.example.florra_a;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.view.Window;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import com.example.florra_a.network.RetrofitClient;

public class FlorraApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // Initialize RetrofitClient with application context on app startup
        RetrofitClient.init(this);

        // Apply global status bar and edge-to-edge styling to the entire app
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
                Window window = activity.getWindow();
                
                // Enable edge-to-edge (content behind status bar)
                WindowCompat.setDecorFitsSystemWindows(window, false);
                
                // Set status bar and navigation bar to light mode (dark icons)
                WindowInsetsControllerCompat controller = WindowCompat.getInsetsController(window, window.getDecorView());
                if (controller != null) {
                    controller.setAppearanceLightStatusBars(true);
                    controller.setAppearanceLightNavigationBars(true);
                }
            }

            @Override
            public void onActivityStarted(@NonNull Activity activity) {}

            @Override
            public void onActivityResumed(@NonNull Activity activity) {}

            @Override
            public void onActivityPaused(@NonNull Activity activity) {}

            @Override
            public void onActivityStopped(@NonNull Activity activity) {}

            @Override
            public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {}

            @Override
            public void onActivityDestroyed(@NonNull Activity activity) {}
        });
    }
}
