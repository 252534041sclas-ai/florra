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
    import com.example.florra_a.utils.SharedPrefManager;

    public class AdminDashboardActivity extends AppCompatActivity {

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            // Set fullscreen
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            // Set status bar to dark teal to match header
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                getWindow().setStatusBarColor(android.graphics.Color.parseColor("#000000"));
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

            // Load profile image in header
            loadDashboardProfile();

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

            // Catalog button (Modernized Admin Catalog)
            View btnCatalog = findViewById(R.id.bottomInventory);
            if (btnCatalog != null) {
                btnCatalog.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
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

            // NOTIFICATION BELL (Header)
            View btnNotifications = findViewById(R.id.btnNotifications);
            if (btnNotifications != null) {
                btnNotifications.setOnClickListener(v -> {
                    Intent intent = new Intent(AdminDashboardActivity.this, AdminNotificationsActivity.class);
                    startActivity(intent);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
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
                        Intent intent = new Intent(AdminDashboardActivity.this, SavedBillsActivity.class);
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

            // PRODUCT CARD (Linked to Products)
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
                        Intent intent = new Intent(AdminDashboardActivity.this, AdminCustomerListActivity.class);
                        startActivity(intent);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    }
                });
            }

            // REPORTS MANAGEMENT CARD
            View btnReportsDashboard = findViewById(R.id.btnReportsDashboard);
            if (btnReportsDashboard != null) {
                btnReportsDashboard.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SharedPrefManager pref = SharedPrefManager.getInstance(AdminDashboardActivity.this);
                        if ("staff".equalsIgnoreCase(pref.getRole()) && !pref.canAccessReports()) {
                            Toast.makeText(AdminDashboardActivity.this, "Access Denied: You do not have permission to access reports", Toast.LENGTH_LONG).show();
                        } else {
                            Intent intent = new Intent(AdminDashboardActivity.this, AdminReportsActivity.class);
                            startActivity(intent);
                            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        }
                    }
                });
            }

            // SALES PREDICTION CARD
            View cardSalesPrediction = findViewById(R.id.cardSalesPrediction);
            if (cardSalesPrediction != null) {
                cardSalesPrediction.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SharedPrefManager pref = SharedPrefManager.getInstance(AdminDashboardActivity.this);
                        if ("staff".equalsIgnoreCase(pref.getRole()) && !pref.canAccessPredictions()) {
                            Toast.makeText(AdminDashboardActivity.this, "Access Denied: You do not have permission to access sales predictions", Toast.LENGTH_LONG).show();
                        } else {
                            Intent intent = new Intent(AdminDashboardActivity.this, SalesPredictionActivity.class);
                            startActivity(intent);
                            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        }
                    }
                });
            }

            // STOCK ALERTS CARD
            View cardBilling = findViewById(R.id.cardStockAlerts);
            if (cardBilling != null) {
                cardBilling.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SharedPrefManager pref = SharedPrefManager.getInstance(AdminDashboardActivity.this);
                        if ("staff".equalsIgnoreCase(pref.getRole()) && !pref.canAccessBilling()) {
                            Toast.makeText(AdminDashboardActivity.this, "Access Denied: You do not have permission to generate bills", Toast.LENGTH_LONG).show();
                        } else {
                            Intent intent = new Intent(AdminDashboardActivity.this, GenerateBillActivity.class);
                            startActivity(intent);
                            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        }
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
            com.example.florra_a.network.ApiService apiService = 
                com.example.florra_a.network.RetrofitClient.getApiService();

            // Fetch Inventory Stats
            apiService.getInventory(null, null, null).enqueue(new retrofit2.Callback<com.example.florra_a.models.InventoryResponse>() {
                @Override
                public void onResponse(retrofit2.Call<com.example.florra_a.models.InventoryResponse> call, 
                                       retrofit2.Response<com.example.florra_a.models.InventoryResponse> response) {
                    if (response.isSuccessful() && response.body() != null && response.body().getStats() != null) {
                        int total = response.body().getStats().getTotal();
                        android.widget.TextView tvCatalog = findViewById(R.id.tvTotalTilesCount);
                        if (tvCatalog != null) tvCatalog.setText(total + " Products");
                        
                        // Update Recent Activity 3
                        View card3 = findViewById(R.id.cardActivityStockUpdated);
                        if (card3 instanceof android.widget.LinearLayout) {
                            android.widget.TextView tv = (android.widget.TextView) ((android.widget.LinearLayout)card3).getChildAt(0);
                            tv.setText("Inventory Synced (" + total + " Items)");
                        }
                    }
                }
                @Override
                public void onFailure(retrofit2.Call<com.example.florra_a.models.InventoryResponse> call, Throwable t) {}
            });

            // Fetch Enquiries
            apiService.getEnquiries().enqueue(new retrofit2.Callback<java.util.List<com.example.florra_a.models.Enquiry>>() {
                @Override
                public void onResponse(retrofit2.Call<java.util.List<com.example.florra_a.models.Enquiry>> call, 
                                       retrofit2.Response<java.util.List<com.example.florra_a.models.Enquiry>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        int count = response.body().size();
                        android.widget.TextView tvEnquiries = findViewById(R.id.tvEnquiriesCount);
                        if (tvEnquiries != null) tvEnquiries.setText(count + " Enquiries");
                        
                        // Update Recent Activity 1
                        if (count > 0) {
                            // Backend returns newest last or first, we just pick the last one as a fallback or the first.
                            // Let's pick the last element in the list just in case it's appended
                            com.example.florra_a.models.Enquiry latest = response.body().get(count - 1);
                            View card1 = findViewById(R.id.cardActivityNewEnquiry);
                            if (card1 instanceof android.widget.LinearLayout) {
                                android.widget.TextView tv = (android.widget.TextView) ((android.widget.LinearLayout)card1).getChildAt(0);
                                tv.setText("New Enquiry: " + latest.getCustomerName());
                            }
                        } else {
                            View card1 = findViewById(R.id.cardActivityNewEnquiry);
                            if (card1 != null) card1.setVisibility(View.GONE);
                        }
                    }
                }
                @Override
                public void onFailure(retrofit2.Call<java.util.List<com.example.florra_a.models.Enquiry>> call, Throwable t) {}
            });

            // Fetch Bills (Pending Bills)
            apiService.getBills().enqueue(new retrofit2.Callback<java.util.List<com.example.florra_a.models.Bill>>() {
                @Override
                public void onResponse(retrofit2.Call<java.util.List<com.example.florra_a.models.Bill>> call, 
                                       retrofit2.Response<java.util.List<com.example.florra_a.models.Bill>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        int count = response.body().size();
                        android.widget.TextView tvPending = findViewById(R.id.tvPendingBillsCount);
                        if (tvPending != null) tvPending.setText(count + " Bills");
                        
                        // Calculate Revenue (MTD) - Simple sum for demo
                        double revenue = 0;
                        for (com.example.florra_a.models.Bill bill : response.body()) {
                            revenue += bill.getGrandTotal();
                        }
                        android.widget.TextView tvRevenue = findViewById(R.id.tvRevenueMtd);
                        if (tvRevenue != null) {
                            tvRevenue.setText("₹" + String.format(java.util.Locale.getDefault(), "%,.0f", revenue));
                        }
                        
                        // Update Recent Activity 2
                        if (count > 0) {
                            com.example.florra_a.models.Bill latest = response.body().get(count - 1);
                            View card2 = findViewById(R.id.cardActivityQuotationApproved);
                            if (card2 instanceof android.widget.LinearLayout) {
                                android.widget.TextView tv = (android.widget.TextView) ((android.widget.LinearLayout)card2).getChildAt(0);
                                tv.setText("Bill Generated: " + latest.getBillNo());
                            }
                        } else {
                            View card2 = findViewById(R.id.cardActivityQuotationApproved);
                            if (card2 != null) card2.setVisibility(View.GONE);
                        }
                    }
                }
                @Override
                public void onFailure(retrofit2.Call<java.util.List<com.example.florra_a.models.Bill>> call, Throwable t) {}
            });
        }

        @Override
        protected void onResume() {
            super.onResume();
            loadDashboardProfile();
            updateDashboardData();
        }

        private void loadDashboardProfile() {
            ImageView ivDashboardProfile = findViewById(R.id.ivDashboardProfile);
            if (ivDashboardProfile == null) return;

            SharedPrefManager prefManager = SharedPrefManager.getInstance(this);
            String profileImageUrl = prefManager.getProfileImage();

            if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
                // Has a real profile image — load with Picasso
                String fullUrl = profileImageUrl.startsWith("http") ? profileImageUrl
                        : com.example.florra_a.network.RetrofitClient.BASE_URL + profileImageUrl;
                com.squareup.picasso.Picasso.get()
                        .load(fullUrl)
                        .placeholder(R.drawable.ic_profile_placeholder)
                        .error(R.drawable.ic_profile_placeholder)
                        .into(ivDashboardProfile);
            } else {
                // No profile image — draw letter avatar locally
                String fullName = prefManager.getFullName();
                if (fullName == null || fullName.trim().isEmpty()) fullName = "Admin";
                String[] parts = fullName.trim().split("\\s+");
                String initials = parts.length >= 2
                        ? String.valueOf(parts[0].charAt(0)).toUpperCase() + String.valueOf(parts[1].charAt(0)).toUpperCase()
                        : String.valueOf(parts[0].charAt(0)).toUpperCase();

                int size = 128;
                android.graphics.Bitmap bitmap = android.graphics.Bitmap.createBitmap(size, size, android.graphics.Bitmap.Config.ARGB_8888);
                android.graphics.Canvas canvas = new android.graphics.Canvas(bitmap);

                android.graphics.Paint bgPaint = new android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG);
                bgPaint.setColor(android.graphics.Color.parseColor("#334155"));
                canvas.drawCircle(size / 2f, size / 2f, size / 2f, bgPaint);

                android.graphics.Paint textPaint = new android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG);
                textPaint.setColor(android.graphics.Color.WHITE);
                textPaint.setTextSize(initials.length() > 1 ? 44f : 52f);
                textPaint.setTextAlign(android.graphics.Paint.Align.CENTER);
                textPaint.setTypeface(android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.BOLD));

                android.graphics.Rect bounds = new android.graphics.Rect();
                textPaint.getTextBounds(initials, 0, initials.length(), bounds);
                canvas.drawText(initials, size / 2f, size / 2f - bounds.exactCenterY(), textPaint);

                ivDashboardProfile.setImageBitmap(bitmap);
            }
        }
    }