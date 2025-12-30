package com.example.florra_a;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class AIRecommendationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set fullscreen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_ai_recommendation);

        setupClickListeners();
    }

    private void setupClickListeners() {
        // Back Button
        View btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        }

        // Sort Button
        View btnSort = findViewById(R.id.btnSort);
        if (btnSort != null) {
            btnSort.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(AIRecommendationActivity.this, "Sort Options", Toast.LENGTH_SHORT).show();
                }
            });
        }

        // Refine Search Button
        View btnRefineSearch = findViewById(R.id.btnRefineSearch);
        if (btnRefineSearch != null) {
            btnRefineSearch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(AIRecommendationActivity.this, "Refine Search Parameters", Toast.LENGTH_SHORT).show();
                }
            });
        }

        // Filter Buttons
        String[] filterButtons = {"btnTopMatches", "btnTexture", "btnColorPalette", "btnSize", "btnFinish"};
        for (String btnId : filterButtons) {
            int resId = getResources().getIdentifier(btnId, "id", getPackageName());
            View button = findViewById(resId);
            if (button != null) {
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String buttonName = getResources().getResourceEntryName(v.getId());
                        Toast.makeText(AIRecommendationActivity.this,
                                "Filter: " + buttonName.replace("btn", ""),
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }

        // Product Cards
        String[] productCards = {"card1", "card2", "card3", "card4", "card5", "card6"};
        for (String cardId : productCards) {
            int resId = getResources().getIdentifier(cardId, "id", getPackageName());
            View card = findViewById(resId);
            if (card != null) {
                card.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String cardName = getResources().getResourceEntryName(v.getId());
                        Toast.makeText(AIRecommendationActivity.this,
                                "View Product Details: " + cardName,
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }

        // Add to Cart Buttons
        String[] addButtons = {"btnAdd1", "btnAdd2", "btnAdd3", "btnAdd4", "btnAdd5", "btnAdd6"};
        for (String btnId : addButtons) {
            int resId = getResources().getIdentifier(btnId, "id", getPackageName());
            View button = findViewById(resId);
            if (button != null) {
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String buttonName = getResources().getResourceEntryName(v.getId());
                        int productNumber = Character.getNumericValue(buttonName.charAt(buttonName.length() - 1));
                        String[] productNames = {"Statuario Classic", "Crema Marfil", "Urban Concrete",
                                "Venice Terrazzo", "Roman Travertine", "Nero Marquina"};
                        Toast.makeText(AIRecommendationActivity.this,
                                "Added to cart: " + productNames[productNumber - 1],
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}