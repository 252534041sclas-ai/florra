package com.example.florra_a;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.florra_a.adapters.FavoritesAdapter;
import com.example.florra_a.models.Product;
// FavoritesManager removed
import java.util.List;

public class FavoritesActivity extends AppCompatActivity implements FavoritesAdapter.OnFavoriteActionListener {

    private RecyclerView recyclerFavorites;
    private FavoritesAdapter adapter;
    private TextView tvFavoritesCount, tvNoFavorites;
    private List<Product> favoritesList;
    private boolean isGridView = true;
    private TextView tvRequestQuoteText;

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

        setContentView(R.layout.activity_favorites);

        // Init Views
        initializeViews();

        // Setup Buttons
        setupClickListeners();

        // Load Data
        loadFavorites();
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        // Reload in case something changed in details screen
        loadFavorites();
    }

    private void initializeViews() {
        recyclerFavorites = findViewById(R.id.recyclerFavorites);
        tvFavoritesCount  = findViewById(R.id.tvFavoritesCount);
        tvNoFavorites     = findViewById(R.id.tvNoFavorites);
        tvRequestQuoteText = findViewById(R.id.tvRequestQuoteText);

        // Always use 2-column grid for item_tile.xml
        recyclerFavorites.setLayoutManager(new GridLayoutManager(this, 2));
    }

    private void loadFavorites() {
        com.example.florra_a.network.ApiService apiService = com.example.florra_a.network.RetrofitClient.getApiService();
        apiService.getFavorites().enqueue(new retrofit2.Callback<List<Product>>() {
            @Override
            public void onResponse(retrofit2.Call<List<Product>> call, retrofit2.Response<List<Product>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Product> fetched = response.body();
                    favoritesList = new java.util.ArrayList<>();
                    for (Product p : fetched) {
                        if (p.isActive()) favoritesList.add(p);
                    }
                    updateUI(favoritesList);
                } else {
                    // Handle empty or error
                    favoritesList = new java.util.ArrayList<>();
                    updateUI(favoritesList);
                }
            }

            @Override
            public void onFailure(retrofit2.Call<List<Product>> call, Throwable t) {
                Toast.makeText(FavoritesActivity.this, "Error loading favorites: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                 favoritesList = new java.util.ArrayList<>();
                 updateUI(favoritesList);
            }
        });
    }

    private void updateUI(List<Product> list) {
        updateCountUI();

        if (list.isEmpty()) {
            recyclerFavorites.setVisibility(View.GONE);
            tvNoFavorites.setVisibility(View.VISIBLE);
        } else {
            recyclerFavorites.setVisibility(View.VISIBLE);
            tvNoFavorites.setVisibility(View.GONE);
            adapter = new FavoritesAdapter(this, list, this);
            recyclerFavorites.setAdapter(adapter);
        }
    }
    
    private void updateCountUI() {
        int count = favoritesList != null ? favoritesList.size() : 0;
        tvFavoritesCount.setText(count + " items saved");
        if (tvRequestQuoteText != null) {
            tvRequestQuoteText.setText("Request Quotation (" + count + ")");
        }
    }

    @Override
    public void onRemoveFavorite(Product product, int position) {
        com.example.florra_a.network.ApiService apiService = com.example.florra_a.network.RetrofitClient.getApiService();
        apiService.removeFromFavorites(product.getId()).enqueue(new retrofit2.Callback<okhttp3.ResponseBody>() {
            @Override
            public void onResponse(retrofit2.Call<okhttp3.ResponseBody> call, retrofit2.Response<okhttp3.ResponseBody> response) {
                 if (response.isSuccessful()) {
                     Toast.makeText(FavoritesActivity.this, "Removed from Favorites", Toast.LENGTH_SHORT).show();
                     if (favoritesList != null && position < favoritesList.size()) {
                         favoritesList.remove(position);
                         adapter.notifyItemRemoved(position);
                         adapter.notifyItemRangeChanged(position, favoritesList.size());
                         updateCountUI();
                         
                         if (favoritesList.isEmpty()) {
                             recyclerFavorites.setVisibility(View.GONE);
                             tvNoFavorites.setVisibility(View.VISIBLE);
                         }
                     }
                 } else {
                     Toast.makeText(FavoritesActivity.this, "Failed to remove", Toast.LENGTH_SHORT).show();
                 }
            }
            @Override
            public void onFailure(retrofit2.Call<okhttp3.ResponseBody> call, Throwable t) {
                Toast.makeText(FavoritesActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupClickListeners() {
        // Toggle Buttons
        LinearLayout btnGridView = findViewById(R.id.btnGridView);
        LinearLayout btnListView = findViewById(R.id.btnListView);

        if (btnGridView != null) {
            btnGridView.setOnClickListener(v -> {
                if (!isGridView) {
                    isGridView = true;
                    recyclerFavorites.setLayoutManager(new GridLayoutManager(this, 2));
                    // Update UI state visually if needed (omitted for brevity)
                    Toast.makeText(this, "Grid View", Toast.LENGTH_SHORT).show();
                }
            });
        }

        if (btnListView != null) {
            btnListView.setOnClickListener(v -> {
                if (isGridView) {
                    isGridView = false;
                    recyclerFavorites.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(this));
                    Toast.makeText(this, "List View", Toast.LENGTH_SHORT).show();
                }
            });
        }
        
        // Request Quotation
        RelativeLayout btnRequestQuotation = findViewById(R.id.btnRequestQuotation);
        if (btnRequestQuotation != null) {
            btnRequestQuotation.setOnClickListener(v -> {
                if (favoritesList != null && !favoritesList.isEmpty()) {
                    // Navigate to Request Quotation with first item or logic
                    Intent intent = new Intent(FavoritesActivity.this, RequestQuotationActivity.class);
                    // Just passing first for now or we can implement bulk
                    Product first = favoritesList.get(0);
                    intent.putExtra("productName", first.getTileName());
                    intent.putExtra("productImage", first.getImage());
                    startActivity(intent);
                } else {
                    Toast.makeText(this, "No items to request quotation for", Toast.LENGTH_SHORT).show();
                }
            });
        }

        // Bottom Navigation
        setupBottomNavigation();
    }

    private void setupBottomNavigation() {
        LinearLayout btnNavHome = findViewById(R.id.btnNavHome);
        if (btnNavHome != null) btnNavHome.setOnClickListener(v -> {
             startActivity(new Intent(this, CustomerHomeActivity.class));
             overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        LinearLayout btnNavCatalog = findViewById(R.id.btnNavCatalog);
        if (btnNavCatalog != null) btnNavCatalog.setOnClickListener(v -> {
             startActivity(new Intent(this, CatalogActivity.class));
             overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        LinearLayout btnNavEnquiries = findViewById(R.id.btnNavEnquiries);
        if (btnNavEnquiries != null) btnNavEnquiries.setOnClickListener(v -> {
             startActivity(new Intent(this, QuotationsActivity.class));
             overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        LinearLayout btnNavAccount = findViewById(R.id.btnNavAccount);
        if (btnNavAccount != null) btnNavAccount.setOnClickListener(v -> {
             startActivity(new Intent(this, CustomerAccountActivity.class));
             overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}