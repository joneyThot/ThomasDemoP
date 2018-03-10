package com.rogi.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.rogi.Model.ProjectPhaseModel;
import com.rogi.R;
import com.rogi.View.Utils;

import java.util.List;

/**
 * Created by "Mayuri" on 4/8/17.
 */
public class ProjectPhaseListAdapter extends RecyclerView.Adapter<ProjectPhaseListAdapter.ViewHolder> {
    private LayoutInflater inflater;
    private Context context;
    List<ProjectPhaseModel> projectsDatas;
    private OnEditProjectPhaseListner onEditProjectPhaseListner;
    private OnDeleteProjectPhaseListner onDeleteProjectPhaseListner;
    boolean projectFlag;


    public ProjectPhaseListAdapter(Context context, List<ProjectPhaseModel> projectsDatas, OnEditProjectPhaseListner onEditProjectPhaseListner, OnDeleteProjectPhaseListner onDeleteProjectPhaseListner, boolean projectFlag) {
        super();
        this.context = context;
        this.projectsDatas = projectsDatas;
        this.onEditProjectPhaseListner = onEditProjectPhaseListner;
        this.onDeleteProjectPhaseListner = onDeleteProjectPhaseListner;
        this.projectFlag = projectFlag;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.activity_task_project_phase_list_view, parent, false);
        RecyclerView.ViewHolder holder = new ViewHolder(v);
        return (ViewHolder) holder;
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final ProjectPhaseModel mainData = projectsDatas.get(position);
        String date = Utils.parseDateFromyyyyMMdd(mainData.getProjectCreatedDate());
        String[] separated = date.split(",");
        holder.projectDateText.setText(separated[0].trim());
        holder.projectDesriptionText.setText(mainData.getProjectDesription());

        if (projectFlag) {
            holder.editProjectPhaseBTN.setVisibility(View.VISIBLE);
            holder.deleteProjectPhaseBTN.setVisibility(View.VISIBLE);

            holder.editProjectPhaseBTN.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onEditProjectPhaseListner.onItemClick(mainData);
                }
            });

            holder.deleteProjectPhaseBTN.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onDeleteProjectPhaseListner.onItemClick(mainData);
                }
            });
        } else {
//            holder.editProjectPhaseBTN.setOnClickListener(null);
//            holder.deleteProjectPhaseBTN.setOnClickListener(null);
            holder.editProjectPhaseBTN.setVisibility(View.GONE);
            holder.deleteProjectPhaseBTN.setVisibility(View.GONE);
        }

    }

    public interface OnEditProjectPhaseListner {
        void onItemClick(ProjectPhaseModel data);
    }

    public interface OnDeleteProjectPhaseListner {
        void onItemClick(ProjectPhaseModel data);
    }


    @Override
    public int getItemCount() {
        return projectsDatas.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView projectDateText, projectDesriptionText;
        ImageView editProjectPhaseBTN, deleteProjectPhaseBTN;

        public ViewHolder(View itemView) {
            super(itemView);

            projectDateText = (TextView) itemView.findViewById(R.id.projectDateText);
            projectDesriptionText = (TextView) itemView.findViewById(R.id.projectDesriptionText);
            editProjectPhaseBTN = (ImageView) itemView.findViewById(R.id.editProjectPhaseBTN);
            deleteProjectPhaseBTN = (ImageView) itemView.findViewById(R.id.deleteProjectPhaseBTN);
        }
    }
}

