package com.rogi.View;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import com.rogi.R;
import com.rogi.Service.GPSTracker;
import com.rogi.logger.Logger;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.SocketTimeoutException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Utils {

    public static String PREF_NAME = "rogi";
    public static String DEVICE_TYPE = "Android";
    public static String LOGIN_TYPE = "normal";
    public static String TAG = "Utils";
    public static String MAIN_URL = "";
    public static String WEB_SITE_URL = "";

    // *********   Testing Authorize.net Cradential  *****************

    public static String API_LOGIN_ID = "2XuB3h8h"; // replace with YOUR_API_LOGIN_ID
    public static String TRANSACTION_KEY = "4A2Rkh3cW435mW2b";
    public static final String PUBLIC_KEY_SEC = "78Cn4rKVb7Jx5mjxSRnpP4J8NuZdhe363B83GZT3pn7nPm4KReV3nukmpa4fbBZK";
    public static String AUTHORIZED_PAYMENT_API = "https://apitest.authorize.net/xml/v1/request.api";

    // ***********   Live Authorize.net Cradential  ********************

//    public static String API_LOGIN_ID = "54JQnHg9M"; // replace with YOUR_API_LOGIN_ID
//    public static String TRANSACTION_KEY = "7mP3yq86UdVD336X";
//    public static final String PUBLIC_KEY_SEC = "78Cn4rKVb7Jx5mjxSRnpP4J8NuZdhe363B83GZT3pn7nPm4KReV3nukmpa4fbBZK";
//    public static String AUTHORIZED_PAYMENT_API = "https://api.authorize.net/xml/v1/request.api";

    // *************    Development URL   ***********************
    public static String DOMIN_URL = "http://192.168.1.113/rogi/api/web/users/geturl";

    // *************    Stage URL   ***********************
//    public static String DOMIN_URL = "http://203.109.113.155/rogi/api/web/users/geturl";

    // *************    Beta URL    ***********************
//    public static String DOMIN_URL = "http://www.myrogi.com/beta/api/web/" + "users/geturl";

    // *************    Live URL    ***********************
//    public static String DOMIN_URL = "http://www.myrogi.com/api/web/" + "users/geturl";

    // *************    FAQ Beta URL    ***********************
    public static String FAQ_URL = "/frontend/web/home/faqapp";

    // *************    FAQ Live URL    ***********************
//    public static String FAQ_URL = "/home/faqapp";

//    public static String URL_ADDRESS = "http://203.109.113.155/rogi/";
//    public static String URL_ADDRESS = "http://192.168.1.113/rogi/";
//    public static String URL_ADDRESS = "http://192.168.1.205/rogi/";

    public static String REGISTER_USER_API = "users/registration";
    public static String LOGIN_API = "users/login";
    public static String LOGOUT_API = "users/logout";
    public static String FORGOT_PASSWORD_API = "users/forgotpassword";
    public static String RESET_PASSWORD_API = "users/resetpassword";
    public static String UPDATE_USER_API = "users/updateprofile";
    public static String ADD_TASK_API = "project/addproject";
    public static String ADD_NOTE_API = "project/addnote";
    public static String EDIT_NOTE_API = "project/updatenote";
    public static String DELETE_NOTE_API = "project/deletenote";
    //    public static String PROJECT_LIST_API = URL + "project/projectlist";
    public static String PROJECT_LIST_API = "project/projectlistv2";
    public static String HISTROY_LIST_API = "project/historylist";
    public static String FILTER_HISTROY_API = "project/filterproject";
    public static String COMPANY_LIST_API = "project/companylist";
    public static String PROJECT_RESPONIBILITY_API = "project/projectresponsiblity";
    public static String PROJECT_DETAILS_API = "project/projectdetails";
    public static String PROJECT_UPDATE_API = "project/updateproject";
    public static String ADDITIONAL_CONTACT_API = "project/additionalcontact";
    public static String DELETE_ADDITIONAL_CONTACT_API = "project/deleteadditionalcontact";
    public static String UPDATE_ADDITIONAL_CONTACT_API = "project/updateadditionalcontact";
    public static String ADD_PROJCET_RESPONSIBILITY_API = "project/addprojectresponsiblity";
    public static String ADD_PROJECT_PHASE_API = "project/addprojectphase";
    public static String UPDATE_PROJECT_PHASE_API = "project/updateprojectphase";
    public static String DELETE_PROJECT_PHASE_API = "project/deleteprojectphase";
    public static String UPLOAD_MEDIA_API = "project/mediaupload";
    public static String UPDATE_MEDIA_API = "project/updatemedia";
    public static String UPLOAD_SIGNATURE_API = "project/addsignature";
    public static String UPDATE_STATUS_API = "project/updatestatus";
    public static String GENERATE_REPORT_API = "project/report";
    public static String ADD_PHOTO_DESCRIPTION = "project/addphotodescription";
    public static String SYNC_PROJECT = "project/synkproject";
    public static String PLAN_LIST_API = "plan/planlist";
    public static String PLAN_ADD_API = "plan/addplan";
    public static String PLAN_APPLY_CODE_API = "plan/addpromocode";
    public static String PLAN_CHECK_API = "plan/plancheck";
    public static String CANCEL_PLAN_API = "plan/cancelplan";
    public static String ADD_SUB_LEVEL_USERS = "plan/addsublevelusers";
    public static String GET_SUB_LEVEL_USERS = "plan/getsublevelusers";
    public static String GET_PURCHASE_PLAN_DETAILS = "plan/getpurchasedplandetail";
    public static String UPDATE_SUB_LEVEL_USERS = "plan/updatesublevelusers";
    public static String TECHNICAL_SUPPORT = "users/technicalsupport";
    public static String SALES_FORCE_SYNC = "salesforce/syncsalesforceapp";
    public static String UPDATE_TIMEZONE = "users/updatetimezone";
    public static String FAQ_API = "users/faq";

    public static String ABOUT_US = "content/getcontent?id=1";
    public static String PRIVACY_POLICY = "content/getcontent?id=2";
    public static String TERMS_AND_CONDITION = "content/getcontent?id=3";

    public static String SALESFORCE_GUIDE_VIDEO = "https://www.youtube.com/watch?v=Om4SDNu4b8M";
    public static String APP_GUIDE_VIDEO = "https://www.youtube.com/watch?v=YnkJY2fkF08";
    public static String ADMIN_SALESFORCE_GUIDE_VIDEO = "https://www.youtube.com/watch?v=8PXgedb-gII";
    public static String ADMIN_SALESFORCE_CONF_GUIDE_VIDEO = "https://www.youtube.com/watch?v=XcePUKJAZK0";
    public static String ADMIN_CREATE_SUB_LEVEL_USER = "https://www.youtube.com/watch?v=xP693Q6NlKA&feature=youtu.be";

    public static final String REG_ID = "registration_id";
    // broadcast receiver intent filters
    public static final String REGISTRATION_COMPLETE = "registrationComplete";
    public static final String PUSH_NOTIFICATION = "pushNotification";
    // global topic to receive app wide push notifications
    public static final String TOPIC_GLOBAL = "global";

    //TODO: Pref_Name...........
    public static String USER_ID = "user_id";
    public static String FIRST_NAME = "first_name";
    public static String LAST_NAME = "last_name";
    public static String STREET = "street";
    public static String CITY = "city";
    public static String STATE = "state";
    public static String COUNTRY = "country";
    public static String ZIPCODE = "zipcode";
    public static String PHONE_NUMBER = "phone_number";
    public static String EMAIL = "email";
    public static String PASSWORD = "password";
    public static String PROFILE_IMAGE = "profile_image";
    public static String TOKEN = "token";
    public static String COMPANY_NAME = "company_name";
    public static String COMPANY_IMAGE = "company_image";
    public static String TEMP_PASSWORD = "temp_password";
    public static String PLAN_PRICE = "PlanPrice";
    public static String PLAN_ARRAY = "PlanArray";
    public static String PAYBLE_PRICE = "PayblePrice";
    public static String PLAN_NAME = "PlanName";
    public static String TOTAL_AMOUNT = "total_amount";
    public static String SUB_USER_TOTAL_AMOUNT = "sub_user_total_amount";
    public static String PER_USER_PRICE = "per_user_price";
    public static String ENTER_SUB_USER = "enter_sub_user";
    public static String DEVICE_IP = "DeviceIp";
    public static String PLAN_ID = "PlanId";
    public static String ROLE = "role";
    public static String AUTO_RENEWAL = "auto_renewal";
    public static String SUBSCRIPTION_ID = "subscription_id";
    public static String MY_PROFILE = "my_profile";
    public static String AUTHORIZE_TEXT = "authorize_text";

    public static String PLAN_TYPE = "PlanType";
    public static String MONTH_YEAR_TYPE = "MonthYearType";
    public static String REMINDER_TYPE = "reminder_type";
    public static String MONTH_TYPE = "MonthType";
    public static String YEAR_TYPE = "YearType";
    public static String MONTH_POS = "MonthNumber";
    public static String YEAR_POS = "YearNumber";
    public static String PER_RATE_USER = "per_rate_user";
    public static String EXITING_USER_OBJECT = "exiting_user_object";
    public static String EXITING_USER_SCREEN = "exiting_user_screen";
    public static String EXITING_USER_IDS = "exiting_user_ids";

    public static String FILTER_FROM_DATE = "filter_from_date";
    public static String FILTER_TO_DATE = "filter_to_date";
    public static String FILTER_PRIORITY = "filter_priority";
    public static String FILTER_STATUS = "filter_status";

    public static String TODAY_PROJECT = "today_project";
    public static String COMPLETED_PROJECT = "completed_project";
    public static String PENDING_PROJECT = "pending_project";

    public static String TASK_OBJECT = "TaskObject";
    public static String ADDITIONAL_CONTACT_OBJECT = "additional_contact_object";
    public static String ADDITIONAL_CONTACT = "additional_contact";
    public static String ADDEDPRORESPONSIBILITY_ARRAY = "addedProResponsibility_array";
    public static String RESPONSIBILITYID_ARRAY = "responsibilityid_array";
    public static String PROJECT_ID = "project_id";
    public static String SWITCH = "switch";
    public static String ITEM_FILTER_STATUS = "FilterStatus";
    public static String CHECK_CURRENT_DATE = "check_current_date";

    public static String START_WORK = "Start Project";
    public static String COMPLETE = "Finish Project";
    public static String OPEN = "Open";
    public static String GENERATE_REPORT = "Generate Project Report";
    public static String GENERATE_PROPOSAL = "Generate Proposal";
    public static String START = "Start";
    public static String PENDING = "Pending";
    public static String REPORT = "Completed";
    public static String FINISH = "Finish";
    public static String FINISHED = "Finished";

    public static String MEDIA_OBJECT = "media_object";
    public static String MEDIA_IMAGE_URL = "media_image_url";
    public static String MEDIA_ID = "media_id";
    public static String MEDIA_EDITED_IMAGE_PATH = "media_edited_image_path";

    public static String MEDIA_CUSTOM_PATH = "PATH";
    public static String MEDIA_CUSTOM_THUMB = "THUMB";
    public static String MEDIA_CUSTOM_WHO = "WHO";
    public static String PROJECT_STATUS = "project_status";
    public static String PROJECT_START_TIME = "project_start_time";
    public static String PROJECT_REPORT_STATUS = "project_report_status";
    public static String PROJECT_REPORT_START_TIME = "project_report_start_time";

    public static String PROJECT_RESPONSIBILITY = "project_responsibility";
    public static String RESPONSIBILITY_ID = "responsibility_id";

    public static String COMPANY_LIST = "company_list";
    public static String RESPONSIBILITY_LIST = "responsibility_list";
    public static String TEMP_RESPONSIBILITY_LIST = "TEMP_RESPONSIBILITY_LIST";

    public static String PDF_REPORT_URL = "pdf_report_url";
    public static String PDF_FILE_NAME = "pdf_file_name";

    public static String SUB_LEVEL_USER_COUNT = "sub_level_user_count";
    public static String SUB_LEVEL_USER_AMOUNT = "sub_level_user_amount";
    public static String PLAN_TOTAL_PRICE = "plan_total_price";
    public static String PROMOCODE = "promocode";
    public static String PROMOCODE_VALUE = "promocode_value";
    public static String TABLET = "Tablet";

    public static String URL_API_ADDRESS = "url_api_address";
    public static String URL_WEB_ADDRESS = "url_web_address";

    public static boolean projectRefreshFlag = false;
    public static boolean projectAddedFlag = false;
    public static boolean projectFilterRefreshFlag = false;
    public static boolean projectSyncFlag = false;
    public static String trialOccurrences = "";

    // SalesForce

    public static String SF_CONSUMER_KEY = "sf_consumer_key";
    public static String SF_CONSUMER_SECRET_KEY = "sf_consumer_secret_key";
    public static String SF_SECURITY_TOKEN = "sf_security_token";
    public static String SF_USERNAME = "sf_username";
    public static String SF_PASSWORD = "sf_password";

    // FAQ

    public static String FAQ_DATA = "faq_data";
    public static String FAQ_QUESTION = "faq_question";
    public static String FAQ_ANSWER = "faq_answer";

    // Find Location
    public static final int SUCCESS_RESULT = 0;
    public static final int FAILURE_RESULT = 1;

    public static final int USE_ADDRESS_NAME = 1;
    public static final int USE_ADDRESS_LOCATION = 2;

    public static final String PACKAGE_NAME = "com.rogiproject";
    public static final String RECEIVER = PACKAGE_NAME + ".RECEIVER";
    public static final String RESULT_DATA_KEY = PACKAGE_NAME + ".RESULT_DATA_KEY";
    public static final String RESULT_ADDRESS = PACKAGE_NAME + ".RESULT_ADDRESS";
    public static final String LOCATION_LATITUDE_DATA_EXTRA = PACKAGE_NAME + ".LOCATION_LATITUDE_DATA_EXTRA";
    public static final String LOCATION_LONGITUDE_DATA_EXTRA = PACKAGE_NAME + ".LOCATION_LONGITUDE_DATA_EXTRA";
    public static final String LOCATION_NAME_DATA_EXTRA = PACKAGE_NAME + ".LOCATION_NAME_DATA_EXTRA";
    public static final String FETCH_TYPE_EXTRA = PACKAGE_NAME + ".FETCH_TYPE_EXTRA";

    // return true if string is not null or empty
    public static boolean validateString(String object) {
        boolean flag = false;
        if (object != null && !object.isEmpty() && object.trim().length() > 0 && !object.equalsIgnoreCase("null") && !object.equalsIgnoreCase("")) {
            flag = true;
        }
        return flag;
    }

    public static void storeString(SharedPreferences sharedPreferences, String key, String value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static boolean checkInternetConnection(Context context) {

        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm.getActiveNetworkInfo() != null
                && cm.getActiveNetworkInfo().isAvailable()
                && cm.getActiveNetworkInfo().isConnected()) {
            return true;
        } else {
            // Toast.makeText(context, "Conex�o com a internet indispon�vel.",
            // Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    public static final void showMessageDialog(Context context, String title, String message) {
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

    public static boolean isEmailValid(String email) {
        String expression = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        CharSequence inputStr = email;
        boolean flag = false;
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches()) {
            flag = true;
        }
        return flag;
    }

    public static String bitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    public static void CopyStream(InputStream is, OutputStream os) {
        final int buffer_size = 1024;
        try {
            byte[] bytes = new byte[buffer_size];
            for (; ; ) {
                int count = is.read(bytes, 0, buffer_size);
                if (count == -1)
                    break;
                os.write(bytes, 0, count);
            }
        } catch (Exception ex) {
        }
    }

    public static void showResponseMessage(Context context, View view, String message) {
        Snackbar snackbar = Snackbar.make(view, message, 2000);
        ViewGroup group = (ViewGroup) snackbar.getView();
        group.setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimaryDark));
        snackbar.show();
    }

    public static Snackbar showResponseMessageHandleResponse(View view, String message) {
        Snackbar snackbar = Snackbar.make(view, message, 800);
        snackbar.show();
        return snackbar;
    }

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }

    public static Typeface getTypeFace(Context context) {
        Typeface typeface = Typeface.createFromAsset(context.getAssets(), "OpenSans-Regular.ttf");
        return typeface;
    }

    // FCM

    public static boolean isAppIsInBackground(Context context) {
        boolean isInBackground = true;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    for (String activeProcess : processInfo.pkgList) {
                        if (activeProcess.equals(context.getPackageName())) {
                            isInBackground = false;
                        }
                    }
                }
            }
        } else {
            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
            ComponentName componentInfo = taskInfo.get(0).topActivity;
            if (componentInfo.getPackageName().equals(context.getPackageName())) {
                isInBackground = false;
            }
        }

        return isInBackground;
    }

    public static String getPhoneNumberFormat(String number) {
        String a = "", b = "", c = "";
        for (int j = 1; j <= number.length(); j++) {
            if (j == 3) {
                a = number.substring(0, 3);
            }
            if (j == 6) {
                b = number.substring(3, 6);
            }
            if (j > 6) {
                if (j == 7) {
                    c = number.substring(6, 7);
                }
                if (j == 8) {
                    c = number.substring(6, 8);
                }
                if (j == 9) {
                    c = number.substring(6, 9);
                }
                if (j == 10) {
                    c = number.substring(6, 10);
                }
            }
        }
        number = "(" + a + ") " + b + "-" + c;
        return number;
    }

    public static String getCurrentDateAndTime() {
        Calendar c = Calendar.getInstance();
        System.out.println("Current time =&gt; " + c.getTime());

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentDate = df.format(c.getTime());
        Logger.e(TAG, "currentDate :" + currentDate);
        return currentDate;
    }

    public static String getCurrentDateAndTimeWithAMorPM() {
        Calendar c = Calendar.getInstance();
        System.out.println("Current time =&gt; " + c.getTime());
        SimpleDateFormat df = new SimpleDateFormat("MMM dd yyyy, hh:mm a");
        String currentDate = df.format(c.getTime());
        Logger.e(TAG, "currentDate :" + currentDate);
        return currentDate;
    }

    public static String getCurrentDate() {
        Calendar c = Calendar.getInstance();
        System.out.println("Current time =&gt; " + c.getTime());
        SimpleDateFormat df = new SimpleDateFormat("MMM dd yyyy");
        String currentDate = df.format(c.getTime());
        Logger.e(TAG, "currentDate :" + currentDate);
        return currentDate;
    }

    public static String getCurrentDateForTodays() {
        Calendar c = Calendar.getInstance();
        System.out.println("Current time =&gt; " + c.getTime());
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String currentDate = df.format(c.getTime());
        Logger.e(TAG, "currentDate :" + currentDate);
        return currentDate;
    }


    public static String parseDateToyyyyMMdd(String dateFormat) {
        String inputPattern = "MMM dd yyyy, hh:mm a";
        String outputPattern = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

        Date date = null;
        String str = null;

        try {
            date = inputFormat.parse(dateFormat);
            str = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return str;
    }

    public static String parseDateFromyyyyMMdd(String dateFormat) {
        String inputPattern = "yyyy-MM-dd HH:mm:ss";
        String outputPattern = "MMM dd yyyy, hh:mm a";
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

        Date date = null;
        String str = null;

        try {
            date = inputFormat.parse(dateFormat);
            str = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return str;
    }

    public static String parseDateTo(String dateFormat) {
        String inputPattern = "MMM dd yyyy";
        String outputPattern = "yyyy-MM-dd";
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

        Date date = null;
        String str = null;

        try {
            date = inputFormat.parse(dateFormat);
            str = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return str;
    }

    public static String parseDateFrom(String dateFormat) {
        String inputPattern = "yyyy-MM-dd";
        String outputPattern = "MMM dd yyyy";
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

        Date date = null;
        String str = null;

        try {
            date = inputFormat.parse(dateFormat);
            str = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return str;
    }

    public static String parseTime(String timeFormat) {
        String inputPattern = "HH:mm:ss";
        String outputPattern = "hh:mm a";
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

        Date date = null;
        String str = null;

        try {
            date = inputFormat.parse(timeFormat);
            str = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return str;
    }

    public static String getRandomAlphaNumeric(int count) {
        String ALPHA_NUMERIC_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmanopqrstuvwxyz0123456789";
        StringBuilder builder = new StringBuilder();
        while (count-- != 0) {
            int character = (int) (Math.random() * ALPHA_NUMERIC_STRING.length());
            builder.append(ALPHA_NUMERIC_STRING.charAt(character));
        }
        return builder.toString();
    }

    public static GPSTracker getCurrentLocation(Context context) {
        GPSTracker gps = new GPSTracker(context);
        // check if GPS enabled
        if (gps.canGetLocation()) {
            // double latitude = gps.getLatitude();
            // double longitude = gps.getLongitude();
            return gps;
        } else {
            // can't get location // GPS or Network is not enabled // Ask user
            // to enable GPS/network in settings
//			gps.showSettingsAlert();
        }
        return null;
    }

    public static String updateTime(int hours, int mins) {

        String timeSet = "";
        if (hours > 12) {
            hours -= 12;
            timeSet = "PM";
        } else if (hours == 0) {
            hours += 12;
            timeSet = "AM";
        } else if (hours == 12)
            timeSet = "PM";
        else
            timeSet = "AM";

        String hourses = utilTime(hours);
        String minutes = utilTime(mins);
        String timeformat = new StringBuilder().append(hourses).append(':').append(minutes).append(" ").append(timeSet).toString();

        return timeformat;
    }

    public static String utilTime(int value) {
        if (value < 10)
            return "0" + String.valueOf(value);
        else
            return String.valueOf(value);
    }

    public static boolean deleteDirectory(File path) {
        if (path.exists()) {
            File[] files = path.listFiles();
            if (files == null) {
                return true;
            }
            for (int i = 0; i < files.length; i++) {
                if (files[i].isDirectory()) {
                    deleteDirectory(files[i]);
                } else {
                    files[i].delete();
                }
            }
        }
        return (path.delete());
    }

    public static void moveFile(String inputPath, String outputPath, String image_name) {

        InputStream in = null;
        OutputStream out = null;
        try {

            //create output directory if it doesn't exist
            File dir = new File(outputPath);
            if (!dir.exists()) {
                dir.mkdirs();
            }


            in = new FileInputStream(inputPath);
            out = new FileOutputStream(outputPath + image_name);

            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            in.close();
            in = null;

            // write the output file
            out.flush();
            out.close();
            out = null;

            // delete the original file
            new File(inputPath).delete();


        } catch (FileNotFoundException fnfe1) {
            Logger.e(TAG, fnfe1.getMessage());
        } catch (Exception e) {
            Logger.e(TAG, e.getMessage());
        }
    }

    public static void copyFile(String inputPath, String outputPath, String image_name) {
        InputStream in = null;
        OutputStream out = null;
        try {

            //create output directory if it doesn't exist
            File dir = new File(outputPath);
            if (!dir.exists()) {
                dir.mkdirs();
            }


            in = new FileInputStream(inputPath);
            out = new FileOutputStream(outputPath + image_name);

            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) > 0) {
                out.write(buffer, 0, read);
            }
            in.close();
            in = null;

            // write the output file
            out.flush();
            out.close();
            out = null;

        } catch (FileNotFoundException fnfe1) {
            Logger.e(TAG, fnfe1.getMessage());
        } catch (Exception e) {
            Logger.e(TAG, e.getMessage());
        }
    }

    public static String storeSignatureImage(Bitmap image, String imageName) {

        String PATH = Environment.getExternalStorageDirectory() + "/Media/";
        File folder = new File(PATH);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        String PATH2 = PATH + "/Signature/";
        File pictureFile = new File(PATH2, imageName);
        if (!pictureFile.exists()) {
            pictureFile.getParentFile().mkdirs();
        }
        if (pictureFile == null) {
            Logger.d(TAG, "Error creating media file, check storage permissions: ");
            return null;
        }
        try {
            FileOutputStream fos = new FileOutputStream(pictureFile);
            image.compress(Bitmap.CompressFormat.JPEG, 90, fos);
            fos.close();
        } catch (FileNotFoundException e) {
            Logger.d(TAG, "File not found: " + e.getMessage());
        } catch (IOException e) {
            Logger.d(TAG, "Error accessing file: " + e.getMessage());
        }

        String path = pictureFile.toString();
        return path;
    }

    public static String isTablet(Activity context) {
        DisplayMetrics metrics = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(metrics);

        int widthPixels = metrics.widthPixels;
        int heightPixels = metrics.heightPixels;
        float scaleFactor = metrics.density;
        float widthDp = widthPixels / scaleFactor;
        float heightDp = heightPixels / scaleFactor;

        float smallestWidth = Math.min(widthDp, heightDp);
        String str = "";
        if (smallestWidth > 720) {
            //Device is a 10" tablet
            str = TABLET;
        } else if (smallestWidth > 600) {
            //Device is a 7" tablet
            str = TABLET;
        } else {
            str = "Mobile";
        }
        return str;
    }

    //HTTP post request
    public static String POST(String url, JSONObject jsonObject) {
        InputStream inputStream = null;
        String result = "";
        try {

            HttpClient httpclient = new DefaultHttpClient();
            Logger.e(TAG, "url:: " + url);
            HttpPost httpPost = new HttpPost(url);
            HttpParams httpParameters = new BasicHttpParams();
            // Set the timeout in milliseconds until a connection is
            // established.
            int timeoutConnection = 200000;
            HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
            // Set the default socket timeout (SO_TIMEOUT) in milliseconds which
            // is the timeout for waiting for data.
            int timeoutSocket = 200000;
            HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
            String json = "";
            Logger.e(TAG, "request:: " + jsonObject.toString());
            json = jsonObject.toString();
            StringEntity se = new StringEntity(json);
            httpPost.setEntity(se);
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");
            HttpResponse httpResponse = httpclient.execute(httpPost);
            inputStream = httpResponse.getEntity().getContent();
            if (inputStream != null) {
                result = convertInputStreamToString(inputStream);
                Logger.e("TAG", "result -- " + result);
            } else {
                result = "Did not work!";
                Logger.e("TAG", "result -- " + result);
            }

        } catch (SocketTimeoutException e) {
            e.printStackTrace();
        } catch (Exception e) {
            Logger.d("InputStream", e.getLocalizedMessage());
        }

        return result;
    }

    public static String postRequest(String url) {
        String result = null;
        try {
            Logger.e("TAG", "url:: " + url);
            /*
             * for (NameValuePair nvp : nameValuePairs) { String name =
			 * nvp.getName(); String value = nvp.getValue(); Loggger.e("TAG",
			 * name +"="+value); }
			 */
            // Execute HTTP Post Request
            HttpClient httpclient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(url);
            HttpParams httpParameters = new BasicHttpParams();
            // Set the timeout in milliseconds until a connection is
            // established.
            int timeoutConnection = 20000;
            HttpConnectionParams.setConnectionTimeout(httpParameters,
                    timeoutConnection);
            // Set the default socket timeout (SO_TIMEOUT) in milliseconds which
            // is the timeout for waiting for data.
            int timeoutSocket = 20000;
            HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
            // httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            HttpResponse response1 = httpclient.execute(httpGet);

            result = EntityUtils.toString(response1.getEntity());

            int maxLogSize = 1000;
            int start = 0, end = 0;
            for (int i = 0; i <= result.length() / maxLogSize; i++) {
                start = i * maxLogSize;
                end = (i + 1) * maxLogSize;
                end = end > result.length() ? result.length() : end;
                Logger.i("TAG", "" + result.substring(start, end));
            }
            return result;
        } catch (SocketTimeoutException e) {
            e.printStackTrace();
            return null;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // convert inputstream to String
    private static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while ((line = bufferedReader.readLine()) != null)
            result += line;
//		Logger.e("TAG", "result -- " + result);
        inputStream.close();
        return result;
    }

    public static String getTimeZoneId() {
        String timeZone = TimeZone.getDefault().getID();
        Logger.e(TAG, "Time Zone :: " + timeZone);
        return timeZone;
    }
}
