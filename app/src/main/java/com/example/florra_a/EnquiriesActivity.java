package com.example.florra_a;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsControllerCompat;

public class EnquiriesActivity extends AppCompatActivity {

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

        setContentView(R.layout.activity_enquiries);

        // Initialize views
        initViews();

        // Setup back button
        setupBackButton();

        // Setup bottom navigation
        setupBottomNavigation();

        // Setup filter buttons
        setupFilterButtons();


    }

    private void initViews() {
        // Initialize all the included item views
        setupEnquiryItem(R.id.itemMichael, "New", "2h ago", "Michael Foster",
                "Hi, I'm looking for Italian Carrara marble tiles for my living room...",
                "+1 (555) 123-4567", "Respond");

        setupEnquiryItem(R.id.itemSarah, "Quoted", "Yesterday", "Sarah Jenkins",
                "Quote #Q-2024-89 sent for \"Rustic Beige Ceramic\". Waiting for confirmation...",
                "$1,250.00", "View Quote");

        setupEnquiryItem(R.id.itemDavid, "Site Visit", "2 days ago", "David Chen",
                "Scheduled site visit for measurements on Friday at 10 AM. Client needs...",
                "Fri, Oct 24 • 10:00 AM", "Details");

        setupEnquiryItem(R.id.itemEmma, "Resolved", "1 week ago", "Emma Wilson",
                "Order delivered and installed. Customer left 5-star feedback.",
                "Order #9921", "Archive");
    }

    private void setupEnquiryItem(int viewId, String status, String timeAgo, String name,
                                  String text, String detail, String action) {
        View itemView = findViewById(viewId);
        if (itemView != null) {
            TextView tvStatus = itemView.findViewById(R.id.tvStatus);
            TextView tvTimeAgo = itemView.findViewById(R.id.tvTimeAgo);
            TextView tvCustomerName = itemView.findViewById(R.id.tvCustomerName);
            TextView tvEnquiryText = itemView.findViewById(R.id.tvEnquiryText);
            TextView tvDetailInfo = itemView.findViewById(R.id.tvDetailInfo);
            TextView tvAction = itemView.findViewById(R.id.tvAction);

            tvStatus.setText(status);
            tvTimeAgo.setText(timeAgo);
            tvCustomerName.setText(name);
            tvEnquiryText.setText(text);
            tvDetailInfo.setText(detail);
            tvAction.setText(action);

            // Set status background based on status
            switch (status) {
                case "New":
                    tvStatus.setBackgroundResource(R.drawable.bg_status_new);
                    break;
                case "Quoted":
                    tvStatus.setBackgroundResource(R.drawable.bg_status_quoted);
                    break;
                case "Site Visit":
                    tvStatus.setBackgroundResource(R.drawable.bg_status_site_visit);
                    break;
                case "Resolved":
                    tvStatus.setBackgroundResource(R.drawable.bg_status_resolved);
                    break;
            }

            // Set click listeners
            tvAction.setOnClickListener(v -> handleActionClick(status, name));
            itemView.setOnClickListener(v -> handleItemClick(status, name));
        }
    }

    private void handleActionClick(String status, String name) {
        String message = "Action clicked for " + name + " (" + status + ")";
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void handleItemClick(String status, String name) {
        String message = "Enquiry clicked: " + name + " (" + status + ")";
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void setupBackButton() {
        ImageView btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        }
    }

    private void setupFilterButtons() {
        // All filter
        View btnAll = findViewById(R.id.btnAll);
        if (btnAll != null) {
            btnAll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(EnquiriesActivity.this, "Showing all enquiries", Toast.LENGTH_SHORT).show();
                    updateFilterSelection("all");
                }
            });
        }

        // New filter
        View btnNew = findViewById(R.id.btnNew);
        if (btnNew != null) {
            btnNew.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(EnquiriesActivity.this, "Showing new enquiries", Toast.LENGTH_SHORT).show();
                    updateFilterSelection("new");
                }
            });
        }

        // Quoted filter
        View btnQuoted = findViewById(R.id.btnQuoted);
        if (btnQuoted != null) {
            btnQuoted.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(EnquiriesActivity.this, "Showing quoted enquiries", Toast.LENGTH_SHORT).show();
                    updateFilterSelection("quoted");
                }
            });
        }

        // Follow-up filter
        View btnFollowUp = findViewById(R.id.btnFollowUp);
        if (btnFollowUp != null) {
            btnFollowUp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(EnquiriesActivity.this, "Showing follow-up enquiries", Toast.LENGTH_SHORT).show();
                    updateFilterSelection("follow-up");
                }
            });
        }

        // Resolved filter
        View btnResolved = findViewById(R.id.btnResolved);
        if (btnResolved != null) {
            btnResolved.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(EnquiriesActivity.this, "Showing resolved enquiries", Toast.LENGTH_SHORT).show();
                    updateFilterSelection("resolved");
                }
            });
        }
    }

    private void updateFilterSelection(String selectedFilter) {
        // Reset all tabs to unselected
        int[] tabIds = {R.id.btnAll, R.id.btnNew, R.id.btnQuoted, R.id.btnFollowUp, R.id.btnResolved};

        for (int id : tabIds) {
            View tab = findViewById(id);
            if (tab != null) {
                tab.setBackgroundResource(R.drawable.bg_filter_tab_unselected);
            }
        }

        // Set selected tab
        switch (selectedFilter) {
            case "all":
                findViewById(R.id.btnAll).setBackgroundResource(R.drawable.bg_filter_tab_selected);
                break;
            case "new":
                findViewById(R.id.btnNew).setBackgroundResource(R.drawable.bg_filter_tab_selected);
                break;
            case "quoted":
                findViewById(R.id.btnQuoted).setBackgroundResource(R.drawable.bg_filter_tab_selected);
                break;
            case "follow-up":
                findViewById(R.id.btnFollowUp).setBackgroundResource(R.drawable.bg_filter_tab_selected);
                break;
            case "resolved":
                findViewById(R.id.btnResolved).setBackgroundResource(R.drawable.bg_filter_tab_selected);
                break;
        }
    }

    private void setupBottomNavigation() {
        // Home button (goes to Dashboard)
        Button btnHome = findViewById(R.id.btnHome);
        if (btnHome != null) {
            btnHome.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(EnquiriesActivity.this, AdminDashboardActivity.class);
                    startActivity(intent);
                    finish();
                }
            });
        }

        // Catalog button
        Button btnCatalog = findViewById(R.id.btnCatalog);
        if (btnCatalog != null) {
            btnCatalog.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(EnquiriesActivity.this, "Catalog screen coming soon", Toast.LENGTH_SHORT).show();
                }
            });
        }

        // Enquiries button (stay on same screen)
        Button btnEnquiries = findViewById(R.id.btnEnquiries);
        if (btnEnquiries != null) {
            btnEnquiries.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(EnquiriesActivity.this, "You are already on Enquiries", Toast.LENGTH_SHORT).show();
                }
            });
        }

        // Account button
        Button btnAccount = findViewById(R.id.btnAccount);
        if (btnAccount != null) {
            btnAccount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(EnquiriesActivity.this, AdminAccountActivity.class);
                    startActivity(intent);
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}