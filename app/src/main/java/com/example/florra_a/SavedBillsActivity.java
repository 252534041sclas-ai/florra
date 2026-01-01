package com.example.florra_a;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsControllerCompat;

public class SavedBillsActivity extends AppCompatActivity {

    // UI Components
    private ImageButton btnBack, btnFilter;
    private EditText etSearch;
    private CardView chipAll, chipPaid, chipUnpaid, chipCancelled, chipThisMonth;

    // REMOVE BOTTOM NAVIGATION VARIABLES
    // private Button btnDashboard, btnCatalog, bottomQuotes, btnAccount;

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

        setContentView(R.layout.activity_saved_bills);

        // Initialize UI components
        initializeViews();

        // Setup click listeners
        setupClickListeners();

        // REMOVE THIS LINE - No bottom nav in XML
        // setupBottomNavigation();
    }

    private void initializeViews() {
        // Header buttons
        btnBack = findViewById(R.id.btnBack);
        btnFilter = findViewById(R.id.btnFilter);

        // Search
        etSearch = findViewById(R.id.etSearch);

        // Filter chips
        chipAll = findViewById(R.id.chipAll);
        chipPaid = findViewById(R.id.chipPaid);
        chipUnpaid = findViewById(R.id.chipUnpaid);
        chipCancelled = findViewById(R.id.chipCancelled);
        chipThisMonth = findViewById(R.id.chipThisMonth);

        // REMOVE BOTTOM NAVIGATION FINDVIEWBYID
        // btnDashboard = findViewById(R.id.btnDashboard);
        // btnCatalog = findViewById(R.id.btnCatalog);
        // bottomQuotes = findViewById(R.id.bottomQuotes);
        // btnAccount = findViewById(R.id.btnAccount);
    }

    private void setupClickListeners() {
        // Back button
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // Filter button
        btnFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFilterOptions();
            }
        });

        // Filter chips
        chipAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectChip(chipAll);
                Toast.makeText(SavedBillsActivity.this, "Showing all bills", Toast.LENGTH_SHORT).show();
            }
        });

        chipPaid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectChip(chipPaid);
                Toast.makeText(SavedBillsActivity.this, "Showing paid bills", Toast.LENGTH_SHORT).show();
            }
        });

        chipUnpaid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectChip(chipUnpaid);
                Toast.makeText(SavedBillsActivity.this, "Showing unpaid bills", Toast.LENGTH_SHORT).show();
            }
        });

        chipCancelled.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectChip(chipCancelled);
                Toast.makeText(SavedBillsActivity.this, "Showing cancelled bills", Toast.LENGTH_SHORT).show();
            }
        });

        chipThisMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectChip(chipThisMonth);
                Toast.makeText(SavedBillsActivity.this, "Showing this month's bills", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // REMOVE ENTIRE setupBottomNavigation() METHOD
    /*
    private void setupBottomNavigation() {
        // Dashboard
        btnDashboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SavedBillsActivity.this, AdminDashboardActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
            }
        });

        // Catalog
        btnCatalog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(SavedBillsActivity.this, "Catalog screen coming soon", Toast.LENGTH_SHORT).show();
            }
        });

        // Enquiries
        bottomQuotes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SavedBillsActivity.this, EnquiriesActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
            }
        });

        // Account
        btnAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SavedBillsActivity.this, AdminAccountActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
            }
        });
    }
    */

    private void selectChip(CardView selectedChip) {
        if (chipAll != null) chipAll.setCardBackgroundColor(getResources().getColor(R.color.white));
        if (chipPaid != null) chipPaid.setCardBackgroundColor(getResources().getColor(R.color.white));
        if (chipUnpaid != null) chipUnpaid.setCardBackgroundColor(getResources().getColor(R.color.white));
        if (chipCancelled != null) chipCancelled.setCardBackgroundColor(getResources().getColor(R.color.white));
        if (chipThisMonth != null) chipThisMonth.setCardBackgroundColor(getResources().getColor(R.color.white));

        // Set selected chip
        if (selectedChip != null) {
            selectedChip.setCardBackgroundColor(getResources().getColor(R.color.primary_color));
        }
    }

    private void showFilterOptions() {
        // Create filter options dialog
        String[] filterOptions = {
                "Sort by Date (Newest First)",
                "Sort by Date (Oldest First)",
                "Sort by Amount (High to Low)",
                "Sort by Amount (Low to High)",
                "Sort by Customer Name"
        };

        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setTitle("Sort Bills")
                .setItems(filterOptions, (dialog, which) -> {
                    switch (which) {
                        case 0:
                            Toast.makeText(SavedBillsActivity.this, "Sorted by newest first", Toast.LENGTH_SHORT).show();
                            break;
                        case 1:
                            Toast.makeText(SavedBillsActivity.this, "Sorted by oldest first", Toast.LENGTH_SHORT).show();
                            break;
                        case 2:
                            Toast.makeText(SavedBillsActivity.this, "Sorted by amount (high to low)", Toast.LENGTH_SHORT).show();
                            break;
                        case 3:
                            Toast.makeText(SavedBillsActivity.this, "Sorted by amount (low to high)", Toast.LENGTH_SHORT).show();
                            break;
                        case 4:
                            Toast.makeText(SavedBillsActivity.this, "Sorted by customer name", Toast.LENGTH_SHORT).show();
                            break;
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}