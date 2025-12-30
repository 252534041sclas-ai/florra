package com.example.florra_a;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
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
                // TODO: Open help section
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
                // TODO: Open Terms of Service
            }
        });

        // Privacy Policy
        btnPrivacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(CreateAccountActivity.this, "Privacy Policy clicked", Toast.LENGTH_SHORT).show();
                // TODO: Open Privacy Policy
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

                // Create account logic
                String fullName = edtFullName.getText().toString().trim();
                String email = edtEmail.getText().toString().trim();
                String mobile = edtMobile.getText().toString().trim();
                String password = edtPassword.getText().toString().trim();

                Toast.makeText(CreateAccountActivity.this, "Account creation in progress...", Toast.LENGTH_SHORT).show();

                // TODO: Add account creation API call here
                // For now, just show success message and navigate to login
                /*
                // After successful registration:
                Toast.makeText(CreateAccountActivity.this, "Account created successfully!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(CreateAccountActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
                */
            }
        });
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
                edtMobile.setError("Please enter a valid mobile number");
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
}