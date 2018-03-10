package com.rogi.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.OpenableColumns;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.URLUtil;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.rogi.R;
import com.rogi.View.Utils;
import com.rogi.logger.Logger;
import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;
import com.shockwave.pdfium.PdfDocument;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.List;


public class GenerateReportActivity extends AppCompatActivity implements View.OnClickListener, OnPageChangeListener, OnLoadCompleteListener {
    public static final String TAG = "GenerateReportActivity";
    private final static int REQUEST_CODE = 42;
    public static final int PERMISSION_CODE = 42042;

    public static final String SAMPLE_FILE = "sample.pdf";
    public static final String READ_EXTERNAL_STORAGE = "android.permission.READ_EXTERNAL_STORAGE";

    TextView titleText;
    WebView webView;
    PDFView pdfView;
    ImageView shareProfileView;
    String myPdfUrl = "", myPdfName = "";
    String pdfFileName, filePath = "";
    Uri uri;
    Integer pageNumber = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_generate_report_view);

        int currentapiVersion = Build.VERSION.SDK_INT;
        if (currentapiVersion > Build.VERSION_CODES.LOLLIPOP) {
            // Do something for lollipop and above versions
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if ((this.checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
                    requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 101);
                }
            }
        }
        init();
    }

    public void init() {
        titleText = (TextView) findViewById(R.id.titleText);
        titleText.setText("Project Report");

        webView = (WebView) findViewById(R.id.webView);
        pdfView = (PDFView) findViewById(R.id.pdfView);
        webView.setVisibility(View.VISIBLE);
        pdfView.setVisibility(View.GONE);
        shareProfileView = (ImageView) findViewById(R.id.shareProfileView);
        shareProfileView.setVisibility(View.VISIBLE);
        findViewById(R.id.backLayoutclick).setOnClickListener(this);
        shareProfileView.setOnClickListener(this);

        Intent intent = getIntent();
        myPdfUrl = intent.getStringExtra(Utils.PDF_REPORT_URL);
        myPdfName = intent.getStringExtra(Utils.PDF_FILE_NAME);
        Logger.e(TAG, "URL ::" + myPdfUrl);

        File file = new File(myPdfUrl.toString());
        if (file.exists()) {
            webView.setVisibility(View.GONE);
            pdfView.setVisibility(View.VISIBLE);
            pdfView.setBackgroundColor(Color.LTGRAY);
            uri = Uri.fromFile(file);
            if (uri != null) {
                displayFromUri(uri);
            }
        } else {
            //        String url1 = "https://docs.google.com/viewer?url=" + myPdfUrl;
//        String url2 = "http://drive.google.com/viewerng/viewer?embedded=true&url=" + myPdfUrl;
        /*String url = "http://docs.google.com/gview?embedded=true&url=" + myPdfUrl;
        Log.i("Generate Report", "Opening PDF: " + url);

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setPluginState(WebSettings.PluginState.ON);
        webView.setWebViewClient(new Callback());
        webView.loadUrl(url);*/

            webView.getSettings().setSupportMultipleWindows(true);
            webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
            webView.getSettings().setAllowFileAccess(true);
            webView.getSettings().setJavaScriptEnabled(true);
            webView.getSettings().setBuiltInZoomControls(true);
            webView.getSettings().setDisplayZoomControls(false);
            webView.getSettings().setLoadWithOverviewMode(true);
            webView.getSettings().setUseWideViewPort(true);
            webView.setDownloadListener(new DownloadListener() {

                @Override
                public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimeType, long contentLength) {
                    String fileName;
                    try {
                        fileName = URLUtil.guessFileName(url, contentDisposition, mimeType);
                        downloadFileAsync(url, fileName);
                    } catch (Exception e) {

                    }
                }
            });
            webView.loadUrl(myPdfUrl);
        }

    }

    private class Callback extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(
                WebView view, String url) {
            return (false);
        }
    }

    private void downloadFileAsync(String url, String filename) {

        new AsyncTask<String, Void, String>() {
            String SDCard;
            ProgressDialog pdialog;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                pdialog = new ProgressDialog(GenerateReportActivity.this);
                pdialog.setMessage(getString(R.string.please_wait));
                pdialog.setCanceledOnTouchOutside(false);
                pdialog.show();
            }

            @Override
            protected String doInBackground(String... params) {
                try {
                    URL url = new URL(params[0]);
                    HttpURLConnection urlConnection = null;
                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("GET");
                    urlConnection.setDoOutput(true);
                    urlConnection.connect();
                    int lengthOfFile = urlConnection.getContentLength();

                    String PATH = Environment.getExternalStorageDirectory() + "/Media/";
                    File folder = new File(PATH);
                    if (!folder.exists()) {
                        folder.mkdirs();
                    }
                    String PATH2 = PATH + "/PDF/";
                    File file = new File(PATH2, myPdfName);
                    if (!file.exists()) {
                        file.getParentFile().mkdirs();
                    }

                    FileOutputStream fileOutput = null;
                    fileOutput = new FileOutputStream(file, true);
                    InputStream inputStream = null;
                    inputStream = urlConnection.getInputStream();
                    byte[] buffer = new byte[1024];
                    int count;
                    long total = 0;
                    while ((count = inputStream.read(buffer)) != -1) {
                        total += count;
                        fileOutput.write(buffer, 0, count);
                    }
                    fileOutput.flush();
                    fileOutput.close();
                    inputStream.close();
                } catch (MalformedURLException e) {
                } catch (ProtocolException e) {
                } catch (FileNotFoundException e) {
                } catch (IOException e) {
                } catch (Exception e) {
                }
                return params[1];
            }

            @Override
            protected void onPostExecute(final String result) {
                if (pdialog != null && pdialog.isShowing()) {
                    pdialog.dismiss();
                }
                webView.setVisibility(View.GONE);
                pdfView.setVisibility(View.VISIBLE);
                pdfView.setBackgroundColor(Color.LTGRAY);
                filePath = Environment.getExternalStorageDirectory() + "/Media/PDF/" + myPdfName;
                File file = new File(filePath);
                uri = Uri.fromFile(file);
                if (uri != null) {
                    displayFromUri(uri);
                }
            }

        }.execute(url, filename);
    }

    private void displayFromUri(Uri uri) {
        pdfFileName = getFileName(uri);

        pdfView.fromUri(uri)
                .defaultPage(pageNumber)
                .onPageChange(this)
                .enableAnnotationRendering(true)
                .onLoad(this)
                .scrollHandle(new DefaultScrollHandle(this))
                .spacing(10) // in dp
                .load();
    }

    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }
        if (result == null) {
            result = uri.getLastPathSegment();
        }
        return result;
    }

    @Override
    public void loadComplete(int nbPages) {
        PdfDocument.Meta meta = pdfView.getDocumentMeta();
        Logger.e(TAG, "title = " + meta.getTitle());
        Logger.e(TAG, "author = " + meta.getAuthor());
        Logger.e(TAG, "subject = " + meta.getSubject());
        Logger.e(TAG, "keywords = " + meta.getKeywords());
        Logger.e(TAG, "creator = " + meta.getCreator());
        Logger.e(TAG, "producer = " + meta.getProducer());
        Logger.e(TAG, "creationDate = " + meta.getCreationDate());
        Logger.e(TAG, "modDate = " + meta.getModDate());

        printBookmarksTree(pdfView.getTableOfContents(), "-");
    }

    @Override
    public void onPageChanged(int page, int pageCount) {
        pageNumber = page;
    }

    public void printBookmarksTree(List<PdfDocument.Bookmark> tree, String sep) {
        for (PdfDocument.Bookmark b : tree) {

            Logger.e(TAG, String.format("%s %s, p %d", sep, b.getTitle(), b.getPageIdx()));

            if (b.hasChildren()) {
                printBookmarksTree(b.getChildren(), sep + "-");
            }
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.backLayoutclick:
                finish();
                break;

            case R.id.shareProfileView:

                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, "Project Report PDF: " + myPdfUrl);
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
                break;
        }
    }
}