/*
package com.rogiproject.Crop;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;

*/
/**
 * Created by adminz on 6/4/17.
 *//*

public class CounterMain extends Activity {
//    TextView textGoesHere;
//    long startTime;
//    long countUp;

    TextView textView;

    Button start, pause, reset, lap;
    int Seconds, Minutes, Hours, Day, MilliSeconds;
    long MillisecondTime, StartTime, TimeBuff, UpdateTime = 0L;
    Handler handler;
    EditText edtText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.counter);

        textView = (TextView) findViewById(R.id.textView);
        start = (Button) findViewById(R.id.button);
        reset = (Button) findViewById(R.id.button3);
        edtText = (EditText) findViewById(R.id.edtText);
        handler = new Handler();

        StartTime = SystemClock.uptimeMillis();
        TimeBuff = 2356;
        handler.postDelayed(runnable, 0);

       */
/* start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                StartTime = SystemClock.uptimeMillis();
                TimeBuff = 452638;
                handler.postDelayed(runnable, 0);

            }
        });*//*


        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                MillisecondTime = 0L;
                StartTime = 0L;
                TimeBuff = 0L;
                UpdateTime = 0L;
                Day = 0;
                Seconds = 0;
                Minutes = 0;
                MilliSeconds = 0;

                textView.setText("00:00:00:00");
                handler.removeCallbacksAndMessages(null);
            }
        });

        edtText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0)
                    Log.e("TEXT", s.toString());
            }
        });


       */
/* final Chronometer stopWatch = (Chronometer) findViewById(R.id.chrono);
        startTime = SystemClock.elapsedRealtime();

        textGoesHere = (TextView) findViewById(R.id.textGoesHere);
        stopWatch.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer arg0) {
//                countUp = (SystemClock.elapsedRealtime() - arg0.getBase()) / 1000;
                countUp = (SystemClock.elapsedRealtime() - arg0.getBase()) / 1000;
                String asText = (countUp / 3600) + ":" + ((countUp % 3600) / 60) + ":" + (countUp % 60);

               *//*
*/
/* string astext = ((countup / (1000*60*60*24)) % 365) + ":"
                        + ((countup / (1000*60*60)) % 24) + ":"
                        + ((countup / (1000*60) % 60)) + ":"
                        + ((countup / 1000) % 60);*//*
*/
/*
                textGoesHere.setText("" + asText);
            }
        });
        stopWatch.start();

        textGoesHere.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopWatch.stop();
            }
        });*//*



    }

    public Runnable runnable = new Runnable() {

        public void run() {

            MillisecondTime = SystemClock.uptimeMillis() - StartTime;
            UpdateTime = (TimeBuff * 1000) + MillisecondTime;
            Seconds = (int) (UpdateTime / 1000);

//            Hours = (Seconds / 3600);
//            Minutes = (Seconds / 60) % 60;
//            Seconds = Seconds % 60;
//            MilliSeconds = (int) (UpdateTime % 1000);

            Day = (int) TimeUnit.SECONDS.toDays(Seconds);
            Hours = (int) TimeUnit.SECONDS.toHours(Seconds) - (Day * 24);
            Minutes = (int) (TimeUnit.SECONDS.toMinutes(Seconds) - (TimeUnit.SECONDS.toHours(Seconds) * 60));
            Seconds = (int) (TimeUnit.SECONDS.toSeconds(Seconds) - (TimeUnit.SECONDS.toMinutes(Seconds) * 60));

            String time = String.format("%02d", Day) + "d " + String.format("%02d", Hours) + "h " + String.format("%02d", Minutes) + "m " + String.format("%02d", Seconds) + "s";
            textView.setText(time);
            handler.postDelayed(this, 0);
        }

    };

}*/
