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
    private EditText editTileNo; // New
    private Spinner spinnerCategory;
    private Spinner spinnerSize;
    private EditText editBrandName;
    private Button btnFinishGlossy, btnFinishMatte, btnFinishSatin, btnFinishRustic;
    private android.widget.AutoCompleteTextView autoCompleteColor; // New
    private EditText editPrice;
    private EditText editStock;
    private EditText editDescription;
    private EditText editThickness; // New
    private EditText editCoverage; // New
    private EditText editWarehouse; // New
    private Switch switchActive, switchNotification;
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
    // private String selectedColor = "White"; // Removed, using AutoCompleteTextView text

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
        setupColorAdapter(); // New
        setupClickListeners();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        btnReset = findViewById(R.id.btnReset);
        editTileName = findViewById(R.id.editTileName);
        editTileNo = findViewById(R.id.editTileNo); // Bind
        spinnerCategory = findViewById(R.id.spinnerCategory);
        spinnerSize = findViewById(R.id.spinnerSize);
        editBrandName = findViewById(R.id.editBrandName);

        // Finish type buttons
        btnFinishGlossy = findViewById(R.id.btnFinishGlossy);
        btnFinishMatte = findViewById(R.id.btnFinishMatte);
        btnFinishSatin = findViewById(R.id.btnFinishSatin);
        btnFinishRustic = findViewById(R.id.btnFinishRustic);

        // Color input
        autoCompleteColor = findViewById(R.id.autoCompleteColor); // Bind

        editPrice = findViewById(R.id.editPrice);
        editStock = findViewById(R.id.editStock);
        editThickness = findViewById(R.id.editThickness); // Bind
        editCoverage = findViewById(R.id.editCoverage); // Bind
        editWarehouse = findViewById(R.id.editWarehouse); // Bind
        editDescription = findViewById(R.id.editDescription);
        switchActive = findViewById(R.id.switchActive);
        switchNotification = findViewById(R.id.switchNotification);
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
        String[] categories = com.example.florra_a.utils.Constants.getCategoriesForSpinner();
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                categories
        );
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(categoryAdapter);
        spinnerCategory.setSelection(0);

        // Size spinner
        String[] sizes = {"Select", "12x12", "2x2 ft", "2x4 ft", "12x18", "12x8"};
        ArrayAdapter<String> sizeAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                sizes
        );
        sizeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSize.setAdapter(sizeAdapter);
        spinnerSize.setSelection(0);
    }

    private void setupColorAdapter() {
        String[] colors = {"White", "Beige", "Grey", "Black", "Brown", "Blue", "Green", "Red", "Yellow", "Cream", "Ivory", "Charcoal"};
        ArrayAdapter<String> colorAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, colors);
        autoCompleteColor.setAdapter(colorAdapter);
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

        // Color buttons listener removed

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
                    try {
                        File imageFile = uriToFile(selectedImageUri);
                        currentPhotoPath = imageFile.getAbsolutePath();
                        showSelectedImage(selectedImageUri);
                        Toast.makeText(this, "Image selected successfully", Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Failed to process image", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }

    private File uriToFile(Uri uri) throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File imageFile = File.createTempFile(imageFileName, ".jpg", storageDir);

        try (java.io.InputStream inputStream = getContentResolver().openInputStream(uri);
             java.io.OutputStream outputStream = new java.io.FileOutputStream(imageFile)) {
            
            if (inputStream == null) {
                throw new IOException("Failed to open input stream");
            }

            byte[] buffer = new byte[4 * 1024];
            int read;
            while ((read = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, read);
            }
            outputStream.flush();
        }
        return imageFile;
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

    // setupColorListeners removed

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

    // resetColorButtons removed as buttons are gone

    private void resetForm() {
        editTileName.setText("");
        editTileNo.setText(""); // Reset
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

        autoCompleteColor.setText(""); // Reset color
        // selectedColor = "White"; // Removed

        switchActive.setChecked(true);
        switchNotification.setChecked(false);

        // Reset image view
        removeSelectedImage();
    }

    private void saveProduct() {
        String tileName = editTileName.getText().toString().trim();
        String tileNo = editTileNo.getText().toString().trim(); // Get Tile No
        String category = spinnerCategory.getSelectedItem().toString();
        String size = spinnerSize.getSelectedItem().toString();
        String brandName = editBrandName.getText().toString().trim();
        String price = editPrice.getText().toString().trim();
        String stock = editStock.getText().toString().trim();
        String thickness = editThickness.getText().toString().trim(); // Get Thickness
        String coverage = editCoverage.getText().toString().trim(); // Get Coverage
        String warehouse = editWarehouse.getText().toString().trim(); // Get Warehouse
        String description = editDescription.getText().toString().trim();
        boolean isActive = switchActive.isChecked();
        boolean sendNotification = switchNotification.isChecked();
        
        String color = autoCompleteColor.getText().toString().trim(); // Get Color

        if (tileName.isEmpty()) {
            Toast.makeText(this, "Please enter tile name", Toast.LENGTH_SHORT).show();
            editTileName.requestFocus();
            return;
        }

        if (tileNo.isEmpty()) {
            Toast.makeText(this, "Please enter tile number", Toast.LENGTH_SHORT).show();
            editTileNo.requestFocus();
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
        
        if (color.isEmpty()) {
             Toast.makeText(this, "Please select or type a color", Toast.LENGTH_SHORT).show();
             autoCompleteColor.requestFocus();
             return;
        }

        // Create RequestBody for text fields
        java.util.Map<String, okhttp3.RequestBody> textFields = new java.util.HashMap<>();
        textFields.put("tile_name", createPartFromString(tileName));
        textFields.put("tile_no", createPartFromString(tileNo)); // Add Tile No
        textFields.put("brand_name", createPartFromString(brandName));
        textFields.put("category", createPartFromString(category));
        textFields.put("size", createPartFromString(size));
        textFields.put("finish", createPartFromString(selectedFinish));
        textFields.put("color", createPartFromString(color)); // Use new color
        textFields.put("price", createPartFromString(price));
        textFields.put("stock", createPartFromString(stock));
        textFields.put("thickness", createPartFromString(thickness)); // Add Thickness
        textFields.put("coverage", createPartFromString(coverage)); // Add Coverage
        textFields.put("warehouse", createPartFromString(warehouse)); // Add Warehouse
        textFields.put("description", createPartFromString(description));
        textFields.put("is_active", createPartFromString(String.valueOf(isActive)));
        textFields.put("send_notification", createPartFromString(String.valueOf(sendNotification)));

        // Handle Image
        okhttp3.MultipartBody.Part imagePart = null;
        if (currentPhotoPath != null) {
            File file = new File(currentPhotoPath);
            // Ensure file exists
            if (file.exists()) {
                okhttp3.RequestBody requestFile = okhttp3.RequestBody.create(
                        okhttp3.MediaType.parse("image/*"),
                        file
                );
                imagePart = okhttp3.MultipartBody.Part.createFormData("image", file.getName(), requestFile);
            }
        } else if (mainImageView.getDrawable() != null && mainImageView.getTag() != null) {
             // Handle generic uri if needed, or enforce file path logic
        }

        // Show loading state (optional, simplified for now)
        btnSave.setEnabled(false);
        Toast.makeText(this, "Saving product...", Toast.LENGTH_SHORT).show();

        // Make API Call
        com.example.florra_a.network.ApiService apiService = com.example.florra_a.network.RetrofitClient.getApiService();
        retrofit2.Call<com.example.florra_a.models.Product> call = apiService.addProduct(textFields, imagePart);

        call.enqueue(new retrofit2.Callback<com.example.florra_a.models.Product>() {
            @Override
            public void onResponse(retrofit2.Call<com.example.florra_a.models.Product> call, retrofit2.Response<com.example.florra_a.models.Product> response) {
                btnSave.setEnabled(true);
                if (response.isSuccessful()) {
                    Toast.makeText(AddProductActivity.this, "Product saved successfully!", Toast.LENGTH_SHORT).show();
                    finish(); // Close activity
                } else {
                    try {
                        String errorBody = response.errorBody().string();
                        Toast.makeText(AddProductActivity.this, "Failed: " + errorBody, Toast.LENGTH_LONG).show();
                        android.util.Log.e("AddProduct", "Error: " + errorBody);
                    } catch (Exception e) {
                        Toast.makeText(AddProductActivity.this, "Failed to save product", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(retrofit2.Call<com.example.florra_a.models.Product> call, Throwable t) {
                btnSave.setEnabled(true);
                Toast.makeText(AddProductActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                android.util.Log.e("AddProduct", "Failure: ", t);
            }
        });
    }

    private okhttp3.RequestBody createPartFromString(String value) {
        return okhttp3.RequestBody.create(okhttp3.MediaType.parse("text/plain"), value != null ? value : "");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}