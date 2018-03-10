package com.rogi.Activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.system.ErrnoException;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.rogi.Crop.CropImage;
import com.rogi.Crop.CropImageView;
import com.rogi.R;
import com.rogi.View.Upload_Image;
import com.rogi.View.Utils;
import com.rogi.logger.Logger;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String TAG = "SignUpActivity";

    private Uri mCropImageUri;
    private SharedPreferences mSharedPreferences;

    TextView titleText, signInText, signUpBTN;
    LinearLayout linMainRegister, linImagBtn;
    EditText edtFirstName, edtLastName, edtEmailAddress, edtPhoneNumber, edtPasswordRegister, edtConfirmPasswordRegister, edtCompanyName;
    ImageView circularImageView, imgShowPass, imgShowCPass;
    RequestQueue requestQueue;
    String VersionName, DeviceId, selectedImagePath = "", firstName, lastName, email, password, phoneNo, CompanyName;
    boolean showPass = true, showCPass = true, flagClick = true;
    private final int REQUEST_CODE_ASK_PERMISSIONS = 123;
    private BroadcastReceiver mRegistrationBroadcastReceiver;

    String advancePrice, planId, planName, planType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        mSharedPreferences = getSharedPreferences(Utils.PREF_NAME, MODE_PRIVATE);
        requestQueue = Volley.newRequestQueue(this);
        try {
            PackageInfo pInfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);
            VersionName = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        Intent intent = getIntent();
        advancePrice = intent.getStringExtra(Utils.PLAN_PRICE);
        planId = intent.getStringExtra(Utils.PLAN_ID);
        planName = intent.getStringExtra(Utils.PLAN_NAME);

        planType = mSharedPreferences.getString(Utils.PLAN_TYPE, "");
        DeviceId = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        init();
    }

    public void init() {

        linMainRegister = (LinearLayout) findViewById(R.id.linMainRegister);

        titleText = (TextView) findViewById(R.id.titleText);
        titleText.setText("Sign Up");

        circularImageView = (ImageView) findViewById(R.id.circularImageView);
        linImagBtn = (LinearLayout) findViewById(R.id.linImagBtn);

        edtFirstName = (EditText) findViewById(R.id.edtFirstName);
        edtLastName = (EditText) findViewById(R.id.edtLastName);
        edtCompanyName = (EditText) findViewById(R.id.edtCompanyName);
//        if (Utils.validateString(planId)) {
//            if (planId.equalsIgnoreCase("1") || planId.equalsIgnoreCase("2")) {
//                findViewById(R.id.txtInputCompanyName).setVisibility(View.GONE);
//            } else {
        findViewById(R.id.txtInputCompanyName).setVisibility(View.VISIBLE);
//            }
//        }
        edtEmailAddress = (EditText) findViewById(R.id.edtEmailAddress);
        edtPhoneNumber = (EditText) findViewById(R.id.edtPhoneNumber);
        edtPasswordRegister = (EditText) findViewById(R.id.edtPasswordRegister);
        edtConfirmPasswordRegister = (EditText) findViewById(R.id.edtConfirmPasswordRegister);

        imgShowPass = (ImageView) findViewById(R.id.imgShowPass);
        imgShowCPass = (ImageView) findViewById(R.id.imgShowCPass);

        signInText = (TextView) findViewById(R.id.signInText);
        signUpBTN = (TextView) findViewById(R.id.signUpBTN);

        linImagBtn.setOnClickListener(this);
        signInText.setOnClickListener(this);
        signUpBTN.setOnClickListener(this);
        imgShowPass.setOnClickListener(this);
        imgShowCPass.setOnClickListener(this);
        findViewById(R.id.backLayoutclick).setOnClickListener(this);

        edtFirstName.setTypeface(Utils.getTypeFace(SignUpActivity.this));
        edtLastName.setTypeface(Utils.getTypeFace(SignUpActivity.this));
        edtEmailAddress.setTypeface(Utils.getTypeFace(SignUpActivity.this));
        edtPhoneNumber.setTypeface(Utils.getTypeFace(SignUpActivity.this));
        edtPasswordRegister.setTypeface(Utils.getTypeFace(SignUpActivity.this));
        edtConfirmPasswordRegister.setTypeface(Utils.getTypeFace(SignUpActivity.this));

        InputFilter filter = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                if (source.length() > 0) {
                    if (!Character.isDigit(source.charAt(0)))
                        return "";
                    else {
                        if (dstart == 3) {
                            return source + ") ";
                        } else if (dstart == 0) {
                            return "(" + source;
                        } else if ((dstart == 5) || (dstart == 9))
                            return "-" + source;
                        else if (dstart >= 14)
                            return "";
                    }
                } else {
                }
                return null;
            }
        };

        edtPhoneNumber.setFilters(new InputFilter[]{filter});

        boolean isNetAvailable = Utils.checkInternetConnection(this);
        if (!isNetAvailable) {
            Utils.showMessageDialog(this, getResources().getString(R.string.alert), getResources().getString(R.string.connection));
        } else {
            mRegistrationBroadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    try {
                        if (intent.getAction().equals(Utils.REGISTRATION_COMPLETE)) {
                            // gcm successfully registered
                            // now subscribe to `global` topic to receive app wide notifications
                            FirebaseMessaging.getInstance().subscribeToTopic(Utils.TOPIC_GLOBAL);

                            String GCMRegistrationId = mSharedPreferences.getString(Utils.REG_ID, "");
                            Logger.e(TAG, "GCMRegistrationId : " + GCMRegistrationId);

                        } else if (intent.getAction().equals(Utils.PUSH_NOTIFICATION)) {

                        }
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                }
            };
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Utils.REGISTRATION_COMPLETE));
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Utils.PUSH_NOTIFICATION));
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.linImagBtn:
                int currentapiVersion = Build.VERSION.SDK_INT;
                if (currentapiVersion > Build.VERSION_CODES.LOLLIPOP) {
                    // Do something for lollipop and above versions
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
                    }
                }
                takePicture_Click();
                //CropImage.startPickImageActivity(this);
                break;

            case R.id.signUpBTN:
                Utils.hideSoftKeyboard(this);
                if (fieldValidation()) {
                    if (flagClick) {
                        flagClick = false;
                        firstName = edtFirstName.getText().toString().trim();
                        lastName = edtLastName.getText().toString().trim();
                        email = edtEmailAddress.getText().toString().trim();
                        password = edtPasswordRegister.getText().toString().trim();
                        CompanyName = edtCompanyName.getText().toString().trim();
                        String str = edtPhoneNumber.getText().toString().trim().replaceAll("\\D+", "");
                        phoneNo = str;

                        new userRegistration().execute();
                    }
                }
                break;

            case R.id.signInText:
                Intent signInIntent = new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(signInIntent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
                finish();
                break;

            case R.id.backLayoutclick:
                /*Intent signupIntent = new Intent(SignUpActivity.this, PlanActivity.class);
                startActivity(signupIntent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);*/
                finish();
                break;

            case R.id.imgShowPass:
                if (!edtPasswordRegister.getText().toString().equals("")) {
                    if (showPass) {
                        edtPasswordRegister.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                        edtPasswordRegister.setSelection(edtPasswordRegister.getText().toString().trim().length());
                        showPass = false;
                    } else {
                        edtPasswordRegister.setTransformationMethod(PasswordTransformationMethod.getInstance());
                        edtPasswordRegister.setSelection(edtPasswordRegister.getText().toString().trim().length());
                        showPass = true;
                    }
                }
                break;

            case R.id.imgShowCPass:
                if (!edtConfirmPasswordRegister.getText().toString().equals("")) {
                    if (showCPass) {
                        edtConfirmPasswordRegister.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                        edtConfirmPasswordRegister.setSelection(edtConfirmPasswordRegister.getText().toString().trim().length());
                        showCPass = false;
                    } else {
                        edtConfirmPasswordRegister.setTransformationMethod(PasswordTransformationMethod.getInstance());
                        edtConfirmPasswordRegister.setSelection(edtConfirmPasswordRegister.getText().toString().trim().length());
                        showCPass = true;
                    }
                }
                break;
        }
    }

    ProgressDialog pdialog;

    public class userRegistration extends AsyncTask<Void, Void, Void> {
        boolean exception = false, isSuccess = false;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pdialog = new ProgressDialog(SignUpActivity.this);
            pdialog.setMessage(getString(R.string.please_wait));
            pdialog.setCanceledOnTouchOutside(false);
            pdialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                Upload_Image uploadimage = new Upload_Image(SignUpActivity.this);
                Logger.d(TAG, "Selected Path" + selectedImagePath);
                String uploaddata = uploadimage.uploadRegistration(selectedImagePath, firstName, lastName, email, phoneNo, password,
                        /*DeviceId*/ mSharedPreferences.getString(Utils.REG_ID, ""), VersionName, CompanyName);

                JSONObject jsonObject = new JSONObject(uploaddata);
                Logger.e(TAG, "Server response :::" + uploaddata);
                boolean success = jsonObject.getBoolean("success");
                String msg = jsonObject.getString("message");

                if (success) {
                    flagClick = true;
                    if (!jsonObject.isNull("data")) {
                        isSuccess = true;
                        JSONObject jObj = jsonObject.getJSONObject("data");

                        String userId = jObj.getString("id");
                        String first_name = jObj.getString("first_name");
                        String last_name = jObj.getString("last_name");
                        String email = jObj.getString("email");
                        String phone_number = jObj.getString("phone_number");
                        String session_token = jObj.getString("session_token");
                        String profile_image = jObj.getString("profile_image");
                        String company_name = jObj.getString("company_name");
                        String company_image = jObj.getString("company_image");
                        String role = jObj.getString("role");
                        String authorize = jObj.getString("authorize_text");

                        Utils.storeString(mSharedPreferences, Utils.USER_ID, userId);
                        Utils.storeString(mSharedPreferences, Utils.FIRST_NAME, first_name);
                        Utils.storeString(mSharedPreferences, Utils.LAST_NAME, last_name);
                        Utils.storeString(mSharedPreferences, Utils.PHONE_NUMBER, phone_number);
                        Utils.storeString(mSharedPreferences, Utils.EMAIL, email);
                        Utils.storeString(mSharedPreferences, Utils.PASSWORD, password);
                        Utils.storeString(mSharedPreferences, Utils.PROFILE_IMAGE, profile_image);
                        Utils.storeString(mSharedPreferences, Utils.TOKEN, session_token);
                        Utils.storeString(mSharedPreferences, Utils.COMPANY_NAME, company_name);
                        Utils.storeString(mSharedPreferences, Utils.COMPANY_IMAGE, company_image);
                        Utils.storeString(mSharedPreferences, Utils.ROLE, role);
                        Utils.storeString(mSharedPreferences, Utils.AUTHORIZE_TEXT, authorize);

                    } else {
                        pdialog.dismiss();
                        showMessage(msg);
                    }
                } else {
                    flagClick = true;
                    pdialog.dismiss();
                    showMessage(msg);
                }

            } catch (JSONException e) {
                flagClick = true;
                exception = true;
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            //  Log.d("success--->", "");
            super.onPostExecute(result);
            pdialog.dismiss();

            if (isSuccess) {

                if (planType.equals("free")) {
                    addPlanRequest(planId);
                } else {
                    Intent planIntent = new Intent(SignUpActivity.this, PaymentActivity.class);
                    planIntent.putExtra(Utils.PLAN_PRICE, advancePrice);
                    planIntent.putExtra(Utils.PLAN_ID, planId);
                    planIntent.putExtra(Utils.PLAN_NAME, planName);
                    startActivity(planIntent);
                    finish();
                }
                /*
                Intent planIntent = new Intent(SignUpActivity.this, PlanActivity.class);
                startActivity(planIntent);*/
                /*Intent signinIntent = new Intent(SignUpActivity.this, NavigationDrawerActivity.class);
                startActivity(signinIntent);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
                finish();*/
            }
        }
    }

    public void addPlanRequest(String planId) {
        JSONObject params = new JSONObject();

        String USERID = mSharedPreferences.getString(Utils.USER_ID, "");
        String TOKEN = mSharedPreferences.getString(Utils.TOKEN, "");
        try {
            params.put("user_id", USERID);
            params.put("session_token", TOKEN);
            params.put("subscription_id", "");
            params.put("plan_id", planId);
            params.put("auto_renewal", "N");

        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.e("Add Plan PARA---->", String.valueOf(params));
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.POST, Utils.MAIN_URL + Utils.PLAN_ADD_API, params, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Logger.e("FreePlan ::", "Add Plan Res ::" + String.valueOf(response));
                        try {
                            boolean success = response.getBoolean("success");
                            String msg = response.getString("message");
                            if (success) {
                                if (!response.isNull("data")) {
                                    JSONObject jObj = response.getJSONObject("data");
                                    String plainId = jObj.getString("id");
                                    String autoRenewal = jObj.getString("auto_renewal");
                                    Utils.storeString(mSharedPreferences, Utils.PLAN_ID, plainId);
                                    Utils.storeString(mSharedPreferences, Utils.AUTO_RENEWAL, autoRenewal);
                                    Intent intent = new Intent(SignUpActivity.this, NavigationDrawerActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
                                }

                            } /*else {
                                Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
                            }*/

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e("ERR", e.getMessage());
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error instanceof NoConnectionError) {
                        }
                    }
                });
        int socketTimeout = 30000;//30 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsObjRequest.setRetryPolicy(policy);
        requestQueue.add(jsObjRequest);
    }


    @Override
    @SuppressLint("NewApi")
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // handle result of pick image chooser
        if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Uri imageUri = CropImage.getPickImageResultUri(this, data);

            // For API >= 23 we need to check specifically that we have permissions to read external storage.
            boolean requirePermissions = false;
            if (CropImage.isReadExternalStoragePermissionsRequired(this, imageUri)) {
                // request permissions and handle the result in onRequestPermissionsResult()
                requirePermissions = true;
                mCropImageUri = imageUri;
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
            } else {
                // no permissions required or already grunted, can start crop image activity
                startCropImageActivity(imageUri);
            }
        }

        // handle result of CropImageActivity
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                circularImageView.setImageURI(result.getUri());
                selectedImagePath = result.getUri().toString();
                selectedImagePath = selectedImagePath.replace("file:///", "");
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Toast.makeText(this, "Cropping failed: " + result.getError(), Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (mCropImageUri != null && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // required permissions granted, start crop image activity
            startCropImageActivity(mCropImageUri);
        } /*else {
            Toast.makeText(this, "Cancelling, required permissions are not granted", Toast.LENGTH_LONG).show();
        }*/
    }

    /**
     * Start crop image activity for the given image.
     */
    private void startCropImageActivity(Uri imageUri) {
        CropImage.activity(imageUri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(this);
    }

    public void takePicture_Click() {
        final CharSequence[] options = {"Camera", "Gallery", "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Option");
        builder.setItems(options, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Camera")) {
                    startActivityForResult(getPickImageChooserIntent(), 200);

                    /*Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, LOAD_IMAGE_CAMERA);*/

                } else if (options[item].equals("Gallery")) {
                    startActivityForResult(getPickImageChooserGalleryIntent(), 200);

                    /*Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(Intent.createChooser(intent, "Select Picture"), LOAD_IMAGE_GALLARY);*/

                } else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    public Intent getPickImageChooserIntent() {

        Uri outputFileUri = getCaptureImageOutputUri();
        List<Intent> allIntents = new ArrayList<>();
        PackageManager packageManager = getPackageManager();
        // collect all camera intents
        Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
        for (ResolveInfo res : listCam) {
            Intent intent = new Intent(captureIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(res.activityInfo.packageName);
            if (outputFileUri != null) {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            }
            allIntents.add(intent);
        }

        // the main intent is the last in the  list so pickup the useless one
        Intent mainIntent = allIntents.get(allIntents.size() - 1);
        for (Intent intent : allIntents) {
            if (intent.getComponent().getClassName().equals("com.android.documentsui.DocumentsActivity")) {
                mainIntent = intent;
                break;
            }
        }
        allIntents.remove(mainIntent);
        Intent chooserIntent = Intent.createChooser(mainIntent, "Select source");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, allIntents.toArray(new Parcelable[allIntents.size()]));

        return chooserIntent;
    }

    public Intent getPickImageChooserGalleryIntent() {

        // Uri outputFileUri = getCaptureImageOutputUri();
        List<Intent> allIntents = new ArrayList<>();
        PackageManager packageManager = getPackageManager();

        // collect all gallery intents
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        List<ResolveInfo> listGallery = packageManager.queryIntentActivities(galleryIntent, 0);
        for (ResolveInfo res : listGallery) {
            Intent intent = new Intent(galleryIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(res.activityInfo.packageName);
            allIntents.add(intent);
        }

        // the main intent is the last in the  list so pickup the useless one
        Intent mainIntent = allIntents.get(allIntents.size() - 1);
        for (Intent intent : allIntents) {
            if (intent.getComponent().getClassName().equals("com.android.documentsui.DocumentsActivity")) {
                mainIntent = intent;
                break;
            }
        }
        allIntents.remove(mainIntent);
        Intent chooserIntent = Intent.createChooser(mainIntent, "Select source");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, allIntents.toArray(new Parcelable[allIntents.size()]));

        return chooserIntent;
    }

    private Uri getCaptureImageOutputUri() {
        Uri outputFileUri = null;
        File getImage = getExternalCacheDir();
        if (getImage != null) {
            outputFileUri = Uri.fromFile(new File(getImage.getPath(), "pickImageResult.jpeg"));
        }
        return outputFileUri;
    }

    public Uri getPickImageResultUri(Intent data) {
        boolean isCamera = true;
        if (data != null && data.getData() != null) {
            String action = data.getAction();
            isCamera = action != null && action.equals(MediaStore.ACTION_IMAGE_CAPTURE);
        }
        return isCamera ? getCaptureImageOutputUri() : data.getData();
    }

    /**
     * Test if we can open the given Android URI to test if permission required error is thrown.<br>
     */
    public boolean isUriRequiresPermissions(Uri uri) {
        try {
            ContentResolver resolver = getContentResolver();
            InputStream stream = resolver.openInputStream(uri);
            stream.close();
            return false;
        } catch (FileNotFoundException e) {
            if (e.getCause() instanceof ErrnoException) {
                return true;
            }
        } catch (Exception e) {
        }
        return false;
    }

    public boolean fieldValidation() {
        boolean flag = true;
        if (!Utils.validateString(edtCompanyName.getText().toString())) {
            flag = false;
            edtCompanyName.requestFocus();
            showMessage(getString(R.string.enter_company_name));
        } else if (!Utils.validateString(edtFirstName.getText().toString())) {
            flag = false;
            edtFirstName.requestFocus();
            showMessage(getString(R.string.enter_firstname));
        } else if (!Utils.validateString(edtLastName.getText().toString())) {
            flag = false;
            edtLastName.requestFocus();
            showMessage(getString(R.string.enter_lastname));
        } else if (!Utils.validateString(edtEmailAddress.getText().toString())) {
            flag = false;
            edtEmailAddress.requestFocus();
            showMessage(getString(R.string.enter_emailaddress));
        } else if (edtEmailAddress.getText().toString().length() > 0 && !Utils.isEmailValid(edtEmailAddress.getText().toString())) {
            flag = false;
            showMessage(getString(R.string.invalid_email));
            edtEmailAddress.requestFocus();
        } else if (!Utils.validateString(edtPhoneNumber.getText().toString())) {
            flag = false;
            edtPhoneNumber.requestFocus();
            showMessage(getString(R.string.enter_phonenumber));
        } else if (edtPhoneNumber.getText().toString().trim().length() < 14) {
            flag = false;
            edtPhoneNumber.requestFocus();
            showMessage(getString(R.string.enter_phonenumber_length));
        } else if (!Utils.validateString(edtPasswordRegister.getText().toString())) {
            flag = false;
            edtPasswordRegister.requestFocus();
            showMessage(getString(R.string.enter_password));
        } else if (edtPasswordRegister.getText().toString().trim().length() < 8) {
            flag = false;
            edtPasswordRegister.requestFocus();
            showMessage(getString(R.string.check_password_length));
        } else if (!Utils.validateString(edtConfirmPasswordRegister.getText().toString())) {
            flag = false;
            edtConfirmPasswordRegister.requestFocus();
            showMessage(getString(R.string.enter_confirampassword));
        } else if (!edtPasswordRegister.getText().toString().equalsIgnoreCase(edtConfirmPasswordRegister.getText().toString())) {
            flag = false;
            edtConfirmPasswordRegister.requestFocus();
            showMessage(getString(R.string.password_not_match));
        } /*else if (!Utils.validateString(selectedImagePath)) {
            flag = false;
            showMessage(getString(R.string.enter_image));
        }*/
        return flag;
    }

    private void showMessage(String message) {
        Utils.showResponseMessage(this, linMainRegister, message);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        /*Intent signInIntent = new Intent(SignUpActivity.this, PlanActivity.class);
        startActivity(signInIntent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);*/
        finish();
    }
}
