package com.example.florra_a;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.florra_a.utils.SharedPrefManager;

public class AboutFlorraActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Immersive status bar setup
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(android.view.View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR); // Dark icons for light theme
            getWindow().setStatusBarColor(android.graphics.Color.WHITE);
        }
        
        setContentView(R.layout.activity_about_florra);

        ImageButton btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        }

        // Live Role & Access Permissions Binding
        SharedPrefManager pref = SharedPrefManager.getInstance(this);
        String role = pref.getRole();

        // 1. Show Admin & Showroom Features for both Admins and Staff
        TextView tvAdminFeaturesHeader = findViewById(R.id.tvAdminFeaturesHeader);
        View cardShopMgmt = findViewById(R.id.cardShopMgmt);
        View cardStaffMgmt = findViewById(R.id.cardStaffMgmt);

        if ("admin".equalsIgnoreCase(role) || "staff".equalsIgnoreCase(role)) {
            if (tvAdminFeaturesHeader != null) tvAdminFeaturesHeader.setVisibility(View.VISIBLE);
            if (cardShopMgmt != null) cardShopMgmt.setVisibility(View.VISIBLE);
            if (cardStaffMgmt != null) cardStaffMgmt.setVisibility(View.VISIBLE);
        }

        // 2. Show Staff Specific Permission Indicators ONLY if logged-in user is "staff"
        TextView tvStaffAccessHeader = findViewById(R.id.tvStaffAccessHeader);
        View rowAccessBilling = findViewById(R.id.rowAccessBilling);
        View rowAccessReports = findViewById(R.id.rowAccessReports);
        View rowAccessPredictions = findViewById(R.id.rowAccessPredictions);

        if ("staff".equalsIgnoreCase(role)) {
            if (tvStaffAccessHeader != null) tvStaffAccessHeader.setVisibility(View.VISIBLE);
            if (rowAccessBilling != null) rowAccessBilling.setVisibility(View.VISIBLE);
            if (rowAccessReports != null) rowAccessReports.setVisibility(View.VISIBLE);
            if (rowAccessPredictions != null) rowAccessPredictions.setVisibility(View.VISIBLE);

            // Bind live states for Billing
            ImageView imgAccessBillingStatus = findViewById(R.id.imgAccessBillingStatus);
            TextView tvAccessBillingLabel = findViewById(R.id.tvAccessBillingLabel);
            if (pref.canAccessBilling()) {
                if (imgAccessBillingStatus != null) {
                    imgAccessBillingStatus.setImageResource(R.drawable.ic_check_circle);
                    imgAccessBillingStatus.setColorFilter(android.graphics.Color.parseColor("#22C55E"));
                }
                if (tvAccessBillingLabel != null) {
                    tvAccessBillingLabel.setText("ALLOWED");
                    tvAccessBillingLabel.setTextColor(android.graphics.Color.parseColor("#22C55E"));
                }
            } else {
                if (imgAccessBillingStatus != null) {
                    imgAccessBillingStatus.setImageResource(R.drawable.ic_lock);
                    imgAccessBillingStatus.setColorFilter(android.graphics.Color.parseColor("#EF4444"));
                }
                if (tvAccessBillingLabel != null) {
                    tvAccessBillingLabel.setText("LOCKED");
                    tvAccessBillingLabel.setTextColor(android.graphics.Color.parseColor("#EF4444"));
                }
            }

            // Bind live states for Reports
            ImageView imgAccessReportsStatus = findViewById(R.id.imgAccessReportsStatus);
            TextView tvAccessReportsLabel = findViewById(R.id.tvAccessReportsLabel);
            if (pref.canAccessReports()) {
                if (imgAccessReportsStatus != null) {
                    imgAccessReportsStatus.setImageResource(R.drawable.ic_check_circle);
                    imgAccessReportsStatus.setColorFilter(android.graphics.Color.parseColor("#22C55E"));
                }
                if (tvAccessReportsLabel != null) {
                    tvAccessReportsLabel.setText("ALLOWED");
                    tvAccessReportsLabel.setTextColor(android.graphics.Color.parseColor("#22C55E"));
                }
            } else {
                if (imgAccessReportsStatus != null) {
                    imgAccessReportsStatus.setImageResource(R.drawable.ic_lock);
                    imgAccessReportsStatus.setColorFilter(android.graphics.Color.parseColor("#EF4444"));
                }
                if (tvAccessReportsLabel != null) {
                    tvAccessReportsLabel.setText("LOCKED");
                    tvAccessReportsLabel.setTextColor(android.graphics.Color.parseColor("#EF4444"));
                }
            }

            // Bind live states for Predictions
            ImageView imgAccessPredictionsStatus = findViewById(R.id.imgAccessPredictionsStatus);
            TextView tvAccessPredictionsLabel = findViewById(R.id.tvAccessPredictionsLabel);
            if (pref.canAccessPredictions()) {
                if (imgAccessPredictionsStatus != null) {
                    imgAccessPredictionsStatus.setImageResource(R.drawable.ic_check_circle);
                    imgAccessPredictionsStatus.setColorFilter(android.graphics.Color.parseColor("#22C55E"));
                }
                if (tvAccessPredictionsLabel != null) {
                    tvAccessPredictionsLabel.setText("ALLOWED");
                    tvAccessPredictionsLabel.setTextColor(android.graphics.Color.parseColor("#22C55E"));
                }
            } else {
                if (imgAccessPredictionsStatus != null) {
                    imgAccessPredictionsStatus.setImageResource(R.drawable.ic_lock);
                    imgAccessPredictionsStatus.setColorFilter(android.graphics.Color.parseColor("#EF4444"));
                }
                if (tvAccessPredictionsLabel != null) {
                    tvAccessPredictionsLabel.setText("LOCKED");
                    tvAccessPredictionsLabel.setTextColor(android.graphics.Color.parseColor("#EF4444"));
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}
