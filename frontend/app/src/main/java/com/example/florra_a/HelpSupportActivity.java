package com.example.florra_a;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class HelpSupportActivity extends AppCompatActivity {

    private EditText edtFeedback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set fullscreen and modern status bar color
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(android.graphics.Color.WHITE);
        }

        setContentView(R.layout.activity_help_support);

        // Bind layout views
        edtFeedback = findViewById(R.id.edtFeedback);

        setupBackButton();
        setupNavigationButtons();
        setupSupportButtons();
        setupFeedbackSubmission();
    }

    private void setupBackButton() {
        View btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> onBackPressed());
        }
    }

    private void setupNavigationButtons() {
        setupNavButton(R.id.btnNavHome, this::openHomeScreen);
        setupNavButton(R.id.btnNavCatalog, this::openCatalogScreen);
        setupNavButton(R.id.btnNavEnquiries, this::openQuotationsScreen);
        setupNavButton(R.id.btnNavAccount, this::openAccountScreen);
    }

    private void setupNavButton(int id, Runnable action) {
        LinearLayout button = findViewById(id);
        if (button != null) {
            button.setOnClickListener(v -> action.run());
        }
    }

    private void setupSupportButtons() {
        // AI Support Assistant Card Launch
        View btnLaunchAI = findViewById(R.id.btnLaunchAI);
        if (btnLaunchAI != null) {
            btnLaunchAI.setOnClickListener(v -> launchAIChatbot());
        }
    }

    private void setupFeedbackSubmission() {
        View btnSubmitFeedback = findViewById(R.id.btnSubmitFeedback);
        if (btnSubmitFeedback != null) {
            btnSubmitFeedback.setOnClickListener(v -> handleFeedbackSubmission());
        }
    }

    private void handleFeedbackSubmission() {
        if (edtFeedback == null) return;

        String feedbackText = edtFeedback.getText().toString().trim();
        if (feedbackText.isEmpty()) {
            Toast.makeText(this, "Please describe your problem or feedback first.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Show a sleek progress submission loader dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View loaderView = getLayoutInflater().inflate(R.layout.dialog_submission_loader, null);
        builder.setView(loaderView);
        builder.setCancelable(false);
        AlertDialog loaderDialog = builder.create();

        if (loaderDialog.getWindow() != null) {
            loaderDialog.getWindow().setBackgroundDrawable(new android.graphics.drawable.ColorDrawable(android.graphics.Color.TRANSPARENT));
        }
        loaderDialog.show();

        // Get details from SharedPrefManager
        com.example.florra_a.utils.SharedPrefManager prefManager = com.example.florra_a.utils.SharedPrefManager.getInstance(this);
        String customerName = prefManager.getFullName();
        String customerEmail = prefManager.getEmail();

        // Construct Enquiry object as feedback container
        com.example.florra_a.models.Enquiry feedbackEnquiry = new com.example.florra_a.models.Enquiry();
        feedbackEnquiry.setCustomerName(customerName != null && !customerName.isEmpty() ? customerName : "Anonymous Customer");
        feedbackEnquiry.setPhone(customerEmail != null && !customerEmail.isEmpty() ? customerEmail : "no-email@florra.design");
        feedbackEnquiry.setCustomerEmail(customerEmail);
        
        // Formulate feedback body
        StringBuilder messageBuilder = new StringBuilder();
        messageBuilder.append("FEEDBACK: ").append(feedbackText).append("\n\n");
        messageBuilder.append("Submitted via Help & Support Screen\n");
        messageBuilder.append("App Version: v2.4.0\n");
        messageBuilder.append("Device: ").append(Build.MANUFACTURER).append(" ").append(Build.MODEL).append("\n");
        messageBuilder.append("Device OS: Android ").append(Build.VERSION.RELEASE);
        
        feedbackEnquiry.setMessage(messageBuilder.toString());
        feedbackEnquiry.setStatus("new");
        feedbackEnquiry.setReference("FEEDBACK-" + System.currentTimeMillis());

        // Make the real backend API network call
        com.example.florra_a.network.ApiService apiService = com.example.florra_a.network.RetrofitClient.getApiService();
        apiService.createEnquiry(feedbackEnquiry).enqueue(new retrofit2.Callback<com.example.florra_a.models.Enquiry>() {
            @Override
            public void onResponse(retrofit2.Call<com.example.florra_a.models.Enquiry> call, retrofit2.Response<com.example.florra_a.models.Enquiry> response) {
                loaderDialog.dismiss();
                if (response.isSuccessful()) {
                    edtFeedback.setText(""); // Reset the input box
                    showFeedbackSuccessDialog();
                } else {
                    Toast.makeText(HelpSupportActivity.this, "Failed to send feedback. Please try again.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(retrofit2.Call<com.example.florra_a.models.Enquiry> call, Throwable t) {
                loaderDialog.dismiss();
                Toast.makeText(HelpSupportActivity.this, "Network error. Please check your connection.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showFeedbackSuccessDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View successView = getLayoutInflater().inflate(R.layout.dialog_feedback_success, null);
        builder.setView(successView);
        AlertDialog successDialog = builder.create();

        if (successDialog.getWindow() != null) {
            successDialog.getWindow().setBackgroundDrawable(new android.graphics.drawable.ColorDrawable(android.graphics.Color.TRANSPARENT));
        }

        View btnDone = successView.findViewById(R.id.btnDone);
        if (btnDone != null) {
            btnDone.setOnClickListener(v -> successDialog.dismiss());
        }

        successDialog.show();
    }

    private void launchAIChatbot() {
        try {
            Intent intent = new Intent(this, AIChatActivity.class);
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, "AI Chatbot is currently unavailable", Toast.LENGTH_SHORT).show();
        }
    }

    private void openHomeScreen() {
        try {
            Intent intent = new Intent(this, CustomerHomeActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        } catch (Exception e) {
            Toast.makeText(this, "Unable to open Home screen", Toast.LENGTH_SHORT).show();
        }
    }

    private void openCatalogScreen() {
        try {
            Intent intent = new Intent(this, CatalogActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        } catch (Exception e) {
            Toast.makeText(this, "Unable to open Catalog screen", Toast.LENGTH_SHORT).show();
        }
    }

    private void openQuotationsScreen() {
        try {
            Intent intent = new Intent(this, QuotationsActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        } catch (Exception e) {
            Toast.makeText(this, "Unable to open Quotations screen", Toast.LENGTH_SHORT).show();
        }
    }

    private void openAccountScreen() {
        try {
            Intent intent = new Intent(this, CustomerAccountActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        } catch (Exception e) {
            Toast.makeText(this, "Unable to open Account screen", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        openAccountScreen();
    }
}