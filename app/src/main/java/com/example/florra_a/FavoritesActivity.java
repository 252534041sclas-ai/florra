package com.example.florra_a;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class FavoritesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set fullscreen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_favorites);

        setupAllClickListeners();
    }

    private void setupAllClickListeners() {
        // Navigation buttons
        setupNavigationButtons();

        // View toggle buttons
        setupViewToggleButtons();

        // Header buttons
        setupHeaderButtons();

        // Tile buttons
        setupTileButtons();

        // Bottom navigation
        setupBottomNavigation();
    }

    private void setupNavigationButtons() {
        // Filter button
        RelativeLayout btnFilter = findViewById(R.id.btnFilter);
        if (btnFilter != null) {
            btnFilter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(FavoritesActivity.this, "Filter", Toast.LENGTH_SHORT).show();
                }
            });
        }

        // Select to Compare
        TextView btnSelectCompare = findViewById(R.id.btnSelectCompare);
        if (btnSelectCompare != null) {
            btnSelectCompare.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(FavoritesActivity.this, "Select to Compare", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void setupViewToggleButtons() {
        // Grid View button
        LinearLayout btnGridView = findViewById(R.id.btnGridView);
        if (btnGridView != null) {
            btnGridView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Already on Grid view
                }
            });
        }

        // List View button
        LinearLayout btnListView = findViewById(R.id.btnListView);
        if (btnListView != null) {
            btnListView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(FavoritesActivity.this, "List View", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void setupHeaderButtons() {
        // Request Quotation button
        RelativeLayout btnRequestQuotation = findViewById(R.id.btnRequestQuotation);
        if (btnRequestQuotation != null) {
            btnRequestQuotation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(FavoritesActivity.this, "Request Quotation", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void setupTileButtons() {
        // Tile 1
        LinearLayout tile1 = findViewById(R.id.tile1);
        if (tile1 != null) {
            tile1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(FavoritesActivity.this, "Statuario Venato", Toast.LENGTH_SHORT).show();
                }
            });
        }

        // Favorite buttons
        setupFavoriteButtons();

        // Details buttons
        setupDetailsButtons();

        // Add Comment buttons
        setupAddCommentButtons();
    }

    private void setupFavoriteButtons() {
        // Favorite 1
        RelativeLayout btnFavorite1 = findViewById(R.id.btnFavorite1);
        if (btnFavorite1 != null) {
            btnFavorite1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(FavoritesActivity.this, "Removed from favorites", Toast.LENGTH_SHORT).show();
                }
            });
        }

        // Favorite 2
        RelativeLayout btnFavorite2 = findViewById(R.id.btnFavorite2);
        if (btnFavorite2 != null) {
            btnFavorite2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(FavoritesActivity.this, "Removed from favorites", Toast.LENGTH_SHORT).show();
                }
            });
        }

        // Favorite 3
        RelativeLayout btnFavorite3 = findViewById(R.id.btnFavorite3);
        if (btnFavorite3 != null) {
            btnFavorite3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(FavoritesActivity.this, "Removed from favorites", Toast.LENGTH_SHORT).show();
                }
            });
        }

        // Favorite 4
        RelativeLayout btnFavorite4 = findViewById(R.id.btnFavorite4);
        if (btnFavorite4 != null) {
            btnFavorite4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(FavoritesActivity.this, "Removed from favorites", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void setupDetailsButtons() {
        // Details 1 - Now TextView
        TextView btnDetails1 = findViewById(R.id.btnDetails1);
        if (btnDetails1 != null) {
            btnDetails1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(FavoritesActivity.this, "Details", Toast.LENGTH_SHORT).show();
                }
            });
        }

        // Details 2
        TextView btnDetails2 = findViewById(R.id.btnDetails2);
        if (btnDetails2 != null) {
            btnDetails2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(FavoritesActivity.this, "Details", Toast.LENGTH_SHORT).show();
                }
            });
        }

        // Details 3
        TextView btnDetails3 = findViewById(R.id.btnDetails3);
        if (btnDetails3 != null) {
            btnDetails3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(FavoritesActivity.this, "Details", Toast.LENGTH_SHORT).show();
                }
            });
        }

        // Details 4
        TextView btnDetails4 = findViewById(R.id.btnDetails4);
        if (btnDetails4 != null) {
            btnDetails4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(FavoritesActivity.this, "Details", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void setupAddCommentButtons() {
        // Add Comment 1
        RelativeLayout btnAddComment1 = findViewById(R.id.btnAddComment1);
        if (btnAddComment1 != null) {
            btnAddComment1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(FavoritesActivity.this, "Add Comment", Toast.LENGTH_SHORT).show();
                }
            });
        }

        // Add Comment 2
        RelativeLayout btnAddComment2 = findViewById(R.id.btnAddComment2);
        if (btnAddComment2 != null) {
            btnAddComment2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(FavoritesActivity.this, "Add Comment", Toast.LENGTH_SHORT).show();
                }
            });
        }

        // Add Comment 3
        RelativeLayout btnAddComment3 = findViewById(R.id.btnAddComment3);
        if (btnAddComment3 != null) {
            btnAddComment3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(FavoritesActivity.this, "Add Comment", Toast.LENGTH_SHORT).show();
                }
            });
        }

        // Add Comment 4
        RelativeLayout btnAddComment4 = findViewById(R.id.btnAddComment4);
        if (btnAddComment4 != null) {
            btnAddComment4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(FavoritesActivity.this, "Add Comment", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void setupBottomNavigation() {
        // Home button
        LinearLayout btnNavHome = findViewById(R.id.btnNavHome);
        if (btnNavHome != null) {
            btnNavHome.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Navigate to home
                    Intent intent = new Intent(FavoritesActivity.this, CustomerHomeActivity.class);
                    startActivity(intent);
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
                    Toast.makeText(FavoritesActivity.this, "Catalog", Toast.LENGTH_SHORT).show();
                }
            });
        }

        // Enquiries button
        LinearLayout btnNavEnquiries = findViewById(R.id.btnNavEnquiries);
        if (btnNavEnquiries != null) {
            btnNavEnquiries.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(FavoritesActivity.this, "Enquiries", Toast.LENGTH_SHORT).show();
                }
            });
        }

        // Account button
        LinearLayout btnNavAccount = findViewById(R.id.btnNavAccount);
        if (btnNavAccount != null) {
            btnNavAccount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(FavoritesActivity.this, "Account", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}