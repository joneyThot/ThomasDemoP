package com.rogi.Fragment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
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
import com.rogi.R;
import com.rogi.View.Utils;
import com.rogi.logger.Logger;

import org.json.JSONException;
import org.json.JSONObject;

public class TechnicalSupportFragment extends Fragment implements View.OnClickListener {

    public static final String TAG = "TechnicalSupportFragment";
    private SharedPreferences mSharedPreferences;

    View view;
    RequestQueue requestQueue;
    EditText edtFirstName, edtLastName, edtEmailAddress, edtComment, edtPhoneNumber, edtSubject;
    TextView btnSubmit;
    RelativeLayout rootLayout;
    String TOKEN = "";
    ProgressDialog pdialog;
    boolean flag = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_technical_support, container, false);
        requestQueue = Volley.newRequestQueue(getActivity());
        mSharedPreferences = getActivity().getSharedPreferences(Utils.PREF_NAME, getActivity().MODE_PRIVATE);
        TOKEN = mSharedPreferences.getString(Utils.TOKEN, "");

        NavigationDrawerActivity.titleText.setVisibility(View.VISIBLE);
        NavigationDrawerActivity.titleText.setText(getString(R.string.nav_technical_support));
        NavigationDrawerActivity.imgLogo.setVisibility(View.GONE);
        NavigationDrawerActivity.filterView.setVisibility(View.INVISIBLE);
        NavigationDrawerActivity.imgSyc.setVisibility(View.INVISIBLE);

        init();
        return view;
    }

    private void init() {
        rootLayout = (RelativeLayout) view.findViewById(R.id.rootLayout);
        edtFirstName = (EditText) view.findViewById(R.id.edtFirstName);
        edtLastName = (EditText) view.findViewById(R.id.edtLastName);
        edtEmailAddress = (EditText) view.findViewById(R.id.edtEmailAddress);
        edtComment = (EditText) view.findViewById(R.id.edtComment);
        edtPhoneNumber = (EditText) view.findViewById(R.id.edtPhoneNumber);
        edtSubject = (EditText) view.findViewById(R.id.edtSubject);

        btnSubmit = (TextView) view.findViewById(R.id.btnSubmit);
        btnSubmit.setOnClickListener(this);

        edtFirstName.setTypeface(Utils.getTypeFace(getActivity()));
        edtLastName.setTypeface(Utils.getTypeFace(getActivity()));
        edtEmailAddress.setTypeface(Utils.getTypeFace(getActivity()));
        edtComment.setTypeface(Utils.getTypeFace(getActivity()));
        edtPhoneNumber.setTypeface(Utils.getTypeFace(getActivity()));
        edtSubject.setTypeface(Utils.getTypeFace(getActivity()));

        InputFilter filter = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                if (source.length() > 0) {
                    if (!Character.isDigit(source.charAt(0)))
                        return "";
                    else {
                        if (dstart == 3) {
                            return source + ") ";
                        } else if (dstart == 0) {
                            return "(" + source;
                        } else if ((dstart == 5) || (dstart == 9))
                            return "-" + source;
                        else if (dstart >= 14)
                            return "";
                    }
                } else {
                }
                return null;
            }
        };

        edtPhoneNumber.setFilters(new InputFilter[]{filter});

    }

    public void sendTechnicalSupport(String firstName, String lastName, String subject, String email,
                                     String phone_number, String des) {

        pdialog = new ProgressDialog(getActivity());
        pdialog.setMessage(getString(R.string.please_wait));
        pdialog.setCanceledOnTouchOutside(false);
        pdialog.show();

        JSONObject params = new JSONObject();
        try {
            params.put("first_name", firstName);
            params.put("last_name", lastName);
            params.put("session_token", TOKEN);
            params.put("email", email);
            params.put("phone_number", phone_number);
            params.put("subject", subject);
            params.put("description", des);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Logger.e(TAG, "Technical Support PARA---->" + String.valueOf(params));
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.POST, Utils.MAIN_URL + Utils.TECHNICAL_SUPPORT, params, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Logger.e(TAG, "URL---->" + Utils.MAIN_URL + Utils.TECHNICAL_SUPPORT);
                        Logger.e(TAG, "Technical Support RES---->" + String.valueOf(response));
                        try {
                            boolean success = response.getBoolean("success");
                            String msg = response.getString("message");
                            pdialog.dismiss();
                            if (success) {
                                flag = true;
                                showDialog(getActivity(), "Rogi", msg);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e("ERR", e.getMessage());
                            flag = true;
                            pdialog.dismiss();
                            edtFirstName.setText("");
                            edtLastName.setText("");
                            edtEmailAddress.setText("");
                            edtComment.setText("");
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error instanceof NoConnectionError) {
                        }
                        pdialog.dismiss();
                        flag = true;
                        edtFirstName.setText("");
                        edtLastName.setText("");
                        edtEmailAddress.setText("");
                        edtComment.setText("");
                    }
                });
        int socketTimeout = 30000;//30 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsObjRequest.setRetryPolicy(policy);
        requestQueue.add(jsObjRequest);
    }

    public void showDialog(final Context context, String title, String message) {
        if (message != null && message.trim().length() > 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle(title);
            builder.setCancelable(false);
            builder.setMessage(message);
            builder.setPositiveButton("Yes",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            flag = true;
                            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new HomeFragment()).commit();

                        }
                    });
            // create alert dialog
            AlertDialog alertDialog = builder.create();
            // show it
            alertDialog.show();
        }
    }

    public boolean fieldValidation() {
        boolean flag = true;
        if (!Utils.validateString(edtFirstName.getText().toString())) {
            flag = false;
            edtFirstName.requestFocus();
            showMessage(getString(R.string.enter_firstname));
        } else if (!Utils.validateString(edtLastName.getText().toString())) {
            flag = false;
            edtLastName.requestFocus();
            showMessage(getString(R.string.enter_lastname));
        } else if (!Utils.validateString(edtSubject.getText().toString())) {
            flag = false;
            edtSubject.requestFocus();
            showMessage(getString(R.string.tech_enter_subject));
        } else if (!Utils.validateString(edtEmailAddress.getText().toString())) {
            flag = false;
            edtEmailAddress.requestFocus();
            showMessage(getString(R.string.enter_emailaddress));
        } else if (edtEmailAddress.getText().toString().length() > 0 && !Utils.isEmailValid(edtEmailAddress.getText().toString())) {
            flag = false;
            showMessage(getString(R.string.invalid_email));
            edtEmailAddress.requestFocus();
        } else if (!Utils.validateString(edtPhoneNumber.getText().toString())) {
            flag = false;
            edtPhoneNumber.requestFocus();
            showMessage(getString(R.string.enter_phonenumber));
        } else if (edtPhoneNumber.getText().toString().trim().length() < 14) {
            flag = false;
            edtPhoneNumber.requestFocus();
            showMessage(getString(R.string.enter_phonenumber_length));
        } else if (!Utils.validateString(edtComment.getText().toString())) {
            flag = false;
            edtComment.requestFocus();
            showMessage(getString(R.string.enter_comment));
        }
        return flag;
    }

    private void showMessage(String message) {
        Utils.showResponseMessage(getActivity(), rootLayout, message);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSubmit:
                if (fieldValidation()) {
                    if (flag) {
                        final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
                        flag = false;
                        String str = edtPhoneNumber.getText().toString().trim().replaceAll("\\D+", "");
                        String phoneNo = str;
                        sendTechnicalSupport(edtFirstName.getText().toString().trim(), edtLastName.getText().toString().trim(),
                                edtSubject.getText().toString().trim(), edtEmailAddress.getText().toString().trim(),
                                phoneNo, edtComment.getText().toString().trim());
                    }
                }
                break;
        }

    }
}
