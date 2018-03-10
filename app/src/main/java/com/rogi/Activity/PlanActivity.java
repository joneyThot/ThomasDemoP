package com.rogi.Activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
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
import com.rogi.Adapter.PlanViewPagerAdapter;
import com.rogi.Model.PlanModel;
import com.rogi.R;
import com.rogi.View.Utils;
import com.rogi.logger.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class PlanActivity extends AppCompatActivity implements View.OnClickListener {

    public String TAG = "PlanActivity";
    private ArrayList<PlanModel> planModel = new ArrayList<>();

    static int PAGES = 4;
    public final static int LOOPS = 1000;
    public static int FIRST_PAGE = PAGES * LOOPS / 4;

    public PlanViewPagerAdapter adapter;
    public ViewPager pager;
    TextView titleText;
    ProgressDialog pdialog;
    RequestQueue requestQueue;
    private SharedPreferences mSharedPreferences;

    String USERID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan_view);
        mSharedPreferences = getSharedPreferences(Utils.PREF_NAME, MODE_PRIVATE);
        USERID = mSharedPreferences.getString(Utils.USER_ID, "");
        requestQueue = Volley.newRequestQueue(this);
        init();
        getPlanList();
    }

    public void init() {
        findViewById(R.id.backLayoutclick).setOnClickListener(this);
        pager = (ViewPager) findViewById(R.id.myviewpager);
        titleText = (TextView) findViewById(R.id.titleText);

        titleText.setText("Our Plan");
    }


    public void getPlanList() {
        JSONObject params = new JSONObject();
        try {
            params.put("user_id", USERID);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.e("Get PARA---->", String.valueOf(params));
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.POST, Utils.MAIN_URL + Utils.PLAN_LIST_API, params, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Logger.e("GetPlan ::", "Get Plan Res ::" + String.valueOf(response));
                        try {
                            boolean success = response.getBoolean("success");
                            String msg = response.getString("message");
                            if (success) {
                                JSONArray dataArray = response.getJSONArray("data");
                                if (dataArray.length() > 0) {
                                    setPlanData(dataArray);
                                } else {
                                    showMessageDialog(PlanActivity.this, "Rogi", msg);
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

    /* private void getPlanList() {
        pdialog = new ProgressDialog(this);
        pdialog.setMessage(getString(R.string.please_wait));
        pdialog.setCanceledOnTouchOutside(false);
        pdialog.show();

        JsonObjectRequest jsonObjReq = new JsonObjectRequest
                (Request.Method.GET, Utils.PLAN_LIST_API, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString());
                        Logger.e(TAG, "PlanList ::" + response);
                        pdialog.dismiss();
                        try {
                            boolean success = response.getBoolean("success");
                            String msg = response.getString("message");
                            if (success) {
                                JSONArray dataArray = response.getJSONArray("data");
                                setPlanData(dataArray);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        VolleyLog.d(TAG, "Error: " + error.getMessage());
                    }
                });
        requestQueue.add(jsonObjReq);
    } */

    String plan1, plan2, plan3, plan4, plan5, plan6, plan7, plan8, plan9, plan10, plan11, plan12, plan13, plan14, plan15;
    ArrayList<String> planFeatures = new ArrayList<>();

    public void setPlanData(JSONArray dataArray) {
        try {
            for (int i = 0; i < dataArray.length(); i++) {
                JSONObject obj = dataArray.getJSONObject(i);
                planFeatures = new ArrayList<>();
                String planId = obj.getString("id");
                String planType = obj.getString("type");
                String planName = obj.getString("name");
                String price = obj.getString("price");
                String duration = obj.getString("duration");
                String description = obj.getString("description");
                //items = spiltString(obj.getString("full_description"));
                //setItemValue(items);
                plan1 = obj.getString("feature_1");
                plan2 = obj.getString("feature_2");
                plan3 = obj.getString("feature_3");
                plan4 = obj.getString("feature_4");
                plan5 = obj.getString("feature_5");
                plan6 = obj.getString("feature_6");
                plan7 = obj.getString("feature_7");
                plan8 = obj.getString("feature_8");
                plan9 = obj.getString("feature_9");
                plan10 = obj.getString("feature_10");
                plan11 = obj.getString("feature_11");
                plan12 = obj.getString("feature_12");
                plan13 = obj.getString("feature_13");
                plan14 = obj.getString("feature_14");
                plan15 = obj.getString("feature_15");

                if (!plan1.equals("")) {
                    planFeatures.add(plan1);
                }
                if (!plan2.equals("")) {
                    planFeatures.add(plan2);
                }
                if (!plan3.equals("")) {
                    planFeatures.add(plan3);
                }
                if (!plan4.equals("")) {
                    planFeatures.add(plan4);
                }
                if (!plan5.equals("")) {
                    planFeatures.add(plan5);
                }
                if (!plan6.equals("")) {
                    planFeatures.add(plan6);
                }
                if (!plan7.equals("")) {
                    planFeatures.add(plan7);
                }
                if (!plan8.equals("")) {
                    planFeatures.add(plan8);
                }
                if (!plan9.equals("")) {
                    planFeatures.add(plan9);
                }
                if (!plan10.equals("")) {
                    planFeatures.add(plan10);
                }
                if (!plan11.equals("")) {
                    planFeatures.add(plan11);
                }
                if (!plan12.equals("")) {
                    planFeatures.add(plan12);
                }
                if (!plan13.equals("")) {
                    planFeatures.add(plan13);
                }
                if (!plan14.equals("")) {
                    planFeatures.add(plan14);
                }
                if (!plan15.equals("")) {
                    planFeatures.add(plan15);
                }
                planModel.add(new PlanModel(planFeatures, planId, planName, planType, price, description, duration, plan1,
                        plan2, plan3, plan4, plan5, plan6, plan7, plan8, plan9, plan10, plan11, plan12, plan13, plan14, plan15));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        adapter = new PlanViewPagerAdapter(this, this.getSupportFragmentManager(), planModel);
        pager.setAdapter(adapter);
        pager.setPageTransformer(false, adapter);
        pager.setCurrentItem(FIRST_PAGE);
        pager.setOffscreenPageLimit(3);
        pager.setPageMargin(-300);
    }

   /*static ArrayList<String> items = new ArrayList<String>();

    public static ArrayList<String> spiltString(String s) {
        String[] lines = s.split("<br>");
        for (String ss : lines) {
            items.add(ss);
        }
        return items;
    }

    public void setItemValue(ArrayList<String> items) {
        plan1 = items.get(0);
        plan2 = items.get(1);
        plan3 = items.get(2);
        plan4 = items.get(3);
        plan5 = items.get(4);
    }*/

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.backLayoutclick:
                finish();
                break;
        }
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


    @Override
    public void onBackPressed() {
        finish();
    }
}
