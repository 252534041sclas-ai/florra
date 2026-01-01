package com.example.florra_a;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.appcompat.app.AlertDialog;

public class AdminProductDetailsActivity extends AppCompatActivity {

    // UI Components
    private ImageView btnBack, btnMore;
    private TextView tvViewHistory, tvProductName, tvProductSku, tvStockStatus;
    private TextView tvPrice, tvRetailPrice, tvMargin, tvStockQuantity;
    private TextView tvWarehouse, tvDimensions, tvFinish, tvThickness, tvCoverage;
    private TextView tvDescription;
    private Button btnEditDetails;

    // Product data variables
    private String currentProductName = "";
    private String currentProductSku = "";
    private String currentProductCategory = "";
    private String currentProductSize = "";
    private String currentPrice = "";
    private String currentStock = "";
    private String currentStockStatus = "";
    private String currentWarehouse = "";
    private String currentDimensions = "";
    private String currentFinish = "";
    private String currentThickness = "";
    private String currentCoverage = "";
    private String currentDescription = "";
    private String currentMargin = "";
    private String currentRetailPrice = "";

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

        setContentView(R.layout.activity_adminproduct_details);

        // Initialize views
        initViews();

        // Load product data
        loadProductData();

        // Setup click listeners
        setupClickListeners();

        Toast.makeText(this, "Product Details", Toast.LENGTH_SHORT).show();
    }

    private void initViews() {
        // Header
        btnBack = findViewById(R.id.btnBack);
        btnMore = findViewById(R.id.btnMore);

        // Product Info
        tvProductName = findViewById(R.id.tvProductName);
        tvProductSku = findViewById(R.id.tvProductSku);
        tvStockStatus = findViewById(R.id.tvStockStatus);

        // Price Section
        tvViewHistory = findViewById(R.id.tvViewHistory);
        tvPrice = findViewById(R.id.tvPrice);
        tvRetailPrice = findViewById(R.id.tvRetailPrice);
        tvMargin = findViewById(R.id.tvMargin);

        // Stock & Warehouse
        tvStockQuantity = findViewById(R.id.tvStockQuantity);
        tvWarehouse = findViewById(R.id.tvWarehouse);

        // Specifications
        tvDimensions = findViewById(R.id.tvDimensions);
        tvFinish = findViewById(R.id.tvFinish);
        tvThickness = findViewById(R.id.tvThickness);
        tvCoverage = findViewById(R.id.tvCoverage);

        // Description
        tvDescription = findViewById(R.id.tvDescription);

        // Buttons
        btnEditDetails = findViewById(R.id.btnEditDetails);
    }

    private void setupClickListeners() {
        // Back button
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // More options button
        btnMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMoreOptionsMenu();
            }
        });

        // View History
        tvViewHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPriceHistory();
            }
        });

        // Edit Details button
        btnEditDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editProductDetails();
            }
        });

        // Image click listeners
        ImageView ivMainView = findViewById(R.id.ivMainView);
        if (ivMainView != null) {
            ivMainView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(AdminProductDetailsActivity.this, "Viewing Main Image", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void loadProductData() {
        // Get data from intent (when coming from Products screen)
        Intent intent = getIntent();
        if (intent != null) {
            // Store data in variables
            currentProductName = intent.getStringExtra("product_name");
            currentProductSku = intent.getStringExtra("product_sku");
            currentProductCategory = intent.getStringExtra("product_category");
            currentProductSize = intent.getStringExtra("product_size");
            currentPrice = intent.getStringExtra("product_price");
            currentStock = intent.getStringExtra("product_stock");
            currentStockStatus = intent.getStringExtra("product_status");
            currentWarehouse = intent.getStringExtra("product_warehouse");
            currentDimensions = intent.getStringExtra("product_dimensions");
            currentFinish = intent.getStringExtra("product_finish");
            currentThickness = intent.getStringExtra("product_thickness");
            currentCoverage = intent.getStringExtra("product_coverage");
            currentDescription = intent.getStringExtra("product_description");
            currentMargin = intent.getStringExtra("product_margin");
            currentRetailPrice = intent.getStringExtra("product_retail_price");

            // Update UI with data
            updateUIWithProductData();
        } else {
            // Default data if no intent (for testing)
            setDefaultProductData();
            updateUIWithProductData();
        }
    }

    private void updateUIWithProductData() {
        // Product Name and SKU
        if (currentProductName != null && !currentProductName.isEmpty()) {
            tvProductName.setText(currentProductName);
        }

        if (currentProductSku != null && currentProductCategory != null) {
            String skuText = "SKU: " + currentProductSku + " • " + currentProductCategory;
            tvProductSku.setText(skuText);
        }

        // Price
        if (currentPrice != null && !currentPrice.isEmpty()) {
            // Convert ₹ to $ if needed
            String displayPrice = currentPrice;
            if (currentPrice.contains("₹")) {
                displayPrice = currentPrice.replace("₹", "$");
            }
            tvPrice.setText(displayPrice);
        }

        // Retail Price and Margin
        if (currentRetailPrice != null && !currentRetailPrice.isEmpty()) {
            String displayRetailPrice = currentRetailPrice;
            if (currentRetailPrice.contains("₹")) {
                displayRetailPrice = currentRetailPrice.replace("₹", "$");
            }
            tvRetailPrice.setText("Retail Price: " + displayRetailPrice);
        }

        if (currentMargin != null && !currentMargin.isEmpty()) {
            tvMargin.setText(currentMargin + " Margin");
        }

        // Stock
        if (currentStock != null && !currentStock.isEmpty()) {
            tvStockQuantity.setText(currentStock);
        }

        if (currentStockStatus != null && !currentStockStatus.isEmpty()) {
            tvStockStatus.setText(currentStockStatus);
            // Update stock status background based on status
            updateStockStatusUI();
        }

        // Warehouse
        if (currentWarehouse != null && !currentWarehouse.isEmpty()) {
            tvWarehouse.setText(currentWarehouse);
        }

        // Specifications
        if (currentDimensions != null && !currentDimensions.isEmpty()) {
            tvDimensions.setText(currentDimensions);
        }

        if (currentFinish != null && !currentFinish.isEmpty()) {
            tvFinish.setText(currentFinish);
        }

        if (currentThickness != null && !currentThickness.isEmpty()) {
            tvThickness.setText(currentThickness);
        }

        if (currentCoverage != null && !currentCoverage.isEmpty()) {
            tvCoverage.setText(currentCoverage);
        }

        // Description
        if (currentDescription != null && !currentDescription.isEmpty()) {
            tvDescription.setText(currentDescription);
        }
    }

    private void updateStockStatusUI() {
        if (currentStockStatus == null) return;

        switch (currentStockStatus) {
            case "In Stock":
                tvStockStatus.setBackgroundResource(R.drawable.bg_stock_in);
                tvStockStatus.setTextColor(getResources().getColor(R.color.emerald_700));
                break;
            case "Low Stock":
                tvStockStatus.setBackgroundResource(R.drawable.bg_stock_low);
                tvStockStatus.setTextColor(getResources().getColor(R.color.amber_700));
                break;
            case "Out of Stock":
                tvStockStatus.setBackgroundResource(R.drawable.bg_stock_out);
                tvStockStatus.setTextColor(getResources().getColor(R.color.zinc_500));
                break;
            default:
                tvStockStatus.setBackgroundResource(R.drawable.bg_stock_in);
                tvStockStatus.setTextColor(getResources().getColor(R.color.emerald_700));
        }
    }

    private void setDefaultProductData() {
        // Set default data (Carrara White Marble from your design)
        currentProductName = "Carrara White Marble";
        currentProductSku = "FL-8821";
        currentProductCategory = "Porcelain";
        currentProductSize = "600×1200mm";
        currentPrice = "$45.00";
        currentStock = "120";
        currentStockStatus = "In Stock";
        currentWarehouse = "Zone A";
        currentDimensions = "600 x 1200 mm";
        currentFinish = "High Gloss";
        currentThickness = "9 mm";
        currentCoverage = "1.44 sq. mt / box";
        currentDescription = "The Carrara White Marble porcelain tile offers the timeless elegance of natural marble with the durability of porcelain. Featuring distinctive grey veining against a luminous white background, this tile brings sophistication to any interior space. Suitable for both residential and commercial high-traffic areas.";
        currentMargin = "18%";
        currentRetailPrice = "$55.00";
    }

    private void showMoreOptionsMenu() {
        // Create options menu
        String[] options = {"Share Product", "Duplicate Product", "Print Barcode", "Delete Product", "View Analytics"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Product Options")
                .setItems(options, (dialog, which) -> {
                    switch (which) {
                        case 0:
                            shareProduct();
                            break;
                        case 1:
                            duplicateProduct();
                            break;
                        case 2:
                            printBarcode();
                            break;
                        case 3:
                            deleteProduct();
                            break;
                        case 4:
                            viewAnalytics();
                            break;
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showPriceHistory() {
        Toast.makeText(this, "Opening Price History for " + currentProductName, Toast.LENGTH_SHORT).show();
        // Here you would open a price history screen or dialog
    }

    private void editProductDetails() {
        Toast.makeText(this, "Editing " + currentProductName, Toast.LENGTH_SHORT).show();

        // Open Edit Product Details Activity
        Intent intent = new Intent(AdminProductDetailsActivity.this, EditProductDetailsActivity.class);
        intent.putExtra("product_name", currentProductName);
        startActivityForResult(intent, 100);
    }

    private void shareProduct() {
        Toast.makeText(this, "Sharing " + currentProductName, Toast.LENGTH_SHORT).show();

        // Create share intent
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        String shareText = "Check out this product: " + currentProductName +
                "\nSKU: " + currentProductSku +
                "\nPrice: " + currentPrice +
                "\nStock: " + currentStock +
                "\n\nDescription: " + currentDescription;
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Product Details: " + currentProductName);

        // Start share activity
        startActivity(Intent.createChooser(shareIntent, "Share Product"));
    }

    private void duplicateProduct() {
        Toast.makeText(this, "Duplicating " + currentProductName, Toast.LENGTH_SHORT).show();
        // Implementation for duplicating product
    }

    private void printBarcode() {
        Toast.makeText(this, "Printing barcode for " + currentProductSku, Toast.LENGTH_SHORT).show();
        // Implementation for printing barcode
    }

    private void deleteProduct() {
        // Show confirmation dialog before deleting
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Product")
                .setMessage("Are you sure you want to delete " + currentProductName + "? This action cannot be undone.")
                .setPositiveButton("Delete", (dialog, which) -> {
                    // Delete product logic here
                    Toast.makeText(AdminProductDetailsActivity.this, currentProductName + " deleted", Toast.LENGTH_SHORT).show();
                    finish(); // Go back to products list
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void viewAnalytics() {
        Toast.makeText(this, "Viewing analytics for " + currentProductName, Toast.LENGTH_SHORT).show();
        // Implementation for viewing analytics
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            if (data.getBooleanExtra("product_deleted", false)) {
                Toast.makeText(this, "Product deleted", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                String updatedName = data.getStringExtra("updated_product_name");
                if (updatedName != null) {
                    currentProductName = updatedName;
                    tvProductName.setText(updatedName);
                    Toast.makeText(this, "Product updated successfully", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // Optional: Add slide animation
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}