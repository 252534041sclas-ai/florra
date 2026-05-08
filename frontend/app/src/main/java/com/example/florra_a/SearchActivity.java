package com.example.florra_a;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.florra_a.TileAdapter;
import com.example.florra_a.models.InventoryResponse;
import com.example.florra_a.models.Product;
import com.example.florra_a.network.ApiService;
import com.example.florra_a.network.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchActivity extends AppCompatActivity {

    private EditText searchEditText;
    private RecyclerView recyclerView;
    private TextView tvResultsCount, tvNoResults;
    private TileAdapter tileAdapter;
    private List<Product> productList;
    private LinearLayout btnAll, btnFloorTiles, btnWallTiles, btnBathroom, btnKitchen, btnOutdoor;
    
    private String currentCategory = "all";
    private String currentQuery = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Enable edge-to-edge
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);

        // Handle notch and status bar - Set to true for dark icons on light background
        WindowInsetsControllerCompat windowInsetsController =
                WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());
        if (windowInsetsController != null) {
            windowInsetsController.setAppearanceLightStatusBars(true);
            windowInsetsController.setAppearanceLightNavigationBars(true);
        }

        setContentView(R.layout.activity_search);

        // Initialize views
        initializeViews();

        // Setup click listeners
        setupClickListeners();

        // Initialize RecyclerView
        productList = new ArrayList<>();
        tileAdapter = new TileAdapter(this, productList);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.setAdapter(tileAdapter);
        
        // Setup Item Click Listener
        tileAdapter.setOnItemClickListener(new TileAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Product product) {
                openProductDetails(product);
            }

            @Override
            public void onItemLongClick(Product product) {
                 // No action for long click in search
            }

            @Override
            public void onBookmarkClick(Product product) {
                Toast.makeText(SearchActivity.this, "Added to favorites: " + product.getTileName(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAddToCartClick(Product product) {
                Toast.makeText(SearchActivity.this, "Added to cart: " + product.getTileName(), Toast.LENGTH_SHORT).show();
            }
        });

        // Set search query if passed from previous activity
        String searchQuery = getIntent().getStringExtra("searchQuery");
        if (searchQuery != null && !searchQuery.isEmpty()) {
            searchEditText.setText(searchQuery);
            currentQuery = searchQuery;
            performSearch(currentQuery, currentCategory);
        } else {
            // Load initial data (all products)
            performSearch("", "all");
        }
    }

    private void initializeViews() {
        // Search field
        searchEditText = findViewById(R.id.editTextSearch);

        // Results Count & No Results
        tvResultsCount = findViewById(R.id.tvResultsCount);
        tvNoResults = findViewById(R.id.tvNoResults);

        // Category buttons
        btnAll = findViewById(R.id.btnAll);
        btnFloorTiles = findViewById(R.id.btnFloorTiles);
        btnWallTiles = findViewById(R.id.btnWallTiles);
        btnBathroom = findViewById(R.id.btnBathroom);
        btnKitchen = findViewById(R.id.btnKitchen);
        btnOutdoor = findViewById(R.id.btnOutdoor);

        // RecyclerView
        recyclerView = findViewById(R.id.recyclerViewSearch);

        // Set All as active by default
        setCategoryActive(btnAll);
    }

    private void setupClickListeners() {
        // Back button
        LinearLayout btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> onBackPressed());
        }

        // Clear search button
        ImageView btnClearSearch = findViewById(R.id.btnClearSearch);
        if (btnClearSearch != null) {
            btnClearSearch.setOnClickListener(v -> {
                searchEditText.setText("");
                currentQuery = "";
                performSearch("", currentCategory);
            });
        }

        // Search Input Listener
        searchEditText.setOnEditorActionListener((v, actionId, event) -> {
            boolean handled = false;
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                currentQuery = searchEditText.getText().toString();
                performSearch(currentQuery, currentCategory);
                handled = true;
            }
            return handled;
        });

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                 // Optional: Debounce here if needed, for now using explicit enter or clear
                 // But basic real-time search:
                 currentQuery = s.toString();
                 // performSearch(currentQuery, currentCategory); // Uncomment for instant search
            }
        });

        // Category buttons
        setupCategoryButtons();

        // Bottom Navigation
        setupBottomNavigation();
    }

    private void setupCategoryButtons() {
        btnAll.setOnClickListener(v -> updateCategory("all", btnAll));
        btnFloorTiles.setOnClickListener(v -> updateCategory("floor", btnFloorTiles));
        btnWallTiles.setOnClickListener(v -> updateCategory("wall", btnWallTiles));
        btnBathroom.setOnClickListener(v -> updateCategory("bathroom", btnBathroom));
        btnKitchen.setOnClickListener(v -> updateCategory("kitchen", btnKitchen));
        btnOutdoor.setOnClickListener(v -> updateCategory("outdoor", btnOutdoor));
    }

    private void updateCategory(String category, LinearLayout activeButton) {
        currentCategory = category;
        setCategoryActive(activeButton);
        performSearch(currentQuery, currentCategory);
    }

    private void performSearch(String query, String category) {
        tvResultsCount.setText("Searching...");
        tvNoResults.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);

        ApiService apiService = RetrofitClient.getApiService();
        // If category is "all", we skip passing it to API so it returns everything, 
        // OR pass "all" if backend handles it. Based on previous work, "all" seems to work or null.
        String catParam = (category.equals("all")) ? null : category;
        
        apiService.getInventory(query, catParam, null).enqueue(new Callback<InventoryResponse>() {
            @Override
            public void onResponse(Call<InventoryResponse> call, Response<InventoryResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Product> fetchedProducts = response.body().getProducts();
                    
                    if (fetchedProducts != null) {
                        productList.clear();
                        productList.addAll(fetchedProducts);
                        tileAdapter.updateData(fetchedProducts);
                        
                        tvResultsCount.setText(fetchedProducts.size() + " Results found");
                        
                        if (fetchedProducts.isEmpty()) {
                            tvNoResults.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.GONE);
                        } else {
                            tvNoResults.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);
                        }
                    }
                } else {
                    tvResultsCount.setText("Error fetching results");
                    Toast.makeText(SearchActivity.this, "Server Error: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<InventoryResponse> call, Throwable t) {
                tvResultsCount.setText("Network Error");
                Toast.makeText(SearchActivity.this, "Network Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setCategoryActive(LinearLayout activeButton) {
        // Reset all buttons
        resetCategoryButtons();

        // Set active button style
        activeButton.setBackgroundResource(R.drawable.bg_category_active);
        TextView textView = (TextView) activeButton.getChildAt(0);
        if (textView != null) {
            textView.setTextColor(getResources().getColor(R.color.white));
        }
    }

    private void resetCategoryButtons() {
        LinearLayout[] buttons = {btnAll, btnFloorTiles, btnWallTiles, btnBathroom, btnKitchen, btnOutdoor};
        String[] buttonTexts = {"All", "Floor Tiles", "Wall Tiles", "Bathroom", "Kitchen", "Outdoor"};

        for (int i = 0; i < buttons.length; i++) {
            buttons[i].setBackgroundResource(R.drawable.bg_category_inactive);
            TextView textView = (TextView) buttons[i].getChildAt(0);
            if (textView != null) {
                textView.setText(buttonTexts[i]);
                textView.setTextColor(getResources().getColor(R.color.slate_600));
            }
        }
    }

    private void setupBottomNavigation() {
        // Home button
        LinearLayout btnNavHome = findViewById(R.id.btnNavHome);
        if (btnNavHome != null) {
            btnNavHome.setOnClickListener(v -> goToHome());
        }

        // Catalog button
        LinearLayout btnNavCatalog = findViewById(R.id.btnNavCatalog);
        if (btnNavCatalog != null) {
            btnNavCatalog.setOnClickListener(v -> goToCatalog());
        }

        // Enquiries button
        LinearLayout btnNavEnquiries = findViewById(R.id.btnNavEnquiries);
        if (btnNavEnquiries != null) {
            btnNavEnquiries.setOnClickListener(v -> openQuotationsScreen());
        }

        // Account button
        LinearLayout btnNavAccount = findViewById(R.id.btnNavAccount);
        if (btnNavAccount != null) {
            btnNavAccount.setOnClickListener(v -> openAccountScreen());
        }
    }

    private void openProductDetails(Product product) {
        try {
            Intent intent = new Intent(SearchActivity.this, ProductDetailsActivity.class);
            intent.putExtra("productName", product.getTileName());
            intent.putExtra("productPrice", String.valueOf(product.getPrice()));
            intent.putExtra("productStock", product.getStockStatus());
            intent.putExtra("productCategory", product.getCategory());
            intent.putExtra("productSize", product.getSize());
            intent.putExtra("productFinish", product.getFinish());
            intent.putExtra("productImage", product.getImage());
            intent.putExtra("productDescription", product.getDescription()); // Assuming getter exists
            intent.putExtra("productId", product.getId());

            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        } catch (Exception e) {
            Toast.makeText(this, "Cannot open product details", Toast.LENGTH_SHORT).show();
        }
    }
    
    // Navigation helpers
    private void goToHome() {
        Intent intent = new Intent(SearchActivity.this, CustomerHomeActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    private void goToCatalog() {
        Intent intent = new Intent(SearchActivity.this, CatalogActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    private void openQuotationsScreen() {
        try {
            Intent intent = new Intent(SearchActivity.this, QuotationsActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        } catch (Exception e) {
            Toast.makeText(this, "Cannot open Quotations", Toast.LENGTH_SHORT).show();
        }
    }

    private void openAccountScreen() {
        try {
            Intent intent = new Intent(SearchActivity.this, CustomerAccountActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        } catch (Exception e) {
            Toast.makeText(this, "Cannot open Account", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}
