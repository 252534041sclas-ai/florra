package com.example.florra_a;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateAccountActivity extends AppCompatActivity {

    // Views
    private ImageButton btnBack, btnTogglePassword, btnToggleConfirmPassword;
    private Button btnHelp, btnCreateAccount;
    private TextView btnTerms, btnPrivacy, btnLogin;
    private EditText edtFullName, edtEmail, edtMobile, edtPassword, edtConfirmPassword;
    private CheckBox chkTerms;

    // State
    private boolean isPasswordVisible = false;
    private boolean isConfirmPasswordVisible = false;

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

        setContentView(R.layout.activity_create_account);

        // Initialize progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Creating account...");

        // Initialize all views
        initViews();

        // Setup click listeners
        setupClickListeners();
    }

    private void initViews() {
        // Buttons
        btnBack = findViewById(R.id.btnBack);
        btnHelp = findViewById(R.id.btnHelp);
        btnCreateAccount = findViewById(R.id.btnCreateAccount);
        btnTogglePassword = findViewById(R.id.btnTogglePassword);
        btnToggleConfirmPassword = findViewById(R.id.btnToggleConfirmPassword);

        // Text views
        btnTerms = findViewById(R.id.btnTerms);
        btnPrivacy = findViewById(R.id.btnPrivacy);
        btnLogin = findViewById(R.id.btnLogin);

        // EditTexts
        edtFullName = findViewById(R.id.edtFullName);
        edtEmail = findViewById(R.id.edtEmail);
        edtMobile = findViewById(R.id.edtMobile);
        edtPassword = findViewById(R.id.edtPassword);
        edtConfirmPassword = findViewById(R.id.edtConfirmPassword);

        // Checkbox
        chkTerms = findViewById(R.id.chkTerms);
    }

    private void setupClickListeners() {
        // Back button
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // Help button
        btnHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(CreateAccountActivity.this, "Help clicked", Toast.LENGTH_SHORT).show();
            }
        });

        // Toggle password visibility
        btnTogglePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isPasswordVisible = !isPasswordVisible;

                if (isPasswordVisible) {
                    edtPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    btnTogglePassword.setImageResource(R.drawable.ic_visibility);
                } else {
                    edtPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    btnTogglePassword.setImageResource(R.drawable.ic_visibility_off);
                }
                edtPassword.setSelection(edtPassword.getText().length());
            }
        });

        // Toggle confirm password visibility
        btnToggleConfirmPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isConfirmPasswordVisible = !isConfirmPasswordVisible;

                if (isConfirmPasswordVisible) {
                    edtConfirmPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    btnToggleConfirmPassword.setImageResource(R.drawable.ic_visibility);
                } else {
                    edtConfirmPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    btnToggleConfirmPassword.setImageResource(R.drawable.ic_visibility_off);
                }
                edtConfirmPassword.setSelection(edtConfirmPassword.getText().length());
            }
        });

        // Terms of Service
        btnTerms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(CreateAccountActivity.this, "Terms of Service clicked", Toast.LENGTH_SHORT).show();
            }
        });

        // Privacy Policy
        btnPrivacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(CreateAccountActivity.this, "Privacy Policy clicked", Toast.LENGTH_SHORT).show();
            }
        });

        // Login text
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate back to LoginActivity
                Intent intent = new Intent(CreateAccountActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

        // Create Account button
        btnCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Validate inputs
                if (!validateInputs()) {
                    return;
                }

                // Get user data
                String fullName = edtFullName.getText().toString().trim();
                String email = edtEmail.getText().toString().trim();
                String mobile = edtMobile.getText().toString().trim();
                String password = edtPassword.getText().toString().trim();

                // Call API to register
                performRegistration(fullName, email, mobile, password);
            }
        });
    }

    private void performRegistration(String fullName, String email, String mobile, String password) {
        // Show loading dialog
        progressDialog.show();

        // Log the request details
        Log.d("FLORRA_REGISTRATION", "=== STARTING REAL REGISTRATION ===");
        Log.d("FLORRA_REGISTRATION", "Full Name: " + fullName);
        Log.d("FLORRA_REGISTRATION", "Email: " + email);
        Log.d("FLORRA_REGISTRATION", "Mobile: " + mobile);
        Log.d("FLORRA_REGISTRATION", "API URL: " + ApiService.BASE_URL + "api/auth/register.php");

        // Create API service
        ApiService apiService = RetrofitClient.getApiService();

        // Create registration request
        RegisterRequest request = new RegisterRequest(
                fullName,
                email,
                mobile,
                password,
                password // confirm password (same as password)
        );

        // Make API call
        Call<RegisterResponse> call = apiService.registerUser(request);
        call.enqueue(new Callback<RegisterResponse>() {
            @Override
            public void onResponse(Call<RegisterResponse> call, Response<RegisterResponse> response) {
                progressDialog.dismiss();

                Log.d("FLORRA_REGISTRATION", "Response Code: " + response.code());
                Log.d("FLORRA_REGISTRATION", "Response Successful: " + response.isSuccessful());

                if (response.isSuccessful() && response.body() != null) {
                    RegisterResponse registerResponse = response.body();

                    Log.d("FLORRA_REGISTRATION", "Response Status: " + registerResponse.getStatus());
                    Log.d("FLORRA_REGISTRATION", "Response Message: " + registerResponse.getMessage());

                    if ("success".equals(registerResponse.getStatus())) {
                        // ✅ REGISTRATION SUCCESSFUL
                        Toast.makeText(CreateAccountActivity.this,
                                "✅ Account created successfully!", Toast.LENGTH_SHORT).show();

                        // Save user data to SharedPreferences
                        saveUserData(registerResponse.getData(), email);

                        // Navigate to Customer Home
                        navigateToCustomerHome();

                    } else {
                        // ❌ Registration failed - API returned error
                        String errorMessage = registerResponse.getMessage();
                        Log.e("FLORRA_REGISTRATION", "Registration failed: " + errorMessage);
                        Toast.makeText(CreateAccountActivity.this,
                                "❌ " + errorMessage, Toast.LENGTH_LONG).show();
                    }
                } else {
                    // ❌ HTTP error (404, 500, etc.)
                    String errorMessage = "Server Error: HTTP " + response.code();

                    // Try to read error body
                    try {
                        if (response.errorBody() != null) {
                            String errorBody = response.errorBody().string();
                            Log.e("FLORRA_REGISTRATION", "Error Body: " + errorBody);

                            if (errorBody.contains("Cannot connect to server")) {
                                errorMessage = "❌ Cannot connect to PHP backend. Check:\n1. XAMPP is running\n2. URL is correct";
                            } else if (errorBody.contains("Method not allowed")) {
                                errorMessage = "❌ API Error: Wrong request method";
                            } else if (response.code() == 404) {
                                errorMessage = "❌ API Not Found (404)\nCheck URL: " + ApiService.BASE_URL + "api/auth/register.php";
                            } else if (response.code() == 500) {
                                errorMessage = "❌ Server Error (500)\nCheck PHP error logs";
                            }
                        }
                    } catch (Exception e) {
                        Log.e("FLORRA_REGISTRATION", "Error reading error body: " + e.getMessage());
                    }

                    Log.e("FLORRA_REGISTRATION", errorMessage);
                    Toast.makeText(CreateAccountActivity.this,
                            errorMessage, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<RegisterResponse> call, Throwable t) {
                progressDialog.dismiss();

                Log.e("FLORRA_REGISTRATION", "❌ NETWORK FAILURE: " + t.getMessage());
                t.printStackTrace();

                // ❌ NETWORK ERROR - Cannot connect at all
                String errorMsg = "";

                if (t.getMessage().contains("Failed to connect")) {
                    errorMsg = "❌ Cannot connect to server.\n\n";
                    errorMsg += "Current URL: " + ApiService.BASE_URL + "\n\n";
                    errorMsg += "Please check:\n";
                    errorMsg += "✅ 1. XAMPP is running (Apache & MySQL - both GREEN)\n";
                    errorMsg += "✅ 2. Test in browser: http://localhost/florra_api/\n";
                    errorMsg += "✅ 3. If using emulator: Use 10.0.2.2\n";
                    errorMsg += "✅ 4. If using phone: Use your PC IP address\n";
                    errorMsg += "✅ 5. PC and phone must be on same WiFi\n";
                    errorMsg += "✅ 6. Disable Windows Firewall temporarily";
                } else if (t.getMessage().contains("timeout")) {
                    errorMsg = "❌ Connection timeout. Server is not responding.";
                } else {
                    errorMsg = "❌ Network Error: " + t.getMessage();
                }

                Log.e("FLORRA_REGISTRATION", errorMsg);

                // Show error as dialog for better visibility
                showConnectionErrorDialog(errorMsg);
            }
        });
    }

    private void showConnectionErrorDialog(String errorMessage) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Connection Failed")
                .setMessage(errorMessage)
                .setPositiveButton("OK", (dialog, which) -> {
                    dialog.dismiss();
                })
                .setNegativeButton("Test in Browser", (dialog, which) -> {
                    // Show browser test instructions
                    String testUrl = ApiService.BASE_URL.replace("10.0.2.2", "localhost") + "api/auth/register.php";
                    String instructions = "Open browser and test:\n\n" + testUrl + "\n\nShould show: {\"status\":\"error\",\"message\":\"Method not allowed\"}";

                    new android.app.AlertDialog.Builder(CreateAccountActivity.this)
                            .setTitle("Test Instructions")
                            .setMessage(instructions)
                            .setPositiveButton("OK", null)
                            .show();
                })
                .show();
    }

    private void saveUserData(RegisterResponse.Data data, String email) {
        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Save basic user info
        editor.putBoolean("is_logged_in", true);
        editor.putString("user_type", "customer");
        editor.putString("email", email);

        if (data != null) {
            // Save token if available
            if (data.getToken() != null) {
                editor.putString("token", data.getToken());
                Log.d("FLORRA_REGISTRATION", "Token saved: " + data.getToken());
            }

            // Save user details if available
            if (data.getUser() != null) {
                editor.putString("full_name", data.getUser().getFull_name());
                editor.putInt("user_id", data.getUser().getId());
                editor.putString("mobile", data.getUser().getMobile() != null ?
                        data.getUser().getMobile() : "");

                Log.d("FLORRA_REGISTRATION", "User data saved to SharedPreferences:");
                Log.d("FLORRA_REGISTRATION", "ID: " + data.getUser().getId());
                Log.d("FLORRA_REGISTRATION", "Name: " + data.getUser().getFull_name());
                Log.d("FLORRA_REGISTRATION", "Email: " + data.getUser().getEmail());
            }
        }

        editor.apply();
        Log.d("FLORRA_REGISTRATION", "✅ User data saved to SharedPreferences");
    }

    private void navigateToCustomerHome() {
        Log.d("FLORRA_REGISTRATION", "✅ Navigating to CustomerHomeActivity");
        Intent intent = new Intent(CreateAccountActivity.this, CustomerHomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

    private boolean validateInputs() {
        String fullName = edtFullName.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();
        String mobile = edtMobile.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();
        String confirmPassword = edtConfirmPassword.getText().toString().trim();

        // Check full name
        if (fullName.isEmpty()) {
            edtFullName.setError("Please enter your full name");
            edtFullName.requestFocus();
            return false;
        }

        // Check email
        if (email.isEmpty()) {
            edtEmail.setError("Please enter your email address");
            edtEmail.requestFocus();
            return false;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            edtEmail.setError("Please enter a valid email address");
            edtEmail.requestFocus();
            return false;
        }

        // Check mobile (optional but validate format if entered)
        if (!mobile.isEmpty()) {
            // Simple validation for phone number (adjust as needed)
            if (mobile.length() < 10) {
                edtMobile.setError("Please enter a valid mobile number (10 digits)");
                edtMobile.requestFocus();
                return false;
            }
        }

        // Check password
        if (password.isEmpty()) {
            edtPassword.setError("Please create a password");
            edtPassword.requestFocus();
            return false;
        }

        if (password.length() < 6) {
            edtPassword.setError("Password must be at least 6 characters");
            edtPassword.requestFocus();
            return false;
        }

        // Check confirm password
        if (confirmPassword.isEmpty()) {
            edtConfirmPassword.setError("Please confirm your password");
            edtConfirmPassword.requestFocus();
            return false;
        }

        if (!password.equals(confirmPassword)) {
            edtConfirmPassword.setError("Passwords do not match");
            edtConfirmPassword.requestFocus();
            return false;
        }

        // Check terms agreement
        if (!chkTerms.isChecked()) {
            Toast.makeText(this, "Please agree to the Terms of Service and Privacy Policy", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
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