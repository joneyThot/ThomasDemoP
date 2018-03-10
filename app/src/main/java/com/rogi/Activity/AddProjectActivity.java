package com.rogi.Activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

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
import com.rogi.Fragment.Month_YearDialogFragment;
import com.rogi.R;
import com.rogi.Service.GPSTracker;
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
import java.util.Date;
import java.util.List;


public class AddProjectActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String EXTRA_CIRCULAR_REVEAL_X = "EXTRA_CIRCULAR_REVEAL_X";
    public static final String EXTRA_CIRCULAR_REVEAL_Y = "EXTRA_CIRCULAR_REVEAL_Y";
    public static final String TAG = "AddProjectActivity";

    private int revealX;
    private int revealY;
    private SharedPreferences mSharedPreferences;
    public double latitude = 0.0, longitude = 0.0;
    private DBHelper mDbHelper;

    View rootLayout;
    TextView titleText, createTaskBTN;
    EditText edtTaskTitle, edtTaskDescription, edtTaskStartDate, edtTaskStartTime, edtTaskDueDate, edtTaskDueTime,
            edtTaskStreet, edtTaskCity, edtTaskState, edtTaskCountry, edtTaskZipCode, edtReminderTime;
    LinearLayout linNormal, linHigh, linReminderTime;
    ImageView imgNormal, imgHigh;

    RequestQueue requestQueue;
    Intent intent;
    Calendar myCalendar;
    ProgressDialog pdialog;
    String titleStr = "", descriptionStr = "", startTimeStr = "", startDateStr = "", dueTimeStr = "",
            dueDateStr = "", priorityStr = "", streetStr = "", citystr = "", stateStr = "", countryStr = "",
            zipCodeStr = "", reminderHoursStr = "", TOKEN = "", USERID = "", addrs = "";
    SimpleDateFormat dfDate;
    AddressResultReceiver mResultReceiver;
    boolean fetchAddress;
    int fetchType = Utils.USE_ADDRESS_NAME;
    boolean flagOnClick = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);
        requestQueue = Volley.newRequestQueue(this);
        mSharedPreferences = getSharedPreferences(Utils.PREF_NAME, MODE_PRIVATE);
        USERID = mSharedPreferences.getString(Utils.USER_ID, "");
        TOKEN = mSharedPreferences.getString(Utils.TOKEN, "");

        init();
        animationOpen(savedInstanceState);
    }

    public void init() {
        rootLayout = findViewById(R.id.root_layout);

        titleText = (TextView) findViewById(R.id.titleText);
        titleText.setText("Add Project");

        createTaskBTN = (TextView) findViewById(R.id.createTaskBTN);

        edtTaskTitle = (EditText) findViewById(R.id.edtTaskTitle);
        edtTaskDescription = (EditText) findViewById(R.id.edtTaskDescription);
        edtTaskStartDate = (EditText) findViewById(R.id.edtTaskStartDate);
        edtTaskStartTime = (EditText) findViewById(R.id.edtTaskStartTime);
        edtTaskDueDate = (EditText) findViewById(R.id.edtTaskDueDate);
        edtTaskDueTime = (EditText) findViewById(R.id.edtTaskDueTime);
        edtTaskStreet = (EditText) findViewById(R.id.edtTaskStreet);
        edtTaskCity = (EditText) findViewById(R.id.edtTaskCity);
        edtTaskState = (EditText) findViewById(R.id.edtTaskState);
        edtTaskCountry = (EditText) findViewById(R.id.edtTaskCountry);
        edtTaskZipCode = (EditText) findViewById(R.id.edtTaskZipCode);
        linReminderTime = (LinearLayout) findViewById(R.id.linReminderTime);
        edtReminderTime = (EditText) findViewById(R.id.edtReminderTime);

        linNormal = (LinearLayout) findViewById(R.id.linNormal);
        linHigh = (LinearLayout) findViewById(R.id.linHigh);
        imgNormal = (ImageView) findViewById(R.id.imgNormal);
        imgHigh = (ImageView) findViewById(R.id.imgHigh);

        edtTaskStartDate.setOnClickListener(this);
        edtTaskDueDate.setOnClickListener(this);
        edtTaskStartTime.setOnClickListener(this);
        edtTaskDueTime.setOnClickListener(this);
        createTaskBTN.setOnClickListener(this);
        linNormal.setOnClickListener(this);
        linHigh.setOnClickListener(this);

        imgNormal.setImageResource(R.mipmap.radio_btn_active);
        imgHigh.setImageResource(R.mipmap.radio_btn_red);
        priorityStr = "Normal";
        findViewById(R.id.backLayoutclick).setOnClickListener(this);

        myCalendar = Calendar.getInstance();

        edtTaskTitle.setTypeface(Utils.getTypeFace(AddProjectActivity.this));
        edtTaskDescription.setTypeface(Utils.getTypeFace(AddProjectActivity.this));
        edtTaskStartDate.setTypeface(Utils.getTypeFace(AddProjectActivity.this));
        edtTaskStartTime.setTypeface(Utils.getTypeFace(AddProjectActivity.this));
        edtTaskDueDate.setTypeface(Utils.getTypeFace(AddProjectActivity.this));
        edtTaskDueTime.setTypeface(Utils.getTypeFace(AddProjectActivity.this));
        edtTaskStreet.setTypeface(Utils.getTypeFace(AddProjectActivity.this));
        edtTaskCity.setTypeface(Utils.getTypeFace(AddProjectActivity.this));
        edtTaskState.setTypeface(Utils.getTypeFace(AddProjectActivity.this));
        edtTaskCountry.setTypeface(Utils.getTypeFace(AddProjectActivity.this));
        edtTaskZipCode.setTypeface(Utils.getTypeFace(AddProjectActivity.this));
        edtReminderTime.setTypeface(Utils.getTypeFace(AddProjectActivity.this));

        dfDate = new SimpleDateFormat("yyyy-MM-dd");
        mResultReceiver = new AddressResultReceiver(null);

        mDbHelper = new DBHelper(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.edtTaskStartDate:
                edtReminderTime.setText("");
                int year = 0, month = 0, day = 0;
                if (edtTaskStartDate.getText().toString().trim().equalsIgnoreCase("")) {
                    year = myCalendar.get(Calendar.YEAR);
                    month = myCalendar.get(Calendar.MONTH);
                    day = myCalendar.get(Calendar.DAY_OF_MONTH);
                } else {
                    String d = edtTaskStartDate.getText().toString();
                    year = Integer.parseInt(d.substring(0, 4));
                    month = Integer.parseInt(d.substring(5, 7)) - 1;
                    day = Integer.parseInt(d.substring(8, 10));
                }

                DatePickerDialog dpdFrom = new DatePickerDialog(AddProjectActivity.this, R.style.DialogTheme, taskStartDatePicker, year, month, day);
                dpdFrom.show();
                break;

            case R.id.edtTaskDueDate:
                edtReminderTime.setText("");
                if (!edtTaskStartDate.getText().toString().equalsIgnoreCase("") && !edtTaskStartDate.getText().toString().equalsIgnoreCase(null)) {
                    int year1 = 0, month1 = 0, day1 = 0;
                    if (edtTaskDueDate.getText().toString().trim().equalsIgnoreCase("")) {
                        year1 = myCalendar.get(Calendar.YEAR);
                        month1 = myCalendar.get(Calendar.MONTH);
                        day1 = myCalendar.get(Calendar.DAY_OF_MONTH);
                    } else {
                        String d = edtTaskDueDate.getText().toString();
                        year1 = Integer.parseInt(d.substring(0, 4));
                        month1 = Integer.parseInt(d.substring(5, 7)) - 1;
                        day1 = Integer.parseInt(d.substring(8, 10));
                    }

                    DatePickerDialog dpdTo = new DatePickerDialog(AddProjectActivity.this, R.style.DialogTheme, taskDueDatePicker, year1, month1, day1);
                    long currentTime = System.currentTimeMillis() - 1000;
                    dpdTo.getDatePicker().setMinDate(currentTime);
                    dpdTo.show();
                } else {
                    Utils.showMessageDialog(AddProjectActivity.this, getResources().getString(R.string.alert), getString(R.string.start_date));
                }
                break;

            case R.id.edtTaskStartTime:
                edtReminderTime.setText("");
                if (edtTaskStartDate.getText().toString().equalsIgnoreCase(edtTaskDueDate.getText().toString())) {
                    int taskStartHour, taskStartMinute;
                    if (edtTaskStartTime.getText().toString().trim().equalsIgnoreCase("")) {
                        Calendar c = Calendar.getInstance();
                        taskStartHour = c.get(Calendar.HOUR_OF_DAY);
                        taskStartMinute = c.get(Calendar.MINUTE);
                    } else {
                        String time = edtTaskStartTime.getText().toString().trim();
                        taskStartHour = Integer.parseInt(time.substring(0, 2));
                        taskStartMinute = Integer.parseInt(time.substring(3, 5));
                    }

                    TimePickerDialog mTaskStartTimePicker = new TimePickerDialog(AddProjectActivity.this, R.style.DialogTheme, new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                            String h = Utils.utilTime(selectedHour);
                            String m = Utils.utilTime(selectedMinute);
                            edtTaskStartTime.setText(h + ":" + m);
                            if (edtTaskStartTime.getText().toString().equalsIgnoreCase(edtTaskDueTime.getText().toString())) {
                                Utils.showMessageDialog(AddProjectActivity.this, getResources().getString(R.string.alert), getString(R.string.same_time));
                                edtTaskStartTime.setText("");
                            }
                            reminderHours();
                        }
                    }, taskStartHour, taskStartMinute, false);//Yes 24 hour time
                    mTaskStartTimePicker.show();
                } else {
                    int taskStartHour, taskStartMinute;
                    if (edtTaskStartTime.getText().toString().trim().equalsIgnoreCase("")) {
                        Calendar c = Calendar.getInstance();
                        taskStartHour = c.get(Calendar.HOUR_OF_DAY);
                        taskStartMinute = c.get(Calendar.MINUTE);
                    } else {
                        String time = edtTaskStartTime.getText().toString().trim();
                        taskStartHour = Integer.parseInt(time.substring(0, 2));
                        taskStartMinute = Integer.parseInt(time.substring(3, 5));
                    }

                    TimePickerDialog mTaskStartTimePicker = new TimePickerDialog(AddProjectActivity.this, R.style.DialogTheme, new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                            String h = Utils.utilTime(selectedHour);
                            String m = Utils.utilTime(selectedMinute);
                            edtTaskStartTime.setText(h + ":" + m);
                            reminderHours();
                        }
                    }, taskStartHour, taskStartMinute, false);//Yes 24 hour time
                    mTaskStartTimePicker.show();
                }

                break;

            case R.id.edtTaskDueTime:
                edtReminderTime.setText("");
                if (!edtTaskStartTime.getText().toString().equalsIgnoreCase("") && !edtTaskStartTime.getText().toString().equalsIgnoreCase(null)) {
                    if (edtTaskStartDate.getText().toString().equalsIgnoreCase(edtTaskDueDate.getText().toString())) {
                        int taskDueHour, taskDueMinute;
                        if (edtTaskDueTime.getText().toString().trim().equalsIgnoreCase("")) {
                            Calendar c = Calendar.getInstance();
                            taskDueHour = c.get(Calendar.HOUR_OF_DAY);
                            taskDueMinute = c.get(Calendar.MINUTE);
                        } else {
                            String time = edtTaskDueTime.getText().toString().trim();
                            taskDueHour = Integer.parseInt(time.substring(0, 2));
                            taskDueMinute = Integer.parseInt(time.substring(3, 5));
                        }
                        TimePickerDialog mTaskDueTimePicker = new TimePickerDialog(AddProjectActivity.this, R.style.DialogTheme, new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                                String h = Utils.utilTime(selectedHour);
                                String m = Utils.utilTime(selectedMinute);
                                edtTaskDueTime.setText(h + ":" + m);
                                if (edtTaskStartTime.getText().toString().equalsIgnoreCase(edtTaskDueTime.getText().toString())) {
                                    Utils.showMessageDialog(AddProjectActivity.this, getResources().getString(R.string.alert), getString(R.string.same_time));
                                    edtTaskDueTime.setText("");
                                }
                                reminderHours();
                            }
                        }, taskDueHour, taskDueMinute, false);//Yes 24 hour time
                        mTaskDueTimePicker.show();

                    } else {
                        int taskDueHour, taskDueMinute;
                        if (edtTaskDueTime.getText().toString().trim().equalsIgnoreCase("")) {
                            Calendar c = Calendar.getInstance();
                            taskDueHour = c.get(Calendar.HOUR_OF_DAY);
                            taskDueMinute = c.get(Calendar.MINUTE);
                        } else {
                            String time = edtTaskDueTime.getText().toString().trim();
                            taskDueHour = Integer.parseInt(time.substring(0, 2));
                            taskDueMinute = Integer.parseInt(time.substring(3, 5));
                        }
                        TimePickerDialog mTaskDueTimePicker = new TimePickerDialog(AddProjectActivity.this, R.style.DialogTheme, new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                                String h = Utils.utilTime(selectedHour);
                                String m = Utils.utilTime(selectedMinute);
                                edtTaskDueTime.setText(h + ":" + m);
                                reminderHours();
                            }
                        }, taskDueHour, taskDueMinute, false);//Yes 24 hour time
                        mTaskDueTimePicker.show();
                    }
                } else {
                    Utils.showMessageDialog(AddProjectActivity.this, getResources().getString(R.string.alert), getString(R.string.due_time));
                }
                break;

            case R.id.createTaskBTN:
                Utils.hideSoftKeyboard(this);
                if (fieldValidation()) {
                    if (flagOnClick) {
                        flagOnClick = false;
                        getEditTextData();
                        if (Utils.checkInternetConnection(this)) {
                            fetchAddress = false;
                            Intent intent = new Intent(this, GeocodeAddressIntentService.class);
                            intent.putExtra(Utils.RECEIVER, mResultReceiver);
                            intent.putExtra(Utils.FETCH_TYPE_EXTRA, fetchType);
                            if (fetchType == Utils.USE_ADDRESS_NAME) {
                                if (addrs.length() == 0) {
                                    showMessage("Address is not valid");
                                    return;
                                }
                                intent.putExtra(Utils.LOCATION_NAME_DATA_EXTRA, addrs);
                            }
                            Logger.e(TAG, "Starting Service");
                            startService(intent);

                            pdialog = new ProgressDialog(AddProjectActivity.this);
                            pdialog.setMessage(getString(R.string.please_wait));
                            pdialog.setCanceledOnTouchOutside(false);
                            pdialog.show();
                        } else {
                            String projectCreatedTime = Utils.getCurrentDateAndTime();
                            String tempId = Utils.getRandomAlphaNumeric(6);

                            GPSTracker gpsTracker = Utils.getCurrentLocation(this);
                            if (gpsTracker != null) {
                                latitude = gpsTracker.getLatitude();
                                longitude = gpsTracker.getLongitude();
                            } else {
                                latitude = 0;
                                longitude = 0;
                            }

                            String assign_by_name = mSharedPreferences.getString(Utils.FIRST_NAME, "") + " " + mSharedPreferences.getString(Utils.LAST_NAME, "");
                            String assign_by_phone = mSharedPreferences.getString(Utils.PHONE_NUMBER, "");
                            String assign_by_email = mSharedPreferences.getString(Utils.EMAIL, "");

                            try {
                                mDbHelper.openDataBase();
                                mDbHelper.insertProject(mDbHelper.myDataBase, tempId, titleStr, descriptionStr, startDateStr, startTimeStr + ":00",
                                        dueDateStr, dueTimeStr + ":00", priorityStr, "Open", streetStr, citystr, stateStr, countryStr, zipCodeStr,
                                        String.valueOf(latitude), String.valueOf(longitude), assign_by_name, assign_by_phone, assign_by_email,
                                        "N", "", "A", "", projectCreatedTime, reminderHoursStr);
                                mDbHelper.close();
                            } catch (SQLException e) {
                                e.printStackTrace();

                            }

                            finish();
                        }
                    }

                }

                break;

            case R.id.linNormal:
                imgNormal.setImageResource(R.mipmap.radio_btn_active);
                imgHigh.setImageResource(R.mipmap.radio_btn_red);
                priorityStr = "Normal";
                break;

            case R.id.linHigh:
                imgNormal.setImageResource(R.mipmap.radio_btn_red);
                imgHigh.setImageResource(R.mipmap.radio_btn_active);
                priorityStr = "High";
                break;

            case R.id.backLayoutclick:
                Utils.hideSoftKeyboard(AddProjectActivity.this);
                unRevealActivity();
                break;

            case R.id.edtReminderTime:
                if (hours > 24) {
                    reminderHoursValues(24);
                } else {
                    reminderHoursValues(hours - 1);
                }

                break;
        }
    }

    long hours = 0;

    public void reminderHours() {
        if (dateTimeValidation()) {
            String strStartDateAndTime = edtTaskStartDate.getText().toString().trim() + " " + edtTaskStartTime.getText().toString().trim();
            String strDueDateAndTime = edtTaskDueDate.getText().toString().trim() + " " + edtTaskDueTime.getText().toString().trim();
            Logger.e(TAG, "StartDate :: " + strStartDateAndTime + " DueDate :: " + strDueDateAndTime);
            Date dStart = null, dDue = null;
            SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            try {
                dStart = input.parse(strStartDateAndTime);
                dDue = input.parse(strDueDateAndTime);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            long diff = dDue.getTime() - dStart.getTime();
            long seconds = diff / 1000;
            long minutes = seconds / 60;
            hours = minutes / 60;
            long days = hours / 24;
            Logger.e(TAG, "hours :: " + hours);

            if (hours > 1) {
                edtReminderTime.setOnClickListener(this);
            } else {
                edtReminderTime.setOnClickListener(null);
            }

        }
    }

    List<String> reminderHoursItems = new ArrayList<>();

    public void reminderHoursValues(long hours) {
        int iEnd = (int) hours;
        reminderHoursItems = new ArrayList<>();
        for (int i = 1; i <= iEnd; i++) {
            String month = String.valueOf(i);
            if (month.length() < 1)
                reminderHoursItems.add("0" + month);
            else
                reminderHoursItems.add(month);
        }
        Utils.storeString(mSharedPreferences, Utils.REMINDER_TYPE, Utils.REMINDER_TYPE);
        FragmentManager manager1 = this.getSupportFragmentManager();
        Month_YearDialogFragment dialogFragment = new Month_YearDialogFragment("Reminder Hours", reminderHoursItems, reminderHoursListner, null);
        dialogFragment.show(manager1, "dialog");
    }

    View.OnClickListener reminderHoursListner = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int index = (int) v.getTag();
            Log.d("INDEX", "" + index);
            dismissDialog();
            String reminderHours = reminderHoursItems.get(index);
            edtReminderTime.setText("" + reminderHours + " Hours");
        }
    };

    public void dismissDialog() {
        Fragment prev = getSupportFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            DialogFragment df = (DialogFragment) prev;
            df.dismiss();
        }
    }

    public void getEditTextData() {
        titleStr = edtTaskTitle.getText().toString().trim();
        descriptionStr = edtTaskDescription.getText().toString().trim();
        startDateStr = edtTaskStartDate.getText().toString().trim();
        startTimeStr = edtTaskStartTime.getText().toString().trim();
        dueDateStr = edtTaskDueDate.getText().toString().trim();
        dueTimeStr = edtTaskDueTime.getText().toString().trim();
        streetStr = edtTaskStreet.getText().toString().trim();
        citystr = edtTaskCity.getText().toString().trim();
        stateStr = edtTaskState.getText().toString().trim();
        countryStr = edtTaskCountry.getText().toString().trim();
        zipCodeStr = edtTaskZipCode.getText().toString().trim();
        if (Utils.validateString(edtReminderTime.getText().toString().trim())) {
            String number = edtReminderTime.getText().toString().trim().replaceAll("[^0-9]", "");
            reminderHoursStr = number;
        } else {
            reminderHoursStr = "";
        }
        addrs = streetStr + " " + citystr + " " + stateStr + " " + countryStr + " " + zipCodeStr;
    }


    JSONArray projectArray;

    public void taskDataArray() {
        try {
            projectArray = new JSONArray();
            JSONObject object = new JSONObject();
            object.put("title", titleStr);
            object.put("description", descriptionStr);
            object.put("start_date", startDateStr);
            object.put("start_time", startTimeStr);
            object.put("due_date", dueDateStr);
            object.put("due_time", dueTimeStr);
            object.put("priority", priorityStr);
            object.put("street", streetStr);
            object.put("city", citystr);
            object.put("state", stateStr);
            object.put("country", countryStr);
            object.put("pincode", zipCodeStr);
            object.put("lattitude", latitude);
            object.put("longitude", longitude);
            object.put("remind_hours", reminderHoursStr);
            projectArray.put(object);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void addTask() {

        pdialog = new ProgressDialog(AddProjectActivity.this);
        pdialog.setMessage(getString(R.string.please_wait));
        pdialog.setCanceledOnTouchOutside(false);
        pdialog.show();

        JSONObject params = new JSONObject();
        try {
            params.put("user_id", USERID);
            params.put("session_token", TOKEN);
            params.put("projects", projectArray);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        Logger.e(TAG, "addTask PARA---->" + String.valueOf(params));
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.POST, Utils.MAIN_URL + Utils.ADD_TASK_API, params, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Logger.e(TAG, "addTask RES---->" + String.valueOf(response));
                        try {
                            boolean success = response.getBoolean("success");
                            String msg = response.getString("message");
                            flagOnClick = true;
                            boolean Auth = response.getBoolean("authenticated");
                            if (Auth) {
                                if (success) {
                                    pdialog.dismiss();
                                    Utils.projectRefreshFlag = true;
                                    finish();

                                } else {
                                    pdialog.dismiss();
                                    showMessage(msg);
                                }
                            } else {
                                pdialog.dismiss();
                                NavigationDrawerActivity.clearPrefData();
                                AddProjectActivity.this.deleteDatabase(DBConstants.DB_NAME);
                                File dir = new File(Environment.getExternalStorageDirectory() + "/Media");
                                Utils.deleteDirectory(dir);
                                startActivity(new Intent(AddProjectActivity.this, LoginActivity.class));
                                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
                                finish();
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
                            showMessage(getString(R.string.checkInternet));
                        }
                    }
                });
        int socketTimeout = 30000;//30 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsObjRequest.setRetryPolicy(policy);
        requestQueue.add(jsObjRequest);
    }


    //TODO: Time & Date Picker...........

    DatePickerDialog.OnDateSetListener taskStartDatePicker = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int yy, int mm,
                              int dd) {
            // TODO Auto-generated method stub

            String day = String.valueOf(dd);
            String month = String.valueOf(mm + 1);
            if (day.trim().length() == 1)
                day = "0" + day;
            if (month.trim().length() == 1)
                month = "0" + month;
            String from = yy + "-" + month + "-" + day;
            String to = edtTaskDueDate.getText().toString();
            if (Utils.validateString(to)) {
                boolean flag = CheckDates(from, to);
                if (flag == true) {
                    edtTaskStartDate.setText(new StringBuilder().append(yy).append("-").append(month).append("-").append(day));
                } else {
                    Utils.showMessageDialog(AddProjectActivity.this, getResources().getString(R.string.alert), getString(R.string.before_date));
                }
            } else {
                edtTaskStartDate.setText(new StringBuilder().append(yy).append("-").append(month).append("-").append(day));
            }

            reminderHours();
        }

    };

    DatePickerDialog.OnDateSetListener taskDueDatePicker = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int yy, int mm,
                              int dd) {
            // TODO Auto-generated method stub

            String day = String.valueOf(dd);
            String month = String.valueOf(mm + 1);
            if (day.trim().length() == 1)
                day = "0" + day;
            if (month.trim().length() == 1)
                month = "0" + month;
            String to = yy + "-" + month + "-" + day;
            String from = edtTaskStartDate.getText().toString();
            boolean flag = CheckDates(from, to);

            if (flag == true) {
                edtTaskDueDate.setText(new StringBuilder().append(yy).append("-").append(month).append("-").append(day));
            } else {
                Utils.showMessageDialog(AddProjectActivity.this, getResources().getString(R.string.alert), getString(R.string.after_date));
            }

            reminderHours();

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

    //TODO: animation code for screen,............

    private void animationOpen(Bundle savedInstanceState) {

        intent = getIntent();

        if (savedInstanceState == null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP &&
                intent.hasExtra(EXTRA_CIRCULAR_REVEAL_X) &&
                intent.hasExtra(EXTRA_CIRCULAR_REVEAL_Y)) {
            rootLayout.setVisibility(View.INVISIBLE);

            revealX = intent.getIntExtra(EXTRA_CIRCULAR_REVEAL_X, 0);
            revealY = intent.getIntExtra(EXTRA_CIRCULAR_REVEAL_Y, 0);


            ViewTreeObserver viewTreeObserver = rootLayout.getViewTreeObserver();
            if (viewTreeObserver.isAlive()) {
                viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        revealActivity(revealX, revealY);
                        rootLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                });
            }
        } else {
            rootLayout.setVisibility(View.VISIBLE);
        }
    }

    protected void revealActivity(int x, int y) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            float finalRadius = (float) (Math.max(rootLayout.getWidth(), rootLayout.getHeight()) * 1.1);

            // create the animator for this view (the start radius is zero)
            Animator circularReveal = ViewAnimationUtils.createCircularReveal(rootLayout, x, y, 0, finalRadius);
            circularReveal.setDuration(400);
            circularReveal.setInterpolator(new AccelerateInterpolator());

            // make the view visible and start the animation
            rootLayout.setVisibility(View.VISIBLE);
            circularReveal.start();
        } else {
            finish();
        }
    }

    protected void unRevealActivity() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            finish();
        } else {
            float finalRadius = (float) (Math.max(rootLayout.getWidth(), rootLayout.getHeight()) * 1.1);
            Animator circularReveal = ViewAnimationUtils.createCircularReveal(
                    rootLayout, revealX, revealY, finalRadius, 0);

            circularReveal.setDuration(400);
            circularReveal.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    rootLayout.setVisibility(View.INVISIBLE);
                    finish();
                }
            });


            circularReveal.start();
        }
    }

    @SuppressLint("ParcelCreator")
    class AddressResultReceiver extends ResultReceiver {
        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, final Bundle resultData) {
            if (resultCode == Utils.SUCCESS_RESULT) {
                final Address address = resultData.getParcelable(Utils.RESULT_ADDRESS);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        pdialog.dismiss();
                        latitude = address.getLatitude();
                        longitude = address.getLongitude();
                        flagOnClick = true;
//                        if (latitude != 0 && longitude != 0) {
                        taskDataArray();
                        addTask();
//                        } else {
//                            showMessage("Please check your location");
//                        }
                    }
                });
            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        pdialog.dismiss();
                        flagOnClick = true;
//                        if (latitude != 0 && longitude != 0) {
                        GPSTracker gpsTracker = Utils.getCurrentLocation(AddProjectActivity.this);
                        if (gpsTracker != null) {
                            latitude = gpsTracker.getLatitude();
                            longitude = gpsTracker.getLongitude();
                        } else {
                            latitude = 0;
                            longitude = 0;
                        }

                        taskDataArray();
                        addTask();
//                        } else {
//                            showMessage(resultData.getString(Utils.RESULT_DATA_KEY));
//                        }
                    }
                });
            }
        }
    }

    public boolean fieldValidation() {
        boolean flag = true;
        if (!Utils.validateString(edtTaskTitle.getText().toString())) {
            flag = false;
            edtTaskTitle.requestFocus();
            showMessage(getString(R.string.enter_project_title));
        } else if (!Utils.validateString(edtTaskDescription.getText().toString())) {
            flag = false;
            edtTaskDescription.requestFocus();
            showMessage(getString(R.string.enter_project_description));
        } else if (!Utils.validateString(edtTaskStartDate.getText().toString())) {
            flag = false;
            edtTaskStartDate.requestFocus();
            showMessage(getString(R.string.select_start_date));
        } else if (!Utils.validateString(edtTaskStartTime.getText().toString())) {
            flag = false;
            edtTaskStartTime.requestFocus();
            showMessage(getString(R.string.select_start_time));
        } else if (!Utils.validateString(edtTaskDueDate.getText().toString())) {
            flag = false;
            edtTaskDueDate.requestFocus();
            showMessage(getString(R.string.select_due_date));
        } else if (!Utils.validateString(edtTaskDueTime.getText().toString())) {
            flag = false;
            edtTaskDueTime.requestFocus();
            showMessage(getString(R.string.select_due_time));
        } else if (!Utils.validateString(priorityStr)) {
            flag = false;
            showMessage(getString(R.string.select_priority));
        } else if (!Utils.validateString(edtTaskStreet.getText().toString())) {
            flag = false;
            edtTaskStreet.requestFocus();
            showMessage(getString(R.string.enter_project_street));
        } else if (!Utils.validateString(edtTaskCity.getText().toString())) {
            flag = false;
            edtTaskCity.requestFocus();
            showMessage(getString(R.string.enter_project_city));
        } else if (!Utils.validateString(edtTaskState.getText().toString())) {
            flag = false;
            edtTaskState.requestFocus();
            showMessage(getString(R.string.enter_project_state));
        } else if (!Utils.validateString(edtTaskCountry.getText().toString())) {
            flag = false;
            edtTaskCountry.requestFocus();
            showMessage(getString(R.string.enter_project_country));
        } else if (!Utils.validateString(edtTaskZipCode.getText().toString())) {
            flag = false;
            edtTaskZipCode.requestFocus();
            showMessage(getString(R.string.enter_project_zipcode));
        } else if (edtTaskZipCode.getText().toString().trim().length() < 3) {
            flag = false;
            edtTaskZipCode.requestFocus();
            showMessage(getString(R.string.zipcode_length));
        }
        return flag;
    }

    public boolean dateTimeValidation() {
        boolean flag = true;
        if (!Utils.validateString(edtTaskStartDate.getText().toString())) {
            flag = false;
        } else if (!Utils.validateString(edtTaskStartTime.getText().toString())) {
            flag = false;
        } else if (!Utils.validateString(edtTaskDueDate.getText().toString())) {
            flag = false;
        } else if (!Utils.validateString(edtTaskDueTime.getText().toString())) {
            flag = false;
        }
        return flag;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (pdialog != null && pdialog.isShowing()) {
            pdialog.dismiss();
            flagOnClick = true;
        } else {
            unRevealActivity();
        }

    }

    private void showMessage(String message) {
        Utils.showResponseMessage(this, rootLayout, message);
    }
}

