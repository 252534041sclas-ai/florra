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
import android.widget.ScrollView;
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

    // UI Components
    private ImageButton btnBack;
    private Button btnShare, btnDownload;
    private ScrollView scrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set fullscreen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Enable edge-to-edge
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);

        // Handle notch and status bar
        WindowInsetsControllerCompat windowInsetsController =
                WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());
        windowInsetsController.setAppearanceLightStatusBars(false);
        windowInsetsController.setAppearanceLightNavigationBars(false);

        setContentView(R.layout.activity_view_bill);

        // Get data from intent (if passed from SavedBillsActivity)
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String billNumber = extras.getString("billNumber", "INV-2023-001");
            String customerName = extras.getString("customerName", "Rahul Sharma");
            String amount = extras.getString("amount", "$2,360.00");
            String date = extras.getString("date", "Oct 24, 2023");
            String status = extras.getString("status", "Paid");

            // Update TextViews with data
            // You need to initialize these TextViews first
        }

        // Initialize UI components
        initializeViews();

        // Setup click listeners
        setupClickListeners();
    }

    private void initializeViews() {
        // Header buttons
        btnBack = findViewById(R.id.btnBack);

        // Action buttons
        btnShare = findViewById(R.id.btnShare);
        btnDownload = findViewById(R.id.btnDownload);

        // ScrollView for capturing screenshot
        scrollView = findViewById(R.id.scrollView); // You need to add id to your NestedScrollView
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
                downloadBillAsPDF();
            }
        });
    }

    private void shareBill() {
        try {
            // Create bill details text
            StringBuilder billDetails = new StringBuilder();
            billDetails.append("🌿 *Florra Tile Studio Bill* 🌿\n\n");
            billDetails.append("📋 *Bill Number:* INV-2023-001\n");
            billDetails.append("📅 *Date:* Oct 24, 2023\n");
            billDetails.append("👤 *Customer:* Rahul Sharma\n");
            billDetails.append("📱 *Mobile:* +91 98765 43210\n");
            billDetails.append("📍 *Address:* 123, Green Avenue, City Center\n\n");
            billDetails.append("🛒 *Items Purchased:*\n");
            billDetails.append("   • Marble White (10 x $120) = $1200\n");
            billDetails.append("   • Onyx Black (5 x $160) = $800\n\n");
            billDetails.append("💰 *Payment Summary:*\n");
            billDetails.append("   Subtotal: $2,000.00\n");
            billDetails.append("   GST (18%): $360.00\n");
            billDetails.append("   Discount: $0.00\n");
            billDetails.append("   ------------------\n");
            billDetails.append("   *Grand Total: $2,360.00*\n\n");
            billDetails.append("✅ *Status:* Paid\n");
            billDetails.append("🎯 *Thank you for your business!*\n\n");
            billDetails.append("Generated via Florra Tiles App");

            // Create share intent
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Florra Bill - INV-2023-001");
            shareIntent.putExtra(Intent.EXTRA_TEXT, billDetails.toString());

            // Start share activity
            startActivity(Intent.createChooser(shareIntent, "Share Bill Details"));

            Toast.makeText(this, "Sharing bill details...", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            Toast.makeText(this, "Error sharing bill: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void downloadBillAsPDF() {
        try {
            // Create PDF
            PdfDocument document = new PdfDocument();

            // Get screen dimensions
            DisplayMetrics displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            int height = displayMetrics.heightPixels;
            int width = displayMetrics.widthPixels;

            // Create a PageInfo
            PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(width, height, 1).create();

            // Start a page
            PdfDocument.Page page = document.startPage(pageInfo);

            // Create a bitmap of the bill content
            View billContent = findViewById(R.id.billContainer); // You need to add id to your CardView

            if (billContent != null) {
                // Measure and layout the view
                billContent.measure(View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY),
                        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
                billContent.layout(0, 0, billContent.getMeasuredWidth(), billContent.getMeasuredHeight());

                // Draw the view on canvas
                Canvas canvas = page.getCanvas();
                billContent.draw(canvas);

                // Add some metadata
                Paint paint = new Paint();
                paint.setColor(Color.GRAY);
                paint.setTextSize(10);
                canvas.drawText("Generated by Florra Tiles App", 20, height - 20, paint);

                document.finishPage(page);

                // Create file name with timestamp
                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
                String fileName = "Florra_Bill_" + timeStamp + ".pdf";

                // Create downloads directory if not exists
                File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                File florraDir = new File(downloadsDir, "Florra Bills");
                if (!florraDir.exists()) {
                    florraDir.mkdirs();
                }

                // Save PDF file
                File file = new File(florraDir, fileName);
                FileOutputStream fos = new FileOutputStream(file);
                document.writeTo(fos);
                document.close();
                fos.close();

                // Show success message
                Toast.makeText(this, "Bill saved to Downloads/Florra Bills", Toast.LENGTH_LONG).show();

                // Optionally open the PDF
                openPDFFile(file);

            } else {
                Toast.makeText(this, "Could not generate PDF", Toast.LENGTH_SHORT).show();
            }

        } catch (IOException e) {
            Toast.makeText(this, "Error saving PDF: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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