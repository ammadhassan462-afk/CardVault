package com.example.cardvault;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;

/**
 * Screen 1: Splash Screen for CardVault
 * Integrates Android's native SplashScreen API, displays CardVault branding,
 * initializes the local SQLite database safely, and smoothly routes to Login
 * (or directly to MainActivity if an active local session exists).
 */
public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Install native Android SplashScreen API
        SplashScreen.installSplashScreen(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Ensure database tables and sample cards initialize safely on startup
        DatabaseHelper.getInstance(this);

        // Brief, clean transition to Login or Home
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                navigateNext();
            }
        }, 1200);
    }

    private void navigateNext() {
        CardVaultAuthManager auth = CardVaultAuthManager.getInstance(this);
        Intent intent;
        if (auth.isLoggedIn()) {
            intent = new Intent(SplashActivity.this, MainActivity.class);
        } else {
            intent = new Intent(SplashActivity.this, LoginActivity.class);
        }
        startActivity(intent);
        finish();
    }
}
