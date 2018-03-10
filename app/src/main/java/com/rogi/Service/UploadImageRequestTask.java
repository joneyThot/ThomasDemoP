package com.rogi.Service;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.rogi.R;
import com.rogi.View.Upload_Image;
import com.rogi.logger.Logger;

import org.json.JSONException;
import org.json.JSONObject;

public class UploadImageRequestTask extends AsyncTask<String, Integer, JSONObject> {

    public static final String TAG = "UploadImageRequestTask";
    public Context mContext;
    private ProgressDialog mProgressDialog;
    private AsyncCallListener mAsyncCallListener;
    private String mErrorMessage;
    private boolean mIsError = false;

    public UploadImageRequestTask(Context mContext) {
        this.mContext = mContext;
        mProgressDialog = new ProgressDialog(mContext);
        if (!mProgressDialog.isShowing()) {
            mProgressDialog.setMessage(mContext.getResources().getString(R.string.please_wait));
            mProgressDialog.setCanceledOnTouchOutside(false);
            mProgressDialog.setCancelable(false);
        }
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (mProgressDialog != null && !mProgressDialog.isShowing()) {
            mProgressDialog.show();
        }
    }

    @Override
    protected JSONObject doInBackground(String... params) {
        return uploadImage(params[0], params[1], params[2], params[3], params[4], params[5], params[6], params[7],
                params[8], params[9], params[10], params[11], params[12], params[13],params[14]);
    }

    @Override
    protected void onPostExecute(JSONObject result) {
        super.onPostExecute(result);
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
        if (mAsyncCallListener != null) {
            if (mIsError) {
                Logger.e("", "In Asnyc call->errorMessage:" + mErrorMessage);
                mAsyncCallListener.onErrorReceived(mErrorMessage);
            } else {
                mAsyncCallListener.onResponseReceived(result);
            }
        }
    }

    public void setAsyncCallListener(AsyncCallListener listener) {
        this.mAsyncCallListener = listener;
    }

    private JSONObject uploadImage(String user_id, String session_token, String project_id, String media_type, String selectedImagePath,
                                   String lat, String lang, String street, String city, String state, String country, String pincode, String created_date,
                                   String media_temp_id, String media_description) {
        JSONObject jObj = null;
        try {
            Upload_Image uploadimage = new Upload_Image(mContext);
            String result = uploadimage.uploadMedia(user_id, session_token, project_id, media_type, selectedImagePath, lat,
                    lang, street, city, state, country, pincode, created_date, media_temp_id, media_description);

            jObj = new JSONObject(result);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jObj;
    }
}
