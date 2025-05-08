package com.project.watermelon;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class bResultActivity extends AppCompatActivity {

    private static final String TAG = "bResultActivity";
    ImageView imageView;
    TextView resultTextView;
    String ageGroup = "Larvae";
    // Declare all TextView variables
    private TextView tvSummary;
    private TextView ageAndScientificName;
    private TextView tvDamageSymptoms;
    private TextView tv_short_description;
    private TextView tvDescription;
    private TextView tvHostPlants;
    private TextView tvGeographicalDistribution;

    // Declare all ImageView variables
    private ImageView imgLifeCycle;
    private ImageView image1;
    private ImageView image2;
    private ImageView image3;
    private ImageView image4;
    ImageView iconShoppee, iconLazada, iconRandomStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(R.layout.activity_bresult);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        imageView = findViewById(R.id.imageView);
        resultTextView = findViewById(R.id.resultTextView);

        // Initialize TextView variables
        tvSummary = findViewById(R.id.tv_summary);
        ageAndScientificName = findViewById(R.id.age_group);
        tvDamageSymptoms = findViewById(R.id.tv_damage_symptoms);
        tv_short_description = findViewById(R.id.tv_short_description);
        tvDescription = findViewById(R.id.tv_description);
        tvHostPlants = findViewById(R.id.tv_host_plants);
        tvGeographicalDistribution = findViewById(R.id.tv_geographical_distribution);
        iconShoppee = findViewById(R.id.icon_shoppee);
        iconLazada = findViewById(R.id.icon_lazada);
        iconRandomStore = findViewById(R.id.icon_random_store);

        // Initialize ImageView variables
        imgLifeCycle = findViewById(R.id.img_life_cycle);
        image1 = findViewById(R.id.image1);
        image2 = findViewById(R.id.image2);
        image3 = findViewById(R.id.image3);
        image4 = findViewById(R.id.image4);

        Intent intent = getIntent();
        String result = intent.getStringExtra("result");
        String imagePath = intent.getStringExtra("image_path");
        Log.d(TAG, "jsonResult: " + result);
        Log.d(TAG, "imagepath: " + imagePath);

        // Parse the detections from the JSON result
        List<Map<String, Object>> detections = new ArrayList<>();
        if (result != null) {
            detections = parseDetectionData(result);
            Log.d(TAG, "Detections: " + detections);
        } else {
            Log.e(TAG, "No result found in Intent.");
            Toast.makeText(this, "No result found in Intent.", Toast.LENGTH_SHORT).show();
        }

        // Check if at least one target object is detected.
        // Valid targets include: Watermelon, Mobile phone, Computer, Pumpkin, Cantaloupe, Honeydew.
        boolean hasTarget = false;
        for (Map<String, Object> detection : detections) {
            String type = (String) detection.get("type");
            if (type != null && (
                    type.equalsIgnoreCase("Watermelon") ||
                            type.equalsIgnoreCase("Mobile phone") ||
                            type.equalsIgnoreCase("Computer") ||
                            type.equalsIgnoreCase("Pumpkin") ||
                            type.equalsIgnoreCase("Cantaloupe") ||
                            type.equalsIgnoreCase("Honeydew")
            )) {
                Log.d(TAG, "Comparing detected with: " + type);
                hasTarget = true;
                break;
            }
        }

        // Load the image
        Bitmap bitmap = null;
        if (imagePath != null) {
            File imageFile = new File(imagePath);
            if (imageFile.exists()) {
                bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
            } else {
                Log.e(TAG, "Image file does not exist.");
                Toast.makeText(this, "Image file not found.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Log.e(TAG, "No image path found in intent.");
            Toast.makeText(this, "No image data found.", Toast.LENGTH_SHORT).show();
        }

        // If no valid target is detected, upload the scan result with scan_type "failed" and show an alert
        if (!hasTarget) {
            if (bitmap != null) {
                uploadScanResult("failed", "0.00%", bitmap, "failed");
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("No Detected Target")
                    .setMessage("No valid target object was identified in the provided image. Returning to the main screen.")
                    .setCancelable(false)
                    .setPositiveButton("OK", (dialog, which) -> {
                        Intent intent2 = new Intent(this, MainActivity.class);
                        intent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent2);
                        finish();
                    });
            AlertDialog alert = builder.create();
            alert.show();
            return;
        }

        // Validate detections and image
        if (bitmap == null || detections.isEmpty()) {
            Toast.makeText(this, "Image is not valid", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }

        // Prepare for drawing on the image
        Bitmap mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(mutableBitmap);
        Paint paint = new Paint();
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(20);

        // Text paint for labels
        Paint textPaint = new Paint();
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(120);
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setShadowLayer(20f, 0f, 0f, Color.BLACK);

        boolean resultUploaded = false; // To ensure we upload only once

        // Process each detection and draw bounding boxes for valid targets
        for (Map<String, Object> detection : detections) {
            String type = (String) detection.get("type");
            double confidence = (double) detection.get("confidence");
            double[] coordinates = (double[]) detection.get("coordinates");

            Log.d(TAG, "Detection: " + type + " with confidence " + confidence);

            if (type != null && (
                    type.equalsIgnoreCase("Watermelon") ||
                            type.equalsIgnoreCase("Mobile phone") ||
                            type.equalsIgnoreCase("Computer") ||
                            type.equalsIgnoreCase("Pumpkin") ||
                            type.equalsIgnoreCase("Cantaloupe") ||
                            type.equalsIgnoreCase("Honeydew")
            )) {
                // Bounding box coordinates scaled to the bitmap size
                float xMin = (float) coordinates[0] * bitmap.getWidth();
                float yMin = (float) coordinates[2] * bitmap.getHeight();
                float xMax = (float) coordinates[1] * bitmap.getWidth();
                float yMax = (float) coordinates[3] * bitmap.getHeight();

                // Draw bounding box
                canvas.drawRect(new RectF(xMin, yMin, xMax, yMax), paint);

                // Add label with confidence
                String confidenceText = String.format("%.2f%%", confidence * 100);
                String label = "Watermelon" + " " + confidenceText;
                float textWidth = textPaint.measureText(label);
                float textHeight = textPaint.getTextSize();
                float labelPadding = 2f;

                // Background for text
                Paint backgroundPaint = new Paint();
                backgroundPaint.setColor(Color.RED);
                backgroundPaint.setStyle(Paint.Style.FILL);
                canvas.drawRect(xMin, yMin, xMin + textWidth + labelPadding * 2, yMin + textHeight + labelPadding * 2, backgroundPaint);

                // Draw text label
                canvas.drawText(label, xMin + labelPadding, yMin + textHeight + labelPadding, textPaint);

                // Analyze maturity and update UI with additional pest information
                String maturityInfo = analyzeMaturity(bitmap, xMin, yMin, xMax, yMax);
                Log.d(TAG, "Maturity Info: " + maturityInfo);
                putInformationAboutTheThePest(maturityInfo, confidenceText, ageGroup);

                // Upload scan result only once (for the first valid detection) with scan_type "success"
                if (!resultUploaded) {
                    uploadScanResult(maturityInfo, confidenceText, mutableBitmap, "success");
                    resultUploaded = true;
                }
            }
        }

        // Set the modified image with bounding boxes and text
        imageView.setImageBitmap(mutableBitmap);

        Button homeButton = findViewById(R.id.homeButton);
        homeButton.setOnClickListener(v -> {
            Intent intent2 = new Intent(bResultActivity.this, MainActivity.class);
            intent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent2);
            finish();
        });
    }

    private void putInformationAboutTheThePest(String ripeness, String confidence, String age) {
        resultTextView.setText(ripeness + " (" + confidence + ")");

        String watermelonVariety = "";
        String nutritionalSummary = "";
        String ripenessIndicators = "";
        String commonIssues = "";
        int lifeCycleImageRes = 0;
        String detailedDescription = "";
        String optimalGrowingConditions = "";
        String regionsCultivated = "";
        int[] imageResIds = new int[4];

        switch (ripeness.toLowerCase()) {
            case "unripe":
                watermelonVariety = "Unripe Watermelon";
                nutritionalSummary = "Unripe watermelon is bland and low in natural sugars.";
                ripenessIndicators = "Pale green or whitish rind with underdeveloped color.";
                commonIssues = "Less sweetness and poor texture.";
                lifeCycleImageRes = R.drawable.watermelon_lifecycle;
                detailedDescription = "Harvested prematurely, resulting in hard and unappealing texture.";
                optimalGrowingConditions = "Warm climates with plenty of sunlight.";
                regionsCultivated = "Common in regions with early harvest.";
                imageResIds = new int[]{
                        R.drawable.unripe_a,
                        R.drawable.unripe_b,
                        R.drawable.unripe_c,
                        R.drawable.unripe_d
                };
                break;
            case "underripe":
                watermelonVariety = "Underripe Watermelon";
                nutritionalSummary = "Moderately sweet and juicy but not fully developed.";
                ripenessIndicators = "Light green rind with faint stripes and pale ground spot.";
                commonIssues = "Slightly firm texture and mild flavor.";
                lifeCycleImageRes = R.drawable.watermelon_lifecycle;
                detailedDescription = "In transition between unripe and fully ripe.";
                optimalGrowingConditions = "Requires consistent warmth and sunlight.";
                regionsCultivated = "Typically found in regions with fluctuating conditions.";
                imageResIds = new int[]{
                        R.drawable.underripe_a,
                        R.drawable.underripe_b,
                        R.drawable.underripe_c,
                        R.drawable.underripe_d
                };
                break;
            case "ripe":
                watermelonVariety = "Ripe Watermelon";
                nutritionalSummary = "Sweet, juicy, and nutrient-rich.";
                ripenessIndicators = "Dark green rind with vibrant stripes and creamy yellow ground spot.";
                commonIssues = "Perishable if not handled properly.";
                lifeCycleImageRes = R.drawable.watermelon_lifecycle;
                detailedDescription = "Optimal stage with perfect sweetness and texture.";
                optimalGrowingConditions = "Warm, sunny conditions with proper irrigation.";
                regionsCultivated = "Cultivated in tropical and subtropical regions.";
                imageResIds = new int[]{
                        R.drawable.ripe_a,
                        R.drawable.ripe_b,
                        R.drawable.ripe_c,
                        R.drawable.ripe_d
                };
                break;
            case "overripe":
                watermelonVariety = "Overripe Watermelon";
                nutritionalSummary = "Excessively soft and overly sweet.";
                ripenessIndicators = "Dull rind and overly soft flesh with orange tones.";
                commonIssues = "Spoils quickly and loses texture.";
                lifeCycleImageRes = R.drawable.watermelon_lifecycle;
                detailedDescription = "Surpassed peak ripeness with compromised texture.";
                optimalGrowingConditions = "Requires prompt harvest to avoid overripeness.";
                regionsCultivated = "Often observed in regions with delayed harvest.";
                imageResIds = new int[]{
                        R.drawable.overripe_a,
                        R.drawable.overripe_b,
                        R.drawable.overripe_c,
                        R.drawable.overripe_d
                };
                break;
            default:
                watermelonVariety = "Unknown";
                nutritionalSummary = "Nutritional data is not available.";
                ripenessIndicators = "No clear ripeness indicators.";
                commonIssues = "Unable to determine issues.";
                lifeCycleImageRes = 0;
                detailedDescription = "Description not available.";
                optimalGrowingConditions = "N/A";
                regionsCultivated = "N/A";
                imageResIds = new int[]{0, 0, 0, 0};
                break;
        }

        ageAndScientificName.setText(watermelonVariety);
        tvSummary.setText(nutritionalSummary);
        tv_short_description.setText(ripenessIndicators);
        tvDamageSymptoms.setText(commonIssues);
        tvDescription.setText(detailedDescription);
        tvHostPlants.setText(optimalGrowingConditions);
        tvGeographicalDistribution.setText(regionsCultivated);

        if (lifeCycleImageRes != 0) {
            imgLifeCycle.setImageResource(lifeCycleImageRes);
            int finalLifeCycleImageRes = lifeCycleImageRes;
            imgLifeCycle.setOnClickListener(v -> openFullScreenImage(finalLifeCycleImageRes));
        } else {
            imgLifeCycle.setImageDrawable(null);
            imgLifeCycle.setOnClickListener(null);
        }

        ImageView[] imageViews = {image1, image2, image3, image4};
        for (int i = 0; i < imageViews.length; i++) {
            if (imageResIds[i] != 0) {
                imageViews[i].setImageResource(imageResIds[i]);
                int finalI = i;
                int[] finalImageResIds = imageResIds;
                imageViews[i].setOnClickListener(v -> openFullScreenImage(finalImageResIds[finalI]));
            } else {
                imageViews[i].setImageDrawable(null);
                imageViews[i].setOnClickListener(null);
            }
        }
    }

    private void openFullScreenImage(int imageResId) {
        Intent intent = new Intent(this, FullScreenImageActivity.class);
        intent.putExtra("image_res_id", imageResId);
        startActivity(intent);
    }

    private void setIconClickListeners(String shoppeeUrl, String lazadaUrl, String randomStoreUrl) {
        iconShoppee.setOnClickListener(v -> openUrl(shoppeeUrl));
        iconLazada.setOnClickListener(v -> openUrl(lazadaUrl));
        iconRandomStore.setOnClickListener(v -> openUrl(randomStoreUrl));
    }

    private void openUrl(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(intent);
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        // Disable the back button
    }

    private List<Map<String, Object>> parseDetectionData(String jsonResponse) {
        List<Map<String, Object>> detectionList = new ArrayList<>();
        try {
            JSONObject root = new JSONObject(jsonResponse);
            JSONObject content = root.getJSONObject("content");
            JSONObject results = content.getJSONObject("results");
            JSONObject objectDetection = results.getJSONObject("image__object_detection");
            JSONArray detectionArray = objectDetection.getJSONArray("results");
            Map<String, Map<String, Object>> uniqueDetections = new HashMap<>();

            for (int i = 0; i < detectionArray.length(); i++) {
                JSONObject detection = detectionArray.getJSONObject(i);
                if (detection.has("items")) {
                    JSONArray items = detection.getJSONArray("items");
                    for (int j = 0; j < items.length(); j++) {
                        JSONObject item = items.getJSONObject(j);
                        String type = item.getString("label");
                        if (uniqueDetections.containsKey(type)) {
                            continue;
                        }
                        double confidence = item.getDouble("confidence");
                        double xMin = item.getDouble("x_min");
                        double xMax = item.getDouble("x_max");
                        double yMin = item.getDouble("y_min");
                        double yMax = item.getDouble("y_max");

                        Map<String, Object> detectionData = new HashMap<>();
                        detectionData.put("type", type);
                        detectionData.put("confidence", confidence);
                        detectionData.put("coordinates", new double[]{xMin, xMax, yMin, yMax});
                        uniqueDetections.put(type, detectionData);
                    }
                }
            }
            detectionList.addAll(uniqueDetections.values());
        } catch (Exception e) {
            Log.e(TAG, "Failed to parse detections: " + e.getMessage(), e);
        }
        return detectionList;
    }

    private String analyzeMaturity(Bitmap bitmap, float xMin, float yMin, float xMax, float yMax) {
        if (bitmap == null) {
            Log.e("analyzeMaturity", "Bitmap is null");
            return "Unknown - Bitmap is null";
        }

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int startX = Math.max(0, Math.min(width, (int) xMin));
        int startY = Math.max(0, Math.min(height, (int) yMin));
        int endX = Math.max(0, Math.min(width, (int) xMax));
        int endY = Math.max(0, Math.min(height, (int) yMax));

        Log.d("BoundingBox", "startX: " + startX + ", startY: " + startY +
                ", endX: " + endX + ", endY: " + endY +
                ", Bitmap width: " + width + ", Bitmap height: " + height);

        if (startX >= endX || startY >= endY) {
            Log.e("analyzeMaturity", "Invalid bounding box: startX=" + startX + ", startY=" + startY +
                    ", endX=" + endX + ", endY=" + endY);
            return "Unknown - Invalid bounding box";
        }

        long whiteCount = 0;
        long lightGreenCount = 0;
        long darkGreenCount = 0;
        long creamyYellowCount = 0;
        long orangeCount = 0;

        Log.d("analyzeMaturity", "Iterating over pixels in bounding box...");
        for (int x = startX; x < endX; x++) {
            for (int y = startY; y < endY; y++) {
                int pixel = bitmap.getPixel(x, y);
                int red = (pixel >> 16) & 0xFF;
                int green = (pixel >> 8) & 0xFF;
                int blue = pixel & 0xFF;

                if (red > 220 && green > 220 && blue > 220) {
                    whiteCount++;
                } else if (green > red && green > blue && green > 180) {
                    lightGreenCount++;
                } else if (green > red && green > blue && green > 100 && green <= 180) {
                    darkGreenCount++;
                } else if (red > green && red > blue && red > 180 && green > 150) {
                    creamyYellowCount++;
                } else if (red > green && red > blue && red > 180 && green <= 90) {
                    orangeCount++;
                }
            }
        }

        Log.d("ColorCounts", "White: " + whiteCount +
                ", LightGreen: " + lightGreenCount +
                ", DarkGreen: " + darkGreenCount +
                ", CreamyYellow: " + creamyYellowCount +
                ", Orange: " + orangeCount);

        long totalPixels = whiteCount + lightGreenCount + darkGreenCount + creamyYellowCount + orangeCount;
        if (totalPixels == 0) {
            Log.e("analyzeMaturity", "No pixels analyzed.");
            return "Unknown - No valid pixels";
        }

        if (whiteCount > 0.4 * totalPixels) {
            return "Unripe";
        } else if (lightGreenCount > 0.3 * totalPixels || lightGreenCount > darkGreenCount) {
            return "Underripe";
        } else if (darkGreenCount > 0.25 * totalPixels || creamyYellowCount > 0.2 * totalPixels) {
            return "Ripe";
        } else if (orangeCount > 0.15 * totalPixels) {
            return "Overripe";
        }
        return "Unknown";
    }

    private void uploadScanResult(String ripeness, String confidence, Bitmap bitmap, String scanType) {
        Log.d(TAG, "Starting upload of scan result to Firestore");
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String deviceId = sharedPreferences.getString("session_device_id", "unknown");

        String currentDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        String fileName = "images/" + System.currentTimeMillis() + ".jpg";
        StorageReference imageRef = storageRef.child(fileName);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = imageRef.putBytes(data);
        uploadTask.addOnSuccessListener(taskSnapshot -> {
            imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                String imageUrl = uri.toString();
                Log.d(TAG, "Image uploaded successfully. URL: " + imageUrl);

                Map<String, Object> scanResult = new HashMap<>();
                scanResult.put("device_id", deviceId);
                scanResult.put("date_time", currentDateTime);
                scanResult.put("result", ripeness);
                scanResult.put("confidence", confidence);
                scanResult.put("image_url", imageUrl);
                scanResult.put("scan_type", scanType);

                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("scan_results")
                        .add(scanResult)
                        .addOnSuccessListener(documentReference -> {
                            Log.d(TAG, "Document added with ID: " + documentReference.getId());
                            Toast.makeText(bResultActivity.this, "Scan result saved to Firestore", Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e -> {
                            Log.w(TAG, "Error adding document", e);
                            Toast.makeText(bResultActivity.this, "Failed to save scan result", Toast.LENGTH_SHORT).show();
                        });
            }).addOnFailureListener(e -> {
                Log.w(TAG, "Failed to get download URL", e);
                Toast.makeText(bResultActivity.this, "Failed to get image URL", Toast.LENGTH_SHORT).show();
            });
        }).addOnFailureListener(e -> {
            Log.w(TAG, "Image upload failed", e);
            Toast.makeText(bResultActivity.this, "Image upload failed", Toast.LENGTH_SHORT).show();
        });
    }
}
