package com.rogi.Activity;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.Spanned;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.LinkMovementMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
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
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.rogi.R;
import com.rogi.View.Utils;
import com.rogi.logger.Logger;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;


public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String TAG = "LoginActivity";

    private SharedPreferences mSharedPreferences;

    // Button signInBTN;
    TextView forgotPasswordText, signupText, signInBTN, txtRogiUrl;
    EditText edtUsername, edtPasswordLogin;
    LinearLayout linMainLogin;
    RequestQueue requestQueue;
    String VersionName, DeviceId;

    boolean exception = false, showPass = true, flagClick = true;
    ProgressDialog pdialog;
    ImageView imgShowPass;
    ImageButton salesForceLoginView;
    private BroadcastReceiver mRegistrationBroadcastReceiver;

    // String user = "jackey123@gmail.com";
    // String pass = "admin123";
    String user = "";
    String pass = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        requestQueue = Volley.newRequestQueue(this);
        mSharedPreferences = getSharedPreferences(Utils.PREF_NAME, MODE_PRIVATE);

        try {
            PackageInfo pInfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);
            VersionName = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        DeviceId = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        String GCMRegistrationId = mSharedPreferences.getString(Utils.REG_ID, "");
        Logger.e(TAG, "GCMRegistrationId : " + GCMRegistrationId);

        if (Utils.checkInternetConnection(LoginActivity.this)) {
            getDomin();
        }

        init();
    }

    public void init() {
        linMainLogin = (LinearLayout) findViewById(R.id.linMainLogin);
        edtUsername = (EditText) findViewById(R.id.edtUsername);
        edtPasswordLogin = (EditText) findViewById(R.id.edtPasswordLogin);

        txtRogiUrl = (TextView) findViewById(R.id.txtRogiUrl);
        signInBTN = (TextView) findViewById(R.id.signInBTN);
        forgotPasswordText = (TextView) findViewById(R.id.forgotPasswordText);
        signupText = (TextView) findViewById(R.id.signupText);
        imgShowPass = (ImageView) findViewById(R.id.imgShowPass);
        salesForceLoginView = (ImageButton) findViewById(R.id.salesForceLoginView);

        signInBTN.setOnClickListener(this);
        forgotPasswordText.setOnClickListener(this);
        signupText.setOnClickListener(this);
        imgShowPass.setOnClickListener(this);
        salesForceLoginView.setOnClickListener(this);

        edtUsername.setTypeface(Utils.getTypeFace(LoginActivity.this));
        edtPasswordLogin.setTypeface(Utils.getTypeFace(LoginActivity.this));

        edtUsername.setText(user);
        edtPasswordLogin.setText(pass);

        boolean isNetAvailable = Utils.checkInternetConnection(this);
        if (!isNetAvailable) {
//            Utils.showMessageDialog(this, getResources().getString(R.string.alert), getResources().getString(R.string.connection));
            showMessage(getString(R.string.checkInternet));
        } else {
            mRegistrationBroadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    try {
                        if (intent.getAction().equals(Utils.REGISTRATION_COMPLETE)) {
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

        if (Utils.validateString(Utils.WEB_SITE_URL)) {
            String html = "<a href=\"" + Utils.WEB_SITE_URL + "\">myrogi.com</a>";
            Spanned result;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                result = Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY);
            } else {
                result = Html.fromHtml(html);
            }
            txtRogiUrl.setText(result);
            txtRogiUrl.setMovementMethod(LinkMovementMethod.getInstance());
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

    String emailText, passwordtext;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.signInBTN:
//                Utils.hideSoftKeyboard(this);
                /*Intent signinIntent = new Intent(LoginActivity.this, NavigationDrawerActivity.class);
                startActivity(signinIntent);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
                finish();*/
                if (Utils.checkInternetConnection(LoginActivity.this)) {
                    if (!Utils.validateString(Utils.MAIN_URL)) {
                        getDomin();
                    } else {
                        if (fieldValidation()) {
                            if (flagClick) {
                                flagClick = false;
                                emailText = edtUsername.getText().toString().trim();
                                passwordtext = edtPasswordLogin.getText().toString().trim();
                                Utils.projectSyncFlag = false;
                                login();
                            }
                        }

                        File dir = new File(Environment.getExternalStorageDirectory() + "/Media");
                        Utils.deleteDirectory(dir);
                    }
                } else {
                    showMessage(getString(R.string.checkInternet));
                }

                break;
            case R.id.forgotPasswordText:
                if (!Utils.validateString(Utils.MAIN_URL)) {
                    getDomin();
                } else {
                    Intent forgotPWIntent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
                    startActivity(forgotPWIntent);
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
                    finish();
                }
                break;
            case R.id.signupText:
                /*Intent signupIntent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(signupIntent);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
                finish();*/
                if (Utils.checkInternetConnection(LoginActivity.this)) {
                    if (!Utils.validateString(Utils.MAIN_URL)) {
                        getDomin();
                    } else {
                        if (Utils.checkInternetConnection(LoginActivity.this)) {
                            Intent planIntent = new Intent(LoginActivity.this, PlanActivity.class);
                            startActivity(planIntent);
                            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
                            Utils.storeString(mSharedPreferences, Utils.MY_PROFILE, "");
                            Utils.storeString(mSharedPreferences, Utils.AUTO_RENEWAL, "");
                            Utils.storeString(mSharedPreferences, Utils.SUBSCRIPTION_ID, "");
                        }

                        File dir1 = new File(Environment.getExternalStorageDirectory() + "/Media");
                        Utils.deleteDirectory(dir1);
                    }
                } else {
                    showMessage(getString(R.string.checkInternet));
                }
                break;

            case R.id.imgShowPass:
                if (!edtPasswordLogin.getText().toString().equals("")) {
                    if (showPass) {
                        edtPasswordLogin.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                        edtPasswordLogin.setSelection(edtPasswordLogin.getText().toString().trim().length());
                        showPass = false;
                    } else {
                        edtPasswordLogin.setTransformationMethod(PasswordTransformationMethod.getInstance());
                        edtPasswordLogin.setSelection(edtPasswordLogin.getText().toString().trim().length());
                        showPass = true;
                    }
                }
                break;
            case R.id.salesForceLoginView:
                //  Intent planIntent = new Intent(LoginActivity.this, PlanActivity.class);
                //  startActivity(planIntent);
                break;
        }
    }

    public void login() {
        pdialog = new ProgressDialog(LoginActivity.this);
        pdialog.setMessage(getString(R.string.please_wait));
        pdialog.setCanceledOnTouchOutside(false);
        pdialog.show();

        JSONObject params = new JSONObject();
        try {
            params.put("email", emailText);
            params.put("password", passwordtext);
            params.put("login_type", Utils.LOGIN_TYPE);
            params.put("device_token", /*DeviceId*/mSharedPreferences.getString(Utils.REG_ID, ""));
            params.put("device_type", Utils.DEVICE_TYPE);
            params.put("app_version", VersionName);
            params.put("timezone", Utils.getTimeZoneId());

        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.e("Login PARA---->", String.valueOf(params));
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.POST, Utils.MAIN_URL + Utils.LOGIN_API, params, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Logger.e(TAG, "URL ::" + Utils.MAIN_URL + Utils.LOGIN_API);
                        Logger.e(TAG, "Response" + String.valueOf(response));
                        try {
                            boolean success = response.getBoolean("success");
                            String msg = response.getString("message");
                            flagClick = true;
                            if (success) {
                                if (!response.isNull("data")) {
                                    pdialog.dismiss();

                                    String planActive = response.getString("plan_active");
                                    JSONObject planInfoObject = response.getJSONObject("plan_info");

                                    JSONObject jObj = response.getJSONObject("data");
                                    String userId = jObj.getString("id");
                                    String first_name = jObj.getString("first_name");
                                    String last_name = jObj.getString("last_name");
                                    String phone_number = jObj.getString("phone_number");
                                    String email = jObj.getString("email");
                                    String profile_image = jObj.getString("profile_image");
                                    String session_token = jObj.getString("session_token");
                                    String company_name = jObj.getString("company_name");
                                    String company_image = jObj.getString("company_image");
                                    String role = jObj.getString("role");
                                    String authorize = "";
                                    if (!jObj.isNull("authorize_text")) {
                                        authorize = jObj.getString("authorize_text");
                                    } else {
                                        authorize = "";
                                    }

                                    Utils.storeString(mSharedPreferences, Utils.USER_ID, userId);
                                    Utils.storeString(mSharedPreferences, Utils.FIRST_NAME, first_name);
                                    Utils.storeString(mSharedPreferences, Utils.LAST_NAME, last_name);
                                    Utils.storeString(mSharedPreferences, Utils.PHONE_NUMBER, phone_number);
                                    Utils.storeString(mSharedPreferences, Utils.EMAIL, email);
                                    Utils.storeString(mSharedPreferences, Utils.PASSWORD, edtPasswordLogin.getText().toString());
                                    Utils.storeString(mSharedPreferences, Utils.PROFILE_IMAGE, profile_image);
                                    Utils.storeString(mSharedPreferences, Utils.TOKEN, session_token);
                                    Utils.storeString(mSharedPreferences, Utils.COMPANY_NAME, company_name);
                                    Utils.storeString(mSharedPreferences, Utils.COMPANY_IMAGE, company_image);
                                    Utils.storeString(mSharedPreferences, Utils.ROLE, role);
                                    Utils.storeString(mSharedPreferences, Utils.MY_PROFILE, "");
                                    Utils.storeString(mSharedPreferences, Utils.AUTO_RENEWAL, "");
                                    Utils.storeString(mSharedPreferences, Utils.SUBSCRIPTION_ID, "");
                                    Utils.storeString(mSharedPreferences, Utils.AUTHORIZE_TEXT, authorize);

                                    Utils.storeString(mSharedPreferences, Utils.STREET, jObj.getString("street"));
                                    Utils.storeString(mSharedPreferences, Utils.CITY, jObj.getString("city"));
                                    Utils.storeString(mSharedPreferences, Utils.STATE, jObj.getString("state"));
                                    Utils.storeString(mSharedPreferences, Utils.COUNTRY, jObj.getString("country"));
                                    Utils.storeString(mSharedPreferences, Utils.ZIPCODE, jObj.getString("zipcode"));

                                    if (planInfoObject.length() > 0) {
                                        String planId = planInfoObject.getString("id");
                                        String planType = planInfoObject.getString("name");
                                        String autoRenewal = planInfoObject.getString("auto_renewal");
                                        String subscription_id = planInfoObject.getString("subscription_id");
                                        Utils.storeString(mSharedPreferences, Utils.PLAN_ID, planId);
                                        Utils.storeString(mSharedPreferences, Utils.PLAN_TYPE, planType);
                                        Utils.storeString(mSharedPreferences, Utils.AUTO_RENEWAL, autoRenewal);
                                        Utils.storeString(mSharedPreferences, Utils.SUBSCRIPTION_ID, subscription_id);
                                        Intent signinIntent = new Intent(LoginActivity.this, NavigationDrawerActivity.class);
                                        startActivity(signinIntent);
                                        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
                                        finish();
                                    } else {
                                        pdialog.dismiss();
                                        Intent planIntent = new Intent(LoginActivity.this, PlanActivity.class);
                                        startActivity(planIntent);
                                        Utils.storeString(mSharedPreferences, Utils.MY_PROFILE, TAG);
                                    }
                                } else {
                                    pdialog.dismiss();
                                    showMessage(msg);
                                }
                            } else {
                                pdialog.dismiss();
                                showMessage(msg);
                            }
                        } catch (JSONException e) {
                            flagClick = true;
                            e.printStackTrace();
                            Log.e("ERR", e.getMessage());
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        flagClick = true;
                        pdialog.dismiss();
                        if (error instanceof NoConnectionError) {
                            showMessage(getString(R.string.checkInternet));
                        }
                    }
                });
        int socketTimeout = 30000;//30 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsObjRequest.setRetryPolicy(policy);
        requestQueue.add(jsObjRequest);
    }

    public void getDomin() {
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, Utils.DOMIN_URL, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Logger.e(TAG, "URL ::" + Utils.DOMIN_URL);
                        Logger.e(TAG, "Response" + String.valueOf(response));
                        try {
                            boolean success = response.getBoolean("success");
                            String msg = response.getString("message");
                            if (success) {
                                if (!response.isNull("data")) {
                                    JSONObject dataObj = response.getJSONObject("data");
                                    String url = dataObj.getString("url");
                                    Utils.storeString(mSharedPreferences, Utils.URL_WEB_ADDRESS, url);
                                    Utils.storeString(mSharedPreferences, Utils.URL_API_ADDRESS, url + "/api/web/");
                                    Utils.MAIN_URL = mSharedPreferences.getString(Utils.URL_API_ADDRESS, "");
                                    Utils.WEB_SITE_URL = mSharedPreferences.getString(Utils.URL_WEB_ADDRESS, "");

                                    if (Utils.validateString(Utils.WEB_SITE_URL)) {
                                        String html = "<a href=\"" + Utils.WEB_SITE_URL + "\">myrogi.com</a>";
                                        Spanned result;
                                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                                            result = Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY);
                                        } else {
                                            result = Html.fromHtml(html);
                                        }
                                        txtRogiUrl.setText(result);
                                        txtRogiUrl.setMovementMethod(LinkMovementMethod.getInstance());
                                    }

                                } else {
                                    showMessage(msg);
                                }
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

                        }
                    }
                });
        int socketTimeout = 30000;//30 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsObjRequest.setRetryPolicy(policy);
        requestQueue.add(jsObjRequest);
    }

    public boolean fieldValidation() {
        boolean flag = true;
        if (!Utils.validateString(edtUsername.getText().toString())) {
            flag = false;
            edtUsername.requestFocus();
            showMessage(getString(R.string.enter_emailaddress));
        } else if (edtUsername.getText().toString().length() > 0 && !Utils.isEmailValid(edtUsername.getText().toString())) {
            flag = false;
            showMessage(getString(R.string.invalid_email));
            edtUsername.requestFocus();
        } else if (!Utils.validateString(edtPasswordLogin.getText().toString())) {
            flag = false;
            edtPasswordLogin.requestFocus();
            showMessage(getString(R.string.enter_password));
        } /*else if (edtPasswordLogin.getText().toString().trim().length() < 8) {
            flag = false;
            edtPasswordLogin.requestFocus();
            showMessage(getString(R.string.check_password_length));
        }*/
        return flag;
    }

    private void showMessage(String message) {
        Utils.showResponseMessage(this, linMainLogin, message);
    }

    /*public void getDomin() {

        if (Utils.checkInternetConnection(LoginActivity.this)) {
            GetDominNameRequestTask getDominNameRequestTask = new GetDominNameRequestTask(LoginActivity.this);
            getDominNameRequestTask.setAsyncCallListener(new AsyncCallListener() {

                @Override
                public void onResponseReceived(Object response) {
                    ExistingUserModel advertiseListModel = (ExistingUserModel) response;
                    try {
                        if (advertiseListModel.getSuccess().equalsIgnoreCase("true") && advertiseListModel.getAuthenticated().equalsIgnoreCase("true")) {
                            if (Utils.validateString(advertiseListModel.getUrl())) {
                                Utils.URL = advertiseListModel.getUrl();
                            }

                            Utils.URL = mSharedPreferences.getString(Utils.URL_API_ADDRESS, "");
                        }
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onResponseReceived(JSONObject object) {

                }

                @Override
                public void onErrorReceived(String error) {
                    Toast.makeText(LoginActivity.this, getString(R.string.try_again), Toast.LENGTH_SHORT).show();
                }
            });
            getDominNameRequestTask.execute();
        }
    }*/

}