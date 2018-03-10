package com.rogi.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.rogi.Model.AdditionalContactModel;
import com.rogi.R;
import com.rogi.View.Utils;

import java.util.List;

/**
 * Created by "Mayuri" on 4/8/17.
 */
public class AdditionalContactListAdapter extends RecyclerView.Adapter<AdditionalContactListAdapter.ViewHolder> {
    private LayoutInflater inflater;
    private Context context;
    List<AdditionalContactModel> contactDatas;
    OnEditAdditionalContactListner editClick;
    OnDeleteAdditionalContactListner deleteClick;
    boolean additionalFlag;

    public AdditionalContactListAdapter(Context context, List<AdditionalContactModel> contactDatas,
                                        OnEditAdditionalContactListner editClick, OnDeleteAdditionalContactListner deleteClick,
                                        boolean additionalFlag) {
        super();
        this.context = context;
        this.contactDatas = contactDatas;
        this.editClick = editClick;
        this.deleteClick = deleteClick;
        this.additionalFlag = additionalFlag;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.activity_task_additional_contact_list_view, parent, false);
        RecyclerView.ViewHolder holder = new ViewHolder(v);
        return (ViewHolder) holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final AdditionalContactModel contactItems = contactDatas.get(position);

        if (Utils.validateString(contactItems.getAdtnlContactName()))
            holder.adtnlCustomerNameText.setText(contactItems.getAdtnlContactName());

        if (Utils.validateString(contactItems.getAdtnlContactCompany()))
            holder.adtnlCompanyNameText.setText(contactItems.getAdtnlContactCompany());


        if (Utils.validateString(contactItems.getAdtnlContactAddress())) {
            if (contactItems.getAdtnlContactAddress().contains("&$&")) {
                String addressWithComma = contactItems.getAdtnlContactAddress().replace("&$&", ", ");
                holder.adtnlAddressText.setText(addressWithComma);
            } else {
                holder.adtnlAddressText.setText(contactItems.getAdtnlContactAddress());
            }
        }

        if (Utils.validateString(contactItems.getAdtnlContactPhone()))
            holder.adtnlPhoneText.setText(Utils.getPhoneNumberFormat(contactItems.getAdtnlContactPhone()));

        if (Utils.validateString(contactItems.getAdtnlContactEmail()))
            holder.adtnlEmailText.setText(contactItems.getAdtnlContactEmail());

        if (Utils.validateString(contactItems.getAdtnlContactResponsibility())) {
            holder.adtnlResponsibilityText.setText(contactItems.getAdtnlContactResponsibility());
        }

        if (additionalFlag) {
            holder.editAdtnlCustomerBTN.setVisibility(View.VISIBLE);
            holder.deleteAdtnlCustomerBTN.setVisibility(View.VISIBLE);

            holder.editAdtnlCustomerBTN.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    editClick.onItemClick(contactItems);
                }
            });

            holder.deleteAdtnlCustomerBTN.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteClick.onItemClick(contactItems);
                }
            });
        } else {
//            holder.editAdtnlCustomerBTN.setOnClickListener(null);
//            holder.deleteAdtnlCustomerBTN.setOnClickListener(null);

            holder.editAdtnlCustomerBTN.setVisibility(View.GONE);
            holder.deleteAdtnlCustomerBTN.setVisibility(View.GONE);
        }
    }

    public interface OnEditAdditionalContactListner {
        void onItemClick(AdditionalContactModel data);
    }

    public interface OnDeleteAdditionalContactListner {
        void onItemClick(AdditionalContactModel data);
    }


    @Override
    public int getItemCount() {
        return contactDatas.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView adtnlCustomerNameText, adtnlCompanyNameText, adtnlAddressText, adtnlPhoneText, adtnlEmailText,
                adtnlResponsibilityText;
        ImageView editAdtnlCustomerBTN, deleteAdtnlCustomerBTN;

        public ViewHolder(View itemView) {
            super(itemView);

            adtnlCustomerNameText = (TextView) itemView.findViewById(R.id.adtnlCustomerNameText);
            adtnlCompanyNameText = (TextView) itemView.findViewById(R.id.adtnlCompanyNameText);
            adtnlAddressText = (TextView) itemView.findViewById(R.id.adtnlAddressText);
            adtnlPhoneText = (TextView) itemView.findViewById(R.id.adtnlPhoneText);
            adtnlEmailText = (TextView) itemView.findViewById(R.id.adtnlEmailText);
            adtnlResponsibilityText = (TextView) itemView.findViewById(R.id.adtnlResponsibilityText);
            editAdtnlCustomerBTN = (ImageView) itemView.findViewById(R.id.editAdtnlCustomerBTN);
            deleteAdtnlCustomerBTN = (ImageView) itemView.findViewById(R.id.deleteAdtnlCustomerBTN);
        }
    }
}

