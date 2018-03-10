package com.rogi.Service;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import com.rogi.Model.ExistingUserModel;
import com.rogi.View.Utils;
import com.rogi.logger.Logger;

import org.json.JSONObject;

public class GetDominNameRequestTask extends AsyncTask<String, Integer, Object> {

    public static final String TAG = "GetDominNameRequestTask";
    public Context mContext;
    //	private ProgressDialog mProgressDialog;
    private AsyncCallListener mAsyncCallListener;
    private String mErrorMessage;
    private SharedPreferences mSharedPreferences;
    private boolean mIsError = false;

    public GetDominNameRequestTask(Context mContext) {
        this.mContext = mContext;
//		mProgressDialog = new ProgressDialog(mContext);
//		mProgressDialog.setMessage(mContext.getResources().getString(
//				R.string.loading));
//		mProgressDialog.setCanceledOnTouchOutside(false);
//		mProgressDialog.setCancelable(false);
        mSharedPreferences = mContext.getSharedPreferences(Utils.PREF_NAME, mContext.MODE_PRIVATE);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
//		if (mProgressDialog != null && !mProgressDialog.isShowing()) {
//			mProgressDialog.show();
//		}
    }

    @Override
    protected Object doInBackground(String... params) {
        return getDomin();
    }

    @Override
    protected void onPostExecute(Object result) {
        super.onPostExecute(result);
//		if (mProgressDialog != null && mProgressDialog.isShowing()) {
//			mProgressDialog.dismiss();
//		}
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

    private Object getDomin() {

        ExistingUserModel dominModel = new ExistingUserModel();
        String result = "";

        String getServerPath = Utils.MAIN_URL + Utils.DOMIN_URL;
        try {

            result = Utils.postRequest(getServerPath);
            JSONObject jObj = new JSONObject(result.toString());

            dominModel.setSuccess(jObj.getString("success"));
            dominModel.setAuthenticated(jObj.getString("authenticated"));
            dominModel.setMessage(jObj.getString("message"));
            if (!jObj.isNull("data")) {
                JSONObject dataObj = jObj.getJSONObject("data");
                dominModel.setUrl(dataObj.getString("url"));
                Utils.storeString(mSharedPreferences, Utils.URL_API_ADDRESS, dataObj.getString("url"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return dominModel;
    }
}
