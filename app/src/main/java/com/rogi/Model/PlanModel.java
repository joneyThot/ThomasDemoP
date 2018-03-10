package com.rogi.Model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by "Mayuri" on 10/8/17.
 */
public class PlanModel implements Serializable {
    String planAmount, planName;
    String description;
    String planId;
    String planType;
    String plan1, plan2, plan3, plan4, plan5, plan6, plan7, plan8, plan9, plan10, plan11, plan12, plan13, plan14, plan15;
    String duration;
    int viewType;
    ArrayList<String> planFeatures = new ArrayList<>();

    public PlanModel() {

    }

    public PlanModel(ArrayList<String> planFeatures, String planId, String planName, String planType, String planAmount, String description, String duration, String plan1, String plan2, String plan3,
                     String plan4, String plan5, String plan6, String plan7, String plan8, String plan9, String plan10
            , String plan11, String plan12, String plan13, String plan14, String plan15) {
        this.planId = planId;
        this.planFeatures = planFeatures;
        this.planName = planName;
        this.planType = planType;
        this.planAmount = planAmount;
        this.description = description;
        this.duration = duration;
        this.plan1 = plan1;
        this.plan2 = plan2;
        this.plan3 = plan3;
        this.plan4 = plan4;
        this.plan5 = plan5;
        this.plan6 = plan6;
        this.plan7 = plan7;
        this.plan8 = plan8;
        this.plan9 = plan9;
        this.plan10 = plan10;
        this.plan11 = plan11;
        this.plan12 = plan12;
        this.plan13 = plan13;
        this.plan14 = plan14;
        this.plan15 = plan15;

    }

    public ArrayList<String> getPlanFeatures() {
        return planFeatures;
    }

    public void setPlanFeatures(ArrayList<String> planFeatures) {
        this.planFeatures = planFeatures;
    }

    public int getViewType() {
        return viewType;
    }

    public void setViewType(int viewType) {
        this.viewType = viewType;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getPlanType() {
        return planType;
    }

    public void setPlanType(String planType) {
        this.planType = planType;
    }

    public String getPlanId() {
        return planId;
    }

    public void setPlanId(String planId) {
        this.planId = planId;
    }

    public String getPlan1() {
        return plan1;
    }

    public void setPlan1(String plan1) {
        this.plan1 = plan1;
    }

    public String getPlan2() {
        return plan2;
    }

    public void setPlan2(String plan2) {
        this.plan2 = plan2;
    }

    public String getPlan3() {
        return plan3;
    }

    public void setPlan3(String plan3) {
        this.plan3 = plan3;
    }

    public String getPlan4() {
        return plan4;
    }

    public void setPlan4(String plan4) {
        this.plan4 = plan4;
    }

    public String getPlan5() {
        return plan5;
    }

    public void setPlan5(String plan5) {
        this.plan5 = plan5;
    }

    public String getPlan6() {
        return plan6;
    }

    public void setPlan6(String plan6) {
        this.plan6 = plan6;
    }

    public String getPlan7() {
        return plan7;
    }

    public void setPlan7(String plan7) {
        this.plan7 = plan7;
    }

    public String getPlan8() {
        return plan8;
    }

    public void setPlan8(String plan8) {
        this.plan8 = plan8;
    }

    public String getPlan9() {
        return plan9;
    }

    public void setPlan9(String plan9) {
        this.plan9 = plan9;
    }

    public String getPlan10() {
        return plan10;
    }

    public void setPlan10(String plan10) {
        this.plan10 = plan10;
    }

    public String getPlan11() {
        return plan11;
    }

    public void setPlan11(String plan11) {
        this.plan11 = plan11;
    }

    public String getPlan12() {
        return plan12;
    }

    public void setPlan12(String plan12) {
        this.plan12 = plan12;
    }

    public String getPlan13() {
        return plan13;
    }

    public void setPlan13(String plan13) {
        this.plan13 = plan13;
    }

    public String getPlan14() {
        return plan14;
    }

    public void setPlan14(String plan14) {
        this.plan14 = plan14;
    }

    public String getPlan15() {
        return plan15;
    }

    public void setPlan15(String plan15) {
        this.plan15 = plan15;
    }

    public String getPlanAmount() {
        return planAmount;
    }

    public void setPlanAmount(String planAmount) {
        this.planAmount = planAmount;
    }

    public String getPlanName() {
        return planName;
    }

    public void setPlanName(String planName) {
        this.planName = planName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
