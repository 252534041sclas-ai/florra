package com.example.florra_a;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

public class AdminDashboardActivity extends AppCompatActivity {

    // Bottom Navigation Buttons
    private Button btnDashboard, btnCatalog, btnEnquiries, btnAccount;
    private ImageView btnMenu;

    // Dashboard Cards
    private CardView cardTotalTiles, cardEnquiries, cardQuotations, cardLowStock;
    private CardView cardInventory, cardProduct, cardEnquiriesMgmt, cardQuotationsMgmt, cardSalesPrediction, cardStockAlerts;

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

        setContentView(R.layout.activity_admin_dashboard);

        // Initialize all views
        initViews();

        // Setup click listeners
        setupClickListeners();
    }

    private void initViews() {
        // Header menu button
        btnMenu = findViewById(R.id.btnMenu);

        // Dashboard cards
        cardTotalTiles = findViewById(R.id.cardTotalTiles);
        cardEnquiries = findViewById(R.id.cardEnquiries);
        cardQuotations = findViewById(R.id.cardQuotations);
        cardLowStock = findViewById(R.id.cardLowStock);

        // Management cards
        cardInventory = findViewById(R.id.cardInventory);
        cardProduct = findViewById(R.id.cardProduct);
        cardEnquiriesMgmt = findViewById(R.id.cardEnquiriesMgmt);
        cardQuotationsMgmt = findViewById(R.id.cardQuotationsMgmt);
        cardSalesPrediction = findViewById(R.id.cardSalesPrediction);
        cardStockAlerts = findViewById(R.id.cardStockAlerts);

        // Bottom navigation
        btnDashboard = findViewById(R.id.btnDashboard);
        btnCatalog = findViewById(R.id.btnCatalog);
        btnEnquiries = findViewById(R.id.btnEnquiries);
        btnAccount = findViewById(R.id.btnAccount);

        // Set Dashboard as active initially
        setActiveTab(btnDashboard);
    }

    private void setupClickListeners() {
        // Menu button
        btnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(AdminDashboardActivity.this, "Menu clicked", Toast.LENGTH_SHORT).show();
                // TODO: Open navigation drawer or menu
            }
        });

        // Dashboard cards click listeners
        cardTotalTiles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(AdminDashboardActivity.this, "View Total Tiles", Toast.LENGTH_SHORT).show();
                // TODO: Navigate to tiles inventory
            }
        });

        // ENQUIRIES DASHBOARD CARD (with number 12)
        cardEnquiries.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(AdminDashboardActivity.this, "View Enquiries", Toast.LENGTH_SHORT).show();
                // Navigate to Enquiries screen
                Intent intent = new Intent(AdminDashboardActivity.this, EnquiriesActivity.class);
                startActivity(intent);
            }
        });

        cardQuotations.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(AdminDashboardActivity.this, "View Quotations", Toast.LENGTH_SHORT).show();
                // TODO: Navigate to quotations
            }
        });

        cardLowStock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(AdminDashboardActivity.this, "View Low Stock Items", Toast.LENGTH_SHORT).show();
                // TODO: Navigate to low stock items
            }
        });

        // Management cards click listeners
        cardInventory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(AdminDashboardActivity.this, "Manage Inventory", Toast.LENGTH_SHORT).show();
                // TODO: Navigate to inventory management
            }
        });

        cardProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(AdminDashboardActivity.this, "Organize Products", Toast.LENGTH_SHORT).show();
                // TODO: Navigate to product management
            }
        });

        // ENQUIRIES MANAGEMENT CARD (Customer chats) - MOST IMPORTANT
        cardEnquiriesMgmt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(AdminDashboardActivity.this, "Manage Customer Enquiries", Toast.LENGTH_SHORT).show();
                // Navigate to Enquiries screen
                Intent intent = new Intent(AdminDashboardActivity.this, EnquiriesActivity.class);
                startActivity(intent);
            }
        });

        cardQuotationsMgmt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(AdminDashboardActivity.this, "Manage Quotations", Toast.LENGTH_SHORT).show();
                // TODO: Navigate to quotations management
            }
        });

        cardSalesPrediction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(AdminDashboardActivity.this, "View Sales Predictions", Toast.LENGTH_SHORT).show();
                // TODO: Navigate to analytics
            }
        });

        cardStockAlerts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(AdminDashboardActivity.this, "View Stock Alerts", Toast.LENGTH_SHORT).show();
                // TODO: Navigate to stock alerts
            }
        });

        // Bottom navigation click listeners
        btnDashboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setActiveTab(btnDashboard);
                // Already on dashboard, no navigation needed
            }
        });

        btnCatalog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setActiveTab(btnCatalog);
                Toast.makeText(AdminDashboardActivity.this, "Catalog clicked", Toast.LENGTH_SHORT).show();
                // TODO: Navigate to catalog
            }
        });

        // BOTTOM NAVIGATION ENQUIRIES BUTTON
        btnEnquiries.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setActiveTab(btnEnquiries);
                Toast.makeText(AdminDashboardActivity.this, "Enquiries clicked", Toast.LENGTH_SHORT).show();
                // Navigate to Enquiries screen
                Intent intent = new Intent(AdminDashboardActivity.this, EnquiriesActivity.class);
                startActivity(intent);
            }
        });

        // UPDATED: ACCOUNT BUTTON WITH NAVIGATION
        btnAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setActiveTab(btnAccount);
                Toast.makeText(AdminDashboardActivity.this, "Opening Account...", Toast.LENGTH_SHORT).show();
                // Navigate to Admin Account screen
                Intent intent = new Intent(AdminDashboardActivity.this, AdminAccountActivity.class);
                startActivity(intent);
            }
        });
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

    @Override
    public void onBackPressed() {
        // Ask for confirmation before exiting
        Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT).show();
        // You can implement double press to exit if needed
        super.onBackPressed();
    }
}