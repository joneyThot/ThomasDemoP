package com.rogi.Activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.os.StrictMode;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.rogi.Adapter.AdditionalContactListAdapter;
import com.rogi.Adapter.CropingOption;
import com.rogi.Adapter.CropingOptionAdapter;
import com.rogi.Adapter.MediaAdapter;
import com.rogi.Adapter.NoteListAdapter;
import com.rogi.Adapter.ProjectDetailAdapter;
import com.rogi.Adapter.ProjectPhaseListAdapter;
import com.rogi.Audio.AndroidAudioRecorder;
import com.rogi.Audio.AudioChannel;
import com.rogi.Audio.AudioSampleRate;
import com.rogi.Audio.AudioSource;
import com.rogi.Crop.CropImage;
import com.rogi.Crop.CropImageView;
import com.rogi.Database.DBHelper;
import com.rogi.FilePicker.MaterialFilePicker;
import com.rogi.FilePicker.ui.FilePickerActivity;
import com.rogi.Model.AdditionalContactModel;
import com.rogi.Model.MediaModel;
import com.rogi.Model.NotesModel;
import com.rogi.Model.ProjectPhaseModel;
import com.rogi.Model.TaskModel;
import com.rogi.R;
import com.rogi.Service.AsyncCallListener;
import com.rogi.Service.DounloadImageRequestTask;
import com.rogi.Service.DounloadThumbImageRequestTask;
import com.rogi.Service.GPSTracker;
import com.rogi.Service.RunTimePermission;
import com.rogi.View.Upload_Image;
import com.rogi.View.Utils;
import com.rogi.logger.Logger;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.simplify.ink.InkView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;


public class ProjectDetailsActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private SharedPreferences mSharedPreferences;
    public static final String TAG = "ProjectDetailsActivity";
    private static AlertDialog.Builder builder;
    private final int REQUEST_CODE_ASK_PERMISSIONS = 123;
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;
    public static final int PERMISSIONS_REQUEST_CODE = 0;
    public static final int FILE_PICKER_REQUEST_CODE = 1;
    private static final int GALLERY_CODE = 201, CROPING_CODE = 301, REQUEST_RECORD_AUDIO = 401, PICK_FILE_REQUEST = 501;
    final static int REQUEST_LOCATION = 199;
    private AlertDialog dialog;
    RequestQueue requestQueue;
    ProgressDialog pdialog;
    String TOKEN, USERID;

    LinearLayout rootLayout, linCamera, linGallery;
    TextView titleText, txtRogiUrl;
    RecyclerView recyclerView;

    private List<TaskModel> taskModel;
    final static private int TASK_WEBSITE_VIEW = 14;
    final static private int TASK_TITLE_VIEW = 0;
    final static private int TASK_CONTACT_VIEW = 1;
    final static private int TASK_TIMING_VIEW = 2;
    final static private int TASK_ADDRESS_VIEW = 3;
    final static private int TASK_ADDITIONAL_CONTACT_TITLE_VIEW = 4;
    final static private int TASK_ADDITIONAL_CONTACT_VIEW = 5;
    final static private int TASK_NOTE_TITLE_VIEW = 6;
    final static private int TASK_NOTE_VIEW = 7;
    final static private int TASK_PROJECT_PHASE_TITLE_VIEW = 8;
    final static private int TASK_PROJECT_PHASE_VIEW = 9;
    final static private int TASK_MEDIA_TITLE_VIEW = 10;
    final static private int TASK_MEDIA_VIEW = 11;
    final static private int TASK_STATUS_BTN_VIEW = 12;
    final static private int TASK_GENERATE_REPORT_BTN_VIEW = 13;

    ProjectDetailAdapter adapter;

    String project_phase_id = "", project_des = "", selectedImagePath = "", fileMediaType = "";
    boolean projectPhaseFlag = true, projectShareFlag = true, FlagGallery = false;
    int hwidth, hHeight;
    private Uri mCropImageUri;
    private RunTimePermission runTimePermission;
    ImageView shareProfileView, editProfileView;

    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    String street = "", city = "", state = "", country = "", pincode = "", currentDate = "", signatureImage = "",
            isTablet = "";
    private DBHelper mDbHelper;
    StringBuffer mediaIDs;
    Calendar myCalendar;
    SimpleDateFormat dfDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_details);
        requestQueue = Volley.newRequestQueue(this);
        mSharedPreferences = getSharedPreferences(Utils.PREF_NAME, MODE_PRIVATE);
        String taskData = mSharedPreferences.getString(Utils.TASK_OBJECT, "");
        USERID = mSharedPreferences.getString(Utils.USER_ID, "");
        TOKEN = mSharedPreferences.getString(Utils.TOKEN, "");

        int currentapiVersion = Build.VERSION.SDK_INT;
        if (currentapiVersion > Build.VERSION_CODES.LOLLIPOP) {
            // Do something for lollipop and above versions
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if ((this.checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) ||
                        (this.checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {

                    requestPermissions(new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION,
                                    android.Manifest.permission.ACCESS_FINE_LOCATION},
                            REQUEST_CODE_ASK_PERMISSIONS);
                }
            }
        }

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        myCalendar = Calendar.getInstance();
        dfDate = new SimpleDateFormat("yyyy-MM-dd");

        mDbHelper = new DBHelper(this);
        isTablet = Utils.isTablet(ProjectDetailsActivity.this);
        init();
        taskModel = new ArrayList<>();

        adapter = new ProjectDetailAdapter(taskModel, ProjectDetailsActivity.this, updateDueDateTimeClick, addNoteClick, editNoteClick, deleteNoteClick, addProjectClick,
                addAdditionalContactClick, editContactClick, deleteContactClick, onEditProjectPhaseListner, onDeleteProjectPhaseListner,
                addMediaClick, mediaItemClick, mediaItemLongClick, updateStatusClick, generateReportClick, phoneClick, isTablet);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(adapter);

        try {
            JSONObject taskObject = new JSONObject(taskData);
            Logger.e(TAG, "projectObject :::" + taskObject);
            setTaskData(taskObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        titleText.setText("Project Details");
    }

    public void init() {
        rootLayout = (LinearLayout) findViewById(R.id.rootLayout);
        titleText = (TextView) findViewById(R.id.titleText);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        shareProfileView = (ImageView) findViewById(R.id.shareProfileView);
        shareProfileView.setVisibility(View.VISIBLE);

        editProfileView = (ImageView) findViewById(R.id.editProfileView);
        editProfileView.setVisibility(View.VISIBLE);

//        txtRogiUrl = (TextView) findViewById(R.id.txtRogiUrl);

        findViewById(R.id.backLayoutclick).setOnClickListener(this);
        shareProfileView.setOnClickListener(this);
        editProfileView.setOnClickListener(this);

        Display display = getWindowManager().getDefaultDisplay();
        hwidth = display.getWidth();  // deprecated
        hHeight = display.getHeight();

        mediaIDs = new StringBuffer();
        mediaIDs.append("");

        /*if (Utils.validateString(Utils.WEB_SITE_URL)) {
            String html = "<a href=\"" + Utils.WEB_SITE_URL + "\">myrogi.com</a>";
            Spanned result;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                result = Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY);
            } else {
                result = Html.fromHtml(html);
            }
            txtRogiUrl.setText(result);
            txtRogiUrl.setMovementMethod(LinkMovementMethod.getInstance());
        }*/
    }

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
            String from = projectStartDate;
            boolean flag = CheckDates(from, to);

            if (flag == true) {
                edtTaskDueDate.setText(new StringBuilder().append(yy).append("-").append(month).append("-").append(day));
            } else {
                Utils.showMessageDialog(ProjectDetailsActivity.this, getResources().getString(R.string.alert), getString(R.string.after_date));
            }
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

    JSONArray noteArray, dueProjectDateAndTimeArray;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.resetBTN:
                edtNotes.setText("");
                dialog.dismiss();
                break;

            case R.id.edtTaskDueDate:
                if (!projectStartDate.equalsIgnoreCase("") && !projectStartDate.equalsIgnoreCase(null)) {
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

                    DatePickerDialog dpdTo = new DatePickerDialog(ProjectDetailsActivity.this, R.style.DialogTheme, taskDueDatePicker, year1, month1, day1);
                    long currentTime = System.currentTimeMillis() - 1000;
                    dpdTo.getDatePicker().setMinDate(currentTime);
                    dpdTo.show();
                } else {
                    Utils.showMessageDialog(ProjectDetailsActivity.this, getResources().getString(R.string.alert), getString(R.string.start_date));
                }
                break;

            case R.id.edtTaskDueTime:

                if (!projectstartTime.equalsIgnoreCase("") && !projectstartTime.equalsIgnoreCase(null)) {
                    if (projectstartTime.equalsIgnoreCase(edtTaskDueDate.getText().toString())) {
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
                        TimePickerDialog mTaskDueTimePicker = new TimePickerDialog(ProjectDetailsActivity.this, R.style.DialogTheme, new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                                String h = Utils.utilTime(selectedHour);
                                String m = Utils.utilTime(selectedMinute);
                                edtTaskDueTime.setText(h + ":" + m);
                                if (projectstartTime.equalsIgnoreCase(edtTaskDueTime.getText().toString())) {
                                    Utils.showMessageDialog(ProjectDetailsActivity.this, getResources().getString(R.string.alert), getString(R.string.same_time));
                                    edtTaskDueTime.setText("");
                                }
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
                        TimePickerDialog mTaskDueTimePicker = new TimePickerDialog(ProjectDetailsActivity.this, R.style.DialogTheme, new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                                String h = Utils.utilTime(selectedHour);
                                String m = Utils.utilTime(selectedMinute);
                                edtTaskDueTime.setText(h + ":" + m);
                            }
                        }, taskDueHour, taskDueMinute, false);//Yes 24 hour time
                        mTaskDueTimePicker.show();
                    }
                } else {
                    Utils.showMessageDialog(ProjectDetailsActivity.this, getResources().getString(R.string.alert), getString(R.string.due_time));
                }
                break;

            case R.id.SaveBTN:
                if (fieldValidation()) {
                    String dueDate = edtTaskDueDate.getText().toString().trim();
                    String dueTime = edtTaskDueTime.getText().toString().trim() + ":00";
                    try {
                        if (Utils.checkInternetConnection(this)) {
                            dueProjectDateAndTimeArray = new JSONArray();
                            JSONObject dueObj = new JSONObject();
                            dueObj.put("project_id", ProjectID);
                            dueObj.put("due_date", dueDate);
                            dueObj.put("due_time", dueTime);
                            dueProjectDateAndTimeArray.put(dueObj);
                            UpdateDueDateTime(dueProjectDateAndTimeArray);
                        } else {
                            JSONObject object = new JSONObject(mSharedPreferences.getString(Utils.TASK_OBJECT, ""));
                            object.put("due_date", dueDate);
                            object.put("due_time", dueTime);
                            if (object.getString("project_operation").equalsIgnoreCase("A") &&
                                    object.getString("is_sync").equalsIgnoreCase("N")) {
                                object.put("is_sync", "N");
                                object.put("project_operation", "A");
                            } else {
                                object.put("is_sync", "N");
                                object.put("project_operation", "U");
                            }

                            try {
                                mDbHelper.openDataBase();
                                mDbHelper.updateProjectDetails(ProjectID, projectName,
                                        object.getString("description"), object.getString("start_date"),
                                        object.getString("start_time"), object.getString("due_date"),
                                        object.getString("due_time"), object.getString("priority"),
                                        object.getString("project_status"), object.getString("street"),
                                        object.getString("city"), object.getString("state"),
                                        object.getString("country"), object.getString("pincode"),
                                        object.getString("lattitude"), object.getString("longitude"),
                                        object.getString("assigned_by"), object.getString("assigned_by_phone"),
                                        object.getString("assigned_by_email"), object.getString("is_sync"),
                                        object.getString("signature_image"), object.getString("project_operation"),
                                        object.getString("project_start_time"), object.getString("created_date"),
                                        object.getString("remind_hours"));
                                mDbHelper.close();

                            } catch (SQLException e) {
                                e.printStackTrace();

                            }

                            taskModel.clear();
                            Utils.storeString(mSharedPreferences, Utils.TASK_OBJECT, object.toString());
                            setTaskData(object);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    dialog.dismiss();
                }
                break;

            case R.id.cancelBTN:
                edtTaskDueDate.setText("");
                edtTaskDueTime.setText("");
                break;

            case R.id.addNoteBTN:
                String Notes = edtNotes.getText().toString().trim();
                Utils.hideSoftKeyboard(this);
                if (Utils.validateString(Notes)) {
                    if (Utils.checkInternetConnection(this)) {
                        if (NOTE_FLAG == true) {
                            try {
                                noteArray = new JSONArray();
                                JSONObject noteObject = new JSONObject();
                                noteObject.put("project_id", ProjectID);
                                noteObject.put("note", Notes);
                                noteArray.put(noteObject);
                                addNotes(noteArray);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {
                            if (!ProjectID.equals("") && !noteID.equals("")) {
                                editNotes(Notes);
                            }
                        }
                    } else {
                        if (NOTE_FLAG == true) {
                            // Add Notes Offline
                            try {
                                ArrayList<NotesModel> notesModelArrayList = new ArrayList<>();
                                String created_date = Utils.getCurrentDateAndTime();
                                String tempId = Utils.getRandomAlphaNumeric(6);
                                try {
                                    mDbHelper.openDataBase();
                                    mDbHelper.insertProjectNotes(mDbHelper.myDataBase, tempId, ProjectID, Notes, created_date, "N", "A");
                                    notesModelArrayList = mDbHelper.getProjectNotesDetails();
                                    mDbHelper.close();
                                } catch (SQLException e) {
                                    e.printStackTrace();
                                }

                                if (notesModelArrayList.size() > 0) {
                                    JSONObject object = new JSONObject(mSharedPreferences.getString(Utils.TASK_OBJECT, ""));
                                    JSONArray notesArray = object.getJSONArray("project_notes");
                                    JSONArray notesNewArray = new JSONArray();
                                    JSONObject notesObject;
                                    Logger.e(TAG, "Notes Array before :" + notesArray);
                                    if (notesArray.length() > 0) {
                                        for (int i = 0; i < notesArray.length(); i++) {
                                            notesObject = notesArray.getJSONObject(i);
                                            notesNewArray.put(notesObject);
                                        }
                                    }

                                    for (int j = 0; j < notesModelArrayList.size(); j++) {
                                        if (tempId.equalsIgnoreCase(notesModelArrayList.get(j).getNoteId())) {
                                            notesObject = new JSONObject();
                                            notesObject.put("id", notesModelArrayList.get(j).getNoteId());
                                            notesObject.put("note", notesModelArrayList.get(j).getNote());
                                            notesObject.put("created_date", notesModelArrayList.get(j).getNote_created_date());
                                            notesObject.put("is_sync", notesModelArrayList.get(j).getIs_sync());
                                            notesObject.put("notes_operation", notesModelArrayList.get(j).getNote_status());
                                            notesNewArray.put(notesObject);
                                        }
                                    }

                                    Logger.e(TAG, "Notes Array after :" + notesNewArray);
                                    object.put("project_notes", notesNewArray);
                                    if (object.getString("project_operation").equalsIgnoreCase("A") &&
                                            object.getString("is_sync").equalsIgnoreCase("N")) {
                                        object.put("is_sync", "N");
                                        object.put("project_operation", "A");
                                    } else {
                                        object.put("is_sync", "N");
                                        object.put("project_operation", "U");
                                    }

                                    Logger.e(TAG, "Project Object :" + object);
                                    dialog.dismiss();
                                    taskModel.clear();

                                    Utils.storeString(mSharedPreferences, Utils.TASK_OBJECT, object.toString());
                                    setTaskData(object);
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            NOTE_FLAG = true;

                        } else {
                            // Update Notes Offline
                            if (!ProjectID.equals("") && !noteID.equals("")) {
                                try {
                                    ArrayList<NotesModel> notesModelArrayList = new ArrayList<>();
                                    String created_date = Utils.getCurrentDateAndTime();
                                    try {
                                        mDbHelper.openDataBase();
                                        if (notesStatus.equalsIgnoreCase("A") && noteSync.equalsIgnoreCase("N")) {
                                            mDbHelper.updateProjectNotes(noteID, ProjectID, Notes, created_date, "N", "A");
                                        } else {
                                            mDbHelper.updateProjectNotes(noteID, ProjectID, Notes, created_date, "N", "U");
                                        }
                                        notesModelArrayList = mDbHelper.getProjectNotesDetails();
                                        mDbHelper.close();
                                    } catch (SQLException e) {
                                        e.printStackTrace();
                                    }

                                    if (notesModelArrayList.size() > 0) {
                                        JSONObject object = new JSONObject(mSharedPreferences.getString(Utils.TASK_OBJECT, ""));
                                        JSONArray notesArray = object.getJSONArray("project_notes");
                                        Logger.e(TAG, "Notes Array update before :" + notesArray);
                                        if (notesArray.length() > 0) {
                                            for (int i = 0; i < notesArray.length(); i++) {
                                                if (noteID.equalsIgnoreCase(notesArray.getJSONObject(i).getString("id"))) {
                                                    for (int j = 0; j < notesModelArrayList.size(); j++) {
                                                        if (noteID.equalsIgnoreCase(notesModelArrayList.get(j).getNoteId())) {
                                                            notesArray.getJSONObject(i).put("id", notesModelArrayList.get(j).getNoteId());
                                                            notesArray.getJSONObject(i).put("note", notesModelArrayList.get(j).getNote());
                                                            notesArray.getJSONObject(i).put("created_date", notesModelArrayList.get(j).getNote_created_date());
                                                            notesArray.getJSONObject(i).put("is_sync", notesModelArrayList.get(j).getIs_sync());
                                                            notesArray.getJSONObject(i).put("notes_operation", notesModelArrayList.get(j).getNote_status());
                                                            break;
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                        Logger.e(TAG, "Notes Array update after :" + notesArray);
                                        object.put("project_notes", notesArray);
                                        if (object.getString("project_operation").equalsIgnoreCase("A") &&
                                                object.getString("is_sync").equalsIgnoreCase("N")) {
                                            object.put("is_sync", "N");
                                            object.put("project_operation", "A");
                                        } else {
                                            object.put("is_sync", "N");
                                            object.put("project_operation", "U");
                                        }

                                        Logger.e(TAG, "Project Object :" + object);
                                        dialog.dismiss();
                                        taskModel.clear();

                                        Utils.storeString(mSharedPreferences, Utils.TASK_OBJECT, object.toString());
                                        setTaskData(object);
                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                NOTE_FLAG = true;
                            }
                        }
                    }

                } else {
                    showMessage(getString(R.string.enter_notes));
                }
                break;
            case R.id.addProjectBTN:
                String projectDescription = edtProjects.getText().toString().trim();
                Utils.hideSoftKeyboard(this);
                if (Utils.validateString(projectDescription)) {
                    if (Utils.checkInternetConnection(this)) {
                        if (projectPhaseFlag == true) {
                            if (!ProjectID.equals("")) {
                                addProjectPhase(projectDescription);
                            }
                        } else {
                            if (!ProjectID.equals("") && !project_phase_id.equals("")) {
                                UpdateProjectPhase(projectDescription);
                            }
                        }

                    } else {
                        if (projectPhaseFlag == true) {
                            // Add Project Offline
                            try {
                                ArrayList<ProjectPhaseModel> phaseModelArrayList = new ArrayList<>();
                                String created_date = Utils.getCurrentDateAndTime();
                                String tempId = Utils.getRandomAlphaNumeric(6);
                                try {
                                    mDbHelper.openDataBase();
                                    mDbHelper.insertProjectPhase(mDbHelper.myDataBase, tempId, ProjectID, projectDescription, created_date, "N", "A");
                                    phaseModelArrayList = mDbHelper.getProjectPhaseDetails();
                                    mDbHelper.close();
                                } catch (SQLException e) {
                                    e.printStackTrace();
                                }

                                if (phaseModelArrayList.size() > 0) {
                                    JSONObject object = new JSONObject(mSharedPreferences.getString(Utils.TASK_OBJECT, ""));
                                    JSONArray phaseArray = object.getJSONArray("project_phase_data");
                                    JSONArray phaseNewArray = new JSONArray();
                                    JSONObject phaseObject;
                                    Logger.e(TAG, "Phase Array before :" + phaseArray);
                                    if (phaseArray.length() > 0) {
                                        for (int i = 0; i < phaseArray.length(); i++) {
                                            phaseObject = phaseArray.getJSONObject(i);
                                            phaseNewArray.put(phaseObject);
                                        }
                                    }

                                    for (int j = 0; j < phaseModelArrayList.size(); j++) {
                                        if (tempId.equalsIgnoreCase(phaseModelArrayList.get(j).getProjectPhaseId())) {
                                            phaseObject = new JSONObject();
                                            phaseObject.put("id", phaseModelArrayList.get(j).getProjectPhaseId());
                                            phaseObject.put("description", phaseModelArrayList.get(j).getProjectDesription());
                                            phaseObject.put("created_date", phaseModelArrayList.get(j).getProjectCreatedDate());
                                            phaseObject.put("is_sync", phaseModelArrayList.get(j).getIs_sync());
                                            phaseObject.put("project_phase_operation", phaseModelArrayList.get(j).getPhase_status());
                                            phaseNewArray.put(phaseObject);
                                        }
                                    }

                                    Logger.e(TAG, "Phase Array after :" + phaseNewArray);
                                    object.put("project_phase_data", phaseNewArray);
                                    if (object.getString("project_operation").equalsIgnoreCase("A") &&
                                            object.getString("is_sync").equalsIgnoreCase("N")) {
                                        object.put("is_sync", "N");
                                        object.put("project_operation", "A");
                                    } else {
                                        object.put("is_sync", "N");
                                        object.put("project_operation", "U");
                                    }

                                    Logger.e(TAG, "Project Object :" + object);
                                    dialog.dismiss();
                                    taskModel.clear();

                                    Utils.storeString(mSharedPreferences, Utils.TASK_OBJECT, object.toString());
                                    setTaskData(object);
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            projectPhaseFlag = true;
                        } else {
                            // Update Project Offline
                            if (!ProjectID.equals("") && !project_phase_id.equals("")) {
                                try {
                                    ArrayList<ProjectPhaseModel> phaseModelArrayList = new ArrayList<>();
                                    String created_date = Utils.getCurrentDateAndTime();
                                    try {
                                        mDbHelper.openDataBase();
                                        if (phaseStatus.equalsIgnoreCase("A") && phaseSync.equalsIgnoreCase("N")) {
                                            mDbHelper.updateProjectPhaseDetails(project_phase_id, ProjectID, projectDescription, created_date, "N", "A");
                                        } else {
                                            mDbHelper.updateProjectPhaseDetails(project_phase_id, ProjectID, projectDescription, created_date, "N", "U");
                                        }
                                        phaseModelArrayList = mDbHelper.getProjectPhaseDetails();
                                        mDbHelper.close();
                                    } catch (SQLException e) {
                                        e.printStackTrace();
                                    }

                                    if (phaseModelArrayList.size() > 0) {
                                        JSONObject object = new JSONObject(mSharedPreferences.getString(Utils.TASK_OBJECT, ""));
                                        JSONArray phaseArray = object.getJSONArray("project_phase_data");
                                        Logger.e(TAG, "Phase Array before :" + phaseArray);
                                        if (phaseArray.length() > 0) {
                                            for (int i = 0; i < phaseArray.length(); i++) {
                                                if (project_phase_id.equalsIgnoreCase(phaseArray.getJSONObject(i).getString("id"))) {
                                                    for (int j = 0; j < phaseModelArrayList.size(); j++) {
                                                        if (project_phase_id.equalsIgnoreCase(phaseModelArrayList.get(j).getProjectPhaseId())) {
                                                            phaseArray.getJSONObject(i).put("id", phaseModelArrayList.get(j).getProjectPhaseId());
                                                            phaseArray.getJSONObject(i).put("description", phaseModelArrayList.get(j).getProjectDesription());
                                                            phaseArray.getJSONObject(i).put("created_date", phaseModelArrayList.get(j).getProjectCreatedDate());
                                                            phaseArray.getJSONObject(i).put("is_sync", phaseModelArrayList.get(j).getIs_sync());
                                                            phaseArray.getJSONObject(i).put("project_phase_operation", phaseModelArrayList.get(j).getPhase_status());
                                                            break;
                                                        }
                                                    }
                                                }
                                            }

                                        }
                                        Logger.e(TAG, "Phase Array after : " + phaseArray);
                                        object.put("project_phase_data", phaseArray);
                                        if (object.getString("project_operation").equalsIgnoreCase("A") &&
                                                object.getString("is_sync").equalsIgnoreCase("N")) {
                                            object.put("is_sync", "N");
                                            object.put("project_operation", "A");
                                        } else {
                                            object.put("is_sync", "N");
                                            object.put("project_operation", "U");
                                        }

                                        Logger.e(TAG, "Project Object :" + object);
                                        dialog.dismiss();
                                        taskModel.clear();

                                        Utils.storeString(mSharedPreferences, Utils.TASK_OBJECT, object.toString());
                                        setTaskData(object);
                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                projectPhaseFlag = true;
                            }
                        }
                    }

                } else {
                    showMessage(getString(R.string.enter_project));
                }
                break;

            case R.id.resetProjectBTN:
                edtProjects.setText("");
                dialog.dismiss();
                break;

            case R.id.backLayoutclick:
                Utils.hideSoftKeyboard(this);
                finish();
                break;

            case R.id.editProfileView:
                adapter.notifyDataSetChanged();
                Intent intent = new Intent(ProjectDetailsActivity.this, EditProjectActivity.class);
                startActivityForResult(intent, 1);
                break;

            case R.id.shareProfileView:

                if (Utils.checkInternetConnection(ProjectDetailsActivity.this)) {
                    if (projectShareFlag) {
                        projectShareFlag = false;
                        shareGenerateReportLink();
                    }
                }

                /*String additionContact = "";
                for (int i = 0; i < _additionalContact.size(); i++) {
                    int count = i + 1;
                    String address = _additionalContact.get(i).getAdtnlContactAddress().replace("&$&", ", ");
                    String str = "<br>" + count + ".Name:&nbsp;&nbsp;" + _additionalContact.get(i).getAdtnlContactName() + "<br>"
                            + "&nbsp;&nbsp;&nbsp;Company:&nbsp;&nbsp;" + _additionalContact.get(i).getAdtnlContactCompany() + "<br>"
                            + "&nbsp;&nbsp;&nbsp;Address:&nbsp;&nbsp;" + address + "<br>"
                            + "&nbsp;&nbsp;&nbsp;Phone:&nbsp;&nbsp;" + _additionalContact.get(i).getAdtnlContactPhone() + "<br>"
                            + "&nbsp;&nbsp;&nbsp;Email:&nbsp;&nbsp;" + _additionalContact.get(i).getAdtnlContactEmail() + "<br>"
                            + "&nbsp;&nbsp;&nbsp;Responsibility:&nbsp;&nbsp;" + _additionalContact.get(i).getAdtnlContactResponsibility();

                    additionContact = additionContact + "<br>" + str;
                }

                String notes = "";
                for (int i = 0; i < _notes.size(); i++) {
                    int count = i + 1;
                    notes = notes + "<br>" + count + ". " + _notes.get(i).getNote();
                }

                String phase = "";
                for (int i = 0; i < _projects.size(); i++) {
                    int count = i + 1;
                    phase = phase + "<br>" + count + ". " + _projects.get(i).getProjectDesription();
                }

                String mediaUrl = "";
                int count1 = 0;
                if (mediaIDs != null && mediaIdsJArray != null && mediaIdsJArray.length() > 0) {
                    try {
                        for (int i = 0; i < mediaList.size(); i++) {
                            for (int j = 0; j < mediaIdsJArray.length(); j++) {
                                String id = mediaIdsJArray.get(j).toString();
                                Logger.e(TAG, "Selected value ::" + id);
                                if (mediaList.get(i).getMediaId().equalsIgnoreCase(id)) {
                                    count1 = count1 + 1;
                                    mediaUrl = mediaUrl + "<br>" + count1 + ". <u>" + "<a href=\"" + mediaList.get(i).getMedia() + "\">" + mediaList.get(i).getMedia() + "</a></u>";
                                }
                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    for (int i = 0; i < mediaList.size(); i++) {
                        int count = i + 1;
                        mediaUrl = mediaUrl + "<br>" + count + ". <u>" + "<a href=\"" + mediaList.get(i).getMedia() + "\">" + mediaList.get(i).getMedia() + "</a></u>";
                    }
                }

                String sendTxt = "<html><body>Project Name: " + projectName
                        + "<br>Status: " + projectStatus
                        + "<br>Priority: " + projectPriority
                        + "<br>Description: " + projectdescription
                        + "<br>Assigned By: " + assignByName
                        + "<br>Assigned By Email: " + assignByEmail
                        + "<br>Assigned By Phone: " + Utils.getPhoneNumberFormat(assignByPhone)
                        + "<br>Start Date: " + projectStartDate
                        + "<br>Start Time: " + projectstartTime
                        + "<br>Due Date: " + projectDueDate
                        + "<br>Due Time: " + projectDueTime
                        + "<br>Address: " + projectAddress
                        + "<br><br><b>Additional Contact(s)</b>" + additionContact + "<br>"
                        + "<br><br><b>Note(s)</b><br>" + notes
                        + "<br><br><b>Phase(s)</b><br>" + phase
                        + "<br><br><b>Media</b><br>" + mediaUrl
                        + "<br><br><br><br>Provided by ROGi</body></html>";*/

               /* Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.setType("text/plain");
                sendIntent.putExtra(Intent.EXTRA_TEXT, Html.fromHtml(sendTxt).toString());
                startActivity(sendIntent);

                mediaIdsJArray = new JSONArray();
                mediaIDs = new StringBuffer();

                if (Utils.checkInternetConnection(ProjectDetailsActivity.this)) {
                    ProjectDetails();
                }*/

                /*String additionContact = "";
                for (int i = 0; i < _additionalContact.size(); i++) {
                    int count = i + 1;
                    String address = _additionalContact.get(i).getAdtnlContactAddress().replace("&$&", ", ");
                    String str = "\n" + count + ".Name:  " + _additionalContact.get(i).getAdtnlContactName() + "\n"
                            + "   Company:  " + _additionalContact.get(i).getAdtnlContactCompany() + "\n"
                            + "   Address:  " + address + "\n"
                            + "   Phone:  " + _additionalContact.get(i).getAdtnlContactPhone() + "\n"
                            + "   Email:  " + _additionalContact.get(i).getAdtnlContactEmail() + "\n"
                            + "   Responsibility:  " + _additionalContact.get(i).getAdtnlContactResponsibility();

                    additionContact = additionContact + "\n" + str;
                }

                String notes = "";
                for (int i = 0; i < _notes.size(); i++) {
                    int count = i + 1;
                    notes = notes + "\n" + count + ". " + _notes.get(i).getNote();
                }

                String phase = "";
                for (int i = 0; i < _projects.size(); i++) {
                    int count = i + 1;
                    phase = phase + "\n" + count + ". " + _projects.get(i).getProjectDesription();
                }

                String mediaUrl = "";
                int count1 = 0;
                if (mediaIDs != null && mediaIdsJArray != null && mediaIdsJArray.length() > 0) {
                    try {
                        for (int i = 0; i < mediaList.size(); i++) {
                            for (int j = 0; j < mediaIdsJArray.length(); j++) {
                                String id = mediaIdsJArray.get(j).toString();
                                Logger.e(TAG, "Selected value ::" + id);
                                if (mediaList.get(i).getMediaId().equalsIgnoreCase(id)) {
                                    count1 = count1 + 1;
                                    mediaUrl = mediaUrl + "\n" + count1 + ". " + mediaList.get(i).getMedia();
                                }
                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    for (int i = 0; i < mediaList.size(); i++) {
                        int count = i + 1;
                        mediaUrl = mediaUrl + "\n" + count + ". " + mediaList.get(i).getMedia();
                    }
                }

                String sendTxt = "Project Name: " + projectName
                        + "\nStatus: " + projectStatus
                        + "\nPriority: " + projectPriority
                        + "\nDescription: " + projectdescription
                        + "\nAssigned By: " + assignByName
                        + "\nAssigned By Email: " + assignByEmail
                        + "\nAssigned By Phone: " + Utils.getPhoneNumberFormat(assignByPhone)
                        + "\nStart Date: " + projectStartDate
                        + "\nStart Time: " + projectstartTime
                        + "\nDue Date: " + projectDueDate
                        + "\nDue Time: " + projectDueTime
                        + "\nAddress: " + projectAddress
                        + "\n\nAdditional Contact(s)" + additionContact + "\n"
                        + "\n\nNote(s)\n" + notes
                        + "\n\nPhase(s)\n" + phase
                        + "\n\nMedia\n" + mediaUrl
                        + "\n\n\n\nProvided by ROGi";*/

               /* String additionContact = "";
                for (int i = 0; i < _additionalContact.size(); i++) {
                    int count = i + 1;
                    String address = _additionalContact.get(i).getAdtnlContactAddress().replace("&$&", ", ");
                    String str = "<tr><td>" + count + ".Name:</td><td>" + _additionalContact.get(i).getAdtnlContactName() + "</td></tr>"
                            + "<tr><td>&nbsp;&nbsp;&nbsp;Company:</td><td>" + _additionalContact.get(i).getAdtnlContactCompany() + "</td></tr>"
                            + "<tr><td>&nbsp;&nbsp;&nbsp;Address:</td><td>" + address + "</td></tr>"
                            + "<tr><td>&nbsp;&nbsp;&nbsp;Phone:</td><td>" + _additionalContact.get(i).getAdtnlContactPhone() + "</td></tr>"
                            + "<tr><td>&nbsp;&nbsp;&nbsp;Email:</td><td>" + _additionalContact.get(i).getAdtnlContactEmail() + "</td></tr>"
                            + "<tr><td>&nbsp;&nbsp;&nbsp;Responsibility:</td><td>" + _additionalContact.get(i).getAdtnlContactResponsibility() + "</td></tr>";

                    additionContact = additionContact + str;
                }

                String notes = "";
                for (int i = 0; i < _notes.size(); i++) {
                    int count = i + 1;
                    notes = notes + "<tr><td>" + count + ". " + _notes.get(i).getNote() + "</td></tr>";
                }

                String phase = "";
                for (int i = 0; i < _projects.size(); i++) {
                    int count = i + 1;
                    phase = phase + "<tr><td>" + count + ". " + _projects.get(i).getProjectDesription() + "</td></tr>";
                }

                String mediaUrl = "";
                int count1 = 0;
                if (mediaIDs != null && mediaIdsJArray != null && mediaIdsJArray.length() > 0) {
                    try {
                        for (int i = 0; i < mediaList.size(); i++) {
                            for (int j = 0; j < mediaIdsJArray.length(); j++) {
                                String id = mediaIdsJArray.get(j).toString();
                                Logger.e(TAG, "Selected value ::" + id);
                                if (mediaList.get(i).getMediaId().equalsIgnoreCase(id)) {
                                    count1 = count1 + 1;
                                    mediaUrl = mediaUrl + "<tr><td>" + count1 + ". <u>" + "<a href=\"" + mediaList.get(i).getMedia() + "\">" + mediaList.get(i).getMedia() + "</a></u></td></tr>";
                                }
                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    for (int i = 0; i < mediaList.size(); i++) {
                        int count = i + 1;
                        mediaUrl = mediaUrl + "<tr><td>" + count + ". <u>" + "<a href=\"" + mediaList.get(i).getMedia() + "\">" + mediaList.get(i).getMedia() + "</a></u></td></tr>";
                    }
                }

                String sendTxt = "<!DOCTYPE html><html><body><table><tr><td>Project Name:</td><td>" + projectName
                        + "</td></tr><tr><td>Status:</td><td>" + projectStatus
                        + "</td></tr><tr><td>Priority:</td><td>" + projectPriority
                        + "</td></tr><tr><td>Description:</td><td>" + projectdescription
                        + "</td></tr><tr><td>Assigned By:</td><td>" + assignByName
                        + "</td></tr><tr><td>Assigned By Email:</td><td>" + assignByEmail
                        + "</td></tr><tr><td>Assigned By Phone:</td><td>" + Utils.getPhoneNumberFormat(assignByPhone)
                        + "</td></tr><tr><td>Start Date:</td><td>" + projectStartDate
                        + "</td></tr><tr><td>Start Time:</td><td>" + projectstartTime
                        + "</td></tr><tr><td>Due Date:</td><td>" + projectDueDate
                        + "</td></tr><tr><td>Due Time:</td><td>" + projectDueTime
                        + "</td></tr><tr><td>Address:</td><td>" + projectAddress
                        + "</td></tr></table><br><table><tr><th>Additional Contact(s)</th></tr></table><br><table>" + additionContact + "</table><br>"
                        + "<table><tr><th>Note(s)</th></tr></table><br><table>" + notes + "</table><br>"
                        + "<table><tr><th>Phase(s)</th></tr></table><br><table>" + phase + "</table><br>"
                        + "<table><tr><th>Media</th></tr></table><br><table>" + mediaUrl + "</table><br>"
                        + "<br><br><br><br>Provided by ROGi</body></html>";

                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.setType("text/html");
                sendIntent.putExtra(Intent.EXTRA_TEXT, Html.fromHtml(sendTxt).toString());
                startActivity(sendIntent);

                mediaIdsJArray = new JSONArray();
                mediaIDs = new StringBuffer();

                if (Utils.checkInternetConnection(ProjectDetailsActivity.this)) {
                    ProjectDetails();
                }*/

                break;
        }
    }

    String projectName = "", projectStatus = "", projectdescription = "", projectStartDate = "", projectstartTime = "",
            projectDueDate = "", projectDueTime = "", projectPriority = "", projectAddress = "", assignByName = "",
            assignByEmail = "", assignByPhone = "";
    ArrayList<NotesModel> _notes;
    ArrayList<ProjectPhaseModel> _projects;
    ArrayList<AdditionalContactModel> _additionalContact;
    ArrayList<MediaModel> mediaList;

    public void setTaskData(JSONObject taskObject) {
        try {
            mDbHelper.openDataBase();
            ProjectID = taskObject.getString("id");
            projectName = taskObject.getString("title");
            projectStatus = taskObject.getString("project_status");

            String status = taskObject.getString("project_status");
            String signature = taskObject.getString("signature_image");
            String projectStartTime = taskObject.getString("project_start_time");


            if (status.equalsIgnoreCase(Utils.START) || status.equalsIgnoreCase(Utils.PENDING) ||
                    (status.equalsIgnoreCase(Utils.OPEN) && !Utils.validateString(projectStartTime))) {
                status = Utils.START_WORK;
                editProfileView.setVisibility(View.VISIBLE);
            } else if (status.equalsIgnoreCase(Utils.OPEN) && Utils.validateString(projectStartTime)) {
                status = Utils.COMPLETE;
                editProfileView.setVisibility(View.GONE);
            } else if (status.equals(Utils.REPORT) && Utils.validateString(signature)) {
                status = Utils.GENERATE_REPORT;
                editProfileView.setVisibility(View.GONE);
            }

            TaskModel taskWebsite = new TaskModel();
            taskWebsite.setViewType(TASK_WEBSITE_VIEW);
            taskModel.add(taskWebsite);

            TaskModel taskModelTitle = new TaskModel();
            taskModelTitle.setTitle(taskObject.getString("title"));
            taskModelTitle.setDescription(taskObject.getString("description"));
            taskModelTitle.setPriority(taskObject.getString("priority"));
            taskModelTitle.setViewType(TASK_TITLE_VIEW);
            taskModel.add(taskModelTitle);

            TaskModel taskModelContact = new TaskModel();
            taskModelContact.setAssignName(taskObject.getString("assigned_by"));
            String phoneNo = "";
            if (Utils.validateString(taskObject.getString("assigned_by_phone"))) {
                phoneNo = Utils.getPhoneNumberFormat(taskObject.getString("assigned_by_phone"));
            }
            taskModelContact.setAssignContact(phoneNo);
            taskModelContact.setAssignEmail(taskObject.getString("assigned_by_email"));
            taskModelContact.setViewType(TASK_CONTACT_VIEW);
            taskModel.add(taskModelContact);

            TaskModel taskModelTiming = new TaskModel();
            taskModelTiming.setStartDate(taskObject.getString("start_date"));
            taskModelTiming.setStartTime(taskObject.getString("start_time"));
            taskModelTiming.setDueDate(taskObject.getString("due_date"));
            taskModelTiming.setDueTime(taskObject.getString("due_time"));
            taskModelTiming.setStatus(status);
            taskModelTiming.setViewType(TASK_TIMING_VIEW);
            taskModel.add(taskModelTiming);

            TaskModel taskModelAddress = new TaskModel();
            taskModelAddress.setStreet(taskObject.getString("street"));
            taskModelAddress.setCity(taskObject.getString("city"));
            taskModelAddress.setState(taskObject.getString("state"));
            taskModelAddress.setCountry(taskObject.getString("country"));
            taskModelAddress.setPincode(taskObject.getString("pincode"));
            taskModelAddress.setLatitude(taskObject.getString("lattitude"));
            taskModelAddress.setLongitude(taskObject.getString("longitude"));
            taskModelAddress.setViewType(TASK_ADDRESS_VIEW);
            taskModel.add(taskModelAddress);
            String street1 = "", city1 = "", state1 = "", country1 = "", pincode1 = "";
            if (Utils.validateString(taskObject.getString("street"))) {
                street1 = taskObject.getString("street") + ", ";
            }
            if (Utils.validateString(taskObject.getString("city"))) {
                city1 = taskObject.getString("city") + ", ";
            }
            if (Utils.validateString(taskObject.getString("state"))) {
                state1 = taskObject.getString("state") + ", ";
            }
            if (Utils.validateString(taskObject.getString("country"))) {
                country1 = taskObject.getString("country") + ", ";
            }
            if (Utils.validateString(taskObject.getString("pincode"))) {
                pincode1 = taskObject.getString("pincode");
            }
            projectAddress = street1 + city1 + state1 + country1 + pincode1;

            TaskModel taskModelAdditionalContactTitle = new TaskModel();
            taskModelAdditionalContactTitle.setId(taskObject.getString("id"));
            taskModelAdditionalContactTitle.setHeaderName("Additional Contact(s)");

            JSONArray additionalContactsArray = taskObject.getJSONArray("additional_contacts");
            _additionalContact = new ArrayList<>();
            if (additionalContactsArray.length() > 0) {
                String id = "", name = "", company = "", address = "", phone = "", email = "", projectResponsiblityId = "",
                        projectResponsiblityName = "", isSync = "", contactStatus = "", created_date;
                for (int i = 0; i < additionalContactsArray.length(); i++) {
                    JSONObject contactObject = additionalContactsArray.getJSONObject(i);

                    if (Utils.validateString(contactObject.getString("id")))
                        id = contactObject.getString("id");
                    else
                        id = "";

                    if (Utils.validateString(contactObject.getString("name")))
                        name = contactObject.getString("name");
                    else
                        name = "";

                    if (Utils.validateString(contactObject.getString("company")))
                        company = contactObject.getString("company");
                    else
                        company = "";

                    if (Utils.validateString(contactObject.getString("address")))
                        address = contactObject.getString("address");
                    else
                        address = "";

                    if (Utils.validateString(contactObject.getString("phone")))
                        phone = contactObject.getString("phone");
                    else
                        phone = "";

                    if (Utils.validateString(contactObject.getString("email")))
                        email = contactObject.getString("email");
                    else
                        email = "";

                    if (!contactObject.isNull("project_responsiblity_id"))
                        projectResponsiblityId = contactObject.getString("project_responsiblity_id");
                    else
                        projectResponsiblityId = "";

                    if (!contactObject.isNull("project_responsiblity"))
                        projectResponsiblityName = contactObject.getString("project_responsiblity");
                    else
                        projectResponsiblityName = "";

                    if (!contactObject.isNull("is_sync"))
                        isSync = contactObject.getString("is_sync");
                    else
                        isSync = "Y";

                    if (!contactObject.isNull("additional_contact_operation"))
                        contactStatus = contactObject.getString("additional_contact_operation");
                    else
                        contactStatus = "A";

                    if (!contactObject.isNull("created_date"))
                        created_date = contactObject.getString("created_date");
                    else
                        created_date = Utils.getCurrentDateAndTime();

                    _additionalContact.add(new AdditionalContactModel(id, name, company, address, phone, email, projectResponsiblityName,
                            projectResponsiblityId, isSync, contactStatus, created_date, contactObject));

                    mDbHelper.insertProjectAdditionalContact(mDbHelper.myDataBase, id, ProjectID, name, company, address, phone, email,
                            projectResponsiblityName, projectResponsiblityId, isSync, contactStatus, created_date);

                }
            }

            taskModelAdditionalContactTitle.setAdditionalContactModel(_additionalContact);
            taskModelAdditionalContactTitle.setStatus(status);
            taskModelAdditionalContactTitle.setViewType(TASK_ADDITIONAL_CONTACT_VIEW);
            taskModel.add(taskModelAdditionalContactTitle);

            TaskModel taskModelNoteTitle = new TaskModel();
            taskModelNoteTitle.setId(taskObject.getString("id"));
            taskModelNoteTitle.setHeaderName("Notes(s)");
            JSONArray notesArray = taskObject.getJSONArray("project_notes");
            _notes = new ArrayList<>();
            if (notesArray.length() > 0) {
                String id = "", note = "", createdDate = "", isSync = "", noteStatus = "";
                for (int i = 0; i < notesArray.length(); i++) {
                    JSONObject notesObject = notesArray.getJSONObject(i);
                    id = notesObject.getString("id");
                    note = notesObject.getString("note");
                    createdDate = notesObject.getString("created_date");
                    if (!notesObject.isNull("is_sync"))
                        isSync = notesObject.getString("is_sync");
                    else
                        isSync = "Y";
                    if (!notesObject.isNull("notes_operation"))
                        noteStatus = notesObject.getString("notes_operation");
                    else
                        noteStatus = "A";

                    _notes.add(new NotesModel(id, note, createdDate, isSync, noteStatus));

                    mDbHelper.insertProjectNotes(mDbHelper.myDataBase, id, ProjectID, note, createdDate, isSync, noteStatus);

                }
            }
            taskModelNoteTitle.setNotesModel(_notes);
            taskModelNoteTitle.setStatus(status);
            taskModelNoteTitle.setViewType(TASK_NOTE_VIEW);
            taskModel.add(taskModelNoteTitle);

            TaskModel taskModelProjectPhaseTitle = new TaskModel();
            taskModelProjectPhaseTitle.setHeaderName("Project Phase(s)");

            JSONArray projectPhaseArray = taskObject.getJSONArray("project_phase_data");
            _projects = new ArrayList<>();
            if (projectPhaseArray.length() > 0) {
                String id = "", description = "", createdDate = "", isSync = "", phaseStatus = "";
                for (int i = 0; i < projectPhaseArray.length(); i++) {
                    JSONObject projectObject = projectPhaseArray.getJSONObject(i);
                    id = projectObject.getString("id");
                    description = projectObject.getString("description");
                    createdDate = projectObject.getString("created_date");
                    if (!projectObject.isNull("is_sync"))
                        isSync = projectObject.getString("is_sync");
                    else
                        isSync = "Y";
                    if (!projectObject.isNull("project_phase_operation"))
                        phaseStatus = projectObject.getString("project_phase_operation");
                    else
                        phaseStatus = "A";

                    _projects.add(new ProjectPhaseModel(id, description, createdDate, isSync, phaseStatus));

                    mDbHelper.insertProjectPhase(mDbHelper.myDataBase, id, ProjectID, description, createdDate, isSync,
                            phaseStatus);

                }
            }
            taskModelProjectPhaseTitle.setProjectPhaseModel(_projects);
            taskModelProjectPhaseTitle.setStatus(status);
            taskModelProjectPhaseTitle.setViewType(TASK_PROJECT_PHASE_VIEW);
            taskModel.add(taskModelProjectPhaseTitle);

            TaskModel taskModelMediaTitle = new TaskModel();
            taskModelMediaTitle.setHeaderName("Media & Documents (press and hold to select images for report)");
            JSONArray mediaArray = taskObject.getJSONArray("project_media");
            mediaList = new ArrayList<>();
            if (mediaArray.length() > 0) {
                String id = "", mediaType = "", media = "", videoThumb = "", description = "", latitude = "", longitude = "",
                        street = "", city = "", state = "", country = "", pincode = "", createdDate = "", isSync = "",
                        mediaStatus = "", docThumb = "";
                for (int i = 0; i < mediaArray.length(); i++) {
                    JSONObject mediaObject = mediaArray.getJSONObject(i);
                    id = mediaObject.getString("id");
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
                    if (!mediaObject.isNull("is_sync"))
                        isSync = mediaObject.getString("is_sync");
                    else
                        isSync = "Y";
                    if (!mediaObject.isNull("media_operation"))
                        mediaStatus = mediaObject.getString("media_operation");
                    else
                        mediaStatus = "A";
                    if (!mediaObject.isNull("doc_thumb_image"))
                        docThumb = mediaObject.getString("doc_thumb_image");
                    else
                        docThumb = "";


                    mediaList.add(new MediaModel(id, mediaType, media, videoThumb, description, latitude, longitude, street, city,
                            state, country, pincode, createdDate, isSync, mediaStatus, docThumb, mediaObject));

                    mDbHelper.insertProjectMedia(mDbHelper.myDataBase, id, ProjectID, mediaType, media, videoThumb, description,
                            latitude, longitude, street, city, state, country, pincode, createdDate, isSync, mediaStatus, docThumb);

                }
            }
            taskModelMediaTitle.setMediaModel(mediaList);
            taskModelMediaTitle.setStatus(status);
            taskModelMediaTitle.setViewType(TASK_MEDIA_VIEW);
            taskModel.add(taskModelMediaTitle);

            TaskModel taskStatusModel = new TaskModel();
            taskStatusModel.setStatus(status);
            taskStatusModel.setViewType(TASK_STATUS_BTN_VIEW);
            taskModel.add(taskStatusModel);

//            if (status.equalsIgnoreCase(Utils.COMPLETE)) {
            TaskModel taskReport = new TaskModel();
            taskReport.setStatus(status);
            taskReport.setViewType(TASK_GENERATE_REPORT_BTN_VIEW);
            taskModel.add(taskReport);
//            }


            //Project Required Details
            projectdescription = taskObject.getString("description");
            String project_start_time = taskObject.getString("project_start_time");
            projectStartDate = taskObject.getString("start_date");
            projectstartTime = taskObject.getString("start_time");
            projectDueDate = taskObject.getString("due_date");
            projectDueTime = taskObject.getString("due_time");
            projectPriority = taskObject.getString("priority");
            String street = taskObject.getString("street");
            String city = taskObject.getString("city");
            String state = taskObject.getString("state");
            String country = taskObject.getString("country");
            String pincode = taskObject.getString("pincode");
            String latitude = taskObject.getString("lattitude");
            String longitude = taskObject.getString("longitude");
            assignByName = taskObject.getString("assigned_by");
            assignByPhone = taskObject.getString("assigned_by_phone");
            assignByEmail = taskObject.getString("assigned_by_email");
            String remind_hours = "";
            if (!taskObject.isNull("remind_hours"))
                remind_hours = taskObject.getString("remind_hours");
            else
                remind_hours = "";
            String isSync = "";
            if (!taskObject.isNull("is_sync"))
                isSync = taskObject.getString("is_sync");
            else
                isSync = "Y";

            String projectUpdateStatus = "";
            if (!taskObject.isNull("project_operation"))
                projectUpdateStatus = taskObject.getString("project_operation");
            else
                projectUpdateStatus = "A";

            String createdDate = "";
            if (!taskObject.isNull("created_date"))
                createdDate = taskObject.getString("created_date");
            else
                createdDate = "";

            mDbHelper.insertProject(mDbHelper.myDataBase, ProjectID, projectName, projectdescription, projectStartDate,
                    projectstartTime, projectDueDate, projectDueTime, projectPriority, projectStatus, street, city, state,
                    country, pincode, latitude, longitude, assignByName, assignByPhone, assignByEmail, isSync, signature,
                    projectUpdateStatus, project_start_time, createdDate, remind_hours);

            mDbHelper.close();

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        adapter.notifyDataSetChanged();

        if (Utils.checkInternetConnection(this)) {
            dounloadImages();
        }

        String pro_status = mSharedPreferences.getString(Utils.PROJECT_STATUS, "");
        if (Utils.validateString(pro_status)) {
            pro_status = mSharedPreferences.getString(Utils.PROJECT_STATUS, "");
            String proStartTime = mSharedPreferences.getString(Utils.PROJECT_START_TIME, "");
            if (pro_status.equalsIgnoreCase(Utils.START) || pro_status.equalsIgnoreCase(Utils.PENDING) ||
                    (pro_status.equalsIgnoreCase(Utils.OPEN) && !Utils.validateString(proStartTime))) {
                pro_status = Utils.START_WORK;
                editProfileView.setVisibility(View.VISIBLE);
            } else if (pro_status.equalsIgnoreCase(Utils.OPEN) && Utils.validateString(proStartTime)) {
                pro_status = Utils.COMPLETE;
                editProfileView.setVisibility(View.GONE);
            } else if (pro_status.equals(Utils.REPORT)) {
                pro_status = "";
                editProfileView.setVisibility(View.GONE);
            }

            Utils.storeString(mSharedPreferences, Utils.PROJECT_STATUS, "");
            Utils.storeString(mSharedPreferences, Utils.PROJECT_START_TIME, "");

            if (pro_status.equals(Utils.START_WORK)) {
                if (Utils.checkInternetConnection(ProjectDetailsActivity.this)) {
                    updateProjectStatus(getStatus("Open"));
                } else {
                    String projectStartTime = Utils.getCurrentDateAndTime();
                    try {
                        JSONObject object = new JSONObject(mSharedPreferences.getString(Utils.TASK_OBJECT, ""));
                        object.put("project_status", Utils.OPEN);
                        object.put("project_start_time", projectStartTime);
                        if (object.getString("project_operation").equalsIgnoreCase("A") &&
                                object.getString("is_sync").equalsIgnoreCase("N")) {
                            object.put("is_sync", "N");
                            object.put("project_operation", "A");
                        } else {
                            object.put("is_sync", "N");
                            object.put("project_operation", "U");
                        }

                        try {
                            mDbHelper.openDataBase();
                            mDbHelper.updateProjectDetails(ProjectID, projectName,
                                    object.getString("description"), object.getString("start_date"),
                                    object.getString("start_time"), object.getString("due_date"),
                                    object.getString("due_time"), object.getString("priority"),
                                    Utils.OPEN, object.getString("street"),
                                    object.getString("city"), object.getString("state"),
                                    object.getString("country"), object.getString("pincode"),
                                    object.getString("lattitude"), object.getString("longitude"),
                                    object.getString("assigned_by"), object.getString("assigned_by_phone"),
                                    object.getString("assigned_by_email"), object.getString("is_sync"),
                                    object.getString("signature_image"), object.getString("project_operation"),
                                    projectStartTime, object.getString("created_date"), object.getString("remind_hours"));
                            mDbHelper.close();

                        } catch (SQLException e) {
                            e.printStackTrace();

                        }

                        taskModel.clear();
                        Utils.storeString(mSharedPreferences, Utils.TASK_OBJECT, object.toString());
                        setTaskData(object);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            } else if (pro_status.equals(Utils.COMPLETE)) {
                openSignatureView();
            } else if (pro_status.equals(Utils.GENERATE_REPORT)) {
                if (Utils.checkInternetConnection(ProjectDetailsActivity.this)) {
//                    generateReports(USERID, TOKEN, ProjectID, mediaIdsJArray.toString());
                    generateReports();
                } else {
                    Utils.showMessageDialog(ProjectDetailsActivity.this, getString(R.string.alert), "Please check you internet connection.");
                }
                /*else {
                    File file = new File(Environment.getExternalStorageDirectory() + "/Media/PDF/", projectId + ".pdf");
                    if (file.exists()) {
                        Intent intent = new Intent(ProjectDetailsActivity.this, GenerateReportActivity.class);
                        intent.putExtra(Utils.PDF_REPORT_URL, file.toString());
                        intent.putExtra(Utils.PDF_FILE_NAME, projectId + ".pdf");
                        startActivity(intent);
                    }
                }*/

            }
        }
    }

    private void dounloadImages() {
        ArrayList<MediaModel> mediaModelArrayList = new ArrayList<>();
        mediaModelArrayList = mDbHelper.getProjectMediaDetails();
        File file;
        for (int i = 0; i < mediaModelArrayList.size(); i++) {
            String media_id = mediaModelArrayList.get(i).getMediaId();
            String mediaType = mediaModelArrayList.get(i).getMediaType();
            String media = mediaModelArrayList.get(i).getMedia().trim();
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

//                file = new File(Environment.getExternalStorageDirectory() + "/Media/VideoThumb/", media_id + ".jpg");
//                if (!file.exists()) {
//                    docThumb = mediaModelArrayList.get(i).getDocThumbImage().trim();
//                    storeThumbImage(docThumb, media_id + ".jpg");
//                }
            } else if (mediaType.equalsIgnoreCase("txt")) {
                file = new File(Environment.getExternalStorageDirectory() + "/Media/Txt/", media_id + ".txt");
                if (!file.exists()) {
                    storeMediaImage(media, mediaType, media_id + ".txt");
                }

//                file = new File(Environment.getExternalStorageDirectory() + "/Media/VideoThumb/", media_id + ".jpg");
//                if (!file.exists()) {
//                    docThumb = mediaModelArrayList.get(i).getDocThumbImage().trim();
//                    storeThumbImage(docThumb, media_id + ".jpg");
//                }
            } else if (mediaType.equalsIgnoreCase("docx")) {
                file = new File(Environment.getExternalStorageDirectory() + "/Media/Docx/", media_id + ".docx");
                if (!file.exists()) {
                    storeMediaImage(media, mediaType, media_id + ".docx");
                }

//                file = new File(Environment.getExternalStorageDirectory() + "/Media/VideoThumb/", media_id + ".jpg");
//                if (!file.exists()) {
//                    docThumb = mediaModelArrayList.get(i).getDocThumbImage().trim();
//                    storeThumbImage(docThumb, media_id + ".jpg");
//                }
            } else if (mediaType.equalsIgnoreCase("pdf")) {
                file = new File(Environment.getExternalStorageDirectory() + "/Media/PDF/", media_id + ".pdf");
                if (!file.exists()) {
                    storeMediaImage(media, mediaType, media_id + ".pdf");
                }

//                file = new File(Environment.getExternalStorageDirectory() + "/Media/VideoThumb/", media_id + ".jpg");
//                if (!file.exists()) {
//                    docThumb = mediaModelArrayList.get(i).getDocThumbImage().trim();
//                    storeThumbImage(docThumb, media_id + ".jpg");
//                }
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

    private void storeMediaImage(String media, String media_type, String image_name) {

        if (Utils.checkInternetConnection(this)) {
            DounloadImageRequestTask dounloadImageRequestTask = new DounloadImageRequestTask(this);
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

        if (Utils.checkInternetConnection(this)) {
            DounloadThumbImageRequestTask dounloadThumbImageRequestTask = new DounloadThumbImageRequestTask(this);
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

    //TODO: Add, Delete, Edit Notes..........
    String ProjectID, note, noteID, notesStatus, noteSync, phaseStatus, phaseSync;
    boolean NOTE_FLAG = true;

    View.OnClickListener updateDueDateTimeClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int index = (int) v.getTag();
            Logger.e(TAG, "ProjectId :" + ProjectID + " INDEX : " + index);
            openUpdateDueDateTimeView();
        }
    };

    View.OnClickListener addNoteClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int index = (int) v.getTag();
            Logger.e(TAG, "ProjectId ::" + ProjectID + "&& INDEX :: " + index);
            openAddNoteView();
        }
    };

    NoteListAdapter.OnEditNoteListner editNoteClick = new NoteListAdapter.OnEditNoteListner() {
        @Override
        public void onItemClick(NotesModel data) {
            noteID = data.getNoteId();
            note = data.getNote();
            notesStatus = data.getNote_status();
            noteSync = data.getIs_sync();
            NOTE_FLAG = false;
            openAddNoteView();
        }
    };

    NoteListAdapter.OnDeleteNoteListner deleteNoteClick = new NoteListAdapter.OnDeleteNoteListner() {
        @Override
        public void onItemClick(final NotesModel data) {
            noteID = data.getNoteId();
            note = data.getNote();
            if (!noteID.equals("") && !ProjectID.equals("")) {
                AlertDialog.Builder newDialog = new AlertDialog.Builder(ProjectDetailsActivity.this);
                newDialog.setTitle(R.string.alert);
                newDialog.setMessage("Are you sure for Delete Note?");
                newDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (Utils.checkInternetConnection(ProjectDetailsActivity.this)) {
                            deleteNotes();
                        } else {
                            try {
                                if (data.getIs_sync().equalsIgnoreCase("N") && data.getNote_status().equalsIgnoreCase("A")) {
                                    ArrayList<NotesModel> notesModelArrayList = new ArrayList<>();
                                    try {
                                        mDbHelper.openDataBase();
                                        mDbHelper.deleteProjectNotes(noteID);
                                        notesModelArrayList = mDbHelper.getProjectNotesDetails();
                                        mDbHelper.close();
                                    } catch (SQLException e) {
                                        e.printStackTrace();
                                    }

//                                    if (notesModelArrayList.size() > 0) {
                                        JSONObject object = new JSONObject(mSharedPreferences.getString(Utils.TASK_OBJECT, ""));
                                        JSONArray notesArray = object.getJSONArray("project_notes");
                                        Logger.e(TAG, "Notes Array update before : " + notesArray);
                                        if (notesArray.length() > 0) {
                                            for (int i = 0; i < notesArray.length(); i++) {
                                                if (noteID.equalsIgnoreCase(notesArray.getJSONObject(i).getString("id"))) {
                                                    notesArray.remove(i);
                                                    break;
                                                }
                                            }
                                        }
                                        Logger.e(TAG, "Notes Array update after : " + notesArray);
                                        object.put("project_notes", notesArray);
                                        Logger.e(TAG, "Project Object :" + object);
                                        dialog.dismiss();
                                        taskModel.clear();

                                        Utils.storeString(mSharedPreferences, Utils.TASK_OBJECT, object.toString());
                                        setTaskData(object);
//                                    }

                                } else {
                                    ArrayList<NotesModel> notesModelArrayList = new ArrayList<>();
                                    try {
                                        mDbHelper.openDataBase();
                                        mDbHelper.updateProjectNotes(noteID, ProjectID, note, data.getNote_created_date(), "N", "D");
                                        notesModelArrayList = mDbHelper.getProjectNotesDetails();
                                        mDbHelper.close();
                                    } catch (SQLException e) {
                                        e.printStackTrace();
                                    }

                                    JSONObject object = new JSONObject(mSharedPreferences.getString(Utils.TASK_OBJECT, ""));
                                    JSONArray notesArray = object.getJSONArray("project_notes");
                                    Logger.e(TAG, "Notes Array update before : " + notesArray);
                                    if (notesArray.length() > 0) {
                                        for (int i = 0; i < notesArray.length(); i++) {
                                            if (noteID.equalsIgnoreCase(notesArray.getJSONObject(i).getString("id"))) {
                                                notesArray.remove(i);
                                                break;
                                            }
                                        }
                                    }
                                    Logger.e(TAG, "Notes Array update after : " + notesArray);
                                    object.put("project_notes", notesArray);
                                    if (object.getString("project_operation").equalsIgnoreCase("A") &&
                                            object.getString("is_sync").equalsIgnoreCase("N")) {
                                        object.put("is_sync", "N");
                                        object.put("project_operation", "A");
                                    } else {
                                        object.put("is_sync", "N");
                                        object.put("project_operation", "U");
                                    }
                                    Logger.e(TAG, "Project Object :" + object);
                                    dialog.dismiss();
                                    taskModel.clear();

                                    Utils.storeString(mSharedPreferences, Utils.TASK_OBJECT, object.toString());
                                    setTaskData(object);
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        dialog.dismiss();
                    }
                });
                newDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                newDialog.show();
            }
        }
    };

    View.OnClickListener phoneClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int index = (int) v.getTag();
            Log.d("INDEX", "" + index);
            String number = taskModel.get(index).getAssignContact();
            Log.d("number", "" + number);
            Intent callIntent = new Intent(Intent.ACTION_DIAL);
            callIntent.setData(Uri.parse("tel:" + number));
            startActivity(callIntent);
        }
    };

    //TODO: Add, Delete, Edit Additional Contact..........
    String additionalConatctID;

    View.OnClickListener addAdditionalContactClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int index = (int) v.getTag();
            Log.d("INDEX", "" + index);
            Intent additionalIntent = new Intent(ProjectDetailsActivity.this, AdditionalContactsActivity.class);
            additionalIntent.putExtra(Utils.PROJECT_ID, ProjectID);
            additionalIntent.putExtra(Utils.ADDITIONAL_CONTACT, "");
            startActivityForResult(additionalIntent, 1);
        }
    };

    AdditionalContactListAdapter.OnEditAdditionalContactListner editContactClick = new AdditionalContactListAdapter.OnEditAdditionalContactListner() {
        @Override
        public void onItemClick(AdditionalContactModel data) {
            Logger.e(TAG, "Additional Contact Obj" + data.getContactObject());
            JSONObject additionalObj = data.getContactObject();
            Intent additionalIntent = new Intent(ProjectDetailsActivity.this, AdditionalContactsActivity.class);
            additionalIntent.putExtra(Utils.PROJECT_ID, ProjectID);
            additionalIntent.putExtra(Utils.ADDITIONAL_CONTACT, "1");
            additionalIntent.putExtra(Utils.ADDITIONAL_CONTACT_OBJECT, additionalObj.toString());
            startActivityForResult(additionalIntent, 1);
        }
    };

    AdditionalContactListAdapter.OnDeleteAdditionalContactListner deleteContactClick = new AdditionalContactListAdapter.OnDeleteAdditionalContactListner() {
        @Override
        public void onItemClick(final AdditionalContactModel data) {
            additionalConatctID = data.getAdtnlContactID();
            if (!additionalConatctID.equals("") && !ProjectID.equals("")) {
                AlertDialog.Builder newDialog = new AlertDialog.Builder(ProjectDetailsActivity.this);
                newDialog.setTitle(R.string.alert);
                newDialog.setMessage("Are you sure for Delete Additional Contact?");
                newDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (Utils.checkInternetConnection(ProjectDetailsActivity.this)) {
                            deleteAdditionalContact();
                        } else {
                            try {
                                if (data.getIs_sync().equalsIgnoreCase("N") && data.getContact_status().equalsIgnoreCase("A")) {
                                    ArrayList<AdditionalContactModel> additionalContactModelArrayList = new ArrayList<>();
                                    try {
                                        mDbHelper.openDataBase();
                                        mDbHelper.deleteProjectAdditionalContact(additionalConatctID);
                                        additionalContactModelArrayList = mDbHelper.getAdditionalContactDetails();
                                        mDbHelper.close();
                                    } catch (SQLException e) {
                                        e.printStackTrace();
                                    }

//                                    if (additionalContactModelArrayList.size() > 0) {
                                        JSONObject object = new JSONObject(mSharedPreferences.getString(Utils.TASK_OBJECT, ""));
                                        JSONArray additionalContactsArray = object.getJSONArray("additional_contacts");
                                        Logger.e(TAG, "Additional Contact Array before :" + additionalContactsArray);
                                        if (additionalContactsArray.length() > 0) {
                                            for (int i = 0; i < additionalContactsArray.length(); i++) {
                                                if (additionalConatctID.equalsIgnoreCase(additionalContactsArray.getJSONObject(i).getString("id"))) {
                                                    additionalContactsArray.remove(i);
                                                    break;
                                                }
                                            }
                                        }
                                        Logger.e(TAG, "Additional Contact Array after :" + additionalContactsArray);
                                        object.put("additional_contacts", additionalContactsArray);
                                        Logger.e(TAG, "Project Object :" + object);
                                        dialog.dismiss();
                                        taskModel.clear();

                                        Utils.storeString(mSharedPreferences, Utils.TASK_OBJECT, object.toString());
                                        setTaskData(object);
//                                    }

                                } else {
                                    ArrayList<AdditionalContactModel> additionalContactModelArrayList = new ArrayList<>();
                                    try {
                                        mDbHelper.openDataBase();
                                        mDbHelper.updateProjectAdditionalContact(additionalConatctID, ProjectID, data.getAdtnlContactName(), data.getAdtnlContactCompany(),
                                                data.getAdtnlContactAddress(), data.getAdtnlContactPhone(), data.getAdtnlContactEmail(),
                                                data.getAdtnlContactResponsibility(), data.getAdtnlContactResponsibilityId(), "N",
                                                "D", data.getCreated_date());
                                        additionalContactModelArrayList = mDbHelper.getAdditionalContactDetails();
                                        mDbHelper.close();
                                    } catch (SQLException e) {
                                        e.printStackTrace();
                                    }

                                    JSONObject object = new JSONObject(mSharedPreferences.getString(Utils.TASK_OBJECT, ""));
                                    JSONArray additionalContactsArray = object.getJSONArray("additional_contacts");
                                    Logger.e(TAG, "Additional Contact Array before :" + additionalContactsArray);
                                    if (additionalContactsArray.length() > 0) {
                                        for (int i = 0; i < additionalContactsArray.length(); i++) {
                                            if (additionalConatctID.equalsIgnoreCase(additionalContactsArray.getJSONObject(i).getString("id"))) {
                                                additionalContactsArray.remove(i);
                                                break;
                                            }
                                        }
                                    }

                                    Logger.e(TAG, "Additional Contact Array after :" + additionalContactsArray);
                                    object.put("additional_contacts", additionalContactsArray);
                                    if (object.getString("project_operation").equalsIgnoreCase("A") &&
                                            object.getString("is_sync").equalsIgnoreCase("N")) {
                                        object.put("is_sync", "N");
                                        object.put("project_operation", "A");
                                    } else {
                                        object.put("is_sync", "N");
                                        object.put("project_operation", "U");
                                    }
                                    Logger.e(TAG, "Project Object :" + object);
                                    dialog.dismiss();
                                    taskModel.clear();

                                    Utils.storeString(mSharedPreferences, Utils.TASK_OBJECT, object.toString());
                                    setTaskData(object);
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        dialog.dismiss();
                    }
                });
                newDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                newDialog.show();
            }
        }
    };

    //TODO: Add Project Click....
    View.OnClickListener addProjectClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int index = (int) v.getTag();
            Log.d("INDEX", "" + index);
            openAddProjectPhaseView();
        }
    };

    //TODO: Update Project Phase
    ProjectPhaseListAdapter.OnEditProjectPhaseListner onEditProjectPhaseListner = new ProjectPhaseListAdapter.OnEditProjectPhaseListner() {

        @Override
        public void onItemClick(ProjectPhaseModel data) {
            project_phase_id = data.getProjectPhaseId();
            project_des = data.getProjectDesription();
            phaseStatus = data.getPhase_status();
            phaseSync = data.getIs_sync();
            projectPhaseFlag = false;
            openAddProjectPhaseView();
        }
    };

    //TODO: Delete Project Phase
    ProjectPhaseListAdapter.OnDeleteProjectPhaseListner onDeleteProjectPhaseListner = new ProjectPhaseListAdapter.OnDeleteProjectPhaseListner() {
        @Override
        public void onItemClick(final ProjectPhaseModel data) {
            project_phase_id = data.getProjectPhaseId();
            if (!project_phase_id.equals("") && !ProjectID.equals("")) {
                AlertDialog.Builder newDialog = new AlertDialog.Builder(ProjectDetailsActivity.this);
                newDialog.setTitle(R.string.alert);
                newDialog.setMessage("Are you sure for Delete Project Phase?");
                newDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (Utils.checkInternetConnection(ProjectDetailsActivity.this)) {
                            deleteProjectPhase();
                        } else {
                            try {
                                if (data.getIs_sync().equalsIgnoreCase("N") && data.getPhase_status().equalsIgnoreCase("A")) {
                                    ArrayList<ProjectPhaseModel> phaseModelArrayList = new ArrayList<>();
                                    try {
                                        mDbHelper.openDataBase();
                                        mDbHelper.deleteProjectPhaseDetails(project_phase_id);
                                        phaseModelArrayList = mDbHelper.getProjectPhaseDetails();
                                        mDbHelper.close();
                                    } catch (SQLException e) {
                                        e.printStackTrace();
                                    }

//                                    if (phaseModelArrayList.size() > 0) {
                                        JSONObject object = new JSONObject(mSharedPreferences.getString(Utils.TASK_OBJECT, ""));
                                        JSONArray phaseArray = object.getJSONArray("project_phase_data");
                                        Logger.e(TAG, "Phase Array update before : " + phaseArray);
                                        if (phaseArray.length() > 0) {
                                            for (int i = 0; i < phaseArray.length(); i++) {
                                                if (project_phase_id.equalsIgnoreCase(phaseArray.getJSONObject(i).getString("id"))) {
                                                    phaseArray.remove(i);
                                                    break;
                                                }
                                            }
                                        }
                                        Logger.e(TAG, "Phase Array update after : " + phaseArray);
                                        object.put("project_phase_data", phaseArray);
                                        Logger.e(TAG, "Project Object :" + object);
                                        dialog.dismiss();
                                        taskModel.clear();

                                        Utils.storeString(mSharedPreferences, Utils.TASK_OBJECT, object.toString());
                                        setTaskData(object);
//                                    }

                                } else {
                                    ArrayList<ProjectPhaseModel> phaseModelArrayList = new ArrayList<>();
                                    try {
                                        mDbHelper.openDataBase();
                                        mDbHelper.updateProjectPhaseDetails(project_phase_id, ProjectID, data.getProjectDesription(), data.getProjectCreatedDate(),
                                                "N", "D");
                                        phaseModelArrayList = mDbHelper.getProjectPhaseDetails();
                                        mDbHelper.close();
                                    } catch (SQLException e) {
                                        e.printStackTrace();
                                    }

                                    JSONObject object = new JSONObject(mSharedPreferences.getString(Utils.TASK_OBJECT, ""));
                                    JSONArray phaseArray = object.getJSONArray("project_phase_data");
                                    Logger.e(TAG, "Phase Array update before : " + phaseArray);
                                    if (phaseArray.length() > 0) {
                                        for (int i = 0; i < phaseArray.length(); i++) {
                                            if (project_phase_id.equalsIgnoreCase(phaseArray.getJSONObject(i).getString("id"))) {
                                                phaseArray.remove(i);
                                                break;
                                            }
                                        }
                                    }
                                    Logger.e(TAG, "Phase Array update after : " + phaseArray);
                                    object.put("project_phase_data", phaseArray);
                                    if (object.getString("project_operation").equalsIgnoreCase("A") &&
                                            object.getString("is_sync").equalsIgnoreCase("N")) {
                                        object.put("is_sync", "N");
                                        object.put("project_operation", "A");
                                    } else {
                                        object.put("is_sync", "N");
                                        object.put("project_operation", "U");
                                    }
                                    Logger.e(TAG, "Project Object :" + object);
                                    dialog.dismiss();
                                    taskModel.clear();

                                    Utils.storeString(mSharedPreferences, Utils.TASK_OBJECT, object.toString());
                                    setTaskData(object);
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }

                        dialog.dismiss();
                    }
                });
                newDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                newDialog.show();
            }
        }
    };

    //TODO: Add Media Click.....
    View.OnClickListener addMediaClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int index = (int) v.getTag();
            Log.d("INDEX", "" + index);
            takeMediaOptions();
        }
    };


    JSONArray mediaIdsJArray = new JSONArray();
    MediaAdapter.OnMediaClickListner mediaItemClick = new MediaAdapter.OnMediaClickListner() {
        @Override
        public void onItemClick(MediaModel data, boolean flag) {
            Logger.e(TAG, "Media Contact Obj" + data.getMediaObject());
            Logger.d("Media ID", "" + data.getMediaId());
            String media_id = data.getMediaId();
            String mediaType = data.getMediaType();
            String media = data.getMedia();
            if (!flag) {
                if (mediaType.equalsIgnoreCase("doc") || mediaType.equalsIgnoreCase("txt") || mediaType.equalsIgnoreCase("docx") ||
                        mediaType.equalsIgnoreCase("pdf")) {

//                    File file;
//                    if(mediaType.equalsIgnoreCase("docx")){
//                        file = new File(Environment.getExternalStorageDirectory() + "/Media/Docx/", media_id + ".docx");
//                        if (file.exists()) {
//                            Intent intent = new Intent();
//                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                            intent.setAction(Intent.ACTION_VIEW);
//                            String type = "application/msword";
//                            intent.setDataAndType(Uri.fromFile(file), type);
//                            startActivity(intent);
//                        }
//                    } else {
//                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(media));
//                    startActivity(browserIntent);
//                    }

                    File file;
                    if (mediaType.equalsIgnoreCase("doc")) {
                        file = new File(Environment.getExternalStorageDirectory() + "/Media/Doc/", media_id + ".doc");
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setDataAndType(Uri.fromFile(file), "application/msword");
                        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                        startActivity(Intent.createChooser(intent, ""));
                    } else if (mediaType.equalsIgnoreCase("docx")) {
                        file = new File(Environment.getExternalStorageDirectory() + "/Media/Docx/", media_id + ".docx");
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setDataAndType(Uri.fromFile(file), "application/vnd.openxmlformats-officedocument.wordprocessingml.document");
                        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                        startActivity(Intent.createChooser(intent, ""));
                    } else if (mediaType.equalsIgnoreCase("txt")) {
                        file = new File(Environment.getExternalStorageDirectory() + "/Media/Txt/", media_id + ".txt");
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setDataAndType(Uri.fromFile(file), "text/plain");
                        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                        startActivity(Intent.createChooser(intent, ""));
                    } else if (mediaType.equalsIgnoreCase("pdf")) {
                        file = new File(Environment.getExternalStorageDirectory() + "/Media/PDF/", media_id + ".pdf");
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setDataAndType(Uri.fromFile(file), "application/pdf");
                        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                        startActivity(Intent.createChooser(intent, ""));
                    }

                } else {
                    JSONObject mediaObj = data.getMediaObject();
                    Intent mediaIntent = new Intent(ProjectDetailsActivity.this, ProjectMediaActivity.class);
                    mediaIntent.putExtra(Utils.PROJECT_ID, ProjectID);
                    mediaIntent.putExtra(Utils.MEDIA_ID, data.getMediaId());
                    mediaIntent.putExtra(Utils.MEDIA_OBJECT, mediaObj.toString());
                    mediaIntent.putExtra(Utils.PROJECT_STATUS, projectStatus);
                    startActivityForResult(mediaIntent, 1);
                }
            } else {
                if (data.isSelected()) {
                    mediaIDs.append(media_id + ",");
                    mediaIdsJArray.put(media_id);
                } else {
                    for (int i = 0; i < mediaIdsJArray.length(); i++) {
                        try {
                            if (mediaIdsJArray.get(i).toString().equalsIgnoreCase(media_id)) {
                                mediaIdsJArray.remove(i);
                                break;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    int i = mediaIDs.indexOf(media_id);
                    if (i != -1) {
                        String str = media_id + ",";
                        mediaIDs.delete(i, i + str.length());
                    }
                }
                Logger.e(TAG, "Media IDS : " + mediaIdsJArray.toString());
            }
        }

//        @Override
//        public void onItemClick(MediaModel data) {
//
//        }
    };

    MediaAdapter.OnMediaLongClickListner mediaItemLongClick = new MediaAdapter.OnMediaLongClickListner() {
        @Override
        public void onItemClick(MediaModel data) {
            Logger.e(TAG, "Media LongPress Id :: " + data.getMediaId());
            Logger.e(TAG, "Media Selection :: " + data.isSelected());
            String media_id = data.getMediaId();
            if (!String.valueOf(mediaIDs).contains(media_id)) {
                mediaIDs.append(media_id + ",");
                mediaIdsJArray.put(media_id);
            }
            Logger.e(TAG, "Media IDS : " + mediaIdsJArray.toString());
        }
    };

    View.OnClickListener updateStatusClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int index = (int) v.getTag();
            Log.d("INDEX", "" + index);

            String status = taskModel.get(index).getStatus();
            String project_start_time = taskModel.get(index).getProject_start_time();

            if (status.equals(Utils.START_WORK)) {
                if (Utils.checkInternetConnection(ProjectDetailsActivity.this)) {
                    updateProjectStatus(getStatus("Open"));
                } else {
                    String projectStartTime = Utils.getCurrentDateAndTime();
                    try {
                        JSONObject object = new JSONObject(mSharedPreferences.getString(Utils.TASK_OBJECT, ""));
                        object.put("project_status", Utils.OPEN);
                        object.put("project_start_time", projectStartTime);
                        if (object.getString("project_operation").equalsIgnoreCase("A") &&
                                object.getString("is_sync").equalsIgnoreCase("N")) {
                            object.put("is_sync", "N");
                            object.put("project_operation", "A");
                        } else {
                            object.put("is_sync", "N");
                            object.put("project_operation", "U");
                        }

                        try {
                            mDbHelper.openDataBase();
                            mDbHelper.updateProjectDetails(ProjectID, projectName,
                                    object.getString("description"), object.getString("start_date"),
                                    object.getString("start_time"), object.getString("due_date"),
                                    object.getString("due_time"), object.getString("priority"),
                                    Utils.OPEN, object.getString("street"),
                                    object.getString("city"), object.getString("state"),
                                    object.getString("country"), object.getString("pincode"),
                                    object.getString("lattitude"), object.getString("longitude"),
                                    object.getString("assigned_by"), object.getString("assigned_by_phone"),
                                    object.getString("assigned_by_email"), object.getString("is_sync"),
                                    object.getString("signature_image"), object.getString("project_operation"),
                                    projectStartTime, object.getString("created_date"), object.getString("remind_hours"));
                            mDbHelper.close();

                        } catch (SQLException e) {
                            e.printStackTrace();

                        }

                        taskModel.clear();
                        Utils.storeString(mSharedPreferences, Utils.TASK_OBJECT, object.toString());
                        setTaskData(object);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            } else if (status.equals(Utils.COMPLETE)) {
                openSignatureView();
            } else if (status.equals(Utils.GENERATE_REPORT)) {
                if (Utils.checkInternetConnection(ProjectDetailsActivity.this)) {
//                    generateReports(USERID, TOKEN, ProjectID, mediaIdsJArray.toString());
                    generateReports();
                } else {
                    Utils.showMessageDialog(ProjectDetailsActivity.this, getString(R.string.alert), "Please check you internet connection.");
                }
                /*else {
                    File file = new File(Environment.getExternalStorageDirectory() + "/Media/PDF/", projectId + ".pdf");
                    if (file.exists()) {
                        Intent intent = new Intent(ProjectDetailsActivity.this, GenerateReportActivity.class);
                        intent.putExtra(Utils.PDF_REPORT_URL, file.toString());
                        intent.putExtra(Utils.PDF_FILE_NAME, projectId + ".pdf");
                        startActivity(intent);
                    }
                }*/

            }
        }
    };

    View.OnClickListener generateReportClick = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            if (Utils.checkInternetConnection(ProjectDetailsActivity.this)) {
//                generateReports(USERID, TOKEN, ProjectID, mediaIdsJArray.toString());
                generateReports();
            } else {
                Utils.showMessageDialog(ProjectDetailsActivity.this, getString(R.string.alert), "Please check you internet connection.");
            }

            /*else {
                File file = new File(Environment.getExternalStorageDirectory() + "/Media/PDF/", projectId + ".pdf");
                if (file.exists()) {
                    Intent intent = new Intent(ProjectDetailsActivity.this, GenerateReportActivity.class);
                    intent.putExtra(Utils.PDF_REPORT_URL, file.toString());
                    intent.putExtra(Utils.PDF_FILE_NAME, projectId + ".pdf");
                    startActivity(intent);
                }
            }*/
        }
    };

    public JSONArray getStatus(String status) {
        JSONArray statusArray = new JSONArray();
        try {
            JSONObject statusObject = new JSONObject();
            statusObject.put("project_id", ProjectID);
            statusObject.put("project_status", status);
            statusArray.put(statusObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return statusArray;
    }


//TODO : -----------------------------------SERVICES ------------------------------------------

    //TODO: Update DueDate and DueTime
    public void UpdateDueDateTime(JSONArray dueDateAndTime) {
        pdialog = new ProgressDialog(this);
        pdialog.setMessage(getString(R.string.please_wait));
        pdialog.setCanceledOnTouchOutside(false);
        pdialog.show();

        JSONObject params = new JSONObject();
        try {
            params.put("user_id", USERID);
            params.put("session_token", TOKEN);
            params.put("project", dueDateAndTime);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Logger.e(TAG, "UpdatePorject PARA---->" + String.valueOf(params));
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.POST, Utils.MAIN_URL + Utils.PROJECT_UPDATE_API, params, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Logger.e(TAG, "URL---->" + Utils.MAIN_URL + Utils.PROJECT_UPDATE_API);
                        Logger.e(TAG, "UpdatePorject RES---->" + String.valueOf(response));
                        pdialog.dismiss();
                        try {
                            boolean success = response.getBoolean("success");
                            String msg = response.getString("message");

                            if (success) {
                                Utils.projectRefreshFlag = true;
                                Utils.projectFilterRefreshFlag = true;
                                dialog.dismiss();
                                taskModel.clear();
                                JSONArray jsonArray = response.getJSONArray("data");
                                JSONObject object = jsonArray.getJSONObject(0);
                                JSONObject newObj = addObjectValue(object);
                                setTaskData(newObj);
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

    //TODO: Add note.....
    public void addNotes(JSONArray Notes) {
        pdialog = new ProgressDialog(this);
        pdialog.setMessage(getString(R.string.please_wait));
        pdialog.setCanceledOnTouchOutside(false);
        pdialog.show();

        JSONObject params = new JSONObject();
        try {
            params.put("user_id", USERID);
            params.put("session_token", TOKEN);
            params.put("notes", Notes);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Logger.e(TAG, "addNote PARA---->" + String.valueOf(params));
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.POST, Utils.MAIN_URL + Utils.ADD_NOTE_API, params, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Logger.e(TAG, "URL---->" + Utils.MAIN_URL + Utils.ADD_NOTE_API);
                        Logger.e(TAG, "addNote RES---->" + String.valueOf(response));
                        pdialog.dismiss();
                        try {
                            boolean success = response.getBoolean("success");
                            String msg = response.getString("message");

                            if (success) {
                                Utils.projectRefreshFlag = true;
                                Utils.projectFilterRefreshFlag = true;
                                NOTE_FLAG = true;
                                dialog.dismiss();
                                taskModel.clear();
                                JSONArray jsonArray = response.getJSONArray("data");
                                JSONObject object = jsonArray.getJSONObject(0);
                                JSONObject newObj = addObjectValue(object);
                                setTaskData(newObj);
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

    //TODO: Edit note.....
    public void editNotes(String note) {
        pdialog = new ProgressDialog(this);
        pdialog.setMessage(getString(R.string.please_wait));
        pdialog.setCanceledOnTouchOutside(false);
        pdialog.show();

        JSONObject params = new JSONObject();
        try {
            params.put("user_id", USERID);
            params.put("note_id", noteID);
            params.put("project_id", ProjectID);
            params.put("session_token", TOKEN);
            params.put("note", note);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Logger.e(TAG, "editNotes PARA---->" + String.valueOf(params));
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.POST, Utils.MAIN_URL + Utils.EDIT_NOTE_API, params, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Logger.e(TAG, "editNotes RES---->" + String.valueOf(response));
                        pdialog.dismiss();
                        try {
                            boolean success = response.getBoolean("success");
                            String msg = response.getString("message");

                            if (success) {
                                Utils.projectRefreshFlag = true;
                                Utils.projectFilterRefreshFlag = true;
                                NOTE_FLAG = true;
                                dialog.dismiss();
                                taskModel.clear();
                                JSONObject object = response.getJSONObject("data");
                                JSONObject newObj = addObjectValue(object);
                                setTaskData(newObj);
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

    public void deleteNotes() {
        pdialog = new ProgressDialog(this);
        pdialog.setMessage(getString(R.string.please_wait));
        pdialog.setCanceledOnTouchOutside(false);
        pdialog.show();

        JSONObject params = new JSONObject();
        try {
            params.put("user_id", USERID);
            params.put("note_id", noteID);
            params.put("project_id", ProjectID);
            params.put("session_token", TOKEN);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Logger.e(TAG, "deleteNotes PARA---->" + String.valueOf(params));
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.POST, Utils.MAIN_URL + Utils.DELETE_NOTE_API, params, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Logger.e(TAG, "deleteNotes RES---->" + String.valueOf(response));
                        pdialog.dismiss();
                        try {
                            boolean success = response.getBoolean("success");
                            String msg = response.getString("message");

                            if (success) {
                                ArrayList<NotesModel> notesModelArrayList = new ArrayList<>();
                                try {
                                    mDbHelper.openDataBase();
                                    mDbHelper.deleteProjectNotes(noteID);
                                    notesModelArrayList = mDbHelper.getProjectNotesDetails();
                                    mDbHelper.close();
                                } catch (SQLException e) {
                                    e.printStackTrace();
                                }
                                Utils.projectRefreshFlag = true;
                                Utils.projectFilterRefreshFlag = true;
                                taskModel.clear();
                                JSONObject object = response.getJSONObject("data");
                                JSONObject newObj = addObjectValue(object);
                                setTaskData(newObj);
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

    //TODO: Add Project Phase.....
    public void addProjectPhase(String Projects) {

        pdialog = new ProgressDialog(this);
        pdialog.setMessage(getString(R.string.please_wait));
        pdialog.setCanceledOnTouchOutside(false);
        pdialog.show();

        JSONObject params = new JSONObject();
        try {
            params.put("user_id", USERID);
            params.put("session_token", TOKEN);
            params.put("project_id", ProjectID);
            params.put("description", Projects);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Logger.e(TAG, "addProject PARA---->" + String.valueOf(params));
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.POST, Utils.MAIN_URL + Utils.ADD_PROJECT_PHASE_API, params, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Logger.e(TAG, "addProject RES---->" + String.valueOf(response));
                        pdialog.dismiss();
                        try {
                            boolean success = response.getBoolean("success");
                            String msg = response.getString("message");

                            if (success) {
                                Utils.projectRefreshFlag = true;
                                Utils.projectFilterRefreshFlag = true;
                                projectPhaseFlag = true;
                                dialog.dismiss();
                                taskModel.clear();
                                JSONObject object = response.getJSONObject("data");
                                JSONObject newObj = addObjectValue(object);
                                setTaskData(newObj);
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

    //TODO: Edit Project Phase.....
    public void UpdateProjectPhase(String des) {
        Utils.hideSoftKeyboard(this);
        pdialog = new ProgressDialog(this);
        pdialog.setMessage(getString(R.string.please_wait));
        pdialog.setCanceledOnTouchOutside(false);
        pdialog.show();

        JSONObject params = new JSONObject();
        try {
            params.put("user_id", USERID);
            params.put("session_token", TOKEN);
            params.put("project_id", ProjectID);
            params.put("project_phase_id", project_phase_id);
            params.put("description", des);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Logger.e(TAG, "update Project Phase PARA---->" + String.valueOf(params));
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.POST, Utils.MAIN_URL + Utils.UPDATE_PROJECT_PHASE_API, params, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Logger.e(TAG, "URL---->" + Utils.MAIN_URL + Utils.UPDATE_PROJECT_PHASE_API);
                        Logger.e(TAG, "update Project Phase RES---->" + String.valueOf(response));
                        pdialog.dismiss();
                        try {
                            boolean success = response.getBoolean("success");
                            String msg = response.getString("message");

                            if (success) {
                                Utils.projectRefreshFlag = true;
                                Utils.projectFilterRefreshFlag = true;
                                dialog.dismiss();
                                taskModel.clear();
                                JSONObject object = response.getJSONObject("data");
                                JSONObject newObj = addObjectValue(object);
                                setTaskData(newObj);
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

    public void deleteProjectPhase() {
        pdialog = new ProgressDialog(this);
        pdialog.setMessage(getString(R.string.please_wait));
        pdialog.setCanceledOnTouchOutside(false);
        pdialog.show();

        JSONObject params = new JSONObject();
        try {
            params.put("user_id", USERID);
            params.put("session_token", TOKEN);
            params.put("project_id", ProjectID);
            params.put("project_phase_id", project_phase_id);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Logger.e(TAG, "delete Project Phase PARA---->" + String.valueOf(params));
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.POST, Utils.MAIN_URL + Utils.DELETE_PROJECT_PHASE_API, params, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Logger.e(TAG, "URL---->" + Utils.MAIN_URL + Utils.DELETE_PROJECT_PHASE_API);
                        Logger.e(TAG, "delete Project Phase RES---->" + String.valueOf(response));
                        pdialog.dismiss();
                        try {
                            boolean success = response.getBoolean("success");
                            String msg = response.getString("message");

                            if (success) {
                                ArrayList<ProjectPhaseModel> phaseModelArrayList = new ArrayList<>();
                                try {
                                    mDbHelper.openDataBase();
                                    mDbHelper.deleteProjectPhaseDetails(project_phase_id);
                                    phaseModelArrayList = mDbHelper.getProjectPhaseDetails();
                                    mDbHelper.close();
                                } catch (SQLException e) {
                                    e.printStackTrace();
                                }
                                Utils.projectRefreshFlag = true;
                                Utils.projectFilterRefreshFlag = true;
                                projectPhaseFlag = true;
                                taskModel.clear();
                                JSONObject object = response.getJSONObject("data");
                                JSONObject newObj = addObjectValue(object);
                                setTaskData(newObj);
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

    //TODO: Delete Contact...
    public void deleteAdditionalContact() {
        pdialog = new ProgressDialog(this);
        pdialog.setMessage(getString(R.string.please_wait));
        pdialog.setCanceledOnTouchOutside(false);
        pdialog.show();

        JSONObject params = new JSONObject();
        try {
            params.put("user_id", USERID);
            params.put("session_token", TOKEN);
            params.put("project_id", ProjectID);
            params.put("additional_contacts_id", additionalConatctID);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Logger.e(TAG, "deleteContact PARA---->" + String.valueOf(params));
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.POST, Utils.MAIN_URL + Utils.DELETE_ADDITIONAL_CONTACT_API, params, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Logger.e(TAG, "deleteContact RES---->" + String.valueOf(response));
                        pdialog.dismiss();
                        try {
                            boolean success = response.getBoolean("success");
                            String msg = response.getString("message");

                            if (success) {
                                ArrayList<AdditionalContactModel> additionalContactModelArrayList = new ArrayList<>();
                                try {
                                    mDbHelper.openDataBase();
                                    mDbHelper.deleteProjectAdditionalContact(additionalConatctID);
                                    additionalContactModelArrayList = mDbHelper.getAdditionalContactDetails();
                                    mDbHelper.close();
                                } catch (SQLException e) {
                                    e.printStackTrace();
                                }
                                Utils.projectRefreshFlag = true;
                                Utils.projectFilterRefreshFlag = true;
                                taskModel.clear();
                                JSONObject object = response.getJSONObject("data");
                                JSONObject newObj = addObjectValue(object);
                                setTaskData(newObj);
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


    /*private void uploadVideo() {
        class UploadVideo extends AsyncTask<Void, Void, String> {

            ProgressDialog pdialog;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                pdialog = new ProgressDialog(ProjectDetailsActivity.this);
                pdialog.setMessage(getString(R.string.please_wait));
                pdialog.setCanceledOnTouchOutside(false);
                pdialog.show();

                currentDate = Utils.getCurrentDateAndTime();
                Logger.e(TAG, "currentDate :" + currentDate);

                if (curLatitude != 0 && curLongitude != 0) {
                    Geocoder gcd = new Geocoder(ProjectDetailsActivity.this, Locale.getDefault());
                    List<Address> addresses = null;
                    try {
                        addresses = gcd.getFromLocation(curLatitude, curLongitude, 1);
                        if (addresses.size() > 0) {
                            street = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                            city = addresses.get(0).getLocality();
                            state = addresses.get(0).getAdminArea();
                            country = addresses.get(0).getCountryName();
                            pincode = addresses.get(0).getPostalCode();
                        } else {
                            // do your staff
                        }
                    } catch (IOException e) {

                    }
                }
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                pdialog.dismiss();
                System.out.println("Upload SuccessFull ::" + "SUCCESS");

            }

            @Override
            protected String doInBackground(Void... params) {
                Upload u = new Upload();
                // String msg = u.uploadVideo(selectedPath);
                Logger.d(TAG, "Selected Path :" + selectedImagePath);
                String image_type = "";
                if (selectedImagePath.contains(".mp4")) {
                    image_type = "video";
                } else if (selectedImagePath.contains(".wav")) {
                    image_type = "audio";
                } else {
                    image_type = "image";
                }

                if (fileMediaType.equalsIgnoreCase("doc")) {
                    image_type = "doc";
                } else if (fileMediaType.equalsIgnoreCase("docx")) {
                    image_type = "docx";
                } else if (fileMediaType.equalsIgnoreCase("txt")) {
                    image_type = "txt";
                } else if (fileMediaType.equalsIgnoreCase("pdf")) {
                    image_type = "pdf";
                }

                String msg = u.uploadVideo(USERID, TOKEN, ProjectID, image_type, selectedImagePath, String.valueOf(curLatitude),
                        String.valueOf(curLongitude), street, city, state, country, pincode, currentDate, "", "");

                return msg;
            }
        }
        UploadVideo uv = new UploadVideo();
        uv.execute();
    }*/


    public class uploadMedia extends AsyncTask<Void, Void, Void> {
        boolean exception = false, isSuccess = false;
        ProgressDialog pdialog;
        JSONObject jObj = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pdialog = new ProgressDialog(ProjectDetailsActivity.this);
            pdialog.setMessage(getString(R.string.please_wait));
            pdialog.setCanceledOnTouchOutside(false);
            pdialog.show();

            currentDate = Utils.getCurrentDateAndTime();
            Logger.e(TAG, "currentDate :" + currentDate);

            if (curLatitude != 0 && curLongitude != 0) {
                Geocoder gcd = new Geocoder(ProjectDetailsActivity.this, Locale.getDefault());
                List<Address> addresses = null;
                try {
                    addresses = gcd.getFromLocation(curLatitude, curLongitude, 1);
                    if (addresses.size() > 0) {
                        street = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                        city = addresses.get(0).getLocality();
                        state = addresses.get(0).getAdminArea();
                        country = addresses.get(0).getCountryName();
                        pincode = addresses.get(0).getPostalCode();
                    } else {
                        // do your staff
                    }
                } catch (IOException e) {

                }
            }

        }


        @Override
        protected Void doInBackground(Void... params) {
            try {
                Upload_Image uploadimage = new Upload_Image(ProjectDetailsActivity.this);
                Logger.d(TAG, "Selected Path :" + selectedImagePath);
//                if (Utils.validateString(selectedImagePath)) {
                String image_type = "";
                if (selectedImagePath.contains(".mp4")) {
                    image_type = "video";
                } else if (selectedImagePath.contains(".wav")) {
                    image_type = "audio";
                } else {
                    image_type = "image";
                }

                if (fileMediaType.equalsIgnoreCase("doc")) {
                    image_type = "doc";
                } else if (fileMediaType.equalsIgnoreCase("docx")) {
                    image_type = "docx";
                } else if (fileMediaType.equalsIgnoreCase("txt")) {
                    image_type = "txt";
                } else if (fileMediaType.equalsIgnoreCase("pdf")) {
                    image_type = "pdf";
                }

                String uploaddata = uploadimage.uploadMedia(USERID, TOKEN, ProjectID, image_type, selectedImagePath, String.valueOf(curLatitude),
                        String.valueOf(curLongitude), street, city, state, country, pincode, currentDate, "", "");

                JSONObject jsonObject = new JSONObject(uploaddata);
                Logger.e(TAG, "Server response :::" + uploaddata);
                boolean success = jsonObject.getBoolean("success");
                String msg = jsonObject.getString("message");
                if (success) {
                    if (!jsonObject.isNull("data")) {
                        isSuccess = true;
                        jObj = jsonObject.getJSONObject("data");
                        JSONArray additionalContactsArray = jObj.getJSONArray("additional_contacts");
                        JSONArray additionalContactsNewArray = new JSONArray();
                        String projectResponsiblityName = "", projectResponsiblityId = "";
                        Logger.e(TAG, "Additional Contact Array before :" + additionalContactsArray);
                        if (additionalContactsArray.length() > 0) {
                            for (int i = 0; i < additionalContactsArray.length(); i++) {
                                JSONObject contactObject = additionalContactsArray.getJSONObject(i);

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
                                contactObject.put("project_responsiblity", projectResponsiblityName);
                                contactObject.put("project_responsiblity_id", projectResponsiblityId);

                                additionalContactsNewArray.put(contactObject);
                            }
                        }

                        Logger.e(TAG, "Additional Contact Array after :" + additionalContactsNewArray);
                        jObj.put("additional_contacts", additionalContactsNewArray);
                        Logger.e(TAG, "Project Object :" + jObj);

                    } else {
                        pdialog.dismiss();
                        showMessage(msg);
                    }
                } else {
                    pdialog.dismiss();
                    showMessage(msg);
                }

//                if (!Utils.validateString(fileMediaType)) {
                if (!FlagGallery) {
                    File fdelete = new File(selectedImagePath);
                    if (fdelete.exists()) {
                        if (fdelete.delete()) {
                            Logger.d(TAG, "file Deleted :" + selectedImagePath);
                        } else {
                            Logger.d(TAG, "file not Deleted :" + selectedImagePath);
                        }
                    }
                }
//                }
//                }

            } catch (JSONException e) {
                exception = true;
                pdialog.dismiss();
                e.printStackTrace();
            } catch (NullPointerException e) {
                exception = true;
                pdialog.dismiss();
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            pdialog.dismiss();

            if (isSuccess) {
                if (jObj != null) {
                    fileMediaType = "";
                    selectedImagePath = "";
                    Utils.projectRefreshFlag = true;
                    Utils.projectFilterRefreshFlag = true;
                    taskModel.clear();
                    setTaskData(jObj);
                }
            }
        }
    }


    //TODO: UPDATE Status .................................................

    public void updateProjectStatus(JSONArray projectArray) {
        pdialog = new ProgressDialog(this);
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
                (Request.Method.POST, Utils.MAIN_URL + Utils.UPDATE_STATUS_API, params, new Response.Listener<JSONObject>() {
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
                                taskModel.clear();
                                JSONArray jsonArray = response.getJSONArray("data");
                                JSONObject object = jsonArray.getJSONObject(0);
                                JSONObject newObj = addObjectValue(object);
                                setTaskData(newObj);
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


    public class uploadSignature extends AsyncTask<Void, Void, Void> {
        boolean exception = false, isSuccess = false;
        ProgressDialog pdialog;
        JSONObject jObj = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pdialog = new ProgressDialog(ProjectDetailsActivity.this);
            pdialog.setMessage(getString(R.string.please_wait));
            pdialog.setCanceledOnTouchOutside(false);
            pdialog.show();
        }


        @Override
        protected Void doInBackground(Void... params) {
            try {
                Upload_Image uploadimage = new Upload_Image(ProjectDetailsActivity.this);
                Logger.d(TAG, "Selected Path" + signatureImage);
                String uploaddata = uploadimage.uploadSignature(USERID, TOKEN, ProjectID, signatureImage);

                JSONObject jsonObject = new JSONObject(uploaddata);
                Logger.e(TAG, "Server response :::" + uploaddata);
                boolean success = jsonObject.getBoolean("success");
                String msg = jsonObject.getString("message");

                if (success) {
                    if (!jsonObject.isNull("data")) {
                        isSuccess = true;
                        jObj = jsonObject.getJSONObject("data");
                        JSONArray additionalContactsArray = jObj.getJSONArray("additional_contacts");
                        JSONArray additionalContactsNewArray = new JSONArray();
                        String projectResponsiblityName = "", projectResponsiblityId = "";
                        Logger.e(TAG, "Additional Contact Array before :" + additionalContactsArray);
                        if (additionalContactsArray.length() > 0) {
                            for (int i = 0; i < additionalContactsArray.length(); i++) {
                                JSONObject contactObject = additionalContactsArray.getJSONObject(i);

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
                                contactObject.put("project_responsiblity", projectResponsiblityName);
                                contactObject.put("project_responsiblity_id", projectResponsiblityId);

                                additionalContactsNewArray.put(contactObject);
                            }
                        }

                        Logger.e(TAG, "Additional Contact Array after :" + additionalContactsNewArray);
                        jObj.put("additional_contacts", additionalContactsNewArray);
                        Logger.e(TAG, "Project Object :" + jObj);

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
                if (jObj != null) {
                    Utils.projectRefreshFlag = true;
                    Utils.projectFilterRefreshFlag = true;
                    taskModel.clear();
                    setTaskData(jObj);
                }
            }
        }

    }

    /*private void generateReports(String userId, String token, String projectId, String mediaIdsArray) {

        if (Utils.checkInternetConnection(ProjectDetailsActivity.this)) {
            GeneratReportRequestTask generatReportRequestTask = new GeneratReportRequestTask(ProjectDetailsActivity.this);
            generatReportRequestTask.setAsyncCallListener(new AsyncCallListener() {

                @Override
                public void onResponseReceived(Object response) {
                }

                @Override
                public void onResponseReceived(JSONObject object) {
                    try {
                        boolean success = object.getBoolean("success");
                        String msg = object.getString("message");

                        if (success) {
                            mediaIdsJArray = new JSONArray();
                            mediaIDs = new StringBuffer();
                            JSONObject obj = object.getJSONObject("data");
                            String pdfUrl = obj.getString("pdf");

                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(pdfUrl));
                            startActivity(browserIntent);
                            ProjectDetails();
                        } else {
                            showMessage(msg);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e("ERR", e.getMessage());
                    }
                }

                @Override
                public void onErrorReceived(String error) {
                    Toast.makeText(ProjectDetailsActivity.this, "Try Again", Toast.LENGTH_SHORT).show();
                }
            });
            generatReportRequestTask.execute(userId, token, projectId, mediaIdsArray);
        } else {
            Toast.makeText(ProjectDetailsActivity.this, "Internet connection is not available", Toast.LENGTH_SHORT).show();
        }
    }*/

    public void generateReports() {
        pdialog = new ProgressDialog(this);
        pdialog.setMessage(getString(R.string.please_wait));
        pdialog.setCanceledOnTouchOutside(false);
        pdialog.show();

        JSONObject params = new JSONObject();
        try {
            params.put("user_id", USERID);
            params.put("session_token", TOKEN);
            params.put("project_id", ProjectID);
            if (Utils.validateString(String.valueOf(mediaIDs))) {
                params.put("media_ids", mediaIdsJArray);
            } else {
                mediaIdsJArray = new JSONArray();
                params.put("media_ids", mediaIdsJArray);
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

        Logger.e(TAG, "generateReports PARA---->" + String.valueOf(params));
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.POST, Utils.MAIN_URL + Utils.GENERATE_REPORT_API, params, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Logger.e(TAG, "generateReports RES---->" + String.valueOf(response));
                        pdialog.dismiss();
                        try {
                            boolean success = response.getBoolean("success");
                            String msg = response.getString("message");

                            if (success) {
                                mediaIdsJArray = new JSONArray();
                                mediaIDs = new StringBuffer();
                                JSONObject object = response.getJSONObject("data");
                                String pdfUrl = object.getString("pdf");

//                                Intent intent = new Intent(ProjectDetailsActivity.this, GenerateReportActivity.class);
//                                intent.putExtra(Utils.PDF_REPORT_URL, pdfUrl);
//                                intent.putExtra(Utils.PDF_FILE_NAME, projectId + ".pdf");
//                                startActivity(intent);

                                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(pdfUrl));
                                startActivity(browserIntent);
                                ProjectDetails();

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
                            Utils.showMessageDialog(ProjectDetailsActivity.this, getResources().getString(R.string.alert),
                                    "Please check your internet.");
                        } else {
                            pdialog.dismiss();

                        }
                    }
                });
        int socketTimeout = 30000;//30 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsObjRequest.setRetryPolicy(policy);
        requestQueue.add(jsObjRequest);
    }

    public void shareGenerateReportLink() {
        pdialog = new ProgressDialog(this);
        pdialog.setMessage(getString(R.string.please_wait));
        pdialog.setCanceledOnTouchOutside(false);
        pdialog.show();

        JSONObject params = new JSONObject();
        try {
            params.put("user_id", USERID);
            params.put("session_token", TOKEN);
            params.put("project_id", ProjectID);
            if (Utils.validateString(String.valueOf(mediaIDs))) {
                params.put("media_ids", mediaIdsJArray);
            } else {
                mediaIdsJArray = new JSONArray();
                params.put("media_ids", mediaIdsJArray);
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

        Logger.e(TAG, "generateReports PARA---->" + String.valueOf(params));
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.POST, Utils.MAIN_URL + Utils.GENERATE_REPORT_API, params, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Logger.e(TAG, "generateReports RES---->" + String.valueOf(response));
                        pdialog.dismiss();
                        try {
                            boolean success = response.getBoolean("success");
                            String msg = response.getString("message");
                            projectShareFlag = true;
                            if (success) {
                                mediaIdsJArray = new JSONArray();
                                mediaIDs = new StringBuffer();
                                JSONObject object = response.getJSONObject("data");
                                String pdfUrl = object.getString("pdf");

                                Intent sendIntent = new Intent();
                                sendIntent.setAction(Intent.ACTION_SEND);
                                sendIntent.setType("text/plain");
                                sendIntent.putExtra(Intent.EXTRA_TEXT, projectName + " " + pdfUrl);
                                startActivity(Intent.createChooser(sendIntent, "Share"));

                                ProjectDetails();

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
                            Utils.showMessageDialog(ProjectDetailsActivity.this, getResources().getString(R.string.alert),
                                    "Please check your internet.");
                        } else {
                            pdialog.dismiss();

                        }
                    }
                });
        int socketTimeout = 30000;//30 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsObjRequest.setRetryPolicy(policy);
        requestQueue.add(jsObjRequest);
    }

    public void ProjectDetails() {
//        pdialog = new ProgressDialog(this);
//        pdialog.setMessage(getString(R.string.please_wait));
//        pdialog.setCanceledOnTouchOutside(false);
//        pdialog.show();

        JSONObject params = new JSONObject();
        try {
            params.put("user_id", USERID);
            params.put("session_token", TOKEN);
            params.put("project_id", ProjectID);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Logger.e(TAG, "ProjectDetails PARA---->" + String.valueOf(params));
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.POST, Utils.MAIN_URL + Utils.PROJECT_DETAILS_API, params, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Logger.e(TAG, "ProjectDetails RES---->" + String.valueOf(response));
//                        pdialog.dismiss();
                        try {
                            boolean success = response.getBoolean("success");
                            String msg = response.getString("message");

                            if (success) {
                                JSONObject object = response.getJSONObject("data");
                                taskModel.clear();
                                Utils.storeString(mSharedPreferences, Utils.TASK_OBJECT, object.toString());
                                setTaskData(object);

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
//                            pdialog.dismiss();
                            Utils.showMessageDialog(ProjectDetailsActivity.this, getResources().getString(R.string.alert),
                                    "Please check your internet.");
                        } /*else {
                            pdialog.dismiss();
                            Utils.showMessageDialog(ProjectDetailsActivity.this, getResources().getString(R.string.alert),
                                    "500 error from server.");

                        }*/
                    }
                });
        int socketTimeout = 30000;//30 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsObjRequest.setRetryPolicy(policy);
        requestQueue.add(jsObjRequest);
    }


    TextView SaveBTN, cancelBTN;
    EditText edtTaskDueDate, edtTaskDueTime;

    public void openUpdateDueDateTimeView() {
        builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.activity_task_update_due_datetime_view, null);
        builder.setView(dialogView);

        SaveBTN = (TextView) dialogView.findViewById(R.id.SaveBTN);
        edtTaskDueDate = (EditText) dialogView.findViewById(R.id.edtTaskDueDate);
        edtTaskDueTime = (EditText) dialogView.findViewById(R.id.edtTaskDueTime);
        cancelBTN = (TextView) dialogView.findViewById(R.id.cancelBTN);
        ImageView imgClose = (ImageView) dialogView.findViewById(R.id.imgClose);

        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        edtTaskDueDate.setText(projectDueDate);
        edtTaskDueTime.setText(projectDueTime.substring(0, 2) + ":" + projectDueTime.substring(3, 5));

        edtTaskDueDate.setTypeface(Utils.getTypeFace(ProjectDetailsActivity.this));
        edtTaskDueTime.setTypeface(Utils.getTypeFace(ProjectDetailsActivity.this));

        edtTaskDueDate.setOnClickListener(this);
        edtTaskDueTime.setOnClickListener(this);
        cancelBTN.setOnClickListener(this);
        SaveBTN.setOnClickListener(this);

        dialog = builder.create();
        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.CENTER;
        window.setAttributes(wlp);

        dialog.show();
    }

    TextView addNoteBTN, resetBTN;
    EditText edtNotes;

    public void openAddNoteView() {
        builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.activity_task_add_note_view, null);
        builder.setView(dialogView);

        addNoteBTN = (TextView) dialogView.findViewById(R.id.addNoteBTN);
        edtNotes = (EditText) dialogView.findViewById(R.id.edtNotes);
        resetBTN = (TextView) dialogView.findViewById(R.id.resetBTN);
        ImageView imgClose = (ImageView) dialogView.findViewById(R.id.imgClose);

        if (NOTE_FLAG == false) {
            edtNotes.setText(note);
            edtNotes.setSelection(note.length());
        } else {
            NOTE_FLAG = true;
        }

        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        resetBTN.setOnClickListener(this);
        addNoteBTN.setOnClickListener(this);

        dialog = builder.create();
        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.CENTER;
        window.setAttributes(wlp);

        dialog.show();
    }

    TextView addProjectBTN, resetProjectBTN;
    EditText edtProjects;

    public void openAddProjectPhaseView() {
        builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.activity_task_add_project_view, null);
        builder.setView(dialogView);

        addProjectBTN = (TextView) dialogView.findViewById(R.id.addProjectBTN);
        edtProjects = (EditText) dialogView.findViewById(R.id.edtProjects);
        resetProjectBTN = (TextView) dialogView.findViewById(R.id.resetProjectBTN);
        ImageView imgClose = (ImageView) dialogView.findViewById(R.id.imgClose);

        if (projectPhaseFlag == false) {
            edtProjects.setText(project_des);
            edtProjects.setSelection(project_des.length());
        } else {
            projectPhaseFlag = true;
        }

        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        addProjectBTN.setOnClickListener(this);
        resetProjectBTN.setOnClickListener(this);

        dialog = builder.create();
        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.CENTER;
        window.setAttributes(wlp);
        dialog.show();
    }

    TextView submitSignatureBTN, resetSignatureBTN;
    InkView inkView;

    public void openSignatureView() {
        builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.activity_task_signature_view, null);
        builder.setView(dialogView);

        submitSignatureBTN = (TextView) dialogView.findViewById(R.id.submitSignatureBTN);
        resetSignatureBTN = (TextView) dialogView.findViewById(R.id.resetSignatureBTN);
        ImageView imgClose = (ImageView) dialogView.findViewById(R.id.imgClose);

        inkView = (InkView) dialogView.findViewById(R.id.ink);
        inkView.setColor(getResources().getColor(android.R.color.black));
        inkView.setMinStrokeWidth(1.5f);
        inkView.setMaxStrokeWidth(10f);

        submitSignatureBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap drawing = inkView.getBitmap(getResources().getColor(R.color.colorWhite));
                signatureImage = Utils.storeSignatureImage(drawing, ProjectID + ".jpg");
                inkView.setDrawingCacheEnabled(true);
                if (Utils.checkInternetConnection(ProjectDetailsActivity.this)) {
                    new uploadSignature().execute();
                } else {
                    try {
                        JSONObject object = new JSONObject(mSharedPreferences.getString(Utils.TASK_OBJECT, ""));
                        object.put("project_status", Utils.REPORT);
                        object.put("signature_image", signatureImage);
                        if (object.getString("project_operation").equalsIgnoreCase("A") &&
                                object.getString("is_sync").equalsIgnoreCase("N")) {
                            object.put("is_sync", "N");
                            object.put("project_operation", "A");
                        } else {
                            object.put("is_sync", "N");
                            object.put("project_operation", "U");
                        }

                        try {
                            mDbHelper.openDataBase();
                            mDbHelper.updateProjectDetails(ProjectID, projectName,
                                    object.getString("description"), object.getString("start_date"),
                                    object.getString("start_time"), object.getString("due_date"),
                                    object.getString("due_time"), object.getString("priority"),
                                    Utils.REPORT, object.getString("street"),
                                    object.getString("city"), object.getString("state"),
                                    object.getString("country"), object.getString("pincode"),
                                    object.getString("lattitude"), object.getString("longitude"),
                                    object.getString("assigned_by"), object.getString("assigned_by_phone"),
                                    object.getString("assigned_by_email"), object.getString("is_sync"),
                                    ProjectID + ".jpg", object.getString("project_operation"),
                                    object.getString("project_start_time"), object.getString("created_date"),
                                    object.getString("remind_hours"));
                            mDbHelper.close();

                        } catch (SQLException e) {
                            e.printStackTrace();

                        }

                        taskModel.clear();
                        Utils.storeString(mSharedPreferences, Utils.TASK_OBJECT, object.toString());
                        setTaskData(object);


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
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
    }

    public Intent getPickImageChooserGalleryIntent() {

        // Uri outputFileUri = getCaptureImageOutputUri();
        List<Intent> allIntents = new ArrayList<>();
        PackageManager packageManager = getPackageManager();

        // collect all gallery intents
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        List<ResolveInfo> listGallery = packageManager.queryIntentActivities(galleryIntent, 0);
        for (ResolveInfo res : listGallery) {
            Intent intent = new Intent(galleryIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(res.activityInfo.packageName);
            allIntents.add(intent);
        }

        // the main intent is the last in the  list so pickup the useless one
        Intent mainIntent = allIntents.get(allIntents.size() - 1);
        for (Intent intent : allIntents) {
            if (intent.getComponent().getClassName().equals("com.android.documentsui.DocumentsActivity")) {
                mainIntent = intent;
                break;
            }
        }
        allIntents.remove(mainIntent);
        Intent chooserIntent = Intent.createChooser(mainIntent, "Select source");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, allIntents.toArray(new Parcelable[allIntents.size()]));

        return chooserIntent;
    }

    public void takeMediaOptions() {
        final CharSequence[] options = {"Camera & Video", "Gallery", "Audio Recorder", "Document", "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setTitle("Option");
        builder.setItems(options, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Camera & Video")) {
                    selectedImagePath = "";
                    fileMediaType = "";
                    FlagGallery = false;
                    runTimePermission = new RunTimePermission(ProjectDetailsActivity.this);
                    runTimePermission.requestPermission(new String[]{Manifest.permission.CAMERA,
                            Manifest.permission.RECORD_AUDIO,
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    }, new RunTimePermission.RunTimePermissionListener() {

                        @Override
                        public void permissionGranted() {
                            // First we need to check availability of play services
                            Intent customeCameraIntent = new Intent(ProjectDetailsActivity.this, CustomCameraActivity.class);
                            startActivityForResult(customeCameraIntent, 1);

//                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                            File f = new File(android.os.Environment.getExternalStorageDirectory(), "temp1.jpg");
//                            mCropImageUri = Uri.fromFile(f);
//                            intent.putExtra(MediaStore.EXTRA_OUTPUT, mCropImageUri);
//                            startActivityForResult(intent, CAMERA_CODE);

                        }

                        @Override
                        public void permissionDenied() {

                            finish();
                        }
                    });

                } else if (options[item].equals("Gallery")) {
                    selectedImagePath = "";
                    fileMediaType = "";
                    FlagGallery = true;
                    runTimePermission = new RunTimePermission(ProjectDetailsActivity.this);
                    runTimePermission.requestPermission(new String[]{
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    }, new RunTimePermission.RunTimePermissionListener() {

                        @Override
                        public void permissionGranted() {
//                           startActivityForResult(getPickImageChooserGalleryIntent(), 200);

//                            Intent intent = new Intent();
//                            intent.setAction(Intent.ACTION_GET_CONTENT);
//                            startActivityForResult(Intent.createChooser(intent, "Select Picture"), GALLERY_CODE);

                            Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            startActivityForResult(Intent.createChooser(i, "Select Picture"), GALLERY_CODE);

                            /*Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            startActivityForResult(Intent.createChooser(intent, "Select Picture"), LOAD_IMAGE_GALLARY);*/
                        }

                        @Override
                        public void permissionDenied() {

                            finish();
                        }
                    });
                } else if (options[item].equals("Audio Recorder")) {
                    selectedImagePath = "";
                    fileMediaType = "";
                    FlagGallery = false;
                    runTimePermission = new RunTimePermission(ProjectDetailsActivity.this);
                    runTimePermission.requestPermission(new String[]{
                            Manifest.permission.RECORD_AUDIO,
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    }, new RunTimePermission.RunTimePermissionListener() {

                        @Override
                        public void permissionGranted() {

                            selectedImagePath = Environment.getExternalStorageDirectory().getPath() + "/recorded_audio.wav";

                            AndroidAudioRecorder.with(ProjectDetailsActivity.this)
                                    // Required
                                    .setFilePath(selectedImagePath)
                                    .setColor(ContextCompat.getColor(ProjectDetailsActivity.this, R.color.colorPrimaryDark))
                                    .setRequestCode(REQUEST_RECORD_AUDIO)

                                    // Optional
                                    .setSource(AudioSource.MIC)
                                    .setChannel(AudioChannel.STEREO)
                                    .setSampleRate(AudioSampleRate.HZ_11025)
                                    .setAutoStart(false)
                                    .setKeepDisplayOn(true)

                                    // Start recording
                                    .record();
                        }

                        @Override
                        public void permissionDenied() {
                            finish();
                        }
                    });

                } else if (options[item].equals("Document")) {
                    selectedImagePath = "";
                    fileMediaType = "";
                    FlagGallery = false;
                    runTimePermission = new RunTimePermission(ProjectDetailsActivity.this);
                    runTimePermission.requestPermission(new String[]{
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    }, new RunTimePermission.RunTimePermissionListener() {

                        @Override
                        public void permissionGranted() {

                            showFileChooser();
                        }

                        @Override
                        public void permissionDenied() {
                            finish();
                        }
                    });

//                    checkPermissionsAndOpenFilePicker();
//                    showFileChooser();

                } else if (options[item].equals("Cancel")) {
                    selectedImagePath = "";
                    fileMediaType = "";
                    FlagGallery = false;
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void checkPermissionsAndOpenFilePicker() {
        String permission = Manifest.permission.READ_EXTERNAL_STORAGE;

        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                showError();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{permission}, PERMISSIONS_REQUEST_CODE);
            }
        } else {
            openFilePicker();
        }
    }

    private void showError() {
        Toast.makeText(this, "Allow external storage reading", Toast.LENGTH_SHORT).show();
    }

    private void openFilePicker() {
        new MaterialFilePicker()
                .withActivity(this)
                .withRequestCode(FILE_PICKER_REQUEST_CODE)
                .withHiddenFiles(true)
                .withTitle("Choose File")
                .start();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (mCropImageUri != null && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startCropImageActivity(mCropImageUri);
        } else if (runTimePermission != null) {
            runTimePermission.onRequestPermissionsResult(requestCode, permissions, grantResults);
        } /*else {
            Toast.makeText(this, "Cancelling, required permissions are not granted", Toast.LENGTH_LONG).show();
        }*/
    }

    /**
     * Start crop image activity for the given image.
     */
    private void startCropImageActivity(Uri imageUri) {
        CropImage.activity(imageUri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(this);
    }


    private File outPutFile = null;

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            String result = data.getStringExtra(Utils.TASK_OBJECT);
            JSONObject taskObject = null;
            switch (data.getIntExtra(Utils.SWITCH, 101)) {
                case 0:
                    try {
                        taskModel.clear();
                        taskObject = new JSONObject(result.toString());
                        Logger.e(TAG, "Additional projectObject :::" + taskObject);
                        setTaskData(taskObject);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    break;

                case 1:
                    String path = data.getStringExtra(Utils.MEDIA_CUSTOM_PATH);
                    if (data.getStringExtra(Utils.MEDIA_CUSTOM_WHO).equalsIgnoreCase("Image")) {
                        Logger.e(TAG, "Image :" + path);
                        selectedImagePath = path;
                        outPutFile = new File(selectedImagePath);
                        mCropImageUri = Uri.fromFile(outPutFile);
//                        CropingIMG();
                        openAlert(selectedImagePath);
                    } else {
                        Logger.e(TAG, "Video :" + path);
                        selectedImagePath = path;

                        /*String outPath = Environment.getExternalStorageDirectory() + "/Media/Video/";
                        boolean isCompressSuccess = MediaController.getInstance().convertVideo(selectedImagePath, outPath);
                        if (isCompressSuccess) {

                        }*/
                        final String thumbVideoImage = data.getStringExtra(Utils.MEDIA_CUSTOM_THUMB);
                        AlertDialog.Builder newDialog = new AlertDialog.Builder(this);
                        newDialog.setTitle(getString(R.string.alert));
                        newDialog.setMessage("Upload this video to this project?");
                        newDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                dialog.dismiss();

                                if (Utils.checkInternetConnection(ProjectDetailsActivity.this)) {
//                                    if (curLatitude != 0 && curLongitude != 0) {
                                    File fdelete = new File(thumbVideoImage);
                                    if (fdelete.exists()) {
                                        if (fdelete.delete()) {
                                            Logger.d(TAG, "file Deleted :" + thumbVideoImage);
                                        } else {
                                            Logger.d(TAG, "file not Deleted :" + thumbVideoImage);
                                        }
                                    }
                                    new uploadMedia().execute();
//                                        uploadVideo();
//                                    } else {
//                                        showMessage("Please Check Your Location.");
//                                    }
                                } else {
                                    try {

                                        String tempId = Utils.getRandomAlphaNumeric(6);
                                        String createdDate = Utils.getCurrentDateAndTime();

                                        GPSTracker gpsTracker = Utils.getCurrentLocation(ProjectDetailsActivity.this);
                                        if (gpsTracker != null) {
                                            curLatitude = gpsTracker.getLatitude();
                                            curLongitude = gpsTracker.getLongitude();
                                        } else {
                                            curLatitude = 0;
                                            curLongitude = 0;
                                        }

                                        ArrayList<MediaModel> mediaModelArrayList = new ArrayList<>();
                                        try {
                                            mDbHelper.openDataBase();
                                            mDbHelper.insertProjectMedia(mDbHelper.myDataBase, tempId, ProjectID, "video", "", "",
                                                    "", String.valueOf(curLatitude), String.valueOf(curLongitude), "", "", "", "", "", createdDate, "N", "A", "");
                                            mediaModelArrayList = mDbHelper.getProjectMediaDetails();
                                            mDbHelper.close();
                                        } catch (SQLException e) {
                                            e.printStackTrace();
                                        }

                                        if (mediaModelArrayList.size() > 0) {
                                            JSONObject object = new JSONObject(mSharedPreferences.getString(Utils.TASK_OBJECT, ""));
                                            JSONArray mediaArray = object.getJSONArray("project_media");
                                            JSONArray mediaNewArray = new JSONArray();
                                            JSONObject mediaObject;
                                            Logger.e(TAG, "Media Array update before : " + mediaArray);
                                            if (mediaArray.length() > 0) {
                                                for (int i = 0; i < mediaArray.length(); i++) {
                                                    mediaObject = mediaArray.getJSONObject(i);
                                                    mediaNewArray.put(mediaObject);
                                                }
                                            }
                                            for (int j = 0; j < mediaModelArrayList.size(); j++) {
                                                if (tempId.equalsIgnoreCase(mediaModelArrayList.get(j).getMediaId())) {
                                                    mediaObject = new JSONObject();
                                                    mediaObject.put("id", mediaModelArrayList.get(j).getMediaId());
                                                    mediaObject.put("media_type", mediaModelArrayList.get(j).getMediaType());
                                                    mediaObject.put("media", mediaModelArrayList.get(j).getMedia());
                                                    mediaObject.put("video_thumb_image", mediaModelArrayList.get(j).getVideoThumbImage());
                                                    mediaObject.put("media_description", mediaModelArrayList.get(j).getMediaDescription());
                                                    mediaObject.put("latitude", mediaModelArrayList.get(j).getLatitude());
                                                    mediaObject.put("longitude", mediaModelArrayList.get(j).getLongitude());
                                                    mediaObject.put("street", mediaModelArrayList.get(j).getStreet());
                                                    mediaObject.put("city", mediaModelArrayList.get(j).getCity());
                                                    mediaObject.put("state", mediaModelArrayList.get(j).getState());
                                                    mediaObject.put("country", mediaModelArrayList.get(j).getCountry());
                                                    mediaObject.put("pincode", mediaModelArrayList.get(j).getPincode());
                                                    mediaObject.put("created_date", mediaModelArrayList.get(j).getCreated_date());
                                                    mediaObject.put("is_sync", mediaModelArrayList.get(j).getIs_sync());
                                                    mediaObject.put("media_operation", mediaModelArrayList.get(j).getMedia_status());
                                                    mediaObject.put("doc_thumb_image", mediaModelArrayList.get(j).getDocThumbImage());
                                                    mediaNewArray.put(mediaObject);
                                                }
                                            }

                                            Logger.e(TAG, "Media Array update after : " + mediaNewArray);
                                            object.put("project_media", mediaNewArray);
                                            if (object.getString("project_operation").equalsIgnoreCase("A") &&
                                                    object.getString("is_sync").equalsIgnoreCase("N")) {
                                                object.put("is_sync", "N");
                                            } else {
                                                object.put("is_sync", "N");
                                                object.put("project_operation", "U");
                                            }
                                            Logger.e(TAG, "Project Object :" + object);
                                            taskModel.clear();

                                            Utils.storeString(mSharedPreferences, Utils.TASK_OBJECT, object.toString());
                                            setTaskData(object);
                                        }

                                        //Video
                                        String outputPath = Environment.getExternalStorageDirectory() + "/Media/Video/";
                                        Utils.moveFile(selectedImagePath, outputPath, tempId + ".mp4");

                                        //VideoThumbImage
                                        String outputPath2 = Environment.getExternalStorageDirectory() + "/Media/VideoThumb/";
                                        Utils.moveFile(thumbVideoImage, outputPath2, tempId + ".jpg");

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                }

                            }
                        });
                        newDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                                if (Utils.validateString(selectedImagePath)) {
                                    File fdelete = new File(selectedImagePath);
                                    if (fdelete.exists()) {
                                        if (fdelete.delete()) {
                                            Logger.d(TAG, "file Deleted :" + selectedImagePath);
                                        } else {
                                            Logger.d(TAG, "file not Deleted :" + selectedImagePath);
                                        }
                                    }
                                }

                                if (Utils.validateString(thumbVideoImage)) {
                                    File fdelete = new File(thumbVideoImage);
                                    if (fdelete.exists()) {
                                        if (fdelete.delete()) {
                                            Logger.d(TAG, "file Deleted :" + thumbVideoImage);
                                        } else {
                                            Logger.d(TAG, "file not Deleted :" + thumbVideoImage);
                                        }
                                    }
                                }
                            }
                        });
                        newDialog.show();
                    }

                    break;

                case 2:
                    try {
                        taskModel.clear();
                        taskObject = new JSONObject(result.toString());
                        Logger.e(TAG, "Media projectObject :::" + taskObject);
                        setTaskData(taskObject);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    break;

                case 3:
                    try {
                        taskModel.clear();

                        adapter = new ProjectDetailAdapter(taskModel, ProjectDetailsActivity.this, updateDueDateTimeClick, addNoteClick, editNoteClick, deleteNoteClick, addProjectClick,
                                addAdditionalContactClick, editContactClick, deleteContactClick, onEditProjectPhaseListner, onDeleteProjectPhaseListner,
                                addMediaClick, mediaItemClick, mediaItemLongClick, updateStatusClick, generateReportClick, phoneClick, isTablet);
                        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                        recyclerView.setLayoutManager(mLayoutManager);
                        recyclerView.setAdapter(adapter);

                        taskObject = new JSONObject(result.toString());
                        Logger.e(TAG, "Edit projectObject :::" + taskObject);
                        setTaskData(taskObject);
                        Utils.storeString(mSharedPreferences, Utils.TASK_OBJECT, taskObject.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;

                default:
                    break;
            }

        } else if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Uri imageUri = CropImage.getPickImageResultUri(this, data);

            // For API >= 23 we need to check specifically that we have permissions to read external storage.
            boolean requirePermissions = false;
            if (CropImage.isReadExternalStoragePermissionsRequired(this, imageUri)) {
                // request permissions and handle the result in onRequestPermissionsResult()
                requirePermissions = true;
                mCropImageUri = imageUri;
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
            } else {
                // no permissions required or already grunted, can start crop image activity
                startCropImageActivity(imageUri);
            }
        } else if (requestCode == GALLERY_CODE && resultCode == RESULT_OK && null != data) {
            selectedImagePath = "";
            mCropImageUri = data.getData();
            try {
                String[] filePath = {MediaStore.Images.Media.DATA};
                Cursor c = getContentResolver().query(mCropImageUri, filePath, null, null, null);
                c.moveToFirst();
                int columnIndex = c.getColumnIndex(filePath[0]);
                selectedImagePath = c.getString(columnIndex);
                c.close();
                Logger.e(TAG, "Gallery Image URI :--" + selectedImagePath + "");
                if (!Utils.validateString(selectedImagePath)) {
                    FlagGallery = false;
                    selectedImagePath = getDownloadedFile(mCropImageUri);
                }

                outPutFile = new File(selectedImagePath);
                System.out.println("Gallery Image URI : " + mCropImageUri);
                //                CropingIMG();
                openAlert(selectedImagePath);

            } catch (Exception e) {
                Log.v("ERROR", e.toString());
                Toast.makeText(ProjectDetailsActivity.this, "This image is deleted from your mobile so please select another image.", Toast.LENGTH_SHORT).show();
            }

        } else if (requestCode == REQUEST_RECORD_AUDIO && resultCode == RESULT_OK) {

//            Toast.makeText(this, "Audio recorded successfully!", Toast.LENGTH_SHORT).show();

            try {
                AlertDialog.Builder newDialog = new AlertDialog.Builder(this);
                newDialog.setTitle(getString(R.string.alert));
                newDialog.setMessage("Upload this audio to this project?");
                newDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();
                        if (Utils.checkInternetConnection(ProjectDetailsActivity.this)) {
                            new uploadMedia().execute();

                        } else {
                            try {
                                String media_type = "";
                                if (selectedImagePath.contains(".mp4")) {
                                    media_type = "video";
                                } else if (selectedImagePath.contains(".wav")) {
                                    media_type = "audio";
                                } else {
                                    media_type = "image";
                                }

                                String tempId = Utils.getRandomAlphaNumeric(6);
                                String createdDate = Utils.getCurrentDateAndTime();

                                GPSTracker gpsTracker = Utils.getCurrentLocation(ProjectDetailsActivity.this);
                                if (gpsTracker != null) {
                                    curLatitude = gpsTracker.getLatitude();
                                    curLongitude = gpsTracker.getLongitude();
                                } else {
                                    curLatitude = 0;
                                    curLongitude = 0;
                                }

                                ArrayList<MediaModel> mediaModelArrayList = new ArrayList<>();
                                try {
                                    mDbHelper.openDataBase();
                                    mDbHelper.insertProjectMedia(mDbHelper.myDataBase, tempId, ProjectID, media_type, "", "",
                                            "", String.valueOf(curLatitude), String.valueOf(curLongitude), "", "", "", "", "", createdDate, "N", "A", "");
                                    mediaModelArrayList = mDbHelper.getProjectMediaDetails();
                                    mDbHelper.close();
                                } catch (SQLException e) {
                                    e.printStackTrace();
                                }

                                if (mediaModelArrayList.size() > 0) {
                                    JSONObject object = new JSONObject(mSharedPreferences.getString(Utils.TASK_OBJECT, ""));
                                    JSONArray mediaArray = object.getJSONArray("project_media");
                                    JSONArray mediaNewArray = new JSONArray();
                                    JSONObject mediaObject;
                                    Logger.e(TAG, "Media Array update before : " + mediaArray);
                                    if (mediaArray.length() > 0) {
                                        for (int i = 0; i < mediaArray.length(); i++) {
                                            mediaObject = mediaArray.getJSONObject(i);
                                            mediaNewArray.put(mediaObject);
                                        }
                                    }
                                    for (int j = 0; j < mediaModelArrayList.size(); j++) {
                                        if (tempId.equalsIgnoreCase(mediaModelArrayList.get(j).getMediaId())) {
                                            mediaObject = new JSONObject();
                                            mediaObject.put("id", mediaModelArrayList.get(j).getMediaId());
                                            mediaObject.put("media_type", mediaModelArrayList.get(j).getMediaType());
                                            mediaObject.put("media", mediaModelArrayList.get(j).getMedia());
                                            mediaObject.put("video_thumb_image", mediaModelArrayList.get(j).getVideoThumbImage());
                                            mediaObject.put("media_description", mediaModelArrayList.get(j).getMediaDescription());
                                            mediaObject.put("latitude", mediaModelArrayList.get(j).getLatitude());
                                            mediaObject.put("longitude", mediaModelArrayList.get(j).getLongitude());
                                            mediaObject.put("street", mediaModelArrayList.get(j).getStreet());
                                            mediaObject.put("city", mediaModelArrayList.get(j).getCity());
                                            mediaObject.put("state", mediaModelArrayList.get(j).getState());
                                            mediaObject.put("country", mediaModelArrayList.get(j).getCountry());
                                            mediaObject.put("pincode", mediaModelArrayList.get(j).getPincode());
                                            mediaObject.put("created_date", mediaModelArrayList.get(j).getCreated_date());
                                            mediaObject.put("is_sync", mediaModelArrayList.get(j).getIs_sync());
                                            mediaObject.put("media_operation", mediaModelArrayList.get(j).getMedia_status());
                                            mediaObject.put("doc_thumb_image", mediaModelArrayList.get(j).getDocThumbImage());
                                            mediaNewArray.put(mediaObject);
                                        }
                                    }

                                    Logger.e(TAG, "Media Array update after : " + mediaNewArray);
                                    object.put("project_media", mediaNewArray);
                                    if (object.getString("project_operation").equalsIgnoreCase("A") &&
                                            object.getString("is_sync").equalsIgnoreCase("N")) {
                                        object.put("is_sync", "N");
                                    } else {
                                        object.put("is_sync", "N");
                                        object.put("project_operation", "U");
                                    }
                                    Logger.e(TAG, "Project Object :" + object);
                                    taskModel.clear();

                                    Utils.storeString(mSharedPreferences, Utils.TASK_OBJECT, object.toString());
                                    setTaskData(object);
                                }

                                if (media_type.equalsIgnoreCase("audio")) {
                                    String outputPath = Environment.getExternalStorageDirectory() + "/Media/Audio/";
                                    Utils.moveFile(selectedImagePath, outputPath, tempId + ".wav");
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                    }
                });
                newDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        if (Utils.validateString(selectedImagePath)) {
                            File fdelete = new File(selectedImagePath);
                            if (fdelete.exists()) {
                                if (fdelete.delete()) {
                                    Logger.d(TAG, "file Deleted :" + selectedImagePath);
                                } else {
                                    Logger.d(TAG, "file not Deleted :" + selectedImagePath);
                                }
                            }
                        }
                    }
                });
                newDialog.show();
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else if (requestCode == CROPING_CODE) {
            try {
                if (outPutFile.exists()) {
                    AlertDialog.Builder newDialog = new AlertDialog.Builder(this);
                    newDialog.setTitle(getString(R.string.alert));
                    newDialog.setMessage("Upload this image to this project?");
                    newDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                            dialog.dismiss();
                            if (Utils.checkInternetConnection(ProjectDetailsActivity.this)) {
                                new uploadMedia().execute();
                            } else {
                                try {
                                    String media_type = "";
                                    if (!selectedImagePath.contains(".mp4")) {
                                        media_type = "image";
                                    } else {
                                        media_type = "video";
                                    }

                                    String tempId = Utils.getRandomAlphaNumeric(6);
                                    String createdDate = Utils.getCurrentDateAndTime();

                                    GPSTracker gpsTracker = Utils.getCurrentLocation(ProjectDetailsActivity.this);
                                    if (gpsTracker != null) {
                                        curLatitude = gpsTracker.getLatitude();
                                        curLongitude = gpsTracker.getLongitude();
                                    } else {
                                        curLatitude = 0;
                                        curLongitude = 0;
                                    }

                                    ArrayList<MediaModel> mediaModelArrayList = new ArrayList<>();
                                    try {
                                        mDbHelper.openDataBase();
                                        mDbHelper.insertProjectMedia(mDbHelper.myDataBase, tempId, ProjectID, media_type, "", "",
                                                "", String.valueOf(curLatitude), String.valueOf(curLongitude), "", "", "", "", "", createdDate, "N", "A", "");
                                        mediaModelArrayList = mDbHelper.getProjectMediaDetails();
                                        mDbHelper.close();
                                    } catch (SQLException e) {
                                        e.printStackTrace();
                                    }

                                    if (mediaModelArrayList.size() > 0) {
                                        JSONObject object = new JSONObject(mSharedPreferences.getString(Utils.TASK_OBJECT, ""));
                                        JSONArray mediaArray = object.getJSONArray("project_media");
                                        JSONArray mediaNewArray = new JSONArray();
                                        JSONObject mediaObject;
                                        Logger.e(TAG, "Media Array update before : " + mediaArray);
                                        if (mediaArray.length() > 0) {
                                            for (int i = 0; i < mediaArray.length(); i++) {
                                                mediaObject = mediaArray.getJSONObject(i);
                                                mediaNewArray.put(mediaObject);
                                            }
                                        }
                                        for (int j = 0; j < mediaModelArrayList.size(); j++) {
                                            if (tempId.equalsIgnoreCase(mediaModelArrayList.get(j).getMediaId())) {
                                                mediaObject = new JSONObject();
                                                mediaObject.put("id", mediaModelArrayList.get(j).getMediaId());
                                                mediaObject.put("media_type", mediaModelArrayList.get(j).getMediaType());
                                                mediaObject.put("media", mediaModelArrayList.get(j).getMedia());
                                                mediaObject.put("video_thumb_image", mediaModelArrayList.get(j).getVideoThumbImage());
                                                mediaObject.put("media_description", mediaModelArrayList.get(j).getMediaDescription());
                                                mediaObject.put("latitude", mediaModelArrayList.get(j).getLatitude());
                                                mediaObject.put("longitude", mediaModelArrayList.get(j).getLongitude());
                                                mediaObject.put("street", mediaModelArrayList.get(j).getStreet());
                                                mediaObject.put("city", mediaModelArrayList.get(j).getCity());
                                                mediaObject.put("state", mediaModelArrayList.get(j).getState());
                                                mediaObject.put("country", mediaModelArrayList.get(j).getCountry());
                                                mediaObject.put("pincode", mediaModelArrayList.get(j).getPincode());
                                                mediaObject.put("created_date", mediaModelArrayList.get(j).getCreated_date());
                                                mediaObject.put("is_sync", mediaModelArrayList.get(j).getIs_sync());
                                                mediaObject.put("media_operation", mediaModelArrayList.get(j).getMedia_status());
                                                mediaObject.put("doc_thumb_image", mediaModelArrayList.get(j).getDocThumbImage());
                                                mediaNewArray.put(mediaObject);
                                            }
                                        }

                                        Logger.e(TAG, "Media Array update after : " + mediaNewArray);
                                        object.put("project_media", mediaNewArray);
                                        if (object.getString("project_operation").equalsIgnoreCase("A") &&
                                                object.getString("is_sync").equalsIgnoreCase("N")) {
                                            object.put("is_sync", "N");
                                        } else {
                                            object.put("is_sync", "N");
                                            object.put("project_operation", "U");
                                        }
                                        Logger.e(TAG, "Project Object :" + object);
                                        taskModel.clear();

                                        Utils.storeString(mSharedPreferences, Utils.TASK_OBJECT, object.toString());
                                        setTaskData(object);
                                    }


                                    if (media_type.equalsIgnoreCase("image")) {
                                        if (!FlagGallery) {
                                            String outputPath = Environment.getExternalStorageDirectory() + "/Media/Image/";
                                            Utils.moveFile(selectedImagePath, outputPath, tempId + ".jpg");
                                        } else {
                                            String outputPath = Environment.getExternalStorageDirectory() + "/Media/Image/";
                                            Utils.copyFile(selectedImagePath, outputPath, tempId + ".jpg");
                                        }
                                    } else {
                                        String outputPath = Environment.getExternalStorageDirectory() + "/Media/Video/";
                                        Utils.moveFile(selectedImagePath, outputPath, tempId + ".mp4");
                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                        }
                    });
                    newDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            if (!FlagGallery) {
                                if (Utils.validateString(selectedImagePath)) {
                                    File fdelete = new File(selectedImagePath);
                                    if (fdelete.exists()) {
                                        if (fdelete.delete()) {
                                            Logger.d(TAG, "file Deleted :" + selectedImagePath);
                                        } else {
                                            Logger.d(TAG, "file not Deleted :" + selectedImagePath);
                                        }
                                    }
                                }
                            }
                        }
                    });
                    newDialog.show();
                } else {
                    Toast.makeText(getApplicationContext(), "Error while save image", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (resultCode == RESULT_CANCELED) {
            //Write your code if there's no result
        } else if (requestCode == PICK_FILE_REQUEST && resultCode == RESULT_OK) {

            try {
                AlertDialog.Builder newDialog = new AlertDialog.Builder(this);
                newDialog.setTitle(getString(R.string.alert));
                newDialog.setMessage("Upload this document to this project?");
                newDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();
                        Uri selectedFileURI = data.getData();
                        String mimeType = getMimeType(selectedFileURI);
                        Logger.e(TAG, "Mime Type : " + mimeType);
                        String mediaType = "";
                        if (mimeType.equalsIgnoreCase("application/msword")) {
                            mediaType = "doc";
                            getDocumentUri(selectedFileURI, mediaType);
                            Logger.e(TAG, "Mime Type Available with " + mediaType);
                        } else if (mimeType.equalsIgnoreCase("application/vnd.openxmlformats-officedocument.wordprocessingml.document")) {
                            mediaType = "docx";
                            getDocumentUri(selectedFileURI, mediaType);
                            Logger.e(TAG, "Mime Type Available with " + mediaType);
                        } else if (mimeType.equalsIgnoreCase("text/plain")) {
                            mediaType = "txt";
                            getDocumentUri(selectedFileURI, mediaType);
                            Logger.e(TAG, "Mime Type Available with " + mediaType);
                        } else if (mimeType.equalsIgnoreCase("application/pdf")) {
                            mediaType = "pdf";
                            getDocumentUri(selectedFileURI, mediaType);
                            Logger.e(TAG, "Mime Type Available with " + mediaType);
                        } else {
                            Utils.showMessageDialog(ProjectDetailsActivity.this, getString(R.string.alert), "This format is not supported for document.");
                        }

                    }
                });
                newDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        selectedImagePath = "";
                        fileMediaType = "";
                    }
                });
                newDialog.show();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        // handle result of CropImageActivity
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Logger.e(TAG, "Croup Image URI " + result.getUri().toString());
                selectedImagePath = result.getUri().toString();
                selectedImagePath = selectedImagePath.replace("file:///", "");
                AlertDialog.Builder newDialog = new AlertDialog.Builder(this);
                newDialog.setTitle(R.string.alert);
                newDialog.setMessage("Upload this image to this project?");
                newDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();
//                        if (curLatitude != 0 && curLongitude != 0) {
                        new uploadMedia().execute();
//                        } else {
//                            showMessage("Please Check Your Location.");
//                        }
                    }
                });
                newDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                newDialog.show();

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Toast.makeText(this, "Cropping failed: " + result.getError(), Toast.LENGTH_LONG).show();
            }
        }

        /*if (requestCode == FILE_PICKER_REQUEST_CODE && resultCode == RESULT_OK) {
            final String path = data.getStringExtra(FilePickerActivity.RESULT_FILE_PATH);

            if (path != null && Utils.validateString(path)) {
                Logger.d(TAG, "File Path : " + path);
                if (path.contains(".txt") || path.contains(".doc")
                        || path.contains(".docx") || path.contains(".pdf")) {

                    try {
                        AlertDialog.Builder newDialog = new AlertDialog.Builder(this);
                        newDialog.setTitle(getString(R.string.alert));
                        newDialog.setMessage("Upload this document to this project?");
                        newDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();

                                if (path.contains(".txt")) {
                                    fileMediaType = "txt";
                                    getFilePath(fileMediaType, path);
                                } else if (path.contains(".doc")) {
                                    fileMediaType = "doc";
                                    getFilePath(fileMediaType, path);
                                } else if (path.contains(".docx")) {
                                    fileMediaType = "docx";
                                    getFilePath(fileMediaType, path);
                                } else if (path.contains(".pdf")) {
                                    fileMediaType = "pdf";
                                    getFilePath(fileMediaType, path);
                                } else {
                                    Utils.showMessageDialog(ProjectDetailsActivity.this, getString(R.string.alert), "This format is not supported for document.");
                                }

                            }
                        });
                        newDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                                selectedImagePath = "";
                                fileMediaType = "";
                            }
                        });
                        newDialog.show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else {
                    Utils.showMessageDialog(ProjectDetailsActivity.this, getString(R.string.alert), "This format is not supported for document.");
                }
            } else {

            }
        }*/

        switch (requestCode) {
            case REQUEST_LOCATION:
                switch (resultCode) {
                    case Activity.RESULT_CANCELED:
                        break;
                    case Activity.RESULT_OK:
                        startLocationUpdates();
                        break;
                }
                break;
        }

    }

    private void copyFileUsingIO(File sourceFile, File destinationFile) throws IOException {
        InputStream inputStreamData = null;
        OutputStream outputStreamData = null;

        try {
            inputStreamData = new BufferedInputStream(new FileInputStream(sourceFile));
            outputStreamData = new BufferedOutputStream(new FileOutputStream(destinationFile));
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStreamData.read(buffer)) > 0) {
                outputStreamData.write(buffer, 0, length);
            }

        } finally {
            inputStreamData.close();
            outputStreamData.close();
        }
    }

    private void showFileChooser() {
        String[] mimeTypes =
                {"application/msword", "application/vnd.openxmlformats-officedocument.wordprocessingml.document", // .doc & .docx
                        "text/plain",
                        "application/pdf"};

        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            intent.setType(mimeTypes.length == 1 ? mimeTypes[0] : "*/*");
            if (mimeTypes.length > 0) {
                intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
            }
        } else {
            String mimeTypesStr = "";
            for (String mimeType : mimeTypes) {
                mimeTypesStr += mimeType + "|";
            }
            intent.setType(mimeTypesStr.substring(0, mimeTypesStr.length() - 1));
        }

//        final Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
//        intent.setType("*/*");
//        intent.addCategory(Intent.CATEGORY_OPENABLE);

        try {
            startActivityForResult(Intent.createChooser(intent, "ChooseFile"), PICK_FILE_REQUEST);
        } catch (ActivityNotFoundException ex) {
            Toast.makeText(getApplicationContext(), "Please install a File Manager.", Toast.LENGTH_SHORT).show();
        }
    }

    public String getMimeType(Uri uri) {
        String mimeType = null;
        if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
            ContentResolver cr = getApplicationContext().getContentResolver();
            mimeType = cr.getType(uri);
        } else {
            String fileExtension = MimeTypeMap.getFileExtensionFromUrl(uri
                    .toString());
            mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(
                    fileExtension.toLowerCase());
        }
        return mimeType;
    }

    public void getFilePath(String mediaType, String path) {
        try {
            fileMediaType = mediaType;
            selectedImagePath = path;
            Logger.e(TAG, "Selected Path ::" + selectedImagePath);

            if (Utils.validateString(selectedImagePath)) {
                if (Utils.checkInternetConnection(ProjectDetailsActivity.this)) {
                    new uploadMedia().execute();
                } else {
                    try {

                        String tempId = Utils.getRandomAlphaNumeric(6);
                        String createdDate = Utils.getCurrentDateAndTime();

                        GPSTracker gpsTracker = Utils.getCurrentLocation(ProjectDetailsActivity.this);
                        if (gpsTracker != null) {
                            curLatitude = gpsTracker.getLatitude();
                            curLongitude = gpsTracker.getLongitude();
                        } else {
                            curLatitude = 0;
                            curLongitude = 0;
                        }

                        ArrayList<MediaModel> mediaModelArrayList = new ArrayList<>();
                        try {
                            mDbHelper.openDataBase();
                            mDbHelper.insertProjectMedia(mDbHelper.myDataBase, tempId, ProjectID, fileMediaType, "", "",
                                    "", String.valueOf(curLatitude), String.valueOf(curLongitude), "", "", "", "", "", createdDate, "N", "A", "");
                            mediaModelArrayList = mDbHelper.getProjectMediaDetails();
                            mDbHelper.close();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }

                        if (mediaModelArrayList.size() > 0) {
                            JSONObject object = new JSONObject(mSharedPreferences.getString(Utils.TASK_OBJECT, ""));
                            JSONArray mediaArray = object.getJSONArray("project_media");
                            JSONArray mediaNewArray = new JSONArray();
                            JSONObject mediaObject;
                            Logger.e(TAG, "Media Array update before : " + mediaArray);
                            if (mediaArray.length() > 0) {
                                for (int i = 0; i < mediaArray.length(); i++) {
                                    mediaObject = mediaArray.getJSONObject(i);
                                    mediaNewArray.put(mediaObject);
                                }
                            }
                            for (int j = 0; j < mediaModelArrayList.size(); j++) {
                                if (tempId.equalsIgnoreCase(mediaModelArrayList.get(j).getMediaId())) {
                                    mediaObject = new JSONObject();
                                    mediaObject.put("id", mediaModelArrayList.get(j).getMediaId());
                                    mediaObject.put("media_type", mediaModelArrayList.get(j).getMediaType());
                                    mediaObject.put("media", mediaModelArrayList.get(j).getMedia());
                                    mediaObject.put("video_thumb_image", mediaModelArrayList.get(j).getVideoThumbImage());
                                    mediaObject.put("media_description", mediaModelArrayList.get(j).getMediaDescription());
                                    mediaObject.put("latitude", mediaModelArrayList.get(j).getLatitude());
                                    mediaObject.put("longitude", mediaModelArrayList.get(j).getLongitude());
                                    mediaObject.put("street", mediaModelArrayList.get(j).getStreet());
                                    mediaObject.put("city", mediaModelArrayList.get(j).getCity());
                                    mediaObject.put("state", mediaModelArrayList.get(j).getState());
                                    mediaObject.put("country", mediaModelArrayList.get(j).getCountry());
                                    mediaObject.put("pincode", mediaModelArrayList.get(j).getPincode());
                                    mediaObject.put("created_date", mediaModelArrayList.get(j).getCreated_date());
                                    mediaObject.put("is_sync", mediaModelArrayList.get(j).getIs_sync());
                                    mediaObject.put("media_operation", mediaModelArrayList.get(j).getMedia_status());
                                    mediaObject.put("doc_thumb_image", mediaModelArrayList.get(j).getDocThumbImage());
                                    mediaNewArray.put(mediaObject);
                                }
                            }

                            Logger.e(TAG, "Media Array update after : " + mediaNewArray);
                            object.put("project_media", mediaNewArray);
                            if (object.getString("project_operation").equalsIgnoreCase("A") &&
                                    object.getString("is_sync").equalsIgnoreCase("N")) {
                                object.put("is_sync", "N");
                            } else {
                                object.put("is_sync", "N");
                                object.put("project_operation", "U");
                            }
                            Logger.e(TAG, "Project Object :" + object);
                            taskModel.clear();

                            Utils.storeString(mSharedPreferences, Utils.TASK_OBJECT, object.toString());
                            setTaskData(object);
                        }

                        if (fileMediaType.equalsIgnoreCase("doc")) {
                            String outputPath = Environment.getExternalStorageDirectory() + "/Media/Doc/";
                            Utils.moveFile(selectedImagePath, outputPath, tempId + ".doc");
                        } else if (fileMediaType.equalsIgnoreCase("docx")) {
                            String outputPath = Environment.getExternalStorageDirectory() + "/Media/Docx/";
                            Utils.moveFile(selectedImagePath, outputPath, tempId + ".docx");
                        } else if (fileMediaType.equalsIgnoreCase("txt")) {
                            String outputPath = Environment.getExternalStorageDirectory() + "/Media/Txt/";
                            Utils.moveFile(selectedImagePath, outputPath, tempId + ".txt");
                        } else if (fileMediaType.equalsIgnoreCase("pdf")) {
                            String outputPath = Environment.getExternalStorageDirectory() + "/Media/PDF/";
                            Utils.moveFile(selectedImagePath, outputPath, tempId + ".pdf");
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            } else {
                Toast.makeText(ProjectDetailsActivity.this, "Uploading failed, Please try again.", Toast.LENGTH_SHORT).show();
            }


        } catch (Exception e) {
            Logger.v(TAG, "ERROR : " + e.toString());
        }
    }

    public void getDocumentUri(Uri uri, String mediaType) {
        try {
            fileMediaType = mediaType;
//            selectedImagePath = getPath(ProjectDetailsActivity.this, uri);
//            selectedImagePath = getPath(ProjectDetailsActivity.this, uri);
//            if (!Utils.validateString(selectedImagePath)) {
//                if (isGoogleDriveDocument(uri)) {
//                    selectedImagePath = getDownloadedFile(uri);
//                }
//            }
            selectedImagePath = getDownloadedFile(uri);
            Logger.e(TAG, "Selected Path ::" + selectedImagePath);

            if (Utils.validateString(selectedImagePath)) {
                if (Utils.checkInternetConnection(ProjectDetailsActivity.this)) {
                    new uploadMedia().execute();
                } else {
                    try {

                        String tempId = Utils.getRandomAlphaNumeric(6);
                        String createdDate = Utils.getCurrentDateAndTime();

                        GPSTracker gpsTracker = Utils.getCurrentLocation(ProjectDetailsActivity.this);
                        if (gpsTracker != null) {
                            curLatitude = gpsTracker.getLatitude();
                            curLongitude = gpsTracker.getLongitude();
                        } else {
                            curLatitude = 0;
                            curLongitude = 0;
                        }

                        ArrayList<MediaModel> mediaModelArrayList = new ArrayList<>();
                        try {
                            mDbHelper.openDataBase();
                            mDbHelper.insertProjectMedia(mDbHelper.myDataBase, tempId, ProjectID, fileMediaType, "", "",
                                    "", String.valueOf(curLatitude), String.valueOf(curLongitude), "", "", "", "", "", createdDate, "N", "A", "");
                            mediaModelArrayList = mDbHelper.getProjectMediaDetails();
                            mDbHelper.close();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }

                        if (mediaModelArrayList.size() > 0) {
                            JSONObject object = new JSONObject(mSharedPreferences.getString(Utils.TASK_OBJECT, ""));
                            JSONArray mediaArray = object.getJSONArray("project_media");
                            JSONArray mediaNewArray = new JSONArray();
                            JSONObject mediaObject;
                            Logger.e(TAG, "Media Array update before : " + mediaArray);
                            if (mediaArray.length() > 0) {
                                for (int i = 0; i < mediaArray.length(); i++) {
                                    mediaObject = mediaArray.getJSONObject(i);
                                    mediaNewArray.put(mediaObject);
                                }
                            }
                            for (int j = 0; j < mediaModelArrayList.size(); j++) {
                                if (tempId.equalsIgnoreCase(mediaModelArrayList.get(j).getMediaId())) {
                                    mediaObject = new JSONObject();
                                    mediaObject.put("id", mediaModelArrayList.get(j).getMediaId());
                                    mediaObject.put("media_type", mediaModelArrayList.get(j).getMediaType());
                                    mediaObject.put("media", mediaModelArrayList.get(j).getMedia());
                                    mediaObject.put("video_thumb_image", mediaModelArrayList.get(j).getVideoThumbImage());
                                    mediaObject.put("media_description", mediaModelArrayList.get(j).getMediaDescription());
                                    mediaObject.put("latitude", mediaModelArrayList.get(j).getLatitude());
                                    mediaObject.put("longitude", mediaModelArrayList.get(j).getLongitude());
                                    mediaObject.put("street", mediaModelArrayList.get(j).getStreet());
                                    mediaObject.put("city", mediaModelArrayList.get(j).getCity());
                                    mediaObject.put("state", mediaModelArrayList.get(j).getState());
                                    mediaObject.put("country", mediaModelArrayList.get(j).getCountry());
                                    mediaObject.put("pincode", mediaModelArrayList.get(j).getPincode());
                                    mediaObject.put("created_date", mediaModelArrayList.get(j).getCreated_date());
                                    mediaObject.put("is_sync", mediaModelArrayList.get(j).getIs_sync());
                                    mediaObject.put("media_operation", mediaModelArrayList.get(j).getMedia_status());
                                    mediaObject.put("doc_thumb_image", mediaModelArrayList.get(j).getDocThumbImage());
                                    mediaNewArray.put(mediaObject);
                                }
                            }

                            Logger.e(TAG, "Media Array update after : " + mediaNewArray);
                            object.put("project_media", mediaNewArray);
                            if (object.getString("project_operation").equalsIgnoreCase("A") &&
                                    object.getString("is_sync").equalsIgnoreCase("N")) {
                                object.put("is_sync", "N");
                            } else {
                                object.put("is_sync", "N");
                                object.put("project_operation", "U");
                            }
                            Logger.e(TAG, "Project Object :" + object);
                            taskModel.clear();

                            Utils.storeString(mSharedPreferences, Utils.TASK_OBJECT, object.toString());
                            setTaskData(object);
                        }

                        if (fileMediaType.equalsIgnoreCase("doc")) {
                            String outputPath = Environment.getExternalStorageDirectory() + "/Media/Doc/";
                            Utils.moveFile(selectedImagePath, outputPath, tempId + ".doc");
                        } else if (fileMediaType.equalsIgnoreCase("docx")) {
                            String outputPath = Environment.getExternalStorageDirectory() + "/Media/Docx/";
                            Utils.moveFile(selectedImagePath, outputPath, tempId + ".docx");
                        } else if (fileMediaType.equalsIgnoreCase("txt")) {
                            String outputPath = Environment.getExternalStorageDirectory() + "/Media/Txt/";
                            Utils.moveFile(selectedImagePath, outputPath, tempId + ".txt");
                        } else if (fileMediaType.equalsIgnoreCase("pdf")) {
                            String outputPath = Environment.getExternalStorageDirectory() + "/Media/PDF/";
                            Utils.moveFile(selectedImagePath, outputPath, tempId + ".pdf");
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            } else {
                Toast.makeText(ProjectDetailsActivity.this, "This file is deleted from your mobile so please select another file.", Toast.LENGTH_SHORT).show();
            }


        } catch (Exception e) {
            Logger.v(TAG, "ERROR : " + e.toString());
            Toast.makeText(ProjectDetailsActivity.this, "This file is deleted from your mobile so please select another file.", Toast.LENGTH_SHORT).show();
        }
    }

    private String getDownloadedFile(Uri uri) throws FileNotFoundException {

        Cursor returnCursor = getContentResolver().query(uri, null, null, null, null);
        int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
        int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
        returnCursor.moveToFirst();
        String fileName = returnCursor.getString(nameIndex);
//        Log.d("FileName", returnCursor.getString(nameIndex));
//        Log.d("Size", Long.toString(returnCursor.getLong(sizeIndex)));
        returnCursor.close();
        InputStream is = getContentResolver().openInputStream(uri);

        File file = new File(Environment.getExternalStorageDirectory().getPath() + "/" + fileName);
        return copyInputStreamToFile(is, file);

    }

    public static String copyInputStreamToFile(InputStream in, File file) {
        OutputStream out = null;

        try {
            out = new FileOutputStream(file);
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return file.getAbsolutePath();
    }

    public String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);

            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }


    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }


    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    public static boolean isGoogleDriveDocument(Uri uri) {
        return uri.getAuthority().contains("com.google.android.apps");
    }


    private void openAlert(final String path) {
        try {
            if (Utils.validateString(path)) {
                AlertDialog.Builder newDialog = new AlertDialog.Builder(this);
                newDialog.setTitle(getString(R.string.alert));
                newDialog.setMessage("Upload this image to this project?");
                newDialog.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        if (!FlagGallery) {
                            if (Utils.validateString(selectedImagePath)) {
                                File fdelete = new File(selectedImagePath);
                                if (fdelete.exists()) {
                                    if (fdelete.delete()) {
                                        Logger.d(TAG, "file Deleted :" + selectedImagePath);
                                    } else {
                                        Logger.d(TAG, "file not Deleted :" + selectedImagePath);
                                    }
                                }
                            }
                        }
                    }
                });
                newDialog.setNegativeButton("Crop", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        CropingIMG();
                    }
                });
                newDialog.setNeutralButton("Upload", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        if (Utils.validateString(path)) {
                            if (Utils.checkInternetConnection(ProjectDetailsActivity.this)) {
                                new uploadMedia().execute();
                            } else {
                                try {
                                    String media_type = "";
                                    if (!selectedImagePath.contains(".mp4")) {
                                        media_type = "image";
                                    } else {
                                        media_type = "video";
                                    }

                                    String tempId = Utils.getRandomAlphaNumeric(6);
                                    String createdDate = Utils.getCurrentDateAndTime();

                                    GPSTracker gpsTracker = Utils.getCurrentLocation(ProjectDetailsActivity.this);
                                    if (gpsTracker != null) {
                                        curLatitude = gpsTracker.getLatitude();
                                        curLongitude = gpsTracker.getLongitude();
                                    } else {
                                        curLatitude = 0;
                                        curLongitude = 0;
                                    }

                                    ArrayList<MediaModel> mediaModelArrayList = new ArrayList<>();
                                    try {
                                        mDbHelper.openDataBase();
                                        mDbHelper.insertProjectMedia(mDbHelper.myDataBase, tempId, ProjectID, media_type, "", "",
                                                "", String.valueOf(curLatitude), String.valueOf(curLongitude), "", "", "", "", "", createdDate, "N", "A", "");
                                        mediaModelArrayList = mDbHelper.getProjectMediaDetails();
                                        mDbHelper.close();
                                    } catch (SQLException e) {
                                        e.printStackTrace();
                                    }

                                    if (mediaModelArrayList.size() > 0) {
                                        JSONObject object = new JSONObject(mSharedPreferences.getString(Utils.TASK_OBJECT, ""));
                                        JSONArray mediaArray = object.getJSONArray("project_media");
                                        JSONArray mediaNewArray = new JSONArray();
                                        JSONObject mediaObject;
                                        Logger.e(TAG, "Media Array update before : " + mediaArray);
                                        if (mediaArray.length() > 0) {
                                            for (int i = 0; i < mediaArray.length(); i++) {
                                                mediaObject = mediaArray.getJSONObject(i);
                                                mediaNewArray.put(mediaObject);
                                            }
                                        }
                                        for (int j = 0; j < mediaModelArrayList.size(); j++) {
                                            if (tempId.equalsIgnoreCase(mediaModelArrayList.get(j).getMediaId())) {
                                                mediaObject = new JSONObject();
                                                mediaObject.put("id", mediaModelArrayList.get(j).getMediaId());
                                                mediaObject.put("media_type", mediaModelArrayList.get(j).getMediaType());
                                                mediaObject.put("media", mediaModelArrayList.get(j).getMedia());
                                                mediaObject.put("video_thumb_image", mediaModelArrayList.get(j).getVideoThumbImage());
                                                mediaObject.put("media_description", mediaModelArrayList.get(j).getMediaDescription());
                                                mediaObject.put("latitude", mediaModelArrayList.get(j).getLatitude());
                                                mediaObject.put("longitude", mediaModelArrayList.get(j).getLongitude());
                                                mediaObject.put("street", mediaModelArrayList.get(j).getStreet());
                                                mediaObject.put("city", mediaModelArrayList.get(j).getCity());
                                                mediaObject.put("state", mediaModelArrayList.get(j).getState());
                                                mediaObject.put("country", mediaModelArrayList.get(j).getCountry());
                                                mediaObject.put("pincode", mediaModelArrayList.get(j).getPincode());
                                                mediaObject.put("created_date", mediaModelArrayList.get(j).getCreated_date());
                                                mediaObject.put("is_sync", mediaModelArrayList.get(j).getIs_sync());
                                                mediaObject.put("media_operation", mediaModelArrayList.get(j).getMedia_status());
                                                mediaObject.put("doc_thumb_image", mediaModelArrayList.get(j).getDocThumbImage());
                                                mediaNewArray.put(mediaObject);
                                            }
                                        }

                                        Logger.e(TAG, "Media Array update after : " + mediaNewArray);
                                        object.put("project_media", mediaNewArray);
                                        if (object.getString("project_operation").equalsIgnoreCase("A") &&
                                                object.getString("is_sync").equalsIgnoreCase("N")) {
                                            object.put("is_sync", "N");
                                        } else {
                                            object.put("is_sync", "N");
                                            object.put("project_operation", "U");
                                        }
                                        Logger.e(TAG, "Project Object :" + object);
                                        taskModel.clear();

                                        Utils.storeString(mSharedPreferences, Utils.TASK_OBJECT, object.toString());
                                        setTaskData(object);
                                    }


                                    if (media_type.equalsIgnoreCase("image")) {
                                        if (!FlagGallery) {
                                            String outputPath = Environment.getExternalStorageDirectory() + "/Media/Image/";
                                            Utils.moveFile(selectedImagePath, outputPath, tempId + ".jpg");
                                        } else {
                                            String outputPath = Environment.getExternalStorageDirectory() + "/Media/Image/";
                                            Utils.copyFile(selectedImagePath, outputPath, tempId + ".jpg");
                                        }
                                    } else {
                                        String outputPath = Environment.getExternalStorageDirectory() + "/Media/Video/";
                                        Utils.moveFile(selectedImagePath, outputPath, tempId + ".mp4");
                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        } else {
                            Toast.makeText(ProjectDetailsActivity.this, "This image is deleted from your mobile so please select another image.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                newDialog.show();
            } else {
                Toast.makeText(ProjectDetailsActivity.this, "This image is deleted from your mobile so please select another image.", Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void CropingIMG() {

        final ArrayList<CropingOption> cropOptions = new ArrayList<CropingOption>();

        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setType("image/*");

        List<ResolveInfo> list = getPackageManager().queryIntentActivities(intent, 0);
        int size = list.size();
        if (size == 0) {
            Toast.makeText(this, "Cann't find image croping app", Toast.LENGTH_SHORT).show();
            return;
        } else {
            intent.setData(mCropImageUri);
            intent.putExtra("outputX", 512);
            intent.putExtra("outputY", 512);
            intent.putExtra("aspectX", 1);
            intent.putExtra("aspectY", 1);
            intent.putExtra("scale", true);

            //Create output file here
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(outPutFile));

            if (size == 1) {
                Intent i = new Intent(intent);
                ResolveInfo res = (ResolveInfo) list.get(0);

                i.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));

                startActivityForResult(i, CROPING_CODE);
            } else {
                for (ResolveInfo res : list) {
                    final CropingOption co = new CropingOption();

                    co.title = getPackageManager().getApplicationLabel(res.activityInfo.applicationInfo);
                    co.icon = getPackageManager().getApplicationIcon(res.activityInfo.applicationInfo);
                    co.appIntent = new Intent(intent);
                    co.appIntent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
                    cropOptions.add(co);
                }

                CropingOptionAdapter adapter = new CropingOptionAdapter(getApplicationContext(), cropOptions);

                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
                builder.setTitle("Choose Croping App");
                builder.setCancelable(false);
                builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        startActivityForResult(cropOptions.get(item).appIntent, CROPING_CODE);
                    }
                });

                builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {

                        if (mCropImageUri != null) {
                            getContentResolver().delete(mCropImageUri, null, null);
                            mCropImageUri = null;
                        }
                    }
                });

                android.app.AlertDialog alert = builder.create();
                alert.show();
            }
        }
    }

    private void enableLoc() {

        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();

            googleApiClient.connect();

            locationRequest = LocationRequest.create();
            locationRequest.setInterval(10000);
            locationRequest.setFastestInterval(5000);
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);

            builder.setAlwaysShow(true);

            PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
            result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
                @Override
                public void onResult(LocationSettingsResult result) {
                    final Status status = result.getStatus();
                    final LocationSettingsStates state = result.getLocationSettingsStates();
                    switch (status.getStatusCode()) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            try {
                                status.startResolutionForResult(ProjectDetailsActivity.this, REQUEST_LOCATION);
                            } catch (IntentSender.SendIntentException e) {
                                // Ignore the error.
                            }
                            break;

                        case LocationSettingsStatusCodes.SUCCESS:

                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            // Location settings are not satisfied. However, we have no way to fix the
                            // settings so we won't show the dialog.
                            break;
                    }
                }
            });
        }

    }

    private void startLocationUpdates() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            return;
        }
        if (googleApiClient != null) {
            if (googleApiClient.isConnected()) {
                LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
            }
        }

    }

    @Override
    protected void onStart() {
        if (googleApiClient != null) {
            googleApiClient.connect();
        } else {
            // First we need to check availability of play services
            if (checkPlayServices()) {
                enableLoc();
            }
        }
        super.onStart();
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        if (googleApiClient != null) {
            if (googleApiClient.isConnected()) {
                startLocationUpdates();
            }
        }
    }

    @Override
    protected void onStop() {
        if (googleApiClient != null) {
            googleApiClient.disconnect();
        }

        if (requestQueue != null) {
            requestQueue.cancelAll("all");
        }

        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    protected void stopLocationUpdates() {
        if (googleApiClient != null && googleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
        }
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            return;
        }
        if (googleApiClient != null) {
            if (googleApiClient.isConnected()) {
                LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        if (googleApiClient != null) {
            googleApiClient.connect();
        }
    }

    double curLatitude = 0, curLongitude = 0;

    @Override
    public void onLocationChanged(Location location) {
        LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean statusOfGPS = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (statusOfGPS) {
            if (location != null) {
                curLatitude = location.getLatitude();
                curLongitude = location.getLongitude();
            }
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this, PLAY_SERVICES_RESOLUTION_REQUEST).show();
            }
            return false;
        }
        return true;
    }

    public JSONObject addObjectValue(JSONObject object) {
        JSONArray additionalContactsArray = null;
        try {
            additionalContactsArray = object.getJSONArray("additional_contacts");

            JSONArray additionalContactsNewArray = new JSONArray();
            String projectResponsiblityName = "", projectResponsiblityId = "";
            Logger.e(TAG, "Additional Contact Array before :" + additionalContactsArray);
            if (additionalContactsArray.length() > 0) {
                for (int i = 0; i < additionalContactsArray.length(); i++) {
                    JSONObject contactObject = additionalContactsArray.getJSONObject(i);

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
                    contactObject.put("project_responsiblity", projectResponsiblityName);
                    contactObject.put("project_responsiblity_id", projectResponsiblityId);

                    additionalContactsNewArray.put(contactObject);
                }
            }

            Logger.e(TAG, "Additional Contact Array after :" + additionalContactsNewArray);
            object.put("additional_contacts", additionalContactsNewArray);
            Logger.e(TAG, "Project Object :" + object);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    public boolean fieldValidation() {
        boolean flag = true;
        if (!Utils.validateString(edtTaskDueDate.getText().toString())) {
            flag = false;
            edtTaskDueDate.requestFocus();
            showMessage(getString(R.string.select_due_date));
        } else if (!Utils.validateString(edtTaskDueTime.getText().toString())) {
            flag = false;
            edtTaskDueTime.requestFocus();
            showMessage(getString(R.string.select_due_time));
        }
        return flag;
    }

    private void showMessage(String message) {
        Utils.showResponseMessage(this, rootLayout, message);
    }

}
