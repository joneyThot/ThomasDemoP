package com.rogi.Service;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;

import com.rogi.logger.Logger;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

public class DounloadThumbImageRequestTask extends AsyncTask<String, Integer, Object> {

    public static final String TAG = "DounloadThumbImageRequestTask";
    public Context mContext;
    private AsyncCallListener mAsyncCallListener;
    private String mErrorMessage;
    private boolean mIsError = false;

    public DounloadThumbImageRequestTask(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Object doInBackground(String... params) {
        return storeThumbImage(params[0], params[1]);
    }

    @Override
    protected void onPostExecute(Object result) {
        super.onPostExecute(result);
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

    private Object storeThumbImage(String video_thumb, String image_name) {
        int count;
        try {
            URL url = new URL(video_thumb);
            URLConnection conexion = url.openConnection();
            conexion.connect();
            int lenghtOfFile = conexion.getContentLength();
            String PATH = Environment.getExternalStorageDirectory() + "/Media/";
            File folder = new File(PATH);
            if (!folder.exists()) {
                folder.mkdirs();//If there is no folder it will be created.
            }

            String PATH2 = PATH + "/VideoThumb/";
            File myFile = new File(PATH2, image_name);

            if (!myFile.exists()) {
                myFile.getParentFile().mkdirs();
            }

            InputStream input = new BufferedInputStream(url.openStream());
            OutputStream output = new FileOutputStream(myFile);
            byte data[] = new byte[1024];
            long total = 0;
            while ((count = input.read(data)) != -1) {
                total += count;
                publishProgress((int) (total * 100 / lenghtOfFile));
                output.write(data, 0, count);
            }
            output.flush();
            output.close();
            input.close();
        } catch (Exception e) {
            e.printStackTrace();
            Logger.e(TAG, "Dounload Failed Exception : " + image_name);
        }

        return null;
    }
}
