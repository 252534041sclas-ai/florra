package com.example.florra_a;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    private EditText searchEditText;
    private RecyclerView recyclerView;
    private TileAdapter tileAdapter;
    private List<Tile> tileList;
    private LinearLayout btnAll, btnFloorTiles, btnWallTiles, btnBathroom, btnKitchen, btnOutdoor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set fullscreen
        getWindow().setFlags(android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN,
                android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_search);

        // Initialize views
        initializeViews();

        // Setup click listeners
        setupClickListeners();

        // Setup tiles
        setupTiles();

        // Set search query if passed from previous activity
        String searchQuery = getIntent().getStringExtra("searchQuery");
        if (searchQuery != null && !searchQuery.isEmpty()) {
            searchEditText.setText(searchQuery);
        }
    }

    private void initializeViews() {
        // Back button
        LinearLayout btnBack = findViewById(R.id.btnBack);

        // Search field
        searchEditText = findViewById(R.id.editTextSearch);

        // Clear search button
        ImageView btnClearSearch = findViewById(R.id.btnClearSearch);

        // Filter button
        LinearLayout btnFilter = findViewById(R.id.btnFilter);

        // Category buttons
        btnAll = findViewById(R.id.btnAll);
        btnFloorTiles = findViewById(R.id.btnFloorTiles);
        btnWallTiles = findViewById(R.id.btnWallTiles);
        btnBathroom = findViewById(R.id.btnBathroom);
        btnKitchen = findViewById(R.id.btnKitchen);
        btnOutdoor = findViewById(R.id.btnOutdoor);

        // Sort button
        LinearLayout btnSort = findViewById(R.id.btnSort);

        // RecyclerView
        recyclerView = findViewById(R.id.recyclerViewSearch);

        // Set All as active by default
        setCategoryActive(btnAll);
    }

    private void setupClickListeners() {
        // Back button
        LinearLayout btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        }

        // Clear search button
        ImageView btnClearSearch = findViewById(R.id.btnClearSearch);
        if (btnClearSearch != null) {
            btnClearSearch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    searchEditText.setText("");
                }
            });
        }

        // Filter button
        LinearLayout btnFilter = findViewById(R.id.btnFilter);
        if (btnFilter != null) {
            btnFilter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(SearchActivity.this, "Filter", Toast.LENGTH_SHORT).show();
                }
            });
        }

        // Sort button
        LinearLayout btnSort = findViewById(R.id.btnSort);
        if (btnSort != null) {
            btnSort.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(SearchActivity.this, "Sort by", Toast.LENGTH_SHORT).show();
                }
            });
        }

        // Category buttons
        setupCategoryButtons();

        // Bottom Navigation
        setupBottomNavigation();
    }

    private void setupCategoryButtons() {
        btnAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setCategoryActive(btnAll);
                Toast.makeText(SearchActivity.this, "All", Toast.LENGTH_SHORT).show();
            }
        });

        btnFloorTiles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setCategoryActive(btnFloorTiles);
                Toast.makeText(SearchActivity.this, "Floor Tiles", Toast.LENGTH_SHORT).show();
            }
        });

        btnWallTiles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setCategoryActive(btnWallTiles);
                Toast.makeText(SearchActivity.this, "Wall Tiles", Toast.LENGTH_SHORT).show();
            }
        });

        btnBathroom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setCategoryActive(btnBathroom);
                Toast.makeText(SearchActivity.this, "Bathroom", Toast.LENGTH_SHORT).show();
            }
        });

        btnKitchen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setCategoryActive(btnKitchen);
                Toast.makeText(SearchActivity.this, "Kitchen", Toast.LENGTH_SHORT).show();
            }
        });

        btnOutdoor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setCategoryActive(btnOutdoor);
                Toast.makeText(SearchActivity.this, "Outdoor", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setCategoryActive(LinearLayout activeButton) {
        // Reset all buttons
        resetCategoryButtons();

        // Set active button style
        activeButton.setBackgroundResource(R.drawable.bg_category_active);
        TextView textView = (TextView) activeButton.getChildAt(0);
        if (textView != null) {
            textView.setTextColor(getResources().getColor(R.color.white));
        }
    }

    private void resetCategoryButtons() {
        LinearLayout[] buttons = {btnAll, btnFloorTiles, btnWallTiles, btnBathroom, btnKitchen, btnOutdoor};
        int[] buttonIds = {R.id.btnAll, R.id.btnFloorTiles, R.id.btnWallTiles, R.id.btnBathroom, R.id.btnKitchen, R.id.btnOutdoor};
        String[] buttonTexts = {"All", "Floor Tiles", "Wall Tiles", "Bathroom", "Kitchen", "Outdoor"};

        for (int i = 0; i < buttons.length; i++) {
            buttons[i].setBackgroundResource(R.drawable.bg_category_inactive);
            TextView textView = (TextView) buttons[i].getChildAt(0);
            if (textView != null) {
                textView.setText(buttonTexts[i]);
                textView.setTextColor(getResources().getColor(R.color.slate_600));
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
                    goToHome();
                }
            });
        }

        // Catalog button
        LinearLayout btnNavCatalog = findViewById(R.id.btnNavCatalog);
        if (btnNavCatalog != null) {
            btnNavCatalog.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goToCatalog();
                }
            });
        }

        // Enquiries button
        LinearLayout btnNavEnquiries = findViewById(R.id.btnNavEnquiries);
        if (btnNavEnquiries != null) {
            btnNavEnquiries.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(SearchActivity.this, "Enquiries", Toast.LENGTH_SHORT).show();
                }
            });
        }

        // Account button
        LinearLayout btnNavAccount = findViewById(R.id.btnNavAccount);
        if (btnNavAccount != null) {
            btnNavAccount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(SearchActivity.this, "Account", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void setupTiles() {
        // Create tile list for search results
        tileList = new ArrayList<>();

        // Add search result tiles - matching the HTML design
        tileList.add(new Tile("Statuario White", "$4.50", "60x120cm • High Gloss", "IN STOCK",
                R.drawable.tile_placeholder, "stock"));
        tileList.add(new Tile("Nero Marquina", "$3.20", "60x60cm • Matte", "IN STOCK",
                R.drawable.tile_placeholder, "stock"));
        tileList.add(new Tile("Travertine Beige", "$2.80", "30x60cm • Rustic", "IN STOCK",
                R.drawable.tile_placeholder, "stock"));
        tileList.add(new Tile("Carrara Grey", "$5.00", "80x80cm • Polished", "IN STOCK",
                R.drawable.tile_placeholder, "stock"));

        // Setup RecyclerView
        tileAdapter = new TileAdapter(this, tileList);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.setAdapter(tileAdapter);

        // Set item click listener
        tileAdapter.setOnItemClickListener(new TileAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Tile tile) {
                openProductDetails(tile);
            }

            @Override
            public void onBookmarkClick(Tile tile) {
                Toast.makeText(SearchActivity.this, "Added to favorites: " + tile.getName(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAddToCartClick(Tile tile) {
                Toast.makeText(SearchActivity.this, "Added to cart: " + tile.getName(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openProductDetails(Tile tile) {
        Intent intent = new Intent(SearchActivity.this, ProductDetailsActivity.class);
        intent.putExtra("tileName", tile.getName());
        intent.putExtra("tilePrice", tile.getPrice());
        intent.putExtra("tileSize", tile.getSize());
        intent.putExtra("tileStock", tile.getStock());
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    private void goToHome() {
        Intent intent = new Intent(SearchActivity.this, CustomerHomeActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    private void goToCatalog() {
        Intent intent = new Intent(SearchActivity.this, CatalogActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}