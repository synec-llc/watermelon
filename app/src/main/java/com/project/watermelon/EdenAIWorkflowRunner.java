package com.project.watermelon;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class EdenAIWorkflowRunner {
    private static final String TAG = "EdenAIWorkflowRunner";
    private static final String TOKEN = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX2lkIjoiNzY2Y2ZkMjktYWZmMy00NmIwLWE3YmYtOWYxMTE0YTUyODBhIiwidHlwZSI6ImFwaV90b2tlbiJ9.m6Jz4XY0ETBW7ZjPpcOG9eHszPZDxKIBk2OsvjkRh6g";
    private static final String WORKFLOW_ID = "f191b0ac-c96b-4ccf-b781-5707408ac20b";
    private static final String BASE_URL = "https://api.edenai.run/v2/workflow/";
    File imageFile2;
    String ageGroup = "";

    private Context context;

    public EdenAIWorkflowRunner(Context context) {
        this.context = context;
    }

    public void runWorkflow(File imageFile) {
        if (imageFile == null || !imageFile.exists()) {
            Toast.makeText(context, "Invalid image file.", Toast.LENGTH_SHORT).show();
            return;
        }

        imageFile2 = imageFile;
        Toast.makeText(context, "Model is processing the image...", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "Starting workflow with image: " + imageFile.getAbsolutePath());
        new StartWorkflowTask().execute(imageFile);
    }

    private class StartWorkflowTask extends AsyncTask<File, Void, String> {
        @Override
        protected String doInBackground(File... params) {
            File imageFile = params[0];
            try {
                Log.d(TAG, "Preparing request to start workflow...");
                String url = BASE_URL + WORKFLOW_ID + "/execution/";
                Log.d(TAG, "Request URL: " + url);

                // Open HTTP connection
                HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Authorization", TOKEN);
                conn.setDoOutput(true);

                String boundary = "Boundary-" + System.currentTimeMillis();
                conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

                // Build multipart form data payload
                MultipartUtility multipartUtility = new MultipartUtility(conn, boundary);
                multipartUtility.addFilePart("image_input", imageFile);
                multipartUtility.addFilePart("file", imageFile); // Correct field name for EdenAI


                // Finish and get the server's response
                int responseCode = conn.getResponseCode();
                Log.d(TAG, "Response Code: " + responseCode);

                if (responseCode == 200 || responseCode == 201) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder responseBuilder = new StringBuilder();
                    String line;
                    while ((line = in.readLine()) != null) {
                        responseBuilder.append(line);
                    }
                    in.close();

                    String responseString = responseBuilder.toString();
                    Log.d(TAG, "Workflow Response: " + responseString);

                    JSONObject responseJson = new JSONObject(responseString);
                    return responseJson.getString("id"); // Extract execution ID
                } else {
                    Log.e(TAG, "Failed to send request. HTTP code: " + responseCode);
                    BufferedReader errorReader = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                    StringBuilder errorResponse = new StringBuilder();
                    String errorLine;
                    while ((errorLine = errorReader.readLine()) != null) {
                        errorResponse.append(errorLine);
                    }
                    Log.e(TAG, "Error Response: " + errorResponse.toString());
                }

            } catch (Exception e) {
                Log.e(TAG, "Exception while sending request: ", e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(String executionId) {
            if (executionId != null) {
                Log.d(TAG, "Execution ID: " + executionId);
                new FetchResultTask().execute(executionId);
            } else {
                Log.e(TAG, "Failed to retrieve execution ID.");
                Toast.makeText(context, "Failed to start workflow.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class FetchResultTask extends AsyncTask<String, Void, String> {
        private static final int POLLING_INTERVAL_MS = 2000; // 2 seconds
        private static final int MAX_RETRIES = 10; // Maximum retries

        @Override
        protected String doInBackground(String... params) {
            String executionId = params[0];
            String result = null;
            int retryCount = 0;

            while (retryCount < MAX_RETRIES) {
                try {
                    Log.d(TAG, "Polling results for execution ID: " + executionId);
                    URL url = new URL(BASE_URL + WORKFLOW_ID + "/execution/" + executionId + "/");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setRequestProperty("Authorization", TOKEN);

                    int responseCode = conn.getResponseCode();
                    Log.d(TAG, "Polling Response Code: " + responseCode);

                    if (responseCode == 200) {
                        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                        StringBuilder responseBuilder = new StringBuilder();
                        String line;
                        while ((line = in.readLine()) != null) {
                            responseBuilder.append(line);
                        }
                        in.close();

                        String responseString = responseBuilder.toString();
                        Log.d(TAG, "Polling Response: " + responseString);

                        JSONObject responseJson = new JSONObject(responseString);
                        String status = responseJson.getJSONObject("content").getString("status");

                        if ("success".equalsIgnoreCase(status) || "succeeded".equalsIgnoreCase(status) ) {
                            result = responseJson.toString(); // Final result
                            break;
                        } else if ("error".equalsIgnoreCase(status)) {
                            Log.e(TAG, "Error status received: " + responseJson.toString());
                            break;
                        } else {
                            Log.d(TAG, "Status is still 'running'. Retrying...");
                        }
                    } else {
                        Log.e(TAG, "Failed to fetch results. HTTP code: " + responseCode);
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Exception while fetching results: ", e);
                }

                // Increment retry count and sleep before retrying
                retryCount++;
                try {
                    Thread.sleep(POLLING_INTERVAL_MS);
                } catch (InterruptedException e) {
                    Log.e(TAG, "Sleep interrupted: ", e);
                }
            }

            return result;
        }


        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                Log.d(TAG, "Final Workflow Result: " + result);
                boolean targetFound = isTargetObjectDetected(result);
                if (targetFound) {
                    Log.d(TAG, "Target object detected in the image.");
                    Toast.makeText(context, "Target object detected!", Toast.LENGTH_SHORT).show();
                } else {
                    Log.d(TAG, "No target object detected.");
                    Toast.makeText(context, "No target object detected.", Toast.LENGTH_SHORT).show();
                }

                // Pass the result and image path to the next activity
                Intent i = new Intent(context, bResultActivity.class);
                i.putExtra("result", result);
                i.putExtra("age", ageGroup);
                i.putExtra("image_path", imageFile2.getAbsolutePath());
                context.startActivity(i);

            } else {
                Log.e(TAG, "Failed to retrieve final results.");
                Toast.makeText(context, "Failed to fetch results.", Toast.LENGTH_SHORT).show();
            }
        }





    }

    public static boolean isTargetObjectDetected(String jsonResponse) {
        List<Map<String, Object>> detections = extractDetectionData(jsonResponse);
        // Define valid labels: watermelon, mobile phone, computer,
        // and additional fruits that might resemble a watermelon
        Set<String> validLabels = new HashSet<>();
        validLabels.add("watermelon");
        validLabels.add("mobile phone");
        validLabels.add("computer");
        validLabels.add("pumpkin");       // example fruit that might look similar
        validLabels.add("cantaloupe");    // another similar fruit
        validLabels.add("honeydew");      // and another example

        for (Map<String, Object> detection : detections) {
            String label = (String) detection.get("type");
            if (label != null && validLabels.contains(label.toLowerCase())) {
                return true;
            }
        }
        return false;
    }


    public static List<Map<String, Object>> extractDetectionData(String jsonResponse) {
        List<Map<String, Object>> detectionResults = new ArrayList<>();

        try {
            // Parse the root JSON object
            JSONObject root = new JSONObject(jsonResponse);
            JSONObject content = root.getJSONObject("content");
            JSONObject results = content.getJSONObject("results");
            JSONObject objectDetection = results.getJSONObject("image__object_detection");

            // Get the results array
            JSONArray detectionArray = objectDetection.getJSONArray("results");

            for (int i = 0; i < detectionArray.length(); i++) {
                JSONObject detection = detectionArray.getJSONObject(i);

                if (detection.has("items")) {
                    JSONArray items = detection.getJSONArray("items");

                    for (int j = 0; j < items.length(); j++) {
                        JSONObject item = items.getJSONObject(j);

                        // Extract relevant data
                        String type = item.getString("label");
                        double confidence = item.getDouble("confidence");
                        double xMin = item.getDouble("x_min");
                        double xMax = item.getDouble("x_max");
                        double yMin = item.getDouble("y_min");
                        double yMax = item.getDouble("y_max");

                        // Store in a map
                        Map<String, Object> detectionData = new HashMap<>();
                        detectionData.put("type", type);
                        detectionData.put("confidence", confidence);
                        detectionData.put("coordinates", new double[]{xMin, xMax, yMin, yMax});

                        // Add to results list
                        detectionResults.add(detectionData);
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return detectionResults;
    }
}
