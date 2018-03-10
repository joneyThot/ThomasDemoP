package com.rogi.Fragment;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.rogi.Activity.NavigationDrawerActivity;
import com.rogi.Activity.ProjectDetailsActivity;
import com.rogi.Adapter.Fragment_HomeAdapter;
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
import com.rogi.View.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class HistoryFragment extends Fragment implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {

    public static final String TAG = "HistoryFragment";

    private AlertDialog.Builder builder;
    private AlertDialog dialog;
    private SharedPreferences mSharedPreferences;
    private DBHelper mDbHelper;

    View view;
    Calendar myCalendar;
    RequestQueue requestQueue;
    ProgressDialog pdialog;
    SimpleDateFormat dfDate;
    Fragment_HomeAdapter homeAdapter;

    ListView lisHistroy;
    RelativeLayout relRoot;
    LinearLayout linPending, linComplete, linNH, linHN, linDefault, linDetails;
    EditText fromDateDialog, toDateDialog;
    ImageView imgClose, imgPending, imgCompeled, imgNH, imgHN;
    TextView btnFilter, btnReset, btnClearFilter, txtError;
    SwipeRefreshLayout swipeRefreshLayout;
    String TOKEN = "", USERID = "", radioStatus = "", radioPriority = "", fromDate = "", toDate = "", signatureImage = "",
            filter_status = "";

    ArrayList<TaskModel> historyArrayList;
    ArrayList<TaskModel> projectModelArrayList;
    ArrayList<AdditionalContactModel> additionalContactModelArrayList;
    ArrayList<NotesModel> notesModelArrayList;
    ArrayList<ProjectPhaseModel> projectPhaseModelArrayList;
    ArrayList<MediaModel> mediaModelArrayList;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_history, container, false);
        requestQueue = Volley.newRequestQueue(getActivity());
        mSharedPreferences = getActivity().getSharedPreferences(Utils.PREF_NAME, getActivity().MODE_PRIVATE);
        USERID = mSharedPreferences.getString(Utils.USER_ID, "");
        TOKEN = mSharedPreferences.getString(Utils.TOKEN, "");
        myCalendar = Calendar.getInstance();
        filter_status = mSharedPreferences.getString(Utils.ITEM_FILTER_STATUS, "");

        historyArrayList = new ArrayList<>();
        projectModelArrayList = new ArrayList<>();
        additionalContactModelArrayList = new ArrayList<>();
        notesModelArrayList = new ArrayList<>();
        projectPhaseModelArrayList = new ArrayList<>();
        mediaModelArrayList = new ArrayList<>();

        NavigationDrawerActivity.fab.setVisibility(View.GONE);
        NavigationDrawerActivity.filterView.setOnClickListener(this);
        NavigationDrawerActivity.titleText.setVisibility(View.VISIBLE);
        NavigationDrawerActivity.titleText.setText(getString(R.string.nav_history));
        NavigationDrawerActivity.filterView.setVisibility(View.VISIBLE);
        NavigationDrawerActivity.imgSyc.setVisibility(View.INVISIBLE);
        NavigationDrawerActivity.imgLogo.setVisibility(View.GONE);

        init();

//        if (Utils.checkInternetConnection(getActivity())) {
//            if (Utils.projectFilterRefreshFlag == false) {
//                if (filter_status.equals("")) {
//                    getHistroyList();
//                } else {
//                    getFilterHistroyList("", "", "", filter_status);
//                }
//            }
//        }

        mDbHelper = new DBHelper(getActivity());

        return view;
    }

    private void init() {
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_histroy);
        lisHistroy = (ListView) view.findViewById(R.id.lisHistroy);
        relRoot = (RelativeLayout) view.findViewById(R.id.relRoot);
        btnClearFilter = (TextView) view.findViewById(R.id.btnClearFilter);
        linDefault = (LinearLayout) view.findViewById(R.id.linDefault);
        linDetails = (LinearLayout) view.findViewById(R.id.linDetails);
        txtError = (TextView) view.findViewById(R.id.txtError);

        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeColors(Color.RED, Color.GREEN, Color.BLUE, Color.CYAN);

        btnClearFilter.setOnClickListener(this);
        btnClearFilter.setVisibility(View.GONE);

        dfDate = new SimpleDateFormat("yyyy-MM-dd");

        Utils.storeString(mSharedPreferences, Utils.FILTER_FROM_DATE, "");
        Utils.storeString(mSharedPreferences, Utils.FILTER_TO_DATE, "");
        Utils.storeString(mSharedPreferences, Utils.FILTER_PRIORITY, "");
        Utils.storeString(mSharedPreferences, Utils.FILTER_STATUS, "");

        builder = new AlertDialog.Builder(getContext());

    }

    public void openHistroyFilterView() {
        builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.fragment_histroy_filter_view, null);
        builder.setView(dialogView);

        fromDateDialog = (EditText) dialogView.findViewById(R.id.fromDate);
        toDateDialog = (EditText) dialogView.findViewById(R.id.toDate);
        imgClose = (ImageView) dialogView.findViewById(R.id.imgClose);

        btnFilter = (TextView) dialogView.findViewById(R.id.btnFilter);
        btnReset = (TextView) dialogView.findViewById(R.id.btnReset);

        linPending = (LinearLayout) dialogView.findViewById(R.id.linPending);
        linComplete = (LinearLayout) dialogView.findViewById(R.id.linComplete);
        linNH = (LinearLayout) dialogView.findViewById(R.id.linNH);
        linHN = (LinearLayout) dialogView.findViewById(R.id.linHN);

        imgPending = (ImageView) dialogView.findViewById(R.id.imgPending);
        imgCompeled = (ImageView) dialogView.findViewById(R.id.imgCompeled);
        imgNH = (ImageView) dialogView.findViewById(R.id.imgNH);
        imgHN = (ImageView) dialogView.findViewById(R.id.imgHN);

        fromDateDialog.setTypeface(Utils.getTypeFace(getContext()));
        toDateDialog.setTypeface(Utils.getTypeFace(getContext()));

        filter_status = mSharedPreferences.getString(Utils.ITEM_FILTER_STATUS, "");

        if (Utils.validateString(filter_status)) {
            if (filter_status.equalsIgnoreCase("Pending")) {
                imgPending.setImageResource(R.mipmap.radio_btn_active);
                imgCompeled.setImageResource(R.mipmap.radio_btn_green);
                Utils.storeString(mSharedPreferences, Utils.ITEM_FILTER_STATUS, "Pending");
            } else if (filter_status.equalsIgnoreCase("Completed")) {
                imgPending.setImageResource(R.mipmap.radio_btn_green);
                imgCompeled.setImageResource(R.mipmap.radio_btn_active);
                Utils.storeString(mSharedPreferences, Utils.ITEM_FILTER_STATUS, "Completed");
            }
        }

        if (Utils.validateString(mSharedPreferences.getString(Utils.FILTER_STATUS, ""))) {
            String status = mSharedPreferences.getString(Utils.FILTER_STATUS, "");
            if (status.equalsIgnoreCase("Pending")) {
                imgPending.setImageResource(R.mipmap.radio_btn_active);
                imgCompeled.setImageResource(R.mipmap.radio_btn_green);
                Utils.storeString(mSharedPreferences, Utils.FILTER_STATUS, "Pending");
            } else if (status.equalsIgnoreCase("Completed")) {
                imgPending.setImageResource(R.mipmap.radio_btn_green);
                imgCompeled.setImageResource(R.mipmap.radio_btn_active);
                Utils.storeString(mSharedPreferences, Utils.FILTER_STATUS, "Completed");
            }
        }

        if (Utils.validateString(mSharedPreferences.getString(Utils.FILTER_PRIORITY, ""))) {
            String priority = mSharedPreferences.getString(Utils.FILTER_PRIORITY, "");
            if (priority.equalsIgnoreCase("NH")) {
                imgNH.setImageResource(R.mipmap.radio_btn_active);
                imgHN.setImageResource(R.mipmap.radio_btn_green);
            } else if (priority.equalsIgnoreCase("HN")) {
                imgNH.setImageResource(R.mipmap.radio_btn_green);
                imgHN.setImageResource(R.mipmap.radio_btn_active);
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
        linPending.setOnClickListener(this);
        linComplete.setOnClickListener(this);
        linNH.setOnClickListener(this);
        linHN.setOnClickListener(this);

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
                openHistroyFilterView();
                break;

            case R.id.fromDate:
                if (Utils.validateString(filter_status)) {
                    radioStatus = filter_status;
                    filter_status = "";
                }
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
                if (Utils.validateString(filter_status)) {
                    radioStatus = filter_status;
                    filter_status = "";
                }
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
                if (Utils.validateString(filter_status)) {
//                    if (filter_status.equalsIgnoreCase("Completed")) {
//                        filter_status = "Complete";
//                    }
                    radioStatus = filter_status;
                    filter_status = "";
                } else {
                    filter_status = "";
                }

                if (fieldValidation()) {
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
//                    if (Utils.checkInternetConnection(getActivity())) {
//                        getFilterHistroyList(radioPriority, fromDate, toDate, radioStatus);
//                    } else {
                    String strPriority = "", startDate = "", dueDate = "";
                    if (Utils.validateString(radioPriority)) {
                        if (radioPriority.toUpperCase().equalsIgnoreCase("HN"))
                            strPriority = "ASC";
                        else
                            strPriority = "DESC";
                    }

                    String strQueryString = "";
                    if (Utils.validateString(radioStatus)) {
                        if (radioStatus.toUpperCase().equalsIgnoreCase("COMPLETED")) {
                            strQueryString = "project_status = 'Completed'";
                        } else {
                            strQueryString = "(project_status = 'Open' AND project_start_time = '')";
                        }
                    }

                    offlineFilterStatusHistroyProject(strPriority, strQueryString, fromDateDialog.getText().toString().trim(),
                            toDateDialog.getText().toString().trim());
                }
//                }

                break;

            case R.id.btnReset:
                Utils.storeString(mSharedPreferences, Utils.FILTER_FROM_DATE, "");
                Utils.storeString(mSharedPreferences, Utils.FILTER_TO_DATE, "");
                Utils.storeString(mSharedPreferences, Utils.FILTER_PRIORITY, "");
                Utils.storeString(mSharedPreferences, Utils.FILTER_STATUS, "");
                Utils.storeString(mSharedPreferences, Utils.ITEM_FILTER_STATUS, "");

                imgPending.setImageResource(R.mipmap.radio_btn_green);
                imgCompeled.setImageResource(R.mipmap.radio_btn_green);

                imgNH.setImageResource(R.mipmap.radio_btn_green);
                imgHN.setImageResource(R.mipmap.radio_btn_green);

                radioStatus = "";
                radioPriority = "";
                filter_status = "";

                fromDateDialog.setText("");
                toDateDialog.setText("");
                break;

            case R.id.btnClearFilter:
                Utils.storeString(mSharedPreferences, Utils.FILTER_FROM_DATE, "");
                Utils.storeString(mSharedPreferences, Utils.FILTER_TO_DATE, "");
                Utils.storeString(mSharedPreferences, Utils.FILTER_PRIORITY, "");
                Utils.storeString(mSharedPreferences, Utils.FILTER_STATUS, "");
                Utils.storeString(mSharedPreferences, Utils.ITEM_FILTER_STATUS, "");
                radioStatus = "";
                radioPriority = "";
                filter_status = "";

                btnClearFilter.setVisibility(View.GONE);
//                if (Utils.checkInternetConnection(getActivity())) {
//                    getHistroyList();
//                } else {
                offlineHistroyProject();
//                }
                break;

            case R.id.linPending:
                imgPending.setImageResource(R.mipmap.radio_btn_active);
                imgCompeled.setImageResource(R.mipmap.radio_btn_green);
                radioStatus = "Pending";
                filter_status = "";
                Utils.storeString(mSharedPreferences, Utils.FILTER_STATUS, radioStatus);
                break;

            case R.id.linComplete:
                imgPending.setImageResource(R.mipmap.radio_btn_green);
                imgCompeled.setImageResource(R.mipmap.radio_btn_active);
                radioStatus = "Completed";
                filter_status = "";
                Utils.storeString(mSharedPreferences, Utils.FILTER_STATUS, radioStatus);
                break;

            case R.id.linNH:
                // NH - Noraml to High
                imgNH.setImageResource(R.mipmap.radio_btn_active);
                imgHN.setImageResource(R.mipmap.radio_btn_green);
                radioPriority = "NH";
                if (Utils.validateString(filter_status)) {
                    radioStatus = filter_status;
                    filter_status = "";
                }
                Utils.storeString(mSharedPreferences, Utils.FILTER_STATUS, radioStatus);
                Utils.storeString(mSharedPreferences, Utils.FILTER_PRIORITY, radioPriority);
                break;

            case R.id.linHN:
                // NH - Noraml to High
                imgNH.setImageResource(R.mipmap.radio_btn_green);
                imgHN.setImageResource(R.mipmap.radio_btn_active);
                radioPriority = "HN";
                if (Utils.validateString(filter_status)) {
                    radioStatus = filter_status;
                    filter_status = "";
                }
                Utils.storeString(mSharedPreferences, Utils.FILTER_STATUS, radioStatus);
                Utils.storeString(mSharedPreferences, Utils.FILTER_PRIORITY, radioPriority);
                break;
        }
    }

    /*public void getHistroyList() {

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

        Logger.e(TAG, "HistroyList PARA---->" + String.valueOf(params));
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.POST, Utils.HISTROY_LIST_API, params, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Logger.e(TAG, "URL ::" + Utils.HISTROY_LIST_API);
                        Logger.e(TAG, "HistroyList RES---->" + String.valueOf(response));
                        pdialog.dismiss();
                        try {
                            boolean success = response.getBoolean("success");
                            String msg = response.getString("message");

                            if (success) {
                                historyArrayList.clear();
                                JSONArray dataArray = response.getJSONArray("data");
                                if (dataArray.length() > 0) {
//                                    setHistroyTaskList(dataArray);
                                    setHistroyList(dataArray);
                                } else {
                                    homeAdapter = new Fragment_HomeAdapter(getContext(), historyArrayList, histroyItemOnClick, statusBtnClick);
                                    homeAdapter.notifyDataSetChanged();
                                }
                            } else {
                                showMessage(msg);
                            }

                            if (!historyArrayList.isEmpty()) {
                                linDefault.setVisibility(View.GONE);
                                linDetails.setVisibility(View.VISIBLE);
                            } else {
                                linDefault.setVisibility(View.VISIBLE);
                                txtError.setText(msg);
                                linDetails.setVisibility(View.GONE);
                            }

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

    /*public void getFilterHistroyList(String priority, String fromDate, String toDate, String status) {

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
            params.put("project_status", status);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Logger.e(TAG, "HistroyFilterList PARA---->" + String.valueOf(params));
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.POST, Utils.FILTER_HISTROY_API, params, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Logger.e(TAG, "URL ::" + Utils.FILTER_HISTROY_API);
                        Logger.e(TAG, "HistroyFilterList RES---->" + String.valueOf(response));
                        pdialog.dismiss();

                        try {
                            boolean success = response.getBoolean("success");
                            String msg = response.getString("message");

                            if (success) {
                                historyArrayList.clear();
                                JSONArray dataArray = response.getJSONArray("data");
                                if (dataArray.length() > 0) {
//                                    setHistroyTaskList(dataArray);
                                    setHistroyList(dataArray);
                                } else {
                                    homeAdapter = new Fragment_HomeAdapter(getContext(), historyArrayList, histroyItemOnClick, statusBtnClick);
                                    homeAdapter.notifyDataSetChanged();
                                }
                            } else {
                                historyArrayList.clear();
                                showMessage(msg);
                            }

                            if (!historyArrayList.isEmpty()) {
                                linDefault.setVisibility(View.GONE);
                                linDetails.setVisibility(View.VISIBLE);
                            } else {
                                linDefault.setVisibility(View.VISIBLE);
                                txtError.setText(msg);
                                linDetails.setVisibility(View.GONE);
                            }

                            if (!Utils.validateString(filter_status))
                                btnClearFilter.setVisibility(View.VISIBLE);
                            else
                                btnClearFilter.setVisibility(View.GONE);

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

    public void offlineHistroyProject() {
        try {
            mDbHelper.openDataBase();

            historyArrayList = new ArrayList<>();
            projectModelArrayList = new ArrayList<>();
            projectModelArrayList = mDbHelper.getHistroyProjectDetails(Utils.REPORT);

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

        if (projectModelArrayList.size() > 0 || !projectModelArrayList.isEmpty()) {
            UpdateProjectObject();
        } else {
            linDefault.setVisibility(View.VISIBLE);
            txtError.setText("There are no projects in History.");
            linDetails.setVisibility(View.GONE);
        }
    }

    public void offlineFilterHistroyProject(String projectStatus) {
        try {
            mDbHelper.openDataBase();

            historyArrayList = new ArrayList<>();
            projectModelArrayList = new ArrayList<>();
            projectModelArrayList = mDbHelper.getHistroyStatusWiseProjectDetails(projectStatus);

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

        if (projectModelArrayList.size() > 0 || !projectModelArrayList.isEmpty()) {
            UpdateProjectObject();
        } else {
            linDefault.setVisibility(View.VISIBLE);
            txtError.setText("No projects planned!");
            linDetails.setVisibility(View.GONE);

            homeAdapter = new Fragment_HomeAdapter(getContext(), historyArrayList, histroyItemOnClick, statusBtnClick);
            homeAdapter.notifyDataSetChanged();
        }
    }

    public void offlineFilterStatusHistroyProject(String priority, String projectStatus, String startDate,
                                                  String dueDate) {
        try {
            mDbHelper.openDataBase();

            historyArrayList = new ArrayList<>();
            projectModelArrayList = new ArrayList<>();
            projectModelArrayList = mDbHelper.getHistroyFilterProjectDetails(priority, projectStatus, startDate, dueDate);

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

        if (projectModelArrayList.size() > 0 || !projectModelArrayList.isEmpty()) {
            btnClearFilter.setVisibility(View.VISIBLE);
            UpdateProjectObject();
        } else {
            btnClearFilter.setVisibility(View.VISIBLE);
            linDefault.setVisibility(View.VISIBLE);
            txtError.setText("No records found.");
            linDetails.setVisibility(View.GONE);

            homeAdapter = new Fragment_HomeAdapter(getContext(), historyArrayList, histroyItemOnClick, statusBtnClick);
            homeAdapter.notifyDataSetChanged();
        }
    }

    public void UpdateProjectObject() {
        if (projectModelArrayList.size() > 0 || !projectModelArrayList.isEmpty()) {
            linDefault.setVisibility(View.GONE);
            swipeRefreshLayout.setVisibility(View.VISIBLE);
            linDetails.setVisibility(View.VISIBLE);

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
            txtError.setText("There are no Histroy");
            linDetails.setVisibility(View.GONE);
            swipeRefreshLayout.setVisibility(View.GONE);
        }
    }

    /*public void setHistroyList(JSONArray dataArray) {
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

                                mediaModelArrayList.add(mediaModel);

                                mDbHelper.insertProjectMedia(mDbHelper.myDataBase, mediaModel.getMediaId(), object.getString("id"),
                                        mediaModel.getMediaType(), mediaModel.getMedia(), mediaModel.getVideoThumbImage(), mediaModel.getMediaDescription(),
                                        mediaModel.getLatitude(), mediaModel.getLongitude(), mediaModel.getStreet(), mediaModel.getCity(),
                                        mediaModel.getState(), mediaModel.getCountry(), mediaModel.getPincode(), mediaModel.getCreated_date(),
                                        mediaModel.getIs_sync(), mediaModel.getMedia_status());

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
                            taskModel.getProject_update_status(), taskModel.getProject_start_time(), taskModel.getCreated_date());

                    mDbHelper.close();

                } catch (SQLException e) {
                    e.printStackTrace();

                }

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        offlineHistroyProject();

    }*/

    public void setProjectList(JSONArray dataArray) {
        try {
            historyArrayList = new ArrayList<>();

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
//                if (status.equalsIgnoreCase(Utils.OPEN) || status.equalsIgnoreCase(Utils.START) || status.equalsIgnoreCase(Utils.PENDING)) {
//                    status = Utils.PENDING;
//                } else if (status.equalsIgnoreCase(Utils.OPEN) && Utils.validateString(projectStartTime)) {
//                    status = Utils.COMPLETE;
//                } else if (status.equals(Utils.COMPLETE) && Utils.validateString(signature)) {
//                    status = Utils.REPORT;
//                }
//
//                taskModel.setStatus(status);
                historyArrayList.add(taskModel);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        homeAdapter = new Fragment_HomeAdapter(getContext(), historyArrayList, histroyItemOnClick, statusBtnClick);
        lisHistroy.setAdapter(homeAdapter);

        if (Utils.checkInternetConnection(getActivity())) {
            if (Utils.checkInternetConnection(getActivity())) {
                mediaModelArrayList = new ArrayList<>();
                mediaModelArrayList = mDbHelper.getProjectMediaDetails();
                File file;
                for (int i = 0; i < mediaModelArrayList.size(); i++) {
                    String media_id = mediaModelArrayList.get(i).getMediaId();
                    String mediaType = mediaModelArrayList.get(i).getMediaType();
                    String media = mediaModelArrayList.get(i).getMedia();
                    String videoThumb = "", docThumb = "";
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
        }

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

    /*public void setHistroyTaskList(JSONArray dataArray) {
        try {
            for (int i = 0; i < dataArray.length(); i++) {
                JSONObject object = dataArray.getJSONObject(i);
                TaskModel taskModel = new TaskModel();
                taskModel.setTaskObject(String.valueOf(object));
                taskModel.setId(object.getString("id"));
                taskModel.setTitle(object.getString("title"));
                taskModel.setDescription(object.getString("description"));
                taskModel.setStartDate(object.getString("start_date"));
                taskModel.setStartTime(object.getString("start_time"));
                taskModel.setDueDate(object.getString("due_date"));
                taskModel.setDueTime(object.getString("due_time"));
                taskModel.setPriority(object.getString("priority"));
                String signature = object.getString("signature_image");

                String status = object.getString("project_status");

                if (status.equalsIgnoreCase("open") || status.equalsIgnoreCase("Start") || status.equalsIgnoreCase("Pending")) {
                    if (status.equalsIgnoreCase("Pending")) {
                        status = Utils.PENDING;
                    } else {
                        status = Utils.START;
                    }
                } else if (status.equalsIgnoreCase("complete") && signature.equals("")) {
                    status = Utils.COMPLETE;
                } else if (status.equals("Complete") && !signature.equals("")) {
                    status = Utils.REPORT;
                }

                taskModel.setStatus(status);
                historyArrayList.add(taskModel);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        homeAdapter = new Fragment_HomeAdapter(getContext(), historyArrayList, histroyItemOnClick, statusBtnClick);
        lisHistroy.setAdapter(homeAdapter);
    }*/

    View.OnClickListener histroyItemOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int index = (int) v.getTag();
            Log.d("INDEX", "" + index);
            String taskObject = historyArrayList.get(index).getTaskObject();

            Intent taskIntent = new Intent(getActivity(), ProjectDetailsActivity.class);
            startActivity(taskIntent);
            Utils.storeString(mSharedPreferences, Utils.TASK_OBJECT, taskObject);
            Utils.storeString(mSharedPreferences, Utils.PROJECT_STATUS, "");
            Utils.storeString(mSharedPreferences, Utils.PROJECT_START_TIME, "");
        }
    };

    String projectID;
    boolean statusFlag = true;

    View.OnClickListener statusBtnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (statusFlag) {
                statusFlag = false;
                int index = (int) v.getTag();
                Log.d("INDEX", "" + index);
                String taskObject = historyArrayList.get(index).getTaskObject();
                String status = historyArrayList.get(index).getProject_status();
                String project_start_time = historyArrayList.get(index).getProject_start_time();
                projectID = historyArrayList.get(index).getId();
                System.out.println("status :::" + status + " ID : " + projectID);

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

    public void openSignatureView() {
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
                new uploadSignature().execute();
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
    }


    public void generateReports() {
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
                                Utils.projectFilterRefreshFlag = true;
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


    public class uploadSignature extends AsyncTask<Void, Void, Void> {
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

                if (success) {
                    if (!jsonObject.isNull("data")) {
                        Utils.projectRefreshFlag = true;
                        Utils.projectFilterRefreshFlag = true;
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
                *//*Intent taskIntent = new Intent(getActivity(), ProjectDetailsActivity.class);
                startActivity(taskIntent);
                Utils.storeString(mSharedPreferences, Utils.TASK_OBJECT, jObj.toString());*//*
                *//*if (Utils.validateString(radioPriority) || Utils.validateString(fromDate) ||
                        Utils.validateString(toDate) || Utils.validateString(radioStatus)) {
                    getFilterHistroyList(radioPriority, fromDate, toDate, radioStatus);
                    filter_status = "";
                    Utils.storeString(mSharedPreferences, Utils.ITEM_FILTER_STATUS, "");
                } else if (Utils.validateString(mSharedPreferences.getString(Utils.ITEM_FILTER_STATUS, ""))) {
                    getFilterHistroyList("", "", "", mSharedPreferences.getString(Utils.ITEM_FILTER_STATUS, ""));
                } else {
                    getHistroyList();
                }*//*
            }
        }
    }


    public void updateProjectStatus(JSONArray projectArray) {
        pdialog = new ProgressDialog(getActivity());
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

                            if (success) {
                                Utils.projectRefreshFlag = true;
                                Utils.projectFilterRefreshFlag = true;
                                statusFlag = true;
                                *//*if (Utils.validateString(radioPriority) || Utils.validateString(fromDate) ||
                                        Utils.validateString(toDate) || Utils.validateString(radioStatus)) {
                                    getFilterHistroyList(radioPriority, fromDate, toDate, radioStatus);
                                    filter_status = "";
                                    Utils.storeString(mSharedPreferences, Utils.ITEM_FILTER_STATUS, "");
                                } else if (Utils.validateString(mSharedPreferences.getString(Utils.ITEM_FILTER_STATUS, ""))) {
                                    getFilterHistroyList("", "", "", mSharedPreferences.getString(Utils.ITEM_FILTER_STATUS, ""));
                                } else {
                                    getHistroyList();
                                }*//*

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


    @Override
    public void onRefresh() {
        /*if (Utils.checkInternetConnection(getActivity())) {
            if (pdialog != null && !pdialog.isShowing()) {
                swipeRefreshLayout.setRefreshing(true);
                if (btnClearFilter.getVisibility() == View.GONE) {
                    getHistroyList();
                } else {
                    if (Utils.validateString(radioPriority) || Utils.validateString(fromDate) ||
                            Utils.validateString(toDate) || Utils.validateString(radioStatus)) {
                        getFilterHistroyList(radioPriority, fromDate, toDate, radioStatus);
                        filter_status = "";
                        Utils.storeString(mSharedPreferences, Utils.ITEM_FILTER_STATUS, "");
                    } else if (Utils.validateString(mSharedPreferences.getString(Utils.ITEM_FILTER_STATUS, ""))) {
                        getFilterHistroyList("", "", "", mSharedPreferences.getString(Utils.ITEM_FILTER_STATUS, ""));
                    }

                }
            }
        } else {
            swipeRefreshLayout.setRefreshing(false);
        }*/

        swipeRefreshLayout.setRefreshing(false);

    }


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
            String to = toDateDialog.getText().toString();
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
            String from = fromDateDialog.getText().toString();
            boolean flag = CheckDates(from, to);

            if (flag == true) {
                toDateDialog.setText(new StringBuilder().append(yy).append("-").append(month).append("-").append(day));
            } else {
                Utils.showMessageDialog(getActivity(), getResources().getString(R.string.alert), getString(R.string.before_date));
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
        Utils.showResponseMessage(getActivity(), relRoot, message);
    }

    public boolean fieldValidation() {
        boolean flag = true;
        if (!Utils.validateString(radioStatus)) {
            flag = false;
            showMessage(getString(R.string.select_status));
        }
        return flag;
    }

    @Override
    public void onResume() {
        super.onResume();

        NavigationDrawerActivity.navItemIndex = 1;
        NavigationDrawerActivity.selectMenu(1);

//        if (Utils.checkInternetConnection(getActivity())) {
//            if (Utils.projectFilterRefreshFlag == true) {
//                Utils.projectFilterRefreshFlag = false;
//                if (Utils.validateString(radioPriority) || Utils.validateString(fromDate) ||
//                        Utils.validateString(toDate) || Utils.validateString(radioStatus)) {
//                    getFilterHistroyList(radioPriority, fromDate, toDate, radioStatus);
//                    filter_status = "";
//                    Utils.storeString(mSharedPreferences, Utils.ITEM_FILTER_STATUS, "");
//                } else if (Utils.validateString(mSharedPreferences.getString(Utils.ITEM_FILTER_STATUS, ""))) {
//                    getFilterHistroyList("", "", "", mSharedPreferences.getString(Utils.ITEM_FILTER_STATUS, ""));
//                } else {
//                    getHistroyList();
//                }
//            }
//        } else {
        if (Utils.validateString(radioPriority) || Utils.validateString(fromDate) ||
                Utils.validateString(toDate) || Utils.validateString(radioStatus)) {
            String strPriority = "", startDate = "", dueDate = "";
            if (Utils.validateString(radioPriority)) {
                if (radioPriority.toUpperCase().equalsIgnoreCase("HN"))
                    strPriority = "ASC";
                else
                    strPriority = "DESC";
            }

            String strQueryString = "";
            if (radioStatus.toUpperCase().equalsIgnoreCase("COMPLETED")) {
                strQueryString = "project_status = 'Completed'";
            } else {
                strQueryString = "(project_status = 'Open' AND project_start_time = '')";
            }

//            if (Utils.validateString(fromDate)) {
//                startDate = Utils.parseDateFrom(fromDate);
//            }
//
//            if (Utils.validateString(toDate)) {
//                dueDate = Utils.parseDateFrom(toDate);
//            }

            offlineFilterStatusHistroyProject(strPriority, strQueryString, fromDate, toDate);
            filter_status = "";
            Utils.storeString(mSharedPreferences, Utils.ITEM_FILTER_STATUS, "");
        } else if (Utils.validateString(mSharedPreferences.getString(Utils.ITEM_FILTER_STATUS, ""))) {
            if (mSharedPreferences.getString(Utils.ITEM_FILTER_STATUS, "").equalsIgnoreCase(Utils.REPORT)) {
                offlineFilterHistroyProject(Utils.REPORT);
            } else {
                offlineFilterHistroyProject(Utils.OPEN);
            }
        } else {
            offlineHistroyProject();
        }
        statusFlag = true;
//        }
    }

}
