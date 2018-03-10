package com.rogi.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.rogi.Adapter.PlanDetailAdapter;
import com.rogi.Model.PlanModel;
import com.rogi.R;
import com.rogi.View.TextViewPlus;
import com.rogi.View.Utils;
import com.rogi.logger.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class PlanDetailsActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String TAG = "PlanDetailsActivity";
    SharedPreferences mSharedPreferences;
    RequestQueue requestQueue;
    String USERID = "", TOKEN = "", autoRenewal = "", subscription_id = "", plain_id = "";

    Button getPlanBtn;
    PlanModel planModel;
    TextViewPlus titleText;
    Bundle bundle;
    PlanDetailAdapter adapter;
    RecyclerView recyclerView;

    final static private int PLAN_TITLE_VIEW = 0;
    final static private int PLAN_FEATURES_VIEW = 1;

    ArrayList<PlanModel> planDetailsModel = new ArrayList<>();
    ArrayList<String> featuresList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan_details_view);
        mSharedPreferences = getSharedPreferences(Utils.PREF_NAME, MODE_PRIVATE);
        USERID = mSharedPreferences.getString(Utils.USER_ID, "");
        TOKEN = mSharedPreferences.getString(Utils.TOKEN, "");
        autoRenewal = mSharedPreferences.getString(Utils.AUTO_RENEWAL, "");
        subscription_id = mSharedPreferences.getString(Utils.SUBSCRIPTION_ID, "");
        plain_id = mSharedPreferences.getString(Utils.PLAN_ID, "");

        requestQueue = Volley.newRequestQueue(this);
        bundle = getIntent().getExtras();
        planModel = (PlanModel) bundle.get(Utils.PLAN_ARRAY);
        init();

        adapter = new PlanDetailAdapter(this, planDetailsModel);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(adapter);

        featuresList = new ArrayList<>(planModel.getPlanFeatures());
        System.out.println("FeaturesList ::" + featuresList);


        titleText.setText(planModel.getPlanName());
        setData();
    }

    public void init() {
        findViewById(R.id.backLayoutclick).setOnClickListener(this);
        titleText = (TextViewPlus) findViewById(R.id.titleText);
        getPlanBtn = (Button) findViewById(R.id.getPlanBtn);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        if (autoRenewal.equalsIgnoreCase("Y") && plain_id.equalsIgnoreCase(planModel.getPlanId())) {
            getPlanBtn.setOnClickListener(null);
        } else {
            getPlanBtn.setOnClickListener(this);
        }

    }


    public void setData() {
        PlanModel planTitleModel = new PlanModel();
        planTitleModel.setViewType(PLAN_TITLE_VIEW);
        planTitleModel.setDescription(planModel.getDescription());
        planTitleModel.setPlanAmount(planModel.getPlanAmount());
        planTitleModel.setDuration(planModel.getDuration());
        planDetailsModel.add(planTitleModel);


        for (int i = 0; i < featuresList.size(); i++) {
            PlanModel planFeaturesModel = new PlanModel();
            planFeaturesModel.setViewType(PLAN_FEATURES_VIEW);
            planFeaturesModel.setPlan1(featuresList.get(i));
            planDetailsModel.add(planFeaturesModel);
        }

        adapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.backLayoutclick:
                finish();
                break;
            case R.id.getPlanBtn:
                /*if (planModel.getPlanType().equals("free")) {
                    addPlanRequest(planModel.getPlanId());
                } else {
                    Intent signupIntent = new Intent(this, SignUpActivity.class);
                    signupIntent.putExtra(Utils.PLAN_PRICE, planModel.getPlanAmount());
                    signupIntent.putExtra(Utils.PLAN_ID, planModel.getPlanId());
                    signupIntent.putExtra(Utils.PLAN_NAME, planModel.getPlanName());
                    startActivity(signupIntent);
                    Utils.storeString(mSharedPreferences, Utils.PLAN_TYPE, planModel.getPlanType());

                    *//*
                    Intent planIntent = new Intent(this, PaymentActivity.class);
                    planIntent.putExtra(Utils.PLAN_PRICE, planModel.getPlanAmount());
                    planIntent.putExtra(Utils.PLAN_ID, planModel.getPlanId());
                    planIntent.putExtra(Utils.PLAN_NAME, planModel.getPlanName());
                    Utils.storeString(mSharedPreferences, Utils.PLAN_TYPE, planModel.getPlanType());
                    startActivity(planIntent);*//*
                }*/

                if (Utils.validateString(mSharedPreferences.getString(Utils.MY_PROFILE, ""))) {
                    if (planModel.getPlanType().equals("free")) {
                        addPlanRequest(planModel.getPlanId());
                    } else {
                        getSubLevelUser(planModel.getPlanId());
                    }
                } else {
                    Intent signupIntent = new Intent(PlanDetailsActivity.this, SignUpActivity.class);
                    signupIntent.putExtra(Utils.PLAN_PRICE, planModel.getPlanAmount());
                    signupIntent.putExtra(Utils.PLAN_ID, planModel.getPlanId());
                    signupIntent.putExtra(Utils.PLAN_NAME, planModel.getPlanName());
                    Utils.storeString(mSharedPreferences, Utils.PLAN_TYPE, planModel.getPlanType());
                    startActivity(signupIntent);
                }

                break;
        }
    }


    public void addPlanRequest(String planId) {
        JSONObject params = new JSONObject();
        try {
            params.put("user_id", USERID);
            params.put("session_token", TOKEN);
            params.put("subscription_id", "");
            params.put("plan_id", planId);
            params.put("auto_renewal", "N");

        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.e("Add Plan PARA---->", String.valueOf(params));
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.POST, Utils.MAIN_URL + Utils.PLAN_ADD_API, params, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Logger.e("FreePlan ::", "Add Plan Res ::" + String.valueOf(response));
                        try {
                            boolean success = response.getBoolean("success");
                            String msg = response.getString("message");
                            if (success) {
                                if (!response.isNull("data")) {
                                    JSONObject jObj = response.getJSONObject("data");
                                    String autoRenewal = jObj.getString("auto_renewal");
                                    String subscription_id = jObj.getString("subscription_id");
                                    Utils.storeString(mSharedPreferences, Utils.AUTO_RENEWAL, autoRenewal);
                                    Utils.storeString(mSharedPreferences, Utils.SUBSCRIPTION_ID, subscription_id);
                                    Intent intent = new Intent(PlanDetailsActivity.this, NavigationDrawerActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
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
                        }
                    }
                });
        int socketTimeout = 30000;//30 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsObjRequest.setRetryPolicy(policy);
        requestQueue.add(jsObjRequest);
    }

    public void getSubLevelUser(String planId) {
        JSONObject params = new JSONObject();

        try {
            params.put("user_id", USERID);
            params.put("session_token", TOKEN);
            params.put("plan_id", planId);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        Logger.e(TAG, "Get Sublevel User PARA : " + String.valueOf(params));
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.POST, Utils.MAIN_URL + Utils.GET_SUB_LEVEL_USERS, params, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Logger.e(TAG, "Get Sublevel User Res :: " + String.valueOf(response));
                        try {
                            boolean success = response.getBoolean("success");
                            boolean authenticated = response.getBoolean("authenticated");
                            String msg = response.getString("message");
                            String perUserRate = response.getString("per_user_rate");
                            if (success && authenticated) {
                                if (!response.isNull("data")) {
                                    JSONArray array = response.getJSONArray("data");
                                    if (array.length() > 0) {
                                        Intent existingUserIntent = new Intent(PlanDetailsActivity.this, ExistingUserActivity.class);
                                        existingUserIntent.putExtra(Utils.PER_RATE_USER, perUserRate);
                                        existingUserIntent.putExtra(Utils.EXITING_USER_OBJECT, array.toString());
                                        existingUserIntent.putExtra(Utils.PLAN_PRICE, planModel.getPlanAmount());
                                        existingUserIntent.putExtra(Utils.PLAN_ID, planModel.getPlanId());
                                        existingUserIntent.putExtra(Utils.PLAN_NAME, planModel.getPlanName());
                                        Utils.storeString(mSharedPreferences, Utils.PLAN_TYPE, planModel.getPlanType());
                                        Utils.storeString(mSharedPreferences, Utils.EXITING_USER_SCREEN, Utils.EXITING_USER_SCREEN);
                                        startActivity(existingUserIntent);
                                    } else {
                                        Intent planIntent = new Intent(PlanDetailsActivity.this, PaymentActivity.class);
                                        planIntent.putExtra(Utils.PLAN_PRICE, planModel.getPlanAmount());
                                        planIntent.putExtra(Utils.PLAN_ID, planModel.getPlanId());
                                        planIntent.putExtra(Utils.PLAN_NAME, planModel.getPlanName());
                                        Utils.storeString(mSharedPreferences, Utils.PLAN_TYPE, planModel.getPlanType());
                                        Utils.storeString(mSharedPreferences, Utils.EXITING_USER_SCREEN, "");
                                        startActivity(planIntent);
                                    }

                                }

                            } else {
                                Utils.showMessageDialog(PlanDetailsActivity.this, getString(R.string.alert), msg);
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

}
