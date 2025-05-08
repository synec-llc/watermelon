package com.project.watermelon;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Random;

public class aSplash extends AppCompatActivity {

    // Public preference constants (accessible anywhere in your app)
    public static final String PREFS_NAME = "MyPrefs";
    public static final String SESSION_DEVICE_ID = "session_device_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Disable dark mode
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        requestWindowFeature(android.view.Window.FEATURE_NO_TITLE);
        // getSupportActionBar().hide();

        // Enable Edge to Edge display
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_asplash);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Retrieve the SharedPreferences using the public name
        SharedPreferences sharedPrefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String deviceId = sharedPrefs.getString(SESSION_DEVICE_ID, null);

        if (deviceId == null) {
            // Generate a random 7-digit number (from 1000000 to 9999999)
            int randomNumber = 1000000 + new Random().nextInt(9000000);
            deviceId = String.valueOf(randomNumber);
            // Save the new device ID in SharedPreferences
            sharedPrefs.edit().putString(SESSION_DEVICE_ID, deviceId).apply();
            Log.d("aSplash", "Created new device ID: " + deviceId);
        } else {
            Log.d("aSplash", "Existing device ID found: " + deviceId);
        }

        // Delay for 2 seconds then start the home activity
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Intent intent = new Intent(aSplash.this, aHomeActivity.class);
            startActivity(intent);
            // Apply the swipe-right animation
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            finish();
        }, 2000); // 2000 milliseconds = 2 seconds
    }
}
