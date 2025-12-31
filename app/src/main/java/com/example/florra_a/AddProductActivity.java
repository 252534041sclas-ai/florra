package com.example.florra_a;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AddProductActivity extends AppCompatActivity {

    // UI Components
    private ImageView btnBack;
    private TextView btnReset;
    private EditText editTileName;
    private Spinner spinnerCategory;
    private Spinner spinnerSize;
    private EditText editBrandName;
    private Button btnFinishGlossy, btnFinishMatte, btnFinishSatin, btnFinishRustic;
    private Button btnColorWhite, btnColorBeige, btnColorGrey, btnColorBlack, btnColorOther;
    private EditText editPrice;
    private EditText editStock;
    private EditText editDescription;
    private Switch switchActive;
    private Button btnSave;
    private Button btnCancel;

    // Image related components
    private FrameLayout uploadMainImage;
    private ImageView mainImageView;
    private ImageView btnRemoveMainImage;
    private LinearLayout addMoreImage;
    private ImageView thumbnailImageView;
    private FrameLayout thumbnailContainer;
    private LinearLayout defaultUploadView;

    // Selected states
    private String selectedFinish = "Glossy";
    private String selectedColor = "White";

    // Image picker constants
    private static final int CAMERA_REQUEST = 100;
    private static final int GALLERY_REQUEST = 200;
    private static final int PERMISSION_REQUEST_CODE = 300;
    private Uri cameraImageUri;
    private String currentPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        // Initialize UI components
        initViews();
        setupSpinners();
        setupClickListeners();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        btnReset = findViewById(R.id.btnReset);
        editTileName = findViewById(R.id.editTileName);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        spinnerSize = findViewById(R.id.spinnerSize);
        editBrandName = findViewById(R.id.editBrandName);

        // Finish type buttons
        btnFinishGlossy = findViewById(R.id.btnFinishGlossy);
        btnFinishMatte = findViewById(R.id.btnFinishMatte);
        btnFinishSatin = findViewById(R.id.btnFinishSatin);
        btnFinishRustic = findViewById(R.id.btnFinishRustic);

        // Color buttons
        btnColorWhite = findViewById(R.id.btnColorWhite);
        btnColorBeige = findViewById(R.id.btnColorBeige);
        btnColorGrey = findViewById(R.id.btnColorGrey);
        btnColorBlack = findViewById(R.id.btnColorBlack);
        btnColorOther = findViewById(R.id.btnColorOther);

        editPrice = findViewById(R.id.editPrice);
        editStock = findViewById(R.id.editStock);
        editDescription = findViewById(R.id.editDescription);
        switchActive = findViewById(R.id.switchActive);
        btnSave = findViewById(R.id.btnSave);
        btnCancel = findViewById(R.id.btnCancel);

        // Image views
        uploadMainImage = findViewById(R.id.uploadMainImage);
        mainImageView = findViewById(R.id.mainImageView);
        btnRemoveMainImage = findViewById(R.id.btnRemoveMainImage);
        addMoreImage = findViewById(R.id.addMoreImage);
        thumbnailImageView = findViewById(R.id.thumbnailImageView);
        thumbnailContainer = findViewById(R.id.thumbnailContainer);
        defaultUploadView = findViewById(R.id.defaultUploadView);
    }

    private void setupSpinners() {
        // Category spinner
        String[] categories = {"Select", "Ceramic", "Porcelain", "Vitrified", "Mosaic"};
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                categories
        );
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(categoryAdapter);
        spinnerCategory.setSelection(0);

        // Size spinner
        String[] sizes = {"Select", "600x600 mm", "600x1200 mm", "300x300 mm", "800x800 mm"};
        ArrayAdapter<String> sizeAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                sizes
        );
        sizeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSize.setAdapter(sizeAdapter);
        spinnerSize.setSelection(0);
    }

    private void setupClickListeners() {
        // Back button
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // Reset button
        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetForm();
                Toast.makeText(AddProductActivity.this, "Form reset", Toast.LENGTH_SHORT).show();
            }
        });

        // Finish type buttons
        setupFinishTypeListeners();

        // Color buttons
        setupColorListeners();

        // Save button
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveProduct();
            }
        });

        // Cancel button
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Upload main image
        uploadMainImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImagePickerDialog();
            }
        });

        // Remove main image button
        btnRemoveMainImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeSelectedImage();
            }
        });

        // Add more image
        addMoreImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImagePickerDialog();
            }
        });
    }

    private void showImagePickerDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose Image Source");

        String[] options = {"Take Photo", "Choose from Gallery", "Cancel"};
        builder.setItems(options, (dialog, which) -> {
            switch (which) {
                case 0: // Take Photo
                    checkCameraPermission();
                    break;
                case 1: // Choose from Gallery
                    checkStoragePermission();
                    break;
                case 2: // Cancel
                    dialog.dismiss();
                    break;
            }
        });
        builder.show();
    }

    private void checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    PERMISSION_REQUEST_CODE);
        } else {
            openCamera();
        }
    }

    private void checkStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // For Android 13 and above, use READ_MEDIA_IMAGES
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_MEDIA_IMAGES},
                        PERMISSION_REQUEST_CODE);
            } else {
                openGallery();
            }
        } else {
            // For older versions
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        PERMISSION_REQUEST_CODE);
            } else {
                openGallery();
            }
        }
    }

    private void openCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException e) {
                Toast.makeText(this, "Error creating file", Toast.LENGTH_SHORT).show();
                return;
            }

            if (photoFile != null) {
                cameraImageUri = FileProvider.getUriForFile(this,
                        getApplicationContext().getPackageName() + ".provider",
                        photoFile);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, cameraImageUri);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            }
        }
    }

    private void openGallery() {
        Intent galleryIntent = new Intent();
        galleryIntent.setType("image/*");
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(galleryIntent, "Select Image"), GALLERY_REQUEST);
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );

        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Check which permission was granted
                if (permissions[0].equals(Manifest.permission.CAMERA)) {
                    openCamera();
                } else if (permissions[0].equals(Manifest.permission.READ_EXTERNAL_STORAGE) ||
                        permissions[0].equals(Manifest.permission.READ_MEDIA_IMAGES)) {
                    openGallery();
                }
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == CAMERA_REQUEST) {
                // Handle camera image
                if (cameraImageUri != null) {
                    showSelectedImage(cameraImageUri);
                    Toast.makeText(this, "Image captured successfully", Toast.LENGTH_SHORT).show();
                }
            } else if (requestCode == GALLERY_REQUEST && data != null) {
                // Handle gallery image
                Uri selectedImageUri = data.getData();
                if (selectedImageUri != null) {
                    showSelectedImage(selectedImageUri);
                    Toast.makeText(this, "Image selected successfully", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void showSelectedImage(Uri imageUri) {
        try {
            // Set image to main ImageView
            mainImageView.setImageURI(imageUri);
            mainImageView.setVisibility(View.VISIBLE);

            // Hide default upload view
            defaultUploadView.setVisibility(View.GONE);

            // Show remove button
            btnRemoveMainImage.setVisibility(View.VISIBLE);

            // Show thumbnail container and add more button
            thumbnailContainer.setVisibility(View.VISIBLE);
            addMoreImage.setVisibility(View.VISIBLE);

            // Also set thumbnail image
            thumbnailImageView.setImageURI(imageUri);

        } catch (Exception e) {
            Toast.makeText(this, "Error loading image", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void removeSelectedImage() {
        // Clear image
        mainImageView.setImageURI(null);
        mainImageView.setVisibility(View.GONE);

        // Show default upload view
        defaultUploadView.setVisibility(View.VISIBLE);

        // Hide remove button
        btnRemoveMainImage.setVisibility(View.GONE);

        // Hide thumbnail and add more button
        thumbnailContainer.setVisibility(View.GONE);
        addMoreImage.setVisibility(View.GONE);

        // Clear thumbnail image too
        thumbnailImageView.setImageURI(null);

        Toast.makeText(this, "Image removed", Toast.LENGTH_SHORT).show();
    }

    private void setupFinishTypeListeners() {
        View.OnClickListener finishClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetFinishButtons();

                Button clickedButton = (Button) v;
                clickedButton.setTextColor(getResources().getColor(android.R.color.white));
                clickedButton.setBackgroundResource(R.drawable.bg_chip_selected);

                selectedFinish = clickedButton.getText().toString();
            }
        };

        btnFinishGlossy.setOnClickListener(finishClickListener);
        btnFinishMatte.setOnClickListener(finishClickListener);
        btnFinishSatin.setOnClickListener(finishClickListener);
        btnFinishRustic.setOnClickListener(finishClickListener);
    }

    private void setupColorListeners() {
        View.OnClickListener colorClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetColorButtons();

                Button clickedButton = (Button) v;
                clickedButton.setTextColor(getResources().getColor(android.R.color.white));
                clickedButton.setBackgroundResource(R.drawable.bg_chip_selected);

                selectedColor = clickedButton.getText().toString();
            }
        };

        btnColorWhite.setOnClickListener(colorClickListener);
        btnColorBeige.setOnClickListener(colorClickListener);
        btnColorGrey.setOnClickListener(colorClickListener);
        btnColorBlack.setOnClickListener(colorClickListener);
        btnColorOther.setOnClickListener(colorClickListener);
    }

    private void resetFinishButtons() {
        btnFinishGlossy.setTextColor(getResources().getColor(R.color.slate_600));
        btnFinishGlossy.setBackgroundResource(R.drawable.bg_chip_unselected);

        btnFinishMatte.setTextColor(getResources().getColor(R.color.slate_600));
        btnFinishMatte.setBackgroundResource(R.drawable.bg_chip_unselected);

        btnFinishSatin.setTextColor(getResources().getColor(R.color.slate_600));
        btnFinishSatin.setBackgroundResource(R.drawable.bg_chip_unselected);

        btnFinishRustic.setTextColor(getResources().getColor(R.color.slate_600));
        btnFinishRustic.setBackgroundResource(R.drawable.bg_chip_unselected);
    }

    private void resetColorButtons() {
        btnColorWhite.setTextColor(getResources().getColor(R.color.slate_600));
        btnColorWhite.setBackgroundResource(R.drawable.bg_chip_unselected);

        btnColorBeige.setTextColor(getResources().getColor(R.color.slate_600));
        btnColorBeige.setBackgroundResource(R.drawable.bg_chip_unselected);

        btnColorGrey.setTextColor(getResources().getColor(R.color.slate_600));
        btnColorGrey.setBackgroundResource(R.drawable.bg_chip_unselected);

        btnColorBlack.setTextColor(getResources().getColor(R.color.slate_600));
        btnColorBlack.setBackgroundResource(R.drawable.bg_chip_unselected);

        btnColorOther.setTextColor(getResources().getColor(R.color.slate_600));
        btnColorOther.setBackgroundResource(R.drawable.bg_chip_unselected);
    }

    private void resetForm() {
        editTileName.setText("");
        editBrandName.setText("");
        editPrice.setText("");
        editStock.setText("");
        editDescription.setText("");

        spinnerCategory.setSelection(0);
        spinnerSize.setSelection(0);

        resetFinishButtons();
        btnFinishGlossy.setTextColor(getResources().getColor(android.R.color.white));
        btnFinishGlossy.setBackgroundResource(R.drawable.bg_chip_selected);
        selectedFinish = "Glossy";

        resetColorButtons();
        btnColorWhite.setTextColor(getResources().getColor(android.R.color.white));
        btnColorWhite.setBackgroundResource(R.drawable.bg_chip_selected);
        selectedColor = "White";

        switchActive.setChecked(true);

        // Reset image view
        removeSelectedImage();
    }

    private void saveProduct() {
        String tileName = editTileName.getText().toString().trim();
        String category = spinnerCategory.getSelectedItem().toString();
        String size = spinnerSize.getSelectedItem().toString();
        String brandName = editBrandName.getText().toString().trim();
        String price = editPrice.getText().toString().trim();
        String stock = editStock.getText().toString().trim();
        String description = editDescription.getText().toString().trim();
        boolean isActive = switchActive.isChecked();

        if (tileName.isEmpty()) {
            Toast.makeText(this, "Please enter tile name", Toast.LENGTH_SHORT).show();
            editTileName.requestFocus();
            return;
        }

        if (category.equals("Select")) {
            Toast.makeText(this, "Please select a category", Toast.LENGTH_SHORT).show();
            return;
        }

        if (size.equals("Select")) {
            Toast.makeText(this, "Please select a size", Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(this, "Product saved successfully!", Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}