package com.example.florra_a.network;

import android.content.Context;
import android.content.SharedPreferences;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AuthInterceptor implements Interceptor {

    private Context context;

    public AuthInterceptor(Context context) {
        this.context = context.getApplicationContext();
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        SharedPreferences prefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        String token = prefs.getString("auth_token", "");

        Request.Builder requestBuilder = chain.request().newBuilder();

        if (!token.isEmpty()) {
            android.util.Log.d("AuthInterceptor", "Attaching Token: " + token);
            requestBuilder.addHeader("Authorization", "Token " + token);
        } else {
            android.util.Log.e("AuthInterceptor", "Token is EMPTY!");
        }

        return chain.proceed(requestBuilder.build());
    }
}
