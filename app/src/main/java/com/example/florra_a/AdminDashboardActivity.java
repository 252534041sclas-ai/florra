package com.example.florra_a;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.cardview.widget.CardView;

public class AdminDashboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set fullscreen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Enable edge-to-edge
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);

        // Handle notch and status bar
        WindowInsetsControllerCompat windowInsetsController =
                WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());
        windowInsetsController.setAppearanceLightStatusBars(false);
        windowInsetsController.setAppearanceLightNavigationBars(false);

        setContentView(R.layout.activity_admin_dashboard);

        // Show toast
        Toast.makeText(this, "Admin Dashboard", Toast.LENGTH_SHORT).show();

        // Setup navigation
        setupNavigation();
    }

    private void setupNavigation() {
        // Dashboard button - Already on this screen
        Button btnDashboard = findViewById(R.id.btnDashboard);
        if (btnDashboard != null) {
            btnDashboard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Already on dashboard, just show toast
                    Toast.makeText(AdminDashboardActivity.this, "You are already on Dashboard", Toast.LENGTH_SHORT).show();
                }
            });
        }

        // Catalog button
        Button btnCatalog = findViewById(R.id.btnCatalog);
        if (btnCatalog != null) {
            btnCatalog.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // For now show toast, you can change later
                    Toast.makeText(AdminDashboardActivity.this, "Catalog screen coming soon", Toast.LENGTH_SHORT).show();
                }
            });
        }

        // Enquiries button (bottomQuotes)
        Button bottomQuotes = findViewById(R.id.bottomQuotes);
        if (bottomQuotes != null) {
            bottomQuotes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(AdminDashboardActivity.this, EnquiriesActivity.class);
                    startActivity(intent);
                }
            });
        }

        // Account button
        Button btnAccount = findViewById(R.id.btnAccount);
        if (btnAccount != null) {
            btnAccount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(AdminDashboardActivity.this, AdminAccountActivity.class);
                    startActivity(intent);
                }
            });
        }

        // Logout button
        Button btnLogout = findViewById(R.id.btnLogout);
        if (btnLogout != null) {
            btnLogout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    performLogout();
                }
            });
        }

        // ADD THIS: Enquiries card click listener
        CardView cardEnquiries = findViewById(R.id.cardEnquiries);
        if (cardEnquiries != null) {
            cardEnquiries.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(AdminDashboardActivity.this, EnquiriesActivity.class);
                    startActivity(intent);
                }
            });
        }

        // ADD THIS: Enquiries management card
        CardView cardEnquiriesMgmt = findViewById(R.id.cardEnquiriesMgmt);
        if (cardEnquiriesMgmt != null) {
            cardEnquiriesMgmt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(AdminDashboardActivity.this, EnquiriesActivity.class);
                    startActivity(intent);
                }
            });
        }

        // ADD THIS: Product card click listener
        CardView cardProduct = findViewById(R.id.cardProduct);
        if (cardProduct != null) {
            cardProduct.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(AdminDashboardActivity.this, ProductsActivity.class);
                    startActivity(intent);
                }
            });
        }

        // ADD THIS: Sales Prediction card click listener
        CardView cardSalesPrediction = findViewById(R.id.cardSalesPrediction);
        if (cardSalesPrediction != null) {
            cardSalesPrediction.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(AdminDashboardActivity.this, SalesPredictionActivity.class);
                    startActivity(intent);
                }
            });
        }

        // ADD THIS: Menu button (top left)
        ImageView btnMenu = findViewById(R.id.btnMenu);
        if (btnMenu != null) {
            btnMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(AdminDashboardActivity.this, "Menu", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void performLogout() {
        getSharedPreferences("user_prefs", MODE_PRIVATE).edit().clear().apply();
        Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}