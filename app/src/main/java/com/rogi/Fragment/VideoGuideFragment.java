package com.rogi.Fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rogi.Activity.NavigationDrawerActivity;
import com.rogi.R;
import com.rogi.View.Utils;

public class VideoGuideFragment extends Fragment implements View.OnClickListener {

    public static final String TAG = "VideoGuideFragment";
    private SharedPreferences mSharedPreferences;

    View view;
    TextView btnSFConfig, btnSFSyn, btnAppWalk, btnAppSF, btnCreateSubUser;
    RelativeLayout rootLayout;
    String TOKEN = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_video_guide, container, false);
        mSharedPreferences = getActivity().getSharedPreferences(Utils.PREF_NAME, getActivity().MODE_PRIVATE);
        TOKEN = mSharedPreferences.getString(Utils.TOKEN, "");

        NavigationDrawerActivity.titleText.setVisibility(View.VISIBLE);
        NavigationDrawerActivity.titleText.setText(getString(R.string.nav_video_guide));
        NavigationDrawerActivity.imgLogo.setVisibility(View.GONE);
        NavigationDrawerActivity.filterView.setVisibility(View.INVISIBLE);
        NavigationDrawerActivity.imgSyc.setVisibility(View.INVISIBLE);

        init();
        return view;
    }

    private void init() {
        rootLayout = (RelativeLayout) view.findViewById(R.id.rootLayout);

        btnSFConfig = (TextView) view.findViewById(R.id.btnSFConfig);
        btnSFSyn = (TextView) view.findViewById(R.id.btnSFSyn);
        btnAppWalk = (TextView) view.findViewById(R.id.btnAppWalk);
        btnAppSF = (TextView) view.findViewById(R.id.btnAppSF);
        btnCreateSubUser = (TextView) view.findViewById(R.id.btnCreateSubUser);

        btnSFConfig.setOnClickListener(this);
        btnSFSyn.setOnClickListener(this);
        btnAppWalk.setOnClickListener(this);
        btnAppSF.setOnClickListener(this);
        btnCreateSubUser.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSFConfig:
                Intent AdminSFConfGuideVideoIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(Utils.ADMIN_SALESFORCE_CONF_GUIDE_VIDEO));
                startActivity(AdminSFConfGuideVideoIntent);
                break;

            case R.id.btnSFSyn:
                Intent AdminSFGuideVideoIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(Utils.ADMIN_SALESFORCE_GUIDE_VIDEO));
                startActivity(AdminSFGuideVideoIntent);
                break;

            case R.id.btnAppWalk:
                Intent AppGuideVideoIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(Utils.APP_GUIDE_VIDEO));
                startActivity(AppGuideVideoIntent);
                break;

            case R.id.btnAppSF:
                Intent SFGuideVideorIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(Utils.SALESFORCE_GUIDE_VIDEO));
                startActivity(SFGuideVideorIntent);
                break;

            case R.id.btnCreateSubUser:
                Intent CreateSubUserVideorIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(Utils.ADMIN_CREATE_SUB_LEVEL_USER));
                startActivity(CreateSubUserVideorIntent);
                break;

        }

    }
}
