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

    private android.widget.ProgressBar progressBar;
    private androidx.recyclerview.widget.RecyclerView recyclerView;
    private com.example.florra_a.adapters.EnquiryAdapter adapter;
    private java.util.List<com.example.florra_a.models.Enquiry> allEnquiries = new java.util.ArrayList<>();
    private android.widget.EditText etSearch;
    private String currentFilter = "all";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set status bar to white with dark icons
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            getWindow().setStatusBarColor(android.graphics.Color.WHITE);
            WindowCompat.setDecorFitsSystemWindows(getWindow(), true);
            WindowInsetsControllerCompat controller = new WindowInsetsControllerCompat(getWindow(), getWindow().getDecorView());
            controller.setAppearanceLightStatusBars(true);
        }

        setContentView(R.layout.activity_enquiries);

        progressBar = findViewById(R.id.progressBar);
        etSearch = findViewById(R.id.etSearch);
        
        initRecyclerView();
        setupBottomNavigation();
        fetchEnquiries();
        setupFilterButtons();
        setupSearch();
    }

    private void setupSearch() {
        if (etSearch != null) {
            etSearch.addTextChangedListener(new android.text.TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    applyFilters();
                }

                @Override
                public void afterTextChanged(android.text.Editable s) {}
            });
        }
    }

    private void fetchEnquiries() {
        if (progressBar != null) progressBar.setVisibility(View.VISIBLE);
        
        com.example.florra_a.network.ApiService apiService = 
            com.example.florra_a.network.RetrofitClient.getApiService();
            
        apiService.getEnquiries().enqueue(new retrofit2.Callback<java.util.List<com.example.florra_a.models.Enquiry>>() {
            @Override
            public void onResponse(retrofit2.Call<java.util.List<com.example.florra_a.models.Enquiry>> call,
                                   retrofit2.Response<java.util.List<com.example.florra_a.models.Enquiry>> response) {
                if (progressBar != null) progressBar.setVisibility(View.GONE);
                
                if (response.isSuccessful() && response.body() != null) {
                    allEnquiries = response.body();
                    updateBadgeCounts();
                    updateFilterSelection("all"); 
                } else {
                    Toast.makeText(EnquiriesActivity.this, "Failed to load enquiries", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(retrofit2.Call<java.util.List<com.example.florra_a.models.Enquiry>> call, Throwable t) {
                if (progressBar != null) progressBar.setVisibility(View.GONE);
                Toast.makeText(EnquiriesActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initRecyclerView() {
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(this));
        adapter = new com.example.florra_a.adapters.EnquiryAdapter(new java.util.ArrayList<>(), new com.example.florra_a.adapters.EnquiryAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(com.example.florra_a.models.Enquiry enquiry) {
                try {
                    Intent intent = new Intent(EnquiriesActivity.this, RespondEnquiryActivity.class);
                    intent.putExtra("enquiry", enquiry);
                    startActivity(intent);
                } catch (Exception e) {
                    Toast.makeText(EnquiriesActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }
        });
        recyclerView.setAdapter(adapter);
    }

    private void setupNavigation() {
        setupBackButton();
        setupBottomNavigation();
    }

    private void setupBackButton() {
        ImageView btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> onBackPressed());
        }
    }

    private void setupFilterButtons() {
        View btnAll = findViewById(R.id.btnAll);
        if (btnAll != null) btnAll.setOnClickListener(v -> updateFilterSelection("all"));

        View btnNew = findViewById(R.id.btnNew);
        if (btnNew != null) btnNew.setOnClickListener(v -> updateFilterSelection("new"));

        View btnQuoted = findViewById(R.id.btnQuoted);
        if (btnQuoted != null) btnQuoted.setOnClickListener(v -> updateFilterSelection("quoted"));

        View btnRejected = findViewById(R.id.btnRejected);
        if (btnRejected != null) btnRejected.setOnClickListener(v -> updateFilterSelection("rejected"));

        View btnResolved = findViewById(R.id.btnResolved);
        if (btnResolved != null) btnResolved.setOnClickListener(v -> updateFilterSelection("resolved"));
    }

    private void updateBadgeCounts() {
        int newCount = 0;
        for (com.example.florra_a.models.Enquiry e : allEnquiries) {
            if ("new".equalsIgnoreCase(e.getStatus())) newCount++;
        }
        
        TextView badgeNew = findViewById(R.id.badgeNew);
        if (badgeNew != null) {
            if (newCount > 0) {
                badgeNew.setText(String.valueOf(newCount));
                badgeNew.setVisibility(View.VISIBLE);
            } else {
                badgeNew.setVisibility(View.GONE);
            }
        }
    }

    private void updateFilterSelection(String selectedFilter) {
        this.currentFilter = selectedFilter;
        
        // Reset all tabs UI
        int[] tabIds = {R.id.btnAll, R.id.btnNew, R.id.btnQuoted, R.id.btnRejected, R.id.btnResolved};
        for (int id : tabIds) {
            View tab = findViewById(id);
            if (tab != null) {
                tab.setBackgroundResource(R.drawable.bg_filter_tab_unselected);
                if (tab instanceof android.view.ViewGroup) {
                    android.view.ViewGroup vg = (android.view.ViewGroup) tab;
                    for (int i = 0; i < vg.getChildCount(); i++) {
                        View child = vg.getChildAt(i);
                        if (child instanceof TextView && child.getId() != R.id.badgeNew) {
                            ((TextView) child).setTextColor(android.graphics.Color.parseColor("#64748B"));
                        }
                    }
                }
            }
        }

        // Set selected tab UI
        View selectedTab = null;
        switch (selectedFilter) {
            case "all": selectedTab = findViewById(R.id.btnAll); break;
            case "new": selectedTab = findViewById(R.id.btnNew); break;
            case "quoted": selectedTab = findViewById(R.id.btnQuoted); break;
            case "rejected": selectedTab = findViewById(R.id.btnRejected); break;
            case "resolved": selectedTab = findViewById(R.id.btnResolved); break;
        }

        if (selectedTab != null) {
            selectedTab.setBackgroundResource(R.drawable.bg_filter_tab_selected);
            if (selectedTab instanceof android.view.ViewGroup) {
                android.view.ViewGroup vg = (android.view.ViewGroup) selectedTab;
                for (int i = 0; i < vg.getChildCount(); i++) {
                    View child = vg.getChildAt(i);
                    if (child instanceof TextView && child.getId() != R.id.badgeNew) {
                        ((TextView) child).setTextColor(android.graphics.Color.WHITE);
                    }
                }
            }
        }
        
        applyFilters();
    }

    private void applyFilters() {
        String query = etSearch != null ? etSearch.getText().toString().toLowerCase().trim() : "";
        java.util.List<com.example.florra_a.models.Enquiry> filteredList = new java.util.ArrayList<>();
        
        for (com.example.florra_a.models.Enquiry e : allEnquiries) {
            // 1. Filter by status (Tabs)
            boolean matchesStatus = false;
            if (currentFilter.equals("all")) {
                matchesStatus = true;
            } else {
                String s = e.getStatus().toLowerCase().replace("-", "_").replace(" ", "_");
                String f = currentFilter.toLowerCase().replace("-", "_").replace(" ", "_");
                if (s.equals(f)) matchesStatus = true;
            }
            
            // 2. Filter by Search Query
            boolean matchesSearch = true;
            if (!query.isEmpty()) {
                String name = e.getCustomerName() != null ? e.getCustomerName().toLowerCase() : "";
                String message = e.getMessage() != null ? e.getMessage().toLowerCase() : "";
                String id = String.valueOf(e.getId());
                matchesSearch = name.contains(query) || message.contains(query) || id.contains(query);
            }
            
            if (matchesStatus && matchesSearch) {
                filteredList.add(e);
            }
        }
        adapter.updateData(filteredList);
    }

    private void setupBottomNavigation() {
        // Dashboard button
        View btnDashboard = findViewById(R.id.bottomDashboard);
        if (btnDashboard != null) {
            btnDashboard.setOnClickListener(v -> {
                Intent intent = new Intent(EnquiriesActivity.this, AdminDashboardActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            });
        }

        // Catalog button
        View btnCatalog = findViewById(R.id.bottomInventory);
        if (btnCatalog != null) {
            btnCatalog.setOnClickListener(v -> {
                Intent intent = new Intent(EnquiriesActivity.this, AdminCatalogActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            });
        }

        // Enquiries button - Already here
        View btnEnquiries = findViewById(R.id.bottomQuotes);
        if (btnEnquiries != null) {
            btnEnquiries.setOnClickListener(v -> {
                // Already on enquiries
            });
        }

        // Account button
        View btnAccount = findViewById(R.id.bottomAccount);
        if (btnAccount != null) {
            btnAccount.setOnClickListener(v -> {
                Intent intent = new Intent(EnquiriesActivity.this, AdminAccountActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            });
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}