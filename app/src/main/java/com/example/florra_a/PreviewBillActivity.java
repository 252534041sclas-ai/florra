package com.example.florra_a;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import android.widget.LinearLayout;

public class PreviewBillActivity extends AppCompatActivity {

    // TextViews for bill details
    private TextView tvBillNo, tvDate;
    private TextView tvCustomerName, tvCustomerPhone, tvCustomerAddress;
    private TextView tvItem1Name, tvItem1Details, tvItem1Qty, tvItem1Rate, tvItem1Amount;
    private TextView tvItem2Name, tvItem2Details, tvItem2Qty, tvItem2Rate, tvItem2Amount;
    private TextView tvItem3Name, tvItem3Details, tvItem3Qty, tvItem3Rate, tvItem3Amount;
    private TextView tvSubtotal, tvTaxAmount, tvDiscountAmount, tvGrandTotal;
    private TextView tvPreviewTime;

    // Buttons
    private Button btnShareDownload;
    private LinearLayout btnBack;  // Changed from Button to LinearLayout

    // LinearLayouts for table rows
    private LinearLayout tableRow1, tableRow2, tableRow3;

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

        setContentView(R.layout.activity_preview_bill);

        // Initialize views
        initializeViews();

        // Set up listeners
        setupListeners();

        // Set data from intent (if coming from GenerateBillActivity)
        setBillData();
    }

    private void initializeViews() {
        // Initialize all TextViews
        tvBillNo = findViewById(R.id.tvBillNo);
        tvDate = findViewById(R.id.tvDate);

        tvCustomerName = findViewById(R.id.tvCustomerName);
        tvCustomerPhone = findViewById(R.id.tvCustomerPhone);
        tvCustomerAddress = findViewById(R.id.tvCustomerAddress);

        // Item 1
        tvItem1Name = findViewById(R.id.tvItem1Name);
        tvItem1Details = findViewById(R.id.tvItem1Details);
        tvItem1Qty = findViewById(R.id.tvItem1Qty);
        tvItem1Rate = findViewById(R.id.tvItem1Rate);
        tvItem1Amount = findViewById(R.id.tvItem1Amount);

        // Item 2
        tvItem2Name = findViewById(R.id.tvItem2Name);
        tvItem2Details = findViewById(R.id.tvItem2Details);
        tvItem2Qty = findViewById(R.id.tvItem2Qty);
        tvItem2Rate = findViewById(R.id.tvItem2Rate);
        tvItem2Amount = findViewById(R.id.tvItem2Amount);

        // Item 3
        tvItem3Name = findViewById(R.id.tvItem3Name);
        tvItem3Details = findViewById(R.id.tvItem3Details);
        tvItem3Qty = findViewById(R.id.tvItem3Qty);
        tvItem3Rate = findViewById(R.id.tvItem3Rate);
        tvItem3Amount = findViewById(R.id.tvItem3Amount);

        // Amounts
        tvSubtotal = findViewById(R.id.tvSubtotal);
        tvTaxAmount = findViewById(R.id.tvTaxAmount);
        tvDiscountAmount = findViewById(R.id.tvDiscountAmount);
        tvGrandTotal = findViewById(R.id.tvGrandTotal);

        tvPreviewTime = findViewById(R.id.tvPreviewTime);

        // Buttons - NOTE: btnBack is LinearLayout, not Button
        btnBack = findViewById(R.id.btnBack);  // This is LinearLayout in XML
        btnShareDownload = findViewById(R.id.btnShareDownload);

        // Initialize table rows
        tableRow1 = findViewById(R.id.tableRow1);
        tableRow2 = findViewById(R.id.tableRow2);
        tableRow3 = findViewById(R.id.tableRow3);
    }

    private void setupListeners() {
        // Back button (LinearLayout with click listener)
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // Share/Download button
        btnShareDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Implement share/download functionality
                // For now, just show a toast
                android.widget.Toast.makeText(PreviewBillActivity.this,
                        "Share/Download functionality coming soon",
                        android.widget.Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setBillData() {
        // Set default values (these should come from intent in real scenario)
        tvBillNo.setText("#INV-2024-001");
        tvDate.setText("Oct 24, 2023");

        tvCustomerName.setText("Mr. Rajesh Kumar");
        tvCustomerPhone.setText("+91 98765 43210");
        tvCustomerAddress.setText("45 Green Park, Near Central Mall, Metro City, 560001");

        // Item 1
        tvItem1Name.setText("Carrara Marble Glossy");
        tvItem1Details.setText("600x600mm • Vitrified");
        tvItem1Qty.setText("10 box");
        tvItem1Rate.setText("$12.00");
        tvItem1Amount.setText("$120.00");

        // Item 2
        tvItem2Name.setText("Rustic Wood Plank");
        tvItem2Details.setText("200x1000mm • Ceramic");
        tvItem2Qty.setText("5 box");
        tvItem2Rate.setText("$9.00");
        tvItem2Amount.setText("$45.00");

        // Item 3
        tvItem3Name.setText("Adhesive Type-2");
        tvItem3Details.setText("20kg Bag");
        tvItem3Qty.setText("2 bag");
        tvItem3Rate.setText("$8.00");
        tvItem3Amount.setText("$16.00");

        // Amounts
        tvSubtotal.setText("$181.00");
        tvTaxAmount.setText("$18.10");
        tvDiscountAmount.setText("-$5.00");
        tvGrandTotal.setText("$194.10");

        tvPreviewTime.setText("Preview generated on Oct 24, 2023 at 10:45 AM");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}