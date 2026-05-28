package com.example.florra_a;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.example.florra_a.models.Product;
import com.example.florra_a.network.RetrofitClient;

public class CompareActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_PICK_PRODUCT = 1001;

    // View References
    private ImageView imgProductA, imgProductB, btnCloseB;
    private TextView tvNameA, tvNameB;
    private TextView tvPriceA, tvPriceB;
    private TextView tvCategoryA, tvCategoryB;
    private TextView tvSizeA, tvSizeB;
    private TextView tvFinishA, tvFinishB;
    // New Fields
    private TextView tvModelA, tvModelB;
    private TextView tvThicknessA, tvThicknessB;
    private TextView tvCoverageA, tvCoverageB;
    private TextView tvPackingA, tvPackingB;

    private LinearLayout layoutAddProduct;
    private LinearLayout layoutProductBDetails;

    private Product productA; // Not fully used, using individual fields
    private Product productB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compare);

        // Hide ActionBar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        initializeViews();
        setupListeners();

        // Load Product A from Intent
        Intent intent = getIntent();
        if (intent != null) {
            loadProductA(intent);
            
            // Check if Product B data is also passed (from direct compare flow)
            if (intent.hasExtra("productBName")) {
                Product pB = new Product();
                pB.setTileName(intent.getStringExtra("productBName"));
                pB.setPrice(intent.getStringExtra("productBPrice"));
                pB.setCategory(intent.getStringExtra("productBCategory"));
                pB.setSize(intent.getStringExtra("productBSize"));
                pB.setFinish(intent.getStringExtra("productBFinish"));
                pB.setImage(intent.getStringExtra("productBImage"));
                pB.setTileNo(intent.getStringExtra("productBTileNo"));
                pB.setThickness(intent.getStringExtra("productBThickness"));
                pB.setCoverage(intent.getStringExtra("productBCoverage"));
                // Packing is passed separately or part of product
                
                loadProductB(pB, intent.getStringExtra("productBPacking"));
            }
        }
    }

    private void initializeViews() {
        imgProductA = findViewById(R.id.imgProductA);
        tvNameA = findViewById(R.id.tvNameA);
        tvPriceA = findViewById(R.id.tvPriceA);
        tvCategoryA = findViewById(R.id.tvCategoryA);
        tvSizeA = findViewById(R.id.tvSizeA);
        tvFinishA = findViewById(R.id.tvFinishA);
        
        tvModelA = findViewById(R.id.tvModelA);
        tvThicknessA = findViewById(R.id.tvThicknessA);
        tvCoverageA = findViewById(R.id.tvCoverageA);
        tvPackingA = findViewById(R.id.tvPackingA);

        imgProductB = findViewById(R.id.imgProductB);
        btnCloseB = findViewById(R.id.btnCloseB); // Close button for B
        tvNameB = findViewById(R.id.tvNameB);
        tvPriceB = findViewById(R.id.tvPriceB);
        tvCategoryB = findViewById(R.id.tvCategoryB);
        tvSizeB = findViewById(R.id.tvSizeB);
        tvFinishB = findViewById(R.id.tvFinishB);
        
        tvModelB = findViewById(R.id.tvModelB);
        tvThicknessB = findViewById(R.id.tvThicknessB);
        tvCoverageB = findViewById(R.id.tvCoverageB);
        tvPackingB = findViewById(R.id.tvPackingB);

        layoutAddProduct = findViewById(R.id.layoutAddProduct);
        layoutProductBDetails = findViewById(R.id.layoutProductBDetails);
    }

    private void setupListeners() {
        findViewById(R.id.btnBack).setOnClickListener(v -> onBackPressed());

        layoutAddProduct.setOnClickListener(v -> {
            Intent intent = new Intent(CompareActivity.this, CatalogActivity.class);
            intent.putExtra("IS_PICKER", true);
            startActivityForResult(intent, REQUEST_CODE_PICK_PRODUCT);
        });

        if (btnCloseB != null) {
             btnCloseB.setOnClickListener(v -> removeProductB());
        }
    }

    private void loadProductA(Intent intent) {
        String name = intent.getStringExtra("productName");
        String price = intent.getStringExtra("productPrice");
        String category = intent.getStringExtra("productCategory");
        String size = intent.getStringExtra("productSize"); 
        String finish = intent.getStringExtra("productFinish");
        String image = intent.getStringExtra("productImage");
        
        // New Fields
        String model = intent.getStringExtra("productModel");
        String thickness = intent.getStringExtra("productThickness");
        String coverage = intent.getStringExtra("productCoverage");
        String packing = intent.getStringExtra("productPacking");

        // Populate UI
        tvNameA.setText(name != null ? name : "-");
        tvPriceA.setText(price != null ? "₹" + price + " / sq.ft" : "-");
        tvCategoryA.setText(category != null ? category : "-");
        tvSizeA.setText(size != null ? size : "-");
        tvFinishA.setText(finish != null ? finish : "-");
        
        tvModelA.setText(model != null ? model : "-");
        tvThicknessA.setText(thickness != null ? thickness : "-");
        tvCoverageA.setText(coverage != null ? coverage : "-");
        tvPackingA.setText(packing != null ? packing : "-");

        loadImage(image, imgProductA);
    }

    private void loadProductB(Product p, String packing) {
        if (p == null) return;
        
        productB = p;

        tvNameB.setText(p.getTileName());
        tvPriceB.setText("₹" + p.getPrice() + " / sq.ft");
        tvCategoryB.setText(p.getCategory() != null ? p.getCategory() : "-");
        tvSizeB.setText(p.getSize() != null ? p.getSize() : "-");
        tvFinishB.setText(p.getFinish() != null ? p.getFinish() : "-");
        
        tvModelB.setText(p.getTileNo() != null ? p.getTileNo() : "-");
        tvThicknessB.setText(p.getThickness() != null ? p.getThickness() : "-");
        tvCoverageB.setText(p.getCoverage() != null ? p.getCoverage() : "-");
        tvPackingB.setText(packing != null ? packing : "-");

        loadImage(p.getImage(), imgProductB);

        // Switch View
        layoutAddProduct.setVisibility(View.GONE);
        layoutProductBDetails.setVisibility(View.VISIBLE);
    }
    
    private void removeProductB() {
        productB = null;
        layoutProductBDetails.setVisibility(View.GONE);
        layoutAddProduct.setVisibility(View.VISIBLE);
    }

    private void loadImage(String url, ImageView imageView) {
        if (url != null && !url.isEmpty()) {
            String imageUrl = url;
            if (!imageUrl.startsWith("http")) {
                if (imageUrl.startsWith("/")) imageUrl = imageUrl.substring(1);

                // Prepend media/ if it's missing in relative path
                if (!imageUrl.startsWith("media/")) {
                    imageUrl = "media/" + imageUrl;
                }
                imageUrl = RetrofitClient.BASE_URL + imageUrl;
            } else {
                // If it's absolute, replace 127.0.0.1/localhost with our base host IP
                String baseHost = RetrofitClient.BASE_URL
                        .replace("http://", "")
                        .replace("https://", "")
                        .split(":")[0];
                imageUrl = imageUrl.replace("127.0.0.1", baseHost)
                                   .replace("localhost", baseHost);
            }

            Glide.with(this)
                .load(imageUrl)
                .placeholder(R.drawable.tile_placeholder)
                .error(R.drawable.tile_placeholder)
                .centerCrop()
                .into(imageView);
        } else {
            imageView.setImageResource(R.drawable.tile_placeholder);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_PICK_PRODUCT && resultCode == RESULT_OK && data != null) {
            Product p = new Product();
            p.setTileName(data.getStringExtra("productName"));
            String priceStr = data.getStringExtra("productPrice");
            try {
                 p.setPrice(priceStr);
            } catch (Exception e) {}
            
            p.setCategory(data.getStringExtra("productCategory"));
            p.setSize(data.getStringExtra("productSize"));
            p.setFinish(data.getStringExtra("productFinish"));
            p.setImage(data.getStringExtra("productImage"));
            
            // New Fields
            p.setTileNo(data.getStringExtra("productTileNo"));
            p.setThickness(data.getStringExtra("productThickness"));
            p.setCoverage(data.getStringExtra("productCoverage"));
            String packing = data.getStringExtra("productPacking");
            
            loadProductB(p, packing);
        }
     }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}
