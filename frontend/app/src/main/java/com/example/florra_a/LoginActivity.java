package com.example.florra_a;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.example.florra_a.models.AuthResponse;
import com.example.florra_a.models.LoginRequest;
import com.example.florra_a.network.RetrofitClient;
import com.example.florra_a.utils.SharedPrefManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    // Views
    private Button btnCustomerLogin, btnAdminLogin, btnLogin, btnCreateAccount;
    private TextView txtWelcomeCustomer, txtWelcomeAdmin, txtDescCustomer, txtDescAdmin;
    private TextView btnForgotPassword, btnForgotPasswordAdmin, btnContactSupport;
    private ImageView btnToggleCustomerPassword, btnToggleAdminPassword, logoIcon;
    private EditText edtCustomerEmail, edtCustomerPassword, edtAdminEmail, edtAdminPassword;
    private LinearLayout customerLoginLayout, adminLoginLayout, adminFooter;

    // State
    private boolean isCustomerPasswordVisible = false;
    private boolean isAdminPasswordVisible = false;
    private boolean isAdminMode = false; // Start with Admin mode as per your requirement

    // Progress dialog
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set fullscreen and edge-to-edge
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // Set status bar to white with dark icons
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            getWindow().setStatusBarColor(android.graphics.Color.WHITE);
            androidx.core.view.WindowInsetsControllerCompat controller = 
                androidx.core.view.WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());
            if (controller != null) {
                controller.setAppearanceLightStatusBars(true);
            }
        }

        // Ensure content doesn't go under status bar
        WindowCompat.setDecorFitsSystemWindows(getWindow(), true);

        // Initialize Retrofit Client - Now handled in FlorraApplication
        // RetrofitClient.init(this);

        setContentView(R.layout.activity_login);

        // Initialize all views FIRST
        initViews();

        // Initialize progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);

        // Check if user is already logged in
        checkLoginStatus();

        // Set initial state to Customer login
        setCustomerLoginMode();

        // Setup click listeners
        setupClickListeners();
    }

    private void checkLoginStatus() {
        SharedPrefManager prefManager = SharedPrefManager.getInstance(this);
        if (prefManager.isLoggedIn()) {
            if (prefManager.isAdmin()) {
                // Navigate to admin dashboard
                Intent intent = new Intent(LoginActivity.this, AdminDashboardActivity.class);
                startActivity(intent);
                finish();
            } else {
                // Navigate to customer home
                Intent intent = new Intent(LoginActivity.this, CustomerHomeActivity.class);
                startActivity(intent);
                finish();
            }
        }
    }

    private void initViews() {
        // Tab buttons
        btnCustomerLogin = findViewById(R.id.btnCustomerLogin);
        btnAdminLogin = findViewById(R.id.btnAdminLogin);

        // Titles and descriptions
        txtWelcomeCustomer = findViewById(R.id.txtWelcomeCustomer);
        txtWelcomeAdmin = findViewById(R.id.txtWelcomeAdmin);
        txtDescCustomer = findViewById(R.id.txtDescCustomer);
        txtDescAdmin = findViewById(R.id.txtDescAdmin);

        // Layouts - Check if these IDs exist in your XML
        customerLoginLayout = findViewById(R.id.customerLoginLayout);
        adminLoginLayout = findViewById(R.id.adminLoginLayout);

        // These might not exist in your XML
        // adminFooter = findViewById(R.id.adminFooter);


        // Login button
        btnLogin = findViewById(R.id.btnLogin);

        // Create account button
        btnCreateAccount = findViewById(R.id.btnCreateAccount);

        // Forgot password
        btnForgotPassword = findViewById(R.id.btnForgotPassword);
        btnForgotPasswordAdmin = findViewById(R.id.btnForgotPasswordAdmin);

        // Contact support
        //btnContactSupport = findViewById(R.id.btnContactSupport);

        // Password toggle buttons
        btnToggleCustomerPassword = findViewById(R.id.btnToggleCustomerPassword);
        btnToggleAdminPassword = findViewById(R.id.btnToggleAdminPassword);

        // EditTexts
        edtCustomerEmail = findViewById(R.id.edtCustomerEmail);
        edtCustomerPassword = findViewById(R.id.edtCustomerPassword);
        edtAdminEmail = findViewById(R.id.edtAdminEmail);
        edtAdminPassword = findViewById(R.id.edtAdminPassword);

        // Logo for secret gesture
        logoIcon = findViewById(R.id.logoIcon);

        // Load custom logo if exists
        if (logoIcon != null) {
            SharedPreferences shopPrefs = getSharedPreferences("ShowroomPrefs", MODE_PRIVATE);
            String customLogoPath = shopPrefs.getString("shop_logo_path", null);
            if (customLogoPath != null && !customLogoPath.isEmpty()) {
                java.io.File imgFile = new java.io.File(customLogoPath);
                if (imgFile.exists()) {
                    com.bumptech.glide.Glide.with(this).load(imgFile).circleCrop().into(logoIcon);
                }
            }
        }
    }

    private void setupClickListeners() {
        // Toggle customer password visibility
        if (btnToggleCustomerPassword != null) {
            btnToggleCustomerPassword.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    isCustomerPasswordVisible = !isCustomerPasswordVisible;

                    if (isCustomerPasswordVisible) {
                        edtCustomerPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                        btnToggleCustomerPassword.setImageResource(R.drawable.ic_visibility_off);
                    } else {
                        edtCustomerPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        btnToggleCustomerPassword.setImageResource(R.drawable.ic_visibility);
                    }
                    edtCustomerPassword.setSelection(edtCustomerPassword.getText().length());
                }
            });
        }

        // Toggle admin password visibility
        if (btnToggleAdminPassword != null) {
            btnToggleAdminPassword.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    isAdminPasswordVisible = !isAdminPasswordVisible;

                    if (isAdminPasswordVisible) {
                        edtAdminPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                        btnToggleAdminPassword.setImageResource(R.drawable.ic_visibility_off);
                    } else {
                        edtAdminPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        btnToggleAdminPassword.setImageResource(R.drawable.ic_visibility);
                    }
                    edtAdminPassword.setSelection(edtAdminPassword.getText().length());
                }
            });
        }

        // Customer login tab
        if (btnCustomerLogin != null) {
            btnCustomerLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setCustomerLoginMode();
                }
            });
        }

        // Admin login tab
        if (btnAdminLogin != null) {
            btnAdminLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setAdminLoginMode();
                }
            });
        }

        // Login button
        if (btnLogin != null) {
            btnLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isAdminMode) {
                        // Admin login
                        String email = edtAdminEmail.getText().toString().trim();
                        String password = edtAdminPassword.getText().toString().trim();

                        if (email.isEmpty()) {
                            Toast.makeText(LoginActivity.this, "Please enter admin email", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (password.isEmpty()) {
                            Toast.makeText(LoginActivity.this, "Please enter admin password", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        loginAdmin(email, password);

                    } else {
                        // Customer login
                        String email = edtCustomerEmail.getText().toString().trim();
                        String password = edtCustomerPassword.getText().toString().trim();

                        if (email.isEmpty()) {
                            Toast.makeText(LoginActivity.this, "Please enter email or username", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (password.isEmpty()) {
                            Toast.makeText(LoginActivity.this, "Please enter password", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        loginCustomer(email, password);
                    }
                }
            });
        }

        // Create account button
        if (btnCreateAccount != null) {
            btnCreateAccount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Navigate to CreateAccountActivity
                    Intent intent = new Intent(LoginActivity.this, CreateAccountActivity.class);
                    startActivity(intent);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                }
            });
        }

        // For Customer forgot password:
        if (btnForgotPassword != null) {
            btnForgotPassword.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
                    // Pass email if available
                    String email = edtCustomerEmail.getText().toString().trim();
                    if (!email.isEmpty()) {
                        intent.putExtra("email", email);
                    }
                    startActivity(intent);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                }
            });
        }

        // For Admin forgot password:
        if (btnForgotPasswordAdmin != null) {
            btnForgotPasswordAdmin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
                    // Pass email if available
                    String email = edtAdminEmail.getText().toString().trim();
                    if (!email.isEmpty()) {
                        intent.putExtra("email", email);
                    }
                    startActivity(intent);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                }
            });
        }

        // Contact IT Support
        if (btnContactSupport != null) {
            btnContactSupport.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Open email intent or contact support screen
                    Intent emailIntent = new Intent(Intent.ACTION_SEND);
                    emailIntent.setType("message/rfc822");
                    emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"support@florra.com"});
                    emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Support Request - Florra App");
                    emailIntent.putExtra(Intent.EXTRA_TEXT, "Dear Support Team,\n\n");

                    try {
                        startActivity(Intent.createChooser(emailIntent, "Send email..."));
                    } catch (android.content.ActivityNotFoundException ex) {
                        Toast.makeText(LoginActivity.this, "No email client installed.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }


        // Secret Gesture: Long Press + Swipe Down on Logo
        if (logoIcon != null) {
            logoIcon.setOnTouchListener(new View.OnTouchListener() {
                private long lastDownTime;
                private float startRawY;
                private boolean isLongPressed = false;
                private static final long LONG_PRESS_THRESHOLD = 700; // ms
                private static final float SWIPE_THRESHOLD = 180; // pixels

                @Override
                public boolean onTouch(View v, android.view.MotionEvent event) {
                    switch (event.getAction()) {
                        case android.view.MotionEvent.ACTION_DOWN:
                            lastDownTime = System.currentTimeMillis();
                            startRawY = event.getRawY();
                            isLongPressed = false;
                            return true;

                        case android.view.MotionEvent.ACTION_MOVE:
                            if (!isLongPressed && (System.currentTimeMillis() - lastDownTime) > LONG_PRESS_THRESHOLD) {
                                isLongPressed = true;
                                v.performHapticFeedback(android.view.HapticFeedbackConstants.LONG_PRESS);
                                // Prevent ScrollView from stealing the swipe
                                if (v.getParent() != null) {
                                    v.getParent().requestDisallowInterceptTouchEvent(true);
                                }
                            }
                            return true;

                        case android.view.MotionEvent.ACTION_UP:
                        case android.view.MotionEvent.ACTION_CANCEL:
                            float endRawY = event.getRawY();
                            if (isLongPressed && (endRawY - startRawY) > SWIPE_THRESHOLD) {
                                if (isAdminMode) {
                                    setCustomerLoginMode();
                                } else {
                                    setAdminLoginMode();
                                }
                            }
                            v.performClick();
                            // Reset parent interception
                            if (v.getParent() != null) {
                                v.getParent().requestDisallowInterceptTouchEvent(false);
                            }
                            return true;
                    }
                    return false;
                }
            });
        }
    }

    private void loginCustomer(String email, String password) {
        progressDialog.setMessage("Logging in...");
        progressDialog.show();

        LoginRequest request = new LoginRequest(email, password);
        RetrofitClient.getApiService().loginCustomer(request).enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                progressDialog.dismiss();
                if (response.isSuccessful() && response.body() != null) {
                    saveUserData(response.body().getEmail(), response.body().getFullName(), response.body().getToken(), false, response.body().getProfileImage());
                    navigateAfterLogin(false);
                } else {
                    String errorMessage = "Login Failed";
                    try {
                        if (response.errorBody() != null) {
                            errorMessage = response.errorBody().string();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(LoginActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loginAdmin(String email, String password) {
        progressDialog.setMessage("Logging in as Admin...");
        progressDialog.show();

        LoginRequest request = new LoginRequest(email, password);
        RetrofitClient.getApiService().loginAdmin(request).enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                progressDialog.dismiss();
                if (response.isSuccessful() && response.body() != null) {
                    saveUserData(response.body().getEmail(), response.body().getFullName(), response.body().getToken(), true, response.body().getRole(), response.body().getProfileImage(), response.body().isCanAccessBilling(), response.body().isCanAccessReports(), response.body().isCanAccessPredictions());
                    navigateAfterLogin(true);
                } else {
                    String errorMessage = "Admin Login Failed";
                    try {
                        if (response.errorBody() != null) {
                            errorMessage = response.errorBody().string();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(LoginActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveUserData(String email, String fullName, String token, boolean isAdmin, String role, String profileImage, boolean canAccessBilling, boolean canAccessReports, boolean canAccessPredictions) {
        SharedPrefManager.getInstance(this).saveUser(email, fullName, token, isAdmin, role, profileImage, canAccessBilling, canAccessReports, canAccessPredictions);
        Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show();
    }

    private void saveUserData(String email, String fullName, String token, boolean isAdmin, String role, String profileImage, boolean canAccessBilling, boolean canAccessReports) {
        saveUserData(email, fullName, token, isAdmin, role, profileImage, canAccessBilling, canAccessReports, false);
    }

    private void saveUserData(String email, String fullName, String token, boolean isAdmin, String profileImage) {
        saveUserData(email, fullName, token, isAdmin, isAdmin ? "admin" : "customer", profileImage, false, false, false);
    }

    private void navigateAfterLogin(boolean isAdmin) {
        if (isAdmin) {
            // Navigate to Admin Dashboard
            Intent intent = new Intent(LoginActivity.this, AdminDashboardActivity.class);
            startActivity(intent);
        } else {
            // Navigate to Customer Home Screen
            Intent intent = new Intent(LoginActivity.this, CustomerHomeActivity.class);
            startActivity(intent);
        }
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish(); // Close login activity
    }

    private void setCustomerLoginMode() {
        isAdminMode = false;

        // Update tab buttons
        if (btnCustomerLogin != null) {
            btnCustomerLogin.setBackgroundResource(R.drawable.bg_active_tab);
            btnCustomerLogin.setTextColor(getResources().getColor(R.color.white));
        }

        if (btnAdminLogin != null) {
            btnAdminLogin.setBackgroundResource(android.R.color.transparent);
            btnAdminLogin.setTextColor(getResources().getColor(R.color.slate_400));
        }

        // Show customer UI elements
        if (txtWelcomeCustomer != null) txtWelcomeCustomer.setVisibility(View.VISIBLE);
        if (txtWelcomeAdmin != null) txtWelcomeAdmin.setVisibility(View.GONE);

        if (txtDescCustomer != null) txtDescCustomer.setVisibility(View.VISIBLE);
        if (txtDescAdmin != null) txtDescAdmin.setVisibility(View.GONE);

        if (customerLoginLayout != null) customerLoginLayout.setVisibility(View.VISIBLE);
        if (adminLoginLayout != null) adminLoginLayout.setVisibility(View.GONE);

        // Safely handle optional views
        if (btnCreateAccount != null) btnCreateAccount.setVisibility(View.VISIBLE);
        if (adminFooter != null) adminFooter.setVisibility(View.GONE);

        // Update login button if needed
        if (btnLogin != null) btnLogin.setText("Log in");
    }

    private void setAdminLoginMode() {
        isAdminMode = true;

        // Update tab buttons
        if (btnAdminLogin != null) {
            btnAdminLogin.setBackgroundResource(R.drawable.bg_active_tab);
            btnAdminLogin.setTextColor(getResources().getColor(R.color.white));
        }

        if (btnCustomerLogin != null) {
            btnCustomerLogin.setBackgroundResource(android.R.color.transparent);
            btnCustomerLogin.setTextColor(getResources().getColor(R.color.slate_400));
        }

        // Show admin UI elements
        if (txtWelcomeCustomer != null) txtWelcomeCustomer.setVisibility(View.GONE);
        if (txtWelcomeAdmin != null) txtWelcomeAdmin.setVisibility(View.VISIBLE);

        if (txtDescCustomer != null) txtDescCustomer.setVisibility(View.GONE);
        if (txtDescAdmin != null) txtDescAdmin.setVisibility(View.VISIBLE);

        if (customerLoginLayout != null) customerLoginLayout.setVisibility(View.GONE);
        if (adminLoginLayout != null) adminLoginLayout.setVisibility(View.VISIBLE);

        // Safely handle optional views
        if (btnCreateAccount != null) btnCreateAccount.setVisibility(View.GONE);
        if (adminFooter != null) adminFooter.setVisibility(View.VISIBLE);

        // Update login button if needed
        if (btnLogin != null) btnLogin.setText("Log in");
    }

    @Override
    public void onBackPressed() {
        // Exit app when back pressed from login screen
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void showGoogleAccountChooser() {
        com.google.android.material.bottomsheet.BottomSheetDialog dialog = 
            new com.google.android.material.bottomsheet.BottomSheetDialog(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_google_accounts, null);
        dialog.setContentView(view);

        View layoutAccount1 = view.findViewById(R.id.layoutAccount1);
        View layoutAccount2 = view.findViewById(R.id.layoutAccount2);
        View layoutUseAnother = view.findViewById(R.id.layoutUseAnother);

        if (layoutAccount1 != null) {
            layoutAccount1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    loginCustomerWithGoogle("akash@gmail.com", "Akash");
                }
            });
        }

        if (layoutAccount2 != null) {
            layoutAccount2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    loginCustomerWithGoogle("user@florra.com", "Florra Guest");
                }
            });
        }

        if (layoutUseAnother != null) {
            layoutUseAnother.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    showCustomGoogleAccountDialog();
                }
            });
        }

        dialog.show();
    }

    private void showCustomGoogleAccountDialog() {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setTitle("Google Account");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        input.setHint("email@gmail.com");

        LinearLayout container = new LinearLayout(this);
        container.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        );
        // Margins in density independent pixels
        float scale = getResources().getDisplayMetrics().density;
        int verticalMargin = (int) (12 * scale + 0.5f);
        int horizontalMargin = (int) (24 * scale + 0.5f);
        params.setMargins(horizontalMargin, verticalMargin, horizontalMargin, verticalMargin);
        input.setLayoutParams(params);
        container.addView(input);
        builder.setView(container);

        builder.setPositiveButton("Sign in", new android.content.DialogInterface.OnClickListener() {
            @Override
            public void onClick(android.content.DialogInterface dialog, int which) {
                String email = input.getText().toString().trim();
                if (!email.isEmpty() && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    String name = email;
                    if (email.contains("@")) {
                        name = email.split("@")[0];
                    }
                    if (name.length() > 0) {
                        name = name.substring(0, 1).toUpperCase() + name.substring(1);
                    }
                    loginCustomerWithGoogle(email, name);
                } else {
                    Toast.makeText(LoginActivity.this, "Please enter a valid Google email", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void loginCustomerWithGoogle(String email, String fullName) {
        progressDialog.setMessage("Signing in with Google...");
        progressDialog.show();

        com.example.florra_a.models.GoogleLoginRequest request = new com.example.florra_a.models.GoogleLoginRequest(email, fullName);
        RetrofitClient.getApiService().loginGoogle(request).enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                progressDialog.dismiss();
                if (response.isSuccessful() && response.body() != null) {
                    saveUserData(response.body().getEmail(), response.body().getFullName(), response.body().getToken(), false, response.body().getProfileImage());
                    navigateAfterLogin(false);
                } else {
                    String errorMessage = "Google Sign-In Failed";
                    try {
                        if (response.errorBody() != null) {
                            errorMessage = response.errorBody().string();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(LoginActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        // Dismiss progress dialog to prevent memory leak
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        super.onDestroy();
    }
}