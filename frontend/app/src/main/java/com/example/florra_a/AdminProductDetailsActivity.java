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
    private int currentProductId = -1; // New
    private String currentProductName = "";
    private String currentProductSku = ""; // Restored
    private String currentProductTileNo = ""; // New
    private String currentProductBrand = ""; // New
    private String currentProductCategory = "";
    private String currentProductSize = "";
    private String currentPrice = "";
    private String currentStock = "";
    private String currentStockStatus = "";
    private String currentProductColor = ""; // New
    private String currentWarehouse = "";
    private String currentDimensions = "";
    private String currentFinish = "";
    private String currentThickness = "";
    private String currentCoverage = "";
    private String currentDescription = "";
    private String currentMargin = "";
    private String currentRetailPrice = "";
    private String currentProductImage = "";
    private boolean currentIsActive = true; // New

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set fullscreen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
                // Set status bar to white with dark icons
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(android.view.View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(android.graphics.Color.WHITE);
        }

        // Enable edge-to-edge
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);

        // Handle notch and status bar
        WindowInsetsControllerCompat windowInsetsController =
                WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());
        windowInsetsController.setAppearanceLightStatusBars(true);
        windowInsetsController.setAppearanceLightNavigationBars(true);

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
            // Store data in variables
            currentProductId = intent.getIntExtra("product_id", -1);
            currentProductName = intent.getStringExtra("product_name");
            currentProductTileNo = intent.getStringExtra("product_tile_no"); // New
            currentProductSku = intent.getStringExtra("product_sku"); // Keep existing if used, though adapter doesn't send it yet (adapter sends tileName as name)
            if (currentProductTileNo != null) currentProductSku = currentProductTileNo; // Use Tile No as SKU if available
            
            currentProductBrand = intent.getStringExtra("product_brand");
            currentProductCategory = intent.getStringExtra("product_category");
            currentProductSize = intent.getStringExtra("product_size");
            currentFinish = intent.getStringExtra("product_finish");
            currentProductColor = intent.getStringExtra("product_color");
            currentPrice = intent.getStringExtra("product_price");
            currentStock = intent.getStringExtra("product_stock");
            currentStockStatus = intent.getStringExtra("product_status"); // Changed key to match adapter
            
            currentWarehouse = intent.getStringExtra("product_warehouse");
            currentDimensions = intent.getStringExtra("product_dimensions");
            
            currentThickness = intent.getStringExtra("product_thickness");
            currentCoverage = intent.getStringExtra("product_coverage");
            currentDescription = intent.getStringExtra("product_description");
            currentMargin = intent.getStringExtra("product_margin");
            currentRetailPrice = intent.getStringExtra("product_retail_price");
            currentProductImage = intent.getStringExtra("product_image");
            currentIsActive = intent.getBooleanExtra("product_is_active", true);

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

        if (currentProductSku != null && !currentProductSku.isEmpty()) {
            String skuText = "No: " + currentProductSku;
            if (currentProductCategory != null && !currentProductCategory.isEmpty()) {
                skuText += " • " + currentProductCategory;
            }
            tvProductSku.setText(skuText);
        } else if (currentProductCategory != null && !currentProductCategory.isEmpty()) {
            tvProductSku.setText(currentProductCategory);
        } else {
            tvProductSku.setText("No: --");
        }

        // Price
        if (currentPrice != null && !currentPrice.isEmpty()) {
            String displayPrice = currentPrice;
            if (currentPrice.contains("$")) {
                displayPrice = currentPrice.replace("$", "₹");
            } else if (!currentPrice.contains("₹")) {
                displayPrice = "₹" + currentPrice;
            }
            tvPrice.setText(displayPrice);
        }

        // Retail Price and Margin
        if (currentRetailPrice != null && !currentRetailPrice.isEmpty()) {
            String displayRetailPrice = currentRetailPrice;
            if (currentRetailPrice.contains("$")) {
                displayRetailPrice = currentRetailPrice.replace("$", "₹");
            } else if (!currentRetailPrice.contains("₹")) {
                displayRetailPrice = "₹" + currentRetailPrice;
            }
            tvRetailPrice.setText("Retail Price: " + displayRetailPrice);
        }

        if (currentMargin != null && !currentMargin.isEmpty()) {
            tvMargin.setText(currentMargin + " Margin");
        }

        // Stock
        if (currentStock != null && !currentStock.isEmpty()) {
            tvStockQuantity.setText(currentStock);
            try {
                int stockNum = Integer.parseInt(currentStock);
                if (stockNum <= 0) {
                    currentStockStatus = "Out of Stock";
                } else if (stockNum < 10) {
                    currentStockStatus = "Low Stock";
                } else {
                    currentStockStatus = "In Stock";
                }
            } catch (Exception e) {
                // Keep existing status if not parseable
            }
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

        // Load Image
        if (currentProductImage != null && !currentProductImage.isEmpty()) {
            String imageUrl = currentProductImage;
            
             // If URL is relative (e.g. /media/products/img.jpg), prepend base URL
             if (!imageUrl.startsWith("http")) {
                 if (imageUrl.startsWith("/")) {
                     imageUrl = imageUrl.substring(1);
                 }
                 imageUrl = com.example.florra_a.network.RetrofitClient.BASE_URL + imageUrl;
             }
             else {
                 String baseHost = com.example.florra_a.network.RetrofitClient.BASE_URL
                         .replace("http://", "")
                         .replace("https://", "")
                         .split(":")[0];
                 imageUrl = imageUrl.replace("127.0.0.1", baseHost)
                                    .replace("localhost", baseHost);
             }

            ImageView ivMainView = findViewById(R.id.ivMainView);
            com.bumptech.glide.Glide.with(this)
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_tile_placeholder)
                    .error(R.drawable.ic_tile_placeholder)
                    .into(ivMainView);
        }
    }

    private void updateStockStatusUI() {
        if (currentStockStatus == null) return;

        if (!currentIsActive) {
            tvStockStatus.setText("FROZEN");
            tvStockStatus.setBackgroundResource(R.drawable.bg_zinc_badge);
            tvStockStatus.setTextColor(getResources().getColor(R.color.zinc_600));
            // Hide the green dot or change it
            View greenDot = ((View)tvStockStatus.getParent()).findViewById(R.id.viewStockDot);
            if (greenDot != null) greenDot.setVisibility(View.GONE);
            return;
        }

        // Restore dot if it was hidden
        View greenDot = ((View)tvStockStatus.getParent()).findViewById(R.id.viewStockDot);
        if (greenDot != null) greenDot.setVisibility(View.VISIBLE);

        if ("In Stock".equalsIgnoreCase(currentStockStatus)) {
            tvStockStatus.setBackgroundResource(R.drawable.bg_stock_in);
            tvStockStatus.setTextColor(getResources().getColor(R.color.emerald_700));
            if (greenDot != null) greenDot.setBackgroundResource(R.drawable.bg_green_dot);
        } else if ("Low Stock".equalsIgnoreCase(currentStockStatus)) {
            tvStockStatus.setBackgroundResource(R.drawable.bg_stock_low);
            tvStockStatus.setTextColor(getResources().getColor(R.color.orange_600));
            if (greenDot != null) greenDot.setBackgroundResource(R.drawable.bg_amber_dot);
        } else if ("Out of Stock".equalsIgnoreCase(currentStockStatus)) {
            tvStockStatus.setBackgroundResource(R.drawable.bg_stock_out);
            tvStockStatus.setTextColor(getResources().getColor(R.color.red_600));
            if (greenDot != null) greenDot.setBackgroundResource(R.drawable.bg_gray_dot);
        } else {
            tvStockStatus.setBackgroundResource(R.drawable.bg_stock_in);
            tvStockStatus.setTextColor(getResources().getColor(R.color.emerald_700));
        }
    }

    private void setDefaultProductData() {
        // Clear default data
        currentProductName = "";
        currentProductSku = "";
        currentProductCategory = "";
        currentProductSize = "";
        currentPrice = "";
        currentStock = "";
        currentStockStatus = "";
        currentWarehouse = "";
        currentDimensions = "";
        currentFinish = "";
        currentThickness = "";
        currentCoverage = "";
        currentDescription = "";
        currentMargin = "";
        currentRetailPrice = "";
    }

    private void showMoreOptionsMenu() {
        // Create options menu
        String freezeTitle = currentIsActive ? "Freeze Product" : "Unfreeze Product";
        String[] options = {"Share Product", "Duplicate Product", "Print Barcode", "Delete Product", "View Analytics", freezeTitle};

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
                        case 5:
                            toggleProductFreeze();
                            break;
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showPriceHistory() {
        // Method placeholder
    }

    private void editProductDetails() {
        Toast.makeText(this, "Editing " + currentProductName, Toast.LENGTH_SHORT).show();

        // Open Edit Product Details Activity
        Intent intent = new Intent(AdminProductDetailsActivity.this, EditProductDetailsActivity.class);
        intent.putExtra("product_id", currentProductId);
        intent.putExtra("product_name", currentProductName);
        intent.putExtra("product_tile_no", currentProductTileNo);
        intent.putExtra("product_brand", currentProductBrand);
        intent.putExtra("product_category", currentProductCategory);
        intent.putExtra("product_size", currentProductSize);
        intent.putExtra("product_finish", currentFinish);
        intent.putExtra("product_color", currentProductColor);
        intent.putExtra("product_price", currentPrice);
        intent.putExtra("product_stock", currentStock);
        intent.putExtra("product_description", currentDescription);
        intent.putExtra("product_image", currentProductImage);
        intent.putExtra("product_is_active", currentIsActive);
        
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

    private void toggleProductFreeze() {
        boolean newStatus = !currentIsActive;
        String action = newStatus ? "Unfreeze" : "Freeze";
        
        new AlertDialog.Builder(this)
            .setTitle(action + " Product")
            .setMessage("Are you sure you want to " + action.toLowerCase() + " this product? " + 
                     (newStatus ? "It will become visible to customers." : "It will be hidden from customers."))
            .setPositiveButton(action, (dialog, which) -> performFreezeToggle(newStatus))
            .setNegativeButton("Cancel", null)
            .show();
    }

    private void performFreezeToggle(boolean newStatus) {
        com.example.florra_a.network.ApiService apiService = com.example.florra_a.network.RetrofitClient.getApiService();
        
        java.util.Map<String, okhttp3.RequestBody> fields = new java.util.HashMap<>();
        fields.put("is_active", okhttp3.RequestBody.create(okhttp3.MediaType.parse("text/plain"), String.valueOf(newStatus)));
        
        apiService.updateProduct(currentProductId, fields, null).enqueue(new retrofit2.Callback<com.example.florra_a.models.Product>() {
            @Override
            public void onResponse(retrofit2.Call<com.example.florra_a.models.Product> call, retrofit2.Response<com.example.florra_a.models.Product> response) {
                if (response.isSuccessful()) {
                    currentIsActive = newStatus;
                    String message = newStatus ? "Product unfrozen successfully" : "Product frozen successfully";
                    Toast.makeText(AdminProductDetailsActivity.this, message, Toast.LENGTH_SHORT).show();
                    
                    // Update UI if needed (e.g., status badge)
                    updateUIWithProductData();
                } else {
                    Toast.makeText(AdminProductDetailsActivity.this, "Failed to update product status", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(retrofit2.Call<com.example.florra_a.models.Product> call, Throwable t) {
                Toast.makeText(AdminProductDetailsActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
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
                    currentProductTileNo = data.getStringExtra("updated_product_tile_no");
                    currentProductSku = currentProductTileNo;
                    currentProductBrand = data.getStringExtra("updated_product_brand");
                    currentProductCategory = data.getStringExtra("updated_product_category");
                    currentProductSize = data.getStringExtra("updated_product_size");
                    currentFinish = data.getStringExtra("updated_product_finish");
                    currentProductColor = data.getStringExtra("updated_product_color");
                    currentPrice = data.getStringExtra("updated_product_price");
                    currentStock = data.getStringExtra("updated_product_stock");
                    currentDescription = data.getStringExtra("updated_product_description");
                    currentThickness = data.getStringExtra("updated_product_thickness");
                    currentCoverage = data.getStringExtra("updated_product_coverage");
                    currentWarehouse = data.getStringExtra("updated_product_warehouse");
                    currentIsActive = data.getBooleanExtra("updated_product_is_active", currentIsActive);
                    
                    try {
                        int stockNum = Integer.parseInt(currentStock);
                        if (stockNum <= 0) currentStockStatus = "Out of Stock";
                        else if (stockNum < 10) currentStockStatus = "Low Stock";
                        else currentStockStatus = "In Stock";
                    } catch (Exception e) {
                        currentStockStatus = "In Stock";
                    }
                    
                    updateUIWithProductData();
                    Toast.makeText(this, "Product updated successfully", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

@Override
    protected void onResume() {
        super.onResume();
        // Refresh product data in case it changed while this activity was paused
        if (currentProductId != -1) {
            // Re-fetch latest data if needed; for now reload from intent extras or server.
            loadProductData();
        }
    }
}