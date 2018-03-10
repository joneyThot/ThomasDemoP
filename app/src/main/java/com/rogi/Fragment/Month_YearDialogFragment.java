package com.rogi.Fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ListView;
import android.widget.TextView;

import com.rogi.Adapter.Month_YearListAdapter;
import com.rogi.R;

import java.util.List;

/**
 * Created by "Mayuri" on 8/9/17.
 */
public class Month_YearDialogFragment extends DialogFragment {

    View view;
    Month_YearListAdapter adapter;
    List<String> items;
    TextView titleText;
    ListView listView;
    String title;
    View.OnClickListener monthListner;
    View.OnClickListener yearListner;

    public Month_YearDialogFragment() {
    }

    @SuppressLint("ValidFragment")
    public Month_YearDialogFragment(String title, List<String> items, View.OnClickListener monthListner, View.OnClickListener yearListner) {
        this.title = title;
        this.items = items;
        this.monthListner = monthListner;
        this.yearListner = yearListner;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.dialog_month_year_view, null, false);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        titleText = (TextView) view.findViewById(R.id.titleText);
        listView = (ListView) view.findViewById(R.id.list);

        titleText.setText(title);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);
        adapter = new Month_YearListAdapter(getContext(), items, monthListner, yearListner);
        listView.setAdapter(adapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        }
    }

}
