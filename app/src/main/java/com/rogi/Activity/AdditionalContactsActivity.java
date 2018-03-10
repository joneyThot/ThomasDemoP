package com.rogi.Activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
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
import com.rogi.Adapter.CompanyAdapter;
import com.rogi.Database.DBHelper;
import com.rogi.Model.AdditionalContactModel;
import com.rogi.Model.CompanyModel;
import com.rogi.R;
import com.rogi.View.Utils;
import com.rogi.logger.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.StringTokenizer;


public class AdditionalContactsActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String TAG = "AdditionalContactsActivity";
    private AlertDialog.Builder builder;
    private AlertDialog dialog;
    private SharedPreferences mSharedPreferences;
    private DBHelper mDbHelper;

    LinearLayout linMainAdditional, backLayoutclick, linAddProject;
    TextView titleText, btnAddContacts;
    EditText edtAdditionalName, edtAdditionalTitle, edtAdditionalAddress, edtAdditionalPhoneno, edtAdditionalEmail,
            edtAdditionalCity, edtAdditionalState, edtAdditionalCountry, edtAdditionalZipCode, edtProResponsibility;
    ListView lisProject;
    //    ArrayList<CompanyModel> complanyArrayList = new ArrayList<>();
    ArrayList<CompanyModel> projectResArrayList = new ArrayList<>();

    RequestQueue requestQueue;
    ProgressDialog pdialog, progressDialog;
    String TOKEN, USERID;
    CompanyAdapter companyAdapter;
    String project_id = "", additional_contacts_id = "", name = "", companyTitle = "", address = "",
            phoneNo = "", email = "", addUpdateflag = "", responsibilityId = "", responsibilityName = "", contactStatus = "", isSync = "";
    StringBuffer projectResIDs, projectResTitles;
    Bundle bundle;
    JSONArray jArray;
    Boolean callFlag = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_additional_contacts);
        requestQueue = Volley.newRequestQueue(this);
        mSharedPreferences = getSharedPreferences(Utils.PREF_NAME, MODE_PRIVATE);
        USERID = mSharedPreferences.getString(Utils.USER_ID, "");
        TOKEN = mSharedPreferences.getString(Utils.TOKEN, "");

        init();

    }

    public void init() {
        linMainAdditional = (LinearLayout) findViewById(R.id.linMainAdditional);

        titleText = (TextView) findViewById(R.id.titleText);
        titleText.setText(getString(R.string.additional_title));

        edtAdditionalName = (EditText) findViewById(R.id.edtAdditionalName);
        edtAdditionalTitle = (EditText) findViewById(R.id.edtAdditionalTitle);
        edtAdditionalAddress = (EditText) findViewById(R.id.edtAdditionalAddress);
        edtAdditionalCity = (EditText) findViewById(R.id.edtAdditionalCity);
        edtAdditionalState = (EditText) findViewById(R.id.edtAdditionalState);
        edtAdditionalCountry = (EditText) findViewById(R.id.edtAdditionalCountry);
        edtAdditionalZipCode = (EditText) findViewById(R.id.edtAdditionalZipCode);
        edtAdditionalPhoneno = (EditText) findViewById(R.id.edtAdditionalPhoneno);
        edtAdditionalEmail = (EditText) findViewById(R.id.edtAdditionalEmail);
        edtProResponsibility = (EditText) findViewById(R.id.edtProResponsibility);

        linAddProject = (LinearLayout) findViewById(R.id.linAddProject);
        btnAddContacts = (TextView) findViewById(R.id.btnAddContacts);

        backLayoutclick = (LinearLayout) findViewById(R.id.backLayoutclick);

        linAddProject.setOnClickListener(this);
        btnAddContacts.setOnClickListener(this);
//        edtAdditionalTitle.setOnClickListener(this);
        backLayoutclick.setOnClickListener(this);

        edtAdditionalName.setTypeface(Utils.getTypeFace(AdditionalContactsActivity.this));
        edtAdditionalTitle.setTypeface(Utils.getTypeFace(AdditionalContactsActivity.this));
        edtAdditionalAddress.setTypeface(Utils.getTypeFace(AdditionalContactsActivity.this));
        edtAdditionalCity.setTypeface(Utils.getTypeFace(AdditionalContactsActivity.this));
        edtAdditionalState.setTypeface(Utils.getTypeFace(AdditionalContactsActivity.this));
        edtAdditionalCountry.setTypeface(Utils.getTypeFace(AdditionalContactsActivity.this));
        edtAdditionalZipCode.setTypeface(Utils.getTypeFace(AdditionalContactsActivity.this));
        edtAdditionalPhoneno.setTypeface(Utils.getTypeFace(AdditionalContactsActivity.this));
        edtAdditionalEmail.setTypeface(Utils.getTypeFace(AdditionalContactsActivity.this));
        edtProResponsibility.setTypeface(Utils.getTypeFace(AdditionalContactsActivity.this));

//        complanyArrayList = new ArrayList<>();
        projectResArrayList = new ArrayList<>();
        mDbHelper = new DBHelper(this);

        bundle = getIntent().getExtras();
        if (bundle != null) {
            addUpdateflag = bundle.getString(Utils.ADDITIONAL_CONTACT);
            project_id = bundle.getString(Utils.PROJECT_ID);
        }

        /*if (Utils.validateString(mSharedPreferences.getString(Utils.COMPANY_LIST, ""))) {
            String companyList = mSharedPreferences.getString(Utils.COMPANY_LIST, "");
            JSONArray dataArray = null;
            try {
                dataArray = new JSONArray(companyList);
                for (int i = 0; i < dataArray.length(); i++) {
                    JSONObject object = dataArray.getJSONObject(i);
                    CompanyModel companyModel = new CompanyModel();
                    companyModel.setId(object.getString("id"));
                    companyModel.setName(object.getString("name"));
                    complanyArrayList.add(companyModel);
                }
            } catch (JSONException e) {

            }
        }*/

        if (Utils.validateString(mSharedPreferences.getString(Utils.RESPONSIBILITY_LIST, ""))) {
            String responsibilityList = mSharedPreferences.getString(Utils.RESPONSIBILITY_LIST, "");
            JSONArray dataArray = null;
            try {
                dataArray = new JSONArray(responsibilityList);
                for (int i = 0; i < dataArray.length(); i++) {
                    JSONObject object = dataArray.getJSONObject(i);
                    CompanyModel projectModel = new CompanyModel();
                    projectModel.setId(object.getString("id"));
                    projectModel.setTitle(object.getString("title"));
                    projectResArrayList.add(projectModel);
                }
            } catch (JSONException e) {

            }
        }

        projectResTitles = new StringBuffer();
        projectResTitles.append("");

        projectResIDs = new StringBuffer();
        projectResIDs.append("");

        if (Utils.validateString(addUpdateflag)) {
            btnAddContacts.setText(getString(R.string.additional_update_contacts));
            setUpdatedData();
        } else {
            btnAddContacts.setText(getString(R.string.additional_add_contacts));
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

            edtAdditionalPhoneno.setFilters(new InputFilter[]{filter});
        }

//        getComplanyList();
//        getProjectResponsibilityList();

    }

    @Override
    protected void onResume() {
        super.onResume();

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.linAddProject:
//                if (Utils.validateString(responsibilityId)) {
//                    String[] parts = responsibilityId.split(",");
//                    for (String part : parts) {
//                        for (int i = 0; i < projectResArrayList.size(); i++) {
//                            if (part.equalsIgnoreCase(projectResArrayList.get(i).getId())) {
//                                projectResArrayList.get(i).setSelected(true);
//                            }
//                        }
//                    }
//                }
                Intent intent = new Intent(AdditionalContactsActivity.this, ProjectResponsibilityActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable(Utils.PROJECT_RESPONSIBILITY, (Serializable) projectResArrayList);
                bundle.putString(Utils.RESPONSIBILITY_ID, responsibilityId);
                intent.putExtra("BUNDLE", bundle);
                startActivityForResult(intent, 1);

                break;

           /* case R.id.edtAdditionalTitle:
//                openCompanyView();
                break;*/

            case R.id.btnAddContacts:
                callFlag = false;
                if (fieldValidationNameAndCompanyName()) {
                    name = edtAdditionalName.getText().toString().trim();
                    companyTitle = edtAdditionalTitle.getText().toString().trim();

                    if (Utils.validateString(edtAdditionalAddress.getText().toString().trim()) || Utils.validateString(edtAdditionalAddress.getText().toString().trim())
                            || Utils.validateString(edtAdditionalAddress.getText().toString().trim()) || Utils.validateString(edtAdditionalAddress.getText().toString().trim())
                            || Utils.validateString(edtAdditionalAddress.getText().toString().trim())) {

                        if (fieldValidationAddress()) {
                            address = edtAdditionalAddress.getText().toString().trim() + "&$&"
                                    + edtAdditionalCity.getText().toString().trim() + "&$&"
                                    + edtAdditionalState.getText().toString().trim() + "&$&"
                                    + edtAdditionalCountry.getText().toString().trim() + "&$&"
                                    + edtAdditionalZipCode.getText().toString().trim();
                        }
                    } else {
                        address = "";
                    }

                    if (fieldValidationEmail()) {
                        email = edtAdditionalEmail.getText().toString().trim();
                    } else {
                        email = "";
                    }

                    if (fieldValidationPhone()) {
                        String str = edtAdditionalPhoneno.getText().toString().trim().replaceAll("\\D+", "");
                        phoneNo = str;
                    } else {
                        phoneNo = "";
                    }

                    if (Utils.validateString(edtProResponsibility.getText().toString()) && Utils.validateString(responsibilityId)) {
                        responsibilityName = edtProResponsibility.getText().toString().trim();
                    } else {
                        responsibilityName = "";
                        responsibilityId = "";
                    }

                    if (!callFlag) {
                        String projectData = mSharedPreferences.getString(Utils.TASK_OBJECT, "");

                        if (Utils.checkInternetConnection(this)) {
                            if (!Utils.validateString(addUpdateflag)) {
                                AddAdditionalContact(project_id, name, companyTitle, address, phoneNo, email, responsibilityId);
                            } else {
                                UpdateAdditionalContact(project_id, additional_contacts_id, name, companyTitle, address, phoneNo, email, responsibilityId);
                            }
                        } else {
                            if (!Utils.validateString(addUpdateflag)) {
                                // Insert Additional Contact
                                try {
                                    ArrayList<AdditionalContactModel> additionalContactModelArrayList = new ArrayList<>();
                                    String tempId = Utils.getRandomAlphaNumeric(6);
                                    String created_date = Utils.getCurrentDateAndTime();
                                    try {
                                        mDbHelper.openDataBase();
                                        mDbHelper.insertProjectAdditionalContact(mDbHelper.myDataBase, tempId, project_id, name, companyTitle,
                                                address, phoneNo, email, responsibilityName, responsibilityId, "N", "A", created_date);
                                        additionalContactModelArrayList = mDbHelper.getAdditionalContactDetails();
                                        mDbHelper.close();
                                    } catch (SQLException e) {
                                        e.printStackTrace();
                                    }

                                    if (additionalContactModelArrayList.size() > 0) {
                                        JSONObject object = new JSONObject(projectData);
                                        JSONArray additionalContactsArray = object.getJSONArray("additional_contacts");
                                        JSONArray additionalContactsNewArray = new JSONArray();
                                        JSONObject contactObj;
                                        Logger.e(TAG, "Additional Contact Array before :" + additionalContactsArray);
                                        if (additionalContactsArray.length() > 0) {
                                            for (int i = 0; i < additionalContactsArray.length(); i++) {
                                                contactObj = additionalContactsArray.getJSONObject(i);
                                                additionalContactsNewArray.put(contactObj);
                                            }
                                        }

                                        for (int j = 0; j < additionalContactModelArrayList.size(); j++) {
                                            if (tempId.equalsIgnoreCase(additionalContactModelArrayList.get(j).getAdtnlContactID())) {
                                                contactObj = new JSONObject();
                                                contactObj.put("id", additionalContactModelArrayList.get(j).getAdtnlContactID());
                                                contactObj.put("name", additionalContactModelArrayList.get(j).getAdtnlContactName());
                                                contactObj.put("company", additionalContactModelArrayList.get(j).getAdtnlContactCompany());
                                                contactObj.put("address", additionalContactModelArrayList.get(j).getAdtnlContactAddress());
                                                contactObj.put("phone", additionalContactModelArrayList.get(j).getAdtnlContactPhone());
                                                contactObj.put("email", additionalContactModelArrayList.get(j).getAdtnlContactEmail());
                                                if (Utils.validateString(additionalContactModelArrayList.get(j).getAdtnlContactResponsibility())) {
                                                    contactObj.put("project_responsiblity", additionalContactModelArrayList.get(j).getAdtnlContactResponsibility());
                                                } else {
                                                    contactObj.put("project_responsiblity", "");
                                                }
                                                if (Utils.validateString(additionalContactModelArrayList.get(j).getAdtnlContactResponsibilityId())) {
                                                    contactObj.put("project_responsiblity_id", additionalContactModelArrayList.get(j).getAdtnlContactResponsibilityId());
                                                } else {
                                                    contactObj.put("project_responsiblity_id", "");
                                                }
                                                contactObj.put("is_sync", additionalContactModelArrayList.get(j).getIs_sync());
                                                contactObj.put("additional_contact_operation", additionalContactModelArrayList.get(j).getContact_status());
                                                contactObj.put("created_date", additionalContactModelArrayList.get(j).getCreated_date());
                                                additionalContactsNewArray.put(contactObj);
                                            }
                                        }

                                        Logger.e(TAG, "Additional Contact Array after :" + additionalContactsNewArray);
                                        object.put("additional_contacts", additionalContactsNewArray);
                                        if (object.getString("project_operation").equalsIgnoreCase("A") &&
                                                object.getString("is_sync").equalsIgnoreCase("N")) {
                                            object.put("is_sync", "N");
                                            object.put("project_operation", "A");
                                        } else {
                                            object.put("is_sync", "N");
                                            object.put("project_operation", "U");
                                        }
                                        Logger.e(TAG, "Project Object :" + object);

                                        Utils.storeString(mSharedPreferences, Utils.TASK_OBJECT, object.toString());
                                        Intent returnIntent = new Intent();
                                        returnIntent.putExtra(Utils.SWITCH, 0);
                                        returnIntent.putExtra(Utils.TASK_OBJECT, mSharedPreferences.getString(Utils.TASK_OBJECT, ""));
                                        setResult(Activity.RESULT_OK, returnIntent);
                                        finish();
                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            } else {
                                // Update Additional Contact

                                try {
                                    ArrayList<AdditionalContactModel> additionalContactModelArrayList = new ArrayList<>();
                                    String created_date = Utils.getCurrentDateAndTime();
                                    try {
                                        mDbHelper.openDataBase();
                                        if (contactStatus.equalsIgnoreCase("A") && isSync.equalsIgnoreCase("N")) {
                                            mDbHelper.updateProjectAdditionalContact(additional_contacts_id, project_id, name, companyTitle,
                                                    address, phoneNo, email, responsibilityName, responsibilityId, "N", "A", created_date);
                                        } else {
                                            mDbHelper.updateProjectAdditionalContact(additional_contacts_id, project_id, name, companyTitle,
                                                    address, phoneNo, email, responsibilityName, responsibilityId, "N", "U", created_date);
                                        }
                                        additionalContactModelArrayList = mDbHelper.getAdditionalContactDetails();
                                        mDbHelper.close();
                                    } catch (SQLException e) {
                                        e.printStackTrace();
                                    }

                                    if (additionalContactModelArrayList.size() > 0) {
                                        JSONObject object = new JSONObject(projectData);
                                        JSONArray additionalContactsArray = object.getJSONArray("additional_contacts");
                                        Logger.e(TAG, "Additional Contact Array before :" + additionalContactsArray);
                                        if (additionalContactsArray.length() > 0) {
                                            for (int i = 0; i < additionalContactsArray.length(); i++) {
                                                if (additional_contacts_id.equalsIgnoreCase(additionalContactsArray.getJSONObject(i).getString("id"))) {
                                                    for (int j = 0; j < additionalContactModelArrayList.size(); j++) {
                                                        if (additional_contacts_id.equalsIgnoreCase(additionalContactModelArrayList.get(j).getAdtnlContactID())) {
                                                            additionalContactsArray.getJSONObject(i).put("id", additionalContactModelArrayList.get(j).getAdtnlContactID());
                                                            additionalContactsArray.getJSONObject(i).put("name", additionalContactModelArrayList.get(j).getAdtnlContactName());
                                                            additionalContactsArray.getJSONObject(i).put("company", additionalContactModelArrayList.get(j).getAdtnlContactCompany());
                                                            additionalContactsArray.getJSONObject(i).put("address", additionalContactModelArrayList.get(j).getAdtnlContactAddress());
                                                            additionalContactsArray.getJSONObject(i).put("phone", additionalContactModelArrayList.get(j).getAdtnlContactPhone());
                                                            additionalContactsArray.getJSONObject(i).put("email", additionalContactModelArrayList.get(j).getAdtnlContactEmail());
                                                            if (Utils.validateString(additionalContactModelArrayList.get(j).getAdtnlContactResponsibility())) {
                                                                additionalContactsArray.getJSONObject(i).put("project_responsiblity", additionalContactModelArrayList.get(j).getAdtnlContactResponsibility());
                                                            } else {
                                                                additionalContactsArray.getJSONObject(i).put("project_responsiblity", "");
                                                            }
                                                            if (Utils.validateString(additionalContactModelArrayList.get(j).getAdtnlContactResponsibilityId())) {
                                                                additionalContactsArray.getJSONObject(i).put("project_responsiblity_id", additionalContactModelArrayList.get(j).getAdtnlContactResponsibilityId());
                                                            } else {
                                                                additionalContactsArray.getJSONObject(i).put("project_responsiblity_id", "");
                                                            }

                                                            additionalContactsArray.getJSONObject(i).put("is_sync", additionalContactModelArrayList.get(j).getIs_sync());
                                                            additionalContactsArray.getJSONObject(i).put("additional_contact_operation", additionalContactModelArrayList.get(j).getContact_status());
                                                            additionalContactsArray.getJSONObject(i).put("created_date", additionalContactModelArrayList.get(j).getCreated_date());
                                                            break;
                                                        }
                                                    }
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

                                        Utils.storeString(mSharedPreferences, Utils.TASK_OBJECT, object.toString());
                                        Intent returnIntent = new Intent();
                                        returnIntent.putExtra(Utils.SWITCH, 0);
                                        returnIntent.putExtra(Utils.TASK_OBJECT, mSharedPreferences.getString(Utils.TASK_OBJECT, ""));
                                        setResult(Activity.RESULT_OK, returnIntent);
                                        finish();
                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }


                }
                break;

            case R.id.backLayoutclick:
                Utils.hideSoftKeyboard(this);
                finish();
                break;
        }
    }


    public void getProjectResponsibilityList() {

        progressDialog = new ProgressDialog(AdditionalContactsActivity.this);
        progressDialog.setMessage(getString(R.string.please_wait));
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

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
                        progressDialog.dismiss();
                        projectResArrayList.clear();
                        try {
                            boolean success = response.getBoolean("success");
                            String msg = response.getString("message");

                            if (success) {
                                JSONArray dataArray = response.getJSONArray("data");
                                Utils.storeString(mSharedPreferences, Utils.RESPONSIBILITY_LIST, dataArray.toString());
                                for (int i = 0; i < dataArray.length(); i++) {
                                    JSONObject object = dataArray.getJSONObject(i);
                                    CompanyModel projectModel = new CompanyModel();
                                    projectModel.setId(object.getString("id"));
                                    projectModel.setTitle(object.getString("title"));
                                    projectResArrayList.add(projectModel);
                                }

                                if (Utils.validateString(addUpdateflag)) {
                                    setUpdatedData();
                                }


                            } else {
                                showMessage(msg);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            progressDialog.dismiss();
                            Logger.e("ERR", e.getMessage());
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error instanceof NoConnectionError) {
                            showMessage(getString(R.string.checkInternet));
                        }
                        progressDialog.dismiss();
                    }
                });
        requestQueue.add(jsObjRequest);
    }

    private void setUpdatedData() {

        btnAddContacts.setText(getString(R.string.additional_update_contacts));
        String adtContachObj = bundle.getString(Utils.ADDITIONAL_CONTACT_OBJECT);
        try {
            JSONObject jObj = new JSONObject(adtContachObj);
            name = jObj.getString("name");
            additional_contacts_id = jObj.getString("id");
            companyTitle = jObj.getString("company");
            address = jObj.getString("address");
            if (Utils.validateString(jObj.getString("phone"))) {
                phoneNo = Utils.getPhoneNumberFormat(jObj.getString("phone"));
            }

            email = jObj.getString("email");
            contactStatus = jObj.getString("additional_contact_operation");
            isSync = jObj.getString("is_sync");

            edtAdditionalName.setText(name);
            edtAdditionalTitle.setText(companyTitle);
            if (Utils.validateString(address)) {
                StringTokenizer tokens = new StringTokenizer(address, "&$&");
                String address = tokens.nextToken();
                String city = tokens.nextToken();
                String state = tokens.nextToken();
                String country = tokens.nextToken();
                String zipcode = tokens.nextToken();

                Logger.e(TAG, "Address : " + address + ", " + city + ", " + state + ", " + country + ", " + zipcode);

                if (Utils.validateString(address)) {
                    edtAdditionalAddress.setText(address);
                    edtAdditionalAddress.setSelection(address.length());
                }
                if (Utils.validateString(city)) {
                    edtAdditionalCity.setText(city);
                    edtAdditionalCity.setSelection(city.length());
                }
                if (Utils.validateString(state)) {
                    edtAdditionalState.setText(state);
                    edtAdditionalState.setSelection(state.length());
                }
                if (Utils.validateString(country)) {
                    edtAdditionalCountry.setText(country);
                    edtAdditionalCountry.setSelection(country.length());
                }
                if (Utils.validateString(zipcode)) {
                    edtAdditionalZipCode.setText(zipcode);
                }

            }
            edtAdditionalPhoneno.setText(phoneNo);
            edtAdditionalEmail.setText(email);

            if (Utils.validateString(name))
                edtAdditionalName.setSelection(name.length());

            if (Utils.validateString(companyTitle))
                edtAdditionalTitle.setSelection(companyTitle.length());
//            edtAdditionalPhoneno.setSelection(phoneNo.length());
            if (Utils.validateString(email))
                edtAdditionalEmail.setSelection(email.length());

//            if (!jObj.isNull("project_responsiblity")) {
//                jArray = jObj.getJSONArray("project_responsiblity");
//                for (int i = 0; i < jArray.length(); i++) {
//                    JSONObject object = jArray.getJSONObject(i);
//                    String id = object.getString("id");
//                    for (int j = 0; j < projectResArrayList.size(); j++) {
//                        if (projectResArrayList.get(j).getId().equalsIgnoreCase(id)) {
//                            projectResArrayList.get(j).setSelected(true);
//                            projectResArrayList.get(j).setPos(String.valueOf(j));
//
//                            projectResTitles.append(projectResArrayList.get(j).getTitle() + ", ");
//                            projectResIDs.append(projectResArrayList.get(j).getId() + ",");
//                        }
//                    }
//                }
//            }

            if (!jObj.isNull("project_responsiblity") && !jObj.isNull("project_responsiblity_id")) {
                if (Utils.validateString(jObj.getString("project_responsiblity_id"))) {
                    String[] ids = jObj.getString("project_responsiblity_id").split(",");
                    for (String id : ids) {
                        for (int j = 0; j < projectResArrayList.size(); j++) {
                            if (projectResArrayList.get(j).getId().equalsIgnoreCase(id)) {
                                projectResArrayList.get(j).setSelected(true);
                                projectResArrayList.get(j).setPos(String.valueOf(j));

                                projectResTitles.append(projectResArrayList.get(j).getTitle() + ", ");
                                projectResIDs.append(projectResArrayList.get(j).getId() + ",");
                            }

                        }
                    }
                }
            }

            if (Utils.validateString(projectResTitles.toString())) {
                edtProResponsibility.setText(projectResTitles.substring(0, projectResTitles.length() - 2));
                responsibilityId = projectResIDs.substring(0, projectResIDs.length() - 1);
            } else {
                edtProResponsibility.setText("");
                responsibilityId = "";
            }

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

            edtAdditionalPhoneno.setFilters(new InputFilter[]{filter});

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void AddAdditionalContact(String project_id, String name, String companyTitle, String address, String phoneNo, String email, String projectID) {

        progressDialog = new ProgressDialog(AdditionalContactsActivity.this);
        progressDialog.setMessage(getString(R.string.please_wait));
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        JSONObject params = new JSONObject();
        try {
            params.put("user_id", USERID);
            params.put("session_token", TOKEN);
            params.put("project_id", project_id);
            params.put("name", name);
            params.put("company", companyTitle);
            params.put("address", address);
            params.put("phone", phoneNo);
            params.put("email", email);
            params.put("project_responsiblity_id", projectID);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        Logger.e(TAG, "Additional Contact PARA---->" + String.valueOf(params));
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.POST, Utils.MAIN_URL + Utils.ADDITIONAL_CONTACT_API, params, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Logger.e(TAG, "URL ::" + Utils.MAIN_URL + Utils.ADDITIONAL_CONTACT_API);
                        Logger.e(TAG, "Additional Contact RES---->" + String.valueOf(response));
                        progressDialog.dismiss();
                        try {
                            boolean success = response.getBoolean("success");
                            String msg = response.getString("message");

                            if (success) {
                                if (!response.isNull("data")) {
                                    Utils.projectFilterRefreshFlag = true;
                                    Utils.projectRefreshFlag = true;
                                    JSONObject jObj = response.getJSONObject("data");

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

                                    Utils.storeString(mSharedPreferences, Utils.TASK_OBJECT, jObj.toString());
                                    Intent returnIntent = new Intent();
                                    returnIntent.putExtra(Utils.SWITCH, 0);
                                    returnIntent.putExtra(Utils.TASK_OBJECT, jObj.toString());
                                    setResult(Activity.RESULT_OK, returnIntent);
                                    finish();
                                } else {
                                    showMessage(msg);
                                }

                            } else {
                                showMessage(msg);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            progressDialog.dismiss();
                            Logger.e("ERR", e.getMessage());
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error instanceof NoConnectionError) {
//                            showMessage(getString(R.string.checkInternet));
                        }
                        progressDialog.dismiss();
                        showMessage(error.toString());
                    }
                });
        int socketTimeout = 30000;//30 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsObjRequest.setRetryPolicy(policy);
        requestQueue.add(jsObjRequest);
    }

    public void UpdateAdditionalContact(String project_id, String additional_contacts_id, String name, String companyTitle, String address, String phoneNo, String email, String projectID) {

        progressDialog = new ProgressDialog(AdditionalContactsActivity.this);
        progressDialog.setMessage(getString(R.string.please_wait));
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        JSONObject params = new JSONObject();
        try {
            params.put("user_id", USERID);
            params.put("session_token", TOKEN);
            params.put("project_id", project_id);
            params.put("additional_contacts_id", additional_contacts_id);
            params.put("name", name);
            params.put("company", companyTitle);
            params.put("address", address);
            params.put("phone", phoneNo);
            params.put("email", email);
            params.put("project_responsiblity_id", projectID);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        Logger.e(TAG, "Update Additional Contact PARA---->" + String.valueOf(params));
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.POST, Utils.MAIN_URL + Utils.UPDATE_ADDITIONAL_CONTACT_API, params, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Logger.e(TAG, "URL ::" + Utils.MAIN_URL + Utils.UPDATE_ADDITIONAL_CONTACT_API);
                        Logger.e(TAG, "Update Additional Contact RES---->" + String.valueOf(response));
                        progressDialog.dismiss();
                        try {
                            boolean success = response.getBoolean("success");
                            String msg = response.getString("message");

                            if (success) {
                                if (!response.isNull("data")) {
                                    Utils.projectFilterRefreshFlag = true;
                                    Utils.projectRefreshFlag = true;
                                    JSONObject jObj = response.getJSONObject("data");

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

                                    Utils.storeString(mSharedPreferences, Utils.TASK_OBJECT, jObj.toString());
                                    Intent returnIntent = new Intent();
                                    returnIntent.putExtra(Utils.SWITCH, 0);
                                    returnIntent.putExtra(Utils.TASK_OBJECT, jObj.toString());
                                    setResult(Activity.RESULT_OK, returnIntent);
                                    finish();
                                } else {
                                    showMessage(msg);
                                }

                            } else {
                                showMessage(msg);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            progressDialog.dismiss();
                            Logger.e("ERR", e.getMessage());
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error instanceof NoConnectionError) {
//                            progressDialog.dismiss();
//                            showMessage(getString(R.string.checkInternet));
                        }
                        progressDialog.dismiss();
                        showMessage(error.toString());
                    }
                });
        int socketTimeout = 30000;//30 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsObjRequest.setRetryPolicy(policy);
        requestQueue.add(jsObjRequest);
    }


    /*public void openCompanyView() {
        builder = new AlertDialog.Builder(AdditionalContactsActivity.this);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.dialog_project_add, null);
        builder.setView(dialogView);

        dialogView.findViewById(R.id.relClose).setVisibility(View.GONE);
        dialogView.findViewById(R.id.linBottom).setVisibility(View.GONE);
        lisProject = (ListView) dialogView.findViewById(R.id.lisProject);

        companyAdapter = new CompanyAdapter(AdditionalContactsActivity.this, complanyArrayList, companyItemOnClick);
        lisProject.setAdapter(companyAdapter);

        dialog = builder.create();
        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.CENTER;
        window.setAttributes(wlp);
        dialog.show();
    }*/

    /*View.OnClickListener companyItemOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int index = (int) v.getTag();
            Log.d("INDEX", "" + index);
            edtAdditionalTitle.setText(complanyArrayList.get(index).getName());
            dialog.cancel();
        }
    };*/

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK) {
            String result = data.getStringExtra(Utils.ADDEDPRORESPONSIBILITY_ARRAY);
            switch (data.getIntExtra(Utils.SWITCH, 101)) {
                case 0:
                    String addedProResponsibility = result;
                    if (Utils.validateString(addedProResponsibility)) {
                        edtProResponsibility.setText(addedProResponsibility);
                        responsibilityId = data.getStringExtra(Utils.RESPONSIBILITYID_ARRAY);
                        projectResArrayList = (ArrayList<CompanyModel>) data.getSerializableExtra(Utils.PROJECT_RESPONSIBILITY);
                        Logger.e(TAG, "projectResArrayList Size : " + projectResArrayList.size());
                    }
                    break;

                default:
                    break;
            }

        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void showMessage(String message) {
        Utils.showResponseMessage(this, linMainAdditional, message);
    }

    /*public boolean fieldValidation() {
        boolean flag = true;
        if (!Utils.validateString(edtAdditionalName.getText().toString())) {
            flag = false;
            edtAdditionalName.requestFocus();
            showMessage(getString(R.string.enter_firstname));
        } else if (!Utils.validateString(edtAdditionalTitle.getText().toString())) {
            flag = false;
            edtAdditionalTitle.requestFocus();
            showMessage(getString(R.string.additional_select_company_name));
        } else if (!Utils.validateString(edtAdditionalAddress.getText().toString())) {
            flag = false;
            edtAdditionalAddress.requestFocus();
            showMessage(getString(R.string.additional_enter_address));
        } else if (!Utils.validateString(edtAdditionalCity.getText().toString())) {
            flag = false;
            edtAdditionalCity.requestFocus();
            showMessage(getString(R.string.enter_project_city));
        } else if (!Utils.validateString(edtAdditionalState.getText().toString())) {
            flag = false;
            edtAdditionalState.requestFocus();
            showMessage(getString(R.string.enter_project_state));
        } else if (!Utils.validateString(edtAdditionalCountry.getText().toString())) {
            flag = false;
            edtAdditionalCountry.requestFocus();
            showMessage(getString(R.string.enter_project_country));
        } else if (!Utils.validateString(edtAdditionalZipCode.getText().toString())) {
            flag = false;
            edtAdditionalZipCode.requestFocus();
            showMessage(getString(R.string.enter_project_zipcode));
        } else if (edtAdditionalZipCode.getText().toString().trim().length() < 3) {
            flag = false;
            edtAdditionalZipCode.requestFocus();
            showMessage(getString(R.string.zipcode_length));
        } else if (!Utils.validateString(edtAdditionalPhoneno.getText().toString())) {
            flag = false;
            edtAdditionalPhoneno.requestFocus();
            showMessage(getString(R.string.enter_phonenumber));
        } else if (edtAdditionalPhoneno.getText().toString().trim().length() < 14) {
            flag = false;
            edtAdditionalPhoneno.requestFocus();
            showMessage(getString(R.string.enter_phonenumber_length));
        } else if (!Utils.validateString(edtAdditionalEmail.getText().toString())) {
            flag = false;
            edtAdditionalEmail.requestFocus();
            showMessage(getString(R.string.enter_emailaddress));
        } else if (edtAdditionalEmail.getText().toString().length() > 0 && !Utils.isEmailValid(edtAdditionalEmail.getText().toString())) {
            flag = false;
            showMessage(getString(R.string.invalid_email));
            edtAdditionalEmail.requestFocus();
        } else if (!Utils.validateString(edtProResponsibility.getText().toString()) && !Utils.validateString(responsibilityId)) {
            flag = false;
            edtProResponsibility.requestFocus();
            showMessage(getString(R.string.additional_select_project_responsibility));
        }
        return flag;
    }*/

    public boolean fieldValidationNameAndCompanyName() {
        boolean flag = true;
        if (!Utils.validateString(edtAdditionalName.getText().toString())) {
            flag = false;
            edtAdditionalName.requestFocus();
            showMessage(getString(R.string.enter_firstname));
        } else if (!Utils.validateString(edtAdditionalTitle.getText().toString())) {
            flag = false;
            edtAdditionalTitle.requestFocus();
            showMessage(getString(R.string.additional_select_company_name));
        }
        return flag;
    }

    public boolean fieldValidationAddress() {
        boolean flag = true;
        if (!Utils.validateString(edtAdditionalAddress.getText().toString())) {
            flag = false;
            edtAdditionalAddress.requestFocus();
            showMessage(getString(R.string.additional_enter_address));
            callFlag = true;
        } else if (!Utils.validateString(edtAdditionalCity.getText().toString())) {
            flag = false;
            edtAdditionalCity.requestFocus();
            showMessage(getString(R.string.enter_project_city));
            callFlag = true;
        } else if (!Utils.validateString(edtAdditionalState.getText().toString())) {
            flag = false;
            edtAdditionalState.requestFocus();
            showMessage(getString(R.string.enter_project_state));
            callFlag = true;
        } else if (!Utils.validateString(edtAdditionalCountry.getText().toString())) {
            flag = false;
            edtAdditionalCountry.requestFocus();
            showMessage(getString(R.string.enter_project_country));
            callFlag = true;
        } else if (!Utils.validateString(edtAdditionalZipCode.getText().toString())) {
            flag = false;
            edtAdditionalZipCode.requestFocus();
            showMessage(getString(R.string.enter_project_zipcode));
            callFlag = true;
        } else if (edtAdditionalZipCode.getText().toString().trim().length() < 3) {
            flag = false;
            edtAdditionalZipCode.requestFocus();
            showMessage(getString(R.string.zipcode_length));
            callFlag = true;
        }
        return flag;
    }

    public boolean fieldValidationEmail() {
        boolean flag = true;
        if (Utils.validateString(edtAdditionalEmail.getText().toString())) {
            if (edtAdditionalEmail.getText().toString().length() > 0 && !Utils.isEmailValid(edtAdditionalEmail.getText().toString())) {
                flag = false;
                showMessage(getString(R.string.invalid_email));
                edtAdditionalEmail.requestFocus();
                callFlag = true;
            }
        } else {
            flag = false;
        }
        return flag;
    }

    public boolean fieldValidationPhone() {
        boolean flag = true;
        if (Utils.validateString(edtAdditionalPhoneno.getText().toString())) {
            if (edtAdditionalPhoneno.getText().toString().trim().length() < 14) {
                flag = false;
                edtAdditionalPhoneno.requestFocus();
                showMessage(getString(R.string.enter_phonenumber_length));
                callFlag = true;
            }
        } else {
            flag = false;
        }
        return flag;
    }

}



