package com.rogi.Fragment;

import android.annotation.TargetApi;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.rogi.Activity.LoginActivity;
import com.rogi.Activity.NavigationDrawerActivity;
import com.rogi.Activity.ProjectDetailsActivity;
import com.rogi.Adapter.Fragment_HomeRecyclerViewAdapter;
import com.rogi.Database.DBConstants;
import com.rogi.Database.DBHelper;
import com.rogi.Model.AdditionalContactModel;
import com.rogi.Model.MediaModel;
import com.rogi.Model.NotesModel;
import com.rogi.Model.ProjectPhaseModel;
import com.rogi.Model.TaskModel;
import com.rogi.R;
import com.rogi.Service.AsyncCallListener;
import com.rogi.Service.DounloadImageRequestTask;
import com.rogi.Service.DounloadThumbImageRequestTask;
import com.rogi.Service.UpdateImageRequestTask;
import com.rogi.Service.UploadImageRequestTask;
import com.rogi.View.Utils;
import com.rogi.logger.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class HomeFragment extends Fragment implements OnClickListener, SwipeRefreshLayout.OnRefreshListener {

    public static final String TAG = "HomeFragment";
    private static final int HOME_TITLE_VIEW = 0;
    private static final int HOME_PROJECT_VIEW = 1;

    private DBHelper mDbHelper;
    private AlertDialog.Builder builder;
    private AlertDialog dialog;
    private SharedPreferences mSharedPreferences;

    int hwidth, hHeight;
    boolean pendingFlag = true, completeFlag = true, isSync = true;

    View view;
    Calendar myCalendar;
    Fragment_HomeRecyclerViewAdapter homeAdapter;
    RequestQueue requestQueue;
    ProgressDialog pdialog;
    SimpleDateFormat dfDate;

    RecyclerView listView;
    EditText fromDateDialog, toDateDialog;
    ImageView imgClose, imgLH, imgHL;
    LinearLayout linLH, linHL, linDefault;
    RelativeLayout rootLayout;
    TextView btnFilter, btnReset, btnClearFilter, txtError, txtMsg, txtRogiUrl;
    SwipeRefreshLayout swipeRefreshLayout;

    String TOKEN = "", USERID = "", radioPriority = "", fromDate = "", toDate = "", media_id = "", mediaType = "",
            media = "", videoThumb = "", signatureImage = "", docThumb = "";

    ArrayList<TaskModel> taskModelArrayList;
    ArrayList<TaskModel> projectModelArrayList;
    ArrayList<AdditionalContactModel> additionalContactModelArrayList;
    ArrayList<NotesModel> notesModelArrayList;
    ArrayList<ProjectPhaseModel> projectPhaseModelArrayList;
    ArrayList<MediaModel> mediaModelArrayList;

    Handler mHandler = new Handler();
    Runnable mRunnable;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);
        requestQueue = Volley.newRequestQueue(getActivity());
        mSharedPreferences = getActivity().getSharedPreferences(Utils.PREF_NAME, getActivity().MODE_PRIVATE);
        USERID = mSharedPreferences.getString(Utils.USER_ID, "");
        TOKEN = mSharedPreferences.getString(Utils.TOKEN, "");
        init();
        myCalendar = Calendar.getInstance();

        taskModelArrayList = new ArrayList<>();
        projectModelArrayList = new ArrayList<>();
        additionalContactModelArrayList = new ArrayList<>();
        notesModelArrayList = new ArrayList<>();
        projectPhaseModelArrayList = new ArrayList<>();
        mediaModelArrayList = new ArrayList<>();

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        listView.setLayoutManager(layoutManager);

        homeAdapter = new Fragment_HomeRecyclerViewAdapter(getContext(), taskModelArrayList, taskItemClick, statusBtnClick,
                completedProjectViewClick, pendingProjectViewClick);
        listView.setAdapter(homeAdapter);


        NavigationDrawerActivity.titleText.setVisibility(View.GONE);
        NavigationDrawerActivity.imgLogo.setVisibility(View.VISIBLE);
        NavigationDrawerActivity.fab.setVisibility(View.VISIBLE);
        NavigationDrawerActivity.filterView.setVisibility(View.VISIBLE);
        NavigationDrawerActivity.imgSyc.setVisibility(View.VISIBLE);
        NavigationDrawerActivity.filterView.setOnClickListener(this);
        NavigationDrawerActivity.imgSyc.setOnClickListener(this);
        NavigationDrawerActivity.doubleBackToExitPressedOnce = true;

        Display display = getActivity().getWindowManager().getDefaultDisplay();

        hwidth = display.getWidth();  // deprecated
        hHeight = display.getHeight();

        mDbHelper = new DBHelper(getActivity());

        if (Utils.projectRefreshFlag == false) {
            int currentapiVersion = Build.VERSION.SDK_INT;
            if (currentapiVersion > Build.VERSION_CODES.LOLLIPOP) {
                // Do something for lollipop and above versions
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if ((getActivity().checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) &&
                            (getActivity().checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
                        requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                android.Manifest.permission.READ_EXTERNAL_STORAGE}, 101);
                    } else {
                        if (Utils.checkInternetConnection(getActivity())) {
                            if (Utils.projectRefreshFlag == false) {
                                getTaskList();
                            }
                        }
                    }
                } else {
                    if (Utils.checkInternetConnection(getActivity())) {
                        if (Utils.projectRefreshFlag == false) {
                            getTaskList();
                        }
                    }
                }
            } else {
                if (Utils.checkInternetConnection(getActivity())) {
                    if (Utils.projectRefreshFlag == false) {
                        getTaskList();
                    }
                }
            }
        }

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        return view;
    }

    public void init() {
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_project);


        rootLayout = (RelativeLayout) view.findViewById(R.id.rootLayout);
        listView = (RecyclerView) view.findViewById(R.id.listView);
        linDefault = (LinearLayout) view.findViewById(R.id.linDefault);

        btnClearFilter = (TextView) view.findViewById(R.id.btnClearFilter);
        txtError = (TextView) view.findViewById(R.id.txtError);
        txtMsg = (TextView) view.findViewById(R.id.txtMsg);

        txtRogiUrl = (TextView) view.findViewById(R.id.txtRogiUrl);
        txtRogiUrl.setVisibility(View.VISIBLE);

        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeColors(Color.RED, Color.GREEN, Color.BLUE, Color.CYAN);

        btnClearFilter.setOnClickListener(this);
        btnClearFilter.setVisibility(View.GONE);

        dfDate = new SimpleDateFormat("yyyy-MM-dd");

        Utils.storeString(mSharedPreferences, Utils.FILTER_FROM_DATE, "");
        Utils.storeString(mSharedPreferences, Utils.FILTER_TO_DATE, "");
        Utils.storeString(mSharedPreferences, Utils.FILTER_PRIORITY, "");

        if (Utils.validateString(Utils.WEB_SITE_URL)) {
            String html = "<a href=\"" + Utils.WEB_SITE_URL + "\">myrogi.com</a>";
            Spanned result;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                result = Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY);
            } else {
                result = Html.fromHtml(html);
            }
            txtRogiUrl.setText(result);
            txtRogiUrl.setMovementMethod(LinkMovementMethod.getInstance());
        } else {
            String html = "<a href=\"http://www.myrogi.com\">myrogi.com</a>";
            Spanned result;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                result = Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY);
            } else {
                result = Html.fromHtml(html);
            }
            txtRogiUrl.setText(result);
            txtRogiUrl.setMovementMethod(LinkMovementMethod.getInstance());
        }
    }

    public void openFilterView() {
        builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.fragment_home_filter_view, null);
        builder.setView(dialogView);

        fromDateDialog = (EditText) dialogView.findViewById(R.id.fromDate);
        toDateDialog = (EditText) dialogView.findViewById(R.id.toDate);
        imgClose = (ImageView) dialogView.findViewById(R.id.imgClose);

        btnFilter = (TextView) dialogView.findViewById(R.id.btnFilter);
        btnReset = (TextView) dialogView.findViewById(R.id.btnReset);

        linLH = (LinearLayout) dialogView.findViewById(R.id.linLH);
        linHL = (LinearLayout) dialogView.findViewById(R.id.linHL);

        imgLH = (ImageView) dialogView.findViewById(R.id.imgLH);
        imgHL = (ImageView) dialogView.findViewById(R.id.imgHL);

        fromDateDialog.setTypeface(Utils.getTypeFace(getContext()));
        toDateDialog.setTypeface(Utils.getTypeFace(getContext()));

        if (Utils.validateString(mSharedPreferences.getString(Utils.FILTER_PRIORITY, ""))) {
            String priority = mSharedPreferences.getString(Utils.FILTER_PRIORITY, "");
            if (priority.equalsIgnoreCase("NH")) {
                imgLH.setImageResource(R.mipmap.radio_btn_active);
                imgHL.setImageResource(R.mipmap.radio_btn);
            } else if (priority.equalsIgnoreCase("HN")) {
                imgLH.setImageResource(R.mipmap.radio_btn);
                imgHL.setImageResource(R.mipmap.radio_btn_active);
            }
        }

        if (Utils.validateString(mSharedPreferences.getString(Utils.FILTER_FROM_DATE, ""))) {
            String fromDate = mSharedPreferences.getString(Utils.FILTER_FROM_DATE, "");
            fromDateDialog.setText(fromDate);
        }

        if (Utils.validateString(mSharedPreferences.getString(Utils.FILTER_TO_DATE, ""))) {
            String toDate = mSharedPreferences.getString(Utils.FILTER_TO_DATE, "");
            toDateDialog.setText(toDate);
        }


        fromDateDialog.setOnClickListener(this);
        toDateDialog.setOnClickListener(this);
        imgClose.setOnClickListener(this);
        btnFilter.setOnClickListener(this);
        btnReset.setOnClickListener(this);

        linHL.setOnClickListener(this);
        linLH.setOnClickListener(this);

        dialog = builder.create();
        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.CENTER;
        window.setAttributes(wlp);
        dialog.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.filterView:
                openFilterView();
                break;

            case R.id.imgSyc:
                if (Utils.checkInternetConnection(getActivity())) {
                    if (isSync) {
                        syncMedia();
                    }
                }
                break;

            case R.id.btnClearFilter:
                Utils.storeString(mSharedPreferences, Utils.FILTER_FROM_DATE, "");
                Utils.storeString(mSharedPreferences, Utils.FILTER_TO_DATE, "");
                Utils.storeString(mSharedPreferences, Utils.FILTER_PRIORITY, "");

                btnClearFilter.setVisibility(View.GONE);
                txtRogiUrl.setVisibility(View.VISIBLE);
                NavigationDrawerActivity.fab.setVisibility(View.VISIBLE);
                if (Utils.checkInternetConnection(getActivity())) {
                    getTaskList();
                } else {
                    offlineProject();
                }
                radioPriority = "";
                break;

            case R.id.fromDate:

                int year = 0, month = 0, day = 0;
                if (fromDateDialog.getText().toString().trim().equalsIgnoreCase("")) {
                    year = myCalendar.get(Calendar.YEAR);
                    month = myCalendar.get(Calendar.MONTH);
                    day = myCalendar.get(Calendar.DAY_OF_MONTH);
                } else {
                    String d = fromDateDialog.getText().toString();
                    year = Integer.parseInt(d.substring(0, 4));
                    month = Integer.parseInt(d.substring(5, 7)) - 1;
                    day = Integer.parseInt(d.substring(8, 10));
                }

                DatePickerDialog dpdFrom = new DatePickerDialog(getActivity(), R.style.DialogTheme, filterStartDatePicker, year, month, day);
                dpdFrom.show();

                break;

            case R.id.toDate:

                if (!fromDateDialog.getText().toString().equalsIgnoreCase("") && !fromDateDialog.getText().toString().equalsIgnoreCase(null)) {
                    int year1 = 0, month1 = 0, day1 = 0;
                    if (toDateDialog.getText().toString().trim().equalsIgnoreCase("")) {
                        year1 = myCalendar.get(Calendar.YEAR);
                        month1 = myCalendar.get(Calendar.MONTH);
                        day1 = myCalendar.get(Calendar.DAY_OF_MONTH);
                    } else {
                        String d = toDateDialog.getText().toString();
                        year1 = Integer.parseInt(d.substring(0, 4));
                        month1 = Integer.parseInt(d.substring(5, 7)) - 1;
                        day1 = Integer.parseInt(d.substring(8, 10));
                    }

                    DatePickerDialog dpdTo = new DatePickerDialog(getActivity(), R.style.DialogTheme, filterDueDatePicker, year1, month1, day1);
                    dpdTo.show();
                } else {
                    Utils.showMessageDialog(getActivity(), getResources().getString(R.string.alert), getString(R.string.start_date));
                }

                break;

            case R.id.imgClose:
                dialog.dismiss();
                break;

            case R.id.btnFilter:
                if (fieldValidation()) {
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
//                    if (Utils.checkInternetConnection(getActivity())) {
//                        getFilterHistroyList(radioPriority, fromDate, toDate);
//                    } else {
                    String strPriority = "", startDate = "", dueDate = "";
                    if (Utils.validateString(radioPriority)) {
                        if (radioPriority.toUpperCase().equalsIgnoreCase("HN"))
                            strPriority = "ASC";
                        else
                            strPriority = "DESC";
                    }

                    offlineProjectFilter(strPriority, fromDateDialog.getText().toString().trim(), toDateDialog.getText().toString());

//                    }
                }
                break;

            case R.id.btnReset:
                Utils.storeString(mSharedPreferences, Utils.FILTER_FROM_DATE, "");
                Utils.storeString(mSharedPreferences, Utils.FILTER_TO_DATE, "");
                Utils.storeString(mSharedPreferences, Utils.FILTER_PRIORITY, "");

                radioPriority = "";
                imgLH.setImageResource(R.mipmap.radio_btn);
                imgHL.setImageResource(R.mipmap.radio_btn);

                fromDateDialog.setText("");
                toDateDialog.setText("");
                break;

            case R.id.linLH:
                // NH - Noraml to High
                imgLH.setImageResource(R.mipmap.radio_btn_active);
                imgHL.setImageResource(R.mipmap.radio_btn);
                radioPriority = "NH";
                Utils.storeString(mSharedPreferences, Utils.FILTER_PRIORITY, radioPriority);
                break;

            case R.id.linHL:
                // HN - High to Normal
                imgLH.setImageResource(R.mipmap.radio_btn);
                imgHL.setImageResource(R.mipmap.radio_btn_active);
                radioPriority = "HN";
                Utils.storeString(mSharedPreferences, Utils.FILTER_PRIORITY, radioPriority);
                break;
        }
    }

    View.OnClickListener completedProjectViewClick = new OnClickListener() {
        @Override
        public void onClick(View v) {

            if (completeFlag) {
                completeFlag = false;
                NavigationDrawerActivity.fab.setVisibility(View.GONE);
                HistoryFragment fragment2 = new HistoryFragment();
                replaceFragment(fragment2);
                NavigationDrawerActivity.navItemIndex = 1;
                NavigationDrawerActivity.selectMenu(1);
                NavigationDrawerActivity.doubleBackToExitPressedOnce = false;
                Utils.storeString(mSharedPreferences, Utils.ITEM_FILTER_STATUS, "Completed");
            }
        }
    };


    View.OnClickListener pendingProjectViewClick = new OnClickListener() {
        @Override
        public void onClick(View v) {

            if (pendingFlag) {
                pendingFlag = false;
                NavigationDrawerActivity.fab.setVisibility(View.GONE);
                HistoryFragment fragment = new HistoryFragment();
                replaceFragment(fragment);
                NavigationDrawerActivity.navItemIndex = 1;
                NavigationDrawerActivity.selectMenu(1);
                NavigationDrawerActivity.doubleBackToExitPressedOnce = false;
                Utils.storeString(mSharedPreferences, Utils.ITEM_FILTER_STATUS, "Pending");
            }
        }
    };

    public void replaceFragment(Fragment fragment) {
        if (fragment != null) {
            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, fragment);
            ft.commit();
        }
    }

    public void getTaskList() {
        pdialog = new ProgressDialog(getActivity());
        pdialog.setMessage(getString(R.string.please_wait));
        pdialog.setCanceledOnTouchOutside(false);
        pdialog.show();

        JSONObject params = new JSONObject();
        try {
            params.put("user_id", USERID);
            params.put("session_token", TOKEN);
            params.put("is_pagination", "");
            params.put("limit", "");
            params.put("page_number", "");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Logger.e(TAG, "projectList PARA---->" + String.valueOf(params));
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.POST, Utils.MAIN_URL + Utils.PROJECT_LIST_API, params, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Logger.e(TAG, "projectList RES---->" + String.valueOf(response));
                        pdialog.dismiss();
                        try {
                            boolean success = response.getBoolean("success");
                            boolean Auth = response.getBoolean("authenticated");
                            String msg = response.getString("message");
                            if (!Auth) {
                                NavigationDrawerActivity.clearPrefData();
                                getActivity().deleteDatabase(DBConstants.DB_NAME);
                                File dir = new File(Environment.getExternalStorageDirectory() + "/Media");
                                Utils.deleteDirectory(dir);
                                startActivity(new Intent(getActivity(), LoginActivity.class));
                                getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
                                getActivity().finish();
                            } else {
//                                isSync = false;
                                pendingFlag = true;
                                completeFlag = true;
                                if (success) {
                                    taskModelArrayList.clear();

                                    JSONArray dataArray = response.getJSONArray("data");

                                    if (dataArray.length() > 0) {
                                        setTaskList(dataArray);
                                    } else {
                                        homeAdapter.notifyDataSetChanged();
                                    }
                                } else {
                                    taskModelArrayList.clear();
                                    showMessage(msg);
                                }

                                if (!taskModelArrayList.isEmpty()) {
                                    linDefault.setVisibility(View.GONE);
                                    swipeRefreshLayout.setVisibility(View.VISIBLE);
                                } else {
                                    linDefault.setVisibility(View.VISIBLE);
                                    txtError.setText(msg);
                                    txtMsg.setVisibility(View.VISIBLE);
                                    swipeRefreshLayout.setVisibility(View.VISIBLE);
                                    offlineProject();
                                }
                            }

                            pdialog.dismiss();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e("ERR", e.getMessage());
                        }
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error instanceof NoConnectionError) {
                            pdialog.dismiss();
                            showMessage(getString(R.string.checkInternet));
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    }
                });
        int socketTimeout = 30000;//30 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsObjRequest.setRetryPolicy(policy);
        requestQueue.add(jsObjRequest);
    }

    /*public void getFilterHistroyList(String priority, String fromDate, String toDate) {

        pdialog = new ProgressDialog(getActivity());
        pdialog.setMessage(getString(R.string.please_wait));
        pdialog.setCanceledOnTouchOutside(false);
        pdialog.show();

        JSONObject params = new JSONObject();
        try {
            params.put("user_id", USERID);
            params.put("session_token", TOKEN);
            params.put("priority", priority);
            params.put("from_date", fromDate);
            params.put("to_date", toDate);
            params.put("project_status", "");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Logger.e(TAG, "HistroyFilterList PARA---->" + String.valueOf(params));
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.POST, Utils.FILTER_HISTROY_API, params, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Logger.e(TAG, "HistroyFilterList RES---->" + String.valueOf(response));
                        pdialog.dismiss();
                        dialog.dismiss();
                        try {
                            boolean success = response.getBoolean("success");
                            String msg = response.getString("message");
                            pendingFlag = true;
                            completeFlag = true;
                            if (success) {
                                taskModelArrayList.clear();

                                JSONArray dataArray = response.getJSONArray("data");
                                if (dataArray.length() > 0) {
                                    setTaskList(dataArray);
                                } else {
                                    homeAdapter.notifyDataSetChanged();
                                }
                            } else {
                                taskModelArrayList.clear();
                                showMessage(msg);
                            }

                            if (!taskModelArrayList.isEmpty()) {
                                linDefault.setVisibility(View.GONE);
                                swipeRefreshLayout.setVisibility(View.VISIBLE);
                            } else {
                                linDefault.setVisibility(View.VISIBLE);
                                txtError.setText(msg);
                                txtMsg.setVisibility(View.GONE);
                                swipeRefreshLayout.setVisibility(View.GONE);
                            }

                            btnClearFilter.setVisibility(View.VISIBLE);
                            NavigationDrawerActivity.fab.setVisibility(View.GONE);

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e("ERR", e.getMessage());
                        }
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error instanceof NoConnectionError) {
                            pdialog.dismiss();
                            swipeRefreshLayout.setRefreshing(false);
                            showMessage(getString(R.string.checkInternet));
                        }
                    }
                });
        requestQueue.add(jsObjRequest);
    }*/

    public void setProjectTitleView() {
        TaskModel taskModel1 = new TaskModel();
        taskModel1.setTodayProject(String.valueOf(mDbHelper.todaysCount()));
        taskModel1.setPenddingProject(String.valueOf(mDbHelper.othersCount(Utils.OPEN)));
        taskModel1.setCompletedProject(String.valueOf(mDbHelper.othersCount(Utils.REPORT)));
        taskModel1.setViewType(HOME_TITLE_VIEW);
        taskModelArrayList.add(taskModel1);

    }

    public void syncRequest() {
        try {
            mDbHelper.openDataBase();

            projectModelArrayList = new ArrayList<>();
            projectModelArrayList = mDbHelper.getSyncProjectDetails();

            notesModelArrayList = new ArrayList<>();
            notesModelArrayList = mDbHelper.getSyncProjectNotesDetails();

            projectPhaseModelArrayList = new ArrayList<>();
            projectPhaseModelArrayList = mDbHelper.getSyncProjectPhaseDetails();

            additionalContactModelArrayList = new ArrayList<>();
            additionalContactModelArrayList = mDbHelper.getSyncAdditionalContactDetails();

            mDbHelper.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (projectModelArrayList.size() > 0 || !projectModelArrayList.isEmpty()) {
//            isSync = true;
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
        } else {
            int currentapiVersion = Build.VERSION.SDK_INT;
            if (currentapiVersion > Build.VERSION_CODES.LOLLIPOP) {
                // Do something for lollipop and above versions
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if ((getActivity().checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
                        requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 101);
                    } else {
                        if (Utils.checkInternetConnection(getActivity())) {
                            if (Utils.projectRefreshFlag == false) {
                                getTaskList();
                            }
                        }
                    }
                } else {
                    if (Utils.checkInternetConnection(getActivity())) {
                        if (Utils.projectRefreshFlag == false) {
                            getTaskList();
                        }
                    }
                }
            } else {
                if (Utils.checkInternetConnection(getActivity())) {
                    if (Utils.projectRefreshFlag == false) {
                        getTaskList();
                    }
                }
            }
        }


    }

    public void syncMedia() {

        try {
            mDbHelper.openDataBase();

            projectModelArrayList = new ArrayList<>();
            projectModelArrayList = mDbHelper.getMediaUnSyncProjectDetails();

            mediaModelArrayList = new ArrayList<>();
            mediaModelArrayList = mDbHelper.getSyncMediaDetails();

            mDbHelper.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            for (int i = 0; i < projectModelArrayList.size(); i++) {
                if (mediaModelArrayList.size() > 0) {
                    isSync = false;
                    String media_id = "", mediaType = "", media = "", mediaProjectId = "", description = "", latitude = "", longitude = "",
                            street = "", city = "", state = "", country = "", pincode = "", createdDate = "", is_Sync = "",
                            mediaStatus = "";
                    for (int j = 0; j < mediaModelArrayList.size(); j++) {

                        if (projectModelArrayList.get(i).getId().equalsIgnoreCase(mediaModelArrayList.get(j).getProject_id())) {
                            mediaProjectId = projectModelArrayList.get(i).getId();
                            media_id = mediaModelArrayList.get(j).getMediaId();
                            mediaType = mediaModelArrayList.get(j).getMediaType();
                            if (mediaType.equalsIgnoreCase("image")) {
                                File file = new File(Environment.getExternalStorageDirectory() + "/Media/Image/", media_id + ".jpg");
                                if (file.exists()) {
                                    media = file.toString();
                                }
                            } else if (mediaType.equalsIgnoreCase("video")) {
                                File file = new File(Environment.getExternalStorageDirectory() + "/Media/Video/", media_id + ".mp4");
                                if (file.exists()) {
                                    media = file.toString();
                                }
                            } else if (mediaType.equalsIgnoreCase("audio")) {
                                File file = new File(Environment.getExternalStorageDirectory() + "/Media/Audio/", media_id + ".wav");
                                if (file.exists()) {
                                    media = file.toString();
                                }
                            } else if (mediaType.equalsIgnoreCase("doc")) {
                                File file = new File(Environment.getExternalStorageDirectory() + "/Media/Doc/", media_id + ".doc");
                                if (file.exists()) {
                                    media = file.toString();
                                }
                            } else if (mediaType.equalsIgnoreCase("txt")) {
                                File file = new File(Environment.getExternalStorageDirectory() + "/Media/Txt/", media_id + ".txt");
                                if (file.exists()) {
                                    media = file.toString();
                                }
                            } else if (mediaType.equalsIgnoreCase("docx")) {
                                File file = new File(Environment.getExternalStorageDirectory() + "/Media/Docx/", media_id + ".docx");
                                if (file.exists()) {
                                    media = file.toString();
                                }
                            } else if (mediaType.equalsIgnoreCase("pdf")) {
                                File file = new File(Environment.getExternalStorageDirectory() + "/Media/PDF/", media_id + ".pdf");
                                if (file.exists()) {
                                    media = file.toString();
                                }
                            }

                            description = mediaModelArrayList.get(j).getMediaDescription();
                            latitude = mediaModelArrayList.get(j).getLatitude();
                            longitude = mediaModelArrayList.get(j).getLongitude();
                            street = mediaModelArrayList.get(j).getStreet();
                            city = mediaModelArrayList.get(j).getCity();
                            state = mediaModelArrayList.get(j).getState();
                            country = mediaModelArrayList.get(j).getCountry();
                            pincode = mediaModelArrayList.get(j).getPincode();
                            createdDate = mediaModelArrayList.get(j).getCreated_date();
                            is_Sync = mediaModelArrayList.get(j).getIs_sync();
                            mediaStatus = mediaModelArrayList.get(j).getMedia_status();

                            if (mediaStatus.equalsIgnoreCase("A")) {
                                uploadMedia(USERID, TOKEN, mediaProjectId, mediaType, media, latitude,
                                        longitude, street, city, state, country, pincode, createdDate, media_id, description);
                            } else {

//                                UpdateMediaDescription(media_id, mediaProjectId, description);

                                updateMedia(USERID, TOKEN, mediaProjectId, media_id, media, latitude,
                                        longitude, street, city, state, country, pincode, createdDate, "", description);
                            }

                        }
                    }
                } else {
                    isSync = true;
                    break;
                }
            }

        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    private void uploadMedia(String user_id, String session_token, String project_id, String media_type, String selectedImagePath,
                             String lat, String lang, String street, String city, String state, String country, String pincode, String created_date,
                             String media_temp_id, String media_description) {

        if (Utils.checkInternetConnection(getActivity())) {
            UploadImageRequestTask uploadImageRequestTask = new UploadImageRequestTask(getActivity());
            uploadImageRequestTask.setAsyncCallListener(new AsyncCallListener() {
                @Override
                public void onResponseReceived(Object response) {

                }

                @Override
                public void onResponseReceived(JSONObject object) {
                    try {
                        boolean isSuccess = object.getBoolean("success");
                        String msg = object.getString("message");
                        if (isSuccess) {
                            if (!object.isNull("data")) {
                                JSONObject jObj = object.getJSONObject("data");
                                setUploadedSyncImage(jObj);
                                isSync = true;
                            }
                        } else {
                            showMessage(msg);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onErrorReceived(String error) {

                }
            });
            uploadImageRequestTask.execute(user_id, session_token, project_id, media_type, selectedImagePath, lat, lang, street,
                    city, state, country, pincode, created_date, media_temp_id, media_description);
        }
    }

    private void setUploadedSyncImage(JSONObject jObj) {
        try {
            if (!jObj.isNull("project_media")) {
                JSONArray mediaArray = jObj.getJSONArray("project_media");
                if (mediaArray.length() > 0) {
                    for (int i = 0; i < mediaArray.length(); i++) {
                        JSONObject mediaObject = mediaArray.getJSONObject(i);
                        String mediaTempId = mediaObject.getString("media_temp_id");
                        String mediaId = mediaObject.getString("id");
                        String mediaType = mediaObject.getString("media_type");
                        String is_Sync = mediaObject.getString("is_sync");
                        String mediaStatus = mediaObject.getString("media_operation");

                        try {
                            mDbHelper.openDataBase();
                            boolean successMedia = mDbHelper.recordExistOrNot(mDbHelper.myDataBase,
                                    DBConstants.TABLE.MEDIA_DETAILS, DBConstants.Media_details.id, mediaTempId);

                            if (successMedia) {
                                mDbHelper.updateSyncMediaDetails(DBConstants.TABLE.MEDIA_DETAILS,
                                        mediaId, is_Sync, jObj.getString("id"),
                                        DBConstants.Media_details.media_status, mediaStatus,
                                        mediaTempId);
//                                mDbHelper.updateSyncMediaDetails(mediaId, is_Sync, jObj.getString("id"), mediaStatus, mediaTempId);

                                if (mediaType.equalsIgnoreCase("image")) {
                                    File file = new File(Environment.getExternalStorageDirectory() + "/Media/Image/", mediaTempId + ".jpg");
                                    if (file.exists()) {
                                        file.renameTo(new File(Environment.getExternalStorageDirectory() + "/Media/Image/", mediaId + ".jpg"));
                                    }
                                } else if (mediaType.equalsIgnoreCase("audio")) {
                                    File file = new File(Environment.getExternalStorageDirectory() + "/Media/Audio/", mediaTempId + ".wav");
                                    if (file.exists()) {
                                        file.renameTo(new File(Environment.getExternalStorageDirectory() + "/Media/Audio/", mediaId + ".wav"));
                                    }
                                } else if (mediaType.equalsIgnoreCase("video")) {
                                    File file = new File(Environment.getExternalStorageDirectory() + "/Media/Video/", mediaTempId + ".mp4");
                                    if (file.exists()) {
                                        file.renameTo(new File(Environment.getExternalStorageDirectory() + "/Media/Video/", mediaId + ".mp4"));
                                    }

                                    File file1 = new File(Environment.getExternalStorageDirectory() + "/Media/VideoThumb/", mediaTempId + ".jpg");
                                    if (file1.exists()) {
                                        file1.renameTo(new File(Environment.getExternalStorageDirectory() + "/Media/VideoThumb/", mediaId + ".jpg"));
                                    }

                                } else if (mediaType.equalsIgnoreCase("doc")) {
                                    File file = new File(Environment.getExternalStorageDirectory() + "/Media/Doc/", mediaTempId + ".doc");
                                    if (file.exists()) {
                                        file.renameTo(new File(Environment.getExternalStorageDirectory() + "/Media/Doc/", mediaId + ".doc"));
                                    }
                                } else if (mediaType.equalsIgnoreCase("txt")) {
                                    File file = new File(Environment.getExternalStorageDirectory() + "/Media/Txt/", mediaTempId + ".txt");
                                    if (file.exists()) {
                                        file.renameTo(new File(Environment.getExternalStorageDirectory() + "/Media/Txt/", mediaId + ".txt"));
                                    }
                                } else if (mediaType.equalsIgnoreCase("docx")) {
                                    File file = new File(Environment.getExternalStorageDirectory() + "/Media/Docx/", mediaTempId + ".docx");
                                    if (file.exists()) {
                                        file.renameTo(new File(Environment.getExternalStorageDirectory() + "/Media/Docx/", mediaId + ".docx"));
                                    }
                                } else if (mediaType.equalsIgnoreCase("pdf")) {
                                    File file = new File(Environment.getExternalStorageDirectory() + "/Media/PDF/", mediaTempId + ".pdf");
                                    if (file.exists()) {
                                        file.renameTo(new File(Environment.getExternalStorageDirectory() + "/Media/PDF/", mediaId + ".pdf"));
                                    }
                                }

                            }
                            mDbHelper.close();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }

                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        offlineProject();
    }

    private void updateMedia(String user_id, String session_token, String project_id, String media_id, String selectedImagePath,
                             String lat, String lang, String street, String city, String state, String country, String pincode,
                             String created_date, String media_temp_id, String media_description) {

        if (Utils.checkInternetConnection(getActivity())) {
            UpdateImageRequestTask updateImageRequestTask = new UpdateImageRequestTask(getActivity());
            updateImageRequestTask.setAsyncCallListener(new AsyncCallListener() {
                @Override
                public void onResponseReceived(Object response) {

                }

                @Override
                public void onResponseReceived(JSONObject object) {
                    try {
                        boolean isSuccess = object.getBoolean("success");
                        String msg = object.getString("message");
                        if (isSuccess) {
                            if (!object.isNull("data")) {
                                JSONObject jObj = object.getJSONObject("data");
                                setUpdatedSyncImage(jObj);
                                isSync = true;
                            }
                        } else {
                            showMessage(msg);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onErrorReceived(String error) {

                }
            });
            updateImageRequestTask.execute(user_id, session_token, project_id, media_id, selectedImagePath, lat, lang, street,
                    city, state, country, pincode, created_date, media_temp_id, media_description);
        }
    }

    private void setUpdatedSyncImage(JSONObject jObj) {
        try {
            if (!jObj.isNull("project_media")) {
                JSONArray mediaArray = jObj.getJSONArray("project_media");
                String media_id = "", mediaType = "", media = "", videoThumb = "", description = "", latitude = "", longitude = "",
                        street = "", city = "", state = "", country = "", pincode = "", createdDate = "", is_Sync = "",
                        mediaStatus = "", docThumbImage = "";
                if (mediaArray.length() > 0) {
                    for (int i = 0; i < mediaArray.length(); i++) {
                        JSONObject mediaObject = mediaArray.getJSONObject(i);
                        media_id = mediaObject.getString("id");
                        mediaType = mediaObject.getString("media_type");
                        media = mediaObject.getString("media");
                        videoThumb = mediaObject.getString("video_thumb_image");
                        description = mediaObject.getString("media_description");
                        latitude = mediaObject.getString("latitude");
                        longitude = mediaObject.getString("longitude");
                        street = mediaObject.getString("street");
                        city = mediaObject.getString("city");
                        state = mediaObject.getString("state");
                        country = mediaObject.getString("country");
                        pincode = mediaObject.getString("pincode");
                        createdDate = mediaObject.getString("created_date");
                        is_Sync = mediaObject.getString("is_sync");
                        mediaStatus = mediaObject.getString("media_operation");
                        if (!mediaObject.isNull("doc_thumb_image"))
                            docThumbImage = mediaObject.getString("doc_thumb_image");
                        else
                            docThumbImage = "";

                        try {
                            mDbHelper.openDataBase();
                            boolean successMedia = mDbHelper.recordExistOrNot(mDbHelper.myDataBase,
                                    DBConstants.TABLE.MEDIA_DETAILS, DBConstants.Media_details.id, media_id);

                            if (successMedia) {

                                mDbHelper.updateProjectMedia(media_id, jObj.getString("id"), mediaType, media, videoThumb, description,
                                        latitude, longitude, street, city, state, country, pincode, createdDate, is_Sync, mediaStatus, docThumbImage);

                                if (mediaType.equalsIgnoreCase("image")) {
                                    File file = new File(Environment.getExternalStorageDirectory() + "/Media/Image/", media_id + ".jpg");
                                    if (file.exists()) {
                                        file.renameTo(new File(Environment.getExternalStorageDirectory() + "/Media/Image/", media_id + ".jpg"));
                                    }
                                } else {
                                    File file = new File(Environment.getExternalStorageDirectory() + "/Media/Video/", media_id + ".mp4");
                                    if (file.exists()) {
                                        file.renameTo(new File(Environment.getExternalStorageDirectory() + "/Media/Video/", media_id + ".mp4"));
                                    }

                                    File file1 = new File(Environment.getExternalStorageDirectory() + "/Media/VideoThumb/", media_id + ".jpg");
                                    if (file1.exists()) {
                                        file1.renameTo(new File(Environment.getExternalStorageDirectory() + "/Media/VideoThumb/", media_id + ".jpg"));
                                    }

                                }


                            }
                            mDbHelper.close();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }

                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        offlineProject();
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
                        Logger.e(TAG, "URL---->" + Utils.ADD_PHOTO_DESCRIPTION);
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

    public void setTaskList(JSONArray dataArray) {
        try {
            for (int i = 0; i < dataArray.length(); i++) {
                try {
                    mDbHelper.openDataBase();
                    JSONObject object = dataArray.getJSONObject(i);
                    TaskModel taskModel = new TaskModel();
                    taskModel.setTaskObject(String.valueOf(object));
                    taskModel.setId(object.getString("id"));
                    taskModel.setTitle(object.getString("title"));
                    taskModel.setDescription(object.getString("description"));
                    taskModel.setProject_start_time(object.getString("project_start_time"));
                    taskModel.setStartDate(object.getString("start_date"));
                    taskModel.setStartTime(object.getString("start_time"));
                    taskModel.setDueDate(object.getString("due_date"));
                    taskModel.setDueTime(object.getString("due_time"));
                    taskModel.setPriority(object.getString("priority"));
                    taskModel.setProject_status(object.getString("project_status"));
                    taskModel.setStreet(object.getString("street"));
                    taskModel.setCity(object.getString("city"));
                    taskModel.setState(object.getString("state"));
                    taskModel.setCountry(object.getString("country"));
                    taskModel.setPincode(object.getString("pincode"));
                    taskModel.setLatitude(object.getString("lattitude"));
                    taskModel.setLongitude(object.getString("longitude"));
                    taskModel.setSignature_image(object.getString("signature_image"));
                    taskModel.setAssignName(object.getString("assigned_by"));
                    taskModel.setAssignContact(object.getString("assigned_by_phone"));
                    taskModel.setAssignEmail(object.getString("assigned_by_email"));
                    if (!object.isNull("remind_hours"))
                        taskModel.setReminder_hours(object.getString("remind_hours"));
                    else
                        taskModel.setReminder_hours("");
//                    taskModel.setReminder_hours(object.getString("remind_hours"));
                    if (!object.isNull("is_sync"))
                        taskModel.setIs_sync(object.getString("is_sync"));
                    else
                        taskModel.setIs_sync("Y");

                    if (!object.isNull("project_operation"))
                        taskModel.setProject_update_status(object.getString("project_operation"));
                    else
                        taskModel.setProject_update_status("A");

                    if (!object.isNull("created_date"))
                        taskModel.setCreated_date(object.getString("created_date"));
                    else
                        taskModel.setCreated_date("");

                    if (!object.isNull("project_notes")) {
                        JSONArray notesArray = object.getJSONArray("project_notes");
                        ArrayList<NotesModel> notesModelArrayList = new ArrayList<>();
                        if (notesArray.length() > 0) {
                            for (int j = 0; j < notesArray.length(); j++) {
                                JSONObject notesObj = notesArray.getJSONObject(j);
                                NotesModel notesModel = new NotesModel();
                                notesModel.setNoteId(notesObj.getString("id"));
                                notesModel.setNote(notesObj.getString("note"));
                                notesModel.setNote_created_date(notesObj.getString("created_date"));
                                if (!notesObj.isNull("is_sync"))
                                    notesModel.setIs_sync(notesObj.getString("is_sync"));
                                else
                                    notesModel.setIs_sync("Y");
                                if (!notesObj.isNull("notes_operation"))
                                    notesModel.setNote_status(notesObj.getString("notes_operation"));
                                else
                                    notesModel.setNote_status("A");
                                notesModelArrayList.add(notesModel);
                                mDbHelper.insertProjectNotes(mDbHelper.myDataBase, notesModel.getNoteId(), object.getString("id"),
                                        notesModel.getNote(), notesModel.getNote_created_date(), notesModel.getIs_sync(),
                                        notesModel.getNote_status());
                            }
                        }
                        taskModel.setNotesModel(notesModelArrayList);
                    }

                    if (!object.isNull("project_media")) {
                        JSONArray mediaArray = object.getJSONArray("project_media");
                        ArrayList<MediaModel> mediaModelArrayList = new ArrayList<>();
                        if (mediaArray.length() > 0) {
                            for (int j = 0; j < mediaArray.length(); j++) {
                                JSONObject mediaObject = mediaArray.getJSONObject(j);
                                MediaModel mediaModel = new MediaModel();
                                mediaModel.setMediaId(mediaObject.getString("id"));
                                mediaModel.setMediaType(mediaObject.getString("media_type"));
                                mediaModel.setMedia(mediaObject.getString("media"));
                                mediaModel.setVideoThumbImage(mediaObject.getString("video_thumb_image"));
                                mediaModel.setMediaDescription(mediaObject.getString("media_description"));
                                mediaModel.setLatitude(mediaObject.getString("latitude"));
                                mediaModel.setLongitude(mediaObject.getString("longitude"));
                                mediaModel.setStreet(mediaObject.getString("street"));
                                mediaModel.setCity(mediaObject.getString("city"));
                                mediaModel.setState(mediaObject.getString("state"));
                                mediaModel.setCountry(mediaObject.getString("country"));
                                mediaModel.setPincode(mediaObject.getString("pincode"));
                                mediaModel.setCreated_date(mediaObject.getString("created_date"));
                                if (!mediaObject.isNull("is_sync"))
                                    mediaModel.setIs_sync(mediaObject.getString("is_sync"));
                                else
                                    mediaModel.setIs_sync("Y");
                                if (!mediaObject.isNull("media_operation"))
                                    mediaModel.setMedia_status(mediaObject.getString("media_operation"));
                                else
                                    mediaModel.setMedia_status("A");
                                if (!mediaObject.isNull("doc_thumb_image"))
                                    mediaModel.setDocThumbImage(mediaObject.getString("doc_thumb_image"));
                                else
                                    mediaModel.setDocThumbImage("");

                                mediaModelArrayList.add(mediaModel);

                                mDbHelper.insertProjectMedia(mDbHelper.myDataBase, mediaModel.getMediaId(), object.getString("id"),
                                        mediaModel.getMediaType(), mediaModel.getMedia(), mediaModel.getVideoThumbImage(), mediaModel.getMediaDescription(),
                                        mediaModel.getLatitude(), mediaModel.getLongitude(), mediaModel.getStreet(), mediaModel.getCity(),
                                        mediaModel.getState(), mediaModel.getCountry(), mediaModel.getPincode(), mediaModel.getCreated_date(),
                                        mediaModel.getIs_sync(), mediaModel.getMedia_status(), mediaModel.getDocThumbImage());

                            }
                        }
                        taskModel.setMediaModel(mediaModelArrayList);
                    }

                    if (!object.isNull("project_phase_data")) {
                        JSONArray projectPhaseArray = object.getJSONArray("project_phase_data");
                        ArrayList<ProjectPhaseModel> projectPhaseModelArrayList = new ArrayList<>();
                        if (projectPhaseArray.length() > 0) {
                            for (int j = 0; j < projectPhaseArray.length(); j++) {
                                JSONObject projectPhaseObj = projectPhaseArray.getJSONObject(j);
                                ProjectPhaseModel phaseModel = new ProjectPhaseModel();
                                phaseModel.setProjectPhaseId(projectPhaseObj.getString("id"));
                                phaseModel.setProjectDesription(projectPhaseObj.getString("description"));
                                phaseModel.setProjectCreatedDate(projectPhaseObj.getString("created_date"));
                                if (!projectPhaseObj.isNull("is_sync"))
                                    phaseModel.setIs_sync(projectPhaseObj.getString("is_sync"));
                                else
                                    phaseModel.setIs_sync("Y");
                                if (!projectPhaseObj.isNull("project_phase_operation"))
                                    phaseModel.setPhase_status(projectPhaseObj.getString("project_phase_operation"));
                                else
                                    phaseModel.setPhase_status("A");

                                projectPhaseModelArrayList.add(phaseModel);

                                mDbHelper.insertProjectPhase(mDbHelper.myDataBase, phaseModel.getProjectPhaseId(),
                                        object.getString("id"), phaseModel.getProjectDesription(),
                                        phaseModel.getProjectCreatedDate(), phaseModel.getIs_sync(),
                                        phaseModel.getPhase_status());
                            }
                        }
                        taskModel.setProjectPhaseModel(projectPhaseModelArrayList);
                    }

                    if (!object.isNull("additional_contacts")) {
                        String projectResponsiblityName = "", projectResponsiblityId = "";
                        JSONArray additionalContactsArray = object.getJSONArray("additional_contacts");
                        ArrayList<AdditionalContactModel> additionalContactModelArrayList = new ArrayList<>();
                        if (additionalContactsArray.length() > 0) {
                            for (int j = 0; j < additionalContactsArray.length(); j++) {
                                JSONObject contactObject = additionalContactsArray.getJSONObject(j);
                                AdditionalContactModel contactModel = new AdditionalContactModel();
                                contactModel.setAdtnlContactID(contactObject.getString("id"));
                                contactModel.setAdtnlContactName(contactObject.getString("name"));
                                contactModel.setAdtnlContactCompany(contactObject.getString("company"));
                                contactModel.setAdtnlContactAddress(contactObject.getString("address"));
                                contactModel.setAdtnlContactPhone(contactObject.getString("phone"));
                                contactModel.setAdtnlContactEmail(contactObject.getString("email"));
                                if (!contactObject.isNull("project_responsiblity")) {
                                    JSONArray jArray = contactObject.getJSONArray("project_responsiblity");
                                    StringBuilder stringBuilderId = new StringBuilder();
                                    StringBuilder stringBuilderTitle = new StringBuilder();
                                    for (int k = 0; k < jArray.length(); k++) {
                                        JSONObject responsibilityObject = jArray.getJSONObject(k);
                                        stringBuilderId.append(responsibilityObject.getString("id") + ",");
                                        stringBuilderTitle.append(responsibilityObject.getString("title") + ", ");
                                    }

                                    if (Utils.validateString(stringBuilderTitle.toString())) {
                                        projectResponsiblityId = stringBuilderId.substring(0, stringBuilderId.length() - 1);
                                        projectResponsiblityName = stringBuilderTitle.substring(0, stringBuilderTitle.length() - 2);
                                    }
                                }
                                contactModel.setAdtnlContactResponsibility(projectResponsiblityName);
                                contactModel.setAdtnlContactResponsibilityId(projectResponsiblityId);
                                if (!contactObject.isNull("is_sync"))
                                    contactModel.setIs_sync(contactObject.getString("is_sync"));
                                else
                                    contactModel.setIs_sync("Y");
                                if (!contactObject.isNull("additional_contact_operation"))
                                    contactModel.setContact_status(contactObject.getString("additional_contact_operation"));
                                else
                                    contactModel.setContact_status("A");

                                if (!contactObject.isNull("created_date"))
                                    contactModel.setCreated_date(contactObject.getString("created_date"));
                                else
                                    contactModel.setCreated_date(Utils.getCurrentDateAndTime());

                                additionalContactModelArrayList.add(contactModel);

                                mDbHelper.insertProjectAdditionalContact(mDbHelper.myDataBase, contactModel.getAdtnlContactID(),
                                        object.getString("id"), contactModel.getAdtnlContactName(), contactModel.getAdtnlContactCompany(),
                                        contactModel.getAdtnlContactAddress(), contactModel.getAdtnlContactPhone(), contactModel.getAdtnlContactEmail(),
                                        contactModel.getAdtnlContactResponsibility(), contactModel.getAdtnlContactResponsibilityId(),
                                        contactModel.getIs_sync(), contactModel.getContact_status(), contactModel.getCreated_date());
                            }
                        }
                        taskModel.setAdditionalContactModel(additionalContactModelArrayList);
                    }


                    mDbHelper.insertProject(mDbHelper.myDataBase, taskModel.getId(), taskModel.getTitle(), taskModel.getDescription(), taskModel.getStartDate(), taskModel.getStartTime(),
                            taskModel.getDueDate(), taskModel.getDueTime(), taskModel.getPriority(), taskModel.getProject_status(), taskModel.getStreet(),
                            taskModel.getCity(), taskModel.getState(), taskModel.getCountry(), taskModel.getPincode(), taskModel.getLatitude(), taskModel.getLongitude(),
                            taskModel.getAssignName(), taskModel.getAssignContact(), taskModel.getAssignEmail(), taskModel.getIs_sync(), taskModel.getSignature_image(),
                            taskModel.getProject_update_status(), taskModel.getProject_start_time(), taskModel.getCreated_date(),
                            taskModel.getReminder_hours());

                    mDbHelper.close();

                } catch (SQLException e) {
                    e.printStackTrace();

                }

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        offlineProject();

    }

    public void setProjectList(JSONArray dataArray) {
        try {
//            taskModelArrayList = new ArrayList<>();

            for (int i = 0; i < dataArray.length(); i++) {
                JSONObject object = dataArray.getJSONObject(i);
                TaskModel taskModel = new TaskModel();
                taskModel.setTaskObject(String.valueOf(object));
                taskModel.setId(object.getString("id"));
                taskModel.setTitle(object.getString("title"));
                taskModel.setDescription(object.getString("description"));
                taskModel.setProject_start_time(object.getString("project_start_time"));
                taskModel.setStartDate(object.getString("start_date"));
                taskModel.setStartTime(object.getString("start_time"));
                taskModel.setDueDate(object.getString("due_date"));
                taskModel.setDueTime(object.getString("due_time"));
                taskModel.setPriority(object.getString("priority"));
                taskModel.setProject_status(object.getString("project_status"));
                taskModel.setStreet(object.getString("street"));
                taskModel.setCity(object.getString("city"));
                taskModel.setState(object.getString("state"));
                taskModel.setCountry(object.getString("country"));
                taskModel.setPincode(object.getString("pincode"));
                taskModel.setLatitude(object.getString("lattitude"));
                taskModel.setLongitude(object.getString("longitude"));
                taskModel.setSignature_image(object.getString("signature_image"));
                taskModel.setAssignName(object.getString("assigned_by"));
                taskModel.setAssignContact(object.getString("assigned_by_phone"));
                taskModel.setAssignEmail(object.getString("assigned_by_email"));
                taskModel.setIs_sync(object.getString("is_sync"));
                taskModel.setProject_update_status(object.getString("project_operation"));
                taskModel.setCreated_date(object.getString("created_date"));
                taskModel.setReminder_hours(object.getString("remind_hours"));

                if (!object.isNull("project_notes")) {
                    JSONArray notesArray = object.getJSONArray("project_notes");
                    ArrayList<NotesModel> notesModelArrayList = new ArrayList<>();
                    if (notesArray.length() > 0) {
                        for (int j = 0; j < notesArray.length(); j++) {
                            JSONObject notesObj = notesArray.getJSONObject(j);
                            NotesModel notesModel = new NotesModel();
                            notesModel.setNoteId(notesObj.getString("id"));
                            notesModel.setNote(notesObj.getString("note"));
                            notesModel.setNote_created_date(notesObj.getString("created_date"));
                            notesModel.setIs_sync(notesObj.getString("is_sync"));
                            notesModel.setNote_status(notesObj.getString("notes_operation"));
                            notesModelArrayList.add(notesModel);
                        }
                    }
                    taskModel.setNotesModel(notesModelArrayList);
                }

                if (!object.isNull("project_media")) {
                    JSONArray mediaArray = object.getJSONArray("project_media");
                    ArrayList<MediaModel> mediaModelArrayList = new ArrayList<>();
                    if (mediaArray.length() > 0) {
                        for (int j = 0; j < mediaArray.length(); j++) {
                            JSONObject mediaObject = mediaArray.getJSONObject(j);
                            MediaModel mediaModel = new MediaModel();
                            mediaModel.setMediaId(mediaObject.getString("id"));
                            mediaModel.setMediaType(mediaObject.getString("media_type"));
                            mediaModel.setMedia(mediaObject.getString("media"));
                            mediaModel.setVideoThumbImage(mediaObject.getString("video_thumb_image"));
                            mediaModel.setMediaDescription(mediaObject.getString("media_description"));
                            mediaModel.setLatitude(mediaObject.getString("latitude"));
                            mediaModel.setLongitude(mediaObject.getString("longitude"));
                            mediaModel.setStreet(mediaObject.getString("street"));
                            mediaModel.setCity(mediaObject.getString("city"));
                            mediaModel.setState(mediaObject.getString("state"));
                            mediaModel.setCountry(mediaObject.getString("country"));
                            mediaModel.setPincode(mediaObject.getString("pincode"));
                            mediaModel.setCreated_date(mediaObject.getString("created_date"));
                            mediaModel.setIs_sync(mediaObject.getString("is_sync"));
                            mediaModel.setMedia_status(mediaObject.getString("media_operation"));
                            mediaModel.setDocThumbImage(mediaObject.getString("doc_thumb_image"));
                            mediaModelArrayList.add(mediaModel);
                        }
                    }
                    taskModel.setMediaModel(mediaModelArrayList);
                }

                if (!object.isNull("project_phase_data")) {
                    JSONArray projectPhaseArray = object.getJSONArray("project_phase_data");
                    ArrayList<ProjectPhaseModel> projectPhaseModelArrayList = new ArrayList<>();
                    if (projectPhaseArray.length() > 0) {
                        for (int j = 0; j < projectPhaseArray.length(); j++) {
                            JSONObject projectPhaseObj = projectPhaseArray.getJSONObject(j);
                            ProjectPhaseModel phaseModel = new ProjectPhaseModel();
                            phaseModel.setProjectPhaseId(projectPhaseObj.getString("id"));
                            phaseModel.setProjectDesription(projectPhaseObj.getString("description"));
                            phaseModel.setProjectCreatedDate(projectPhaseObj.getString("created_date"));
                            phaseModel.setIs_sync(projectPhaseObj.getString("is_sync"));
                            phaseModel.setPhase_status(projectPhaseObj.getString("project_phase_operation"));
                            projectPhaseModelArrayList.add(phaseModel);
                        }
                    }
                    taskModel.setProjectPhaseModel(projectPhaseModelArrayList);
                }

                if (!object.isNull("additional_contacts")) {
                    String projectResponsiblityName = "", projectResponsiblityId = "";
                    JSONArray additionalContactsArray = object.getJSONArray("additional_contacts");
                    ArrayList<AdditionalContactModel> additionalContactModelArrayList = new ArrayList<>();
                    if (additionalContactsArray.length() > 0) {
                        for (int j = 0; j < additionalContactsArray.length(); j++) {
                            JSONObject contactObject = additionalContactsArray.getJSONObject(j);
                            AdditionalContactModel contactModel = new AdditionalContactModel();
                            contactModel.setAdtnlContactID(contactObject.getString("id"));
                            contactModel.setAdtnlContactName(contactObject.getString("name"));
                            contactModel.setAdtnlContactCompany(contactObject.getString("company"));
                            contactModel.setAdtnlContactAddress(contactObject.getString("address"));
                            contactModel.setAdtnlContactPhone(contactObject.getString("phone"));
                            contactModel.setAdtnlContactEmail(contactObject.getString("email"));
                            contactModel.setAdtnlContactResponsibility(contactObject.getString("project_responsiblity"));
                            contactModel.setAdtnlContactResponsibilityId(contactObject.getString("project_responsiblity_id"));
//                            if (!contactObject.isNull("project_responsiblity")) {
//                                JSONArray jArray = contactObject.getJSONArray("project_responsiblity");
//                                StringBuilder stringBuilderId = new StringBuilder();
//                                StringBuilder stringBuilderTitle = new StringBuilder();
//                                for (int k = 0; k < jArray.length(); k++) {
//                                    JSONObject responsibilityObject = jArray.getJSONObject(k);
//                                    stringBuilderId.append(responsibilityObject.getString("id") + ",");
//                                    stringBuilderTitle.append(responsibilityObject.getString("title") + ", ");
//                                }
//
//                                if (Utils.validateString(stringBuilderTitle.toString())) {
//                                    projectResponsiblityId = stringBuilderId.substring(0, stringBuilderId.length() - 1);
//                                    projectResponsiblityName = stringBuilderTitle.substring(0, stringBuilderTitle.length() - 2);
//                                }
//                            }
//                            contactModel.setAdtnlContactResponsibility(projectResponsiblityName);
//                            contactModel.setAdtnlContactResponsibilityId(projectResponsiblityId);
                            contactModel.setAdtnlContactResponsibility(projectResponsiblityName);
                            contactModel.setAdtnlContactResponsibilityId(projectResponsiblityId);
                            contactModel.setIs_sync(contactObject.getString("is_sync"));
                            contactModel.setContact_status(contactObject.getString("additional_contact_operation"));
                            contactModel.setCreated_date(contactObject.getString("created_date"));
                            additionalContactModelArrayList.add(contactModel);
                        }
                    }
                    taskModel.setAdditionalContactModel(additionalContactModelArrayList);
                }

//                String signature = taskModel.getSignature_image();
//                String status = taskModel.getProject_status();
//                String projectStartTime = taskModel.getProject_start_time();
//
//                if (status.equalsIgnoreCase(Utils.OPEN)) {
//                    status = Utils.START;
//                } else if (status.equalsIgnoreCase(Utils.OPEN) && Utils.validateString(projectStartTime)) {
//                    status = Utils.COMPLETE;
//                } else if (status.equals(Utils.COMPLETE) && Utils.validateString(signature)) {
//                    status = Utils.REPORT;
//                }
//
//                taskModel.setStatus(status);
                taskModel.setViewType(HOME_PROJECT_VIEW);
                taskModelArrayList.add(taskModel);
            }
//            Utils.projectSyncFlag = true;

        } catch (JSONException e) {
            e.printStackTrace();
        }

        homeAdapter = new Fragment_HomeRecyclerViewAdapter(getContext(), taskModelArrayList, taskItemClick, statusBtnClick,
                completedProjectViewClick, pendingProjectViewClick);
        listView.setAdapter(homeAdapter);

        if (Utils.checkInternetConnection(getActivity())) {
            doTheAutoRefresh();
        }

    }

    private void doTheAutoRefresh() {
        mRunnable = new Runnable() {

            @Override
            public void run() {
                mediaModelArrayList = new ArrayList<>();
                mediaModelArrayList = mDbHelper.getProjectMediaDetails();
                File file;
                for (int i = 0; i < mediaModelArrayList.size(); i++) {
                    media_id = mediaModelArrayList.get(i).getMediaId();
                    mediaType = mediaModelArrayList.get(i).getMediaType();
                    media = mediaModelArrayList.get(i).getMedia().trim();
                    videoThumb = "";
                    docThumb = "";
                    if (mediaType.equalsIgnoreCase("image")) {
                        file = new File(Environment.getExternalStorageDirectory() + "/Media/Image/", media_id + ".jpg");
                        if (!file.exists()) {
                            storeMediaImage(media, mediaType, media_id + ".jpg");
                        }

                    } else if (mediaType.equalsIgnoreCase("audio")) {
                        file = new File(Environment.getExternalStorageDirectory() + "/Media/Audio/", media_id + ".wav");
                        if (!file.exists()) {
                            storeMediaImage(media, mediaType, media_id + ".wav");
                        }
                    } else if (mediaType.equalsIgnoreCase("doc")) {
                        file = new File(Environment.getExternalStorageDirectory() + "/Media/Doc/", media_id + ".doc");
                        if (!file.exists()) {
                            storeMediaImage(media, mediaType, media_id + ".doc");
                        }

//                        file = new File(Environment.getExternalStorageDirectory() + "/Media/VideoThumb/", media_id + ".jpg");
//                        if (!file.exists()) {
//                            docThumb = mediaModelArrayList.get(i).getDocThumbImage().trim();
//                            storeThumbImage(docThumb, media_id + ".jpg");
//                        }
                    } else if (mediaType.equalsIgnoreCase("txt")) {
                        file = new File(Environment.getExternalStorageDirectory() + "/Media/Txt/", media_id + ".txt");
                        if (!file.exists()) {
                            storeMediaImage(media, mediaType, media_id + ".txt");
                        }

//                        file = new File(Environment.getExternalStorageDirectory() + "/Media/VideoThumb/", media_id + ".jpg");
//                        if (!file.exists()) {
//                            docThumb = mediaModelArrayList.get(i).getDocThumbImage().trim();
//                            storeThumbImage(docThumb, media_id + ".jpg");
//                        }
                    } else if (mediaType.equalsIgnoreCase("docx")) {
                        file = new File(Environment.getExternalStorageDirectory() + "/Media/Docx/", media_id + ".docx");
                        if (!file.exists()) {
                            storeMediaImage(media, mediaType, media_id + ".docx");
                        }

//                        file = new File(Environment.getExternalStorageDirectory() + "/Media/VideoThumb/", media_id + ".jpg");
//                        if (!file.exists()) {
//                            docThumb = mediaModelArrayList.get(i).getDocThumbImage().trim();
//                            storeThumbImage(docThumb, media_id + ".jpg");
//                        }
                    } else if (mediaType.equalsIgnoreCase("pdf")) {
                        file = new File(Environment.getExternalStorageDirectory() + "/Media/PDF/", media_id + ".pdf");
                        if (!file.exists()) {
                            storeMediaImage(media, mediaType, media_id + ".pdf");
                        }

//                        file = new File(Environment.getExternalStorageDirectory() + "/Media/VideoThumb/", media_id + ".jpg");
//                        if (!file.exists()) {
//                            docThumb = mediaModelArrayList.get(i).getDocThumbImage().trim();
//                            storeThumbImage(docThumb, media_id + ".jpg");
//                        }
                    } else if (mediaType.equalsIgnoreCase("video")) {
                        file = new File(Environment.getExternalStorageDirectory() + "/Media/Video/", media_id + ".mp4");
                        if (!file.exists()) {
                            storeMediaImage(media, mediaType, media_id + ".mp4");
                        }

                        file = new File(Environment.getExternalStorageDirectory() + "/Media/VideoThumb/", media_id + ".jpg");
                        if (!file.exists()) {
                            videoThumb = mediaModelArrayList.get(i).getVideoThumbImage().trim();
                            storeThumbImage(videoThumb, media_id + ".jpg");
                        }
                    }
                }
            }
        };
        mHandler.postDelayed(mRunnable, 1000);
    }

    private void storeMediaImage(String media, String media_type, String image_name) {

        if (Utils.checkInternetConnection(getActivity())) {
            DounloadImageRequestTask dounloadImageRequestTask = new DounloadImageRequestTask(getActivity());
            dounloadImageRequestTask.setAsyncCallListener(new AsyncCallListener() {

                @Override
                public void onResponseReceived(Object response) {
                }

                @Override
                public void onResponseReceived(JSONObject object) {

                }

                @Override
                public void onErrorReceived(String error) {
                }
            });
            dounloadImageRequestTask.execute(media, media_type, image_name);
        }
    }

    private void storeThumbImage(String video_thumb, String image_name) {

        if (Utils.checkInternetConnection(getActivity())) {
            DounloadThumbImageRequestTask dounloadThumbImageRequestTask = new DounloadThumbImageRequestTask(getActivity());
            dounloadThumbImageRequestTask.setAsyncCallListener(new AsyncCallListener() {

                @Override
                public void onResponseReceived(Object response) {
                }

                @Override
                public void onResponseReceived(JSONObject object) {

                }

                @Override
                public void onErrorReceived(String error) {
                }
            });
            dounloadThumbImageRequestTask.execute(video_thumb, image_name);
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 101:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    if (Utils.checkInternetConnection(getActivity())) {
                        if (Utils.projectRefreshFlag == false) {
                            getTaskList();
                        }
                    }
                } else {
                    // Permission Denied
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }


    @Override
    public void onDestroy() {
        mHandler.removeCallbacks(mRunnable);
        super.onDestroy();

    }

    @Override
    public void onRefresh() {
        if (Utils.checkInternetConnection(getActivity())) {
            if (pdialog != null && !pdialog.isShowing()) {
                pendingFlag = true;
                completeFlag = true;
                swipeRefreshLayout.setRefreshing(true);
                if (btnClearFilter.getVisibility() == View.GONE) {
                    getTaskList();
                } else {
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        } else {
            swipeRefreshLayout.setRefreshing(false);
        }
    }


    /*public class uploadSignature extends AsyncTask<Void, Void, Void> {
        boolean exception = false, isSuccess = false;
        ProgressDialog pdialog;
        JSONObject jObj = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pdialog = new ProgressDialog(getActivity());
            pdialog.setMessage(getString(R.string.please_wait));
            pdialog.setCanceledOnTouchOutside(false);
            pdialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                Upload_Image uploadimage = new Upload_Image(getActivity());
                Logger.d(TAG, "Selected Path" + signatureImage);
                String uploaddata = uploadimage.uploadSignature(USERID, TOKEN, projectID, signatureImage);

                JSONObject jsonObject = new JSONObject(uploaddata);
                Logger.e(TAG, "Server response :::" + uploaddata);
                boolean success = jsonObject.getBoolean("success");
                String msg = jsonObject.getString("message");
                pendingFlag = true;
                completeFlag = true;
                if (success) {
                    if (!jsonObject.isNull("data")) {
                        Utils.projectRefreshFlag = true;
                        isSuccess = true;
                        jObj = jsonObject.getJSONObject("data");

                    } else {
                        pdialog.dismiss();
                        showMessage(msg);
                    }
                } else {
                    pdialog.dismiss();
                    showMessage(msg);
                }

            } catch (JSONException e) {
                exception = true;
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            pdialog.dismiss();

            if (isSuccess) {
                getTaskList();
            }
        }

    }*/

    String projectID;

    //TODO: Task Item Click................
    View.OnClickListener taskItemClick = new OnClickListener() {
        @Override
        public void onClick(View v) {
            int index = (int) v.getTag();
            Log.d("INDEX", "" + index);
            String taskObject = taskModelArrayList.get(index).getTaskObject();
            projectID = taskModelArrayList.get(index).getId();
            Logger.e(TAG, "Project ID : " + projectID);

            Intent taskIntent = new Intent(getActivity(), ProjectDetailsActivity.class);
            startActivity(taskIntent);
            Utils.storeString(mSharedPreferences, Utils.TASK_OBJECT, taskObject);
            Utils.storeString(mSharedPreferences, Utils.PROJECT_STATUS, "");
            Utils.storeString(mSharedPreferences, Utils.PROJECT_START_TIME, "");
        }
    };


    boolean statusFlag = true;
    View.OnClickListener statusBtnClick = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (statusFlag) {
                statusFlag = false;
                int index = (int) v.getTag();
                Log.d("INDEX", "" + index);
                String taskObject = taskModelArrayList.get(index).getTaskObject();
                String status = taskModelArrayList.get(index).getProject_status();
                String project_start_time = taskModelArrayList.get(index).getProject_start_time();
                projectID = taskModelArrayList.get(index).getId();
                Logger.e(TAG, "Project ID : " + projectID + "\nProject Status : " + status);

                Intent taskIntent = new Intent(getActivity(), ProjectDetailsActivity.class);
                startActivity(taskIntent);
                Utils.storeString(mSharedPreferences, Utils.TASK_OBJECT, taskObject);
                Utils.storeString(mSharedPreferences, Utils.PROJECT_STATUS, status);
                Utils.storeString(mSharedPreferences, Utils.PROJECT_START_TIME, project_start_time);
            }
        }
    };

    /*public JSONArray getStatus(String status) {
        JSONArray statusArray = new JSONArray();
        try {
            JSONObject statusObject = new JSONObject();
            statusObject.put("project_id", projectID);
            statusObject.put("project_status", status);
            statusArray.put(statusObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return statusArray;
    }


    TextView submitSignatureBTN, resetSignatureBTN;
    InkView inkView;

    public void openSignatureView(final int index) {
        builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.activity_task_signature_view, null);
        builder.setView(dialogView);

        submitSignatureBTN = (TextView) dialogView.findViewById(R.id.submitSignatureBTN);
        resetSignatureBTN = (TextView) dialogView.findViewById(R.id.resetSignatureBTN);
        ImageView imgClose = (ImageView) dialogView.findViewById(R.id.imgClose);

        inkView = (InkView) dialogView.findViewById(R.id.ink);
        inkView.setColor(getResources().getColor(android.R.color.black));
        inkView.setMinStrokeWidth(1.5f);
        inkView.setMaxStrokeWidth(10f);

        statusFlag = true;

        submitSignatureBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap drawing = inkView.getBitmap(getResources().getColor(R.color.colorWhite));
                signatureImage = Utils.storeSignatureImage(drawing, projectID + ".jpg");
                inkView.setDrawingCacheEnabled(true);
                if (Utils.checkInternetConnection(getActivity())) {
                    new uploadSignature().execute();
                } else {
                    try {
                        String createdDate = "";
                        if (!Utils.validateString(taskModelArrayList.get(index).getCreated_date())) {
                            createdDate = Utils.getCurrentDateAndTime();
                        } else {
                            createdDate = taskModelArrayList.get(index).getCreated_date();
                        }

                        mDbHelper.openDataBase();
                        mDbHelper.updateProjectDetails(taskModelArrayList.get(index).getId(), taskModelArrayList.get(index).getTitle(),
                                taskModelArrayList.get(index).getDescription(), taskModelArrayList.get(index).getStartDate(),
                                taskModelArrayList.get(index).getStartTime(), taskModelArrayList.get(index).getDueDate(),
                                taskModelArrayList.get(index).getDueTime(), taskModelArrayList.get(index).getPriority(),
                                Utils.REPORT, taskModelArrayList.get(index).getStreet(),
                                taskModelArrayList.get(index).getCity(), taskModelArrayList.get(index).getState(),
                                taskModelArrayList.get(index).getCountry(), taskModelArrayList.get(index).getPincode(),
                                taskModelArrayList.get(index).getLatitude(), taskModelArrayList.get(index).getLongitude(),
                                taskModelArrayList.get(index).getAssignName(), taskModelArrayList.get(index).getAssignContact(),
                                taskModelArrayList.get(index).getAssignEmail(), "N",
                                projectID + ".jpg", "U",
                                taskModelArrayList.get(index).getProject_start_time(), createdDate);
                        mDbHelper.close();

                        int pendingCount = 0, compeletdCount = 0;
                        if (Utils.validateString(mSharedPreferences.getString(Utils.COMPLETED_PROJECT, ""))) {
                            compeletdCount = Integer.parseInt(mSharedPreferences.getString(Utils.COMPLETED_PROJECT, "")) + 1;
                        } else {
                            compeletdCount = 1;
                        }

                        if (Utils.validateString(mSharedPreferences.getString(Utils.PENDING_PROJECT, ""))) {
                            pendingCount = Integer.parseInt(mSharedPreferences.getString(Utils.PENDING_PROJECT, "")) - 1;
                        } else {
                            pendingCount = 1;
                        }

                        Utils.storeString(mSharedPreferences, Utils.COMPLETED_PROJECT, String.valueOf(compeletdCount));
                        Utils.storeString(mSharedPreferences, Utils.PENDING_PROJECT, String.valueOf(pendingCount));

                    } catch (SQLException e) {
                        e.printStackTrace();

                    }

                    offlineProject();

                    pendingFlag = true;
                    completeFlag = true;
                    statusFlag = true;
                }

                dialog.dismiss();
            }
        });

        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        resetSignatureBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inkView.clear();
            }
        });

        dialog = builder.create();
        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.CENTER;
        window.setAttributes(wlp);
        dialog.show();
    }*/

    public void syncProject(JSONArray syncProjectArray) {
        pdialog = new ProgressDialog(getActivity());
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
                            pendingFlag = true;
                            completeFlag = true;
                            statusFlag = true;
                            if (success) {
                                JSONArray dataArray = response.getJSONArray("data");
                                if (dataArray.length() > 0) {
                                    setSyncProkectList(dataArray);
                                }

                            } else {
                                showMessage(msg);
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
                    mDbHelper.openDataBase();
                    JSONObject object = dataArray.getJSONObject(i);
                    String project_temp_id = object.getString("project_temp_id");
                    boolean successProject = mDbHelper.recordExistOrNot(mDbHelper.myDataBase, DBConstants.TABLE.PROJECT_DETAILS,
                            DBConstants.Project_details.id, project_temp_id);
                    if (successProject) {
                        mDbHelper.updateSyncProject(mDbHelper.myDataBase, DBConstants.TABLE.PROJECT_DETAILS,
                                object.getString("id"), object.getString("is_sync"), DBConstants.Project_details.project_update_status,
                                object.getString("project_operation"), project_temp_id);

//                        mDbHelper.updateSyncProjectDetails(object.getString("id"), object.getString("is_sync"),
//                                object.getString("project_operation"), project_temp_id);

                    }

                    boolean successMedia = mDbHelper.recordExistOrNot(mDbHelper.myDataBase,
                            DBConstants.TABLE.MEDIA_DETAILS, DBConstants.Media_details.project_id, project_temp_id);

                    if (successMedia) {
                        mDbHelper.updateMediaProjectID(mDbHelper.myDataBase, DBConstants.TABLE.MEDIA_DETAILS, object.getString("id"),
                                project_temp_id);

//                        mDbHelper.updateSyncMediaProjectID(object.getString("id"), project_temp_id);
                    }

                    if (!object.isNull("project_notes")) {
                        JSONArray notesArray = object.getJSONArray("project_notes");
                        if (notesArray.length() > 0) {
                            for (int j = 0; j < notesArray.length(); j++) {
                                JSONObject notesObj = notesArray.getJSONObject(j);
                                String note_temp_id = notesObj.getString("note_temp_id");
                                boolean successNote = mDbHelper.recordExistOrNot(mDbHelper.myDataBase,
                                        DBConstants.TABLE.NOTES_DETAILS, DBConstants.Notes_details.id, note_temp_id);

                                if (successNote) {
                                    mDbHelper.updateSyncProjectDetails(mDbHelper.myDataBase, DBConstants.TABLE.NOTES_DETAILS,
                                            notesObj.getString("id"), notesObj.getString("is_sync"), object.getString("id"),
                                            DBConstants.Notes_details.note_status, notesObj.getString("notes_operation"),
                                            note_temp_id);
//                                    mDbHelper.updateSyncNotesDetails(notesObj.getString("id"), notesObj.getString("is_sync"), object.getString("id"),
//                                            notesObj.getString("notes_operation"), note_temp_id);

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
                                boolean successPhase = mDbHelper.recordExistOrNot(mDbHelper.myDataBase,
                                        DBConstants.TABLE.PHASE_DETAILS, DBConstants.Phase_details.id, project_phase_temp_id);

                                if (successPhase) {
                                    mDbHelper.updateSyncProjectDetails(mDbHelper.myDataBase, DBConstants.TABLE.PHASE_DETAILS,
                                            projectPhaseObj.getString("id"), projectPhaseObj.getString("is_sync"), object.getString("id"),
                                            DBConstants.Phase_details.phase_status, projectPhaseObj.getString("project_phase_operation"),
                                            project_phase_temp_id);
//                                    mDbHelper.updateSyncPhaseDetails(projectPhaseObj.getString("id"), projectPhaseObj.getString("is_sync"), object.getString("id"),
//                                    projectPhaseObj.getString("project_phase_operation"), project_phase_temp_id);
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

                                boolean successContact = mDbHelper.recordExistOrNot(mDbHelper.myDataBase,
                                        DBConstants.TABLE.CONTACTS_DETAILS, DBConstants.Contacts_details.id, additional_contact_temp_id);

                                if (successContact) {
                                    mDbHelper.updateSyncProjectDetails(mDbHelper.myDataBase, DBConstants.TABLE.CONTACTS_DETAILS,
                                            contactObject.getString("id"), contactObject.getString("is_sync"), object.getString("id"),
                                            DBConstants.Contacts_details.contact_status, contactObject.getString("additional_contact_operation"),
                                            additional_contact_temp_id);

//                                    mDbHelper.updateSyncContactDetails(contactObject.getString("id"), contactObject.getString("is_sync"), object.getString("id"),
//                                            contactObject.getString("additional_contact_operation"), additional_contact_temp_id);
                                }
                            }
                        }
                    }

                    mDbHelper.close();

                } catch (SQLException e) {
                    e.printStackTrace();

                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        getTaskList();
    }


    /*public void generateReports() {
        pdialog = new ProgressDialog(getActivity());
        pdialog.setMessage(getString(R.string.please_wait));
        pdialog.setCanceledOnTouchOutside(false);
        pdialog.show();

        JSONObject params = new JSONObject();
        try {
            params.put("user_id", USERID);
            params.put("session_token", TOKEN);
            params.put("project_id", projectID);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        pendingFlag = true;
        completeFlag = true;
        statusFlag = true;

        Logger.e(TAG, "generateReports PARA---->" + String.valueOf(params));
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.POST, Utils.GENERATE_REPORT_API, params, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Logger.e(TAG, "generateReports RES---->" + String.valueOf(response));
                        pdialog.dismiss();
                        try {
                            boolean success = response.getBoolean("success");
                            String msg = response.getString("message");
                            if (success) {
                                Utils.projectRefreshFlag = true;
                                JSONObject object = response.getJSONObject("data");
                                String pdfUrl = object.getString("pdf");

                                Intent intent = new Intent(getActivity(), GenerateReportActivity.class);
                                intent.putExtra(Utils.PDF_REPORT_URL, pdfUrl);
                                intent.putExtra(Utils.PDF_FILE_NAME, projectID + ".pdf");
                                startActivity(intent);

                            } else {
                                showMessage(msg);
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
        requestQueue.add(jsObjRequest);
    }


    public void updateProjectStatus(JSONArray projectArray) {
        pdialog = new ProgressDialog(getActivity());
        pdialog.setMessage(getString(R.string.please_wait));
        pdialog.setCanceledOnTouchOutside(false);
        pdialog.show();

        JSONObject params = new JSONObject();
        try {
            params.put("user_id", USERID);
            params.put("session_token", TOKEN);
            params.put("project", projectArray);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Logger.e(TAG, "updateProjectStatus PARA---->" + String.valueOf(params));
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.POST, Utils.UPDATE_STATUS_API, params, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Logger.e(TAG, "updateProjectStatus RES---->" + String.valueOf(response));
                        pdialog.dismiss();
                        try {
                            boolean success = response.getBoolean("success");
                            String msg = response.getString("message");
                            pendingFlag = true;
                            completeFlag = true;
                            statusFlag = true;
                            if (success) {
                                Utils.projectRefreshFlag = true;
//                                if (!isSync) {
                                getTaskList();
//                                }
                            } else {
                                showMessage(msg);
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
        requestQueue.add(jsObjRequest);
    }*/


    //TODO: Filter Date & Time picker........
    DatePickerDialog.OnDateSetListener filterStartDatePicker = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int yy, int mm, int dd) {
            // TODO Auto-generated method stub
            String day = String.valueOf(dd);
            String month = String.valueOf(mm + 1);
            if (day.trim().length() == 1)
                day = "0" + day;
            if (month.trim().length() == 1)
                month = "0" + month;
            String from = yy + "-" + month + "-" + day;
            String to = toDateDialog.getText().toString().trim();
            if (Utils.validateString(to)) {
                boolean flag = CheckDates(from, to);

                if (flag == true) {
                    fromDateDialog.setText(new StringBuilder().append(yy).append("-").append(month).append("-").append(day));
                } else {
                    Utils.showMessageDialog(getActivity(), getResources().getString(R.string.alert), getString(R.string.before_date));
                }
            } else {
                fromDateDialog.setText(new StringBuilder().append(yy).append("-").append(month).append("-").append(day));
            }

            fromDate = fromDateDialog.getText().toString().trim();
            Utils.storeString(mSharedPreferences, Utils.FILTER_FROM_DATE, fromDate);

        }

    };

    DatePickerDialog.OnDateSetListener filterDueDatePicker = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int yy, int mm, int dd) {
            // TODO Auto-generated method stub
            String day = String.valueOf(dd);
            String month = String.valueOf(mm + 1);
            if (day.trim().length() == 1)
                day = "0" + day;
            if (month.trim().length() == 1)
                month = "0" + month;
            String to = yy + "-" + month + "-" + day;
            String from = fromDateDialog.getText().toString().trim();
            boolean flag = CheckDates(from, to);

            if (flag == true) {
                toDateDialog.setText(new StringBuilder().append(yy).append("-").append(month).append("-").append(day));
            } else {
                Utils.showMessageDialog(getActivity(), getResources().getString(R.string.alert), getString(R.string.after_date));
            }

            toDate = toDateDialog.getText().toString().trim();
            Utils.storeString(mSharedPreferences, Utils.FILTER_TO_DATE, toDate);

        }
    };

    public boolean CheckDates(String d1, String d2) {
        boolean b = false;
        try {
            if (dfDate.parse(d1).before(dfDate.parse(d2))) {
                b = true;//If start date is before end date
            } else if (dfDate.parse(d1).equals(dfDate.parse(d2))) {
                b = true;//If two dates are equal
            } else {
                b = false; //If start date is after the end date
            }
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return b;
    }

    private void showMessage(String message) {
        Utils.showResponseMessage(getActivity(), rootLayout, message);
    }


    public boolean fieldValidation() {
        boolean flag = true;
        if (!Utils.validateString(radioPriority)) {
            flag = false;
            showMessage(getString(R.string.select_priority));
        }
        return flag;
    }

    public void offlineProject() {
        try {
            mDbHelper.openDataBase();

            taskModelArrayList = new ArrayList<>();
            projectModelArrayList = new ArrayList<>();
            projectModelArrayList = mDbHelper.getProjectDetails();

            notesModelArrayList = new ArrayList<>();
            notesModelArrayList = mDbHelper.getProjectNotesDetails();

            mediaModelArrayList = new ArrayList<>();
            mediaModelArrayList = mDbHelper.getProjectMediaDetails();

            projectPhaseModelArrayList = new ArrayList<>();
            projectPhaseModelArrayList = mDbHelper.getProjectPhaseDetails();

            additionalContactModelArrayList = new ArrayList<>();
            additionalContactModelArrayList = mDbHelper.getAdditionalContactDetails();

            mDbHelper.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        setProjectTitleView();

        if (projectModelArrayList.size() > 0 || !projectModelArrayList.isEmpty()) {
            UpdateProjectObject();
        } else {
            linDefault.setVisibility(View.VISIBLE);
            txtError.setText("There are no projects planned");
            txtMsg.setVisibility(View.VISIBLE);
            swipeRefreshLayout.setVisibility(View.VISIBLE);
            homeAdapter = new Fragment_HomeRecyclerViewAdapter(getContext(), taskModelArrayList, taskItemClick, statusBtnClick,
                    completedProjectViewClick, pendingProjectViewClick);
            listView.setAdapter(homeAdapter);
        }
    }

    public void offlineProjectFilter(String priority, String startDate, String dueDate) {
        try {
            mDbHelper.openDataBase();

            taskModelArrayList = new ArrayList<>();
            projectModelArrayList = new ArrayList<>();
            projectModelArrayList = mDbHelper.getFilterProjectDetails(priority, startDate, dueDate);

            notesModelArrayList = new ArrayList<>();
            notesModelArrayList = mDbHelper.getProjectNotesDetails();

            mediaModelArrayList = new ArrayList<>();
            mediaModelArrayList = mDbHelper.getProjectMediaDetails();

            projectPhaseModelArrayList = new ArrayList<>();
            projectPhaseModelArrayList = mDbHelper.getProjectPhaseDetails();

            additionalContactModelArrayList = new ArrayList<>();
            additionalContactModelArrayList = mDbHelper.getAdditionalContactDetails();

            mDbHelper.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        setProjectTitleView();

        if (projectModelArrayList.size() > 0 || !projectModelArrayList.isEmpty()) {
            btnClearFilter.setVisibility(View.VISIBLE);
            txtRogiUrl.setVisibility(View.GONE);
            UpdateProjectObject();
        } else {
            btnClearFilter.setVisibility(View.VISIBLE);
            txtRogiUrl.setVisibility(View.GONE);
            linDefault.setVisibility(View.VISIBLE);
            txtError.setText("No project found");
            txtMsg.setVisibility(View.GONE);
            swipeRefreshLayout.setVisibility(View.VISIBLE);
            homeAdapter = new Fragment_HomeRecyclerViewAdapter(getContext(), taskModelArrayList, taskItemClick, statusBtnClick,
                    completedProjectViewClick, pendingProjectViewClick);
            listView.setAdapter(homeAdapter);
        }
    }


    public void UpdateProjectObject() {
        if (projectModelArrayList.size() > 0 || !projectModelArrayList.isEmpty()) {
            linDefault.setVisibility(View.GONE);
            swipeRefreshLayout.setVisibility(View.VISIBLE);

            JSONArray projectArray, notesArray, mediaArray, phaseArray, contactArray, projectResponsbility;
            projectArray = new JSONArray();

            try {
                for (int i = 0; i < projectModelArrayList.size(); i++) {
                    try {
                        JSONObject object = new JSONObject();
                        object.put("id", projectModelArrayList.get(i).getId());
                        object.put("title", projectModelArrayList.get(i).getTitle());
                        object.put("description", projectModelArrayList.get(i).getDescription());
                        object.put("project_start_time", projectModelArrayList.get(i).getProject_start_time());
                        object.put("start_date", projectModelArrayList.get(i).getStartDate());
                        object.put("start_time", projectModelArrayList.get(i).getStartTime());
                        object.put("due_date", projectModelArrayList.get(i).getDueDate());
                        object.put("due_time", projectModelArrayList.get(i).getDueTime());
                        object.put("priority", projectModelArrayList.get(i).getPriority());
                        object.put("project_status", projectModelArrayList.get(i).getProject_status());
                        object.put("street", projectModelArrayList.get(i).getStreet());
                        object.put("city", projectModelArrayList.get(i).getCity());
                        object.put("state", projectModelArrayList.get(i).getState());
                        object.put("country", projectModelArrayList.get(i).getCountry());
                        object.put("pincode", projectModelArrayList.get(i).getPincode());
                        object.put("lattitude", projectModelArrayList.get(i).getLatitude());
                        object.put("longitude", projectModelArrayList.get(i).getLongitude());
                        object.put("signature_image", projectModelArrayList.get(i).getSignature_image());
                        object.put("assigned_by", projectModelArrayList.get(i).getAssignName());
                        object.put("assigned_by_phone", projectModelArrayList.get(i).getAssignContact());
                        object.put("assigned_by_email", projectModelArrayList.get(i).getAssignEmail());
                        object.put("is_sync", projectModelArrayList.get(i).getIs_sync());
                        object.put("project_operation", projectModelArrayList.get(i).getProject_update_status());
                        object.put("created_date", projectModelArrayList.get(i).getCreated_date());
                        object.put("remind_hours", projectModelArrayList.get(i).getReminder_hours());

                        notesArray = new JSONArray();
                        if (notesModelArrayList.size() > 0) {
                            for (int j = 0; j < notesModelArrayList.size(); j++) {
                                if (projectModelArrayList.get(i).getId().equalsIgnoreCase(notesModelArrayList.get(j).getProject_id())) {
                                    JSONObject notesObj = new JSONObject();
                                    notesObj.put("id", notesModelArrayList.get(j).getNoteId());
                                    notesObj.put("note", notesModelArrayList.get(j).getNote());
                                    notesObj.put("created_date", notesModelArrayList.get(j).getNote_created_date());
                                    notesObj.put("is_sync", notesModelArrayList.get(j).getIs_sync());
                                    notesObj.put("notes_operation", notesModelArrayList.get(j).getNote_status());
                                    notesArray.put(notesObj);
                                }
                            }
                        }
                        object.put("project_notes", notesArray);

                        mediaArray = new JSONArray();
                        if (mediaModelArrayList.size() > 0) {
                            for (int j = 0; j < mediaModelArrayList.size(); j++) {
                                if (projectModelArrayList.get(i).getId().equalsIgnoreCase(mediaModelArrayList.get(j).getProject_id())) {
                                    JSONObject mediaObj = new JSONObject();
                                    mediaObj.put("id", mediaModelArrayList.get(j).getMediaId());
                                    mediaObj.put("media_type", mediaModelArrayList.get(j).getMediaType());
                                    mediaObj.put("media", mediaModelArrayList.get(j).getMedia());
                                    mediaObj.put("video_thumb_image", mediaModelArrayList.get(j).getVideoThumbImage());
                                    mediaObj.put("media_description", mediaModelArrayList.get(j).getMediaDescription());
                                    mediaObj.put("latitude", mediaModelArrayList.get(j).getLatitude());
                                    mediaObj.put("longitude", mediaModelArrayList.get(j).getLongitude());
                                    mediaObj.put("street", mediaModelArrayList.get(j).getStreet());
                                    mediaObj.put("city", mediaModelArrayList.get(j).getCity());
                                    mediaObj.put("state", mediaModelArrayList.get(j).getState());
                                    mediaObj.put("country", mediaModelArrayList.get(j).getCountry());
                                    mediaObj.put("pincode", mediaModelArrayList.get(j).getPincode());
                                    mediaObj.put("created_date", mediaModelArrayList.get(j).getCreated_date());
                                    mediaObj.put("is_sync", mediaModelArrayList.get(j).getIs_sync());
                                    mediaObj.put("media_operation", mediaModelArrayList.get(j).getMedia_status());
                                    mediaObj.put("doc_thumb_image", mediaModelArrayList.get(j).getDocThumbImage());
                                    mediaArray.put(mediaObj);
                                }
                            }
                        }
                        object.put("project_media", mediaArray);

                        phaseArray = new JSONArray();
                        if (projectPhaseModelArrayList.size() > 0) {
                            for (int j = 0; j < projectPhaseModelArrayList.size(); j++) {
                                if (projectModelArrayList.get(i).getId().equalsIgnoreCase(projectPhaseModelArrayList.get(j).getProject_id())) {
                                    JSONObject phaseObj = new JSONObject();
                                    phaseObj.put("id", projectPhaseModelArrayList.get(j).getProjectPhaseId());
                                    phaseObj.put("description", projectPhaseModelArrayList.get(j).getProjectDesription());
                                    phaseObj.put("created_date", projectPhaseModelArrayList.get(j).getProjectCreatedDate());
                                    phaseObj.put("is_sync", projectPhaseModelArrayList.get(j).getIs_sync());
                                    phaseObj.put("project_phase_operation", projectPhaseModelArrayList.get(j).getPhase_status());
                                    phaseArray.put(phaseObj);
                                }
                            }
                        }
                        object.put("project_phase_data", phaseArray);

                        contactArray = new JSONArray();
                        if (additionalContactModelArrayList.size() > 0) {
                            for (int j = 0; j < additionalContactModelArrayList.size(); j++) {
                                if (projectModelArrayList.get(i).getId().equalsIgnoreCase(additionalContactModelArrayList.get(j).getProject_id())) {
                                    JSONObject contactObj = new JSONObject();
                                    contactObj.put("id", additionalContactModelArrayList.get(j).getAdtnlContactID());
                                    contactObj.put("name", additionalContactModelArrayList.get(j).getAdtnlContactName());
                                    contactObj.put("company", additionalContactModelArrayList.get(j).getAdtnlContactCompany());
                                    contactObj.put("address", additionalContactModelArrayList.get(j).getAdtnlContactAddress());
                                    contactObj.put("phone", additionalContactModelArrayList.get(j).getAdtnlContactPhone());
                                    contactObj.put("email", additionalContactModelArrayList.get(j).getAdtnlContactEmail());
//                                    projectResponsbility = new JSONArray();
//                                    if (Utils.validateString(additionalContactModelArrayList.get(j).getAdtnlContactResponsibilityId())) {
//                                        String responsibilityList = mSharedPreferences.getString(Utils.RESPONSIBILITY_LIST, "");
//                                        String[] parts = additionalContactModelArrayList.get(j).getAdtnlContactResponsibilityId().split(",");
//                                        try {
//                                            JSONArray dataArray = new JSONArray(responsibilityList);
//                                            for (String part : parts) {
//                                                for (int k = 0; k < dataArray.length(); k++) {
//                                                    if (part.equalsIgnoreCase(dataArray.getJSONObject(k).getString("id"))) {
//                                                        projectResponsbility.put(dataArray.getJSONObject(k));
//                                                    }
//                                                }
//                                            }
//                                        } catch (JSONException e) {
//                                            e.printStackTrace();
//                                        }
//                                    }
//                                    contactObj.put("project_responsiblity", projectResponsbility);
                                    contactObj.put("project_responsiblity", additionalContactModelArrayList.get(j).getAdtnlContactResponsibility());
                                    contactObj.put("project_responsiblity_id", additionalContactModelArrayList.get(j).getAdtnlContactResponsibilityId());
                                    contactObj.put("is_sync", additionalContactModelArrayList.get(j).getIs_sync());
                                    contactObj.put("additional_contact_operation", additionalContactModelArrayList.get(j).getContact_status());
                                    contactObj.put("created_date", additionalContactModelArrayList.get(j).getCreated_date());
                                    contactArray.put(contactObj);
                                }

                            }
                        }
                        object.put("additional_contacts", contactArray);
                        projectArray.put(object);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                setProjectList(projectArray);

            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        } else {
            linDefault.setVisibility(View.VISIBLE);
            txtError.setText("There are no projects planned");
            txtMsg.setVisibility(View.VISIBLE);
            swipeRefreshLayout.setVisibility(View.VISIBLE);
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        Utils.storeString(mSharedPreferences, Utils.REMINDER_TYPE, "");
        NavigationDrawerActivity.navItemIndex = 0;
        NavigationDrawerActivity.selectMenu(0);
        if (Utils.checkInternetConnection(getActivity())) {
            if (Utils.projectRefreshFlag == true) {
                Utils.projectRefreshFlag = false;
                pendingFlag = true;
                completeFlag = true;
                statusFlag = true;
                isSync = true;
                syncRequest();
//                getTaskList();
            }
        } else {
            offlineProject();
        }
//        pendingFlag = true;
//        completeFlag = true;
        statusFlag = true;
//        offlineProject();


    }
}
