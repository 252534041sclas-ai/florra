package com.example.florra_a;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsControllerCompat;

public class GenerateBillActivity extends AppCompatActivity {

    // TextViews
    private TextView tvCustomerName, tvCustomerPhone, tvCustomerAddress;
    private TextView tvBillNo, tvDate;
    private TextView tvItem1Price, tvItem2Price;
    private TextView tvSubtotal, tvGSTAmount, tvDiscountAmount, tvLoadingAmount, tvGrandTotal;

    // EditTexts
    private EditText etGST, etDiscount, etLoading;

    // Buttons
    private Button btnBack, btnAddItem, btnPreview, btnSaveBill;

    // Calculation variables
    private double subtotal = 47000.0;
    private double gstPercentage = 18.0;
    private double discount = 1000.0;
    private double loading = 500.0;

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

        setContentView(R.layout.activity_generate_bill);

        // Initialize views
        initializeViews();

        // Set up listeners
        setupListeners();

        // Calculate initial totals
        calculateTotals();
    }

    private void initializeViews() {
        // Customer details
        tvCustomerName = findViewById(R.id.tvCustomerName);
        tvCustomerPhone = findViewById(R.id.tvCustomerPhone);
        tvCustomerAddress = findViewById(R.id.tvCustomerAddress);

        // Bill info
        tvBillNo = findViewById(R.id.tvBillNo);
        tvDate = findViewById(R.id.tvDate);

        // Item prices
        tvItem1Price = findViewById(R.id.tvItem1Price);
        tvItem2Price = findViewById(R.id.tvItem2Price);

        // Payment summary
        tvSubtotal = findViewById(R.id.tvSubtotal);
        tvGSTAmount = findViewById(R.id.tvGSTAmount);
        tvDiscountAmount = findViewById(R.id.tvDiscountAmount);
        tvLoadingAmount = findViewById(R.id.tvLoadingAmount);
        tvGrandTotal = findViewById(R.id.tvGrandTotal);

        // Input fields
        etGST = findViewById(R.id.etGST);
        etDiscount = findViewById(R.id.etDiscount);
        etLoading = findViewById(R.id.etLoading);

        // Buttons
        btnBack = findViewById(R.id.btnBack);
        btnAddItem = findViewById(R.id.btnAddItem);
        btnPreview = findViewById(R.id.btnPreview);
        btnSaveBill = findViewById(R.id.btnSaveBill);

        // Set initial values
        tvCustomerName.setText("Rahul Sharma");
        tvCustomerPhone.setText("+91 98765 43210");
        tvCustomerAddress.setText("12, Green Park Avenue, Delhi");
        tvBillNo.setText("#FL-2023-89");
        tvDate.setText("24 Oct, 2023");
        tvItem1Price.setText("₹42,500");
        tvItem2Price.setText("₹4,500");
    }

    private void setupListeners() {
        // Back button
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // Add Item button
        btnAddItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(GenerateBillActivity.this, "Add Item functionality coming soon", Toast.LENGTH_SHORT).show();
            }
        });

        // Preview button
        btnPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(GenerateBillActivity.this, "Preview functionality coming soon", Toast.LENGTH_SHORT).show();
            }
        });

        // Save Bill button
        btnSaveBill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveBill();
            }
        });

        // Preview button
        btnPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GenerateBillActivity.this, PreviewBillActivity.class);
                // You can pass data from current activity to preview activity
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

        // GST input listener
        etGST.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    try {
                        gstPercentage = Double.parseDouble(s.toString());
                        calculateTotals();
                    } catch (NumberFormatException e) {
                        gstPercentage = 0.0;
                    }
                } else {
                    gstPercentage = 0.0;
                }
                calculateTotals();
            }
        });

        // Discount input listener
        etDiscount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    try {
                        discount = Double.parseDouble(s.toString());
                        calculateTotals();
                    } catch (NumberFormatException e) {
                        discount = 0.0;
                    }
                } else {
                    discount = 0.0;
                }
                calculateTotals();
            }
        });

        // Loading input listener
        etLoading.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    try {
                        loading = Double.parseDouble(s.toString());
                        calculateTotals();
                    } catch (NumberFormatException e) {
                        loading = 0.0;
                    }
                } else {
                    loading = 0.0;
                }
                calculateTotals();
            }
        });
    }

    private void calculateTotals() {
        // Calculate GST amount
        double gstAmount = (subtotal * gstPercentage) / 100;

        // Calculate grand total
        double grandTotal = subtotal + gstAmount - discount + loading;

        // Update UI with formatted amounts
        tvSubtotal.setText(formatCurrency(subtotal));
        tvGSTAmount.setText("+ " + formatCurrency(gstAmount));
        tvDiscountAmount.setText("- " + formatCurrency(discount));
        tvLoadingAmount.setText("+ " + formatCurrency(loading));
        tvGrandTotal.setText(formatCurrency(grandTotal));
    }

    private String formatCurrency(double amount) {
        // Format currency with Indian Rupee symbol and commas
        return "₹" + String.format("%,.0f", amount);
    }

    private void saveBill() {
        // Show save confirmation
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Save Bill")
                .setMessage("Are you sure you want to save this bill?")
                .setPositiveButton("Save", (dialog, which) -> {
                    // Save logic will be implemented later with backend
                    Toast.makeText(GenerateBillActivity.this, "Bill saved successfully!", Toast.LENGTH_SHORT).show();

                    // You can navigate back to dashboard or clear form
                    // finish();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    public void onBackPressed() {
        // Check if any changes were made
        if (gstPercentage != 18.0 || discount != 1000.0 || loading != 500.0) {
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
            builder.setTitle("Unsaved Changes")
                    .setMessage("You have unsaved changes. Do you want to discard them?")
                    .setPositiveButton("Discard", (dialog, which) -> {
                        super.onBackPressed();
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        } else {
            super.onBackPressed();
        }
    }
}