package com.example.florra_a;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import androidx.core.widget.NestedScrollView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ShareCompat;
import androidx.core.content.FileProvider;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ViewBillActivity extends AppCompatActivity {

    // TextViews
    private android.widget.TextView tvBillNumber, tvDate, tvStatus;
    private android.widget.TextView tvCustomerName, tvMobile, tvAddress;
    private android.widget.TextView tvSubtotal, tvGST, tvDiscount, tvGrandTotal;
    private android.widget.LinearLayout llItemsContainer;

    // UI Components
    private ImageButton btnBack;
    private Button btnShare, btnDownload;
    private NestedScrollView scrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set status bar to white with dark icons
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(android.view.View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(android.graphics.Color.WHITE);
        }

        // Enable edge-to-edge
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);

        setContentView(R.layout.activity_view_bill);
        
        initializeViews();
        setupClickListeners();

        // Get data from intent
        com.example.florra_a.models.Bill bill = (com.example.florra_a.models.Bill) getIntent().getSerializableExtra("bill");
        if (bill != null) {
            populateBillData(bill);
        }
    }

    private void initializeViews() {
        // Header buttons
        btnBack = findViewById(R.id.btnBack);

        // Action buttons
        btnShare = findViewById(R.id.btnShare);
        btnDownload = findViewById(R.id.btnDownload);

        // ScrollView
        scrollView = findViewById(R.id.scrollView);

        // Text Views
        tvBillNumber = findViewById(R.id.tvBillNumber);
        tvDate = findViewById(R.id.tvDate);
        tvStatus = findViewById(R.id.tvStatus);

        tvCustomerName = findViewById(R.id.tvCustomerName);
        tvMobile = findViewById(R.id.tvMobile); // ensure id exists in XML or update XML
        tvAddress = findViewById(R.id.tvAddress);

        tvSubtotal = findViewById(R.id.tvSubtotal);
        tvGST = findViewById(R.id.tvGST);
        tvDiscount = findViewById(R.id.tvDiscount);
        tvGrandTotal = findViewById(R.id.tvGrandTotal);

        llItemsContainer = findViewById(R.id.llItemsContainer);
    }
    
    private void populateBillData(com.example.florra_a.models.Bill bill) {
        tvBillNumber.setText(bill.getBillNo());
        tvDate.setText(bill.getCreatedAt());
        tvStatus.setText(bill.getStatus());
        
        tvCustomerName.setText(bill.getCustomerName());
        tvMobile.setText(bill.getCustomerPhone());
        tvAddress.setText(bill.getCustomerAddress());
        
        tvSubtotal.setText("₹" + (int)bill.getSubtotal());
        tvGST.setText("₹" + (int)bill.getGstAmount());
        tvDiscount.setText("-₹" + (int)bill.getDiscount());
        tvGrandTotal.setText("₹" + (int)bill.getGrandTotal());

        // Populate Items
        if (bill.getItems() != null && llItemsContainer != null) {
            llItemsContainer.removeAllViews();
            android.view.LayoutInflater inflater = android.view.LayoutInflater.from(this);
            
            for (com.example.florra_a.models.BillItem item : bill.getItems()) {
                // Reusing item_preview_row or similar. item_preview_row matches the structure we need
                // We might need to adjust styles but let's reuse for now if compatible, or inflate a simple one
                // Actually earlier XML had specific structure. Let's use item_preview_row for consistency
                // But wait, ViewBill has a slightly different look (white background). 
                // Let's stick to the XML structure we just replaced. 
                // We need to inflate a row that looks like the ones we deleted.
                
                // Let's use item_bill_row.xml logic but simpler, or better: reuse item_preview_row but it might have different styling.
                // Best for now: Use item_preview_row as it is cleaner.
                
                View itemView = inflater.inflate(R.layout.item_preview_row, llItemsContainer, false);
                ((android.widget.TextView) itemView.findViewById(R.id.tvItemName)).setText(item.getItemName());
                ((android.widget.TextView) itemView.findViewById(R.id.tvItemDetails)).setText(item.getSize());
                ((android.widget.TextView) itemView.findViewById(R.id.tvItemQty)).setText(String.valueOf(item.getQuantity())); 
                ((android.widget.TextView) itemView.findViewById(R.id.tvItemRate)).setText("₹" + (int)item.getRate());
                ((android.widget.TextView) itemView.findViewById(R.id.tvItemAmount)).setText("₹" + (int)item.getAmount());
                
                // Adjust text colors if needed to match ViewBill style (black text)
                // item_preview_row uses default or mostly black/gray. Should be fine.
                
                llItemsContainer.addView(itemView);
            }
        }
    }

    private void setupClickListeners() {
        // Back button
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // Share button
        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareBill();
            }
        });

        // Download button
        btnDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPermissionAndDownload();
            }
        });
    }

    private void checkPermissionAndDownload() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
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
                Toast.makeText(this, "Permission denied. Cannot save PDF.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void shareBill() {
        View billContent = findViewById(R.id.billContainer);
        if (billContent == null) {
            Toast.makeText(this, "Could not find bill content", Toast.LENGTH_SHORT).show();
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
            File newFile = new File(cachePath, "Florra_Bill_Shared.pdf"); // Overwrite
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
            Toast.makeText(this, "Error preparing PDF: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void downloadBillAsPDF() {
        // Create PDF
        PdfDocument document = new PdfDocument();

        // Get screen dimensions
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;



        // Create a bitmap of the bill content
        View billContent = findViewById(R.id.billContainer); 

        if (billContent != null) {
            Toast.makeText(this, "Generating PDF...", Toast.LENGTH_SHORT).show();

            int displayHeight = displayMetrics.heightPixels;
            int displayWidth = displayMetrics.widthPixels;
            
            // 1. Measure the view explicitly
            // Use width from display metrics to ensure it fits the page
            int measureWidth = View.MeasureSpec.makeMeasureSpec(displayWidth, View.MeasureSpec.EXACTLY);
            // Allow header to grow as needed
            int measureHeight = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
            billContent.measure(measureWidth, measureHeight);
            
            // 2. Get the measured dimensions
            int totalHeight = billContent.getMeasuredHeight();
            int totalWidth = billContent.getMeasuredWidth();
            
            // 3. Layout the view
            billContent.layout(0, 0, totalWidth, totalHeight);
            
            // 4. Start the page with the CORRECT dimensions
            // Create PageInfo using the measured dimensions
            PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(totalWidth, totalHeight, 1).create();
            PdfDocument.Page page = document.startPage(pageInfo);

            // 5. Draw
            Canvas canvas = page.getCanvas();
            canvas.drawColor(Color.WHITE); 
            billContent.draw(canvas);

            // Add metadata
            Paint paint = new Paint();
            paint.setColor(Color.GRAY);
            paint.setTextSize(20);
            canvas.drawText("Generated by Florra Tiles App", 20, totalHeight - 20, paint);

            document.finishPage(page);

            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            String fileName = "Florra_Bill_" + timeStamp + ".pdf";

            try {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                    // Use MediaStore for Android 10+
                    savePdfToMediaStore(document, fileName);
                } else {
                    // Legacy method for older versions
                    savePdfToExternalStorage(document, fileName);
                }
            } catch (IOException e) {
                Toast.makeText(this, "Error saving PDF: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            } finally {
                document.close();
            }

        } else {
            Toast.makeText(this, "Could not identify bill content", Toast.LENGTH_SHORT).show();
            document.close();
        }
    }

    private void savePdfToMediaStore(PdfDocument document, String fileName) throws IOException {
        android.content.ContentValues values = new android.content.ContentValues();
        values.put(android.provider.MediaStore.MediaColumns.DISPLAY_NAME, fileName);
        values.put(android.provider.MediaStore.MediaColumns.MIME_TYPE, "application/pdf");
        values.put(android.provider.MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS + "/Florra Bills");

        Uri uri = getContentResolver().insert(android.provider.MediaStore.Files.getContentUri("external"), values);
        if (uri != null) {
            java.io.OutputStream outputStream = getContentResolver().openOutputStream(uri);
            if (outputStream != null) {
                document.writeTo(outputStream);
                outputStream.close();
                Toast.makeText(this, "Saved to Downloads", Toast.LENGTH_LONG).show();
                openPDFFromUri(uri);
            }
        }
    }

    private void savePdfToExternalStorage(PdfDocument document, String fileName) throws IOException {
        File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File florraDir = new File(downloadsDir, "Florra Bills");
        if (!florraDir.exists()) florraDir.mkdirs();

        File file = new File(florraDir, fileName);
        FileOutputStream fos = new FileOutputStream(file);
        document.writeTo(fos);
        fos.close();
        
        Toast.makeText(this, "Saved to: " + file.getAbsolutePath(), Toast.LENGTH_LONG).show();
        openPDFFile(file);
    }
    
    private void openPDFFromUri(Uri uri) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, "application/pdf");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        try {
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, "PDF Viewer not found", Toast.LENGTH_SHORT).show();
        }
    }

    private void openPDFFile(File file) {
        try {
            // Create URI using FileProvider
            Uri uri = FileProvider.getUriForFile(this,
                    getApplicationContext().getPackageName() + ".provider",
                    file);

            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(uri, "application/pdf");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

            // Check if there's an app to open PDF
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            } else {
                Toast.makeText(this, "Install a PDF viewer to open the file", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Cannot open PDF file", Toast.LENGTH_SHORT).show();
        }
    }

    // Simple text-based download (alternative)
    private void downloadBillAsText() {
        try {
            // Create bill text content
            String billContent = createBillTextContent();

            // Create file name
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            String fileName = "Florra_Bill_" + timeStamp + ".txt";

            // Save to downloads
            File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            File file = new File(downloadsDir, fileName);

            FileOutputStream fos = new FileOutputStream(file);
            fos.write(billContent.getBytes());
            fos.close();

            Toast.makeText(this, "Bill saved to Downloads folder", Toast.LENGTH_LONG).show();

        } catch (Exception e) {
            Toast.makeText(this, "Error saving bill: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private String createBillTextContent() {
        return "==========================================\n" +
                "         FLORRA TILE STUDIO\n" +
                "       Premium Ceramics & Stones\n" +
                "==========================================\n\n" +
                "Bill Number: INV-2023-001\n" +
                "Date: Oct 24, 2023\n" +
                "Status: PAID\n\n" +
                "Customer Details:\n" +
                "-----------------\n" +
                "Name: Rahul Sharma\n" +
                "Mobile: +91 98765 43210\n" +
                "Address: 123, Green Avenue, City Center\n\n" +
                "Items Summary:\n" +
                "--------------\n" +
                "Marble White (600x600mm • Glossy)  10 x $120 = $1200\n" +
                "Onyx Black (300x600mm • Matte)      5 x $160 = $800\n\n" +
                "Payment Summary:\n" +
                "----------------\n" +
                "Subtotal:                 $2,000.00\n" +
                "GST (18%):                $360.00\n" +
                "Discount:                 $0.00\n" +
                "------------------------------------\n" +
                "Grand Total:              $2,360.00\n\n" +
                "Thank you for doing business with us!\n\n" +
                "Authorized Signatory\n" +
                "Florra Tiles Approved\n" +
                "==========================================\n" +
                "Generated via Florra Tiles App\n" +
                new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(new Date());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}