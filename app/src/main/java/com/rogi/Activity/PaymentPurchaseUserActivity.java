package com.rogi.Activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
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

/**
 * Created by "Mayuri" on 24/8/17.
 */
public class PaymentPurchaseUserActivity extends AppCompatActivity implements View.OnClickListener {

    private String TAG = "PaymentPurchaseUserActivity";
    private TextViewPlus payNowBtn, advancePlanPriceText, discountPriceText, netPriceText, applyPromoCodeText, validText;
    private EditText promoCodeEdt;
    double advancePrice, discountPrice, netPrice;
    String planId, planName, totalAmount = "", enterSubUserCount = "", auto_renewal = "", perUserRate = "";
    SharedPreferences mSharedPreferences;
    RequestQueue requestQueue;
    TextViewPlus titleText, planNameText, txtPlanTitle;
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
//        advancePrice = Integer.parseInt(intent.getStringExtra(Utils.PLAN_PRICE));
        planId = intent.getStringExtra(Utils.PLAN_ID);
        planName = intent.getStringExtra(Utils.PLAN_NAME);
        totalAmount = intent.getStringExtra(Utils.TOTAL_AMOUNT);
        advancePrice = Integer.parseInt(intent.getStringExtra(Utils.SUB_USER_TOTAL_AMOUNT));
        enterSubUserCount = intent.getStringExtra(Utils.ENTER_SUB_USER);
        perUserRate = intent.getStringExtra(Utils.PER_USER_PRICE);
        Utils.storeString(mSharedPreferences, Utils.PLAN_ID, planId);
        auto_renewal = mSharedPreferences.getString(Utils.AUTO_RENEWAL, "");
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
        txtPlanTitle = (TextViewPlus) findViewById(R.id.txtPlanTitle);
        txtPlanTitle.setText("Payment of sub level users");

        titleText.setText("Payment");
        planNameText.setText("Amount");
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
                String msg = "";
//                if (auto_renewal.equalsIgnoreCase("Y")) {
//                    msg = "You will be paying $ " + netPrice + " to buy " + enterSubUserCount + " new users." +
//                            "Your current monthly subscription amount is : $ " + Double.parseDouble(totalAmount) + ". From now it will be : $ " +
//                            Double.parseDouble(totalAmount) + " + " + advancePrice + " = $ " + (Double.parseDouble(totalAmount) + advancePrice) + ".";
                msg = "You will be paying $ " + String.format("%.2f", netPrice) + " to buy " + enterSubUserCount + " new users. This amount will be inclueded in current subscription.";
                showPaymnetAmountDialog(this, "Rogi", msg);
//                } else {
//                    msg = "You will be paying $ " + netPrice + " to buy " + enterSubUserCount + " new users.";
//                    showPaymnetAmountDialog(this, "Rogi", msg);
//                }

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
        pdialog = new ProgressDialog(PaymentPurchaseUserActivity.this);
        pdialog.setMessage(getString(R.string.please_wait));
        pdialog.setCanceledOnTouchOutside(false);
        pdialog.show();

        JSONObject params = new JSONObject();
        try {
            params.put("promo_code", promoCode);
            params.put("plan_id", planId);
            params.put("amount", advancePrice);
            params.put("type", "sub_level");

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
                            Utils.hideSoftKeyboard(PaymentPurchaseUserActivity.this);
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

    public void showPaymnetAmountDialog(final Context context, String title, String message) {
        if (message != null && message.trim().length() > 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle(title);
            builder.setCancelable(false);
            builder.setMessage(message);
            builder.setPositiveButton("Yes",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
//                            int advancPrice = (int) advancePrice;
                            double updateSubscriptiontotal = Double.parseDouble(totalAmount) + advancePrice;
                            Intent planIntent = new Intent(PaymentPurchaseUserActivity.this, PaymentPurchaseUserInfoActivity.class);
                            planIntent.putExtra(Utils.ENTER_SUB_USER, enterSubUserCount);
                            planIntent.putExtra(Utils.PER_USER_PRICE, perUserRate);
                            planIntent.putExtra(Utils.SUB_USER_TOTAL_AMOUNT, String.format("%.2f", updateSubscriptiontotal).toString());
                            Utils.storeString(mSharedPreferences, Utils.PAYBLE_PRICE, String.format("%.2f", netPrice).toString());
                            startActivity(planIntent);
                        }
                    });

            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
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
        Utils.showResponseMessage(this, rootView, message);
    }
}
