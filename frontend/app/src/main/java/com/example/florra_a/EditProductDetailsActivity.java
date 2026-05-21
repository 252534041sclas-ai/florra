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
import android.graphics.Color; // Added for color manipulation if needed, or just use resources
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.HashMap;
import java.util.Map;

public class EditProductDetailsActivity extends AppCompatActivity {

    private ImageView btnBack, btnDelete, mainImageView, btnRemoveMainImage;
    private FrameLayout uploadMainImage;
    private LinearLayout defaultUploadView;
    private Button btnCancel, btnSave;
    // Finish Buttons
    private Button btnFinishGlossy, btnFinishMatte, btnFinishSatin, btnFinishRustic;
    
    private EditText editTileName, editTileNo, editBrandName, editPrice, editStock, editDescription;
    private EditText editThickness, editCoverage, editWarehouse;
    private Spinner spinnerCategory, spinnerSize;
    private android.widget.AutoCompleteTextView autoCompleteColor;
    private Switch switchActive, switchNotification;
    private int productId = -1;
    private Uri selectedImageUri;

    private String selectedFinish = "Glossy"; // Default

    private static final int PICK_IMAGE = 100;
    private static final int REQUEST_IMAGE_CAPTURE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_product_details);

        initViews();
        setupSpinners();
        setupColorAdapter();
        setupFinishButtons(); // New
        setupClickListeners();
        loadProductData();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        btnDelete = findViewById(R.id.btnDelete);
        
        // Image UI
        uploadMainImage = findViewById(R.id.uploadMainImage);
        defaultUploadView = findViewById(R.id.defaultUploadView);
        mainImageView = findViewById(R.id.mainImageView);
        btnRemoveMainImage = findViewById(R.id.btnRemoveMainImage);

        editTileName = findViewById(R.id.editTileName);
        editTileNo = findViewById(R.id.editTileNo);
        editBrandName = findViewById(R.id.editBrandName);

        spinnerCategory = findViewById(R.id.spinnerCategory);
        spinnerSize = findViewById(R.id.spinnerSize);
        
        // Finish Buttons
        btnFinishGlossy = findViewById(R.id.btnFinishGlossy);
        btnFinishMatte = findViewById(R.id.btnFinishMatte);
        btnFinishSatin = findViewById(R.id.btnFinishSatin);
        btnFinishRustic = findViewById(R.id.btnFinishRustic);

        autoCompleteColor = findViewById(R.id.autoCompleteColor);
        
        editThickness = findViewById(R.id.editThickness);
        editCoverage = findViewById(R.id.editCoverage);

        editPrice = findViewById(R.id.editPrice);
        editStock = findViewById(R.id.editStock);
        editWarehouse = findViewById(R.id.editWarehouse);
        editDescription = findViewById(R.id.editDescription);
        
        switchActive = findViewById(R.id.switchActive);
        switchNotification = findViewById(R.id.switchNotification);

        btnCancel = findViewById(R.id.btnCancel);
        btnSave = findViewById(R.id.btnSave);
    }

    private void setupSpinners() {
        String[] categories = com.example.florra_a.utils.Constants.getCategoriesForSpinner();
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, categories);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(categoryAdapter);
        spinnerCategory.setSelection(0);

        String[] sizes = {"Select", "12x12", "2x2 ft", "2x4 ft", "12x18", "12x8"};
        ArrayAdapter<String> sizeAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, sizes);
        sizeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSize.setAdapter(sizeAdapter);
        spinnerSize.setSelection(0);
    }
    
    private void setupFinishButtons() {
        View.OnClickListener finishClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button btn = (Button) v;
                selectedFinish = btn.getText().toString();
                updateFinishButtonStyles();
            }
        };

        btnFinishGlossy.setOnClickListener(finishClickListener);
        btnFinishMatte.setOnClickListener(finishClickListener);
        btnFinishSatin.setOnClickListener(finishClickListener);
        btnFinishRustic.setOnClickListener(finishClickListener);
    }

    private void updateFinishButtonStyles() {
        // Reset all
        setButtonState(btnFinishGlossy, false);
        setButtonState(btnFinishMatte, false);
        setButtonState(btnFinishSatin, false);
        setButtonState(btnFinishRustic, false);

        // Set active
        switch (selectedFinish) {
            case "Glossy": setButtonState(btnFinishGlossy, true); break;
            case "Matte": setButtonState(btnFinishMatte, true); break;
            case "Satin": setButtonState(btnFinishSatin, true); break;
            case "Rustic": setButtonState(btnFinishRustic, true); break;
        }
    }

    private void setButtonState(Button btn, boolean isActive) {
        if (isActive) {
            btn.setBackgroundResource(R.drawable.bg_chip_selected);
            btn.setTextColor(getResources().getColor(R.color.white));
        } else {
            btn.setBackgroundResource(R.drawable.bg_chip_unselected);
            btn.setTextColor(getResources().getColor(R.color.slate_600));
        }
    }

    private void setupColorAdapter() {
        String[] colors = {"White", "Beige", "Grey", "Black", "Brown", "Blue", "Green", "Red", "Yellow", "Cream", "Ivory", "Charcoal"};
        ArrayAdapter<String> colorAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, colors);
        autoCompleteColor.setAdapter(colorAdapter);
    }

    private void loadProductData() {
        Intent intent = getIntent();
        if (intent != null) {
            productId = intent.getIntExtra("product_id", -1);
            editTileName.setText(intent.getStringExtra("product_name"));
            editTileNo.setText(intent.getStringExtra("product_tile_no"));
            editBrandName.setText(intent.getStringExtra("product_brand"));
            
            setSpinnerValue(spinnerCategory, intent.getStringExtra("product_category"));
            setSpinnerValue(spinnerSize, intent.getStringExtra("product_size"));
            
            // Set Finish
            String finish = intent.getStringExtra("product_finish");
            if (finish != null && !finish.isEmpty()) {
                selectedFinish = finish;
                updateFinishButtonStyles();
            } else {
                selectedFinish = "Glossy"; // Default
                updateFinishButtonStyles();
            }
            
            autoCompleteColor.setText(intent.getStringExtra("product_color"));
            
            // New fields
            editThickness.setText(intent.getStringExtra("product_thickness"));
            editCoverage.setText(intent.getStringExtra("product_coverage"));
            editWarehouse.setText(intent.getStringExtra("product_warehouse"));

            editPrice.setText(intent.getStringExtra("product_price"));
            editStock.setText(intent.getStringExtra("product_stock"));
            editDescription.setText(intent.getStringExtra("product_description"));
            switchActive.setChecked(intent.getBooleanExtra("product_is_active", true));
            
             // Load Image
            String imageUrl = intent.getStringExtra("product_image");
            if (imageUrl != null && !imageUrl.isEmpty()) {
                 if (!imageUrl.startsWith("http")) {
                     imageUrl = com.example.florra_a.network.RetrofitClient.BASE_URL + imageUrl;
                 }
                com.bumptech.glide.Glide.with(this)
                        .load(imageUrl)
                        .placeholder(R.drawable.ic_tile_placeholder)
                        .into(mainImageView);
                
                mainImageView.setVisibility(View.VISIBLE);
                defaultUploadView.setVisibility(View.GONE);
                btnRemoveMainImage.setVisibility(View.VISIBLE);
            }
        }
    }

    private void setSpinnerValue(Spinner spinner, String value) {
        if (value == null || spinner.getAdapter() == null) return;
        android.widget.Adapter adapter = spinner.getAdapter();
        for (int i = 0; i < adapter.getCount(); i++) {
            if (adapter.getItem(i).toString().equalsIgnoreCase(value)) {
                spinner.setSelection(i);
                return;
            }
        }
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> finish());
        btnDelete.setOnClickListener(v -> showDeleteConfirmation());
        
        uploadMainImage.setOnClickListener(v -> showImagePickerOptions());
        btnRemoveMainImage.setOnClickListener(v -> {
            selectedImageUri = null;
            mainImageView.setImageDrawable(null);
            mainImageView.setVisibility(View.GONE);
            defaultUploadView.setVisibility(View.VISIBLE);
            btnRemoveMainImage.setVisibility(View.GONE);
        });

        btnCancel.setOnClickListener(v -> finish());
        btnSave.setOnClickListener(v -> saveProductDetails());
    }

    private void showImagePickerOptions() {
        String[] options = {"Take Photo", "Choose from Gallery", "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Image Source")
                .setItems(options, (dialog, which) -> {
                    switch (which) {
                        case 0: openCamera(); break;
                        case 1: openGallery(); break;
                        case 2: dialog.dismiss(); break;
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
                selectedImageUri = data.getData();
                mainImageView.setImageURI(selectedImageUri);
                mainImageView.setVisibility(View.VISIBLE);
                defaultUploadView.setVisibility(View.GONE);
                btnRemoveMainImage.setVisibility(View.VISIBLE);
                Toast.makeText(this, "Image selected", Toast.LENGTH_SHORT).show();
            } else if (requestCode == REQUEST_IMAGE_CAPTURE) {
                 Bundle extras = data.getExtras();
                 if (extras != null) {
                     android.graphics.Bitmap imageBitmap = (android.graphics.Bitmap) extras.get("data");
                     mainImageView.setImageBitmap(imageBitmap);
                     mainImageView.setVisibility(View.VISIBLE);
                     defaultUploadView.setVisibility(View.GONE);
                     btnRemoveMainImage.setVisibility(View.VISIBLE);
                     Toast.makeText(this, "Photo taken", Toast.LENGTH_SHORT).show();
                 }
            }
        }
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
        String tileName = editTileName.getText().toString().trim();
        String tileNo = editTileNo.getText().toString().trim();
        String brandName = editBrandName.getText().toString().trim();
        String price = editPrice.getText().toString().trim();
        String stock = editStock.getText().toString().trim();
        String description = editDescription.getText().toString().trim();
        
        String thickness = editThickness.getText().toString().trim();
        String coverage = editCoverage.getText().toString().trim();
        String warehouse = editWarehouse.getText().toString().trim();
        
        String category = spinnerCategory.getSelectedItem().toString();
        String size = spinnerSize.getSelectedItem().toString();
        String finish = selectedFinish;
        String color = autoCompleteColor.getText().toString().trim();
        boolean isActive = switchActive.isChecked();
        boolean sendNotification = switchNotification.isChecked();

        // VALIDATION
        if (tileName.isEmpty()) { editTileName.setError("Required"); return; }
        if (tileNo.isEmpty()) { editTileNo.setError("Required"); return; }
        if (price.isEmpty()) { editPrice.setError("Required"); return; }
        if (stock.isEmpty()) { editStock.setError("Required"); return; }
        if (color.isEmpty()) { autoCompleteColor.setError("Required"); return; }

        if (category.equals("Select")) {
            Toast.makeText(this, "Please select a category", Toast.LENGTH_SHORT).show();
            return;
        }
        if (size.equals("Select")) {
            Toast.makeText(this, "Please select a size", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create Request Body
        Map<String, RequestBody> textFields = new HashMap<>();
        textFields.put("tile_name", createPartFromString(tileName));
        textFields.put("tile_no", createPartFromString(tileNo));
        textFields.put("brand_name", createPartFromString(brandName));
        textFields.put("category", createPartFromString(category));
        textFields.put("size", createPartFromString(size));
        textFields.put("finish", createPartFromString(finish));
        textFields.put("color", createPartFromString(color));
        textFields.put("thickness", createPartFromString(thickness));
        textFields.put("coverage", createPartFromString(coverage));
        textFields.put("warehouse", createPartFromString(warehouse));
        textFields.put("price", createPartFromString(price));
        textFields.put("stock", createPartFromString(stock));
        textFields.put("description", createPartFromString(description));
        textFields.put("is_active", createPartFromString(String.valueOf(isActive)));
        textFields.put("send_notification", createPartFromString(String.valueOf(sendNotification)));

        MultipartBody.Part imagePart = null;
        if (selectedImageUri != null) {
            try {
                java.io.InputStream inputStream = getContentResolver().openInputStream(selectedImageUri);
                byte[] bytes = new byte[inputStream.available()];
                inputStream.read(bytes);
                inputStream.close();
                
                RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), bytes);
                imagePart = MultipartBody.Part.createFormData("image", "product_image.jpg", requestFile);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        com.example.florra_a.network.ApiService apiService = com.example.florra_a.network.RetrofitClient.getApiService();
        Call<com.example.florra_a.models.Product> call = apiService.updateProduct(productId, textFields, imagePart);
        
        call.enqueue(new Callback<com.example.florra_a.models.Product>() {
            @Override
            public void onResponse(Call<com.example.florra_a.models.Product> call, Response<com.example.florra_a.models.Product> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(EditProductDetailsActivity.this, "Product updated!", Toast.LENGTH_SHORT).show();
                    Intent result = new Intent();
                    result.putExtra("updated_product_name", tileName);
                    result.putExtra("updated_product_tile_no", tileNo);
                    result.putExtra("updated_product_brand", brandName);
                    result.putExtra("updated_product_category", category);
                    result.putExtra("updated_product_size", size);
                    result.putExtra("updated_product_finish", finish);
                    result.putExtra("updated_product_color", color);
                    result.putExtra("updated_product_price", price);
                    result.putExtra("updated_product_stock", stock);
                    result.putExtra("updated_product_description", description);
                    result.putExtra("updated_product_thickness", thickness);
                    result.putExtra("updated_product_coverage", coverage);
                    result.putExtra("updated_product_warehouse", warehouse);
                    result.putExtra("updated_product_is_active", isActive);
                    setResult(RESULT_OK, result);
                    finish();
                } else {
                    String errorMsg = "Code: " + response.code();
                    try {
                        if (response.errorBody() != null) {
                            errorMsg += " - " + response.errorBody().string();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(EditProductDetailsActivity.this, "Update failed: " + errorMsg, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<com.example.florra_a.models.Product> call, Throwable t) {
                Toast.makeText(EditProductDetailsActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private RequestBody createPartFromString(String value) {
        return RequestBody.create(MediaType.parse("text/plain"), value != null ? value : "");
    }
}