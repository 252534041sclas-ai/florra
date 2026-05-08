package com.example.florra_a;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

public class PaymentSuccessActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Set fullscreen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_payment_success);

        setupClickListeners();
    }

    private void setupClickListeners() {
        findViewById(R.id.btnStartRequesting).setOnClickListener(v -> {
            // Navigate to Home / Quotations (for now Home)
            Intent intent = new Intent(PaymentSuccessActivity.this, CustomerHomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });

        findViewById(R.id.btnViewSubscription).setOnClickListener(v -> {
            // Navigate to Account (or stay here if we had a dedicated "My Subscription" page)
            // For now, let's go to Account screen
            Intent intent = new Intent(PaymentSuccessActivity.this, CustomerAccountActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }

    @Override
    public void onBackPressed() {
        // Prevent going back to payment flow
        Intent intent = new Intent(PaymentSuccessActivity.this, CustomerHomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}
