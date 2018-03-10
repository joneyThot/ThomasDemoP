package com.rogi.Fragment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
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
import com.rogi.Activity.PaymentPurchaseUserActivity;
import com.rogi.R;
import com.rogi.View.Utils;
import com.rogi.logger.Logger;

import org.json.JSONException;
import org.json.JSONObject;

public class PurchaseUserFragment extends Fragment implements View.OnClickListener {

    public static final String TAG = "PurchaseUserFragment";
    private SharedPreferences mSharedPreferences;

    View view;
    RequestQueue requestQueue;
    String TOKEN = "", USERID = "", PLANID = "", sub_level_per_user_rate = "", planPrice = "", planName = "",
            total_amount = "";
    int subUsertotal = 0;
    EditText edtSubUserNo;
    TextView btnPayNow, txtDefaultUser, txtPaidUser, txtAmountPerUser, txtTotalAmount;
    ProgressDialog pdialog;
    RelativeLayout rootLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_purchase_user, container, false);
        requestQueue = Volley.newRequestQueue(getActivity());
        mSharedPreferences = getActivity().getSharedPreferences(Utils.PREF_NAME, getActivity().MODE_PRIVATE);
        TOKEN = mSharedPreferences.getString(Utils.TOKEN, "");
        USERID = mSharedPreferences.getString(Utils.USER_ID, "");
        PLANID = mSharedPreferences.getString(Utils.PLAN_ID, "");

        NavigationDrawerActivity.titleText.setVisibility(View.VISIBLE);
        NavigationDrawerActivity.titleText.setText(getString(R.string.nav_purchase_user));
        NavigationDrawerActivity.imgLogo.setVisibility(View.GONE);
        NavigationDrawerActivity.filterView.setVisibility(View.INVISIBLE);
        NavigationDrawerActivity.imgSyc.setVisibility(View.INVISIBLE);

        init();
        return view;
    }

    private void init() {
        rootLayout = (RelativeLayout) view.findViewById(R.id.rootLayout);

        edtSubUserNo = (EditText) view.findViewById(R.id.edtSubUserNo);

        txtDefaultUser = (TextView) view.findViewById(R.id.txtDefaultUser);
        txtPaidUser = (TextView) view.findViewById(R.id.txtPaidUser);
        txtAmountPerUser = (TextView) view.findViewById(R.id.txtAmountPerUser);
        txtTotalAmount = (TextView) view.findViewById(R.id.txtTotalAmount);

        btnPayNow = (TextView) view.findViewById(R.id.btnPayNow);
        btnPayNow.setOnClickListener(this);

        if (Utils.checkInternetConnection(getActivity()))
            getPurchasePlanDetails();
        else
            showMessageDialog(getActivity(), getString(R.string.alert), getString(R.string.purchase_user_offline));

        edtSubUserNo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    int perSubUserRate = 0, subUserRate = 0;
                    if (Utils.validateString(edtSubUserNo.getText().toString().trim())) {
                        subUserRate = Integer.parseInt(edtSubUserNo.getText().toString().trim());
                        if (Utils.validateString(sub_level_per_user_rate)) {
                            perSubUserRate = Integer.parseInt(sub_level_per_user_rate);
                        }
                        subUsertotal = 0;
                        if (subUserRate > 0) {
                            subUsertotal = (subUserRate * perSubUserRate);
                            txtTotalAmount.setText("$ " + subUsertotal);
                        } else {
                            edtSubUserNo.setText("");
                            txtTotalAmount.setText("$ " + 0);
                        }

                    } else {
                        txtTotalAmount.setText("$ " + 0);
                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }

        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnPayNow:
                if (fieldValidation()) {
                    Intent planIntent = new Intent(getActivity(), PaymentPurchaseUserActivity.class);
                    planIntent.putExtra(Utils.PLAN_PRICE, planPrice);
                    planIntent.putExtra(Utils.PLAN_ID, PLANID);
                    planIntent.putExtra(Utils.PLAN_NAME, planName);
                    planIntent.putExtra(Utils.TOTAL_AMOUNT, total_amount);
                    planIntent.putExtra(Utils.ENTER_SUB_USER, edtSubUserNo.getText().toString().trim());
                    planIntent.putExtra(Utils.SUB_USER_TOTAL_AMOUNT, String.valueOf(subUsertotal));
                    planIntent.putExtra(Utils.PER_USER_PRICE, sub_level_per_user_rate);
                    startActivity(planIntent);
                }
                break;
        }

    }

    public void getPurchasePlanDetails() {

        pdialog = new ProgressDialog(getActivity());
        pdialog.setMessage(getString(R.string.please_wait));
        pdialog.setCanceledOnTouchOutside(false);
        pdialog.show();

        JSONObject params = new JSONObject();
        try {
            params.put("user_id", USERID);
            params.put("session_token", TOKEN);
            params.put("plan_id", PLANID);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Logger.e(TAG, "Purchase Plan PARA---->" + String.valueOf(params));
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.POST, Utils.MAIN_URL + Utils.GET_PURCHASE_PLAN_DETAILS, params, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Logger.e(TAG, "URL---->" + Utils.MAIN_URL + Utils.GET_PURCHASE_PLAN_DETAILS);
                        Logger.e(TAG, "Purchase Plan RES---->" + String.valueOf(response));
                        try {
                            boolean success = response.getBoolean("success");
                            String msg = response.getString("message");
                            pdialog.dismiss();
                            if (success) {
                                JSONObject dataObject = response.getJSONObject("data");
                                String id = dataObject.getString("id");
                                planName = dataObject.getString("name");
                                String type = dataObject.getString("type");
                                String sub_type = dataObject.getString("sub_type");
                                planPrice = dataObject.getString("price");
                                String duration = dataObject.getString("duration");
                                String description = dataObject.getString("description");
                                String full_description = dataObject.getString("full_description");
                                String start_date = dataObject.getString("start_date");
                                String expire_date = dataObject.getString("expire_date");
                                String auto_renewal = dataObject.getString("auto_renewal");
                                String subscription_id = dataObject.getString("subscription_id");
                                sub_level_per_user_rate = dataObject.getString("sub_level_per_user_rate");
                                txtAmountPerUser.setText(sub_level_per_user_rate);
                                String default_user_count = dataObject.getString("default_user_count");
                                txtDefaultUser.setText(default_user_count);
                                String sub_level_user_count = dataObject.getString("sub_level_user_count");
                                txtPaidUser.setText(sub_level_user_count);
                                total_amount = dataObject.getString("total_amount");
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
        if (!Utils.validateString(edtSubUserNo.getText().toString().trim())) {
            flag = false;
            edtSubUserNo.requestFocus();
            showMessage("Please Enter Number Of Sub Level Users.");
        } else if (edtSubUserNo.getText().toString().trim().equalsIgnoreCase("0")) {
            flag = false;
            edtSubUserNo.requestFocus();
            showMessage("Please Enter Valid Number Of Sub Level Users.");
        }
        return flag;
    }

    public void showMessageDialog(Context context, String title, String message) {
        if (message != null && message.trim().length() > 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle(title);
            builder.setCancelable(false);
            builder.setMessage(message);
            builder.setPositiveButton("OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            NavigationDrawerActivity.fab.setVisibility(View.VISIBLE);
                            NavigationDrawerActivity.navItemIndex = 0;
                            NavigationDrawerActivity.selectMenu(0);
                            NavigationDrawerActivity.doubleBackToExitPressedOnce = true;
                            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new HomeFragment()).commit();
                            dialog.dismiss();
                        }
                    });
            // create alert dialog
            AlertDialog alertDialog = builder.create();
            // show it
            alertDialog.show();
        }
    }

    private void showMessage(String message) {
        Utils.showResponseMessage(getActivity(), rootLayout, message);
    }
}
