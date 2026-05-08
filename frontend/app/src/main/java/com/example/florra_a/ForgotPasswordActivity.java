package com.example.florra_a;

import              android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.example.florra_a.network.RetrofitClient;

public class ForgotPasswordActivity extends AppCompatActivity {

    // Views
    private ImageView btnBack;
    private TextView txtTitle, txtDescription, txtAppVersion;
    private EditText edtEmail;
    private Button btnSendResetLink, btnBackToLogin;
    private ImageView iconMail;
    
    private View layoutOtp, layoutNewPassword;
    private EditText edtOtp, edtNewPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set fullscreen and edge-to-edge
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        // Enable edge-to-edge
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);

        // Handle notch and status bar
        WindowInsetsControllerCompat windowInsetsController =
                WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());
        windowInsetsController.setAppearanceLightStatusBars(false);
        windowInsetsController.setAppearanceLightNavigationBars(false);

        setContentView(R.layout.activity_forgot_password);

        // Initialize views
        initViews();

        // Setup click listeners
        setupClickListeners();
    }

    private void initViews() {
        // Back button is handled by system back
        txtTitle = findViewById(R.id.txtTitle);
        txtDescription = findViewById(R.id.txtDescription);
        edtEmail = findViewById(R.id.edtEmail);
        btnSendResetLink = findViewById(R.id.btnSendResetLink);
        btnBackToLogin = findViewById(R.id.btnBackToLogin);
        txtAppVersion = findViewById(R.id.txtAppVersion);
        iconMail = findViewById(R.id.iconMail);
        
        layoutOtp = findViewById(R.id.layoutOtp);
        layoutNewPassword = findViewById(R.id.layoutNewPassword);
        edtOtp = findViewById(R.id.edtOtp);
        edtNewPassword = findViewById(R.id.edtNewPassword);
    }

    private void setupClickListeners() {
        // Send reset link button (which now starts OTP flow)
        btnSendResetLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (layoutNewPassword.getVisibility() == View.VISIBLE) {
                    // Step 2: Reset Password
                    resetPassword();
                } else {
                    // Step 1: Send OTP
                    sendOtp();
                }
            }
        });

        // Back to login button
        btnBackToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void sendOtp() {
        String email = edtEmail.getText().toString().trim();

        if (email.isEmpty()) {
            Toast.makeText(this,"Enter email", Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(this, "Sending OTP...", Toast.LENGTH_SHORT).show();
        com.example.florra_a.models.OtpRequest request = new com.example.florra_a.models.OtpRequest(email, "reset");

        RetrofitClient.getApiService().sendOtp(request).enqueue(new retrofit2.Callback<okhttp3.ResponseBody>() {
            @Override
            public void onResponse(retrofit2.Call<okhttp3.ResponseBody> call, retrofit2.Response<okhttp3.ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(ForgotPasswordActivity.this, "OTP Sent! Enter OTP and New Password", Toast.LENGTH_LONG).show();
                    
                    // Show OTP fields
                    layoutOtp.setVisibility(View.VISIBLE);
                    layoutNewPassword.setVisibility(View.VISIBLE);
                    btnSendResetLink.setText("Reset Password");
                    edtEmail.setEnabled(false); // Lock email
                    
                } else {
                     try {
                        String err = response.errorBody() != null ? response.errorBody().string() : "Failed";
                        Toast.makeText(ForgotPasswordActivity.this, err, Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {}
                }
            }

            @Override
            public void onFailure(retrofit2.Call<okhttp3.ResponseBody> call, Throwable t) {
                Toast.makeText(ForgotPasswordActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void resetPassword() {
        String email = edtEmail.getText().toString().trim();
        String otp = edtOtp.getText().toString().trim();
        String newPass = edtNewPassword.getText().toString().trim();
        
        if (otp.isEmpty() || newPass.isEmpty()) {
            Toast.makeText(this, "Fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }
        
        com.example.florra_a.models.ResetPasswordRequest request = new com.example.florra_a.models.ResetPasswordRequest(email, otp, newPass);
        
        RetrofitClient.getApiService().resetPasswordWithOtp(request).enqueue(new retrofit2.Callback<okhttp3.ResponseBody>() {
            @Override
            public void onResponse(retrofit2.Call<okhttp3.ResponseBody> call, retrofit2.Response<okhttp3.ResponseBody> response) {
                if (response.isSuccessful()) {
                     Toast.makeText(ForgotPasswordActivity.this, "Password Reset Successful! Login now.", Toast.LENGTH_LONG).show();
                     finish();
                } else {
                     try {
                        String err = response.errorBody() != null ? response.errorBody().string() : "Failed";
                        Toast.makeText(ForgotPasswordActivity.this, err, Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {}
                }
            }

            @Override
            public void onFailure(retrofit2.Call<okhttp3.ResponseBody> call, Throwable t) {
                 Toast.makeText(ForgotPasswordActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean isValidEmail(String email) {
        String emailPattern = "[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}";
        return email.matches(emailPattern);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}
