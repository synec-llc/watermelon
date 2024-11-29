package com.project.watermelon;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
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

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

        imageView = findViewById(R.id.imageView); // Add this to your XML layout
        resultTextView = findViewById(R.id.resultTextView); // Add this to your XML layout
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


        // Display the result image
        if (result != null) {
            try {
                // Parse and filter the result
                List<Map<String, Object>> detections = parseDetectionData(result);
                Log.d(TAG, "onCreate: JSONRESULTS");
                Log.d(TAG, "onCreate: " + detections);
                String typeFinal = "";

                // Log the filtered results
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

                // If image and detections are available, draw bounding boxes
                if (bitmap != null && !detections.isEmpty()) {
                    Bitmap mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
                    Canvas canvas = new Canvas(mutableBitmap);
                    Paint paint = new Paint();
                    paint.setColor(Color.RED);
                    paint.setStyle(Paint.Style.STROKE);
                    paint.setStrokeWidth(10);

                    // Customize text paint for labels and confidence
                    Paint textPaint = new Paint();
                    textPaint.setColor(Color.WHITE); // Set text color to white
                    textPaint.setTextSize(100);
                    textPaint.setStyle(Paint.Style.FILL);
                    textPaint.setShadowLayer(20f, 0f, 0f, Color.BLACK); // Add shadow for better visibility

//                    for (Map<String, Object> detection : detections) {
//                        Log.d(TAG, "Detected: "+detection.toString());
//                        String type = (String) detection.get("type");
//                        Log.d(TAG, "onCreate: "+type);
//                        if ("Watermelon".equals(type)) {
//                            Log.d(TAG, "lalala");
//
//
//
//                        }
//                    }

                    for (Map<String, Object> detection : detections) {
                        String type1 = (String) detection.get("type");
                        Log.d(TAG, "onCreate: "+type1);


                        if ("Watermelon".equals(type1)) {
                            Log.d(TAG, "lalala");
                            final String[] type = {(String) detection.get("type")};

                            double confidence = (double) detection.get("confidence");
                            double[] coordinates = (double[]) detection.get("coordinates");

                            // Convert confidence to percentage
                            String confidenceText = String.format("%.2f%%", confidence * 100);

                            // Get coordinates
                            float xMin = (float) coordinates[0] * bitmap.getWidth();
                            float yMin = (float) coordinates[2] * bitmap.getHeight();
                            float xMax = (float) coordinates[1] * bitmap.getWidth();
                            float yMax = (float) coordinates[3] * bitmap.getHeight();


                            // Draw the bounding box
                            canvas.drawRect(new RectF(xMin, yMin, xMax, yMax), paint);
                            String label = type[0] + " " + confidenceText;
                            float textWidth = textPaint.measureText(label);
                            Paint backgroundPaint = new Paint();
                            backgroundPaint.setColor(Color.RED); // Background color for the label
                            backgroundPaint.setStyle(Paint.Style.FILL);

                            float textHeight = textPaint.getTextSize();
                            float labelPadding = 8f; // Padding around the text
                            canvas.drawRect(xMin, yMin, xMin + textWidth + labelPadding * 2, yMin + textHeight + labelPadding * 2, backgroundPaint);

                            // Draw the text inside the bounding box
                            canvas.drawText(label, xMin + labelPadding, yMin + textHeight + labelPadding, textPaint);

                            // Set the modified image with bounding boxes and text
                            Log.d(TAG, "Mutable Bitmap now here");
                            imageView.setImageBitmap(mutableBitmap);

                            Log.d(TAG, "onResponseReceived: " + type[0]);
                            if (type[0].toLowerCase().equals("watermelon")) {
                                String maturityInfo = analyzeMaturity(bitmap, xMin, yMin, xMax, yMax);
                                Log.d(TAG, "Maturity Info: " + maturityInfo);

//                            putInformationAboutTheThePest(type[0], confidenceText, ageGroup);
                                putInformationAboutTheThePest(maturityInfo, confidenceText, ageGroup);
                            }
                        }else {
//                          Show an alert dialog
                            AlertDialog.Builder builder = new AlertDialog.Builder(this);
                            builder.setTitle("No Detected Watermelon ")
                                    .setMessage("There is no watermelon image identified in the provided image. Detected " + type1 + ". Returning to the main screen.")
                                    .setCancelable(false) // Prevent dismissing by tapping outside
                                    .setPositiveButton("OK", (dialog, which) -> {
                                        // Redirect to MainActivity and finish the current activity
                                        Intent intent2 = new Intent(this, MainActivity.class);
                                        intent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent2);
                                        finish();
                                    });

                            AlertDialog alert = builder.create();
                            alert.show();
                        }

                    }
                }

            } catch (Exception e) {
                Log.e(TAG, "Failed to process result: " + e.getMessage(), e);
            }
        } else {
            Log.e(TAG, "No result found in Intent.");
        }


        Button homeButton = findViewById(R.id.homeButton);

        homeButton.setOnClickListener(v -> {
            Intent intent2 = new Intent(bResultActivity.this, MainActivity.class); // Replace `CurrentActivity` with your current activity name
            intent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent2);
            finish(); // Close the current activity
        });


    }


    private void putInformationAboutTheThePest(String ripeness, String confidence, String age) {
        // Set the result text with pest type and confidence
        resultTextView.setText(ripeness + " (" + confidence + ")");

// Variables to hold dynamic content for watermelon
        String watermelonVariety = ""; // For the variety of the watermelon
        String nutritionalSummary = ""; // Nutritional information summary
        String ripenessIndicators = ""; // Indicators of ripeness
        String commonIssues = ""; // Common issues (e.g., overripe, underripe, pests)
        int lifeCycleImageRes = 0; // Replace with an image showing growth stages, if applicable
        String detailedDescription = ""; // Detailed description of the watermelon variety
        String optimalGrowingConditions = ""; // Growing conditions (e.g., temperature, soil type)
        String regionsCultivated = ""; // Geographical regions where it is cultivated
        int[] imageResIds = new int[4]; // Array of image resources (e.g., cross-section, full fruit, vine)


        // Determine content based on pest type
        switch (ripeness.toLowerCase()) {
            case "unripe":
                watermelonVariety = "Unripe Watermelon";
                nutritionalSummary = "Unripe watermelon is bland, low in natural sugars, and lacks the characteristic sweetness and juiciness of ripe fruit.";
                ripenessIndicators = "The most apparent visual indicator of an unripe watermelon is its pale green or whitish rind. These watermelons often lack the well-defined stripes or deep green coloration associated with ripeness. Additionally, the ground spot on the underside, where the watermelon rests on the soil, is typically white or very pale rather than the creamy yellow seen in ripe fruits. Internally, the flesh may appear whitish, pale pink, or light green, with a hard texture that is difficult to bite into. The seeds are often underdeveloped and white, rather than black and fully mature.";
                commonIssues = "Consuming an unripe watermelon can be an unsatisfactory experience due to its bland or sour taste and unappealing texture. The lack of sweetness makes it less desirable as a fruit, and its harder flesh can be unpleasant to chew. Additionally, unripe watermelons are not as hydrating as ripe ones due to their lower water content. For farmers, harvesting unripe watermelons too early can lead to poor market acceptance and reduced profit margins. Improper monitoring of ripeness indicators, such as the ground spot color and tendril dryness, often results in unripe harvests.";
                lifeCycleImageRes = R.drawable.watermelon_lifecycle; // Replace with an appropriate image resource
                detailedDescription = "Unripe watermelons are those harvested prematurely, before the fruit has completed its natural ripening process. These watermelons are often smaller in size compared to their ripe counterparts. Their outer rind lacks the vibrant, deep green shade associated with maturity, and the interior flesh is pale and firm, indicating underdeveloped sugar content. Tapping on an unripe watermelon produces a high-pitched sound, unlike the deep hollow sound of a ripe one. While they are safe to eat, unripe watermelons are rarely enjoyable due to their lack of flavor and texture. In agricultural settings, unripe watermelons are often a result of premature harvesting driven by market demand or improper ripeness assessment. Ensuring optimal ripeness before harvest is crucial for maximizing the fruit's nutritional value, taste, and marketability.";
                optimalGrowingConditions = "To avoid unripe watermelons, it is essential to provide the plant with adequate growing conditions. Watermelons thrive in warm climates with plenty of sunlight and well-drained sandy loam soil. Regular watering during the early growth stages is critical, but overwatering should be avoided as the fruit matures, as it can delay ripening or affect sugar concentration. Monitoring the ripeness indicators, such as the ground spot color and the condition of the tendril nearest the fruit, is key to ensuring proper harvest timing.";
                regionsCultivated = "Unripe watermelons are a common occurrence in regions where farmers are forced to harvest prematurely due to time constraints, environmental factors, or market demand. These regions often lack sufficient infrastructure or knowledge for proper ripeness monitoring. Additionally, areas with shorter growing seasons may see higher incidences of unripe watermelons as plants struggle to complete their natural maturation process before harvest.";
                imageResIds = new int[]{
                        R.drawable.unripe_a,
                        R.drawable.unripe_b,
                        R.drawable.unripe_c,
                        R.drawable.unripe_d
                };
                break;

            case "underripe":
                watermelonVariety = "Underripe Watermelon";
                nutritionalSummary = "An underripe watermelon is moderately sweet and juicy but lacks the rich flavor, full antioxidants, and refreshing quality of a ripe fruit.";
                ripenessIndicators = "Externally, underripe watermelons show some improvement in their coloration compared to unripe ones, with a more prominent green rind that may display faint or less-defined stripes. However, the ground spot, the area of the rind resting on the ground, remains pale or light yellow, signaling incomplete ripening. Internally, the flesh of an underripe watermelon is pinkish-red rather than the vibrant red of a ripe watermelon. The texture may feel slightly firmer, and while the seeds are more developed than in unripe watermelons, they might still include a mix of white and black seeds, indicating the fruit's transitional stage.";
                commonIssues = "While underripe watermelons are closer to ripeness, they still fall short in flavor and texture, making them less enjoyable to eat. The sweetness is mild and sometimes accompanied by a slightly sour or vegetal aftertaste. For farmers and vendors, selling underripe watermelons can result in customer dissatisfaction, as the fruit does not meet expectations for a fully ripe watermelon. Inconsistent monitoring of ripeness indicators, such as the tendril and ground spot color, is a common cause of underripe harvests. Additionally, underripe watermelons are prone to faster dehydration once picked, as their skin has not fully developed its natural protective qualities.";
                lifeCycleImageRes = R.drawable.watermelon_lifecycle; // Replace with an appropriate image resource
                detailedDescription = "Underripe watermelons are a step closer to ripeness but remain in a transitional phase where their full flavor, color, and texture have not yet developed. These fruits often exhibit light green rinds with partially defined stripes and ground spots that are pale yellow instead of creamy yellow. Tapping an underripe watermelon may yield a sound that is neither hollow nor sharp but somewhere in between, indicating incomplete ripening. The internal flesh, while edible, is less satisfying due to its muted sweetness and firmer texture. Farmers and consumers alike can misjudge the ripeness of these fruits, especially in large-scale harvests where the process is rushed. To maximize taste and nutritional benefits, it is essential to allow the watermelon to remain on the vine until clear ripeness indicators are present.";
                optimalGrowingConditions = "For a watermelon to progress from underripe to fully ripe, it requires an environment with consistent warmth, ample sunlight, and nutrient-rich, well-drained soil. Adequate irrigation is necessary during the early stages of growth, but reducing water as the fruit nears maturity helps concentrate the sugars within. Close monitoring of ripeness indicators such as the tendril (which dries and turns brown when the fruit is ripe) and the ground spot (which turns creamy yellow) is crucial for ensuring proper harvest timing.";
                regionsCultivated = "Underripe watermelons are often found in areas where harvesting schedules are dictated by external factors such as early market demand, unpredictable weather, or transportation logistics. Regions with inconsistent growing conditions, such as fluctuating temperatures or irregular rainfall, may also produce more underripe watermelons as the plants struggle to complete their growth cycle. These factors often lead to harvesting before the fruit can achieve full ripeness.";
                imageResIds = new int[]{
                        R.drawable.underripe_a,
                        R.drawable.underripe_b,
                        R.drawable.underripe_c,
                        R.drawable.underripe_d
                };
                break;
            case "ripe":
                watermelonVariety = "Ripe Watermelon";
                nutritionalSummary = "A ripe watermelon is sweet, juicy, hydrating, and rich in nutrients like vitamin C, vitamin A, and antioxidants such as lycopene.";
                ripenessIndicators = "Externally, ripe watermelons have a dark green rind with clearly defined, vibrant stripes. The underside of the fruit, known as the ground spot, has transitioned to a creamy yellow color, a key indicator of full ripeness. The tendril closest to the stem is dry and brown, another sign that the watermelon has reached its peak. Internally, the flesh is bright red, juicy, and sweet, with fully developed black seeds. When tapped, a ripe watermelon produces a deep, hollow sound, signaling that it is full of water and ready to eat.";
                commonIssues = "While ripe watermelons are at their best for consumption, they are also at their most perishable. Improper storage or delayed transportation can cause them to spoil quickly, leading to soft spots, decay, or an overripe state. Additionally, ripe watermelons are more prone to damage during handling due to their softer flesh and thinner rind compared to unripe ones. Farmers and vendors must take extra care during harvesting, packing, and shipping to maintain the fruit's quality and extend its shelf life.";
                lifeCycleImageRes = R.drawable.watermelon_lifecycle; // Replace with an appropriate image resource
                detailedDescription = "A ripe watermelon represents the ideal stage of ripeness, offering a perfect combination of sweetness and juiciness that satisfies the palate. At this stage, the fruit has completed its natural sugar conversion process, resulting in a deep red flesh that is both visually appealing and flavorful. The exterior features a glossy dark green rind with contrasting light green stripes, indicating full development. The ground spot on the underside of the watermelon has turned creamy yellow, signifying prolonged contact with the soil during its maturation. The seeds are fully black and mature, further confirming the fruit's readiness. Tapping on a ripe watermelon produces a distinct, deep, and hollow sound, indicating it is full of water and perfectly ripe for consumption. Ripe watermelons are versatile and can be enjoyed fresh, in salads, blended into juices, or used in a variety of culinary applications.";
                optimalGrowingConditions = "Ripe watermelons thrive in warm, sunny environments with consistent temperatures and well-drained soil. They require careful irrigation to ensure steady growth, with reduced watering during the final weeks to concentrate the fruit's natural sugars. Proper fertilization with balanced nutrients supports healthy vine and fruit development. Monitoring the ground spot color, tendril dryness, and rind texture is critical for determining the perfect time to harvest. Harvesting too early can result in underripe fruit, while waiting too long may lead to overripeness.";
                regionsCultivated = "Ripe watermelons are widely cultivated in regions with long, hot growing seasons, such as tropical and subtropical climates. Countries like the Philippines, India, and parts of the United States (e.g., Texas, Florida, and Georgia) are known for producing high-quality ripe watermelons. These regions offer the optimal conditions of warm weather, fertile soil, and sufficient sunlight, enabling watermelons to reach their full ripeness.";
                imageResIds = new int[]{
                        R.drawable.ripe_a,
                        R.drawable.ripe_b,
                        R.drawable.ripe_c,
                        R.drawable.ripe_d
                };
                break;
            case "overripe":
                watermelonVariety = "Overripe Watermelon";
                nutritionalSummary = "An overripe watermelon is overly sweet with soft, grainy flesh, diminished hydration, and reduced nutritional value.";
                ripenessIndicators = "Externally, overripe watermelons often show signs of their advanced state. The rind may appear darker and duller, sometimes even faded, and the stripes lose their sharp contrast. The ground spot may transition from creamy yellow to a deeper yellow-orange or even a brownish hue. Internally, the flesh becomes excessively soft and watery, losing its vibrant red color and taking on a darker or mushy appearance. The seeds may separate from the flesh and appear loose, further indicating overripeness. When tapped, an overripe watermelon produces a dull, muffled sound instead of the characteristic hollow resonance of a ripe fruit.";
                commonIssues = "Overripe watermelons pose several issues for both consumers and farmers. Their overly soft flesh and watery texture make them unappealing to eat fresh, and their flavor may be overly sweet, tangy, or fermented. These watermelons spoil rapidly, often developing mold or a sour smell if not consumed immediately. For farmers, overripe watermelons represent a loss of marketability, as their fragile state makes them difficult to transport and sell. Vendors often face customer dissatisfaction when overripe watermelons are mistaken for ripe ones due to their similar external appearance in some cases.";
                lifeCycleImageRes = R.drawable.watermelon_lifecycle; // Replace with an appropriate image resource
                detailedDescription = "Overripe watermelons are fruits that have surpassed their peak stage of ripeness, resulting in diminished quality. The most obvious sign is the fruit's overly soft and watery flesh, which lacks the firmness and texture of a ripe watermelon. The flesh may begin to separate from the seeds, and the once-vibrant red color may darken or fade. The rind, which is typically firm and glossy in ripe watermelons, becomes dull and may even soften in places. The ground spot, located on the underside of the watermelon, often turns orange or brown, signaling the fruit's extended contact with the ground. Additionally, overripe watermelons can develop a fermented smell or taste, making them less desirable for consumption. Despite these drawbacks, overripe watermelons can still be used in recipes like smoothies or jams, where their softer texture and intensified sweetness can be an advantage.";
                optimalGrowingConditions = "To avoid overripeness, watermelons should be harvested promptly once they show clear ripeness indicators, such as a creamy yellow ground spot and a dry, brown tendril near the stem. Prolonged exposure to sunlight and heat can accelerate overripeness, so proper timing is essential. Additionally, post-harvest handling and storage conditions, such as cool and dry environments, can help extend the shelf life of ripe watermelons and prevent them from becoming overripe prematurely.";
                regionsCultivated = "Overripe watermelons are a common occurrence in regions where harvesting schedules are delayed due to logistical or labor challenges. Tropical and subtropical climates, where temperatures remain high for extended periods, often see a higher prevalence of overripeness if the fruit is not harvested and sold quickly. Improper storage conditions, such as excessive heat or humidity, can also accelerate overripeness, particularly in areas with limited infrastructure for cold storage.";
                imageResIds = new int[]{
                        R.drawable.overripe_a,
                        R.drawable.overripe_b,
                        R.drawable.overripe_c,
                        R.drawable.overripe_d
                };
                break;
        }


        // Set the age and scientific name
        ageAndScientificName.setText(watermelonVariety);

        // Populate the TextViews with the dynamic content
        tvSummary.setText(nutritionalSummary);
        tv_short_description.setText(ripenessIndicators);
        tvDamageSymptoms.setText(commonIssues);
        tvDescription.setText(detailedDescription);
        tvHostPlants.setText(optimalGrowingConditions);
        tvGeographicalDistribution.setText(regionsCultivated);

        // Set the lifecycle image
        if (lifeCycleImageRes != 0) {
            imgLifeCycle.setImageResource(lifeCycleImageRes);
            int finalLifeCycleImageRes = lifeCycleImageRes;
            imgLifeCycle.setOnClickListener(v -> openFullScreenImage(finalLifeCycleImageRes));
        } else {
            imgLifeCycle.setImageDrawable(null);
            imgLifeCycle.setOnClickListener(null);
        }

        // Set the images and their click listeners
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
        // Do nothing to disable the back button
    }

    private List<Map<String, Object>> parseDetectionData(String jsonResponse) {
        List<Map<String, Object>> detectionList = new ArrayList<>();
        try {
            // Convert the response into a JSONObject
            JSONObject root = new JSONObject(jsonResponse);
            JSONObject content = root.getJSONObject("content");
            JSONObject results = content.getJSONObject("results");
            JSONObject objectDetection = results.getJSONObject("image__object_detection");

            // Get all detection results
            JSONArray detectionArray = objectDetection.getJSONArray("results");

            // Use a map to track unique detection types
            Map<String, Map<String, Object>> uniqueDetections = new HashMap<>();

            // Iterate through all detection results
            for (int i = 0; i < detectionArray.length(); i++) {
                JSONObject detection = detectionArray.getJSONObject(i);

                if (detection.has("items")) {
                    JSONArray items = detection.getJSONArray("items");

                    for (int j = 0; j < items.length(); j++) {
                        JSONObject item = items.getJSONObject(j);

                        String type = item.getString("label");

                        // Skip if the type is already processed (to avoid duplicates)
                        if (uniqueDetections.containsKey(type)) {
                            continue;
                        }

                        // Extract confidence and coordinates
                        double confidence = item.getDouble("confidence");
                        double xMin = item.getDouble("x_min");
                        double xMax = item.getDouble("x_max");
                        double yMin = item.getDouble("y_min");
                        double yMax = item.getDouble("y_max");

                        // Store the data in a map
                        Map<String, Object> detectionData = new HashMap<>();
                        detectionData.put("type", type);
                        detectionData.put("confidence", confidence);
                        detectionData.put("coordinates", new double[]{xMin, xMax, yMin, yMax});

                        // Add unique detection to the map
                        uniqueDetections.put(type, detectionData);
                    }
                }
            }

            // Convert the map to a list of detections
            detectionList.addAll(uniqueDetections.values());

        } catch (Exception e) {
            Log.e(TAG, "Failed to parse detections: " + e.getMessage(), e);
        }

        return detectionList;
    }

    private String analyzeMaturity(Bitmap bitmap, float xMin, float yMin, float xMax, float yMax) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        // Convert bounding box coordinates to integer pixel values
        int startX = Math.max(0, (int) (xMin * width));
        int startY = Math.max(0, (int) (yMin * height));
        int endX = Math.min(width, (int) (xMax * width));
        int endY = Math.min(height, (int) (yMax * height));

        long whiteCount = 0;
        long lightGreenCount = 0;
        long darkGreenCount = 0;
        long creamyYellowCount = 0;
        long orangeCount = 0;

        // Iterate over the pixels in the bounding box
        for (int x = startX; x < endX; x++) {
            for (int y = startY; y < endY; y++) {
                int pixel = bitmap.getPixel(x, y);

                // Extract RGB values
                int red = (pixel >> 16) & 0xFF;
                int green = (pixel >> 8) & 0xFF;
                int blue = pixel & 0xFF;

                // Log RGB values for debugging
                Log.d("PixelColors", "Red: " + red + ", Green: " + green + ", Blue: " + blue);

                // Determine the dominant color category
                if (red > 240 && green > 240 && blue > 240) { // White
                    whiteCount++;
                } else if (red > 120 && green > 200 && blue > 120) { // Light Green
                    lightGreenCount++;
                } else if (red < 120 && green > 100 && blue < 80) { // Dark Green
                    darkGreenCount++;
                } else if (red > 200 && green > 180 && blue < 80) { // Creamy Yellow
                    creamyYellowCount++;
                } else if (red > 200 && green > 100 && blue < 80) { // Orange
                    orangeCount++;
                }
            }
        }

        // Log color counts for debugging
        Log.d("ColorCounts", "White: " + whiteCount +
                ", LightGreen: " + lightGreenCount +
                ", DarkGreen: " + darkGreenCount +
                ", CreamyYellow: " + creamyYellowCount +
                ", Orange: " + orangeCount);

        // Determine the ripeness level based on dominant color or ratios
        if (whiteCount > lightGreenCount && whiteCount > darkGreenCount && whiteCount > creamyYellowCount && whiteCount > orangeCount) {
            return "Unripe";
        } else if (lightGreenCount > whiteCount && lightGreenCount > darkGreenCount && lightGreenCount > creamyYellowCount && lightGreenCount > orangeCount) {
            return "Underripe";
        } else if (darkGreenCount + creamyYellowCount > whiteCount + lightGreenCount + orangeCount) {
            return "Ripe";
        } else if (orangeCount > whiteCount && orangeCount > lightGreenCount && orangeCount > darkGreenCount && orangeCount > creamyYellowCount) {
            return "Overripe";
        }

        Log.d(TAG, "analyzeMaturity: nothing detected");
        return "Ripe";
    }



}
