package com.example.florra_a;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsControllerCompat;
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

public class CatalogActivity extends AppCompatActivity {

    private TileAdapter tileAdapter;
    private List<Product> allProducts = new ArrayList<>();
    private String selectedFilter = "all"; // Default filter
    private Product selectedProductForCompare; // State for compare flow

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

        setContentView(R.layout.activity_catalog);

        // Check if intent has filter parameter
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("filter_type")) {
            selectedFilter = intent.getStringExtra("filter_type");
        }

        setupAllClickListeners();
        setupRecyclerView();
        fetchProducts(); // Changed to fetch from API
    }

    private void setupAllClickListeners() {
        // Back button
        ImageView btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goBackToHome();
                }
            });
        }

        // Search button
        ImageView btnSearch = findViewById(R.id.btnSearch);
        if (btnSearch != null) {
            btnSearch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openSearchScreen();
                }
            });
        }

        // Filter categories
        setupFilterButtons();

        // Bottom Navigation
        setupBottomNavigation();
    }

    private void setupFilterButtons() {
        LinearLayout btnAllTiles = findViewById(R.id.btnAllTiles);
        LinearLayout btnWall = findViewById(R.id.btnWall);
        LinearLayout btnLiving = findViewById(R.id.btnLiving);
        LinearLayout btnBathroom = findViewById(R.id.btnBathroom);
        LinearLayout btnKitchen = findViewById(R.id.btnKitchen);
        LinearLayout btnBedroom = findViewById(R.id.btnBedroom);

        // Reset all buttons first
        resetFilterButtons();

        // Set initial state based on selectedFilter
        switch (selectedFilter.toLowerCase()) {
            case "wall":
                activateButton(btnWall, "Wall");
                break;
            case "living":
                activateButton(btnLiving, "Living");
                break;
            case "bathroom":
                activateButton(btnBathroom, "Bathroom");
                break;
            case "kitchen":
                activateButton(btnKitchen, "Kitchen");
                break;
            case "bedroom":
                activateButton(btnBedroom, "Bedroom");
                break;
            default:
                activateButton(btnAllTiles, "All Tiles");
                break;
        }

        // Set click listeners
        View.OnClickListener filterClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetFilterButtons();
                String filter = "all";
                String title = "All Tiles";

                int id = v.getId();
                if (id == R.id.btnAllTiles) {
                    filter = "all";
                    title = "All Tiles";
                } else if (id == R.id.btnWall) {
                    filter = "wall";
                    title = "Wall";
                } else if (id == R.id.btnLiving) {
                    filter = "living";
                    title = "Living";
                } else if (id == R.id.btnBathroom) {
                    filter = "bathroom";
                    title = "Bathroom";
                } else if (id == R.id.btnKitchen) {
                    filter = "kitchen";
                    title = "Kitchen";
                } else if (id == R.id.btnBedroom) {
                    filter = "bedroom";
                    title = "Bedroom";
                }

                activateButton((LinearLayout) v, title);
                applyFilter(filter);
            }
        };

        if (btnAllTiles != null) btnAllTiles.setOnClickListener(filterClickListener);
        if (btnWall != null) btnWall.setOnClickListener(filterClickListener);
        if (btnLiving != null) btnLiving.setOnClickListener(filterClickListener);
        if (btnBathroom != null) btnBathroom.setOnClickListener(filterClickListener);
        if (btnKitchen != null) btnKitchen.setOnClickListener(filterClickListener);
        if (btnBedroom != null) btnBedroom.setOnClickListener(filterClickListener);
    }

    private void resetFilterButtons() {
        int[][] buttonIds = {
                {R.id.btnAllTiles, R.drawable.bg_filter_inactive},
                {R.id.btnWall, R.drawable.bg_filter_inactive},
                {R.id.btnLiving, R.drawable.bg_filter_inactive},
                {R.id.btnBathroom, R.drawable.bg_filter_inactive},
                {R.id.btnKitchen, R.drawable.bg_filter_inactive},
                {R.id.btnBedroom, R.drawable.bg_filter_inactive}
        };

        for (int[] id : buttonIds) {
            LinearLayout button = findViewById(id[0]);
            if (button != null) {
                button.setBackgroundResource(id[1]);
                if (button.getChildAt(0) instanceof TextView) {
                    ((TextView) button.getChildAt(0)).setTextColor(getResources().getColor(R.color.slate_600));
                }
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

    private void setupBottomNavigation() {
        // Home button
        LinearLayout btnNavHome = findViewById(R.id.btnNavHome);
        if (btnNavHome != null) {
            btnNavHome.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goBackToHome();
                }
            });
        }

        // Catalog button
        LinearLayout btnNavCatalog = findViewById(R.id.btnNavCatalog);
        if (btnNavCatalog != null) {
            btnNavCatalog.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Already on catalog
                }
            });
        }

        // Enquiries button - Updated to open Quotations
        LinearLayout btnNavEnquiries = findViewById(R.id.btnNavEnquiries);
        if (btnNavEnquiries != null) {
            btnNavEnquiries.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openQuotationsScreen();
                }
            });
        }

        // Account button
        LinearLayout btnNavAccount = findViewById(R.id.btnNavAccount);
        if (btnNavAccount != null) {
            btnNavAccount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openAccountScreen();
                }
            });
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
                     boolean isPicker = getIntent().getBooleanExtra("IS_PICKER", false);
                     if (isPicker) {
                          Toast.makeText(CatalogActivity.this, "Long press to select this product", Toast.LENGTH_SHORT).show();
                     } else {
                          // Normal Mode Check
                          if (selectedProductForCompare != null) {
                              // Product A already selected, this is Product B
                              android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(CatalogActivity.this);
                              builder.setTitle("Compare Products");
                              builder.setMessage("Compare " + selectedProductForCompare.getTileName() + " vs " + product.getTileName() + "?");
                              builder.setPositiveButton("Yes", (dialog, which) -> {
                                  Intent intent = new Intent(CatalogActivity.this, CompareActivity.class);
                                  // Pass Product A
                                  intent.putExtra("productName", selectedProductForCompare.getTileName());
                                  intent.putExtra("productPrice", String.valueOf(selectedProductForCompare.getPrice()));
                                  intent.putExtra("productCategory", selectedProductForCompare.getCategory());
                                  intent.putExtra("productSize", selectedProductForCompare.getSize());
                                  intent.putExtra("productFinish", selectedProductForCompare.getFinish());
                                  intent.putExtra("productImage", selectedProductForCompare.getImage());
                                  intent.putExtra("productTileNo", selectedProductForCompare.getTileNo());
                                  intent.putExtra("productThickness", selectedProductForCompare.getThickness());
                                  intent.putExtra("productCoverage", selectedProductForCompare.getCoverage());
                                  intent.putExtra("productPacking", "2 Pcs / Box");
                                  // Pass Product B (via extra intent logic or just let CompareActivity load it dynamically?
                                  // CompareActivity seems to expect A in extras and B via picker result.
                                  // Workaround: We will modify CompareActivity to accept B in extras too, OR just pass A and let user add B manually if this flow is strict.
                                  // Actually, to support "Compare A vs B" directly, we should pass B's ID or details too.
                                  // But CompareActivity currently only loads A from intent.
                                  
                                  // Let's pass A details as usual. And we need a way to pass B.
                                  // Since CompareActivity logic is "Load A, then Add B", we can just pass A.
                                  // BUT the user wants to compare "A vs B" immediately.
                                  // So we need to pass B's details too.
                                  // Let's add extras for Product B.
                                  intent.putExtra("productBName", product.getTileName());
                                  intent.putExtra("productBPrice", String.valueOf(product.getPrice()));
                                  intent.putExtra("productBCategory", product.getCategory());
                                  intent.putExtra("productBSize", product.getSize());
                                  intent.putExtra("productBFinish", product.getFinish());
                                  intent.putExtra("productBImage", product.getImage());
                                  intent.putExtra("productBTileNo", product.getTileNo());
                                  intent.putExtra("productBThickness", product.getThickness());
                                  intent.putExtra("productBCoverage", product.getCoverage());
                                  intent.putExtra("productBPacking", "2 Pcs / Box");
                                  
                                  startActivity(intent);
                                  selectedProductForCompare = null; // Reset
                              });
                              builder.setNegativeButton("Cancel", (dialog, which) -> {
                                  selectedProductForCompare = null; // Reset on cancel
                              });
                              builder.setNeutralButton("View Details", (dialog, which) -> {
                                  openProductDetails(product); 
                              });
                              builder.show();
                          } else {
                              openProductDetails(product);
                          }
                     }
                }

                @Override
                public void onItemLongClick(Product product) {
                     // Toast.makeText(CatalogActivity.this, "Long Pressed: " + product.getTileName(), Toast.LENGTH_SHORT).show(); 
                     boolean isPicker = getIntent().getBooleanExtra("IS_PICKER", false);
                     if (isPicker) {
                         // Selection Logic for Product B
                         Intent resultIntent = new Intent();
                         resultIntent.putExtra("productName", product.getTileName());
                         resultIntent.putExtra("productPrice", String.valueOf(product.getPrice()));
                         resultIntent.putExtra("productCategory", product.getCategory());
                         resultIntent.putExtra("productSize", product.getSize());
                         resultIntent.putExtra("productFinish", product.getFinish());
                         resultIntent.putExtra("productImage", product.getImage());
                         resultIntent.putExtra("productTileNo", product.getTileNo());
                         resultIntent.putExtra("productThickness", product.getThickness());
                         resultIntent.putExtra("productCoverage", product.getCoverage());
                         resultIntent.putExtra("productPacking", "2 Pcs / Box"); // Default
                         resultIntent.putExtra("productDescription", product.getDescription());
                         
                         setResult(RESULT_OK, resultIntent);
                         finish();
                     } else {
                         // Normal Mode: Select as Product A
                         selectedProductForCompare = product;
                         Toast.makeText(CatalogActivity.this, "Selected " + product.getTileName() + " for comparison. Now click another product.", Toast.LENGTH_LONG).show();
                     }
                }

                @Override
                public void onBookmarkClick(Product product) {
                    ApiService apiService = RetrofitClient.getApiService();
                    if (product.isFavorite()) {
                         // Add to favorites
                         java.util.Map<String, Integer> map = new java.util.HashMap<>();
                         map.put("product_id", product.getId());
                         android.util.Log.d("FavoritesDebug", "Adding Product ID: " + product.getId());
                         apiService.addToFavorites(map).enqueue(new Callback<okhttp3.ResponseBody>() {
                             @Override
                             public void onResponse(Call<okhttp3.ResponseBody> call, Response<okhttp3.ResponseBody> response) {
                                  if (response.isSuccessful()) {
                                      Toast.makeText(CatalogActivity.this, "Added to Favorites", Toast.LENGTH_SHORT).show();
                                  } else {
                                      try {
                                          String errorBody = response.errorBody() != null ? response.errorBody().string() : "Unknown error";
                                          android.util.Log.e("FavoritesDebug", "Add Failed: " + response.code() + " - " + errorBody);
                                          Toast.makeText(CatalogActivity.this, "Failed: " + response.code() + " " + errorBody, Toast.LENGTH_LONG).show();
                                      } catch (java.io.IOException e) {
                                          e.printStackTrace();
                                      }
                                  }
                             }
                             @Override
                             public void onFailure(Call<okhttp3.ResponseBody> call, Throwable t) {
                                 android.util.Log.e("FavoritesDebug", "Network Error: " + t.getMessage());
                                 Toast.makeText(CatalogActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                             }
                         });
                    } else {
                        // Remove from favorites
                        apiService.removeFromFavorites(product.getId()).enqueue(new Callback<okhttp3.ResponseBody>() {
                             @Override
                             public void onResponse(Call<okhttp3.ResponseBody> call, Response<okhttp3.ResponseBody> response) {
                                  if (response.isSuccessful()) {
                                      Toast.makeText(CatalogActivity.this, "Removed from Favorites", Toast.LENGTH_SHORT).show();
                                  } else {
                                      Toast.makeText(CatalogActivity.this, "Failed to remove", Toast.LENGTH_SHORT).show();
                                  }
                             }
                             @Override
                             public void onFailure(Call<okhttp3.ResponseBody> call, Throwable t) {
                                 Toast.makeText(CatalogActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                             }
                        });
                    }
                }

                @Override
                public void onAddToCartClick(Product product) {
                    Toast.makeText(CatalogActivity.this, "Added to cart", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void fetchProducts() {
        ApiService apiService = RetrofitClient.getApiService();
        // Passing null for search and category to get all, "all" for stock status
        // Ensure API supports these parameters or adjust as per ApiService definition
        // Based on ApiService.java seen earlier: getInventory(String search, String category, String finish)
        apiService.getInventory(null, null, null).enqueue(new Callback<InventoryResponse>() {
            @Override
            public void onResponse(Call<InventoryResponse> call, Response<InventoryResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().getProducts() != null) {
                        allProducts = response.body().getProducts();
                        tileAdapter.updateData(allProducts);
                        applyFilter(selectedFilter);
                    }
                } else {
                    Toast.makeText(CatalogActivity.this, "Failed to load catalog", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<InventoryResponse> call, Throwable t) {
                Toast.makeText(CatalogActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void applyFilter(String filterType) {
        if (tileAdapter == null) return;
        
        // Use adapter's filter method
        tileAdapter.filterByCategory(filterType);
    }

    private void goBackToHome() {
        Intent intent = new Intent(CatalogActivity.this, CustomerHomeActivity.class); // Or relevant home
        startActivity(intent);
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    private void openSearchScreen() {
        try {
            Intent intent = new Intent(CatalogActivity.this, SearchActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        } catch (Exception e) {
            Toast.makeText(this, "Cannot open Search", Toast.LENGTH_SHORT).show();
        }
    }

    // New navigation methods
    private void openQuotationsScreen() {
        try {
            Intent intent = new Intent(CatalogActivity.this, QuotationsActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        } catch (Exception e) {
            Toast.makeText(this, "Cannot open Quotations", Toast.LENGTH_SHORT).show();
        }
    }

    private void openAccountScreen() {
        try {
            Intent intent = new Intent(CatalogActivity.this, CustomerAccountActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        } catch (Exception e) {
            Toast.makeText(this, "Cannot open Account", Toast.LENGTH_SHORT).show();
        }
    }

    private void openProductDetails(Product product) {
        // Check if in Picker Mode
        if (getIntent().getBooleanExtra("IS_PICKER", false)) {
            Intent resultIntent = new Intent();
            resultIntent.putExtra("productName", product.getTileName());
            resultIntent.putExtra("productPrice", String.valueOf(product.getPrice()));
            resultIntent.putExtra("productCategory", product.getCategory());
            resultIntent.putExtra("productSize", product.getSize());
            resultIntent.putExtra("productFinish", product.getFinish());
            resultIntent.putExtra("productImage", product.getImage());
            resultIntent.putExtra("productTileNo", product.getTileNo());
            resultIntent.putExtra("productThickness", product.getThickness());
            resultIntent.putExtra("productCoverage", product.getCoverage());
            resultIntent.putExtra("productPacking", "2 Pcs / Box"); // Default as model lacks this field
            resultIntent.putExtra("productDescription", product.getDescription());
            
            setResult(RESULT_OK, resultIntent);
            finish();
            return;
        }

        try {
            Intent intent = new Intent(CatalogActivity.this, ProductDetailsActivity.class);

            // Pass known data with standardized keys
            intent.putExtra("tileName", product.getTileName());
            intent.putExtra("tilePrice", String.valueOf(product.getPrice()));
            intent.putExtra("productName", product.getTileName()); // Keep for compatibility
            intent.putExtra("productPrice", String.valueOf(product.getPrice())); // Keep for compatibility
            intent.putExtra("productStock", product.getStockStatus());
            intent.putExtra("tileStock", product.getStockStatus()); // Fallback key
            intent.putExtra("productCategory", product.getCategory());
            // Add other fields from product if available
            intent.putExtra("productSize", product.getSize());
            intent.putExtra("productFinish", product.getFinish());
            intent.putExtra("productImage", product.getImage());
            intent.putExtra("productDescription", product.getDescription());
            intent.putExtra("productTileNo", product.getTileNo());
            intent.putExtra("productId", product.getId());

            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        } catch (Exception e) {
            Toast.makeText(this, "Cannot open product details", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        goBackToHome();
    }
}
