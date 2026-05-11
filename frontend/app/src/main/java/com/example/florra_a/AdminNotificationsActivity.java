package com.example.florra_a;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.florra_a.adapters.AdminNotificationAdapter;
import com.example.florra_a.adapters.QuotationRequestAdapter;
import com.example.florra_a.models.AdminNotificationItem;
import com.example.florra_a.models.Enquiry;
import com.example.florra_a.network.ApiService;
import com.example.florra_a.network.RetrofitClient;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminNotificationsActivity extends AppCompatActivity {

    // ─── Enum for active tab ──────────────────────────────────────────────────
    private enum Tab { REQUESTS, HISTORY, CREATE }
    private Tab activeTab = Tab.REQUESTS;

    // ─── Tab views ────────────────────────────────────────────────────────────
    private View tabRequests, tabHistoryWrap, tabCreateWrap;
    private View tabRequestsIndicator, tabHistoryIndicator, tabCreateIndicator;
    private TextView tabHistory, tabCreate, tvRequestBadge;

    // ─── Panels ───────────────────────────────────────────────────────────────
    private View panelRequests, panelHistory, panelCreate;

    // ─── Requests tab ─────────────────────────────────────────────────────────
    private RecyclerView rvQuotationRequests;
    private QuotationRequestAdapter requestAdapter;
    private ProgressBar progressRequests;
    private TextView tvEmptyRequests, tvNewCount, tvQuotedCount, tvResolvedCount;

    // ─── History tab ──────────────────────────────────────────────────────────
    private RecyclerView rvAdminNotifications;
    private AdminNotificationAdapter notifAdapter;
    private ProgressBar progressBar;
    private TextView tvEmpty, tvTotalSent, tvTodaySent;

    // ─── Create tab ───────────────────────────────────────────────────────────
    private EditText etTitle, etMessage;
    private TextView tvPreviewTitle, tvPreviewMessage;
    private TextView chipSystem, chipPromotion, chipAlert, chipAnnouncement;
    private androidx.cardview.widget.CardView btnSendNotification;
    private ProgressBar btnProgress;
    private ImageView btnSendIcon;
    private TextView tvSendLabel;
    private String selectedType = "system";

    // ─── Product Tag ──────────────────────────────────────────────────────────
    private EditText etProductSearch;
    private ProgressBar progressProductSearch;
    private android.widget.HorizontalScrollView scrollProductResults;
    private android.widget.LinearLayout llProductChips;
    private androidx.cardview.widget.CardView cardSelectedProduct;
    private ImageView ivSelectedProduct;
    private TextView tvSelectedProductName, tvSelectedProductInfo;
    private ImageView btnRemoveProduct;
    private com.example.florra_a.models.Product selectedProduct = null;
    private java.util.List<com.example.florra_a.models.Product> searchResults = new java.util.ArrayList<>();
    private android.os.Handler searchHandler = new android.os.Handler();
    private Runnable searchRunnable;

    // ─────────────────────────────────────────────────────────────────────────

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(0);
            getWindow().setStatusBarColor(android.graphics.Color.parseColor("#014D4E"));
        }
        setContentView(R.layout.activity_admin_notifications);

        initViews();
        setupTabClicks();
        setupTypeChips();
        setupPreviewListeners();
        setupSendButton();
        switchTab(Tab.REQUESTS); // default tab

        setupProductSearch();

        // Load all data
        loadQuotationRequests();
        loadNotificationHistory();
    }

    // ─── Init ─────────────────────────────────────────────────────────────────

    private void initViews() {
        // Tab containers
        tabRequests        = findViewById(R.id.tabRequests);
        tabHistoryWrap     = findViewById(R.id.tabHistoryWrap);
        tabCreateWrap      = findViewById(R.id.tabCreateWrap);

        // Tab indicators (underline)
        tabRequestsIndicator = findViewById(R.id.tabRequestsIndicator);
        tabHistoryIndicator  = findViewById(R.id.tabHistoryIndicator);
        tabCreateIndicator   = findViewById(R.id.tabCreateIndicator);

        // Tab text views
        tabHistory    = findViewById(R.id.tabHistory);
        tabCreate     = findViewById(R.id.tabCreate);
        tvRequestBadge = findViewById(R.id.tvRequestBadge);

        // Panels
        panelRequests = findViewById(R.id.panelRequests);
        panelHistory  = findViewById(R.id.panelHistory);
        panelCreate   = findViewById(R.id.panelCreate);

        // Requests panel
        rvQuotationRequests = findViewById(R.id.rvQuotationRequests);
        rvQuotationRequests.setLayoutManager(new LinearLayoutManager(this));
        requestAdapter = new QuotationRequestAdapter(new ArrayList<>(), this::onRespondClicked);
        rvQuotationRequests.setAdapter(requestAdapter);
        progressRequests = findViewById(R.id.progressRequests);
        tvEmptyRequests  = findViewById(R.id.tvEmptyRequests);
        tvNewCount      = findViewById(R.id.tvNewCount);
        tvQuotedCount   = findViewById(R.id.tvQuotedCount);
        tvResolvedCount = findViewById(R.id.tvResolvedCount);

        // History panel
        rvAdminNotifications = findViewById(R.id.rvAdminNotifications);
        rvAdminNotifications.setLayoutManager(new LinearLayoutManager(this));
        notifAdapter = new AdminNotificationAdapter(new ArrayList<>());
        rvAdminNotifications.setAdapter(notifAdapter);
        progressBar  = findViewById(R.id.progressBar);
        tvEmpty      = findViewById(R.id.tvEmpty);
        tvTotalSent  = findViewById(R.id.tvTotalSent);
        tvTodaySent  = findViewById(R.id.tvTodaySent);

        // Create panel
        etTitle          = findViewById(R.id.etTitle);
        etMessage        = findViewById(R.id.etMessage);
        tvPreviewTitle   = findViewById(R.id.tvPreviewTitle);
        tvPreviewMessage = findViewById(R.id.tvPreviewMessage);
        chipSystem       = findViewById(R.id.chipSystem);
        chipPromotion    = findViewById(R.id.chipPromotion);
        chipAlert        = findViewById(R.id.chipAlert);
        chipAnnouncement = findViewById(R.id.chipAnnouncement);
        btnSendNotification = findViewById(R.id.btnSendNotification);
        btnProgress      = findViewById(R.id.btnProgress);
        btnSendIcon      = findViewById(R.id.btnSendIcon);
        tvSendLabel      = findViewById(R.id.tvSendLabel);

        // Product tag views
        etProductSearch      = findViewById(R.id.etProductSearch);
        progressProductSearch = findViewById(R.id.progressProductSearch);
        scrollProductResults  = findViewById(R.id.scrollProductResults);
        llProductChips        = findViewById(R.id.llProductChips);
        cardSelectedProduct   = findViewById(R.id.cardSelectedProduct);
        ivSelectedProduct     = findViewById(R.id.ivSelectedProduct);
        tvSelectedProductName = findViewById(R.id.tvSelectedProductName);
        tvSelectedProductInfo = findViewById(R.id.tvSelectedProductInfo);
        btnRemoveProduct      = findViewById(R.id.btnRemoveProduct);

        // Header buttons
        findViewById(R.id.btnBack).setOnClickListener(v -> onBackPressed());
        findViewById(R.id.btnRefresh).setOnClickListener(v -> refreshCurrent());
    }

    // ─── Tab Navigation ───────────────────────────────────────────────────────

    private void setupTabClicks() {
        tabRequests.setOnClickListener(v -> switchTab(Tab.REQUESTS));
        tabHistoryWrap.setOnClickListener(v -> switchTab(Tab.HISTORY));
        tabCreateWrap.setOnClickListener(v -> switchTab(Tab.CREATE));
    }

    private void switchTab(Tab tab) {
        activeTab = tab;

        // Hide all panels
        panelRequests.setVisibility(View.GONE);
        panelHistory.setVisibility(View.GONE);
        panelCreate.setVisibility(View.GONE);

        // Reset all indicators & colours
        tabRequestsIndicator.setBackgroundColor(Color.TRANSPARENT);
        tabHistoryIndicator.setBackgroundColor(Color.TRANSPARENT);
        tabCreateIndicator.setBackgroundColor(Color.TRANSPARENT);
        tabHistory.setTextColor(Color.argb(128, 255, 255, 255));
        tabCreate.setTextColor(Color.argb(128, 255, 255, 255));

        // Activate selected
        switch (tab) {
            case REQUESTS:
                panelRequests.setVisibility(View.VISIBLE);
                tabRequestsIndicator.setBackgroundColor(Color.WHITE);
                break;
            case HISTORY:
                panelHistory.setVisibility(View.VISIBLE);
                tabHistoryIndicator.setBackgroundColor(Color.WHITE);
                tabHistory.setTextColor(Color.WHITE);
                tabHistory.setTypeface(null, android.graphics.Typeface.BOLD);
                break;
            case CREATE:
                panelCreate.setVisibility(View.VISIBLE);
                tabCreateIndicator.setBackgroundColor(Color.WHITE);
                tabCreate.setTextColor(Color.WHITE);
                tabCreate.setTypeface(null, android.graphics.Typeface.BOLD);
                break;
        }
    }

    private void refreshCurrent() {
        if (activeTab == Tab.REQUESTS) loadQuotationRequests();
        else if (activeTab == Tab.HISTORY) loadNotificationHistory();
    }

    // ─── Load Customer Quotation Requests ─────────────────────────────────────

    private void loadQuotationRequests() {
        progressRequests.setVisibility(View.VISIBLE);
        tvEmptyRequests.setVisibility(View.GONE);
        rvQuotationRequests.setVisibility(View.GONE);

        ApiService api = RetrofitClient.getApiService();
        api.getEnquiries().enqueue(new Callback<List<Enquiry>>() {
            @Override
            public void onResponse(Call<List<Enquiry>> call, Response<List<Enquiry>> response) {
                progressRequests.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    List<Enquiry> list = response.body();
                    requestAdapter.updateList(list);

                    // Count by status
                    int newCount = 0, quotedCount = 0, resolvedCount = 0;
                    for (Enquiry e : list) {
                        String s = e.getStatus() != null ? e.getStatus().toLowerCase() : "new";
                        if ("new".equals(s)) newCount++;
                        else if ("quoted".equals(s)) quotedCount++;
                        else if ("resolved".equals(s)) resolvedCount++;
                    }
                    tvNewCount.setText(String.valueOf(newCount));
                    tvQuotedCount.setText(String.valueOf(quotedCount));
                    tvResolvedCount.setText(String.valueOf(resolvedCount));

                    // Badge on tab
                    if (newCount > 0) {
                        tvRequestBadge.setText(String.valueOf(newCount));
                        tvRequestBadge.setVisibility(View.VISIBLE);
                    } else {
                        tvRequestBadge.setVisibility(View.GONE);
                    }

                    if (list.isEmpty()) {
                        tvEmptyRequests.setVisibility(View.VISIBLE);
                    } else {
                        rvQuotationRequests.setVisibility(View.VISIBLE);
                    }
                } else {
                    tvEmptyRequests.setText("Failed to load requests");
                    tvEmptyRequests.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<List<Enquiry>> call, Throwable t) {
                progressRequests.setVisibility(View.GONE);
                tvEmptyRequests.setText("Network error");
                tvEmptyRequests.setVisibility(View.VISIBLE);
            }
        });
    }

    private void onRespondClicked(Enquiry enquiry) {
        Intent intent = new Intent(this, RespondEnquiryActivity.class);
        intent.putExtra("enquiry", enquiry);
        startActivityForResult(intent, 101);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101) {
            // Refresh after responding
            loadQuotationRequests();
        }
    }

    // ─── Load Sent History ────────────────────────────────────────────────────

    private void loadNotificationHistory() {
        progressBar.setVisibility(View.VISIBLE);
        tvEmpty.setVisibility(View.GONE);
        rvAdminNotifications.setVisibility(View.GONE);

        ApiService api = RetrofitClient.getApiService();
        api.getAdminNotifications().enqueue(new Callback<List<AdminNotificationItem>>() {
            @Override
            public void onResponse(Call<List<AdminNotificationItem>> call,
                                   Response<List<AdminNotificationItem>> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    List<AdminNotificationItem> list = response.body();
                    notifAdapter.updateList(list);
                    tvTotalSent.setText(String.valueOf(list.size()));

                    // Today count
                    String today = new java.text.SimpleDateFormat("yyyy-MM-dd",
                            java.util.Locale.getDefault()).format(new java.util.Date());
                    long todayCount = 0;
                    for (AdminNotificationItem n : list) {
                        if (n.getTimestamp() != null && n.getTimestamp().startsWith(today)) todayCount++;
                    }
                    tvTodaySent.setText(String.valueOf(todayCount));

                    if (list.isEmpty()) {
                        tvEmpty.setVisibility(View.VISIBLE);
                    } else {
                        rvAdminNotifications.setVisibility(View.VISIBLE);
                    }
                } else {
                    tvEmpty.setText("Failed to load notifications");
                    tvEmpty.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<List<AdminNotificationItem>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                tvEmpty.setText("Network error");
                tvEmpty.setVisibility(View.VISIBLE);
            }
        });
    }

    // ─── Type Chips ───────────────────────────────────────────────────────────

    private void setupTypeChips() {
        View.OnClickListener chipClick = v -> {
            if (v.getId() == R.id.chipSystem)       selectedType = "system";
            else if (v.getId() == R.id.chipPromotion) selectedType = "promotion";
            else if (v.getId() == R.id.chipAlert)     selectedType = "alert";
            else if (v.getId() == R.id.chipAnnouncement) selectedType = "announcement";
            updateChipStyles();
        };
        chipSystem.setOnClickListener(chipClick);
        chipPromotion.setOnClickListener(chipClick);
        chipAlert.setOnClickListener(chipClick);
        chipAnnouncement.setOnClickListener(chipClick);
    }

    private void updateChipStyles() {
        TextView[] chips = {chipSystem, chipPromotion, chipAlert, chipAnnouncement};
        String[]   types = {"system", "promotion", "alert", "announcement"};
        for (int i = 0; i < chips.length; i++) {
            if (types[i].equals(selectedType)) {
                chips[i].setBackgroundResource(R.drawable.bg_button_primary);
                chips[i].setTextColor(Color.WHITE);
            } else {
                chips[i].setBackgroundResource(R.drawable.bg_filter_inactive);
                chips[i].setTextColor(Color.parseColor("#64748B"));
            }
        }
    }

    // ─── Live Preview ─────────────────────────────────────────────────────────

    private void setupPreviewListeners() {
        TextWatcher watcher = new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String t = etTitle.getText().toString().trim();
                String m = etMessage.getText().toString().trim();
                tvPreviewTitle.setText(t.isEmpty() ? "Notification Title" : t);
                tvPreviewMessage.setText(m.isEmpty() ? "Your notification message will appear here." : m);
            }
        };
        etTitle.addTextChangedListener(watcher);
        etMessage.addTextChangedListener(watcher);
    }

    // ─── Send Notification ────────────────────────────────────────────────────

    // ─── Product Tag Search ───────────────────────────────────────────────────

    private void setupProductSearch() {
        etProductSearch.addTextChangedListener(new android.text.TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(android.text.Editable s) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String query = s.toString().trim();
                if (searchRunnable != null) searchHandler.removeCallbacks(searchRunnable);
                if (query.isEmpty()) {
                    scrollProductResults.setVisibility(android.view.View.GONE);
                    return;
                }
                searchRunnable = () -> searchProducts(query);
                searchHandler.postDelayed(searchRunnable, 400); // 400ms debounce
            }
        });

        btnRemoveProduct.setOnClickListener(v -> clearSelectedProduct());
    }

    private void searchProducts(String query) {
        progressProductSearch.setVisibility(android.view.View.VISIBLE);
        RetrofitClient.getApiService().getProducts().enqueue(
            new retrofit2.Callback<java.util.List<com.example.florra_a.models.Product>>() {
                @Override
                public void onResponse(retrofit2.Call<java.util.List<com.example.florra_a.models.Product>> call,
                                       retrofit2.Response<java.util.List<com.example.florra_a.models.Product>> response) {
                    progressProductSearch.setVisibility(android.view.View.GONE);
                    if (response.isSuccessful() && response.body() != null) {
                        // Filter locally by query
                        searchResults.clear();
                        String lq = query.toLowerCase();
                        for (com.example.florra_a.models.Product p : response.body()) {
                            String name = p.getTileName() != null ? p.getTileName().toLowerCase() : "";
                            String cat  = p.getCategory() != null ? p.getCategory().toLowerCase() : "";
                            if (name.contains(lq) || cat.contains(lq)) searchResults.add(p);
                        }
                        showProductChips();
                    }
                }
                @Override
                public void onFailure(retrofit2.Call<java.util.List<com.example.florra_a.models.Product>> call, Throwable t) {
                    progressProductSearch.setVisibility(android.view.View.GONE);
                }
            });
    }

    private void showProductChips() {
        llProductChips.removeAllViews();
        if (searchResults.isEmpty()) {
            scrollProductResults.setVisibility(android.view.View.GONE);
            return;
        }
        scrollProductResults.setVisibility(android.view.View.VISIBLE);

        android.content.Context ctx = this;
        for (com.example.florra_a.models.Product p : searchResults) {
            // Build chip TextView
            android.widget.TextView chip = new android.widget.TextView(ctx);
            chip.setText("🟩 " + p.getTileName());
            chip.setTextColor(android.graphics.Color.parseColor("#014D4E"));
            chip.setTextSize(13f);
            chip.setBackground(getResources().getDrawable(R.drawable.bg_filter_inactive, null));

            android.widget.LinearLayout.LayoutParams lp =
                new android.widget.LinearLayout.LayoutParams(
                    android.widget.LinearLayout.LayoutParams.WRAP_CONTENT,
                    android.widget.LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.setMarginEnd((int)(8 * getResources().getDisplayMetrics().density));
            chip.setLayoutParams(lp);
            int pad = (int)(10 * getResources().getDisplayMetrics().density);
            int padV = (int)(6 * getResources().getDisplayMetrics().density);
            chip.setPadding(pad, padV, pad, padV);
            chip.setClickable(true);
            chip.setFocusable(true);

            final com.example.florra_a.models.Product selected = p;
            chip.setOnClickListener(v -> selectProduct(selected));
            llProductChips.addView(chip);
        }
    }

    private void selectProduct(com.example.florra_a.models.Product p) {
        selectedProduct = p;
        // Hide search results
        scrollProductResults.setVisibility(android.view.View.GONE);
        etProductSearch.setText("");
        android.view.inputmethod.InputMethodManager imm =
            (android.view.inputmethod.InputMethodManager) getSystemService(android.content.Context.INPUT_METHOD_SERVICE);
        if (imm != null) imm.hideSoftInputFromWindow(etProductSearch.getWindowToken(), 0);

        // Show selected card
        tvSelectedProductName.setText(p.getTileName());
        String info = (p.getCategory() != null ? p.getCategory() : "") +
                      (p.getSize() != null ? " · " + p.getSize() : "");
        tvSelectedProductInfo.setText(info);

        // Load product image with Glide
        if (p.getImage() != null && !p.getImage().isEmpty()) {
            com.bumptech.glide.Glide.with(this)
                .load(p.getImage())
                .placeholder(R.drawable.bg_filter_inactive)
                .centerCrop()
                .into(ivSelectedProduct);
        }

        cardSelectedProduct.setVisibility(android.view.View.VISIBLE);
    }

    private void clearSelectedProduct() {
        selectedProduct = null;
        cardSelectedProduct.setVisibility(android.view.View.GONE);
    }

    // ─────────────────────────────────────────────────────────────────────────

    private void setupSendButton() {
        btnSendNotification.setOnClickListener(v -> {
            String title   = etTitle.getText().toString().trim();
            String message = etMessage.getText().toString().trim();
            if (title.isEmpty())   { etTitle.setError("Title is required");   etTitle.requestFocus();   return; }
            if (message.isEmpty()) { etMessage.setError("Message is required"); etMessage.requestFocus(); return; }
            sendNotification(title, message, selectedType);
        });
    }

    private void sendNotification(String title, String message, String type) {
        btnProgress.setVisibility(View.VISIBLE);
        btnSendIcon.setVisibility(View.GONE);
        tvSendLabel.setText("Sending...");
        btnSendNotification.setClickable(false);

        Map<String, String> body = new HashMap<>();
        body.put("title", title);
        // Append product tag to message if a product is selected
        if (selectedProduct != null) {
            body.put("message", message + "\n\n🟩 Product: " + selectedProduct.getTileName() +
                    (selectedProduct.getSize() != null ? " (" + selectedProduct.getSize() + ")" : ""));
            body.put("product_id", String.valueOf(selectedProduct.getId()));
            body.put("product_name", selectedProduct.getTileName());
        } else {
            body.put("message", message);
        }
        body.put("type", type);

        RetrofitClient.getApiService().createAdminNotification(body).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                resetSendButton();
                if (response.isSuccessful()) {
                    Toast.makeText(AdminNotificationsActivity.this,
                            "✅ Notification sent to all customers!", Toast.LENGTH_SHORT).show();
                    etTitle.setText("");
                    etMessage.setText("");
                    clearSelectedProduct();
                    switchTab(Tab.HISTORY);
                    loadNotificationHistory();
                } else {
                    Toast.makeText(AdminNotificationsActivity.this,
                            "Failed. Try again.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                resetSendButton();
                Toast.makeText(AdminNotificationsActivity.this,
                        "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void resetSendButton() {
        btnProgress.setVisibility(View.GONE);
        btnSendIcon.setVisibility(View.VISIBLE);
        tvSendLabel.setText("Send to All Customers");
        btnSendNotification.setClickable(true);
    }
}
