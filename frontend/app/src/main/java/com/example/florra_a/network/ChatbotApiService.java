package com.example.florra_a.network;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ChatbotApiService {

    class ChatRequest {
        String message;

        public ChatRequest(String message) {
            this.message = message;
        }
    }

    class ChatResponse {
        public String reply;
    }

    @POST("customer/chat/")
    Call<ChatResponse> chat(@Body ChatRequest request);

    @retrofit2.http.Multipart
    @POST("customer/search_image/")
    Call<ChatResponse> searchImage(@retrofit2.http.Part okhttp3.MultipartBody.Part image);
}
