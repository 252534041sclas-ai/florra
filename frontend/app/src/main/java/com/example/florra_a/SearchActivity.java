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
    private LinearLayout btnAll, categoryContainer;
    
    private String currentCategory = "all";
    private String currentQuery = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set fullscreen
                // Set status bar to white with dark icons
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(android.view.View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(android.graphics.Color.WHITE);
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
        categoryContainer = findViewById(R.id.categoryContainer);
        setupDynamicCategories();

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
        setupDynamicCategories();

        // Bottom Navigation
        setupBottomNavigation();
    }

    private void setupDynamicCategories() {
        btnAll.setOnClickListener(v -> updateCategory("all", btnAll));

        categoryContainer.removeAllViews();
        categoryContainer.addView(btnAll); // Re-add All button
        for (String category : com.example.florra_a.utils.Constants.CATEGORIES) {
            LinearLayout btn = createCategoryButton(category);
            btn.setOnClickListener(v -> updateCategory(category.toLowerCase(), btn));
            categoryContainer.addView(btn);
        }
    }

    private LinearLayout createCategoryButton(String text) {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.HORIZONTAL);
        layout.setGravity(android.view.Gravity.CENTER);
        layout.setBackgroundResource(R.drawable.bg_category_inactive);
        layout.setClickable(true);
        layout.setFocusable(true);
        
        int height = (int)(36 * getResources().getDisplayMetrics().density);
        int paddingH = (int)(16 * getResources().getDisplayMetrics().density);
        int marginR = (int)(8 * getResources().getDisplayMetrics().density);
        
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT, height
        );
        params.setMargins(0, 0, marginR, 0);
        layout.setLayoutParams(params);
        layout.setPadding(paddingH, 0, paddingH, 0);

        TextView tv = new TextView(this);
        tv.setText(text);
        tv.setTextColor(getResources().getColor(R.color.slate_600));
        tv.setTextSize(14);
        tv.setTypeface(android.graphics.Typeface.DEFAULT_BOLD);
        
        layout.addView(tv);
        return layout;
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
        String catParam = (category.equals("all")) ? null : category;
        
        // 1. Fetch Favorites first
        apiService.getFavorites().enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> favResponse) {
                final java.util.Set<Integer> favoriteIds = new java.util.HashSet<>();
                if (favResponse.isSuccessful() && favResponse.body() != null) {
                    for (Product p : favResponse.body()) {
                        favoriteIds.add(p.getId());
                    }
                }

                // 2. Fetch All Products for the category (local filtering will handle the search)
                apiService.getInventory(null, catParam, null).enqueue(new Callback<InventoryResponse>() {
                    @Override
                    public void onResponse(Call<InventoryResponse> call, Response<InventoryResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            List<Product> fetchedProducts = response.body().getProducts();
                            
                            if (fetchedProducts != null) {
                                // 3. Sync Favorite Status, Filter Inactive, and Apply Multi-field Filter
                                java.util.List<Product> processedProducts = new java.util.ArrayList<>();
                                for (Product product : fetchedProducts) {
                                    product.setFavorite(favoriteIds.contains(product.getId()));
                                    if (product.isActive()) {
                                        processedProducts.add(product);
                                    }
                                }
                                
                                productList.clear();
                                productList.addAll(processedProducts);
                                
                                // Apply the multi-field filter locally to ensure Name, No, Finish, Color etc. are covered
                                tileAdapter.updateData(processedProducts);
                                if (query != null && !query.isEmpty()) {
                                    tileAdapter.filterByQuery(query, "all"); // Category already handled by API catParam
                                }
                                
                                int finalCount = tileAdapter.getItemCount();
                                tvResultsCount.setText(finalCount + " Results found");
                                
                                if (finalCount == 0) {
                                    tvNoResults.setVisibility(View.VISIBLE);
                                    recyclerView.setVisibility(View.GONE);
                                } else {
                                    tvNoResults.setVisibility(View.GONE);
                                    recyclerView.setVisibility(View.VISIBLE);
                                }
                            }
                        } else {
                            tvResultsCount.setText("Error fetching results");
                            Toast.makeText(SearchActivity.this, "Server Error", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<InventoryResponse> call, Throwable t) {
                        tvResultsCount.setText("Network Error");
                        Toast.makeText(SearchActivity.this, "Network Error", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                // If favorites fail, still perform search but without heart icons
                apiService.getInventory(null, catParam, null).enqueue(new Callback<InventoryResponse>() {
                    @Override
                    public void onResponse(Call<InventoryResponse> call, Response<InventoryResponse> response) {
                         if (response.isSuccessful() && response.body() != null) {
                            List<Product> fetchedProducts = response.body().getProducts();
                            if (fetchedProducts != null) {
                                List<Product> processedProducts = new ArrayList<>();
                                for (Product p : fetchedProducts) {
                                    if (p.isActive()) processedProducts.add(p);
                                }
                                productList.clear();
                                productList.addAll(processedProducts);
                                tileAdapter.updateData(processedProducts);
                                if (query != null && !query.isEmpty()) {
                                    tileAdapter.filterByQuery(query, "all");
                                }
                                int finalCount = tileAdapter.getItemCount();
                                tvResultsCount.setText(finalCount + " Results found");
                            }
                        }
                    }
                    @Override
                    public void onFailure(Call<InventoryResponse> call, Throwable t) {
                        tvResultsCount.setText("Network Error");
                    }
                });
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
        btnAll.setBackgroundResource(R.drawable.bg_category_inactive);
        TextView tvAll = (TextView) btnAll.getChildAt(0);
        if (tvAll != null) tvAll.setTextColor(getResources().getColor(R.color.slate_600));

        for (int i = 0; i < categoryContainer.getChildCount(); i++) {
            View v = categoryContainer.getChildAt(i);
            if (v instanceof LinearLayout) {
                v.setBackgroundResource(R.drawable.bg_category_inactive);
                TextView tv = (TextView) ((LinearLayout)v).getChildAt(0);
                if (tv != null) tv.setTextColor(getResources().getColor(R.color.slate_600));
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
            intent.putExtra("productId", product.getId());
            intent.putExtra("rawStock", product.getStock());
            intent.putExtra("productName", product.getTileName());
            intent.putExtra("productPrice", String.valueOf(product.getPrice()));
            intent.putExtra("productStock", product.getStockStatus());
            intent.putExtra("productCategory", product.getCategory());
            intent.putExtra("productSize", product.getSize());
            intent.putExtra("productFinish", product.getFinish());
            intent.putExtra("productImage", product.getImage());
            intent.putExtra("productDescription", product.getDescription());
            intent.putExtra("productTileNo", product.getTileNo());
            
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