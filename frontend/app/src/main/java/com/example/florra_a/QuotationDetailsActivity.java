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
        
        String dateString = "-";
        if (enquiry.getCreatedAt() != null) {
            String[] parts = enquiry.getCreatedAt().split("T");
            dateString = parts[0];
            if (parts.length > 1) {
                String timePart = parts[1].substring(0, 5); // HH:mm
                dateString += " • " + timePart;
            }
        }
        setTv(R.id.tvDate, dateString);
        setTv(R.id.tvTimelineSub1, dateString);

        String rawMessage  = enquiry.getMessage();
        String productName = "Enquiry Product";
        String category    = "-";
        String details     = "";
        String imageUrl    = "";

        if (rawMessage != null) {
            for (String line : rawMessage.split("\n")) {
                if (line.startsWith("Product: "))        productName = line.replace("Product: ", "").trim();
                else if (line.startsWith("Category: "))   category    = line.replace("Category: ", "").trim();
                else if (line.startsWith("Product Image: ")) imageUrl = line.replace("Product Image: ", "").trim();
                else if (line.startsWith("Details: "))   details     = line.replace("Details: ", "").trim();
            }
        }

        setTv(R.id.tvProductName, productName);
        setTv(R.id.tvProductCategory, "Category: " + category);
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

        String status = enquiry.getStatus();
        if (status == null) status = "Pending";

        // Dynamic Status Badge
        android.widget.TextView tvStatus = findViewById(R.id.tvStatusDetails);
        android.widget.ImageView imgStatus = findViewById(R.id.imgStatusDetails);
        android.view.View badgeDetails = findViewById(R.id.statusBadgeDetails);

        tvStatus.setText(status);
        if (status.equalsIgnoreCase("Approved") || status.equalsIgnoreCase("quoted") || status.equalsIgnoreCase("responded")) {
            badgeDetails.setBackgroundResource(R.drawable.bg_status_approved);
            tvStatus.setTextColor(android.graphics.Color.WHITE);
            imgStatus.setImageResource(R.drawable.ic_verified);
            imgStatus.setColorFilter(android.graphics.Color.WHITE);
        } else if (status.equalsIgnoreCase("Rejected") || status.equalsIgnoreCase("Cancelled")) {
            badgeDetails.setBackgroundResource(R.drawable.bg_status_rejected);
            tvStatus.setTextColor(android.graphics.Color.WHITE);
            imgStatus.setImageResource(R.drawable.ic_block);
            imgStatus.setColorFilter(android.graphics.Color.WHITE);
        } else {
            badgeDetails.setBackgroundResource(R.drawable.bg_status_pending);
            tvStatus.setTextColor(getResources().getColor(R.color.slate_600));
            imgStatus.setImageResource(R.drawable.ic_pending);
            imgStatus.setColorFilter(getResources().getColor(R.color.slate_600));
        }

        // Show/Hide Response details based on status
        boolean isPending = status.equalsIgnoreCase("Pending") || status.equalsIgnoreCase("new");
        String price    = enquiry.getQuotationPrice();
        String boxes    = enquiry.getQuotationBoxes();
        String delivery = enquiry.getQuotationDeliveryTime();
        String notes    = enquiry.getQuotationNotes();

        if (isPending || price == null || price.isEmpty()) {
            findViewById(R.id.layoutAdminResponseTitle).setVisibility(View.GONE);
            findViewById(R.id.cardAdminResponse).setVisibility(View.GONE);
            findViewById(R.id.btnDownload).setVisibility(View.GONE);
            findViewById(R.id.cardPendingResponse).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.layoutAdminResponseTitle).setVisibility(View.VISIBLE);
            findViewById(R.id.cardAdminResponse).setVisibility(View.VISIBLE);
            findViewById(R.id.btnDownload).setVisibility(View.VISIBLE);
            findViewById(R.id.cardPendingResponse).setVisibility(View.GONE);

            setTv(R.id.tvPricePerSqft, "₹" + price);
            setTv(R.id.tvQuantity,     boxes + " Boxes");
            setTv(R.id.tvDelivery,     (delivery != null && !delivery.isEmpty()) ? delivery : "-");
            setTv(R.id.tvNote,         (notes != null && !notes.isEmpty()) ? notes : "No notes provided.");

            String adminName = enquiry.getAdminName();
            if (adminName == null || adminName.trim().isEmpty()) {
                adminName = "Showroom Manager";
            }
            setTv(R.id.tvAdminName, adminName);

            // Extract avatar initials
            String initials = "SM";
            try {
                String[] nameParts = adminName.trim().split("\\s+");
                if (nameParts.length > 0 && !nameParts[0].isEmpty()) {
                    String firstInitial = String.valueOf(nameParts[0].charAt(0)).toUpperCase();
                    String secondInitial = "";
                    if (nameParts.length > 1 && !nameParts[1].isEmpty()) {
                        secondInitial = String.valueOf(nameParts[1].charAt(0)).toUpperCase();
                    }
                    initials = firstInitial + secondInitial;
                }
            } catch (Exception e) {
                initials = "SM";
            }
            setTv(R.id.tvAdminInitials, initials);

            try {
                double p     = Double.parseDouble(price.replaceAll("[^\\d.]", ""));
                double b     = Double.parseDouble(boxes.replaceAll("[^\\d.]", ""));
                double total = p * b * 10;
                setTv(R.id.tvTotalEstimate, String.format("₹ %,.2f", total));
            } catch (Exception e) {
                setTv(R.id.tvTotalEstimate, "₹ -");
            }
        }

        // Dynamic History Timeline Visuals
        android.view.View line1 = findViewById(R.id.lineTimeline1);
        android.view.View line2 = findViewById(R.id.lineTimeline2);
        android.view.View bgStep2 = findViewById(R.id.bgTimeline2);
        android.widget.ImageView imgStep2 = findViewById(R.id.imgTimeline2);
        android.widget.TextView tvTitle2 = findViewById(R.id.tvTimelineTitle2);
        android.widget.TextView tvSub2 = findViewById(R.id.tvTimelineSub2);

        android.view.View bgStep3 = findViewById(R.id.bgTimeline3);
        android.widget.ImageView imgStep3 = findViewById(R.id.imgTimeline3);
        android.widget.TextView tvTitle3 = findViewById(R.id.tvTimelineTitle3);
        android.widget.TextView tvSub3 = findViewById(R.id.tvTimelineSub3);

        if (!isPending) {
            // Reviewed by Admin - Done
            line1.setBackgroundColor(android.graphics.Color.parseColor("#10B981")); // green_600
            bgStep2.setBackgroundResource(R.drawable.bg_timeline_green);
            imgStep2.setImageResource(R.drawable.ic_check);
            imgStep2.setColorFilter(android.graphics.Color.parseColor("#10B981"));
            tvTitle2.setTextColor(getResources().getColor(R.color.slate_900));
            tvSub2.setText("Admin review complete");
            tvSub2.setTextColor(getResources().getColor(R.color.slate_500));

            // Line 2 - Done
            line2.setBackgroundColor(android.graphics.Color.parseColor("#10B981"));

            if (status.equalsIgnoreCase("Approved") || status.equalsIgnoreCase("quoted") || status.equalsIgnoreCase("responded")) {
                // Response Received - Done
                bgStep3.setBackgroundResource(R.drawable.bg_timeline_primary);
                imgStep3.setImageResource(R.drawable.ic_mark_email_read);
                imgStep3.setColorFilter(android.graphics.Color.WHITE);
                tvTitle3.setText("Response Received");
                tvTitle3.setTextColor(getResources().getColor(R.color.primary));
                tvSub3.setText("Quotation ready to download");
                tvSub3.setTextColor(getResources().getColor(R.color.slate_500));
            } else {
                // Cancelled / Rejected - Done
                bgStep3.setBackgroundResource(R.drawable.bg_grey_circle);
                imgStep3.setImageResource(R.drawable.ic_block);
                imgStep3.setColorFilter(getResources().getColor(R.color.slate_400));
                tvTitle3.setText("Quotation Cancelled");
                tvTitle3.setTextColor(getResources().getColor(R.color.slate_500));
                tvSub3.setText("Cancelled by admin");
                tvSub3.setTextColor(getResources().getColor(R.color.slate_400));
            }
        } else {
            // Awaiting Admin Review - Incomplete
            line1.setBackgroundColor(android.graphics.Color.parseColor("#E5E7EB")); // gray_200
            bgStep2.setBackgroundResource(R.drawable.bg_grey_circle);
            imgStep2.setImageResource(R.drawable.ic_pending);
            imgStep2.setColorFilter(getResources().getColor(R.color.slate_400));
            tvTitle2.setTextColor(getResources().getColor(R.color.slate_400));
            tvSub2.setText("Awaiting admin review");
            tvSub2.setTextColor(getResources().getColor(R.color.slate_400));

            // Line 2 - Incomplete
            line2.setBackgroundColor(android.graphics.Color.parseColor("#E5E7EB"));

            // Response Received - Incomplete
            bgStep3.setBackgroundResource(R.drawable.bg_grey_circle);
            imgStep3.setImageResource(R.drawable.ic_pending);
            imgStep3.setColorFilter(getResources().getColor(R.color.slate_400));
            tvTitle3.setText("Response Received");
            tvTitle3.setTextColor(getResources().getColor(R.color.slate_400));
            tvSub3.setText("Waiting for price details");
            tvSub3.setTextColor(getResources().getColor(R.color.slate_400));
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