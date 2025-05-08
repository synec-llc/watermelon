package com.project.watermelon;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class aHomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_ahome);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button scanButton = findViewById(R.id.scanButton);
        Button howItWorksButton = findViewById(R.id.howItWorksButton);
        Button scanningTipButton = findViewById(R.id.scanningTipButton);
        Button libraryButton = findViewById(R.id.libraryButton); // Added button for Library
        Button scanHistoryButton = findViewById(R.id.ScanHistoryButton);

        scanButton.setOnClickListener(v -> {
            // Redirect to MainActivity
            Intent intent = new Intent(aHomeActivity.this, MainActivity.class);
            startActivity(intent);
        });
        scanHistoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(aHomeActivity.this);
                builder.setTitle("Select Scan History Option");
                String[] options = {"Success", "Failed"};
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(aHomeActivity.this, bScanHistoryActivity.class);
                        if (which == 0) {
                            intent.putExtra("scan_type", "success");
                        } else if (which == 1) {
                            intent.putExtra("scan_type", "failed");
                        }
                        startActivity(intent);
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });




        howItWorksButton.setOnClickListener(v -> {
            showImageDialog();
        });

        scanningTipButton.setOnClickListener(v -> {
            showScanningTipDialog();
        });

        libraryButton.setOnClickListener(v -> {
            // Redirect to bLibraryActivity
            Intent intent = new Intent(aHomeActivity.this, bLibraryActivity.class);
            startActivity(intent);
        });
    }

    private void showImageDialog() {
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_image, null);

        ImageView imageView = dialogView.findViewById(R.id.dialogImageView);
        imageView.setImageResource(R.drawable.process_flowchart);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("How the App Works")
                .setView(dialogView)
                .setPositiveButton("Close", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showScanningTipDialog() {
        String tips = "\u2022 Ensure good lighting.\n" +
                "\u2022 Keep the camera steady.\n" +
                "\u2022 Focus on the watermelon surface.\n" +
                "\u2022 Avoid glare and reflections.";

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Scanning Tips")
                .setMessage(tips)
                .setPositiveButton("Close", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
