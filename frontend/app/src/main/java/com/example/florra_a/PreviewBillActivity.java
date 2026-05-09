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

public class PreviewBillActivity extends AppCompatActivity {

    // TextViews for bill details
    private TextView tvBillNo, tvDate;
    private TextView tvCustomerName, tvCustomerPhone, tvCustomerAddress;
    private TextView tvSubtotal, tvTaxAmount, tvDiscountAmount, tvGrandTotal;
    private TextView tvPreviewTime;

    // Buttons
    private Button btnShareDownload;
    private LinearLayout btnBack;

    // Items Container
    private LinearLayout llItemsContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set fullscreen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
                // Set status bar to white with dark icons
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(android.view.View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(android.graphics.Color.WHITE);
        }

        // Enable edge-to-edge
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);

        // Handle notch and status bar
        WindowInsetsControllerCompat windowInsetsController =
                WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());
        windowInsetsController.setAppearanceLightStatusBars(false);
        windowInsetsController.setAppearanceLightNavigationBars(false);

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
        btnShareDownload = findViewById(R.id.btnShareDownload);

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

        btnShareDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPermissionAndDownload();
            }
        });
    }

    private void checkPermissionAndDownload() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            // Android 10+ doesn't need WRITE_EXTERNAL_STORAGE for Downloads
            downloadBillAsPDF();
        } else {
            if (androidx.core.content.ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                androidx.core.app.ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        101);
            } else {
                downloadBillAsPDF();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @androidx.annotation.NonNull String[] permissions, @androidx.annotation.NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 101) {
            if (grantResults.length > 0 && grantResults[0] == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                downloadBillAsPDF();
            } else {
                android.widget.Toast.makeText(this, "Permission denied. Cannot save PDF.", android.widget.Toast.LENGTH_SHORT).show();
            }
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
            java.util.List<com.example.florra_a.models.BillItem> items = (java.util.List<com.example.florra_a.models.BillItem>) intent.getSerializableExtra("items");
            if (items != null && llItemsContainer != null) {
                llItemsContainer.removeAllViews();
                android.view.LayoutInflater inflater = android.view.LayoutInflater.from(this);
                
                for (com.example.florra_a.models.BillItem item : items) {
                    android.view.View itemView = inflater.inflate(R.layout.item_preview_row, llItemsContainer, false);
                    
                    ((TextView) itemView.findViewById(R.id.tvItemName)).setText(item.getItemName());
                    ((TextView) itemView.findViewById(R.id.tvItemDetails)).setText(item.getSize());
                    ((TextView) itemView.findViewById(R.id.tvItemQty)).setText(item.getQuantity() + " box");
                    ((TextView) itemView.findViewById(R.id.tvItemRate)).setText("₹" + (int)item.getRate());
                    ((TextView) itemView.findViewById(R.id.tvItemAmount)).setText("₹" + (int)item.getAmount());
                    
                    llItemsContainer.addView(itemView);
                }
            }

            // Amounts
            String subtotal = intent.getStringExtra("subtotal");
            if (subtotal != null) tvSubtotal.setText(subtotal);

            String gstAmount = intent.getStringExtra("gstAmount");
            if (gstAmount != null) tvTaxAmount.setText(gstAmount);

            String discount = intent.getStringExtra("discount");
            if (discount != null) tvDiscountAmount.setText(discount);

            String grandTotal = intent.getStringExtra("grandTotal");
            if (grandTotal != null) tvGrandTotal.setText(grandTotal);

            // Set current time for preview
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(
                    "'Preview generated on' MMM dd, yyyy 'at' hh:mm a", 
                    java.util.Locale.getDefault());
            tvPreviewTime.setText(sdf.format(new java.util.Date()));
        }
    }

    private void downloadBillAsPDF() {
        try {
            PdfDocument document = new PdfDocument();
            DisplayMetrics displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            int height = displayMetrics.heightPixels;
            int width = displayMetrics.widthPixels;

            PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(width, height, 1).create();
            PdfDocument.Page page = document.startPage(pageInfo);

            View billContent = findViewById(R.id.billContainer);

            if (billContent != null) {
                // Measure calculation
                int measureWidth = View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY);
                int measureHeight = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
                billContent.measure(measureWidth, measureHeight);
                
                // Layout
                billContent.layout(0, 0, billContent.getMeasuredWidth(), billContent.getMeasuredHeight());

                Canvas canvas = page.getCanvas();
                billContent.draw(canvas);

                Paint paint = new Paint();
                paint.setColor(Color.GRAY);
                paint.setTextSize(20);
                canvas.drawText("Generated by Florra Tiles App", 20, billContent.getMeasuredHeight() + 40, paint);

                document.finishPage(page);

                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
                String fileName = "Florra_Bill_" + timeStamp + ".pdf";

                File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                File florraDir = new File(downloadsDir, "Florra Bills");
                if (!florraDir.exists()) {
                    florraDir.mkdirs();
                }

                File file = new File(florraDir, fileName);
                FileOutputStream fos = new FileOutputStream(file);
                document.writeTo(fos);
                document.close();
                fos.close();

                android.widget.Toast.makeText(this, "PDF Saved: " + file.getAbsolutePath(), android.widget.Toast.LENGTH_LONG).show();
                openPDFFile(file);

            } else {
                android.widget.Toast.makeText(this, "Error: Bill content not found!", android.widget.Toast.LENGTH_SHORT).show();
                document.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
            android.widget.Toast.makeText(this, "Error saving PDF: " + e.getMessage(), android.widget.Toast.LENGTH_SHORT).show();
        }
    }

    private void openPDFFile(File file) {
        try {
            Uri uri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", file);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(uri, "application/pdf");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            } else {
                 android.widget.Toast.makeText(this, "No PDF viewer found", android.widget.Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
             android.widget.Toast.makeText(this, "Cannot open PDF", android.widget.Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}