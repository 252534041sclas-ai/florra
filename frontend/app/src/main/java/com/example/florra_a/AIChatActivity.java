package com.example.florra_a;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.florra_a.adapters.ChatAdapter;
import com.example.florra_a.models.ChatMessage;
import com.example.florra_a.network.ChatbotApiService;
import com.example.florra_a.network.ChatbotClient;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AIChatActivity extends AppCompatActivity {

    private static final int REQUEST_CAMERA_PERMISSION = 101;
    private static final int REQUEST_IMAGE_CAPTURE = 103;
    private static final int REQUEST_PICK_IMAGE = 104;

    private ImageButton btnBack, btnAttach, btnSend, btnMic, btnRefresh;
    private EditText edtMessage;
    private RecyclerView rvChatMessages;
    private ChatAdapter chatAdapter;
    private List<ChatMessage> messageList;

    private LinearLayout actionSimilar, actionCompare, actionQuote, actionStock;
    
    private Uri photoURI;
    private String currentPhotoPath;
    private Uri selectedImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        android.util.Log.d("DEBUG", "AIChatActivity started!");
        
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(android.graphics.Color.WHITE);
        }
        
        setContentView(R.layout.activity_chatbot);

        initializeViews();
        setupRecyclerView();
        setupClickListeners();
        
        // Add welcome message
        addBotMessage("Hello! I'm your Florra Assistant. How can I help you with your tile design today?\n\nYou can ask about tiles, track your orders, or even upload a photo to find similar products!");
    }

    private void initializeViews() {
        btnBack = findViewById(R.id.btnBack);
        btnAttach = findViewById(R.id.btnAttach);
        btnSend = findViewById(R.id.btnSend);
        btnMic = findViewById(R.id.btnMic);
        btnRefresh = findViewById(R.id.btnRefresh);
        edtMessage = findViewById(R.id.edtMessage);
        rvChatMessages = findViewById(R.id.rvChatMessages);

        actionSimilar = findViewById(R.id.actionSimilar);
        actionCompare = findViewById(R.id.actionCompare);
        actionQuote = findViewById(R.id.actionQuote);
        actionStock = findViewById(R.id.actionStock);
    }

    private void setupRecyclerView() {
        messageList = new ArrayList<>();
        chatAdapter = new ChatAdapter(messageList);
        rvChatMessages.setLayoutManager(new LinearLayoutManager(this));
        rvChatMessages.setAdapter(chatAdapter);
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> onBackPressed());
        btnAttach.setOnClickListener(v -> showImageSourceDialog());
        btnSend.setOnClickListener(v -> sendMessage());
        
        btnRefresh.setOnClickListener(v -> {
            messageList.clear();
            chatAdapter.notifyDataSetChanged();
            addBotMessage("Chat cleared. How can I help you now?");
        });
        
        btnMic.setOnClickListener(v -> Toast.makeText(this, "Voice search coming soon", Toast.LENGTH_SHORT).show());

        // Quick Actions
        actionSimilar.setOnClickListener(v -> {
            showImageSourceDialog();
        });
        
        actionQuote.setOnClickListener(v -> {
            edtMessage.setText("Check my quotation status");
            sendMessage();
        });

        actionStock.setOnClickListener(v -> {
            edtMessage.setText("Check stock for 2x2 vitrified tiles");
            sendMessage();
        });

        actionCompare.setOnClickListener(v -> {
            addBotMessage("To compare tiles, please select at least two tiles from the catalog or describe the types you're looking for.");
        });
    }

    private void sendMessage() {
        String messageText = edtMessage.getText().toString().trim();
        if (messageText.isEmpty() && selectedImageUri == null) {
            return;
        }

        // 1. Add User Message to UI
        ChatMessage userMsg = new ChatMessage(messageText, true);
        if (selectedImageUri != null) {
            userMsg.setImagePath(getPathFromUri(selectedImageUri));
        }
        messageList.add(userMsg);
        chatAdapter.notifyItemInserted(messageList.size() - 1);
        rvChatMessages.scrollToPosition(messageList.size() - 1);

        // 2. Clear input
        edtMessage.setText("");
        
        // 3. Show Typing Indicator
        ChatMessage typingMsg = ChatMessage.createTypingIndicator();
        messageList.add(typingMsg);
        chatAdapter.notifyItemInserted(messageList.size() - 1);
        rvChatMessages.scrollToPosition(messageList.size() - 1);

        // 4. API Call
        if (selectedImageUri != null) {
            sendImageSearchRequest(selectedImageUri);
            selectedImageUri = null; // Reset after sending
        } else {
            sendTextChatRequest(messageText);
        }
    }

    private void sendTextChatRequest(String text) {
        ChatbotApiService.ChatRequest request = new ChatbotApiService.ChatRequest(text);
        ChatbotClient.getApiService().chat(request).enqueue(new Callback<ChatbotApiService.ChatResponse>() {
            @Override
            public void onResponse(Call<ChatbotApiService.ChatResponse> call, Response<ChatbotApiService.ChatResponse> response) {
                removeTypingIndicator();
                if (response.isSuccessful() && response.body() != null) {
                    addBotResponse(response.body().reply, response.body().products);
                } else {
                    addBotMessage("Sorry, I'm having trouble connecting to the server. Please try again later.");
                }
            }

            @Override
            public void onFailure(Call<ChatbotApiService.ChatResponse> call, Throwable t) {
                removeTypingIndicator();
                addBotMessage("Network error. Please check your connection.");
            }
        });
    }

    private void sendImageSearchRequest(Uri imageUri) {
        try {
            File file = new File(getPathFromUri(imageUri));
            RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);
            MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestFile);

            ChatbotClient.getApiService().searchImage(body).enqueue(new Callback<ChatbotApiService.ChatResponse>() {
                @Override
                public void onResponse(Call<ChatbotApiService.ChatResponse> call, Response<ChatbotApiService.ChatResponse> response) {
                    removeTypingIndicator();
                    if (response.isSuccessful() && response.body() != null) {
                        addBotResponse(response.body().reply, response.body().products);
                    } else {
                        addBotMessage("I couldn't process that image. Try another one!");
                    }
                }

                @Override
                public void onFailure(Call<ChatbotApiService.ChatResponse> call, Throwable t) {
                    removeTypingIndicator();
                    addBotMessage("Error uploading image.");
                }
            });
        } catch (Exception e) {
            removeTypingIndicator();
            addBotMessage("Error preparing image for upload.");
        }
    }

    private void addBotMessage(String text) {
        ChatMessage botMsg = new ChatMessage(text, false);
        messageList.add(botMsg);
        chatAdapter.notifyItemInserted(messageList.size() - 1);
        rvChatMessages.scrollToPosition(messageList.size() - 1);
    }

    private void addBotResponse(String text, List<com.example.florra_a.models.Product> products) {
        ChatMessage botMsg = new ChatMessage(text, false);
        if (products != null && !products.isEmpty()) {
            botMsg.setProducts(products);
        }
        messageList.add(botMsg);
        chatAdapter.notifyItemInserted(messageList.size() - 1);
        rvChatMessages.scrollToPosition(messageList.size() - 1);
    }

    private void removeTypingIndicator() {
        if (!messageList.isEmpty() && messageList.get(messageList.size() - 1).isTyping()) {
            messageList.remove(messageList.size() - 1);
            chatAdapter.notifyItemRemoved(messageList.size());
        }
    }

    // --- HELPER METHODS FOR IMAGES ---

    private void showImageSourceDialog() {
        String[] options = {"Take Photo", "Choose from Gallery", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Attach Image");
        builder.setItems(options, (dialog, which) -> {
            if (which == 0) checkCameraPermission();
            else if (which == 1) openGallery();
            else dialog.dismiss();
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
        // Removed resolveActivity check as it's unreliable on Android 11+
        File photoFile = null;
        try {
            photoFile = createImageFile();
        } catch (IOException ex) {
            Toast.makeText(this, "Error creating file", Toast.LENGTH_SHORT).show();
        }
        if (photoFile != null) {
            photoURI = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", photoFile);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile("JPEG_" + timeStamp + "_", ".jpg", storageDir);
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_CAPTURE) {
                selectedImageUri = photoURI;
                sendMessage(); // Auto send for visual search
            } else if (requestCode == REQUEST_PICK_IMAGE && data != null) {
                selectedImageUri = data.getData();
                sendMessage(); // Auto send for visual search
            }
        }
    }

    private String getPathFromUri(Uri uri) {
        if (uri.getScheme().equals("file")) {
            return uri.getPath();
        }
        // Simplified for this task - usually requires more robust content resolver logic
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            File file = new File(getExternalFilesDir(null), "temp_image.jpg");
            OutputStream outputStream = new FileOutputStream(file);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
            outputStream.close();
            inputStream.close();
            return file.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}
