package com.example.florra_a;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

public class SubscriptionActivity extends AppCompatActivity implements com.razorpay.PaymentResultListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscription);

        // Preload Razorpay for faster loading
        try {
            com.razorpay.Checkout.preload(getApplicationContext());
        } catch (Exception e) {
            android.util.Log.e("Razorpay", "Error in preload", e);
        }

        // Hide ActionBar if present
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        initializeViews();
    }

    private void initializeViews() {
        // Back Button
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        // Load Showroom Image
        ImageView ivShowroom = findViewById(R.id.ivShowroom);
        String imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuASjN-lJAl_odH5UmSyjWZbIP9sUCCabPE8G2jLhDu3fZbN7Y-IbgQ_SZiG_I2n8iXnTTVabRLD8VwGn4Y3r-10Lza7Vfx-tRxo7NTRL1vJTWHyfh4LL3V4wdiBuWjC9klhnyPEV-a1FkiGeUanB51iQvfjSLVnFRdqERXXooA2lAZzVpnzeIq3d25RoBgn4ZnbnZcX6Lwd9UHhSbJkFqituqyLq6zZEooG1hehIlRq0cKr39yRy6LMj7nUJmZiwzKxKkwbl9eT3Y9N";
        
        Glide.with(this)
                .load(imageUrl)
                .centerCrop()
                .placeholder(R.drawable.placeholder_image) 
                .error(android.R.color.darker_gray)
                .into(ivShowroom);

        // Subscribe Button
        findViewById(R.id.btnSubscribe).setOnClickListener(v -> startPayment());

        // Restore Purchase
        findViewById(R.id.btnRestore).setOnClickListener(v -> {
            Toast.makeText(this, "Restore purchase logic not implemented yet", Toast.LENGTH_SHORT).show();
            // TODO: Implement restore purchase logic
        });
    }

    private void startPayment() {
        com.razorpay.Checkout checkout = new com.razorpay.Checkout();
        // TEST Key ID - Replace with your LIVE Key ID in production
        checkout.setKeyID("rzp_test_1DP5mmOlF5G5ag"); 
        
        checkout.setImage(R.drawable.ic_launcher_foreground); // App Icon

        try {
            org.json.JSONObject options = new org.json.JSONObject();
            options.put("name", "Florra");
            options.put("description", "Premium Subscription");
            options.put("image", "https://s3.amazonaws.com/rzp-mobile/images/rzp.png");
            options.put("theme.color", "#3399cc");
            options.put("currency", "INR");
            options.put("amount", "10000"); // Amount in paise (100.00 INR)
            options.put("prefill.email", "test@florra.com");
            options.put("prefill.contact", "9988776655");

            checkout.open(this, options);
        } catch(Exception e) {
            android.util.Log.e("Razorpay", "Error in starting Razorpay Checkout", e);
            Toast.makeText(this, "Error in payment: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onPaymentSuccess(String razorpayPaymentID) {
        // Navigate to Success Screen
        Toast.makeText(this, "Payment Successful: " + razorpayPaymentID, Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(SubscriptionActivity.this, PaymentSuccessActivity.class);
        startActivity(intent);
        finish(); // Optional: Close subscription screen so user can't go back directly
    }

    @Override
    public void onPaymentError(int code, String response) {
        Toast.makeText(this, "Payment Failed or Cancelled", Toast.LENGTH_SHORT).show();
        // Uncomment below to debug error details
        // Toast.makeText(this, "Error: " + response, Toast.LENGTH_LONG).show();
    }

    public static Intent newIntent(Context context) {
        return new Intent(context, SubscriptionActivity.class);
    }
}
