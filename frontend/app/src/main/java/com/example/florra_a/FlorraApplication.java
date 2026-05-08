package com.example.florra_a;

import android.app.Application;
import com.example.florra_a.network.RetrofitClient;

public class FlorraApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // Initialize RetrofitClient with application context on app startup
        RetrofitClient.init(this);
    }
}
