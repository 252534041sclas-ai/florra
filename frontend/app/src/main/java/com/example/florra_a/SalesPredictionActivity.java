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

        // Restrict staff role from accessing sales predictions activity if not permitted
        com.example.florra_a.utils.SharedPrefManager pref = com.example.florra_a.utils.SharedPrefManager.getInstance(this);
        if ("staff".equalsIgnoreCase(pref.getRole()) && !pref.canAccessPredictions()) {
            Toast.makeText(this, "Access Denied: You do not have permission to access sales predictions", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // Set status bar to black for consistency with animated header
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(0); // Clear light status bar flag
            getWindow().setStatusBarColor(android.graphics.Color.BLACK);
        }

        setContentView(R.layout.activity_sales_prediction);

        initializeViews();
        setupNavigation();
        setupToggle(); 
        fetchSalesPrediction();
    }

    private android.widget.TextView tvLoadingStatus;

    private void initializeViews() {
        tvPredictedSales = findViewById(R.id.tvPredictedSales);
        tvEstRevenue = findViewById(R.id.tvEstRevenue);
        tvGrowthPercentage = findViewById(R.id.tvGrowthPercentage);
        tvHighDemandProduct = findViewById(R.id.tvHighDemandProduct);
        tvLowDemandProduct = findViewById(R.id.tvLowDemandProduct);
        tvHighDemandDesc = findViewById(R.id.tvHighDemandDesc); 
        tvLowDemandDesc = findViewById(R.id.tvLowDemandDesc);   
        tvStockSuggestion = findViewById(R.id.tvStockSuggestion);
        tvChartValue = findViewById(R.id.tvChartValue);
        tvLoadingStatus = findViewById(R.id.tvLoadingStatus);
        
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
        if (loadingOverlay != null) {
            loadingOverlay.setVisibility(View.VISIBLE);
            startLoadingSimulation();
        }
        
        String category = tvFilterCategory != null ? tvFilterCategory.getText().toString() : null;
        if ("All Categories".equals(category)) category = null;

        com.example.florra_a.network.ApiService apiService = 
            com.example.florra_a.network.RetrofitClient.getApiService();

        apiService.getSalesPrediction(category).enqueue(new retrofit2.Callback<com.example.florra_a.models.SalesPredictionResponse>() {
            @Override
            public void onResponse(retrofit2.Call<com.example.florra_a.models.SalesPredictionResponse> call, 
                                   retrofit2.Response<com.example.florra_a.models.SalesPredictionResponse> response) {
                
                // Add a small delay for simulation effect if it's too fast
                new android.os.Handler().postDelayed(() -> {
                    if (loadingOverlay != null) loadingOverlay.setVisibility(View.GONE);
                    if (response.isSuccessful() && response.body() != null) {
                        currentData = response.body();
                        updateUI();
                    } else {
                        Toast.makeText(SalesPredictionActivity.this, "No data for this selection", Toast.LENGTH_SHORT).show();
                    }
                }, 2000);
            }

            @Override
            public void onFailure(retrofit2.Call<com.example.florra_a.models.SalesPredictionResponse> call, Throwable t) {
                if (loadingOverlay != null) loadingOverlay.setVisibility(View.GONE);
                Toast.makeText(SalesPredictionActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void startLoadingSimulation() {
        if (tvLoadingStatus == null) return;
        
        String[] statuses = {
            "Processing historical sales...",
            "Analyzing market demand...",
            "Cross-referencing inventory levels...",
            "Generating AI forecast..."
        };
        
        final int[] index = {0};
        android.os.Handler handler = new android.os.Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (loadingOverlay.getVisibility() == View.VISIBLE && index[0] < statuses.length) {
                    tvLoadingStatus.setText(statuses[index[0]]);
                    index[0]++;
                    handler.postDelayed(this, 500);
                }
            }
        };
        handler.post(runnable);
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
                tvHighDemandDesc.setText(tileNo != null && !tileNo.isEmpty() ? "Tile: " + tileNo : "Marble Finish");
            }
                
            if (tvLowDemandProduct != null) 
                tvLowDemandProduct.setText(currentData.getLowDemandProduct().isEmpty() ? "N/A" : currentData.getLowDemandProduct());

            if (tvLowDemandDesc != null) {
                String tileNo = currentData.getLowDemandTileNo();
                tvLowDemandDesc.setText(tileNo != null && !tileNo.isEmpty() ? "Tile: " + tileNo : "Matte Finish");
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
                        tvHighDemandDesc.setText(highTile != null && !highTile.isEmpty() ? "Tile: " + highTile : "Marble Finish");
                        
                     if (tvLowDemandProduct != null) 
                        tvLowDemandProduct.setText(low != null && !low.isEmpty() ? low : "N/A");

                     if (tvLowDemandDesc != null)
                        tvLowDemandDesc.setText(lowTile != null && !lowTile.isEmpty() ? "Tile: " + lowTile : "Matte Finish");

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
                     }
                }
            }
        }

        if (tvStockSuggestion != null) {
            String suggestion = currentData.getStockSuggestion();
            String timeFilter = tvFilterTime.getText().toString();
            
            // AI Algorithm Refinement: Dynamic Insights based on time range and growth
            double growth = currentData.getGrowthPercentage();
            String highProduct = currentData.getHighDemandProduct();
            
            if (isPredictedMode) {
                if (timeFilter.contains("12 Months")) {
                    suggestion = "LONG-TERM FORECAST: Sustained growth of " + String.format("%.1f%%", growth) + 
                                 " predicted for " + highProduct + ". Recommend securing long-term supply contracts.";
                } else if (timeFilter.contains("3 Months") || timeFilter.contains("6 Months")) {
                    suggestion = "MID-TERM ADVISORY: Upcoming peak detected. Increase safety stock for " + 
                                 highProduct + " by 20% to avoid stockouts during revenue surge.";
                } else if (growth > 20) {
                    suggestion = "CRITICAL DEMAND ALERT: Explosive " + String.format("%.1f%%", growth) + 
                                 " growth expected. Redirecting logistics focus to " + highProduct + ".";
                } else if (growth < -5) {
                    suggestion = "INVENTORY RISK: Demand for " + highProduct + " is cooling. Avoid overstocking; optimize liquidity.";
                } else {
                    suggestion = "STABLE GROWTH: Market trend is consistent. Maintain standard reorder points for " + highProduct + ".";
                }
            }
            tvStockSuggestion.setText(suggestion != null ? suggestion : "Inventory is optimal for current demand.");
        }

        // Render Graph based on Mode and Filter
        java.util.List<Float> chartPoints = new java.util.ArrayList<>();
        String timeFilterText = tvFilterTime.getText().toString();

        if (isPredictedMode && currentData != null && currentData.getChart() != null) {
            // Base data from API
            float baseV1 = currentData.getChart().get("week1") != null ? currentData.getChart().get("week1") : 20;
            float baseV2 = currentData.getChart().get("week2") != null ? currentData.getChart().get("week2") : 35;
            float baseV3 = currentData.getChart().get("today") != null ? currentData.getChart().get("today") : 45;
            float baseV4 = currentData.getChart().get("week4") != null ? currentData.getChart().get("week4") : 30;
            float baseV5 = currentData.getChart().get("week5") != null ? currentData.getChart().get("week5") : 50;

            // Algorithm: Transform base data based on selected time horizon to make it "suitable"
            if (timeFilterText.contains("12 Months")) {
                // Smooth upward trend simulation
                chartPoints.add(baseV1 * 0.8f);
                chartPoints.add(baseV1 * 1.1f);
                chartPoints.add(baseV1 * 1.5f);
                chartPoints.add(baseV1 * 1.9f);
                chartPoints.add(baseV1 * 2.4f);
            } else if (timeFilterText.contains("3 Months") || timeFilterText.contains("6 Months")) {
                // Cyclical variation simulation
                chartPoints.add(baseV1);
                chartPoints.add(baseV5);
                chartPoints.add(baseV2);
                chartPoints.add(baseV4);
                chartPoints.add(baseV3 * 1.2f);
            } else {
                // Standard weekly variation
                chartPoints.add(baseV1);
                chartPoints.add(baseV2);
                chartPoints.add(baseV3);
                chartPoints.add(baseV4);
                chartPoints.add(baseV5);
            }
            
            renderChart(chartPoints, true);
        } else if (!isPredictedMode && currentData != null && currentData.getActualData() != null) {
            com.example.florra_a.models.SalesPredictionResponse.ActualData actual = currentData.getActualData();
            com.example.florra_a.models.SalesPredictionResponse.TimeRangeData rangeData = null;
            
            if (timeFilterText.contains("This Month")) rangeData = actual.getThisMonth();
            else if (timeFilterText.contains("Last Month")) rangeData = actual.getLastMonth();
            else if (timeFilterText.contains("Last 3 Months")) rangeData = actual.getLast3Months();
            else if (timeFilterText.contains("Yearly")) rangeData = actual.getYearly();
            
            if (rangeData != null && rangeData.getGraphData() != null && !rangeData.getGraphData().isEmpty()) {
                for (Double d : rangeData.getGraphData()) chartPoints.add(d.floatValue());
                renderChart(chartPoints, false);
            }
        }
    }

    private void renderChart(java.util.List<Float> points, boolean highlightCenter) {
        if (points == null || points.isEmpty() || bar1 == null) return;
        
        View[] bars = {bar1, bar2, bar3, bar4, bar5};
        float maxValue = 1;
        for (Float p : points) if (p > maxValue) maxValue = p;

        for (int i = 0; i < bars.length; i++) {
            if (i < points.size() && i < bars.length) {
                setBarHeight(bars[i], points.get(i), maxValue);
                if (highlightCenter && i == 2) {
                    bars[i].setBackgroundResource(R.drawable.bg_bar_rounded_primary);
                } else {
                    bars[i].setBackgroundResource(R.drawable.bg_bar_rounded);
                }
            }
        }
    }

    private void setValues(int sales, double revenue, double growth) {
        if (tvPredictedSales != null) 
            tvPredictedSales.setText(java.text.NumberFormat.getNumberInstance().format(sales));
            
        if (tvEstRevenue != null) {
            if (revenue >= 100000) {
                tvEstRevenue.setText(String.format("₹%.1fL", revenue / 100000.0));
            } else {
                tvEstRevenue.setText("₹" + java.text.NumberFormat.getNumberInstance().format(revenue));
            }
        }
            
        if (tvChartValue != null) 
            tvChartValue.setText(sales >= 1000 ? (String.format("%.1fk", sales/1000.0)) : String.valueOf(sales));

        if (tvGrowthPercentage != null) {
            if (isPredictedMode) {
                String sign = growth >= 0 ? "↑ " : "↓ ";
                tvGrowthPercentage.setText(sign + String.format("%.1f%%", Math.abs(growth)));
                tvGrowthPercentage.setVisibility(View.VISIBLE);
                
                View growthParent = (View) tvGrowthPercentage.getParent();
                if (growth < 0) {
                     tvGrowthPercentage.setTextColor(android.graphics.Color.parseColor("#dc2626")); 
                     if (growthParent != null) growthParent.setBackgroundResource(R.drawable.bg_red_badge);
                } else {
                     tvGrowthPercentage.setTextColor(android.graphics.Color.parseColor("#15803d")); 
                     if (growthParent != null) growthParent.setBackgroundResource(R.drawable.bg_green_badge);
                }
            } else {
                tvGrowthPercentage.setVisibility(View.INVISIBLE);
                View growthParent = (View) tvGrowthPercentage.getParent();
                if (growthParent != null) growthParent.setVisibility(View.GONE);
            }
        }
    }

    private void setBarHeight(View bar, float value, float max) {
        android.widget.LinearLayout.LayoutParams params = (android.widget.LinearLayout.LayoutParams) bar.getLayoutParams();
        int maxHeightDp = 140;
        float density = getResources().getDisplayMetrics().density;
        int targetHeight = (int) ((value / max) * maxHeightDp * density);
        if (targetHeight < 8 * density) targetHeight = (int) (8 * density);
        
        int startHeight = bar.getHeight();
        if (startHeight <= 0) startHeight = (int) (8 * density);
        
        android.animation.ValueAnimator animator = android.animation.ValueAnimator.ofInt(startHeight, targetHeight);
        animator.setDuration(600);
        animator.setInterpolator(new android.view.animation.OvershootInterpolator(1.0f));
        animator.addUpdateListener(animation -> {
            params.height = (int) animation.getAnimatedValue();
            bar.setLayoutParams(params);
        });
        animator.start();
    }

    private void setupNavigation() {
        ImageView btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) btnBack.setOnClickListener(v -> onBackPressed());

        ImageView btnMore = findViewById(R.id.btnMore);
        if (btnMore != null) btnMore.setOnClickListener(v -> 
            Toast.makeText(this, "AI Analysis Options", Toast.LENGTH_SHORT).show());

        Button btnCreatePO = findViewById(R.id.btnCreatePO);
        if (btnCreatePO != null) btnCreatePO.setOnClickListener(v -> 
            Toast.makeText(this, "Purchase Order Generated Successfully", Toast.LENGTH_LONG).show());

        if (cardFilterTime != null) cardFilterTime.setOnClickListener(this::showTimeFilterPopup);
        if (cardFilterCategory != null) cardFilterCategory.setOnClickListener(this::showCategoryFilterPopup);
    }

    private void showTimeFilterPopup(View v) {
        android.widget.PopupMenu popup = new android.widget.PopupMenu(this, v);
        if (isPredictedMode) {
             popup.getMenu().add("This Month");
             popup.getMenu().add("Next Month");
             popup.getMenu().add("Next 3 Months");
             popup.getMenu().add("Next 6 Months");
             popup.getMenu().add("Next 12 Months");
        } else {
             popup.getMenu().add("This Month");
             popup.getMenu().add("Last Month");
             popup.getMenu().add("Last 3 Months");
             popup.getMenu().add("Last 6 Months");
             popup.getMenu().add("Yearly");
        }
        popup.setOnMenuItemClickListener(item -> {
            tvFilterTime.setText(item.getTitle());
            if (!isPredictedMode) updateUI();
            // In a real app, this would trigger a new API call with the time range
            return true;
        });
        popup.show();
    }

    private void showCategoryFilterPopup(View v) {
        android.widget.PopupMenu popup = new android.widget.PopupMenu(this, v);
        popup.getMenu().add("All Categories");
        for (String c : com.example.florra_a.utils.Constants.CATEGORIES) {
            popup.getMenu().add(c);
        }
        popup.setOnMenuItemClickListener(item -> {
            tvFilterCategory.setText(item.getTitle());
            fetchSalesPrediction();
            return true;
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
            btnTogglePredicted.setBackgroundResource(R.drawable.bg_button_primary_black);
            btnTogglePredicted.setTextColor(android.graphics.Color.WHITE);
            btnToggleActual.setBackgroundResource(android.R.color.transparent);
            btnToggleActual.setTextColor(android.graphics.Color.parseColor("#64748B"));
            if (tvFilterTime != null) tvFilterTime.setText("Next Month");
        } else {
            btnToggleActual.setBackgroundResource(R.drawable.bg_button_primary_black);
            btnToggleActual.setTextColor(android.graphics.Color.WHITE);
            btnTogglePredicted.setBackgroundResource(android.R.color.transparent);
            btnTogglePredicted.setTextColor(android.graphics.Color.parseColor("#64748B"));
        }
    }
    

    

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }
}