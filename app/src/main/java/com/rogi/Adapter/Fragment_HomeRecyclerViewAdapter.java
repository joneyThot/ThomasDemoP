package com.rogi.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rogi.Model.TaskModel;
import com.rogi.R;
import com.rogi.View.Utils;

import java.util.List;

/**
 * Created by "Mayuri" on 23/8/17.
 */
public class Fragment_HomeRecyclerViewAdapter extends RecyclerView.Adapter<Fragment_HomeRecyclerViewAdapter.ViewHolder> {

    Context context;
    LayoutInflater inflater;
    List<TaskModel> taskModelArrayList;
    View.OnClickListener viewClick;
    View.OnClickListener statusBtnClick;

    final static private int HOME_TITLE_VIEW = 0;
    final static private int HOME_PROJECT_VIEW = 1;


    View.OnClickListener completedProjectViewClick;
    View.OnClickListener pendingProjectViewClick;


    public Fragment_HomeRecyclerViewAdapter(Context context, List<TaskModel> taskModelArrayList, View.OnClickListener viewClick,
                                            View.OnClickListener statusBtnClick, View.OnClickListener completedProjectViewClick,
                                            View.OnClickListener pendingProjectViewClick) {
        super();
        this.context = context;
        this.taskModelArrayList = taskModelArrayList;
        this.viewClick = viewClick;
        this.statusBtnClick = statusBtnClick;

        this.completedProjectViewClick = completedProjectViewClick;
        this.pendingProjectViewClick = pendingProjectViewClick;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        inflater = LayoutInflater.from(context);
        View v1 = inflater.inflate(R.layout.activity_home_title_fragment, parent, false);
        View v2 = inflater.inflate(R.layout.fragment_home_item_view, parent, false);

        RecyclerView.ViewHolder homeTitle = new ViewHolder(v1);
        RecyclerView.ViewHolder homeProjectView = new ViewHolder(v2);

        switch (viewType) {
            case HOME_TITLE_VIEW:
                return (ViewHolder) homeTitle;
            case HOME_PROJECT_VIEW:
                return (ViewHolder) homeProjectView;

            default:
                return (ViewHolder) homeTitle;
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        final TaskModel projectItems = taskModelArrayList.get(position);

        int TASK_VIEW_TYPE = projectItems.getViewType();

        if (TASK_VIEW_TYPE == HOME_PROJECT_VIEW) {

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
                holder.statusBTN.setText(Utils.START);
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

            holder.view.setTag(position);
            holder.view.setOnClickListener(viewClick);

            holder.statusBTN.setTag(position);
            holder.statusBTN.setOnClickListener(statusBtnClick);
        }

        if (TASK_VIEW_TYPE == HOME_TITLE_VIEW) {

            holder.todayTaskCountText.setText(projectItems.getTodayProject());
            holder.completedTaskCountText.setText(projectItems.getCompletedProject());
            holder.penddingTaskCountText.setText(projectItems.getPenddingProject());

            holder.completedProjectLayout.setTag(position);
            holder.completedProjectLayout.setOnClickListener(completedProjectViewClick);

            holder.penddingProjectLayout.setTag(position);
            holder.penddingProjectLayout.setOnClickListener(pendingProjectViewClick);

        }
    }

    @Override
    public int getItemViewType(int position) {
        return taskModelArrayList.get(position).getViewType();
    }

    @Override
    public int getItemCount() {
        return taskModelArrayList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView dateText, timeText, detailText, priorityBTN, statusBTN;
        View view;

        TextView todayTaskCountText, completedTaskCountText, penddingTaskCountText;
        LinearLayout todayProjectLayout, completedProjectLayout, penddingProjectLayout, linRaw;

        public ViewHolder(View vi) {
            super(vi);
            view = vi;
            priorityBTN = (TextView) vi.findViewById(R.id.priorityBTN);
            statusBTN = (TextView) vi.findViewById(R.id.statusBTN);
            dateText = (TextView) vi.findViewById(R.id.dateText);
            timeText = (TextView) vi.findViewById(R.id.timeText);
            detailText = (TextView) vi.findViewById(R.id.detailText);
            linRaw = (LinearLayout) vi.findViewById(R.id.linRaw);

            todayTaskCountText = (TextView) view.findViewById(R.id.todayTaskCountText);
            completedTaskCountText = (TextView) view.findViewById(R.id.completedTaskCountText);
            penddingTaskCountText = (TextView) view.findViewById(R.id.penddingTaskCountText);

            todayProjectLayout = (LinearLayout) view.findViewById(R.id.todayProjectLayout);
            completedProjectLayout = (LinearLayout) view.findViewById(R.id.completedProjectLayout);
            penddingProjectLayout = (LinearLayout) view.findViewById(R.id.penddingProjectLayout);
        }
    }
}
