package com.example.florra_a;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsControllerCompat;

public class EditProductDetailsActivity extends AppCompatActivity {

    // UI Components
    private ImageView btnBack, btnDelete;
    private ImageView ivProductImage;
    private Button btnChangePhoto, btnColor, btnCancel, btnSave;
    private EditText etTileName, etBrandName, etPrice, etStock, etDescription;
    private Spinner spCategory, spSize, spFinish;
    private Switch switchActive;

    // Constants for image picker
    private static final int PICK_IMAGE_REQUEST = 100;
    private static final int CAPTURE_IMAGE_REQUEST = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set fullscreen
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

        setContentView(R.layout.activity_edit_product_details);

        // Initialize views
        initViews();

        // Setup spinners
        setupSpinners();

        // Setup click listeners
        setupClickListeners();

        // Load existing data from intent
        loadProductData();
    }

    private void initViews() {
        // Header buttons
        btnBack = findViewById(R.id.btnBack);
        btnDelete = findViewById(R.id.btnDelete);

        // Image section
        ivProductImage = findViewById(R.id.ivProductImage);
        btnChangePhoto = findViewById(R.id.btnChangePhoto);

        // Basic Information
        etTileName = findViewById(R.id.etTileName);
        etBrandName = findViewById(R.id.etBrandName);

        // Classification
        spCategory = findViewById(R.id.spCategory);
        spSize = findViewById(R.id.spSize);
        spFinish = findViewById(R.id.spFinish);
        btnColor = findViewById(R.id.btnColor);

        // Inventory & Pricing
        etPrice = findViewById(R.id.etPrice);
        etStock = findViewById(R.id.etStock);

        // Additional Details
        etDescription = findViewById(R.id.etDescription);
        switchActive = findViewById(R.id.switchActive);

        // Bottom buttons
        btnCancel = findViewById(R.id.btnCancel);
        btnSave = findViewById(R.id.btnSave);
    }

    private void setupSpinners() {
        // Category spinner
        String[] categories = {"Ceramic", "Porcelain", "Vitrified", "Mosaic"};
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, categories);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCategory.setAdapter(categoryAdapter);
        spCategory.setSelection(1); // Default to Porcelain

        // Size spinner
        String[] sizes = {"300x300mm", "600x600mm", "600x1200mm", "800x800mm"};
        ArrayAdapter<String> sizeAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, sizes);
        sizeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spSize.setAdapter(sizeAdapter);
        spSize.setSelection(1); // Default to 600x600mm

        // Finish spinner
        String[] finishes = {"Glossy", "Matte", "Satin", "Rustic"};
        ArrayAdapter<String> finishAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, finishes);
        finishAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spFinish.setAdapter(finishAdapter);
        spFinish.setSelection(0); // Default to Glossy
    }

    private void setupClickListeners() {
        // Back button
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // Delete button
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteConfirmation();
            }
        });

        // Change Photo button
        btnChangePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImagePickerOptions();
            }
        });

        // Color button
        btnColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showColorSelectionDialog();
            }
        });

        // Cancel button
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCancelConfirmation();
            }
        });

        // Save button
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveProductDetails();
            }
        });
    }

    private void loadProductData() {
        // Get data from intent (when coming from ProductDetails screen)
        Intent intent = getIntent();
        if (intent != null) {
            String productName = intent.getStringExtra("product_name");
            String brandName = intent.getStringExtra("brand_name");
            String category = intent.getStringExtra("category");
            String size = intent.getStringExtra("size");
            String price = intent.getStringExtra("price");
            String stock = intent.getStringExtra("stock");
            String description = intent.getStringExtra("description");
            boolean isActive = intent.getBooleanExtra("is_active", true);

            // Set values
            if (productName != null) etTileName.setText(productName);
            if (brandName != null) etBrandName.setText(brandName);
            if (price != null) etPrice.setText(price);
            if (stock != null) etStock.setText(stock);
            if (description != null) etDescription.setText(description);
            switchActive.setChecked(isActive);

            // Set spinners
            if (category != null) {
                for (int i = 0; i < spCategory.getCount(); i++) {
                    if (spCategory.getItemAtPosition(i).toString().equalsIgnoreCase(category)) {
                        spCategory.setSelection(i);
                        break;
                    }
                }
            }

            if (size != null) {
                for (int i = 0; i < spSize.getCount(); i++) {
                    if (spSize.getItemAtPosition(i).toString().equalsIgnoreCase(size)) {
                        spSize.setSelection(i);
                        break;
                    }
                }
            }
        }
    }

    private void showImagePickerOptions() {
        // Create options dialog for image source
        String[] options = {"Take Photo", "Choose from Gallery", "Cancel"};

        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setTitle("Select Image Source")
                .setItems(options, (dialog, which) -> {
                    switch (which) {
                        case 0:
                            takePhoto();
                            break;
                        case 1:
                            chooseFromGallery();
                            break;
                        case 2:
                            dialog.dismiss();
                            break;
                    }
                })
                .show();
    }

    private void takePhoto() {
        // For now, just show a toast
        // In real implementation, you would use Camera Intent
        Toast.makeText(this, "Camera functionality would be implemented here", Toast.LENGTH_SHORT).show();
    }

    private void chooseFromGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == PICK_IMAGE_REQUEST && data != null && data.getData() != null) {
                Uri imageUri = data.getData();
                // Set the image to ImageView
                ivProductImage.setImageURI(imageUri);
                Toast.makeText(this, "Image selected successfully", Toast.LENGTH_SHORT).show();
            } else if (requestCode == CAPTURE_IMAGE_REQUEST) {
                Toast.makeText(this, "Photo captured successfully", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void showColorSelectionDialog() {
        String[] colors = {"White", "Black", "Gray", "Beige", "Blue", "Green", "Red"};

        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setTitle("Select Color")
                .setItems(colors, (dialog, which) -> {
                    String selectedColor = colors[which];
                    btnColor.setText(selectedColor);
                    // You can also change the color indicator icon here
                })
                .show();
    }

    private void showDeleteConfirmation() {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setTitle("Delete Product")
                .setMessage("Are you sure you want to delete this product? This action cannot be undone.")
                .setPositiveButton("Delete", (dialog, which) -> {
                    deleteProduct();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showCancelConfirmation() {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setTitle("Discard Changes")
                .setMessage("Are you sure you want to discard all changes?")
                .setPositiveButton("Discard", (dialog, which) -> {
                    finish();
                })
                .setNegativeButton("Continue Editing", null)
                .show();
    }

    private void saveProductDetails() {
        // Get all values
        String tileName = etTileName.getText().toString().trim();
        String brandName = etBrandName.getText().toString().trim();
        String category = spCategory.getSelectedItem().toString();
        String size = spSize.getSelectedItem().toString();
        String finish = spFinish.getSelectedItem().toString();
        String color = btnColor.getText().toString();
        String price = etPrice.getText().toString().trim();
        String stock = etStock.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        boolean isActive = switchActive.isChecked();

        // Validate inputs
        if (tileName.isEmpty()) {
            etTileName.setError("Tile name is required");
            etTileName.requestFocus();
            return;
        }

        if (price.isEmpty()) {
            etPrice.setError("Price is required");
            etPrice.requestFocus();
            return;
        }

        if (stock.isEmpty()) {
            etStock.setError("Stock quantity is required");
            etStock.requestFocus();
            return;
        }

        // Save logic here (for now just show success message)
        Toast.makeText(this, "Product details saved successfully!", Toast.LENGTH_SHORT).show();

        // Return result to previous activity
        Intent resultIntent = new Intent();
        resultIntent.putExtra("updated_product_name", tileName);
        resultIntent.putExtra("updated_price", price);
        resultIntent.putExtra("updated_stock", stock);
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }

    private void deleteProduct() {
        // Delete logic here
        Toast.makeText(this, "Product deleted successfully", Toast.LENGTH_SHORT).show();

        // Return delete result
        Intent resultIntent = new Intent();
        resultIntent.putExtra("product_deleted", true);
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }

    @Override
    public void onBackPressed() {
        // Check if any changes were made
        showCancelConfirmation();
    }
}