package com.example.florra_a;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.example.florra_a.adapters.RecommendationAdapter;
import com.example.florra_a.models.Product;

import java.util.ArrayList;
import java.util.List;

public class AIRecommendationActivity extends AppCompatActivity {

    private RecyclerView rvRecommendations;
    private TileAdapter adapter;
    private List<Product> originalProducts;
    private List<Product> filteredProducts;
    private TextView tvMatchCount;
    private String currentCategory = "All";
    private String currentFilterType = "top";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set fullscreen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
                // Set status bar to white with dark icons
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(android.view.View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(android.graphics.Color.WHITE);
        }

        setContentView(R.layout.activity_ai_recommendation);

        initializeViews();
        setupClickListeners();
        loadRecommendations();
    }

    private void initializeViews() {
        rvRecommendations = findViewById(R.id.rvRecommendations);
        // Use Grid Layout with 2 columns
        rvRecommendations.setLayoutManager(new GridLayoutManager(this, 2));

        tvMatchCount = findViewById(R.id.tvMatchCount);
    }

    private void loadRecommendations() {
        // Get data from intent
        if (getIntent().hasExtra("recommendations")) {
            originalProducts = (List<Product>) getIntent().getSerializableExtra("recommendations");
            if (originalProducts != null) {
                Toast.makeText(this, "Received " + originalProducts.size() + " matches", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "No matches in intent", Toast.LENGTH_SHORT).show();
            }
        }

        if (originalProducts == null) {
            originalProducts = new ArrayList<>();
        }

        // Filter inactive products from recommendations
        filteredProducts = new ArrayList<>();
        if (originalProducts != null) {
            for (Product p : originalProducts) {
                if (p.isActive()) {
                    filteredProducts.add(p);
                }
            }
        }

        tvMatchCount.setText("Found " + filteredProducts.size() + " similar matches");

        adapter = new TileAdapter(this, filteredProducts);
        adapter.setOnItemClickListener(new TileAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Product product) {
                Intent intent = new Intent(AIRecommendationActivity.this, ProductDetailsActivity.class);
                intent.putExtra("productId", product.getId());
                intent.putExtra("tileName", product.getTileName());
                intent.putExtra("tilePrice", String.valueOf(product.getPrice()));
                intent.putExtra("tileSize", product.getSize());
                intent.putExtra("productFinish", product.getFinish());
                intent.putExtra("productCategory", product.getCategory());
                intent.putExtra("productDescription", product.getDescription());
                intent.putExtra("productImage", product.getImage());
                intent.putExtra("stockStatus", product.getStock() > 0 ? "IN STOCK" : "OUT OF STOCK");
                intent.putExtra("productTileNo", product.getTileNo());
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }

            @Override
            public void onItemLongClick(Product product) {}

            @Override
            public void onBookmarkClick(Product product) {
                // Logic for bookmarking
                toggleFavorite(product);
            }

            @Override
            public void onAddToCartClick(Product product) {}
        });
        rvRecommendations.setAdapter(adapter);
    }

    private void toggleFavorite(Product product) {
        if (product.getId() == 0) return;
        
        java.util.Map<String, Integer> body = new java.util.HashMap<>();
        body.put("product_id", product.getId());

        com.example.florra_a.network.RetrofitClient.getApiService().addToFavorites(body).enqueue(new retrofit2.Callback<okhttp3.ResponseBody>() {
            @Override
            public void onResponse(retrofit2.Call<okhttp3.ResponseBody> call, retrofit2.Response<okhttp3.ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(AIRecommendationActivity.this, "Updated favorites", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(retrofit2.Call<okhttp3.ResponseBody> call, Throwable t) {}
        });
    }

    private void setupClickListeners() {
        findViewById(R.id.btnBack).setOnClickListener(v -> onBackPressed());

        findViewById(R.id.btnFilterIcon).setOnClickListener(v -> showFilterBottomSheet());
        
        findViewById(R.id.btnRefineSearch).setOnClickListener(v -> 
             Toast.makeText(this, "Refine Search Parameters", Toast.LENGTH_SHORT).show());
        
        // Setup filter button listeners
        findViewById(R.id.btnTopMatches).setOnClickListener(v -> applyFilter("top"));
        findViewById(R.id.btnTexture).setOnClickListener(v -> applyFilter("texture"));
        findViewById(R.id.btnColorPalette).setOnClickListener(v -> applyFilter("color"));
        findViewById(R.id.btnPattern).setOnClickListener(v -> applyFilter("pattern"));
    }

    private void applyFilter(String type) {
        this.currentFilterType = type;
        refreshList();
    }

    private void refreshList() {
        List<Product> baseList = new ArrayList<>();
        
        // Step 1: Filter by Category first
        if (currentCategory.equals("All")) {
            baseList.addAll(originalProducts);
        } else {
            for (Product p : originalProducts) {
                if (currentCategory.equalsIgnoreCase(p.getCategory())) baseList.add(p);
            }
        }

        // Step 2: Apply Secondary Filter/Sort
        filteredProducts.clear();
        resetChips();
        int activeChipId = 0;

        switch (currentFilterType) {
            case "top":
                filteredProducts.addAll(baseList);
                java.util.Collections.sort(filteredProducts, (p1, p2) -> 
                    Double.compare(p2.getSimilarityScore(), p1.getSimilarityScore()));
                activeChipId = R.id.btnTopMatches;
                break;
            case "texture":
                for (Product p : baseList) {
                    if (p.getFinish() != null && !p.getFinish().isEmpty()) filteredProducts.add(p);
                }
                activeChipId = R.id.btnTexture;
                break;
            case "color":
                for (Product p : baseList) {
                    if (p.getColor() != null && !p.getColor().isEmpty()) filteredProducts.add(p);
                }
                activeChipId = R.id.btnColorPalette;
                break;
            case "pattern":
                for (Product p : baseList) {
                    if (p.getTileName().toLowerCase().contains("marquina") || 
                        p.getTileName().toLowerCase().contains("carrara") ||
                        p.getTileName().toLowerCase().contains("oak")) filteredProducts.add(p);
                }
                activeChipId = R.id.btnPattern;
                break;
        }

        if (activeChipId != 0) {
            View v = findViewById(activeChipId);
            if (v != null) {
                v.setBackgroundResource(R.drawable.bg_chip_selected);
                if (v instanceof TextView) ((TextView)v).setTextColor(android.graphics.Color.WHITE);
            }
        }

        adapter.notifyDataSetChanged();
        String categoryText = currentCategory.equals("All") ? "" : "[" + currentCategory + "] ";
        tvMatchCount.setText(categoryText + "Showing " + filteredProducts.size() + " matches");
    }

    private void showCategoryFilter(View v) {
        try {
            android.widget.PopupMenu popup = new android.widget.PopupMenu(this, v);
            popup.getMenu().add("All");
            for (String category : com.example.florra_a.utils.Constants.CATEGORIES) {
                popup.getMenu().add(category);
            }

            popup.setOnMenuItemClickListener(item -> {
                try {
                    currentCategory = item.getTitle().toString();
                    refreshList();
                } catch (Exception e) {
                    Toast.makeText(this, "Filter logic error", Toast.LENGTH_SHORT).show();
                }
                return true;
            });
            popup.show();
        } catch (Exception e) {
            Toast.makeText(this, "Error showing categories", Toast.LENGTH_SHORT).show();
        }
    }

    private void showFilterBottomSheet() {
        try {
            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this, R.style.BottomSheetDialogTheme);
            View bottomSheetView = getLayoutInflater().inflate(R.layout.layout_filter_bottom_sheet, null);
            
            if (bottomSheetView == null) {
                Toast.makeText(this, "Error inflating layout", Toast.LENGTH_SHORT).show();
                return;
            }

            bottomSheetDialog.setContentView(bottomSheetView);

            ChipGroup cgCategories = bottomSheetView.findViewById(R.id.cgCategories);
            cgCategories.removeAllViews();
            
            // Add All chip
            Chip chipAll = new Chip(new android.view.ContextThemeWrapper(this, R.style.ChoiceChipStyle), null, 0);
            chipAll.setId(View.generateViewId());
            chipAll.setText("All");
            chipAll.setCheckable(true);
            chipAll.setChecked(currentCategory.equals("All"));
            cgCategories.addView(chipAll);

            // Add dynamic chips
            int idCounter = 1000;
            for (String category : com.example.florra_a.utils.Constants.CATEGORIES) {
                Chip chip = new Chip(new android.view.ContextThemeWrapper(this, R.style.ChoiceChipStyle), null, 0);
                chip.setId(idCounter++);
                chip.setText(category);
                chip.setCheckable(true);
                chip.setChecked(currentCategory.equalsIgnoreCase(category));
                cgCategories.addView(chip);
            }

            android.widget.Button btnApply = bottomSheetView.findViewById(R.id.btnApplyFilters);

                btnApply.setOnClickListener(v -> {
                try {
                    int selectedId = cgCategories.getCheckedChipId();
                    if (selectedId != -1) {
                        Chip selectedChip = bottomSheetView.findViewById(selectedId);
                        currentCategory = selectedChip.getText().toString();
                        refreshList();
                    }
                    bottomSheetDialog.dismiss();
                } catch (Exception e) {
                    Toast.makeText(this, "Filter Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            });

            bottomSheetDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Bottom Sheet Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            // Fallback to simple popup if bottom sheet fails
            showCategoryFilter(findViewById(R.id.btnFilterIcon));
        }
    }

    private void resetChips() {
        int[] chipIds = {R.id.btnTopMatches, R.id.btnTexture, R.id.btnColorPalette, R.id.btnPattern};
        for (int id : chipIds) {
            View chipView = findViewById(id);
            if (chipView != null) {
                chipView.setBackgroundResource(R.drawable.bg_chip_unselected);
                if (chipView instanceof TextView) {
                    ((TextView)chipView).setTextColor(android.graphics.Color.parseColor("#475569"));
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}