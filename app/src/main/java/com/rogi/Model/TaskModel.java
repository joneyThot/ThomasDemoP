package com.rogi.Model;

import java.util.ArrayList;

/**
 * Created by "Mayuri" on 21/7/17.
 */
public class TaskModel {

    int viewType;

    public int getViewType() {
        return viewType;
    }

    public void setViewType(int viewType) {
        this.viewType = viewType;
    }

    String taskObject;
    String assignContact, assignEmail, assignName;
    String todayProject, completedProject, penddingProject;

    String id, title, description, project_start_time, startTime, startDate, dueTime, dueDate, priority, project_status, status, street, city, state, country,
            pincode, headerName, latitude, longitude, signature_image, is_sync, project_update_status, created_date, reminder_hours;

    ArrayList<NotesModel> NotesModel;
    ArrayList<AdditionalContactModel> AdditionalContactModel;
    ArrayList<ProjectPhaseModel> ProjectPhaseModel;
    ArrayList<MediaModel> MediaModel;

    public String getTodayProject() {
        return todayProject;
    }

    public void setTodayProject(String todayProject) {
        this.todayProject = todayProject;
    }

    public String getCompletedProject() {
        return completedProject;
    }

    public void setCompletedProject(String completedProject) {
        this.completedProject = completedProject;
    }

    public String getPenddingProject() {
        return penddingProject;
    }

    public void setPenddingProject(String penddingProject) {
        this.penddingProject = penddingProject;
    }

    public ArrayList<MediaModel> getMediaModel() {
        return MediaModel;
    }

    public void setMediaModel(ArrayList<MediaModel> mediaModel) {
        MediaModel = mediaModel;
    }

    public ArrayList<ProjectPhaseModel> getProjectPhaseModel() {
        return ProjectPhaseModel;
    }

    public void setProjectPhaseModel(ArrayList<ProjectPhaseModel> projectPhaseModel) {
        ProjectPhaseModel = projectPhaseModel;
    }

    public ArrayList<AdditionalContactModel> getAdditionalContactModel() {
        return AdditionalContactModel;
    }

    public void setAdditionalContactModel(ArrayList<AdditionalContactModel> additionalContactModel) {
        AdditionalContactModel = additionalContactModel;
    }

    public ArrayList<NotesModel> getNotesModel() {
        return NotesModel;
    }

    public void setNotesModel(ArrayList<NotesModel> notesModel) {
        NotesModel = notesModel;
    }


    public String getAssignContact() {
        return assignContact;
    }

    public void setAssignContact(String assignContact) {
        this.assignContact = assignContact;
    }

    public String getAssignEmail() {
        return assignEmail;
    }

    public void setAssignEmail(String assignEmail) {
        this.assignEmail = assignEmail;
    }

    public String getAssignName() {
        return assignName;
    }

    public void setAssignName(String assignName) {
        this.assignName = assignName;
    }


    public String getHeaderName() {
        return headerName;
    }

    public void setHeaderName(String headerName) {
        this.headerName = headerName;
    }


    public String getTaskObject() {
        return taskObject;
    }

    public void setTaskObject(String taskObject) {
        this.taskObject = taskObject;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getDueTime() {
        return dueTime;
    }

    public void setDueTime(String dueTime) {
        this.dueTime = dueTime;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }


    public String getProject_start_time() {
        return project_start_time;
    }

    public void setProject_start_time(String project_start_time) {
        this.project_start_time = project_start_time;
    }

    public String getProject_status() {
        return project_status;
    }

    public void setProject_status(String project_status) {
        this.project_status = project_status;
    }

    public String getSignature_image() {
        return signature_image;
    }

    public void setSignature_image(String signature_image) {
        this.signature_image = signature_image;
    }

    public String getIs_sync() {
        return is_sync;
    }

    public void setIs_sync(String is_sync) {
        this.is_sync = is_sync;
    }

    public String getProject_update_status() {
        return project_update_status;
    }

    public void setProject_update_status(String project_update_status) {
        this.project_update_status = project_update_status;
    }

    public String getCreated_date() {
        return created_date;
    }

    public void setCreated_date(String created_date) {
        this.created_date = created_date;
    }

    public String getReminder_hours() {
        return reminder_hours;
    }

    public void setReminder_hours(String reminder_hours) {
        this.reminder_hours = reminder_hours;
    }
}
