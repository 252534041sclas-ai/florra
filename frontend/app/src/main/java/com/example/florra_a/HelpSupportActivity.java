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

    private boolean isFaqExpanded = false;
    private LinearLayout faqAccordionContent;
    private ImageView imgFaqChevron;
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
        faqAccordionContent = findViewById(R.id.faqAccordionContent);
        imgFaqChevron = findViewById(R.id.imgFaqChevron);
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

        // Accordion FAQ dropdown toggle
        View btnFaqs = findViewById(R.id.btnFaqs);
        if (btnFaqs != null) {
            btnFaqs.setOnClickListener(v -> toggleFaqAccordion());
        }

        // Resources & Guides Dialog Modals
        View btnVideoTutorials = findViewById(R.id.btnVideoTutorials);
        if (btnVideoTutorials != null) {
            btnVideoTutorials.setOnClickListener(v -> showVideoTutorialsDialog());
        }

        View btnUserManual = findViewById(R.id.btnUserManual);
        if (btnUserManual != null) {
            btnUserManual.setOnClickListener(v -> showUserManualDialog());
        }

        // Real Intents for Communication
        View btnCallService = findViewById(R.id.btnCallService);
        if (btnCallService != null) {
            btnCallService.setOnClickListener(v -> launchPhoneDialer());
        }

        View btnEmailSupport = findViewById(R.id.btnEmailSupport);
        if (btnEmailSupport != null) {
            btnEmailSupport.setOnClickListener(v -> launchEmailComposer());
        }
    }

    private void setupFeedbackSubmission() {
        View btnSubmitFeedback = findViewById(R.id.btnSubmitFeedback);
        if (btnSubmitFeedback != null) {
            btnSubmitFeedback.setOnClickListener(v -> handleFeedbackSubmission());
        }
    }

    private void toggleFaqAccordion() {
        if (faqAccordionContent == null || imgFaqChevron == null) return;

        isFaqExpanded = !isFaqExpanded;
        if (isFaqExpanded) {
            faqAccordionContent.setVisibility(View.VISIBLE);
            imgFaqChevron.animate().rotation(90).setDuration(200).start();
        } else {
            faqAccordionContent.setVisibility(View.GONE);
            imgFaqChevron.animate().rotation(0).setDuration(200).start();
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

        // Simulate backend submission lag (1.5 seconds)
        new Handler().postDelayed(() -> {
            loaderDialog.dismiss();
            edtFeedback.setText(""); // Reset the input box
            showFeedbackSuccessDialog();
        }, 1500);
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

    private void showVideoTutorialsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View tutorialsView = getLayoutInflater().inflate(R.layout.dialog_video_tutorials, null);
        builder.setView(tutorialsView);
        AlertDialog dialog = builder.create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new android.graphics.drawable.ColorDrawable(android.graphics.Color.TRANSPARENT));
        }

        View btnClose = tutorialsView.findViewById(R.id.btnClose);
        if (btnClose != null) {
            btnClose.setOnClickListener(v -> dialog.dismiss());
        }

        // Setup mock play triggers on cards
        View videoCard1 = tutorialsView.findViewById(R.id.videoCard1);
        if (videoCard1 != null) {
            videoCard1.setOnClickListener(v -> Toast.makeText(this, "Playing 'Catalog Navigation' tutorial...", Toast.LENGTH_SHORT).show());
        }

        View videoCard2 = tutorialsView.findViewById(R.id.videoCard2);
        if (videoCard2 != null) {
            videoCard2.setOnClickListener(v -> Toast.makeText(this, "Playing 'Request Quotations' tutorial...", Toast.LENGTH_SHORT).show());
        }

        View videoCard3 = tutorialsView.findViewById(R.id.videoCard3);
        if (videoCard3 != null) {
            videoCard3.setOnClickListener(v -> Toast.makeText(this, "Playing 'Using AI Recommendations' tutorial...", Toast.LENGTH_SHORT).show());
        }

        dialog.show();
    }

    private void showUserManualDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View manualView = getLayoutInflater().inflate(R.layout.dialog_user_manual, null);
        builder.setView(manualView);
        AlertDialog dialog = builder.create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new android.graphics.drawable.ColorDrawable(android.graphics.Color.TRANSPARENT));
        }

        View btnClose = manualView.findViewById(R.id.btnClose);
        if (btnClose != null) {
            btnClose.setOnClickListener(v -> dialog.dismiss());
        }

        dialog.show();
    }

    private void launchAIChatbot() {
        try {
            Intent intent = new Intent(this, AIChatActivity.class);
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, "AI Chatbot is currently unavailable", Toast.LENGTH_SHORT).show();
        }
    }

    private void launchPhoneDialer() {
        try {
            Intent dialIntent = new Intent(Intent.ACTION_DIAL);
            dialIntent.setData(Uri.parse("tel:+18005550199"));
            startActivity(dialIntent);
        } catch (Exception e) {
            Toast.makeText(this, "Unable to access phone dialer", Toast.LENGTH_SHORT).show();
        }
    }

    private void launchEmailComposer() {
        try {
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
            emailIntent.setData(Uri.parse("mailto:support@florra.design"));
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Florra App Support Request");
            emailIntent.putExtra(Intent.EXTRA_TEXT, "Hello Florra Support Team,\n\n[Explain your problem/feedback here]\n\n-----------------\nApp Version: v2.4.0\nDevice OS: Android " + Build.VERSION.RELEASE);
            startActivity(Intent.createChooser(emailIntent, "Send support email..."));
        } catch (Exception e) {
            Toast.makeText(this, "No email client application found", Toast.LENGTH_SHORT).show();
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