package com.rogi.Model;

/**
 * Created by "Mayuri" on 4/8/17.
 */
public class ProjectPhaseModel {
    String projectPhaseId, projectDesription, projectCreatedDate, project_id, is_sync, phase_status;

    public ProjectPhaseModel(String projectPhaseId, String projectDesription, String projectCreatedDate, String is_sync,
                             String phase_status) {
        this.projectPhaseId = projectPhaseId;
        this.projectDesription = projectDesription;
        this.projectCreatedDate = projectCreatedDate;
        this.is_sync = is_sync;
        this.phase_status = phase_status;
    }

    public ProjectPhaseModel() {

    }

    public String getProjectPhaseId() {
        return projectPhaseId;
    }

    public void setProjectPhaseId(String projectPhaseId) {
        this.projectPhaseId = projectPhaseId;
    }

    public String getProjectDesription() {
        return projectDesription;
    }

    public void setProjectDesription(String projectDesription) {
        this.projectDesription = projectDesription;
    }

    public String getProjectCreatedDate() {
        return projectCreatedDate;
    }

    public void setProjectCreatedDate(String projectCreatedDate) {
        this.projectCreatedDate = projectCreatedDate;
    }

    public String getProject_id() {
        return project_id;
    }

    public void setProject_id(String project_id) {
        this.project_id = project_id;
    }

    public String getPhase_status() {
        return phase_status;
    }

    public void setPhase_status(String phase_status) {
        this.phase_status = phase_status;
    }

    public String getIs_sync() {
        return is_sync;
    }

    public void setIs_sync(String is_sync) {
        this.is_sync = is_sync;
    }
}
