package com.rogi.Activity;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.rogi.Database.DBHelper;
import com.rogi.Model.MediaModel;
import com.rogi.R;
import com.rogi.Service.GPSTracker;
import com.rogi.View.CustomVolleyRequest;
import com.rogi.View.Upload_Image;
import com.rogi.View.Utils;
import com.rogi.logger.Logger;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class ProjectMediaActivity extends AppCompatActivity implements View.OnClickListener, OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    public static final String TAG = "ProjectMediaActivity";
    private final int REQUEST_CODE_ASK_PERMISSIONS = 123;
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;
    final static int REQUEST_LOCATION = 199;
    private SharedPreferences mSharedPreferences;
    private LatLng mobileLocation;
    private ImageLoader imageLoader;

    LinearLayout editImageLayout, rootLayout, linAudioline;
    TextView titleText, txtDescription, txtAddress, txtDate, txtTime;
    FrameLayout frmLayout;
    NetworkImageView NetworkImgCamera;
    ImageView imgVideo, imgMediaDes, imgClose, imgOffline, imgAudioline;

    String TOKEN = "", USERID = "", project_id = "", media_id = "", media_type = "", media_url = "", videoThumbImg = "", description = "",
            latitude = "", longitude = "", street = "", city = "", state = "", country = "", pincode = "", created_date = "", setDateTime = "",
            project_Status = "", canvasImagePath = "", canvas_media_url = "", mediaSync = "", mediaStatus = "", docThumb = "";
    Bundle bundle;
    //    boolean imageFlag = false;
    double curLatitude = 0, curLongitude = 0;

    ProgressDialog pDialog;
    private GoogleMap mMap;
    private MapFragment mMapFragment;
    Bitmap imageBitmap;
    BitmapDescriptor icon;
    private AlertDialog.Builder builder;
    private AlertDialog dialog;
    TextView addDescBTN, resetBTN;
    ProgressDialog pdialog;
    EditText edtDescription;
    RequestQueue requestQueue;
    JSONObject projectObj;
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private DBHelper mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_media_view);
        requestQueue = Volley.newRequestQueue(this);
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        int currentapiVersion = Build.VERSION.SDK_INT;
        if (currentapiVersion > Build.VERSION_CODES.LOLLIPOP) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if ((this.checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) ||
                        (this.checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {

                    requestPermissions(new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION,
                                    android.Manifest.permission.ACCESS_FINE_LOCATION},
                            REQUEST_CODE_ASK_PERMISSIONS);
                }
            }
        }

        mSharedPreferences = getSharedPreferences(Utils.PREF_NAME, MODE_PRIVATE);
        USERID = mSharedPreferences.getString(Utils.USER_ID, "");
        TOKEN = mSharedPreferences.getString(Utils.TOKEN, "");
        init();
    }

    public void init() {
        titleText = (TextView) findViewById(R.id.titleText);
        titleText.setText("Media Details");

        rootLayout = (LinearLayout) findViewById(R.id.root_layout);
        frmLayout = (FrameLayout) findViewById(R.id.frmLayout);
        frmLayout.setVisibility(View.VISIBLE);
        linAudioline = (LinearLayout) findViewById(R.id.linAudioline);
        editImageLayout = (LinearLayout) findViewById(R.id.editImageLayout);
        NetworkImgCamera = (NetworkImageView) findViewById(R.id.NetworkImgCamera);
        imgOffline = (ImageView) findViewById(R.id.imgOffline);
        imgAudioline = (ImageView) findViewById(R.id.imgAudioline);
        imgVideo = (ImageView) findViewById(R.id.imgVideo);
        imgMediaDes = (ImageView) findViewById(R.id.imgMediaDes);
        txtDescription = (TextView) findViewById(R.id.txtDescription);
        txtAddress = (TextView) findViewById(R.id.txtAddress);
        txtDate = (TextView) findViewById(R.id.txtDate);
        txtTime = (TextView) findViewById(R.id.txtTime);

        editImageLayout.setOnClickListener(this);
        imgMediaDes.setOnClickListener(this);
        findViewById(R.id.backLayoutclick).setOnClickListener(this);

        bundle = getIntent().getExtras();
        if (bundle != null) {
            project_id = bundle.getString(Utils.PROJECT_ID);
            media_id = bundle.getString(Utils.MEDIA_ID);
            project_Status = bundle.getString(Utils.PROJECT_STATUS);

            String mediaObj = bundle.getString(Utils.MEDIA_OBJECT);
            Logger.e(TAG, "Media Obj : " + mediaObj);
            setMediaData(mediaObj);
        }

        mDbHelper = new DBHelper(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.editImageLayout:
                Intent intent = new Intent(ProjectMediaActivity.this, ImagePaintActivity.class);
                File file1 = new File(Environment.getExternalStorageDirectory() + "/Media/Image/", media_id + ".jpg");
                if (file1.exists()) {
                    intent.putExtra(Utils.MEDIA_IMAGE_URL, canvas_media_url);
                } else {
                    intent.putExtra(Utils.MEDIA_IMAGE_URL, media_url);
                }
                intent.putExtra(Utils.MEDIA_ID, media_id);
                startActivityForResult(intent, 1);
                break;

            case R.id.frmLayout:
                Intent videoIntent = new Intent(ProjectMediaActivity.this, VideoActivity.class);
                File file = new File(Environment.getExternalStorageDirectory() + "/Media/Video/", media_id + ".mp4");
                if (file.exists()) {
                    String url = Environment.getExternalStorageDirectory() + "/Media/Video/" + media_id + ".mp4";
                    videoIntent.putExtra(Utils.MEDIA_IMAGE_URL, url);
                } else {
                    videoIntent.putExtra(Utils.MEDIA_IMAGE_URL, media_url);
                }
                startActivity(videoIntent);
                break;

            case R.id.imgMediaDes:
                openMediaDescriptionView();
                break;

            case R.id.resetBTN:
                edtDescription.setText("");
                dialog.dismiss();
                break;

            case R.id.addDescBTN:
                if (Utils.validateString(edtDescription.getText().toString())) {
                    description = edtDescription.getText().toString();
                    if (Utils.checkInternetConnection(this)) {
                        if (Utils.validateString(project_id) && Utils.validateString(media_id)) {
                            UpdateMediaDescription(description);
                        }
                    } else {
                        try {
                            if (Utils.validateString(project_id) && Utils.validateString(media_id)) {
                                ArrayList<MediaModel> mediaModelArrayList = new ArrayList<>();
                                String created_date = Utils.getCurrentDateAndTime();

                                try {
                                    mDbHelper.openDataBase();
                                    if (mediaStatus.equalsIgnoreCase("A") && mediaSync.equalsIgnoreCase("N")) {
                                        mDbHelper.updateProjectMedia(media_id, project_id, media_type, media_url, videoThumbImg,
                                                edtDescription.getText().toString(), latitude, longitude, street, city, state, country, pincode, created_date, "N", "A", docThumb);
                                    } else {
                                        mDbHelper.updateProjectMedia(media_id, project_id, media_type, media_url, videoThumbImg,
                                                edtDescription.getText().toString(), latitude, longitude, street, city, state, country, pincode, created_date, "N", "U", docThumb);
                                    }
                                    mediaModelArrayList = mDbHelper.getProjectMediaDetails();
                                    mDbHelper.close();
                                } catch (SQLException e) {
                                    e.printStackTrace();
                                }

                                if (mediaModelArrayList.size() > 0) {
                                    projectObj = new JSONObject(mSharedPreferences.getString(Utils.TASK_OBJECT, ""));
                                    JSONArray mediaArray = projectObj.getJSONArray("project_media");
                                    Logger.e(TAG, "Media Array update before : " + mediaArray);
                                    if (mediaArray.length() > 0) {
                                        for (int i = 0; i < mediaArray.length(); i++) {
                                            if (media_id.equalsIgnoreCase(mediaArray.getJSONObject(i).getString("id"))) {
                                                for (int j = 0; j < mediaModelArrayList.size(); j++) {
                                                    if (media_id.equalsIgnoreCase(mediaModelArrayList.get(j).getMediaId())) {
                                                        mediaArray.getJSONObject(i).put("id", mediaModelArrayList.get(j).getMediaId());
                                                        mediaArray.getJSONObject(i).put("media_type", mediaModelArrayList.get(j).getMediaType());
                                                        mediaArray.getJSONObject(i).put("media", mediaModelArrayList.get(j).getMedia());
                                                        mediaArray.getJSONObject(i).put("video_thumb_image", mediaModelArrayList.get(j).getVideoThumbImage());
                                                        mediaArray.getJSONObject(i).put("media_description", mediaModelArrayList.get(j).getMediaDescription());
                                                        mediaArray.getJSONObject(i).put("latitude", mediaModelArrayList.get(j).getLatitude());
                                                        mediaArray.getJSONObject(i).put("longitude", mediaModelArrayList.get(j).getLongitude());
                                                        mediaArray.getJSONObject(i).put("street", mediaModelArrayList.get(j).getStreet());
                                                        mediaArray.getJSONObject(i).put("city", mediaModelArrayList.get(j).getCity());
                                                        mediaArray.getJSONObject(i).put("state", mediaModelArrayList.get(j).getState());
                                                        mediaArray.getJSONObject(i).put("country", mediaModelArrayList.get(j).getCountry());
                                                        mediaArray.getJSONObject(i).put("pincode", mediaModelArrayList.get(j).getPincode());
                                                        mediaArray.getJSONObject(i).put("created_date", mediaModelArrayList.get(j).getCreated_date());
                                                        mediaArray.getJSONObject(i).put("is_sync", mediaModelArrayList.get(j).getIs_sync());
                                                        mediaArray.getJSONObject(i).put("media_operation", mediaModelArrayList.get(j).getMedia_status());
                                                        mediaArray.getJSONObject(i).put("doc_thumb_image", mediaModelArrayList.get(j).getDocThumbImage());
                                                        setMediaData(mediaArray.getJSONObject(i).toString());
                                                        break;
                                                    }
                                                }
                                            }

                                        }

                                        Logger.e(TAG, "Media Array update after : " + mediaArray);
                                        projectObj.put("project_media", mediaArray);
                                        if (projectObj.getString("project_operation").equalsIgnoreCase("A") &&
                                                projectObj.getString("is_sync").equalsIgnoreCase("N")) {
                                            projectObj.put("is_sync", "N");
                                            projectObj.put("project_operation", "A");
                                        } else {
                                            projectObj.put("is_sync", "N");
                                            projectObj.put("project_operation", "U");
                                        }
                                        Logger.e(TAG, "Project Object :" + projectObj);
                                        Utils.storeString(mSharedPreferences, Utils.TASK_OBJECT, projectObj.toString());
                                    }
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        dialog.dismiss();
                    }
                } else {
                    showMessage("Please Enter Media Description.");
                }

                break;
            case R.id.imgClose:
                dialog.dismiss();
                break;

            case R.id.backLayoutclick:
                if (projectObj != null) {
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra(Utils.SWITCH, 2);
                    returnIntent.putExtra(Utils.TASK_OBJECT, projectObj.toString());
                    setResult(Activity.RESULT_OK, returnIntent);
                    finish();
                } else {
                    finish();
                }
                break;
        }

    }

    //TODO: Edit Project Phase.....
    public void UpdateMediaDescription(final String des) {
        pdialog = new ProgressDialog(this);
        pdialog.setMessage(getString(R.string.please_wait));
        pdialog.setCanceledOnTouchOutside(false);
        pdialog.show();

        JSONObject params = new JSONObject();
        try {
            params.put("user_id", USERID);
            params.put("project_id", project_id);
            params.put("media_id", media_id);
            params.put("session_token", TOKEN);
            params.put("media_description", des);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Logger.e(TAG, "Photo Description PARA---->" + String.valueOf(params));
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.POST, Utils.MAIN_URL + Utils.ADD_PHOTO_DESCRIPTION, params, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Logger.e(TAG, "URL---->" + Utils.MAIN_URL + Utils.ADD_PHOTO_DESCRIPTION);
                        Logger.e(TAG, "Photo Description RES---->" + String.valueOf(response));
                        pdialog.dismiss();
                        try {
                            boolean success = response.getBoolean("success");
                            String msg = response.getString("message");

                            if (success) {
                                dialog.dismiss();
                                Utils.projectRefreshFlag = true;
                                Utils.projectFilterRefreshFlag = true;
                                txtDescription.setText(des);
                                projectObj = response.getJSONObject("data");

                                JSONArray additionalContactsArray = projectObj.getJSONArray("additional_contacts");
                                JSONArray additionalContactsNewArray = new JSONArray();
                                String projectResponsiblityName = "", projectResponsiblityId = "";
                                Logger.e(TAG, "Additional Contact Array before :" + additionalContactsArray);
                                if (additionalContactsArray.length() > 0) {
                                    for (int i = 0; i < additionalContactsArray.length(); i++) {
                                        JSONObject contactObject = additionalContactsArray.getJSONObject(i);

                                        if (!contactObject.isNull("project_responsiblity")) {
                                            JSONArray jArray = contactObject.getJSONArray("project_responsiblity");
                                            StringBuilder stringBuilderId = new StringBuilder();
                                            StringBuilder stringBuilderTitle = new StringBuilder();
                                            for (int k = 0; k < jArray.length(); k++) {
                                                JSONObject responsibilityObject = jArray.getJSONObject(k);
                                                stringBuilderId.append(responsibilityObject.getString("id") + ",");
                                                stringBuilderTitle.append(responsibilityObject.getString("title") + ", ");
                                            }

                                            if (Utils.validateString(stringBuilderTitle.toString())) {
                                                projectResponsiblityId = stringBuilderId.substring(0, stringBuilderId.length() - 1);
                                                projectResponsiblityName = stringBuilderTitle.substring(0, stringBuilderTitle.length() - 2);
                                            }
                                        }
                                        contactObject.put("project_responsiblity", projectResponsiblityName);
                                        contactObject.put("project_responsiblity_id", projectResponsiblityId);

                                        additionalContactsNewArray.put(contactObject);
                                    }
                                }

                                Logger.e(TAG, "Additional Contact Array after :" + additionalContactsNewArray);
                                projectObj.put("additional_contacts", additionalContactsNewArray);
                                Logger.e(TAG, "Project Object :" + projectObj);

                            } else {
                                showMessage(msg);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e("ERR", e.getMessage());
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error instanceof NoConnectionError) {
                            pdialog.dismiss();
                        }
                    }
                });
        int socketTimeout = 30000;//30 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsObjRequest.setRetryPolicy(policy);
        requestQueue.add(jsObjRequest);
    }

    public class updateImage extends AsyncTask<Void, Void, Void> {
        boolean exception = false, isSuccess = false;
        ProgressDialog pdialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pdialog = new ProgressDialog(ProjectMediaActivity.this);
            pdialog.setMessage(getString(R.string.please_wait));
            pdialog.setCanceledOnTouchOutside(false);
            pdialog.show();

            created_date = Utils.getCurrentDateAndTime();
            Logger.e(TAG, "created_date :" + created_date);

            Geocoder gcd = new Geocoder(ProjectMediaActivity.this, Locale.getDefault());
            List<Address> addresses = null;

            if (curLatitude != 0 && curLongitude != 0) {
                try {
                    addresses = gcd.getFromLocation(curLatitude, curLongitude, 1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    curLatitude = Double.parseDouble(latitude);
                    curLongitude = Double.parseDouble(longitude);
                    addresses = gcd.getFromLocation(curLatitude, curLongitude, 1);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (NumberFormatException e) {

                }
            }

            try {
                if (addresses.size() > 0) {
                    street = addresses.get(0).getAddressLine(0);
                    city = addresses.get(0).getLocality();
                    state = addresses.get(0).getAdminArea();
                    country = addresses.get(0).getCountryName();
                    pincode = addresses.get(0).getPostalCode();
                }
            } catch (NullPointerException e) {
                e.printStackTrace();
            }

            try {
                if (Utils.validateString(edtDescription.getText().toString().trim())) {
                    description = edtDescription.getText().toString().trim();
                } else {
                    description = "";
                }
            } catch (NullPointerException e) {
                e.printStackTrace();
            }


        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                Upload_Image uploadimage = new Upload_Image(ProjectMediaActivity.this);
                Logger.d(TAG, "Selected Path" + canvasImagePath);
                String uploaddata = uploadimage.updateMedia(USERID, TOKEN, project_id, media_id, canvasImagePath,
                        String.valueOf(curLatitude), String.valueOf(curLongitude), street, city, state, country,
                        pincode, created_date, "", description);

                Logger.e(TAG, "Server response :::" + uploaddata);

                JSONObject jsonObject = new JSONObject(uploaddata);
                boolean success = jsonObject.getBoolean("success");
                String msg = jsonObject.getString("message");
                if (success) {
                    if (!jsonObject.isNull("data")) {
                        Utils.projectRefreshFlag = true;
                        Utils.projectFilterRefreshFlag = true;
                        isSuccess = true;
                        projectObj = jsonObject.getJSONObject("data");

                        JSONArray additionalContactsArray = projectObj.getJSONArray("additional_contacts");
                        JSONArray additionalContactsNewArray = new JSONArray();
                        String projectResponsiblityName = "", projectResponsiblityId = "";
                        Logger.e(TAG, "Additional Contact Array before :" + additionalContactsArray);
                        if (additionalContactsArray.length() > 0) {
                            for (int i = 0; i < additionalContactsArray.length(); i++) {
                                JSONObject contactObject = additionalContactsArray.getJSONObject(i);

                                if (!contactObject.isNull("project_responsiblity")) {
                                    JSONArray jArray = contactObject.getJSONArray("project_responsiblity");
                                    StringBuilder stringBuilderId = new StringBuilder();
                                    StringBuilder stringBuilderTitle = new StringBuilder();
                                    for (int k = 0; k < jArray.length(); k++) {
                                        JSONObject responsibilityObject = jArray.getJSONObject(k);
                                        stringBuilderId.append(responsibilityObject.getString("id") + ",");
                                        stringBuilderTitle.append(responsibilityObject.getString("title") + ", ");
                                    }

                                    if (Utils.validateString(stringBuilderTitle.toString())) {
                                        projectResponsiblityId = stringBuilderId.substring(0, stringBuilderId.length() - 1);
                                        projectResponsiblityName = stringBuilderTitle.substring(0, stringBuilderTitle.length() - 2);
                                    }
                                }
                                contactObject.put("project_responsiblity", projectResponsiblityName);
                                contactObject.put("project_responsiblity_id", projectResponsiblityId);

                                additionalContactsNewArray.put(contactObject);
                            }
                        }

                        Logger.e(TAG, "Additional Contact Array after :" + additionalContactsNewArray);
                        projectObj.put("additional_contacts", additionalContactsNewArray);
                        Logger.e(TAG, "Project Object :" + projectObj);
                    }
                }

            } catch (JSONException e) {
                exception = true;
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            //  Log.d("success--->", "");
            super.onPostExecute(result);

            if (isSuccess) {
                if (projectObj != null) {
                    try {
                        Logger.e(TAG, "MediaObject :::" + projectObj);
                        JSONArray mediaArray = projectObj.getJSONArray("project_media");
                        for (int i = 0; i < mediaArray.length(); i++) {
                            JSONObject mediaObject = mediaArray.getJSONObject(i);
                            if (media_id.equalsIgnoreCase(mediaObject.getString("id"))) {
                                setMediaData(mediaObject.toString());
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            if (pdialog != null && pdialog.isShowing()) {
                pdialog.dismiss();
            }
        }
    }

    public void openMediaDescriptionView() {
        builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.add_photo_description_view, null);
        builder.setView(dialogView);

        addDescBTN = (TextView) dialogView.findViewById(R.id.addDescBTN);
        edtDescription = (EditText) dialogView.findViewById(R.id.edtDescription);
        resetBTN = (TextView) dialogView.findViewById(R.id.resetBTN);
        imgClose = (ImageView) dialogView.findViewById(R.id.imgClose);

        edtDescription.setText(description);
        edtDescription.setSelection(description.length());

        resetBTN.setOnClickListener(this);
        addDescBTN.setOnClickListener(this);
        imgClose.setOnClickListener(this);

        dialog = builder.create();
        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.CENTER;
        window.setAttributes(wlp);
        dialog.show();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        googleMap.addMarker(new MarkerOptions().position(mobileLocation).icon(icon));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(mobileLocation));
        String isTablet = Utils.isTablet(ProjectMediaActivity.this);
        if (!isTablet.equalsIgnoreCase(Utils.TABLET)) {
            googleMap.moveCamera(CameraUpdateFactory.zoomTo(6.0f));
        }

    }

    public static Bitmap getBitmapFromURL(String src) {
        try {
            int height = 150;
            int width = 150;
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            Bitmap smallMarker = Bitmap.createScaledBitmap(myBitmap, width, height, false);
            return smallMarker;
        } catch (IOException e) {
            return null;
        } catch (NullPointerException e) {
            return null;
        }
    }

    public static Bitmap getBitmapFromURI(String path) {
        try {
            int height = 150;
            int width = 150;
            File file = new File(path);
            Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
            Bitmap smallMarker = Bitmap.createScaledBitmap(bitmap, width, height, false);
            return smallMarker;
        } catch (NullPointerException e) {
            return null;
        }
    }

    public void setMediaData(String mediaObject) {
        try {
            JSONObject jObj = new JSONObject(mediaObject.toString());
            media_id = jObj.getString("id");
            media_type = jObj.getString("media_type");
//            if (media_type.equalsIgnoreCase("image")) {
//                imageFlag = true;
//            } else {
//                imageFlag = false;
//            }
            media_url = jObj.getString("media").trim();
            videoThumbImg = jObj.getString("video_thumb_image").trim();
            description = jObj.getString("media_description");
            latitude = jObj.getString("latitude");
            longitude = jObj.getString("longitude");
            street = jObj.getString("street");
            city = jObj.getString("city");
            state = jObj.getString("state");
            country = jObj.getString("country");
            pincode = jObj.getString("pincode");
            created_date = jObj.getString("created_date");
            setDateTime = jObj.getString("created_date");
            mediaSync = jObj.getString("is_sync");
            mediaStatus = jObj.getString("media_operation");
            docThumb = jObj.getString("doc_thumb_image");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (Utils.validateString(description))
            txtDescription.setText(description);

        if (Utils.validateString(street)) {
            street = street + ", ";
        }
        if (Utils.validateString(city)) {
            city = city + ", ";
        }
        if (Utils.validateString(state)) {
            state = state + ", ";
        }
        if (Utils.validateString(country)) {
            country = country + ", ";
        }
        if (Utils.validateString(pincode)) {
            pincode = pincode;
        }
        txtAddress.setText(street + city + state + country + pincode);

        if (Utils.validateString(setDateTime)) {
            String date = Utils.parseDateFromyyyyMMdd(setDateTime);
            String[] separated = date.split(",");
            txtDate.setText(separated[0].trim());
            txtTime.setText(separated[1].trim());
        }

        File file;
        if (media_type.equalsIgnoreCase("image")) {
            file = new File(Environment.getExternalStorageDirectory() + "/Media/Image/", media_id + ".jpg");
            if (file.exists()) {
                imgOffline.setVisibility(View.VISIBLE);
                imgOffline.setImageResource(0);
                NetworkImgCamera.setVisibility(View.GONE);
                editImageLayout.setVisibility(View.VISIBLE);
                imgAudioline.setVisibility(View.GONE);
                linAudioline.setVisibility(View.GONE);
                imgVideo.setVisibility(View.GONE);
                frmLayout.setOnClickListener(null);

                canvas_media_url = Environment.getExternalStorageDirectory() + "/Media/Image/" + media_id + ".jpg";
                imgOffline.setImageURI(Uri.parse(canvas_media_url));

                imageBitmap = getBitmapFromURI(canvas_media_url);
                if (imageBitmap != null) {
                    icon = BitmapDescriptorFactory.fromBitmap(imageBitmap);
                }
            } else {
                imgOffline.setVisibility(View.GONE);
                NetworkImgCamera.setVisibility(View.VISIBLE);
                editImageLayout.setVisibility(View.VISIBLE);
                imgAudioline.setVisibility(View.GONE);
                linAudioline.setVisibility(View.GONE);
                imgVideo.setVisibility(View.GONE);
                frmLayout.setOnClickListener(null);
                imageLoader = CustomVolleyRequest.getInstance(ProjectMediaActivity.this).getImageLoader();
                if (!media_url.isEmpty()) {
                    imageLoader.get(media_url, ImageLoader.getImageListener(NetworkImgCamera, R.mipmap.default_image, R.mipmap.default_image));
                    NetworkImgCamera.setImageUrl(media_url, imageLoader);

                    imageBitmap = getBitmapFromURL(media_url);
                    if (imageBitmap != null) {
                        icon = BitmapDescriptorFactory.fromBitmap(imageBitmap);
                    }
                }

            }
        } else if (media_type.equalsIgnoreCase("video")) {
            file = new File(Environment.getExternalStorageDirectory() + "/Media/VideoThumb/", media_id + ".jpg");
            if (file.exists()) {
                imgOffline.setVisibility(View.VISIBLE);
                imgOffline.setImageResource(0);
                NetworkImgCamera.setVisibility(View.GONE);
                imgAudioline.setVisibility(View.GONE);
                linAudioline.setVisibility(View.GONE);
                editImageLayout.setVisibility(View.GONE);
                imgVideo.setVisibility(View.VISIBLE);
                frmLayout.setOnClickListener(this);

                String VIDEO_URL = Environment.getExternalStorageDirectory() + "/Media/VideoThumb/" + media_id + ".jpg";
                imgOffline.setImageURI(Uri.parse(VIDEO_URL));

                imageBitmap = getBitmapFromURI(VIDEO_URL);
                if (imageBitmap != null) {
                    icon = BitmapDescriptorFactory.fromBitmap(imageBitmap);
                }

            } else {
                imgOffline.setVisibility(View.GONE);
                NetworkImgCamera.setVisibility(View.VISIBLE);
                editImageLayout.setVisibility(View.GONE);
                imgAudioline.setVisibility(View.GONE);
                linAudioline.setVisibility(View.GONE);
                imgVideo.setVisibility(View.VISIBLE);
                frmLayout.setOnClickListener(this);
                imageLoader = CustomVolleyRequest.getInstance(ProjectMediaActivity.this).getImageLoader();
                if (!videoThumbImg.isEmpty()) {
                    imageLoader.get(videoThumbImg, ImageLoader.getImageListener(NetworkImgCamera, R.mipmap.default_image, R.mipmap.default_image));
                    NetworkImgCamera.setImageUrl(videoThumbImg, imageLoader);

                    imageBitmap = getBitmapFromURL(videoThumbImg);
                    if (imageBitmap != null) {
                        icon = BitmapDescriptorFactory.fromBitmap(imageBitmap);
                    }
                }

            }
        } else if (media_type.equalsIgnoreCase("audio")){
            file = new File(Environment.getExternalStorageDirectory() + "/Media/Audio/", media_id + ".wav");
            if (file.exists()) {
                imgOffline.setVisibility(View.GONE);
                imgOffline.setImageResource(0);
                imgAudioline.setVisibility(View.VISIBLE);
                linAudioline.setVisibility(View.VISIBLE);
                NetworkImgCamera.setVisibility(View.GONE);
                editImageLayout.setVisibility(View.GONE);
                imgVideo.setVisibility(View.VISIBLE);
                frmLayout.setOnClickListener(this);

                String VIDEO_URL = Environment.getExternalStorageDirectory() + "/Media/Audio/" + media_id + ".wav";
                imgAudioline.setImageURI(Uri.parse(VIDEO_URL));

                Bitmap bm = BitmapFactory.decodeResource(getResources(), R.mipmap.audiotrack);
                Bitmap smallMarker = Bitmap.createScaledBitmap(bm, 150, 150, false);
                imageBitmap = smallMarker;
                if (imageBitmap != null) {
                    icon = BitmapDescriptorFactory.fromBitmap(imageBitmap);
                }

            } else {
                imgOffline.setVisibility(View.GONE);
                NetworkImgCamera.setVisibility(View.GONE);
                imgAudioline.setVisibility(View.VISIBLE);
                linAudioline.setVisibility(View.VISIBLE);
                editImageLayout.setVisibility(View.GONE);
                imgVideo.setVisibility(View.VISIBLE);
                frmLayout.setOnClickListener(this);

                Bitmap bm = BitmapFactory.decodeResource(getResources(), R.mipmap.audiotrack);
                Bitmap smallMarker = Bitmap.createScaledBitmap(bm, 150, 150, false);
                imageBitmap = smallMarker;
                if (imageBitmap != null) {
                    icon = BitmapDescriptorFactory.fromBitmap(imageBitmap);
                }

            }
        }


        mMapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mMapFragment.getMapAsync(this);

        if (Utils.validateString(latitude) && Utils.validateString(longitude)) {
            mobileLocation = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
        } else {
            double lat = 0, lang = 0;
            GPSTracker gpsTracker = Utils.getCurrentLocation(this);
            if (gpsTracker != null) {
                lat = gpsTracker.getLatitude();
                lang = gpsTracker.getLongitude();
            }
            mobileLocation = new LatLng(lat, lang);
        }

        if (project_Status.equalsIgnoreCase(Utils.REPORT)) {
            editImageLayout.setVisibility(View.GONE);
            imgMediaDes.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK) {
            String result = data.getStringExtra(Utils.MEDIA_EDITED_IMAGE_PATH);
            switch (data.getIntExtra(Utils.SWITCH, 101)) {
                case 0:
                    canvasImagePath = result;
                    if (Utils.validateString(canvasImagePath)) {
                        if (Utils.checkInternetConnection(this)) {
                            new updateImage().execute();
                        } else {
                            Logger.e(TAG, "canvasImagePath : " + canvasImagePath);
                            try {
                                if (!project_id.equals("") && !media_id.equals("")) {
                                    ArrayList<MediaModel> mediaModelArrayList = new ArrayList<>();
                                    String created_date = Utils.getCurrentDateAndTime();

                                    try {
                                        mDbHelper.openDataBase();
                                        if (mediaStatus.equalsIgnoreCase("A") && mediaSync.equalsIgnoreCase("N")) {
                                            mDbHelper.updateProjectMedia(media_id, project_id, media_type, media_url, videoThumbImg,
                                                    description, latitude, longitude, street, city, state, country, pincode, created_date, "N", "A", docThumb);
                                        } else {
                                            mDbHelper.updateProjectMedia(media_id, project_id, media_type, media_url, videoThumbImg,
                                                    description, latitude, longitude, street, city, state, country, pincode, created_date, "N", "U", docThumb);
                                        }
                                        mediaModelArrayList = mDbHelper.getProjectMediaDetails();
                                        mDbHelper.close();
                                    } catch (SQLException e) {
                                        e.printStackTrace();
                                    }

                                    if (mediaModelArrayList.size() > 0) {
                                        projectObj = new JSONObject(mSharedPreferences.getString(Utils.TASK_OBJECT, ""));
                                        JSONArray mediaArray = projectObj.getJSONArray("project_media");
                                        Logger.e(TAG, "Media Array update before : " + mediaArray);
                                        if (mediaArray.length() > 0) {
                                            for (int i = 0; i < mediaArray.length(); i++) {
                                                if (media_id.equalsIgnoreCase(mediaArray.getJSONObject(i).getString("id"))) {
                                                    setMediaData(mediaArray.getJSONObject(i).toString());
                                                    for (int j = 0; j < mediaModelArrayList.size(); j++) {
                                                        if (media_id.equalsIgnoreCase(mediaModelArrayList.get(j).getMediaId())) {
                                                            mediaArray.getJSONObject(i).put("id", mediaModelArrayList.get(j).getMediaId());
                                                            mediaArray.getJSONObject(i).put("media_type", mediaModelArrayList.get(j).getMediaType());
                                                            mediaArray.getJSONObject(i).put("media", mediaModelArrayList.get(j).getMedia());
                                                            mediaArray.getJSONObject(i).put("video_thumb_image", mediaModelArrayList.get(j).getVideoThumbImage());
                                                            mediaArray.getJSONObject(i).put("media_description", mediaModelArrayList.get(j).getMediaDescription());
                                                            mediaArray.getJSONObject(i).put("latitude", mediaModelArrayList.get(j).getLatitude());
                                                            mediaArray.getJSONObject(i).put("longitude", mediaModelArrayList.get(j).getLongitude());
                                                            mediaArray.getJSONObject(i).put("street", mediaModelArrayList.get(j).getStreet());
                                                            mediaArray.getJSONObject(i).put("city", mediaModelArrayList.get(j).getCity());
                                                            mediaArray.getJSONObject(i).put("state", mediaModelArrayList.get(j).getState());
                                                            mediaArray.getJSONObject(i).put("country", mediaModelArrayList.get(j).getCountry());
                                                            mediaArray.getJSONObject(i).put("pincode", mediaModelArrayList.get(j).getPincode());
                                                            mediaArray.getJSONObject(i).put("created_date", mediaModelArrayList.get(j).getCreated_date());
                                                            mediaArray.getJSONObject(i).put("is_sync", mediaModelArrayList.get(j).getIs_sync());
                                                            mediaArray.getJSONObject(i).put("media_operation", mediaModelArrayList.get(j).getMedia_status());
                                                            mediaArray.getJSONObject(i).put("doc_thumb_image", mediaModelArrayList.get(j).getDocThumbImage());
                                                            break;
                                                        }
                                                    }
                                                }

                                            }

                                            Logger.e(TAG, "Media Array update after : " + mediaArray);
                                            projectObj.put("project_media", mediaArray);
                                            if (projectObj.getString("project_operation").equalsIgnoreCase("A") &&
                                                    projectObj.getString("is_sync").equalsIgnoreCase("N")) {
                                                projectObj.put("is_sync", "N");
                                                projectObj.put("project_operation", "A");
                                            } else {
                                                projectObj.put("is_sync", "N");
                                                projectObj.put("project_operation", "U");
                                            }
                                            Logger.e(TAG, "Project Object :" + projectObj);
                                            Utils.storeString(mSharedPreferences, Utils.TASK_OBJECT, projectObj.toString());
                                        }
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    break;

                default:
                    break;
            }

        }
    }

    @Override
    public void onBackPressed() {
        if (projectObj != null) {
            Intent returnIntent = new Intent();
            returnIntent.putExtra(Utils.SWITCH, 2);
            returnIntent.putExtra(Utils.TASK_OBJECT, projectObj.toString());
            setResult(Activity.RESULT_OK, returnIntent);
            finish();
        } else {
            finish();
        }
        super.onBackPressed();
    }

    private void enableLoc() {

        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();

            googleApiClient.connect();

            locationRequest = LocationRequest.create();
            locationRequest.setInterval(10000);
            locationRequest.setFastestInterval(5000);
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);

            builder.setAlwaysShow(true);

            PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
            result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
                @Override
                public void onResult(LocationSettingsResult result) {
                    final Status status = result.getStatus();
                    final LocationSettingsStates state = result.getLocationSettingsStates();
                    switch (status.getStatusCode()) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            try {
                                status.startResolutionForResult(ProjectMediaActivity.this, REQUEST_LOCATION);
                            } catch (IntentSender.SendIntentException e) {
                                // Ignore the error.
                            }
                            break;

                        case LocationSettingsStatusCodes.SUCCESS:

                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            // Location settings are not satisfied. However, we have no way to fix the
                            // settings so we won't show the dialog.
                            break;
                    }
                }
            });
        }

    }

    private void startLocationUpdates() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            return;
        }
        if (googleApiClient != null) {
            if (googleApiClient.isConnected()) {
                LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
            }
        }

    }

    @Override
    protected void onStart() {
        if (googleApiClient != null) {
            googleApiClient.connect();
        } else {
            // First we need to check availability of play services
            if (checkPlayServices()) {
                enableLoc();
            }
        }
        super.onStart();
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        if (googleApiClient != null) {
            if (googleApiClient.isConnected()) {
                startLocationUpdates();
            }
        }
    }

    @Override
    protected void onStop() {
        if (googleApiClient != null) {
            googleApiClient.disconnect();
        }
        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    protected void stopLocationUpdates() {
        if (googleApiClient != null && googleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
        }
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            return;
        }
        if (googleApiClient != null) {
            if (googleApiClient.isConnected()) {
                LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        if (googleApiClient != null) {
            googleApiClient.connect();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean statusOfGPS = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (statusOfGPS) {
            if (location != null) {
                curLatitude = location.getLatitude();
                curLongitude = location.getLongitude();
            }
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this, PLAY_SERVICES_RESOLUTION_REQUEST).show();
            }
            return false;
        }
        return true;
    }

    private void showMessage(String message) {
        Utils.showResponseMessage(this, rootLayout, message);
    }

}
