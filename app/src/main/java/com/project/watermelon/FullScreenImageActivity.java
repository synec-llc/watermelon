package com.project.watermelon;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;


public class FullScreenImageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_image);

        ImageView fullscreenImage = findViewById(R.id.fullscreen_image);
        ImageButton closeButton = findViewById(R.id.close_button);

        // Get the image resource ID from the intent
        int imageResId = getIntent().getIntExtra("image_res_id", -1);

        if (imageResId != -1) {
            fullscreenImage.setImageResource(imageResId);
        }

        // Close the activity when the close button is clicked
        closeButton.setOnClickListener(v -> finish());
    }

    private void openFullScreenImage(int imageResId) {
        Intent intent = new Intent(this, FullScreenImageActivity.class);
        intent.putExtra("image_res_id", imageResId);
        startActivity(intent);
    }

}
