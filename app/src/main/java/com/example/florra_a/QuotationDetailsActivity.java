package com.example.florra_a;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class QuotationDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set fullscreen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_quotation_details);

        setupAllClickListeners();

        // You can get quotation data from intent here
        Intent intent = getIntent();
        if (intent != null) {
            String quotationId = intent.getStringExtra("quotation_id");
            if (quotationId != null) {
                TextView tvQuotationId = findViewById(R.id.tvQuotationId);
                tvQuotationId.setText(quotationId);
            }
        }
    }

    private void setupAllClickListeners() {
        // Back button
        LinearLayout btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        }

        // Download PDF button
        LinearLayout btnDownloadPdf = findViewById(R.id.btnDownloadPdf);
        if (btnDownloadPdf != null) {
            btnDownloadPdf.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    downloadPdfInvoice();
                }
            });
        }

        // Share button
        LinearLayout btnShare = findViewById(R.id.btnShare);
        if (btnShare != null) {
            btnShare.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    shareQuotation();
                }
            });
        }

        // Item click listeners (optional)
        setupItemClickListeners();
    }

    private void setupItemClickListeners() {
        LinearLayout item1 = findViewById(R.id.item1);
        if (item1 != null) {
            item1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Handle item click if needed
                }
            });
        }

        LinearLayout item2 = findViewById(R.id.item2);
        if (item2 != null) {
            item2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Handle item click if needed
                }
            });
        }

        LinearLayout item3 = findViewById(R.id.item3);
        if (item3 != null) {
            item3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Handle item click if needed
                }
            });
        }
    }

    private void downloadPdfInvoice() {
        Toast.makeText(this, "Downloading PDF ", Toast.LENGTH_SHORT).show();
        // Here you would implement PDF download functionality
    }

    private void shareQuotation() {
        Toast.makeText(this, "Sharing quotation...", Toast.LENGTH_SHORT).show();

        // Create sharing intent
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Quotation #QT-8832");
        shareIntent.putExtra(Intent.EXTRA_TEXT, "Check out this quotation from Florra Tiles: \n\n" +
                "Quotation ID: #QT-8832\n" +
                "Total Amount: $1,562.00\n" +
                "Valid until: Nov 24, 2023");

        startActivity(Intent.createChooser(shareIntent, "Share Quotation"));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}