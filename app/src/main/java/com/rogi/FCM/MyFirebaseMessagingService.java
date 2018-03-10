package com.rogi.FCM;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.os.Build;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.rogi.Activity.NavigationDrawerActivity;
import com.rogi.Activity.SplashActivity;
import com.rogi.R;
import com.rogi.View.Utils;
import com.rogi.logger.Logger;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;


public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = MyFirebaseMessagingService.class.getSimpleName();
    private Intent notificationIntent;
    private SharedPreferences mSharedPreferences;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.e(TAG, "From: " + remoteMessage.getFrom());
        mSharedPreferences = getSharedPreferences(Utils.PREF_NAME, MODE_PRIVATE);

        if (remoteMessage == null)
            return;

        String message = remoteMessage.getData().toString();
        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Logger.e(TAG, "Notification Body: " + remoteMessage.getNotification().getBody());
            if (!Utils.isAppIsInBackground(getApplicationContext())) {
                // app is in foreground, broadcast the push message
                Intent pushNotification = new Intent(Utils.PUSH_NOTIFICATION);
                pushNotification.putExtra("message", message);
                LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);

            } else {
                // If the app is in background, firebase itself handles the notification
            }
        }

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Logger.e(TAG, "Data Payload: " + remoteMessage.getData().toString());

            try {
                JSONObject json = new JSONObject(remoteMessage.getData().toString());
                handleDataMessage(getApplicationContext(), json);
            } catch (Exception e) {
                Log.e(TAG, "Exception: " + e.getMessage());
            }

        }


    }

    @Override
    public void onDeletedMessages() {
        super.onDeletedMessages();
        Logger.i(TAG, "Received deleted messages notification");
    }

    @Override
    public void onMessageSent(String s) {
        super.onMessageSent(s);
    }

    @Override
    public void onSendError(String s, Exception e) {
        super.onSendError(s, e);
        Logger.i(TAG, "Received recoverable error: " + s);
    }

    private void handleDataMessage(Context context, JSONObject json) {
        int icon = R.mipmap.logo_app_rogi;
        long when = System.currentTimeMillis();
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        String msg = "";
        JSONObject jObj = null;
        try {
            jObj = json.getJSONObject("message");
            msg = jObj.getString("message");
            if (!jObj.isNull("data")) {
                JSONObject data_jobj = jObj.getJSONObject("data");
            }
        } catch (JSONException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        Notification notification = new Notification(icon, msg, when);
        String title = context.getString(R.string.app_name);

        try {
            mSharedPreferences = getSharedPreferences(Utils.PREF_NAME, MODE_PRIVATE);
            if ((!mSharedPreferences.getString(Utils.EMAIL, "").isEmpty()) && (!mSharedPreferences.getString(Utils.PASSWORD, "").isEmpty())
                    && (!mSharedPreferences.getString(Utils.TOKEN, "").isEmpty())) {
                notificationIntent = new Intent(context, NavigationDrawerActivity.class);
            } else {
                notificationIntent = new Intent(context, SplashActivity.class);
            }
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            notificationIntent.putExtra("FROM", "MyFirebaseMessagingService");
            notificationIntent.putExtra("response", jObj.toString());


            PendingIntent intent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);

            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mBuilder.setSmallIcon(R.mipmap.noti_icon);
                mBuilder.setColor(Color.parseColor("#7a1417"));
            } else {
                mBuilder.setSmallIcon(icon);
            }

            mBuilder.setTicker(title).setWhen(when);
            mBuilder.setAutoCancel(true);
            mBuilder.setContentTitle(title);
            mBuilder.setContentIntent(intent);
            mBuilder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
            mBuilder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), icon));
            mBuilder.setContentText(msg);
            mBuilder.setPriority(Notification.PRIORITY_MAX);
            mBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText(msg));

            Notification noti = mBuilder.build();
            notificationManager.notify(0, noti);

        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }
}
