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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    // Views
    private Button btnCustomerLogin, btnAdminLogin, btnLogin, btnCreateAccount;
    private TextView txtWelcomeCustomer, txtWelcomeAdmin, txtDescCustomer, txtDescAdmin;
    private TextView btnForgotPassword, btnForgotPasswordAdmin, btnContactSupport;
    private ImageView btnToggleCustomerPassword, btnToggleAdminPassword;
    private EditText edtCustomerEmail, edtCustomerPassword, edtAdminEmail, edtAdminPassword;
    private LinearLayout customerLoginLayout, adminLoginLayout, orDivider, adminFooter, customerFooter;

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
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Enable edge-to-edge
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);

        // Handle notch and status bar
        WindowInsetsControllerCompat windowInsetsController =
                WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());
        windowInsetsController.setAppearanceLightStatusBars(false);
        windowInsetsController.setAppearanceLightNavigationBars(false);

        setContentView(R.layout.activity_login);

        // Initialize progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);

        // Check if user is already logged in
        checkLoginStatus();

        // Initialize all views
        initViews();

        // Set initial state to Admin login (as per your requirement)
        setAdminLoginMode();

        // Setup click listeners
        setupClickListeners();
    }

    private void checkLoginStatus() {
        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean("is_logged_in", false);
        String userType = sharedPreferences.getString("user_type", "");

        if (isLoggedIn) {
            if ("admin".equals(userType)) {
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

        // Layouts
        customerLoginLayout = findViewById(R.id.customerLoginLayout);
        adminLoginLayout = findViewById(R.id.adminLoginLayout);
        orDivider = findViewById(R.id.orDivider);
        adminFooter = findViewById(R.id.adminFooter);
        customerFooter = findViewById(R.id.customerFooter);

        // Login button
        btnLogin = findViewById(R.id.btnLogin);

        // Create account button
        btnCreateAccount = findViewById(R.id.btnCreateAccount);

        // Forgot password
        btnForgotPassword = findViewById(R.id.btnForgotPassword);
        btnForgotPasswordAdmin = findViewById(R.id.btnForgotPasswordAdmin);

        // Contact support
        btnContactSupport = findViewById(R.id.btnContactSupport);

        // Password toggle buttons
        btnToggleCustomerPassword = findViewById(R.id.btnToggleCustomerPassword);
        btnToggleAdminPassword = findViewById(R.id.btnToggleAdminPassword);

        // EditTexts
        edtCustomerEmail = findViewById(R.id.edtCustomerEmail);
        edtCustomerPassword = findViewById(R.id.edtCustomerPassword);
        edtAdminEmail = findViewById(R.id.edtAdminEmail);
        edtAdminPassword = findViewById(R.id.edtAdminPassword);
    }

    private void setupClickListeners() {
        // Toggle customer password visibility
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

        // Toggle admin password visibility
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

        // Customer login tab
        btnCustomerLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setCustomerLoginMode();
            }
        });

        // Admin login tab
        btnAdminLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setAdminLoginMode();
            }
        });

        // Login button
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

                    // Call API for admin login
                    performLogin(email, password, true);

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

                    // Call API for customer login
                    performLogin(email, password, false);
                }
            }
        });

        // Create account button
        btnCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to CreateAccountActivity
                Intent intent = new Intent(LoginActivity.this, CreateAccountActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

        // For Customer forgot password:
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

        // For Admin forgot password:
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

        // Contact IT Support
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

    private void performLogin(String email, String password, boolean isAdmin) {
        // Show loading dialog
        progressDialog.setMessage(isAdmin ? "Logging in as Admin..." : "Logging in...");
        progressDialog.show();

        // Create API service
        ApiService apiService = RetrofitClient.getApiService();

        // Create login request
        LoginRequest request = new LoginRequest(email, password, isAdmin ? "admin" : "customer");

        // Make API call
        Call<LoginResponse> call = apiService.loginUser(request);
        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                progressDialog.dismiss();

                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse loginResponse = response.body();

                    if ("success".equals(loginResponse.getStatus())) {
                        // Login successful
                        Toast.makeText(LoginActivity.this, loginResponse.getMessage(), Toast.LENGTH_SHORT).show();

                        // Save user data to SharedPreferences
                        saveUserData(loginResponse.getData(), email, isAdmin);

                        // Navigate to appropriate screen
                        navigateAfterLogin(isAdmin);

                    } else {
                        // Login failed
                        Toast.makeText(LoginActivity.this, loginResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Handle API error
                    Toast.makeText(LoginActivity.this, "Login failed. Please try again.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                progressDialog.dismiss();

                // Handle network error
                if (t.getMessage().contains("Failed to connect")) {
                    Toast.makeText(LoginActivity.this,
                            "Cannot connect to server. Please check your internet connection or server URL.",
                            Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(LoginActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }

                // For testing without backend, use mock login
                if (isAdmin && email.equals("admin@florra.com") && password.equals("admin123")) {
                    mockLoginForTesting(email, isAdmin);
                } else if (!isAdmin && !email.isEmpty() && !password.isEmpty()) {
                    mockLoginForTesting(email, isAdmin);
                }
            }
        });
    }

    private void saveUserData(LoginResponse.Data data, String email, boolean isAdmin) {
        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Save basic user info
        editor.putBoolean("is_logged_in", true);
        editor.putString("user_type", isAdmin ? "admin" : "customer");
        editor.putString("email", email);

        if (data != null) {
            // Save token if available
            if (data.getToken() != null) {
                editor.putString("token", data.getToken());
            }

            // Save user details if available
            if (data.getUser() != null) {
                editor.putString("full_name", data.getUser().getFull_name());
                editor.putInt("user_id", data.getUser().getId());
            }
        }

        editor.apply();
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

    // Method for testing without backend
    private void mockLoginForTesting(String email, boolean isAdmin) {
        Toast.makeText(this, "Mock login successful for testing", Toast.LENGTH_SHORT).show();

        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("is_logged_in", true);
        editor.putString("user_type", isAdmin ? "admin" : "customer");
        editor.putString("email", email);
        editor.putString("full_name", "Test User");
        editor.apply();

        navigateAfterLogin(isAdmin);
    }

    private void setCustomerLoginMode() {
        isAdminMode = false;

        // Update tab buttons
        btnCustomerLogin.setBackgroundResource(R.drawable.bg_active_tab);
        btnCustomerLogin.setTextColor(getResources().getColor(R.color.white));

        btnAdminLogin.setBackgroundResource(android.R.color.transparent);
        btnAdminLogin.setTextColor(getResources().getColor(R.color.slate_400));

        // Show customer UI elements
        txtWelcomeCustomer.setVisibility(View.VISIBLE);
        txtWelcomeAdmin.setVisibility(View.GONE);

        txtDescCustomer.setVisibility(View.VISIBLE);
        txtDescAdmin.setVisibility(View.GONE);

        customerLoginLayout.setVisibility(View.VISIBLE);
        adminLoginLayout.setVisibility(View.GONE);

        orDivider.setVisibility(View.VISIBLE);
        btnCreateAccount.setVisibility(View.VISIBLE);

        adminFooter.setVisibility(View.GONE);
        customerFooter.setVisibility(View.VISIBLE);

        // Update login button if needed
        btnLogin.setText("Log in");
    }

    private void setAdminLoginMode() {
        isAdminMode = true;

        // Update tab buttons
        btnAdminLogin.setBackgroundResource(R.drawable.bg_active_tab);
        btnAdminLogin.setTextColor(getResources().getColor(R.color.white));

        btnCustomerLogin.setBackgroundResource(android.R.color.transparent);
        btnCustomerLogin.setTextColor(getResources().getColor(R.color.slate_400));

        // Show admin UI elements
        txtWelcomeCustomer.setVisibility(View.GONE);
        txtWelcomeAdmin.setVisibility(View.VISIBLE);

        txtDescCustomer.setVisibility(View.GONE);
        txtDescAdmin.setVisibility(View.VISIBLE);

        customerLoginLayout.setVisibility(View.GONE);
        adminLoginLayout.setVisibility(View.VISIBLE);

        orDivider.setVisibility(View.GONE);
        btnCreateAccount.setVisibility(View.GONE);

        adminFooter.setVisibility(View.VISIBLE);
        customerFooter.setVisibility(View.GONE);

        // Update login button if needed
        btnLogin.setText("Log in");
    }

    @Override
    public void onBackPressed() {
        // Exit app when back pressed from login screen
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
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