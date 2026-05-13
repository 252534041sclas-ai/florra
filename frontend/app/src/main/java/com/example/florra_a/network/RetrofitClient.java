package com.example.florra_a.network;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    // Using LAN IP for physical device compatibility
    public static final String BASE_URL = "http://10.160.157.10:8001/";
    private static Retrofit retrofit = null;
    private static android.content.Context context;

    public static void init(android.content.Context ctx) {
        context = ctx.getApplicationContext();
        retrofit = null; // Force rebuild with new context
    }

    public static ApiService getApiService() {
        if (retrofit == null) {
            okhttp3.logging.HttpLoggingInterceptor logging = new okhttp3.logging.HttpLoggingInterceptor();
            logging.setLevel(okhttp3.logging.HttpLoggingInterceptor.Level.BODY);

            okhttp3.OkHttpClient.Builder clientBuilder = new okhttp3.OkHttpClient.Builder()
                    .addInterceptor(logging)
                    .connectTimeout(120, java.util.concurrent.TimeUnit.SECONDS)
                    .readTimeout(120, java.util.concurrent.TimeUnit.SECONDS)
                    .writeTimeout(120, java.util.concurrent.TimeUnit.SECONDS);
            
            if (context != null) {
                clientBuilder.addInterceptor(new AuthInterceptor(context));
            }

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(clientBuilder.build())
                    .build();
        }
        return retrofit.create(ApiService.class);
    }
}
