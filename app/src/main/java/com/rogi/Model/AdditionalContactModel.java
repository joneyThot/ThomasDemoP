package com.rogi.Model;

import org.json.JSONObject;

/**
 * Created by "Mayuri" on 4/8/17.
 */
public class AdditionalContactModel {

    String adtnlContactID, adtnlContactName, adtnlContactCompany, adtnlContactAddress, adtnlContactPhone,
            adtnlContactEmail, adtnlContactResponsibility, adtnlContactResponsibilityId, project_id, is_sync, contact_status, created_date;

    JSONObject contactObject;

    public AdditionalContactModel(String id, String name, String company, String address, String phone, String email,
                                  String adtnlContactResponsibility, String adtnlContactResponsibilityId, String is_sync, String contact_status, String created_date,
                                  JSONObject contactObject) {
        this.adtnlContactID = id;
        this.adtnlContactName = name;
        this.adtnlContactCompany = company;
        this.adtnlContactAddress = address;
        this.adtnlContactPhone = phone;
        this.adtnlContactEmail = email;
        this.adtnlContactResponsibility = adtnlContactResponsibility;
        this.adtnlContactResponsibilityId = adtnlContactResponsibilityId;
        this.is_sync = is_sync;
        this.contact_status = contact_status;
        this.created_date = created_date;
        this.contactObject = contactObject;
    }

    public AdditionalContactModel() {

    }


    public JSONObject getContactObject() {
        return contactObject;
    }

    public void setContactObject(JSONObject contactObject) {
        this.contactObject = contactObject;
    }

    public String getAdtnlContactID() {
        return adtnlContactID;
    }

    public void setAdtnlContactID(String adtnlContactID) {
        this.adtnlContactID = adtnlContactID;
    }

    public String getAdtnlContactName() {
        return adtnlContactName;
    }

    public void setAdtnlContactName(String adtnlContactName) {
        this.adtnlContactName = adtnlContactName;
    }

    public String getAdtnlContactCompany() {
        return adtnlContactCompany;
    }

    public void setAdtnlContactCompany(String adtnlContactCompany) {
        this.adtnlContactCompany = adtnlContactCompany;
    }

    public String getAdtnlContactAddress() {
        return adtnlContactAddress;
    }

    public void setAdtnlContactAddress(String adtnlContactAddress) {
        this.adtnlContactAddress = adtnlContactAddress;
    }

    public String getAdtnlContactPhone() {
        return adtnlContactPhone;
    }

    public void setAdtnlContactPhone(String adtnlContactPhone) {
        this.adtnlContactPhone = adtnlContactPhone;
    }

    public String getAdtnlContactEmail() {
        return adtnlContactEmail;
    }

    public void setAdtnlContactEmail(String adtnlContactEmail) {
        this.adtnlContactEmail = adtnlContactEmail;
    }

    public String getAdtnlContactResponsibility() {
        return adtnlContactResponsibility;
    }

    public void setAdtnlContactResponsibility(String adtnlContactResponsibility) {
        this.adtnlContactResponsibility = adtnlContactResponsibility;
    }

    public String getAdtnlContactResponsibilityId() {
        return adtnlContactResponsibilityId;
    }

    public void setAdtnlContactResponsibilityId(String adtnlContactResponsibilityId) {
        this.adtnlContactResponsibilityId = adtnlContactResponsibilityId;
    }

    public String getIs_sync() {
        return is_sync;
    }

    public void setIs_sync(String is_sync) {
        this.is_sync = is_sync;
    }

    public String getProject_id() {
        return project_id;
    }

    public void setProject_id(String project_id) {
        this.project_id = project_id;
    }


    public String getContact_status() {
        return contact_status;
    }

    public void setContact_status(String contact_status) {
        this.contact_status = contact_status;
    }

    public String getCreated_date() {
        return created_date;
    }

    public void setCreated_date(String created_date) {
        this.created_date = created_date;
    }
}
