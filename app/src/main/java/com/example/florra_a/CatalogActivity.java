package com.example.florra_a;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class CatalogActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set fullscreen
        getWindow().setFlags(android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN,
                android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_catalog);

        setupAllClickListeners();
        setupTiles();
    }

    private void setupAllClickListeners() {
        // Back button
        ImageView btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goBackToHome();
                }
            });
        }

        // Search button
        ImageView btnSearch = findViewById(R.id.btnSearch);
        if (btnSearch != null) {
            btnSearch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openSearchScreen(); // CHANGED: Now opens Search screen
                }
            });
        }

        // Filter button
        ImageView btnFilter = findViewById(R.id.btnFilter);
        if (btnFilter != null) {
            btnFilter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(CatalogActivity.this, "Filter", Toast.LENGTH_SHORT).show();
                }
            });
        }

        // Filter categories
        setupFilterButtons();

        // Bottom Navigation
        setupBottomNavigation();

        // Floating Action Button
        LinearLayout fab = findViewById(R.id.fab);
        if (fab != null) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(CatalogActivity.this, "Add New", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void setupFilterButtons() {
        LinearLayout btnAllTiles = findViewById(R.id.btnAllTiles);
        if (btnAllTiles != null) {
            btnAllTiles.setBackgroundResource(R.drawable.bg_filter_active);
            if (btnAllTiles.getChildAt(0) instanceof TextView) {
                ((TextView) btnAllTiles.getChildAt(0)).setTextColor(getResources().getColor(R.color.white));
            }

            btnAllTiles.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(CatalogActivity.this, "All Tiles", Toast.LENGTH_SHORT).show();
                }
            });
        }

        // Other filter buttons can be added similarly
        // btnCeramic, btnPorcelain, etc.
    }

    private void setupBottomNavigation() {
        // Home button - Go back to Home
        LinearLayout btnNavHome = findViewById(R.id.btnNavHome);
        if (btnNavHome != null) {
            btnNavHome.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goBackToHome();
                }
            });
        }

        // Catalog button - Already on Catalog
        LinearLayout btnNavCatalog = findViewById(R.id.btnNavCatalog);
        if (btnNavCatalog != null) {
            btnNavCatalog.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Already on catalog
                }
            });
        }

        // Enquiries button
        LinearLayout btnNavEnquiries = findViewById(R.id.btnNavEnquiries);
        if (btnNavEnquiries != null) {
            btnNavEnquiries.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(CatalogActivity.this, "Enquiries", Toast.LENGTH_SHORT).show();
                }
            });
        }

        // Account button
        LinearLayout btnNavAccount = findViewById(R.id.btnNavAccount);
        if (btnNavAccount != null) {
            btnNavAccount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(CatalogActivity.this, "Account", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void setupTiles() {
        // Create tile list
        ArrayList<Tile> tileList = new ArrayList<>();

        // Add tiles - IMPORTANT: Size should be just the dimensions, not including finish
        tileList.add(new Tile("Carrara White", "$3.50", "60×60", "IN STOCK",
                R.drawable.tile_placeholder, "stock"));
        tileList.add(new Tile("Nero Marquina", "$3.80", "60×60", "LOW STOCK",
                R.drawable.tile_placeholder, "low_stock"));
        tileList.add(new Tile("Statuario Classic", "$4.50", "60×120", "IN STOCK",
                R.drawable.tile_placeholder, "stock"));
        tileList.add(new Tile("Travertine Beige", "$2.90", "30×60", "IN STOCK",
                R.drawable.tile_placeholder, "stock"));

        // Add more tiles for testing
        tileList.add(new Tile("Armani Grey", "$3.80", "60×60", "IN STOCK",
                R.drawable.tile_placeholder, "stock"));
        tileList.add(new Tile("Crema Marfil", "$4.10", "60×120", "IN STOCK",
                R.drawable.tile_placeholder, "stock"));
        tileList.add(new Tile("Black Marquina", "$4.50", "60×60", "IN STOCK",
                R.drawable.tile_placeholder, "stock"));
        tileList.add(new Tile("Oak Wood", "$2.90", "30×60", "LOW STOCK",
                R.drawable.tile_placeholder, "low_stock"));

        // Setup RecyclerView
        RecyclerView recyclerView = findViewById(R.id.recyclerViewTiles);
        if (recyclerView != null) {
            TileAdapter tileAdapter = new TileAdapter(this, tileList);
            recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
            recyclerView.setAdapter(tileAdapter);
        }
    }

    private void goBackToHome() {
        Intent intent = new Intent(CatalogActivity.this, CustomerHomeActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    // NEW METHOD: Open Search Screen
    private void openSearchScreen() {
        try {
            Intent intent = new Intent(CatalogActivity.this, SearchActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        } catch (Exception e) {
            Toast.makeText(this, "Cannot open Search", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        goBackToHome();
    }
}