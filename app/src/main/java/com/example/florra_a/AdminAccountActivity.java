package com.example.florra_a;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

public class AdminAccountActivity extends AppCompatActivity {

    // Bottom Navigation Buttons
    private Button btnDashboard, btnInventory, btnQuotes, btnAccount;
    private Button btnBack, btnLogout;

    // Card Views
    private CardView cardEditShop, cardBusinessInfo, cardManageStaff;
    private CardView cardSecurity, cardAppSettings, cardHelp, cardAbout;

    // Profile Image
    private ImageView ivProfile;

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

        setContentView(R.layout.activity_admin_account);

        // Initialize all views
        initViews();

        // Setup click listeners
        setupClickListeners();

        // Set Account as active initially
        setActiveTab(btnAccount);

        // Load profile image
        loadProfileImage();
    }

    private void initViews() {
        try {
            // Header button
            btnBack = findViewById(R.id.btnBack);
            btnLogout = findViewById(R.id.btnLogout);

            // Profile Image
            ivProfile = findViewById(R.id.ivProfile);

            // Bottom navigation - CHECK THESE IDs CAREFULLY
            // Your XML might have different IDs
            btnDashboard = findViewById(R.id.bottomDashboard);
            btnInventory = findViewById(R.id.bottomInventory);
            btnQuotes = findViewById(R.id.bottomQuotes);
            btnAccount = findViewById(R.id.bottomAccount);

            // If above IDs return null, try these common alternatives
            if (btnDashboard == null) {
                btnDashboard = findViewById(R.id.btnDashboard);
            }
            if (btnInventory == null) {
                btnInventory = findViewById(R.id.btnCatalog);
            }
            if (btnQuotes == null) {
                btnQuotes = findViewById(R.id.btnEnquiries);
            }
            if (btnAccount == null) {
                btnAccount = findViewById(R.id.btnAccount);
            }

            // Shop Management Cards
            cardEditShop = findViewById(R.id.cardEditShop);
            cardBusinessInfo = findViewById(R.id.cardBusinessInfo);
            cardManageStaff = findViewById(R.id.cardManageStaff);

            // System & Support Cards
            cardSecurity = findViewById(R.id.cardSecurity);
            cardAppSettings = findViewById(R.id.cardAppSettings);
            cardHelp = findViewById(R.id.cardHelp);
            cardAbout = findViewById(R.id.cardAbout);

            // Log which views are null for debugging
            logNullViews();

        } catch (Exception e) {
            Toast.makeText(this, "Error initializing views: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void logNullViews() {
        StringBuilder log = new StringBuilder("Null Views: ");

        if (btnDashboard == null) log.append("btnDashboard, ");
        if (btnInventory == null) log.append("btnInventory, ");
        if (btnQuotes == null) log.append("btnQuotes, ");
        if (btnAccount == null) log.append("btnAccount, ");
        if (btnBack == null) log.append("btnBack, ");
        if (btnLogout == null) log.append("btnLogout, ");

        // Remove trailing comma and space
        String logMessage = log.toString();
        if (logMessage.endsWith(", ")) {
            logMessage = logMessage.substring(0, logMessage.length() - 2);
        }

        if (!logMessage.equals("Null Views: ")) {
            Toast.makeText(this, logMessage, Toast.LENGTH_LONG).show();
        }
    }

    private void loadProfileImage() {
        try {
            // The profile image URL from your HTML
            String profileImageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuB7A0vo_l2v1FXZFzyvYBbUew2LBVTSZla6MCGIiDJBpTzBtJaD651obQWhd789__6kiefzM_P94pjG8ui_YK9njt0jj9VzDTejy2WUxb3mtRuUqueUOWZILdw8KkVNwLAKNPHw8cfgsd_oYzlZJ66kc0UVAUHSw7Y2nUDX9kR_vHHRl0op43OZrA_Ldp0ENLOtkIEQ4biGuuMRukbUJYHLudw73Ez7UUbloAkXCB_wfPTunxgvkpjYbKARxrkhZ18Ksh8CI8msgyp9";

            // Load image with Picasso
            Picasso.get()
                    .load(profileImageUrl)
                    .placeholder(R.drawable.ic_profile_placeholder) // Make sure this drawable exists
                    .error(R.drawable.ic_profile_placeholder)
                    .transform(new CircleTransform())
                    .into(ivProfile);
        } catch (Exception e) {
            Toast.makeText(this, "Error loading profile image", Toast.LENGTH_SHORT).show();
        }
    }

    // Circle transformation class for Picasso
    public class CircleTransform implements Transformation {
        @Override
        public Bitmap transform(Bitmap source) {
            try {
                int size = Math.min(source.getWidth(), source.getHeight());

                int x = (source.getWidth() - size) / 2;
                int y = (source.getHeight() - size) / 2;

                Bitmap squaredBitmap = Bitmap.createBitmap(source, x, y, size, size);
                if (squaredBitmap != source) {
                    source.recycle();
                }

                Bitmap bitmap = Bitmap.createBitmap(size, size, source.getConfig());

                Canvas canvas = new Canvas(bitmap);
                Paint paint = new Paint();
                BitmapShader shader = new BitmapShader(squaredBitmap,
                        BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP);
                paint.setShader(shader);
                paint.setAntiAlias(true);

                float r = size / 2f;
                canvas.drawCircle(r, r, r, paint);

                squaredBitmap.recycle();
                return bitmap;
            } catch (Exception e) {
                return source;
            }
        }

        @Override
        public String key() {
            return "circle";
        }
    }

    private void setupClickListeners() {
        // Back button
        if (btnBack != null) {
            btnBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        }

        // Logout button
        if (btnLogout != null) {
            btnLogout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    performLogout();
                }
            });
        }

        // Shop Management Cards
        if (cardEditShop != null) {
            cardEditShop.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(AdminAccountActivity.this, "Edit Shop Profile", Toast.LENGTH_SHORT).show();
                }
            });
        }

        if (cardBusinessInfo != null) {
            cardBusinessInfo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(AdminAccountActivity.this, "Business Information", Toast.LENGTH_SHORT).show();
                }
            });
        }

        if (cardManageStaff != null) {
            cardManageStaff.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(AdminAccountActivity.this, "Manage Staff", Toast.LENGTH_SHORT).show();
                }
            });
        }

        // System & Support Cards
        if (cardSecurity != null) {
            cardSecurity.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(AdminAccountActivity.this, "Security & Access", Toast.LENGTH_SHORT).show();
                }
            });
        }

        if (cardAppSettings != null) {
            cardAppSettings.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(AdminAccountActivity.this, "App Settings", Toast.LENGTH_SHORT).show();
                }
            });
        }

        if (cardHelp != null) {
            cardHelp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(AdminAccountActivity.this, "Help & Support", Toast.LENGTH_SHORT).show();
                }
            });
        }

        if (cardAbout != null) {
            cardAbout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(AdminAccountActivity.this, "About Florra", Toast.LENGTH_SHORT).show();
                }
            });
        }

        // Bottom Navigation
        if (btnDashboard != null) {
            btnDashboard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setActiveTab(btnDashboard);
                    Intent intent = new Intent(AdminAccountActivity.this, AdminDashboardActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                    finish();
                }
            });
        }

        if (btnInventory != null) {
            btnInventory.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setActiveTab(btnInventory);
                    Toast.makeText(AdminAccountActivity.this, "Inventory clicked", Toast.LENGTH_SHORT).show();
                }
            });
        }

        if (btnQuotes != null) {
            btnQuotes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setActiveTab(btnQuotes);
                    Toast.makeText(AdminAccountActivity.this, "Quotes clicked", Toast.LENGTH_SHORT).show();
                }
            });
        }

        if (btnAccount != null) {
            btnAccount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setActiveTab(btnAccount);
                }
            });
        }

        // Profile Image Click
        if (ivProfile != null) {
            ivProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(AdminAccountActivity.this, "Edit Profile Picture", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void setActiveTab(Button activeButton) {
        // CRITICAL: Check if button is null
        if (activeButton == null) {
            Toast.makeText(this, "Cannot set active tab - button is null", Toast.LENGTH_SHORT).show();
            return;
        }

        // Reset all buttons - with null checks
        if (btnDashboard != null) {
            btnDashboard.setTextColor(ContextCompat.getColor(this, R.color.zinc_400));
        }
        if (btnInventory != null) {
            btnInventory.setTextColor(ContextCompat.getColor(this, R.color.zinc_400));
        }
        if (btnQuotes != null) {
            btnQuotes.setTextColor(ContextCompat.getColor(this, R.color.zinc_400));
        }
        if (btnAccount != null) {
            btnAccount.setTextColor(ContextCompat.getColor(this, R.color.zinc_400));
        }

        // Set active button color
        activeButton.setTextColor(ContextCompat.getColor(this, R.color.primary_color));

        // Note: You'll need to adjust for icons separately
    }

    private void performLogout() {
        // Clear user data
        getSharedPreferences("user_prefs", MODE_PRIVATE).edit().clear().apply();

        Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();

        // Go to login screen
        Intent intent = new Intent(AdminAccountActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        // Go back to dashboard
        try {
            Intent intent = new Intent(this, AdminDashboardActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        } catch (Exception e) {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}