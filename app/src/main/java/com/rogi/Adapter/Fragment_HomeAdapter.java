package com.rogi.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rogi.Model.TaskModel;
import com.rogi.R;
import com.rogi.View.Utils;

import java.util.List;

/**
 * Created by "Mayuri" on 21/7/17.
 */
public class Fragment_HomeAdapter extends BaseAdapter {
    Context context;
    LayoutInflater inflater;
    List<TaskModel> taskModelArrayList;
    View.OnClickListener viewClick;
    View.OnClickListener statusBtnClick;

    public Fragment_HomeAdapter(Context context, List<TaskModel> taskModelArrayList, View.OnClickListener viewClick,
                                View.OnClickListener statusBtnClick) {
        this.context = context;
        this.taskModelArrayList = taskModelArrayList;
        this.viewClick = viewClick;
        this.statusBtnClick = statusBtnClick;
    }

    @Override
    public int getCount() {
        return taskModelArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return taskModelArrayList.get(position);
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
        vi = inflater.inflate(R.layout.fragment_home_item_view, null);

        holder.priorityBTN = (TextView) vi.findViewById(R.id.priorityBTN);
        holder.statusBTN = (TextView) vi.findViewById(R.id.statusBTN);
        holder.dateText = (TextView) vi.findViewById(R.id.dateText);
        holder.timeText = (TextView) vi.findViewById(R.id.timeText);
        holder.detailText = (TextView) vi.findViewById(R.id.detailText);
        holder.linRaw = (LinearLayout) vi.findViewById(R.id.linRaw);

        vi.setTag(holder);

        if (Utils.validateString(taskModelArrayList.get(position).getPriority())) {
            String priority = taskModelArrayList.get(position).getPriority();
            if (priority.equalsIgnoreCase("High")) {
                holder.priorityBTN.setBackgroundResource(R.color.highPriorityColor);
                holder.priorityBTN.setText(taskModelArrayList.get(position).getPriority());
            } else {
                holder.priorityBTN.setBackgroundResource(R.color.colorGreen);
                holder.priorityBTN.setText(taskModelArrayList.get(position).getPriority());
            }
        }

        String project_status = taskModelArrayList.get(position).getProject_status();

        if (project_status.equalsIgnoreCase(Utils.START) || project_status.equalsIgnoreCase(Utils.PENDING) ||
                (project_status.equalsIgnoreCase(Utils.OPEN) && !Utils.validateString(taskModelArrayList.get(position).getProject_start_time()))) {
            holder.statusBTN.setBackgroundResource(R.color.colorPrimary);
            holder.statusBTN.setText(Utils.PENDING);
        } else if (project_status.equalsIgnoreCase(Utils.OPEN) && Utils.validateString(taskModelArrayList.get(position).getProject_start_time())) {
            holder.statusBTN.setBackgroundResource(R.color.txtColor);
            holder.statusBTN.setText(Utils.FINISH);
        } else if (project_status.equals(Utils.REPORT) && Utils.validateString(taskModelArrayList.get(position).getSignature_image())) {
            holder.statusBTN.setBackgroundResource(R.color.txtColor);
            holder.statusBTN.setText(Utils.FINISHED);
        }

        String startDate = Utils.parseDateFrom(taskModelArrayList.get(position).getStartDate());
        String dueDate = Utils.parseDateFrom(taskModelArrayList.get(position).getDueDate());
        if (!startDate.equals("") || !dueDate.equals("")) {
            holder.dateText.setText(startDate + " - " + dueDate);
        }
        holder.timeText.setText(Utils.parseTime(taskModelArrayList.get(position).getStartTime()));
        holder.detailText.setText(taskModelArrayList.get(position).getTitle());

        if (taskModelArrayList.get(position).getIs_sync().equalsIgnoreCase("Y")) {
            holder.linRaw.setBackgroundResource(R.drawable.background_shadow);
        } else {
            holder.linRaw.setBackgroundResource(R.color.radioButtion_grey);
        }

        vi.setTag(position);
        vi.setOnClickListener(viewClick);

        holder.statusBTN.setTag(position);
        holder.statusBTN.setOnClickListener(statusBtnClick);
        return vi;
    }

    static class ViewHolder {
        TextView dateText, timeText, detailText, priorityBTN, statusBTN;
        LinearLayout linRaw;

    }
}
