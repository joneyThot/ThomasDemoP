package com.rogi.Activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
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


public class EditProjectActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String TAG = "EditProjectActivity";

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
    //    String titleStr = "", descriptionStr = "", startTimeStr = "", startDateStr = "", dueTimeStr = "",
//            dueDateStr = "", priorityStr = "", streetStr = "", citystr = "", stateStr = "", countryStr = "",
//            zipCodeStr = "", reminderHoursStr = "", TOKEN = "", USERID = "", addrs = "";
    String TOKEN = "", USERID = "", projectId = "", projectName = "", projectStatus = "", projectdescription = "",
            project_start_time = "", projectStartDate = "", projectstartTime = "", projectDueDate = "", projectDueTime = "",
            projectPriority = "", street = "", city = "", state = "", country = "", pincode = "", /*latitude = "", longitude = "",*/
            assignByName = "", assignByPhone = "", assignByEmail = "", signature = "", remind_hours = "", isSync = "", projectUpdateStatus = "",
            createdDate = "", addrs = "";

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
    }

    public void init() {
        rootLayout = findViewById(R.id.root_layout);

        titleText = (TextView) findViewById(R.id.titleText);
        titleText.setText("Edit Project");

        createTaskBTN = (TextView) findViewById(R.id.createTaskBTN);
        createTaskBTN.setText("Update Project");

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
        findViewById(R.id.backLayoutclick).setOnClickListener(this);

        myCalendar = Calendar.getInstance();

        edtTaskTitle.setTypeface(Utils.getTypeFace(EditProjectActivity.this));
        edtTaskDescription.setTypeface(Utils.getTypeFace(EditProjectActivity.this));
        edtTaskStartDate.setTypeface(Utils.getTypeFace(EditProjectActivity.this));
        edtTaskStartTime.setTypeface(Utils.getTypeFace(EditProjectActivity.this));
        edtTaskDueDate.setTypeface(Utils.getTypeFace(EditProjectActivity.this));
        edtTaskDueTime.setTypeface(Utils.getTypeFace(EditProjectActivity.this));
        edtTaskStreet.setTypeface(Utils.getTypeFace(EditProjectActivity.this));
        edtTaskCity.setTypeface(Utils.getTypeFace(EditProjectActivity.this));
        edtTaskState.setTypeface(Utils.getTypeFace(EditProjectActivity.this));
        edtTaskCountry.setTypeface(Utils.getTypeFace(EditProjectActivity.this));
        edtTaskZipCode.setTypeface(Utils.getTypeFace(EditProjectActivity.this));
        edtReminderTime.setTypeface(Utils.getTypeFace(EditProjectActivity.this));

        dfDate = new SimpleDateFormat("yyyy-MM-dd");
        mResultReceiver = new AddressResultReceiver(null);

        mDbHelper = new DBHelper(this);

        String taskData = mSharedPreferences.getString(Utils.TASK_OBJECT, "");

        try {
            JSONObject taskObject = new JSONObject(taskData);
            Logger.e(TAG, "projectObject :::" + taskObject);

            //Project Required Details
            projectId = taskObject.getString("id");
            projectName = taskObject.getString("title");
            projectStatus = taskObject.getString("project_status");
            projectdescription = taskObject.getString("description");
            project_start_time = taskObject.getString("project_start_time");
            projectStartDate = taskObject.getString("start_date");
            projectstartTime = taskObject.getString("start_time");
            projectDueDate = taskObject.getString("due_date");
            projectDueTime = taskObject.getString("due_time");
            projectPriority = taskObject.getString("priority");
            street = taskObject.getString("street");
            city = taskObject.getString("city");
            state = taskObject.getString("state");
            country = taskObject.getString("country");
            pincode = taskObject.getString("pincode");
            assignByName = taskObject.getString("assigned_by");
            assignByPhone = taskObject.getString("assigned_by_phone");
            assignByEmail = taskObject.getString("assigned_by_email");
            remind_hours = taskObject.getString("remind_hours");
            signature = taskObject.getString("signature_image");
            isSync = taskObject.getString("is_sync");
            projectUpdateStatus = taskObject.getString("project_operation");
            createdDate = taskObject.getString("created_date");
            latitude = Double.parseDouble(taskObject.getString("lattitude"));
            longitude = Double.parseDouble(taskObject.getString("longitude"));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (Utils.validateString(projectName))
            edtTaskTitle.setText(projectName);

        if (Utils.validateString(projectdescription))
            edtTaskDescription.setText(projectdescription);

        if (Utils.validateString(projectStartDate))
            edtTaskStartDate.setText(projectStartDate);

        if (Utils.validateString(projectstartTime))
            edtTaskStartTime.setText(projectstartTime.substring(0, 2) + ":" + projectstartTime.substring(3, 5));

        if (Utils.validateString(projectDueDate))
            edtTaskDueDate.setText(projectDueDate);

        if (Utils.validateString(projectDueTime))
            edtTaskDueTime.setText(projectDueTime.substring(0, 2) + ":" + projectDueTime.substring(3, 5));

        if (Utils.validateString(projectPriority)) {
            if (projectPriority.equalsIgnoreCase("Normal")) {
                imgNormal.setImageResource(R.mipmap.radio_btn_active);
                imgHigh.setImageResource(R.mipmap.radio_btn_red);
            } else {
                imgNormal.setImageResource(R.mipmap.radio_btn_red);
                imgHigh.setImageResource(R.mipmap.radio_btn_active);
            }
        }

        if (Utils.validateString(street))
            edtTaskStreet.setText(street);

        if (Utils.validateString(city))
            edtTaskCity.setText(city);

        if (Utils.validateString(state))
            edtTaskState.setText(state);

        if (Utils.validateString(country))
            edtTaskCountry.setText(country);

        if (Utils.validateString(pincode))
            edtTaskZipCode.setText(pincode);

        if (Utils.validateString(remind_hours)) {
            edtReminderTime.setText(remind_hours + " Hours");
        }

        reminderHours();
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

                DatePickerDialog dpdFrom = new DatePickerDialog(EditProjectActivity.this, R.style.DialogTheme, taskStartDatePicker, year, month, day);
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

                    DatePickerDialog dpdTo = new DatePickerDialog(EditProjectActivity.this, R.style.DialogTheme, taskDueDatePicker, year1, month1, day1);
                    long currentTime = System.currentTimeMillis() - 1000;
                    dpdTo.getDatePicker().setMinDate(currentTime);
                    dpdTo.show();
                } else {
                    Utils.showMessageDialog(EditProjectActivity.this, getResources().getString(R.string.alert), getString(R.string.start_date));
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

                    TimePickerDialog mTaskStartTimePicker = new TimePickerDialog(EditProjectActivity.this, R.style.DialogTheme, new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                            String h = Utils.utilTime(selectedHour);
                            String m = Utils.utilTime(selectedMinute);
                            edtTaskStartTime.setText(h + ":" + m);
                            if (edtTaskStartTime.getText().toString().equalsIgnoreCase(edtTaskDueTime.getText().toString())) {
                                Utils.showMessageDialog(EditProjectActivity.this, getResources().getString(R.string.alert), getString(R.string.same_time));
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

                    TimePickerDialog mTaskStartTimePicker = new TimePickerDialog(EditProjectActivity.this, R.style.DialogTheme, new TimePickerDialog.OnTimeSetListener() {
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
                        TimePickerDialog mTaskDueTimePicker = new TimePickerDialog(EditProjectActivity.this, R.style.DialogTheme, new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                                String h = Utils.utilTime(selectedHour);
                                String m = Utils.utilTime(selectedMinute);
                                edtTaskDueTime.setText(h + ":" + m);
                                if (edtTaskStartTime.getText().toString().equalsIgnoreCase(edtTaskDueTime.getText().toString())) {
                                    Utils.showMessageDialog(EditProjectActivity.this, getResources().getString(R.string.alert), getString(R.string.same_time));
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
                        TimePickerDialog mTaskDueTimePicker = new TimePickerDialog(EditProjectActivity.this, R.style.DialogTheme, new TimePickerDialog.OnTimeSetListener() {
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
                    Utils.showMessageDialog(EditProjectActivity.this, getResources().getString(R.string.alert), getString(R.string.due_time));
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

                            pdialog = new ProgressDialog(EditProjectActivity.this);
                            pdialog.setMessage(getString(R.string.please_wait));
                            pdialog.setCanceledOnTouchOutside(false);
                            pdialog.show();
                        } else {
                            try {
                                try {
                                    mDbHelper.openDataBase();
                                    mDbHelper.updateProjectDetails(projectId, projectName, projectdescription,
                                            projectStartDate, projectstartTime, projectDueDate, projectDueTime,
                                            projectPriority, projectStatus, street, city, state, country, pincode,
                                            String.valueOf(latitude), String.valueOf(longitude), assignByName, assignByPhone, assignByEmail,
                                            isSync, signature, projectUpdateStatus, project_start_time, createdDate,
                                            remind_hours);

                                    mDbHelper.close();
                                } catch (SQLException e) {
                                    e.printStackTrace();

                                }

                                projectObj = new JSONObject(mSharedPreferences.getString(Utils.TASK_OBJECT, ""));
                                if (projectObj.getString("project_operation").equalsIgnoreCase("A") &&
                                        projectObj.getString("is_sync").equalsIgnoreCase("N")) {
                                    projectObj.put("is_sync", "N");
                                    projectObj.put("project_operation", "A");
                                } else {
                                    projectObj.put("is_sync", "N");
                                    projectObj.put("project_operation", "U");
                                }

                                projectObj.put("id", projectId);
                                projectObj.put("title", projectName);
                                projectObj.put("description", projectdescription);
                                projectObj.put("project_start_time", project_start_time);
                                projectObj.put("start_date", projectStartDate);
                                projectObj.put("start_time", projectstartTime);
                                projectObj.put("due_date", projectDueDate);
                                projectObj.put("due_time", projectDueTime);
                                projectObj.put("priority", projectPriority);
                                projectObj.put("project_status", projectStatus);
                                projectObj.put("street", street);
                                projectObj.put("city", city);
                                projectObj.put("state", state);
                                projectObj.put("country", country);
                                projectObj.put("pincode", pincode);
                                projectObj.put("lattitude", latitude);
                                projectObj.put("longitude", longitude);
                                projectObj.put("signature_image", signature);
                                projectObj.put("assigned_by", assignByName);
                                projectObj.put("assigned_by_phone", assignByPhone);
                                projectObj.put("assigned_by_email", assignByEmail);
                                projectObj.put("created_date", createdDate);
                                projectObj.put("remind_hours", remind_hours);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                            if (projectObj != null) {
                                Intent returnIntent = new Intent();
                                returnIntent.putExtra(Utils.SWITCH, 3);
                                returnIntent.putExtra(Utils.TASK_OBJECT, projectObj.toString());
                                setResult(Activity.RESULT_OK, returnIntent);
                                finish();
                            } else {
                                finish();
                            }
                        }
                    }
                }
                break;

            case R.id.linNormal:
                imgNormal.setImageResource(R.mipmap.radio_btn_active);
                imgHigh.setImageResource(R.mipmap.radio_btn_red);
                projectPriority = "Normal";
                break;

            case R.id.linHigh:
                imgNormal.setImageResource(R.mipmap.radio_btn_red);
                imgHigh.setImageResource(R.mipmap.radio_btn_active);
                projectPriority = "High";
                break;

            case R.id.backLayoutclick:
                finish();
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
        projectName = edtTaskTitle.getText().toString().trim();
        projectdescription = edtTaskDescription.getText().toString().trim();
        projectStartDate = edtTaskStartDate.getText().toString().trim();
        projectstartTime = edtTaskStartTime.getText().toString().trim() + ":00";
        projectDueDate = edtTaskDueDate.getText().toString().trim();
        projectDueTime = edtTaskDueTime.getText().toString().trim() + ":00";
        street = edtTaskStreet.getText().toString().trim();
        city = edtTaskCity.getText().toString().trim();
        state = edtTaskState.getText().toString().trim();
        country = edtTaskCountry.getText().toString().trim();
        pincode = edtTaskZipCode.getText().toString().trim();
        if (Utils.validateString(edtReminderTime.getText().toString().trim())) {
            String number = edtReminderTime.getText().toString().trim().replaceAll("[^0-9]", "");
            remind_hours = number;
        } else {
            remind_hours = "";
        }
        addrs = street + " " + city + " " + state + " " + country + " " + pincode;
    }


    JSONArray projectArray;

    public void taskDataArray() {
        try {
            projectArray = new JSONArray();
            JSONObject object = new JSONObject();
            object.put("project_id", projectId);
            object.put("title", projectName);
            object.put("description", projectdescription);
            object.put("start_date", projectStartDate);
            object.put("start_time", projectstartTime);
            object.put("due_date", projectDueDate);
            object.put("due_time", projectDueTime);
            object.put("priority", projectPriority);
            object.put("street", street);
            object.put("city", city);
            object.put("state", state);
            object.put("country", country);
            object.put("pincode", pincode);
            object.put("lattitude", latitude);
            object.put("longitude", longitude);
            object.put("remind_hours", remind_hours);
            projectArray.put(object);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    JSONObject projectObj;

    public void updateProject() {
        taskDataArray();

        pdialog = new ProgressDialog(EditProjectActivity.this);
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
        Logger.e(TAG, "Update Project PARA---->" + String.valueOf(params));
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.POST, Utils.MAIN_URL + Utils.PROJECT_UPDATE_API, params, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Logger.e(TAG, "Update Project RES---->" + String.valueOf(response));
                        try {
                            boolean success = response.getBoolean("success");
                            String msg = response.getString("message");
                            flagOnClick = true;
                            boolean Auth = response.getBoolean("authenticated");
                            if (Auth) {
                                if (success) {
                                    pdialog.dismiss();
                                    if (!response.isNull("data")) {
                                        Utils.projectRefreshFlag = true;
                                        JSONArray jsonArray = response.getJSONArray("data");
                                        projectObj = jsonArray.getJSONObject(0);
                                        if (projectObj != null) {
                                            Intent returnIntent = new Intent();
                                            returnIntent.putExtra(Utils.SWITCH, 3);
                                            returnIntent.putExtra(Utils.TASK_OBJECT, projectObj.toString());
                                            setResult(Activity.RESULT_OK, returnIntent);
                                            finish();
                                        } else {
                                            finish();
                                        }
                                    } else {
                                        pdialog.dismiss();
                                        showMessage(msg);
                                    }

                                } else {
                                    pdialog.dismiss();
                                    showMessage(msg);
                                }
                            } else {
                                pdialog.dismiss();
                                NavigationDrawerActivity.clearPrefData();
                                EditProjectActivity.this.deleteDatabase(DBConstants.DB_NAME);
                                File dir = new File(Environment.getExternalStorageDirectory() + "/Media");
                                Utils.deleteDirectory(dir);
                                startActivity(new Intent(EditProjectActivity.this, LoginActivity.class));
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
                    Utils.showMessageDialog(EditProjectActivity.this, getResources().getString(R.string.alert), getString(R.string.before_date));
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
                Utils.showMessageDialog(EditProjectActivity.this, getResources().getString(R.string.alert), getString(R.string.after_date));
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
                        updateProject();
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
                        GPSTracker gpsTracker = Utils.getCurrentLocation(EditProjectActivity.this);
                        if (gpsTracker != null) {
                            latitude = gpsTracker.getLatitude();
                            longitude = gpsTracker.getLongitude();
                        } else {
                            latitude = 0;
                            longitude = 0;
                        }

                        taskDataArray();
                        updateProject();
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
        } else if (!Utils.validateString(projectPriority)) {
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
        /*if (pdialog != null && pdialog.isShowing()) {
            pdialog.dismiss();
            flagOnClick = true;
        } else {
            unRevealActivity();
        }*/

    }

    private void showMessage(String message) {
        Utils.showResponseMessage(this, rootLayout, message);
    }
}

