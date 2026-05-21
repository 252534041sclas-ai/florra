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
import androidx.cardview.widget.CardView;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.example.florra_a.models.Product;
import java.util.List;

public class ProductsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProductAdapter productAdapter;

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

        setContentView(R.layout.activity_products);

        // Setup RecyclerView
        recyclerView = findViewById(R.id.recyclerViewProducts);
        recyclerView.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(this));
        
        // Setup navigation
        setupNavigation();
        
        // Fetch products
        fetchProducts();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh list when returning from Add Product or Details
        fetchProducts();
    }

    private void fetchProducts() {
        com.example.florra_a.network.ApiService apiService = com.example.florra_a.network.RetrofitClient.getApiService();
        retrofit2.Call<java.util.List<com.example.florra_a.models.Product>> call = apiService.getProducts();

        call.enqueue(new retrofit2.Callback<java.util.List<com.example.florra_a.models.Product>>() {
            @Override
            public void onResponse(retrofit2.Call<java.util.List<com.example.florra_a.models.Product>> call, retrofit2.Response<java.util.List<com.example.florra_a.models.Product>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    fullProductList = response.body();
                    productAdapter = new ProductAdapter(ProductsActivity.this, fullProductList);
                    recyclerView.setAdapter(productAdapter);
                } else {
                    Toast.makeText(ProductsActivity.this, "Failed to load products: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(retrofit2.Call<java.util.List<com.example.florra_a.models.Product>> call, Throwable t) {
                Toast.makeText(ProductsActivity.this, "Network Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupNavigation() {
        // Back button
        ImageView btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> onBackPressed());
        }

        // More options button
        ImageView btnMore = findViewById(R.id.btnMore);
        if (btnMore != null) {
            btnMore.setOnClickListener(v -> Toast.makeText(ProductsActivity.this, "More options", Toast.LENGTH_SHORT).show());
        }

        // Filter buttons
        Button btnAll = findViewById(R.id.btnAll);
        if (btnAll != null) {
            btnAll.setOnClickListener(v -> {
                fetchProducts(); // Reload all
                Toast.makeText(ProductsActivity.this, "Showing all products", Toast.LENGTH_SHORT).show();
            });
        }

        Button btnCategory = findViewById(R.id.btnCategory);
        if (btnCategory != null) {
            btnCategory.setOnClickListener(v -> showCategoryMenu(v));
        }

        Button btnSize = findViewById(R.id.btnSize);
        if (btnSize != null) {
            btnSize.setOnClickListener(v -> showSizeMenu(v));
        }

        Button btnStock = findViewById(R.id.btnStock);
        if (btnStock != null) {
            btnStock.setOnClickListener(v -> showStockMenu(v));
        }

        // Add Product button
        View btnAddProduct = findViewById(R.id.btnAddProduct);
        if (btnAddProduct != null) {
            btnAddProduct.setOnClickListener(v -> {
                Intent intent = new Intent(ProductsActivity.this, AddProductActivity.class);
                startActivity(intent);
            });
        }
    }

    private void showCategoryMenu(View v) {
        android.widget.PopupMenu popup = new android.widget.PopupMenu(this, v);
        popup.getMenu().add("All");
        for (String category : com.example.florra_a.utils.Constants.CATEGORIES) {
            popup.getMenu().add(category);
        }

        popup.setOnMenuItemClickListener(item -> {
            String category = item.getTitle().toString();
            ((Button)v).setText(category);
            if (category.equals("All")) {
                fetchProducts();
            } else {
                filterByLogic("category", category);
            }
            return true;
        });
        popup.show();
    }

    private void showSizeMenu(View v) {
        android.widget.PopupMenu popup = new android.widget.PopupMenu(this, v);
        popup.getMenu().add("All");
        popup.getMenu().add("12x12");
        popup.getMenu().add("2x2 ft");
        popup.getMenu().add("2x4 ft");
        popup.getMenu().add("12x18");
        popup.getMenu().add("12x8");

        popup.setOnMenuItemClickListener(item -> {
            String size = item.getTitle().toString();
            ((Button)v).setText(size);
            if (size.equals("All")) {
                fetchProducts();
            } else {
                filterByLogic("size", size);
            }
            return true;
        });
        popup.show();
    }

    private void showStockMenu(View v) {
        android.widget.PopupMenu popup = new android.widget.PopupMenu(this, v);
        popup.getMenu().add("All");
        popup.getMenu().add("In Stock");
        popup.getMenu().add("Out of Stock");
        popup.getMenu().add("Low Stock (<20)");

        popup.setOnMenuItemClickListener(item -> {
            String stockType = item.getTitle().toString();
            ((Button)v).setText(stockType);
            if (stockType.equals("All")) {
                fetchProducts();
            } else {
                filterByLogic("stock", stockType);
            }
            return true;
        });
        popup.show();
    }

    private List<Product> fullProductList = new java.util.ArrayList<>();

    private void filterByLogic(String type, String value) {
        if (fullProductList == null || fullProductList.isEmpty()) return;

        List<Product> filteredList = new java.util.ArrayList<>();
        String query = value.toLowerCase();

        for (Product product : fullProductList) {
            boolean matches = false;
            if (type.equals("category")) {
                matches = product.getCategory() != null && product.getCategory().toLowerCase().contains(query);
            } else if (type.equals("size")) {
                matches = product.getSize() != null && product.getSize().toLowerCase().contains(query);
            } else if (type.equals("stock")) {
                int stock = product.getStock();
                if (value.equals("In Stock")) matches = stock > 0;
                else if (value.equals("Out of Stock")) matches = stock == 0;
                else if (value.equals("Low Stock (<20)")) matches = stock > 0 && stock < 20;
            }

            if (matches) {
                filteredList.add(product);
            }
        }

        if (productAdapter != null) {
            productAdapter.updateList(filteredList);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}