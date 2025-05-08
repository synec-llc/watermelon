package com.project.watermelon;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import androidx.recyclerview.widget.RecyclerView;

import android.widget.ShareActionProvider;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class bScanHistoryActivity extends AppCompatActivity {

    private TextView scanHistorySubtitle;
    private RecyclerView scanHistoryRecyclerView;

    private String scanType;  // Global variable to hold the passed scan_type
    private ScanHistoryAdapter adapter;
    private ArrayList<ScanType> scanList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_bscan_history);

        // Edge-to-edge insets handling
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // 1. Call the RV and subtitle
        scanHistorySubtitle = findViewById(R.id.scanHistorySubtitle);
        scanHistoryRecyclerView = findViewById(R.id.scanHistoryRecyclerView);

        // 2. Get the intent scan_type and save it as a global variable
        scanType = getIntent().getStringExtra("scan_type");

        SharedPreferences sharedPreferences = getSharedPreferences(aSplash.PREFS_NAME, MODE_PRIVATE);
        String deviceID = sharedPreferences.getString("session_device_id","");
        // 3. Append that to subtitle as "List of " + scan_type + " scans"
        scanHistorySubtitle.setText("List of " + scanType + " scans");

        // Setup RecyclerView
        scanHistoryRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ScanHistoryAdapter(scanList);
        scanHistoryRecyclerView.setAdapter(adapter);

        // 4. Based on the scan_type, query the firestore collection "scan_results"
        //    where field "scan_type" == scanType variable
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("scan_results")
                .whereEqualTo("scan_type", scanType)
                .whereEqualTo("device_id", deviceID)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        // 5. Put them in the RecyclerView
                        for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                            ScanType item = doc.toObject(ScanType.class);
                            scanList.add(item);
                        }
                        adapter.notifyDataSetChanged();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("bScanHistoryActivity", "Error getting scan results", e);
                    }
                });
    }
}
