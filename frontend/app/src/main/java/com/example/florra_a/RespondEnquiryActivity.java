package com.example.florra_a;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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

public class RespondEnquiryActivity extends AppCompatActivity {

    private EditText etPricePerSqft, etBoxes, etDeliveryTime, etAdditionalNotes;
    private TextView tvTotalPrice;
    private ImageView btnIncrease, btnDecrease;
    private Button btnSaveDraft, btnSendQuotation;
    private ImageView btnBack;
    private com.example.florra_a.models.Enquiry currentEnquiry;

    // Info Views
    private TextView tvEnquiryId, tvStockStatus, tvProductName, tvProductSize, tvEnquiryDate;
    private TextView tvCustomerName, tvCustomerPhone, tvCustomerEmail, tvProjectType, tvRoomType, tvTotalArea, tvCustomerNotes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_respond_enquiry);

        initializeViews();
        setupClickListeners();
        setupTextWatchers();
        
        // Get Enquiry Data
        if (getIntent().hasExtra("enquiry_data")) {
            currentEnquiry = (com.example.florra_a.models.Enquiry) getIntent().getSerializableExtra("enquiry_data");
            populateData();
        }
    }

    private void initializeViews() {
        etPricePerSqft = findViewById(R.id.etPricePerSqft);
        etBoxes = findViewById(R.id.etBoxes);
        etDeliveryTime = findViewById(R.id.etDeliveryTime);
        etAdditionalNotes = findViewById(R.id.etAdditionalNotes);
        tvTotalPrice = findViewById(R.id.tvTotalPrice);
        btnIncrease = findViewById(R.id.btnIncrease);
        btnDecrease = findViewById(R.id.btnDecrease);
        btnSaveDraft = findViewById(R.id.btnSaveDraft);
        btnSendQuotation = findViewById(R.id.btnSendQuotation);
        btnBack = findViewById(R.id.btnBack);

        // Info Views
        tvEnquiryId = findViewById(R.id.tvEnquiryId);
        tvStockStatus = findViewById(R.id.tvStockStatus);
        tvProductName = findViewById(R.id.tvProductName);
        tvProductSize = findViewById(R.id.tvProductSize);
        tvEnquiryDate = findViewById(R.id.tvEnquiryDate);
        tvCustomerName = findViewById(R.id.tvCustomerName);
        tvCustomerPhone = findViewById(R.id.tvCustomerPhone);
        tvCustomerEmail = findViewById(R.id.tvCustomerEmail);
        tvProjectType = findViewById(R.id.tvProjectType);
        tvRoomType = findViewById(R.id.tvRoomType);
        tvTotalArea = findViewById(R.id.tvTotalArea);
        tvCustomerNotes = findViewById(R.id.tvCustomerNotes);
    }

    private void populateData() {
        if (currentEnquiry == null) return;

        tvEnquiryId.setText("Enquiry #" + (currentEnquiry.getReference() != null ? currentEnquiry.getReference() : currentEnquiry.getId()));
        tvStockStatus.setText(currentEnquiry.getStatus());
        
        // Customer Details
        tvCustomerName.setText(currentEnquiry.getCustomerName() != null ? currentEnquiry.getCustomerName() : "N/A");
        tvCustomerPhone.setText(currentEnquiry.getPhone() != null ? currentEnquiry.getPhone() : "N/A");
        tvCustomerEmail.setText(currentEnquiry.getCustomerEmail() != null ? currentEnquiry.getCustomerEmail() : "N/A");
        
        if (currentEnquiry.getMessage() != null) {
            parseMessageAndUpdateUI(currentEnquiry.getMessage());
        } else {
            // Default Fallback
            tvProductName.setText("Enquiry Product"); 
            tvProductSize.setText("-");
            tvProjectType.setText("-");
            tvRoomType.setText("-");
            tvTotalArea.setText("-");
            tvCustomerNotes.setText("No message provided.");
        }
    }

    private void parseMessageAndUpdateUI(String message) {
        String product = "-";
        String details = "-";
        String projectType = "-";
        String roomType = "-";
        String area = "-";
        String image = "-";
        String notes = "-";

        try {
            String[] lines = message.split("\n");
            StringBuilder notesBuilder = new StringBuilder();
            boolean isNotesSection = false;

            for (String line : lines) {
                if (line.startsWith("Product: ")) {
                    product = line.replace("Product: ", "").trim();
                } else if (line.startsWith("Product Image: ")) {
                    image = line.replace("Product Image: ", "").trim();
                } else if (line.startsWith("Details: ")) {
                    details = line.replace("Details: ", "").trim();
                } else if (line.startsWith("Project Type: ")) {
                    projectType = line.replace("Project Type: ", "").trim();
                } else if (line.startsWith("Room Type: ")) {
                    roomType = line.replace("Room Type: ", "").trim();
                } else if (line.startsWith("Total Area: ")) {
                    area = line.replace("Total Area: ", "").trim();
                } else if (line.startsWith("Notes: ")) {
                    isNotesSection = true;
                    notesBuilder.append(line.replace("Notes: ", "")).append("\n");
                } else if (isNotesSection) {
                    notesBuilder.append(line).append("\n");
                }
            }
            
            if (notesBuilder.length() > 0) {
                notes = notesBuilder.toString().trim();
            } else {
                notes = "No additional notes.";
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        // Update UI
        tvProductName.setText(!product.isEmpty() && !product.equals("-") ? product : "Enquiry Product");
        
        // Show actual product details (Size, Finish) if available, else show Area, else "-"
        if (!details.isEmpty() && !details.equals("-")) {
            tvProductSize.setText(details);
        } else {
             tvProductSize.setText(!area.isEmpty() && !area.equals("-") ? "Area: " + area : "-");
        }
        
        tvProjectType.setText(projectType);
        tvRoomType.setText(roomType);
        tvTotalArea.setText(area);
        
        tvCustomerNotes.setText(notes);
        
        // Load Image if found
        if (!image.equals("-") && !image.isEmpty()) {
             if (!image.startsWith("http")) {
                image = com.example.florra_a.network.RetrofitClient.BASE_URL + image;
            }
            
            com.bumptech.glide.Glide.with(this)
                .load(image)
                .placeholder(R.drawable.ti) // Placeholder drawable
                .error(R.drawable.ti) 
                .centerCrop()
                .into((ImageView) findViewById(R.id.ivProductImage));
        }
    }

    private void setupClickListeners() {
        // Back button
        btnBack.setOnClickListener(v -> onBackPressed());

        // Quantity buttons
        btnIncrease.setOnClickListener(v -> {
            int currentValue = getCurrentBoxes();
            etBoxes.setText(String.valueOf(currentValue + 1));
            calculateTotalPrice();
        });

        btnDecrease.setOnClickListener(v -> {
            int currentValue = getCurrentBoxes();
            if (currentValue > 0) {
                etBoxes.setText(String.valueOf(currentValue - 1));
                calculateTotalPrice();
            }
        });

        // Save Draft button
        if (btnSaveDraft != null) {
            btnSaveDraft.setOnClickListener(v -> {
                saveAsDraft();
            });
        }

        // Send Quotation button
        btnSendQuotation.setOnClickListener(v -> {
            sendQuotation();
        });
    }

    private void setupTextWatchers() {
        etPricePerSqft.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                calculateTotalPrice();
            }
        });

        etBoxes.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                calculateTotalPrice();
            }
        });
    }

    private int getCurrentBoxes() {
        try {
            return Integer.parseInt(etBoxes.getText().toString());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private void calculateTotalPrice() {
        try {
            double pricePerSqft = Double.parseDouble(etPricePerSqft.getText().toString());
            int boxes = getCurrentBoxes();

            // Assuming 1 box = 10 sq.ft (you can change this)
            double totalSqft = boxes * 10;
            double totalPrice = pricePerSqft * totalSqft;

            tvTotalPrice.setText(String.format("₹ %,.2f", totalPrice));
        } catch (NumberFormatException e) {
            tvTotalPrice.setText("₹ 0.00");
        }
    }

    private void saveAsDraft() {
        Toast.makeText(this, "Quotation saved as draft", Toast.LENGTH_SHORT).show();
        finish();
    }

    private void sendQuotation() {
        // Validate inputs
        if (etPricePerSqft.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Please enter price per sq.ft", Toast.LENGTH_SHORT).show();
            return;
        }

        if (etDeliveryTime.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Please enter delivery time", Toast.LENGTH_SHORT).show();
            return;
        }

        // Send quotation logic
        if (currentEnquiry != null) {
            currentEnquiry.setQuotationPrice(etPricePerSqft.getText().toString());
            currentEnquiry.setQuotationBoxes(etBoxes.getText().toString());
            currentEnquiry.setQuotationDeliveryTime(etDeliveryTime.getText().toString());
            currentEnquiry.setQuotationNotes(etAdditionalNotes.getText().toString());
            currentEnquiry.setStatus("Quoted"); // Update status

            com.example.florra_a.network.ApiService apiService = com.example.florra_a.network.RetrofitClient.getApiService();
            apiService.respondToEnquiry(currentEnquiry).enqueue(new retrofit2.Callback<com.example.florra_a.models.Enquiry>() {
                @Override
                public void onResponse(retrofit2.Call<com.example.florra_a.models.Enquiry> call, retrofit2.Response<com.example.florra_a.models.Enquiry> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(RespondEnquiryActivity.this, "Quotation sent successfully!", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(RespondEnquiryActivity.this, "Failed to send quotation", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(retrofit2.Call<com.example.florra_a.models.Enquiry> call, Throwable t) {
                    Toast.makeText(RespondEnquiryActivity.this, "Network Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
             Toast.makeText(this, "Error: No enquiry data found", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
