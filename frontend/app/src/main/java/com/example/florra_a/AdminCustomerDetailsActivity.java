package com.example.florra_a;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.florra_a.adapters.EnquiryAdapter;
import com.example.florra_a.models.Bill;
import com.example.florra_a.models.Enquiry;
import com.example.florra_a.network.ApiService;
import com.example.florra_a.network.RetrofitClient;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminCustomerDetailsActivity extends AppCompatActivity {

    private String customerName, customerPhone;
    private TextView tvName, tvPhone, tvBillCount, tvEnquiryCount;
    private android.widget.ImageView ivProfile;
    private RecyclerView rvBills, rvEnquiries;
    
    private List<Bill> customerBills = new ArrayList<>();
    private List<Enquiry> customerEnquiries = new ArrayList<>();
    
    private BillAdapter billAdapter;
    private EnquiryAdapter enquiryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            getWindow().setStatusBarColor(android.graphics.Color.WHITE);
            getWindow().getDecorView().setSystemUiVisibility(android.view.View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        
        setContentView(R.layout.activity_admin_customer_details);

        customerName = getIntent().getStringExtra("customer_name");
        customerPhone = getIntent().getStringExtra("customer_phone");

        initViews();
        fetchCustomerData();
    }

    private void initViews() {
        tvName = findViewById(R.id.tvCustomerName);
        tvPhone = findViewById(R.id.tvCustomerPhone);
        tvBillCount = findViewById(R.id.tvBillCount);
        tvEnquiryCount = findViewById(R.id.tvEnquiryCount);
        rvBills = findViewById(R.id.rvBills);
        rvEnquiries = findViewById(R.id.rvEnquiries);
        ivProfile = findViewById(R.id.ivCustomerProfile);

        tvName.setText(customerName);
        tvPhone.setText(customerPhone);

        // Load Letter Avatar
        String avatarUrl = "https://ui-avatars.com/api/?name=" + customerName + "&background=random&size=256";
        com.bumptech.glide.Glide.with(this)
                .load(avatarUrl)
                .circleCrop()
                .into(ivProfile);

        // Setup Bill Recycler
        rvBills.setLayoutManager(new LinearLayoutManager(this));
        billAdapter = new BillAdapter(this, customerBills);
        rvBills.setAdapter(billAdapter);

        // Setup Enquiry Recycler
        rvEnquiries.setLayoutManager(new LinearLayoutManager(this));
        enquiryAdapter = new EnquiryAdapter(     customerEnquiries, enquiry -> {
            // Handle enquiry click if needed
            Toast.makeText(this, "Enquiry clicked", Toast.LENGTH_SHORT).show();
        });
        rvEnquiries.setAdapter(enquiryAdapter);
        
        // Toolbar and Back button
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setNavigationOnClickListener(v -> onBackPressed());
        }
    }

    private void fetchCustomerData() {
        ApiService apiService = RetrofitClient.getApiService();

        // Fetch Bills
        apiService.getBills().enqueue(new Callback<List<Bill>>() {
            @Override
            public void onResponse(Call<List<Bill>> call, Response<List<Bill>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    customerBills.clear();
                    for (Bill bill : response.body()) {
                        if (customerName.equalsIgnoreCase(bill.getCustomerName())) {
                            customerBills.add(bill);
                        }
                    }
                    billAdapter.notifyDataSetChanged();
                    tvBillCount.setText(customerBills.size() + " Bills");
                }
            }
            @Override
            public void onFailure(Call<List<Bill>> call, Throwable t) {}
        });

        // Fetch Enquiries
        apiService.getEnquiries().enqueue(new Callback<List<Enquiry>>() {
            @Override
            public void onResponse(Call<List<Enquiry>> call, Response<List<Enquiry>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    customerEnquiries.clear();
                    for (Enquiry enquiry : response.body()) {
                        if (customerName.equalsIgnoreCase(enquiry.getCustomerName())) {
                            customerEnquiries.add(enquiry);
                        }
                    }
                    enquiryAdapter.notifyDataSetChanged();
                    tvEnquiryCount.setText(customerEnquiries.size() + " Enquiries");
                }
            }
            @Override
            public void onFailure(Call<List<Enquiry>> call, Throwable t) {}
        });
    }
}
