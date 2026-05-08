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

import com.example.florra_a.models.Product;

import java.util.ArrayList;
import java.util.List;

public class AIRecommendationActivity extends AppCompatActivity implements TileAdapter.OnItemClickListener {

    private RecyclerView rvRecommendations;
    private TileAdapter adapter;
    private List<Product> recommendedProducts;
    private TextView tvMatchCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set fullscreen
        requestWindowFeature(Window.FEATURE_NO_TITLE);

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
        adapter.setOnItemClickListener(this);
        rvRecommendations.setAdapter(adapter);
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
    public void onItemClick(Product product) {
        Intent intent = new Intent(this, ProductDetailsActivity.class);
        intent.putExtra("productId", product.getId());
        intent.putExtra("productName", product.getTileName());
        intent.putExtra("tileName", product.getTileName());
        intent.putExtra("productPrice", String.valueOf(product.getPrice()));
        intent.putExtra("tilePrice", String.valueOf(product.getPrice()));
        intent.putExtra("productStock", product.getStockStatus());
        intent.putExtra("stockStatus", product.getStock() > 0 ? "IN STOCK" : "OUT OF STOCK");
        intent.putExtra("productCategory", product.getCategory());
        intent.putExtra("productMaterial", product.getCategory());
        intent.putExtra("productTileNo", product.getTileNo());
        intent.putExtra("tileSize", product.getSize());
        intent.putExtra("productFinish", product.getFinish());
        intent.putExtra("productDescription", product.getDescription());
        intent.putExtra("productImage", product.getImage());
        startActivity(intent);
    }

    @Override
    public void onItemLongClick(Product product) {
        Toast.makeText(this, product.getTileName(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBookmarkClick(Product product) {
        // Handle bookmark logic
        boolean newState = product.isFavorite(); // TileAdapter toggles it before calling this
        
        com.example.florra_a.network.ApiService apiService = com.example.florra_a.network.RetrofitClient.getApiService();
        if (newState) {
            java.util.Map<String, Integer> map = new java.util.HashMap<>();
            map.put("product_id", product.getId());
            apiService.addToFavorites(map).enqueue(new retrofit2.Callback<okhttp3.ResponseBody>() {
                @Override
                public void onResponse(retrofit2.Call<okhttp3.ResponseBody> call, retrofit2.Response<okhttp3.ResponseBody> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(AIRecommendationActivity.this, "Added to Favorites", Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onFailure(retrofit2.Call<okhttp3.ResponseBody> call, Throwable t) {}
            });
        } else {
            apiService.removeFromFavorites(product.getId()).enqueue(new retrofit2.Callback<okhttp3.ResponseBody>() {
                @Override
                public void onResponse(retrofit2.Call<okhttp3.ResponseBody> call, retrofit2.Response<okhttp3.ResponseBody> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(AIRecommendationActivity.this, "Removed from Favorites", Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onFailure(retrofit2.Call<okhttp3.ResponseBody> call, Throwable t) {}
            });
        }
    }

    @Override
    public void onAddToCartClick(Product product) {
        Toast.makeText(this, "Added to cart: " + product.getTileName(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}
