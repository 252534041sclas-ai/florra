package com.example.florra_a;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class EditProductDetailsActivity extends AppCompatActivity {

    private ImageView btnBack, btnDelete, ivProductImage;
    private Button btnChangePhoto, btnColor, btnCancel, btnSave;
    private EditText etTileName, etBrandName, etPrice, etStock, etDescription;
    private Spinner spCategory, spSize, spFinish;
    private Switch switchActive;

    private static final int PICK_IMAGE = 100;
    private static final int REQUEST_IMAGE_CAPTURE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_product_details);

        initViews();
        setupSpinners();
        setupClickListeners();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        btnDelete = findViewById(R.id.btnDelete);
        ivProductImage = findViewById(R.id.ivProductImage);
        btnChangePhoto = findViewById(R.id.btnChangePhoto);

        // ADD THESE FIELDS
        etTileName = findViewById(R.id.etTileName);
        etBrandName = findViewById(R.id.etBrandName);

        spCategory = findViewById(R.id.spCategory);
        spSize = findViewById(R.id.spSize);
        spFinish = findViewById(R.id.spFinish);
        btnColor = findViewById(R.id.btnColor);

        //etPrice = findViewById(R.id.etPrice);
        //etStock = findViewById(R.id.etStock);

        // ADD THESE FIELDS
        //etDescription = findViewById(R.id.etDescription);
        switchActive = findViewById(R.id.switchActive);

        btnCancel = findViewById(R.id.btnCancel);
        btnSave = findViewById(R.id.btnSave);
    }

    private void setupSpinners() {
        String[] categories = {"Ceramic", "Porcelain", "Vitrified", "Mosaic"};
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, categories);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCategory.setAdapter(categoryAdapter);
        spCategory.setSelection(1);

        String[] sizes = {"300x300mm", "600x600mm", "600x1200mm", "800x800mm"};
        ArrayAdapter<String> sizeAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, sizes);
        sizeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spSize.setAdapter(sizeAdapter);
        spSize.setSelection(1);

        String[] finishes = {"Glossy", "Matte", "Satin", "Rustic"};
        ArrayAdapter<String> finishAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, finishes);
        finishAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spFinish.setAdapter(finishAdapter);
        spFinish.setSelection(0);
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteConfirmation();
            }
        });

        // IMAGE PICKER
        btnChangePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImagePickerOptions();
            }
        });

        btnColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showColorSelectionDialog();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveProductDetails();
            }
        });
    }

    private void showImagePickerOptions() {
        String[] options = {"Take Photo", "Choose from Gallery", "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Image Source")
                .setItems(options, (dialog, which) -> {
                    switch (which) {
                        case 0:
                            openCamera();
                            break;
                        case 1:
                            openGallery();
                            break;
                        case 2:
                            dialog.dismiss();
                            break;
                    }
                })
                .show();
    }

    private void openCamera() {
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE);
        } else {
            Toast.makeText(this, "No camera app found", Toast.LENGTH_SHORT).show();
        }
    }

    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == PICK_IMAGE && data != null) {
                Uri selectedImageUri = data.getData();
                ivProductImage.setImageURI(selectedImageUri);
                Toast.makeText(this, "Image selected", Toast.LENGTH_SHORT).show();
            } else if (requestCode == REQUEST_IMAGE_CAPTURE) {
                Toast.makeText(this, "Photo taken", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void showColorSelectionDialog() {
        String[] colors = {"White", "Black", "Gray", "Beige", "Blue", "Green", "Red"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Color")
                .setItems(colors, (dialog, which) -> {
                    String selectedColor = colors[which];
                    btnColor.setText(selectedColor);
                })
                .show();
    }

    private void showDeleteConfirmation() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Product")
                .setMessage("Are you sure you want to delete this product?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    Intent result = new Intent();
                    result.putExtra("product_deleted", true);
                    setResult(RESULT_OK, result);
                    finish();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void saveProductDetails() {
        // GET ALL VALUES
        String tileName = etTileName.getText().toString().trim();
        String brandName = etBrandName.getText().toString().trim();
        String price = etPrice.getText().toString().trim();
        String stock = etStock.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        boolean isActive = switchActive.isChecked();

        // VALIDATION
        if (tileName.isEmpty()) {
            etTileName.setError("Tile name is required");
            return;
        }

        if (price.isEmpty()) {
            etPrice.setError("Price is required");
            return;
        }

        if (stock.isEmpty()) {
            etStock.setError("Stock is required");
            return;
        }

        Toast.makeText(this, "Product saved successfully!", Toast.LENGTH_SHORT).show();

        Intent result = new Intent();
        result.putExtra("updated_product_name", tileName);
        result.putExtra("updated_price", price);
        result.putExtra("updated_stock", stock);
        setResult(RESULT_OK, result);
        finish();
    }
}