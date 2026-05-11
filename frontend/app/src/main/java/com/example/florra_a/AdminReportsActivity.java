package com.example.florra_a;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class AdminReportsActivity extends AppCompatActivity {

    private ImageButton btnBack, btnHeaderSavedBills;
    private TextView tabSavedBills, tabDocuments, btnViewAllBills;
    private LinearLayout sectionSavedBills, sectionDocuments;
    private androidx.recyclerview.widget.RecyclerView rvSavedBills, rvDocuments;
    private android.widget.ProgressBar progressBar;
    private BillAdapter billAdapter;
    private java.util.List<com.example.florra_a.models.Bill> billList = new java.util.ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_reports);

        initViews();
        setupListeners();
        fetchBills();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        btnHeaderSavedBills = findViewById(R.id.btnHeaderSavedBills);
        tabSavedBills = findViewById(R.id.tabSavedBills);
        tabDocuments = findViewById(R.id.tabDocuments);
        btnViewAllBills = findViewById(R.id.btnViewAllBills);
        sectionSavedBills = findViewById(R.id.sectionSavedBills);
        sectionDocuments = findViewById(R.id.sectionDocuments);
        rvSavedBills = findViewById(R.id.rvSavedBills);
        rvDocuments = findViewById(R.id.rvDocuments);
        progressBar = findViewById(R.id.progressBar);

        // Setup RecyclerViews
        rvSavedBills.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(this));
        billAdapter = new BillAdapter(this, billList);
        rvSavedBills.setAdapter(billAdapter);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());

        View.OnClickListener openSavedBills = v -> {
            android.content.Intent intent = new android.content.Intent(this, SavedBillsActivity.class);
            startActivity(intent);
        };

        btnHeaderSavedBills.setOnClickListener(openSavedBills);
        btnViewAllBills.setOnClickListener(openSavedBills);

        tabSavedBills.setOnClickListener(v -> switchTab(true));
        tabDocuments.setOnClickListener(v -> switchTab(false));
    }

    private void fetchBills() {
        if (progressBar != null) progressBar.setVisibility(View.VISIBLE);
        
        com.example.florra_a.network.ApiService apiService = 
            com.example.florra_a.network.RetrofitClient.getApiService();
            
        apiService.getBills().enqueue(new retrofit2.Callback<java.util.List<com.example.florra_a.models.Bill>>() {
            @Override
            public void onResponse(retrofit2.Call<java.util.List<com.example.florra_a.models.Bill>> call, retrofit2.Response<java.util.List<com.example.florra_a.models.Bill>> response) {
                if (progressBar != null) progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    billList.clear();
                    // Show only first 5 recent bills for the report summary
                    java.util.List<com.example.florra_a.models.Bill> allBills = response.body();
                    for (int i = 0; i < Math.min(5, allBills.size()); i++) {
                        billList.add(allBills.get(i));
                    }
                    billAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(retrofit2.Call<java.util.List<com.example.florra_a.models.Bill>> call, Throwable t) {
                if (progressBar != null) progressBar.setVisibility(View.GONE);
                android.widget.Toast.makeText(AdminReportsActivity.this, "Failed to load bills", android.widget.Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void switchTab(boolean isSavedBills) {
        if (isSavedBills) {
            // Update Tab UI
            tabSavedBills.setBackgroundResource(R.drawable.bg_active_tab_rounded);
            tabSavedBills.setTextColor(androidx.core.content.ContextCompat.getColor(this, R.color.white));
            
            tabDocuments.setBackground(null);
            tabDocuments.setTextColor(androidx.core.content.ContextCompat.getColor(this, R.color.slate_400));
            
            // Show/Hide Sections
            sectionSavedBills.setVisibility(View.VISIBLE);
            sectionDocuments.setVisibility(View.GONE);
        } else {
            // Update Tab UI
            tabDocuments.setBackgroundResource(R.drawable.bg_active_tab_rounded);
            tabDocuments.setTextColor(androidx.core.content.ContextCompat.getColor(this, R.color.white));
            
            tabSavedBills.setBackground(null);
            tabSavedBills.setTextColor(androidx.core.content.ContextCompat.getColor(this, R.color.slate_400));
            
            // Show/Hide Sections
            sectionSavedBills.setVisibility(View.GONE);
            sectionDocuments.setVisibility(View.VISIBLE);
        }
    }
}
