package com.rogi.View;

import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by "Mayuri" on 9/1/18.
 */
public class Upload {

    public static final String UPLOAD_URL = Utils.MAIN_URL + Utils.UPLOAD_MEDIA_API;

    private int serverResponseCode;

    public String uploadVideo(String user_id, String session_token, String project_id, String media_type, String file,
                              String lat, String lang, String street, String city, String state, String country, String pincode,
                              String created_date,
                              String media_temp_id, String media_description) {


        String fileName = file;
        HttpURLConnection conn = null;
        DataOutputStream dos = null;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;

        File sourceFile = new File(file);
        if (!sourceFile.isFile()) {
            Log.e("Huzza", "Source File Does not exist");
            return null;
        }

        try {
            FileInputStream fileInputStream = new FileInputStream(sourceFile);
            URL url = new URL(UPLOAD_URL);
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("ENCTYPE", "multipart/form-data");
            conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);

            conn.setRequestProperty("user_id", user_id);
            conn.setRequestProperty("project_id",project_id);
            conn.setRequestProperty("session_token", session_token);
            conn.setRequestProperty("media_type",media_type);
            conn.setRequestProperty("media", fileName);

            conn.setRequestProperty("latitude", lat);
            conn.setRequestProperty("longitude", lang);
            conn.setRequestProperty("street", street);
            conn.setRequestProperty("city", city);
            conn.setRequestProperty("state", state);
            conn.setRequestProperty("country", country);
            conn.setRequestProperty("pincode", pincode);
            conn.setRequestProperty("created_date", created_date);
            conn.setRequestProperty("media_temp_id", media_temp_id);
            conn.setRequestProperty("media_description", media_description);


            dos = new DataOutputStream(conn.getOutputStream());

            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"myFile\";filename=\"" + fileName + "\"" + lineEnd);
            dos.writeBytes(lineEnd);

            bytesAvailable = fileInputStream.available();
            Log.i("Huzza", "Initial .available : " + bytesAvailable);

            bufferSize = Math.min(bytesAvailable, maxBufferSize);
            buffer = new byte[bufferSize];

            bytesRead = fileInputStream.read(buffer, 0, bufferSize);

            while (bytesRead > 0) {
                dos.write(buffer, 0, bufferSize);
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            }

            dos.writeBytes(lineEnd);
            dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

            serverResponseCode = conn.getResponseCode();

            fileInputStream.close();
            dos.flush();
            dos.close();
        } catch (MalformedURLException ex) {
            ex.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (serverResponseCode == 200) {
            StringBuilder sb = new StringBuilder();
            try {
                BufferedReader rd = new BufferedReader(new InputStreamReader(conn
                        .getInputStream()));
                String line;
                while ((line = rd.readLine()) != null) {
                    sb.append(line);
                }
                rd.close();
            } catch (IOException ioex) {
            }
            return sb.toString();
        } else {
            return "Could not upload";
        }
    }
}
