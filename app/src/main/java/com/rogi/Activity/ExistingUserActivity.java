package com.rogi.Activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;

import com.rogi.Model.ExistingUserModel;
import com.rogi.R;
import com.rogi.View.TextViewPlus;
import com.rogi.View.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ExistingUserActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    public static final String TAG = "ExistingUserActivity";
    private SharedPreferences mSharedPreferences;

    ArrayList<ExistingUserModel> existingUserList = new ArrayList<>();
    ListView lisExitingUser;
    public TextView titleText, txtNumberOfUser, txtperUserPrice, txtPlanPrice, txtNewPrice, payNowBtn;
    int perUserRate = 0, planPrice = 0, newPrice = 0;
    String existingObjStr = "", planId = "", planName = "";
    ExistingUserAdapter existingUserAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exiting_user);
        mSharedPreferences = getSharedPreferences(Utils.PREF_NAME, MODE_PRIVATE);
        Intent intent = getIntent();
        perUserRate = Integer.parseInt(intent.getStringExtra(Utils.PER_RATE_USER));
        existingObjStr = intent.getStringExtra(Utils.EXITING_USER_OBJECT);
        planPrice = Integer.parseInt(intent.getStringExtra(Utils.PLAN_PRICE));
        planId = intent.getStringExtra(Utils.PLAN_ID);
        planName = intent.getStringExtra(Utils.PLAN_NAME);
        init();
    }

    public void init() {
        findViewById(R.id.backLayoutclick).setOnClickListener(this);
        titleText = (TextViewPlus) findViewById(R.id.titleText);
        titleText.setText("Existing Users");
        lisExitingUser = (ListView) findViewById(R.id.lisExitingUser);
        txtNumberOfUser = (TextView) findViewById(R.id.txtNumberOfUser);
        txtperUserPrice = (TextView) findViewById(R.id.txtperUserPrice);
        txtPlanPrice = (TextView) findViewById(R.id.txtPlanPrice);
        txtNewPrice = (TextView) findViewById(R.id.txtNewPrice);
        payNowBtn = (TextView) findViewById(R.id.payNowBtn);
        payNowBtn.setOnClickListener(this);

        txtperUserPrice.setText("$ " + perUserRate);
        txtPlanPrice.setText("$ " + planPrice);
        newPrice = planPrice;
        txtNewPrice.setText("$ " + newPrice);

        existingUserList = new ArrayList<>();

        existingUserAdapter = new ExistingUserAdapter(ExistingUserActivity.this, existingUserList);
        lisExitingUser.setAdapter(existingUserAdapter);
        lisExitingUser.setOnItemClickListener(this);

        try {
            getExistingUser(new JSONArray(existingObjStr));
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.backLayoutclick:
                finish();
                break;
            case R.id.payNowBtn:
                StringBuffer exitingUserUnchecked = new StringBuffer();
                exitingUserUnchecked.append("");
                for (int i = 0; i < existingUserList.size(); i++) {
                    if (!existingUserList.get(i).isSelected()) {
                        exitingUserUnchecked.append(existingUserList.get(i).getId() + ",");
                    }
                }
                String exitingUSerIds = "";
                if (Utils.validateString(exitingUserUnchecked.toString())) {
                    exitingUSerIds = exitingUserUnchecked.substring(0, exitingUserUnchecked.length() - 1);
                } else {
                    exitingUSerIds = "";
                }

                Intent planIntent = new Intent(ExistingUserActivity.this, PaymentActivity.class);
                planIntent.putExtra(Utils.PLAN_PRICE, String.valueOf(newPrice));
                planIntent.putExtra(Utils.PLAN_ID, planId);
                planIntent.putExtra(Utils.PLAN_NAME, planName);
                Utils.storeString(mSharedPreferences, Utils.EXITING_USER_IDS, exitingUSerIds);
                Utils.storeString(mSharedPreferences, Utils.PLAN_PRICE, String.valueOf(planPrice));

                if (Utils.validateString(txtNumberOfUser.getText().toString().trim())) {
                    Utils.storeString(mSharedPreferences, Utils.SUB_LEVEL_USER_COUNT, txtNumberOfUser.getText().toString().trim());
                    int amount = Integer.parseInt(txtNumberOfUser.getText().toString().trim()) * perUserRate;
                    Utils.storeString(mSharedPreferences, Utils.SUB_LEVEL_USER_AMOUNT, String.valueOf(amount));
                }

                startActivity(planIntent);
                break;
        }
    }

    public void getExistingUser(JSONArray array) {
        try {
            existingUserList.clear();
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                ExistingUserModel existingUserModel = new ExistingUserModel();
                existingUserModel.setId(object.getString("id"));
                existingUserModel.setFirst_name(object.getString("first_name"));
                existingUserModel.setLast_name(object.getString("last_name"));
                existingUserModel.setEmail(object.getString("email"));
                existingUserModel.setUser_type(object.getString("user_type"));
                existingUserList.add(existingUserModel);
            }

            existingUserAdapter.notifyDataSetChanged();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        CheckBox checkbox = (CheckBox) view.getTag(R.id.chkboxExistingUser);
//        Toast.makeText(v.getContext(), isCheckedOrNot(checkbox), Toast.LENGTH_LONG).show();
    }

    public class ExistingUserAdapter extends ArrayAdapter<ExistingUserModel> {

        private final ArrayList<ExistingUserModel> list;
        private final Activity context;

        public ExistingUserAdapter(Activity context, ArrayList<ExistingUserModel> list) {
            super(context, R.layout.raw_existing_user, list);
            this.context = context;
            this.list = list;
        }

        class ViewHolder {
            protected CheckBox checkbox;
            protected TextView txtUsername, txtEmailId;
        }

        int no_of_users = 0;

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder viewHolder = null;
            if (convertView == null) {
                LayoutInflater inflator = context.getLayoutInflater();
                convertView = inflator.inflate(R.layout.raw_existing_user, null);
                viewHolder = new ViewHolder();
                viewHolder.checkbox = (CheckBox) convertView.findViewById(R.id.chkboxExistingUser);
                viewHolder.txtUsername = (TextView) convertView.findViewById(R.id.txtUsername);
                viewHolder.txtEmailId = (TextView) convertView.findViewById(R.id.txtEmailId);
                viewHolder.checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (Utils.validateString(txtNumberOfUser.getText().toString())) {
                            no_of_users = Integer.parseInt(txtNumberOfUser.getText().toString().trim());
                        }
                        int getPosition = (Integer) buttonView.getTag();
                        list.get(getPosition).setSelected(buttonView.isChecked());
                        if (buttonView.isChecked()) {
                            no_of_users = no_of_users + 1;
                            newPrice = planPrice + (no_of_users * perUserRate);
                            txtNumberOfUser.setText("" + no_of_users);
                            txtNewPrice.setText("$ " + newPrice);
                        } else {
                            if (no_of_users > 0) {
                                no_of_users = no_of_users - 1;
                                newPrice = planPrice + (no_of_users * perUserRate);
                            } else {
                                no_of_users = 0;
                            }
                            txtNumberOfUser.setText("" + no_of_users);
                            txtNewPrice.setText("$ " + newPrice);
                        }
                    }
                });
                convertView.setTag(viewHolder);
                convertView.setTag(R.id.chkboxExistingUser, viewHolder.checkbox);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.checkbox.setTag(position); // This line is important.

            viewHolder.checkbox.setChecked(list.get(position).isSelected());
            viewHolder.txtUsername.setText(list.get(position).getFirst_name() + " " + list.get(position).getLast_name());
            viewHolder.txtEmailId.setText(list.get(position).getEmail());

            return convertView;
        }
    }
}

