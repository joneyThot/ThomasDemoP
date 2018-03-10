package com.rogi.Fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.rogi.Activity.ExistingUserActivity;
import com.rogi.Activity.NavigationDrawerActivity;
import com.rogi.Activity.PaymentActivity;
import com.rogi.Activity.PlanActivity;
import com.rogi.Activity.PlanDetailsActivity;
import com.rogi.Activity.SignUpActivity;
import com.rogi.Model.PlanModel;
import com.rogi.R;
import com.rogi.View.PlanLinearLayout;
import com.rogi.View.TextViewPlus;
import com.rogi.View.Utils;
import com.rogi.logger.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by "Mayuri" on 10/8/17.
 */

public class PlanFragment extends Fragment implements View.OnClickListener {
    public static final String TAG = "PlanFragment";
    ArrayList<PlanModel> planModel;
    RequestQueue requestQueue;
    private SharedPreferences mSharedPreferences;
    String USERID = "", TOKEN = "", autoRenewal = "", subscription_id = "", plain_id = "";
    TextViewPlus planAmountTxt;
    int pos;
    boolean clickFlag = true;

    public static Fragment newInstance(PlanActivity context, int pos, float scale, ArrayList<PlanModel> planModel) {
        Bundle b = new Bundle();
        b.putInt("pos", pos);
        b.putFloat("scale", scale);
        b.putSerializable("planModel", planModel);
        return Fragment.instantiate(context, PlanFragment.class.getName(), b);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (container == null) {
            return null;
        }
        requestQueue = Volley.newRequestQueue(getActivity());
        mSharedPreferences = getActivity().getSharedPreferences(Utils.PREF_NAME, getActivity().MODE_PRIVATE);
        USERID = mSharedPreferences.getString(Utils.USER_ID, "");
        TOKEN = mSharedPreferences.getString(Utils.TOKEN, "");
        autoRenewal = mSharedPreferences.getString(Utils.AUTO_RENEWAL, "");
        subscription_id = mSharedPreferences.getString(Utils.SUBSCRIPTION_ID, "");
        plain_id = mSharedPreferences.getString(Utils.PLAN_ID, "");
        clickFlag = true;
        LinearLayout l = (LinearLayout) inflater.inflate(R.layout.activity_plan_item_view, container, false);

        pos = this.getArguments().getInt("pos");
        float scale = this.getArguments().getFloat("scale");
        PlanLinearLayout root = (PlanLinearLayout) l.findViewById(R.id.root);
        Button getPlanBtn = (Button) l.findViewById(R.id.getPlanBtn);
        planAmountTxt = (TextViewPlus) l.findViewById(R.id.planAmountTxt);
        final TextViewPlus planNameText = (TextViewPlus) l.findViewById(R.id.planNameText);
        TextViewPlus descriptionText = (TextViewPlus) l.findViewById(R.id.descriptionText);
        TextViewPlus durationText = (TextViewPlus) l.findViewById(R.id.durationText);
        final TextViewPlus readMoreText = (TextViewPlus) l.findViewById(R.id.readMoreText);

        planModel = (ArrayList<PlanModel>) this.getArguments().getSerializable("planModel");

        root.setScaleBoth(scale);

        planAmountTxt.setText(planModel.get(pos).getPlanAmount());
        planNameText.setText(planModel.get(pos).getPlanName());
        descriptionText.setText(planModel.get(pos).getDescription());
        durationText.setText(planModel.get(pos).getDuration());

        String str = Utils.isTablet(getActivity());
        if (str.equalsIgnoreCase(Utils.TABLET)) {
            TextViewPlus plan1Text = (TextViewPlus) l.findViewById(R.id.plan1Text);
            TextViewPlus plan2Text = (TextViewPlus) l.findViewById(R.id.plan2Text);
            TextViewPlus plan3Text = (TextViewPlus) l.findViewById(R.id.plan3Text);
            TextViewPlus plan4Text = (TextViewPlus) l.findViewById(R.id.plan4Text);
            TextViewPlus plan5Text = (TextViewPlus) l.findViewById(R.id.plan5Text);
            TextViewPlus plan6Text = (TextViewPlus) l.findViewById(R.id.plan6Text);
            TextViewPlus plan7Text = (TextViewPlus) l.findViewById(R.id.plan7Text);
            TextViewPlus plan8Text = (TextViewPlus) l.findViewById(R.id.plan8Text);
            TextViewPlus plan9Text = (TextViewPlus) l.findViewById(R.id.plan9Text);
            TextViewPlus plan10Text = (TextViewPlus) l.findViewById(R.id.plan10Text);
            TextViewPlus plan11Text = (TextViewPlus) l.findViewById(R.id.plan11Text);

            if (Utils.validateString(planModel.get(pos).getPlan1())) {
                plan1Text.setText(planModel.get(pos).getPlan1());
                plan1Text.setVisibility(View.VISIBLE);
            } else {
                plan1Text.setVisibility(View.GONE);
            }

            if (Utils.validateString(planModel.get(pos).getPlan2())) {
                plan2Text.setText(planModel.get(pos).getPlan2());
                plan2Text.setVisibility(View.VISIBLE);
            } else {
                plan2Text.setVisibility(View.GONE);
            }

            if (Utils.validateString(planModel.get(pos).getPlan3())) {
                plan3Text.setText(planModel.get(pos).getPlan3());
                plan3Text.setVisibility(View.VISIBLE);
            } else {
                plan3Text.setVisibility(View.GONE);
            }

            if (Utils.validateString(planModel.get(pos).getPlan4())) {
                plan4Text.setText(planModel.get(pos).getPlan4());
                plan4Text.setVisibility(View.VISIBLE);
            } else {
                plan4Text.setVisibility(View.GONE);
            }

            if (Utils.validateString(planModel.get(pos).getPlan5())) {
                plan5Text.setText(planModel.get(pos).getPlan5());
                plan5Text.setVisibility(View.VISIBLE);
            } else {
                plan5Text.setVisibility(View.GONE);
            }

            if (Utils.validateString(planModel.get(pos).getPlan6())) {
                plan6Text.setText(planModel.get(pos).getPlan6());
                plan6Text.setVisibility(View.VISIBLE);
            } else {
                plan6Text.setVisibility(View.GONE);
            }

            if (Utils.validateString(planModel.get(pos).getPlan7())) {
                plan7Text.setText(planModel.get(pos).getPlan7());
                plan7Text.setVisibility(View.VISIBLE);
            } else {
                plan7Text.setVisibility(View.GONE);
            }

            if (Utils.validateString(planModel.get(pos).getPlan8())) {
                plan8Text.setText(planModel.get(pos).getPlan8());
                plan8Text.setVisibility(View.VISIBLE);
            } else {
                plan8Text.setVisibility(View.GONE);
            }

            if (Utils.validateString(planModel.get(pos).getPlan9())) {
                plan9Text.setText(planModel.get(pos).getPlan9());
                plan9Text.setVisibility(View.VISIBLE);
            } else {
                plan9Text.setVisibility(View.GONE);
            }

            if (Utils.validateString(planModel.get(pos).getPlan10())) {
                plan10Text.setText(planModel.get(pos).getPlan10());
                plan10Text.setVisibility(View.VISIBLE);
            } else {
                plan10Text.setVisibility(View.GONE);
            }

            if (Utils.validateString(planModel.get(pos).getPlan11())) {
                plan11Text.setText(planModel.get(pos).getPlan11());
                plan11Text.setVisibility(View.VISIBLE);
            } else {
                plan11Text.setVisibility(View.GONE);
            }

            if (Utils.validateString(planModel.get(pos).getPlan12())) {
                readMoreText.setVisibility(View.VISIBLE);
            } else {
                readMoreText.setVisibility(View.GONE);
            }

        } else {
            TextViewPlus plan1Text = (TextViewPlus) l.findViewById(R.id.plan1Text);
            TextViewPlus plan2Text = (TextViewPlus) l.findViewById(R.id.plan2Text);
            TextViewPlus plan3Text = (TextViewPlus) l.findViewById(R.id.plan3Text);
            TextViewPlus plan4Text = (TextViewPlus) l.findViewById(R.id.plan4Text);
            TextViewPlus plan5Text = (TextViewPlus) l.findViewById(R.id.plan5Text);

            plan1Text.setText(planModel.get(pos).getPlan1());
            plan2Text.setText(planModel.get(pos).getPlan2());
            plan3Text.setText(planModel.get(pos).getPlan3());
            plan4Text.setText(planModel.get(pos).getPlan4());
            plan5Text.setText(planModel.get(pos).getPlan5());
        }


        /*if (planFeaturesList.size() > 5) {
            readMoreText.setVisibility(View.VISIBLE);
        } else {
            readMoreText.setVisibility(View.GONE);
        }*/

        readMoreText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent planIntent = new Intent(getContext(), PlanDetailsActivity.class);
                Bundle b = new Bundle();
                b.putSerializable(Utils.PLAN_ARRAY, planModel.get(pos));
                planIntent.putExtras(b);
                startActivity(planIntent);
            }
        });

        if (autoRenewal.equalsIgnoreCase("Y") && plain_id.equalsIgnoreCase(planModel.get(pos).getPlanId())) {
            getPlanBtn.setOnClickListener(null);
        } else {
            getPlanBtn.setOnClickListener(this);
        }


        return l;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.getPlanBtn:
                if (clickFlag) {
                    clickFlag = false;
                    String planPrice = planAmountTxt.getText().toString();
                    String planType = planModel.get(pos).getPlanType();
                    String planName = planModel.get(pos).getPlanName();
                    String planId = planModel.get(pos).getPlanId();
                    setPos(pos);
                    if (Utils.validateString(mSharedPreferences.getString(Utils.MY_PROFILE, ""))) {
                        if (planType.equals("free")) {
                            addPlanRequest(planId);
                        } else {
                            getSubLevelUser(planId);
                        }
                    } else {
                        Intent signupIntent = new Intent(getContext(), SignUpActivity.class);
                        signupIntent.putExtra(Utils.PLAN_PRICE, planPrice);
                        signupIntent.putExtra(Utils.PLAN_ID, planId);
                        signupIntent.putExtra(Utils.PLAN_NAME, planName);
                        Utils.storeString(mSharedPreferences, Utils.PLAN_TYPE, planType);
                        startActivity(signupIntent);
                        clickFlag = true;
                    }
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
                                clickFlag = true;
                                if (!response.isNull("data")) {
                                    JSONObject jObj = response.getJSONObject("data");
                                    String autoRenewal = jObj.getString("auto_renewal");
                                    Utils.storeString(mSharedPreferences, Utils.AUTO_RENEWAL, autoRenewal);
                                    Intent intent = new Intent(getActivity(), NavigationDrawerActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                    getActivity().overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
                                }

                            } else {
                                clickFlag = true;
                                Utils.showMessageDialog(getActivity(), getString(R.string.alert), msg);
                            }

                        } catch (JSONException e) {
                            clickFlag = true;
                            e.printStackTrace();
                            Log.e("ERR", e.getMessage());
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        clickFlag = true;
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
                            clickFlag = true;
                            if (success && authenticated) {
                                if (!response.isNull("data")) {
                                    JSONArray array = response.getJSONArray("data");
                                    if (array.length() > 0) {
                                        String planPrice = planAmountTxt.getText().toString();
                                        String planType = planModel.get(pos).getPlanType();
                                        String planName = planModel.get(pos).getPlanName();
                                        String planId = planModel.get(pos).getPlanId();
                                        Intent existingUserIntent = new Intent(getContext(), ExistingUserActivity.class);
                                        existingUserIntent.putExtra(Utils.PER_RATE_USER, perUserRate);
                                        existingUserIntent.putExtra(Utils.EXITING_USER_OBJECT, array.toString());
                                        existingUserIntent.putExtra(Utils.PLAN_PRICE, planPrice);
                                        existingUserIntent.putExtra(Utils.PLAN_ID, planId);
                                        existingUserIntent.putExtra(Utils.PLAN_NAME, planName);
                                        Utils.storeString(mSharedPreferences, Utils.PLAN_TYPE, planType);
                                        Utils.storeString(mSharedPreferences, Utils.EXITING_USER_SCREEN, Utils.EXITING_USER_SCREEN);
                                        startActivity(existingUserIntent);
                                    } else {
                                        String planPrice = planAmountTxt.getText().toString();
                                        String planType = planModel.get(pos).getPlanType();
                                        String planName = planModel.get(pos).getPlanName();
                                        String planId = planModel.get(pos).getPlanId();
                                        Intent planIntent = new Intent(getContext(), PaymentActivity.class);
                                        planIntent.putExtra(Utils.PLAN_PRICE, planPrice);
                                        planIntent.putExtra(Utils.PLAN_ID, planId);
                                        planIntent.putExtra(Utils.PLAN_NAME, planName);
                                        Utils.storeString(mSharedPreferences, Utils.PLAN_TYPE, planType);
                                        Utils.storeString(mSharedPreferences, Utils.EXITING_USER_SCREEN, "");
                                        startActivity(planIntent);
                                    }

                                }

                            } else {
                                Utils.showMessageDialog(getActivity(), getString(R.string.alert), msg);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e("ERR", e.getMessage());
                            clickFlag = true;
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error instanceof NoConnectionError) {
                        }
                        clickFlag = true;
                    }
                });
        int socketTimeout = 30000;//30 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsObjRequest.setRetryPolicy(policy);
        requestQueue.add(jsObjRequest);
    }


    int selectPos;

    public void setPos(int pos) {
        if (pos == 0) {
            selectPos = 1;
        } else if (pos == 1) {
            selectPos = 2;
        } else if (pos == 2) {
            selectPos = 3;
        } else if (pos == 3) {
            selectPos = 4;
        }
    }

}
