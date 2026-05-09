    package com.example.florra_a;

    import android.content.Intent;
    import android.os.Bundle;
    import android.view.View;
    import android.view.Window;
    import android.view.WindowManager;
    import android.widget.Button;
    import android.widget.ImageView;
    import android.widget.Toast;
    import androidx.appcompat.app.AppCompatActivity;
    import androidx.core.view.WindowCompat;
    import androidx.core.view.WindowInsetsControllerCompat;
    import androidx.cardview.widget.CardView;

    public class AdminDashboardActivity extends AppCompatActivity {

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            // Set fullscreen
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            // Set status bar to dark teal to match header
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                getWindow().setStatusBarColor(android.graphics.Color.parseColor("#014D4E"));
                // Remove light status bar flag to keep icons white
                getWindow().getDecorView().setSystemUiVisibility(0);
            }

            // Handle notch and status bar appearance
            WindowInsetsControllerCompat windowInsetsController =
                    WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());
            if (windowInsetsController != null) {
                // False means light icons (white) on dark background
                windowInsetsController.setAppearanceLightStatusBars(false);
                windowInsetsController.setAppearanceLightNavigationBars(true);
            }

            setContentView(R.layout.activity_admin_dashboard);

            // Show toast
            Toast.makeText(this, "Admin Dashboard", Toast.LENGTH_SHORT).show();

            // Setup navigation
            setupNavigation();
        }

        private void setupNavigation() {
            // Dashboard button - Already on this screen
            View btnDashboard = findViewById(R.id.bottomDashboard);
            if (btnDashboard != null) {
                btnDashboard.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Already on dashboard, just show toast
                        Toast.makeText(AdminDashboardActivity.this, "You are already on Dashboard", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            // Catalog button
            View btnCatalog = findViewById(R.id.bottomInventory);
            if (btnCatalog != null) {
                btnCatalog.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // For now show toast, you can change later
                        Intent intent = new Intent(AdminDashboardActivity.this, AdminCatalogActivity.class);
                        startActivity(intent);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    }
                });
            }

            // Enquiries button
            View btnEnquiries = findViewById(R.id.bottomQuotes);
            if (btnEnquiries != null) {
                btnEnquiries.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(AdminDashboardActivity.this, EnquiriesActivity.class);
                        startActivity(intent);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    }
                });
            }

            // Account button
            View btnAccount = findViewById(R.id.bottomAccount);
            if (btnAccount != null) {
                btnAccount.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(AdminDashboardActivity.this, AdminAccountActivity.class);
                        startActivity(intent);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    }
                });
            }

            // ========== CARD CLICK LISTENERS ==========

            // TOTAL TILES CARD
            View cardTotalTiles = findViewById(R.id.cardTotalTiles);
            if (cardTotalTiles != null) {
                cardTotalTiles.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(AdminDashboardActivity.this, InventoryActivity.class);
                        startActivity(intent);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    }
                });
            }

            // ENQUIRIES CARD
            View cardEnquiries = findViewById(R.id.cardEnquiries);
            if (cardEnquiries != null) {
                cardEnquiries.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(AdminDashboardActivity.this, EnquiriesActivity.class);
                        startActivity(intent);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    }
                });
            }

            // QUOTATIONS CARD
            View cardQuotations = findViewById(R.id.cardQuotations);
            if (cardQuotations != null) {
                cardQuotations.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(AdminDashboardActivity.this, "Quotations screen coming soon", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            // LOW STOCK CARD
            View cardLowStock = findViewById(R.id.cardLowStock);
            if (cardLowStock != null) {
                cardLowStock.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(AdminDashboardActivity.this, InventoryActivity.class);
                        // Filter for low stock items
                        intent.putExtra("filter", "low_stock");
                        startActivity(intent);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    }
                });
            }

            // INVENTORY CARD (Management Section)
            View cardInventory = findViewById(R.id.cardInventory);
            if (cardInventory != null) {
                cardInventory.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(AdminDashboardActivity.this, InventoryActivity.class);
                        startActivity(intent);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    }
                });
            }

            // PRODUCT CARD
            View cardProduct = findViewById(R.id.cardProduct);
            if (cardProduct != null) {
                cardProduct.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(AdminDashboardActivity.this, ProductsActivity.class);
                        startActivity(intent);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    }
                });
            }

            // ENQUIRIES MANAGEMENT CARD
            View cardEnquiriesMgmt = findViewById(R.id.cardEnquiriesMgmt);
            if (cardEnquiriesMgmt != null) {
                cardEnquiriesMgmt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(AdminDashboardActivity.this, EnquiriesActivity.class);
                        startActivity(intent);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    }
                });
            }

            // QUOTATIONS MANAGEMENT CARD
            View cardQuotationsMgmt = findViewById(R.id.cardQuotationsMgmt);
            if (cardQuotationsMgmt != null) {
                cardQuotationsMgmt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(AdminDashboardActivity.this, "Quotations Management coming soon", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            // SALES PREDICTION CARD
            View cardSalesPrediction = findViewById(R.id.cardSalesPrediction);
            if (cardSalesPrediction != null) {
                cardSalesPrediction.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(AdminDashboardActivity.this, SalesPredictionActivity.class);
                        startActivity(intent);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    }
                });
            }

            // STOCK ALERTS CARD
            View cardStockAlerts = findViewById(R.id.cardStockAlerts);
            if (cardStockAlerts != null) {
                cardStockAlerts.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(AdminDashboardActivity.this, InventoryActivity.class);
                        intent.putExtra("filter", "low_stock");
                        startActivity(intent);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    }
                });
            }

            // ========== ACTIVITY ITEMS CLICK LISTENERS ==========
            // NOTE: Using the correct IDs from the updated XML

            // Activity Item 1: New Enquiry
            View cardActivityNewEnquiry = findViewById(R.id.cardActivityNewEnquiry);
            if (cardActivityNewEnquiry != null) {
                cardActivityNewEnquiry.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(AdminDashboardActivity.this, EnquiriesActivity.class);
                        startActivity(intent);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    }
                });
            }

            // Activity Item 2: Quotation Approved
            View cardActivityQuotationApproved = findViewById(R.id.cardActivityQuotationApproved);
            if (cardActivityQuotationApproved != null) {
                cardActivityQuotationApproved.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(AdminDashboardActivity.this, "View Quotation Details", Toast.LENGTH_SHORT).show();
                        // You can create a QuotationDetailsActivity later
                    }
                });
            }

            // Activity Item 3: Stock Updated
            View cardActivityStockUpdated = findViewById(R.id.cardActivityStockUpdated);
            if (cardActivityStockUpdated != null) {
                cardActivityStockUpdated.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(AdminDashboardActivity.this, InventoryActivity.class);
                        startActivity(intent);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    }
                });
            }

            // In AdminDashboardActivity.java, add this inside setupNavigation() method:

            // Generate Bill Card (previously Stock Alerts)
            View cardGenerateBill = findViewById(R.id.cardStockAlerts);
            if (cardStockAlerts != null) {
                cardStockAlerts.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(AdminDashboardActivity.this, GenerateBillActivity.class);
                        startActivity(intent);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    }
                });
            }

            // In setupNavigation() method of AdminDashboardActivity.java:

            // Quotations Management Card (now Saved Bills)
            View cardSavedBills = findViewById(R.id.cardQuotationsMgmt);
            if (cardSavedBills != null) {  // <-- Use the same variable name!
                cardSavedBills.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(AdminDashboardActivity.this, SavedBillsActivity.class);
                        startActivity(intent);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    }
                });
            }

            // ========== NOTIFICATIONS ==========

            // Notifications button
            View btnNotifications = findViewById(R.id.btnNotifications);
            if (btnNotifications != null) {
                btnNotifications.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(AdminDashboardActivity.this, "Notifications coming soon", Toast.LENGTH_SHORT).show();
                    }
                });
            }


        }

        private void showMenuOptions() {
            // Create a simple menu dialog
            String[] menuOptions = {
                    "Settings",
                    "Notifications",
                    "Help & Support",
                    "About",
                    "Logout"
            };

            androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
            builder.setTitle("Menu")
                    .setItems(menuOptions, (dialog, which) -> {
                        switch (which) {
                            case 0:
                                Toast.makeText(AdminDashboardActivity.this, "Settings coming soon", Toast.LENGTH_SHORT).show();
                                break;
                            case 1:
                                Toast.makeText(AdminDashboardActivity.this, "Notifications coming soon", Toast.LENGTH_SHORT).show();
                                break;
                            case 2:
                                Toast.makeText(AdminDashboardActivity.this, "Help & Support coming soon", Toast.LENGTH_SHORT).show();
                                break;
                            case 3:
                                Toast.makeText(AdminDashboardActivity.this, "About Florra App", Toast.LENGTH_SHORT).show();
                                break;
                            case 4:
                                performLogout();
                                break;
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        }

        private void performLogout() {
            // Clear shared preferences
            getSharedPreferences("user_prefs", MODE_PRIVATE).edit().clear().apply();

            // Show logout confirmation
            androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
            builder.setTitle("Logout")
                    .setMessage("Are you sure you want to logout?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();

                        // Navigate to Login screen
                        Intent intent = new Intent(this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        finish();
                    })
                    .setNegativeButton("No", null)
                    .show();
        }

        @Override
        public void onBackPressed() {
            // Show exit confirmation dialog
            androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
            builder.setTitle("Exit App")
                    .setMessage("Are you sure you want to exit the application?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        // Close the app
                        finishAffinity();
                        System.exit(0);
                    })
                    .setNegativeButton("No", null)
                    .show();
        }

        // Optional: Update dashboard data dynamically
        private void updateDashboardData() {
            // You can call this method to refresh dashboard data
            // For example, from a refresh button or onResume()

            // Update total tiles count
            // Update enquiries count
            // Update low stock count
            // etc.
        }

        @Override
        protected void onResume() {
            super.onResume();
            // Optional: Refresh data when returning to dashboard
            // updateDashboardData();
        }
    }