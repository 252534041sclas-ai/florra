package com.example.florra_a;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.florra_a.adapters.NotificationsAdapter;
import com.example.florra_a.models.Notification;
import com.example.florra_a.network.ApiService;
import com.example.florra_a.network.RetrofitClient;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationsActivity extends AppCompatActivity implements NotificationsAdapter.OnNotificationClickListener {

    private RecyclerView rvNotifications;
    private NotificationsAdapter adapter;
    private List<Notification> allNotifications = new ArrayList<>();
    private TextView emptyStateView;
    private ProgressBar progressBar;
    
    // Tabs
    private TextView tabAll, tabQuotations, tabSystem;
    private String currentFilter = "ALL"; // ALL, QUOTATION, SYSTEM

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set fullscreen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_notifications);

        initializeViews();
        setupTabs();
        setupClickListeners();
        fetchNotifications();
    }

    private void initializeViews() {
        rvNotifications = findViewById(R.id.rvNotifications);
        rvNotifications.setLayoutManager(new LinearLayoutManager(this));
        
        adapter = new NotificationsAdapter(this, new ArrayList<>(), this);
        rvNotifications.setAdapter(adapter);

        emptyStateView = findViewById(R.id.emptyStateView);
        progressBar = findViewById(R.id.progressBar);

        tabAll = findViewById(R.id.tabAll);
        tabQuotations = findViewById(R.id.tabQuotations);
        tabSystem = findViewById(R.id.tabSystem);
    }

    private void setupClickListeners() {
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        
        findViewById(R.id.btnMarkAllRead).setOnClickListener(v -> {
            markAllAsRead();
        });
    }

    private void setupTabs() {
        tabAll.setOnClickListener(v -> updateFilter("ALL"));
        tabQuotations.setOnClickListener(v -> updateFilter("QUOTATION"));
        tabSystem.setOnClickListener(v -> updateFilter("SYSTEM"));
    }

    private void updateFilter(String filter) {
        this.currentFilter = filter;
        updateTabStyles();
        filterList();
    }

    private void updateTabStyles() {
        // Reset all
        tabAll.setBackgroundResource(0);
        tabAll.setTextColor(Color.parseColor("#64748b"));
        
        tabQuotations.setBackgroundResource(0);
        tabQuotations.setTextColor(Color.parseColor("#64748b"));
        
        tabSystem.setBackgroundResource(0);
        tabSystem.setTextColor(Color.parseColor("#64748b"));

        // Set active
        TextView activeTab = tabAll;
        if ("QUOTATION".equals(currentFilter)) activeTab = tabQuotations;
        else if ("SYSTEM".equals(currentFilter)) activeTab = tabSystem;

        activeTab.setBackgroundResource(R.drawable.bg_tab_selected);
        activeTab.setTextColor(Color.WHITE);
    }

    private void fetchNotifications() {
        progressBar.setVisibility(View.VISIBLE);
        rvNotifications.setVisibility(View.GONE);
        emptyStateView.setVisibility(View.GONE);

        ApiService apiService = RetrofitClient.getApiService();
        Call<List<Notification>> call = apiService.getNotifications();

        call.enqueue(new Callback<List<Notification>>() {
            @Override
            public void onResponse(Call<List<Notification>> call, Response<List<Notification>> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    allNotifications = response.body();
                    filterList();
                } else {
                    showError("Failed to load notifications");
                }
            }

            @Override
            public void onFailure(Call<List<Notification>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                showError("Network error: " + t.getMessage());
                Log.e("NotificationsActivity", "Error", t);
            }
        });
    }

    private void filterList() {
        List<Notification> filteredList;
        
        if ("ALL".equals(currentFilter)) {
            filteredList = new ArrayList<>(allNotifications);
        } else {
            // Case insensitive filter by type
            filteredList = new ArrayList<>();
            for (Notification n : allNotifications) {
                if (n.getType() != null && n.getType().equalsIgnoreCase(currentFilter)) {
                    filteredList.add(n);
                }
            }
        }

        adapter.updateList(filteredList);
        
        if (filteredList.isEmpty()) {
            rvNotifications.setVisibility(View.GONE);
            emptyStateView.setVisibility(View.VISIBLE);
        } else {
            rvNotifications.setVisibility(View.VISIBLE);
            emptyStateView.setVisibility(View.GONE);
        }
    }

    private void markAllAsRead() {
        // Mock implementation for UI update
        for (Notification n : allNotifications) {
            n.setRead(true);
        }
        adapter.notifyDataSetChanged();
        Toast.makeText(this, "All notifications marked as read", Toast.LENGTH_SHORT).show();
        
        // TODO: Call API endpoint to update status on server
    }

    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        emptyStateView.setText(message);
        emptyStateView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onNotificationClick(Notification notification) {
        // Handle click, e.g., open details
        Toast.makeText(this, "Clicked: " + notification.getTitle(), Toast.LENGTH_SHORT).show();
        
        if (!notification.isRead()) {
            notification.setRead(true);
            adapter.notifyDataSetChanged();
            // TODO: API call to mark single read
        }
    }
}