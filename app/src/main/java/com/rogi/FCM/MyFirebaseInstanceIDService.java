package com.rogi.FCM;

import android.content.SharedPreferences;
import android.util.Log;

import com.rogi.View.Utils;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {
    private static final String TAG = MyFirebaseInstanceIDService.class.getSimpleName();
    private SharedPreferences mSharedPreferences;

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();

        mSharedPreferences = getSharedPreferences(Utils.PREF_NAME, MODE_PRIVATE);

        String refreshedToken = FirebaseInstanceId.getInstance().getToken();

        // Saving reg id to shared preferences
        Utils.storeString(mSharedPreferences, Utils.REG_ID, refreshedToken);

        // sending reg id to your server
        sendRegistrationToServer(refreshedToken);

        // Notify UI that registration has completed, so the progress indicator can be hidden.
       /* if (Utils.checkInternetConnection(this)) {
            try {
                Intent registrationComplete = new Intent(Utils.REGISTRATION_COMPLETE);
                registrationComplete.putExtra("token", refreshedToken);
                LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }*/
    }

    private void sendRegistrationToServer(final String token) {
        // sending gcm token to server
        Log.e(TAG, "sendRegistrationToServer: " + token);
    }

}

