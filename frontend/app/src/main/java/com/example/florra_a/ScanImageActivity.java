package com.example.florra_a;

import android.app.AlertDialog;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.example.florra_a.models.Product;
import com.example.florra_a.network.ApiService;
import com.example.florra_a.network.RetrofitClient;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;

public class ScanImageActivity extends AppCompatActivity {

    private static final String TAG = "ScanImageActivity";
    private static final int REQUEST_CAMERA = 1;
    private static final int REQUEST_GALLERY = 2;

    private String currentPhotoPath;
    private AlertDialog processingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set fullscreen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
                // Set status bar to white with dark icons
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(android.view.View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(android.graphics.Color.WHITE);
        }

        setContentView(R.layout.activity_scan_image);

        setupClickListeners();
        loadRecentScans();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadRecentScans();
    }

    private void setupClickListeners() {
        // Back Button
        View btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> onBackPressed());
        }

        // Upload Card
        View uploadCard = findViewById(R.id.uploadCard);
        if (uploadCard != null) {
            uploadCard.setOnClickListener(v -> showImageSourceDialog());
        }

        // Choose Image Button
        View btnChooseImage = findViewById(R.id.btnChooseImage);
        if (btnChooseImage != null) {
            btnChooseImage.setOnClickListener(v -> showImageSourceDialog());
        }

        // View All Button
        View btnViewAll = findViewById(R.id.btnViewAll);
        if (btnViewAll != null) {
            btnViewAll.setOnClickListener(v -> 
                Toast.makeText(ScanImageActivity.this, "View All Recent Scans", Toast.LENGTH_SHORT).show());
        }

        // Recent Scans (Mock logic removed)
    }

    private void loadRecentScans() {
        android.content.SharedPreferences prefs = getSharedPreferences("recent_scans", MODE_PRIVATE);
        Gson gson = new Gson();
        
        ImageView[] imgViews = {findViewById(R.id.imgScan1), findViewById(R.id.imgScan2), findViewById(R.id.imgScan3)};
        android.widget.TextView[] tvViews = {findViewById(R.id.tvScan1), findViewById(R.id.tvScan2), findViewById(R.id.tvScan3)};
        View[] containers = {findViewById(R.id.scan1), findViewById(R.id.scan2), findViewById(R.id.scan3)};
        
        for (int i = 0; i < 3; i++) {
            String path = prefs.getString("scan_path_" + (i + 1), null);
            String name = prefs.getString("scan_name_" + (i + 1), null);
            String resultsJson = prefs.getString("scan_results_" + (i + 1), null);
            
            if (path != null && new File(path).exists()) {
                com.bumptech.glide.Glide.with(this)
                    .load(new File(path))
                    .into(imgViews[i]);
                if (name != null) tvViews[i].setText(name);
                
                // Set click listener to re-open results
                if (resultsJson != null) {
                    final int index = i;
                    final String finalPath = path;
                    View.OnClickListener listener = v -> {
                        Toast.makeText(ScanImageActivity.this, "Loading saved results...", Toast.LENGTH_SHORT).show();
                        android.util.Log.d("ScanImage", "Clicking recent scan: " + finalPath);
                        Type listType = new TypeToken<List<Product>>(){}.getType();
                        List<Product> recommendations = gson.fromJson(resultsJson, listType);
                        if (recommendations != null && !recommendations.isEmpty()) {
                            android.util.Log.d("ScanImage", "Recommendations found: " + recommendations.size());
                            Intent intent = new Intent(ScanImageActivity.this, AIRecommendationActivity.class);
                            intent.putExtra("recommendations", (Serializable) recommendations);
                            startActivity(intent);
                            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        } else {
                             android.util.Log.w("ScanImage", "No recommendations found for this scan");
                             Toast.makeText(ScanImageActivity.this, "No saved results for this scan", Toast.LENGTH_SHORT).show();
                        }
                    };
                    containers[i].setOnClickListener(listener);
                    imgViews[i].setOnClickListener(listener);

                    // Add long press to delete
                    final int deleteIndex = i + 1;
                    View.OnLongClickListener longListener = v -> {
                        showDeleteConfirmation(deleteIndex);
                        return true;
                    };
                    containers[i].setOnLongClickListener(longListener);
                    imgViews[i].setOnLongClickListener(longListener);
                }
            } else {
                imgViews[i].setImageResource(R.drawable.tile_placeholder);
                tvViews[i].setText("Empty Scan");
                containers[i].setOnClickListener(null);
            }
        }
    }

    private void showDeleteConfirmation(int index) {
        new AlertDialog.Builder(this)
            .setTitle("Delete Scan")
            .setMessage("Are you sure you want to delete this scan from history?")
            .setPositiveButton("Delete", (dialog, which) -> deleteRecentScan(index))
            .setNegativeButton("Cancel", null)
            .show();
    }

    private void deleteRecentScan(int index) {
        android.content.SharedPreferences prefs = getSharedPreferences("recent_scans", MODE_PRIVATE);
        android.content.SharedPreferences.Editor editor = prefs.edit();
        
        // Remove the selected scan
        editor.remove("scan_path_" + index);
        editor.remove("scan_name_" + index);
        editor.remove("scan_results_" + index);
        
        // Shift remaining scans up
        for (int i = index; i < 3; i++) {
            String nextPath = prefs.getString("scan_path_" + (i + 1), null);
            String nextName = prefs.getString("scan_name_" + (i + 1), null);
            String nextResults = prefs.getString("scan_results_" + (i + 1), null);
            
            if (nextPath != null) {
                editor.putString("scan_path_" + i, nextPath);
                editor.putString("scan_name_" + i, nextName);
                editor.putString("scan_results_" + i, nextResults);
                
                // Clear the next one
                editor.remove("scan_path_" + (i + 1));
                editor.remove("scan_name_" + (i + 1));
                editor.remove("scan_results_" + (i + 1));
            } else {
                editor.remove("scan_path_" + i);
                editor.remove("scan_name_" + i);
                editor.remove("scan_results_" + i);
            }
        }
        
        editor.apply();
        Toast.makeText(this, "Scan deleted", Toast.LENGTH_SHORT).show();
        loadRecentScans();
    }

    private void saveRecentScan(String path, List<Product> results) {
        android.content.SharedPreferences prefs = getSharedPreferences("recent_scans", MODE_PRIVATE);
        android.content.SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String resultsJson = gson.toJson(results);
        
        // Shift existing scans
        for (int i = 2; i >= 1; i--) {
            String prevPath = prefs.getString("scan_path_" + i, null);
            String prevName = prefs.getString("scan_name_" + i, null);
            String prevResults = prefs.getString("scan_results_" + i, null);
            
            if (prevPath != null) {
                editor.putString("scan_path_" + (i + 1), prevPath);
                editor.putString("scan_name_" + (i + 1), prevName);
                editor.putString("scan_results_" + (i + 1), prevResults);
            }
        }
        
        String timeStamp = new SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault()).format(new Date());
        editor.putString("scan_path_1", path);
        editor.putString("scan_name_1", "Scan " + timeStamp);
        editor.putString("scan_results_1", resultsJson);
        
        editor.apply();
        Toast.makeText(this, "Scan history updated", Toast.LENGTH_SHORT).show();
        loadRecentScans();
    }

    private void showImageSourceDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Image Source");
        builder.setItems(new String[]{"Camera", "Gallery"}, (dialog, which) -> {
            if (which == 0) {
                openCamera();
            } else {
                openGallery();
            }
        });
        builder.show();
    }

    private void openCamera() {
        Log.d(TAG, "openCamera() called");
        if (androidx.core.content.ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
                != android.content.pm.PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Requesting camera permission");
            androidx.core.app.ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.CAMERA}, REQUEST_CAMERA);
            return;
        }

        try {
            Log.d(TAG, "Creating camera intent");
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            // Removed resolveActivity check as it's unreliable on Android 11+
            File photoFile = createImageFile();
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        getApplicationContext().getPackageName() + ".fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_CAMERA);
            } else {
                Log.e(TAG, "Failed to create image file");
            }
        } catch (Exception e) {
            Log.e(TAG, "Camera Error", e);
            e.printStackTrace();
            Toast.makeText(this, "Camera Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA) {
            if (grantResults.length > 0 && grantResults[0] == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "Camera permission granted");
                openCamera();
            } else {
                Log.w(TAG, "Camera permission denied");
                Toast.makeText(this, "Camera permission is required to scan tiles", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_GALLERY);
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CAMERA) {
                uploadImage(new File(currentPhotoPath));
            } else if (requestCode == REQUEST_GALLERY && data != null) {
                Uri selectedImageUri = data.getData();
                if (selectedImageUri != null) {
                    // Need to create a file from URI to upload
                    try {
                         File file = FileUtil.from(this, selectedImageUri);
                         uploadImage(file);
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Failed to process image", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }
    
    // Simple FileUtil helper or inline implementation for URI to File
    public static class FileUtil {
        public static File from(android.content.Context context, Uri uri) throws IOException {
            java.io.InputStream inputStream = context.getContentResolver().openInputStream(uri);
            String fileName = "upload_" + System.currentTimeMillis() + ".jpg";
            File file = new File(context.getCacheDir(), fileName);
            FileOutputStream outputStream = new FileOutputStream(file);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
            outputStream.close();
            inputStream.close();
            return file;
        }
    }

    private void uploadImage(File file) {
        showProcessingDialog();
        
        // Compress if needed (optional optimization)
        
        RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpeg"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("image", file.getName(), requestFile);

        ApiService apiService = RetrofitClient.getApiService();
        Call<List<Product>> call = apiService.searchByImage(body);
        
        call.enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (processingDialog != null && processingDialog.isShowing()) {
                    processingDialog.dismiss();
                }
                
                if (response.isSuccessful() && response.body() != null) {
                    List<Product> recommendations = response.body();
                    if (recommendations.isEmpty()) {
                        Toast.makeText(ScanImageActivity.this, "No matching products found", Toast.LENGTH_SHORT).show();
                    } else {
                        // Log first product details
                        Product first = recommendations.get(0);
                        android.util.Log.d("ScanImage", "First Product: " + first.getTileName() + ", ID: " + first.getId());
                        android.util.Log.d("ScanImage", "Image URL: " + first.getImage());
                        
                        // Save to recent scans with results
                        saveRecentScan(file.getAbsolutePath(), recommendations);

                        // Navigate to AIRecommendationActivity with results
                        Intent intent = new Intent(ScanImageActivity.this, AIRecommendationActivity.class);
                        // Pass as Serializable (Product implements Serializable)
                        intent.putExtra("recommendations", (Serializable) recommendations);
                        startActivity(intent);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    }
                } else {
                    Toast.makeText(ScanImageActivity.this, "Failed to get recommendations", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                if (processingDialog != null && processingDialog.isShowing()) {
                    processingDialog.dismiss();
                }
                Toast.makeText(ScanImageActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                android.util.Log.e("ScanImage", "Upload error", t);
            }
        });
    }

    private void showProcessingDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Processing Image");
        builder.setMessage("AI is finding matching tiles...");
        builder.setCancelable(false);
        processingDialog = builder.create();
        processingDialog.show();
    }

    private void openScanDetails(String scanName) {
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}