package com.rogi.View;

import android.content.Context;
import android.util.Log;

import com.rogi.logger.Logger;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.FileInputStream;
import java.net.SocketTimeoutException;

public class Upload_Image {
    public static final String TAG = "Upload_Image";

    String response_str;
    HttpEntity resEntity;

    Context context;

    public Upload_Image(Context c) {
        this.context = c;
    }


    public String uploadRegistration(String selectedImagePath, String firstName, String lastName, String email, String phoneNo, String password
            , String DeviceId, String VersionName, String companyName) {

        String urlString = Utils.MAIN_URL + Utils.REGISTER_USER_API;
        Logger.e(TAG, "URL :: " + urlString);
        try {
            HttpClient client = new DefaultHttpClient();
            HttpPost post = new HttpPost(urlString);
            MultipartEntity reqEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);

            if (Utils.validateString(selectedImagePath)) {
                File file1 = new File(selectedImagePath);
                FileInputStream fileInputStream = new FileInputStream(file1);
                reqEntity.addPart("profile_image", new InputStreamBody(fileInputStream, "image/jpeg", "file_name.jpg"));
            } else {
                reqEntity.addPart("profile_image", new StringBody(""));
            }

            reqEntity.addPart("first_name", new StringBody(firstName));
            reqEntity.addPart("last_name", new StringBody(lastName));
            reqEntity.addPart("email", new StringBody(email));
            reqEntity.addPart("phone_number", new StringBody(phoneNo));
            reqEntity.addPart("password", new StringBody(password));
            reqEntity.addPart("device_type", new StringBody(Utils.DEVICE_TYPE));
            reqEntity.addPart("device_token", new StringBody(DeviceId));
            reqEntity.addPart("app_version", new StringBody(VersionName));
            reqEntity.addPart("login_type", new StringBody(Utils.LOGIN_TYPE));
            reqEntity.addPart("company_name", new StringBody(companyName));
            reqEntity.addPart("timezone", new StringBody(Utils.getTimeZoneId()));

            post.setEntity(reqEntity);
            HttpResponse response = client.execute(post);
            resEntity = response.getEntity();
            response_str = EntityUtils.toString(resEntity);
            if (resEntity != null) {
                Logger.e(TAG, "RESPONSE" + response_str);
            }
        } catch (Exception ex) {
            response_str = "";
            Logger.e(TAG, "error: " + ex.getMessage());
        }

        return response_str;
    }

    public String uploadUpdateProfile(String selectedImagePath, String firstName, String lastName, String email, String phoneNo, String password
            , String session_token, String user_id, String company_name, String street, String city, String state,
                                      String country, String zipcode) {

        String urlString = Utils.MAIN_URL + Utils.UPDATE_USER_API;
        Logger.e(TAG, "URL :: " + urlString);
        try {
            HttpClient client = new DefaultHttpClient();
            HttpPost post = new HttpPost(urlString);

            MultipartEntity reqEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);

            if (Utils.validateString(selectedImagePath)) {
                File file1 = new File(selectedImagePath);
                FileInputStream fileInputStream = new FileInputStream(file1);
                reqEntity.addPart("profile_image", new InputStreamBody(fileInputStream, "image/jpeg", "file_name.jpg"));
            } else {
                reqEntity.addPart("profile_image", new StringBody(""));
            }

            reqEntity.addPart("first_name", new StringBody(firstName));
            reqEntity.addPart("last_name", new StringBody(lastName));
            reqEntity.addPart("email", new StringBody(email));
            reqEntity.addPart("phone_number", new StringBody(phoneNo));
            reqEntity.addPart("password", new StringBody(password));
            reqEntity.addPart("session_token", new StringBody(session_token));
            reqEntity.addPart("user_id", new StringBody(user_id));
            reqEntity.addPart("company_name", new StringBody(company_name));
            reqEntity.addPart("street", new StringBody(street));
            reqEntity.addPart("city", new StringBody(city));
            reqEntity.addPart("state", new StringBody(state));
            reqEntity.addPart("country", new StringBody(country));
            reqEntity.addPart("zipcode", new StringBody(zipcode));
            reqEntity.addPart("timezone", new StringBody(Utils.getTimeZoneId()));

            post.setEntity(reqEntity);
            HttpResponse response = client.execute(post);
            resEntity = response.getEntity();
            response_str = EntityUtils.toString(resEntity);
            if (resEntity != null) {
                Logger.e(TAG, "RESPONSE" + response_str);
            }
        } catch (Exception ex) {
            response_str = "";
            Logger.e(TAG, "error: " + ex.getMessage());
        }
        return response_str;
    }

    File file1;
    FileInputStream fileInputStream;

    public String uploadMedia(String user_id, String session_token, String project_id, String media_type, String selectedImagePath,
                              String lat, String lang, String street, String city, String state, String country, String pincode, String created_date,
                              String media_temp_id, String media_description) {
        file1 = new File(selectedImagePath);

        try {
            HttpClient client = new DefaultHttpClient();
            HttpPost post = new HttpPost(Utils.MAIN_URL + Utils.UPLOAD_MEDIA_API);
            HttpParams httpParameters = new BasicHttpParams();
            // Set the timeout in milliseconds until a connection is
            // established.
            int timeoutConnection = 600000;
            HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
            // Set the default socket timeout (SO_TIMEOUT) in milliseconds which
            // is the timeout for waiting for data.
            int timeoutSocket = 600000;
            HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);

            MultipartEntity reqEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
            // post.addHeader("Authorization",Applicationclass.prefrence.get_device_token());
            // FileBody bin1 = new FileBody(file1);
            fileInputStream = new FileInputStream(file1);

            reqEntity.addPart("user_id", new StringBody(user_id));
            reqEntity.addPart("project_id", new StringBody(project_id));
            reqEntity.addPart("session_token", new StringBody(session_token));
            reqEntity.addPart("media_type", new StringBody(media_type));
            if (media_type.equalsIgnoreCase("image")) {
                reqEntity.addPart("media", new InputStreamBody(fileInputStream, "image/jpeg", "file_name.jpg"));
            } else if (media_type.equalsIgnoreCase("video")) {
                reqEntity.addPart("media", new InputStreamBody(fileInputStream, "video/mp4", "file_name.mp4"));
            } else if (media_type.equalsIgnoreCase("audio")) {
                reqEntity.addPart("media", new InputStreamBody(fileInputStream, "audio/wav", "file_name.wav"));
            } else if (media_type.equalsIgnoreCase("doc")) {
                reqEntity.addPart("media", new InputStreamBody(fileInputStream, "application/msword", "file_name.doc"));
            } else if (media_type.equalsIgnoreCase("docx")) {
                reqEntity.addPart("media", new InputStreamBody(fileInputStream, "application/msword", "file_name.docx"));
            } else if (media_type.equalsIgnoreCase("txt")) {
                reqEntity.addPart("media", new InputStreamBody(fileInputStream, "text/plain", "file_name.txt"));
            } else if (media_type.equalsIgnoreCase("pdf")) {
                reqEntity.addPart("media", new InputStreamBody(fileInputStream, "application/pdf", "file_name.pdf"));
            }
            reqEntity.addPart("latitude", new StringBody(lat));
            reqEntity.addPart("longitude", new StringBody(lang));
            reqEntity.addPart("street", new StringBody(street));
            reqEntity.addPart("city", new StringBody(city));
            reqEntity.addPart("state", new StringBody(state));
            reqEntity.addPart("country", new StringBody(country));
            reqEntity.addPart("pincode", new StringBody(pincode));
            reqEntity.addPart("created_date", new StringBody(created_date));
            reqEntity.addPart("media_temp_id", new StringBody(media_temp_id));
            reqEntity.addPart("media_description", new StringBody(media_description));

            post.setEntity(reqEntity);
            HttpResponse response = client.execute(post);
            resEntity = response.getEntity();
            response_str = EntityUtils.toString(resEntity);
            if (resEntity != null) {
                Logger.e(TAG, "RESPONSE" + response_str);
            }
        } catch (SocketTimeoutException e) {
            e.printStackTrace();
            response_str = "";
        } catch (Exception ex) {
            response_str = "";
            Logger.e(TAG, "error: " + ex.getMessage());
        }
        return response_str;
    }


    public String updateMedia(String user_id, String session_token, String project_id, String media_id, String selectedImagePath,
                              String lat, String lang, String street, String city, String state, String country, String pincode,
                              String created_date, String media_temp_id, String media_description) {
        File file1 = new File(selectedImagePath);

        try {
            HttpClient client = new DefaultHttpClient();
            HttpPost post = new HttpPost(Utils.MAIN_URL + Utils.UPDATE_MEDIA_API);

            MultipartEntity reqEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
            // post.addHeader("Authorization",Applicationclass.prefrence.get_device_token());
            // FileBody bin1 = new FileBody(file1);
            FileInputStream fileInputStream = new FileInputStream(file1);

            reqEntity.addPart("user_id", new StringBody(user_id));
            reqEntity.addPart("project_id", new StringBody(project_id));
            reqEntity.addPart("session_token", new StringBody(session_token));
            reqEntity.addPart("media_type", new StringBody("image"));
            reqEntity.addPart("media_id", new StringBody(media_id));
            reqEntity.addPart("media", new InputStreamBody(fileInputStream, "image/jpeg", "file_name.jpg"));
            reqEntity.addPart("latitude", new StringBody(lat));
            reqEntity.addPart("longitude", new StringBody(lang));
            reqEntity.addPart("street", new StringBody(street));
            reqEntity.addPart("city", new StringBody(city));
            reqEntity.addPart("state", new StringBody(state));
            reqEntity.addPart("country", new StringBody(country));
            reqEntity.addPart("pincode", new StringBody(pincode));
            reqEntity.addPart("created_date", new StringBody(created_date));
            reqEntity.addPart("media_temp_id", new StringBody(media_temp_id));
            reqEntity.addPart("media_description", new StringBody(media_description));

            post.setEntity(reqEntity);
            HttpResponse response = client.execute(post);
            resEntity = response.getEntity();
            response_str = EntityUtils.toString(resEntity);
            if (resEntity != null) {
                Logger.e(TAG, "RESPONSE" + response_str);
            }
        } catch (Exception ex) {
            response_str = "";
            Logger.e(TAG, "error: " + ex.getMessage());
        }
        return response_str;
    }


    public String uploadSignature(String user_id, String session_token, String project_id, String signaturePath) {
        File file1 = new File(signaturePath);

        try {
            HttpClient client = new DefaultHttpClient();
            HttpPost post = new HttpPost(Utils.MAIN_URL + Utils.UPLOAD_SIGNATURE_API);
            MultipartEntity reqEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);

            FileInputStream fileInputStream = new FileInputStream(file1);


            reqEntity.addPart("user_id", new StringBody(user_id));
            reqEntity.addPart("project_id", new StringBody(project_id));
            reqEntity.addPart("session_token", new StringBody(session_token));
            reqEntity.addPart("signature_image", new InputStreamBody(fileInputStream, "image/jpeg", "file_name.jpg"));

            post.setEntity(reqEntity);
            System.out.println("reqEntity::" + reqEntity.toString());
            HttpResponse response = client.execute(post);
            resEntity = response.getEntity();
            response_str = EntityUtils.toString(resEntity);
            if (resEntity != null) {
                Log.i("RESPONSE signature", response_str);

            }
        } catch (Exception ex) {
            response_str = "";
            Log.e("Debug", "error: " + ex.getMessage(), ex);
        }
        return response_str;
    }


}
