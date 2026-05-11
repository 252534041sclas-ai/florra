package com.example.florra_a;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class QuotationDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(android.graphics.Color.WHITE);
        }

        setContentView(R.layout.activity_quotation_details);
        setupClickListeners();
        getIntentData();
    }

    private void setupClickListeners() {
        findViewById(R.id.btnBack).setOnClickListener(v -> onBackPressed());

        findViewById(R.id.btnHelp).setOnClickListener(v ->
            Toast.makeText(this, "Contact support for help", Toast.LENGTH_SHORT).show());

        findViewById(R.id.btnDownload).setOnClickListener(v -> downloadQuotationPDF());

        setupBottomNavigation();
    }

    private void getIntentData() {
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("enquiry_data")) {
            com.example.florra_a.models.Enquiry enquiry =
                (com.example.florra_a.models.Enquiry) intent.getSerializableExtra("enquiry_data");
            if (enquiry != null) populateData(enquiry);
        }
    }

    private void populateData(com.example.florra_a.models.Enquiry enquiry) {
        setTv(R.id.tvEnquiryNumber, "Enquiry #" + enquiry.getId());
        if (enquiry.getCreatedAt() != null)
            setTv(R.id.tvDate, enquiry.getCreatedAt().split("T")[0]);

        String rawMessage  = enquiry.getMessage();
        String productName = "Enquiry Product";
        String details     = "";
        String imageUrl    = "";

        if (rawMessage != null) {
            for (String line : rawMessage.split("\n")) {
                if (line.startsWith("Product: "))        productName = line.replace("Product: ", "").trim();
                else if (line.startsWith("Product Image: ")) imageUrl = line.replace("Product Image: ", "").trim();
                else if (line.startsWith("Details: "))   details     = line.replace("Details: ", "").trim();
            }
        }

        setTv(R.id.tvProductName, productName);
        setTv(R.id.tvDetails, details);
        findViewById(R.id.tvSize).setVisibility(View.GONE);

        if (!imageUrl.isEmpty()) {
            if (!imageUrl.startsWith("http"))
                imageUrl = com.example.florra_a.network.RetrofitClient.BASE_URL + imageUrl;
            com.bumptech.glide.Glide.with(this)
                .load(imageUrl)
                .placeholder(R.drawable.img_sample_product)
                .error(R.drawable.img_sample_product)
                .centerCrop()
                .into((android.widget.ImageView) findViewById(R.id.imgProduct));
        }

        String price    = enquiry.getQuotationPrice();
        String boxes    = enquiry.getQuotationBoxes();
        String delivery = enquiry.getQuotationDeliveryTime();
        String notes    = enquiry.getQuotationNotes();

        setTv(R.id.tvPricePerSqft, (price    != null && !price.isEmpty())    ? "₹" + price    : "-");
        setTv(R.id.tvQuantity,     (boxes    != null && !boxes.isEmpty())     ? boxes + " Boxes" : "-");
        setTv(R.id.tvDelivery,     (delivery != null && !delivery.isEmpty())  ? delivery        : "-");
        setTv(R.id.tvNote,         (notes    != null && !notes.isEmpty())     ? notes           : "No notes provided.");

        try {
            if (price != null && boxes != null) {
                double p     = Double.parseDouble(price.replaceAll("[^\\d.]", ""));
                double b     = Double.parseDouble(boxes.replaceAll("[^\\d.]", ""));
                double total = p * b * 10;
                setTv(R.id.tvTotalEstimate, String.format("₹ %,.2f", total));
            }
        } catch (Exception e) {
            setTv(R.id.tvTotalEstimate, "₹ -");
        }
    }

    // ── PDF Generation ────────────────────────────────────────────────────────
    private void downloadQuotationPDF() {
        String enquiryNo   = getTv(R.id.tvEnquiryNumber);
        String date        = getTv(R.id.tvDate);
        String productName = getTv(R.id.tvProductName);
        String details     = getTv(R.id.tvDetails);
        String price       = getTv(R.id.tvPricePerSqft);
        String quantity    = getTv(R.id.tvQuantity);
        String delivery    = getTv(R.id.tvDelivery);
        String total       = getTv(R.id.tvTotalEstimate);
        String note        = getTv(R.id.tvNote);

        android.app.ProgressDialog progress = new android.app.ProgressDialog(this);
        progress.setMessage("Generating PDF...");
        progress.setCancelable(false);
        progress.show();

        new Thread(() -> {
            try {
                android.graphics.pdf.PdfDocument pdfDoc = new android.graphics.pdf.PdfDocument();
                int W = 595, H = 842; // A4 portrait

                android.graphics.pdf.PdfDocument.Page page = pdfDoc.startPage(
                    new android.graphics.pdf.PdfDocument.PageInfo.Builder(W, H, 1).create());
                android.graphics.Canvas c = page.getCanvas();
                android.graphics.Paint p2 = new android.graphics.Paint();
                p2.setAntiAlias(true);

                // ── Header ────────────────────────────────────────────────────
                p2.setColor(android.graphics.Color.BLACK);
                c.drawRect(0, 0, W, 90, p2);

                p2.setColor(android.graphics.Color.WHITE);
                p2.setTextSize(28f);
                p2.setTypeface(android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.BOLD));
                c.drawText("FLORRA", 40, 48, p2);

                p2.setTextSize(11f);
                p2.setTypeface(android.graphics.Typeface.DEFAULT);
                c.drawText("Quotation Details", 40, 68, p2);

                p2.setTextAlign(android.graphics.Paint.Align.RIGHT);
                c.drawText(enquiryNo, W - 40, 48, p2);
                c.drawText("Date: " + date, W - 40, 66, p2);
                p2.setTextAlign(android.graphics.Paint.Align.LEFT);

                // ── Product ───────────────────────────────────────────────────
                int y = sectionHeader(c, p2, "PRODUCT", 115, W);
                p2.setColor(android.graphics.Color.parseColor("#1E293B"));
                p2.setTextSize(16f);
                p2.setTypeface(android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.BOLD));
                c.drawText(productName, 40, y, p2);
                if (!details.isEmpty()) {
                    y += 18;
                    p2.setColor(android.graphics.Color.parseColor("#64748B"));
                    p2.setTextSize(11f);
                    p2.setTypeface(android.graphics.Typeface.DEFAULT);
                    c.drawText(details, 40, y, p2);
                }

                // ── Quotation Rows ────────────────────────────────────────────
                y += 36;
                y = sectionHeader(c, p2, "QUOTATION DETAILS", y, W);

                String[][] rows = {
                    {"Price per sq.ft", price},
                    {"Quantity",        quantity},
                    {"Est. Delivery",   delivery}
                };
                for (String[] row : rows) {
                    p2.setColor(android.graphics.Color.parseColor("#64748B"));
                    p2.setTextSize(11f);
                    p2.setTypeface(android.graphics.Typeface.DEFAULT);
                    c.drawText(row[0], 40, y, p2);
                    p2.setColor(android.graphics.Color.parseColor("#1E293B"));
                    p2.setTypeface(android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.BOLD));
                    p2.setTextAlign(android.graphics.Paint.Align.RIGHT);
                    c.drawText(row[1], W - 40, y, p2);
                    p2.setTextAlign(android.graphics.Paint.Align.LEFT);
                    y += 6;
                    p2.setColor(android.graphics.Color.parseColor("#F1F5F9"));
                    p2.setStrokeWidth(0.5f);
                    c.drawLine(40, y, W - 40, y, p2);
                    y += 20;
                }

                // ── Total bar ─────────────────────────────────────────────────
                y += 8;
                p2.setColor(android.graphics.Color.BLACK);
                c.drawRect(40, y - 14, W - 40, y + 22, p2);
                p2.setColor(android.graphics.Color.WHITE);
                p2.setTextSize(13f);
                p2.setTypeface(android.graphics.Typeface.DEFAULT);
                c.drawText("Total Estimate", 56, y + 8, p2);
                p2.setTextSize(17f);
                p2.setTypeface(android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.BOLD));
                p2.setTextAlign(android.graphics.Paint.Align.RIGHT);
                c.drawText(total, W - 56, y + 8, p2);
                p2.setTextAlign(android.graphics.Paint.Align.LEFT);

                // ── Note ──────────────────────────────────────────────────────
                y += 50;
                sectionHeader(c, p2, "NOTE", y, W);
                y += 18;
                android.text.TextPaint tp = new android.text.TextPaint();
                tp.setColor(android.graphics.Color.parseColor("#475569"));
                tp.setTextSize(11f);
                android.text.StaticLayout sl = android.text.StaticLayout.Builder
                    .obtain(note, 0, note.length(), tp, W - 80)
                    .setAlignment(android.text.Layout.Alignment.ALIGN_NORMAL)
                    .build();
                c.save();
                c.translate(40, y);
                sl.draw(c);
                c.restore();

                // ── Footer ────────────────────────────────────────────────────
                p2.setColor(android.graphics.Color.parseColor("#CBD5E1"));
                p2.setStrokeWidth(1f);
                c.drawLine(40, H - 60, W - 40, H - 60, p2);
                p2.setColor(android.graphics.Color.parseColor("#94A3B8"));
                p2.setTextSize(10f);
                p2.setTextAlign(android.graphics.Paint.Align.CENTER);
                c.drawText("Florra Tiles  •  This is a computer-generated quotation.", W / 2f, H - 44, p2);
                c.drawText("Prices are estimates and subject to change based on stock availability.", W / 2f, H - 30, p2);

                pdfDoc.finishPage(page);

                // ── Save ──────────────────────────────────────────────────────
                String fileName = "Quotation_" + enquiryNo.replaceAll("[^a-zA-Z0-9]", "_") + ".pdf";
                android.net.Uri pdfUri = savePdf(pdfDoc, fileName);
                pdfDoc.close();

                final android.net.Uri finalUri = pdfUri;
                runOnUiThread(() -> {
                    progress.dismiss();
                    if (finalUri != null) {
                        Toast.makeText(this, "PDF saved to Downloads!", Toast.LENGTH_LONG).show();
                        Intent open = new Intent(Intent.ACTION_VIEW);
                        open.setDataAndType(finalUri, "application/pdf");
                        open.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        startActivity(Intent.createChooser(open, "Open PDF"));
                    } else {
                        Toast.makeText(this, "PDF saved to Downloads.", Toast.LENGTH_SHORT).show();
                    }
                });

            } catch (Exception e) {
                runOnUiThread(() -> {
                    progress.dismiss();
                    Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
        }).start();
    }

    private android.net.Uri savePdf(android.graphics.pdf.PdfDocument doc, String fileName) throws Exception {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            android.content.ContentValues cv = new android.content.ContentValues();
            cv.put(android.provider.MediaStore.Downloads.DISPLAY_NAME, fileName);
            cv.put(android.provider.MediaStore.Downloads.MIME_TYPE, "application/pdf");
            cv.put(android.provider.MediaStore.Downloads.IS_PENDING, 1);
            android.net.Uri uri = getContentResolver()
                .insert(android.provider.MediaStore.Downloads.EXTERNAL_CONTENT_URI, cv);
            if (uri != null) {
                try (java.io.OutputStream out = getContentResolver().openOutputStream(uri)) {
                    doc.writeTo(out);
                }
                cv.clear();
                cv.put(android.provider.MediaStore.Downloads.IS_PENDING, 0);
                getContentResolver().update(uri, cv, null, null);
            }
            return uri;
        } else {
            java.io.File dir = android.os.Environment
                .getExternalStoragePublicDirectory(android.os.Environment.DIRECTORY_DOWNLOADS);
            if (!dir.exists()) dir.mkdirs();
            java.io.File file = new java.io.File(dir, fileName);
            try (java.io.FileOutputStream fos = new java.io.FileOutputStream(file)) {
                doc.writeTo(fos);
            }
            return androidx.core.content.FileProvider.getUriForFile(
                this, getPackageName() + ".provider", file);
        }
    }

    /** Draws a section header with a teal label and grey divider, returns new y. */
    private int sectionHeader(android.graphics.Canvas c, android.graphics.Paint p,
                               String title, int y, int W) {
        p.setColor(android.graphics.Color.parseColor("#0F4C4C"));
        p.setTextSize(13f);
        p.setTypeface(android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.BOLD));
        c.drawText(title, 40, y, p);
        y += 6;
        p.setColor(android.graphics.Color.parseColor("#E2E8F0"));
        p.setStrokeWidth(1f);
        c.drawLine(40, y, W - 40, y, p);
        return y + 16;
    }

    // ── Helpers ───────────────────────────────────────────────────────────────
    private void setTv(int id, String text) {
        android.widget.TextView tv = findViewById(id);
        if (tv != null) tv.setText(text);
    }
    private String getTv(int id) {
        android.widget.TextView tv = findViewById(id);
        return tv != null ? tv.getText().toString() : "";
    }

    // ── Navigation ────────────────────────────────────────────────────────────
    private void setupBottomNavigation() {
        findViewById(R.id.btnNavHome).setOnClickListener(v -> {
            startActivity(new Intent(this, CustomerHomeActivity.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        });
        findViewById(R.id.btnNavCatalog).setOnClickListener(v -> {
            startActivity(new Intent(this, CatalogActivity.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        });
        findViewById(R.id.btnNavAccount).setOnClickListener(v -> {
            startActivity(new Intent(this, CustomerAccountActivity.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}