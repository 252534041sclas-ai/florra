package com.example.florra_a;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

        setContentView(R.layout.activity_products);

        // Setup RecyclerView
        recyclerView = findViewById(R.id.recyclerViewProducts);
        recyclerView.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(this));
        
        // Show toast
        // Toast.makeText(this, "Products List", Toast.LENGTH_SHORT).show();

        // Setup navigation
        setupNavigation();
        setupBottomNavigation();
        
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
                    List<com.example.florra_a.models.Product> productList = response.body();
                    productAdapter = new ProductAdapter(ProductsActivity.this, productList);
                    recyclerView.setAdapter(productAdapter);
                } else {
                    Toast.makeText(ProductsActivity.this, "Failed to load products: " + response.code(), Toast.LENGTH_SHORT).show();
                    android.util.Log.e("ProductsActivity", "Error: " + response.code() + " " + response.message());
                }
            }

            @Override
            public void onFailure(retrofit2.Call<java.util.List<com.example.florra_a.models.Product>> call, Throwable t) {
                Toast.makeText(ProductsActivity.this, "Network Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                android.util.Log.e("ProductsActivity", "Failure: " + t.getMessage());
            }
        });
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
                    Toast.makeText(ProductsActivity.this, "More options", Toast.LENGTH_SHORT).show();
                }
            });
        }

        // Filter buttons (Logic can be added later)
        Button btnAll = findViewById(R.id.btnAll);
        if (btnAll != null) {
            btnAll.setOnClickListener(v -> Toast.makeText(ProductsActivity.this, "Showing all products", Toast.LENGTH_SHORT).show());
        }

        // Add Product button
        Button btnAddProduct = findViewById(R.id.btnAddProduct);
        if (btnAddProduct != null) {
            btnAddProduct.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(ProductsActivity.this, AddProductActivity.class);
                    startActivity(intent);
                }
            });
        }
    }

    private void setupBottomNavigation() {
        LinearLayout navDash = findViewById(R.id.bottomDashboard);
        LinearLayout navInventory = findViewById(R.id.bottomInventory);
        LinearLayout navQuotes = findViewById(R.id.bottomQuotes);
        LinearLayout navAccount = findViewById(R.id.bottomAccount);

        if (navDash != null) {
            navDash.setOnClickListener(v -> {
                Intent intent = new Intent(this, AdminDashboardActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            });
        }

        if (navInventory != null) {
            navInventory.setOnClickListener(v -> {
                Intent intent = new Intent(this, AdminCatalogActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            });
        }

        if (navQuotes != null) {
            navQuotes.setOnClickListener(v -> {
                Intent intent = new Intent(this, EnquiriesActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            });
        }

        if (navAccount != null) {
            navAccount.setOnClickListener(v -> {
                Intent intent = new Intent(this, AdminAccountActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            });
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
