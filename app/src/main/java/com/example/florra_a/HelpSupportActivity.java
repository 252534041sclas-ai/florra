package com.example.florra_a;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class HelpSupportActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set fullscreen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_help_support);

        setupBackButton();
        setupNavigationButtons();
        setupSupportButtons();
    }

    private void setupBackButton() {
        View btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        }
    }

    private void setupNavigationButtons() {
        setupNavButton(R.id.btnNavHome, this::openHomeScreen);
        setupNavButton(R.id.btnNavCatalog, this::openCatalogScreen);
        setupNavButton(R.id.btnNavEnquiries, () ->
                Toast.makeText(this, "Enquiries", Toast.LENGTH_SHORT).show());
        setupNavButton(R.id.btnNavAccount, this::openAccountScreen);
    }

    private void setupNavButton(int id, Runnable action) {
        LinearLayout button = findViewById(id);
        if (button != null) {
            button.setOnClickListener(v -> action.run());
        }
    }

    private void setupSupportButtons() {
        setupButton(R.id.btnFaqs, "FAQs");
        setupButton(R.id.btnVideoTutorials, "Video Tutorials");
        setupButton(R.id.btnUserManual, "User Manual");
        setupButton(R.id.btnCallService, "Call Customer Service");
        setupButton(R.id.btnEmailSupport, "Email Support");

        // Only add btnReportIssue if it exists in XML
        // If not, comment this line:
        // setupButton(R.id.btnReportIssue, "Report Technical Issue");
    }

    private void setupButton(int buttonId, String message) {
        View button = findViewById(buttonId);
        if (button != null) {
            button.setOnClickListener(v ->
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show());
        }
    }

    private void openHomeScreen() {
        try {
            Intent intent = new Intent(this, CustomerHomeActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        } catch (Exception e) {
            Toast.makeText(this, "Cannot open Home", Toast.LENGTH_SHORT).show();
        }
    }

    private void openCatalogScreen() {
        try {
            Intent intent = new Intent(this, CatalogActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        } catch (Exception e) {
            Toast.makeText(this, "Cannot open Catalog", Toast.LENGTH_SHORT).show();
        }
    }

    private void openAccountScreen() {
        try {
            Intent intent = new Intent(this, CustomerAccountActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        } catch (Exception e) {
            Toast.makeText(this, "Cannot open Account", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        openAccountScreen();
    }
}