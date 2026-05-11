package com.example.florra_a;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AutoCompleteTextView;
import android.widget.ArrayAdapter;
import com.example.florra_a.models.Product;
import com.example.florra_a.network.RetrofitClient;
import com.example.florra_a.network.ApiService;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import org.json.JSONObject;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class GenerateBillActivity extends AppCompatActivity {

    // TextViews for non-editable fields
    private TextView tvBillNo, tvDate;
    private TextView tvSubtotal, tvGSTAmount, tvDiscountAmount, tvLoadingAmount, tvGrandTotal;

    // EditTexts for inputs
    private EditText etCustomerName, etCustomerPhone, etCustomerAddress;
    private EditText etGST, etDiscount, etLoading;

    // Buttons
    private Button btnBack, btnPreview, btnSaveBill, btnAddItem;
    
    // RecyclerView
    private androidx.recyclerview.widget.RecyclerView rvBillItems;
    private BillItemAdapter billItemAdapter;
    private java.util.List<com.example.florra_a.models.BillItem> billItems;

    // Calculation variables
    private double subtotal = 0.0;
    private double gstPercentage = 18.0;
    private double discount = 0.0;
    private double loading = 0.0;
    
    // Product list for search
    private List<Product> allProducts = new ArrayList<>();
    private List<String> productSuggestions = new ArrayList<>();
    private List<String> numberSuggestions = new ArrayList<>();
    private boolean isAutoPopulating = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
                // Set status bar to white with dark icons
        // Set status bar to white with dark icons
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            getWindow().setStatusBarColor(android.graphics.Color.WHITE);
            WindowCompat.setDecorFitsSystemWindows(getWindow(), true);
            WindowInsetsControllerCompat controller = new WindowInsetsControllerCompat(getWindow(), getWindow().getDecorView());
            controller.setAppearanceLightStatusBars(true);
        }

        setContentView(R.layout.activity_generate_bill);

        initViews();
        setListeners();
        calculateTotals();
        fetchProducts();
    }

    private void initViews() {
        // Customer Inputs
        etCustomerName = findViewById(R.id.etCustomerName);
        etCustomerPhone = findViewById(R.id.etCustomerPhone);
        etCustomerAddress = findViewById(R.id.etCustomerAddress);

        // Bill Details
        tvBillNo = findViewById(R.id.tvBillNo);
        tvDate = findViewById(R.id.tvDate);

        // Financials (Read-only)
        tvSubtotal = findViewById(R.id.tvSubtotal);
        tvGSTAmount = findViewById(R.id.tvGSTAmount);
        tvDiscountAmount = findViewById(R.id.tvDiscountAmount);
        tvLoadingAmount = findViewById(R.id.tvLoadingAmount);
        tvGrandTotal = findViewById(R.id.tvGrandTotal);

        // Financial Inputs
        etGST = findViewById(R.id.etGST);
        etDiscount = findViewById(R.id.etDiscount);
        etLoading = findViewById(R.id.etLoading);

        // Buttons
        btnBack = findViewById(R.id.btnBack);
        btnPreview = findViewById(R.id.btnPreview);
        btnSaveBill = findViewById(R.id.btnSaveBill);
        btnAddItem = findViewById(R.id.btnAddItem);

        // RecyclerView Setup
        rvBillItems = findViewById(R.id.rvBillItems);
        rvBillItems.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(this));
        billItems = new java.util.ArrayList<>();
        billItemAdapter = new BillItemAdapter(billItems);
        rvBillItems.setAdapter(billItemAdapter);

        // Set initial Bill No and Date
        tvBillNo.setText("FL-" + System.currentTimeMillis());
        tvDate.setText(new java.text.SimpleDateFormat("dd MMM, yyyy", java.util.Locale.getDefault()).format(new java.util.Date()));
    }

    private void setListeners() {

        btnBack.setOnClickListener(v -> onBackPressed());

        btnSaveBill.setOnClickListener(v -> confirmSave());
        
        btnAddItem.setOnClickListener(v -> showAddItemDialog());

        // PREVIEW BILL INTEGRATION
        btnPreview.setOnClickListener(v -> {
            Intent intent = new Intent(GenerateBillActivity.this, PreviewBillActivity.class);
            
            // Pass Data
            intent.putExtra("billNo", tvBillNo.getText().toString());
            intent.putExtra("date", tvDate.getText().toString());
            intent.putExtra("customerName", etCustomerName.getText().toString());
            intent.putExtra("customerPhone", etCustomerPhone.getText().toString());
            intent.putExtra("customerAddress", etCustomerAddress.getText().toString());
            
            // Pass Amounts (Formatted Strings or values)
            intent.putExtra("subtotal", tvSubtotal.getText().toString());
            intent.putExtra("gstAmount", tvGSTAmount.getText().toString()); 
            intent.putExtra("discount", tvDiscountAmount.getText().toString());
            intent.putExtra("loading", tvLoadingAmount.getText().toString());
            intent.putExtra("grandTotal", tvGrandTotal.getText().toString());

            // Pass Items List
            intent.putExtra("items", (java.io.Serializable) billItems);

            startActivity(intent);
        });

        etGST.addTextChangedListener(simpleWatcher(value -> {
            gstPercentage = value;
            calculateTotals();
        }));

        etDiscount.addTextChangedListener(simpleWatcher(value -> {
            discount = value;
            calculateTotals();
        }));

        etLoading.addTextChangedListener(simpleWatcher(value -> {
            loading = value;
            calculateTotals();
        }));
    }

    private void showAddItemDialog() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_add_bill_item, null);
        builder.setView(view);

        final AutoCompleteTextView etItemName = view.findViewById(R.id.etItemName);
        final AutoCompleteTextView etItemNo = view.findViewById(R.id.etItemNo);
        final EditText etItemSize = view.findViewById(R.id.etItemSize);
        final EditText etItemQty = view.findViewById(R.id.etItemQty);
        final EditText etItemRate = view.findViewById(R.id.etItemRate);

        // Setup Name AutoComplete
        if (!productSuggestions.isEmpty()) {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, productSuggestions);
            etItemName.setAdapter(adapter);
            etItemName.setOnItemClickListener((parent, v, position, id) -> {
                String selected = (String) parent.getItemAtPosition(position);
                autoPopulateBySelection(selected, true, etItemName, etItemNo, etItemSize, etItemRate);
            });
            etItemName.addTextChangedListener(new SimpleTextWatcher(s -> {
                if (!isAutoPopulating) autoPopulateBySelection(s, true, etItemName, etItemNo, etItemSize, etItemRate);
            }));
        }

        // Setup Number AutoComplete
        if (!numberSuggestions.isEmpty()) {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, numberSuggestions);
            etItemNo.setAdapter(adapter);
            etItemNo.setOnItemClickListener((parent, v, position, id) -> {
                String selected = (String) parent.getItemAtPosition(position);
                autoPopulateBySelection(selected, false, etItemName, etItemNo, etItemSize, etItemRate);
            });
            etItemNo.addTextChangedListener(new SimpleTextWatcher(s -> {
                if (!isAutoPopulating) autoPopulateBySelection(s, false, etItemName, etItemNo, etItemSize, etItemRate);
            }));
        }

        builder.setPositiveButton("Add", null); // Set to null first to override onClick
        builder.setNegativeButton("Cancel", null);

        final android.app.AlertDialog dialog = builder.create();
        dialog.show();

        dialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String name = etItemName.getText().toString().trim();
            String no = etItemNo.getText().toString().trim();
            String size = etItemSize.getText().toString().trim();
            String qtyStr = etItemQty.getText().toString().trim();
            String rateStr = etItemRate.getText().toString().trim();

            if (name.isEmpty() || qtyStr.isEmpty() || rateStr.isEmpty()) {
                Toast.makeText(GenerateBillActivity.this, "Please fill required fields", Toast.LENGTH_SHORT).show();
                return;
            }

            int qty = Integer.parseInt(qtyStr);
            double rate = Double.parseDouble(rateStr);
            double amount = qty * rate;

            com.example.florra_a.models.BillItem item = new com.example.florra_a.models.BillItem(name, no, size, qty, rate, amount);
            billItems.add(item);
            billItemAdapter.notifyItemInserted(billItems.size() - 1);
            
            calculateTotals();
            dialog.dismiss();
        });
    }

    private TextWatcher simpleWatcher(ValueCallback callback) {
        return new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int st, int c, int a) {}
            public void onTextChanged(CharSequence s, int st, int b, int c) {}
            public void afterTextChanged(Editable s) {
                try {
                    callback.onChange(s.length() > 0 ? Double.parseDouble(s.toString()) : 0);
                } catch (Exception e) {
                    callback.onChange(0);
                }
            }
        };
    }

    private void calculateTotals() {
        // Sum up all items
        subtotal = 0.0;
        for (com.example.florra_a.models.BillItem item : billItems) {
            subtotal += item.getAmount();
        }

        double gstAmount = (subtotal * gstPercentage) / 100;
        double grandTotal = subtotal + gstAmount - discount + loading;

        tvSubtotal.setText("₹" + (int) subtotal);
        tvGSTAmount.setText("+ ₹" + (int) gstAmount);
        tvDiscountAmount.setText("- ₹" + (int) discount);
        tvLoadingAmount.setText("+ ₹" + (int) loading);
        tvGrandTotal.setText("₹" + (int) grandTotal);
    }

    private void confirmSave() {
        if (billItems.isEmpty()) {
             Toast.makeText(this, "Please add at least one item", Toast.LENGTH_SHORT).show();
             return;
        }

        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Save Bill")
                .setMessage("Are you sure?")
                .setPositiveButton("Save", (d, w) -> saveBillToBackend())
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void autoPopulateBySelection(String input, boolean isNameInput, EditText etName, EditText etNo, EditText etSize, EditText etRate) {
        if (input == null || input.isEmpty() || isAutoPopulating) return;
        
        isAutoPopulating = true;
        try {
            for (Product p : allProducts) {
                String nameMatch = p.getTileName() + " (" + (p.getTileNo() != null ? p.getTileNo() : "N/A") + ")";
                String noMatch = p.getTileNo();
                
                boolean matched = false;
                if (isNameInput) {
                    if (nameMatch.equals(input) || p.getTileName().equalsIgnoreCase(input)) matched = true;
                } else {
                    if (noMatch != null && noMatch.equalsIgnoreCase(input)) matched = true;
                }

                if (matched) {
                    if (isNameInput) etNo.setText(p.getTileNo());
                    else etName.setText(p.getTileName());
                    
                    etSize.setText(p.getSize());
                    etRate.setText(p.getPrice());
                    break;
                }
            }
        } finally {
            isAutoPopulating = false;
        }
    }

    // Simple helper for TextWatcher
    private class SimpleTextWatcher implements android.text.TextWatcher {
        private final java.util.function.Consumer<String> consumer;
        public SimpleTextWatcher(java.util.function.Consumer<String> consumer) { this.consumer = consumer; }
        @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
        @Override public void afterTextChanged(android.text.Editable s) { consumer.accept(s.toString()); }
    }

    private void saveBillToBackend() {
        // Collect data from EditTexts
        String billNo = tvBillNo.getText().toString();
        String customerName = etCustomerName.getText().toString();
        String customerPhone = etCustomerPhone.getText().toString();
        String customerAddress = etCustomerAddress.getText().toString();
        
        if (customerName.isEmpty()) {
            Toast.makeText(this, "Please enter customer name", Toast.LENGTH_SHORT).show();
            return;
        }

        double gstAmount = (subtotal * gstPercentage) / 100;
        double grandTotal = subtotal + gstAmount - discount + loading;

        // Create Bill Object
        com.example.florra_a.models.Bill bill = new com.example.florra_a.models.Bill(
                billNo, customerName, customerPhone, customerAddress,
                subtotal, gstPercentage, gstAmount, discount, loading, grandTotal, "Unpaid",
                billItems
        );

        // Call API
        com.example.florra_a.network.ApiService apiService = com.example.florra_a.network.RetrofitClient.getApiService();
        retrofit2.Call<okhttp3.ResponseBody> call = apiService.saveBill(bill);

        call.enqueue(new retrofit2.Callback<okhttp3.ResponseBody>() {
            @Override
            public void onResponse(retrofit2.Call<okhttp3.ResponseBody> call, retrofit2.Response<okhttp3.ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(GenerateBillActivity.this, "Bill Saved Successfully", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(GenerateBillActivity.this, SavedBillsActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(GenerateBillActivity.this, "Save Failed: " + response.code(), Toast.LENGTH_SHORT).show();
                    android.util.Log.e("GenerateBill", "Error: " + response.message());
                }
            }

            @Override
            public void onFailure(retrofit2.Call<okhttp3.ResponseBody> call, Throwable t) {
                Toast.makeText(GenerateBillActivity.this, "Network Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                android.util.Log.e("GenerateBill", "Failure: " + t.getMessage());
            }
        });
    }

    private void fetchProducts() {
        ApiService apiService = RetrofitClient.getApiService();
        apiService.getProducts().enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    allProducts = response.body();
                    productSuggestions.clear();
                    numberSuggestions.clear();
                    for (Product p : allProducts) {
                        String suggestion = p.getTileName() + " (" + (p.getTileNo() != null ? p.getTileNo() : "N/A") + ")";
                        productSuggestions.add(suggestion);
                        if (p.getTileNo() != null) numberSuggestions.add(p.getTileNo());
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                // Fail silently or log
            }
        });
    }

    interface ValueCallback {
        void onChange(double value);
    }
}
