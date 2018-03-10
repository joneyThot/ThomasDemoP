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

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class PaymentPurchaseUserInfoActivity extends AppCompatActivity implements View.OnClickListener, EncryptTransactionCallback {

    public static final String TAG = "PaymentPurchaseUserInfoActivity";
    private final String CLIENT_KEY = Utils.PUBLIC_KEY_SEC;

    private final String API_LOGIN_ID = Utils.API_LOGIN_ID;
    String USERID, TOKEN, PLAN_ID, PLAN_TYPE;

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

    String firstName = "", lastName = "", phone = "", email = "", subscription_id = "", updateSubScriptionAmount = "",
            auto_renewal = "", number_of_users = "", per_user_price = "";
    String amount, deviceIp, currentDate;
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

        firstName = mSharedPreferences.getString(Utils.FIRST_NAME, "");
        lastName = mSharedPreferences.getString(Utils.LAST_NAME, "");
        phone = mSharedPreferences.getString(Utils.PHONE_NUMBER, "");
        email = mSharedPreferences.getString(Utils.EMAIL, "");
        amount = mSharedPreferences.getString(Utils.PAYBLE_PRICE, "");
        deviceIp = mSharedPreferences.getString(Utils.DEVICE_IP, "");
        subscription_id = mSharedPreferences.getString(Utils.SUBSCRIPTION_ID, "");
        auto_renewal = mSharedPreferences.getString(Utils.AUTO_RENEWAL, "");

        Intent intent = getIntent();
        number_of_users = intent.getStringExtra(Utils.ENTER_SUB_USER);
        per_user_price = intent.getStringExtra(Utils.PER_USER_PRICE);
        updateSubScriptionAmount = intent.getStringExtra(Utils.SUB_USER_TOTAL_AMOUNT);


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

    String subscriptionValue = "1";

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
        subscriptionValue = "12";

        /*checkSubscription.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    subscriptionValue = "12";
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

//                progressDialog = ProgressDialog.show(PaymentPurchaseUserInfoActivity.this, "Rogi",
//                        "Fetching token....", true);

                try {
//                    EncryptTransactionObject transactionObject = prepareTransactionObject();
//                    apiClient.getTokenWithRequest(transactionObject, this);
//                    realTimeRequest();
                    realTimeXMLRequest();
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

    /*public void realTimeRequest() {
        try {
            JSONObject createTransactionRequestObj = new JSONObject();

            JSONObject authObj = new JSONObject();
            authObj.put("name", Utils.API_LOGIN_ID);
            authObj.put("transactionKey", Utils.TRANSACTION_KEY);
            createTransactionRequestObj.put("merchantAuthentication", authObj);
            createTransactionRequestObj.put("refId", "");

            JSONObject transactionRequestObj = new JSONObject();
            transactionRequestObj.put("transactionType", "authCaptureTransaction");
            transactionRequestObj.put("amount", amount);

            JSONObject pObj = new JSONObject();
            JSONObject dObject = new JSONObject();
            dObject.put("cardNumber", cardNumberView.getText().toString().trim());
            dObject.put("expirationDate", monthView.getText().toString().trim() + yearView.getText().toString().trim());
            dObject.put("cardCode", cvvView.getText().toString().trim());
            pObj.put("creditCard", dObject);
            transactionRequestObj.put("payment", pObj);

            JSONObject customerObject = new JSONObject();
            customerObject.put("email", email);
            transactionRequestObj.put("customer", customerObject);

            JSONObject billObject = new JSONObject();
            billObject.put("firstName", firstName);
            billObject.put("lastName", lastName);
            billObject.put("company", "");
            billObject.put("address", "");
            billObject.put("city", "");
            billObject.put("state", "");
            billObject.put("zip", "");
            billObject.put("country", "");
            billObject.put("phoneNumber", phone);
            billObject.put("email", email);
            transactionRequestObj.put("billTo", billObject);

            transactionRequestObj.put("customerIP", deviceIp);
            createTransactionRequestObj.put("transactionRequest", transactionRequestObj);
            Logger.e(TAG, "RealTime Format Object ::" + createTransactionRequestObj.toString());

            realTimePaymentRequest(createTransactionRequestObj);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }*/

    /*public void UpdateSubscriptionRequest() {
        try {
            JSONObject ARBUpdateSubscriptionRequestObj = new JSONObject();

            JSONObject authObj = new JSONObject();
            authObj.put("name", Utils.API_LOGIN_ID);
            authObj.put("transactionKey", Utils.TRANSACTION_KEY);
            ARBUpdateSubscriptionRequestObj.put("merchantAuthentication", authObj);
            ARBUpdateSubscriptionRequestObj.put("refId", "");
            ARBUpdateSubscriptionRequestObj.put("subscriptionId", subscription_id);

            JSONObject subscriptionObj = new JSONObject();
            subscriptionObj.put("amount", updateSubScriptionAmount);

            JSONObject pObj = new JSONObject();
            JSONObject dObject = new JSONObject();
            dObject.put("cardNumber", cardNumberView.getText().toString().trim());
            dObject.put("expirationDate", monthView.getText().toString().trim() + yearView.getText().toString().trim());
            dObject.put("cardCode", cvvView.getText().toString().trim());
            pObj.put("creditCard", dObject);
            subscriptionObj.put("payment", pObj);

            ARBUpdateSubscriptionRequestObj.put("subscription", subscriptionObj);
            Logger.e(TAG, "Update Subscription Format Object ::" + ARBUpdateSubscriptionRequestObj.toString());

            updateSubscriptionRequest(ARBUpdateSubscriptionRequestObj);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }*/

    public void realTimeXMLRequest() {
        pdialog = new ProgressDialog(PaymentPurchaseUserInfoActivity.this);
        pdialog.setMessage(getString(R.string.please_wait));
        pdialog.setCanceledOnTouchOutside(false);
        pdialog.show();

        String xmlString = "<createTransactionRequest xmlns=\"AnetApi/xml/v1/schema/AnetApiSchema.xsd\">" +
                "<merchantAuthentication><name>" + Utils.API_LOGIN_ID + "</name><transactionKey>" + Utils.TRANSACTION_KEY +
                "</transactionKey></merchantAuthentication><refId></refId><transactionRequest><transactionType>authCaptureTransaction" +
                "</transactionType><amount>" + amount + "</amount><payment><creditCard><cardNumber>" + cardNumberView.getText().toString().trim() + "</cardNumber>" +
                "<expirationDate>" + monthView.getText().toString().trim() + yearView.getText().toString().trim() + "</expirationDate>" +
                "<cardCode>" + cvvView.getText().toString().trim() + "</cardCode></creditCard></payment><customer><email>" + email + "</email>" +
                "</customer><billTo><firstName>" + firstName + "</firstName><lastName>" + lastName + "</lastName><company></company>" +
                "<address></address><city></city><state></state><zip></zip><country></country><phoneNumber>" + phone +
                "</phoneNumber><email>" + email + "</email></billTo><customerIP>" + deviceIp + "</customerIP></transactionRequest></createTransactionRequest>";
        Logger.e(TAG, "realTimeXMLRequest String :: " + xmlString);
        realTimePaymentXmlRequest(xmlString);

    }

    public void realTimePaymentXmlRequest(String str) {
        if (Utils.checkInternetConnection(this)) {
            PaymentRequestTask paymentRequestTask = new PaymentRequestTask(this);
            paymentRequestTask.setAsyncCallListener(new AsyncCallListener() {

                @Override
                public void onResponseReceived(Object response) {
                }

                @Override
                public void onResponseReceived(JSONObject response) {
                    try {
                        Logger.e(TAG, "Response ::" + response.toString());
                        JSONObject createTransObj = response.getJSONObject("createTransactionResponse");
                        JSONObject messagesObj = createTransObj.getJSONObject("messages");
                        String result = messagesObj.getString("resultCode");
                        if (result.equals("Ok")) {
                            JSONObject transactionResObj = createTransObj.getJSONObject("transactionResponse");
                            if (!transactionResObj.isNull("transId")) {
                                transcationID = transactionResObj.getString("transId");
                                Logger.e(TAG, "Transation ID : " + transcationID);
                            }
                            if (auto_renewal.equalsIgnoreCase("Y")) {
//                                UpdateSubscriptionRequest();
                                updateSubscriptionXMLRequest();
                            } else {
                                addSubLevelUsers(transcationID);
                            }
                        } else {
                            if (pdialog.isShowing()) pdialog.dismiss();
                            JSONObject transactionResObj = createTransObj.getJSONObject("transactionResponse");
                            if (!transactionResObj.isNull("messages")) {
                                JSONObject msgObj = createTransObj.getJSONObject("messages");
                                showPaymentSuccessDialog(PaymentPurchaseUserInfoActivity.this, "Rogi", msgObj.get("description").toString());
                            } else {
                                JSONObject msgObj = messagesObj.getJSONObject("message");
                                showPaymentSuccessDialog(PaymentPurchaseUserInfoActivity.this, "Rogi", msgObj.get("text").toString());
                            }
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

    public void updateSubscriptionXMLRequest() {

        String xmlString = "<ARBUpdateSubscriptionRequest xmlns=\"AnetApi/xml/v1/schema/AnetApiSchema.xsd\">" +
                "<merchantAuthentication><name>" + Utils.API_LOGIN_ID + "</name><transactionKey>" + Utils.TRANSACTION_KEY + "" +
                "</transactionKey></merchantAuthentication><refId></refId><subscriptionId>" + subscription_id
                + "</subscriptionId><subscription><amount>" + updateSubScriptionAmount + "</amount><payment><creditCard><cardNumber>" + cardNumberView.getText().toString().trim() + "</cardNumber>" +
                "<expirationDate>" + monthView.getText().toString().trim() + yearView.getText().toString().trim() + "</expirationDate>" +
                "<cardCode>" + cvvView.getText().toString().trim() + "</cardCode></creditCard></payment></subscription>" +
                "</ARBUpdateSubscriptionRequest>";
        Logger.e(TAG, "updateSubscriptionXMLRequest String :: " + xmlString);
        updateSubscriptionXmlRequest(xmlString);

    }

    public void updateSubscriptionXmlRequest(String str) {
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
                        JSONObject ARBUpdateObj = response.getJSONObject("ARBUpdateSubscriptionResponse");
                        JSONObject messagesObj = ARBUpdateObj.getJSONObject("messages");
                        String result = messagesObj.getString("resultCode");
                        if (result.equals("Ok")) {
                            addSubLevelUsers(transcationID);
                        } else {
                            if (pdialog.isShowing()) pdialog.dismiss();
                            JSONObject msgObj = messagesObj.getJSONObject("message");
                            showPaymentSuccessDialog(PaymentPurchaseUserInfoActivity.this, "Rogi", msgObj.get("text").toString());
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


    /*public void realTimePaymentRequest(JSONObject mainObject) {
        pdialog = new ProgressDialog(PaymentPurchaseUserInfoActivity.this);
        pdialog.setMessage(getString(R.string.please_wait));
        pdialog.setCanceledOnTouchOutside(false);
        pdialog.show();

        JSONObject params = new JSONObject();
        try {
            params.put("createTransactionRequest", mainObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Logger.e(TAG, "RealTime Payment PARA : " + String.valueOf(params));
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.POST, Utils.AUTHORIZED_PAYMENT_API, params, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Logger.e(TAG, "RealTime Payment Response::" + String.valueOf(response));
                        try {
                            JSONObject messagesObj = response.getJSONObject("messages");

                            String result = messagesObj.getString("resultCode");
                            if (pdialog.isShowing()) pdialog.dismiss();

                            if (result.equals("Ok")) {

                                JSONObject transactionResObj = response.getJSONObject("transactionResponse");
                                if (!transactionResObj.isNull("transId")) {
                                    transcationID = transactionResObj.getString("transId");
                                    Logger.e(TAG, "Transation ID : " + transcationID);
                                }
                                if (auto_renewal.equalsIgnoreCase("Y")) {
                                    UpdateSubscriptionRequest();
                                } else {
                                    addSubLevelUsers(transcationID);
                                }

                            } else {
                                JSONObject transactionResObj = response.getJSONObject("transactionResponse");
                                if (!transactionResObj.isNull("errors")) {
                                    JSONArray errorArray = transactionResObj.getJSONArray("errors");
                                    for (int i = 0; i < errorArray.length(); i++) {
                                        JSONObject object = errorArray.getJSONObject(i);
                                        showPaymentSuccessDialog(PaymentPurchaseUserInfoActivity.this, "Rogi", object.get("errorText").toString());
                                    }
                                } else {
                                    JSONArray msgArray = messagesObj.getJSONArray("message");
                                    for (int i = 0; i < msgArray.length(); i++) {
                                        JSONObject object = msgArray.getJSONObject(i);
                                        showPaymentSuccessDialog(PaymentPurchaseUserInfoActivity.this, "Rogi", object.get("text").toString());
                                    }
                                }
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
                            if (pdialog.isShowing()) pdialog.dismiss();
                        }
                    }
                });
        requestQueue.add(jsObjRequest);
    }

    public void updateSubscriptionRequest(JSONObject mainObject) {
        pdialog = new ProgressDialog(PaymentPurchaseUserInfoActivity.this);
        pdialog.setMessage(getString(R.string.please_wait));
        pdialog.setCanceledOnTouchOutside(false);
        pdialog.show();

        JSONObject params = new JSONObject();
        try {
            params.put("ARBUpdateSubscriptionRequest", mainObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Logger.e(TAG, "Update Subscription PARA : " + String.valueOf(params));
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.POST, Utils.AUTHORIZED_PAYMENT_API, params, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Logger.e(TAG, "Update Subscription Response::" + String.valueOf(response));
                        try {
                            JSONObject messagesObj = response.getJSONObject("messages");

                            String result = messagesObj.getString("resultCode");
                            if (pdialog.isShowing()) pdialog.dismiss();

                            if (result.equals("Ok")) {
                                addSubLevelUsers(transcationID);
                            } else {
                                JSONArray msgArray = messagesObj.getJSONArray("message");
                                for (int i = 0; i < msgArray.length(); i++) {
                                    JSONObject object = msgArray.getJSONObject(i);
                                    showPaymentSuccessDialog(PaymentPurchaseUserInfoActivity.this, "Rogi", object.get("text").toString());
                                }
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
                            if (pdialog.isShowing()) pdialog.dismiss();
                        }
                    }
                });
        requestQueue.add(jsObjRequest);
    }*/

    public void addSubLevelUsers(String transcationId) {

        JSONObject params = new JSONObject();
        try {
            params.put("user_id", USERID);
            params.put("session_token", TOKEN);
            params.put("plan_id", PLAN_ID);
            params.put("number_of_users", number_of_users);
            params.put("per_user_price", per_user_price);
            params.put("total_amount", amount);
            params.put("transaction_id", transcationId);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.e("Add Sub Level PARA---->", String.valueOf(params));
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.POST, Utils.MAIN_URL + Utils.ADD_SUB_LEVEL_USERS, params, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Logger.e(TAG, "Add Sub Level Res ::" + String.valueOf(response));
                        try {
                            if (pdialog.isShowing()) pdialog.dismiss();
                            Utils.hideSoftKeyboard(PaymentPurchaseUserInfoActivity.this);
                            boolean success = response.getBoolean("success");
                            String msg = response.getString("message");
                            if (success) {
                                showPaymentSuccessDialog(PaymentPurchaseUserInfoActivity.this, "Rogi", getResources().getString(R.string.payment_success));
                            } else {
                                showPaymentSuccessDialog(PaymentPurchaseUserInfoActivity.this, "Rogi", getResources().getString(R.string.payment_api_falied));
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
                        showPaymentSuccessDialog(PaymentPurchaseUserInfoActivity.this, "Rogi", getResources().getString(R.string.payment_api_falied));
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
                            Intent intent = new Intent(context, NavigationDrawerActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent);
                            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
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
            showPaymentSuccessDialog(PaymentPurchaseUserInfoActivity.this, "Rogi", getResources().getString(R.string.unable_get_token));
        } else {
//            realTimeRequest();
        }
    }

    @Override
    public void onErrorReceived(ErrorTransactionResponse errorResponse) {
        hideSoftKeyboard();
        if (progressDialog.isShowing()) progressDialog.dismiss();
        Message error = errorResponse.getFirstErrorMessage();
        Utils.showMessageDialog(PaymentPurchaseUserInfoActivity.this, "Rogi", error.getMessageCode() + "Message :::" + error.getMessageText());
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
        cvv = cvvView.getText().toString();
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
}