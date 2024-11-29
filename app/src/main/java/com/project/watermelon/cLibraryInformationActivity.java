package com.project.watermelon;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class cLibraryInformationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clibrary_information);

        // Retrieve the passed data
        String watermelonName = getIntent().getStringExtra("watermelonName");
        int watermelonImageResId = getIntent().getIntExtra("watermelonImageResId", -1);

        // Find the views in your layout
        TextView nameTextView = findViewById(R.id.watermelonNameTextView);
        ImageView watermelonImageView = findViewById(R.id.watermelonImageView);
        TextView introductionTextView = findViewById(R.id.introductionTextView);
        TextView bulletedListTextView = findViewById(R.id.bulletedListTextView);
        Button goHomeButton = findViewById(R.id.goHomeButton);
        Button learnMoreButton = findViewById(R.id.learnMoreButton);

        // Set the watermelon name and image
        nameTextView.setText(watermelonName);
        if (watermelonImageResId != -1) {
            watermelonImageView.setImageResource(watermelonImageResId);
        }

        // Set introduction text and bulleted list based on watermelon name
        String introductionText = "";
        String bulletedText = "";
        String wikipediaLink = "https://en.wikipedia.org/wiki/watermelon";

        switch (watermelonName) {
            case "Seedless Watermelon":
                introductionText = "Seedless watermelons have gained immense popularity due to their convenience and the absence of seeds, making them easy to enjoy fresh. With a crisp texture and sweet flavor, they are a favorite choice for summer picnics and fruit salads.";
                bulletedText = "• Taste: Typically sweeter and crisper due to a higher concentration of sugars.\n"
                        + "• Color of Flesh: Bright red or pink flesh.\n"
                        + "• Seed Presence: Seedless, which makes it easier to eat but may lack some of the richer flavors found in seeded varieties.\n"
                        + "• Size: Medium to large, usually weighing between 5 to 30 pounds.\n"
                        + "• Shape and Rind Texture: Can be round or oblong, with smooth green skin.\n"
                        + "• Sweetness Levels: Generally high sweetness levels, often measured with a brix count indicating sweeter fruit.";
                wikipediaLink = "https://en.wikipedia.org/wiki/Seedless_watermelon";
                break;

            case "Red Watermelon":
                introductionText = "Red watermelons are the classic variety that many people envision when thinking of summer fruit. Known for their juicy, sweet flesh and vibrant color, they are often enjoyed sliced, cubed, or blended into refreshing drinks.";
                bulletedText = "• Taste: Sweet, refreshing flavor, commonly enjoyed fresh.\n"
                        + "• Color of Flesh: Deep red flesh, known for high levels of lycopene.\n"
                        + "• Seed Presence: Can be either seeded or seedless; seeded varieties often have a richer taste.\n"
                        + "• Size: Standard varieties range from 10 to 25 pounds.\n"
                        + "• Shape and Rind Texture: Usually round or oval with a green rind that may have stripes.\n"
                        + "• Sweetness Levels: High sweetness, typically with a brix count around 10-12.";
                wikipediaLink = "https://en.wikipedia.org/wiki/Watermelon";
                break;

            case "Yellow Watermelon":
                introductionText = "Yellow watermelons offer a delightful twist on the traditional red variety with their bright, sunny flesh and unique taste. Their sweetness is often compared to honey, making them a refreshing option for those looking for something different.";
                bulletedText = "• Taste: Sweet with a slightly different flavor profile compared to red varieties; some describe it as honey-like.\n"
                        + "• Color of Flesh: Bright yellow flesh.\n"
                        + "• Seed Presence: Can be either seeded or seedless.\n"
                        + "• Size: Similar to red watermelons, often medium to large.\n"
                        + "• Shape and Rind Texture: Usually round or oblong, with a smooth green rind.\n"
                        + "• Sweetness Levels: Comparable sweetness to red watermelons, often measuring around 10-12 on the brix scale.";
                wikipediaLink = "https://en.wikipedia.org/wiki/Yellow_watermelon";
                break;

            case "Mini Watermelon":
                introductionText = "Mini watermelons are the perfect personal-sized fruit, providing all the sweet, juicy goodness of their larger counterparts in a more manageable size. Ideal for small households or picnics, these bite-sized treats are sweet and fun to eat.";
                bulletedText = "• Taste: Sweet and crisp, often considered more flavorful due to higher sugar concentrations.\n"
                        + "• Color of Flesh: Typically deep red.\n"
                        + "• Seed Presence: Mostly seedless.\n"
                        + "• Size: Much smaller, weighing between 5 to 10 pounds.\n"
                        + "• Shape and Rind Texture: Round shape with smooth skin.\n"
                        + "• Sweetness Levels: High sweetness levels, often sweeter than larger varieties.";
                wikipediaLink = "https://en.wikipedia.org/wiki/Watermelon";
                break;

            case "Mountain Watermelon":
                introductionText = "Mountain watermelons are a lesser-known variety that thrives in higher elevations, often boasting a uniquely sweet flavor profile. Their adaptation to cooler climates gives them a distinct taste that is cherished by those who grow them.";
                bulletedText = "• Taste: Refreshingly sweet, often described as exceptionally flavorful.\n"
                        + "• Color of Flesh: Red or pink flesh.\n"
                        + "• Seed Presence: Usually seeded.\n"
                        + "• Size: Can vary but often ranges from medium to large.\n"
                        + "• Shape and Rind Texture: Typically round, with a dark green rind and possibly some stripes.\n"
                        + "• Sweetness Levels: High sweetness, often dependent on the specific variety.";
                wikipediaLink = "https://en.wikipedia.org/wiki/Watermelon";
                break;

            case "Pineapple Watermelon":
                introductionText = "Pineapple watermelons are known for their intriguing flavor that combines the sweetness of traditional watermelon with hints of pineapple. This unique taste makes them a favorite for fruit salads and exotic dessert recipes.";
                bulletedText = "• Taste: Unique sweet flavor with hints of pineapple; very refreshing.\n"
                        + "• Color of Flesh: Usually pink to red.\n"
                        + "• Seed Presence: Typically seeded.\n"
                        + "• Size: Medium to large.\n"
                        + "• Shape and Rind Texture: Round to oval, with a smooth green rind.\n"
                        + "• Sweetness Levels: High sweetness, often sweet enough to be eaten fresh or used in desserts.";
                wikipediaLink = "https://en.wikipedia.org/wiki/Watermelon";
                break;

            case "Sugar Baby Watermelon":
                introductionText = "Sugar Baby watermelons are a popular choice among gardeners for their compact size and extraordinarily sweet flavor. These small, round fruits pack a punch of sweetness, making them perfect for fresh eating and summer treats.";
                bulletedText = "• Taste: Extremely sweet and flavorful.\n"
                        + "• Color of Flesh: Deep red.\n"
                        + "• Seed Presence: Can be seeded or seedless.\n"
                        + "• Size: Small, typically around 6 to 10 pounds.\n"
                        + "• Shape and Rind Texture: Round with dark green skin.\n"
                        + "• Sweetness Levels: Very high sweetness, often measuring above 12 on the brix scale.";
                wikipediaLink = "https://en.wikipedia.org/wiki/Sugar_Baby_watermelon";
                break;

            case "Black Diamond Watermelon":
                introductionText = "Black Diamond watermelons are distinguished by their dark green, almost black rind and exceptionally sweet, juicy flesh. They are a traditional favorite, often sought after for their rich flavor and satisfying texture.";
                bulletedText = "• Taste: Known for its exceptionally sweet flavor and juicy texture.\n"
                        + "• Color of Flesh: Deep red flesh.\n"
                        + "• Seed Presence: Usually seeded.\n"
                        + "• Size: Large, can weigh up to 30 pounds.\n"
                        + "• Shape and Rind Texture: Oval to round with a dark green rind that may appear almost black.\n"
                        + "• Sweetness Levels: High sweetness, often very satisfying for consumers.";
                wikipediaLink = "https://en.wikipedia.org/wiki/Watermelon";
                break;

            case "Cream of Saskatchewan":
                introductionText = "Cream of Saskatchewan watermelons are unique for their pale yellowish-white flesh, offering a different taste experience from typical red varieties. Known for their creamy texture and sweetness, they are a rare delight for watermelon lovers.";
                bulletedText = "• Taste: Sweet with a creamy texture, often considered unique.\n"
                        + "• Color of Flesh: Pale yellowish-white flesh.\n"
                        + "• Seed Presence: Seeded.\n"
                        + "• Size: Medium to large.\n"
                        + "• Shape and Rind Texture: Round, with a smooth green rind.\n"
                        + "• Sweetness Levels: Moderately sweet, around 10-11 on the brix scale.";
                wikipediaLink = "https://en.wikipedia.org/wiki/Cream_of_Saskatchewan_watermelon";
                break;

            case "Jubilee Watermelon":
                introductionText = "Jubilee watermelons are celebrated for their bright pink/red flesh and excellent sweetness, making them a staple at summer gatherings. Their oblong shape and dark green stripes make them visually appealing, and their taste is simply irresistible.";
                bulletedText = "• Taste: Very sweet with a finely textured flesh.\n"
                        + "• Color of Flesh: Bright pink/red.\n"
                        + "• Seed Presence: Usually seeded.\n"
                        + "• Size: Large, can weigh between 25 to 40 pounds.\n"
                        + "• Shape and Rind Texture: Oblong shape with dark green stripes.\n"
                        + "• Sweetness Levels: High sweetness levels, often reaching above 12 on the brix scale.";
                wikipediaLink = "https://en.wikipedia.org/wiki/Jubilee_watermelon";
                break;
        }

        introductionTextView.setText(introductionText);
        bulletedListTextView.setText(bulletedText);

        // Set the OnClickListener for the "Go Home" button
        goHomeButton.setOnClickListener(v -> {
            Intent intent = new Intent(cLibraryInformationActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        // Set the OnClickListener for the "Learn More" button
        String finalWikipediaLink = wikipediaLink;
        learnMoreButton.setOnClickListener(v -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(finalWikipediaLink));
            startActivity(browserIntent);
        });
    }
}
