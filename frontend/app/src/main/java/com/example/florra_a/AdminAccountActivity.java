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
import android.net.Uri;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import com.example.florra_a.models.AuthResponse;
import com.example.florra_a.network.ApiService;
import com.example.florra_a.network.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import android.app.ProgressDialog;

public class AdminAccountActivity extends AppCompatActivity {

    // Bottom Navigation Views
    private View btnDashboard, btnInventory, btnQuotes, btnAccount;
    private Button btnBack, btnLogout;

    // Card Views
    private CardView cardEditShop, cardBusinessInfo, cardManageStaff;
    private CardView cardHelp, cardAbout;

    // Profile Image
    private ImageView ivProfile;
    private Button btnEditProfileImage;
    private ActivityResultLauncher<Intent> pickImageLauncher;

    // Profile Details & Access Badge
    private android.widget.TextView tvName, tvEmail, tvAccess;
    private View layoutAccessBadge;
    private ImageView ivAccessIcon;

    // Shop Management Container Views (to hide for staff)
    private android.widget.TextView tvShopManagementTitle;
    private View layoutShopManagementCards;

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

        // Display dynamic name, email, and access badge
        displayUserDetails();

        // Setup click listeners
        setupClickListeners();

        // Load profile image
        loadProfileImage();

        // Initialize Image Picker
        initImagePicker();
    }

    private void initViews() {
        try {
            // Header button
            btnBack = findViewById(R.id.btnBack);
            btnLogout = findViewById(R.id.btnLogout);

            // Profile Image
            ivProfile = findViewById(R.id.ivProfile);
            btnEditProfileImage = findViewById(R.id.btnEditProfileImage);

            // Bottom navigation
            btnDashboard = findViewById(R.id.bottomDashboard);
            btnInventory = findViewById(R.id.bottomInventory);
            btnQuotes = findViewById(R.id.bottomQuotes);
            btnAccount = findViewById(R.id.bottomAccount);

            // Shop Management Cards
            cardEditShop = findViewById(R.id.cardEditShop);
            // cardBusinessInfo removed (no longer in layout)
            cardManageStaff = findViewById(R.id.cardManageStaff);

            // System & Support Cards
            cardHelp = findViewById(R.id.cardHelp);
            cardAbout = findViewById(R.id.cardAbout);

            // Profile Details & Access Badge Views
            tvName = findViewById(R.id.tvName);
            tvEmail = findViewById(R.id.tvEmail);
            tvAccess = findViewById(R.id.tvAccess);
            layoutAccessBadge = findViewById(R.id.layoutAccessBadge);
            ivAccessIcon = findViewById(R.id.ivAccessIcon);

            // Shop Management Container Views
            tvShopManagementTitle = findViewById(R.id.tvShopManagementTitle);
            layoutShopManagementCards = findViewById(R.id.layoutShopManagementCards);

        } catch (Exception e) {
            Toast.makeText(this, "Error initializing views: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void displayUserDetails() {
        try {
            SharedPrefManager prefManager = SharedPrefManager.getInstance(this);
            String name = prefManager.getFullName();
            String email = prefManager.getEmail();
            String role = prefManager.getRole();

            if (tvName != null && name != null) {
                tvName.setText(name);
            }
            if (tvEmail != null && email != null) {
                tvEmail.setText(email);
            }

            if (tvAccess != null && role != null) {
                if ("staff".equalsIgnoreCase(role)) {
                    tvAccess.setText("Staff Access");
                    if (ivAccessIcon != null) {
                        ivAccessIcon.setImageResource(R.drawable.ic_lock); // Show lock for staff
                        ivAccessIcon.setColorFilter(ContextCompat.getColor(this, R.color.slate_600));
                    }
                    if (layoutAccessBadge != null) {
                        layoutAccessBadge.setBackgroundResource(R.drawable.bg_tag);
                    }
                    // Hide Shop Management completely for staff
                    if (tvShopManagementTitle != null) {
                        tvShopManagementTitle.setVisibility(View.GONE);
                    }
                    if (layoutShopManagementCards != null) {
                        layoutShopManagementCards.setVisibility(View.GONE);
                    }
                } else {
                    tvAccess.setText("Admin Access");
                    if (ivAccessIcon != null) {
                        ivAccessIcon.setImageResource(R.drawable.ic_verified); // Show verified check for admin
                        ivAccessIcon.setColorFilter(ContextCompat.getColor(this, R.color.primary_color));
                    }
                    if (layoutAccessBadge != null) {
                        layoutAccessBadge.setBackgroundResource(R.drawable.bg_admin_badge);
                    }
                    // Show Shop Management for admin
                    if (tvShopManagementTitle != null) {
                        tvShopManagementTitle.setVisibility(View.VISIBLE);
                    }
                    if (layoutShopManagementCards != null) {
                        layoutShopManagementCards.setVisibility(View.VISIBLE);
                    }
                }
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error displaying user details: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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
            } else {
                // Generate letter avatar locally — no internet needed
                String fullName = prefManager.getFullName();
                if (fullName == null || fullName.trim().isEmpty()) {
                    fullName = "Admin";
                }
                // Get initials (up to 2 letters)
                String[] parts = fullName.trim().split("\\s+");
                String initials;
                if (parts.length >= 2) {
                    initials = String.valueOf(parts[0].charAt(0)).toUpperCase()
                             + String.valueOf(parts[1].charAt(0)).toUpperCase();
                } else {
                    initials = String.valueOf(parts[0].charAt(0)).toUpperCase();
                }
                // Draw letter avatar on canvas
                int size = 128;
                android.graphics.Bitmap bitmap = android.graphics.Bitmap.createBitmap(size, size, android.graphics.Bitmap.Config.ARGB_8888);
                android.graphics.Canvas avatarCanvas = new android.graphics.Canvas(bitmap);
                android.graphics.Paint bgPaint = new android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG);
                bgPaint.setColor(android.graphics.Color.parseColor("#1E293B"));
                avatarCanvas.drawCircle(size / 2f, size / 2f, size / 2f, bgPaint);
                android.graphics.Paint textPaint = new android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG);
                textPaint.setColor(android.graphics.Color.WHITE);
                textPaint.setTextSize(initials.length() > 1 ? 44f : 52f);
                textPaint.setTextAlign(android.graphics.Paint.Align.CENTER);
                textPaint.setTypeface(android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.BOLD));
                android.graphics.Rect bounds = new android.graphics.Rect();
                textPaint.getTextBounds(initials, 0, initials.length(), bounds);
                float textY = size / 2f - bounds.exactCenterY();
                avatarCanvas.drawText(initials, size / 2f, textY, textPaint);
                if (ivProfile != null) {
                    ivProfile.setImageBitmap(bitmap);
                }
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

        if (btnEditProfileImage != null) {
            btnEditProfileImage.setOnClickListener(v -> {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                pickImageLauncher.launch(intent);
            });
        }

        // Shop Management Cards
        if (cardEditShop != null) {
            cardEditShop.setOnClickListener(v -> {
                Intent intent = new Intent(AdminAccountActivity.this, EditShopProfileActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            });
        }



        if (cardManageStaff != null) {
            String role = SharedPrefManager.getInstance(this).getRole();
            if ("staff".equalsIgnoreCase(role)) {
                cardManageStaff.setVisibility(View.GONE);
            } else {
                cardManageStaff.setVisibility(View.VISIBLE);
                cardManageStaff.setOnClickListener(v -> {
                    Intent intent = new Intent(AdminAccountActivity.this, ManageStaffActivity.class);
                    startActivity(intent);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                });
            }
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
            cardHelp.setOnClickListener(v -> {
                try {
                    Intent intent = new Intent(AdminAccountActivity.this, HelpSupportActivity.class);
                    startActivity(intent);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                } catch (Exception e) {
                    Toast.makeText(AdminAccountActivity.this, "Cannot open Help & Support", Toast.LENGTH_SHORT).show();
                }
            });
        }

        if (cardAbout != null) {
            cardAbout.setOnClickListener(v -> {
                try {
                    Intent intent = new Intent(AdminAccountActivity.this, AboutFlorraActivity.class);
                    startActivity(intent);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                } catch (Exception e) {
                    Toast.makeText(AdminAccountActivity.this, "Cannot open About Florra", Toast.LENGTH_SHORT).show();
                }
            });
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
                // Navigate to Admin Account screen
                Intent intent = new Intent(AdminAccountActivity.this, AdminAccountActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
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

    private void initImagePicker() {
        pickImageLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri selectedImageUri = result.getData().getData();
                    if (selectedImageUri != null) {
                        uploadProfileImage(selectedImageUri);
                    }
                }
            }
        );
    }

    private void uploadProfileImage(Uri imageUri) {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Uploading image...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        try {
            InputStream inputStream = getContentResolver().openInputStream(imageUri);
            File tempFile = new File(getCacheDir(), "admin_profile_temp.jpg");
            FileOutputStream outputStream = new FileOutputStream(tempFile);
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            outputStream.close();
            inputStream.close();

            RequestBody nameBody = RequestBody.create(MediaType.parse("text/plain"), SharedPrefManager.getInstance(this).getFullName());
            RequestBody mobileBody = RequestBody.create(MediaType.parse("text/plain"), ""); // Mobile empty or get from prefs if available

            RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), tempFile);
            MultipartBody.Part body = MultipartBody.Part.createFormData("profile_image", tempFile.getName(), requestFile);

            ApiService apiService = RetrofitClient.getApiService();
            apiService.updateProfile(nameBody, mobileBody, body).enqueue(new Callback<AuthResponse>() {
                @Override
                public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                    progressDialog.dismiss();
                    if (response.isSuccessful() && response.body() != null) {
                        String newImageUrl = response.body().getProfileImage();
                        SharedPrefManager.getInstance(AdminAccountActivity.this).saveProfileImage(newImageUrl);
                        loadProfileImage(); // Refresh UI
                        Toast.makeText(AdminAccountActivity.this, "Profile updated", Toast.LENGTH_SHORT).show();
                    } else {
                        String errorMsg = "Upload failed: " + response.code();
                        try {
                            if (response.errorBody() != null) {
                                errorMsg += " - " + response.errorBody().string();
                            }
                        } catch (Exception e) {}
                        Toast.makeText(AdminAccountActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<AuthResponse> call, Throwable t) {
                    progressDialog.dismiss();
                    Toast.makeText(AdminAccountActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            progressDialog.dismiss();
            Toast.makeText(this, "File error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        // Go back to dashboard
        try {
            Intent intent = new Intent(this, AdminDashboardActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        } catch (Exception e) {
            super.onBackPressed();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
