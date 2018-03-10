package com.rogi.Activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.rogi.Model.FAQModel;
import com.rogi.R;
import com.rogi.View.Utils;

import java.util.ArrayList;

public class FAQDetailsActivity extends Activity implements View.OnClickListener {

    public static final String TAG = "FAQDetailsActivity";
    private SharedPreferences mSharedPreferences;
    TextView txtQuestion, txtAnswer, titleText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faq_details);
        mSharedPreferences = getSharedPreferences(Utils.PREF_NAME, MODE_PRIVATE);
        init();
    }

    private void init() {

        titleText = (TextView) findViewById(R.id.titleText);
        titleText.setText("FAQ");

        txtQuestion = (TextView) findViewById(R.id.txtQuestion);
        txtAnswer = (TextView) findViewById(R.id.txtAnswer);

        txtQuestion.setText(mSharedPreferences.getString(Utils.FAQ_QUESTION, ""));
        txtAnswer.setText(mSharedPreferences.getString(Utils.FAQ_ANSWER, ""));

        findViewById(R.id.backLayoutclick).setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.backLayoutclick:
                finish();
                break;
        }
    }
}
