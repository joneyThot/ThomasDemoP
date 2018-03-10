package com.rogi.Service;

import org.json.JSONObject;

public interface AsyncCallListener {

    public void onResponseReceived(Object response);

    public void onResponseReceived(JSONObject object);

    public void onErrorReceived(String error);
}
