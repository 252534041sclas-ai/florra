package com.example.florra_a;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import android.widget.LinearLayout;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Environment;
import android.util.DisplayMetrics;
import androidx.core.content.FileProvider;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import com.example.florra_a.models.Bill;
import com.example.florra_a.models.BillItem;
import com.example.florra_a.network.RetrofitClient;
import com.example.florra_a.network.ApiService;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import android.app.ProgressDialog;
import android.widget.Toast;
import android.util.Log;

public class PreviewBillActivity extends AppCompatActivity {

    // TextViews for bill details
    private TextView tvBillNo, tvDate;
    private TextView tvCustomerName, tvCustomerPhone, tvCustomerAddress;
    private TextView tvSubtotal, tvTaxAmount, tvDiscountAmount, tvGrandTotal;
    private TextView tvPreviewTime;

    // Buttons
    private Button btnShare, btnDownload;
    private LinearLayout btnBack;

    // Items Container
    private LinearLayout llItemsContainer;
    private java.util.List<BillItem> billItems;

    // Calculation storage
    private double subtotalVal, gstAmountVal, discountVal, grandTotalVal;
    private double gstPercentage, loading;
    private boolean isSaved = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set fullscreen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // Set status bar to white with dark icons
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            getWindow().setStatusBarColor(android.graphics.Color.WHITE);
            WindowCompat.setDecorFitsSystemWindows(getWindow(), true);
            WindowInsetsControllerCompat controller = new WindowInsetsControllerCompat(getWindow(), getWindow().getDecorView());
            controller.setAppearanceLightStatusBars(true);
        }

        setContentView(R.layout.activity_preview_bill);

        // Initialize views
        initializeViews();

        // Set up listeners
        setupListeners();

        // Set data from intent (if coming from GenerateBillActivity)
        setBillData();
    }

    private void initializeViews() {
        // Initialize all TextViews
        tvBillNo = findViewById(R.id.tvBillNo);
        tvDate = findViewById(R.id.tvDate);

        tvCustomerName = findViewById(R.id.tvCustomerName);
        tvCustomerPhone = findViewById(R.id.tvCustomerPhone);
        tvCustomerAddress = findViewById(R.id.tvCustomerAddress);

        // Amounts
        tvSubtotal = findViewById(R.id.tvSubtotal);
        tvTaxAmount = findViewById(R.id.tvTaxAmount);
        tvDiscountAmount = findViewById(R.id.tvDiscountAmount);
        tvGrandTotal = findViewById(R.id.tvGrandTotal);

        tvPreviewTime = findViewById(R.id.tvPreviewTime);

        // Buttons
        btnBack = findViewById(R.id.btnBack);
        btnShare = findViewById(R.id.btnShare);
        btnDownload = findViewById(R.id.btnDownload);

        // Items Container
        llItemsContainer = findViewById(R.id.llItemsContainer);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveBillThenAction(new Runnable() {
                    @Override
                    public void run() {
                        shareBill();
                    }
                });
            }
        });

        btnDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveBillThenAction(new Runnable() {
                    @Override
                    public void run() {
                        downloadBill();
                    }
                });
            }
        });
    }

    private void saveBillThenAction(final Runnable action) {
        if (isSaved) {
            action.run();
            return;
        }

        String billNo = tvBillNo.getText().toString().replace("Bill ", "");
        String customerName = tvCustomerName.getText().toString();
        String customerPhone = tvCustomerPhone.getText().toString();
        String customerAddress = tvCustomerAddress.getText().toString();

        // Refresh values from UI just in case
        subtotalVal = parseAmount(tvSubtotal.getText().toString());
        gstAmountVal = parseAmount(tvTaxAmount.getText().toString());
        discountVal = parseAmount(tvDiscountAmount.getText().toString());
        grandTotalVal = parseAmount(tvGrandTotal.getText().toString());

        Bill bill = new Bill(
                billNo, customerName, customerPhone, customerAddress,
                subtotalVal, gstPercentage, gstAmountVal, discountVal, loading, grandTotalVal, "Unpaid",
                billItems
        );

        Log.d("PreviewBill", "Saving Bill: " + billNo + " for " + customerName);
        Log.d("PreviewBill", "Items count: " + (billItems != null ? billItems.size() : 0));
        Log.d("PreviewBill", "Total: " + grandTotalVal);

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Saving Bill...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        ApiService apiService = RetrofitClient.getApiService();
        apiService.saveBill(bill).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                progressDialog.dismiss();
                if (response.isSuccessful()) {
                    isSaved = true;
                    Toast.makeText(PreviewBillActivity.this, "Bill Saved Successfully", Toast.LENGTH_SHORT).show();
                    action.run();
                } else {
                    String errorMsg = "Save Failed: " + response.code();
                    try {
                        if (response.errorBody() != null) {
                            errorMsg += " - " + response.errorBody().string();
                        }
                    } catch (Exception e) {}
                    Toast.makeText(PreviewBillActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                    Log.e("PreviewBill", errorMsg);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(PreviewBillActivity.this, "Network Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void downloadBill() {
        View billContent = findViewById(R.id.billContainer);
        if (billContent == null) return;

        try {
            PdfDocument document = createPdfFromView(billContent);
            
            String fileName = "Florra_Bill_" + System.currentTimeMillis() + ".pdf";
            File downloadFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            File file = new File(downloadFolder, fileName);
            
            FileOutputStream stream = new FileOutputStream(file);
            document.writeTo(stream);
            document.close();
            stream.close();

            android.widget.Toast.makeText(this, "Bill saved to Downloads: " + fileName, android.widget.Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            android.widget.Toast.makeText(this, "Save Failed: " + e.getMessage(), android.widget.Toast.LENGTH_SHORT).show();
        }
    }

    private PdfDocument createPdfFromView(View view) {
        PdfDocument document = new PdfDocument();

        // Measure full height
        int measureWidth = View.MeasureSpec.makeMeasureSpec(view.getWidth(), View.MeasureSpec.EXACTLY);
        int measureHeight = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        view.measure(measureWidth, measureHeight);
        int totalHeight = view.getMeasuredHeight();
        int totalWidth = view.getMeasuredWidth();

        // Layout
        view.layout(0, 0, totalWidth, totalHeight);

        // Create Page
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(totalWidth, totalHeight, 1).create();
        PdfDocument.Page page = document.startPage(pageInfo);

        // Draw
        Canvas canvas = page.getCanvas();
        canvas.drawColor(Color.WHITE);
        view.draw(canvas);

        document.finishPage(page);
        return document;
    }

    private void shareBill() {
        View billContent = findViewById(R.id.billContainer);
        if (billContent == null) {
            android.widget.Toast.makeText(this, "Could not find bill content", android.widget.Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            // 1. Create PDF Document
            PdfDocument document = createPdfFromView(billContent);

            // 2. Save to Cache
            File cachePath = new File(getCacheDir(), "documents");
            cachePath.mkdirs();
            File newFile = new File(cachePath, "Florra_Bill_Preview.pdf");
            FileOutputStream stream = new FileOutputStream(newFile);
            document.writeTo(stream);
            document.close();
            stream.close();

            // 3. Share URI
            Uri contentUri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", newFile);

            if (contentUri != null) {
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                shareIntent.setDataAndType(contentUri, "application/pdf");
                shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
                startActivity(Intent.createChooser(shareIntent, "Share Bill PDF"));
            }

        } catch (IOException e) {
            android.widget.Toast.makeText(this, "Error preparing PDF: " + e.getMessage(), android.widget.Toast.LENGTH_SHORT).show();
        }
    }

    private void setBillData() {
        Intent intent = getIntent();
        if (intent != null) {
            String billNo = intent.getStringExtra("billNo");
            if (billNo != null) tvBillNo.setText("Bill " + billNo);
            
            String date = intent.getStringExtra("date");
            if (date != null) tvDate.setText(date);

            String customerName = intent.getStringExtra("customerName");
            if (customerName != null) tvCustomerName.setText(customerName);

            String customerPhone = intent.getStringExtra("customerPhone");
            if (customerPhone != null) tvCustomerPhone.setText(customerPhone);

            String customerAddress = intent.getStringExtra("customerAddress");
            if (customerAddress != null) tvCustomerAddress.setText(customerAddress);

            // Populate Items
            billItems = (java.util.List<BillItem>) intent.getSerializableExtra("items");
            if (billItems != null && llItemsContainer != null) {
                llItemsContainer.removeAllViews();
                android.view.LayoutInflater inflater = android.view.LayoutInflater.from(this);
                
                for (BillItem item : billItems) {
                    android.view.View itemView = inflater.inflate(R.layout.item_preview_row, llItemsContainer, false);
                    
                    ((TextView) itemView.findViewById(R.id.tvItemName)).setText(item.getItemName());
                    ((TextView) itemView.findViewById(R.id.tvItemNo)).setText(item.getTileNo() != null ? item.getTileNo() : "-");
                    ((TextView) itemView.findViewById(R.id.tvItemDetails)).setText(item.getSize());
                    ((TextView) itemView.findViewById(R.id.tvItemQty)).setText(item.getQuantity() + " box");
                    ((TextView) itemView.findViewById(R.id.tvItemRate)).setText("₹" + (int)item.getRate());
                    ((TextView) itemView.findViewById(R.id.tvItemAmount)).setText("₹" + (int)item.getAmount());
                    
                    llItemsContainer.addView(itemView);
                }
            }

            // Amounts and numeric values
            gstPercentage = intent.getDoubleExtra("gstPercentage", 18.0);
            loading = intent.getDoubleExtra("loading", 0.0);

            String subtotal = intent.getStringExtra("subtotal");
            if (subtotal != null) {
                tvSubtotal.setText(subtotal);
                subtotalVal = parseAmount(subtotal);
            }

            String gstAmount = intent.getStringExtra("gstAmount");
            if (gstAmount != null) {
                tvTaxAmount.setText(gstAmount);
                gstAmountVal = parseAmount(gstAmount);
            }

            String discount = intent.getStringExtra("discount");
            if (discount != null) {
                tvDiscountAmount.setText(discount);
                discountVal = parseAmount(discount);
            }

            String grandTotal = intent.getStringExtra("grandTotal");
            if (grandTotal != null) {
                tvGrandTotal.setText(grandTotal);
                grandTotalVal = parseAmount(grandTotal);
            }

            // Set current time for preview
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(
                    "'Preview generated on' MMM dd, yyyy 'at' hh:mm a", 
                    java.util.Locale.getDefault());
            tvPreviewTime.setText(sdf.format(new java.util.Date()));
        }
    }

    private double parseAmount(String amount) {
        if (amount == null) return 0.0;
        try {
            return Double.parseDouble(amount.replaceAll("[^0-9.]", ""));
        } catch (Exception e) {
            return 0.0;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}