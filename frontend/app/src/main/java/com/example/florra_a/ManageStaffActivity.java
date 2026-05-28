package com.example.florra_a;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.florra_a.adapters.StaffAdapter;
import com.example.florra_a.models.StaffMember;
import com.example.florra_a.network.RetrofitClient;
import java.util.ArrayList;
import java.util.List;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ManageStaffActivity extends AppCompatActivity {

    private ImageButton btnBack;
    private ImageButton btnAddStaff;
    private EditText etSearch;
    private RecyclerView rvStaff;
    
    private StaffAdapter adapter;
    private List<StaffMember> staffList = new ArrayList<>();
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_staff);

        // Customize status bar color to match premium white aesthetic
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            getWindow().setStatusBarColor(android.graphics.Color.WHITE);
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        initViews();
        setupRecyclerView();
        setupListeners();
        
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading staff members...");
        progressDialog.setCancelable(false);

        fetchStaffMembers();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        btnAddStaff = findViewById(R.id.btnAddStaff);
        etSearch = findViewById(R.id.etSearch);
        rvStaff = findViewById(R.id.rvStaff);
    }

    private void setupRecyclerView() {
        rvStaff.setLayoutManager(new LinearLayoutManager(this));
        adapter = new StaffAdapter(this, staffList, new StaffAdapter.OnStaffActionListener() {
            @Override
            public void onEdit(StaffMember staff) {
                showEditStaffDialog(staff);
            }

            @Override
            public void onDelete(StaffMember staff) {
                showDeleteConfirmationDialog(staff);
            }
        });
        rvStaff.setAdapter(adapter);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());
        
        btnAddStaff.setOnClickListener(v -> showAddStaffDialog());

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void fetchStaffMembers() {
        progressDialog.show();
        RetrofitClient.getApiService().getStaff().enqueue(new Callback<List<StaffMember>>() {
            @Override
            public void onResponse(Call<List<StaffMember>> call, Response<List<StaffMember>> response) {
                progressDialog.dismiss();
                if (response.isSuccessful() && response.body() != null) {
                    staffList = response.body();
                    adapter.updateData(staffList);
                    // Filter again if user has typed something
                    adapter.filter(etSearch.getText().toString());
                } else {
                    Toast.makeText(ManageStaffActivity.this, "Failed to load staff list", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<StaffMember>> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(ManageStaffActivity.this, "Network Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showAddStaffDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_staff, null);
        builder.setView(dialogView);

        EditText etName = dialogView.findViewById(R.id.etName);
        EditText etEmail = dialogView.findViewById(R.id.etEmail);
        EditText etPassword = dialogView.findViewById(R.id.etPassword);
        Spinner spRole = dialogView.findViewById(R.id.spRole);
        android.widget.Switch swBilling = dialogView.findViewById(R.id.swBilling);
        android.widget.Switch swReports = dialogView.findViewById(R.id.swReports);
        android.widget.Switch swPredictions = dialogView.findViewById(R.id.swPredictions);

        // Populate spinner
        String[] roles = {"staff", "admin"};
        ArrayAdapter<String> roleAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, roles);
        roleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spRole.setAdapter(roleAdapter);

        builder.setTitle("Add New Staff");
        builder.setPositiveButton("Create", (dialog, which) -> {
            String name = etName.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            String role = spRole.getSelectedItem().toString();
            boolean billingAccess = swBilling != null && swBilling.isChecked();
            boolean reportsAccess = swReports != null && swReports.isChecked();
            boolean predictionsAccess = swPredictions != null && swPredictions.isChecked();

            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(ManageStaffActivity.this, "All fields are required", Toast.LENGTH_SHORT).show();
                return;
            }

            StaffMember newMember = new StaffMember(name, email, password, role, billingAccess, reportsAccess, predictionsAccess);
            createStaffMember(newMember);
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }

    private void createStaffMember(StaffMember member) {
        progressDialog.setMessage("Creating staff member...");
        progressDialog.show();

        RetrofitClient.getApiService().addStaff(member).enqueue(new Callback<StaffMember>() {
            @Override
            public void onResponse(Call<StaffMember> call, Response<StaffMember> response) {
                progressDialog.dismiss();
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(ManageStaffActivity.this, "Staff member created successfully", Toast.LENGTH_SHORT).show();
                    fetchStaffMembers();
                } else {
                    Toast.makeText(ManageStaffActivity.this, "Failed to create staff member", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<StaffMember> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(ManageStaffActivity.this, "Network Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showEditStaffDialog(StaffMember staff) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_staff, null);
        builder.setView(dialogView);

        EditText etName = dialogView.findViewById(R.id.etName);
        EditText etEmail = dialogView.findViewById(R.id.etEmail);
        EditText etPassword = dialogView.findViewById(R.id.etPassword);
        Spinner spRole = dialogView.findViewById(R.id.spRole);
        android.widget.Switch swBilling = dialogView.findViewById(R.id.swBilling);
        android.widget.Switch swReports = dialogView.findViewById(R.id.swReports);
        android.widget.Switch swPredictions = dialogView.findViewById(R.id.swPredictions);

        // Pre-fill values
        etName.setText(staff.getFullName());
        etEmail.setText(staff.getEmail());
        etPassword.setHint("Leave blank to keep unchanged");
        if (swBilling != null) swBilling.setChecked(staff.isCanAccessBilling());
        if (swReports != null) swReports.setChecked(staff.isCanAccessReports());
        if (swPredictions != null) swPredictions.setChecked(staff.isCanAccessPredictions());

        // Populate spinner
        String[] roles = {"staff", "admin"};
        ArrayAdapter<String> roleAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, roles);
        roleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spRole.setAdapter(roleAdapter);

        if ("admin".equalsIgnoreCase(staff.getRole())) {
            spRole.setSelection(1);
        } else {
            spRole.setSelection(0);
        }

        builder.setTitle("Edit Staff Member");
        builder.setPositiveButton("Save", (dialog, which) -> {
            String name = etName.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            String role = spRole.getSelectedItem().toString();
            boolean billingAccess = swBilling != null && swBilling.isChecked();
            boolean reportsAccess = swReports != null && swReports.isChecked();
            boolean predictionsAccess = swPredictions != null && swPredictions.isChecked();

            if (name.isEmpty() || email.isEmpty()) {
                Toast.makeText(ManageStaffActivity.this, "Name and Email are required", Toast.LENGTH_SHORT).show();
                return;
            }

            staff.setFullName(name);
            staff.setEmail(email);
            staff.setRole(role);
            staff.setCanAccessBilling(billingAccess);
            staff.setCanAccessReports(reportsAccess);
            staff.setCanAccessPredictions(predictionsAccess);
            if (!password.isEmpty()) {
                staff.setPassword(password);
            } else {
                staff.setPassword(null); // Backend won't change password if null or missing
            }

            updateStaffMember(staff);
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }

    private void updateStaffMember(StaffMember staff) {
        progressDialog.setMessage("Updating staff details...");
        progressDialog.show();

        RetrofitClient.getApiService().updateStaff(staff.getId(), staff).enqueue(new Callback<StaffMember>() {
            @Override
            public void onResponse(Call<StaffMember> call, Response<StaffMember> response) {
                progressDialog.dismiss();
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(ManageStaffActivity.this, "Staff updated successfully", Toast.LENGTH_SHORT).show();
                    fetchStaffMembers();
                } else {
                    Toast.makeText(ManageStaffActivity.this, "Failed to update staff details", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<StaffMember> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(ManageStaffActivity.this, "Network Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showDeleteConfirmationDialog(StaffMember staff) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Staff")
                .setMessage("Are you sure you want to delete " + staff.getFullName() + "?")
                .setPositiveButton("Delete", (dialog, which) -> deleteStaffMember(staff))
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteStaffMember(StaffMember staff) {
        progressDialog.setMessage("Deleting staff member...");
        progressDialog.show();

        RetrofitClient.getApiService().deleteStaff(staff.getId()).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                progressDialog.dismiss();
                if (response.isSuccessful()) {
                    Toast.makeText(ManageStaffActivity.this, "Staff deleted successfully", Toast.LENGTH_SHORT).show();
                    fetchStaffMembers();
                } else {
                    Toast.makeText(ManageStaffActivity.this, "Failed to delete staff member", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(ManageStaffActivity.this, "Network Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
