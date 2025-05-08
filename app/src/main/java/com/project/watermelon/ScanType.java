package com.project.watermelon;

public class ScanType {
    private String confidence;
    private String date_time;
    private String device_id;
    private String image_url;
    private String result;
    private String scan_type;

    // Needed for Firestore deserialization
    public ScanType() {}

    public ScanType(String confidence, String date_time, String device_id,
                    String image_url, String result, String scan_type) {
        this.confidence = confidence;
        this.date_time = date_time;
        this.device_id = device_id;
        this.image_url = image_url;
        this.result = result;
        this.scan_type = scan_type;
    }

    public String getConfidence() {
        return confidence;
    }

    public void setConfidence(String confidence) {
        this.confidence = confidence;
    }

    public String getDate_time() {
        return date_time;
    }

    public void setDate_time(String date_time) {
        this.date_time = date_time;
    }

    public String getDevice_id() {
        return device_id;
    }

    public void setDevice_id(String device_id) {
        this.device_id = device_id;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getScan_type() {
        return scan_type;
    }

    public void setScan_type(String scan_type) {
        this.scan_type = scan_type;
    }
}
