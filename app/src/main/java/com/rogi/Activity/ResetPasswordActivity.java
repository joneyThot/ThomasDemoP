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


public class ResetPasswordActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String TAG = "ResetPasswordActivity";

    LinearLayout linMainResetPassword;
    //    Button resetPwBTN;
    TextView titleText, resetPwBTN;
    EditText edtNewPassword, edtReEnterPassword, edtTempPassword;
    RequestQueue requestQueue;
    SharedPreferences mSharedPreference;
    String TempPW, USERID, email;
    String comfirmPW;

    ProgressDialog pdialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        requestQueue = Volley.newRequestQueue(this);
        mSharedPreference = this.getSharedPreferences(Utils.PREF_NAME, Context.MODE_PRIVATE);
        USERID = mSharedPreference.getString(Utils.USER_ID, "");
        TempPW = mSharedPreference.getString(Utils.TEMP_PASSWORD, "");
        email = mSharedPreference.getString(Utils.EMAIL, "");

        init();
    }

    public void init() {
        titleText = (TextView) findViewById(R.id.titleText);
        titleText.setText("Reset Password");

        linMainResetPassword = (LinearLayout) findViewById(R.id.linMainResetPassword);

        edtTempPassword = (EditText) findViewById(R.id.edtTempPassword);
       // edtTempPassword.setText(TempPW);

        edtNewPassword = (EditText) findViewById(R.id.edtNewPassword);
        edtReEnterPassword = (EditText) findViewById(R.id.edtReEnterPassword);

        resetPwBTN = (TextView) findViewById(R.id.resetPwBTN);
        resetPwBTN.setOnClickListener(this);
        findViewById(R.id.backLayoutclick).setOnClickListener(this);

        edtTempPassword.setTypeface(Utils.getTypeFace(ResetPasswordActivity.this));
        edtNewPassword.setTypeface(Utils.getTypeFace(ResetPasswordActivity.this));
        edtReEnterPassword.setTypeface(Utils.getTypeFace(ResetPasswordActivity.this));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.resetPwBTN:
                Utils.hideSoftKeyboard(this);
                if (fieldValidation()) {
                    comfirmPW = edtNewPassword.getText().toString().trim();
                    ResetPassword();
                }
                break;

            case R.id.backLayoutclick:
                Intent signInIntent = new Intent(ResetPasswordActivity.this, LoginActivity.class);
                startActivity(signInIntent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
                finish();
                break;

        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent signInIntent = new Intent(ResetPasswordActivity.this, LoginActivity.class);
        startActivity(signInIntent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
        finish();
    }

    public void ResetPassword() {

        pdialog = new ProgressDialog(ResetPasswordActivity.this);
        pdialog.setMessage(getString(R.string.please_wait));
        pdialog.setCanceledOnTouchOutside(false);
        pdialog.show();

        JSONObject params = new JSONObject();
        try {
            params.put("password", comfirmPW);
            params.put("email", email);
            params.put("temporary_password", TempPW);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        Logger.e(TAG, "ResetPW PARA---->" + String.valueOf(params));
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.POST, Utils.MAIN_URL + Utils.RESET_PASSWORD_API, params, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Logger.e(TAG, "URL ::" + Utils.MAIN_URL + Utils.RESET_PASSWORD_API);
                        Logger.e(TAG, "Response" + String.valueOf(response));
                        try {
                            boolean success = response.getBoolean("success");
                            String msg = response.getString("message");

                            if (success) {
                                pdialog.dismiss();
                                Intent signInIntent = new Intent(ResetPasswordActivity.this, LoginActivity.class);
                                startActivity(signInIntent);
                                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
                                finish();

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
        if (!Utils.validateString(edtNewPassword.getText().toString())) {
            flag = false;
            edtNewPassword.requestFocus();
            showMessage(getString(R.string.enter_password));
        } else if (edtNewPassword.getText().toString().trim().length() < 8) {
            flag = false;
            edtNewPassword.requestFocus();
            showMessage(getString(R.string.check_password_length));
        } else if (!Utils.validateString(edtReEnterPassword.getText().toString())) {
            flag = false;
            edtReEnterPassword.requestFocus();
            showMessage(getString(R.string.enter_confirampassword));
        } else if (!edtNewPassword.getText().toString().equalsIgnoreCase(edtReEnterPassword.getText().toString())) {
            flag = false;
            edtReEnterPassword.requestFocus();
            showMessage(getString(R.string.password_not_match));
        }
        return flag;
    }

    private void showMessage(String message) {
        Utils.showResponseMessage(this, linMainResetPassword, message);
    }

}
