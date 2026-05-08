package com.example.florra_a;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class RequestQuotationActivity extends AppCompatActivity {

    // UI Components
    private ImageView btnBack, imgProduct;
    private TextView btnClear, txtProductName, txtProductDetails, stockBadge;
    private EditText etFullName, etPhone, etEmail, etTotalArea, etAdditionalNotes;
    private RadioGroup rgProjectType;
    private RadioButton rbResidential, rbCommercial;
    private Spinner spinnerRoomType;
    private Button btnSendEnquiry;

    // Product data passed from ProductDetails
    private String productName = "";
    private String productDetails = "";
    private String productImage = "";
    private String stockStatus = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set fullscreen

        setContentView(R.layout.activity_request_quotation);

        // Initialize all views
        initializeViews();

        // Get product data from intent
        getProductDataFromIntent();

        // Setup all click listeners
        setupClickListeners();

        // Setup spinner
        setupRoomTypeSpinner();
    }

    private void initializeViews() {
        btnBack = findViewById(R.id.btnBack);
        btnClear = findViewById(R.id.btnClear);
        imgProduct = findViewById(R.id.imgProduct);
        txtProductName = findViewById(R.id.txtProductName);
        txtProductDetails = findViewById(R.id.txtProductDetails);
        stockBadge = findViewById(R.id.stockBadge);

        etFullName = findViewById(R.id.etFullName);
        etPhone = findViewById(R.id.etPhone);
        etEmail = findViewById(R.id.etEmail);
        etTotalArea = findViewById(R.id.etTotalArea);
        etAdditionalNotes = findViewById(R.id.etAdditionalNotes);

        rgProjectType = findViewById(R.id.rgProjectType);
        rbResidential = findViewById(R.id.rbResidential);
        rbCommercial = findViewById(R.id.rbCommercial);

        spinnerRoomType = findViewById(R.id.spinnerRoomType);
        btnSendEnquiry = findViewById(R.id.btnSendEnquiry);
    }

    private void getProductDataFromIntent() {
        Intent intent = getIntent();
        if (intent != null) {
            productName = intent.getStringExtra("productName");
            productDetails = intent.getStringExtra("productDetails");
            stockStatus = intent.getStringExtra("stockStatus");
            productImage = intent.getStringExtra("productImage");

            // Set product details
            if (productName != null && !productName.isEmpty()) {
                txtProductName.setText(productName);
            }

            if (productDetails != null && !productDetails.isEmpty()) {
                txtProductDetails.setText(productDetails);
            }

            if (stockStatus != null && !stockStatus.isEmpty()) {
                stockBadge.setText(stockStatus);
                // You can change badge color based on stock status
                if (stockStatus.equals("LOW STOCK")) {
                    stockBadge.setBackgroundResource(R.drawable.bg_tag_low_stock);
                    stockBadge.setTextColor(getResources().getColor(R.color.orange_600));
                } else {
                    stockBadge.setBackgroundResource(R.drawable.bg_tag_green);
                    stockBadge.setTextColor(getResources().getColor(R.color.green_800));
                }
            }
            
            // Load product image
            if (productImage != null && !productImage.isEmpty()) {
                 if (!productImage.startsWith("http")) {
                    // Prepend base URL if relative path
                    String fullUrl = com.example.florra_a.network.RetrofitClient.BASE_URL + productImage;
                    com.bumptech.glide.Glide.with(this)
                        .load(fullUrl)
                        .placeholder(R.drawable.tile_placeholder)
                        .error(R.drawable.tile_placeholder)
                        .centerCrop()
                        .into(imgProduct);
                } else {
                    com.bumptech.glide.Glide.with(this)
                        .load(productImage)
                        .placeholder(R.drawable.tile_placeholder)
                        .error(R.drawable.tile_placeholder)
                        .centerCrop()
                        .into(imgProduct);
                }
            }

            // Total Area
            String area = intent.getStringExtra("totalArea");
            if (area != null && !area.isEmpty()) {
                etTotalArea.setText(area);
            }
        }
    }

    private void setupRoomTypeSpinner() {
        // Create array of room types programmatically
        String[] roomTypes = {
                "Living Room",
                "Bathroom",
                "Kitchen",
                "Outdoor",
                "Lobby"
        };

        // Create ArrayAdapter
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                roomTypes
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRoomType.setAdapter(adapter);
    }

    private void setupClickListeners() {
        // Back button
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // Clear button
        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearAllFields();
            }
        });

        // Send Enquiry button
        btnSendEnquiry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateAndSubmitForm();
            }
        });
    }

    private void clearAllFields() {
        // Clear all input fields
        etFullName.setText("");
        etPhone.setText("");
        etEmail.setText("");
        etTotalArea.setText("");
        etAdditionalNotes.setText("");

        // Reset project type to Residential
        rbResidential.setChecked(true);

        // Reset spinner to first item
        spinnerRoomType.setSelection(0);

        // Show confirmation
        Toast.makeText(this, "All fields cleared", Toast.LENGTH_SHORT).show();
    }

    private void validateAndSubmitForm() {
        // Get all input values
        String fullName = etFullName.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String totalArea = etTotalArea.getText().toString().trim();
        String additionalNotes = etAdditionalNotes.getText().toString().trim();

        // Get project type
        String projectType = rbResidential.isChecked() ? "Residential" : "Commercial";

        // Get room type
        String roomType = spinnerRoomType.getSelectedItem().toString();

        // Validate required fields
        if (fullName.isEmpty()) {
            etFullName.setError("Please enter your full name");
            etFullName.requestFocus();
            return;
        }

        if (phone.isEmpty()) {
            etPhone.setError("Please enter your phone number");
            etPhone.requestFocus();
            return;
        }

        if (email.isEmpty()) {
            etEmail.setError("Please enter your email address");
            etEmail.requestFocus();
            return;
        }

        if (totalArea.isEmpty()) {
            etTotalArea.setError("Please enter total area");
            etTotalArea.requestFocus();
            return;
        }

        // Validate email format
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Please enter a valid email address");
            etEmail.requestFocus();
            return;
        }

        // All validations passed - submit the enquiry
        submitEnquiry(fullName, phone, email, projectType, roomType, totalArea, additionalNotes);
    }

    private void submitEnquiry(String fullName, String phone, String email,
                               String projectType, String roomType, String totalArea,
                               String additionalNotes) {

        // Construct detailed message
        StringBuilder messageBuilder = new StringBuilder();
        messageBuilder.append("New Quotation Request\n");
        messageBuilder.append("Product: ").append(productName).append("\n");
        // Append Image URL if available
        if (productImage != null && !productImage.isEmpty()) {
            messageBuilder.append("Product Image: ").append(productImage).append("\n");
        }
        messageBuilder.append("Details: ").append(productDetails).append("\n");
        messageBuilder.append("----------------\n");
        messageBuilder.append("Project Type: ").append(projectType).append("\n");
        messageBuilder.append("Room Type: ").append(roomType).append("\n");
        messageBuilder.append("Total Area: ").append(totalArea).append(" sq.ft\n");
        messageBuilder.append("Email: ").append(email).append("\n");
        if (!additionalNotes.isEmpty()) {
            messageBuilder.append("Notes: ").append(additionalNotes).append("\n");
        }

        // Create Enquiry object
        com.example.florra_a.models.Enquiry enquiry = new com.example.florra_a.models.Enquiry();
        enquiry.setCustomerName(fullName);
        enquiry.setPhone(phone);
        enquiry.setMessage(messageBuilder.toString());
        enquiry.setStatus("new"); // Lowercase to match backend
        enquiry.setReference("REF-" + System.currentTimeMillis()); 

        // Show progress (optional, for now just toast)
        Toast.makeText(this, "Sending enquiry...", Toast.LENGTH_SHORT).show();
        btnSendEnquiry.setEnabled(false);

        // API Call
        com.example.florra_a.network.ApiService apiService = com.example.florra_a.network.RetrofitClient.getApiService();
        apiService.createEnquiry(enquiry).enqueue(new retrofit2.Callback<com.example.florra_a.models.Enquiry>() {
            @Override
            public void onResponse(retrofit2.Call<com.example.florra_a.models.Enquiry> call, retrofit2.Response<com.example.florra_a.models.Enquiry> response) {
                btnSendEnquiry.setEnabled(true);
                if (response.isSuccessful()) {
                    showSuccessDialog();
                } else {
                    Toast.makeText(RequestQuotationActivity.this, "Failed to submit: " + response.message(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(retrofit2.Call<com.example.florra_a.models.Enquiry> call, Throwable t) {
                btnSendEnquiry.setEnabled(true);
                Toast.makeText(RequestQuotationActivity.this, "Network Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showSuccessDialog() {
        android.app.Dialog dialog = new android.app.Dialog(this);
        dialog.setContentView(R.layout.dialog_enquiry_success);
        dialog.getWindow().setBackgroundDrawable(new android.graphics.drawable.ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCancelable(false); // Prevent closing by clicking outside

        Button btnDone = dialog.findViewById(R.id.btnDone);
        btnDone.setOnClickListener(v -> {
            dialog.dismiss();
            finish(); // Close activity and go back
        });

        dialog.show();
    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}
