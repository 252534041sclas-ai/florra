package com.example.florra_a;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AlertDialog;
import android.content.SharedPreferences;
import android.app.ProgressDialog;
import com.example.florra_a.models.UpdateProfileRequest;
import com.example.florra_a.models.AuthResponse;
import com.example.florra_a.network.RetrofitClient;
import com.example.florra_a.network.ApiService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import android.Manifest;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class EditProfileActivity extends AppCompatActivity {
    
    private static final int REQUEST_CAMERA_PERMISSION = 101;
    private static final int REQUEST_STORAGE_PERMISSION = 102;
    private static final int REQUEST_IMAGE_CAPTURE = 103;
    private static final int REQUEST_PICK_IMAGE = 104;

    private ImageView ivProfile;
    private Uri photoURI;
    private String currentPhotoPath;
    private Uri galleryUri;
    private ProgressDialog progressDialog;

    private EditText etFullName, etEmail, etMobile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_edit_profile);

        etFullName = findViewById(R.id.etFullName);
        etEmail = findViewById(R.id.etEmail);
        etFullName = findViewById(R.id.etFullName);
        etEmail = findViewById(R.id.etEmail);
        etMobile = findViewById(R.id.etMobile);
        ivProfile = findViewById(R.id.ivProfile);

        setupClickListeners();
        setupNavigationButtons();
        
        loadUserData();
    }
    
    private void loadUserData() {
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        String fullName = prefs.getString("full_name", "");
        String email = prefs.getString("email", "");
        String mobile = prefs.getString("mobile", "");
        
        etFullName.setText(fullName);
        etEmail.setText(email);
        etMobile.setText(mobile);
        
        String profileImageUrl = prefs.getString("profile_image", null);
        if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
            String fullUrl = profileImageUrl;
            if (!fullUrl.startsWith("http")) {
                fullUrl = com.example.florra_a.network.RetrofitClient.BASE_URL + fullUrl;
            }
             // Fix double slash issue if present
             fullUrl = fullUrl.replace(com.example.florra_a.network.RetrofitClient.BASE_URL + "/", com.example.florra_a.network.RetrofitClient.BASE_URL);

            ivProfile.setColorFilter(null); // Clear tint for existing image
            Glide.with(this)
                .load(fullUrl)
                .placeholder(R.drawable.ic_person_large)
                .error(R.drawable.ic_person_large)
                .circleCrop()
                .into(ivProfile);
        }
    }

    private void setupClickListeners() {
        findViewById(R.id.btnBack).setOnClickListener(v -> goBack());
        findViewById(R.id.btnCancel).setOnClickListener(v -> goBack());
        findViewById(R.id.btnChangePhoto).setOnClickListener(v -> showImageSourceDialog());
        findViewById(R.id.btnSaveChanges).setOnClickListener(v -> saveProfileChanges());
    }
    
    private void showImageSourceDialog() {
        String[] options = {"Take Photo", "Choose from Gallery", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Change Profile Photo");
        builder.setItems(options, (dialog, which) -> {
            if (which == 0) {
                checkCameraPermission();
            } else if (which == 1) {
                openGallery();
            } else {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    private void checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
        } else {
            openCamera();
        }
    }

    private void openCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Toast.makeText(this, "Error creating file", Toast.LENGTH_SHORT).show();
            }
            if (photoFile != null) {
                photoURI = FileProvider.getUriForFile(this,
                        getApplicationContext().getPackageName() + ".fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_PICK_IMAGE);
    }
    
    @Override
    public void onRequestPermissionsResult(int requestCode, @androidx.annotation.NonNull String[] permissions, @androidx.annotation.NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                Toast.makeText(this, "Camera permission needed to take photos.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_CAPTURE) {
                // Image captured from camera
                galleryUri = null; // Clear gallery selection
                ivProfile.clearColorFilter(); // Remove the gray tint
                Glide.with(this)
                    .load(photoURI)
                    .placeholder(R.drawable.ic_person_large)
                    .error(R.drawable.ic_person_large)
                    .circleCrop()
                    .into(ivProfile);
                Toast.makeText(this, "Photo captured", Toast.LENGTH_SHORT).show();
            } else if (requestCode == REQUEST_PICK_IMAGE && data != null) {
                // Image picked from gallery
                galleryUri = data.getData();
                currentPhotoPath = null; // Clear camera selection
                ivProfile.clearColorFilter(); // Remove the gray tint
                Glide.with(this)
                    .load(galleryUri)
                    .placeholder(R.drawable.ic_person_large)
                    .error(R.drawable.ic_person_large)
                    .circleCrop()
                    .into(ivProfile);
                Toast.makeText(this, "Image selected", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void setupNavigationButtons() {
        LinearLayout btnNavHome = findViewById(R.id.btnNavHome);
        LinearLayout btnNavCatalog = findViewById(R.id.btnNavCatalog);
        LinearLayout btnNavEnquiries = findViewById(R.id.btnNavEnquiries);
        LinearLayout btnNavAccount = findViewById(R.id.btnNavAccount);

        if (btnNavHome != null) btnNavHome.setOnClickListener(v -> openHomeScreen());
        if (btnNavCatalog != null) btnNavCatalog.setOnClickListener(v -> openCatalogScreen());
        if (btnNavEnquiries != null) btnNavEnquiries.setOnClickListener(v -> Toast.makeText(this, "Enquiries", Toast.LENGTH_SHORT).show());
        if (btnNavAccount != null) btnNavAccount.setOnClickListener(v -> goBack());
    }

    private void saveProfileChanges() {
        String fullName = etFullName.getText().toString().trim();
        String mobile = etMobile.getText().toString().trim();

        if (fullName.isEmpty()) {
            Toast.makeText(this, "Please enter full name", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Updating profile...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        // Create RequestBody for text fields
        RequestBody nameBody = RequestBody.create(MediaType.parse("text/plain"), fullName);
        RequestBody mobileBody = RequestBody.create(MediaType.parse("text/plain"), mobile);

        MultipartBody.Part imagePart = null;

        // Prepare image part
        try {
            if (currentPhotoPath != null) {
                File file = new File(currentPhotoPath);
                if (file.exists()) {
                    RequestBody reqFile = RequestBody.create(MediaType.parse("image/jpeg"), file);
                    imagePart = MultipartBody.Part.createFormData("profile_image", file.getName(), reqFile);
                }
            } else if (galleryUri != null) {
                File file = getFileFromUri(galleryUri);
                if (file != null) {
                    RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), file);
                    imagePart = MultipartBody.Part.createFormData("profile_image", file.getName(), reqFile);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        ApiService apiService = RetrofitClient.getApiService();
        apiService.updateProfile(nameBody, mobileBody, imagePart).enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                if (progressDialog != null) progressDialog.dismiss();
                
                if (response.isSuccessful() && response.body() != null) {
                    AuthResponse authResponse = response.body();
                    
                    // Update SharedPreferences
                    SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("full_name", authResponse.getFullName());
                    editor.putString("email", authResponse.getEmail()); 
                    if (authResponse.getProfileImage() != null) {
                        editor.putString("profile_image", authResponse.getProfileImage());
                    }
                    editor.apply();

                    Toast.makeText(EditProfileActivity.this, "Profile updated successfully!", Toast.LENGTH_SHORT).show();
                    goBack();
                } else {
                    Toast.makeText(EditProfileActivity.this, "Failed to update profile", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                if (progressDialog != null) progressDialog.dismiss();
                Toast.makeText(EditProfileActivity.this, "Network Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private File getFileFromUri(Uri uri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            if (inputStream == null) return null;
            
            File tempFile = File.createTempFile("upload", ".jpg", getCacheDir());
            OutputStream outputStream = new FileOutputStream(tempFile);
            
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            outputStream.close();
            inputStream.close();
            return tempFile;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void goBack() {
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    private void openHomeScreen() {
        try {
            Intent intent = new Intent(this, CustomerHomeActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        } catch (Exception e) {
            Toast.makeText(this, "Cannot open Home", Toast.LENGTH_SHORT).show();
        }
    }

    private void openCatalogScreen() {
        try {
            Intent intent = new Intent(this, CatalogActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        } catch (Exception e) {
            Toast.makeText(this, "Cannot open Catalog", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        goBack();
    }
}
