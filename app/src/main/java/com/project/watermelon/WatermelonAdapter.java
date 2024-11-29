package com.project.watermelon;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class WatermelonAdapter extends RecyclerView.Adapter<WatermelonAdapter.WatermelonViewHolder> {

    private final List<Watermelon> watermelonList;
    private final Context context; // Add context to start the new activity

    public WatermelonAdapter(Context context, List<Watermelon> watermelonList) {
        this.context = context;
        this.watermelonList = watermelonList;
    }

    @NonNull
    @Override
    public WatermelonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_watermelon, parent, false);
        return new WatermelonViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WatermelonViewHolder holder, int position) {
        Watermelon watermelon = watermelonList.get(position);
        holder.watermelonName.setText(watermelon.getName());
        holder.watermelonDescription.setText(watermelon.getDescription());
        holder.watermelonImage.setImageResource(watermelon.getImageResId());

        // Set an OnClickListener for the itemView (the entire item)
        holder.itemView.setOnClickListener(v -> {
            // Create an intent to navigate to cLibraryInformationActivity
            Intent intent = new Intent(context, cLibraryInformationActivity.class);

            // Pass the watermelon data to the new activity
            intent.putExtra("watermelonName", watermelon.getName());
            intent.putExtra("watermelonImageResId", watermelon.getImageResId());

            // Start the new activity
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return watermelonList.size();
    }

    public static class WatermelonViewHolder extends RecyclerView.ViewHolder {
        ImageView watermelonImage;
        TextView watermelonName, watermelonDescription;

        public WatermelonViewHolder(@NonNull View itemView) {
            super(itemView);
            watermelonImage = itemView.findViewById(R.id.watermelonImage);
            watermelonName = itemView.findViewById(R.id.watermelonName);
            watermelonDescription = itemView.findViewById(R.id.watermelonDescription);
        }
    }
}
