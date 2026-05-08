package com.example.florra_a;

import android.content.Intent;
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

        // Enable edge-to-edge
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);

        // Handle notch and status bar
        WindowInsetsControllerCompat windowInsetsController =
                WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());
        if (windowInsetsController != null) {
            windowInsetsController.setAppearanceLightStatusBars(true);
            windowInsetsController.setAppearanceLightNavigationBars(true);
        }

        setContentView(R.layout.activity_splash);

        // Handler to navigate to main activity after 3 seconds
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Navigate to your existing MainActivity
                Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();

                // Add fade transition
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        }, 3000); // 3 seconds delay
    }

    @Override
    public void onBackPressed() {
        // Disable back button during splash
        // Do nothing
    }
}
