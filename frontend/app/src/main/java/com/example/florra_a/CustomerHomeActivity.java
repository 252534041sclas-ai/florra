package com.example.florra_a;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.TextView;
//import com.example.florra_a.utils.ChatbotActivity;

import com.example.florra_a.models.Enquiry;
import com.example.florra_a.models.Product;
import com.example.florra_a.network.ApiService;
import com.example.florra_a.network.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CustomerHomeActivity extends AppCompatActivity implements TileAdapter.OnItemClickListener {

    private RecyclerView rvNewArrivals;
    private TileAdapter homeProductAdapter;
    private TextView tvActiveEnquiriesCount;

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

        setContentView(R.layout.activity_customer_home);

        Log.d("DEBUG", "CustomerHomeActivity loaded");

        setupViews();
        setupAllClickListeners();
        fetchDashboardData();
    }

    private void setupViews() {
        tvActiveEnquiriesCount = findViewById(R.id.tvActiveEnquiriesCount);
        rvNewArrivals = findViewById(R.id.rvNewArrivals);
        
        // Setup Welcome Message
        TextView tvWelcomeUser = findViewById(R.id.tvWelcomeUser);
        if (tvWelcomeUser != null) {
            android.content.SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
            String fullName = sharedPreferences.getString("full_name", "User");
            tvWelcomeUser.setText("Welcome, " + fullName);
        }
        
        // Setup Horizontal RecyclerView for New Arrivals
        if (rvNewArrivals != null) {
            rvNewArrivals.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
            homeProductAdapter = new TileAdapter(this, new ArrayList<>(), true); // true for horizontal
            homeProductAdapter.setOnItemClickListener(this);
            rvNewArrivals.setAdapter(homeProductAdapter);
        }
    }

    private void fetchDashboardData() {
        ApiService apiService = RetrofitClient.getApiService();

        // 1. Fetch Active Enquiries Count
        apiService.getEnquiries().enqueue(new Callback<List<Enquiry>>() {
            @Override
            public void onResponse(Call<List<Enquiry>> call, Response<List<Enquiry>> callResponse) {
                if (callResponse.isSuccessful() && callResponse.body() != null) {
                    List<Enquiry> enquiries = callResponse.body();
                    int activeCount = 0;
                    for (Enquiry e : enquiries) {
                        if (e.getStatus() != null && (e.getStatus().equalsIgnoreCase("Pending") || e.getStatus().equalsIgnoreCase("New") || e.getStatus().equalsIgnoreCase("Processing"))) {
                            activeCount++;
                        }
                    }
                    if (tvActiveEnquiriesCount != null) {
                        tvActiveEnquiriesCount.setText(activeCount + " Requests in Progress");
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Enquiry>> call, Throwable t) {
                // Ignore silent failure for dashboard
            }
        });

        // 2. Fetch New Arrivals (Products)
        apiService.getProducts().enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Product> allProducts = response.body();
                    // Take top 5 for "New Arrivals" (assuming list is sorted by date or just take first 5)
                    List<Product> newArrivals = new ArrayList<>();
                    for (int i = 0; i < Math.min(allProducts.size(), 5); i++) {
                        newArrivals.add(allProducts.get(i));
                    }
                    if (homeProductAdapter != null) {
                        homeProductAdapter.updateData(newArrivals);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                // Ignore silent failure
            }
        });
    }

    private void setupAllClickListeners() {
        Log.d("DEBUG", "Setting up all click listeners");

        // Catalog button - main navigation
        LinearLayout btnNavCatalog = findViewById(R.id.btnNavCatalog);
        if (btnNavCatalog != null) {
            btnNavCatalog.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openCatalogScreen();
                }
            });
        }

        // Other navigation buttons
        setupNavigationButtons();

        // Header buttons
        setupHeaderButtons();

        // Quick Actions
        setupQuickActions();

        // Collections
        setupCollections();
    }

    private void setupNavigationButtons() {
        // Home button
        LinearLayout btnNavHome = findViewById(R.id.btnNavHome);
        if (btnNavHome != null) {
            btnNavHome.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Already on home
                }
            });
        }

        // Enquiries button - UPDATED TO OPEN QUOTATIONS SCREEN
        LinearLayout btnNavEnquiries = findViewById(R.id.btnNavEnquiries);
        if (btnNavEnquiries != null) {
            btnNavEnquiries.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openQuotationsScreen(); // Changed from toast to opening screen
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

    private void setupHeaderButtons() {
        Log.d("DEBUG", "Setting up header buttons");

        RelativeLayout btnNotifications = findViewById(R.id.btnNotifications);
        if (btnNotifications != null) {
            Log.d("DEBUG", "Notification button found with ID: " + btnNotifications.getId());

            // Make absolutely sure it's clickable
            btnNotifications.setClickable(true);
            btnNotifications.setFocusable(true);
            btnNotifications.setFocusableInTouchMode(true);

            btnNotifications.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("DEBUG", "NOTIFICATION BUTTON CLICKED!");

                    // Direct intent - simplest possible
                    try {
                        Intent intent = new Intent(CustomerHomeActivity.this, NotificationsActivity.class);
                        startActivity(intent);
                        Log.d("DEBUG", "NotificationsActivity started");
                    } catch (Exception e) {
                        Log.e("DEBUG", "Error: " + e.getMessage());
                        Toast.makeText(CustomerHomeActivity.this, "Error opening notifications", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            Log.e("DEBUG", "Notification button NOT FOUND! Check your XML layout.");
        }

        // Chatbot button
        RelativeLayout btnChatbot = findViewById(R.id.btnChatbot);
        if (btnChatbot != null) {
            btnChatbot.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(CustomerHomeActivity.this, AIChatActivity.class);
                    startActivity(intent);
                }
            });
        }



        RelativeLayout btnSearch = findViewById(R.id.btnSearch);
        if (btnSearch != null) {
            btnSearch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openSearchScreen();
                }
            });
        }
    }

    private void setupQuickActions() {
        LinearLayout btnCalculator = findViewById(R.id.btnCalculator);
        if (btnCalculator != null) {
            btnCalculator.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openCalculatorScreen();
                }
            });
        }

        LinearLayout btnScanQR = findViewById(R.id.btnScanQR);
        if (btnScanQR != null) {
            btnScanQR.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openScanImageScreen();
                }
            });
        }

        LinearLayout btnStartEnquiry = findViewById(R.id.btnStartEnquiry);
        if (btnStartEnquiry != null) {
            btnStartEnquiry.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("FLORRA", "Favorites button clicked");
                    openFavoritesScreen();
                }
            });
        }

        LinearLayout btnSubscription = findViewById(R.id.btnSubscription);
        if (btnSubscription != null) {
            btnSubscription.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openSubscriptionScreen();
                }
            });
        }
    }

    // UPDATED: setupCollections() method
    private void setupCollections() {
        LinearLayout btnSeeAll = findViewById(R.id.btnSeeAll);
        if (btnSeeAll != null) {
            btnSeeAll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Open Catalog with all tiles
                    openCatalogWithFilter("all");
                }
            });
        }

        RelativeLayout cardFloorTiles = findViewById(R.id.cardFloorTiles);
        if (cardFloorTiles != null) {
            cardFloorTiles.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Open Catalog with floor tiles filter
                    openCatalogWithFilter("floor");
                }
            });
        }

        RelativeLayout cardWallTiles = findViewById(R.id.cardWallTiles);
        if (cardWallTiles != null) {
            cardWallTiles.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Open Catalog with wall tiles filter
                    openCatalogWithFilter("wall");
                }
            });
        }

        RelativeLayout cardBathroom = findViewById(R.id.cardBathroom);
        if (cardBathroom != null) {
            cardBathroom.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Open Catalog with bathroom tiles filter
                    openCatalogWithFilter("bathroom");
                }
            });
        }

        RelativeLayout cardKitchen = findViewById(R.id.cardKitchen);
        if (cardKitchen != null) {
            cardKitchen.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Open Catalog with kitchen tiles filter
                    openCatalogWithFilter("kitchen");
                }
            });
        }

        LinearLayout cardActiveEnquiries = findViewById(R.id.cardActiveEnquiries);
        if (cardActiveEnquiries != null) {
            cardActiveEnquiries.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(CustomerHomeActivity.this, "Active Enquiries", Toast.LENGTH_SHORT).show();
                    // Optionally open enquiries/quotations screen
                    openQuotationsScreen();
                }
            });
        }
    }

    // =============== SCREEN NAVIGATION METHODS ===============

    // UPDATED: openCatalogScreen() to use filter parameter
    private void openCatalogScreen() {
        openCatalogWithFilter("all"); // Default: show all tiles
    }

    // NEW METHOD: Open Catalog with specific filter
    private void openCatalogWithFilter(String filterType) {
        try {
            Intent intent = new Intent(CustomerHomeActivity.this, CatalogActivity.class);

            // Pass the filter type to CatalogActivity
            intent.putExtra("filter_type", filterType);

            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        } catch (Exception e) {
            Log.e("FLORRA", "Error opening Catalog with filter: " + e.getMessage());
        }
    }

    private void openFavoritesScreen() {
        try {
            Intent intent = new Intent(CustomerHomeActivity.this, FavoritesActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        } catch (Exception e) {
            Toast.makeText(this, "Cannot open Favorites", Toast.LENGTH_SHORT).show();
            Log.e("FLORRA", "Error opening Favorites: " + e.getMessage());
        }
    }

    private void openCalculatorScreen() {
        try {
            Intent intent = new Intent(CustomerHomeActivity.this, CalculatorActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        } catch (Exception e) {
            Toast.makeText(this, "Cannot open Calculator", Toast.LENGTH_SHORT).show();
            Log.e("FLORRA", "Error opening Calculator: " + e.getMessage());
        }
    }

    private void openScanImageScreen() {
        try {
            Intent intent = new Intent(CustomerHomeActivity.this, ScanImageActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        } catch (Exception e) {
            Toast.makeText(this, "Cannot open Scan Image", Toast.LENGTH_SHORT).show();
            Log.e("FLORRA", "Error opening Scan Image: " + e.getMessage());
        }
    }

    private void openAccountScreen() {
        try {
            Intent intent = new Intent(CustomerHomeActivity.this, CustomerAccountActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        } catch (Exception e) {
            Toast.makeText(this, "Cannot open Account", Toast.LENGTH_SHORT).show();
            Log.e("FLORRA", "Error opening Account: " + e.getMessage());
        }
    }

    private void openSearchScreen() {
        try {
            Intent intent = new Intent(CustomerHomeActivity.this, SearchActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        } catch (Exception e) {
            Toast.makeText(this, "Cannot open Search", Toast.LENGTH_SHORT).show();
            Log.e("FLORRA", "Error opening Search: " + e.getMessage());
        }
    }

    private void openNotificationsScreen() {
        try {
            Intent intent = new Intent(CustomerHomeActivity.this, NotificationsActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        } catch (Exception e) {
            Toast.makeText(this, "Cannot open Notifications", Toast.LENGTH_SHORT).show();
            Log.e("FLORRA", "Error opening Notifications: " + e.getMessage());
        }
    }

    // =============== NEW METHOD FOR QUOTATIONS SCREEN ===============

    private void openQuotationsScreen() {
        try {
            Intent intent = new Intent(CustomerHomeActivity.this, QuotationsActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        } catch (Exception e) {
            Toast.makeText(this, "Cannot open Quotations", Toast.LENGTH_SHORT).show();
            Log.e("FLORRA", "Error opening Quotations: " + e.getMessage());
        }
    }

    private void openSubscriptionScreen() {
        try {
            Intent intent = new Intent(CustomerHomeActivity.this, SubscriptionActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        } catch (Exception e) {
            Toast.makeText(this, "Cannot open Subscription", Toast.LENGTH_SHORT).show();
            Log.e("FLORRA", "Error opening Subscription: " + e.getMessage());
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
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Override
    public void onItemLongClick(Product product) {
        Toast.makeText(this, product.getTileName(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBookmarkClick(Product product) {
        android.content.SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean("is_logged_in", false);
        
        if (!isLoggedIn) {
            Toast.makeText(this, "Please login to add favorites", Toast.LENGTH_SHORT).show();
            // Revert state in adapter if possible, or just ignore since it's transient
            return;
        }

        boolean newState = product.isFavorite();
        com.example.florra_a.network.ApiService apiService = RetrofitClient.getApiService();
        
        if (newState) {
            java.util.Map<String, Integer> map = new java.util.HashMap<>();
            map.put("product_id", product.getId());
            apiService.addToFavorites(map).enqueue(new retrofit2.Callback<okhttp3.ResponseBody>() {
                @Override
                public void onResponse(retrofit2.Call<okhttp3.ResponseBody> call, retrofit2.Response<okhttp3.ResponseBody> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(CustomerHomeActivity.this, "Added to Favorites", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(CustomerHomeActivity.this, "Removed from Favorites", Toast.LENGTH_SHORT).show();
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
        finishAffinity();
    }
}
