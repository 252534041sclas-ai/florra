package com.example.florra_a;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.florra_a.adapters.RecommendationAdapter;
import com.example.florra_a.models.Product;

import java.util.ArrayList;
import java.util.List;

public class AIRecommendationActivity extends AppCompatActivity {

    private RecyclerView rvRecommendations;
    private RecommendationAdapter adapter;
    private List<Product> recommendedProducts;
    private TextView tvMatchCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set fullscreen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_ai_recommendation);

        initializeViews();
        setupClickListeners();
        loadRecommendations();
    }

    private void initializeViews() {
        rvRecommendations = findViewById(R.id.rvRecommendations);
        // Use Grid Layout with 2 columns
        rvRecommendations.setLayoutManager(new GridLayoutManager(this, 2));

        tvMatchCount = findViewById(R.id.tvMatchCount);
    }

    private void loadRecommendations() {
        // Get data from intent
        if (getIntent().hasExtra("recommendations")) {
            recommendedProducts = (List<Product>) getIntent().getSerializableExtra("recommendations");
        }

        if (recommendedProducts == null) {
            recommendedProducts = new ArrayList<>();
        }

        tvMatchCount.setText("Found " + recommendedProducts.size() + " similar matches");

        adapter = new RecommendationAdapter(this, recommendedProducts);
        rvRecommendations.setAdapter(adapter);
    }

    private void setupClickListeners() {
        findViewById(R.id.btnBack).setOnClickListener(v -> onBackPressed());

        findViewById(R.id.btnSort).setOnClickListener(v -> 
            Toast.makeText(this, "Sort Options", Toast.LENGTH_SHORT).show());
        
        findViewById(R.id.btnRefineSearch).setOnClickListener(v -> 
             Toast.makeText(this, "Refine Search Parameters", Toast.LENGTH_SHORT).show());
        
        // Setup filter button listeners (visual only for now)
        int[] filterIds = {R.id.btnTopMatches, R.id.btnTexture, R.id.btnColorPalette};
        for (int id : filterIds) {
            findViewById(id).setOnClickListener(v -> {
                 Toast.makeText(this, "Filter applied", Toast.LENGTH_SHORT).show();
            });
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}