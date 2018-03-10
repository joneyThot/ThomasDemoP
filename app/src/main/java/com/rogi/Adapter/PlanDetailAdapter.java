package com.rogi.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.rogi.Model.PlanModel;
import com.rogi.R;
import com.rogi.View.TextViewPlus;

import java.util.List;

/**
 * Created by "Mayuri" on 26/9/17.
 */
public class PlanDetailAdapter extends RecyclerView.Adapter<PlanDetailAdapter.ViewHolder> {
    private LayoutInflater inflater;
    private Context context;
    List<PlanModel> planDatas;

    final static private int PLAN_TITLE_VIEW = 0;
    final static private int PLAN_FEATURES_VIEW = 1;

    public PlanDetailAdapter(Context context, List<PlanModel> planDatas) {
        super();
        this.context = context;
        this.planDatas = planDatas;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        inflater = LayoutInflater.from(context);
        View v1 = inflater.inflate(R.layout.activity_plan_details_name_view, parent, false);
        View v2 = inflater.inflate(R.layout.activity_plan_details_features_view, parent, false);

        RecyclerView.ViewHolder holder1 = new ViewHolder(v1);
        RecyclerView.ViewHolder holder2 = new ViewHolder(v2);

        switch (viewType) {
            case PLAN_TITLE_VIEW:
                return (ViewHolder) holder1;
            case PLAN_FEATURES_VIEW:
                return (ViewHolder) holder2;

            default:
                return (ViewHolder) holder1;
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final PlanModel planData = planDatas.get(position);

        int VIEW_TYPE = planData.getViewType();
        if (VIEW_TYPE == PLAN_TITLE_VIEW) {
            holder.planAmountTxt.setText(planData.getPlanAmount());
            holder.descriptionText.setText(planData.getDescription());
            holder.durationText.setText(planData.getDuration());
        }

        if (VIEW_TYPE == PLAN_FEATURES_VIEW) {
            holder.planFeaturesText.setText(planData.getPlan1());
        }
    }

    @Override
    public int getItemViewType(int position) {
        return planDatas.get(position).getViewType();
    }


    @Override
    public int getItemCount() {
        return planDatas.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextViewPlus planAmountTxt, descriptionText, durationText, planFeaturesText;
        Button getPlanBtn;

        public ViewHolder(View itemView) {
            super(itemView);

            getPlanBtn = (Button) itemView.findViewById(R.id.getPlanBtn);
            planAmountTxt = (TextViewPlus) itemView.findViewById(R.id.planAmountTxt);
            descriptionText = (TextViewPlus) itemView.findViewById(R.id.descriptionText);
            durationText = (TextViewPlus) itemView.findViewById(R.id.durationText);

            planFeaturesText = (TextViewPlus) itemView.findViewById(R.id.planFeaturesText);
        }
    }
}
