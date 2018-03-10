package com.rogi.Fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.rogi.Activity.NavigationDrawerActivity;
import com.rogi.R;
import com.rogi.View.Utils;

public class TermsAndConditionFragment extends Fragment {

    View view;
    WebView webView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_about, container, false);

        NavigationDrawerActivity.titleText.setVisibility(View.VISIBLE);
        NavigationDrawerActivity.titleText.setText(getString(R.string.nav_terms_and_condition));
        NavigationDrawerActivity.imgLogo.setVisibility(View.GONE);
        NavigationDrawerActivity.filterView.setVisibility(View.INVISIBLE);
        NavigationDrawerActivity.imgSyc.setVisibility(View.INVISIBLE);

        init();
        return view;
    }

    private void init() {
        webView = (WebView) view.findViewById(R.id.webview);
        webView.loadUrl(Utils.MAIN_URL + Utils.TERMS_AND_CONDITION);
    }

}
