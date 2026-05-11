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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.os.Handler;
import android.widget.TextView;
//import com.example.florra_a.utils.ChatbotActivity;

import com.example.florra_a.adapters.HomeProductAdapter;
import com.example.florra_a.models.Enquiry;
import com.example.florra_a.models.Product;
import com.example.florra_a.network.ApiService;
import com.example.florra_a.network.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CustomerHomeActivity extends AppCompatActivity {

    private RecyclerView rvNewArrivals;
    private HomeProductAdapter homeProductAdapter;
    private TextView tvActiveEnquiriesCount;
    private View viewChatbotRainbow, viewScanRainbow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set status bar to white with dark icons
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(android.graphics.Color.WHITE);
        }
        setContentView(R.layout.activity_customer_home);

        Log.d("DEBUG", "CustomerHomeActivity loaded");

        setupViews();
        setupAllClickListeners();
        fetchDashboardData();
        startRainbowAnimations();
    }

    private void setupViews() {
        tvActiveEnquiriesCount = findViewById(R.id.tvActiveEnquiriesCount);
        rvNewArrivals = findViewById(R.id.rvNewArrivals);
        
        // Setup Welcome Message
        TextView tvWelcomeUser = findViewById(R.id.tvWelcomeUser);
        if (tvWelcomeUser != null) {
            String fullName = com.example.florra_a.utils.SharedPrefManager.getInstance(this).getFullName();
            tvWelcomeUser.setText("Welcome, " + (fullName != null ? fullName : "User"));
        }
        
        // Setup Horizontal RecyclerView for New Arrivals
        if (rvNewArrivals != null) {
            rvNewArrivals.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
            homeProductAdapter = new HomeProductAdapter(this, new ArrayList<>());
            rvNewArrivals.setAdapter(homeProductAdapter);
        }

        viewChatbotRainbow = findViewById(R.id.viewChatbotRainbow);
        viewScanRainbow = findViewById(R.id.viewScanRainbow);
    }

    private void startRainbowAnimations() {
        if (viewChatbotRainbow == null || viewScanRainbow == null) return;

        Animation rotateAnim = AnimationUtils.loadAnimation(this, R.anim.rotate_infinite);

        // Show and start animation
        viewChatbotRainbow.setVisibility(View.VISIBLE);
        viewScanRainbow.setVisibility(View.VISIBLE);
        
        viewChatbotRainbow.startAnimation(rotateAnim);
        viewScanRainbow.startAnimation(rotateAnim);

        // Stop after 3 seconds
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                viewChatbotRainbow.clearAnimation();
                viewScanRainbow.clearAnimation();
                viewChatbotRainbow.setVisibility(View.GONE);
                viewScanRainbow.setVisibility(View.GONE);
            }
        }, 3000);
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
                    Log.d("DEBUG", "Chatbot button clicked!");
                    Toast.makeText(CustomerHomeActivity.this, "Opening AI Assistant...", Toast.LENGTH_SHORT).show();
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
    public void onBackPressed() {
        finishAffinity();
    }
}