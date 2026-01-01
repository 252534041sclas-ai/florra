package com.example.florra_a;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import java.util.ArrayList;

public class InventoryActivity extends AppCompatActivity {

    // UI Components
    private ImageButton btnBack, btnFilter;
    private EditText etSearch;
    private Button btnAll, btnFloor, btnWall, btnKitchen, btnBathroom;
    private LinearLayout layoutProducts;

    // Stats TextViews
    private TextView tvTotal, tvInStock, tvLowStock, tvEmpty;

    // Product list
    private ArrayList<Product> productList = new ArrayList<>();

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

        setContentView(R.layout.activity_inventory);

        // Initialize views
        initViews();

        // Setup click listeners
        setupClickListeners();

        // Load sample data
        loadSampleData();

        // Populate product list
        populateProductList();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        btnFilter = findViewById(R.id.btnFilter);
        etSearch = findViewById(R.id.etSearch);

        // Category buttons
        btnAll = findViewById(R.id.btnAll);
        btnFloor = findViewById(R.id.btnFloor);
        btnWall = findViewById(R.id.btnWall);
        btnKitchen = findViewById(R.id.btnKitchen);
        btnBathroom = findViewById(R.id.btnBathroom);

        // Stats
        tvTotal = findViewById(R.id.tvTotal);
        tvInStock = findViewById(R.id.tvInStock);
        tvLowStock = findViewById(R.id.tvLowStock);
        tvEmpty = findViewById(R.id.tvEmpty);

        // Products container
        layoutProducts = findViewById(R.id.layoutProducts);
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
                Toast.makeText(InventoryActivity.this, "Filter options", Toast.LENGTH_SHORT).show();
                // Implement filter dialog
            }
        });

        // Category buttons
        btnAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setActiveCategoryButton(btnAll);
                filterProducts("All");
            }
        });

        btnFloor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setActiveCategoryButton(btnFloor);
                filterProducts("Floor");
            }
        });

        btnWall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setActiveCategoryButton(btnWall);
                filterProducts("Wall");
            }
        });

        btnKitchen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setActiveCategoryButton(btnKitchen);
                filterProducts("Kitchen");
            }
        });

        btnBathroom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setActiveCategoryButton(btnBathroom);
                filterProducts("Bathroom");
            }
        });
    }

    private void setActiveCategoryButton(Button activeButton) {
        // Reset all buttons
        btnAll.setBackgroundResource(R.drawable.bg_outline_button);
        btnAll.setTextColor(getResources().getColor(R.color.zinc_600));

        btnFloor.setBackgroundResource(R.drawable.bg_outline_button);
        btnFloor.setTextColor(getResources().getColor(R.color.zinc_600));

        btnWall.setBackgroundResource(R.drawable.bg_outline_button);
        btnWall.setTextColor(getResources().getColor(R.color.zinc_600));

        btnKitchen.setBackgroundResource(R.drawable.bg_outline_button);
        btnKitchen.setTextColor(getResources().getColor(R.color.zinc_600));

        btnBathroom.setBackgroundResource(R.drawable.bg_outline_button);
        btnBathroom.setTextColor(getResources().getColor(R.color.zinc_600));

        // Set active button
        activeButton.setBackgroundResource(R.drawable.bg_primary_button);
        activeButton.setTextColor(getResources().getColor(R.color.white));
    }

    private void loadSampleData() {
        // Add sample products (from your HTML)
        productList.add(new Product(
                "Statuario Marble",
                "60×120cm • Porcelain",
                "SKU: SM-60120-P",
                450,
                "In Stock",
                "Update",
                R.drawable.bg_green_badge
        ));

        productList.add(new Product(
                "Urban Concrete Grey",
                "60×60cm • Ceramic",
                "SKU: UC-6060-C",
                24,
                "Low Stock",
                "Order",
                R.drawable.bg_amber_badge
        ));

        productList.add(new Product(
                "Oak Wood Plank",
                "20x120cm • Wood Look",
                "SKU: OW-20120-W",
                0,
                "Empty",
                "Restock",
                R.drawable.bg_red_badge
        ));

        productList.add(new Product(
                "Ocean Blue Mosaic",
                "30x30cm • Mosaic",
                "SKU: OB-3030-M",
                120,
                "In Stock",
                "Update",
                R.drawable.bg_green_badge
        ));

        productList.add(new Product(
                "Nero Slate",
                "45x90cm • Natural Stone",
                "SKU: NS-4590-S",
                85,
                "In Stock",
                "Update",
                R.drawable.bg_green_badge
        ));
    }

    private void populateProductList() {
        // Clear existing views
        layoutProducts.removeAllViews();

        // Add each product to the layout
        for (Product product : productList) {
            View productView = createProductView(product);
            layoutProducts.addView(productView);
        }
    }

    private View createProductView(Product product) {
        // Inflate product item layout
        LayoutInflater inflater = LayoutInflater.from(this);
        View productView = inflater.inflate(R.layout.item_product, layoutProducts, false);

        // Get views
        CardView cardProduct = productView.findViewById(R.id.cardProduct);
        TextView tvProductName = productView.findViewById(R.id.tvProductName);
        TextView tvProductDetails = productView.findViewById(R.id.tvProductDetails);
        TextView tvSku = productView.findViewById(R.id.tvSku);
        TextView tvStockCount = productView.findViewById(R.id.tvStockCount);
        TextView tvStockStatus = productView.findViewById(R.id.tvStockStatus);
        Button btnAction = productView.findViewById(R.id.btnAction);
        ImageView ivProductImage = productView.findViewById(R.id.ivProductImage);

        // Set data
        tvProductName.setText(product.getName());
        tvProductDetails.setText(product.getDetails());
        tvSku.setText(product.getSku());
        tvStockCount.setText(String.valueOf(product.getStockCount()));
        tvStockStatus.setText(product.getStockStatus());
        btnAction.setText(product.getButtonText());

        // Set stock status color
        switch (product.getStockStatus()) {
            case "In Stock":
                tvStockStatus.setBackgroundResource(R.drawable.bg_green_badge);
                tvStockStatus.setTextColor(getResources().getColor(R.color.emerald_700));
                tvStockCount.setTextColor(getResources().getColor(R.color.primary_color));
                break;
            case "Low Stock":
                tvStockStatus.setBackgroundResource(R.drawable.bg_amber_badge);
                tvStockStatus.setTextColor(getResources().getColor(R.color.amber_700));
                tvStockCount.setTextColor(getResources().getColor(R.color.amber_600));
                break;
            case "Empty":
                tvStockStatus.setBackgroundResource(R.drawable.bg_red_badge);
                tvStockStatus.setTextColor(getResources().getColor(R.color.red_700));
                tvStockCount.setTextColor(getResources().getColor(R.color.red_600));
                productView.setAlpha(0.8f);
                break;
        }

        // Set button color based on stock status
        if (product.getStockStatus().equals("Low Stock")) {
            btnAction.setBackgroundResource(R.drawable.bg_primary_button);
            btnAction.setTextColor(getResources().getColor(R.color.white));
        } else {
            btnAction.setBackgroundResource(R.drawable.bg_outline_button);
            btnAction.setTextColor(getResources().getColor(R.color.primary_color));
        }

        // Set click listener for product card
        cardProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(InventoryActivity.this,
                        "Opening " + product.getName() + " details",
                        Toast.LENGTH_SHORT).show();

                // Open product details activity
                // Intent intent = new Intent(InventoryActivity.this, ProductDetailsActivity.class);
                // intent.putExtra("product_name", product.getName());
                // startActivity(intent);
            }
        });

        // Set click listener for action button
        btnAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleActionButtonClick(product);
            }
        });

        return productView;
    }

    private void handleActionButtonClick(Product product) {
        String message = "";

        switch (product.getButtonText()) {
            case "Update":
                message = "Update stock for " + product.getName();
                break;
            case "Order":
                message = "Order more " + product.getName();
                break;
            case "Restock":
                message = "Restock " + product.getName();
                break;
        }

        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();

        // Here you would implement:
        // 1. Show dialog to update quantity
        // 2. Open order screen
        // 3. Open restock screen
    }

    private void filterProducts(String category) {
        // Implement filtering logic
        Toast.makeText(this, "Showing " + category + " products", Toast.LENGTH_SHORT).show();

        // In real implementation, you would filter the productList
        // and repopulate the layoutProducts
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    // Product model class
    class Product {
        private String name;
        private String details;
        private String sku;
        private int stockCount;
        private String stockStatus;
        private String buttonText;
        private int statusBadge;

        public Product(String name, String details, String sku, int stockCount,
                       String stockStatus, String buttonText, int statusBadge) {
            this.name = name;
            this.details = details;
            this.sku = sku;
            this.stockCount = stockCount;
            this.stockStatus = stockStatus;
            this.buttonText = buttonText;
            this.statusBadge = statusBadge;
        }

        public String getName() { return name; }
        public String getDetails() { return details; }
        public String getSku() { return sku; }
        public int getStockCount() { return stockCount; }
        public String getStockStatus() { return stockStatus; }
        public String getButtonText() { return buttonText; }
        public int getStatusBadge() { return statusBadge; }
    }
}