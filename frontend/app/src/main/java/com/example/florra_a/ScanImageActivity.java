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
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsControllerCompat;

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

public class ScanImageActivity extends AppCompatActivity {

    private static final String TAG = "ScanImageActivity";
    private static final int REQUEST_CAMERA = 1;
    private static final int REQUEST_GALLERY = 2;

    private String currentPhotoPath;
    private AlertDialog processingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Enable edge-to-edge
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);

        // Handle notch and status bar - Set to true for dark icons on light background
        WindowInsetsControllerCompat windowInsetsController =
                WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());
        if (windowInsetsController != null) {
            windowInsetsController.setAppearanceLightStatusBars(true);
            windowInsetsController.setAppearanceLightNavigationBars(true);
        }

        setContentView(R.layout.activity_scan_image);

        setupClickListeners();
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

        // Recent Scans (Mock logic kept)
        findViewById(R.id.scan1).setOnClickListener(v -> openScanDetails("Marble Hexagon"));
        findViewById(R.id.scan2).setOnClickListener(v -> openScanDetails("Beige Ceramic"));
        findViewById(R.id.scan3).setOnClickListener(v -> openScanDetails("Slate Grey"));
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
        if (androidx.core.content.ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
                != android.content.pm.PackageManager.PERMISSION_GRANTED) {
            androidx.core.app.ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.CAMERA}, REQUEST_CAMERA);
            return;
        }

        try {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                File photoFile = createImageFile();
                if (photoFile != null) {
                    Uri photoURI = FileProvider.getUriForFile(this,
                            getApplicationContext().getPackageName() + ".fileprovider",
                            photoFile);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    startActivityForResult(takePictureIntent, REQUEST_CAMERA);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Camera Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
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
        Toast.makeText(this, "Opening: " + scanName, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}
