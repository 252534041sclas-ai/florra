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

public class SalesPredictionActivity extends AppCompatActivity {

    private android.widget.TextView tvPredictedSales, tvEstRevenue, tvGrowthPercentage;
    private android.widget.TextView tvHighDemandProduct, tvLowDemandProduct, tvStockSuggestion, tvChartValue;
    private android.widget.TextView tvHighDemandDesc, tvLowDemandDesc; 
    private android.widget.TextView tvFilterTime, tvFilterCategory;
    private androidx.cardview.widget.CardView cardFilterTime, cardFilterCategory;
    private android.widget.TextView tvTrendName1, tvTrendValue1, tvTrendName2, tvTrendValue2;
    private android.widget.TextView btnToggleActual, btnTogglePredicted; 
    private View bar1, bar2, bar3, bar4, bar5;
    private View loadingOverlay;
    
    private boolean isPredictedMode = true;
    private com.example.florra_a.models.SalesPredictionResponse currentData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set status bar to white with dark icons
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(android.view.View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(android.graphics.Color.WHITE);
        }

        setContentView(R.layout.activity_sales_prediction);

        initializeViews();
        setupNavigation();
        setupToggle(); 
        fetchSalesPrediction();
    }

    private void initializeViews() {
        tvPredictedSales = findViewById(R.id.tvPredictedSales);
        tvEstRevenue = findViewById(R.id.tvEstRevenue);
        tvGrowthPercentage = findViewById(R.id.tvGrowthPercentage);
        tvHighDemandProduct = findViewById(R.id.tvHighDemandProduct);
        tvLowDemandProduct = findViewById(R.id.tvLowDemandProduct);
        tvHighDemandDesc = findViewById(R.id.tvHighDemandDesc); // New
        tvLowDemandDesc = findViewById(R.id.tvLowDemandDesc);   // New
        tvStockSuggestion = findViewById(R.id.tvStockSuggestion);
        tvChartValue = findViewById(R.id.tvChartValue);
        
        // Chart Bars
        bar1 = findViewById(R.id.bar1);
        bar2 = findViewById(R.id.bar2);
        bar3 = findViewById(R.id.bar3);
        bar4 = findViewById(R.id.bar4);
        bar5 = findViewById(R.id.bar5);

        // Filters
        tvFilterTime = findViewById(R.id.tvFilterTime);
        cardFilterTime = findViewById(R.id.cardFilterTime);
        tvFilterCategory = findViewById(R.id.tvFilterCategory);
        cardFilterCategory = findViewById(R.id.cardFilterCategory);
        
        // Market Trends
        tvTrendName1 = findViewById(R.id.tvTrendName1);
        tvTrendValue1 = findViewById(R.id.tvTrendValue1);
        tvTrendName2 = findViewById(R.id.tvTrendName2);
        tvTrendValue2 = findViewById(R.id.tvTrendValue2);
        
        // Toggle
        btnToggleActual = findViewById(R.id.btnToggleActual);
        btnTogglePredicted = findViewById(R.id.btnTogglePredicted);
        
        loadingOverlay = findViewById(R.id.loadingOverlay);
    }
    
    private void fetchSalesPrediction() {
        if (loadingOverlay != null) loadingOverlay.setVisibility(View.VISIBLE);
        
        String category = tvFilterCategory != null ? tvFilterCategory.getText().toString() : null;
        if ("All Categories".equals(category)) category = null;

        com.example.florra_a.network.ApiService apiService = 
            com.example.florra_a.network.RetrofitClient.getApiService();

        apiService.getSalesPrediction(category).enqueue(new retrofit2.Callback<com.example.florra_a.models.SalesPredictionResponse>() {
            @Override
            public void onResponse(retrofit2.Call<com.example.florra_a.models.SalesPredictionResponse> call, 
                                   retrofit2.Response<com.example.florra_a.models.SalesPredictionResponse> response) {
                if (loadingOverlay != null) loadingOverlay.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    currentData = response.body();
                    updateUI();
                } else {
                    Toast.makeText(SalesPredictionActivity.this, "No data for this selection", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(retrofit2.Call<com.example.florra_a.models.SalesPredictionResponse> call, Throwable t) {
                if (loadingOverlay != null) loadingOverlay.setVisibility(View.GONE);
                Toast.makeText(SalesPredictionActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUI() {
        if (currentData == null) return;
        
        if (isPredictedMode) {
            // Show Predicted Data
            setValues(
                currentData.getPredictedSales(),
                currentData.getEstimatedRevenue(),
                currentData.getGrowthPercentage()
            );
            
            // Global (Predicted) Demand
            if (tvHighDemandProduct != null) 
                tvHighDemandProduct.setText(currentData.getHighDemandProduct().isEmpty() ? "N/A" : currentData.getHighDemandProduct());
                
            if (tvHighDemandDesc != null) {
                String tileNo = currentData.getHighDemandTileNo();
                tvHighDemandDesc.setText(tileNo != null && !tileNo.isEmpty() ? "Tile: " + tileNo : "");
            }
                
            if (tvLowDemandProduct != null) 
                tvLowDemandProduct.setText(currentData.getLowDemandProduct().isEmpty() ? "N/A" : currentData.getLowDemandProduct());

            if (tvLowDemandDesc != null) {
                String tileNo = currentData.getLowDemandTileNo();
                tvLowDemandDesc.setText(tileNo != null && !tileNo.isEmpty() ? "Tile: " + tileNo : "");
            }

        } else {
            // Show Actual Data based on Filter
            String filter = tvFilterTime.getText().toString();
            com.example.florra_a.models.SalesPredictionResponse.ActualData actual = currentData.getActualData();
            
            if (actual != null) {
                com.example.florra_a.models.SalesPredictionResponse.TimeRangeData rangeData = null;
                
                if (filter.contains("This Month") || filter.equals("Monthly")) rangeData = actual.getThisMonth();
                else if (filter.contains("Last Month") || filter.equals("Past Month")) rangeData = actual.getLastMonth();
                else if (filter.contains("Last 3 Months")) rangeData = actual.getLast3Months();
                else if (filter.contains("Yearly")) rangeData = actual.getYearly();
                else rangeData = actual.getThisMonth(); // Fallback
                
                if (rangeData != null) {
                     setValues((int)rangeData.getSales(), rangeData.getRevenue(), 0); // No growth % for raw actuals yet
                     
                     // Helper Function to safely get strings
                     String high = rangeData.getHighDemandProduct();
                     String low = rangeData.getLowDemandProduct();
                     String highTile = rangeData.getHighDemandTileNo();
                     String lowTile = rangeData.getLowDemandTileNo();
                     
                     if (tvHighDemandProduct != null) 
                        tvHighDemandProduct.setText(high != null && !high.isEmpty() ? high : "N/A");

                     if (tvHighDemandDesc != null)
                        tvHighDemandDesc.setText(highTile != null && !highTile.isEmpty() ? "Tile: " + highTile : "");
                        
                     if (tvLowDemandProduct != null) 
                        tvLowDemandProduct.setText(low != null && !low.isEmpty() ? low : "N/A");

                     if (tvLowDemandDesc != null)
                        tvLowDemandDesc.setText(lowTile != null && !lowTile.isEmpty() ? "Tile: " + lowTile : "");

                     // Render Graph for Actual Data
                     if (rangeData.getGraphData() != null && rangeData.getGraphData().size() >= 5 && bar1 != null) {
                         java.util.List<Double> dataPoints = rangeData.getGraphData();
                         float v1 = dataPoints.get(0).floatValue();
                         float v2 = dataPoints.get(1).floatValue();
                         float v3 = dataPoints.get(2).floatValue();
                         float v4 = dataPoints.get(3).floatValue();
                         float v5 = dataPoints.get(4).floatValue();
                         
                         float max = Math.max(v1, Math.max(v2, Math.max(v3, Math.max(v4, v5))));
                         if (max == 0) max = 1;
            
                         setBarHeight(bar1, v1, max);
                         setBarHeight(bar2, v2, max);
                         setBarHeight(bar3, v3, max);
                         setBarHeight(bar4, v4, max);
                         setBarHeight(bar5, v5, max);
                     } else {
                         // Reset or keep previous if no data
                         if (bar1 != null) {
                             setBarHeight(bar1, 0, 1);
                             setBarHeight(bar2, 0, 1);
                             setBarHeight(bar3, 0, 1);
                             setBarHeight(bar4, 0, 1);
                             setBarHeight(bar5, 0, 1);
                         }
                     }

                } else {
                     setValues(0, 0, 0);
                     if (tvHighDemandProduct != null) tvHighDemandProduct.setText("N/A");
                     if (tvHighDemandDesc != null) tvHighDemandDesc.setText("");
                     if (tvLowDemandProduct != null) tvLowDemandProduct.setText("N/A");
                     if (tvLowDemandDesc != null) tvLowDemandDesc.setText("");
                     
                     // Clear Graph
                     if (bar1 != null) {
                         setBarHeight(bar1, 0, 1);
                         setBarHeight(bar2, 0, 1);
                         setBarHeight(bar3, 0, 1);
                         setBarHeight(bar4, 0, 1);
                         setBarHeight(bar5, 0, 1);
                     }
                }
            }
        }

        if (tvStockSuggestion != null) 
            tvStockSuggestion.setText("Alert: " + (currentData.getStockSuggestion() != null ? currentData.getStockSuggestion() : "N/A"));

        // Update Market Trends
        if (currentData.getMarketTrends() != null) {
            java.util.List<com.example.florra_a.models.SalesPredictionResponse.MarketTrend> trends = currentData.getMarketTrends();
            if (trends.size() > 0 && tvTrendName1 != null) {
                tvTrendName1.setText(trends.get(0).getName());
                tvTrendValue1.setText(trends.get(0).getValue());
            }
            if (trends.size() > 1 && tvTrendName2 != null) {
                tvTrendName2.setText(trends.get(1).getName());
                tvTrendValue2.setText(trends.get(1).getValue());
            }
        }
        
        // Update Chart Value Text
        double val = isPredictedMode ? currentData.getPredictedSales() : 
                     (Double.parseDouble(tvPredictedSales.getText().toString())); 
                     // Hacky, better to use the setValues source.
                     
        // Only update chart from main response if in PREDICTED mode
        if (isPredictedMode && currentData.getChart() != null && bar1 != null) {
            float v1 = currentData.getChart().get("week1") != null ? currentData.getChart().get("week1") : 0;
            float v2 = currentData.getChart().get("week2") != null ? currentData.getChart().get("week2") : 0;
            float v3 = currentData.getChart().get("today") != null ? currentData.getChart().get("today") : 0;
            float v4 = currentData.getChart().get("week4") != null ? currentData.getChart().get("week4") : 0;
            float v5 = currentData.getChart().get("week5") != null ? currentData.getChart().get("week5") : 0;
            
            float max = Math.max(v1, Math.max(v2, Math.max(v3, Math.max(v4, v5))));
            if (max == 0) max = 1;

            setBarHeight(bar1, v1, max);
            setBarHeight(bar2, v2, max);
            setBarHeight(bar3, v3, max);
            setBarHeight(bar4, v4, max);
            setBarHeight(bar5, v5, max);
            
            // Set predicted colors
            if (bar1 != null) {
                bar1.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#d4d4d8"))); // Zinc 300
                bar2.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#d4d4d8")));
                bar3.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#014D4E"))); // Primary
                bar4.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#93c5fd"))); // Blue 300
                bar5.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#93c5fd")));
            }
        } else if (!isPredictedMode && bar1 != null) {
            // Set actual colors
            bar1.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#71717a"))); // Zinc 500
            bar2.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#71717a")));
            bar3.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#71717a")));
            bar4.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#71717a")));
            bar5.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#71717a")));
        }
    }

    private void setValues(int sales, double revenue, double growth) {
        if (tvPredictedSales != null) 
            tvPredictedSales.setText(java.text.NumberFormat.getNumberInstance().format(sales));
            
        if (tvEstRevenue != null) {
            String formattedRevenue = java.text.NumberFormat.getCurrencyInstance(new java.util.Locale("en", "IN")).format(revenue);
            tvEstRevenue.setText(formattedRevenue);
        }
            
        if (tvChartValue != null) 
            tvChartValue.setText(sales >= 1000 ? (String.format("%.1fk", sales/1000.0)) : String.valueOf(sales));

        if (tvGrowthPercentage != null) {
            if (isPredictedMode) {
                String sign = growth >= 0 ? "+" : "";
                tvGrowthPercentage.setText(sign + String.format("%.1f%%", growth));
                tvGrowthPercentage.setVisibility(View.VISIBLE);
                
                View growthParent = (View) tvGrowthPercentage.getParent();
                if (growth < 0) {
                     tvGrowthPercentage.setTextColor(android.graphics.Color.parseColor("#dc2626")); // Red-600
                     if (growthParent != null) growthParent.setBackgroundResource(R.drawable.bg_red_badge);
                } else {
                     tvGrowthPercentage.setTextColor(android.graphics.Color.parseColor("#15803d")); // Green-700
                     if (growthParent != null) growthParent.setBackgroundResource(R.drawable.bg_green_badge);
                }
            } else {
                tvGrowthPercentage.setVisibility(View.INVISIBLE);
            }
        }
    }

    private void setBarHeight(View bar, float value, float max) {
        android.widget.LinearLayout.LayoutParams params = (android.widget.LinearLayout.LayoutParams) bar.getLayoutParams();
        int maxHeightDp = 150;
        float density = getResources().getDisplayMetrics().density;
        int targetHeight = (int) ((value / max) * maxHeightDp * density);
        if (targetHeight < 8 * density) targetHeight = (int) (8 * density);
        
        // Animate from current height
        int startHeight = params.height;
        if (startHeight < 0) startHeight = 0;
        
        final int finalHeight = targetHeight;
        android.animation.ValueAnimator animator = android.animation.ValueAnimator.ofInt(startHeight, finalHeight);
        animator.setDuration(500);
        animator.setInterpolator(new android.view.animation.DecelerateInterpolator());
        animator.addUpdateListener(animation -> {
            params.height = (int) animation.getAnimatedValue();
            bar.setLayoutParams(params);
        });
        animator.start();
    }

    private void setupNavigation() {
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

        // More options button
        ImageView btnMore = findViewById(R.id.btnMore);
        if (btnMore != null) {
            btnMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(SalesPredictionActivity.this, "More options", Toast.LENGTH_SHORT).show();
                }
            });
        }

        // Create PO button
        Button btnCreatePO = findViewById(R.id.btnCreatePO);
        if (btnCreatePO != null) {
            btnCreatePO.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(SalesPredictionActivity.this, "Purchase Order created", Toast.LENGTH_SHORT).show();
                }
            });
        }

        // Filter Click Listener
        if (cardFilterTime != null) {
            cardFilterTime.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showTimeFilterPopup(v);
                }
            });
        }
        
        if (cardFilterCategory != null) {
            cardFilterCategory.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showCategoryFilterPopup(v);
                }
            });
        }
    }

    private void showTimeFilterPopup(View v) {
        android.widget.PopupMenu popup = new android.widget.PopupMenu(this, v);
        
        if (isPredictedMode) {
             popup.getMenu().add("Monthly"); 
        } else {
             popup.getMenu().add("This Month"); 
             popup.getMenu().add("Past Month");
             popup.getMenu().add("Last 3 Months");
             popup.getMenu().add("Yearly");
        }

        popup.setOnMenuItemClickListener(new android.widget.PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(android.view.MenuItem item) {
                String selectedTime = item.getTitle().toString();
                if (tvFilterTime != null) {
                    tvFilterTime.setText(selectedTime);
                }
                
                if (!isPredictedMode) {
                    updateUI(); 
                }
                return true;
            }
        });

        popup.show();
    }

    private void showCategoryFilterPopup(View v) {
        android.widget.PopupMenu popup = new android.widget.PopupMenu(this, v);
        popup.getMenu().add("All Categories");
        popup.getMenu().add("Porcelain");
        popup.getMenu().add("Ceramic");
        popup.getMenu().add("Marble");
        popup.getMenu().add("Vitrified");
        popup.getMenu().add("Granite");

        popup.setOnMenuItemClickListener(new android.widget.PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(android.view.MenuItem item) {
                String selectedCategory = item.getTitle().toString();
                if (tvFilterCategory != null) {
                    tvFilterCategory.setText(selectedCategory);
                }
                fetchSalesPrediction(); 
                return true;
            }
        });

        popup.show();
    }
    
    private void setupToggle() {
        if (btnToggleActual != null && btnTogglePredicted != null) {
            btnToggleActual.setOnClickListener(v -> {
                isPredictedMode = false;
                updateDisplayMode();
                updateUI();
            });
            
            btnTogglePredicted.setOnClickListener(v -> {
                isPredictedMode = true;
                updateDisplayMode();
                updateUI();
            });
        }
    }

    private void updateDisplayMode() {
        if (isPredictedMode) {
            // Predicted Selected
            btnTogglePredicted.setBackgroundResource(R.drawable.bg_button_primary);
            btnTogglePredicted.setTextColor(android.graphics.Color.WHITE);
            
            btnToggleActual.setBackgroundResource(android.R.color.transparent);
            btnToggleActual.setTextColor(android.graphics.Color.parseColor("#71717a")); // Zinc 500
            
            if (tvFilterTime != null) tvFilterTime.setText("Monthly"); // Default/Reset
        } else {
            // Actual Selected
            btnToggleActual.setBackgroundResource(R.drawable.bg_button_primary);
            btnToggleActual.setTextColor(android.graphics.Color.WHITE);
            
            btnTogglePredicted.setBackgroundResource(android.R.color.transparent);
            btnTogglePredicted.setTextColor(android.graphics.Color.parseColor("#71717a")); // Zinc 500
        }
    }
    

    

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}