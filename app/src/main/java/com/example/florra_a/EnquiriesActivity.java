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
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class EnquiriesActivity extends AppCompatActivity {

    // Bottom Navigation Buttons
    private Button btnDashboard, btnCatalog, btnEnquiries, btnAccount;
    private ImageView btnBack, btnSearch, btnFilter;
    private TextView tvFilterAll, tvFilterNew, tvFilterQuoted, tvFilterFollowup, tvFilterResolved;

    // RecyclerView for enquiries list
    private RecyclerView recyclerViewEnquiries;
    private EnquiryAdapter enquiryAdapter;

    // Search bar
    private TextView etSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set fullscreen and edge-to-edge
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

        // Initialize all views
        initViews();

        // Setup click listeners
        setupClickListeners();

        // Setup RecyclerView
        setupRecyclerView();

        // Set All filter as active initially
        setActiveFilter(tvFilterAll);
    }

    private void initViews() {
        // Header buttons
        btnBack = findViewById(R.id.btnBack);
        btnSearch = findViewById(R.id.btnSearch);
        btnFilter = findViewById(R.id.btnFilter);

        // Search bar
        etSearch = findViewById(R.id.etSearch);

        // Filter buttons
        tvFilterAll = findViewById(R.id.tvFilterAll);
        tvFilterNew = findViewById(R.id.tvFilterNew);
        tvFilterQuoted = findViewById(R.id.tvFilterQuoted);
        tvFilterFollowup = findViewById(R.id.tvFilterFollowup);
        tvFilterResolved = findViewById(R.id.tvFilterResolved);

        // RecyclerView
        recyclerViewEnquiries = findViewById(R.id.recyclerViewEnquiries);

        // Bottom navigation
        btnDashboard = findViewById(R.id.btnDashboard);
        btnCatalog = findViewById(R.id.btnCatalog);
        btnEnquiries = findViewById(R.id.btnEnquiries);
        btnAccount = findViewById(R.id.btnAccount);

        // Set Enquiries as active initially
        setActiveTab(btnEnquiries);
    }

    private void setupClickListeners() {
        // Back button
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Go back to dashboard
            }
        });

        // Search button
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchText = etSearch.getText().toString();
                if (!searchText.isEmpty()) {
                    Toast.makeText(EnquiriesActivity.this, "Searching: " + searchText, Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Filter button
        btnFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(EnquiriesActivity.this, "Filter options", Toast.LENGTH_SHORT).show();
            }
        });

        // Filter buttons click listeners
        tvFilterAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setActiveFilter(tvFilterAll);
                enquiryAdapter.filterEnquiries("all");
            }
        });

        tvFilterNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setActiveFilter(tvFilterNew);
                enquiryAdapter.filterEnquiries("new");
            }
        });

        tvFilterQuoted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setActiveFilter(tvFilterQuoted);
                enquiryAdapter.filterEnquiries("quoted");
            }
        });

        tvFilterFollowup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setActiveFilter(tvFilterFollowup);
                enquiryAdapter.filterEnquiries("followup");
            }
        });

        tvFilterResolved.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setActiveFilter(tvFilterResolved);
                enquiryAdapter.filterEnquiries("resolved");
            }
        });

        // Bottom navigation click listeners
        btnDashboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setActiveTab(btnDashboard);
                finish(); // Go back to dashboard
            }
        });

        btnCatalog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setActiveTab(btnCatalog);
                Toast.makeText(EnquiriesActivity.this, "Catalog clicked", Toast.LENGTH_SHORT).show();
                // TODO: Navigate to catalog
            }
        });

        btnEnquiries.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setActiveTab(btnEnquiries);
                // Already on enquiries
            }
        });

        btnAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setActiveTab(btnAccount);
                Toast.makeText(EnquiriesActivity.this, "Account clicked", Toast.LENGTH_SHORT).show();
                // TODO: Navigate to account
            }
        });
    }

    private void setupRecyclerView() {
        // Create sample data matching your design exactly
        List<EnquiryItem> enquiryList = getSampleEnquiryData();

        // Setup adapter
        enquiryAdapter = new EnquiryAdapter(enquiryList);

        // Setup layout manager
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerViewEnquiries.setLayoutManager(layoutManager);
        recyclerViewEnquiries.setAdapter(enquiryAdapter);
    }

    private List<EnquiryItem> getSampleEnquiryData() {
        List<EnquiryItem> list = new ArrayList<>();

        // Add exact data from your design
        list.add(new EnquiryItem(
                "Michael Foster",
                "Hi, I'm looking for Italian Carrara marble tiles for my living room (approx 600 sqft). Do you have...",
                "2h ago",
                "New",
                "+1 (555) 123-4567",
                "Respond",
                R.drawable.tile_marble_sample
        ));

        list.add(new EnquiryItem(
                "Sarah Jenkins",
                "Quote #Q-2024-89 sent for 'Rustic Beige Ceramic'. Waiting for confirmation on delivery date.",
                "Yesterday",
                "Quoted",
                "$1,250.00",
                "View Quote",
                R.drawable.tile_ceramic_sample
        ));

        list.add(new EnquiryItem(
                "David Chen",
                "Scheduled site visit for measurements on Friday at 10 AM. Client needs bathroom renovation advice.",
                "2 days ago",
                "Site Visit",
                "Fri, Oct 24 • 10:00 AM",
                "Details",
                R.drawable.ic_measure
        ));

        list.add(new EnquiryItem(
                "Emma Wilson",
                "Order delivered and installed. Customer left 5-star feedback.",
                "1 week ago",
                "Resolved",
                "Order #9921",
                "Archive",
                R.drawable.ic_check_circle
        ));

        return list;
    }

    private void setActiveTab(Button activeButton) {
        // Reset all buttons
        btnDashboard.setTextColor(ContextCompat.getColor(this, R.color.zinc_400));
        btnCatalog.setTextColor(ContextCompat.getColor(this, R.color.zinc_400));
        btnEnquiries.setTextColor(ContextCompat.getColor(this, R.color.zinc_400));
        btnAccount.setTextColor(ContextCompat.getColor(this, R.color.zinc_400));

        // Set active button color
        activeButton.setTextColor(ContextCompat.getColor(this, R.color.primary_color));
    }

    private void setActiveFilter(TextView activeFilter) {
        // Reset all filters
        tvFilterAll.setBackgroundResource(R.drawable.bg_filter_inactive);
        tvFilterNew.setBackgroundResource(R.drawable.bg_filter_inactive);
        tvFilterQuoted.setBackgroundResource(R.drawable.bg_filter_inactive);
        tvFilterFollowup.setBackgroundResource(R.drawable.bg_filter_inactive);
        tvFilterResolved.setBackgroundResource(R.drawable.bg_filter_inactive);

        tvFilterAll.setTextColor(ContextCompat.getColor(this, R.color.zinc_600));
        tvFilterNew.setTextColor(ContextCompat.getColor(this, R.color.zinc_600));
        tvFilterQuoted.setTextColor(ContextCompat.getColor(this, R.color.zinc_600));
        tvFilterFollowup.setTextColor(ContextCompat.getColor(this, R.color.zinc_600));
        tvFilterResolved.setTextColor(ContextCompat.getColor(this, R.color.zinc_600));

        // Set active filter
        activeFilter.setBackgroundResource(R.drawable.bg_filter_active);
        activeFilter.setTextColor(ContextCompat.getColor(this, R.color.white));
    }

    @Override
    public void onBackPressed() {
        // Navigate back to dashboard
        finish();
    }

    // Enquiry Item class
    public static class EnquiryItem {
        private String customerName;
        private String enquiryText;
        private String timeAgo;
        private String status;
        private String detailInfo;
        private String actionText;
        private int imageResource;

        public EnquiryItem(String customerName, String enquiryText, String timeAgo,
                           String status, String detailInfo, String actionText, int imageResource) {
            this.customerName = customerName;
            this.enquiryText = enquiryText;
            this.timeAgo = timeAgo;
            this.status = status;
            this.detailInfo = detailInfo;
            this.actionText = actionText;
            this.imageResource = imageResource;
        }

        public String getCustomerName() { return customerName; }
        public String getEnquiryText() { return enquiryText; }
        public String getTimeAgo() { return timeAgo; }
        public String getStatus() { return status; }
        public String getDetailInfo() { return detailInfo; }
        public String getActionText() { return actionText; }
        public int getImageResource() { return imageResource; }
    }
}