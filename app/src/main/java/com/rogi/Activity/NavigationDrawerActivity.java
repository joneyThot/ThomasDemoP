package com.rogi.Activity;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Display;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.rogi.Database.DBConstants;
import com.rogi.Database.DBHelper;
import com.rogi.Fragment.AboutFragment;
import com.rogi.Fragment.FAQFragment;
import com.rogi.Fragment.HistoryFragment;
import com.rogi.Fragment.HomeFragment;
import com.rogi.Fragment.PrivacyPilocyFragment;
import com.rogi.Fragment.PurchaseUserFragment;
import com.rogi.Fragment.SalesForceFragment;
import com.rogi.Fragment.TechnicalSupportFragment;
import com.rogi.Fragment.TermsAndConditionFragment;
import com.rogi.Fragment.VideoGuideFragment;
import com.rogi.Model.AdditionalContactModel;
import com.rogi.Model.MediaModel;
import com.rogi.Model.NotesModel;
import com.rogi.Model.ProjectPhaseModel;
import com.rogi.Model.TaskModel;
import com.rogi.R;
import com.rogi.View.Utils;
import com.rogi.logger.Logger;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

public class NavigationDrawerActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    public static final String TAG = "NavigationDrawerActivity";
    public static NavigationView nvDrawer;
    public static FloatingActionButton fab;
    public static int navItemIndex = 0;
    public static ImageView filterView, imgLogo, imgSyc;
    public static TextView titleText, txtHome, txtHistroy, txtAbout, txtPrivacyPolicy, txtLogout,
            txtTechnicalSupport, txtSalesForce, txtPurchaseUser, txtTermsAndCondtion, txtVideoGuide, txtFAQ;
    public static ImageView cirProfileImage;
    public static View viewHome, viewHistroy, viewAbout, viewPrivacyPolicy, viewLogout,
            viewTechnicalSupport, viewSalesForce, viewPurchaseUser, viewTermsAndCondtion, viewVideoGuide, viewFAQ;
    public static boolean doubleBackToExitPressedOnce = true;
    public static TextView txtUserName;
    public static TextView txtEmailId;

    private static SharedPreferences mSharedPreferences;
    private static int mDeviceWidth = 480;

    private boolean shouldLoadHomeFragOnBackPress = true;
    private DBHelper myDbHelper;
    private NetworkChangeReceiver receiver;
    boolean isConnected = false;

    RequestQueue requestQueue;
    ActionBarDrawerToggle toggle;
    DrawerLayout drawer;
    FrameLayout content_frame;
    LinearLayout menuLayout, profileLayout, menu_linHome, menu_linHistroy, menu_linAbout, menu_linPrivacyPolicy, menu_linLogout,
            menu_linTechnicalSupport, menu_linSalesForce, menu_linTermsAndCondtion, menu_linRogiUrl, menu_linVideoGuide,
            menu_linFAQ;
    public static LinearLayout menu_linPurchaseUser;
    ImageView imgProjectLogo;
    String TOKEN, USERID, projectLogoUrl, PLAN_ID, ROLE;

    ArrayList<TaskModel> projectModelArrayList;
    ArrayList<AdditionalContactModel> additionalContactModelArrayList;
    ArrayList<NotesModel> notesModelArrayList;
    ArrayList<ProjectPhaseModel> projectPhaseModelArrayList;
    ArrayList<MediaModel> mediaModelArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nav_activity_navigation_drawer);
        requestQueue = Volley.newRequestQueue(this);
        mSharedPreferences = getSharedPreferences(Utils.PREF_NAME, MODE_PRIVATE);
        USERID = mSharedPreferences.getString(Utils.USER_ID, "");
        TOKEN = mSharedPreferences.getString(Utils.TOKEN, "");
        PLAN_ID = mSharedPreferences.getString(Utils.PLAN_ID, "");
        projectLogoUrl = mSharedPreferences.getString(Utils.COMPANY_IMAGE, "");
        ROLE = mSharedPreferences.getString(Utils.ROLE, "");

        myDbHelper = new DBHelper(this);
        try {
            myDbHelper.createDataBase();
        } catch (IOException ioe) {
            throw new Error("Unable to create database");
        }

        init();
//        getComplanyList();
        getProjectResponsibilityList();

        toggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadDrawer();
            }
        });
//        displaySelectedScreen(R.id.nav_home);
//        selectNavMenu();
    }

    static View navHeader;

    public void init() {
        nvDrawer = (NavigationView) findViewById(R.id.nav_view);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(this);
        filterView = (ImageView) findViewById(R.id.filterView);
        imgLogo = (ImageView) findViewById(R.id.imgLogo);
        imgSyc = (ImageView) findViewById(R.id.imgSyc);
        titleText = (TextView) findViewById(R.id.titleText);
        content_frame = (FrameLayout) findViewById(R.id.content_frame);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.setHomeAsUpIndicator(R.mipmap.menu_icon);
        toggle.setDrawerIndicatorEnabled(false);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        nvDrawer.setNavigationItemSelectedListener(this);
        navHeader = nvDrawer.getHeaderView(0);

        menuLayout = (LinearLayout) navHeader.findViewById(R.id.menuLayout);
        profileLayout = (LinearLayout) navHeader.findViewById(R.id.profileLayout);
        profileLayout.setOnClickListener(this);

        imgProjectLogo = (ImageView) navHeader.findViewById(R.id.imgProjectLogo);
        cirProfileImage = (ImageView) navHeader.findViewById(R.id.cirProfileImage);
        setImage(NavigationDrawerActivity.this);

        txtUserName = (TextView) navHeader.findViewById(R.id.txtUserName);
        txtEmailId = (TextView) navHeader.findViewById(R.id.txtEmailId);

        //Custome Raw
        menu_linHome = (LinearLayout) navHeader.findViewById(R.id.menu_linHome);
        txtHome = (TextView) navHeader.findViewById(R.id.txtHome);
        viewHome = (View) navHeader.findViewById(R.id.viewHome);

        menu_linHistroy = (LinearLayout) navHeader.findViewById(R.id.menu_linHistroy);
        txtHistroy = (TextView) navHeader.findViewById(R.id.txtHistroy);
        viewHistroy = (View) navHeader.findViewById(R.id.viewHistroy);

        menu_linSalesForce = (LinearLayout) navHeader.findViewById(R.id.menu_linSalesForce);
        txtSalesForce = (TextView) navHeader.findViewById(R.id.txtSalesForce);
        viewSalesForce = (View) navHeader.findViewById(R.id.viewSalesForce);

        menu_linPurchaseUser = (LinearLayout) navHeader.findViewById(R.id.menu_linPurchaseUser);
        txtPurchaseUser = (TextView) navHeader.findViewById(R.id.txtPurchaseUser);
        viewPurchaseUser = (View) navHeader.findViewById(R.id.viewPurchaseUser);

        menu_linTechnicalSupport = (LinearLayout) navHeader.findViewById(R.id.menu_linTechnicalSupport);
        txtTechnicalSupport = (TextView) navHeader.findViewById(R.id.txtTechnicalSupport);
        viewTechnicalSupport = (View) navHeader.findViewById(R.id.viewTechnicalSupport);

        menu_linVideoGuide = (LinearLayout) navHeader.findViewById(R.id.menu_linVideoGuide);
        txtVideoGuide = (TextView) navHeader.findViewById(R.id.txtVideoGuide);
        viewVideoGuide = (View) navHeader.findViewById(R.id.viewVideoGuide);

        menu_linFAQ = (LinearLayout) navHeader.findViewById(R.id.menu_linFAQ);
        txtFAQ = (TextView) navHeader.findViewById(R.id.txtFAQ);
        viewFAQ = (View) navHeader.findViewById(R.id.viewFAQ);

        menu_linAbout = (LinearLayout) navHeader.findViewById(R.id.menu_linAbout);
        txtAbout = (TextView) navHeader.findViewById(R.id.txtAbout);
        viewAbout = (View) navHeader.findViewById(R.id.viewAbout);

        menu_linPrivacyPolicy = (LinearLayout) navHeader.findViewById(R.id.menu_linPrivacyPolicy);
        txtPrivacyPolicy = (TextView) navHeader.findViewById(R.id.txtPrivacyPolicy);
        viewPrivacyPolicy = (View) navHeader.findViewById(R.id.viewPrivacyPolicy);

        menu_linTermsAndCondtion = (LinearLayout) navHeader.findViewById(R.id.menu_linTermsAndCondtion);
        txtTermsAndCondtion = (TextView) navHeader.findViewById(R.id.txtTermsAndCondtion);
        viewTermsAndCondtion = (View) navHeader.findViewById(R.id.viewTermsAndCondtion);

        menu_linLogout = (LinearLayout) navHeader.findViewById(R.id.menu_linLogout);
        txtLogout = (TextView) navHeader.findViewById(R.id.txtLogout);
        viewLogout = (View) navHeader.findViewById(R.id.viewLogout);

        menu_linRogiUrl = (LinearLayout) navHeader.findViewById(R.id.menu_linRogiUrl);
        TextView txtRogiUrl = (TextView) navHeader.findViewById(R.id.txtRogiUrl);

        if (Utils.validateString(Utils.WEB_SITE_URL)) {
            String html = "Visit <a href=\"" + Utils.WEB_SITE_URL + "\">www.myrogi.com</a> for user login to edit projects,  print reports, plus much more. ";
            Spanned result;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                result = Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY);
            } else {
                result = Html.fromHtml(html);
            }
            txtRogiUrl.setText(result);
            txtRogiUrl.setMovementMethod(LinkMovementMethod.getInstance());
        } else {
            String html = "Visit <a href=\"http://www.myrogi.com\">www.myrogi.com</a> for user login to edit projects,  print reports, plus much more. ";
            Spanned result;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                result = Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY);
            } else {
                result = Html.fromHtml(html);
            }
            txtRogiUrl.setText(result);
            txtRogiUrl.setMovementMethod(LinkMovementMethod.getInstance());
        }

        for (int i = 1; i < menuLayout.getChildCount(); i++) {
            menuLayout.getChildAt(i).setOnClickListener(this);
        }

        navItemIndex = 0;
//        selectMenu(navItemIndex);
        Fragment fragment = new HomeFragment();
        Utils.storeString(mSharedPreferences, Utils.ITEM_FILTER_STATUS, "");
        Utils.storeString(mSharedPreferences, Utils.MY_PROFILE, "");
        fab.setVisibility(View.VISIBLE);
        selectMenu(navItemIndex);
        if (fragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, fragment);
            ft.commit();
        }
        drawer.closeDrawers();
        doubleBackToExitPressedOnce = true;
        Utils.projectSyncFlag = false;
        Utils.projectRefreshFlag = false;

        WindowManager w = getWindowManager();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            Point size = new Point();
            w.getDefaultDisplay().getSize(size);
            mDeviceWidth = size.x;
        } else {
            Display d = w.getDefaultDisplay();
            mDeviceWidth = d.getWidth();
        }

        setComanyImage(projectLogoUrl);

        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        receiver = new NetworkChangeReceiver();
        registerReceiver(receiver, filter);
    }

//    @Override
//    protected void onStop() {
//        unregisterReceiver(receiver);
//        super.onStop();
//    }


    @Override
    public void onClick(View v) {
        Fragment fragment = null;

        switch (v.getId()) {
            case R.id.profileLayout:
                hideSoftKeyboard();
                Intent profileIntent = new Intent(NavigationDrawerActivity.this, MyProfileActivity.class);
                startActivity(profileIntent);
                Utils.storeString(mSharedPreferences, Utils.MY_PROFILE, "");
                setComanyImage(projectLogoUrl);
                drawer.closeDrawers();
                break;

            case R.id.menu_linHome:
                hideSoftKeyboard();
                fragment = new HomeFragment();
                navItemIndex = 0;
                Utils.storeString(mSharedPreferences, Utils.ITEM_FILTER_STATUS, "");
                Utils.storeString(mSharedPreferences, Utils.MY_PROFILE, "");
                fab.setVisibility(View.VISIBLE);
                selectMenu(navItemIndex);
                doubleBackToExitPressedOnce = true;
                planCheck();
                setComanyImage(projectLogoUrl);
//                setTitle(getString(R.string.nav_home));
                break;

            case R.id.menu_linHistroy:
                hideSoftKeyboard();
                fragment = new HistoryFragment();
                navItemIndex = 1;
                Utils.storeString(mSharedPreferences, Utils.ITEM_FILTER_STATUS, "");
                Utils.storeString(mSharedPreferences, Utils.MY_PROFILE, "");
                fab.setVisibility(View.GONE);
                selectMenu(navItemIndex);
                setTitle(getString(R.string.nav_history));
                doubleBackToExitPressedOnce = false;
                planCheck();
                setComanyImage(projectLogoUrl);
                break;

            case R.id.menu_linSalesForce:
                fragment = new SalesForceFragment();
                navItemIndex = 2;
                Utils.storeString(mSharedPreferences, Utils.ITEM_FILTER_STATUS, "");
                Utils.storeString(mSharedPreferences, Utils.MY_PROFILE, "");
                fab.setVisibility(View.GONE);
                selectMenu(navItemIndex);
                setTitle(getString(R.string.nav_sales_force));
                doubleBackToExitPressedOnce = false;
                planCheck();
                setComanyImage(projectLogoUrl);
                break;

            case R.id.menu_linPurchaseUser:
                hideSoftKeyboard();
                fragment = new PurchaseUserFragment();
                navItemIndex = 3;
                Utils.storeString(mSharedPreferences, Utils.ITEM_FILTER_STATUS, "");
                Utils.storeString(mSharedPreferences, Utils.MY_PROFILE, "");
                fab.setVisibility(View.GONE);
                selectMenu(navItemIndex);
                setTitle(getString(R.string.nav_sales_force));
                doubleBackToExitPressedOnce = false;
                planCheck();
                setComanyImage(projectLogoUrl);
                break;

            case R.id.menu_linTechnicalSupport:
                hideSoftKeyboard();
                fragment = new TechnicalSupportFragment();
                navItemIndex = 4;
                Utils.storeString(mSharedPreferences, Utils.ITEM_FILTER_STATUS, "");
                Utils.storeString(mSharedPreferences, Utils.MY_PROFILE, "");
                fab.setVisibility(View.GONE);
                selectMenu(navItemIndex);
                setTitle(getString(R.string.nav_technical_support));
                doubleBackToExitPressedOnce = false;
                planCheck();
                setComanyImage(projectLogoUrl);
                break;

            case R.id.menu_linVideoGuide:
                hideSoftKeyboard();
                fragment = new VideoGuideFragment();
                navItemIndex = 5;
                Utils.storeString(mSharedPreferences, Utils.ITEM_FILTER_STATUS, "");
                Utils.storeString(mSharedPreferences, Utils.MY_PROFILE, "");
                fab.setVisibility(View.GONE);
                selectMenu(navItemIndex);
                setTitle(getString(R.string.nav_video_guide));
                doubleBackToExitPressedOnce = false;
                planCheck();
                setComanyImage(projectLogoUrl);
                break;

            case R.id.menu_linFAQ:
                hideSoftKeyboard();
                fragment = new FAQFragment();
                navItemIndex = 6;
                Utils.storeString(mSharedPreferences, Utils.ITEM_FILTER_STATUS, "");
                Utils.storeString(mSharedPreferences, Utils.MY_PROFILE, "");
                fab.setVisibility(View.GONE);
                selectMenu(navItemIndex);
                setTitle(getString(R.string.nav_faq));
                doubleBackToExitPressedOnce = false;
                planCheck();
                setComanyImage(projectLogoUrl);
                break;

            case R.id.menu_linAbout:
                hideSoftKeyboard();
                fragment = new AboutFragment();
                navItemIndex = 7;
                Utils.storeString(mSharedPreferences, Utils.ITEM_FILTER_STATUS, "");
                Utils.storeString(mSharedPreferences, Utils.MY_PROFILE, "");
                fab.setVisibility(View.GONE);
                selectMenu(navItemIndex);
                setTitle(getString(R.string.nav_about));
                doubleBackToExitPressedOnce = false;
                planCheck();
                setComanyImage(projectLogoUrl);
                break;

            case R.id.menu_linPrivacyPolicy:
                hideSoftKeyboard();
                fragment = new PrivacyPilocyFragment();
                navItemIndex = 8;
                Utils.storeString(mSharedPreferences, Utils.ITEM_FILTER_STATUS, "");
                Utils.storeString(mSharedPreferences, Utils.MY_PROFILE, "");
                fab.setVisibility(View.GONE);
                selectMenu(navItemIndex);
                setTitle(getString(R.string.nav_privacy_policy));
                doubleBackToExitPressedOnce = false;
                planCheck();
                setComanyImage(projectLogoUrl);
                break;

            case R.id.menu_linTermsAndCondtion:
                hideSoftKeyboard();
                fragment = new TermsAndConditionFragment();
                navItemIndex = 9;
                Utils.storeString(mSharedPreferences, Utils.ITEM_FILTER_STATUS, "");
                Utils.storeString(mSharedPreferences, Utils.MY_PROFILE, "");
                fab.setVisibility(View.GONE);
                selectMenu(navItemIndex);
                setTitle(getString(R.string.nav_terms_and_condition));
                doubleBackToExitPressedOnce = false;
                planCheck();
                setComanyImage(projectLogoUrl);
                break;

            case R.id.menu_linLogout:
                hideSoftKeyboard();
//                navItemIndex = 4;
                selectMenu(navItemIndex);
                setComanyImage(projectLogoUrl);
                showLogoutDialog(this, "Rogi", "Are you sure you want to logout?");
//                doubleBackToExitPressedOnce = true;
                break;

            case R.id.fab:
                presentActivity(v);
                break;
        }

        //replacing the fragment
        if (fragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, fragment);
            ft.commit();
        }
        drawer.closeDrawers();
    }

    ProgressDialog pdialog;

    public void logOut() {

        pdialog = new ProgressDialog(NavigationDrawerActivity.this);
        pdialog.setMessage(getString(R.string.please_wait));
        pdialog.setCanceledOnTouchOutside(false);
        pdialog.show();

        JSONObject params = new JSONObject();
        try {
            params.put("user_id", USERID);
            params.put("session_token", TOKEN);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        Logger.e(TAG, "logOut PARA---->" + String.valueOf(params));
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.POST, Utils.MAIN_URL + Utils.LOGOUT_API, params, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Logger.e(TAG, "logOut RES---->" + String.valueOf(response));
                        try {
                            pdialog.dismiss();
                            boolean success = response.getBoolean("success");
                            boolean Auth = response.getBoolean("authenticated");
                            String msg = response.getString("message");

                            if (!Auth) {
                                clearAllData();
                            }

                            if (success) {
                                clearAllData();
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
                            pdialog.dismiss();
                        }
                    }
                });
        int socketTimeout = 30000;//30 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsObjRequest.setRetryPolicy(policy);
        requestQueue.add(jsObjRequest);
    }


    public void planCheck() {

        JSONObject params = new JSONObject();
        try {
            params.put("user_id", USERID);
            params.put("session_token", TOKEN);
            params.put("plan_id", PLAN_ID);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        Logger.e(TAG, "planCheck PARA---->" + String.valueOf(params));
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.POST, Utils.MAIN_URL + Utils.PLAN_CHECK_API, params, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Logger.e(TAG, "planCheck RES---->" + String.valueOf(response));
                        try {
                            boolean success = response.getBoolean("success");
                            boolean Auth = response.getBoolean("authenticated");
                            String msg = response.getString("message");

                            if (!success || !Auth) {
                                if (alertDialog != null && !alertDialog.isShowing()) {
                                    showPlanDialog(NavigationDrawerActivity.this, getString(R.string.alert), msg);
                                }
                            } else {
                                if (success) {
                                    String plan_active = response.getString("plan_active");
                                    if (plan_active.equals("N")) {
                                        if (alertDialog != null && !alertDialog.isShowing()) {
                                            showPlanDialog(NavigationDrawerActivity.this, getString(R.string.alert), msg);
                                        }
                                    } else {
                                        if (!response.isNull("user_info")) {
                                            JSONObject jObj = response.getJSONObject("user_info");
                                            String first_name = jObj.getString("first_name");
                                            String last_name = jObj.getString("last_name");
                                            String phone_number = jObj.getString("phone_number");
                                            String email = jObj.getString("email");
                                            String company_name = jObj.getString("company_name");
                                            String company_image = jObj.getString("company_image");
                                            String role = jObj.getString("role");
                                            String authorize = jObj.getString("authorize_text");

                                            Utils.storeString(mSharedPreferences, Utils.FIRST_NAME, first_name);
                                            Utils.storeString(mSharedPreferences, Utils.LAST_NAME, last_name);
                                            Utils.storeString(mSharedPreferences, Utils.PHONE_NUMBER, phone_number);
                                            Utils.storeString(mSharedPreferences, Utils.EMAIL, email);
                                            Utils.storeString(mSharedPreferences, Utils.COMPANY_NAME, company_name);
                                            if (Utils.validateString(company_image)) {
                                                Utils.storeString(mSharedPreferences, Utils.COMPANY_IMAGE, company_image);
                                                projectLogoUrl = company_image;
                                            }
                                            Utils.storeString(mSharedPreferences, Utils.ROLE, role);
                                            Utils.storeString(mSharedPreferences, Utils.STREET, jObj.getString("street"));
                                            Utils.storeString(mSharedPreferences, Utils.CITY, jObj.getString("city"));
                                            Utils.storeString(mSharedPreferences, Utils.STATE, jObj.getString("state"));
                                            Utils.storeString(mSharedPreferences, Utils.COUNTRY, jObj.getString("country"));
                                            Utils.storeString(mSharedPreferences, Utils.ZIPCODE, jObj.getString("zipcode"));
                                            Utils.storeString(mSharedPreferences, Utils.AUTHORIZE_TEXT, authorize);
                                        }
                                        if (!response.isNull("data")) {
                                            JSONObject dataObject = response.getJSONObject("data");
                                            String plainId = dataObject.getString("id");
                                            String planType = dataObject.getString("name");
                                            String subscription_id = dataObject.getString("subscription_id");
                                            String autoRenewal = dataObject.getString("auto_renewal");
                                            Utils.storeString(mSharedPreferences, Utils.PLAN_ID, plainId);
                                            Utils.storeString(mSharedPreferences, Utils.PLAN_TYPE, planType);
                                            Utils.storeString(mSharedPreferences, Utils.SUBSCRIPTION_ID, subscription_id);
                                            Utils.storeString(mSharedPreferences, Utils.AUTO_RENEWAL, autoRenewal);
                                        }
                                    }
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

    public void updateTimeZone() {

        JSONObject params = new JSONObject();
        try {
            params.put("user_id", USERID);
            params.put("session_token", TOKEN);
            params.put("timezone", Utils.getTimeZoneId());

        } catch (JSONException e) {
            e.printStackTrace();
        }
        Logger.e(TAG, "TimeZone PARA---->" + String.valueOf(params));
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.POST, Utils.MAIN_URL + Utils.UPDATE_TIMEZONE, params, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Logger.e(TAG, "Update timezone RES---->" + String.valueOf(response));
                        try {
                            boolean success = response.getBoolean("success");
                            boolean Auth = response.getBoolean("authenticated");
                            String msg = response.getString("message");

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


    public void presentActivity(View view) {
        ActivityOptionsCompat options = ActivityOptionsCompat.
                makeSceneTransitionAnimation(this, view, "transition");
        int revealX = (int) (view.getX() + view.getWidth() / 2);
        int revealY = (int) (view.getY() + view.getHeight() / 2);

        Intent intent = new Intent(NavigationDrawerActivity.this, AddProjectActivity.class);
        intent.putExtra(AddProjectActivity.EXTRA_CIRCULAR_REVEAL_X, revealX);
        intent.putExtra(AddProjectActivity.EXTRA_CIRCULAR_REVEAL_Y, revealY);

        ActivityCompat.startActivity(this, intent, options.toBundle());
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        if (drawer.isDrawerOpen(GravityCompat.START)) {
//            drawer.closeDrawer(GravityCompat.START);
//        }

//        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
//            getSupportFragmentManager().addOnBackStackChangedListener(getListener());
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
//            } else {
//                super.onBackPressed();
//                return;
//            }
        } else if (!doubleBackToExitPressedOnce) {
            this.doubleBackToExitPressedOnce = true;
            if (navItemIndex != 0) {
                fab.setVisibility(View.VISIBLE);
                navItemIndex = 0;
                selectMenu(navItemIndex);
                getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new HomeFragment()).commit();
            }

        } else {
            Utils.projectSyncFlag = false;
            unregisterReceiver(receiver);
            super.onBackPressed();
            return;
        }

       /* if (shouldLoadHomeFragOnBackPress) {
            if (navItemIndex != 0) {
                navItemIndex = 0;
//                displaySelectedScreen(R.id.nav_home);
//                selectNavMenu();
                selectMenu(navItemIndex);
                fab.setVisibility(View.VISIBLE);
                return;
            }
        }
        super.onBackPressed();*/

    }

    private FragmentManager.OnBackStackChangedListener getListener() {
        FragmentManager.OnBackStackChangedListener result = new FragmentManager.OnBackStackChangedListener() {
            public void onBackStackChanged() {
                FragmentManager fm = NavigationDrawerActivity.this.getSupportFragmentManager();
                Fragment fragment = null;

                if (fm != null) {
                    fragment = (Fragment) fm.findFragmentById(R.id.content_frame);
                    fragment.onResume();
                }
            }
        };

        return result;
    }

    @Override
    protected void onResume() {
        super.onResume();
        System.out.println("RESUME :::" + "SUCCESS");
//        if (!Utils.validateString(Utils.MAIN_URL)) {
//            getDomin();
//        } else {
        if (Utils.validateString(ROLE) || (Utils.validateString(PLAN_ID))) {
            if (!ROLE.equalsIgnoreCase("company") || PLAN_ID.equalsIgnoreCase("1") || PLAN_ID.equalsIgnoreCase("2")) {
                menu_linPurchaseUser.setVisibility(View.GONE);
                menu_linSalesForce.setVisibility(View.GONE);
            } else {
                menu_linPurchaseUser.setVisibility(View.VISIBLE);
                menu_linSalesForce.setVisibility(View.VISIBLE);
            }

            /*if(ROLE.equalsIgnoreCase("company") && PLAN_ID.equalsIgnoreCase("6")){
                menu_linSalesForce.setVisibility(View.VISIBLE);
            } else {
                menu_linSalesForce.setVisibility(View.GONE);
            }*/
        }
        planCheck();
        updateTimeZone();
//        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public static void selectNavMenu() {
        nvDrawer.getMenu().getItem(navItemIndex).setChecked(true);
    }


    public static void selectMenu(int pos) {
        switch (pos) {
            case 0:
                txtHome.setTextColor(Color.parseColor("#7a1417"));
                viewHome.setBackgroundResource(R.color.colorPrimaryDark);
                txtHistroy.setTextColor(Color.parseColor("#555555"));
                viewHistroy.setBackgroundResource(R.color.lineBorderColor);
                txtSalesForce.setTextColor(Color.parseColor("#555555"));
                viewSalesForce.setBackgroundResource(R.color.lineBorderColor);
                txtPurchaseUser.setTextColor(Color.parseColor("#555555"));
                viewPurchaseUser.setBackgroundResource(R.color.lineBorderColor);
                txtTechnicalSupport.setTextColor(Color.parseColor("#555555"));
                viewTechnicalSupport.setBackgroundResource(R.color.lineBorderColor);
                txtVideoGuide.setTextColor(Color.parseColor("#555555"));
                viewVideoGuide.setBackgroundResource(R.color.lineBorderColor);
                txtFAQ.setTextColor(Color.parseColor("#555555"));
                viewFAQ.setBackgroundResource(R.color.lineBorderColor);
                txtAbout.setTextColor(Color.parseColor("#555555"));
                viewAbout.setBackgroundResource(R.color.lineBorderColor);
                txtPrivacyPolicy.setTextColor(Color.parseColor("#555555"));
                viewPrivacyPolicy.setBackgroundResource(R.color.lineBorderColor);
                txtTermsAndCondtion.setTextColor(Color.parseColor("#555555"));
                viewTermsAndCondtion.setBackgroundResource(R.color.lineBorderColor);
                txtLogout.setTextColor(Color.parseColor("#555555"));
                viewLogout.setBackgroundResource(R.color.lineBorderColor);
                break;
            case 1:
                txtHome.setTextColor(Color.parseColor("#555555"));
                viewHome.setBackgroundResource(R.color.lineBorderColor);
                txtHistroy.setTextColor(Color.parseColor("#7a1417"));
                viewHistroy.setBackgroundResource(R.color.colorPrimaryDark);
                txtSalesForce.setTextColor(Color.parseColor("#555555"));
                viewSalesForce.setBackgroundResource(R.color.lineBorderColor);
                txtPurchaseUser.setTextColor(Color.parseColor("#555555"));
                viewPurchaseUser.setBackgroundResource(R.color.lineBorderColor);
                txtTechnicalSupport.setTextColor(Color.parseColor("#555555"));
                viewTechnicalSupport.setBackgroundResource(R.color.lineBorderColor);
                txtVideoGuide.setTextColor(Color.parseColor("#555555"));
                viewVideoGuide.setBackgroundResource(R.color.lineBorderColor);
                txtFAQ.setTextColor(Color.parseColor("#555555"));
                viewFAQ.setBackgroundResource(R.color.lineBorderColor);
                txtAbout.setTextColor(Color.parseColor("#555555"));
                viewAbout.setBackgroundResource(R.color.lineBorderColor);
                txtPrivacyPolicy.setTextColor(Color.parseColor("#555555"));
                viewPrivacyPolicy.setBackgroundResource(R.color.lineBorderColor);
                txtTermsAndCondtion.setTextColor(Color.parseColor("#555555"));
                viewTermsAndCondtion.setBackgroundResource(R.color.lineBorderColor);
                txtLogout.setTextColor(Color.parseColor("#555555"));
                viewLogout.setBackgroundResource(R.color.lineBorderColor);
                break;
            case 2:
                txtHome.setTextColor(Color.parseColor("#555555"));
                viewHome.setBackgroundResource(R.color.lineBorderColor);
                txtHistroy.setTextColor(Color.parseColor("#555555"));
                viewHistroy.setBackgroundResource(R.color.lineBorderColor);
                txtSalesForce.setTextColor(Color.parseColor("#7a1417"));
                viewSalesForce.setBackgroundResource(R.color.colorPrimaryDark);
                txtPurchaseUser.setTextColor(Color.parseColor("#555555"));
                viewPurchaseUser.setBackgroundResource(R.color.lineBorderColor);
                txtTechnicalSupport.setTextColor(Color.parseColor("#555555"));
                viewTechnicalSupport.setBackgroundResource(R.color.lineBorderColor);
                txtVideoGuide.setTextColor(Color.parseColor("#555555"));
                viewVideoGuide.setBackgroundResource(R.color.lineBorderColor);
                txtFAQ.setTextColor(Color.parseColor("#555555"));
                viewFAQ.setBackgroundResource(R.color.lineBorderColor);
                txtAbout.setTextColor(Color.parseColor("#555555"));
                viewAbout.setBackgroundResource(R.color.lineBorderColor);
                txtPrivacyPolicy.setTextColor(Color.parseColor("#555555"));
                viewPrivacyPolicy.setBackgroundResource(R.color.lineBorderColor);
                txtTermsAndCondtion.setTextColor(Color.parseColor("#555555"));
                viewTermsAndCondtion.setBackgroundResource(R.color.lineBorderColor);
                txtLogout.setTextColor(Color.parseColor("#555555"));
                viewLogout.setBackgroundResource(R.color.lineBorderColor);
                break;
            case 3:
                txtHome.setTextColor(Color.parseColor("#555555"));
                viewHome.setBackgroundResource(R.color.lineBorderColor);
                txtHistroy.setTextColor(Color.parseColor("#555555"));
                viewHistroy.setBackgroundResource(R.color.lineBorderColor);
                txtSalesForce.setTextColor(Color.parseColor("#555555"));
                viewSalesForce.setBackgroundResource(R.color.lineBorderColor);
                txtPurchaseUser.setTextColor(Color.parseColor("#7a1417"));
                viewPurchaseUser.setBackgroundResource(R.color.colorPrimaryDark);
                txtTechnicalSupport.setTextColor(Color.parseColor("#555555"));
                viewTechnicalSupport.setBackgroundResource(R.color.lineBorderColor);
                txtVideoGuide.setTextColor(Color.parseColor("#555555"));
                viewVideoGuide.setBackgroundResource(R.color.lineBorderColor);
                txtFAQ.setTextColor(Color.parseColor("#555555"));
                viewFAQ.setBackgroundResource(R.color.lineBorderColor);
                txtAbout.setTextColor(Color.parseColor("#555555"));
                viewAbout.setBackgroundResource(R.color.lineBorderColor);
                txtPrivacyPolicy.setTextColor(Color.parseColor("#555555"));
                viewPrivacyPolicy.setBackgroundResource(R.color.lineBorderColor);
                txtTermsAndCondtion.setTextColor(Color.parseColor("#555555"));
                viewTermsAndCondtion.setBackgroundResource(R.color.lineBorderColor);
                txtLogout.setTextColor(Color.parseColor("#555555"));
                viewLogout.setBackgroundResource(R.color.lineBorderColor);
                break;
            case 4:
                txtHome.setTextColor(Color.parseColor("#555555"));
                viewHome.setBackgroundResource(R.color.lineBorderColor);
                txtHistroy.setTextColor(Color.parseColor("#555555"));
                viewHistroy.setBackgroundResource(R.color.lineBorderColor);
                txtSalesForce.setTextColor(Color.parseColor("#555555"));
                viewSalesForce.setBackgroundResource(R.color.lineBorderColor);
                txtPurchaseUser.setTextColor(Color.parseColor("#555555"));
                viewPurchaseUser.setBackgroundResource(R.color.lineBorderColor);
                txtTechnicalSupport.setTextColor(Color.parseColor("#7a1417"));
                viewTechnicalSupport.setBackgroundResource(R.color.colorPrimaryDark);
                txtVideoGuide.setTextColor(Color.parseColor("#555555"));
                viewVideoGuide.setBackgroundResource(R.color.lineBorderColor);
                txtFAQ.setTextColor(Color.parseColor("#555555"));
                viewFAQ.setBackgroundResource(R.color.lineBorderColor);
                txtAbout.setTextColor(Color.parseColor("#555555"));
                viewAbout.setBackgroundResource(R.color.lineBorderColor);
                txtPrivacyPolicy.setTextColor(Color.parseColor("#555555"));
                viewPrivacyPolicy.setBackgroundResource(R.color.lineBorderColor);
                txtTermsAndCondtion.setTextColor(Color.parseColor("#555555"));
                viewTermsAndCondtion.setBackgroundResource(R.color.lineBorderColor);
                txtLogout.setTextColor(Color.parseColor("#555555"));
                viewLogout.setBackgroundResource(R.color.lineBorderColor);
                break;
            case 5:
                txtHome.setTextColor(Color.parseColor("#555555"));
                viewHome.setBackgroundResource(R.color.lineBorderColor);
                txtHistroy.setTextColor(Color.parseColor("#555555"));
                viewHistroy.setBackgroundResource(R.color.lineBorderColor);
                txtSalesForce.setTextColor(Color.parseColor("#555555"));
                viewSalesForce.setBackgroundResource(R.color.lineBorderColor);
                txtPurchaseUser.setTextColor(Color.parseColor("#555555"));
                viewPurchaseUser.setBackgroundResource(R.color.lineBorderColor);
                txtTechnicalSupport.setTextColor(Color.parseColor("#555555"));
                viewTechnicalSupport.setBackgroundResource(R.color.lineBorderColor);
                txtVideoGuide.setTextColor(Color.parseColor("#7a1417"));
                viewVideoGuide.setBackgroundResource(R.color.colorPrimaryDark);
                txtFAQ.setTextColor(Color.parseColor("#555555"));
                viewFAQ.setBackgroundResource(R.color.lineBorderColor);
                txtAbout.setTextColor(Color.parseColor("#555555"));
                viewAbout.setBackgroundResource(R.color.lineBorderColor);
                txtPrivacyPolicy.setTextColor(Color.parseColor("#555555"));
                viewPrivacyPolicy.setBackgroundResource(R.color.lineBorderColor);
                txtTermsAndCondtion.setTextColor(Color.parseColor("#555555"));
                viewTermsAndCondtion.setBackgroundResource(R.color.lineBorderColor);
                txtLogout.setTextColor(Color.parseColor("#555555"));
                viewLogout.setBackgroundResource(R.color.lineBorderColor);
                break;
            case 6:
                txtHome.setTextColor(Color.parseColor("#555555"));
                viewHome.setBackgroundResource(R.color.lineBorderColor);
                txtHistroy.setTextColor(Color.parseColor("#555555"));
                viewHistroy.setBackgroundResource(R.color.lineBorderColor);
                txtSalesForce.setTextColor(Color.parseColor("#555555"));
                viewSalesForce.setBackgroundResource(R.color.lineBorderColor);
                txtPurchaseUser.setTextColor(Color.parseColor("#555555"));
                viewPurchaseUser.setBackgroundResource(R.color.lineBorderColor);
                txtTechnicalSupport.setTextColor(Color.parseColor("#555555"));
                viewTechnicalSupport.setBackgroundResource(R.color.lineBorderColor);
                txtVideoGuide.setTextColor(Color.parseColor("#555555"));
                viewVideoGuide.setBackgroundResource(R.color.lineBorderColor);
                txtFAQ.setTextColor(Color.parseColor("#7a1417"));
                viewFAQ.setBackgroundResource(R.color.colorPrimaryDark);
                txtAbout.setTextColor(Color.parseColor("#555555"));
                viewAbout.setBackgroundResource(R.color.lineBorderColor);
                txtPrivacyPolicy.setTextColor(Color.parseColor("#555555"));
                viewPrivacyPolicy.setBackgroundResource(R.color.lineBorderColor);
                txtTermsAndCondtion.setTextColor(Color.parseColor("#555555"));
                viewTermsAndCondtion.setBackgroundResource(R.color.lineBorderColor);
                txtLogout.setTextColor(Color.parseColor("#555555"));
                viewLogout.setBackgroundResource(R.color.lineBorderColor);
                break;
            case 7:
                txtHome.setTextColor(Color.parseColor("#555555"));
                viewHome.setBackgroundResource(R.color.lineBorderColor);
                txtHistroy.setTextColor(Color.parseColor("#555555"));
                viewHistroy.setBackgroundResource(R.color.lineBorderColor);
                txtSalesForce.setTextColor(Color.parseColor("#555555"));
                viewSalesForce.setBackgroundResource(R.color.lineBorderColor);
                txtPurchaseUser.setTextColor(Color.parseColor("#555555"));
                viewPurchaseUser.setBackgroundResource(R.color.lineBorderColor);
                txtTechnicalSupport.setTextColor(Color.parseColor("#555555"));
                viewTechnicalSupport.setBackgroundResource(R.color.lineBorderColor);
                txtVideoGuide.setTextColor(Color.parseColor("#555555"));
                viewVideoGuide.setBackgroundResource(R.color.lineBorderColor);
                txtFAQ.setTextColor(Color.parseColor("#555555"));
                viewFAQ.setBackgroundResource(R.color.lineBorderColor);
                txtAbout.setTextColor(Color.parseColor("#7a1417"));
                viewAbout.setBackgroundResource(R.color.colorPrimaryDark);
                txtPrivacyPolicy.setTextColor(Color.parseColor("#555555"));
                viewPrivacyPolicy.setBackgroundResource(R.color.lineBorderColor);
                txtTermsAndCondtion.setTextColor(Color.parseColor("#555555"));
                viewTermsAndCondtion.setBackgroundResource(R.color.lineBorderColor);
                txtLogout.setTextColor(Color.parseColor("#555555"));
                viewLogout.setBackgroundResource(R.color.lineBorderColor);
                break;
            case 8:
                txtHome.setTextColor(Color.parseColor("#555555"));
                viewHome.setBackgroundResource(R.color.lineBorderColor);
                txtHistroy.setTextColor(Color.parseColor("#555555"));
                viewHistroy.setBackgroundResource(R.color.lineBorderColor);
                txtSalesForce.setTextColor(Color.parseColor("#555555"));
                viewSalesForce.setBackgroundResource(R.color.lineBorderColor);
                txtPurchaseUser.setTextColor(Color.parseColor("#555555"));
                viewPurchaseUser.setBackgroundResource(R.color.lineBorderColor);
                txtTechnicalSupport.setTextColor(Color.parseColor("#555555"));
                viewTechnicalSupport.setBackgroundResource(R.color.lineBorderColor);
                txtVideoGuide.setTextColor(Color.parseColor("#555555"));
                viewVideoGuide.setBackgroundResource(R.color.lineBorderColor);
                txtFAQ.setTextColor(Color.parseColor("#555555"));
                viewFAQ.setBackgroundResource(R.color.lineBorderColor);
                txtAbout.setTextColor(Color.parseColor("#555555"));
                viewAbout.setBackgroundResource(R.color.lineBorderColor);
                txtPrivacyPolicy.setTextColor(Color.parseColor("#7a1417"));
                viewPrivacyPolicy.setBackgroundResource(R.color.colorPrimaryDark);
                txtTermsAndCondtion.setTextColor(Color.parseColor("#555555"));
                viewTermsAndCondtion.setBackgroundResource(R.color.lineBorderColor);
                txtLogout.setTextColor(Color.parseColor("#555555"));
                viewLogout.setBackgroundResource(R.color.lineBorderColor);
                break;

            case 9:
                txtHome.setTextColor(Color.parseColor("#555555"));
                viewHome.setBackgroundResource(R.color.lineBorderColor);
                txtHistroy.setTextColor(Color.parseColor("#555555"));
                viewHistroy.setBackgroundResource(R.color.lineBorderColor);
                txtSalesForce.setTextColor(Color.parseColor("#555555"));
                viewSalesForce.setBackgroundResource(R.color.lineBorderColor);
                txtPurchaseUser.setTextColor(Color.parseColor("#555555"));
                viewPurchaseUser.setBackgroundResource(R.color.lineBorderColor);
                txtTechnicalSupport.setTextColor(Color.parseColor("#555555"));
                viewTechnicalSupport.setBackgroundResource(R.color.lineBorderColor);
                txtVideoGuide.setTextColor(Color.parseColor("#555555"));
                viewVideoGuide.setBackgroundResource(R.color.lineBorderColor);
                txtFAQ.setTextColor(Color.parseColor("#555555"));
                viewFAQ.setBackgroundResource(R.color.lineBorderColor);
                txtAbout.setTextColor(Color.parseColor("#555555"));
                viewAbout.setBackgroundResource(R.color.lineBorderColor);
                txtPrivacyPolicy.setTextColor(Color.parseColor("#555555"));
                viewPrivacyPolicy.setBackgroundResource(R.color.lineBorderColor);
                txtTermsAndCondtion.setTextColor(Color.parseColor("#7a1417"));
                viewTermsAndCondtion.setBackgroundResource(R.color.colorPrimaryDark);
                txtLogout.setTextColor(Color.parseColor("#555555"));
                viewLogout.setBackgroundResource(R.color.lineBorderColor);
                break;

            case 10:
                txtHome.setTextColor(Color.parseColor("#555555"));
                viewHome.setBackgroundResource(R.color.lineBorderColor);
                txtHistroy.setTextColor(Color.parseColor("#555555"));
                viewHistroy.setBackgroundResource(R.color.lineBorderColor);
                txtSalesForce.setTextColor(Color.parseColor("#555555"));
                viewSalesForce.setBackgroundResource(R.color.lineBorderColor);
                txtPurchaseUser.setTextColor(Color.parseColor("#555555"));
                viewPurchaseUser.setBackgroundResource(R.color.lineBorderColor);
                txtTechnicalSupport.setTextColor(Color.parseColor("#555555"));
                viewTechnicalSupport.setBackgroundResource(R.color.lineBorderColor);
                txtVideoGuide.setTextColor(Color.parseColor("#555555"));
                viewVideoGuide.setBackgroundResource(R.color.lineBorderColor);
                txtFAQ.setTextColor(Color.parseColor("#555555"));
                viewFAQ.setBackgroundResource(R.color.lineBorderColor);
                txtAbout.setTextColor(Color.parseColor("#555555"));
                viewAbout.setBackgroundResource(R.color.lineBorderColor);
                txtPrivacyPolicy.setTextColor(Color.parseColor("#555555"));
                viewPrivacyPolicy.setBackgroundResource(R.color.lineBorderColor);
                txtTermsAndCondtion.setTextColor(Color.parseColor("#555555"));
                viewTermsAndCondtion.setBackgroundResource(R.color.lineBorderColor);
                txtLogout.setTextColor(Color.parseColor("#7a1417"));
                viewLogout.setBackgroundResource(R.color.colorPrimaryDark);
                break;
        }

    }

    public void loadDrawer() {
        if (drawer.isDrawerVisible(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            drawer.openDrawer(GravityCompat.START);
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

//        displaySelectedScreen(item.getItemId());
//        item.setChecked(true);
//        setTitle(item.getTitle());
        return true;
    }

    /*private void displaySelectedScreen(int itemId) {

        Fragment fragment = null;

        //initializing the fragment object which is selected
        switch (itemId) {
            case R.id.nav_home:
                fragment = new HomeFragment();
                navItemIndex = 0;
                Utils.storeString(mSharedPreferences, Utils.ITEM_FILTER_STATUS, "");
                fab.setVisibility(View.VISIBLE);
                break;
            *//*case R.id.nav_report:
                fab.setVisibility(View.GONE);
                break;*//*
            case R.id.nav_history:
                fragment = new HistoryFragment();
                navItemIndex = 1;
                Utils.storeString(mSharedPreferences, Utils.ITEM_FILTER_STATUS, "");
                fab.setVisibility(View.GONE);
                break;
            case R.id.nav_about:
                fragment = new AboutFragment();
                navItemIndex = 2;
                fab.setVisibility(View.GONE);
                break;
            case R.id.nav_privacy_policy:
                fragment = new PrivacyPilocyFragment();
                navItemIndex = 3;
                fab.setVisibility(View.GONE);
                break;
            case R.id.nav_logout:
                showLogoutDialog(this, "Rogi", "Are you sure you want to logout?");
                break;
        }
        //replacing the fragment
        if (fragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, fragment);
            ft.commit();
        }
        drawer.closeDrawers();
    }*/


    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    @SuppressLint("ResourceAsColor")
    public static void setImage(Context context) {
        Logger.e(TAG, "setImage");
        mSharedPreferences = context.getSharedPreferences(Utils.PREF_NAME, Context.MODE_PRIVATE);

        WindowManager w = ((Activity) context).getWindowManager();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            Point size = new Point();
            w.getDefaultDisplay().getSize(size);
            mDeviceWidth = size.x;
        } else {
            Display d = w.getDefaultDisplay();
            mDeviceWidth = d.getWidth();
        }

        txtUserName = (TextView) navHeader.findViewById(R.id.txtUserName);
        txtEmailId = (TextView) navHeader.findViewById(R.id.txtEmailId);

        txtUserName.setText(mSharedPreferences.getString(Utils.FIRST_NAME, "") + " " + mSharedPreferences.getString(Utils.LAST_NAME, ""));
        txtEmailId.setText(mSharedPreferences.getString(Utils.EMAIL, ""));

        cirProfileImage = (ImageView) navHeader.findViewById(R.id.cirProfileImage);

        if (!mSharedPreferences.getString(Utils.PROFILE_IMAGE, "").isEmpty()) {

            Transformation transformation = new Transformation() {

                @Override
                public Bitmap transform(Bitmap source) {
                    int targetWidth = mDeviceWidth;

                    double aspectRatio = (double) source.getHeight() / (double) source.getWidth();
                    int targetHeight = (int) (targetWidth * aspectRatio);
                    if (targetHeight > targetWidth) {
                        targetHeight = targetWidth;
                    }
                    Bitmap result = Bitmap.createScaledBitmap(source, targetWidth, targetHeight, false);
                    if (result != source) {
                        // Same bitmap is returned if sizes are the same
                        source.recycle();
                    }
                    return result;
                }

                @Override
                public String key() {
                    return "transformation" + " desiredWidth";
                }
            };

            Picasso.with(context)
                    .load(mSharedPreferences.getString(Utils.PROFILE_IMAGE, ""))
                    .placeholder(R.mipmap.profile_logo)
                    .error(R.mipmap.profile_logo)
                    .transform(transformation)
                    .centerCrop()
                    .resize(mDeviceWidth, (int) (mDeviceWidth))
                    .into(cirProfileImage);
        }
    }

    public void setComanyImage(String url) {
        if (Utils.validateString(url)) {

            Transformation transformation = new Transformation() {

                @Override
                public Bitmap transform(Bitmap source) {
                    int targetWidth = mDeviceWidth;

                    double aspectRatio = (double) source.getHeight() / (double) source.getWidth();
                    int targetHeight = (int) (targetWidth * aspectRatio);
                    if (targetHeight > targetWidth) {
                        targetHeight = targetWidth;
                    }
                    Bitmap result = Bitmap.createScaledBitmap(source, targetWidth, targetHeight, false);
                    if (result != source) {
                        // Same bitmap is returned if sizes are the same
                        source.recycle();
                    }
                    return result;
                }

                @Override
                public String key() {
                    return "transformation" + " desiredWidth";
                }
            };

            Picasso.with(NavigationDrawerActivity.this)
                    .load(url)
                    .placeholder(R.mipmap.title_img)
                    .error(R.mipmap.title_img)
                    .transform(transformation)
                    .centerCrop()
                    .resize(mDeviceWidth, (int) (mDeviceWidth * 0.25))
                    .into(imgProjectLogo);
        }
    }


    AlertDialog alertDialog;

    public void showLogoutDialog(final Context context, String title, String message) {
        if (message != null && message.trim().length() > 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle(title);
            builder.setCancelable(false);
            builder.setMessage(message);
            builder.setPositiveButton("Yes",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            if (Utils.checkInternetConnection(context)) {
                                alertDialog.dismiss();
                                logOut();
                            } else {
                                clearAllData();
                            }


                        }
                    });

            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();


                }
            });
            // create alert dialog
            alertDialog = builder.create();
            // show it
            alertDialog.show();
        }
    }

    public void showPlanDialog(final Context context, String title, String message) {
        if (message != null && message.trim().length() > 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle(title);
            builder.setCancelable(false);
            builder.setMessage(message);
            builder.setPositiveButton("OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            alertDialog.dismiss();
                            clearAllData();

                        }
                    });
            // create alert dialog
            alertDialog = builder.create();
            // show it
            alertDialog.show();
        }
    }

    /*public void getComplanyList() {

        JSONObject params = new JSONObject();
        try {
            params.put("user_id", USERID);
            params.put("session_token", TOKEN);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        Logger.e(TAG, "Complany PARA---->" + String.valueOf(params));
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.POST, Utils.COMPANY_LIST_API, params, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Logger.e(TAG, "URL ::" + Utils.COMPANY_LIST_API);
                        Logger.e(TAG, "Complany RES---->" + String.valueOf(response));
                        try {
                            boolean success = response.getBoolean("success");
                            String msg = response.getString("message");

                            if (success) {
                                JSONArray dataArray = response.getJSONArray("data");
                                Utils.storeString(mSharedPreferences, Utils.COMPANY_LIST, dataArray.toString());
                            } else {
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Logger.e("ERR", e.getMessage());
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error instanceof NoConnectionError) {
                        }
                    }
                });
        requestQueue.add(jsObjRequest);
    }*/

    public void getProjectResponsibilityList() {

        JSONObject params = new JSONObject();
        try {
            params.put("user_id", USERID);
            params.put("session_token", TOKEN);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        Logger.e(TAG, "ProjectResponsibility PARA---->" + String.valueOf(params));
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.POST, Utils.MAIN_URL + Utils.PROJECT_RESPONIBILITY_API, params, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Logger.e(TAG, "URL ::" + Utils.MAIN_URL + Utils.PROJECT_RESPONIBILITY_API);
                        Logger.e(TAG, "ProjectResponsibility RES---->" + String.valueOf(response));
                        try {
                            boolean success = response.getBoolean("success");
                            String msg = response.getString("message");

                            if (success) {
                                JSONArray dataArray = response.getJSONArray("data");
                                Utils.storeString(mSharedPreferences, Utils.RESPONSIBILITY_LIST, dataArray.toString());
                            } else {
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Logger.e("ERR", e.getMessage());
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

    public void syncRequest() {
        try {
            myDbHelper.openDataBase();

            projectModelArrayList = new ArrayList<>();
            projectModelArrayList = myDbHelper.getSyncProjectDetails();

            notesModelArrayList = new ArrayList<>();
            notesModelArrayList = myDbHelper.getSyncProjectNotesDetails();

            projectPhaseModelArrayList = new ArrayList<>();
            projectPhaseModelArrayList = myDbHelper.getSyncProjectPhaseDetails();

            additionalContactModelArrayList = new ArrayList<>();
            additionalContactModelArrayList = myDbHelper.getSyncAdditionalContactDetails();

            myDbHelper.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (projectModelArrayList.size() > 0 || !projectModelArrayList.isEmpty()) {
            JSONArray syncProjectArray, syncNotesArray, syncPhaseArray, syncContactArray;
            String status = "";
            syncProjectArray = new JSONArray();

            try {
                for (int i = 0; i < projectModelArrayList.size(); i++) {
                    try {
                        JSONObject object = new JSONObject();
                        if (projectModelArrayList.get(i).getProject_update_status().equalsIgnoreCase("A")) {
                            object.put("id", "");
                        } else {
                            object.put("id", projectModelArrayList.get(i).getId());
                        }
                        object.put("title", projectModelArrayList.get(i).getTitle());
                        object.put("description", projectModelArrayList.get(i).getDescription());
                        object.put("start_date", projectModelArrayList.get(i).getStartDate());
                        object.put("start_time", projectModelArrayList.get(i).getStartTime());
                        object.put("due_date", projectModelArrayList.get(i).getDueDate());
                        object.put("due_time", projectModelArrayList.get(i).getDueTime());
                        object.put("priority", projectModelArrayList.get(i).getPriority());
                        if (projectModelArrayList.get(i).getProject_status().equalsIgnoreCase(Utils.START) ||
                                (projectModelArrayList.get(i).getProject_status().equalsIgnoreCase(Utils.OPEN) &&
                                        !Utils.validateString(projectModelArrayList.get(i).getProject_start_time()))) {
                            status = Utils.OPEN;
                        } else if (projectModelArrayList.get(i).getProject_status().equalsIgnoreCase(Utils.OPEN) &&
                                Utils.validateString(projectModelArrayList.get(i).getProject_start_time())) {
                            status = Utils.OPEN;
                        } else if (projectModelArrayList.get(i).getProject_status().equalsIgnoreCase(Utils.REPORT) &&
                                Utils.validateString(projectModelArrayList.get(i).getSignature_image())) {
                            status = Utils.REPORT;
                        }
                        object.put("project_status", status);
                        object.put("project_start_time", projectModelArrayList.get(i).getProject_start_time());
                        object.put("street", projectModelArrayList.get(i).getStreet());
                        object.put("city", projectModelArrayList.get(i).getCity());
                        object.put("state", projectModelArrayList.get(i).getState());
                        object.put("country", projectModelArrayList.get(i).getCountry());
                        object.put("pincode", projectModelArrayList.get(i).getPincode());
                        object.put("lattitude", projectModelArrayList.get(i).getLatitude());
                        object.put("longitude", projectModelArrayList.get(i).getLongitude());
                        object.put("signature_image", projectModelArrayList.get(i).getSignature_image());
                        object.put("project_operation", projectModelArrayList.get(i).getProject_update_status());
                        object.put("is_sync", projectModelArrayList.get(i).getIs_sync());
                        object.put("project_temp_id", projectModelArrayList.get(i).getId());
                        object.put("created_date", projectModelArrayList.get(i).getCreated_date());
                        object.put("remind_hours", projectModelArrayList.get(i).getReminder_hours());

                        syncNotesArray = new JSONArray();
                        if (notesModelArrayList.size() > 0) {
                            for (int j = 0; j < notesModelArrayList.size(); j++) {
                                if (projectModelArrayList.get(i).getId().equalsIgnoreCase(notesModelArrayList.get(j).getProject_id())) {
                                    JSONObject notesObj = new JSONObject();
                                    if (notesModelArrayList.get(j).getNote_status().equalsIgnoreCase("A")) {
                                        notesObj.put("id", "");
                                    } else {
                                        notesObj.put("id", notesModelArrayList.get(j).getNoteId());
                                    }
                                    notesObj.put("note", notesModelArrayList.get(j).getNote());
                                    notesObj.put("project_notes_temp_id", notesModelArrayList.get(j).getNoteId());
                                    notesObj.put("created_date", notesModelArrayList.get(j).getNote_created_date());
                                    notesObj.put("notes_operation", notesModelArrayList.get(j).getNote_status());
                                    notesObj.put("is_sync", notesModelArrayList.get(j).getIs_sync());
                                    syncNotesArray.put(notesObj);
                                }
                            }
                        }
                        object.put("project_notes", syncNotesArray);

                        syncPhaseArray = new JSONArray();
                        if (projectPhaseModelArrayList.size() > 0) {
                            for (int j = 0; j < projectPhaseModelArrayList.size(); j++) {
                                if (projectModelArrayList.get(i).getId().equalsIgnoreCase(projectPhaseModelArrayList.get(j).getProject_id())) {
                                    JSONObject phaseObj = new JSONObject();
                                    if (projectPhaseModelArrayList.get(j).getPhase_status().equalsIgnoreCase("A")) {
                                        phaseObj.put("project_phase_id", "");
                                    } else {
                                        phaseObj.put("project_phase_id", projectPhaseModelArrayList.get(j).getProjectPhaseId());
                                    }
                                    phaseObj.put("description", projectPhaseModelArrayList.get(j).getProjectDesription());
                                    phaseObj.put("project_phase_temp_id", projectPhaseModelArrayList.get(j).getProjectPhaseId());
                                    phaseObj.put("created_date", projectPhaseModelArrayList.get(j).getProjectCreatedDate());
                                    phaseObj.put("project_phase_operation", projectPhaseModelArrayList.get(j).getPhase_status());
                                    phaseObj.put("is_sync", projectPhaseModelArrayList.get(j).getIs_sync());
                                    syncPhaseArray.put(phaseObj);
                                }
                            }
                        }
                        object.put("project_phase", syncPhaseArray);

                        syncContactArray = new JSONArray();
                        if (additionalContactModelArrayList.size() > 0) {
                            for (int j = 0; j < additionalContactModelArrayList.size(); j++) {
                                if (projectModelArrayList.get(i).getId().equalsIgnoreCase(additionalContactModelArrayList.get(j).getProject_id())) {
                                    JSONObject contactObj = new JSONObject();
                                    if (additionalContactModelArrayList.get(j).getContact_status().equalsIgnoreCase("A")) {
                                        contactObj.put("additional_contact_id", "");
                                    } else {
                                        contactObj.put("additional_contact_id", additionalContactModelArrayList.get(j).getAdtnlContactID());
                                    }
                                    contactObj.put("name", additionalContactModelArrayList.get(j).getAdtnlContactName());
                                    contactObj.put("company", additionalContactModelArrayList.get(j).getAdtnlContactCompany());
                                    contactObj.put("address", additionalContactModelArrayList.get(j).getAdtnlContactAddress());
                                    contactObj.put("phone", additionalContactModelArrayList.get(j).getAdtnlContactPhone());
                                    contactObj.put("email", additionalContactModelArrayList.get(j).getAdtnlContactEmail());
                                    contactObj.put("project_responsiblity_id", additionalContactModelArrayList.get(j).getAdtnlContactResponsibilityId());
                                    contactObj.put("created_date", additionalContactModelArrayList.get(j).getCreated_date());
                                    contactObj.put("additional_contact_operation", additionalContactModelArrayList.get(j).getContact_status());
                                    contactObj.put("additional_contact_temp_id", additionalContactModelArrayList.get(j).getAdtnlContactID());
                                    contactObj.put("is_sync", additionalContactModelArrayList.get(j).getIs_sync());
                                    syncContactArray.put(contactObj);
                                }

                            }
                        }
                        object.put("additional_contact", syncContactArray);
                        syncProjectArray.put(object);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                Logger.d(TAG, "Sync Array : " + syncProjectArray);
                syncProject(syncProjectArray);

            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
    }

    public void syncProject(JSONArray syncProjectArray) {

        pdialog = new ProgressDialog(NavigationDrawerActivity.this);
        pdialog.setMessage(getString(R.string.please_wait));
        pdialog.setCanceledOnTouchOutside(false);
        pdialog.setCancelable(false);
        pdialog.show();

        JSONObject params = new JSONObject();
        try {
            params.put("user_id", USERID);
            params.put("session_token", TOKEN);
            params.put("synk_project", syncProjectArray);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Logger.e(TAG, "syncProject PARA---->" + String.valueOf(params));
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.POST, Utils.MAIN_URL + Utils.SYNC_PROJECT, params, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Logger.e(TAG, "syncProject RES---->" + String.valueOf(response));
                        pdialog.dismiss();
                        try {
                            boolean success = response.getBoolean("success");
                            String msg = response.getString("message");
                            if (success) {
                                JSONArray dataArray = response.getJSONArray("data");
                                if (dataArray.length() > 0) {
                                    setSyncProkectList(dataArray);
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
                            pdialog.dismiss();
                        }
                    }
                });
        int socketTimeout = 30000;//30 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsObjRequest.setRetryPolicy(policy);
        requestQueue.add(jsObjRequest);
    }

    public void setSyncProkectList(JSONArray dataArray) {
        try {
            for (int i = 0; i < dataArray.length(); i++) {
                try {
                    myDbHelper.openDataBase();
                    JSONObject object = dataArray.getJSONObject(i);
                    String project_temp_id = object.getString("project_temp_id");
                    boolean successProject = myDbHelper.recordExistOrNot(myDbHelper.myDataBase, DBConstants.TABLE.PROJECT_DETAILS,
                            DBConstants.Project_details.id, project_temp_id);
                    if (successProject) {
                        myDbHelper.updateSyncProject(myDbHelper.myDataBase, DBConstants.TABLE.PROJECT_DETAILS,
                                object.getString("id"), object.getString("is_sync"), DBConstants.Project_details.project_update_status,
                                object.getString("project_operation"), project_temp_id);

                    }

                    boolean successMedia = myDbHelper.recordExistOrNot(myDbHelper.myDataBase,
                            DBConstants.TABLE.MEDIA_DETAILS, DBConstants.Media_details.project_id, project_temp_id);

                    if (successMedia)
                        myDbHelper.updateMediaProjectID(myDbHelper.myDataBase, DBConstants.TABLE.MEDIA_DETAILS, object.getString("id"),
                                project_temp_id);


                    if (!object.isNull("project_notes")) {
                        JSONArray notesArray = object.getJSONArray("project_notes");
                        if (notesArray.length() > 0) {
                            for (int j = 0; j < notesArray.length(); j++) {
                                JSONObject notesObj = notesArray.getJSONObject(j);
                                String note_temp_id = notesObj.getString("note_temp_id");
                                boolean successNote = myDbHelper.recordExistOrNot(myDbHelper.myDataBase,
                                        DBConstants.TABLE.NOTES_DETAILS, DBConstants.Notes_details.id, note_temp_id);

                                if (successNote) {
                                    myDbHelper.updateSyncProjectDetails(myDbHelper.myDataBase, DBConstants.TABLE.NOTES_DETAILS,
                                            notesObj.getString("id"), notesObj.getString("is_sync"), object.getString("id"),
                                            DBConstants.Notes_details.note_status, notesObj.getString("notes_operation"),
                                            note_temp_id);

                                }

                            }
                        }
                    }

                    if (!object.isNull("project_phase_data")) {
                        JSONArray projectPhaseArray = object.getJSONArray("project_phase_data");
                        if (projectPhaseArray.length() > 0) {
                            for (int j = 0; j < projectPhaseArray.length(); j++) {
                                JSONObject projectPhaseObj = projectPhaseArray.getJSONObject(j);
                                String project_phase_temp_id = projectPhaseObj.getString("project_phase_temp_id");
                                boolean successPhase = myDbHelper.recordExistOrNot(myDbHelper.myDataBase,
                                        DBConstants.TABLE.PHASE_DETAILS, DBConstants.Phase_details.id, project_phase_temp_id);

                                if (successPhase) {
                                    myDbHelper.updateSyncProjectDetails(myDbHelper.myDataBase, DBConstants.TABLE.PHASE_DETAILS,
                                            projectPhaseObj.getString("id"), projectPhaseObj.getString("is_sync"), object.getString("id"),
                                            DBConstants.Phase_details.phase_status, projectPhaseObj.getString("project_phase_operation"),
                                            project_phase_temp_id);

                                }
                            }
                        }
                    }

                    if (!object.isNull("additional_contacts")) {
                        JSONArray additionalContactsArray = object.getJSONArray("additional_contacts");
                        if (additionalContactsArray.length() > 0) {
                            for (int j = 0; j < additionalContactsArray.length(); j++) {
                                JSONObject contactObject = additionalContactsArray.getJSONObject(j);
                                String additional_contact_temp_id = contactObject.getString("additional_contact_temp_id");

                                boolean successContact = myDbHelper.recordExistOrNot(myDbHelper.myDataBase,
                                        DBConstants.TABLE.CONTACTS_DETAILS, DBConstants.Contacts_details.id, additional_contact_temp_id);

                                if (successContact) {
                                    myDbHelper.updateSyncProjectDetails(myDbHelper.myDataBase, DBConstants.TABLE.CONTACTS_DETAILS,
                                            contactObject.getString("id"), contactObject.getString("is_sync"), object.getString("id"),
                                            DBConstants.Contacts_details.contact_status, contactObject.getString("additional_contact_operation"),
                                            additional_contact_temp_id);

                                }
                            }
                        }
                    }

                    mediaModelArrayList = new ArrayList<>();
                    mediaModelArrayList = myDbHelper.getProjectMediaDetails();
                    if (mediaModelArrayList.size() > 0) {
                        for (int j = 0; j < mediaModelArrayList.size(); j++) {
                            if (mediaModelArrayList.get(j).getMedia_status().equalsIgnoreCase("U")) {
                                if (object.getString("id").equalsIgnoreCase(mediaModelArrayList.get(j).getProject_id())) {
                                    String media_id = mediaModelArrayList.get(j).getMediaId();
                                    String description = mediaModelArrayList.get(j).getMediaDescription();
                                    UpdateMediaDescription(media_id, object.getString("id"), description);
                                }
                            }
                        }
                    }

                    myDbHelper.close();

                } catch (SQLException e) {
                    e.printStackTrace();

                }
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.content_frame);
        if (fragment instanceof HomeFragment)
            ((HomeFragment) fragment).offlineProject();

//        if (fragment instanceof HistoryFragment)
//            if (Utils.validateString(mSharedPreferences.getString(Utils.ITEM_FILTER_STATUS, ""))) {
//                if (mSharedPreferences.getString(Utils.ITEM_FILTER_STATUS, "").equalsIgnoreCase(Utils.REPORT)) {
//                    ((HistoryFragment) fragment).offlineFilterHistroyProject(Utils.REPORT);
//                } else {
//                    ((HistoryFragment) fragment).offlineFilterHistroyProject(Utils.OPEN);
//                }
//            } else {
//                ((HistoryFragment) fragment).offlineHistroyProject();
//            }
    }

    public void UpdateMediaDescription(String media_id, String project_id, String des) {

        JSONObject params = new JSONObject();
        try {
            params.put("user_id", USERID);
            params.put("project_id", project_id);
            params.put("media_id", media_id);
            params.put("session_token", TOKEN);
            params.put("media_description", des);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Logger.e(TAG, "Photo Description PARA---->" + String.valueOf(params));
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.POST, Utils.MAIN_URL + Utils.ADD_PHOTO_DESCRIPTION, params, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Logger.e(TAG, "URL---->" + Utils.MAIN_URL + Utils.ADD_PHOTO_DESCRIPTION);
                        Logger.e(TAG, "Photo Description RES---->" + String.valueOf(response));
                        try {
                            boolean success = response.getBoolean("success");
                            String msg = response.getString("message");

                            if (success) {
                                JSONObject jObj = response.getJSONObject("data");
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

    public class NetworkChangeReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(final Context context, final Intent intent) {
            isNetworkAvailable(context);

        }

        private boolean isNetworkAvailable(Context context) {
            ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivity != null) {
                NetworkInfo[] info = connectivity.getAllNetworkInfo();
                if (info != null) {
                    for (int i = 0; i < info.length; i++) {
                        if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                            if (!isConnected) {
                                Logger.v(TAG, "Now you are connected to Internet!");
                                isConnected = true;
//                                if (Utils.projectSyncFlag == true) {
//                                getDomin();
//                                }
                                syncRequest();
                            }
                            return true;
                        }
                    }
                }
            }
//            Logger.v(TAG, "You are not connected to Internet!");
            isConnected = false;
            return false;
        }
    }

    public void clearAllData() {
        clearPrefData();

        NavigationDrawerActivity.this.deleteDatabase(DBConstants.DB_NAME);
        File dir = new File(Environment.getExternalStorageDirectory() + "/Media");
        Utils.deleteDirectory(dir);
        Intent intent = new Intent(NavigationDrawerActivity.this, LoginActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
        unregisterReceiver(receiver);
        finish();
    }

    public static void clearPrefData() {
        Utils.storeString(mSharedPreferences, Utils.USER_ID, "");
        Utils.storeString(mSharedPreferences, Utils.FIRST_NAME, "");
        Utils.storeString(mSharedPreferences, Utils.LAST_NAME, "");
        Utils.storeString(mSharedPreferences, Utils.STREET, "");
        Utils.storeString(mSharedPreferences, Utils.CITY, "");
        Utils.storeString(mSharedPreferences, Utils.STATE, "");
        Utils.storeString(mSharedPreferences, Utils.COUNTRY, "");
        Utils.storeString(mSharedPreferences, Utils.ZIPCODE, "");
        Utils.storeString(mSharedPreferences, Utils.PHONE_NUMBER, "");
        Utils.storeString(mSharedPreferences, Utils.EMAIL, "");
        Utils.storeString(mSharedPreferences, Utils.PASSWORD, "");
        Utils.storeString(mSharedPreferences, Utils.PROFILE_IMAGE, "");
        Utils.storeString(mSharedPreferences, Utils.TOKEN, "");
        Utils.storeString(mSharedPreferences, Utils.COMPANY_NAME, "");
        Utils.storeString(mSharedPreferences, Utils.COMPANY_IMAGE, "");
        Utils.storeString(mSharedPreferences, Utils.TODAY_PROJECT, "");
        Utils.storeString(mSharedPreferences, Utils.COMPLETED_PROJECT, "");
        Utils.storeString(mSharedPreferences, Utils.PENDING_PROJECT, "");
        Utils.storeString(mSharedPreferences, Utils.PLAN_ID, "");
        Utils.storeString(mSharedPreferences, Utils.PLAN_TYPE, "");
        Utils.storeString(mSharedPreferences, Utils.AUTO_RENEWAL, "");
        Utils.storeString(mSharedPreferences, Utils.ROLE, "");
        Utils.storeString(mSharedPreferences, Utils.MY_PROFILE, "");
        Utils.storeString(mSharedPreferences, Utils.TODAY_PROJECT, "");
        Utils.storeString(mSharedPreferences, Utils.COMPLETED_PROJECT, "");
        Utils.storeString(mSharedPreferences, Utils.PENDING_PROJECT, "");
        Utils.storeString(mSharedPreferences, Utils.SF_CONSUMER_KEY, "");
        Utils.storeString(mSharedPreferences, Utils.SF_CONSUMER_SECRET_KEY, "");
        Utils.storeString(mSharedPreferences, Utils.SF_SECURITY_TOKEN, "");
        Utils.storeString(mSharedPreferences, Utils.SF_USERNAME, "");
        Utils.storeString(mSharedPreferences, Utils.SF_PASSWORD, "");
        Utils.storeString(mSharedPreferences, Utils.PROMOCODE, "");
        Utils.storeString(mSharedPreferences, Utils.PROMOCODE_VALUE, "");
        Utils.storeString(mSharedPreferences, Utils.EXITING_USER_IDS, "");
//        Utils.storeString(mSharedPreferences, Utils.URL_API_ADDRESS, "");
        Utils.projectSyncFlag = false;

    }

    public void hideSoftKeyboard() {
        if (getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }


    /*public void getDomin() {

        if (Utils.checkInternetConnection(NavigationDrawerActivity.this)) {
            GetDominNameRequestTask getDominNameRequestTask = new GetDominNameRequestTask(NavigationDrawerActivity.this);
            getDominNameRequestTask.setAsyncCallListener(new AsyncCallListener() {

                @Override
                public void onResponseReceived(Object response) {
                    ExistingUserModel advertiseListModel = (ExistingUserModel) response;
                    try {
                        if (advertiseListModel.getSuccess().equalsIgnoreCase("true") && advertiseListModel.getAuthenticated().equalsIgnoreCase("true")) {
                            if (Utils.validateString(advertiseListModel.getUrl())) {
                                Utils.URL = advertiseListModel.getUrl();
                            }

                            Utils.URL = mSharedPreferences.getString(Utils.URL_API_ADDRESS, "");
//                            syncRequest();
                            if (Utils.validateString(ROLE) || (Utils.validateString(PLAN_ID))) {
                                if (!ROLE.equalsIgnoreCase("company") || PLAN_ID.equalsIgnoreCase("1") || PLAN_ID.equalsIgnoreCase("2")) {
                                    menu_linPurchaseUser.setVisibility(View.GONE);
                                } else {
                                    menu_linPurchaseUser.setVisibility(View.VISIBLE);
                                }
                            }
                            planCheck();
                            updateTimeZone();
                        }
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onResponseReceived(JSONObject object) {

                }

                @Override
                public void onErrorReceived(String error) {
                    Toast.makeText(NavigationDrawerActivity.this, getString(R.string.try_again), Toast.LENGTH_SHORT).show();
                }
            });
            getDominNameRequestTask.execute();
        }
    }*/

    public void getDomin() {
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, Utils.DOMIN_URL, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Logger.e(TAG, "URL ::" + Utils.DOMIN_URL);
                        Logger.e(TAG, "Response" + String.valueOf(response));
                        try {
                            boolean success = response.getBoolean("success");
                            String msg = response.getString("message");
                            if (success) {
                                if (!response.isNull("data")) {
                                    JSONObject dataObj = response.getJSONObject("data");
                                    String url = dataObj.getString("url");
                                    Utils.storeString(mSharedPreferences, Utils.URL_WEB_ADDRESS, url);
                                    Utils.storeString(mSharedPreferences, Utils.URL_API_ADDRESS, url + "/api/web/");
                                    Utils.MAIN_URL = mSharedPreferences.getString(Utils.URL_API_ADDRESS, "");
                                    Utils.WEB_SITE_URL = mSharedPreferences.getString(Utils.URL_WEB_ADDRESS, "");
                                    if (Utils.validateString(ROLE) || (Utils.validateString(PLAN_ID))) {
                                        if (!ROLE.equalsIgnoreCase("company") || PLAN_ID.equalsIgnoreCase("1") || PLAN_ID.equalsIgnoreCase("2")) {
                                            menu_linPurchaseUser.setVisibility(View.GONE);
                                        } else {
                                            menu_linPurchaseUser.setVisibility(View.VISIBLE);
                                        }
                                    }
                                    planCheck();
                                    updateTimeZone();
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

}
