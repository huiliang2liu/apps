package com.base.net;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkRequest;
import android.os.Build;

import java.util.ArrayList;
import java.util.List;

/**
 * com.net
 * 2018/9/28 9:59
 * instructions：
 * author:liuhuiliang  email:825378291@qq.com
 **/
public class NetState {
    private static NetState state;
    private BroadcastReceiver receiver;
    private ConnectivityManager.NetworkCallback callback;
    private Context context;
    private List<NetCallback> callbacks;

    public static NetState getNetState(Context context) {
        if (state == null) {
            synchronized (NetState.class) {
                if (state == null)
                    state = new NetState(context);
            }
        }
        return state;
    }

    private NetState(Context context) {
        if (context == null)
            throw new NullPointerException("context is null");
        this.context = context.getApplicationContext();
        if (Build.VERSION.SDK_INT >= 21) {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            callback = new ConnectivityManager.NetworkCallback() {
                @Override
                public void onAvailable(Network network) {
                    super.onAvailable(network);
                    NetState.this.onAvailable();
                }

                @Override
                public void onLost(Network network) {
                    super.onLost(network);
                    NetState.this.onLost();
                }
            };
            connectivityManager.requestNetwork(new NetworkRequest.Builder().build(), callback);
        } else {
            IntentFilter filter = new IntentFilter();
            filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
            receiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    if (!NetworkUtil.isNetConnected(context)) {
                        onLost();
                    } else {
                        onAvailable();
                    }
                }
            };
            this.context.registerReceiver(receiver, filter);
        }
    }

    public void registerCallback(NetCallback callback) {
        if (callback == null)
            return;
        if (callbacks == null)
            callbacks = new ArrayList<>();
        callbacks.add(callback);
    }

    public void unRegisterCallback(NetCallback callback) {
        if (callback == null || callbacks == null || callbacks.size() <= 0)
            return;
        callbacks.remove(callback);
    }

    public void destroy() {
        if (Build.VERSION.SDK_INT >= 21) {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            connectivityManager.unregisterNetworkCallback(callback);
        } else {
            context.unregisterReceiver(receiver);
        }
        if (callbacks != null && callbacks.size() > 0)
            callbacks.clear();
        state = null;
    }

    private void onAvailable() {
        if (callbacks != null && callbacks.size() > 0)
            for (NetCallback callback : callbacks)
                callback.onAvailable();
    }

    private void onLost() {
        if (callbacks != null && callbacks.size() > 0)
            for (NetCallback callback : callbacks)
                callback.onLost();
    }
}
