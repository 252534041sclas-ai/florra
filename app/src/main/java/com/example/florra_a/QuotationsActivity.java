package com.example.florra_a;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class QuotationsActivity extends AppCompatActivity {

    // Tab views
    private LinearLayout tabAll, tabPending, tabApproved, tabRejected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set fullscreen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_quotations);

        setupAllClickListeners();
    }

    private void setupAllClickListeners() {
        // Back button
        RelativeLayout btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        }

        // Tabs
        setupTabs();

        // View Details buttons
        setupViewDetailsButtons();

        // Bottom Navigation
        setupBottomNavigation();
    }

    private void setupTabs() {
        tabAll = findViewById(R.id.tabAll);
        tabPending = findViewById(R.id.tabPending);
        tabApproved = findViewById(R.id.tabApproved);
        tabRejected = findViewById(R.id.tabRejected);

        // Set All as active by default
        setActiveTab(tabAll);

        if (tabAll != null) {
            tabAll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setActiveTab(tabAll);
                    Toast.makeText(QuotationsActivity.this, "Showing all quotations", Toast.LENGTH_SHORT).show();
                    // Here you would filter the quotations list
                }
            });
        }

        if (tabPending != null) {
            tabPending.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setActiveTab(tabPending);
                    Toast.makeText(QuotationsActivity.this, "Showing pending quotations", Toast.LENGTH_SHORT).show();
                    // Here you would filter the quotations list
                }
            });
        }

        if (tabApproved != null) {
            tabApproved.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setActiveTab(tabApproved);
                    Toast.makeText(QuotationsActivity.this, "Showing approved quotations", Toast.LENGTH_SHORT).show();
                    // Here you would filter the quotations list
                }
            });
        }

        if (tabRejected != null) {
            tabRejected.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setActiveTab(tabRejected);
                    Toast.makeText(QuotationsActivity.this, "Showing rejected quotations", Toast.LENGTH_SHORT).show();
                    // Here you would filter the quotations list
                }
            });
        }
    }

    private void setActiveTab(LinearLayout activeTab) {
        // Reset all tabs to inactive state
        if (tabAll != null) {
            tabAll.setBackgroundResource(R.drawable.bg_tab_inactive);
            TextView tabAllText = null;
            for (int i = 0; i < tabAll.getChildCount(); i++) {
                View child = tabAll.getChildAt(i);
                if (child instanceof TextView) {
                    tabAllText = (TextView) child;
                    break;
                }
            }
            if (tabAllText != null) {
                tabAllText.setTextColor(getResources().getColor(R.color.slate_600));
            }
        }

        if (tabPending != null) {
            tabPending.setBackgroundResource(R.drawable.bg_tab_inactive);
            TextView tabPendingText = null;
            for (int i = 0; i < tabPending.getChildCount(); i++) {
                View child = tabPending.getChildAt(i);
                if (child instanceof TextView) {
                    tabPendingText = (TextView) child;
                    break;
                }
            }
            if (tabPendingText != null) {
                tabPendingText.setTextColor(getResources().getColor(R.color.slate_600));
            }
        }

        if (tabApproved != null) {
            tabApproved.setBackgroundResource(R.drawable.bg_tab_inactive);
            TextView tabApprovedText = null;
            for (int i = 0; i < tabApproved.getChildCount(); i++) {
                View child = tabApproved.getChildAt(i);
                if (child instanceof TextView) {
                    tabApprovedText = (TextView) child;
                    break;
                }
            }
            if (tabApprovedText != null) {
                tabApprovedText.setTextColor(getResources().getColor(R.color.slate_600));
            }
        }

        if (tabRejected != null) {
            tabRejected.setBackgroundResource(R.drawable.bg_tab_inactive);
            TextView tabRejectedText = null;
            for (int i = 0; i < tabRejected.getChildCount(); i++) {
                View child = tabRejected.getChildAt(i);
                if (child instanceof TextView) {
                    tabRejectedText = (TextView) child;
                    break;
                }
            }
            if (tabRejectedText != null) {
                tabRejectedText.setTextColor(getResources().getColor(R.color.slate_600));
            }
        }

        // Set active tab
        if (activeTab != null) {
            activeTab.setBackgroundResource(R.drawable.bg_tab_active);
            TextView activeTabText = null;
            for (int i = 0; i < activeTab.getChildCount(); i++) {
                View child = activeTab.getChildAt(i);
                if (child instanceof TextView) {
                    activeTabText = (TextView) child;
                    break;
                }
            }
            if (activeTabText != null) {
                activeTabText.setTextColor(getResources().getColor(R.color.white));
            }
        }
    }

    private void setupViewDetailsButtons() {
        LinearLayout btnViewDetails1 = findViewById(R.id.btnViewDetails1);
        if (btnViewDetails1 != null) {
            btnViewDetails1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openQuotationDetails("QT-8832");
                }
            });
        }

        LinearLayout btnViewDetails2 = findViewById(R.id.btnViewDetails2);
        if (btnViewDetails2 != null) {
            btnViewDetails2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openQuotationDetails("QT-8831");
                }
            });
        }

        LinearLayout btnViewDetails3 = findViewById(R.id.btnViewDetails3);
        if (btnViewDetails3 != null) {
            btnViewDetails3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openQuotationDetails("QT-8830");
                }
            });
        }
    }

    private void openQuotationDetails(String quotationId) {
        try {
            Intent intent = new Intent(QuotationsActivity.this, QuotationDetailsActivity.class);
            intent.putExtra("quotation_id", quotationId);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        } catch (Exception e) {
            Toast.makeText(this, "Cannot open quotation details", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void setupBottomNavigation() {
        // Home button
        LinearLayout btnNavHome = findViewById(R.id.btnNavHome);
        if (btnNavHome != null) {
            btnNavHome.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openHomeScreen();
                }
            });
        }

        // Catalog button
        LinearLayout btnNavCatalog = findViewById(R.id.btnNavCatalog);
        if (btnNavCatalog != null) {
            btnNavCatalog.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openCatalogScreen();
                }
            });
        }

        // Enquiries button - already active
        LinearLayout btnNavEnquiries = findViewById(R.id.btnNavEnquiries);
        if (btnNavEnquiries != null) {
            btnNavEnquiries.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Already on quotations screen
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

    private void openHomeScreen() {
        try {
            Intent intent = new Intent(QuotationsActivity.this, CustomerHomeActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        } catch (Exception e) {
            Toast.makeText(this, "Cannot open Home", Toast.LENGTH_SHORT).show();
        }
    }

    private void openCatalogScreen() {
        try {
            Intent intent = new Intent(QuotationsActivity.this, CatalogActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        } catch (Exception e) {
            Toast.makeText(this, "Cannot open Catalog", Toast.LENGTH_SHORT).show();
        }
    }

    private void openAccountScreen() {
        try {
            Intent intent = new Intent(QuotationsActivity.this, CustomerAccountActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        } catch (Exception e) {
            Toast.makeText(this, "Cannot open Account", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}