package com.rogi.Activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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
import com.rogi.R;
import com.rogi.View.Utils;
import com.rogi.logger.Logger;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.security.ProviderInstaller;

import org.json.JSONException;
import org.json.JSONObject;

public class SplashActivity extends AppCompatActivity {

    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;
    private static int SPLASH_TIME_OUT = 2000;
    private SharedPreferences mSharedPreferences;
    RequestQueue requestQueue;
    public static final String TAG = "SplashActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        requestQueue = Volley.newRequestQueue(this);
        mSharedPreferences = getSharedPreferences(Utils.PREF_NAME, MODE_PRIVATE);
        Utils.getTimeZoneId();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                if (checkPlayServices()) {
                    updateAndroidSecurityProvider(SplashActivity.this);
                    Utils.projectSyncFlag = false;

                    if (Utils.checkInternetConnection(SplashActivity.this)) {
                        getDomin();
                    } else {
                        Utils.MAIN_URL = mSharedPreferences.getString(Utils.URL_API_ADDRESS, "");
                        Utils.WEB_SITE_URL = mSharedPreferences.getString(Utils.URL_WEB_ADDRESS, "");
                        if ((!mSharedPreferences.getString(Utils.USER_ID, "").isEmpty()) && (!mSharedPreferences.getString(Utils.PLAN_ID, "").isEmpty())) {
                            Intent i = new Intent(SplashActivity.this, NavigationDrawerActivity.class);
                            startActivity(i);
                            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
                            finish();
                        } else {
                            Intent i = new Intent(SplashActivity.this, LoginActivity.class);
                            startActivity(i);
                            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
                            finish();
                        }
                    }

                }

            }
        }, SPLASH_TIME_OUT);
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

    private void updateAndroidSecurityProvider(Activity callingActivity) {
        try {
            ProviderInstaller.installIfNeeded(this);
        } catch (GooglePlayServicesRepairableException e) {
            // Thrown when Google Play Services is not installed, up-to-date, or enabled
            // Show dialog to allow users to install, update, or otherwise enable Google Play services.
            GooglePlayServicesUtil.getErrorDialog(e.getConnectionStatusCode(), callingActivity, 0);
        } catch (GooglePlayServicesNotAvailableException e) {
            Logger.e("SecurityException", "Google Play Services not available.");
        }
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

                                    planCheck();
                                }
                            } else {
                                Toast.makeText(SplashActivity.this, msg, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e("ERR", e.getMessage());
                            Toast.makeText(SplashActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error instanceof NoConnectionError) {
                        }
                        Toast.makeText(SplashActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                    }
                });
        int socketTimeout = 30000;//30 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsObjRequest.setRetryPolicy(policy);
        requestQueue.add(jsObjRequest);
    }

    public void planCheck() {

        JSONObject params = new JSONObject();
        try {
            params.put("user_id", mSharedPreferences.getString(Utils.USER_ID, ""));
            params.put("session_token", mSharedPreferences.getString(Utils.TOKEN, ""));
            params.put("plan_id", mSharedPreferences.getString(Utils.PLAN_ID, ""));

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
                                Intent i = new Intent(SplashActivity.this, LoginActivity.class);
                                startActivity(i);
                                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
                                finish();
                            } else {
                                if (success) {
                                    String plan_active = response.getString("plan_active");
                                    if (plan_active.equals("N")) {
                                        Intent i = new Intent(SplashActivity.this, LoginActivity.class);
                                        startActivity(i);
                                        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
                                        finish();
                                    } else {
                                        Intent i = new Intent(SplashActivity.this, NavigationDrawerActivity.class);
                                        startActivity(i);
                                        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
                                        finish();
                                    }
                                }
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e("ERR", e.getMessage());
                            Toast.makeText(SplashActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error instanceof NoConnectionError) {
                        }
                        Toast.makeText(SplashActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                    }
                });
        int socketTimeout = 30000;//30 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsObjRequest.setRetryPolicy(policy);
        requestQueue.add(jsObjRequest);
    }

}
