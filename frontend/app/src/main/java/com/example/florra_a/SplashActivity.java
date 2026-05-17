package com.example.florra_a;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsControllerCompat;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Make it fullscreen and handle edge-to-edge
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // Set status bar to white with dark icons
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            getWindow().setStatusBarColor(android.graphics.Color.WHITE);
            androidx.core.view.WindowInsetsControllerCompat controller = 
                androidx.core.view.WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());
            if (controller != null) {
                controller.setAppearanceLightStatusBars(true);
            }
        }

        // Handle notch and status bar
        WindowCompat.setDecorFitsSystemWindows(getWindow(), true);
        WindowInsetsControllerCompat windowInsetsController =
                WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());
        if (windowInsetsController != null) {
            windowInsetsController.setAppearanceLightStatusBars(true);
            windowInsetsController.setAppearanceLightNavigationBars(true);
        }

        setContentView(R.layout.activity_splash);

        checkNotificationPermission();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Trigger an immediate check for testing/initial load
                androidx.work.OneTimeWorkRequest immediateWork = 
                    new androidx.work.OneTimeWorkRequest.Builder(com.example.florra_a.network.NotificationWorker.class).build();
                androidx.work.WorkManager.getInstance(SplashActivity.this).enqueue(immediateWork);

                navigateNext();
            }
        }, 3000); // 3 seconds delay
    }

    private void checkNotificationPermission() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) 
                    != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 101);
            }
        }
    }

    private void navigateNext() {
        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean("is_logged_in", false);
        String userType = sharedPreferences.getString("user_type", "");

        Intent intent;
        if (isLoggedIn) {
            if ("admin".equals(userType)) {
                intent = new Intent(SplashActivity.this, AdminDashboardActivity.class);
            } else {
                intent = new Intent(SplashActivity.this, CustomerHomeActivity.class);
            }
        } else {
            intent = new Intent(SplashActivity.this, LoginActivity.class);
        }
        
        startActivity(intent);
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Override
    public void onBackPressed() {
        // Disable back button during splash
        // Do nothing
    }
}