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
import com.example.florra_a.utils.SharedPrefManager;

public class AdminAccountActivity extends AppCompatActivity {

    // Bottom Navigation Views
    private View btnDashboard, btnInventory, btnQuotes, btnAccount;
    private Button btnBack, btnLogout;

    // Card Views
    private CardView cardEditShop, cardBusinessInfo, cardManageStaff;
    private CardView cardHelp, cardAbout;

    // Profile Image
    private ImageView ivProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set status bar to white with dark icons
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            getWindow().setStatusBarColor(android.graphics.Color.WHITE);
            WindowCompat.setDecorFitsSystemWindows(getWindow(), true);
            WindowInsetsControllerCompat controller = new WindowInsetsControllerCompat(getWindow(), getWindow().getDecorView());
            controller.setAppearanceLightStatusBars(true);
        }

        setContentView(R.layout.activity_admin_account);

        // Initialize all views
        initViews();

        // Setup click listeners
        setupClickListeners();

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

            // Bottom navigation
            btnDashboard = findViewById(R.id.bottomDashboard);
            btnInventory = findViewById(R.id.bottomInventory);
            btnQuotes = findViewById(R.id.bottomQuotes);
            btnAccount = findViewById(R.id.bottomAccount);

            // Shop Management Cards
            cardEditShop = findViewById(R.id.cardEditShop);
            cardBusinessInfo = findViewById(R.id.cardBusinessInfo);
            cardManageStaff = findViewById(R.id.cardManageStaff);

            // System & Support Cards
            cardHelp = findViewById(R.id.cardHelp);
            cardAbout = findViewById(R.id.cardAbout);

        } catch (Exception e) {
            Toast.makeText(this, "Error initializing views: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void loadProfileImage() {
        try {
            SharedPrefManager prefManager = SharedPrefManager.getInstance(this);
            String profileImageUrl = prefManager.getProfileImage();

            if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
                if (!profileImageUrl.startsWith("http")) {
                    String baseUrl = com.example.florra_a.network.RetrofitClient.BASE_URL;
                    if (baseUrl.endsWith("/") && profileImageUrl.startsWith("/")) {
                        profileImageUrl = baseUrl + profileImageUrl.substring(1);
                    } else if (!baseUrl.endsWith("/") && !profileImageUrl.startsWith("/")) {
                        profileImageUrl = baseUrl + "/" + profileImageUrl;
                    } else {
                        profileImageUrl = baseUrl + profileImageUrl;
                    }
                }

                // Load image with Picasso
                Picasso.get()
                        .load(profileImageUrl)
                        .placeholder(R.drawable.ic_profile_placeholder)
                        .error(R.drawable.ic_profile_placeholder)
                        .transform(new CircleTransform())
                        .into(ivProfile);
            }
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
            btnBack.setOnClickListener(v -> onBackPressed());
        }

        // Logout button
        if (btnLogout != null) {
            btnLogout.setOnClickListener(v -> performLogout());
        }

        // Shop Management Cards
        if (cardEditShop != null) {
            cardEditShop.setOnClickListener(v -> Toast.makeText(AdminAccountActivity.this, "Edit Shop Profile", Toast.LENGTH_SHORT).show());
        }

        if (cardBusinessInfo != null) {
            cardBusinessInfo.setOnClickListener(v -> Toast.makeText(AdminAccountActivity.this, "Business Information", Toast.LENGTH_SHORT).show());
        }

        if (cardManageStaff != null) {
            cardManageStaff.setOnClickListener(v -> Toast.makeText(AdminAccountActivity.this, "Manage Staff", Toast.LENGTH_SHORT).show());
        }

        // Dashboard Card
        View cardGoToDashboard = findViewById(R.id.cardGoToDashboard);
        if (cardGoToDashboard != null) {
            cardGoToDashboard.setOnClickListener(v -> {
                Intent intent = new Intent(AdminAccountActivity.this, AdminDashboardActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            });
        }

        // System & Support Cards
        if (cardHelp != null) {
            cardHelp.setOnClickListener(v -> Toast.makeText(AdminAccountActivity.this, "Help & Support", Toast.LENGTH_SHORT).show());
        }

        if (cardAbout != null) {
            cardAbout.setOnClickListener(v -> Toast.makeText(AdminAccountActivity.this, "About Florra", Toast.LENGTH_SHORT).show());
        }

        // Bottom Navigation
        if (btnDashboard != null) {
            btnDashboard.setOnClickListener(v -> {
                Intent intent = new Intent(AdminAccountActivity.this, AdminDashboardActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            });
        }

        if (btnInventory != null) {
            btnInventory.setOnClickListener(v -> {
                Intent intent = new Intent(AdminAccountActivity.this, AdminCatalogActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            });
        }

        if (btnQuotes != null) {
            btnQuotes.setOnClickListener(v -> {
                Intent intent = new Intent(AdminAccountActivity.this, EnquiriesActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            });
        }

        if (btnAccount != null) {
            btnAccount.setOnClickListener(v -> {
                // Already on Account
            });
        }

        // Profile Image Click
        if (ivProfile != null) {
            ivProfile.setOnClickListener(v -> Toast.makeText(AdminAccountActivity.this, "Edit Profile Picture", Toast.LENGTH_SHORT).show());
        }
    }

    private void performLogout() {
        // Clear user data using SharedPrefManager
        SharedPrefManager.getInstance(this).logout();

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
