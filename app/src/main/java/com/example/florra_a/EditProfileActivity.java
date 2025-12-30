package com.example.florra_a;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class EditProfileActivity extends AppCompatActivity {

    private EditText etFullName, etEmail, etMobile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_edit_profile);

        etFullName = findViewById(R.id.etFullName);
        etEmail = findViewById(R.id.etEmail);
        etMobile = findViewById(R.id.etMobile);

        setupClickListeners();
        setupNavigationButtons();
    }

    private void setupClickListeners() {
        findViewById(R.id.btnBack).setOnClickListener(v -> goBack());
        findViewById(R.id.btnCancel).setOnClickListener(v -> goBack());
        findViewById(R.id.btnChangePhoto).setOnClickListener(v -> Toast.makeText(this, "Change Photo", Toast.LENGTH_SHORT).show());
        findViewById(R.id.btnSaveChanges).setOnClickListener(v -> saveProfileChanges());
    }

    private void setupNavigationButtons() {
        LinearLayout btnNavHome = findViewById(R.id.btnNavHome);
        LinearLayout btnNavCatalog = findViewById(R.id.btnNavCatalog);
        LinearLayout btnNavEnquiries = findViewById(R.id.btnNavEnquiries);
        LinearLayout btnNavAccount = findViewById(R.id.btnNavAccount);

        if (btnNavHome != null) btnNavHome.setOnClickListener(v -> openHomeScreen());
        if (btnNavCatalog != null) btnNavCatalog.setOnClickListener(v -> openCatalogScreen());
        if (btnNavEnquiries != null) btnNavEnquiries.setOnClickListener(v -> Toast.makeText(this, "Enquiries", Toast.LENGTH_SHORT).show());
        if (btnNavAccount != null) btnNavAccount.setOnClickListener(v -> goBack());
    }

    private void saveProfileChanges() {
        String fullName = etFullName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();

        if (fullName.isEmpty()) {
            Toast.makeText(this, "Please enter full name", Toast.LENGTH_SHORT).show();
            return;
        }
        if (email.isEmpty()) {
            Toast.makeText(this, "Please enter email address", Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(this, "Profile saved successfully!", Toast.LENGTH_SHORT).show();
        goBack();
    }

    private void goBack() {
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
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

    @Override
    public void onBackPressed() {
        goBack();
    }
}