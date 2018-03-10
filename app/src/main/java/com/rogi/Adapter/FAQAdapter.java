package com.rogi.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.rogi.Model.FAQModel;
import com.rogi.R;
import com.rogi.View.Utils;

import java.util.ArrayList;


public class FAQAdapter extends BaseAdapter {
    Context context;
    LayoutInflater inflater;
    ArrayList<FAQModel> faqModelArrayList;
    View.OnClickListener viewClick;

    public FAQAdapter(Context context, ArrayList<FAQModel> faqModelArrayList, View.OnClickListener viewClick) {
        this.context = context;
        this.faqModelArrayList = faqModelArrayList;
        this.viewClick = viewClick;
    }

    @Override
    public int getCount() {
        return faqModelArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return faqModelArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    View vi;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        vi = convertView;
        ViewHolder holder = new ViewHolder();
        if (convertView == null) {
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        vi = inflater.inflate(R.layout.faq_item_view, null);

        holder.txtQuestion = (TextView) vi.findViewById(R.id.txtQuestion);

        vi.setTag(holder);

        if (Utils.validateString(faqModelArrayList.get(position).getQuestion())) {
            String question = faqModelArrayList.get(position).getQuestion();
            holder.txtQuestion.setText(question);
        }

        vi.setTag(position);
        vi.setOnClickListener(viewClick);

        return vi;
    }

    static class ViewHolder {
        TextView txtQuestion;

    }
}
