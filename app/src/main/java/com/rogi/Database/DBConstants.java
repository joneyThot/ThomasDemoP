package com.rogi.Database;

/**
 * Created by user22 on 22/9/15.
 */
public class DBConstants {

    public static String DB_PATH = "/data/data/com.rogi/databases/";
    public static final String DB_NAME = "ROGi.db";
    public static final int DB_VERSION = 1;

    // table
    public class TABLE {
        public static final String PROJECT_DETAILS = "project_details";
        public static final String PHASE_DETAILS = "phase_details";
        public static final String NOTES_DETAILS = "notes_details";
        public static final String MEDIA_DETAILS = "media_details";
        public static final String CONTACTS_DETAILS = "contacts_details";
        public static final String RESPONSIBLITY_DETAILS = "responsiblity_details";

    }

    public class Project_details {
        public static final String id = "id";
        public static final String title = "title";
        public static final String description = "description";
        public static final String start_date = "start_date";
        public static final String start_time = "start_time";
        public static final String due_date = "due_date";
        public static final String due_time = "due_time";
        public static final String priority = "priority";
        public static final String project_status = "project_status";
        public static final String street = "street";
        public static final String city = "city";
        public static final String state = "state";
        public static final String country = "country";
        public static final String pincode = "pincode";
        public static final String lattitude = "lattitude";
        public static final String longitude = "longitude";
        public static final String assigned_by = "assigned_by";
        public static final String assigned_by_phone = "assigned_by_phone";
        public static final String assigned_by_email = "assigned_by_email";
        public static final String is_sync = "is_sync";
        public static final String signature_image = "signature_image";
        public static final String project_update_status = "project_update_status";
        public static final String project_start_time = "project_start_time";
        public static final String created_date = "created_date";
        public static final String reminder_hours = "reminder_hours";
    }

    public class Phase_details {
        public static final String id = "id";
        public static final String project_id = "project_id";
        public static final String description = "description";
        public static final String created_date = "created_date";
        public static final String is_sync = "is_sync";
        public static final String phase_status = "phase_status";
    }

    public class Notes_details {
        public static final String id = "id";
        public static final String project_id = "project_id";
        public static final String note = "note";
        public static final String created_date = "created_date";
        public static final String is_sync = "is_sync";
        public static final String note_status = "note_status";
    }


    public class Media_details {
        public static final String id = "id";
        public static final String project_id = "project_id";
        public static final String media_type = "media_type";
        public static final String media = "media";
        public static final String video_thumb_image = "video_thumb_image";
        public static final String media_description = "media_description";
        public static final String latitude = "latitude";
        public static final String longitude = "longitude";
        public static final String street = "street";
        public static final String city = "city";
        public static final String state = "state";
        public static final String country = "country";
        public static final String pincode = "pincode";
        public static final String created_date = "created_date";
        public static final String is_sync = "is_sync";
        public static final String media_status = "media_status";
        public static final String doc_thumb_image = "doc_thumb_image";
    }

    public class Contacts_details {
        public static final String id = "id";
        public static final String project_id = "project_id";
        public static final String name = "name";
        public static final String company = "company";
        public static final String address = "address";
        public static final String phone = "phone";
        public static final String email = "email";
        public static final String project_responsiblity = "project_responsiblity";
        public static final String project_responsiblity_id = "project_responsiblity_id";
        public static final String is_sync = "is_sync";
        public static final String contact_status = "contact_status";
        public static final String created_date = "created_date";
    }

    public class Responsiblity_details {
        public static final String id = "id";
        public static final String title = "title";
        public static final String user_id = "user_id";
        public static final String is_sync = "is_sync";
    }
}