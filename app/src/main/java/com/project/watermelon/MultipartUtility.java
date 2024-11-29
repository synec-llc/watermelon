package com.project.watermelon;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.HttpURLConnection;

public class MultipartUtility {
    private final DataOutputStream outputStream;
    private final String boundary;

    public MultipartUtility(HttpURLConnection connection, String boundary) throws Exception {
        this.outputStream = new DataOutputStream(connection.getOutputStream());
        this.boundary = boundary;
    }

    public void addFilePart(String fieldName, File file) throws Exception {
        outputStream.writeBytes("--" + boundary + "\r\n");
        outputStream.writeBytes("Content-Disposition: form-data; name=\"" + fieldName + "\"; filename=\"" + file.getName() + "\"\r\n");
        outputStream.writeBytes("Content-Type: application/octet-stream\r\n\r\n");

        FileInputStream fileInputStream = new FileInputStream(file);
        byte[] buffer = new byte[4096];
        int bytesRead;
        while ((bytesRead = fileInputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
        }
        fileInputStream.close();

        outputStream.writeBytes("\r\n");
    }

    public void finish() throws Exception {
        outputStream.writeBytes("--" + boundary + "--\r\n");
        outputStream.flush();
        outputStream.close();
    }
}
