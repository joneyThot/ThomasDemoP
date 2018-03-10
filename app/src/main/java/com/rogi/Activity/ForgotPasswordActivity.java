package com.rogi.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
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

import org.json.JSONException;
import org.json.JSONObject;



public class ForgotPasswordActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String TAG = "ForgotPasswordActivity";
    TextView titleText,forgotPwBTN;
    LinearLayout linMainForgot;
    EditText edtEmailAddressForgot;
    String emailText;
    RequestQueue requestQueue;
    SharedPreferences mSharedPreference;
    ProgressDialog pdialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        requestQueue = Volley.newRequestQueue(this);
        mSharedPreference = this.getSharedPreferences(Utils.PREF_NAME, Context.MODE_PRIVATE);
        init();
    }

    public void init() {
        titleText = (TextView) findViewById(R.id.titleText);
        titleText.setText("Forgot Password");

        linMainForgot = (LinearLayout) findViewById(R.id.linMainForgot);
        edtEmailAddressForgot = (EditText) findViewById(R.id.edtEmailAddressForgot);

        forgotPwBTN = (TextView) findViewById(R.id.forgotPwBTN);
        forgotPwBTN.setOnClickListener(this);
        findViewById(R.id.backLayoutclick).setOnClickListener(this);

        edtEmailAddressForgot.setTypeface(Utils.getTypeFace(ForgotPasswordActivity.this));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.forgotPwBTN:
                Utils.hideSoftKeyboard(this);
                if (fieldValidation()) {
                    emailText = edtEmailAddressForgot.getText().toString().trim();
                    forgotPassword();
                }
                break;

            case R.id.backLayoutclick:
                Utils.hideSoftKeyboard(this);
                Intent signInIntent = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
                startActivity(signInIntent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
                finish();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent signInIntent = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
        startActivity(signInIntent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
        finish();
    }

    public void forgotPassword() {

        pdialog = new ProgressDialog(ForgotPasswordActivity.this);
        pdialog.setMessage(getString(R.string.please_wait));
        pdialog.setCanceledOnTouchOutside(false);
        pdialog.show();

        JSONObject params = new JSONObject();
        try {
            params.put("email", emailText);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        Logger.e(TAG, "Forgot PARA---->" + String.valueOf(params));
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.POST, Utils.MAIN_URL + Utils.FORGOT_PASSWORD_API, params, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Logger.e(TAG, "Forgot RES---->" + String.valueOf(response));
                        try {
                            boolean success = response.getBoolean("success");
                            String msg = response.getString("message");

                            if (success) {
                                if (!response.isNull("data")) {
                                    pdialog.dismiss();

                                    JSONObject dataObject = response.getJSONObject("data");
                                    String tempPW = dataObject.getString("temporary_password");
                                    //  String userID = dataObject.getString("user_id");

                                    Utils.storeString(mSharedPreference, Utils.TEMP_PASSWORD, tempPW);
                                    Utils.storeString(mSharedPreference, Utils.EMAIL, edtEmailAddressForgot.getText().toString());
                                    //   Utils.storeString(mSharedPreference, Utils.USER_ID, userID);

                                    Intent forgotPwIntent = new Intent(ForgotPasswordActivity.this, ResetPasswordActivity.class);
                                    startActivity(forgotPwIntent);
                                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
                                    finish();
                                } else {
                                    pdialog.dismiss();
                                    showMessage(msg);
                                }
                            } else {
                                pdialog.dismiss();
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
                            showMessage(getString(R.string.checkInternet));
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
        if (!Utils.validateString(edtEmailAddressForgot.getText().toString())) {
            flag = false;
            edtEmailAddressForgot.requestFocus();
            showMessage(getString(R.string.enter_emailaddress));
        } else if (edtEmailAddressForgot.getText().toString().length() > 0 && !Utils.isEmailValid(edtEmailAddressForgot.getText().toString())) {
            flag = false;
            showMessage(getString(R.string.invalid_email));
            edtEmailAddressForgot.requestFocus();
        }
        return flag;
    }

    private void showMessage(String message) {
        Utils.showResponseMessage(this, linMainForgot, message);
    }
}
