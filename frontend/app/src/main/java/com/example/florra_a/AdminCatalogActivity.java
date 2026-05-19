package com.example.florra_a;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.florra_a.models.InventoryResponse;
import com.example.florra_a.models.Product;
import com.example.florra_a.network.ApiService;
import com.example.florra_a.network.RetrofitClient;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminCatalogActivity extends AppCompatActivity {

    private TileAdapter tileAdapter;
    private List<Product> allProducts = new ArrayList<>();
    private String selectedFilter = "all"; // Default filter
    private boolean isSelectionMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set fullscreen
        // Set status bar to white with dark icons
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            getWindow().setStatusBarColor(android.graphics.Color.WHITE);
            androidx.core.view.WindowCompat.setDecorFitsSystemWindows(getWindow(), true);
            androidx.core.view.WindowInsetsControllerCompat controller = new androidx.core.view.WindowInsetsControllerCompat(getWindow(), getWindow().getDecorView());
            controller.setAppearanceLightStatusBars(true);
        }

        setContentView(R.layout.activity_admin_catalog);

        // Check if intent has filter parameter
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("filter_type")) {
            selectedFilter = intent.getStringExtra("filter_type");
        }
        if (intent != null && intent.getBooleanExtra("is_selection_mode", false)) {
            isSelectionMode = true;
            // Update UI for selection mode
            TextView tvTitle = findViewById(R.id.tvTitle);
            if (tvTitle != null) tvTitle.setText("Select Product");
            View bottomNav = findViewById(R.id.bottomNav);
            if (bottomNav != null) bottomNav.setVisibility(View.GONE);
        }

        setupAllClickListeners();
        setupRecyclerView();
        if (!isSelectionMode) {
            setupBottomNavigation();
        }
        fetchProducts();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Always refresh the catalog when returning to this screen
        fetchProducts();
    }

    private void setupBottomNavigation() {
        // Dashboard button
        View btnDashboard = findViewById(R.id.bottomDashboard);
        if (btnDashboard != null) {
            btnDashboard.setOnClickListener(v -> {
                Intent intent = new Intent(AdminCatalogActivity.this, AdminDashboardActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            });
        }

        // Catalog button - Already here
        View btnCatalog = findViewById(R.id.bottomInventory);
        if (btnCatalog != null) {
            btnCatalog.setOnClickListener(v -> {
                // Already on catalog
            });
        }

        // Enquiries button
        View btnEnquiries = findViewById(R.id.bottomQuotes);
        if (btnEnquiries != null) {
            btnEnquiries.setOnClickListener(v -> {
                Intent intent = new Intent(AdminCatalogActivity.this, EnquiriesActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            });
        }

        // Account button
        View btnAccount = findViewById(R.id.bottomAccount);
        if (btnAccount != null) {
            btnAccount.setOnClickListener(v -> {
                Intent intent = new Intent(AdminCatalogActivity.this, AdminAccountActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            });
        }
    }

    private void setupAllClickListeners() {
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

        // Search button
        View btnSearch = findViewById(R.id.btnSearch);
        LinearLayout searchContainer = findViewById(R.id.searchContainer);
        LinearLayout titleContainer = findViewById(R.id.titleContainer);
        android.widget.EditText etSearch = findViewById(R.id.etSearch);
        ImageView btnCloseSearch = findViewById(R.id.btnCloseSearch);

        if (btnSearch != null) {
            btnSearch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (searchContainer != null && titleContainer != null) {
                        titleContainer.setVisibility(View.GONE);
                        searchContainer.setVisibility(View.VISIBLE);
                        if (etSearch != null) {
                            etSearch.requestFocus();
                            // Show keyboard
                            android.view.inputmethod.InputMethodManager imm = (android.view.inputmethod.InputMethodManager) getSystemService(android.content.Context.INPUT_METHOD_SERVICE);
                            if (imm != null) {
                                imm.showSoftInput(etSearch, android.view.inputmethod.InputMethodManager.SHOW_IMPLICIT);
                            }
                        }
                    }
                }
            });
        }

        if (btnCloseSearch != null) {
            btnCloseSearch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (etSearch != null) etSearch.setText("");
                    if (searchContainer != null && titleContainer != null) {
                        searchContainer.setVisibility(View.GONE);
                        titleContainer.setVisibility(View.VISIBLE);
                        // Hide keyboard
                        android.view.inputmethod.InputMethodManager imm = (android.view.inputmethod.InputMethodManager) getSystemService(android.content.Context.INPUT_METHOD_SERVICE);
                        if (imm != null && etSearch != null) {
                            imm.hideSoftInputFromWindow(etSearch.getWindowToken(), 0);
                        }
                        // Reset filter
                        applyFilter(selectedFilter);
                    }
                }
            });
        }

        if (etSearch != null) {
            etSearch.addTextChangedListener(new android.text.TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    filterProducts(s.toString());
                }

                @Override
                public void afterTextChanged(android.text.Editable s) {}
            });
        }

        // Filter categories
        setupFilterButtons();
    }

    private void setupFilterButtons() {
        LinearLayout btnAllTiles = findViewById(R.id.btnAllTiles);
        LinearLayout categoryContainer = findViewById(R.id.categoryContainer);
        
        setupDynamicCategories(btnAllTiles, categoryContainer);
    }

    private void setupDynamicCategories(LinearLayout btnAllTiles, LinearLayout categoryContainer) {
        View.OnClickListener filterClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String filter = "all";
                String title = "All Tiles";
                
                if (v.getId() == R.id.btnAllTiles) {
                    filter = "all";
                    title = "All Tiles";
                } else {
                    // It's a dynamic button
                    TextView tv = (TextView) ((LinearLayout) v).getChildAt(0);
                    title = tv.getText().toString();
                    filter = title.toLowerCase();
                }

                resetFilterButtons();
                activateButton((LinearLayout) v, title);
                applyFilter(filter);
            }
        };

        if (btnAllTiles != null) btnAllTiles.setOnClickListener(filterClickListener);

        categoryContainer.removeAllViews();
        // In XML btnAllTiles is already a child of the container in some layouts, but here it's inside
        categoryContainer.addView(btnAllTiles);

        for (String category : com.example.florra_a.utils.Constants.CATEGORIES) {
            LinearLayout btn = createCategoryButton(category);
            btn.setOnClickListener(filterClickListener);
            categoryContainer.addView(btn);
            
            // Check if this should be active
            if (category.equalsIgnoreCase(selectedFilter)) {
                activateButton(btn, category);
            }
        }
        
        if (selectedFilter.equalsIgnoreCase("all")) {
            activateButton(btnAllTiles, "All Tiles");
        }
    }

    private LinearLayout createCategoryButton(String text) {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.HORIZONTAL);
        layout.setGravity(android.view.Gravity.CENTER);
        layout.setBackgroundResource(R.drawable.bg_filter_inactive);
        layout.setClickable(true);
        layout.setFocusable(true);
        
        int paddingH = (int)(20 * getResources().getDisplayMetrics().density);
        int paddingV = (int)(8 * getResources().getDisplayMetrics().density);
        int marginR = (int)(8 * getResources().getDisplayMetrics().density);
        
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 0, marginR, 0);
        layout.setLayoutParams(params);
        layout.setPadding(paddingH, paddingV, paddingH, paddingV);

        TextView tv = new TextView(this);
        tv.setText(text);
        tv.setTextColor(getResources().getColor(R.color.slate_600));
        tv.setTextSize(14);
        tv.setTypeface(android.graphics.Typeface.DEFAULT_BOLD);
        
        layout.addView(tv);
        return layout;
    }


    private void resetFilterButtons() {
        LinearLayout categoryContainer = findViewById(R.id.categoryContainer);
        if (categoryContainer == null) return;

        for (int i = 0; i < categoryContainer.getChildCount(); i++) {
            View v = categoryContainer.getChildAt(i);
            if (v instanceof LinearLayout) {
                v.setBackgroundResource(R.drawable.bg_filter_inactive);
                TextView tv = (TextView) ((LinearLayout) v).getChildAt(0);
                if (tv != null) tv.setTextColor(getResources().getColor(R.color.slate_600));
            }
        }
    }

    private void activateButton(LinearLayout button, String buttonText) {
        if (button != null) {
            button.setBackgroundResource(R.drawable.bg_filter_active);
            if (button.getChildAt(0) instanceof TextView) {
                ((TextView) button.getChildAt(0)).setTextColor(getResources().getColor(R.color.white));
                ((TextView) button.getChildAt(0)).setText(buttonText);
            }
        }
    }

    private void setupRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.recyclerViewTiles);
        if (recyclerView != null) {
            tileAdapter = new TileAdapter(this, new ArrayList<>());
            recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
            recyclerView.setAdapter(tileAdapter);
            
            tileAdapter.setOnItemClickListener(new TileAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(Product product) {
                    if (isSelectionMode) {
                        Intent resultIntent = new Intent();
                        resultIntent.putExtra("selected_product", product);
                        setResult(RESULT_OK, resultIntent);
                        finish();
                    } else {
                        // Open ADMIN Product Details
                        openAdminProductDetails(product);
                    }
                }

                @Override
                public void onBookmarkClick(Product product) {
                    // Optional: Admin might want to see favorites too? Or disable logic.
                    // For now, doing nothing or show toast
                    // Toast.makeText(AdminCatalogActivity.this, "Favorites managed by customer", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onItemLongClick(Product product) {
                    // Optional: Admin Long Press Logic
                }

                @Override
                public void onAddToCartClick(Product product) {
                     // Toast.makeText(AdminCatalogActivity.this, "Cart is for customers", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void fetchProducts() {
        ApiService apiService = RetrofitClient.getApiService();
        apiService.getProducts().enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    allProducts = response.body();
                    tileAdapter.updateData(allProducts);
                    applyFilter(selectedFilter);
                } else {
                    Toast.makeText(AdminCatalogActivity.this, "Failed to load catalog", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                Toast.makeText(AdminCatalogActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void applyFilter(String filterType) {
        if (tileAdapter == null) return;
        selectedFilter = filterType; // Update selected filter
        
        // If search is active (check EditText), we should re-apply search too.
        // But for simplicity, let's just trigger basic category filter first.
        // Or better: Use one centralized filter method.
        
        android.widget.EditText etSearch = findViewById(R.id.etSearch);
        String searchText = "";
        if (etSearch != null && etSearch.getText() != null) {
            searchText = etSearch.getText().toString();
        }
        
        filterProducts(searchText);
    }

    private void filterProducts(String searchText) {
        List<Product> filteredList = new ArrayList<>();
        String query = searchText.toLowerCase().trim();
        String currentCat = selectedFilter.toLowerCase();
        
        for (Product product : allProducts) {
            // Check Category Match
            boolean categoryMatch = currentCat.equals("all") || 
                                    (product.getCategory() != null && product.getCategory().toLowerCase().equals(currentCat));
            
            // Check Search Match
            boolean searchMatch = query.isEmpty() || 
                                  (product.getTileName() != null && product.getTileName().toLowerCase().contains(query));

            if (categoryMatch && searchMatch) {
                filteredList.add(product);
            }
        }
        
        if (tileAdapter != null) {
            tileAdapter.filterList(filteredList);
        }
    }

    private void openAdminProductDetails(Product product) {
        Intent intent = new Intent(AdminCatalogActivity.this, AdminProductDetailsActivity.class);
        // Pass validation same as ProductAdapter
        intent.putExtra("product_id", product.getId());
        intent.putExtra("product_name", product.getTileName());
        intent.putExtra("product_tile_no", product.getTileNo());
        intent.putExtra("product_brand", product.getBrandName());
        intent.putExtra("product_category", product.getCategory());
        intent.putExtra("product_size", product.getSize());
        intent.putExtra("product_finish", product.getFinish());
        intent.putExtra("product_color", product.getColor());
        intent.putExtra("product_price", product.getPrice());
        intent.putExtra("product_stock", String.valueOf(product.getStock()));
        intent.putExtra("product_status", product.getStockStatus());
        intent.putExtra("product_description", product.getDescription());
        intent.putExtra("product_image", product.getImage());
        intent.putExtra("product_is_active", product.isActive());
        
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}
