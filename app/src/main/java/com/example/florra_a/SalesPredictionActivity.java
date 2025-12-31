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

public class SalesPredictionActivity extends AppCompatActivity {

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

        setContentView(R.layout.activity_sales_prediction);

        // Setup navigation
        setupNavigation();
    }

    private void setupNavigation() {
        // Back button
        ImageView btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        }

        // More options button
        ImageView btnMore = findViewById(R.id.btnMore);
        if (btnMore != null) {
            btnMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(SalesPredictionActivity.this, "More options", Toast.LENGTH_SHORT).show();
                }
            });
        }

        // Bottom navigation
        Button btnDashboard = findViewById(R.id.btnDashboard);
        Button btnCatalog = findViewById(R.id.btnCatalog);
        Button btnQuotes = findViewById(R.id.bottomQuotes);
        Button btnAccount = findViewById(R.id.btnAccount);

        if (btnDashboard != null) {
            btnDashboard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(SalesPredictionActivity.this, AdminDashboardActivity.class);
                    startActivity(intent);
                    finish();
                }
            });
        }

        if (btnCatalog != null) {
            btnCatalog.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(SalesPredictionActivity.this, "Catalog screen coming soon", Toast.LENGTH_SHORT).show();
                }
            });
        }

        if (btnQuotes != null) {
            btnQuotes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(SalesPredictionActivity.this, EnquiriesActivity.class);
                    startActivity(intent);
                    finish();
                }
            });
        }

        if (btnAccount != null) {
            btnAccount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(SalesPredictionActivity.this, AdminAccountActivity.class);
                    startActivity(intent);
                    finish();
                }
            });
        }

        // Create PO button
        Button btnCreatePO = findViewById(R.id.btnCreatePO);
        if (btnCreatePO != null) {
            btnCreatePO.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(SalesPredictionActivity.this, "Purchase Order created", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}