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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.florra_a.adapters.QuotationsAdapter;
import com.example.florra_a.models.Enquiry;
import com.example.florra_a.network.ApiService;
import com.example.florra_a.network.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class QuotationsActivity extends AppCompatActivity {

    // Tab views
    private LinearLayout tabAll, tabPending, tabApproved, tabRejected;
    private RecyclerView recyclerView;
    private QuotationsAdapter adapter;
    private TextView tvNoQuotations;
    
    private List<Enquiry> allEnquiries = new ArrayList<>();
    private String currentFilter = "All";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set fullscreen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_quotations);

        initializeViews();
        setupClickListeners();
        fetchQuotations();
    }

    private void initializeViews() {
        tabAll = findViewById(R.id.tabAll);
        tabPending = findViewById(R.id.tabPending);
        tabApproved = findViewById(R.id.tabApproved);
        tabRejected = findViewById(R.id.tabRejected);
        
        recyclerView = findViewById(R.id.recyclerViewQuotations);
        tvNoQuotations = findViewById(R.id.tvNoQuotations);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new QuotationsAdapter(this, new ArrayList<>());
        recyclerView.setAdapter(adapter);

        // Set All as active by default
        setActiveTab(tabAll);
    }

    private void setupClickListeners() {
        // Back button
        RelativeLayout btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> onBackPressed());
        }

        tabAll.setOnClickListener(v -> {
            setActiveTab(tabAll);
            currentFilter = "All";
            filterList();
        });

        tabPending.setOnClickListener(v -> {
            setActiveTab(tabPending);
            currentFilter = "New"; // "New" or "Pending" based on backend
            filterList();
        });

        tabApproved.setOnClickListener(v -> {
            setActiveTab(tabApproved);
            currentFilter = "Approved";
            filterList();
        });

        tabRejected.setOnClickListener(v -> {
            setActiveTab(tabRejected);
            currentFilter = "Rejected";
            filterList();
        });

        setupBottomNavigation();
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchQuotations();
    }

    private void fetchQuotations() {
        ApiService apiService = RetrofitClient.getApiService();
        apiService.getEnquiries().enqueue(new Callback<List<Enquiry>>() {
            @Override
            public void onResponse(Call<List<Enquiry>> call, Response<List<Enquiry>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    allEnquiries = response.body();
                    filterList();
                } else {
                    Toast.makeText(QuotationsActivity.this, "Failed to load quotations", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Enquiry>> call, Throwable t) {
                Toast.makeText(QuotationsActivity.this, "Network Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void filterList() {
        List<Enquiry> filteredList = new ArrayList<>();
        if (currentFilter.equals("All")) {
            filteredList.addAll(allEnquiries);
        } else {
            for (Enquiry enquiry : allEnquiries) {
                if (enquiry.getStatus() == null) continue;
                
                String status = enquiry.getStatus().toLowerCase();
                
                if (currentFilter.equals("New")) {
                    // Pending tab showing "new" or "pending"
                    if (status.equals("new") || status.equals("pending")) {
                        filteredList.add(enquiry);
                    }
                } else if (currentFilter.equals("Approved")) {
                    // "Quotation" tab showing "quoted"
                    if (status.equals("quoted") || status.equals("approved")) {
                        filteredList.add(enquiry);
                    }
                } else if (currentFilter.equals("Rejected")) {
                    // "Rejected" tab showing "resolved" or "rejected"
                    if (status.equals("resolved") || status.equals("rejected")) {
                        filteredList.add(enquiry);
                    }
                }
            }
        }

        adapter.updateData(filteredList);

        if (filteredList.isEmpty()) {
            tvNoQuotations.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            tvNoQuotations.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    private void setActiveTab(LinearLayout activeTab) {
        // Reset all tabs
        resetTab(tabAll);
        resetTab(tabPending);
        resetTab(tabApproved);
        resetTab(tabRejected);

        // Set active
        if (activeTab != null) {
            activeTab.setBackgroundResource(R.drawable.bg_tab_active);
            TextView text = getTextViewFromLayout(activeTab);
            if (text != null) {
                text.setTextColor(getResources().getColor(R.color.white));
            }
        }
    }

    private void resetTab(LinearLayout tab) {
        if (tab != null) {
            tab.setBackgroundResource(R.drawable.bg_tab_inactive);
            TextView text = getTextViewFromLayout(tab);
            if (text != null) {
                text.setTextColor(getResources().getColor(R.color.slate_600));
            }
        }
    }
    
    private TextView getTextViewFromLayout(LinearLayout layout) {
        for (int i = 0; i < layout.getChildCount(); i++) {
            View child = layout.getChildAt(i);
            if (child instanceof TextView) {
                return (TextView) child;
            }
        }
        return null;
    }

    private void setupBottomNavigation() {
        // Home button
        LinearLayout btnNavHome = findViewById(R.id.btnNavHome);
        if (btnNavHome != null) {
            btnNavHome.setOnClickListener(v -> openHomeScreen());
        }

        // Catalog button
        LinearLayout btnNavCatalog = findViewById(R.id.btnNavCatalog);
        if (btnNavCatalog != null) {
            btnNavCatalog.setOnClickListener(v -> openCatalogScreen());
        }

        // Account button
        LinearLayout btnNavAccount = findViewById(R.id.btnNavAccount);
        if (btnNavAccount != null) {
            btnNavAccount.setOnClickListener(v -> openAccountScreen());
        }
    }

    private void openHomeScreen() {
        startActivity(new Intent(QuotationsActivity.this, CustomerHomeActivity.class));
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

    private void openCatalogScreen() {
        startActivity(new Intent(QuotationsActivity.this, CatalogActivity.class));
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

    private void openAccountScreen() {
        startActivity(new Intent(QuotationsActivity.this, CustomerAccountActivity.class));
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}