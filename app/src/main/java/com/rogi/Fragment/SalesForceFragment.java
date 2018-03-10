package com.rogi.Fragment;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
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
import com.rogi.Activity.NavigationDrawerActivity;
import com.rogi.R;
import com.rogi.View.Utils;
import com.rogi.logger.Logger;

import org.json.JSONException;
import org.json.JSONObject;

public class SalesForceFragment extends Fragment implements View.OnClickListener {

    public static final String TAG = "SalesForceFragment";
    private SharedPreferences mSharedPreferences;

    View view;
    RequestQueue requestQueue;
    String TOKEN = "", USERID = "", PLANID = "", ROLE = "";
    EditText edtConsumerKey, edtConsumerSecretKey, edtSecurityToken, edtUsername, edtPassword;
    RelativeLayout rootLayout;
    LinearLayout linDefault;
    ScrollView sv1;
    TextView btnSalesForce;
    ProgressDialog pdialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_sales_force, container, false);
        requestQueue = Volley.newRequestQueue(getActivity());
        mSharedPreferences = getActivity().getSharedPreferences(Utils.PREF_NAME, getActivity().MODE_PRIVATE);
        TOKEN = mSharedPreferences.getString(Utils.TOKEN, "");
        USERID = mSharedPreferences.getString(Utils.USER_ID, "");
        PLANID = mSharedPreferences.getString(Utils.PLAN_ID, "");
        ROLE = mSharedPreferences.getString(Utils.ROLE, "");

        NavigationDrawerActivity.titleText.setVisibility(View.VISIBLE);
        NavigationDrawerActivity.titleText.setText(getString(R.string.nav_sales_force));
        NavigationDrawerActivity.imgLogo.setVisibility(View.GONE);
        NavigationDrawerActivity.filterView.setVisibility(View.INVISIBLE);
        NavigationDrawerActivity.imgSyc.setVisibility(View.INVISIBLE);

        init();
        return view;
    }

    private void init() {

        rootLayout = (RelativeLayout) view.findViewById(R.id.rootLayout);
        sv1 = (ScrollView) view.findViewById(R.id.sv1);
        linDefault = (LinearLayout) view.findViewById(R.id.linDefault);
        linDefault.setVisibility(View.GONE);
        edtConsumerKey = (EditText) view.findViewById(R.id.edtConsumerKey);
        edtConsumerSecretKey = (EditText) view.findViewById(R.id.edtConsumerSecretKey);
        edtSecurityToken = (EditText) view.findViewById(R.id.edtSecurityToken);
        edtUsername = (EditText) view.findViewById(R.id.edtUsername);
        edtPassword = (EditText) view.findViewById(R.id.edtPassword);

        btnSalesForce = (TextView) view.findViewById(R.id.btnSalesForce);
        btnSalesForce.setOnClickListener(this);

        if(ROLE.equalsIgnoreCase("company") && PLANID.equalsIgnoreCase("6")) {
            linDefault.setVisibility(View.GONE);
            sv1.setVisibility(View.VISIBLE);
        } else {
            linDefault.setVisibility(View.VISIBLE);
            sv1.setVisibility(View.GONE);
        }

        if (Utils.validateString(mSharedPreferences.getString(Utils.SF_CONSUMER_KEY, "")))
            edtConsumerKey.setText(mSharedPreferences.getString(Utils.SF_CONSUMER_KEY, ""));

        if (Utils.validateString(mSharedPreferences.getString(Utils.SF_CONSUMER_SECRET_KEY, "")))
            edtConsumerSecretKey.setText(mSharedPreferences.getString(Utils.SF_CONSUMER_SECRET_KEY, ""));

        if (Utils.validateString(mSharedPreferences.getString(Utils.SF_SECURITY_TOKEN, "")))
            edtSecurityToken.setText(mSharedPreferences.getString(Utils.SF_SECURITY_TOKEN, ""));

        if (Utils.validateString(mSharedPreferences.getString(Utils.SF_USERNAME, "")))
            edtUsername.setText(mSharedPreferences.getString(Utils.SF_USERNAME, ""));

        if (Utils.validateString(mSharedPreferences.getString(Utils.SF_PASSWORD, "")))
            edtPassword.setText(mSharedPreferences.getString(Utils.SF_PASSWORD, ""));

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSalesForce:
                if (fieldValidation()) {
                    salesForceSync(edtConsumerKey.getText().toString().trim(), edtConsumerSecretKey.getText().toString().trim(),
                            edtSecurityToken.getText().toString().trim(), edtUsername.getText().toString().trim(),
                            edtPassword.getText().toString());
                }
                break;
        }

    }

    public void salesForceSync(String consumer_key, String consumer_seceret_key, String security_token, String username,
                               String password) {

        pdialog = new ProgressDialog(getActivity());
        pdialog.setMessage(getString(R.string.please_wait));
        pdialog.setCanceledOnTouchOutside(false);
        pdialog.show();

        JSONObject params = new JSONObject();
        try {
            params.put("user_id", USERID);
            params.put("session_token", TOKEN);
            params.put("consumer_key", consumer_key);
            params.put("consumer_seceret_key", consumer_seceret_key);
            params.put("security_token", security_token);
            params.put("username", username);
            params.put("password", password);
            params.put("type", Utils.DEVICE_TYPE);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Logger.e(TAG, "SalesForce Sync PARA---->" + String.valueOf(params));
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.POST, Utils.MAIN_URL + Utils.SALES_FORCE_SYNC, params, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Logger.e(TAG, "URL---->" + Utils.MAIN_URL + Utils.SALES_FORCE_SYNC);
                        Logger.e(TAG, "SalesForce Sync RES---->" + String.valueOf(response));
                        try {
                            boolean success = response.getBoolean("success");
                            boolean auth = response.getBoolean("authenticated");
                            String msg = response.getString("message");
                            pdialog.dismiss();
                            if (success && auth) {
                                Utils.storeString(mSharedPreferences, Utils.SF_CONSUMER_KEY, edtConsumerKey.getText().toString().trim());
                                Utils.storeString(mSharedPreferences, Utils.SF_CONSUMER_SECRET_KEY, edtConsumerSecretKey.getText().toString().trim());
                                Utils.storeString(mSharedPreferences, Utils.SF_SECURITY_TOKEN, edtSecurityToken.getText().toString().trim());
                                Utils.storeString(mSharedPreferences, Utils.SF_USERNAME, edtUsername.getText().toString().trim());
                                Utils.storeString(mSharedPreferences, Utils.SF_PASSWORD, edtPassword.getText().toString().trim());
                                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new HomeFragment()).commit();
                            } else {
                                Utils.showMessageDialog(getActivity(), getString(R.string.alert), msg);
                            }

                        } catch (JSONException e) {
                            pdialog.dismiss();
                            e.printStackTrace();
                            Log.e("ERR", e.getMessage());
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error instanceof NoConnectionError) {
                        }
                        pdialog.dismiss();
                    }
                });
        int socketTimeout = 30000;//30 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsObjRequest.setRetryPolicy(policy);
        requestQueue.add(jsObjRequest);
    }

    public boolean fieldValidation() {
        boolean flag = true;
        if (!Utils.validateString(edtConsumerKey.getText().toString())) {
            flag = false;
            edtConsumerKey.requestFocus();
            showMessage(getString(R.string.enter_consumer_key));
        } else if (!Utils.validateString(edtConsumerSecretKey.getText().toString())) {
            flag = false;
            edtConsumerSecretKey.requestFocus();
            showMessage(getString(R.string.enter_consumer_Secret_key));
        } else if (!Utils.validateString(edtSecurityToken.getText().toString())) {
            flag = false;
            edtSecurityToken.requestFocus();
            showMessage(getString(R.string.enter_security_token));
        } else if (!Utils.validateString(edtUsername.getText().toString())) {
            flag = false;
            showMessage(getString(R.string.enter_user_name));
            edtUsername.requestFocus();
        } else if (!Utils.validateString(edtPassword.getText().toString())) {
            flag = false;
            edtPassword.requestFocus();
            showMessage(getString(R.string.enter_password));
        }
        return flag;
    }

    private void showMessage(String message) {
        Utils.showResponseMessage(getActivity(), rootLayout, message);
    }

}
