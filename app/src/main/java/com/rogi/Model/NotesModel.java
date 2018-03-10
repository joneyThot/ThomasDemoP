package com.rogi.Model;

/**
 * Created by "Mayuri" on 4/8/17.
 */
public class NotesModel {
    String noteId, note, note_created_date, project_id, is_sync, note_status;

    public NotesModel(String noteId, String note, String note_created_date, String is_sync, String note_status) {
        this.noteId = noteId;
        this.note = note;
        this.note_created_date = note_created_date;
        this.is_sync = is_sync;
        this.note_status = note_status;
    }

    public NotesModel() {

    }

    public String getNoteId() {
        return noteId;
    }

    public void setNoteId(String noteId) {
        this.noteId = noteId;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getNote_created_date() {
        return note_created_date;
    }

    public void setNote_created_date(String note_created_date) {
        this.note_created_date = note_created_date;
    }

    public String getProject_id() {
        return project_id;
    }

    public void setProject_id(String project_id) {
        this.project_id = project_id;
    }

    public String getIs_sync() {
        return is_sync;
    }

    public void setIs_sync(String is_sync) {
        this.is_sync = is_sync;
    }

    public String getNote_status() {
        return note_status;
    }

    public void setNote_status(String note_status) {
        this.note_status = note_status;
    }
}
