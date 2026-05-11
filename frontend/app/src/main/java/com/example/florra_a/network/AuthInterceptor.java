package com.example.florra_a.network;

import android.content.Context;
import android.content.SharedPreferences;
import com.example.florra_a.utils.SharedPrefManager;

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
        String token = SharedPrefManager.getInstance(context).getToken();

        Request.Builder requestBuilder = chain.request().newBuilder();

        if (token != null && !token.isEmpty()) {
            android.util.Log.d("AuthInterceptor", "Attaching Token: " + token);
            // Change from "Bearer " to "Token " to match the backend expectation
            requestBuilder.header("Authorization", "Token " + token);
        } else {
            android.util.Log.e("AuthInterceptor", "Token is EMPTY!");
        }

        return chain.proceed(requestBuilder.build());
    }
}
