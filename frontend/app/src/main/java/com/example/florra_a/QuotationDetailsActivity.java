package com.example.florra_a;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class QuotationDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Enable edge-to-edge
        androidx.core.view.WindowCompat.setDecorFitsSystemWindows(getWindow(), false);

        // Handle notch and status bar
        androidx.core.view.WindowInsetsControllerCompat windowInsetsController =
                androidx.core.view.WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());
        if (windowInsetsController != null) {
            windowInsetsController.setAppearanceLightStatusBars(true);
            windowInsetsController.setAppearanceLightNavigationBars(true);
        }

        setContentView(R.layout.activity_quotation_details);

        initializeViews();
        setupClickListeners();

        // Get data from intent (when clicking from Quotations list)
        getIntentData();
    }

    private void initializeViews() {
        // Views are already defined in XML
    }

    private void setupClickListeners() {
        // Back button
        findViewById(R.id.btnBack).setOnClickListener(v -> onBackPressed());

        // Help button
        findViewById(R.id.btnHelp).setOnClickListener(v -> {
            Toast.makeText(this, "Help clicked", Toast.LENGTH_SHORT).show();
        });

        // Download PDF button
        findViewById(R.id.btnDownload).setOnClickListener(v -> {
            downloadQuotationPDF();
        });

        setupBottomNavigation();
    }

    private void getIntentData() {
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("enquiry_data")) {
            com.example.florra_a.models.Enquiry enquiry = (com.example.florra_a.models.Enquiry) intent.getSerializableExtra("enquiry_data");
            if (enquiry != null) {
                populateData(enquiry);
            }
        }
    }

    private void populateData(com.example.florra_a.models.Enquiry enquiry) {
        // IDs: tvEnquiryNumber, tvDate, tvProductName, tvSize, tvDetails, tvAdminName, tvAdminRole, tvPricePerSqft, tvQuantity, tvDelivery, tvTotalEstimate, tvNote

        // Basic Info
        ((android.widget.TextView) findViewById(R.id.tvEnquiryNumber)).setText("Enquiry #" + enquiry.getId());
        if (enquiry.getCreatedAt() != null) {
            ((android.widget.TextView) findViewById(R.id.tvDate)).setText(enquiry.getCreatedAt().split("T")[0]);
        }

        // Product Info (From Inquiry Message or Product if available)
        // Parse Message for Cleaner Display
        String rawMessage = enquiry.getMessage();
        String productName = "Enquiry Product";
        String productDetails = "";
        String imageUrl = "";
        
        if (rawMessage != null) {
            String[] lines = rawMessage.split("\n");
            for (String line : lines) {
                if (line.startsWith("Product: ")) {
                    productName = line.replace("Product: ", "").trim();
                } else if (line.startsWith("Product Image: ")) {
                    imageUrl = line.replace("Product Image: ", "").trim();
                } else if (line.startsWith("Details: ")) {
                    productDetails = line.replace("Details: ", "").trim();
                }
                // Intentionally ignoring Project Type, Room Type, Total Area, Notes
            }
        }

        ((android.widget.TextView) findViewById(R.id.tvProductName)).setText(productName); 
        ((android.widget.TextView) findViewById(R.id.tvDetails)).setText(productDetails);
        ((android.widget.TextView) findViewById(R.id.tvSize)).setVisibility(View.GONE); // Hide size label to cleaner look or merge if needed

        // Load Image
        android.widget.ImageView imgProduct = findViewById(R.id.imgProduct);
        if (!imageUrl.isEmpty()) {
            if (!imageUrl.startsWith("http")) {
                imageUrl = com.example.florra_a.network.RetrofitClient.BASE_URL + imageUrl;
            }
            com.bumptech.glide.Glide.with(this)
                .load(imageUrl)
                .placeholder(R.drawable.img_sample_product) // Use sample as placeholder
                .error(R.drawable.img_sample_product)
                .centerCrop()
                .into(imgProduct);
        }
        
        // Admin Response / Quotation Details
        String price = enquiry.getQuotationPrice();
        String boxes = enquiry.getQuotationBoxes();
        String delivery = enquiry.getQuotationDeliveryTime();
        String notes = enquiry.getQuotationNotes();

        if (price != null && !price.isEmpty()) {
            ((android.widget.TextView) findViewById(R.id.tvPricePerSqft)).setText("₹" + price);
        } else {
             ((android.widget.TextView) findViewById(R.id.tvPricePerSqft)).setText("-");
        }

        if (boxes != null && !boxes.isEmpty()) {
            ((android.widget.TextView) findViewById(R.id.tvQuantity)).setText(boxes + " Boxes");
        } else {
             ((android.widget.TextView) findViewById(R.id.tvQuantity)).setText("-");
        }

        if (delivery != null && !delivery.isEmpty()) {
            ((android.widget.TextView) findViewById(R.id.tvDelivery)).setText(delivery);
        } else {
             ((android.widget.TextView) findViewById(R.id.tvDelivery)).setText("-");
        }

        if (notes != null && !notes.isEmpty()) {
             ((android.widget.TextView) findViewById(R.id.tvNote)).setText(notes);
        } else {
             ((android.widget.TextView) findViewById(R.id.tvNote)).setText("No notes provided.");
        }

        // Calculate Estimate Logic
        try {
            if (price != null && boxes != null) {
                double p = Double.parseDouble(price.replaceAll("[^\\d.]", ""));
                double b = Double.parseDouble(boxes.replaceAll("[^\\d.]", ""));
                // Assuming 1 Box = 10 sqft (Standard) - Ideally this should be dynamic
                double total = p * b * 10; 
                ((android.widget.TextView) findViewById(R.id.tvTotalEstimate)).setText(String.format("₹ %,.2f", total));
            }
        } catch (Exception e) {
             ((android.widget.TextView) findViewById(R.id.tvTotalEstimate)).setText("₹ -");
        }
    }

    private void downloadQuotationPDF() {
        // TODO: Implement PDF download functionality
        // This will be implemented when backend is ready

        Toast.makeText(this, "Downloading quotation PDF...", Toast.LENGTH_SHORT).show();

        // For now, simulate download
        new android.os.Handler().postDelayed(() -> {
            Toast.makeText(this, "PDF downloaded successfully!", Toast.LENGTH_SHORT).show();
        }, 1000);
    }

    private void setupBottomNavigation() {
        // Home button
        findViewById(R.id.btnNavHome).setOnClickListener(v -> openHomeScreen());

        // Catalog button
        findViewById(R.id.btnNavCatalog).setOnClickListener(v -> openCatalogScreen());

        // Account button
        findViewById(R.id.btnNavAccount).setOnClickListener(v -> openAccountScreen());
    }

    private void openHomeScreen() {
        startActivity(new Intent(QuotationDetailsActivity.this, CustomerHomeActivity.class));
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

    private void openCatalogScreen() {
        startActivity(new Intent(QuotationDetailsActivity.this, CatalogActivity.class));
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

    private void openAccountScreen() {
        startActivity(new Intent(QuotationDetailsActivity.this, CustomerAccountActivity.class));
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}
