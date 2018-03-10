package com.rogi.Activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
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
import com.rogi.Model.CompanyModel;
import com.rogi.R;
import com.rogi.View.Utils;
import com.rogi.logger.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;



public class ProjectResponsibilityActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    public static final String TAG = "ProjectResponsibilityActivity";
    private SharedPreferences mSharedPreferences;
    private AlertDialog.Builder builder;
    private AlertDialog dialog;
    ArrayList<CompanyModel> projectResArrayList = new ArrayList<>();
    ArrayList<CompanyModel> tempProjectResArrayList = new ArrayList<>();

    RequestQueue requestQueue;
    String TOKEN, USERID;
    Bundle bundle;
    StringBuffer projectResIDs, projectResTitles;
    MyAdapter myAdapter;
    ProgressDialog progressDialog;
    String responsiblityID = "", addedProResponsibility = "";

    RelativeLayout rootLayout;
    LinearLayout linDefault;
    ListView lisProject;
    TextView btnAdd, btnCancel, titleText, addDescBTN, resetBTN;
    ImageView addProfileView, imgClose;
    EditText edtAddProResponsibility;
    JSONArray responsibilityArray;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_responsibility);
        requestQueue = Volley.newRequestQueue(this);
        mSharedPreferences = getSharedPreferences(Utils.PREF_NAME, MODE_PRIVATE);
        USERID = mSharedPreferences.getString(Utils.USER_ID, "");
        TOKEN = mSharedPreferences.getString(Utils.TOKEN, "");

        init();

    }

    public void init() {
        titleText = (TextView) findViewById(R.id.titleText);
        titleText.setText(getString(R.string.additional_project_responsibility));
        addProfileView = (ImageView) findViewById(R.id.addProfileView);
        addProfileView.setVisibility(View.VISIBLE);
        findViewById(R.id.backLayoutclick).setOnClickListener(this);

        rootLayout = (RelativeLayout) findViewById(R.id.rootLayout);
        lisProject = (ListView) findViewById(R.id.lisProject);
        linDefault = (LinearLayout) findViewById(R.id.linDefault);
        linDefault.setVisibility(View.GONE);

        btnAdd = (TextView) findViewById(R.id.btnAdd);
        btnCancel = (TextView) findViewById(R.id.btnCancel);

        btnAdd.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        addProfileView.setOnClickListener(this);

        projectResArrayList = new ArrayList<>();
        tempProjectResArrayList = new ArrayList<>();

        Intent intent = getIntent();
        bundle = intent.getBundleExtra("BUNDLE");
        if (bundle != null) {
            projectResArrayList = (ArrayList<CompanyModel>) bundle.getSerializable(Utils.PROJECT_RESPONSIBILITY);
            responsiblityID = bundle.getString(Utils.RESPONSIBILITY_ID);
        }

        if (projectResArrayList.isEmpty()) {
            linDefault.setVisibility(View.VISIBLE);
        }

        if (Utils.checkInternetConnection(ProjectResponsibilityActivity.this)) {
            getProjectResponsibilityList();
        } else {
            if (Utils.validateString(responsiblityID)) {
                String[] parts = responsiblityID.split(",");
                for (String part : parts) {
                    for (int i = 0; i < projectResArrayList.size(); i++) {
                        if (part.equalsIgnoreCase(projectResArrayList.get(i).getId())) {
                            projectResArrayList.get(i).setSelected(true);
                        }
                    }
                }
            }
        }

        myAdapter = new MyAdapter(this, projectResArrayList);
        lisProject.setAdapter(myAdapter);
        lisProject.setOnItemClickListener(this);

    }

    @Override
    protected void onResume() {
        super.onResume();

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.btnAdd:
                projectResTitles = new StringBuffer();
                projectResTitles.append("");

                projectResIDs = new StringBuffer();
                projectResIDs.append("");

                for (int i = 0; i < projectResArrayList.size(); i++) {

                    if (projectResArrayList.get(i).isSelected()) {
                        projectResTitles.append(projectResArrayList.get(i).getTitle() + ", ");
                        projectResIDs.append(projectResArrayList.get(i).getId() + ",");
                    }
                }
                if (Utils.validateString(projectResTitles.toString())) {
                    addedProResponsibility = projectResTitles.substring(0, projectResTitles.length() - 2);
                    responsiblityID = projectResIDs.substring(0, projectResIDs.length() - 1);
                } else {
                    addedProResponsibility = "";
                    responsiblityID = "";
                }

                if (Utils.validateString(addedProResponsibility) && Utils.validateString(responsiblityID)) {
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra(Utils.SWITCH, 0);
                    returnIntent.putExtra(Utils.ADDEDPRORESPONSIBILITY_ARRAY, addedProResponsibility);
                    returnIntent.putExtra(Utils.RESPONSIBILITYID_ARRAY, responsiblityID);
                    returnIntent.putExtra(Utils.PROJECT_RESPONSIBILITY, projectResArrayList);
                    setResult(Activity.RESULT_OK, returnIntent);
                    finish();
                } else {
                    showMessage("Please Select Project Responsibility.");
                }

                break;

            case R.id.btnCancel:
                finish();
                break;

            case R.id.addProfileView:
                openNewProResponsibilityView();
                break;

            case R.id.resetBTN:
                edtAddProResponsibility.setText("");
                dialog.dismiss();
                break;

            case R.id.addDescBTN:
//                Utils.hideSoftKeyboard(ProjectResponsibilityActivity.this);
                String responsibility = edtAddProResponsibility.getText().toString().trim();
                if (Utils.validateString(responsibility)) {
                    try {
                        if (Utils.checkInternetConnection(this)) {
                            responsibilityArray = new JSONArray();
                            JSONObject noteObject = new JSONObject();
                            noteObject.put("title", responsibility);
                            noteObject.put("responsibliy_temp_id", "");
                            responsibilityArray.put(noteObject);
                            AddProjectResponsibility(responsibilityArray);
                        } else {
                            String responsibilityList = mSharedPreferences.getString(Utils.RESPONSIBILITY_LIST, "");
                            JSONArray dataArray = new JSONArray(responsibilityList);
                            Logger.e(TAG, "oldDataArray :: " + dataArray);
                            JSONArray newDataArray = new JSONArray();
                            JSONObject jsonObj;

                            for (int i = 0; i < dataArray.length(); i++) {
                                jsonObj = dataArray.getJSONObject(i);
                                newDataArray.put(jsonObj);
                            }

                            String temp = Utils.getRandomAlphaNumeric(6);
                            jsonObj = new JSONObject();
                            jsonObj.put("id", temp);
                            jsonObj.put("title", responsibility);
                            jsonObj.put("user_id", USERID);
                            newDataArray.put(jsonObj);

                            Logger.e(TAG, "newDataArray :: " + newDataArray);
                            Utils.storeString(mSharedPreferences, Utils.RESPONSIBILITY_LIST, newDataArray.toString());
                            Utils.storeString(mSharedPreferences, Utils.TEMP_RESPONSIBILITY_LIST, newDataArray.toString());
                            setProjectResponsibility(newDataArray);
                            dialog.dismiss();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    showMessage("Please Enter Project Responsibility.");
                }

                break;
            case R.id.imgClose:
                dialog.dismiss();
                break;

            case R.id.backLayoutclick:
                finish();
                break;

        }
    }

    public void getProjectResponsibilityList() {

        progressDialog = new ProgressDialog(ProjectResponsibilityActivity.this);
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
                        try {
                            boolean success = response.getBoolean("success");
                            String msg = response.getString("message");

                            if (success) {
                                projectResArrayList.clear();
                                JSONArray dataArray = response.getJSONArray("data");
                                Utils.storeString(mSharedPreferences, Utils.RESPONSIBILITY_LIST, dataArray.toString());
                                for (int i = 0; i < dataArray.length(); i++) {
                                    JSONObject object = dataArray.getJSONObject(i);
                                    CompanyModel projectModel = new CompanyModel();
                                    projectModel.setId(object.getString("id"));
                                    projectModel.setTitle(object.getString("title"));
                                    projectResArrayList.add(projectModel);
                                }

                                if (Utils.validateString(responsiblityID)) {
                                    String[] parts = responsiblityID.split(",");
                                    for (String part : parts) {
                                        for (int i = 0; i < projectResArrayList.size(); i++) {
                                            if (part.equalsIgnoreCase(projectResArrayList.get(i).getId())) {
                                                projectResArrayList.get(i).setSelected(true);
                                            }
                                        }
                                    }
                                }

                                myAdapter.notifyDataSetChanged();

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
                            progressDialog.dismiss();
                            showMessage(getString(R.string.checkInternet));
                        }
                    }
                });
        int socketTimeout = 30000;//30 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsObjRequest.setRetryPolicy(policy);
        requestQueue.add(jsObjRequest);
    }

    public void AddProjectResponsibility(JSONArray responsibility) {

        progressDialog = new ProgressDialog(ProjectResponsibilityActivity.this);
        progressDialog.setMessage(getString(R.string.please_wait));
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        JSONObject params = new JSONObject();
        try {
            params.put("user_id", USERID);
            params.put("session_token", TOKEN);
            params.put("responsibliy", responsibility);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        Logger.e(TAG, "Add Project Responsibility PARA---->" + String.valueOf(params));
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.POST, Utils.MAIN_URL + Utils.ADD_PROJCET_RESPONSIBILITY_API, params, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Logger.e(TAG, "URL ::" + Utils.MAIN_URL + Utils.ADD_PROJCET_RESPONSIBILITY_API);
                        Logger.e(TAG, "Add Project Responsibility RES---->" + String.valueOf(response));
                        progressDialog.dismiss();
                        try {
                            boolean success = response.getBoolean("success");
                            String msg = response.getString("message");

                            if (success) {
                                if (!response.isNull("data")) {
                                    Utils.projectFilterRefreshFlag = true;
                                    Utils.projectRefreshFlag = true;
                                    JSONArray dataArray = response.getJSONArray("data");
                                    Utils.storeString(mSharedPreferences, Utils.RESPONSIBILITY_LIST, dataArray.toString());
                                    setProjectResponsibility(dataArray);
                                } else {
                                    showMessage(msg);
                                }

                            } else {
                                showMessage(msg);
                            }

                            dialog.dismiss();
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
                            progressDialog.dismiss();
                            showMessage(getString(R.string.checkInternet));
                        }
                    }
                });
        int socketTimeout = 30000;//30 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsObjRequest.setRetryPolicy(policy);
        requestQueue.add(jsObjRequest);
    }

    public void setProjectResponsibility(JSONArray dataArray) {
        tempProjectResArrayList.clear();
        tempProjectResArrayList.addAll(projectResArrayList);
        Logger.e(TAG, "tempProjectResArrayList size : " + tempProjectResArrayList.size());
        projectResArrayList.clear();

        try {
            for (int i = 0; i < dataArray.length(); i++) {
                JSONObject object = dataArray.getJSONObject(i);
                CompanyModel projectModel = new CompanyModel();
                projectModel.setId(object.getString("id"));
                projectModel.setTitle(object.getString("title"));
                projectResArrayList.add(projectModel);
            }
            Logger.e(TAG, "projectResArrayList size : " + projectResArrayList.size());

            for (int i = 0; i < projectResArrayList.size(); i++) {
                linDefault.setVisibility(View.GONE);
                for (int j = 0; j < tempProjectResArrayList.size(); j++) {
                    if (projectResArrayList.get(i).getId().equalsIgnoreCase(tempProjectResArrayList.get(j).getId())) {
                        if (tempProjectResArrayList.get(j).isSelected()) {
                            projectResArrayList.get(i).setSelected(true);
                        }
                    }

                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        myAdapter.notifyDataSetChanged();

    }

    public void openNewProResponsibilityView() {
        builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.add_photo_description_view, null);
        builder.setView(dialogView);

        addDescBTN = (TextView) dialogView.findViewById(R.id.addDescBTN);
        edtAddProResponsibility = (EditText) dialogView.findViewById(R.id.edtDescription);
        edtAddProResponsibility.setHint("Add Project Responsibility");
        resetBTN = (TextView) dialogView.findViewById(R.id.resetBTN);
        imgClose = (ImageView) dialogView.findViewById(R.id.imgClose);

        resetBTN.setOnClickListener(this);
        addDescBTN.setOnClickListener(this);
        imgClose.setOnClickListener(this);

        dialog = builder.create();
        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.CENTER;
        window.setAttributes(wlp);
        dialog.show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
        CheckBox checkbox = (CheckBox) v.getTag(R.id.chkboxProject);
//        Toast.makeText(v.getContext(), isCheckedOrNot(checkbox), Toast.LENGTH_LONG).show();
    }

    private String isCheckedOrNot(CheckBox checkbox) {
        if (checkbox.isChecked())
            return "is checked";
        else
            return "is not checked";
    }


    public class MyAdapter extends ArrayAdapter<CompanyModel> {

        private final ArrayList<CompanyModel> list;
        private final Activity context;
        boolean checkAll_flag = false;
        boolean checkItem_flag = false;

        public MyAdapter(Activity context, ArrayList<CompanyModel> list) {
            super(context, R.layout.raw_project_add, list);
            this.context = context;
            this.list = list;
        }

        class ViewHolder {
            protected TextView text;
            protected CheckBox checkbox;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder viewHolder = null;
            if (convertView == null) {
                LayoutInflater inflator = context.getLayoutInflater();
                convertView = inflator.inflate(R.layout.raw_project_add, null);
                viewHolder = new ViewHolder();
                viewHolder.checkbox = (CheckBox) convertView.findViewById(R.id.chkboxProject);
                viewHolder.checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        int getPosition = (Integer) buttonView.getTag();
                        list.get(getPosition).setSelected(buttonView.isChecked());
                    }
                });
                convertView.setTag(viewHolder);
                convertView.setTag(R.id.chkboxProject, viewHolder.checkbox);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.checkbox.setTag(position); // This line is important.

            viewHolder.checkbox.setText(list.get(position).getTitle());
            viewHolder.checkbox.setTypeface(Utils.getTypeFace(context));
            viewHolder.checkbox.setChecked(list.get(position).isSelected());

            return convertView;
        }
    }

    private void showMessage(String message) {
        Utils.showResponseMessage(this, rootLayout, message);
    }
}



