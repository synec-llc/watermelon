package com.project.watermelon;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide; // Make sure to add Glide dependency in your build.gradle

import java.util.List;

public class ScanHistoryAdapter extends RecyclerView.Adapter<ScanHistoryAdapter.ScanHistoryViewHolder> {

    private List<ScanType> scanList;

    public ScanHistoryAdapter(List<ScanType> scanList) {
        this.scanList = scanList;
    }

    @NonNull
    @Override
    public ScanHistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_scan_history, parent, false);
        return new ScanHistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ScanHistoryViewHolder holder, int position) {
        ScanType currentScan = scanList.get(position);

        holder.tvResult.setText("Result: " + currentScan.getResult());
        holder.tvConfidence.setText("Confidence: " + currentScan.getConfidence());
        holder.tvDateTime.setText("Date/Time: " + currentScan.getDate_time());
        holder.tvDeviceId.setText("Device ID: " + currentScan.getDevice_id());
        holder.tvScanType.setText("Scan Type: " + currentScan.getScan_type());

        // Load the image_url into the ImageView (tvThumbnail) using Glide
        Glide.with(holder.itemView.getContext())
                .load(currentScan.getImage_url())
                .placeholder(R.drawable.ripe_d) // Optional placeholder image
                .error(R.drawable.overripe_d)             // Optional error image
                .into(holder.tvThumbnail);

        // When item is clicked, go to cLibrarylnformationActivity with extras
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), cResultViewOnlyActivity.class);
            intent.putExtra("result", currentScan.getResult());
            intent.putExtra("confidence", currentScan.getConfidence());
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return scanList.size();
    }

    static class ScanHistoryViewHolder extends RecyclerView.ViewHolder {
        TextView tvResult, tvConfidence, tvDateTime, tvDeviceId, tvScanType;
        ImageView tvThumbnail;

        public ScanHistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvResult = itemView.findViewById(R.id.tvResult);
            tvConfidence = itemView.findViewById(R.id.tvConfidence);
            tvDateTime = itemView.findViewById(R.id.tvDateTime);
            tvDeviceId = itemView.findViewById(R.id.tvDeviceId);
            tvScanType = itemView.findViewById(R.id.tvScanType);
            tvThumbnail = itemView.findViewById(R.id.ivThumbnail);
        }
    }
}
