package com.example.florra_a;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.florra_a.adapters.ChatAdapter;
import com.example.florra_a.models.ChatMessage;
import com.example.florra_a.network.ChatbotApiService;
import com.example.florra_a.network.ChatbotClient;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AIChatActivity extends AppCompatActivity {

    private RecyclerView rvChatMessages;
    private ChatAdapter chatAdapter;
    private List<ChatMessage> chatMessages;
    private EditText edtMessage;
    private ImageButton btnSend;
    private ImageButton btnAttach;
    private View layoutImagePreview;
    private ImageView imgSelectedPreview;
    private ImageButton btnRemoveImage;
    private File pendingImageFile;
    private String currentPhotoPath;
    private static final int REQUEST_CAMERA = 100;
    private static final int REQUEST_GALLERY = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Enable edge-to-edge
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);

        // Handle notch and status bar
        WindowInsetsControllerCompat windowInsetsController =
                WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());
        if (windowInsetsController != null) {
            windowInsetsController.setAppearanceLightStatusBars(true);
            windowInsetsController.setAppearanceLightNavigationBars(true);
        }

        setContentView(R.layout.activity_chatbot);

        // Initialize views
        rvChatMessages = findViewById(R.id.rvChatMessages);
        edtMessage = findViewById(R.id.edtMessage);
        btnSend = findViewById(R.id.btnSend);
        btnAttach = findViewById(R.id.btnAttach);
        ImageButton btnBack = findViewById(R.id.btnBack);
        layoutImagePreview = findViewById(R.id.layoutImagePreview);
        imgSelectedPreview = findViewById(R.id.imgSelectedPreview);
        btnRemoveImage = findViewById(R.id.btnRemoveImage);

        // Setup RecyclerView
        chatMessages = new ArrayList<>();
        chatAdapter = new ChatAdapter(chatMessages);
        rvChatMessages.setLayoutManager(new LinearLayoutManager(this));
        rvChatMessages.setAdapter(chatAdapter);

        // Back button
        btnBack.setOnClickListener(v -> finish());

        // Send button
        btnSend.setOnClickListener(v -> sendMessage());

        // Attach button
        btnAttach.setOnClickListener(v -> showImageSourceDialog());

        // Remove image button
        btnRemoveImage.setOnClickListener(v -> clearPendingImage());

        // Add initial message
        addMessage("Hello! How can I help you today?", ChatMessage.TYPE_AI);
    }

    private void clearPendingImage() {
        pendingImageFile = null;
        layoutImagePreview.setVisibility(View.GONE);
    }

    private void showImageSourceDialog() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
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
            Intent takePictureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            File photoFile = createImageFile();
            if (photoFile != null) {
                android.net.Uri photoURI = androidx.core.content.FileProvider.getUriForFile(this,
                        getApplicationContext().getPackageName() + ".fileprovider",
                        photoFile);
                takePictureIntent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_CAMERA);
            }
        } catch (Exception e) {
            Toast.makeText(this, "Camera Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_GALLERY);
    }

    private File createImageFile() throws java.io.IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        File storageDir = getExternalFilesDir(android.os.Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile("JPEG_" + timeStamp + "_", ".jpg", storageDir);
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CAMERA) {
                showImagePreview(new File(currentPhotoPath));
            } else if (requestCode == REQUEST_GALLERY && data != null) {
                try {
                    File file = ScanImageActivity.FileUtil.from(this, data.getData());
                    showImagePreview(file);
                } catch (Exception e) {
                    Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void showImagePreview(File file) {
        pendingImageFile = file;
        layoutImagePreview.setVisibility(View.VISIBLE);
        imgSelectedPreview.setImageURI(android.net.Uri.fromFile(file));
    }

    private void sendMessage() {
        String message = edtMessage.getText().toString().trim();
        
        if (message.isEmpty() && pendingImageFile == null) return;

        if (pendingImageFile != null) {
            // Send Image (with or without text)
            uploadImage(pendingImageFile, message);
        } else {
            // Send Text Only
            sendTextMessage(message);
        }
        
        // Clear inputs
        edtMessage.setText("");
        clearPendingImage();
    }

    private void sendTextMessage(String message) {
        addMessage(message, ChatMessage.TYPE_USER);

        ChatbotClient.getApiService().chat(new ChatbotApiService.ChatRequest(message))
            .enqueue(new Callback<ChatbotApiService.ChatResponse>() {
                @Override
                public void onResponse(Call<ChatbotApiService.ChatResponse> call, Response<ChatbotApiService.ChatResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        addMessage(response.body().reply, ChatMessage.TYPE_AI);
                    } else {
                        addMessage("Sorry, I'm having trouble connecting right now.", ChatMessage.TYPE_AI);
                    }
                }

                @Override
                public void onFailure(Call<ChatbotApiService.ChatResponse> call, Throwable t) {
                    addMessage("Error: " + t.getMessage(), ChatMessage.TYPE_AI);
                }
            });
    }

    private void uploadImage(File file, String caption) {
        String userMsg = "Sent an image";
        if (!caption.isEmpty()) userMsg += ": " + caption;
        addMessage(userMsg, ChatMessage.TYPE_USER);
        addMessage("Analysing image...", ChatMessage.TYPE_AI);
        
        okhttp3.RequestBody requestFile = okhttp3.RequestBody.create(okhttp3.MediaType.parse("image/*"), file);
        okhttp3.MultipartBody.Part body = okhttp3.MultipartBody.Part.createFormData("image", file.getName(), requestFile);

        // Note: Currently the backend searchImage doesn't take a caption, 
        // but we show it in UI for consistency.
        ChatbotClient.getApiService().searchImage(body).enqueue(new Callback<ChatbotApiService.ChatResponse>() {
            @Override
            public void onResponse(Call<ChatbotApiService.ChatResponse> call, Response<ChatbotApiService.ChatResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    addMessage(response.body().reply, ChatMessage.TYPE_AI);
                } else {
                    addMessage("I'm sorry, I couldn't process that image.", ChatMessage.TYPE_AI);
                }
            }

            @Override
            public void onFailure(Call<ChatbotApiService.ChatResponse> call, Throwable t) {
                addMessage("Connection error while sending image.", ChatMessage.TYPE_AI);
            }
        });
    }

    private void addMessage(String text, int type) {
        String currentTime = new SimpleDateFormat("hh:mm a", Locale.getDefault()).format(new Date());
        chatMessages.add(new ChatMessage(text, type, currentTime));
        chatAdapter.notifyItemInserted(chatMessages.size() - 1);
        rvChatMessages.scrollToPosition(chatMessages.size() - 1);
    }
}
