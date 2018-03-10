package com.rogi.Service;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.rogi.R;
import com.rogi.View.Utils;
import com.rogi.logger.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class GeneratReportRequestTask extends AsyncTask<String, Integer, JSONObject> {

    public static final String TAG = "GeneratReportRequestTask";
    public Context mContext;
    private ProgressDialog mProgressDialog;
    private AsyncCallListener mAsyncCallListener;
    private String mErrorMessage;
    private boolean mIsError = false;

    public GeneratReportRequestTask(Context mContext) {
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
        return generateReport(params[0], params[1], params[2], params[3]);
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

    private JSONObject generateReport(String userId, String token, String projectId, String mediaIdsArray) {
        JSONObject jObj = new JSONObject();
        String result = "";
        String getServerPath = Utils.MAIN_URL + Utils.GENERATE_REPORT_API;
        try {

            JSONObject jsonObject = new JSONObject();
            jsonObject.accumulate("user_id", userId);
            jsonObject.accumulate("session_token", token);
            jsonObject.accumulate("project_id", projectId);
            if (Utils.validateString(mediaIdsArray) && !mediaIdsArray.equalsIgnoreCase("[]")) {
                JSONArray array = new JSONArray(mediaIdsArray);
                jsonObject.accumulate("media_ids", array);
            } else {
                jsonObject.accumulate("media_ids", "");
            }

            result = Utils.POST(getServerPath, jsonObject);
            jObj = new JSONObject(result.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jObj;
    }
}
