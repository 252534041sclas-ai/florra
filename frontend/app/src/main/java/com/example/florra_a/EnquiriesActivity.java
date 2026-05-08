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
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsControllerCompat;

public class EnquiriesActivity extends AppCompatActivity {

    private androidx.recyclerview.widget.RecyclerView recyclerView;
    private com.example.florra_a.adapters.EnquiryAdapter adapter;
    private java.util.List<com.example.florra_a.models.Enquiry> allEnquiries = new java.util.ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set fullscreen
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_enquiries);

        initRecyclerView();
        setupNavigation(); // Re-use existing navigation
        fetchEnquiries();
        setupFilterButtons();
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchEnquiries();
    }

    private void initRecyclerView() {
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(this));
        adapter = new com.example.florra_a.adapters.EnquiryAdapter(new java.util.ArrayList<>(), new com.example.florra_a.adapters.EnquiryAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(com.example.florra_a.models.Enquiry enquiry) {
                // Debug Toast
                Toast.makeText(EnquiriesActivity.this, "Opening response for ID: " + enquiry.getId(), Toast.LENGTH_SHORT).show();
                
                try {
                    Intent intent = new Intent(EnquiriesActivity.this, RespondEnquiryActivity.class);
                    intent.putExtra("enquiry_data", enquiry);
                    startActivity(intent);
                } catch (Exception e) {
                    Toast.makeText(EnquiriesActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }
        });
        recyclerView.setAdapter(adapter);
    }
    
    private void fetchEnquiries() {
        com.example.florra_a.network.ApiService apiService = 
            com.example.florra_a.network.RetrofitClient.getApiService();
            
        apiService.getEnquiries().enqueue(new retrofit2.Callback<java.util.List<com.example.florra_a.models.Enquiry>>() {
            @Override
            public void onResponse(retrofit2.Call<java.util.List<com.example.florra_a.models.Enquiry>> call,
                                   retrofit2.Response<java.util.List<com.example.florra_a.models.Enquiry>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    android.util.Log.d("EnquiriesActivity", "Enquiries received: " + response.body().size());
                    allEnquiries = response.body();
                    updateFilterSelection("all"); // Show all by default
                    if (allEnquiries.isEmpty()) {
                         Toast.makeText(EnquiriesActivity.this, "No enquiries found in DB", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    android.util.Log.e("EnquiriesActivity", "Failed to load enquiries. Code: " + response.code() + ", Message: " + response.message());
                    Toast.makeText(EnquiriesActivity.this, "Failed to load enquiries. Code: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(retrofit2.Call<java.util.List<com.example.florra_a.models.Enquiry>> call, Throwable t) {
                android.util.Log.e("EnquiriesActivity", "Network Error: " + t.getMessage());
                t.printStackTrace();
                Toast.makeText(EnquiriesActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
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
        // All filter
        View btnAll = findViewById(R.id.btnAll);
        if (btnAll != null) btnAll.setOnClickListener(v -> updateFilterSelection("all"));

        // New filter
        View btnNew = findViewById(R.id.btnNew);
        if (btnNew != null) btnNew.setOnClickListener(v -> updateFilterSelection("new"));

        // Quoted filter
        View btnQuoted = findViewById(R.id.btnQuoted);
        if (btnQuoted != null) btnQuoted.setOnClickListener(v -> updateFilterSelection("quoted"));

        // Follow-up filter
        View btnFollowUp = findViewById(R.id.btnFollowUp);
        if (btnFollowUp != null) btnFollowUp.setOnClickListener(v -> updateFilterSelection("follow_up")); // underscore in backend? or hyphen? Check model.

        // Resolved filter
        View btnResolved = findViewById(R.id.btnResolved);
        if (btnResolved != null) btnResolved.setOnClickListener(v -> updateFilterSelection("resolved"));
    }

    private void updateFilterSelection(String selectedFilter) {
        // Reset all tabs UI
        int[] tabIds = {R.id.btnAll, R.id.btnNew, R.id.btnQuoted, R.id.btnFollowUp, R.id.btnResolved};
        for (int id : tabIds) {
            View tab = findViewById(id);
            if (tab != null) tab.setBackgroundResource(R.drawable.bg_filter_tab_unselected);
        }

        // Set selected tab UI
        switch (selectedFilter) {
            case "all": findViewById(R.id.btnAll).setBackgroundResource(R.drawable.bg_filter_tab_selected); break;
            case "new": findViewById(R.id.btnNew).setBackgroundResource(R.drawable.bg_filter_tab_selected); break;
            case "quoted": findViewById(R.id.btnQuoted).setBackgroundResource(R.drawable.bg_filter_tab_selected); break;
            case "follow_up": 
            case "follow-up": findViewById(R.id.btnFollowUp).setBackgroundResource(R.drawable.bg_filter_tab_selected); break;
            case "resolved": findViewById(R.id.btnResolved).setBackgroundResource(R.drawable.bg_filter_tab_selected); break;
        }
        
        // Filter Data
        java.util.List<com.example.florra_a.models.Enquiry> filteredList = new java.util.ArrayList<>();
        if (selectedFilter.equals("all")) {
            filteredList.addAll(allEnquiries);
        } else {
            for (com.example.florra_a.models.Enquiry e : allEnquiries) {
                // Normalize status check
                String s = e.getStatus().toLowerCase().replace("-", "_").replace(" ", "_");
                String f = selectedFilter.toLowerCase().replace("-", "_").replace(" ", "_");
                if (s.equals(f)) {
                    filteredList.add(e);
                }
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
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
            });
        }

        // Catalog button
        View btnCatalog = findViewById(R.id.bottomInventory);
        if (btnCatalog != null) {
            btnCatalog.setOnClickListener(v -> {
                Intent intent = new Intent(EnquiriesActivity.this, AdminCatalogActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
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
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
            });
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
