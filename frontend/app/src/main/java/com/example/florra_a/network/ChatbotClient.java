package com.example.florra_a.network;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ChatbotClient {

    // Assuming running on same machine/IP as Main API for now. 
    // You can change port here if needed.
    public static final String BASE_URL = "http://192.168.172.10:8000/api/";
    private static Retrofit retrofit = null;

    public static ChatbotApiService getApiService() {
        if (retrofit == null) {
            okhttp3.logging.HttpLoggingInterceptor logging = new okhttp3.logging.HttpLoggingInterceptor();
            logging.setLevel(okhttp3.logging.HttpLoggingInterceptor.Level.BODY);

            okhttp3.OkHttpClient.Builder clientBuilder = new okhttp3.OkHttpClient.Builder()
                    .addInterceptor(logging)
                    .connectTimeout(120, java.util.concurrent.TimeUnit.SECONDS)
                    .readTimeout(120, java.util.concurrent.TimeUnit.SECONDS)
                    .writeTimeout(120, java.util.concurrent.TimeUnit.SECONDS);

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(clientBuilder.build())
                    .build();
        }
        return retrofit.create(ChatbotApiService.class);
    }
}
