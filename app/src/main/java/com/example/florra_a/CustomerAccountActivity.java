package com.example.florra_a;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AlertDialog;

public class CustomerAccountActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set fullscreen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_customer_account);

        setupAllClickListeners();
    }

    private void setupAllClickListeners() {
        // Navigation buttons
        setupNavigationButtons();

        // Profile edit button
        setupProfileButton();

        // Settings buttons
        setupSettingsButtons();

        // Support buttons
        setupSupportButtons();

        // Logout button
        setupLogoutButton();
    }

    private void setupNavigationButtons() {
        // Home button
        LinearLayout btnNavHome = findViewById(R.id.btnNavHome);
        if (btnNavHome != null) {
            btnNavHome.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openHomeScreen();
                }
            });
        }

        // Catalog button
        LinearLayout btnNavCatalog = findViewById(R.id.btnNavCatalog);
        if (btnNavCatalog != null) {
            btnNavCatalog.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openCatalogScreen();
                }
            });
        }

        // Enquiries button
        LinearLayout btnNavEnquiries = findViewById(R.id.btnNavEnquiries);
        if (btnNavEnquiries != null) {
            btnNavEnquiries.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(CustomerAccountActivity.this, "Enquiries", Toast.LENGTH_SHORT).show();
                }
            });
        }

        // Account button (already on account)
        LinearLayout btnNavAccount = findViewById(R.id.btnNavAccount);
        if (btnNavAccount != null) {
            btnNavAccount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Already on account screen
                }
            });
        }
    }

    private void setupProfileButton() {
        View btnEditProfile = findViewById(R.id.btnEditProfile);
        if (btnEditProfile != null) {
            btnEditProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(CustomerAccountActivity.this, "Edit Profile Picture", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void setupSettingsButtons() {
        // Edit Profile Settings
        View btnEditProfileSettings = findViewById(R.id.btnEditProfileSettings);
        if (btnEditProfileSettings != null) {
            btnEditProfileSettings.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Intent intent = new Intent(CustomerAccountActivity.this, EditProfileActivity.class);
                        startActivity(intent);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    } catch (Exception e) {
                        Toast.makeText(CustomerAccountActivity.this, "Cannot open Edit Profile", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        // Change Password
        View btnChangePassword = findViewById(R.id.btnChangePassword);
        if (btnChangePassword != null) {
            btnChangePassword.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Intent intent = new Intent(CustomerAccountActivity.this, ChangePasswordActivity.class);
                        startActivity(intent);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    } catch (Exception e) {
                        Toast.makeText(CustomerAccountActivity.this, "Cannot open Change Password", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void setupSupportButtons() {
        // Help & Support
        View btnHelpSupport = findViewById(R.id.btnHelpSupport);
        if (btnHelpSupport != null) {
            btnHelpSupport.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Intent intent = new Intent(CustomerAccountActivity.this, HelpSupportActivity.class);
                        startActivity(intent);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    } catch (Exception e) {
                        Toast.makeText(CustomerAccountActivity.this, "Cannot open Help & Support", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        // About Florra
        View btnAboutFlorra = findViewById(R.id.btnAboutFlorra);
        if (btnAboutFlorra != null) {
            btnAboutFlorra.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(CustomerAccountActivity.this, "About Florra", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void setupLogoutButton() {
        View btnLogout = findViewById(R.id.btnLogout);
        if (btnLogout != null) {
            btnLogout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showLogoutConfirmationDialog();
                }
            });
        }
    }

    private void showLogoutConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Logout");
        builder.setMessage("Are you sure you want to logout?");

        builder.setPositiveButton("Yes", (dialog, which) -> {
            performLogout();
        });

        builder.setNegativeButton("No", (dialog, which) -> {
            dialog.dismiss();
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void performLogout() {
        // Clear SharedPreferences (login data)
        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        // Show logout message
        Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();

        // Navigate to LoginActivity
        Intent intent = new Intent(CustomerAccountActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

    private void openHomeScreen() {
        try {
            Intent intent = new Intent(CustomerAccountActivity.this, CustomerHomeActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        } catch (Exception e) {
            Toast.makeText(this, "Cannot open Home", Toast.LENGTH_SHORT).show();
        }
    }

    private void openCatalogScreen() {
        try {
            Intent intent = new Intent(CustomerAccountActivity.this, CatalogActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        } catch (Exception e) {
            Toast.makeText(this, "Cannot open Catalog", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        openHomeScreen();
    }
}