package com.rogi.Fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ListView;
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
import com.rogi.Activity.FAQDetailsActivity;
import com.rogi.Activity.NavigationDrawerActivity;
import com.rogi.Activity.PaymentPurchaseUserActivity;
import com.rogi.Activity.ProjectDetailsActivity;
import com.rogi.Adapter.FAQAdapter;
import com.rogi.Model.FAQModel;
import com.rogi.R;
import com.rogi.View.Utils;
import com.rogi.logger.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class FAQFragment extends Fragment {

    public static final String TAG = "FAQFragment";
    View view;
    //    ListView lisFAQ;
    WebView webView;
//    RelativeLayout rootLayout;
//    RequestQueue requestQueue;
//    private SharedPreferences mSharedPreferences;
//    ProgressDialog pdialog;
//    ArrayList<FAQModel> faqModelArrayList = new ArrayList<>();
//
//    FAQAdapter faqAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_about, container, false);

        NavigationDrawerActivity.titleText.setVisibility(View.VISIBLE);
        NavigationDrawerActivity.titleText.setText(getString(R.string.nav_faq));
        NavigationDrawerActivity.imgLogo.setVisibility(View.GONE);
        NavigationDrawerActivity.filterView.setVisibility(View.INVISIBLE);
        NavigationDrawerActivity.imgSyc.setVisibility(View.INVISIBLE);

//        requestQueue = Volley.newRequestQueue(getActivity());
//        mSharedPreferences = getActivity().getSharedPreferences(Utils.PREF_NAME, getActivity().MODE_PRIVATE);
//        NavigationDrawerActivity.doubleBackToExitPressedOnce = false;
        init();
        return view;
    }

    private void init() {
        webView = (WebView) view.findViewById(R.id.webview);
        if (Utils.WEB_SITE_URL.contains("beta")) {
            webView.loadUrl(Utils.WEB_SITE_URL + Utils.FAQ_URL);
        } else {
            webView.loadUrl(Utils.WEB_SITE_URL + Utils.FAQ_URL);
        }

        /*rootLayout = (RelativeLayout) view.findViewById(R.id.rootLayout);
        lisFAQ = (ListView) view.findViewById(R.id.lisFAQ);

        faqModelArrayList = new ArrayList<>();

        pdialog = new ProgressDialog(getActivity());
        pdialog.setMessage(getString(R.string.please_wait));
        pdialog.setCanceledOnTouchOutside(false);
        pdialog.show();

        if (Utils.checkInternetConnection(getActivity())) {
            getFAQ();
        } else {
            if (Utils.validateString(mSharedPreferences.getString(Utils.FAQ_DATA, ""))) {
                String response = mSharedPreferences.getString(Utils.FAQ_DATA, "");
                try {
                    JSONObject object = new JSONObject(response.toString());
                    setFAQData(object);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }*/

    }

    /*public void getFAQ() {
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, Utils.MAIN_URL + Utils.FAQ_API, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Logger.e(TAG, "URL ::" + Utils.DOMIN_URL + Utils.FAQ_API);
                        Logger.e(TAG, "Response" + String.valueOf(response));
                        Utils.storeString(mSharedPreferences, Utils.FAQ_DATA, response.toString());
                        setFAQData(response);
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

    public void setFAQData(JSONObject response) {
        try {
            faqModelArrayList = new ArrayList<>();
            boolean success = response.getBoolean("success");
            boolean auth = response.getBoolean("authenticated");
            String msg = response.getString("message");
            pdialog.dismiss();
            if (success && auth) {
                if (!response.isNull("data")) {
                    JSONArray JArray = response.getJSONArray("data");
                    for (int i = 0; i < JArray.length(); i++) {
                        JSONObject obj = JArray.getJSONObject(i);
                        FAQModel faqModel = new FAQModel();
                        faqModel.setId(obj.getString("id"));
                        faqModel.setQuestion(obj.getString("question"));
                        faqModel.setAnswer(obj.getString("answer"));
                        faqModelArrayList.add(faqModel);
                    }

                    faqAdapter = new FAQAdapter(getActivity(), faqModelArrayList, viewClick);
                    lisFAQ.setAdapter(faqAdapter);
                }
            } else {
                showMessage(msg);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("ERR", e.getMessage());
            pdialog.dismiss();
        }

    }

    View.OnClickListener viewClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int index = (int) v.getTag();
            Log.d("INDEX", "" + index);
            String faq_question = faqModelArrayList.get(index).getQuestion();
            String faq_answer = faqModelArrayList.get(index).getAnswer();
            Utils.storeString(mSharedPreferences, Utils.FAQ_QUESTION, faq_question);
            Utils.storeString(mSharedPreferences, Utils.FAQ_ANSWER, faq_answer);
            Intent intent = new Intent(getActivity(), FAQDetailsActivity.class);
            startActivity(intent);

        }
    };

    private void showMessage(String message) {
        Utils.showResponseMessage(getActivity(), rootLayout, message);
    }*/

}
