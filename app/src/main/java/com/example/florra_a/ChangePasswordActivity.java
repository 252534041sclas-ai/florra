package com.example.florra_a;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class ChangePasswordActivity extends AppCompatActivity {

    // UI Components
    private EditText etCurrentPassword, etNewPassword, etConfirmPassword;
    private ImageView ivCurrentPasswordVisibility, ivNewPasswordVisibility, ivConfirmPasswordVisibility;
    private ImageView ivRequirement1, ivRequirement2, ivRequirement3;
    private RelativeLayout btnBack, btnUpdatePassword, btnToggleCurrentPassword, btnToggleNewPassword, btnToggleConfirmPassword;
    private TextView tvForgotPassword;

    // Password visibility states
    private boolean isCurrentPasswordVisible = false;
    private boolean isNewPasswordVisible = false;
    private boolean isConfirmPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set fullscreen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_change_password);

        initializeViews();
        setupClickListeners();
        setupTextWatchers();
    }

    private void initializeViews() {
        // EditText fields
        etCurrentPassword = findViewById(R.id.etCurrentPassword);
        etNewPassword = findViewById(R.id.etNewPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);

        // Visibility icons
        ivCurrentPasswordVisibility = findViewById(R.id.ivCurrentPasswordVisibility);
        ivNewPasswordVisibility = findViewById(R.id.ivNewPasswordVisibility);
        ivConfirmPasswordVisibility = findViewById(R.id.ivConfirmPasswordVisibility);

        // Requirement icons
        ivRequirement1 = findViewById(R.id.ivRequirement1);
        ivRequirement2 = findViewById(R.id.ivRequirement2);
        ivRequirement3 = findViewById(R.id.ivRequirement3);

        // Buttons
        btnBack = findViewById(R.id.btnBack);
        btnUpdatePassword = findViewById(R.id.btnUpdatePassword);
        btnToggleCurrentPassword = findViewById(R.id.btnToggleCurrentPassword);
        btnToggleNewPassword = findViewById(R.id.btnToggleNewPassword);
        btnToggleConfirmPassword = findViewById(R.id.btnToggleConfirmPassword);

        // TextView
        tvForgotPassword = findViewById(R.id.tvForgotPassword);
    }

    private void setupClickListeners() {
        // Back button
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // Toggle current password visibility
        btnToggleCurrentPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                togglePasswordVisibility(etCurrentPassword, ivCurrentPasswordVisibility, isCurrentPasswordVisible);
                isCurrentPasswordVisible = !isCurrentPasswordVisible;
            }
        });

        // Toggle new password visibility
        btnToggleNewPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                togglePasswordVisibility(etNewPassword, ivNewPasswordVisibility, isNewPasswordVisible);
                isNewPasswordVisible = !isNewPasswordVisible;
            }
        });

        // Toggle confirm password visibility
        btnToggleConfirmPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                togglePasswordVisibility(etConfirmPassword, ivConfirmPasswordVisibility, isConfirmPasswordVisible);
                isConfirmPasswordVisible = !isConfirmPasswordVisible;
            }
        });

        // Forgot password link
        tvForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ChangePasswordActivity.this, "Forgot Password Clicked", Toast.LENGTH_SHORT).show();
                // You can implement forgot password logic here
            }
        });

        // Update password button
        btnUpdatePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updatePassword();
            }
        });
    }

    private void setupTextWatchers() {
        // Watch new password changes to update requirements
        etNewPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                checkPasswordRequirements(s.toString());
            }
        });
    }

    private void togglePasswordVisibility(EditText editText, ImageView imageView, boolean isVisible) {
        if (isVisible) {
            // Hide password
            editText.setTransformationMethod(new PasswordTransformationMethod());
            // Use eye with slash icon (you might need to create this or use a different system icon)
            imageView.setImageResource(android.R.drawable.ic_menu_view);
        } else {
            // Show password
            editText.setTransformationMethod(null);
            // Use eye icon
            imageView.setImageResource(android.R.drawable.ic_menu_view);
        }
        // Move cursor to end
        editText.setSelection(editText.getText().length());
    }

    private void checkPasswordRequirements(String password) {
        if (password == null) password = "";

        // Requirement 1: At least 8 characters
        if (password.length() >= 8) {
            ivRequirement1.setImageResource(android.R.drawable.presence_online);
            ivRequirement1.setColorFilter(getResources().getColor(android.R.color.black));
        } else {
            ivRequirement1.setImageResource(android.R.drawable.radiobutton_off_background);
            ivRequirement1.setColorFilter(getResources().getColor(android.R.color.darker_gray));
        }

        // Requirement 2: Contains at least 1 number
        if (password.matches(".*\\d.*")) {
            ivRequirement2.setImageResource(android.R.drawable.presence_online);
            ivRequirement2.setColorFilter(getResources().getColor(android.R.color.black));
        } else {
            ivRequirement2.setImageResource(android.R.drawable.radiobutton_off_background);
            ivRequirement2.setColorFilter(getResources().getColor(android.R.color.darker_gray));
        }

        // Requirement 3: Contains at least 1 uppercase letter
        if (!password.equals(password.toLowerCase())) {
            ivRequirement3.setImageResource(android.R.drawable.presence_online);
            ivRequirement3.setColorFilter(getResources().getColor(android.R.color.black));
        } else {
            ivRequirement3.setImageResource(android.R.drawable.radiobutton_off_background);
            ivRequirement3.setColorFilter(getResources().getColor(android.R.color.darker_gray));
        }
    }

    private void updatePassword() {
        String currentPassword = etCurrentPassword.getText().toString().trim();
        String newPassword = etNewPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        // Validation
        if (currentPassword.isEmpty()) {
            Toast.makeText(this, "Please enter current password", Toast.LENGTH_SHORT).show();
            return;
        }

        if (newPassword.isEmpty()) {
            Toast.makeText(this, "Please enter new password", Toast.LENGTH_SHORT).show();
            return;
        }

        if (confirmPassword.isEmpty()) {
            Toast.makeText(this, "Please confirm new password", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if passwords match
        if (!newPassword.equals(confirmPassword)) {
            Toast.makeText(this, "New passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check password requirements
        if (newPassword.length() < 8) {
            Toast.makeText(this, "Password must be at least 8 characters", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!newPassword.matches(".*\\d.*")) {
            Toast.makeText(this, "Password must contain at least 1 number", Toast.LENGTH_SHORT).show();
            return;
        }

        if (newPassword.equals(newPassword.toLowerCase())) {
            Toast.makeText(this, "Password must contain at least 1 uppercase letter", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if new password is same as current
        if (currentPassword.equals(newPassword)) {
            Toast.makeText(this, "New password cannot be same as current password", Toast.LENGTH_SHORT).show();
            return;
        }

        // Here you would call your backend API to change password
        // For now, just show success message
        Toast.makeText(this, "Password updated successfully!", Toast.LENGTH_SHORT).show();

        // Go back to previous screen
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}