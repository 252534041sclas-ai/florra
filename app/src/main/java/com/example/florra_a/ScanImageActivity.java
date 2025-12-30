package com.example.florra_a;

import android.app.AlertDialog;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ScanImageActivity extends AppCompatActivity {

    private static final String TAG = "ScanImageActivity";
    private static final int REQUEST_CAMERA = 1;
    private static final int REQUEST_GALLERY = 2;

    private String currentPhotoPath;
    private ImageView selectedImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set fullscreen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_scan_image);

        setupClickListeners();
    }

    private void setupClickListeners() {
        // Back Button
        View btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        }

        // Upload Card (Whole card is clickable)
        View uploadCard = findViewById(R.id.uploadCard);
        if (uploadCard != null) {
            uploadCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showImageSourceDialog();
                }
            });
        }

        // Choose Image Button
        View btnChooseImage = findViewById(R.id.btnChooseImage);
        if (btnChooseImage != null) {
            btnChooseImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showImageSourceDialog();
                }
            });
        }

        // View All Button
        View btnViewAll = findViewById(R.id.btnViewAll);
        if (btnViewAll != null) {
            btnViewAll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(ScanImageActivity.this, "View All Recent Scans", Toast.LENGTH_SHORT).show();
                }
            });
        }

        // Recent Scan 1
        View scan1 = findViewById(R.id.scan1);
        if (scan1 != null) {
            scan1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openScanDetails("Marble Hexagon");
                }
            });
        }

        // Recent Scan 2
        View scan2 = findViewById(R.id.scan2);
        if (scan2 != null) {
            scan2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openScanDetails("Beige Ceramic");
                }
            });
        }

        // Recent Scan 3
        View scan3 = findViewById(R.id.scan3);
        if (scan3 != null) {
            scan3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openScanDetails("Slate Grey");
                }
            });
        }
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
            Toast.makeText(this, "Camera error", Toast.LENGTH_SHORT).show();
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

        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );

        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CAMERA) {
                handleCameraResult();
            } else if (requestCode == REQUEST_GALLERY) {
                handleGalleryResult(data);
            }
        }
    }

    private void handleCameraResult() {
        try {
            Bitmap bitmap = BitmapFactory.decodeFile(currentPhotoPath);
            if (bitmap != null) {
                Toast.makeText(this, "Image captured!", Toast.LENGTH_SHORT).show();
                showProcessingDialog();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Failed to capture image", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleGalleryResult(Intent data) {
        try {
            Uri selectedImageUri = data.getData();
            if (selectedImageUri != null) {
                Toast.makeText(this, "Image selected!", Toast.LENGTH_SHORT).show();
                showProcessingDialog();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Failed to select image", Toast.LENGTH_SHORT).show();
        }
    }

    private void showProcessingDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Processing Image");
        builder.setMessage("Finding matching tiles...");
        builder.setCancelable(false);

        AlertDialog dialog = builder.create();
        dialog.show();

        new android.os.Handler().postDelayed(() -> {
            if (dialog.isShowing()) {
                dialog.dismiss();
                // Open AI Recommendation screen
                Intent intent = new Intent(ScanImageActivity.this, AIRecommendationActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        }, 2000);
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