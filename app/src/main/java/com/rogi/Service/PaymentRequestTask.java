package com.rogi.Service;

import android.content.Context;
import android.os.AsyncTask;

import com.rogi.View.Utils;
import com.rogi.logger.Logger;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;

import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;

public class PaymentRequestTask extends AsyncTask<String, Integer, JSONObject> {

    public static final String TAG = "UpdateImageRequestTask";
    public Context mContext;
//    private ProgressDialog mProgressDialog;
    private AsyncCallListener mAsyncCallListener;
    private String mErrorMessage;
    private boolean mIsError = false;

    public PaymentRequestTask(Context mContext) {
        this.mContext = mContext;
//        mProgressDialog = new ProgressDialog(mContext);
//        if (!mProgressDialog.isShowing()) {
//            mProgressDialog.setMessage(mContext.getResources().getString(R.string.please_wait));
//            mProgressDialog.setCanceledOnTouchOutside(false);
//            mProgressDialog.setCancelable(false);
//        }
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
//        if (mProgressDialog != null && !mProgressDialog.isShowing()) {
//            mProgressDialog.show();
//        }
    }

    @Override
    protected JSONObject doInBackground(String... params) {
        return paymentRequest(params[0]);
    }

    @Override
    protected void onPostExecute(JSONObject result) {
        super.onPostExecute(result);
//        if (mProgressDialog != null && mProgressDialog.isShowing()) {
//            mProgressDialog.dismiss();
//        }
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

    private JSONObject paymentRequest(String str) {
        String result = "";
        String getServerPath = Utils.AUTHORIZED_PAYMENT_API;
        JSONObject jsonObj = null;
        try {
//            result = Utils.postRequest(getServerPath, str);

            try {
                URL url = new URL(getServerPath);
                URLConnection con = url.openConnection();
                // specify that we will send output and accept input
                con.setDoInput(true);
                con.setDoOutput(true);
                con.setConnectTimeout(200000);  // long timeout, but not infinite
                con.setReadTimeout(200000);
                con.setUseCaches(false);
                con.setDefaultUseCaches(false);
                // tell the web server what we are sending
                con.setRequestProperty("Content-Type", "text/xml");
                OutputStreamWriter writer = new OutputStreamWriter(con.getOutputStream());
                writer.write(str);
                writer.flush();
                writer.close();
                // reading the response
                InputStreamReader reader = new InputStreamReader(con.getInputStream());
                StringBuilder buf = new StringBuilder();
                char[] cbuf = new char[2048];
                int num;
                while (-1 != (num = reader.read(cbuf))) {
                    buf.append(cbuf, 0, num);
                }
                result = buf.toString();
                System.err.println("\nResponse from server after POST:\n" + result);
            } catch (Throwable t) {
                t.printStackTrace(System.out);
            }

            try {
                jsonObj = XML.toJSONObject(result.toString());
            } catch (JSONException e) {
                Logger.e(TAG, "JSON exception" + e.getMessage());
                e.printStackTrace();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObj;
    }
}
