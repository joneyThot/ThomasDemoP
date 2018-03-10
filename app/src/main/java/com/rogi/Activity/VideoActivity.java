package com.rogi.Activity;

import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.rogi.R;
import com.rogi.View.Utils;


public class VideoActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String TAG = "VideoActivity";
    private SharedPreferences mSharedPreferences;

    TextView titleText;
    VideoView videoView;

    String TOKEN = "", USERID = "", media_url = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_video);
        mSharedPreferences = getSharedPreferences(Utils.PREF_NAME, MODE_PRIVATE);
        USERID = mSharedPreferences.getString(Utils.USER_ID, "");
        TOKEN = mSharedPreferences.getString(Utils.TOKEN, "");
        init();
    }

    public void init() {
        titleText = (TextView) findViewById(R.id.titleText);
        titleText.setText("Media Details");

        videoView = (VideoView) findViewById(R.id.webViewVideo);

        findViewById(R.id.backLayoutclick).setOnClickListener(this);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            media_url = bundle.getString(Utils.MEDIA_IMAGE_URL);
        }

        try {
            MediaController mediaController = new MediaController(this);
            mediaController.setAnchorView(videoView);
            Uri video = Uri.parse(media_url);
            videoView.setMediaController(mediaController);
            videoView.setVideoURI(video);
            videoView.start();
        } catch (Exception e) {
            // TODO: handle exception
            Toast.makeText(this, "Error connecting", Toast.LENGTH_SHORT).show();
        }

        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mp) {
                // TODO Auto-generated method stub
                finish();
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.backLayoutclick:
                if (videoView.isPlaying()) {
                    videoView.pause();
                }
                finish();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (videoView.isPlaying()) {
            videoView.pause();
        }
        super.onBackPressed();
    }
}
