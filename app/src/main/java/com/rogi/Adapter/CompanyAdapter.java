package com.rogi.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.rogi.Model.CompanyModel;
import com.rogi.R;
import com.rogi.View.Utils;

import java.util.ArrayList;

public class CompanyAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater infalter;
    private ArrayList<CompanyModel> companyModelArrayList;
    View.OnClickListener viewClick;

    public CompanyAdapter(Context mContext, ArrayList<CompanyModel> companyModelArrayList, View.OnClickListener viewClick) {
        super();
        this.mContext = mContext;
        this.companyModelArrayList = companyModelArrayList;
        this.viewClick = viewClick;
    }

    @Override
    public int getCount() {
        return companyModelArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return companyModelArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    View vi;

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        vi = convertView;
        if (convertView == null) {
            infalter = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        vi = infalter.inflate(R.layout.raw_spinner, null);

        if (Utils.validateString(companyModelArrayList.get(position).getName())) {
            ((TextView) vi.findViewById(R.id.txtCompanyName)).setText(companyModelArrayList.get(position).getName());
            //Logger.e("TAG", "Company Name ::" + companyModelArrayList.get(position).getName());
        }

        vi.setTag(position);
        vi.setOnClickListener(viewClick);
        return vi;
    }

    public int getPosition(int position) {
        // TODO Auto-generated method stub
        return position;
    }
}
