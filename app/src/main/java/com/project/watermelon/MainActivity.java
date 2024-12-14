package com.project.watermelon;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;

import com.google.common.util.concurrent.ListenableFuture;

import java.io.File;
import java.io.FileOutputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private AlertDialog loadingDialog;
    private PreviewView cameraPreview;
    private ImageButton captureButton;
    private ImageButton switchCameraBtn;
    private static final int PICK_IMAGE_REQUEST = 1;

    private ImageCapture imageCapture;
    private CameraSelector cameraSelector;
    private Camera camera;
    private boolean isFrontCamera = false;
    ImageButton selectFileBtn;
    private ExecutorService cameraExecutor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        // Initialize UI elements
        cameraPreview = findViewById(R.id.cameraPreview);
        captureButton = findViewById(R.id.captureButton);
        switchCameraBtn = findViewById(R.id.switchCameraBtn);
        selectFileBtn = findViewById(R.id.selectFileBtn);


        // Initialize CameraX
        cameraExecutor = Executors.newSingleThreadExecutor();
        startCamera();

        // Capture button listener
        captureButton.setOnClickListener(v -> takePhoto());

        // Switch camera button listener
        switchCameraBtn.setOnClickListener(v -> {
            isFrontCamera = !isFrontCamera;
            startCamera();
        });

        selectFileBtn.setOnClickListener(v -> openGallery());

        // Find the ImageButton by its ID
        ImageButton topRightImageButton1 = findViewById(R.id.topRightImageButton1);
        ImageButton topRightImageButton2 = findViewById(R.id.topRightImageButton2);
        topRightImageButton1.setOnClickListener(v -> showImageDialog());
        topRightImageButton2.setOnClickListener(v -> {
            // Create an intent to start bLibraryActivity
            Intent intent = new Intent(MainActivity.this, bLibraryActivity.class);
            startActivity(intent);
        });
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*"); // Restrict to image selection only
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();

            // Convert the URI to a File (example method below)
            File photoFile = new File(getRealPathFromURI(imageUri));
            processImageWithEdenAI(photoFile);

        }
    }
    private String getRealPathFromURI(Uri contentUri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = getContentResolver().query(contentUri, projection, null, null, null);
        if (cursor != null) {
            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            String path = cursor.getString(columnIndex);
            cursor.close();
            return path;
        }
        return contentUri.getPath();
    }




    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture =
                ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();

                // Unbind previous use cases before rebinding
                cameraProvider.unbindAll();

                // Build the camera preview
                cameraSelector = new CameraSelector.Builder()
                        .requireLensFacing(isFrontCamera ? CameraSelector.LENS_FACING_FRONT : CameraSelector.LENS_FACING_BACK)
                        .build();

                PreviewView preview = new PreviewView(this);
                androidx.camera.core.Preview previewUseCase = new androidx.camera.core.Preview.Builder().build();
                previewUseCase.setSurfaceProvider(cameraPreview.getSurfaceProvider());

                // Configure image capture
                imageCapture = new ImageCapture.Builder()
                        .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                        .build();

                // Bind use cases to camera
                camera = cameraProvider.bindToLifecycle(
                        this, cameraSelector, previewUseCase, imageCapture);

            } catch (Exception e) {
                Toast.makeText(this, "Failed to start camera: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }, ContextCompat.getMainExecutor(this));
    }

    private void takePhoto() {
        if (imageCapture == null) {
            Toast.makeText(this, "Camera is not ready", Toast.LENGTH_SHORT).show();
            return;
        }

        playShutterSound();

        // Specify where the photo will be saved
        File photoFile = new File(getExternalFilesDir(null), "photo_" + System.currentTimeMillis() + ".jpg");

        // Build output options
        ImageCapture.OutputFileOptions outputOptions =
                new ImageCapture.OutputFileOptions.Builder(photoFile).build();

        // Take picture
        imageCapture.takePicture(
                outputOptions,
                ContextCompat.getMainExecutor(this),
                new ImageCapture.OnImageSavedCallback() {
                    @Override
                    public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
//                        Toast.makeText(MainActivity.this, "Photo saved: " + photoFile.getAbsolutePath(), Toast.LENGTH_SHORT).show();
                        processImageWithEdenAI(photoFile);
                    }

                    @Override
                    public void onError(@NonNull ImageCaptureException exception) {
                        Toast.makeText(MainActivity.this, "Photo capture failed: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cameraExecutor.shutdown();
    }


//    private void processImageWithEdenAI(File image) {
//        //I want to run a loading screen here
//        showLoadingDialog();
//        // Convert the drawable resource to a File
////        File imageFile = getFileFromDrawable(this, R.drawable.img_meal_one);
//        File imageFile = image;
//        Log.d(TAG, "processImageWithEdenAI: image is imported");
//        if (imageFile != null) {
//            // Call the EdenAIWorkflowRunner class
//            EdenAIWorkflowRunner workflowRunner = new EdenAIWorkflowRunner(this);
//            workflowRunner.runWorkflow(imageFile);
//        } else {
//            Log.e(TAG, "Failed to convert drawable to file.");
//        }
//    }

    private void processImageWithEdenAI(File imageFile) {
        showLoadingDialog();

        try {
            // Compress the image file
            File compressedFile = compressImage(imageFile);
            Log.d(TAG, "processImageWithEdenAI: Compressed file size: " + compressedFile.length());

            if (compressedFile != null) {
                // Call the EdenAIWorkflowRunner class
                EdenAIWorkflowRunner workflowRunner = new EdenAIWorkflowRunner(this);
                workflowRunner.runWorkflow(compressedFile);
            } else {
                Log.e(TAG, "Compression failed. Using original file.");
                EdenAIWorkflowRunner workflowRunner = new EdenAIWorkflowRunner(this);
                workflowRunner.runWorkflow(imageFile);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error while compressing image: " + e.getMessage());
            EdenAIWorkflowRunner workflowRunner = new EdenAIWorkflowRunner(this);
            workflowRunner.runWorkflow(imageFile);
        }
    }


    private File getFileFromDrawable(Context context, int drawableId) {
        try {
            // Convert drawable to bitmap
            BitmapDrawable drawable = (BitmapDrawable) getResources().getDrawable(drawableId);
            Bitmap bitmap = drawable.getBitmap();

            // Create a file in the cache directory
            File file = new File(context.getCacheDir(), "input_image.jpg");
            FileOutputStream outputStream = new FileOutputStream(file);

            // Compress the bitmap to JPEG format and write to the file
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            outputStream.close();

            Log.d(TAG, "Image file created at: " + file.getAbsolutePath());
            return file;
        } catch (Exception e) {
            Log.e(TAG, "Error while creating file from drawable: " + e.getMessage());
            return null;
        }
    }

    private void showLoadingDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.loading_dialog, null);
        builder.setView(view);
        builder.setCancelable(false); // Prevent dismissing by tapping outside
        loadingDialog = builder.create();
        loadingDialog.show();
    }

    private void hideLoadingDialog() {
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
        }
    }

    private void playShutterSound() {
        MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.camera_shutter);
        mediaPlayer.setOnCompletionListener(MediaPlayer::release); // Release resources after playing
        mediaPlayer.start();
    }



    private void showImageDialog() {
        // Inflate the custom layout for the dialog
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_image, null);

        // Find the ImageView within the custom layout
        ImageView imageView = dialogView.findViewById(R.id.dialogImageView);

        // Set your drawable image that needs to be displayed
        imageView.setImageResource(R.drawable.process_flowchart); // Replace with your actual image resource

        // Create and show the dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("How the App Works")
                .setView(dialogView)
                .setPositiveButton("Close", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }


    private File compressImage(File originalFile) throws Exception {
        // Decode the original file to a Bitmap
        BitmapFactory.Options options = new BitmapFactory.Options();
        Bitmap bitmap = BitmapFactory.decodeFile(originalFile.getAbsolutePath(), options);

        // Create a compressed file in the cache directory
        File compressedFile = new File(getCacheDir(), "compressed_" + originalFile.getName());
        FileOutputStream outputStream = new FileOutputStream(compressedFile);

        // Compress the bitmap to reduce file size (quality is a percentage: 100 is max)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 1, outputStream); // Adjust quality as needed
        outputStream.close();

        Log.d(TAG, "Image compressed successfully: " + compressedFile.getAbsolutePath());
        return compressedFile;
    }

}
