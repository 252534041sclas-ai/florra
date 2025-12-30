package com.example.florra_a;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class NotificationsActivity extends AppCompatActivity {

    private TextView tabAll, tabQuotations, tabSystem;
    private TextView btnMarkAllRead;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set fullscreen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_notifications);

        Log.d("FLORRA", "NotificationsActivity loaded");

        initializeViews();
        setupTabSelection();
        setupClickListeners();
    }

    private void initializeViews() {
        // Tabs
        tabAll = findViewById(R.id.tabAll);
        tabQuotations = findViewById(R.id.tabQuotations);
        tabSystem = findViewById(R.id.tabSystem);

        // Buttons
        btnMarkAllRead = findViewById(R.id.btnMarkAllRead);

        // Back button
        findViewById(R.id.btnBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void setupTabSelection() {
        // Set All tab as selected by default
        selectTab(tabAll);
        deselectTab(tabQuotations);
        deselectTab(tabSystem);

        tabAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectTab(tabAll);
                deselectTab(tabQuotations);
                deselectTab(tabSystem);
                Toast.makeText(NotificationsActivity.this, "Showing all notifications", Toast.LENGTH_SHORT).show();
            }
        });

        tabQuotations.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectTab(tabQuotations);
                deselectTab(tabAll);
                deselectTab(tabSystem);
                Toast.makeText(NotificationsActivity.this, "Showing quotations notifications", Toast.LENGTH_SHORT).show();
            }
        });

        tabSystem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectTab(tabSystem);
                deselectTab(tabAll);
                deselectTab(tabQuotations);
                Toast.makeText(NotificationsActivity.this, "Showing system notifications", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void selectTab(TextView tab) {
        tab.setBackgroundResource(R.drawable.bg_tab_selected);
        tab.setTextColor(getResources().getColor(android.R.color.white));
    }

    private void deselectTab(TextView tab) {
        tab.setBackground(null);
        tab.setTextColor(getResources().getColor(android.R.color.black));
    }

    private void setupClickListeners() {
        // Mark all read button
        btnMarkAllRead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(NotificationsActivity.this, "All notifications marked as read", Toast.LENGTH_SHORT).show();
                // Here you would update the UI to show all notifications as read
                // For example, hide all unread indicators
            }
        });

        // Notification cards click listeners
        setupNotificationClicks();
    }

    private void setupNotificationClicks() {
        // REMOVED the problematic line that was causing the error
        // View notificationContainer = findViewById(R.id.notification_container);

        // If you want to add click listeners to individual notification cards,
        // you need to give them IDs in your XML first.
        // For now, we'll just show a general click behavior

        // Example of how to set up if you add IDs to CardViews:
        /*
        CardView card1 = findViewById(R.id.notification_card_1);
        if (card1 != null) {
            card1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(NotificationsActivity.this, "Notification 1 clicked", Toast.LENGTH_SHORT).show();
                }
            });
        }
        */

        // For now, we can add a simple toast when the screen is loaded
        // or handle clicks differently
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}