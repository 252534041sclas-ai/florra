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
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Enable edge-to-edge
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);

        // Handle notch and status bar
        WindowInsetsControllerCompat windowInsetsController =
                WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());
        windowInsetsController.setAppearanceLightStatusBars(false);
        windowInsetsController.setAppearanceLightNavigationBars(false);

        setContentView(R.layout.activity_products);

        // Setup RecyclerView
        recyclerView = findViewById(R.id.recyclerViewProducts);
        recyclerView.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(this));
        
        // Show toast
        // Toast.makeText(this, "Products List", Toast.LENGTH_SHORT).show();

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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}