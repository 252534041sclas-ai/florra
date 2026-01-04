package com.example.florra_a;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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
    private Button btnViewBill1, btnViewBill2, btnShare1, btnShare2;

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

        // View Bill buttons
        btnViewBill1 = findViewById(R.id.btnViewBill1);
        btnViewBill2 = findViewById(R.id.btnViewBill2);
        btnShare1 = findViewById(R.id.btnShare1);
        btnShare2 = findViewById(R.id.btnShare2);
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

        // View Bill buttons
        btnViewBill1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open ViewBillActivity for Bill 1
                Intent intent = new Intent(SavedBillsActivity.this, ViewBillActivity.class);

                // You can pass bill data as extras if needed
                intent.putExtra("billNumber", "INV-2023-001");
                intent.putExtra("customerName", "John Doe");
                intent.putExtra("amount", "$2,450.00");
                intent.putExtra("date", "Oct 24, 2023");
                intent.putExtra("status", "Paid");

                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

        btnViewBill2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open ViewBillActivity for Bill 2
                Intent intent = new Intent(SavedBillsActivity.this, ViewBillActivity.class);

                // You can pass bill data as extras if needed
                intent.putExtra("billNumber", "INV-2023-002");
                intent.putExtra("customerName", "Sarah Smith");
                intent.putExtra("amount", "$890.00");
                intent.putExtra("date", "Oct 23, 2023");
                intent.putExtra("status", "Unpaid");

                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

        // Share buttons
        btnShare1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareBill("Bill #1089", "John Doe", "$2,450.00");
            }
        });

        btnShare2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareBill("Bill #1088", "Sarah Smith", "$890.00");
            }
        });
    }

    private void selectChip(CardView selectedChip) {
        // Reset all chips to white
        chipAll.setCardBackgroundColor(getResources().getColor(R.color.white));
        chipPaid.setCardBackgroundColor(getResources().getColor(R.color.white));
        chipUnpaid.setCardBackgroundColor(getResources().getColor(R.color.white));
        chipCancelled.setCardBackgroundColor(getResources().getColor(R.color.white));
        chipThisMonth.setCardBackgroundColor(getResources().getColor(R.color.white));

        // Change text colors back to black for unselected chips
        ((android.widget.TextView) chipAll.getChildAt(0)).setTextColor(getResources().getColor(R.color.black));
        ((android.widget.TextView) chipPaid.getChildAt(0)).setTextColor(getResources().getColor(R.color.black));
        ((android.widget.TextView) chipUnpaid.getChildAt(0)).setTextColor(getResources().getColor(R.color.black));
        ((android.widget.TextView) chipCancelled.getChildAt(0)).setTextColor(getResources().getColor(R.color.black));
        ((android.widget.TextView) chipThisMonth.getChildAt(0)).setTextColor(getResources().getColor(R.color.black));

        // Set selected chip
        if (selectedChip != null) {
            selectedChip.setCardBackgroundColor(getResources().getColor(R.color.primary_color));
            // Change text color to white for selected chip
            ((android.widget.TextView) selectedChip.getChildAt(0)).setTextColor(getResources().getColor(R.color.white));
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

    private void shareBill(String billNumber, String customerName, String amount) {
        // Create share intent
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");

        String shareText = "Bill Details:\n" +
                "Bill Number: " + billNumber + "\n" +
                "Customer: " + customerName + "\n" +
                "Amount: " + amount + "\n\n" +
                "Shared from Florra Tiles App";

        shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Bill Details: " + billNumber);

        // Start the share activity
        startActivity(Intent.createChooser(shareIntent, "Share Bill Details"));

        Toast.makeText(this, "Sharing bill details...", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}