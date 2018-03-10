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

public class DounloadImageRequestTask extends AsyncTask<String, Integer, Object> {

    public static final String TAG = "DounloadImageRequestTask";
    public Context mContext;
    private AsyncCallListener mAsyncCallListener;
    private String mErrorMessage;
    private boolean mIsError = false;

    public DounloadImageRequestTask(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Object doInBackground(String... params) {
        return storeImage(params[0], params[1], params[2]);
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

    private Object storeImage(String media, String media_type, String image_name) {
        int count;
        try {
            URL url = new URL(media);
            URLConnection conexion = url.openConnection();
            conexion.connect();
            int lenghtOfFile = conexion.getContentLength();
            String PATH = Environment.getExternalStorageDirectory() + "/Media/";
            File folder = new File(PATH);
            if (!folder.exists()) {
                folder.mkdirs();//If there is no folder it will be created.
            }

            File myFile;
            String PATH2 = "";
            if (media_type.equalsIgnoreCase("image")) {
                PATH2 = PATH + "/Image/";
                myFile = new File(PATH2, image_name);
            } else if (media_type.equalsIgnoreCase("audio")) {
                PATH2 = PATH + "/Audio/";
                myFile = new File(PATH2, image_name);
            } else if(media_type.equalsIgnoreCase("doc")){
                PATH2 = PATH + "/Doc/";
                myFile = new File(PATH2, image_name);
            } else if(media_type.equalsIgnoreCase("txt")){
                PATH2 = PATH + "/Txt/";
                myFile = new File(PATH2, image_name);
            } else if(media_type.equalsIgnoreCase("docx")){
                PATH2 = PATH + "/Docx/";
                myFile = new File(PATH2, image_name);
            } else if(media_type.equalsIgnoreCase("pdf")){
                PATH2 = PATH + "/PDF/";
                myFile = new File(PATH2, image_name);
            } else {
                PATH2 = PATH + "/Video/";
                myFile = new File(PATH2, image_name);
            }

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
