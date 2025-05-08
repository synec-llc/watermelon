package com.project.watermelon;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;


public class bLibraryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blibrary);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        RecyclerView recyclerView = findViewById(R.id.watermelonRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Create and set the adapter with context
        WatermelonAdapter adapter = new WatermelonAdapter(this, getWatermelonList());
        recyclerView.setAdapter(adapter);

    }

    // Create a list of watermelons
    private List<Watermelon> getWatermelonList() {
        List<Watermelon> watermelonList = new ArrayList<>();
        watermelonList.add(new Watermelon("Seedless Watermelon", "This watermelon has no seeds.", R.drawable.image_seedless_watermelon));
        watermelonList.add(new Watermelon("Red Watermelon", "The classic red-fleshed variety.", R.drawable.image_red_watermellon));
        watermelonList.add(new Watermelon("Yellow Watermelon", "A watermelon with yellow flesh.", R.drawable.image_yellow_watermelon));
        watermelonList.add(new Watermelon("Mini Watermelon", "A smaller version of the classic watermelon.", R.drawable.image_mini_watermelon));
        watermelonList.add(new Watermelon("Mountain Watermelon", "Grown in mountainous regions.", R.drawable.image_mountain_watermelon));
        watermelonList.add(new Watermelon("Pineapple Watermelon", "Slightly pineapple-flavored.", R.drawable.image_pineapple_watermelon));
        watermelonList.add(new Watermelon("Sugar Baby Watermelon", "Sweet and smaller-sized.", R.drawable.image_sugar_baby_watermelon));
        watermelonList.add(new Watermelon("Black Diamond Watermelon", "A darker-skinned variety.", R.drawable.image_black_diamond_watermelon));
        watermelonList.add(new Watermelon("Cream of Saskatchewan", "White-fleshed variety.", R.drawable.image_cream_of_saskatchewan));
        watermelonList.add(new Watermelon("Jubilee Watermelon", "Large, sweet, and striped.", R.drawable.image_jubilee_watermelon));
        return watermelonList;
    }
}
