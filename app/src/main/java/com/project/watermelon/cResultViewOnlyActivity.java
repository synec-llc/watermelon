package com.project.watermelon;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

public class cResultViewOnlyActivity extends AppCompatActivity {

    // Declare UI components
    private TextView resultTextView;
    private TextView ageAndScientificName;
    private TextView tvSummary;
    private TextView tvDamageSymptoms;
    private TextView tv_short_description;
    private TextView tvDescription;
    private TextView tvHostPlants;
    private TextView tvGeographicalDistribution;
    private ImageView imageView; // Main image at the top of the ScrollView
    private ImageView imgLifeCycle;
    private ImageView image1, image2, image3, image4;
    private Button homeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Force light mode
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(R.layout.activity_cresult_view_only);

        // Initialize UI components from XML
        resultTextView = findViewById(R.id.resultTextView);
        ageAndScientificName = findViewById(R.id.age_group);
        tvSummary = findViewById(R.id.tv_summary);
        tvDamageSymptoms = findViewById(R.id.tv_damage_symptoms);
        tv_short_description = findViewById(R.id.tv_short_description);
        tvDescription = findViewById(R.id.tv_description);
        tvHostPlants = findViewById(R.id.tv_host_plants);
        tvGeographicalDistribution = findViewById(R.id.tv_geographical_distribution);
        imageView = findViewById(R.id.imageView);
        imgLifeCycle = findViewById(R.id.img_life_cycle);
        image1 = findViewById(R.id.image1);
        image2 = findViewById(R.id.image2);
        image3 = findViewById(R.id.image3);
        image4 = findViewById(R.id.image4);
        homeButton = findViewById(R.id.homeButton);

        // Get the "result" (ripeness) and "confidence" string from the intent
        String ripeness = getIntent().getStringExtra("result");
        String confidence = getIntent().getStringExtra("confidence");
        if (ripeness == null) {
            ripeness = "unknown";
        }

        // Variables to hold the information based on ripeness
        String watermelonVariety;
        String nutritionalSummary;
        String ripenessIndicators;
        String commonIssues;
        int lifeCycleImageRes;
        String detailedDescription;
        String optimalGrowingConditions;
        String regionsCultivated;
        int[] imageResIds;
        int mainImageRes; // For updating the main image

        // Render information based on ripeness using switch-case
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
                mainImageRes = R.drawable.underripe_a; // Assumed main image resource for unripe
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
                mainImageRes = R.drawable.underripe_a; // Assumed main image resource for underripe
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
                mainImageRes = R.drawable.ripe_a; // Assumed main image resource for ripe
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
                mainImageRes = R.drawable.ripe_b; // Assumed main image resource for overripe
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
                mainImageRes = R.drawable.image_seedless_watermelon; // Assumed main image resource for unknown
                break;
        }

        // Update title text (resultTextView) and main image (imageView)
        updateTitleAndMainImage("Result: " + ripeness.toUpperCase() + " (" + confidence + ")", mainImageRes);

        // Update other UI components with the obtained information
        ageAndScientificName.setText(watermelonVariety);
        tvSummary.setText(nutritionalSummary);
        tv_short_description.setText(ripenessIndicators);
        tvDamageSymptoms.setText(commonIssues);
        tvDescription.setText(detailedDescription);
        tvHostPlants.setText(optimalGrowingConditions);
        tvGeographicalDistribution.setText(regionsCultivated);

        if (lifeCycleImageRes != 0) {
            imgLifeCycle.setImageResource(lifeCycleImageRes);
        } else {
            imgLifeCycle.setImageDrawable(null);
        }

        // Set images for additional image views in the grid
        ImageView[] imageViews = {image1, image2, image3, image4};
        for (int i = 0; i < imageViews.length; i++) {
            if (imageResIds[i] != 0) {
                imageViews[i].setImageResource(imageResIds[i]);
            } else {
                imageViews[i].setImageDrawable(null);
            }
        }

        // Home button returns to MainActivity
        homeButton.setOnClickListener(v -> {
            Intent intent = new Intent(cResultViewOnlyActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }

    /**
     * Updates the title text (resultTextView) and the main image (imageView).
     *
     * @param title      The title text to display.
     * @param imageResId The resource ID of the image to display.
     */
    private void updateTitleAndMainImage(String title, int imageResId) {
        resultTextView.setText(title);
        imageView.setImageResource(imageResId);
    }
}
