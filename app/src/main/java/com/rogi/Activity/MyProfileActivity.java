package com.rogi.Activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.rogi.Database.DBConstants;
import com.rogi.R;
import com.rogi.View.TextViewPlus;
import com.rogi.View.Upload_Image;
import com.rogi.View.Utils;
import com.rogi.logger.Logger;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MyProfileActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String TAG = "MyProfileActivity";

    private SharedPreferences mSharedPreferences;
    private Uri mCropImageUri;

    LinearLayout linMainMyProfile, linImagBtn, linPlan;
    ImageView editProfileView, saveProfileView, circularImageView, imgShowPass, imgShowCPass;
    EditText edtFirstName, edtLastName, edtCompanyName, edtStreet, edtCity, edtState, edtCountry, edtZipCode,
            edtEmailAddress, edtPhoneNumber, edtPasswordRegister, edtConfirmPasswordRegister;
    TextViewPlus nameTxt, planText, cancleText, planTypeText;
    RequestQueue requestQueue;
    String TOKEN = "", USERID = "", selectedImagePath = "", firstName = "", lastName = "", street = "",
            city = "", state = "", country = "", zipcode = "", email = "", password = "", companyName = "",
            phoneNo = "", imgPath = "", planType = "", planID = "", role = "", subscription_id = "";
    private int mDeviceWidth = 480;
    boolean showPass = true, showCPass = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        requestQueue = Volley.newRequestQueue(this);
        mSharedPreferences = getSharedPreferences(Utils.PREF_NAME, MODE_PRIVATE);
        USERID = mSharedPreferences.getString(Utils.USER_ID, "");
        TOKEN = mSharedPreferences.getString(Utils.TOKEN, "");
        planType = mSharedPreferences.getString(Utils.PLAN_TYPE, "");
        planID = mSharedPreferences.getString(Utils.PLAN_ID, "");
        role = mSharedPreferences.getString(Utils.ROLE, "");
        subscription_id = mSharedPreferences.getString(Utils.SUBSCRIPTION_ID, "");
        Utils.storeString(mSharedPreferences, Utils.MY_PROFILE, TAG);
        WindowManager w = getWindowManager();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            Point size = new Point();
            w.getDefaultDisplay().getSize(size);
            mDeviceWidth = size.x;
        } else {
            Display d = w.getDefaultDisplay();
            mDeviceWidth = d.getWidth();
        }
        init();
    }

    public void init() {

        linMainMyProfile = (LinearLayout) findViewById(R.id.linMainMyProfile);
        linImagBtn = (LinearLayout) findViewById(R.id.linImagBtn);
        linPlan = (LinearLayout) findViewById(R.id.linPlan);

        editProfileView = (ImageView) findViewById(R.id.editProfileView);
        saveProfileView = (ImageView) findViewById(R.id.saveProfileView);
        circularImageView = (ImageView) findViewById(R.id.circularImageView);

        edtFirstName = (EditText) findViewById(R.id.firstNameEditText);
        edtLastName = (EditText) findViewById(R.id.lastNameEditText);
        edtCompanyName = (EditText) findViewById(R.id.companyNameEditText);
        edtStreet = (EditText) findViewById(R.id.edtStreet);
        edtCity = (EditText) findViewById(R.id.edtCity);
        edtState = (EditText) findViewById(R.id.edtState);
        edtCountry = (EditText) findViewById(R.id.edtCountry);
        edtZipCode = (EditText) findViewById(R.id.edtZipCode);
        edtEmailAddress = (EditText) findViewById(R.id.emailEditText);
        edtPhoneNumber = (EditText) findViewById(R.id.phoneEditText);
        edtPasswordRegister = (EditText) findViewById(R.id.passwordEditText);
        edtConfirmPasswordRegister = (EditText) findViewById(R.id.confirmPasswordEditText);

        imgShowPass = (ImageView) findViewById(R.id.imgShowPass);
        imgShowCPass = (ImageView) findViewById(R.id.imgShowCPass);

        planTypeText = (TextViewPlus) findViewById(R.id.planTypeText);
        nameTxt = (TextViewPlus) findViewById(R.id.nameTxt);
        planText = (TextViewPlus) findViewById(R.id.planText);
        cancleText = (TextViewPlus) findViewById(R.id.cancleText);

        editProfileView.setOnClickListener(this);
        saveProfileView.setOnClickListener(this);
        imgShowPass.setOnClickListener(this);
        imgShowCPass.setOnClickListener(this);
        planText.setOnClickListener(this);
        cancleText.setOnClickListener(this);

        setNotEditable();

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
    }

    private void setNotEditable() {
        edtFirstName.setEnabled(false);
        edtLastName.setEnabled(false);
        edtStreet.setEnabled(false);
        edtCity.setEnabled(false);
        edtState.setEnabled(false);
        edtCountry.setEnabled(false);
        edtZipCode.setEnabled(false);
        edtCompanyName.setEnabled(false);
        edtEmailAddress.setEnabled(false);
        edtPhoneNumber.setEnabled(false);
        edtPasswordRegister.setEnabled(false);
        edtConfirmPasswordRegister.setEnabled(false);

        editProfileView.setVisibility(View.VISIBLE);
        edtPasswordRegister.setVisibility(View.GONE);
        edtConfirmPasswordRegister.setVisibility(View.GONE);

        findViewById(R.id.backLayoutclick).setOnClickListener(this);
        findViewById(R.id.framePassword).setVisibility(View.GONE);
        findViewById(R.id.frameCPassword).setVisibility(View.GONE);

        nameTxt.setText(mSharedPreferences.getString(Utils.FIRST_NAME, "") + " " + mSharedPreferences.getString(Utils.LAST_NAME, ""));
        edtFirstName.setText(mSharedPreferences.getString(Utils.FIRST_NAME, ""));
        edtLastName.setText(mSharedPreferences.getString(Utils.LAST_NAME, ""));

        if (Utils.validateString(mSharedPreferences.getString(Utils.STREET, ""))) {
            edtStreet.setText(mSharedPreferences.getString(Utils.STREET, ""));
            edtStreet.setSelection(mSharedPreferences.getString(Utils.STREET, "").length());
        }

        if (Utils.validateString(mSharedPreferences.getString(Utils.CITY, ""))) {
            edtCity.setText(mSharedPreferences.getString(Utils.CITY, ""));
            edtCity.setSelection(mSharedPreferences.getString(Utils.CITY, "").length());
        }

        if (Utils.validateString(mSharedPreferences.getString(Utils.STATE, ""))) {
            edtState.setText(mSharedPreferences.getString(Utils.STATE, ""));
            edtState.setSelection(mSharedPreferences.getString(Utils.STATE, "").length());
        }

        if (Utils.validateString(mSharedPreferences.getString(Utils.COUNTRY, ""))) {
            edtCountry.setText(mSharedPreferences.getString(Utils.COUNTRY, ""));
            edtCountry.setSelection(mSharedPreferences.getString(Utils.COUNTRY, "").length());
        }

        if (Utils.validateString(mSharedPreferences.getString(Utils.ZIPCODE, ""))) {
            edtZipCode.setText(mSharedPreferences.getString(Utils.ZIPCODE, ""));
            edtZipCode.setSelection(mSharedPreferences.getString(Utils.ZIPCODE, "").length());
        }

        edtFirstName.setSelection(mSharedPreferences.getString(Utils.FIRST_NAME, "").length());
        edtLastName.setSelection(mSharedPreferences.getString(Utils.LAST_NAME, "").length());

        if (Utils.validateString(planID)) {
//            if (planID.equalsIgnoreCase("1") || planID.equalsIgnoreCase("2")) {
//                findViewById(R.id.txtInputCompanyName).setVisibility(View.GONE);
//            } else {
            if (Utils.validateString(mSharedPreferences.getString(Utils.COMPANY_NAME, ""))) {
//                    findViewById(R.id.txtInputCompanyName).setVisibility(View.VISIBLE);
                edtCompanyName.setText(mSharedPreferences.getString(Utils.COMPANY_NAME, ""));
                edtCompanyName.setSelection(mSharedPreferences.getString(Utils.COMPANY_NAME, "").length());
            } else {
//                    findViewById(R.id.txtInputCompanyName).setVisibility(View.GONE);
//                    edtCompanyName.setText("");
            }

//            }
        }

        if (Utils.validateString(role)) {
            if (role.equalsIgnoreCase("company")) {
                linPlan.setVisibility(View.VISIBLE);
            } else {
                linPlan.setVisibility(View.GONE);
            }
        }

        edtEmailAddress.setText(mSharedPreferences.getString(Utils.EMAIL, ""));
        if (!Utils.validateString(edtPhoneNumber.getText().toString())) {
            if (Utils.validateString(mSharedPreferences.getString(Utils.PHONE_NUMBER, ""))) {
                if (mSharedPreferences.getString(Utils.PHONE_NUMBER, "").length() <= 10) {
                    String main = Utils.getPhoneNumberFormat(mSharedPreferences.getString(Utils.PHONE_NUMBER, ""));
                    edtPhoneNumber.setText(main);
                    edtPhoneNumber.setSelection(main.length());
                } else {
                    edtPhoneNumber.setText(mSharedPreferences.getString(Utils.PHONE_NUMBER, ""));
                    edtPhoneNumber.setSelection(mSharedPreferences.getString(Utils.PHONE_NUMBER, "").length());
                }
            }
        }

        planTypeText.setText(planType);
        edtPasswordRegister.setText(mSharedPreferences.getString(Utils.PASSWORD, ""));
        edtPasswordRegister.setSelection(mSharedPreferences.getString(Utils.PASSWORD, "").length());
        edtConfirmPasswordRegister.setText(mSharedPreferences.getString(Utils.PASSWORD, ""));
        edtConfirmPasswordRegister.setSelection(mSharedPreferences.getString(Utils.PASSWORD, "").length());


        edtFirstName.setTypeface(Utils.getTypeFace(MyProfileActivity.this));
        edtLastName.setTypeface(Utils.getTypeFace(MyProfileActivity.this));
        edtStreet.setTypeface(Utils.getTypeFace(MyProfileActivity.this));
        edtCity.setTypeface(Utils.getTypeFace(MyProfileActivity.this));
        edtState.setTypeface(Utils.getTypeFace(MyProfileActivity.this));
        edtCountry.setTypeface(Utils.getTypeFace(MyProfileActivity.this));
        edtZipCode.setTypeface(Utils.getTypeFace(MyProfileActivity.this));
        edtCompanyName.setTypeface(Utils.getTypeFace(MyProfileActivity.this));
        edtEmailAddress.setTypeface(Utils.getTypeFace(MyProfileActivity.this));
        edtPhoneNumber.setTypeface(Utils.getTypeFace(MyProfileActivity.this));
        edtPasswordRegister.setTypeface(Utils.getTypeFace(MyProfileActivity.this));
        edtConfirmPasswordRegister.setTypeface(Utils.getTypeFace(MyProfileActivity.this));

        imgPath = mSharedPreferences.getString(Utils.PROFILE_IMAGE, "");

        if (Utils.validateString(imgPath)) {
            Transformation transformation = new Transformation() {

                @Override
                public Bitmap transform(Bitmap source) {
                    int targetWidth = mDeviceWidth;

                    double aspectRatio = (double) source.getHeight() / (double) source.getWidth();
                    int targetHeight = (int) (targetWidth * aspectRatio);
                    if (targetHeight > targetWidth) {
                        targetHeight = targetWidth;
                    }
                    Bitmap result = Bitmap.createScaledBitmap(source, targetWidth, targetHeight, false);
                    if (result != source) {
                        // Same bitmap is returned if sizes are the same
                        source.recycle();
                    }
                    return result;
                }

                @Override
                public String key() {
                    return "transformation" + " desiredWidth";
                }
            };

            Picasso.with(MyProfileActivity.this)
                    .load(imgPath)
                    .placeholder(R.mipmap.profile_logo)
                    .error(R.mipmap.profile_logo)
                    .transform(transformation)
                    .centerCrop()
                    .resize(mDeviceWidth, (int) (mDeviceWidth))
                    .into(circularImageView);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.planText:
                if (Utils.checkInternetConnection(MyProfileActivity.this))
                    planCheck(true);
                else
                    Utils.showMessageDialog(MyProfileActivity.this, getString(R.string.alert), getString(R.string.upgrade_offline));

                break;

            case R.id.cancleText:
                if (Utils.checkInternetConnection(MyProfileActivity.this))
                    showDialog(MyProfileActivity.this, "Rogi", "Do you want to cancel the subscription?");
                else
                    Utils.showMessageDialog(MyProfileActivity.this, getString(R.string.alert), getString(R.string.cancel_offline));
                break;

            case R.id.editProfileView:
                edtFirstName.setFocusableInTouchMode(true);
                edtLastName.setFocusableInTouchMode(true);
                edtStreet.setFocusableInTouchMode(true);
                edtCity.setFocusableInTouchMode(true);
                edtState.setFocusableInTouchMode(true);
                edtCountry.setFocusableInTouchMode(true);
                edtZipCode.setFocusableInTouchMode(true);
                edtCompanyName.setFocusableInTouchMode(true);
                edtEmailAddress.setFocusableInTouchMode(true);
                edtPhoneNumber.setFocusableInTouchMode(true);
                edtPasswordRegister.setFocusableInTouchMode(true);
                edtConfirmPasswordRegister.setFocusableInTouchMode(true);

                edtFirstName.setFocusable(true);
                edtLastName.setFocusable(true);
                edtStreet.setFocusable(true);
                edtCity.setFocusable(true);
                edtState.setFocusable(true);
                edtCountry.setFocusable(true);
                edtZipCode.setFocusable(true);
                edtCompanyName.setFocusable(true);
                edtEmailAddress.setFocusable(true);
                edtPhoneNumber.setFocusable(true);
                edtPasswordRegister.setFocusable(true);
                edtConfirmPasswordRegister.setFocusable(true);

                edtFirstName.setEnabled(true);
                edtLastName.setEnabled(true);
                if (role.equalsIgnoreCase("company")) {
                    findViewById(R.id.txtInputCompanyName).setVisibility(View.VISIBLE);
                    edtCompanyName.setEnabled(true);
                } else {
                    findViewById(R.id.txtInputCompanyName).setVisibility(View.VISIBLE);
                    edtCompanyName.setEnabled(false);
                }

//                if (planID.equalsIgnoreCase("1") || planID.equalsIgnoreCase("2")) {
//                    findViewById(R.id.txtInputCompanyName).setVisibility(View.GONE);
//                }

                edtStreet.setEnabled(true);
                edtCity.setEnabled(true);
                edtState.setEnabled(true);
                edtCountry.setEnabled(true);
                edtZipCode.setEnabled(true);
                edtEmailAddress.setEnabled(false);
                edtPhoneNumber.setEnabled(true);
                edtPasswordRegister.setVisibility(View.VISIBLE);
                edtPasswordRegister.setEnabled(true);
                edtConfirmPasswordRegister.setVisibility(View.VISIBLE);
                edtConfirmPasswordRegister.setEnabled(true);
                linImagBtn.setOnClickListener(this);
                findViewById(R.id.framePassword).setVisibility(View.VISIBLE);
                findViewById(R.id.frameCPassword).setVisibility(View.VISIBLE);

                editProfileView.setVisibility(View.GONE);
                saveProfileView.setVisibility(View.VISIBLE);
                break;

            case R.id.saveProfileView:
                Utils.hideSoftKeyboard(MyProfileActivity.this);
                if (Utils.checkInternetConnection(MyProfileActivity.this)) {
                    if (fieldValidation()) {
                        firstName = edtFirstName.getText().toString().trim();
                        lastName = edtLastName.getText().toString().trim();
//                        if (planID.equalsIgnoreCase("1") || planID.equalsIgnoreCase("2")) {
//                            companyName = "";
//                        } else {
                        companyName = edtCompanyName.getText().toString().trim();
//                        }

                        email = edtEmailAddress.getText().toString().trim();
                        password = edtPasswordRegister.getText().toString().trim();

                        String str = edtPhoneNumber.getText().toString().trim().replaceAll("\\D+", "");
                        phoneNo = str;

                        street = edtStreet.getText().toString().trim();
                        city = edtCity.getText().toString().trim();
                        state = edtState.getText().toString().trim();
                        country = edtCountry.getText().toString().trim();
                        zipcode = edtZipCode.getText().toString().trim();

                        new updateProfile().execute();
                    }
                }

                edtFirstName.setFocusableInTouchMode(false);
                edtLastName.setFocusableInTouchMode(false);
                edtStreet.setFocusableInTouchMode(false);
                edtCity.setFocusableInTouchMode(false);
                edtState.setFocusableInTouchMode(false);
                edtCountry.setFocusableInTouchMode(false);
                edtZipCode.setFocusableInTouchMode(false);
                edtCompanyName.setFocusableInTouchMode(false);
                edtEmailAddress.setFocusableInTouchMode(false);
                edtPhoneNumber.setFocusableInTouchMode(false);
                edtPasswordRegister.setFocusableInTouchMode(false);
                edtConfirmPasswordRegister.setFocusableInTouchMode(false);

                edtFirstName.setFocusable(false);
                edtLastName.setFocusable(false);
                edtStreet.setFocusable(false);
                edtCity.setFocusable(false);
                edtState.setFocusable(false);
                edtCountry.setFocusable(false);
                edtZipCode.setFocusable(false);
                edtCompanyName.setFocusable(false);
                edtEmailAddress.setFocusable(false);
                edtPhoneNumber.setFocusable(false);
                edtPasswordRegister.setFocusable(false);
                edtConfirmPasswordRegister.setFocusable(false);

                edtFirstName.setEnabled(false);
                edtLastName.setEnabled(false);
                edtStreet.setEnabled(false);
                edtCity.setEnabled(false);
                edtState.setEnabled(false);
                edtCountry.setEnabled(false);
                edtZipCode.setEnabled(false);
                edtCompanyName.setEnabled(false);
                edtEmailAddress.setEnabled(false);
                edtPhoneNumber.setEnabled(false);
                edtPasswordRegister.setEnabled(false);
                edtConfirmPasswordRegister.setEnabled(false);

                break;

            case R.id.linImagBtn:
                int currentapiVersion = Build.VERSION.SDK_INT;
                if (currentapiVersion > Build.VERSION_CODES.LOLLIPOP) {
                    // Do something for lollipop and above versions
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
                    }
                }
                takePicture_Click();
                break;

            case R.id.backLayoutclick:
//                Utils.hideSoftKeyboard(MyProfileActivity.this);
                MyProfileActivity.this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
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

    public class updateProfile extends AsyncTask<Void, Void, Void> {
        boolean exception = false, isSuccess = false;
        String profile_image = "";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pdialog = new ProgressDialog(MyProfileActivity.this);
            pdialog.setMessage(getString(R.string.please_wait));
            pdialog.setCanceledOnTouchOutside(false);
            pdialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                Upload_Image uploadimage = new Upload_Image(MyProfileActivity.this);
                Logger.d(TAG, "Selected Path" + selectedImagePath);
                String uploaddata = uploadimage.uploadUpdateProfile(selectedImagePath, firstName, lastName, email, phoneNo, password,
                        TOKEN, USERID, companyName, street, city, state, country, zipcode);

                JSONObject jsonObject = new JSONObject(uploaddata);
                Logger.e(TAG, "Response :" + String.valueOf(jsonObject));

                boolean success = jsonObject.getBoolean("success");
                boolean authenticated = jsonObject.getBoolean("authenticated");
                if (authenticated) {

                }
                String msg = jsonObject.getString("message");

                if (success) {
                    if (!jsonObject.isNull("data")) {
                        isSuccess = true;
                        JSONObject jObj = jsonObject.getJSONObject("data");

                        String userId = jObj.getString("id");
                        String first_name = jObj.getString("first_name");
                        String last_name = jObj.getString("last_name");
                        String phone_number = jObj.getString("phone_number");
                        String email = jObj.getString("email");
                        profile_image = jObj.getString("profile_image");
                        String session_token = jObj.getString("session_token");
                        String company_name = jObj.getString("company_name");
                        String company_image = jObj.getString("company_image");

                        Utils.storeString(mSharedPreferences, Utils.USER_ID, userId);
                        Utils.storeString(mSharedPreferences, Utils.FIRST_NAME, first_name);
                        Utils.storeString(mSharedPreferences, Utils.LAST_NAME, last_name);
                        Utils.storeString(mSharedPreferences, Utils.PHONE_NUMBER, phone_number);
                        Utils.storeString(mSharedPreferences, Utils.EMAIL, email);
                        Utils.storeString(mSharedPreferences, Utils.PASSWORD, password);
                        Utils.storeString(mSharedPreferences, Utils.PROFILE_IMAGE, profile_image);
                        Utils.storeString(mSharedPreferences, Utils.TOKEN, session_token);
                        Utils.storeString(mSharedPreferences, Utils.COMPANY_NAME, company_name);
//                        Utils.storeString(mSharedPreferences, Utils.COMPANY_IMAGE, company_image);
                        Utils.storeString(mSharedPreferences, Utils.STREET, jObj.getString("street"));
                        Utils.storeString(mSharedPreferences, Utils.CITY, jObj.getString("city"));
                        Utils.storeString(mSharedPreferences, Utils.STATE, jObj.getString("state"));
                        Utils.storeString(mSharedPreferences, Utils.COUNTRY, jObj.getString("country"));
                        Utils.storeString(mSharedPreferences, Utils.ZIPCODE, jObj.getString("zipcode"));

                    } else {
                        pdialog.dismiss();
                        showMessage(msg);
                    }
                } else {
                    pdialog.dismiss();
                    showMessage(msg);
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
            pdialog.dismiss();

            if (isSuccess) {
                saveProfileView.setVisibility(View.GONE);
                editProfileView.setVisibility(View.VISIBLE);
                NavigationDrawerActivity.setImage(MyProfileActivity.this);
                setNotEditable();
//                finish();
            }
        }
    }

    public void planCheck(final boolean flag) {

        JSONObject params = new JSONObject();
        try {
            params.put("user_id", USERID);
            params.put("session_token", TOKEN);
            params.put("plan_id", planID);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        Logger.e(TAG, "planCheck PARA---->" + String.valueOf(params));
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.POST, Utils.MAIN_URL + Utils.PLAN_CHECK_API, params, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Logger.e(TAG, "planCheck RES---->" + String.valueOf(response));
                        try {
                            boolean success = response.getBoolean("success");
                            boolean Auth = response.getBoolean("authenticated");
                            String msg = response.getString("message");

                            if (!success || !Auth) {
                                showPlanDialog(MyProfileActivity.this, getString(R.string.alert), msg);
                            } else {
                                if (success) {
                                    String plan_active = response.getString("plan_active");
                                    if (plan_active.equals("N")) {
                                        showPlanDialog(MyProfileActivity.this, getString(R.string.alert), msg);
                                    } else {
                                        if (!response.isNull("user_info")) {
                                            JSONObject jObj = response.getJSONObject("user_info");
                                            String first_name = jObj.getString("first_name");
                                            String last_name = jObj.getString("last_name");
                                            String phone_number = jObj.getString("phone_number");
                                            String email = jObj.getString("email");
                                            String company_name = jObj.getString("company_name");
                                            String company_image = jObj.getString("company_image");
                                            String role = jObj.getString("role");

                                            Utils.storeString(mSharedPreferences, Utils.FIRST_NAME, first_name);
                                            Utils.storeString(mSharedPreferences, Utils.LAST_NAME, last_name);
                                            Utils.storeString(mSharedPreferences, Utils.PHONE_NUMBER, phone_number);
                                            Utils.storeString(mSharedPreferences, Utils.EMAIL, email);
                                            Utils.storeString(mSharedPreferences, Utils.COMPANY_NAME, company_name);
//                                            Utils.storeString(mSharedPreferences, Utils.COMPANY_IMAGE, company_image);
                                            Utils.storeString(mSharedPreferences, Utils.ROLE, role);
                                            Utils.storeString(mSharedPreferences, Utils.STREET, jObj.getString("street"));
                                            Utils.storeString(mSharedPreferences, Utils.CITY, jObj.getString("city"));
                                            Utils.storeString(mSharedPreferences, Utils.STATE, jObj.getString("state"));
                                            Utils.storeString(mSharedPreferences, Utils.COUNTRY, jObj.getString("country"));
                                            Utils.storeString(mSharedPreferences, Utils.ZIPCODE, jObj.getString("zipcode"));
                                        }
                                        if (!response.isNull("data")) {
                                            JSONObject dataObject = response.getJSONObject("data");
                                            String plainId = dataObject.getString("id");
                                            String planType = dataObject.getString("name");
                                            String subscription_id = dataObject.getString("subscription_id");
                                            String autoRenewal = dataObject.getString("auto_renewal");
                                            Utils.storeString(mSharedPreferences, Utils.PLAN_ID, plainId);
                                            Utils.storeString(mSharedPreferences, Utils.PLAN_TYPE, planType);
                                            Utils.storeString(mSharedPreferences, Utils.SUBSCRIPTION_ID, subscription_id);
                                            Utils.storeString(mSharedPreferences, Utils.AUTO_RENEWAL, autoRenewal);

                                        }

                                        nameTxt.setText(mSharedPreferences.getString(Utils.FIRST_NAME, "") + " " + mSharedPreferences.getString(Utils.LAST_NAME, ""));
                                        edtFirstName.setText(mSharedPreferences.getString(Utils.FIRST_NAME, ""));
                                        edtLastName.setText(mSharedPreferences.getString(Utils.LAST_NAME, ""));

                                        edtFirstName.setSelection(mSharedPreferences.getString(Utils.FIRST_NAME, "").length());
                                        edtLastName.setSelection(mSharedPreferences.getString(Utils.LAST_NAME, "").length());

                                        planID = mSharedPreferences.getString(Utils.PLAN_ID, "");
                                        if (Utils.validateString(planID)) {
//                                            if (planID.equalsIgnoreCase("1") || planID.equalsIgnoreCase("2")) {
//                                                findViewById(R.id.txtInputCompanyName).setVisibility(View.GONE);
//                                            } else {
                                            if (Utils.validateString(mSharedPreferences.getString(Utils.COMPANY_NAME, ""))) {
//                                                    findViewById(R.id.txtInputCompanyName).setVisibility(View.VISIBLE);
                                                edtCompanyName.setText(mSharedPreferences.getString(Utils.COMPANY_NAME, ""));
                                                edtCompanyName.setSelection(mSharedPreferences.getString(Utils.COMPANY_NAME, "").length());
                                            } else {
//                                                    findViewById(R.id.txtInputCompanyName).setVisibility(View.GONE);
                                                edtCompanyName.setText("");
                                            }

//                                            }
                                        }

                                        role = mSharedPreferences.getString(Utils.ROLE, "");
                                        if (Utils.validateString(role)) {
                                            if (role.equalsIgnoreCase("company")) {
                                                linPlan.setVisibility(View.VISIBLE);
                                            } else {
                                                linPlan.setVisibility(View.GONE);
                                            }
                                        }

                                        edtEmailAddress.setText(mSharedPreferences.getString(Utils.EMAIL, ""));
                                        if (!Utils.validateString(edtPhoneNumber.getText().toString())) {
                                            if (Utils.validateString(mSharedPreferences.getString(Utils.PHONE_NUMBER, ""))) {
                                                if (mSharedPreferences.getString(Utils.PHONE_NUMBER, "").length() <= 10) {
                                                    String main = Utils.getPhoneNumberFormat(mSharedPreferences.getString(Utils.PHONE_NUMBER, ""));
                                                    edtPhoneNumber.setText(main);
                                                    edtPhoneNumber.setSelection(main.length());
                                                } else {
                                                    edtPhoneNumber.setText(mSharedPreferences.getString(Utils.PHONE_NUMBER, ""));
                                                    edtPhoneNumber.setSelection(mSharedPreferences.getString(Utils.PHONE_NUMBER, "").length());
                                                }
                                            }
                                        }

                                        planType = mSharedPreferences.getString(Utils.PLAN_TYPE, "");
                                        planTypeText.setText(planType);

                                        if (flag) {
                                            Intent planIntent = new Intent(MyProfileActivity.this, PlanActivity.class);
                                            startActivity(planIntent);
                                        } else {
                                            cancelPlan();
                                        }
                                    }
                                }
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
                        }
                    }
                });
        int socketTimeout = 30000;//30 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsObjRequest.setRetryPolicy(policy);
        requestQueue.add(jsObjRequest);
    }


    public void cancelPlan() {
        pdialog = new ProgressDialog(MyProfileActivity.this);
        pdialog.setMessage(getString(R.string.please_wait));
        pdialog.setCanceledOnTouchOutside(false);
        pdialog.show();

        JSONObject params = new JSONObject();
        try {
            params.put("user_id", USERID);
            params.put("session_token", TOKEN);
            params.put("subscription_id", subscription_id);


        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.e("Cancel Plan PARA---->", String.valueOf(params));
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.POST, Utils.MAIN_URL + Utils.CANCEL_PLAN_API, params, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Logger.e(TAG, "URL ::" + Utils.MAIN_URL + Utils.CANCEL_PLAN_API);
                        Logger.e(TAG, "Response" + String.valueOf(response));
                        pdialog.dismiss();
                        try {
                            boolean success = response.getBoolean("success");
                            String msg = response.getString("message");

                            if (success) {
                                Utils.showMessageDialog(MyProfileActivity.this, "Rogi", msg);
                            } else {
                                Utils.showMessageDialog(MyProfileActivity.this, "Rogi", msg);
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
                            showMessage(getString(R.string.checkInternet));
                        }
                    }
                });
        int socketTimeout = 30000;//30 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsObjRequest.setRetryPolicy(policy);
        requestQueue.add(jsObjRequest);
    }

    public void showPlanDialog(final Context context, String title, String message) {
        if (message != null && message.trim().length() > 0) {
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
            builder.setTitle(title);
            builder.setCancelable(false);
            builder.setMessage(message);
            builder.setPositiveButton("OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                            NavigationDrawerActivity.clearPrefData();
                            MyProfileActivity.this.deleteDatabase(DBConstants.DB_NAME);
                            File dir = new File(Environment.getExternalStorageDirectory() + "/Media");
                            Utils.deleteDirectory(dir);
                            Intent intent = new Intent(MyProfileActivity.this, LoginActivity.class);
                            startActivity(intent);
                            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
                            finish();

                        }
                    });
            // create alert dialog
            android.app.AlertDialog alertDialog = builder.create();
            // show it
            alertDialog.show();
        }
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
        } /*else if (!Utils.validateString(edtStreet.getText().toString())) {
            flag = false;
            edtStreet.requestFocus();
            showMessage(getString(R.string.enter_project_street));
        } else if (!Utils.validateString(edtCity.getText().toString())) {
            flag = false;
            edtCity.requestFocus();
            showMessage(getString(R.string.enter_project_city));
        } else if (!Utils.validateString(edtState.getText().toString())) {
            flag = false;
            edtState.requestFocus();
            showMessage(getString(R.string.enter_project_state));
        } else if (!Utils.validateString(edtCountry.getText().toString())) {
            flag = false;
            edtCountry.requestFocus();
            showMessage(getString(R.string.enter_project_country));
        } else if (!Utils.validateString(edtZipCode.getText().toString())) {
            flag = false;
            edtZipCode.requestFocus();
            showMessage(getString(R.string.enter_project_zipcode));
        } else if (edtZipCode.getText().toString().trim().length() < 3) {
            flag = false;
            edtZipCode.requestFocus();
            showMessage(getString(R.string.zipcode_length));
        }*/ else if (!Utils.validateString(edtEmailAddress.getText().toString())) {
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
            showMessage(getString(R.string.enter_phonenumber));
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
        Utils.showResponseMessage(this, linMainMyProfile, message);
    }

    public void showDialog(final Context context, String title, String message) {
        if (message != null && message.trim().length() > 0) {
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
            builder.setTitle(title);
            builder.setCancelable(false);
            builder.setMessage(message);
            builder.setPositiveButton("Yes",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                            if (Utils.checkInternetConnection(context)) {
                                planCheck(false);
                            } else {
                                showMessage("Please Check Your Internet Connection.");
                            }
                        }
                    });

            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();


                }
            });
            // create alert dialog
            android.app.AlertDialog alertDialog = builder.create();
            // show it
            alertDialog.show();
        }
    }
}

