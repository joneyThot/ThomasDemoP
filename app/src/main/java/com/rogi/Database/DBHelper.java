package com.rogi.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import com.rogi.Model.AdditionalContactModel;
import com.rogi.Model.MediaModel;
import com.rogi.Model.NotesModel;
import com.rogi.Model.ProjectPhaseModel;
import com.rogi.Model.TaskModel;
import com.rogi.View.Utils;
import com.rogi.logger.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by user22 on 22/9/15.
 */
public class DBHelper extends SQLiteOpenHelper {
    // The Android's default system path of your application database.
    private static String TAG = "DBHelper";
    public SQLiteDatabase myDataBase;
    private final Context myContext;

    /**
     * Constructor Takes and keeps a reference of the passed context in order to
     * access to the application assets and resources.
     *
     * @param context
     */
    public DBHelper(Context context) {
        super(context, DBConstants.DB_NAME, null, DBConstants.DB_VERSION);
        this.myContext = context;
    }

    /**
     * Creates a empty database on the system and rewrites it with your own
     * database.
     */
    public void createDataBase() throws IOException {
        boolean dbExist = checkDataBase();
        if (dbExist) {
            // do nothing - database already exist
        } else {
            // By calling this method and empty database will be created into
            // the default system path
            // of your application so we are gonna be able to overwrite that
            // database with our database.
            this.getReadableDatabase();
            try {
                copyDataBase();
            } catch (IOException e) {
                throw new Error("Error copying database");
            }
        }
    }

    /**
     * Check if the database already exist to avoid re-copying the file each
     * time you open the application.
     *
     * @return true if it exists, false if it doesn't
     */
    public boolean checkDataBase() {
        SQLiteDatabase checkDB = null;
        try {
            String myPath = DBConstants.DB_PATH + DBConstants.DB_NAME;
            Logger.e(TAG, "DB Path : " + myPath);
            File file = new File(myPath);
            if (file.exists() && !file.isDirectory())
                checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);
        } catch (SQLiteException e) {
            // database does't exist yet.
        }

        if (checkDB != null) {
            checkDB.close();
        }
        return checkDB != null ? true : false;
    }

    /**
     * Copies your database from your local assets-folder to the just created
     * empty database in the system folder, from where it can be accessed and
     * handled. This is done by transfering bytestream.
     */

    private void copyDataBase() throws IOException {
        // Open your local db as the input stream
        InputStream myInput = myContext.getAssets().open(DBConstants.DB_NAME);
        // Path to the just created empty db
        String outFileName = DBConstants.DB_PATH + DBConstants.DB_NAME;
        // Open the empty db as the output stream
        OutputStream myOutput = new FileOutputStream(outFileName);
        // transfer bytes from the inputfile to the outputfile
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer)) > 0) {
            myOutput.write(buffer, 0, length);
        }

        // Close the streams
        myOutput.flush();
        myOutput.close();
        myInput.close();
    }

    public void openDataBase() throws SQLException {
        // Open the database
        String myPath = DBConstants.DB_PATH + DBConstants.DB_NAME;
        myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);
    }

    @Override
    public synchronized void close() {
        if (myDataBase != null)
            myDataBase.close();
        super.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public void insertProject(SQLiteDatabase db, String id, String title, String description, String start_date, String start_time,
                              String due_date, String due_time, String priority, String project_status, String street, String city,
                              String state, String country, String pincode, String lattitude, String longitude, String assigned_by,
                              String assigned_by_phone, String assigned_by_email, String is_sync, String signature_image,
                              String project_update_status, String project_start_time, String created_date, String reminder_hours) {

        ContentValues cv = new ContentValues();
        cv.put(DBConstants.Project_details.id, id);
        cv.put(DBConstants.Project_details.title, title);
        cv.put(DBConstants.Project_details.description, description);
        cv.put(DBConstants.Project_details.start_date, start_date);
        cv.put(DBConstants.Project_details.start_time, start_time);
        cv.put(DBConstants.Project_details.due_date, due_date);
        cv.put(DBConstants.Project_details.due_time, due_time);
        cv.put(DBConstants.Project_details.priority, priority);
        cv.put(DBConstants.Project_details.project_status, project_status);
        cv.put(DBConstants.Project_details.street, street);
        cv.put(DBConstants.Project_details.city, city);
        cv.put(DBConstants.Project_details.state, state);
        cv.put(DBConstants.Project_details.country, country);
        cv.put(DBConstants.Project_details.pincode, pincode);
        cv.put(DBConstants.Project_details.lattitude, lattitude);
        cv.put(DBConstants.Project_details.longitude, longitude);
        cv.put(DBConstants.Project_details.assigned_by, assigned_by);
        cv.put(DBConstants.Project_details.assigned_by_phone, assigned_by_phone);
        cv.put(DBConstants.Project_details.assigned_by_email, assigned_by_email);
        cv.put(DBConstants.Project_details.is_sync, is_sync);
        cv.put(DBConstants.Project_details.signature_image, signature_image);
        cv.put(DBConstants.Project_details.project_update_status, project_update_status);
        cv.put(DBConstants.Project_details.project_start_time, project_start_time);
        cv.put(DBConstants.Project_details.created_date, created_date);
        cv.put(DBConstants.Project_details.reminder_hours, reminder_hours);
        db.replace(DBConstants.TABLE.PROJECT_DETAILS, null, cv);

    }

    public ArrayList<TaskModel> getProjectDetails() {
        String currentDate = Utils.getCurrentDateForTodays();
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "select * from " + DBConstants.TABLE.PROJECT_DETAILS + " WHERE ('" + currentDate
                + "' >= start_date AND '" + currentDate
                + "' <= due_date) AND (project_update_status = 'A' or project_update_status = 'U')  ORDER BY created_date DESC";
        Logger.e(TAG, "Project : " + selectQuery);
        Cursor cursor = db.rawQuery(selectQuery, null);
        ArrayList<TaskModel> taskModels = new ArrayList<TaskModel>();
        if (cursor != null)
            if (cursor.moveToFirst()) {
                do {
                    TaskModel taskModel = new TaskModel();
                    taskModel.setId(cursor.getString(0));
                    taskModel.setTitle(cursor.getString(1));
                    taskModel.setDescription(cursor.getString(2));
                    taskModel.setStartDate(cursor.getString(3));
                    taskModel.setStartTime(cursor.getString(4));
                    taskModel.setDueDate(cursor.getString(5));
                    taskModel.setDueTime(cursor.getString(6));
                    taskModel.setPriority(cursor.getString(7));
                    taskModel.setProject_status(cursor.getString(8));
                    taskModel.setStreet(cursor.getString(9));
                    taskModel.setCity(cursor.getString(10));
                    taskModel.setState(cursor.getString(11));
                    taskModel.setCountry(cursor.getString(12));
                    taskModel.setPincode(cursor.getString(13));
                    taskModel.setLatitude(cursor.getString(14));
                    taskModel.setLongitude(cursor.getString(15));
                    taskModel.setAssignName(cursor.getString(16));
                    taskModel.setAssignContact(cursor.getString(17));
                    taskModel.setAssignEmail(cursor.getString(18));
                    taskModel.setIs_sync(cursor.getString(19));
                    taskModel.setSignature_image(cursor.getString(20));
                    taskModel.setProject_update_status(cursor.getString(21));
                    taskModel.setProject_start_time(cursor.getString(22));
                    taskModel.setCreated_date(cursor.getString(23));
                    taskModel.setReminder_hours(cursor.getString(24));
                    taskModels.add(taskModel);
                } while (cursor.moveToNext());
            }
        cursor.close();
        return taskModels;
    }

    public ArrayList<TaskModel> getMediaUnSyncProjectDetails() {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "select * from " + DBConstants.TABLE.PROJECT_DETAILS
                + " WHERE project_update_status = 'A' or project_update_status = 'U' ORDER BY created_date DESC";
        Logger.e(TAG, "Project : " + selectQuery);
        Cursor cursor = db.rawQuery(selectQuery, null);
        ArrayList<TaskModel> taskModels = new ArrayList<TaskModel>();
        if (cursor != null)
            if (cursor.moveToFirst()) {
                do {
                    TaskModel taskModel = new TaskModel();
                    taskModel.setId(cursor.getString(0));
                    taskModel.setTitle(cursor.getString(1));
                    taskModel.setDescription(cursor.getString(2));
                    taskModel.setStartDate(cursor.getString(3));
                    taskModel.setStartTime(cursor.getString(4));
                    taskModel.setDueDate(cursor.getString(5));
                    taskModel.setDueTime(cursor.getString(6));
                    taskModel.setPriority(cursor.getString(7));
                    taskModel.setProject_status(cursor.getString(8));
                    taskModel.setStreet(cursor.getString(9));
                    taskModel.setCity(cursor.getString(10));
                    taskModel.setState(cursor.getString(11));
                    taskModel.setCountry(cursor.getString(12));
                    taskModel.setPincode(cursor.getString(13));
                    taskModel.setLatitude(cursor.getString(14));
                    taskModel.setLongitude(cursor.getString(15));
                    taskModel.setAssignName(cursor.getString(16));
                    taskModel.setAssignContact(cursor.getString(17));
                    taskModel.setAssignEmail(cursor.getString(18));
                    taskModel.setIs_sync(cursor.getString(19));
                    taskModel.setSignature_image(cursor.getString(20));
                    taskModel.setProject_update_status(cursor.getString(21));
                    taskModel.setProject_start_time(cursor.getString(22));
                    taskModel.setCreated_date(cursor.getString(23));
                    taskModel.setReminder_hours(cursor.getString(24));
                    taskModels.add(taskModel);
                } while (cursor.moveToNext());
            }
        cursor.close();
        return taskModels;
    }

    public ArrayList<TaskModel> getSyncProjectDetails() {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "select * from " + DBConstants.TABLE.PROJECT_DETAILS + " WHERE is_sync = 'N'";
        Logger.e(TAG, "Project : " + selectQuery);
        Cursor cursor = db.rawQuery(selectQuery, null);
        ArrayList<TaskModel> taskModels = new ArrayList<TaskModel>();
        if (cursor != null)
            if (cursor.moveToFirst()) {
                do {
                    TaskModel taskModel = new TaskModel();
                    taskModel.setId(cursor.getString(0));
                    taskModel.setTitle(cursor.getString(1));
                    taskModel.setDescription(cursor.getString(2));
                    taskModel.setStartDate(cursor.getString(3));
                    taskModel.setStartTime(cursor.getString(4));
                    taskModel.setDueDate(cursor.getString(5));
                    taskModel.setDueTime(cursor.getString(6));
                    taskModel.setPriority(cursor.getString(7));
                    taskModel.setProject_status(cursor.getString(8));
                    taskModel.setStreet(cursor.getString(9));
                    taskModel.setCity(cursor.getString(10));
                    taskModel.setState(cursor.getString(11));
                    taskModel.setCountry(cursor.getString(12));
                    taskModel.setPincode(cursor.getString(13));
                    taskModel.setLatitude(cursor.getString(14));
                    taskModel.setLongitude(cursor.getString(15));
                    taskModel.setAssignName(cursor.getString(16));
                    taskModel.setAssignContact(cursor.getString(17));
                    taskModel.setAssignEmail(cursor.getString(18));
                    taskModel.setIs_sync(cursor.getString(19));
                    taskModel.setSignature_image(cursor.getString(20));
                    taskModel.setProject_update_status(cursor.getString(21));
                    taskModel.setProject_start_time(cursor.getString(22));
                    taskModel.setCreated_date(cursor.getString(23));
                    taskModel.setReminder_hours(cursor.getString(24));
                    taskModels.add(taskModel);
                } while (cursor.moveToNext());
            }
        cursor.close();
        return taskModels;
    }

    public int updateProjectDetails(String id, String title, String description, String start_date, String start_time,
                                    String due_date, String due_time, String priority, String project_status, String street, String city,
                                    String state, String country, String pincode, String lattitude, String longitude, String assigned_by,
                                    String assigned_by_phone, String assigned_by_email, String is_sync, String signature_image,
                                    String project_update_status, String project_start_time, String created_date, String reminder_hours) {
        ContentValues cv = new ContentValues();
        cv.put(DBConstants.Project_details.id, id);
        cv.put(DBConstants.Project_details.title, title);
        cv.put(DBConstants.Project_details.description, description);
        cv.put(DBConstants.Project_details.start_date, start_date);
        cv.put(DBConstants.Project_details.start_time, start_time);
        cv.put(DBConstants.Project_details.due_date, due_date);
        cv.put(DBConstants.Project_details.due_time, due_time);
        cv.put(DBConstants.Project_details.priority, priority);
        cv.put(DBConstants.Project_details.project_status, project_status);
        cv.put(DBConstants.Project_details.street, street);
        cv.put(DBConstants.Project_details.city, city);
        cv.put(DBConstants.Project_details.state, state);
        cv.put(DBConstants.Project_details.country, country);
        cv.put(DBConstants.Project_details.pincode, pincode);
        cv.put(DBConstants.Project_details.lattitude, lattitude);
        cv.put(DBConstants.Project_details.longitude, longitude);
        cv.put(DBConstants.Project_details.assigned_by, assigned_by);
        cv.put(DBConstants.Project_details.assigned_by_phone, assigned_by_phone);
        cv.put(DBConstants.Project_details.assigned_by_email, assigned_by_email);
        cv.put(DBConstants.Project_details.is_sync, is_sync);
        cv.put(DBConstants.Project_details.signature_image, signature_image);
        cv.put(DBConstants.Project_details.project_update_status, project_update_status);
        cv.put(DBConstants.Project_details.project_start_time, project_start_time);
        cv.put(DBConstants.Project_details.created_date, created_date);
        cv.put(DBConstants.Project_details.reminder_hours,reminder_hours);

        return myDataBase.update(DBConstants.TABLE.PROJECT_DETAILS, cv, "id=?", new String[]{id});
    }

    public ArrayList<TaskModel> getFilterProjectDetails(String priority, String startDate, String dueDate) {
        String currentDate = Utils.getCurrentDateForTodays();
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "";

        if (Utils.validateString(priority) && Utils.validateString(startDate) && Utils.validateString(dueDate)) {

//            selectQuery = "select * from " + DBConstants.TABLE.PROJECT_DETAILS + " WHERE (start_date >= '" +
//                    startDate + "' AND due_date <= '" + dueDate + "') AND (project_update_status = 'A' or project_update_status = 'U') " +
//                    "ORDER BY priority " + priority;

            selectQuery = "select * from " + DBConstants.TABLE.PROJECT_DETAILS + " WHERE (start_date BETWEEN '" +
                    startDate + "' AND '" + dueDate + "') OR (due_date BETWEEN '"
                    + startDate + "' AND '" + dueDate + "') AND (project_update_status = 'A' or project_update_status = 'U') " +
                    " ORDER BY priority " + priority;

        } else if (Utils.validateString(priority) && Utils.validateString(startDate)) {

//            selectQuery = "select * from " + DBConstants.TABLE.PROJECT_DETAILS + " WHERE start_date >= '" +
//                    startDate + "' AND (project_update_status = 'A' or project_update_status = 'U') " +
//                    "ORDER BY priority " + priority;

            selectQuery = "select * from " + DBConstants.TABLE.PROJECT_DETAILS + " WHERE (start_date BETWEEN '" +
                    startDate + "' AND '" + dueDate + "') AND (project_update_status = 'A' or project_update_status = 'U') " +
                    "ORDER BY priority " + priority;

        } else if (Utils.validateString(priority) && Utils.validateString(dueDate)) {

//            selectQuery = "select * from " + DBConstants.TABLE.PROJECT_DETAILS +
//                    " WHERE due_date <= '" + dueDate + "') AND (project_update_status = 'A' or project_update_status = 'U') " +
//                    "ORDER BY priority " + priority;

            selectQuery = "select * from " + DBConstants.TABLE.PROJECT_DETAILS +
                    " WHERE (due_date BETWEEN '" +
                    startDate + "' AND '" + dueDate + "') AND (project_update_status = 'A' or project_update_status = 'U') " +
                    "ORDER BY priority " + priority;

        } else if (Utils.validateString(startDate) && Utils.validateString(dueDate)) {

//            selectQuery = "select * from " + DBConstants.TABLE.PROJECT_DETAILS + " WHERE (start_date >= '" +
//                    startDate + "' AND due_date <= '" + dueDate + "') AND (project_update_status = 'A' or project_update_status = 'U') " +
//                    "ORDER BY created_date DESC";

            selectQuery = "select * from " + DBConstants.TABLE.PROJECT_DETAILS + " WHERE (start_date BETWEEN '" +
                    startDate + "' AND '" + dueDate + "') OR (due_date BETWEEN '" +
                    startDate + "' AND '" + dueDate + "') AND (project_update_status = 'A' or project_update_status = 'U') " +
                    "ORDER BY created_date DESC";

        } else {
            selectQuery = "select * from " + DBConstants.TABLE.PROJECT_DETAILS +
                    " WHERE project_update_status = 'A' or project_update_status = 'U' " +
                    "ORDER BY priority " + priority;
        }

        Logger.e(TAG, "Project : " + selectQuery);
        Cursor cursor = db.rawQuery(selectQuery, null);
        ArrayList<TaskModel> taskModels = new ArrayList<TaskModel>();
        if (cursor != null)
            if (cursor.moveToFirst()) {
                do {
                    TaskModel taskModel = new TaskModel();
                    taskModel.setId(cursor.getString(0));
                    taskModel.setTitle(cursor.getString(1));
                    taskModel.setDescription(cursor.getString(2));
                    taskModel.setStartDate(cursor.getString(3));
                    taskModel.setStartTime(cursor.getString(4));
                    taskModel.setDueDate(cursor.getString(5));
                    taskModel.setDueTime(cursor.getString(6));
                    taskModel.setPriority(cursor.getString(7));
                    taskModel.setProject_status(cursor.getString(8));
                    taskModel.setStreet(cursor.getString(9));
                    taskModel.setCity(cursor.getString(10));
                    taskModel.setState(cursor.getString(11));
                    taskModel.setCountry(cursor.getString(12));
                    taskModel.setPincode(cursor.getString(13));
                    taskModel.setLatitude(cursor.getString(14));
                    taskModel.setLongitude(cursor.getString(15));
                    taskModel.setAssignName(cursor.getString(16));
                    taskModel.setAssignContact(cursor.getString(17));
                    taskModel.setAssignEmail(cursor.getString(18));
                    taskModel.setIs_sync(cursor.getString(19));
                    taskModel.setSignature_image(cursor.getString(20));
                    taskModel.setProject_update_status(cursor.getString(21));
                    taskModel.setProject_start_time(cursor.getString(22));
                    taskModel.setCreated_date(cursor.getString(23));
                    taskModel.setReminder_hours(cursor.getString(24));
                    taskModels.add(taskModel);
                } while (cursor.moveToNext());
            }
        cursor.close();
        return taskModels;
    }

    public ArrayList<TaskModel> getHistroyProjectDetails(String projectStatus) {
        String currentDate = Utils.getCurrentDateForTodays();
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "select * from " + DBConstants.TABLE.PROJECT_DETAILS + " WHERE (project_status = '" +
                projectStatus + "' OR '" + currentDate + "' > due_date) AND (project_update_status = 'A' OR project_update_status = 'U') " +
                "ORDER BY created_date DESC";

        Logger.e(TAG, "Project : " + selectQuery);
        Cursor cursor = db.rawQuery(selectQuery, null);
        ArrayList<TaskModel> taskModels = new ArrayList<TaskModel>();
        if (cursor != null)
            if (cursor.moveToFirst()) {
                do {
                    TaskModel taskModel = new TaskModel();
                    taskModel.setId(cursor.getString(0));
                    taskModel.setTitle(cursor.getString(1));
                    taskModel.setDescription(cursor.getString(2));
                    taskModel.setStartDate(cursor.getString(3));
                    taskModel.setStartTime(cursor.getString(4));
                    taskModel.setDueDate(cursor.getString(5));
                    taskModel.setDueTime(cursor.getString(6));
                    taskModel.setPriority(cursor.getString(7));
                    taskModel.setProject_status(cursor.getString(8));
                    taskModel.setStreet(cursor.getString(9));
                    taskModel.setCity(cursor.getString(10));
                    taskModel.setState(cursor.getString(11));
                    taskModel.setCountry(cursor.getString(12));
                    taskModel.setPincode(cursor.getString(13));
                    taskModel.setLatitude(cursor.getString(14));
                    taskModel.setLongitude(cursor.getString(15));
                    taskModel.setAssignName(cursor.getString(16));
                    taskModel.setAssignContact(cursor.getString(17));
                    taskModel.setAssignEmail(cursor.getString(18));
                    taskModel.setIs_sync(cursor.getString(19));
                    taskModel.setSignature_image(cursor.getString(20));
                    taskModel.setProject_update_status(cursor.getString(21));
                    taskModel.setProject_start_time(cursor.getString(22));
                    taskModel.setCreated_date(cursor.getString(23));
                    taskModel.setReminder_hours(cursor.getString(24));
                    taskModels.add(taskModel);
                } while (cursor.moveToNext());
            }
        cursor.close();
        return taskModels;
    }

    public ArrayList<TaskModel> getHistroyStatusWiseProjectDetails(String projectStatus) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "select * from " + DBConstants.TABLE.PROJECT_DETAILS + " WHERE project_status = '" +
                projectStatus + "' AND (project_update_status = 'A' OR project_update_status = 'U') " +
                "ORDER BY created_date DESC";

        Logger.e(TAG, "Project : " + selectQuery);
        Cursor cursor = db.rawQuery(selectQuery, null);
        ArrayList<TaskModel> taskModels = new ArrayList<TaskModel>();
        if (cursor != null)
            if (cursor.moveToFirst()) {
                do {
                    TaskModel taskModel = new TaskModel();
                    taskModel.setId(cursor.getString(0));
                    taskModel.setTitle(cursor.getString(1));
                    taskModel.setDescription(cursor.getString(2));
                    taskModel.setStartDate(cursor.getString(3));
                    taskModel.setStartTime(cursor.getString(4));
                    taskModel.setDueDate(cursor.getString(5));
                    taskModel.setDueTime(cursor.getString(6));
                    taskModel.setPriority(cursor.getString(7));
                    taskModel.setProject_status(cursor.getString(8));
                    taskModel.setStreet(cursor.getString(9));
                    taskModel.setCity(cursor.getString(10));
                    taskModel.setState(cursor.getString(11));
                    taskModel.setCountry(cursor.getString(12));
                    taskModel.setPincode(cursor.getString(13));
                    taskModel.setLatitude(cursor.getString(14));
                    taskModel.setLongitude(cursor.getString(15));
                    taskModel.setAssignName(cursor.getString(16));
                    taskModel.setAssignContact(cursor.getString(17));
                    taskModel.setAssignEmail(cursor.getString(18));
                    taskModel.setIs_sync(cursor.getString(19));
                    taskModel.setSignature_image(cursor.getString(20));
                    taskModel.setProject_update_status(cursor.getString(21));
                    taskModel.setProject_start_time(cursor.getString(22));
                    taskModel.setCreated_date(cursor.getString(23));
                    taskModel.setReminder_hours(cursor.getString(24));
                    taskModels.add(taskModel);
                } while (cursor.moveToNext());
            }
        cursor.close();
        return taskModels;
    }

    public ArrayList<TaskModel> getHistroyFilterProjectDetails(String priority, String projectStatus, String startDate,
                                                               String dueDate) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "";

        if (Utils.validateString(priority) && Utils.validateString(projectStatus) && Utils.validateString(startDate) && Utils.validateString(dueDate)) {

//            selectQuery = "select * from " + DBConstants.TABLE.PROJECT_DETAILS + " WHERE (start_date >= '" +
//                    startDate + "' AND due_date <= '" + dueDate + "') AND (project_update_status = 'A' or project_update_status = 'U') AND " +
//                    projectStatus + " ORDER BY priority " + priority;

            selectQuery = "select * from " + DBConstants.TABLE.PROJECT_DETAILS + " WHERE (start_date BETWEEN '" +
                    startDate + "' AND '" + dueDate + "') OR (due_date BETWEEN '"
                    + startDate + "' AND '" + dueDate + "') AND (project_update_status = 'A' or project_update_status = 'U') AND " +
                    projectStatus + " ORDER BY priority " + priority;

        } else if (Utils.validateString(priority) && Utils.validateString(projectStatus) && Utils.validateString(startDate)) {

//            selectQuery = "select * from " + DBConstants.TABLE.PROJECT_DETAILS + " WHERE start_date >= '" +
//                    startDate + "' AND (project_update_status = 'A' or project_update_status = 'U') AND " +
//                    projectStatus + " ORDER BY priority " + priority;

            selectQuery = "select * from " + DBConstants.TABLE.PROJECT_DETAILS + " WHERE start_date BETWEEN '" +
                    startDate + "' AND '" + dueDate + "') AND (project_update_status = 'A' or project_update_status = 'U') AND " +
                    projectStatus + " ORDER BY priority " + priority;

        } else if (Utils.validateString(priority) && Utils.validateString(projectStatus) && Utils.validateString(dueDate)) {

//            selectQuery = "select * from " + DBConstants.TABLE.PROJECT_DETAILS + " WHERE due_date <= '" +
//                    dueDate + "' AND (project_update_status = 'A' or project_update_status = 'U') AND " +
//                    projectStatus + " ORDER BY priority " + priority;

            selectQuery = "select * from " + DBConstants.TABLE.PROJECT_DETAILS + " WHERE due_date BETWEEN '" +
                    startDate + "' AND '" + dueDate + "') AND (project_update_status = 'A' or project_update_status = 'U') AND " +
                    projectStatus + " ORDER BY priority " + priority;

        } else if (Utils.validateString(priority) && Utils.validateString(projectStatus)) {

            selectQuery = "select * from " + DBConstants.TABLE.PROJECT_DETAILS +
                    " WHERE (project_update_status = 'A' or project_update_status = 'U') AND " + projectStatus +
                    " ORDER BY priority " + priority;

        } else if (Utils.validateString(startDate) && Utils.validateString(dueDate) && Utils.validateString(projectStatus)) {

//            selectQuery = "select * from " + DBConstants.TABLE.PROJECT_DETAILS + " WHERE (start_date >= '" +
//                    startDate + "' AND due_date <= '" + dueDate + "') AND (project_update_status = 'A' or project_update_status = 'U') AND " +
//                    projectStatus + " ORDER BY created_date DESC";

            selectQuery = "select * from " + DBConstants.TABLE.PROJECT_DETAILS + " WHERE (start_date BETWEEN '" +
                    startDate + "' AND '" + dueDate + "') OR (due_date BETWEEN '" +
                    startDate + "' AND '" + dueDate + "') AND (project_update_status = 'A' or project_update_status = 'U') AND " +
                    projectStatus + " ORDER BY created_date DESC";

        } else {

            selectQuery = "select * from " + DBConstants.TABLE.PROJECT_DETAILS +
                    " WHERE (project_update_status = 'A' or project_update_status = 'U') AND " + projectStatus +
                    " ORDER BY created_date DESC";
        }

        Logger.e(TAG, "Project : " + selectQuery);
        Cursor cursor = db.rawQuery(selectQuery, null);
        ArrayList<TaskModel> taskModels = new ArrayList<TaskModel>();
        if (cursor != null)
            if (cursor.moveToFirst()) {
                do {
                    TaskModel taskModel = new TaskModel();
                    taskModel.setId(cursor.getString(0));
                    taskModel.setTitle(cursor.getString(1));
                    taskModel.setDescription(cursor.getString(2));
                    taskModel.setStartDate(cursor.getString(3));
                    taskModel.setStartTime(cursor.getString(4));
                    taskModel.setDueDate(cursor.getString(5));
                    taskModel.setDueTime(cursor.getString(6));
                    taskModel.setPriority(cursor.getString(7));
                    taskModel.setProject_status(cursor.getString(8));
                    taskModel.setStreet(cursor.getString(9));
                    taskModel.setCity(cursor.getString(10));
                    taskModel.setState(cursor.getString(11));
                    taskModel.setCountry(cursor.getString(12));
                    taskModel.setPincode(cursor.getString(13));
                    taskModel.setLatitude(cursor.getString(14));
                    taskModel.setLongitude(cursor.getString(15));
                    taskModel.setAssignName(cursor.getString(16));
                    taskModel.setAssignContact(cursor.getString(17));
                    taskModel.setAssignEmail(cursor.getString(18));
                    taskModel.setIs_sync(cursor.getString(19));
                    taskModel.setSignature_image(cursor.getString(20));
                    taskModel.setProject_update_status(cursor.getString(21));
                    taskModel.setProject_start_time(cursor.getString(22));
                    taskModel.setCreated_date(cursor.getString(23));
                    taskModel.setReminder_hours(cursor.getString(24));
                    taskModels.add(taskModel);
                } while (cursor.moveToNext());
            }
        cursor.close();
        return taskModels;
    }

    public Cursor updateSyncProject(SQLiteDatabase db, String TableName, String fieldIdValue,
                                    String fieldSyncValue, String fieldStatus, String fieldStatusValue, String tempID) {
//        update project_details SET id = '101',is_sync = 'Y',project_update_status = "U" where id = 'xymsdf';
        String updateQuery = "UPDATE " + TableName + " SET id = '" + fieldIdValue + "',is_sync = '" +
                fieldSyncValue + "'," + fieldStatus + " = '" + fieldStatusValue + "' WHERE id" + " = '" + tempID + "'";
        Logger.e(TAG, "" + updateQuery);
        Cursor cursor = db.rawQuery(updateQuery, null);
        if (cursor != null)
            cursor.moveToFirst();
        cursor.close();
        return cursor;
    }

    public Cursor updateSyncProjectDetails(SQLiteDatabase db, String TableName, String fieldIdValue, String fieldSyncValue,
                                           String projectID, String fieldStatus, String fieldStatusValue, String tempID) {
//        update project_details SET id = '101',project_id = '101',is_sync = 'Y',project_update_status = "U" where id = 'xymsdf';
        String updateQuery = "UPDATE " + TableName + " SET id = '" + fieldIdValue + "',is_sync = '" +
                fieldSyncValue + "',project_id = '" + projectID + "'," + fieldStatus + " = '" + fieldStatusValue + "' WHERE id" + " = '" + tempID + "'";
        Logger.e(TAG, "" + updateQuery);
        Cursor cursor = db.rawQuery(updateQuery, null);
        if (cursor != null)
            cursor.moveToFirst();
        cursor.close();
        return cursor;
    }

    public Cursor updateMediaProjectID(SQLiteDatabase db, String TableName, String fieldIdValue, String tempID) {
//        update project_details SET project_id = '101' where project_id = 'xymsdf';
        String updateQuery = "UPDATE " + TableName + " SET project_id = '" + fieldIdValue + "' WHERE project_id" + " = '" + tempID + "'";
        Logger.e(TAG, "" + updateQuery);
        Cursor cursor = db.rawQuery(updateQuery, null);
        if (cursor != null)
            cursor.moveToFirst();
        cursor.close();
        return cursor;
    }

    public Cursor updateSyncMediaDetails(String TableName, String fieldIdValue, String fieldSyncValue,
                                         String projectID, String fieldStatus, String fieldStatusValue, String tempID) {
//        update project_details SET id = '101',project_id = '101',is_sync = 'Y',project_update_status = "U" where id = 'xymsdf';
        SQLiteDatabase db = this.getReadableDatabase();
        String updateQuery = "UPDATE " + TableName + " SET id = '" + fieldIdValue + "',is_sync = '" +
                fieldSyncValue + "',project_id = '" + projectID + "'," + fieldStatus + " = '" + fieldStatusValue + "' WHERE id" + " = '" + tempID + "'";
        Logger.e(TAG, "" + updateQuery);
        Cursor cursor = db.rawQuery(updateQuery, null);
        if (cursor != null)
            cursor.moveToFirst();
        cursor.close();
        return cursor;
    }

   /* public void updateSyncProjectDetails(String TableName, String fieldIdValue,
                                         String fieldSyncValue, String fieldStatus, String fieldStatusValue, String tempID) {
        SQLiteDatabase db = this.getWritableDatabase();
        String updateQuery = "UPDATE " + TableName + " SET id = '" + fieldIdValue + "',is_sync = '" +
                fieldSyncValue + "'," + fieldStatus + " = '" + fieldStatusValue + "' WHERE id" + " = '" + tempID + "'";
        Logger.e(TAG, "" + updateQuery);
        Object[] bindArgs = {"id", "is_sync", "project_update_status"};
        db.execSQL(updateQuery, bindArgs);
    }*/

    /*public int updateSyncNotesDetails(String id, String project_id, String is_sync, String note_status, String tempID) {
        ContentValues cv = new ContentValues();
        cv.put(DBConstants.Notes_details.id, id);
        cv.put(DBConstants.Notes_details.project_id, project_id);
        cv.put(DBConstants.Notes_details.is_sync, is_sync);
        cv.put(DBConstants.Notes_details.note_status, note_status);

        return myDataBase.update(DBConstants.TABLE.NOTES_DETAILS, cv, "id='" + tempID, null);
    }

    public int updateSyncPhaseDetails(String id, String project_id, String is_sync, String phase_status, String tempID) {
        ContentValues cv = new ContentValues();
        cv.put(DBConstants.Phase_details.id, id);
        cv.put(DBConstants.Phase_details.project_id, project_id);
        cv.put(DBConstants.Phase_details.is_sync, is_sync);
        cv.put(DBConstants.Phase_details.phase_status, phase_status);

        return myDataBase.update(DBConstants.TABLE.PHASE_DETAILS, cv, "id='" + tempID, null);
    }

    public int updateSyncContactDetails(String id, String project_id, String is_sync, String contact_status, String tempID) {
        ContentValues cv = new ContentValues();
        cv.put(DBConstants.Contacts_details.id, id);
        cv.put(DBConstants.Contacts_details.project_id, project_id);
        cv.put(DBConstants.Contacts_details.is_sync, is_sync);
        cv.put(DBConstants.Contacts_details.contact_status, contact_status);

        return myDataBase.update(DBConstants.TABLE.CONTACTS_DETAILS, cv, "id='" + tempID, null);
    }

    public int updateSyncMediaProjectID(String project_id, String tempID) {
        ContentValues cv = new ContentValues();
        cv.put(DBConstants.Media_details.project_id, project_id);

        return myDataBase.update(DBConstants.TABLE.MEDIA_DETAILS, cv, "project_id='" + tempID, null);
    }

    public int updateSyncMediaDetails(String id, String project_id, String is_sync, String media_status, String tempID) {
        ContentValues cv = new ContentValues();
        cv.put(DBConstants.Media_details.id, id);
        cv.put(DBConstants.Media_details.project_id, project_id);
        cv.put(DBConstants.Media_details.is_sync, is_sync);
        cv.put(DBConstants.Media_details.media_status, media_status);

        return myDataBase.update(DBConstants.TABLE.MEDIA_DETAILS, cv, "id='" + tempID, null);
    }*/

    public int todaysCount() {
        String currrentDate = Utils.getCurrentDateForTodays();
        SQLiteDatabase db = this.getReadableDatabase();
        String countQuery = "SELECT COUNT(*) FROM " + DBConstants.TABLE.PROJECT_DETAILS +
                " WHERE ('" + currrentDate + "' >= start_date AND '" + currrentDate +
                "' <= due_date) AND (project_update_status = 'A' OR project_update_status = 'U')";
        Logger.e(TAG, "" + countQuery);
        Cursor cursor = db.rawQuery(countQuery, null);
        int x = 0;
        if (cursor.moveToFirst()) {
            x = cursor.getInt(0);
        }
        cursor.close();
        return x;
    }

    public int othersCount(String project_status) {
        SQLiteDatabase db = this.getReadableDatabase();
        String countQuery = "SELECT COUNT(*) FROM " + DBConstants.TABLE.PROJECT_DETAILS +
                " WHERE project_status = '" + project_status
                + "' AND (project_update_status = 'A' OR project_update_status = 'U')";
        Logger.e(TAG, "" + countQuery);
        Cursor cursor = db.rawQuery(countQuery, null);
        int x = 0;
        if (cursor.moveToFirst()) {
            x = cursor.getInt(0);
        }
        cursor.close();
        return x;
    }

    public void insertProjectPhase(SQLiteDatabase db, String id, String project_id, String description, String created_date,
                                   String is_sync, String phase_Status) {

        ContentValues cv = new ContentValues();
        cv.put(DBConstants.Phase_details.id, id);
        cv.put(DBConstants.Phase_details.project_id, project_id);
        cv.put(DBConstants.Phase_details.description, description);
        cv.put(DBConstants.Phase_details.created_date, created_date);
        cv.put(DBConstants.Phase_details.is_sync, is_sync);
        cv.put(DBConstants.Phase_details.phase_status, phase_Status);
        db.replace(DBConstants.TABLE.PHASE_DETAILS, null, cv);
    }

    public ArrayList<ProjectPhaseModel> getProjectPhaseDetails() {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "select * from " + DBConstants.TABLE.PHASE_DETAILS +
                " where phase_status = 'A' OR phase_status = 'U'";
        Logger.e(TAG, "Project Phase : " + selectQuery);
        Cursor cursor = db.rawQuery(selectQuery, null);
        ArrayList<ProjectPhaseModel> projectPhaseModels = new ArrayList<ProjectPhaseModel>();
        if (cursor != null)
            if (cursor.moveToFirst()) {
                do {
                    ProjectPhaseModel phaseModel = new ProjectPhaseModel();
                    phaseModel.setProjectPhaseId(cursor.getString(0));
                    phaseModel.setProject_id(cursor.getString(1));
                    phaseModel.setProjectDesription(cursor.getString(2));
                    phaseModel.setProjectCreatedDate(cursor.getString(3));
                    phaseModel.setIs_sync(cursor.getString(4));
                    phaseModel.setPhase_status(cursor.getString(5));
                    projectPhaseModels.add(phaseModel);
                } while (cursor.moveToNext());
            }
        cursor.close();
        return projectPhaseModels;
    }

    public ArrayList<ProjectPhaseModel> getSyncProjectPhaseDetails() {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "select * from " + DBConstants.TABLE.PHASE_DETAILS +
                " WHERE is_sync = 'N'";
        Logger.e(TAG, "Project Phase : " + selectQuery);
        Cursor cursor = db.rawQuery(selectQuery, null);
        ArrayList<ProjectPhaseModel> projectPhaseModels = new ArrayList<ProjectPhaseModel>();
        if (cursor != null)
            if (cursor.moveToFirst()) {
                do {
                    ProjectPhaseModel phaseModel = new ProjectPhaseModel();
                    phaseModel.setProjectPhaseId(cursor.getString(0));
                    phaseModel.setProject_id(cursor.getString(1));
                    phaseModel.setProjectDesription(cursor.getString(2));
                    phaseModel.setProjectCreatedDate(cursor.getString(3));
                    phaseModel.setIs_sync(cursor.getString(4));
                    phaseModel.setPhase_status(cursor.getString(5));
                    projectPhaseModels.add(phaseModel);
                } while (cursor.moveToNext());
            }
        cursor.close();
        return projectPhaseModels;
    }

    public int updateProjectPhaseDetails(String id, String project_id, String description, String created_date,
                                         String is_sync, String phase_Status) {
        ContentValues cv = new ContentValues();
        cv.put(DBConstants.Phase_details.id, id);
        cv.put(DBConstants.Phase_details.project_id, project_id);
        cv.put(DBConstants.Phase_details.description, description);
        cv.put(DBConstants.Phase_details.created_date, created_date);
        cv.put(DBConstants.Phase_details.is_sync, is_sync);
        cv.put(DBConstants.Phase_details.phase_status, phase_Status);

        return myDataBase.update(DBConstants.TABLE.PHASE_DETAILS, cv, "id=?", new String[]{id});
    }

    public int deleteProjectPhaseDetails(String id) {
        return myDataBase.delete(DBConstants.TABLE.PHASE_DETAILS, "id=?", new String[]{id});
    }

    public void insertProjectNotes(SQLiteDatabase db, String id, String project_id, String note, String created_date,
                                   String is_sync, String note_status) {

        ContentValues cv = new ContentValues();
        cv.put(DBConstants.Notes_details.id, id);
        cv.put(DBConstants.Notes_details.project_id, project_id);
        cv.put(DBConstants.Notes_details.note, note);
        cv.put(DBConstants.Notes_details.created_date, created_date);
        cv.put(DBConstants.Notes_details.is_sync, is_sync);
        cv.put(DBConstants.Notes_details.note_status, note_status);
        db.replace(DBConstants.TABLE.NOTES_DETAILS, null, cv);
    }

    public ArrayList<NotesModel> getProjectNotesDetails() {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "select * from " + DBConstants.TABLE.NOTES_DETAILS +
                " where note_status = 'A' OR note_status = 'U'";

        Logger.e(TAG, "Project Notes : " + selectQuery);
        Cursor cursor = db.rawQuery(selectQuery, null);
        ArrayList<NotesModel> notesModelArrayList = new ArrayList<>();
        if (cursor != null)
            if (cursor.moveToFirst()) {
                do {
                    NotesModel notesModel = new NotesModel();
                    notesModel.setNoteId(cursor.getString(0));
                    notesModel.setProject_id(cursor.getString(1));
                    notesModel.setNote(cursor.getString(2));
                    notesModel.setNote_created_date(cursor.getString(3));
                    notesModel.setIs_sync(cursor.getString(4));
                    notesModel.setNote_status(cursor.getString(5));
                    notesModelArrayList.add(notesModel);
                } while (cursor.moveToNext());
            }
        cursor.close();
        return notesModelArrayList;
    }

    public ArrayList<NotesModel> getSyncProjectNotesDetails() {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "select * from " + DBConstants.TABLE.NOTES_DETAILS +
                " WHERE is_sync = 'N'";

        Logger.e(TAG, "Project Notes : " + selectQuery);
        Cursor cursor = db.rawQuery(selectQuery, null);
        ArrayList<NotesModel> notesModelArrayList = new ArrayList<>();
        if (cursor != null)
            if (cursor.moveToFirst()) {
                do {
                    NotesModel notesModel = new NotesModel();
                    notesModel.setNoteId(cursor.getString(0));
                    notesModel.setProject_id(cursor.getString(1));
                    notesModel.setNote(cursor.getString(2));
                    notesModel.setNote_created_date(cursor.getString(3));
                    notesModel.setIs_sync(cursor.getString(4));
                    notesModel.setNote_status(cursor.getString(5));
                    notesModelArrayList.add(notesModel);
                } while (cursor.moveToNext());
            }
        cursor.close();
        return notesModelArrayList;
    }

    public int updateProjectNotes(String id, String project_id, String note, String created_date,
                                  String is_sync, String note_status) {
        ContentValues cv = new ContentValues();
        cv.put(DBConstants.Notes_details.id, id);
        cv.put(DBConstants.Notes_details.project_id, project_id);
        cv.put(DBConstants.Notes_details.note, note);
        cv.put(DBConstants.Notes_details.created_date, created_date);
        cv.put(DBConstants.Notes_details.is_sync, is_sync);
        cv.put(DBConstants.Notes_details.note_status, note_status);

        return myDataBase.update(DBConstants.TABLE.NOTES_DETAILS, cv, "id=?", new String[]{id});
    }

    public int deleteProjectNotes(String id) {
        return myDataBase.delete(DBConstants.TABLE.NOTES_DETAILS, "id=?", new String[]{id});
    }

    public void insertProjectMedia(SQLiteDatabase db, String id, String project_id, String media_type, String media, String video_thumb_image,
                                   String media_description, String latitude, String longitude, String street, String city,
                                   String state, String country, String pincode, String created_date, String is_sync, String media_status,
                                   String doc_thumb_image) {

        ContentValues cv = new ContentValues();
        cv.put(DBConstants.Media_details.id, id);
        cv.put(DBConstants.Media_details.project_id, project_id);
        cv.put(DBConstants.Media_details.media_type, media_type);
        cv.put(DBConstants.Media_details.media, media);
        cv.put(DBConstants.Media_details.video_thumb_image, video_thumb_image);
        cv.put(DBConstants.Media_details.media_description, media_description);
        cv.put(DBConstants.Media_details.latitude, latitude);
        cv.put(DBConstants.Media_details.longitude, longitude);
        cv.put(DBConstants.Media_details.street, street);
        cv.put(DBConstants.Media_details.city, city);
        cv.put(DBConstants.Media_details.state, state);
        cv.put(DBConstants.Media_details.country, country);
        cv.put(DBConstants.Media_details.pincode, pincode);
        cv.put(DBConstants.Media_details.created_date, created_date);
        cv.put(DBConstants.Media_details.is_sync, is_sync);
        cv.put(DBConstants.Media_details.media_status, media_status);
        cv.put(DBConstants.Media_details.doc_thumb_image, doc_thumb_image);
        db.replace(DBConstants.TABLE.MEDIA_DETAILS, null, cv);
    }

    public int updateProjectMedia(String id, String project_id, String media_type, String media, String video_thumb_image,
                                  String media_description, String latitude, String longitude, String street, String city,
                                  String state, String country, String pincode, String created_date, String is_sync,
                                  String media_status, String doc_thumb_image) {
        ContentValues cv = new ContentValues();
        cv.put(DBConstants.Media_details.id, id);
        cv.put(DBConstants.Media_details.project_id, project_id);
        cv.put(DBConstants.Media_details.media_type, media_type);
        cv.put(DBConstants.Media_details.media, media);
        cv.put(DBConstants.Media_details.video_thumb_image, video_thumb_image);
        cv.put(DBConstants.Media_details.media_description, media_description);
        cv.put(DBConstants.Media_details.latitude, latitude);
        cv.put(DBConstants.Media_details.longitude, longitude);
        cv.put(DBConstants.Media_details.street, street);
        cv.put(DBConstants.Media_details.city, city);
        cv.put(DBConstants.Media_details.state, state);
        cv.put(DBConstants.Media_details.country, country);
        cv.put(DBConstants.Media_details.pincode, pincode);
        cv.put(DBConstants.Media_details.created_date, created_date);
        cv.put(DBConstants.Media_details.is_sync, is_sync);
        cv.put(DBConstants.Media_details.media_status, media_status);
        cv.put(DBConstants.Media_details.doc_thumb_image, doc_thumb_image);

        return myDataBase.update(DBConstants.TABLE.MEDIA_DETAILS, cv, "id=?", new String[]{id});
    }

    public ArrayList<MediaModel> updateSyncProjectMedia(String id, String project_id, String media_type, String media, String video_thumb_image,
                                                        String media_description, String latitude, String longitude, String street, String city,
                                                        String state, String country, String pincode, String created_date, String is_sync, String media_status, String tempID) {
        SQLiteDatabase db = this.getReadableDatabase();
//        update project_details SET id = '101',project_id = '101',is_sync = 'Y',project_update_status = "U" where id = 'xymsdf';
        String updateQuery = "update media_details SET id = '" + id + "',project_id = '" + project_id
                + "',media_type = '" + media_type + "',media = '" + media + "',video_thumb_image = '" + video_thumb_image
                + "',media_description = '" + media_description + "',latitude = '" + latitude + "',longitude = '" + longitude
                + "',street = '" + street + "',city = '" + city + "',state = '" + state + "',country = '" + country
                + "',pincode = '" + pincode + "',created_date = '" + created_date + "',is_sync = '" + is_sync
                + "',media_status = '" + media_status + "' WHERE id = '" + tempID + "'";
        Logger.e(TAG, "" + updateQuery);
        Cursor cursor = db.rawQuery(updateQuery, null);
        ArrayList<MediaModel> mediaModelArrayList = new ArrayList<>();
        if (cursor != null)
            if (cursor.moveToFirst()) {
                do {
                    MediaModel mediaModel = new MediaModel();
                    mediaModel.setMediaId(cursor.getString(0));
                    mediaModel.setProject_id(cursor.getString(1));
                    mediaModel.setMediaType(cursor.getString(2));
                    mediaModel.setMedia(cursor.getString(3));
                    mediaModel.setVideoThumbImage(cursor.getString(4));
                    mediaModel.setMediaDescription(cursor.getString(5));
                    mediaModel.setLatitude(cursor.getString(6));
                    mediaModel.setLongitude(cursor.getString(7));
                    mediaModel.setStreet(cursor.getString(8));
                    mediaModel.setCity(cursor.getString(9));
                    mediaModel.setState(cursor.getString(10));
                    mediaModel.setCountry(cursor.getString(11));
                    mediaModel.setPincode(cursor.getString(12));
                    mediaModel.setCreated_date(cursor.getString(13));
                    mediaModel.setIs_sync(cursor.getString(14));
                    mediaModel.setMedia_status(cursor.getString(15));
                    mediaModelArrayList.add(mediaModel);
                } while (cursor.moveToNext());
            }
        cursor.close();
        return mediaModelArrayList;
    }

    public ArrayList<MediaModel> getProjectMediaDetails() {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "select * from " + DBConstants.TABLE.MEDIA_DETAILS +
                " where media_status = 'A' OR media_status = 'U'";
        Logger.e(TAG, "Project Media : " + selectQuery);
        Cursor cursor = db.rawQuery(selectQuery, null);
        ArrayList<MediaModel> mediaModelArrayList = new ArrayList<>();
        if (cursor != null)
            if (cursor.moveToFirst()) {
                do {
                    MediaModel mediaModel = new MediaModel();
                    mediaModel.setMediaId(cursor.getString(0));
                    mediaModel.setProject_id(cursor.getString(1));
                    mediaModel.setMediaType(cursor.getString(2));
                    mediaModel.setMedia(cursor.getString(3));
                    mediaModel.setVideoThumbImage(cursor.getString(4));
                    mediaModel.setMediaDescription(cursor.getString(5));
                    mediaModel.setLatitude(cursor.getString(6));
                    mediaModel.setLongitude(cursor.getString(7));
                    mediaModel.setStreet(cursor.getString(8));
                    mediaModel.setCity(cursor.getString(9));
                    mediaModel.setState(cursor.getString(10));
                    mediaModel.setCountry(cursor.getString(11));
                    mediaModel.setPincode(cursor.getString(12));
                    mediaModel.setCreated_date(cursor.getString(13));
                    mediaModel.setIs_sync(cursor.getString(14));
                    mediaModel.setMedia_status(cursor.getString(15));
                    mediaModel.setDocThumbImage(cursor.getString(16));
                    mediaModelArrayList.add(mediaModel);
                } while (cursor.moveToNext());
            }
        cursor.close();
        return mediaModelArrayList;
    }

    public ArrayList<MediaModel> getSyncMediaDetails() {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "select * from " + DBConstants.TABLE.MEDIA_DETAILS +
                " WHERE is_sync = 'N'";
        Logger.e(TAG, "Project Media : " + selectQuery);
        Cursor cursor = db.rawQuery(selectQuery, null);
        ArrayList<MediaModel> mediaModelArrayList = new ArrayList<>();
        if (cursor != null)
            if (cursor.moveToFirst()) {
                do {
                    MediaModel mediaModel = new MediaModel();
                    mediaModel.setMediaId(cursor.getString(0));
                    mediaModel.setProject_id(cursor.getString(1));
                    mediaModel.setMediaType(cursor.getString(2));
                    mediaModel.setMedia(cursor.getString(3));
                    mediaModel.setVideoThumbImage(cursor.getString(4));
                    mediaModel.setMediaDescription(cursor.getString(5));
                    mediaModel.setLatitude(cursor.getString(6));
                    mediaModel.setLongitude(cursor.getString(7));
                    mediaModel.setStreet(cursor.getString(8));
                    mediaModel.setCity(cursor.getString(9));
                    mediaModel.setState(cursor.getString(10));
                    mediaModel.setCountry(cursor.getString(11));
                    mediaModel.setPincode(cursor.getString(12));
                    mediaModel.setCreated_date(cursor.getString(13));
                    mediaModel.setIs_sync(cursor.getString(14));
                    mediaModel.setMedia_status(cursor.getString(15));
                    mediaModelArrayList.add(mediaModel);
                } while (cursor.moveToNext());
            }
        cursor.close();
        return mediaModelArrayList;
    }


    public void insertProjectAdditionalContact(SQLiteDatabase db, String id, String project_id, String name, String company,
                                               String address, String phone, String email, String project_responsibility,
                                               String project_responsiblity_id, String is_sync, String contact_status,
                                               String created_date) {

        ContentValues cv = new ContentValues();
        cv.put(DBConstants.Contacts_details.id, id);
        cv.put(DBConstants.Contacts_details.project_id, project_id);
        cv.put(DBConstants.Contacts_details.name, name);
        cv.put(DBConstants.Contacts_details.company, company);
        cv.put(DBConstants.Contacts_details.address, address);
        cv.put(DBConstants.Contacts_details.phone, phone);
        cv.put(DBConstants.Contacts_details.email, email);
        cv.put(DBConstants.Contacts_details.project_responsiblity, project_responsibility);
        cv.put(DBConstants.Contacts_details.project_responsiblity_id, project_responsiblity_id);
        cv.put(DBConstants.Contacts_details.is_sync, is_sync);
        cv.put(DBConstants.Contacts_details.contact_status, contact_status);
        cv.put(DBConstants.Contacts_details.created_date, created_date);
        db.replace(DBConstants.TABLE.CONTACTS_DETAILS, null, cv);
    }

    public ArrayList<AdditionalContactModel> getAdditionalContactDetails() {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "select * from " + DBConstants.TABLE.CONTACTS_DETAILS +
                " where contact_status = 'A' OR contact_status = 'U'";

        Logger.e(TAG, "Project Additional Contact : " + selectQuery);
        Cursor cursor = db.rawQuery(selectQuery, null);
        ArrayList<AdditionalContactModel> additionalContactModelArrayList = new ArrayList<>();
        if (cursor != null)
            if (cursor.moveToFirst()) {
                do {
                    AdditionalContactModel contactModel = new AdditionalContactModel();
                    contactModel.setAdtnlContactID(cursor.getString(0));
                    contactModel.setProject_id(cursor.getString(1));
                    contactModel.setAdtnlContactName(cursor.getString(2));
                    contactModel.setAdtnlContactCompany(cursor.getString(3));
                    contactModel.setAdtnlContactAddress(cursor.getString(4));
                    contactModel.setAdtnlContactPhone(cursor.getString(5));
                    contactModel.setAdtnlContactEmail(cursor.getString(6));
                    contactModel.setAdtnlContactResponsibility(cursor.getString(7));
                    contactModel.setAdtnlContactResponsibilityId(cursor.getString(8));
                    contactModel.setIs_sync(cursor.getString(9));
                    contactModel.setContact_status(cursor.getString(10));
                    contactModel.setCreated_date(cursor.getString(11));
                    additionalContactModelArrayList.add(contactModel);
                } while (cursor.moveToNext());
            }
        cursor.close();
        return additionalContactModelArrayList;
    }

    public ArrayList<AdditionalContactModel> getSyncAdditionalContactDetails() {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "select * from " + DBConstants.TABLE.CONTACTS_DETAILS +
                " WHERE is_sync = 'N'";

        Logger.e(TAG, "Project Additional Contact : " + selectQuery);
        Cursor cursor = db.rawQuery(selectQuery, null);
        ArrayList<AdditionalContactModel> additionalContactModelArrayList = new ArrayList<>();
        if (cursor != null)
            if (cursor.moveToFirst()) {
                do {
                    AdditionalContactModel contactModel = new AdditionalContactModel();
                    contactModel.setAdtnlContactID(cursor.getString(0));
                    contactModel.setProject_id(cursor.getString(1));
                    contactModel.setAdtnlContactName(cursor.getString(2));
                    contactModel.setAdtnlContactCompany(cursor.getString(3));
                    contactModel.setAdtnlContactAddress(cursor.getString(4));
                    contactModel.setAdtnlContactPhone(cursor.getString(5));
                    contactModel.setAdtnlContactEmail(cursor.getString(6));
                    contactModel.setAdtnlContactResponsibility(cursor.getString(7));
                    contactModel.setAdtnlContactResponsibilityId(cursor.getString(8));
                    contactModel.setIs_sync(cursor.getString(9));
                    contactModel.setContact_status(cursor.getString(10));
                    contactModel.setCreated_date(cursor.getString(11));
                    additionalContactModelArrayList.add(contactModel);
                } while (cursor.moveToNext());
            }
        cursor.close();
        return additionalContactModelArrayList;
    }

    public int updateProjectAdditionalContact(String id, String project_id, String name, String company,
                                              String address, String phone, String email, String project_responsibility,
                                              String project_responsiblity_id, String is_sync, String contact_status,
                                              String created_date) {
        ContentValues cv = new ContentValues();
        cv.put(DBConstants.Contacts_details.id, id);
        cv.put(DBConstants.Contacts_details.project_id, project_id);
        cv.put(DBConstants.Contacts_details.name, name);
        cv.put(DBConstants.Contacts_details.company, company);
        cv.put(DBConstants.Contacts_details.address, address);
        cv.put(DBConstants.Contacts_details.phone, phone);
        cv.put(DBConstants.Contacts_details.email, email);
        cv.put(DBConstants.Contacts_details.project_responsiblity, project_responsibility);
        cv.put(DBConstants.Contacts_details.project_responsiblity_id, project_responsiblity_id);
        cv.put(DBConstants.Contacts_details.is_sync, is_sync);
        cv.put(DBConstants.Contacts_details.contact_status, contact_status);
        cv.put(DBConstants.Contacts_details.created_date, created_date);

        return myDataBase.update(DBConstants.TABLE.CONTACTS_DETAILS, cv, "id=?", new String[]{id});
    }

    public int deleteProjectAdditionalContact(String id) {
        return myDataBase.delete(DBConstants.TABLE.CONTACTS_DETAILS, "id=?", new String[]{id});
    }

    public boolean recordExistOrNot(SQLiteDatabase db, String TableName, String dbfield, String fieldValue) {
        String Query = "Select * from " + TableName + " where " + dbfield + " = " + "'" + fieldValue + "'";
        Cursor cursor = db.rawQuery(Query, null);
        if (cursor.getCount() <= 0) {
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
    }

}