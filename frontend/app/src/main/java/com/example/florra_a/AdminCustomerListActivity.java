package com.example.florra_a;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.florra_a.adapters.CustomerAdapter;
import com.example.florra_a.models.Bill;
import com.example.florra_a.models.CustomerListItem;
import com.example.florra_a.models.Enquiry;
import com.example.florra_a.network.ApiService;
import com.example.florra_a.network.RetrofitClient;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminCustomerListActivity extends AppCompatActivity {

    private RecyclerView rvCustomers;
    private CustomerAdapter adapter;
    private EditText etSearch;
    private List<CustomerListItem> allCustomers = new ArrayList<>();
    private List<CustomerListItem> filteredCustomers = new ArrayList<>();
    
    private List<Bill> allBills = new ArrayList<>();
    private List<Enquiry> allEnquiries = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            getWindow().setStatusBarColor(android.graphics.Color.WHITE);
            getWindow().getDecorView().setSystemUiVisibility(android.view.View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        
        setContentView(R.layout.activity_admin_customer_list);

        initViews();
        fetchData();
    }

    private void initViews() {
        rvCustomers = findViewById(R.id.rvCustomers);
        etSearch = findViewById(R.id.etSearch);
        ImageButton btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(v -> onBackPressed());

        rvCustomers.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CustomerAdapter(filteredCustomers, customer -> {
            Intent intent = new Intent(AdminCustomerListActivity.this, AdminCustomerDetailsActivity.class);
            intent.putExtra("customer_name", customer.getName());
            intent.putExtra("customer_phone", customer.getPhone());
            startActivity(intent);
        });
        rvCustomers.setAdapter(adapter);

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filter(s.toString());
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void fetchData() {
        ApiService apiService = RetrofitClient.getApiService();
        
        // Fetch Bills
        apiService.getBills().enqueue(new Callback<List<Bill>>() {
            @Override
            public void onResponse(Call<List<Bill>> call, Response<List<Bill>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    allBills = response.body();
                    // After bills, fetch enquiries
                    fetchEnquiries();
                }
            }
            @Override
            public void onFailure(Call<List<Bill>> call, Throwable t) {
                fetchEnquiries();
            }
        });
    }

    private void fetchEnquiries() {
        ApiService apiService = RetrofitClient.getApiService();
        apiService.getEnquiries().enqueue(new Callback<List<Enquiry>>() {
            @Override
            public void onResponse(Call<List<Enquiry>> call, Response<List<Enquiry>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    allEnquiries = response.body();
                }
                processData();
            }
            @Override
            public void onFailure(Call<List<Enquiry>> call, Throwable t) {
                processData();
            }
        });
    }

    private void processData() {
        Map<String, CustomerListItem> customerMap = new HashMap<>();

        // Process Bills
        for (Bill bill : allBills) {
            String name = bill.getCustomerName();
            String phone = bill.getCustomerPhone();
            if (name == null || name.isEmpty()) continue;
            
            String key = name.toLowerCase() + "_" + (phone != null ? phone : "");
            CustomerListItem item = customerMap.get(key);
            if (item == null) {
                item = new CustomerListItem(name, phone != null ? phone : "N/A");
                customerMap.put(key, item);
            }
            item.setBillCount(item.getBillCount() + 1);
        }

        // Process Enquiries
        for (Enquiry enquiry : allEnquiries) {
            String name = enquiry.getCustomerName();
            String phone = enquiry.getPhone();
            if (name == null || name.isEmpty()) continue;

            String key = name.toLowerCase() + "_" + (phone != null ? phone : "");
            CustomerListItem item = customerMap.get(key);
            if (item == null) {
                item = new CustomerListItem(name, phone != null ? phone : "N/A");
                customerMap.put(key, item);
            }
            item.setEnquiryCount(item.getEnquiryCount() + 1);
        }

        allCustomers.clear();
        allCustomers.addAll(customerMap.values());
        filteredCustomers.clear();
        filteredCustomers.addAll(allCustomers);
        adapter.notifyDataSetChanged();
    }

    private void filter(String text) {
        filteredCustomers.clear();
        for (CustomerListItem item : allCustomers) {
            if (item.getName().toLowerCase().contains(text.toLowerCase()) || 
                item.getPhone().contains(text)) {
                filteredCustomers.add(item);
            }
        }
        adapter.notifyDataSetChanged();
    }
}
