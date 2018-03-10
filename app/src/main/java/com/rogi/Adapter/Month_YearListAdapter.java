package com.rogi.Adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.rogi.R;
import com.rogi.View.TextViewPlus;
import com.rogi.View.Utils;

import java.util.List;

/**
 * Created by "Mayuri" on 8/9/17.
 */
public class Month_YearListAdapter extends BaseAdapter {
    private Context context;
    List<String> items;
    View.OnClickListener monthListner;
    View.OnClickListener yearListner;
    SharedPreferences mSharedPreferences;

    public Month_YearListAdapter(Context context, List<String> items, View.OnClickListener monthListner, View.OnClickListener yearListner) {
        this.context = context;
        this.items = items;
        this.monthListner = monthListner;
        this.yearListner = yearListner;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mSharedPreferences = context.getSharedPreferences(Utils.PREF_NAME, context.MODE_PRIVATE);
        View itemView = inflater.inflate(R.layout.dialog_month_year_item_view, parent, false);

        TextViewPlus itemText = (TextViewPlus) itemView.findViewById(R.id.itemText);
        itemText.setText(items.get(position));


        if (mSharedPreferences.getString(Utils.MONTH_YEAR_TYPE, "").equals(Utils.MONTH_TYPE)) {
            itemText.setTag(position);
            itemText.setOnClickListener(monthListner);
            String selectedPos = mSharedPreferences.getString(Utils.MONTH_POS, "");
            String pos = String.valueOf(position);
            if (selectedPos.equals(pos)) {
                itemText.setBackgroundColor(Color.parseColor("#7a1417"));
                itemText.setTextColor(Color.parseColor("#FFFFFF"));
            }
        } else if (mSharedPreferences.getString(Utils.REMINDER_TYPE, "").equals(Utils.REMINDER_TYPE)) {
            itemText.setTag(position);
            itemText.setOnClickListener(monthListner);
            String selectedPos = mSharedPreferences.getString(Utils.MONTH_POS, "");
            String pos = String.valueOf(position);
            if (selectedPos.equals(pos)) {
                itemText.setBackgroundColor(Color.parseColor("#7a1417"));
                itemText.setTextColor(Color.parseColor("#FFFFFF"));
            }
        } else {
            itemText.setTag(position);
            itemText.setOnClickListener(yearListner);
            String selectedPos = mSharedPreferences.getString(Utils.YEAR_POS, "");
            String pos = String.valueOf(position);
            if (selectedPos.equals(pos)) {
                itemText.setBackgroundColor(Color.parseColor("#7a1417"));
                itemText.setTextColor(Color.parseColor("#FFFFFF"));
            }
        }

        return itemView;
    }
}
