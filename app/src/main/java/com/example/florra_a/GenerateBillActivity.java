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

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import org.json.JSONObject;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class GenerateBillActivity extends AppCompatActivity {

    // TextViews
    private TextView tvCustomerName, tvCustomerPhone, tvCustomerAddress;
    private TextView tvBillNo, tvDate;
    private TextView tvSubtotal, tvGSTAmount, tvDiscountAmount, tvLoadingAmount, tvGrandTotal;

    // EditTexts
    private EditText etGST, etDiscount, etLoading;

    // Buttons
    private Button btnBack, btnPreview, btnSaveBill;

    // Calculation variables
    private double subtotal = 47000.0;
    private double gstPercentage = 18.0;
    private double discount = 1000.0;
    private double loading = 500.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        WindowInsetsControllerCompat controller =
                WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());
        controller.setAppearanceLightStatusBars(false);

        setContentView(R.layout.activity_generate_bill);

        initViews();
        setListeners();
        calculateTotals();
    }

    private void initViews() {
        tvCustomerName = findViewById(R.id.tvCustomerName);
        tvCustomerPhone = findViewById(R.id.tvCustomerPhone);
        tvCustomerAddress = findViewById(R.id.tvCustomerAddress);

        tvBillNo = findViewById(R.id.tvBillNo);
        tvDate = findViewById(R.id.tvDate);

        tvSubtotal = findViewById(R.id.tvSubtotal);
        tvGSTAmount = findViewById(R.id.tvGSTAmount);
        tvDiscountAmount = findViewById(R.id.tvDiscountAmount);
        tvLoadingAmount = findViewById(R.id.tvLoadingAmount);
        tvGrandTotal = findViewById(R.id.tvGrandTotal);

        etGST = findViewById(R.id.etGST);
        etDiscount = findViewById(R.id.etDiscount);
        etLoading = findViewById(R.id.etLoading);

        btnBack = findViewById(R.id.btnBack);
        btnPreview = findViewById(R.id.btnPreview);
        btnSaveBill = findViewById(R.id.btnSaveBill);

        // Dummy initial data
        tvCustomerName.setText("Rahul Sharma");
        tvCustomerPhone.setText("9876543210");
        tvCustomerAddress.setText("Chennai");
        tvBillNo.setText("FL-" + System.currentTimeMillis());
        tvDate.setText("Today");
    }

    private void setListeners() {

        btnBack.setOnClickListener(v -> onBackPressed());

        btnSaveBill.setOnClickListener(v -> confirmSave());

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
        double gstAmount = (subtotal * gstPercentage) / 100;
        double grandTotal = subtotal + gstAmount - discount + loading;

        tvSubtotal.setText("₹" + (int) subtotal);
        tvGSTAmount.setText("+ ₹" + (int) gstAmount);
        tvDiscountAmount.setText("- ₹" + (int) discount);
        tvLoadingAmount.setText("+ ₹" + (int) loading);
        tvGrandTotal.setText("₹" + (int) grandTotal);
    }

    private void confirmSave() {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Save Bill")
                .setMessage("Are you sure?")
                .setPositiveButton("Save", (d, w) -> saveBillToBackend())
                .setNegativeButton("Cancel", null)
                .show();
    }

    // 🔥 BACKEND INTEGRATION (FINAL)
    private void saveBillToBackend() {
        new Thread(() -> {
            try {
                URL url = new URL("http://10.0.2.2:8000/api/bills/");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);

                JSONObject json = new JSONObject();
                json.put("bill_no", tvBillNo.getText().toString());
                json.put("customer_name", tvCustomerName.getText().toString());
                json.put("customer_phone", tvCustomerPhone.getText().toString());
                json.put("customer_address", tvCustomerAddress.getText().toString());

                json.put("subtotal", subtotal);
                json.put("gst_percentage", gstPercentage);
                json.put("gst_amount", (subtotal * gstPercentage) / 100);
                json.put("discount", discount);
                json.put("loading", loading);

                double grandTotal =
                        subtotal + ((subtotal * gstPercentage) / 100) - discount + loading;

                json.put("grand_total", grandTotal);
                json.put("status", "Unpaid");

                OutputStream os = conn.getOutputStream();
                os.write(json.toString().getBytes());
                os.close();

                int code = conn.getResponseCode();

                runOnUiThread(() -> {
                    if (code == 201) {
                        Toast.makeText(this, "Bill Saved", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(this, SavedBillsActivity.class));
                        finish();
                    } else {
                        Toast.makeText(this, "Save Failed", Toast.LENGTH_SHORT).show();
                    }
                });

            } catch (Exception e) {
                runOnUiThread(() ->
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show()
                );
            }
        }).start();
    }

    interface ValueCallback {
        void onChange(double value);
    }
}
