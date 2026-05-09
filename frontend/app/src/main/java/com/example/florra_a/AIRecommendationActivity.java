package com.example.florra_a;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.florra_a.adapters.RecommendationAdapter;
import com.example.florra_a.models.Product;

import java.util.ArrayList;
import java.util.List;

public class AIRecommendationActivity extends AppCompatActivity {

    private RecyclerView rvRecommendations;
    private TileAdapter adapter;
    private List<Product> recommendedProducts;
    private TextView tvMatchCount;

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

        setContentView(R.layout.activity_ai_recommendation);

        initializeViews();
        setupClickListeners();
        loadRecommendations();
    }

    private void initializeViews() {
        rvRecommendations = findViewById(R.id.rvRecommendations);
        // Use Grid Layout with 2 columns
        rvRecommendations.setLayoutManager(new GridLayoutManager(this, 2));

        tvMatchCount = findViewById(R.id.tvMatchCount);
    }

    private void loadRecommendations() {
        // Get data from intent
        if (getIntent().hasExtra("recommendations")) {
            recommendedProducts = (List<Product>) getIntent().getSerializableExtra("recommendations");
        }

        if (recommendedProducts == null) {
            recommendedProducts = new ArrayList<>();
        }

        tvMatchCount.setText("Found " + recommendedProducts.size() + " similar matches");

        adapter = new TileAdapter(this, recommendedProducts);
        adapter.setOnItemClickListener(new TileAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Product product) {
                Intent intent = new Intent(AIRecommendationActivity.this, ProductDetailsActivity.class);
                intent.putExtra("productId", product.getId());
                intent.putExtra("tileName", product.getTileName());
                intent.putExtra("tilePrice", String.valueOf(product.getPrice()));
                intent.putExtra("tileSize", product.getSize());
                intent.putExtra("productFinish", product.getFinish());
                intent.putExtra("productCategory", product.getCategory());
                intent.putExtra("productDescription", product.getDescription());
                intent.putExtra("productImage", product.getImage());
                intent.putExtra("stockStatus", product.getStock() > 0 ? "IN STOCK" : "OUT OF STOCK");
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }

            @Override
            public void onItemLongClick(Product product) {}

            @Override
            public void onBookmarkClick(Product product) {
                // Logic for bookmarking
                toggleFavorite(product);
            }

            @Override
            public void onAddToCartClick(Product product) {}
        });
        rvRecommendations.setAdapter(adapter);
    }

    private void toggleFavorite(Product product) {
        if (product.getId() == 0) return;
        
        java.util.Map<String, Integer> body = new java.util.HashMap<>();
        body.put("product_id", product.getId());

        com.example.florra_a.network.RetrofitClient.getApiService().addToFavorites(body).enqueue(new retrofit2.Callback<okhttp3.ResponseBody>() {
            @Override
            public void onResponse(retrofit2.Call<okhttp3.ResponseBody> call, retrofit2.Response<okhttp3.ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(AIRecommendationActivity.this, "Updated favorites", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(retrofit2.Call<okhttp3.ResponseBody> call, Throwable t) {}
        });
    }

    private void setupClickListeners() {
        findViewById(R.id.btnBack).setOnClickListener(v -> onBackPressed());

        findViewById(R.id.btnSort).setOnClickListener(v -> 
            Toast.makeText(this, "Sort Options", Toast.LENGTH_SHORT).show());
        
        findViewById(R.id.btnRefineSearch).setOnClickListener(v -> 
             Toast.makeText(this, "Refine Search Parameters", Toast.LENGTH_SHORT).show());
        
        // Setup filter button listeners (visual only for now)
        int[] filterIds = {R.id.btnTopMatches, R.id.btnTexture, R.id.btnColorPalette};
        for (int id : filterIds) {
            findViewById(id).setOnClickListener(v -> {
                 Toast.makeText(this, "Filter applied", Toast.LENGTH_SHORT).show();
            });
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}