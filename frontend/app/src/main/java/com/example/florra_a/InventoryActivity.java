package com.example.florra_a;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.florra_a.adapters.InventoryAdapter;
import com.example.florra_a.models.InventoryResponse;
import com.example.florra_a.models.Product;
import com.example.florra_a.network.ApiService;
import com.example.florra_a.network.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InventoryActivity extends AppCompatActivity {

    private ImageButton btnBack;
    private TextView tvTotal, tvInStock, tvLowStock, tvEmpty;
    private EditText etSearch;
    private Button btnAll;
    private android.widget.LinearLayout categoryContainer;
    
    private RecyclerView recyclerView;
    private InventoryAdapter adapter;
    private List<Product> productList = new ArrayList<>();
    
    private String currentCategory = "";
    private String currentSearch = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);

        initViews();
        setupRecyclerView();
        setupListeners();
        fetchInventoryData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Always refresh the inventory when returning to this screen
        fetchInventoryData();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        
        ImageButton btnDownloadLowStock = findViewById(R.id.btnDownloadLowStock);
        ImageButton btnDownloadEmpty = findViewById(R.id.btnDownloadEmpty);
        
        if (btnDownloadLowStock != null) {
            btnDownloadLowStock.setOnClickListener(v -> {
                Toast.makeText(this, "Downloading Low Stock report...", Toast.LENGTH_SHORT).show();
            });
        }
        
        if (btnDownloadEmpty != null) {
            btnDownloadEmpty.setOnClickListener(v -> {
                Toast.makeText(this, "Downloading Out of Stock report...", Toast.LENGTH_SHORT).show();
            });
        }
        
        tvTotal = findViewById(R.id.tvTotal);
        tvInStock = findViewById(R.id.tvInStock);
        tvLowStock = findViewById(R.id.tvLowStock);
        tvEmpty = findViewById(R.id.tvEmpty);
        
        etSearch = findViewById(R.id.etSearch);
        
        btnAll = findViewById(R.id.btnAll);
        categoryContainer = findViewById(R.id.categoryContainer);
        setupDynamicCategories();
        
        recyclerView = findViewById(R.id.recyclerView);
    }

    private void setupRecyclerView() {
        adapter = new InventoryAdapter(productList, new InventoryAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Product product) {
                // TODO: Navigate to product details
                Toast.makeText(InventoryActivity.this, "Clicked: " + product.getTileName(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onActionClick(Product product) {
                // Custom programmatic dialog layout for maximum compatibility and zero XML dependency
                android.widget.LinearLayout layout = new android.widget.LinearLayout(InventoryActivity.this);
                layout.setOrientation(android.widget.LinearLayout.VERTICAL);
                layout.setPadding(48, 40, 48, 20);

                // Description text
                TextView tvDesc = new TextView(InventoryActivity.this);
                tvDesc.setText("Adjust stock for this product. You can type the quantity or use the buttons below.");
                tvDesc.setTextColor(getResources().getColor(R.color.zinc_600));
                tvDesc.setTextSize(14);
                tvDesc.setPadding(0, 0, 0, 32);
                layout.addView(tvDesc);

                // Row for decrement, edittext, increment
                android.widget.LinearLayout row = new android.widget.LinearLayout(InventoryActivity.this);
                row.setOrientation(android.widget.LinearLayout.HORIZONTAL);
                row.setGravity(android.view.Gravity.CENTER);

                // Decrement Button
                Button btnDec = new Button(InventoryActivity.this);
                btnDec.setText("-");
                btnDec.setTextSize(20);
                btnDec.setTextColor(getResources().getColor(android.R.color.white));
                btnDec.setBackgroundResource(R.drawable.bg_primary_button); // premium primary styling
                
                // Adjust layout params for a neat circular/square look
                android.widget.LinearLayout.LayoutParams btnParams = new android.widget.LinearLayout.LayoutParams(
                        120, 120);
                btnDec.setLayoutParams(btnParams);

                // EditText for Input
                EditText etStock = new EditText(InventoryActivity.this);
                etStock.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
                etStock.setText(String.valueOf(product.getStock()));
                etStock.setGravity(android.view.Gravity.CENTER);
                etStock.setTextSize(22);
                etStock.setPadding(20, 10, 20, 10);
                android.widget.LinearLayout.LayoutParams editParams = new android.widget.LinearLayout.LayoutParams(
                        200, android.widget.LinearLayout.LayoutParams.WRAP_CONTENT);
                editParams.setMargins(32, 0, 32, 0);
                etStock.setLayoutParams(editParams);

                // Increment Button
                Button btnInc = new Button(InventoryActivity.this);
                btnInc.setText("+");
                btnInc.setTextSize(20);
                btnInc.setTextColor(getResources().getColor(android.R.color.white));
                btnInc.setBackgroundResource(R.drawable.bg_primary_button);
                btnInc.setLayoutParams(btnParams);

                row.addView(btnDec);
                row.addView(etStock);
                row.addView(btnInc);
                layout.addView(row);

                // Button click handlers
                btnDec.setOnClickListener(vDec -> {
                    try {
                        int current = Integer.parseInt(etStock.getText().toString());
                        if (current > 0) {
                            etStock.setText(String.valueOf(current - 1));
                        }
                    } catch (NumberFormatException e) {
                        etStock.setText("0");
                    }
                });

                btnInc.setOnClickListener(vInc -> {
                    try {
                        int current = Integer.parseInt(etStock.getText().toString());
                        etStock.setText(String.valueOf(current + 1));
                    } catch (NumberFormatException e) {
                        etStock.setText("1");
                    }
                });

                // Show Dialog
                androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(InventoryActivity.this);
                builder.setTitle(product.getTileName())
                       .setView(layout)
                       .setPositiveButton("SAVE", null) // Set to null first to override dismiss behaviour on validation
                       .setNegativeButton("CANCEL", (dialog, which) -> dialog.dismiss());

                androidx.appcompat.app.AlertDialog dialog = builder.create();
                dialog.show();

                // Style the dialog buttons beautifully to match the app theme
                Button positiveButton = dialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE);
                Button negativeButton = dialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_NEGATIVE);
                if (positiveButton != null) {
                    positiveButton.setTextColor(getResources().getColor(R.color.emerald_700));
                    positiveButton.setOnClickListener(vSave -> {
                        String input = etStock.getText().toString().trim();
                        if (input.isEmpty()) {
                            Toast.makeText(InventoryActivity.this, "Please enter a valid stock amount", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        
                        try {
                            int newStock = Integer.parseInt(input);
                            dialog.dismiss();
                            updateProductStockOnServer(product, newStock);
                        } catch (NumberFormatException e) {
                            Toast.makeText(InventoryActivity.this, "Invalid stock format", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                if (negativeButton != null) {
                    negativeButton.setTextColor(getResources().getColor(R.color.slate_900));
                }
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());

        // Search Listener
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                currentSearch = s.toString();
                // Debouncing could be added here, but for now we'll just fetch
                fetchInventoryData();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void setupDynamicCategories() {
        View.OnClickListener categoryListener = v -> {
            Button btn = (Button) v;
            String text = btn.getText().toString();
            resetFilterButtons();
            btn.setBackgroundResource(R.drawable.bg_primary_button);
            btn.setTextColor(getResources().getColor(R.color.white));
            currentCategory = text.equals("All Items") ? "" : text;
            fetchInventoryData();
        };

        // Attach listener to all buttons defined in XML
        for (int i = 0; i < categoryContainer.getChildCount(); i++) {
            View child = categoryContainer.getChildAt(i);
            if (child instanceof Button) {
                child.setOnClickListener(categoryListener);
            }
        }
    }

    private void resetFilterButtons() {
        int colorZinc600 = getResources().getColor(R.color.zinc_600);
        int bgOutline = R.drawable.bg_outline_button;
        
        btnAll.setBackgroundResource(bgOutline);
        btnAll.setTextColor(colorZinc600);
        
        for (int i = 0; i < categoryContainer.getChildCount(); i++) {
            View v = categoryContainer.getChildAt(i);
            if (v instanceof Button) {
                v.setBackgroundResource(bgOutline);
                ((Button)v).setTextColor(colorZinc600);
            }
        }
    }

    private void fetchInventoryData() {
        ApiService apiService = RetrofitClient.getApiService();
        // Pass null for finish for now as UI doesn't have it explicitly separate from category buttons yet
        Call<InventoryResponse> call = apiService.getInventory(currentSearch, currentCategory, null);
        
        call.enqueue(new Callback<InventoryResponse>() {
            @Override
            public void onResponse(Call<InventoryResponse> call, Response<InventoryResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    InventoryResponse inventoryResponse = response.body();
                    
                    // Update Stats
                    InventoryResponse.InventoryStats stats = inventoryResponse.getStats();
                    if (stats != null) {
                        tvTotal.setText(String.valueOf(stats.getTotal()));
                        tvInStock.setText(String.valueOf(stats.getInStock()));
                        tvLowStock.setText(String.valueOf(stats.getLowStock()));
                        tvEmpty.setText(String.valueOf(stats.getEmpty()));
                    }
                    
                    // Update List
                    productList = inventoryResponse.getProducts();
                    adapter.updateData(productList);
                    
                } else {
                    Toast.makeText(InventoryActivity.this, "Failed to load inventory", Toast.LENGTH_SHORT).show();
                    Log.e("InventoryActivity", "Response not successful: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<InventoryResponse> call, Throwable t) {
                Toast.makeText(InventoryActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("InventoryActivity", "API call failed", t);
            }
        });
    }

    private void updateProductStockOnServer(Product product, int newStock) {
        final android.app.ProgressDialog progressDialog = new android.app.ProgressDialog(this);
        progressDialog.setMessage("Updating stock for " + product.getTileName() + "...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        java.util.Map<String, okhttp3.RequestBody> textFields = new java.util.HashMap<>();
        textFields.put("tile_name", createPartFromString(product.getTileName()));
        textFields.put("tile_no", createPartFromString(product.getTileNo()));
        textFields.put("brand_name", createPartFromString(product.getBrandName()));
        textFields.put("category", createPartFromString(product.getCategory()));
        textFields.put("size", createPartFromString(product.getSize()));
        textFields.put("finish", createPartFromString(product.getFinish()));
        textFields.put("color", createPartFromString(product.getColor()));
        textFields.put("thickness", createPartFromString(product.getThickness()));
        textFields.put("coverage", createPartFromString(product.getCoverage()));
        textFields.put("warehouse", createPartFromString(product.getWarehouse()));
        textFields.put("price", createPartFromString(product.getPrice()));
        textFields.put("stock", createPartFromString(String.valueOf(newStock)));
        textFields.put("description", createPartFromString(product.getDescription()));
        textFields.put("is_active", createPartFromString(String.valueOf(product.isActive())));

        ApiService apiService = RetrofitClient.getApiService();
        apiService.updateProduct(product.getId(), textFields, null).enqueue(new Callback<Product>() {
            @Override
            public void onResponse(Call<Product> call, Response<Product> response) {
                progressDialog.dismiss();
                if (response.isSuccessful()) {
                    Toast.makeText(InventoryActivity.this, "Stock updated successfully!", Toast.LENGTH_SHORT).show();
                    fetchInventoryData(); // Refresh the list and stats immediately!
                } else {
                    Toast.makeText(InventoryActivity.this, "Failed to update stock: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Product> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(InventoryActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private okhttp3.RequestBody createPartFromString(String value) {
        return okhttp3.RequestBody.create(okhttp3.MediaType.parse("text/plain"), value != null ? value : "");
    }
}