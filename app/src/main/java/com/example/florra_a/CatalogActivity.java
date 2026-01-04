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
import com.example.florra_a.Tile;

public class CatalogActivity extends AppCompatActivity {

    private TileAdapter tileAdapter;
    private ArrayList<Tile> allTiles = new ArrayList<>();
    private String selectedFilter = "all"; // Default filter

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set fullscreen
        getWindow().setFlags(android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN,
                android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_catalog);

        // Check if intent has filter parameter
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("filter_type")) {
            selectedFilter = intent.getStringExtra("filter_type");
        }

        setupAllClickListeners();
        setupTiles();
        applyFilter(selectedFilter);
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
                    openSearchScreen();
                }
            });
        }

        // Filter categories
        setupFilterButtons();

        // Bottom Navigation
        setupBottomNavigation();
    }

    private void setupFilterButtons() {
        LinearLayout btnAllTiles = findViewById(R.id.btnAllTiles);
        LinearLayout btnCeramic = findViewById(R.id.btnCeramic);
        LinearLayout btnPorcelain = findViewById(R.id.btnPorcelain);
        LinearLayout btnWall = findViewById(R.id.btnWall);
        LinearLayout btnFloor = findViewById(R.id.btnFloor);
        LinearLayout btnMosaic = findViewById(R.id.btnMosaic);

        // Reset all buttons first
        resetFilterButtons();

        // Set initial state based on selectedFilter
        switch (selectedFilter) {
            case "floor_tiles":
                activateButton(btnFloor, "Floor");
                break;
            case "wall_tiles":
                activateButton(btnWall, "Wall");
                break;
            case "bathroom":
                activateButton(btnAllTiles, "All Tiles"); // Or create a bathroom filter
                break;
            case "kitchen":
                activateButton(btnAllTiles, "All Tiles"); // Or create a kitchen filter
                break;
            default:
                activateButton(btnAllTiles, "All Tiles");
                break;
        }

        // Set click listeners
        if (btnAllTiles != null) {
            btnAllTiles.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    applyFilter("all");
                    resetFilterButtons();
                    activateButton(btnAllTiles, "All Tiles");
                }
            });
        }

        if (btnFloor != null) {
            btnFloor.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    applyFilter("floor");
                    resetFilterButtons();
                    activateButton(btnFloor, "Floor");
                }
            });
        }

        if (btnWall != null) {
            btnWall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    applyFilter("wall");
                    resetFilterButtons();
                    activateButton(btnWall, "Wall");
                }
            });
        }

        // Add other filter buttons similarly
    }

    private void resetFilterButtons() {
        int[][] buttonIds = {
                {R.id.btnAllTiles, R.drawable.bg_filter_inactive},
                {R.id.btnCeramic, R.drawable.bg_filter_inactive},
                {R.id.btnPorcelain, R.drawable.bg_filter_inactive},
                {R.id.btnWall, R.drawable.bg_filter_inactive},
                {R.id.btnFloor, R.drawable.bg_filter_inactive},
                {R.id.btnMosaic, R.drawable.bg_filter_inactive}
        };

        for (int[] id : buttonIds) {
            LinearLayout button = findViewById(id[0]);
            if (button != null) {
                button.setBackgroundResource(id[1]);
                if (button.getChildAt(0) instanceof TextView) {
                    ((TextView) button.getChildAt(0)).setTextColor(getResources().getColor(R.color.slate_600));
                }
            }
        }
    }

    private void activateButton(LinearLayout button, String buttonText) {
        if (button != null) {
            button.setBackgroundResource(R.drawable.bg_filter_active);
            if (button.getChildAt(0) instanceof TextView) {
                ((TextView) button.getChildAt(0)).setTextColor(getResources().getColor(R.color.white));
                ((TextView) button.getChildAt(0)).setText(buttonText);
            }
        }
    }

    private void setupBottomNavigation() {
        // Home button
        LinearLayout btnNavHome = findViewById(R.id.btnNavHome);
        if (btnNavHome != null) {
            btnNavHome.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goBackToHome();
                }
            });
        }

        // Catalog button
        LinearLayout btnNavCatalog = findViewById(R.id.btnNavCatalog);
        if (btnNavCatalog != null) {
            btnNavCatalog.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Already on catalog
                }
            });
        }

        // Enquiries button - Updated to open Quotations
        LinearLayout btnNavEnquiries = findViewById(R.id.btnNavEnquiries);
        if (btnNavEnquiries != null) {
            btnNavEnquiries.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openQuotationsScreen();
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

    private void setupTiles() {
        // Create tile list with categories
        allTiles.clear();

        // Floor Tiles
        allTiles.add(new Tile("Carrara White", "$3.50", "60×60", "IN STOCK",
                R.drawable.tile_placeholder, "stock", "floor"));
        allTiles.add(new Tile("Armani Grey", "$3.80", "60×60", "IN STOCK",
                R.drawable.tile_placeholder, "stock", "floor"));
        allTiles.add(new Tile("Oak Wood", "$2.90", "30×60", "LOW STOCK",
                R.drawable.tile_placeholder, "low_stock", "floor"));

        // Wall Tiles
        allTiles.add(new Tile("Statuario Classic", "$4.50", "60×120", "IN STOCK",
                R.drawable.tile_placeholder, "stock", "wall"));
        allTiles.add(new Tile("Crema Marfil", "$4.10", "60×120", "IN STOCK",
                R.drawable.tile_placeholder, "stock", "wall"));

        // Bathroom Tiles
        allTiles.add(new Tile("Nero Marquina", "$3.80", "60×60", "LOW STOCK",
                R.drawable.tile_placeholder, "low_stock", "bathroom"));
        allTiles.add(new Tile("Marble White", "$4.20", "30×60", "IN STOCK",
                R.drawable.tile_placeholder, "stock", "bathroom"));

        // Kitchen Tiles
        allTiles.add(new Tile("Travertine Beige", "$2.90", "30×60", "IN STOCK",
                R.drawable.tile_placeholder, "stock", "kitchen"));
        allTiles.add(new Tile("Black Marquina", "$4.50", "60×60", "IN STOCK",
                R.drawable.tile_placeholder, "stock", "kitchen"));

        // Add more tiles...
        allTiles.add(new Tile("Bianco Carrara", "$3.75", "60×60", "IN STOCK",
                R.drawable.tile_placeholder, "stock", "floor"));
        allTiles.add(new Tile("Calacatta Gold", "$5.20", "60×120", "IN STOCK",
                R.drawable.tile_placeholder, "stock", "wall"));
        allTiles.add(new Tile("Subway White", "$2.50", "10×20", "IN STOCK",
                R.drawable.tile_placeholder, "stock", "kitchen"));
        allTiles.add(new Tile("Hexagon Blue", "$3.25", "Hexagon", "IN STOCK",
                R.drawable.tile_placeholder, "stock", "bathroom"));

        // Setup RecyclerView
        RecyclerView recyclerView = findViewById(R.id.recyclerViewTiles);
        if (recyclerView != null) {
            tileAdapter = new TileAdapter(this, allTiles);
            recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
            recyclerView.setAdapter(tileAdapter);
        }
    }

    private void applyFilter(String filterType) {
        ArrayList<Tile> filteredList = new ArrayList<>();

        if (filterType.equals("all")) {
            filteredList.addAll(allTiles);
        } else {
            for (Tile tile : allTiles) {
                // This assumes Tile class has a getCategory() method
                // You'll need to update your Tile class to include category
                if (tile.getCategory().equals(filterType)) {
                    filteredList.add(tile);
                }
            }
        }

        if (tileAdapter != null) {
            tileAdapter.filterList(filteredList);
        }
    }

    private void goBackToHome() {
        Intent intent = new Intent(CatalogActivity.this, CustomerHomeActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    private void openSearchScreen() {
        try {
            Intent intent = new Intent(CatalogActivity.this, SearchActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        } catch (Exception e) {
            Toast.makeText(this, "Cannot open Search", Toast.LENGTH_SHORT).show();
        }
    }

    // New navigation methods
    private void openQuotationsScreen() {
        try {
            Intent intent = new Intent(CatalogActivity.this, QuotationsActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        } catch (Exception e) {
            Toast.makeText(this, "Cannot open Quotations", Toast.LENGTH_SHORT).show();
        }
    }

    private void openAccountScreen() {
        try {
            Intent intent = new Intent(CatalogActivity.this, CustomerAccountActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        } catch (Exception e) {
            Toast.makeText(this, "Cannot open Account", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        goBackToHome();
    }
}