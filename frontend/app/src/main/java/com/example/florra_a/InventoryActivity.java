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
                // TODO: Handle action (Order/Restock)
                Toast.makeText(InventoryActivity.this, "Action: " + product.getStockStatus(), Toast.LENGTH_SHORT).show();
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

        btnAll.setOnClickListener(categoryListener);

        categoryContainer.removeAllViews();
        categoryContainer.addView(btnAll);
        for (String category : com.example.florra_a.utils.Constants.CATEGORIES) {
            Button btn = new Button(new android.view.ContextThemeWrapper(this, R.style.FilterButtonStyle), null, 0);
            btn.setText(category);
            btn.setOnClickListener(categoryListener);
            
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(0, 0, (int)(8 * getResources().getDisplayMetrics().density), 0);
            btn.setLayoutParams(params);
            
            categoryContainer.addView(btn);
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
}