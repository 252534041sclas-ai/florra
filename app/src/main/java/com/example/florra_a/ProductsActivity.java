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
import androidx.cardview.widget.CardView;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsControllerCompat;

public class ProductsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set fullscreen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Enable edge-to-edge
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);

        // Handle notch and status bar
        WindowInsetsControllerCompat windowInsetsController =
                WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());
        windowInsetsController.setAppearanceLightStatusBars(false);
        windowInsetsController.setAppearanceLightNavigationBars(false);

        setContentView(R.layout.activity_products);

        // Show toast
        Toast.makeText(this, "Products List", Toast.LENGTH_SHORT).show();

        // Setup navigation
        setupNavigation();
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
                    Toast.makeText(ProductsActivity.this, "More options", Toast.LENGTH_SHORT).show();
                }
            });
        }

        // Filter buttons
        Button btnAll = findViewById(R.id.btnAll);
        if (btnAll != null) {
            btnAll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(ProductsActivity.this, "Showing all products", Toast.LENGTH_SHORT).show();
                }
            });
        }

        Button btnCategory = findViewById(R.id.btnCategory);
        if (btnCategory != null) {
            btnCategory.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(ProductsActivity.this, "Filter by category", Toast.LENGTH_SHORT).show();
                }
            });
        }

        Button btnSize = findViewById(R.id.btnSize);
        if (btnSize != null) {
            btnSize.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(ProductsActivity.this, "Filter by size", Toast.LENGTH_SHORT).show();
                }
            });
        }

        Button btnStock = findViewById(R.id.btnStock);
        if (btnStock != null) {
            btnStock.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(ProductsActivity.this, "Filter by stock", Toast.LENGTH_SHORT).show();
                }
            });
        }

        // Add Product button
        Button btnAddProduct = findViewById(R.id.btnAddProduct);
        if (btnAddProduct != null) {
            btnAddProduct.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(ProductsActivity.this, AddProductActivity.class);
                    startActivity(intent);
                }
            });
        }

        // Product 1: Statuario Gold - Click Listener
        CardView productCard1 = findViewById(R.id.productCard1);
        if (productCard1 != null) {
            productCard1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(ProductsActivity.this, AdminProductDetailsActivity.class);
                    intent.putExtra("product_name", "Statuario Gold");
                    intent.putExtra("product_sku", "SG-001");
                    intent.putExtra("product_category", "Vitrified");
                    intent.putExtra("product_size", "600×1200mm");
                    intent.putExtra("product_price", "₹65/sq.ft");
                    intent.putExtra("product_stock", "450");
                    intent.putExtra("product_status", "In Stock");
                    intent.putExtra("product_warehouse", "Zone A");
                    intent.putExtra("product_dimensions", "600 x 1200 mm");
                    intent.putExtra("product_finish", "High Gloss");
                    intent.putExtra("product_thickness", "10 mm");
                    intent.putExtra("product_coverage", "1.44 sq. mt / box");
                    intent.putExtra("product_description", "Premium vitrified tile with gold veining, perfect for luxury interiors. High gloss finish with excellent durability and stain resistance.");
                    intent.putExtra("product_margin", "22%");
                    intent.putExtra("product_retail_price", "₹80/sq.ft");
                    startActivity(intent);
                }
            });
        }

        // Product 2: Rustic Oak Plank - Click Listener
        CardView productCard2 = findViewById(R.id.productCard2);
        if (productCard2 != null) {
            productCard2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(ProductsActivity.this, ProductDetailsActivity.class);
                    intent.putExtra("product_name", "Rustic Oak Plank");
                    intent.putExtra("product_sku", "ROP-002");
                    intent.putExtra("product_category", "Ceramic");
                    intent.putExtra("product_size", "200×1000mm");
                    intent.putExtra("product_price", "₹45/sq.ft");
                    intent.putExtra("product_stock", "12");
                    intent.putExtra("product_status", "Low Stock");
                    intent.putExtra("product_warehouse", "Zone B");
                    intent.putExtra("product_dimensions", "200 x 1000 mm");
                    intent.putExtra("product_finish", "Matte");
                    intent.putExtra("product_thickness", "8 mm");
                    intent.putExtra("product_coverage", "1.0 sq. mt / box");
                    intent.putExtra("product_description", "Ceramic wood-look plank tiles with rustic oak texture. Matte finish ideal for residential flooring. Water and scratch resistant.");
                    intent.putExtra("product_margin", "18%");
                    intent.putExtra("product_retail_price", "₹55/sq.ft");
                    startActivity(intent);
                }
            });
        }

        // Product 3: Urban Cement Grey - Click Listener
        CardView productCard3 = findViewById(R.id.productCard3);
        if (productCard3 != null) {
            productCard3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(ProductsActivity.this, ProductDetailsActivity.class);
                    intent.putExtra("product_name", "Urban Cement Grey");
                    intent.putExtra("product_sku", "UCG-003");
                    intent.putExtra("product_category", "Porcelain");
                    intent.putExtra("product_size", "600×600mm");
                    intent.putExtra("product_price", "₹52/sq.ft");
                    intent.putExtra("product_stock", "820");
                    intent.putExtra("product_status", "In Stock");
                    intent.putExtra("product_warehouse", "Zone C");
                    intent.putExtra("product_dimensions", "600 x 600 mm");
                    intent.putExtra("product_finish", "Satin");
                    intent.putExtra("product_thickness", "9 mm");
                    intent.putExtra("product_coverage", "1.08 sq. mt / box");
                    intent.putExtra("product_description", "Modern porcelain tiles with cement grey texture. Suitable for both residential and commercial spaces. High durability and low maintenance.");
                    intent.putExtra("product_margin", "20%");
                    intent.putExtra("product_retail_price", "₹65/sq.ft");
                    startActivity(intent);
                }
            });
        }

        // Product 4: Azure Mosaic - Click Listener
        CardView productCard4 = findViewById(R.id.productCard4);
        if (productCard4 != null) {
            productCard4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(ProductsActivity.this, ProductDetailsActivity.class);
                    intent.putExtra("product_name", "Azure Mosaic");
                    intent.putExtra("product_sku", "AM-004");
                    intent.putExtra("product_category", "Glass");
                    intent.putExtra("product_size", "300×300mm");
                    intent.putExtra("product_price", "₹120/sheet");
                    intent.putExtra("product_stock", "0");
                    intent.putExtra("product_status", "Out of Stock");
                    intent.putExtra("product_warehouse", "Zone D");
                    intent.putExtra("product_dimensions", "300 x 300 mm");
                    intent.putExtra("product_finish", "Glossy");
                    intent.putExtra("product_thickness", "6 mm");
                    intent.putExtra("product_coverage", "0.5 sq. mt / sheet");
                    intent.putExtra("product_description", "Blue glass mosaic tiles perfect for bathroom and kitchen backsplashes. Water-resistant, easy to clean, and reflects light beautifully.");
                    intent.putExtra("product_margin", "25%");
                    intent.putExtra("product_retail_price", "₹150/sheet");
                    startActivity(intent);
                }
            });
        }

        // Product 5: Midnight Slate - Click Listener
        CardView productCard5 = findViewById(R.id.productCard5);
        if (productCard5 != null) {
            productCard5.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(ProductsActivity.this, ProductDetailsActivity.class);
                    intent.putExtra("product_name", "Midnight Slate");
                    intent.putExtra("product_sku", "MS-005");
                    intent.putExtra("product_category", "Natural Stone");
                    intent.putExtra("product_size", "400×400mm");
                    intent.putExtra("product_price", "₹85/sq.ft");
                    intent.putExtra("product_stock", "150");
                    intent.putExtra("product_status", "In Stock");
                    intent.putExtra("product_warehouse", "Zone A");
                    intent.putExtra("product_dimensions", "400 x 400 mm");
                    intent.putExtra("product_finish", "Natural");
                    intent.putExtra("product_thickness", "12 mm");
                    intent.putExtra("product_coverage", "0.96 sq. mt / box");
                    intent.putExtra("product_description", "Natural slate tiles with dark grey finish. Ideal for outdoor patios, garden pathways, and rustic interiors. Naturally slip-resistant.");
                    intent.putExtra("product_margin", "30%");
                    intent.putExtra("product_retail_price", "₹110/sq.ft");
                    startActivity(intent);
                }
            });
        }

        // More options on each product
        ImageView moreOptions1 = findViewById(R.id.moreOptions1);
        if (moreOptions1 != null) {
            moreOptions1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showProductOptionsMenu("Statuario Gold", "SG-001");
                }
            });
        }

        ImageView moreOptions2 = findViewById(R.id.moreOptions2);
        if (moreOptions2 != null) {
            moreOptions2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showProductOptionsMenu("Rustic Oak Plank", "ROP-002");
                }
            });
        }

        // More options for product 3
        ImageView moreOptions3 = findViewById(R.id.moreOptions3);
        if (moreOptions3 != null) {
            moreOptions3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showProductOptionsMenu("Urban Cement Grey", "UCG-003");
                }
            });
        }

        // More options for product 4
        ImageView moreOptions4 = findViewById(R.id.moreOptions4);
        if (moreOptions4 != null) {
            moreOptions4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showProductOptionsMenu("Azure Mosaic", "AM-004");
                }
            });
        }

        // More options for product 5
        ImageView moreOptions5 = findViewById(R.id.moreOptions5);
        if (moreOptions5 != null) {
            moreOptions5.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showProductOptionsMenu("Midnight Slate", "MS-005");
                }
            });
        }
    }

    private void showProductOptionsMenu(String productName, String productSku) {
        String[] options = {"Edit Product", "Update Stock", "Duplicate", "View History", "Delete"};

        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setTitle(productName)
                .setItems(options, (dialog, which) -> {
                    switch (which) {
                        case 0: // Edit Product
                            Toast.makeText(ProductsActivity.this, "Edit " + productName, Toast.LENGTH_SHORT).show();
                            // Open edit activity
                            break;
                        case 1: // Update Stock
                            Toast.makeText(ProductsActivity.this, "Update stock for " + productName, Toast.LENGTH_SHORT).show();
                            break;
                        case 2: // Duplicate
                            Toast.makeText(ProductsActivity.this, "Duplicate " + productName, Toast.LENGTH_SHORT).show();
                            break;
                        case 3: // View History
                            Toast.makeText(ProductsActivity.this, "View history for " + productName, Toast.LENGTH_SHORT).show();
                            break;
                        case 4: // Delete
                            showDeleteConfirmation(productName, productSku);
                            break;
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showDeleteConfirmation(String productName, String productSku) {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setTitle("Delete Product")
                .setMessage("Are you sure you want to delete " + productName + " (SKU: " + productSku + ")?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    Toast.makeText(ProductsActivity.this, productName + " deleted", Toast.LENGTH_SHORT).show();
                    // Add actual delete logic here
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}