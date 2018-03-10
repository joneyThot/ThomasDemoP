package com.rogi.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

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
import com.rogi.View.TextViewPlus;
import com.rogi.View.Utils;
import com.rogi.logger.Logger;

import org.json.JSONException;
import org.json.JSONObject;


public class PaymentActivity extends AppCompatActivity implements View.OnClickListener {

    private String TAG = "PaymentActivity";
    private TextViewPlus payNowBtn, advancePlanPriceText, discountPriceText, netPriceText, applyPromoCodeText, validText;
    private EditText promoCodeEdt;
    double advancePrice, discountPrice, netPrice;
    String planId, planName;
    SharedPreferences mSharedPreferences;
    RequestQueue requestQueue;
    TextViewPlus titleText, planNameText;
    LinearLayout errorLayout;
    RelativeLayout rootView;
    ImageView removeErrorView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_view);
        requestQueue = Volley.newRequestQueue(this);
        mSharedPreferences = getSharedPreferences(Utils.PREF_NAME, MODE_PRIVATE);
        Intent intent = getIntent();
        advancePrice = Integer.parseInt(intent.getStringExtra(Utils.PLAN_PRICE));
        planId = intent.getStringExtra(Utils.PLAN_ID);
        planName = intent.getStringExtra(Utils.PLAN_NAME);
        Utils.storeString(mSharedPreferences, Utils.PLAN_ID, planId);
        Log.e("price ::", String.valueOf(advancePrice));
        init();

        setValue();
        getLocalIpAddress();
    }

    public void init() {
        findViewById(R.id.backLayoutclick).setOnClickListener(this);

        removeErrorView = (ImageView) findViewById(R.id.removeErrorView);
        errorLayout = (LinearLayout) findViewById(R.id.errorLayout);
        rootView = (RelativeLayout) findViewById(R.id.rootView);
        titleText = (TextViewPlus) findViewById(R.id.titleText);
        validText = (TextViewPlus) findViewById(R.id.validText);
        payNowBtn = (TextViewPlus) findViewById(R.id.payNowBtn);
        advancePlanPriceText = (TextViewPlus) findViewById(R.id.advancePlanPrice);
        discountPriceText = (TextViewPlus) findViewById(R.id.discountPrice);
        netPriceText = (TextViewPlus) findViewById(R.id.newPrice);
        applyPromoCodeText = (TextViewPlus) findViewById(R.id.applyPromoCodeText);
        promoCodeEdt = (EditText) findViewById(R.id.promoCodeEdt);
        planNameText = (TextViewPlus) findViewById(R.id.planNameText);

        titleText.setText("Payment");
        planNameText.setText(planName);
        payNowBtn.setOnClickListener(this);
        applyPromoCodeText.setOnClickListener(this);
        removeErrorView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.backLayoutclick:
                finish();
                break;
            case R.id.payNowBtn:
                Intent payIntent = new Intent(this, PaymentInfoActivity.class);
                startActivity(payIntent);
                if (advancePrice == netPrice) {
                    Utils.trialOccurrences = "0";
                    Utils.storeString(mSharedPreferences, Utils.PAYBLE_PRICE, String.format("%.2f", netPrice).toString());
                    Utils.storeString(mSharedPreferences, Utils.PLAN_TOTAL_PRICE, String.format("%.2f", advancePrice).toString());
                } else {
                    Utils.trialOccurrences = "1";
                    Utils.storeString(mSharedPreferences, Utils.PAYBLE_PRICE, String.format("%.2f", netPrice).toString());
                    Utils.storeString(mSharedPreferences, Utils.PLAN_TOTAL_PRICE, String.format("%.2f", advancePrice).toString());
                }
                break;
            case R.id.applyPromoCodeText:
                String promoCode = promoCodeEdt.getText().toString();
                if (!promoCode.equals("")) {
                    promoCodeRequest(promoCode);
                } else {
                    showMessage("Please Enter valid Promo code");
                }
                break;
            case R.id.removeErrorView:
                applyPromoCodeText.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                applyPromoCodeText.setText("Apply");
                promoCodeEdt.setText("");
                applyPromoCodeText.setEnabled(true);
                errorLayout.setVisibility(View.GONE);
                setValue();
                break;
        }
    }

    public void setValue() {
        advancePlanPriceText.setText(String.format("$ %.2f", advancePrice));
        discountPriceText.setText(String.format("$ %.2f", discountPrice));
        netPrice = advancePrice;
        netPriceText.setText(String.format("$ %.2f", netPrice));
    }

    public void setAPIValue(double price, double discount, double netPrice) {
        advancePlanPriceText.setText(String.format("$ %.2f", price));
        discountPriceText.setText(String.format("$ %.2f", discount));
        netPriceText.setText(String.format("$ %.2f", netPrice));
    }

    public void setStatusSuccess(String msg) {
        applyPromoCodeText.setBackgroundColor(getResources().getColor(R.color.disableColor));
        applyPromoCodeText.setText("Applied");
        applyPromoCodeText.setEnabled(false);
        validText.setTextColor(getResources().getColor(R.color.colorGreen));
        errorLayout.setVisibility(View.VISIBLE);
        validText.setText(msg);
    }

    public void setStatusError(String msg) {
        applyPromoCodeText.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        applyPromoCodeText.setText("Apply");
        errorLayout.setVisibility(View.VISIBLE);
        validText.setTextColor(getResources().getColor(R.color.highPriorityColor));
        validText.setText(msg);
    }

    public String getLocalIpAddress() {
        WifiManager wm = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        String ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
        Log.e("ip :::", ip);
        Utils.storeString(mSharedPreferences, Utils.DEVICE_IP, ip);
        return ip;
    }

    ProgressDialog pdialog;

    public void promoCodeRequest(String promoCode) {
        pdialog = new ProgressDialog(PaymentActivity.this);
        pdialog.setMessage(getString(R.string.please_wait));
        pdialog.setCanceledOnTouchOutside(false);
        pdialog.show();

        JSONObject params = new JSONObject();
        try {
            params.put("promo_code", promoCode);
            params.put("plan_id", planId);
            params.put("amount", advancePrice);
            params.put("type", "");

        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.e("Promocode PARA---->", String.valueOf(params));
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.POST, Utils.MAIN_URL + Utils.PLAN_APPLY_CODE_API, params, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Logger.e(TAG, "Promocode Res ::" + String.valueOf(response));
                        try {
                            pdialog.dismiss();
                            Utils.hideSoftKeyboard(PaymentActivity.this);
                            boolean success = response.getBoolean("success");
                            String msg = response.getString("message");
                            double price, discount;
                            if (success) {
                                JSONObject dataObject = response.getJSONObject("data");
                                price = Double.parseDouble(dataObject.getString("total_amt"));
                                discount = Double.parseDouble(dataObject.getString("discount_amt"));
                                netPrice = Double.parseDouble(dataObject.getString("net_amt"));
                                setAPIValue(price, discount, netPrice);
                                setStatusSuccess(msg);
                                Utils.storeString(mSharedPreferences, Utils.PROMOCODE, "Y");
                                Utils.storeString(mSharedPreferences, Utils.PROMOCODE_VALUE, promoCodeEdt.getText().toString().trim());
                            } else {
                                setStatusError(msg);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e("ERR", e.getMessage());
                            pdialog.dismiss();
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

    private void showMessage(String message) {
        Utils.showResponseMessage(this, rootView, message);
    }
}
