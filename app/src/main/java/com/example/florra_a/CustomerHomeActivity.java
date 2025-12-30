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

public class CustomerHomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set fullscreen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_customer_home);

        Log.d("DEBUG", "CustomerHomeActivity loaded");

        setupAllClickListeners();
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
                    // Toast.makeText(CustomerHomeActivity.this, "Enquiries", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(CustomerHomeActivity.this, "Opening notifications...", Toast.LENGTH_SHORT).show();

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
                    Toast.makeText(CustomerHomeActivity.this, "Opening Favorites...", Toast.LENGTH_SHORT).show();
                    openFavoritesScreen();
                }
            });
        }
    }

    private void setupCollections() {
        LinearLayout btnSeeAll = findViewById(R.id.btnSeeAll);
        if (btnSeeAll != null) {
            btnSeeAll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(CustomerHomeActivity.this, "See All Collections", Toast.LENGTH_SHORT).show();
                }
            });
        }

        RelativeLayout cardFloorTiles = findViewById(R.id.cardFloorTiles);
        if (cardFloorTiles != null) {
            cardFloorTiles.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(CustomerHomeActivity.this, "Floor Tiles", Toast.LENGTH_SHORT).show();
                }
            });
        }

        RelativeLayout cardWallTiles = findViewById(R.id.cardWallTiles);
        if (cardWallTiles != null) {
            cardWallTiles.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(CustomerHomeActivity.this, "Wall Tiles", Toast.LENGTH_SHORT).show();
                }
            });
        }

        RelativeLayout cardBathroom = findViewById(R.id.cardBathroom);
        if (cardBathroom != null) {
            cardBathroom.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(CustomerHomeActivity.this, "Bathroom", Toast.LENGTH_SHORT).show();
                }
            });
        }

        RelativeLayout cardKitchen = findViewById(R.id.cardKitchen);
        if (cardKitchen != null) {
            cardKitchen.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(CustomerHomeActivity.this, "Kitchen", Toast.LENGTH_SHORT).show();
                }
            });
        }


        LinearLayout cardActiveEnquiries = findViewById(R.id.cardActiveEnquiries);
        if (cardActiveEnquiries != null) {
            cardActiveEnquiries.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(CustomerHomeActivity.this, "Active Enquiries", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    // =============== SCREEN NAVIGATION METHODS ===============

    private void openCatalogScreen() {
        try {
            Intent intent = new Intent(CustomerHomeActivity.this, CatalogActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        } catch (Exception e) {
            Toast.makeText(this, "Cannot open Catalog", Toast.LENGTH_SHORT).show();
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

    @Override
    public void onBackPressed() {
        finishAffinity();
    }
}