package com.example.florra_a;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.net.Uri;
import android.content.Intent;
import android.widget.ImageView;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.FileOutputStream;
import com.bumptech.glide.Glide;

public class EditShopProfileActivity extends AppCompatActivity {

    private EditText edtShopName, edtShopOwner, edtShopPhone, edtShopEmail, edtShopGst, edtShopWastage, edtShopAddress;
    private SharedPreferences sharedPrefs;

    private static final String PREFS_NAME = "ShowroomPrefs";
    private static final String KEY_SHOP_NAME = "shop_name";
    private static final String KEY_SHOP_OWNER = "shop_owner";
    private static final String KEY_SHOP_PHONE = "shop_phone";
    private static final String KEY_SHOP_EMAIL = "shop_email";
    private static final String KEY_SHOP_GST = "shop_gst";
    private static final String KEY_SHOP_WASTAGE = "shop_wastage";
    private static final String KEY_SHOP_ADDRESS = "shop_address";
    private static final String KEY_SHOP_LOGO = "shop_logo_path";

    private ImageView ivShopLogo;
    private String selectedLogoPath = null;

    private final ActivityResultLauncher<Intent> logoPickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri imageUri = result.getData().getData();
                    if (imageUri != null) {
                        try {
                            InputStream is = getContentResolver().openInputStream(imageUri);
                            File logoFile = new File(getFilesDir(), "shop_logo.jpg");
                            OutputStream os = new FileOutputStream(logoFile);
                            byte[] buffer = new byte[1024];
                            int length;
                            while ((length = is.read(buffer)) > 0) {
                                os.write(buffer, 0, length);
                            }
                            os.flush();
                            os.close();
                            is.close();
                            selectedLogoPath = logoFile.getAbsolutePath();
                            Glide.with(this).load(logoFile).into(ivShopLogo);
                        } catch (Exception e) {
                            Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set status bar to white with dark icons
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(Color.WHITE);
        }

        setContentView(R.layout.activity_edit_shop_profile);

        sharedPrefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        initViews();
        loadShowroomDetails();
    }

    private void initViews() {
        // Form EditTexts
        edtShopName = findViewById(R.id.edtShopName);
        edtShopOwner = findViewById(R.id.edtShopOwner);
        edtShopPhone = findViewById(R.id.edtShopPhone);
        edtShopEmail = findViewById(R.id.edtShopEmail);
        edtShopGst = findViewById(R.id.edtShopGst);
        edtShopWastage = findViewById(R.id.edtShopWastage);
        edtShopAddress = findViewById(R.id.edtShopAddress);

        // Header back button
        View btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> onBackPressed());
        }

        // Save Button
        View btnSaveShopProfile = findViewById(R.id.btnSaveShopProfile);
        if (btnSaveShopProfile != null) {
            btnSaveShopProfile.setOnClickListener(v -> validateAndSaveProfile());
        }

        // Change storefront photo click simulator
        ivShopLogo = findViewById(R.id.ivShopLogo);
        View btnChangeLogo = findViewById(R.id.btnChangeLogo);
        if (btnChangeLogo != null) {
            btnChangeLogo.setOnClickListener(v -> {
                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                logoPickerLauncher.launch(intent);
            });
        }
    }

    private void loadShowroomDetails() {
        if (edtShopName != null) {
            edtShopName.setText(sharedPrefs.getString(KEY_SHOP_NAME, "Florra Morbi Tiles"));
        }
        if (edtShopOwner != null) {
            edtShopOwner.setText(sharedPrefs.getString(KEY_SHOP_OWNER, "Akash Patel"));
        }
        if (edtShopPhone != null) {
            edtShopPhone.setText(sharedPrefs.getString(KEY_SHOP_PHONE, "+91 98765 43210"));
        }
        if (edtShopEmail != null) {
            edtShopEmail.setText(sharedPrefs.getString(KEY_SHOP_EMAIL, "morbi.showroom@florra.design"));
        }
        if (edtShopGst != null) {
            edtShopGst.setText(sharedPrefs.getString(KEY_SHOP_GST, "24ABCDE1234F1Z5"));
        }
        if (edtShopWastage != null) {
            edtShopWastage.setText(sharedPrefs.getString(KEY_SHOP_WASTAGE, "8.0"));
        }
        if (edtShopAddress != null) {
            edtShopAddress.setText(sharedPrefs.getString(KEY_SHOP_ADDRESS, "NH-8A, Morbi Bypass Road, Morbi, Gujarat, 363641"));
        }

        selectedLogoPath = sharedPrefs.getString(KEY_SHOP_LOGO, null);
        if (selectedLogoPath != null && ivShopLogo != null) {
            File logoFile = new File(selectedLogoPath);
            if (logoFile.exists()) {
                Glide.with(this).load(logoFile).into(ivShopLogo);
            }
        }
    }

    private void validateAndSaveProfile() {
        String shopName = edtShopName.getText().toString().trim();
        String shopOwner = edtShopOwner.getText().toString().trim();
        String shopPhone = edtShopPhone.getText().toString().trim();
        String shopEmail = edtShopEmail.getText().toString().trim();
        String shopGst = edtShopGst.getText().toString().trim();
        String shopWastage = edtShopWastage.getText().toString().trim();
        String shopAddress = edtShopAddress.getText().toString().trim();

        // Validation Checks
        if (shopName.isEmpty()) {
            Toast.makeText(this, "Showroom Name cannot be empty", Toast.LENGTH_SHORT).show();
            edtShopName.requestFocus();
            return;
        }

        if (shopOwner.isEmpty()) {
            Toast.makeText(this, "Owner / Manager Name cannot be empty", Toast.LENGTH_SHORT).show();
            edtShopOwner.requestFocus();
            return;
        }

        if (shopPhone.isEmpty()) {
            Toast.makeText(this, "Storefront Phone Number cannot be empty", Toast.LENGTH_SHORT).show();
            edtShopPhone.requestFocus();
            return;
        }

        if (shopEmail.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(shopEmail).matches()) {
            Toast.makeText(this, "Please enter a valid showroom email address", Toast.LENGTH_SHORT).show();
            edtShopEmail.requestFocus();
            return;
        }

        if (shopGst.isEmpty()) {
            Toast.makeText(this, "GSTIN number is required", Toast.LENGTH_SHORT).show();
            edtShopGst.requestFocus();
            return;
        }

        if (shopWastage.isEmpty()) {
            Toast.makeText(this, "Please enter a default wastage margin percentage", Toast.LENGTH_SHORT).show();
            edtShopWastage.requestFocus();
            return;
        }

        try {
            double wastage = Double.parseDouble(shopWastage);
            if (wastage < 0 || wastage > 100) {
                Toast.makeText(this, "Wastage margin percentage must be between 0 and 100", Toast.LENGTH_SHORT).show();
                edtShopWastage.requestFocus();
                return;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Please enter a valid wastage number", Toast.LENGTH_SHORT).show();
            edtShopWastage.requestFocus();
            return;
        }

        if (shopAddress.isEmpty()) {
            Toast.makeText(this, "Showroom Address is required", Toast.LENGTH_SHORT).show();
            edtShopAddress.requestFocus();
            return;
        }

        // Show circular spinner dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View loaderView = getLayoutInflater().inflate(R.layout.dialog_submission_loader, null);
        builder.setView(loaderView);
        builder.setCancelable(false);
        AlertDialog loaderDialog = builder.create();

        if (loaderDialog.getWindow() != null) {
            loaderDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        loaderDialog.show();

        // Simulate network / saving delay (1.2 seconds)
        new Handler().postDelayed(() -> {
            loaderDialog.dismiss();

            // Persist the values inside local SharedPreferences
            SharedPreferences.Editor editor = sharedPrefs.edit();
            editor.putString(KEY_SHOP_NAME, shopName);
            editor.putString(KEY_SHOP_OWNER, shopOwner);
            editor.putString(KEY_SHOP_PHONE, shopPhone);
            editor.putString(KEY_SHOP_EMAIL, shopEmail);
            editor.putString(KEY_SHOP_GST, shopGst);
            editor.putString(KEY_SHOP_WASTAGE, shopWastage);
            editor.putString(KEY_SHOP_ADDRESS, shopAddress);
            if (selectedLogoPath != null) {
                editor.putString(KEY_SHOP_LOGO, selectedLogoPath);
            }
            editor.apply();

            // Display success popup
            showSuccessConfirmationDialog();
        }, 1200);
    }

    private void showSuccessConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View successView = getLayoutInflater().inflate(R.layout.dialog_shop_success, null);
        builder.setView(successView);
        AlertDialog successDialog = builder.create();

        if (successDialog.getWindow() != null) {
            successDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        View btnDone = successView.findViewById(R.id.btnDone);
        if (btnDone != null) {
            btnDone.setOnClickListener(v -> {
                successDialog.dismiss();
                finish(); // Go back to Admin Account
            });
        }

        successDialog.show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}
