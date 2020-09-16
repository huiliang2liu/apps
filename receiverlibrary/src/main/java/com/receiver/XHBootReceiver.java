package com.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class XHBootReceiver extends BroadcastReceiver {
    private final static String TAG = "XHBootReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "接收到开机广播");
    }
}
