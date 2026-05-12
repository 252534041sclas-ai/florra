package com.example.florra_a;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.florra_a.models.ReportAnalyticsResponse;
import com.example.florra_a.models.ReportCustomerResponse;
import com.example.florra_a.models.ReportSummaryResponse;
import com.example.florra_a.network.ApiService;
import com.example.florra_a.network.RetrofitClient;
import com.example.florra_a.utils.SharedPrefManager;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminReportsActivity extends AppCompatActivity {

    private Spinner spinnerMonth, spinnerYear;
    private TextView tvTotalRevenue, tvProfit, tvConversion, tvAiInsight;
    private TextView tvTotalEnquiries, tvTotalQuotations, tvConfirmedOrders, tvPendingCancelled;
    private BarChart barChartRevenue;
    private LinearLayout llBestSellers;
    private RecyclerView rvTopCustomers;
    private RelativeLayout loadingOverlay;
    private ImageButton btnSavedBills, btnRefresh;
    private View btnDownloadMonthly, btnDownloadYearly;
    private LinearLayout btnBack;

    private int selectedMonth, selectedYear;
    private TopCustomerAdapter customerAdapter;
    private List<ReportCustomerResponse.TopCustomer> topCustomers = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_reports);

        // Set status bar to black for professional look
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(android.graphics.Color.BLACK);
            getWindow().getDecorView().setSystemUiVisibility(0); // White icons on black bar
        }

        initViews();
        setupSpinners();
        setupListeners();

        // Initial load
        fetchReportData();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        btnSavedBills = findViewById(R.id.btnSavedBills);
        btnRefresh = findViewById(R.id.btnRefresh);
        btnDownloadMonthly = findViewById(R.id.btnDownloadMonthly);
        btnDownloadYearly = findViewById(R.id.btnDownloadYearly);
        
        spinnerMonth = findViewById(R.id.spinnerMonth);
        spinnerYear = findViewById(R.id.spinnerYear);

        tvTotalRevenue = findViewById(R.id.tvTotalRevenue);
        tvProfit = findViewById(R.id.tvProfit);
        tvConversion = findViewById(R.id.tvConversion);
        tvAiInsight = findViewById(R.id.tvAiInsight);

        tvTotalEnquiries = findViewById(R.id.tvTotalEnquiries);
        tvTotalQuotations = findViewById(R.id.tvTotalQuotations);
        tvConfirmedOrders = findViewById(R.id.tvConfirmedOrders);
        tvPendingCancelled = findViewById(R.id.tvPendingCancelled);

        barChartRevenue = findViewById(R.id.barChartRevenue);
        llBestSellers = findViewById(R.id.llBestSellers);
        rvTopCustomers = findViewById(R.id.rvTopCustomers);
        loadingOverlay = findViewById(R.id.loadingOverlay);

        rvTopCustomers.setLayoutManager(new LinearLayoutManager(this));
        customerAdapter = new TopCustomerAdapter(topCustomers);
        rvTopCustomers.setAdapter(customerAdapter);
    }

    private void setupSpinners() {
        String[] months = {"January", "February", "March", "April", "May", "June", 
                          "July", "August", "September", "October", "November", "December"};
        ArrayAdapter<String> monthAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, months);
        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMonth.setAdapter(monthAdapter);

        Calendar cal = Calendar.getInstance();
        selectedMonth = cal.get(Calendar.MONTH) + 1;
        selectedYear = cal.get(Calendar.YEAR);

        spinnerMonth.setSelection(selectedMonth - 1);

        List<String> years = new ArrayList<>();
        for (int i = selectedYear; i >= 2023; i--) {
            years.add(String.valueOf(i));
        }
        ArrayAdapter<String> yearAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, years);
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerYear.setAdapter(yearAdapter);
        spinnerYear.setSelection(0);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());
        
        btnRefresh.setOnClickListener(v -> fetchReportData());

        btnSavedBills.setOnClickListener(v -> {
            Intent intent = new Intent(this, SavedBillsActivity.class);
            startActivity(intent);
        });

        View cardAiInsight = findViewById(R.id.cardAiInsight);
        if (cardAiInsight != null) {
            cardAiInsight.setOnClickListener(v -> {
                Intent intent = new Intent(this, SalesPredictionActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            });
        }

        spinnerMonth.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedMonth = position + 1;
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        spinnerYear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedYear = Integer.parseInt(spinnerYear.getSelectedItem().toString());
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        btnDownloadMonthly.setOnClickListener(v -> exportPdf(selectedMonth, selectedYear, false));
        btnDownloadYearly.setOnClickListener(v -> showYearPickerDialog());
    }

    private void showYearPickerDialog() {
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        String[] years = new String[currentYear - 2023 + 1];
        for (int i = 0; i < years.length; i++) {
            years[i] = String.valueOf(currentYear - i);
        }

        new androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Select Year for Report")
            .setItems(years, (dialog, which) -> {
                int chosenYear = Integer.parseInt(years[which]);
                exportPdf(0, chosenYear, true);
            })
            .show();
    }

    private void exportPdf(int month, int year, boolean isYearly) {
        String token = SharedPrefManager.getInstance(this).getToken();
        String fileName = isYearly ? 
                "Florra_Yearly_Report_" + year + "_" + System.currentTimeMillis() + ".pdf" :
                "Florra_Report_" + month + "_" + year + "_" + System.currentTimeMillis() + ".pdf";
        
        String url = RetrofitClient.BASE_URL + "api/admin/reports/export-pdf/?year=" + year;
        if (!isYearly) {
            url += "&month=" + month;
        } else {
            url += "&is_yearly=true";
        }
        
        try {
            android.app.DownloadManager.Request request = new android.app.DownloadManager.Request(Uri.parse(url));
            request.setTitle(isYearly ? "Florra Yearly Report" : "Florra Monthly Report");
            request.setDescription("Downloading " + (isYearly ? "Yearly" : "Monthly") + " Sales Report PDF");
            
            if (token != null && !token.isEmpty()) {
                request.addRequestHeader("Authorization", "Token " + token);
            }
            
            request.setMimeType("application/pdf");
            request.setNotificationVisibility(android.app.DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);
            
            android.app.DownloadManager manager = (android.app.DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
            if (manager != null) {
                manager.enqueue(request);
                Toast.makeText(this, "Downloading report...", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Download Manager not available", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Download failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void fetchReportData() {
        loadingOverlay.setVisibility(View.VISIBLE);
        ApiService apiService = RetrofitClient.getApiService();

        // 1. Fetch Summary
        apiService.getReportSummary(selectedMonth, selectedYear).enqueue(new Callback<ReportSummaryResponse>() {
            @Override
            public void onResponse(Call<ReportSummaryResponse> call, Response<ReportSummaryResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    updateSummaryUI(response.body());
                }
            }
            @Override
            public void onFailure(Call<ReportSummaryResponse> call, Throwable t) {}
        });

        // 2. Fetch Analytics
        apiService.getReportAnalytics(selectedMonth, selectedYear).enqueue(new Callback<ReportAnalyticsResponse>() {
            @Override
            public void onResponse(Call<ReportAnalyticsResponse> call, Response<ReportAnalyticsResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    updateAnalyticsUI(response.body());
                }
            }
            @Override
            public void onFailure(Call<ReportAnalyticsResponse> call, Throwable t) {}
        });

        // 3. Fetch Customers
        apiService.getReportCustomers(selectedMonth, selectedYear).enqueue(new Callback<ReportCustomerResponse>() {
            @Override
            public void onResponse(Call<ReportCustomerResponse> call, Response<ReportCustomerResponse> response) {
                loadingOverlay.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    updateCustomerUI(response.body());
                }
            }
            @Override
            public void onFailure(Call<ReportCustomerResponse> call, Throwable t) {
                loadingOverlay.setVisibility(View.GONE);
                Toast.makeText(AdminReportsActivity.this, "Failed to load report data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateSummaryUI(ReportSummaryResponse data) {
        ReportSummaryResponse.Summary s = data.getSummary();
        tvTotalRevenue.setText(String.format("₹%.2f", s.getTotalRevenue()));
        tvProfit.setText(String.format("₹%.2f", s.getProfit()));
        tvConversion.setText(String.format("%.1f%%", s.getConversionPercentage()));
        
        tvTotalEnquiries.setText(String.valueOf(s.getTotalEnquiries()));
        tvTotalQuotations.setText(String.valueOf(s.getTotalQuotations()));
        tvConfirmedOrders.setText(String.valueOf(s.getConfirmedOrders()));
        tvPendingCancelled.setText(s.getPendingQuotations() + "/" + s.getCancelledQuotations());

        tvAiInsight.setText(data.getInsights().getAiInsight());
    }

    private void updateAnalyticsUI(ReportAnalyticsResponse data) {
        // Update Bar Chart
        List<BarEntry> entries = new ArrayList<>();
        List<Double> revenue = data.getRevenueChart();
        for (int i = 0; i < revenue.size(); i++) {
            entries.add(new BarEntry(i, revenue.get(i).floatValue()));
        }

        BarDataSet dataSet = new BarDataSet(entries, "Weekly Revenue");
        dataSet.setColor(0xFF000000);
        dataSet.setValueTextColor(0xFF64748B);
        dataSet.setValueTextSize(10f);

        BarData barData = new BarData(dataSet);
        barChartRevenue.setData(barData);
        barChartRevenue.getXAxis().setValueFormatter(new IndexAxisValueFormatter(new String[]{"W1", "W2", "W3", "W4"}));
        barChartRevenue.getXAxis().setGranularity(1f);
        barChartRevenue.getXAxis().setPosition(com.github.mikephil.charting.components.XAxis.XAxisPosition.BOTTOM);
        barChartRevenue.getDescription().setEnabled(false);
        barChartRevenue.animateY(1000);
        barChartRevenue.invalidate();

        // Update Best Sellers
        llBestSellers.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(this);
        for (ReportAnalyticsResponse.BestSeller item : data.getBestSellers()) {
            View view = inflater.inflate(android.R.layout.simple_list_item_2, llBestSellers, false);
            ((TextView) view.findViewById(android.R.id.text1)).setText(item.getName());
            ((TextView) view.findViewById(android.R.id.text2)).setText(item.getQuantity() + " units sold");
            llBestSellers.addView(view);
        }
    }

    private void updateCustomerUI(ReportCustomerResponse data) {
        topCustomers.clear();
        topCustomers.addAll(data.getTopCustomers());
        customerAdapter.notifyDataSetChanged();
    }

    // Top Customer Adapter
    private class TopCustomerAdapter extends RecyclerView.Adapter<TopCustomerAdapter.ViewHolder> {
        private List<ReportCustomerResponse.TopCustomer> customers;

        public TopCustomerAdapter(List<ReportCustomerResponse.TopCustomer> customers) {
            this.customers = customers;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_top_customer, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            ReportCustomerResponse.TopCustomer c = customers.get(position);
            holder.name.setText(c.getCustomerName());
            holder.stats.setText(c.getTotalOrders() + " orders");
            holder.amount.setText(String.format("₹%.2f", c.getTotalAmount()));
        }

        @Override
        public int getItemCount() {
            return customers.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView name, stats, amount;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                name = itemView.findViewById(R.id.tvCustomerName);
                stats = itemView.findViewById(R.id.tvOrderStats);
                amount = itemView.findViewById(R.id.tvTotalAmount);
            }
        }
    }
}
