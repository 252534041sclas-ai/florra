package com.example.florra_a;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

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
    private boolean isFavorite = false;
    private String currentProductName = "";

    // SharedPreferences
    private SharedPreferences favoritesPrefs;
    private static final String PREFS_NAME = "FlorraFavorites";
    private static final String FAVORITES_KEY = "favorite_products";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set fullscreen
        getWindow().setFlags(android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN,
                android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_product_details);

        // Initialize SharedPreferences
        favoritesPrefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

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
    }

    private void loadProductData(Intent intent) {
        // Get product name - check both keys
        String name = intent.getStringExtra("productName");
        if (name == null) {
            name = intent.getStringExtra("tileName"); // Fallback for SearchActivity
        }

        if (name != null) {
            productName.setText(name);
            currentProductName = name; // Store for SharedPreferences
        }

        // Get price - check both keys
        String price = intent.getStringExtra("productPrice");
        if (price == null) {
            price = intent.getStringExtra("tilePrice"); // Fallback for SearchActivity
        }

        if (price != null) {
            productPrice.setText(price);
        }

        // Set model - check both sources
        String model = intent.getStringExtra("productModel");
        if (model == null) {
            // Generate model based on name if not provided
            if (name != null) {
                model = "Model: FL-" + name.substring(0, Math.min(2, name.length())).toUpperCase() +
                        "-" + String.format("%03d", (int)(Math.random() * 1000));
            }
        }

        if (model != null) {
            productModel.setText("Model: " + model);
        }

        // Set stock status - check both keys
        String stock = intent.getStringExtra("productStock");
        if (stock == null) {
            stock = intent.getStringExtra("tileStock"); // Fallback for SearchActivity
        }

        if (stock != null) {
            if (stock.equals("LOW STOCK")) {
                stockBadgeText.setText("LOW STOCK");
                stockBadgeText.setTextColor(getResources().getColor(R.color.orange_600));
                stockBadge.setBackgroundResource(R.drawable.bg_tag_low_stock);
            } else {
                stockBadgeText.setText("IN STOCK");
                stockBadgeText.setTextColor(getResources().getColor(R.color.green_600));
                stockBadge.setBackgroundResource(R.drawable.bg_tag_instock);
            }
        } else {
            // Default to IN STOCK if not provided
            stockBadgeText.setText("IN STOCK");
            stockBadgeText.setTextColor(getResources().getColor(R.color.green_600));
            stockBadge.setBackgroundResource(R.drawable.bg_tag_instock);
        }

        // Set material tag - check both sources
        String material = intent.getStringExtra("productMaterial");
        if (material == null) {
            material = intent.getStringExtra("tileFinish"); // Fallback for SearchActivity
        }

        if (material != null && !material.isEmpty()) {
            tagPorcelain.setText(material.toUpperCase());
        } else {
            tagPorcelain.setText("PORCELAIN"); // Default
        }

        // Set specifications with fallback values
        specMaterial.setText(getValueOrDefault(intent.getStringExtra("productMaterial"), "Porcelain"));
        specFinish.setText(getValueOrDefault(intent.getStringExtra("productFinish"), "High Gloss"));
        specThickness.setText(getValueOrDefault(intent.getStringExtra("productThickness"), "9mm"));

        // Size - check both keys
        String size = intent.getStringExtra("productSize");
        if (size == null) {
            size = intent.getStringExtra("tileSize"); // Fallback for SearchActivity
        }
        specSize.setText(getValueOrDefault(size, "60x120cm"));

        specCoverage.setText(getValueOrDefault(intent.getStringExtra("productCoverage"), "1.44m²/box"));
        specPacking.setText(getValueOrDefault(intent.getStringExtra("productPacking"), "2 Pcs / Box"));

        // Set description based on product
        String description = getDescriptionForProduct(name);
        if (description != null) {
            productDescription.setText(description);
        }

        // Set product image based on product name
        setProductImage(name);

        // Check if this product is already in favorites from SharedPreferences
        checkFavoriteStatus();
    }

    private String getValueOrDefault(String value, String defaultValue) {
        return (value != null && !value.isEmpty()) ? value : defaultValue;
    }

    private void checkFavoriteStatus() {
        // Get favorite status from SharedPreferences
        isFavorite = favoritesPrefs.getBoolean(currentProductName, false);
        updateFavoriteIcon();
    }

    private void updateFavoriteIcon() {
        if (isFavorite) {
            // Change to filled red heart
            btnFavorite.setImageResource(R.drawable.ic_favorite_filled);
            btnFavorite.setColorFilter(getResources().getColor(R.color.red_500));
        } else {
            // Change to outlined heart
            btnFavorite.setImageResource(R.drawable.ic_favorite_border);
            btnFavorite.setColorFilter(getResources().getColor(R.color.slate_900));
        }
    }

    private String getDescriptionForProduct(String productName) {
        if (productName == null) return "";

        switch (productName.toLowerCase()) {
            case "carrara white":
                return "Classic Carrara White marble tiles with subtle grey veining. Perfect for creating a bright, clean aesthetic in bathrooms and kitchens.";
            case "nero marquina":
                return "Elegant black marble with striking white veins. Creates a dramatic, luxurious look perfect for feature walls and high-end spaces.";
            case "statuario classic":
                return "Premium Italian marble with bold grey veining. The ultimate choice for creating a sophisticated, high-end aesthetic.";
            case "statuario white":
                return "Premium white marble with elegant grey veining. Perfect for creating luxurious spaces with a timeless appeal.";
            case "travertine beige":
                return "Natural travertine-look tiles with warm beige tones. Ideal for creating a rustic, Mediterranean-inspired atmosphere.";
            case "carrara grey":
                return "Beautiful grey marble with soft white veining. Creates an elegant, contemporary look perfect for modern interiors.";
            case "armani grey":
                return "Sophisticated grey marble with subtle white veining. Creates a modern, minimalist look perfect for contemporary spaces.";
            case "crema marfil":
                return "Warm beige marble with creamy tones. Adds warmth and elegance to any space, perfect for creating a welcoming atmosphere.";
            case "black marquina":
                return "Luxurious black marble with dramatic white veining. Makes a bold statement in any interior design.";
            case "oak wood":
                return "Realistic wood-look porcelain tiles with authentic oak texture. Combines the warmth of wood with the durability of porcelain.";
            default:
                return "Designed to mimic the luxury of Italian marble, this tile brings an air of sophistication to any space. Ideal for large living rooms, hotels, and high-traffic commercial areas due to its durable vitrified body and stain-resistant gloss finish.";
        }
    }

    private void setProductImage(String productName) {
        if (productName == null) return;

        // You can set different images here when you have them
        // For now, all use the same placeholder
        productImage.setImageResource(R.drawable.tile_placeholder);
    }

    private void setupAllClickListeners() {
        // Back button
        ImageView btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goBackToCatalog();
                }
            });
        }

        // Favorite button - TOGGLE FUNCTIONALITY
        if (btnFavorite != null) {
            btnFavorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    toggleFavorite();
                }
            });
        }

        // Action buttons
        LinearLayout btnCompare = findViewById(R.id.btnCompare);
        if (btnCompare != null) {
            btnCompare.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(ProductDetailsActivity.this, "Compare", Toast.LENGTH_SHORT).show();
                }
            });
        }

        LinearLayout btnCalc = findViewById(R.id.btnCalc);
        if (btnCalc != null) {
            btnCalc.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(ProductDetailsActivity.this, "Calculator", Toast.LENGTH_SHORT).show();
                }
            });
        }

        LinearLayout btnAskQuote = findViewById(R.id.btnAskQuote);
        if (btnAskQuote != null) {
            btnAskQuote.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(ProductDetailsActivity.this, "Ask for Quote", Toast.LENGTH_SHORT).show();
                }
            });
        }

        // Data Sheet button
        TextView btnDataSheet = findViewById(R.id.btnDataSheet);
        if (btnDataSheet != null) {
            btnDataSheet.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(ProductDetailsActivity.this, "Data Sheet", Toast.LENGTH_SHORT).show();
                }
            });
        }

        // See All similar products
        TextView btnSeeAll = findViewById(R.id.btnSeeAll);
        if (btnSeeAll != null) {
            btnSeeAll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(ProductDetailsActivity.this, "See All Similar Products", Toast.LENGTH_SHORT).show();
                }
            });
        }

        // Similar product clicks - FIXED: CardView instead of LinearLayout
        CardView similarArmani = findViewById(R.id.similarArmani);
        if (similarArmani != null) {
            similarArmani.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    navigateToSimilarProduct("Armani Grey", "$3.80", "IN STOCK");
                }
            });
        }

        CardView similarCrema = findViewById(R.id.similarCrema);
        if (similarCrema != null) {
            similarCrema.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    navigateToSimilarProduct("Crema Marfil", "$4.10", "IN STOCK");
                }
            });
        }

        CardView similarMarquina = findViewById(R.id.similarMarquina);
        if (similarMarquina != null) {
            similarMarquina.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    navigateToSimilarProduct("Black Marquina", "$4.50", "IN STOCK");
                }
            });
        }

        CardView similarOak = findViewById(R.id.similarOak);
        if (similarOak != null) {
            similarOak.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    navigateToSimilarProduct("Oak Wood", "$2.90", "IN STOCK");
                }
            });
        }

        // Bottom Navigation
        setupBottomNavigation();
    }

    private void toggleFavorite() {
        isFavorite = !isFavorite;
        updateFavoriteIcon();

        String message = isFavorite ? "Added to favorites" : "Removed from favorites";
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();

        // Save favorite status to SharedPreferences
        saveFavoriteStatus();
    }

    private void saveFavoriteStatus() {
        SharedPreferences.Editor editor = favoritesPrefs.edit();
        editor.putBoolean(currentProductName, isFavorite);
        editor.apply();
    }

    private void navigateToSimilarProduct(String name, String price, String stock) {
        Intent intent = new Intent(this, ProductDetailsActivity.class);

        // Pass product data
        intent.putExtra("productName", name);
        intent.putExtra("productPrice", price);
        intent.putExtra("productStock", stock);
        intent.putExtra("productModel", "FL-" + name.substring(0, Math.min(2, name.length())).toUpperCase() +
                "-" + String.format("%03d", (int)(Math.random() * 1000)));
        intent.putExtra("productMaterial", "Porcelain");
        intent.putExtra("productFinish", "High Gloss");
        intent.putExtra("productThickness", "9mm");
        intent.putExtra("productSize", "600×1200mm");
        intent.putExtra("productCoverage", "1.44m²/box");
        intent.putExtra("productPacking", "2 Pcs / Box");

        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    private void setupBottomNavigation() {
        // Home button
        LinearLayout btnNavHome = findViewById(R.id.btnNavHome);
        if (btnNavHome != null) {
            btnNavHome.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(ProductDetailsActivity.this, CustomerHomeActivity.class);
                    startActivity(intent);
                    finish();
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                }
            });
        }

        // Catalog button
        LinearLayout btnNavCatalog = findViewById(R.id.btnNavCatalog);
        if (btnNavCatalog != null) {
            btnNavCatalog.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(ProductDetailsActivity.this, CatalogActivity.class);
                    startActivity(intent);
                    finish();
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                }
            });
        }

        // Enquiries button
        LinearLayout btnNavEnquiries = findViewById(R.id.btnNavEnquiries);
        if (btnNavEnquiries != null) {
            btnNavEnquiries.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(ProductDetailsActivity.this, "Enquiries", Toast.LENGTH_SHORT).show();
                }
            });
        }

        // Account button
        LinearLayout btnNavAccount = findViewById(R.id.btnNavAccount);
        if (btnNavAccount != null) {
            btnNavAccount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(ProductDetailsActivity.this, "Account", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void goBackToCatalog() {
        Intent intent = new Intent(ProductDetailsActivity.this, CatalogActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Override
    public void onBackPressed() {
        goBackToCatalog();
    }
}