package com.rogi.Activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;
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
import com.rogi.Fragment.Month_YearDialogFragment;
import com.rogi.R;
import com.rogi.Service.AsyncCallListener;
import com.rogi.Service.PaymentRequestTask;
import com.rogi.View.TextViewPlus;
import com.rogi.View.Utils;
import com.rogi.logger.Logger;

import net.authorize.acceptsdk.AcceptSDKApiClient;
import net.authorize.acceptsdk.ValidationManager;
import net.authorize.acceptsdk.datamodel.common.Message;
import net.authorize.acceptsdk.datamodel.merchant.ClientKeyBasedMerchantAuthentication;
import net.authorize.acceptsdk.datamodel.transaction.CardData;
import net.authorize.acceptsdk.datamodel.transaction.EncryptTransactionObject;
import net.authorize.acceptsdk.datamodel.transaction.TransactionObject;
import net.authorize.acceptsdk.datamodel.transaction.TransactionType;
import net.authorize.acceptsdk.datamodel.transaction.callbacks.EncryptTransactionCallback;
import net.authorize.acceptsdk.datamodel.transaction.response.EncryptTransactionResponse;
import net.authorize.acceptsdk.datamodel.transaction.response.ErrorTransactionResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class PaymentInfoActivity extends AppCompatActivity implements View.OnClickListener, EncryptTransactionCallback {

    public static final String TAG = "PaymentInfoActivity";
    private final String CLIENT_KEY = Utils.PUBLIC_KEY_SEC;

    private final String API_LOGIN_ID = Utils.API_LOGIN_ID;
    String USERID, TOKEN, PLAN_ID, PLAN_TYPE, AUTO_RENEWAL;

    private final int MIN_CARD_NUMBER_LENGTH = 13;
    private final int MIN_YEAR_LENGTH = 4;
    private final int MIN_MONTH_LENGTH = 2;
    private final int MIN_CVV_LENGTH = 3;

    private TextView checkoutButton, txtAuthorizeDes;
    private EditText cardNumberView;
    private EditText monthView;
    private EditText yearView;
    private EditText cvvView;
    TextViewPlus titleText;
    private ProgressDialog progressDialog;
    CheckBox checkSubscription;

    private String cardNumber;
    private String month;
    private String year;
    private String cvv;
    int currentYear;
    RequestQueue requestQueue;

    private AcceptSDKApiClient apiClient;

    String firstName, lastName, phone, email;
    String discountAmount = "", totalAmount = "", deviceIp = "", currentDate = "", unselectedIds = "", planPrice = "",
            subLevelUserCount = "", subLevelUserAmount = "";
    private SharedPreferences mSharedPreferences;
    Month_YearDialogFragment dialogFragment;
    RelativeLayout mainPaymentLayout;
    ProgressDialog pdialog;
    String transcationID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_info_view);
        requestQueue = Volley.newRequestQueue(this);
        mSharedPreferences = getSharedPreferences(Utils.PREF_NAME, MODE_PRIVATE);

        USERID = mSharedPreferences.getString(Utils.USER_ID, "");
        TOKEN = mSharedPreferences.getString(Utils.TOKEN, "");
        PLAN_ID = mSharedPreferences.getString(Utils.PLAN_ID, "");
        PLAN_TYPE = mSharedPreferences.getString(Utils.PLAN_TYPE, "");
        AUTO_RENEWAL = mSharedPreferences.getString(Utils.AUTO_RENEWAL, "");
        unselectedIds = mSharedPreferences.getString(Utils.EXITING_USER_IDS, "");

        firstName = mSharedPreferences.getString(Utils.FIRST_NAME, "");
        lastName = mSharedPreferences.getString(Utils.LAST_NAME, "");
        phone = mSharedPreferences.getString(Utils.PHONE_NUMBER, "");
        email = mSharedPreferences.getString(Utils.EMAIL, "");
        discountAmount = mSharedPreferences.getString(Utils.PAYBLE_PRICE, "");
        totalAmount = mSharedPreferences.getString(Utils.PLAN_TOTAL_PRICE, "");
        planPrice = mSharedPreferences.getString(Utils.PLAN_PRICE, "");
        deviceIp = mSharedPreferences.getString(Utils.DEVICE_IP, "");
        subLevelUserCount = mSharedPreferences.getString(Utils.SUB_LEVEL_USER_COUNT, "");
        subLevelUserAmount = mSharedPreferences.getString(Utils.SUB_LEVEL_USER_AMOUNT, "");

        Calendar calendar = Calendar.getInstance();
        currentYear = calendar.get(Calendar.YEAR);

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        currentDate = df.format(calendar.getTime());

        Logger.e("Payment::::", currentDate);

        try {
            apiClient = new AcceptSDKApiClient.Builder(getApplicationContext(),
                    AcceptSDKApiClient.Environment.SANDBOX).connectionTimeout(
                    4000) // optional connection time out in milliseconds
                    .build();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        init();

/*
        cardNumberView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (ValidationManager.isValidCardNumber(cardNumber)) {
                    if (!isEmptyField()) {
                        checkoutButton.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
*/
    }

    String subscriptionValue = "9999";

    public void init() {
        mainPaymentLayout = (RelativeLayout) findViewById(R.id.mainPaymentLayout);
        findViewById(R.id.backLayoutclick).setOnClickListener(this);
        titleText = (TextViewPlus) findViewById(R.id.titleText);
        cardNumberView = (EditText) findViewById(R.id.card_number_view);
        // setUpCreditCardEditText();
        monthView = (EditText) findViewById(R.id.date_month_view);
        yearView = (EditText) findViewById(R.id.date_year_view);
        cvvView = (EditText) findViewById(R.id.security_code_view);
        checkoutButton = (TextView) findViewById(R.id.button_checkout_order);
        checkSubscription = (CheckBox) findViewById(R.id.checkSubscription);
        checkSubscription.setVisibility(View.GONE);

        txtAuthorizeDes = (TextView) findViewById(R.id.txtAuthorizeDes);
        if (Utils.validateString(mSharedPreferences.getString(Utils.AUTHORIZE_TEXT, ""))) {
            txtAuthorizeDes.setVisibility(View.VISIBLE);
            txtAuthorizeDes.setText(mSharedPreferences.getString(Utils.AUTHORIZE_TEXT, ""));
        } else {
            txtAuthorizeDes.setVisibility(View.GONE);
        }

        titleText.setText("Payment Information");
        checkoutButton.setOnClickListener(this);
        monthView.setOnClickListener(this);
        yearView.setOnClickListener(this);
        checkSubscription.setChecked(true);
        subscriptionValue = "9999";

        /*checkSubscription.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    subscriptionValue = "9999";
                } else {
                    subscriptionValue = "1";
                }
            }
        });*/
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.button_checkout_order:
                if (!areFormDetailsValid())
                    return;

                progressDialog = ProgressDialog.show(this, "Please Wait",
                        "Fetching token....", true);

                try {
//                    EncryptTransactionObject transactionObject = prepareTransactionObject();
//                    apiClient.getTokenWithRequest(transactionObject, this);
                    paymentXmlRequest();
//                    paymentFormat();
                } catch (NullPointerException e) {
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
                    if (progressDialog.isShowing()) progressDialog.dismiss();
                    e.printStackTrace();
                }
                break;
            case R.id.backLayoutclick:
                clearValue();
                finish();
                break;
            case R.id.date_month_view:
                monthValues();
                break;
            case R.id.date_year_view:
                yearValues();
                break;
        }
    }

    public void clearValue() {
        Utils.storeString(mSharedPreferences, Utils.MONTH_POS, "");
        Utils.storeString(mSharedPreferences, Utils.YEAR_POS, "");
        Utils.storeString(mSharedPreferences, Utils.MONTH_YEAR_TYPE, "");
    }

    View.OnClickListener monthListner = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int index = (int) v.getTag();
            Log.d("INDEX", "" + index);
            dismissDialog();
            Utils.storeString(mSharedPreferences, Utils.MONTH_POS, String.valueOf(index));
            String month = monthItems.get(index);
            monthView.setText(month);
        }
    };

    View.OnClickListener yearListner = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int index = (int) v.getTag();
            Log.d("INDEX", "" + index);
            dismissDialog();
            Utils.storeString(mSharedPreferences, Utils.YEAR_POS, String.valueOf(index));
            String year = yearItems.get(index);
            yearView.setText(year);
        }
    };

    List<String> monthItems = new ArrayList<>();
    List<String> yearItems = new ArrayList<>();

    public void monthValues() {
        monthItems = new ArrayList<>();
        for (int i = 1; i <= 12; i++) {
            String month = String.valueOf(i);
            if (month.length() < 1)
                monthItems.add("0" + month);
            else
                monthItems.add(month);
        }
        Utils.storeString(mSharedPreferences, Utils.MONTH_YEAR_TYPE, Utils.MONTH_TYPE);
        FragmentManager manager1 = this.getSupportFragmentManager();
        Month_YearDialogFragment dialogFragment = new Month_YearDialogFragment("Month", monthItems, monthListner, yearListner);
        dialogFragment.show(manager1, "dialog");
    }

    public void yearValues() {
        yearItems.clear();
        int year = currentYear;
        for (int i = 0; i <= 30; i++) {
            int tempYear = year;
            year++;
            String Y = String.valueOf(tempYear);
            yearItems.add(Y);
        }
        Utils.storeString(mSharedPreferences, Utils.MONTH_YEAR_TYPE, Utils.YEAR_TYPE);
        FragmentManager manager1 = this.getSupportFragmentManager();
        Month_YearDialogFragment dialogFragment = new Month_YearDialogFragment("Year", yearItems, monthListner, yearListner);
        dialogFragment.show(manager1, "dialog");
    }

    public void dismissDialog() {
        Fragment prev = getSupportFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            DialogFragment df = (DialogFragment) prev;
            df.dismiss();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        clearValue();
    }

    /*public void paymentFormat() {
        try {

            if (Utils.trialOccurrences.equalsIgnoreCase(subscriptionValue)) {
                totalAmount = discountAmount;
                Utils.trialOccurrences = "0";
            }

            JSONObject mainRequestObject = new JSONObject();

            JSONObject authObject = new JSONObject();
            authObject.put("name", Utils.API_LOGIN_ID);
            authObject.put("transactionKey", Utils.TRANSACTION_KEY);

            mainRequestObject.put("merchantAuthentication", authObject);
            mainRequestObject.put("refId", "");

            JSONObject requestObject = new JSONObject();
            requestObject.put("name", PLAN_TYPE);


            JSONObject paymentObject = new JSONObject();
            JSONObject dataObject = new JSONObject();
            dataObject.put("length", "1");
            dataObject.put("unit", "months");
            paymentObject.put("interval", dataObject);
            paymentObject.put("startDate", currentDate);

            paymentObject.put("totalOccurrences", subscriptionValue);
            paymentObject.put("trialOccurrences", Utils.trialOccurrences);

            requestObject.put("paymentSchedule", paymentObject);
            requestObject.put("amount", totalAmount);
            requestObject.put("trialAmount", discountAmount);

            *//*JSONObject pObj = new JSONObject();
            JSONObject dObject = new JSONObject();
            dObject.put("dataDescriptor", "COMMON.ACCEPT.INAPP.PAYMENT");
            dObject.put("dataValue", paymentToken);
            pObj.put("opaqueData", dObject);
            requestObject.put("payment", pObj);*//*

            JSONObject pObj = new JSONObject();
            JSONObject dObject = new JSONObject();
            dObject.put("cardNumber", cardNumberView.getText().toString().trim());
            dObject.put("expirationDate", monthView.getText().toString().trim() + yearView.getText().toString().trim());
            dObject.put("cardCode", cvvView.getText().toString().trim());
            pObj.put("creditCard", dObject);
            requestObject.put("payment", pObj);

            JSONObject customerObject = new JSONObject();
            customerObject.put("email", email);
            customerObject.put("phoneNumber", phone);

            requestObject.put("customer", customerObject);

            JSONObject billObject = new JSONObject();
            billObject.put("firstName", firstName);
            billObject.put("lastName", lastName);


            requestObject.put("billTo", billObject);

            mainRequestObject.put("subscription", requestObject);

            Log.e("FinalRequest Object :::", String.valueOf(mainRequestObject));
            if (progressDialog.isShowing()) progressDialog.dismiss();
            paymentRequest(mainRequestObject);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }*/


    /*public void createPaymentFormat() {
        try {
            JSONObject mainRequestObject = new JSONObject();

            JSONObject authObject = new JSONObject();
            authObject.put("name", Utils.API_LOGIN_ID);
            authObject.put("transactionKey", Utils.TRANSACTION_KEY);

            mainRequestObject.put("merchantAuthentication", authObject);
            mainRequestObject.put("refId", "");

            JSONObject requestObject = new JSONObject();
            requestObject.put("transactionType", "authCaptureTransaction");
            requestObject.put("amount", amount);

            JSONObject paymentObject = new JSONObject();
            JSONObject dataObject = new JSONObject();
            dataObject.put("dataDescriptor", "COMMON.ACCEPT.INAPP.PAYMENT");
            dataObject.put("dataValue", paymentToken);
            paymentObject.put("opaqueData", dataObject);

            requestObject.put("payment", paymentObject);

            JSONObject customerObject = new JSONObject();
            customerObject.put("email", email);
            customerObject.put("phoneNumber", phone);

            requestObject.put("customer", customerObject);

            JSONObject billObject = new JSONObject();
            billObject.put("firstName", firstName);
            billObject.put("lastName", lastName);


            requestObject.put("billTo", billObject);

            mainRequestObject.put("subscription", requestObject);

            Log.e("FinalRequest Object :::", String.valueOf(mainRequestObject));

            //paymentRequest(mainRequestObject);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }*/

    /*public void paymentRequest(JSONObject mainObject) {
        pdialog = new ProgressDialog(PaymentInfoActivity.this);
        pdialog.setMessage(getString(R.string.payment_process));
        pdialog.setCanceledOnTouchOutside(false);
        pdialog.show();

        JSONObject params = new JSONObject();
        try {
            params.put("ARBCreateSubscriptionRequest", mainObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.e("Payment PARA---->", String.valueOf(params));

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.POST, Utils.AUTHORIZED_PAYMENT_API, params, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Logger.e(TAG, "Payment ::" + String.valueOf(response));
                        try {
                            JSONObject messagesObj = response.getJSONObject("messages");

                            String result = messagesObj.getString("resultCode");

                            if (result.equals("Ok")) {
                                transcationID = response.getString("subscriptionId");
                                addPlanRequest(transcationID);
                            } else {
                                pdialog.dismiss();
                                JSONArray msgArray = messagesObj.getJSONArray("message");
                                for (int i = 0; i < msgArray.length(); i++) {
                                    JSONObject object = msgArray.getJSONObject(i);
                                    Utils.showMessageDialog(PaymentInfoActivity.this, "Rogi", object.get("text").toString());
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
                        } else {
                            Logger.e(TAG, "Error:" + error.toString());
                        }
                        pdialog.dismiss();
                    }
                });
        requestQueue.add(jsObjRequest);
    }*/

    public void paymentXmlRequest() {
        pdialog = new ProgressDialog(PaymentInfoActivity.this);
        pdialog.setMessage(getString(R.string.please_wait));
        pdialog.setCanceledOnTouchOutside(false);
        pdialog.show();

        if (Utils.trialOccurrences.equalsIgnoreCase(subscriptionValue)) {
            totalAmount = discountAmount;
            Utils.trialOccurrences = "0";
        }

        String xmlString = "<ARBCreateSubscriptionRequest xmlns=\"AnetApi/xml/v1/schema/AnetApiSchema.xsd\">" +
                "<merchantAuthentication><name>" + Utils.API_LOGIN_ID + "</name><transactionKey>" + Utils.TRANSACTION_KEY +
                "</transactionKey></merchantAuthentication><refId></refId><subscription><name>" + PLAN_TYPE +
                "</name><paymentSchedule><interval><length>1</length><unit>months</unit></interval><startDate>" + currentDate +
                "</startDate><totalOccurrences>" + subscriptionValue + "</totalOccurrences><trialOccurrences>" + Utils.trialOccurrences +
                "</trialOccurrences></paymentSchedule><amount>" + totalAmount + "</amount><trialAmount>" + discountAmount +
                "</trialAmount><payment><creditCard><cardNumber>" + cardNumberView.getText().toString().trim() + "</cardNumber>" +
                "<expirationDate>" + monthView.getText().toString().trim() + yearView.getText().toString().trim() + "</expirationDate>" +
                "<cardCode>" + cvvView.getText().toString().trim() + "</cardCode></creditCard></payment><billTo><firstName>" +
                firstName + "</firstName><lastName>" + lastName + "</lastName></billTo></subscription></ARBCreateSubscriptionRequest>";
        Logger.e(TAG, "Xml String :: " + xmlString);
        if (progressDialog.isShowing()) progressDialog.dismiss();
        paymentStringRequest(xmlString);

    }

    public void paymentStringRequest(String str) {
        if (Utils.checkInternetConnection(this)) {
            PaymentRequestTask paymentRequestTask = new PaymentRequestTask(this);
            paymentRequestTask.setAsyncCallListener(new AsyncCallListener() {

                @Override
                public void onResponseReceived(Object response) {
                }

                @Override
                public void onResponseReceived(JSONObject response) {
                    Logger.e(TAG, "Response ::" + response.toString());
                    try {
                        JSONObject ARBCreateObj = response.getJSONObject("ARBCreateSubscriptionResponse");
                        JSONObject messagesObj = ARBCreateObj.getJSONObject("messages");

                        String result = messagesObj.getString("resultCode");

                        if (result.equals("Ok")) {
                            transcationID = ARBCreateObj.getString("subscriptionId");
                            addPlanRequest(transcationID);
                        } else {
                            if (pdialog.isShowing()) pdialog.dismiss();
                            JSONObject msgObj = messagesObj.getJSONObject("message");
                            Utils.showMessageDialog(PaymentInfoActivity.this, "Rogi", msgObj.get("text").toString());
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e("ERR", e.getMessage());
                        if (pdialog.isShowing()) pdialog.dismiss();
                    }


                }

                @Override
                public void onErrorReceived(String error) {
                    if (pdialog.isShowing()) pdialog.dismiss();
                }
            });
            paymentRequestTask.execute(str);
        }
    }

    public void addPlanRequest(String transcationId) {
//        pdialog = new ProgressDialog(PaymentInfoActivity.this);
//        pdialog.setMessage(getString(R.string.please_wait));
//        pdialog.setCanceledOnTouchOutside(false);
//        pdialog.show();

        JSONObject params = new JSONObject();
        try {
            params.put("user_id", USERID);
            params.put("session_token", TOKEN);
            params.put("subscription_id", transcationId);
            params.put("plan_id", PLAN_ID);
            if (subscriptionValue.equals("1")) {
                params.put("auto_renewal", "N");
            } else {
                params.put("auto_renewal", "Y");
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        Logger.e(TAG, "Add Plan PARA----> " + String.valueOf(params));
        Logger.e(TAG, "URL :: " + Utils.MAIN_URL + Utils.PLAN_ADD_API);
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.POST, Utils.MAIN_URL + Utils.PLAN_ADD_API, params, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Logger.e(TAG, "Add Plan Res ::" + String.valueOf(response));
                        try {
                            Utils.hideSoftKeyboard(PaymentInfoActivity.this);
                            boolean success = response.getBoolean("success");
                            String msg = response.getString("message");
                            if (success) {
                                if (!response.isNull("data")) {
                                    JSONObject jObj = response.getJSONObject("data");
                                    String autoRenewal = jObj.getString("auto_renewal");
                                    String subscription_id = jObj.getString("subscription_id");
                                    Utils.storeString(mSharedPreferences, Utils.AUTO_RENEWAL, autoRenewal);
                                    Utils.storeString(mSharedPreferences, Utils.SUBSCRIPTION_ID, subscription_id);
                                }
                                if (Utils.validateString(mSharedPreferences.getString(Utils.EXITING_USER_SCREEN, ""))) {
                                    JSONArray unSelectedArray = new JSONArray();
                                    unSelectedArray.put(unselectedIds);
                                    UpdateSubLevelUsers(unSelectedArray);
                                } else {
                                    if (pdialog.isShowing()) pdialog.dismiss();
                                    showPaymentSuccessDialog(PaymentInfoActivity.this, "Rogi", getResources().getString(R.string.payment_success));
                                }
                            } else {
                                if (pdialog.isShowing()) pdialog.dismiss();
                                Utils.storeString(mSharedPreferences, Utils.AUTO_RENEWAL, "");
                                Utils.storeString(mSharedPreferences, Utils.SUBSCRIPTION_ID, "");
                                showMessageDialog(PaymentInfoActivity.this, "Rogi", getResources().getString(R.string.payment_api_falied));
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e("ERR", e.getMessage());
                            if (pdialog.isShowing()) pdialog.dismiss();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error instanceof NoConnectionError) {
                        }
                        if (pdialog.isShowing()) pdialog.dismiss();
                        showMessageDialog(PaymentInfoActivity.this, "Rogi", getResources().getString(R.string.payment_api_falied));
                    }
                });
        int socketTimeout = 30000;//30 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsObjRequest.setRetryPolicy(policy);
        requestQueue.add(jsObjRequest);
    }

    public void UpdateSubLevelUsers(JSONArray jsonArray) {

        JSONObject params = new JSONObject();
        try {
            params.put("user_id", USERID);
            params.put("session_token", TOKEN);
            params.put("plan_id", PLAN_ID);
            params.put("sub_level_user_count", subLevelUserCount);
            params.put("plan_amount", planPrice);
            params.put("sub_level_user_amount", subLevelUserAmount);
            params.put("total_amount", totalAmount);
            if (mSharedPreferences.getString(Utils.PROMOCODE, "").equalsIgnoreCase("Y")) {
                params.put("promocode", "Y");
                params.put("promocode_value", mSharedPreferences.getString(Utils.PROMOCODE_VALUE, ""));
            } else {
                params.put("promocode", "N");
                params.put("promocode_value", "");
            }

            params.put("sub_level_user_net_amt", discountAmount);
            params.put("deactivate_users_ids", jsonArray);


        } catch (JSONException e) {
            e.printStackTrace();
        }
        Logger.e(TAG, "Update Sub Level User PARA---->" + String.valueOf(params));
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.POST, Utils.MAIN_URL + Utils.UPDATE_SUB_LEVEL_USERS, params, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Logger.e(TAG, "Update Sub Level User ::" + String.valueOf(response));
                        try {
                            if (pdialog.isShowing()) pdialog.dismiss();
                            Utils.hideSoftKeyboard(PaymentInfoActivity.this);
                            boolean success = response.getBoolean("success");
                            String msg = response.getString("message");
                            if (success) {
                                showPaymentSuccessDialog(PaymentInfoActivity.this, "Rogi", getResources().getString(R.string.payment_success));
                            } else {
                                showMessageDialog(PaymentInfoActivity.this, "Rogi", getResources().getString(R.string.payment_api_falied));
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e("ERR", e.getMessage());
                            if (pdialog.isShowing()) pdialog.dismiss();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error instanceof NoConnectionError) {
                        }
                        if (pdialog.isShowing()) pdialog.dismiss();
                        showMessageDialog(PaymentInfoActivity.this, "Rogi", getResources().getString(R.string.payment_api_falied));
                    }
                });
        int socketTimeout = 30000;//30 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsObjRequest.setRetryPolicy(policy);
        requestQueue.add(jsObjRequest);
    }


    public void showPaymentSuccessDialog(final Context context, String title, String message) {
        if (message != null && message.trim().length() > 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle(title);
            builder.setCancelable(false);
            builder.setMessage(message);
            builder.setPositiveButton("OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                            clearValue();

                            Utils.storeString(mSharedPreferences, Utils.PLAN_PRICE, "");
                            Utils.storeString(mSharedPreferences, Utils.EXITING_USER_IDS, "");
                            Utils.storeString(mSharedPreferences, Utils.EXITING_USER_SCREEN, "");
                            Utils.storeString(mSharedPreferences, Utils.SUB_LEVEL_USER_COUNT, "");
                            Utils.storeString(mSharedPreferences, Utils.SUB_LEVEL_USER_AMOUNT, "");
                            Utils.storeString(mSharedPreferences, Utils.PAYBLE_PRICE, "");
                            Utils.storeString(mSharedPreferences, Utils.PLAN_TOTAL_PRICE, "");
                            Utils.storeString(mSharedPreferences, Utils.PROMOCODE, "");
                            Utils.storeString(mSharedPreferences, Utils.PROMOCODE_VALUE, "");

                            Intent intent = new Intent(context, NavigationDrawerActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent);
                            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
                        }
                    });
            // create alert dialog
            AlertDialog alertDialog = builder.create();
            // show it
            alertDialog.show();
        }
    }


    /*-----------------------Put id & client key---------------------------*/

    private EncryptTransactionObject prepareTransactionObject() {
        ClientKeyBasedMerchantAuthentication merchantAuthentication =
                ClientKeyBasedMerchantAuthentication.
                        createMerchantAuthentication(API_LOGIN_ID, CLIENT_KEY);

        // create a transaction object by calling the predefined api for creation
        return TransactionObject.
                createTransactionObject(
                        TransactionType.SDK_TRANSACTION_ENCRYPTION) // type of transaction object
                .cardData(prepareCardDataFromFields()) // card data to get Token
                .merchantAuthentication(merchantAuthentication).build();
    }

    /* ---------------------- Callback Methods - Start -----------------------*/

    String paymentToken;

    @Override
    public void onEncryptionFinished(EncryptTransactionResponse response) {
        hideSoftKeyboard();

        if (progressDialog.isShowing())
            progressDialog.dismiss();

        paymentToken = response.getDataValue();
        Log.e("responseValue :::", response.getDataDescriptor());
        Log.e("Value :::", paymentToken);
        if (paymentToken.equals("")) {
            showMessageDialog(PaymentInfoActivity.this, "Rogi", getResources().getString(R.string.unable_get_token));
        } else {
//            paymentFormat();
        }
    }

    @Override
    public void onErrorReceived(ErrorTransactionResponse errorResponse) {
        hideSoftKeyboard();
        if (progressDialog.isShowing()) progressDialog.dismiss();
        Message error = errorResponse.getFirstErrorMessage();
        Utils.showMessageDialog(PaymentInfoActivity.this, "Rogi", error.getMessageCode() + "Message :::" + error.getMessageText());
        Log.e("CodeValue :::", error.getMessageCode() + "Message :::" + error.getMessageText());
    }

     /* ---------------------- Callback Methods - End -----------------------*/

    public void hideSoftKeyboard() {
        InputMethodManager keyboard =
                (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        keyboard.hideSoftInputFromInputMethod(getCurrentFocus().getWindowToken(), 0);
    }

    private boolean areFormDetailsValid() {
        cardNumber = cardNumberView.getText().toString().replace(" ", "");
        month = monthView.getText().toString();
        cvv = cvvView.getText().toString().trim();
        year = yearView.getText().toString();

        if (isEmptyField()) {
            checkoutButton.startAnimation(AnimationUtils.loadAnimation(this, R.anim.shake_error));
            showMessage(getString(R.string.empty_all_field));
            // Toast.makeText(this, "Empty fields", Toast.LENGTH_LONG).show();
            return false;
        }

        year = yearView.getText().toString();
        return validateFields();
    }

    private boolean isEmptyField() {
        return (cardNumber != null && cardNumber.isEmpty()) || (month != null && month.isEmpty()) || (year != null
                && year.isEmpty()) || (cvv != null && cvv.isEmpty());
    }

    private boolean validateFields() {
        if (!ValidationManager.isValidCardNumber(cardNumber)) {
            cardNumberView.requestFocus();
            showMessage(getString(R.string.invalid_card_number));
            checkoutButton.startAnimation(
                    AnimationUtils.loadAnimation(this, R.anim.shake_error));
            return false;
        }

        if (!ValidationManager.isValidExpirationDate(month, year)) {
            showMessage(getString(R.string.invalid_date));
            checkoutButton.startAnimation(
                    AnimationUtils.loadAnimation(this, R.anim.shake_error));
            return false;
        }

        if (!ValidationManager.isValidCVV(cvv)) {
            cvvView.requestFocus();
            showMessage(getString(R.string.invalid_cvv));
            checkoutButton.startAnimation(
                    AnimationUtils.loadAnimation(this, R.anim.shake_error));
            return false;
        }
//        checkoutButton.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        return true;
    }

    private void showMessage(String message) {
        Utils.showResponseMessage(this, mainPaymentLayout, message);
    }

    private void setUpCreditCardEditText() {
        cardNumberView.addTextChangedListener(new TextWatcher() {
            private boolean spaceDeleted;

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // check if a space was deleted
                CharSequence charDeleted = s.subSequence(start, start + count);
                spaceDeleted = " ".equals(charDeleted.toString());
            }

            public void afterTextChanged(Editable editable) {
                // disable text watcher
                cardNumberView.removeTextChangedListener(this);

                // record cursor position as setting the text in the textview
                // places the cursor at the end
                int cursorPosition = cardNumberView.getSelectionStart();
                String withSpaces = formatText(editable);
                cardNumberView.setText(withSpaces);
                // set the cursor at the last position + the spaces added since the
                // space are always added before the cursor
                cardNumberView.setSelection(cursorPosition + (withSpaces.length() - editable.length()));

                // if a space was deleted also deleted just move the cursor
                // before the space
                if (spaceDeleted) {
                    cardNumberView.setSelection(cardNumberView.getSelectionStart() - 1);
                    spaceDeleted = false;
                }

                // enable text watcher
                cardNumberView.addTextChangedListener(this);
            }

            private String formatText(CharSequence text) {
                StringBuilder formatted = new StringBuilder();
                int count = 0;
                for (int i = 0; i < text.length(); ++i) {
                    if (Character.isDigit(text.charAt(i))) {
                        if (count % 4 == 0 && count > 0) formatted.append(" ");
                        formatted.append(text.charAt(i));
                        ++count;
                    }
                }
                return formatted.toString();
            }
        });
    }

    private CardData prepareCardDataFromFields() {
        return new CardData.Builder(cardNumber, month, year).cvvCode(cvv) //CVV Code is optional
                .build();
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
                            dialog.dismiss();
                        }
                    });
            // create alert dialog
            AlertDialog alertDialog = builder.create();
            // show it
            alertDialog.show();
        }
    }
}