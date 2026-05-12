package com.example.florra_a;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.florra_a.models.Product;
// FavoritesManager import removed
import java.util.ArrayList;
import java.util.List;

public class ProductDetailsActivity extends AppCompatActivity {

    // Declare views
    private TextView productName, productPrice, productModel, specMaterial, specFinish,
            specThickness, specSize, specCoverage, specPacking, productDescription;
    private LinearLayout stockBadge;
    private TextView stockBadgeText;
    private ImageView productImage;
    private TextView tagPorcelain;

    // Favorite state
    private ImageView btnFavorite;
    private LinearLayout btnCompare; // Add this
    private boolean isFavorite = false;
    // FavoritesManager removed
    private Product currentProduct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set fullscreen
                // Set status bar to white with dark icons
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(android.view.View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(android.graphics.Color.WHITE);
        }

        setContentView(R.layout.activity_product_details);

        // Initialize views
        initializeViews();

        // Get data from intent
        Intent intent = getIntent();
        if (intent != null) {
            loadProductData(intent);
        }

        setupAllClickListeners();
    }

    private void initializeViews() {
        productName = findViewById(R.id.productName);
        productPrice = findViewById(R.id.productPrice);
        productModel = findViewById(R.id.productModel);
        specMaterial = findViewById(R.id.specMaterial);
        specFinish = findViewById(R.id.specFinish);
        specThickness = findViewById(R.id.specThickness);
        specSize = findViewById(R.id.specSize);
        specCoverage = findViewById(R.id.specCoverage);
        specPacking = findViewById(R.id.specPacking);
        productDescription = findViewById(R.id.productDescription);
        stockBadge = findViewById(R.id.stockBadge);
        stockBadgeText = findViewById(R.id.stockBadgeText);
        productImage = findViewById(R.id.productImage);
        tagPorcelain = findViewById(R.id.tagPorcelain);
        btnFavorite = findViewById(R.id.btnFavorite);
        btnCompare = findViewById(R.id.btnCompare); // Initialize this
    }

    private void loadProductData(Intent intent) {
        // Construct Product object from intent data
        currentProduct = new Product();
        
        // 1. Name
        String name = intent.getStringExtra("productName");
        if (name == null) name = intent.getStringExtra("tileName");
        
        currentProduct.setTileName(name);
        if (name != null && !name.isEmpty()) {
            productName.setText(name);
            productName.setTextColor(android.graphics.Color.BLACK); // Force visible color
        } else {
             productName.setText("");
        }

        // 2. Price
        String price = intent.getStringExtra("productPrice");
        if (price == null) price = intent.getStringExtra("tilePrice");
        currentProduct.setPrice(price);
        if (price != null && !price.isEmpty()) {
            String displayPrice = price.startsWith("₹") ? price : "₹" + price;
            productPrice.setText(displayPrice);
        } else {
            productPrice.setText("₹0.00");
        }

        // 3. Model
        String model = intent.getStringExtra("productModel");
        String tileNo = intent.getStringExtra("productTileNo");
        

        
        if (tileNo != null && !tileNo.isEmpty()) {
            model = tileNo;
        } else if (model == null && name != null) {
            model = "FL-" + name.substring(0, Math.min(2, name.length())).toUpperCase() +
                    "-" + String.format("%03d", (int)(Math.random() * 1000));
        }
        
        // Ensure "Model:" prefix is present
        if (model != null) {
             currentProduct.setTileNo(model.replace("Model: ", "")); // Store raw
             productModel.setText(model.startsWith("No:") ? model : "No: " + model);
        } else {
             productModel.setText("");
        }

        // 4. Stock
        String stock = intent.getStringExtra("productStock");
        if (stock == null) stock = intent.getStringExtra("tileStock");
        currentProduct.setStockStatus(stock != null ? stock : "IN STOCK"); // Store simple string
        
        if ("LOW STOCK".equalsIgnoreCase(stock) || "OUT OF STOCK".equalsIgnoreCase(stock)) {
            stockBadgeText.setText(stock);
            stockBadgeText.setTextColor(getResources().getColor(R.color.orange_600));
            stockBadge.setBackgroundResource(R.drawable.bg_tag_low_stock);
        } else {
            stockBadgeText.setText("IN STOCK");
            stockBadgeText.setTextColor(getResources().getColor(R.color.green_600));
            stockBadge.setBackgroundResource(R.drawable.bg_tag_instock);
        }

        // 5. Material/Category (User wants Category on tag)
        String category = intent.getStringExtra("productCategory");
        if (category == null) category = intent.getStringExtra("productMaterial");
        
        tagPorcelain.setText(category != null ? category.toUpperCase() : "PORCELAIN");
        specMaterial.setText(getValueOrDefault(category, "Porcelain"));
        currentProduct.setCategory(category);

        String finish = intent.getStringExtra("productFinish");
        specFinish.setText(getValueOrDefault(finish, "High Gloss"));
        currentProduct.setFinish(finish != null ? finish : "High Gloss");

        // 6. Size
        String size = intent.getStringExtra("productSize");
        if (size == null) size = intent.getStringExtra("tileSize");
        specSize.setText(getValueOrDefault(size, "60x120cm"));
        currentProduct.setSize(size != null ? size : "60x120cm");

        // 7. Other specs
        specThickness.setText(getValueOrDefault(intent.getStringExtra("productThickness"), "9mm"));
        specCoverage.setText(getValueOrDefault(intent.getStringExtra("productCoverage"), "1.44m²/box"));
        specPacking.setText(getValueOrDefault(intent.getStringExtra("productPacking"), "2 Pcs / Box"));

        // 8. Description
        String description = intent.getStringExtra("productDescription");
        if (description == null || description.isEmpty()) {
            description = getDescriptionForProduct(name);
        }
        productDescription.setText(description);
        currentProduct.setDescription(description);

        // 9. Image
        String imageUrl = intent.getStringExtra("productImage");
        currentProduct.setImage(imageUrl);
        setProductImage(imageUrl);
        
        // 10. ID fallback (if passed, though usually 0 for manually constructed ones unless from API object)
        int id = intent.getIntExtra("productId", 0);
        currentProduct.setId(id);

        // Check favorite status
        checkFavoriteStatus();
        
        // Fetch similar
        fetchSimilarProducts();
    }

    private String getValueOrDefault(String value, String defaultValue) {
        return (value != null && !value.isEmpty()) ? value : defaultValue;
    }

    private void checkFavoriteStatus() {
        if (currentProduct == null || currentProduct.getId() == 0) return;

        com.example.florra_a.network.RetrofitClient.getApiService().getFavorites().enqueue(new retrofit2.Callback<List<Product>>() {
            @Override
            public void onResponse(retrofit2.Call<List<Product>> call, retrofit2.Response<List<Product>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    isFavorite = false;
                    for (Product p : response.body()) {
                        if (p.getId() == currentProduct.getId()) {
                            isFavorite = true;
                            break;
                        }
                    }
                    updateFavoriteIcon();
                }
            }

            @Override
            public void onFailure(retrofit2.Call<List<Product>> call, Throwable t) {
                // Ignore error, keep default
            }
        });
    }

    private void updateFavoriteIcon() {
        if (isFavorite) {
            btnFavorite.setImageResource(R.drawable.ic_favorite_filled);
            btnFavorite.setColorFilter(getResources().getColor(R.color.red_500));
        } else {
            btnFavorite.setImageResource(R.drawable.ic_favorite_border);
            btnFavorite.setColorFilter(getResources().getColor(R.color.slate_900));
        }
    }

    private String getDescriptionForProduct(String productName) {
        if (productName == null) return "";
        // Simple fallback logic: return a generic description if empty
        return "Designed to mimic the luxury of Italian marble, this tile brings an air of sophistication to any space.";
    }

    private void setProductImage(String imageUrl) {
        if (imageUrl != null && !imageUrl.isEmpty()) {
            if (!imageUrl.startsWith("http")) {
                imageUrl = com.example.florra_a.network.RetrofitClient.BASE_URL + imageUrl;
            }
            com.bumptech.glide.Glide.with(this)
                .load(imageUrl)
                .placeholder(R.drawable.tile_placeholder)
                .error(R.drawable.tile_placeholder)
                .centerCrop()
                .into(productImage);
        } else {
             productImage.setImageResource(R.drawable.tile_placeholder);
        }
    }

    private void setupAllClickListeners() {
        // Back
        ImageView btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) btnBack.setOnClickListener(v -> onBackPressed());

        // Favorite
        if (btnFavorite != null) btnFavorite.setOnClickListener(v -> toggleFavorite());

        // Request Quote
        LinearLayout btnAskQuote = findViewById(R.id.btnAskQuote);
        if (btnAskQuote != null) {
            btnAskQuote.setOnClickListener(v -> {
                Intent intent = new Intent(ProductDetailsActivity.this, RequestQuotationActivity.class);
                intent.putExtra("productName", productName.getText().toString());
                intent.putExtra("productDetails", specSize.getText().toString() + " • " + specFinish.getText().toString());
                intent.putExtra("stockStatus", stockBadgeText.getText().toString());
                intent.putExtra("productImage", currentProduct.getImage());
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            });
        }

        // Calculator
        LinearLayout btnCalc = findViewById(R.id.btnCalc);
        if (btnCalc != null) {
            btnCalc.setOnClickListener(v -> {
                Intent intent = new Intent(ProductDetailsActivity.this, CalculatorActivity.class);
                // Pass product details for context (optional, but helpful if we want to add to quote later)
                intent.putExtra("productName", productName.getText().toString());
                intent.putExtra("productDetails", specSize.getText().toString() + " • " + specFinish.getText().toString());
                intent.putExtra("stockStatus", stockBadgeText.getText().toString());
                intent.putExtra("productImage", currentProduct.getImage());
                intent.putExtra("fromProductDetails", true); // Flag to indicate source
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            });
        }

            btnCompare.setOnClickListener(v -> {
                Intent intent = new Intent(ProductDetailsActivity.this, CompareActivity.class);
                intent.putExtra("productName", productName.getText().toString());
                intent.putExtra("productDetails", specSize.getText().toString() + " • " + specFinish.getText().toString());
                
                // Pass all visible details
                intent.putExtra("productModel", productModel.getText().toString());
                intent.putExtra("productThickness", specThickness.getText().toString());
                intent.putExtra("productCoverage", specCoverage.getText().toString());
                intent.putExtra("productPacking", specPacking.getText().toString());
                intent.putExtra("productDescription", productDescription.getText().toString());
                
                // Also pass individual fields if possible
                if (currentProduct != null) {
                    intent.putExtra("productPrice", String.valueOf(currentProduct.getPrice()));
                    intent.putExtra("productCategory", currentProduct.getCategory());
                    intent.putExtra("productSize", currentProduct.getSize());
                    intent.putExtra("productFinish", currentProduct.getFinish());
                    intent.putExtra("productImage", currentProduct.getImage());
                } else {
                     // Fallback to UI text if currentProduct is null (unlikely but safe)
                     intent.putExtra("productImage", ""); 
                }
                
                try {
                    startActivity(intent);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                } catch (Exception e) {
                    Toast.makeText(ProductDetailsActivity.this, "Error starting Compare: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            });



        
        // Bottom Nav (simplified for brevity)
        setupBottomNavigation();
    }

    private void toggleFavorite() {
        if (currentProduct == null || currentProduct.getId() == 0) {
            Toast.makeText(this, "Product ID error", Toast.LENGTH_SHORT).show();
            return;
        }
        
        btnFavorite.setEnabled(false); // Prevent double clicks

        if (!isFavorite) {
            // Add to Favorites
            java.util.Map<String, Integer> body = new java.util.HashMap<>();
            body.put("product_id", currentProduct.getId());

            com.example.florra_a.network.RetrofitClient.getApiService().addToFavorites(body).enqueue(new retrofit2.Callback<okhttp3.ResponseBody>() {
                @Override
                public void onResponse(retrofit2.Call<okhttp3.ResponseBody> call, retrofit2.Response<okhttp3.ResponseBody> response) {
                    btnFavorite.setEnabled(true);
                    if (response.isSuccessful()) {
                        isFavorite = true;
                        updateFavoriteIcon();
                        Toast.makeText(ProductDetailsActivity.this, "Added to favorites", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(ProductDetailsActivity.this, "Failed to add", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(retrofit2.Call<okhttp3.ResponseBody> call, Throwable t) {
                    btnFavorite.setEnabled(true);
                    Toast.makeText(ProductDetailsActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            // Remove from Favorites
            com.example.florra_a.network.RetrofitClient.getApiService().removeFromFavorites(currentProduct.getId()).enqueue(new retrofit2.Callback<okhttp3.ResponseBody>() {
                @Override
                public void onResponse(retrofit2.Call<okhttp3.ResponseBody> call, retrofit2.Response<okhttp3.ResponseBody> response) {
                    btnFavorite.setEnabled(true);
                    if (response.isSuccessful()) {
                        isFavorite = false;
                        updateFavoriteIcon();
                        Toast.makeText(ProductDetailsActivity.this, "Removed from favorites", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(ProductDetailsActivity.this, "Failed to remove", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(retrofit2.Call<okhttp3.ResponseBody> call, Throwable t) {
                    btnFavorite.setEnabled(true);
                    Toast.makeText(ProductDetailsActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void fetchSimilarProducts() {
        // ... (Existing logic for fetching similar products) ...
        // Re-implementing briefly to keep context valid
        String category = currentProduct.getCategory() != null ? currentProduct.getCategory() : "all";
        
        androidx.recyclerview.widget.RecyclerView recyclerSimilar = findViewById(R.id.recyclerSimilarProducts);
        recyclerSimilar.setLayoutManager(new androidx.recyclerview.widget.GridLayoutManager(this, 2));
        TextView tvNoSimilarProducts = findViewById(R.id.tvNoSimilarProducts);

        com.example.florra_a.network.ApiService apiService = com.example.florra_a.network.RetrofitClient.getApiService();
        apiService.getInventory(null, category, null).enqueue(new retrofit2.Callback<com.example.florra_a.models.InventoryResponse>() {
            @Override
            public void onResponse(retrofit2.Call<com.example.florra_a.models.InventoryResponse> call, retrofit2.Response<com.example.florra_a.models.InventoryResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getProducts() != null) {
                    List<Product> allProducts = response.body().getProducts();
                    List<Product> similarProducts = new ArrayList<>();
                    String currentName = currentProduct.getTileName();
                    
                    for (Product p : allProducts) {
                         if (currentName != null && !currentName.equalsIgnoreCase(p.getTileName())) {
                             similarProducts.add(p);
                         }
                    }
                    
                    if (similarProducts.isEmpty()) {
                        tvNoSimilarProducts.setVisibility(View.VISIBLE);
                        recyclerSimilar.setVisibility(View.GONE);
                    } else {
                        tvNoSimilarProducts.setVisibility(View.GONE);
                        recyclerSimilar.setVisibility(View.VISIBLE);
                        
                        // Use TileAdapter as requested
                        com.example.florra_a.TileAdapter adapter = new com.example.florra_a.TileAdapter(ProductDetailsActivity.this, similarProducts);
                        
                        // Set click listener for similar products to reload the activity with the new product
                        adapter.setOnItemClickListener(new com.example.florra_a.TileAdapter.OnItemClickListener() {
                            @Override
                            public void onItemClick(Product product) {
                                Intent intent = new Intent(ProductDetailsActivity.this, ProductDetailsActivity.class);
                                // Pass known data
                                intent.putExtra("tileName", product.getTileName());
                                intent.putExtra("tilePrice", String.valueOf(product.getPrice()));
                                intent.putExtra("productName", product.getTileName());
                                intent.putExtra("productPrice", String.valueOf(product.getPrice()));
                                intent.putExtra("productStock", product.getStockStatus());
                                intent.putExtra("tileStock", product.getStockStatus());
                                intent.putExtra("productCategory", product.getCategory());
                                intent.putExtra("productSize", product.getSize());
                                intent.putExtra("productFinish", product.getFinish());
                                intent.putExtra("productImage", product.getImage());
                                intent.putExtra("productDescription", product.getDescription());
                                intent.putExtra("productTileNo", product.getTileNo());
                                
                                startActivity(intent);
                                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                                finish();
                            }

                            @Override
                            public void onItemLongClick(Product product) {
                                // No action for long click
                            }

                            @Override
                            public void onAddToCartClick(Product product) {
                                // Add to cart logic if needed, or simple toast
                                Toast.makeText(ProductDetailsActivity.this, "Added to cart: " + product.getTileName(), Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onBookmarkClick(Product product) {
                                // Handle bookmark
                            }
                        });
                        
                        recyclerSimilar.setAdapter(adapter);
                    }
                } else {
                    tvNoSimilarProducts.setVisibility(View.VISIBLE);
                    recyclerSimilar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(retrofit2.Call<com.example.florra_a.models.InventoryResponse> call, Throwable t) {
                tvNoSimilarProducts.setVisibility(View.VISIBLE);
                recyclerSimilar.setVisibility(View.GONE);
            }
        });
    }

    private void setupBottomNavigation() {
        LinearLayout btnNavHome = findViewById(R.id.btnNavHome);
        if (btnNavHome != null) btnNavHome.setOnClickListener(v -> startActivity(new Intent(this, CustomerHomeActivity.class)));

        LinearLayout btnNavCatalog = findViewById(R.id.btnNavCatalog);
        if (btnNavCatalog != null) btnNavCatalog.setOnClickListener(v -> startActivity(new Intent(this, CatalogActivity.class)));
        
        LinearLayout btnNavEnquiries = findViewById(R.id.btnNavEnquiries);
        if (btnNavEnquiries != null) btnNavEnquiries.setOnClickListener(v -> startActivity(new Intent(this, QuotationsActivity.class))); // Fixed to point to QuotationsActivity
        
        LinearLayout btnNavAccount = findViewById(R.id.btnNavAccount);
        if (btnNavAccount != null) btnNavAccount.setOnClickListener(v -> startActivity(new Intent(this, CustomerAccountActivity.class)));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}