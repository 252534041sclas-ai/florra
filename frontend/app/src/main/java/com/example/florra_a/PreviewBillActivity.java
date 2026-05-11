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
    private Button btnShare, btnBackToEdit;
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
        btnBackToEdit = findViewById(R.id.btnBackToEdit);
        btnShare = findViewById(R.id.btnShare);

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

        btnBackToEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareBill();
            }
        });
    }

    private void shareBill() {
        View billContent = findViewById(R.id.billContainer);
        if (billContent == null) {
            android.widget.Toast.makeText(this, "Could not find bill content", android.widget.Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            // 1. Create PDF Document
            PdfDocument document = new PdfDocument();

            // Measure full height
            int measureWidth = View.MeasureSpec.makeMeasureSpec(billContent.getWidth(), View.MeasureSpec.EXACTLY);
            int measureHeight = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
            billContent.measure(measureWidth, measureHeight);
            int totalHeight = billContent.getMeasuredHeight();
            int totalWidth = billContent.getMeasuredWidth();

            // Layout
            billContent.layout(0, 0, totalWidth, totalHeight);

            // Create Page
            PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(totalWidth, totalHeight, 1).create();
            PdfDocument.Page page = document.startPage(pageInfo);

            // Draw
            Canvas canvas = page.getCanvas();
            canvas.drawColor(Color.WHITE);
            billContent.draw(canvas);
            
            // Add metadata
            Paint paint = new Paint();
            paint.setColor(Color.GRAY);
            paint.setTextSize(20);
            canvas.drawText("Generated by Florra Tiles App", 20, totalHeight - 20, paint);

            document.finishPage(page);

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
            java.util.List<com.example.florra_a.models.BillItem> items = (java.util.List<com.example.florra_a.models.BillItem>) intent.getSerializableExtra("items");
            if (items != null && llItemsContainer != null) {
                llItemsContainer.removeAllViews();
                android.view.LayoutInflater inflater = android.view.LayoutInflater.from(this);
                
                for (com.example.florra_a.models.BillItem item : items) {
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}