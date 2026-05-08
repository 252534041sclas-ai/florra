package com.example.florra_a;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ProductDetails;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.QueryProductDetailsParams;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class SubscriptionActivity extends AppCompatActivity {

    private BillingClient billingClient;
    private ProductDetails productDetails;
    private static final String PRODUCT_ID = "premium_subscription"; // Replace with your actual product ID

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscription);

        // Hide ActionBar if present
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        initializeBillingClient();
        initializeViews();
    }

    private void initializeBillingClient() {
        billingClient = BillingClient.newBuilder(this)
                .setListener(purchasesUpdatedListener)
                .enablePendingPurchases()
                .build();

        startConnection();
    }

    private void startConnection() {
        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(BillingResult billingResult) {
                if (billingResult.getResponseCode() ==  BillingClient.BillingResponseCode.OK) {
                    // The BillingClient is ready. You can query purchases here.
                    queryProductDetails();
                } else {
                    android.util.Log.e("Billing", "Setup failed: " + billingResult.getDebugMessage());
                    Toast.makeText(SubscriptionActivity.this, "Billing setup failed: " + billingResult.getDebugMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onBillingServiceDisconnected() {
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
                android.util.Log.e("Billing", "Service disconnected");
            }
        });
    }

    private void queryProductDetails() {
        List<QueryProductDetailsParams.Product> productList = new ArrayList<>();
        productList.add(
                QueryProductDetailsParams.Product.newBuilder()
                        .setProductId(PRODUCT_ID)
                        .setProductType(BillingClient.ProductType.SUBS)
                        .build()
        );

        QueryProductDetailsParams params = QueryProductDetailsParams.newBuilder()
                .setProductList(productList)
                .build();

        billingClient.queryProductDetailsAsync(params, (billingResult, productDetailsList) -> {
            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && productDetailsList != null) {
                if (!productDetailsList.isEmpty()) {
                    productDetails = productDetailsList.get(0);
                    android.util.Log.d("Billing", "Product details found: " + productDetails.getName());
                } else {
                     // Handle case where product is not found
                     android.util.Log.e("Billing", "Product list empty. Check Product ID in Play Console.");
                     runOnUiThread(() -> Toast.makeText(SubscriptionActivity.this, "Product not found. Check Play Console.", Toast.LENGTH_LONG).show());
                }
            } else {
                android.util.Log.e("Billing", "Query failed: " + billingResult.getDebugMessage());
            }
        });
    }

    private final PurchasesUpdatedListener purchasesUpdatedListener = (billingResult, purchases) -> {
        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && purchases != null) {
            for (Purchase purchase : purchases) {
                handlePurchase(purchase);
            }
        } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED) {
            // Handle an error caused by a user cancelling the purchase flow.
            Toast.makeText(this, "Purchase Canceled", Toast.LENGTH_SHORT).show();
        } else {
            // Handle any other error codes.
            Toast.makeText(this, "Error: " + billingResult.getDebugMessage(), Toast.LENGTH_SHORT).show();
        }
    };

    private void handlePurchase(Purchase purchase) {
        if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
            // Acknowledge the purchase if it hasn't been acknowledged yet.
            if (!purchase.isAcknowledged()) {
                 // In a real app, you should verify the purchase token on your server here.
                Toast.makeText(this, "Purchase Successful!", Toast.LENGTH_SHORT).show();
                
                 // Navigate to Success Screen
                Intent intent = new Intent(SubscriptionActivity.this, PaymentSuccessActivity.class);
                startActivity(intent);
                finish();
            }
        }
    }

    private void initializeViews() {
        // Back Button
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        // Load Showroom Image
        ImageView ivShowroom = findViewById(R.id.ivShowroom);
        String imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuASjN-lJAl_odH5UmSyjWZbIP9sUCCabPE8G2jLhDu3fZbN7Y-IbgQ_SZiG_I2n8iXnTTVabRLD8VwGn4Y3r-10Lza7Vfx-tRxo7NTRL1vJTWHyfh4LL3V4wdiBuWjC9klhnyPEV-a1FkiGeUanB51iQvfjSLVnFRdqERXXooA2lAZzVpnzeIq3d25RoBgn4ZnbnZcX6Lwd9UHhSbJkFqituqyLq6zZEooG1hehIlRq0cKr39yRy6LMj7nUJmZiwzKxKkwbl9eT3Y9N";
        
        Glide.with(this)
                .load(imageUrl)
                .centerCrop()
                .placeholder(R.drawable.placeholder_image) 
                .error(android.R.color.darker_gray)
                .into(ivShowroom);

        // Subscribe Button
        findViewById(R.id.btnSubscribe).setOnClickListener(v -> launchBillingFlow());

        // Restore Purchase
        findViewById(R.id.btnRestore).setOnClickListener(v -> {
            Toast.makeText(this, "Restore purchase logic not implemented yet", Toast.LENGTH_SHORT).show();
            // TODO: Implement restore purchase logic by querying existing purchases
        });
    }

    private void launchBillingFlow() {
        if (productDetails != null) {
            List<BillingFlowParams.ProductDetailsParams> productDetailsParamsList = new ArrayList<>();
            productDetailsParamsList.add(
                    BillingFlowParams.ProductDetailsParams.newBuilder()
                            .setProductDetails(productDetails)
                            // Retrieve the offer token from the product details.
                            // For simplicity, we're taking the first offer token here.
                            // In a real app, you might want to present multiple offers.
                             .setOfferToken(productDetails.getSubscriptionOfferDetails().get(0).getOfferToken())
                            .build()
            );

            BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
                    .setProductDetailsParamsList(productDetailsParamsList)
                    .build();

            billingClient.launchBillingFlow(this, billingFlowParams);
        } else {
             android.util.Log.e("Billing", "ProductDetails is null. Query likely failed.");
             Toast.makeText(this, "Billing not ready. please try again in a moment.", Toast.LENGTH_SHORT).show();
             startConnection(); // Retry connection
        }
    }

    public static Intent newIntent(Context context) {
        return new Intent(context, SubscriptionActivity.class);
    }
}
