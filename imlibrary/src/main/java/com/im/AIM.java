package com.im;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public abstract class AIM<T> implements IIM<T>, IIM.Callback {
    private static final String TAG = "AIM";
    private static final long RECONNECT_TIME = 1 * 1000;
    protected String name, pass;
    protected Context context;
    protected List<MessageListener<T>> messageListeners = new ArrayList<>();
    protected Map<String, MessageListener<T>> messageListenerMap = new HashMap<>();
    protected Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Log.e(TAG, "重新登陆");
            if (name == null || name.isEmpty() || pass == null || pass.isEmpty()) {
                Log.e(TAG, "重新登陆，密码或用户为空");
                return;
            }
            if (!isLogin()) {
                if (hasNetwork()) {
                    Log.e(TAG, "重新登陆。。。。。。。。。。。。。。");
                    login(name, pass);
                } else {
                    Log.e(TAG, "重新登陆没有网络");
                }
                handler.sendEmptyMessageDelayed(0, RECONNECT_TIME);
            } else {
                Log.e(TAG, "已经登陆了");
            }
        }
    };

    @Override
    public final void registerMessageListener(MessageListener<T> messageListener) {
        if (messageListener != null)
            messageListeners.add(messageListener);
    }

    @Override
    public final void registerMessageListener(String name, MessageListener<T> messageListener) {
        if (name != null && !name.isEmpty() && messageListener != null)
            messageListenerMap.put(name, messageListener);
    }

    @Override
    public final void unRegisterMessageListener(MessageListener<T> messageListener) {
        if (messageListener != null) {
            messageListeners.remove(messageListener);
            String name = null;
            for (Map.Entry<String, MessageListener<T>> entry : messageListenerMap.entrySet()) {
                if (entry.getValue().equals(messageListener)) {
                    name = entry.getKey();
                    break;
                }
            }
            if (name != null && !name.isEmpty()) {
                messageListenerMap.remove(name);
            }
        }
    }

    public AIM(Context context) {
        this.context = context;
    }

    @Override
    public final void sendFile(String filePath, boolean group,String name) {
        sendFile(filePath, group, name,this);
    }

    @Override
    public final void sendLocation(double latitude, double longitude, String locationAddress, boolean group,String name) {
        sendLocation(latitude, longitude, locationAddress, group,name, this);
    }

    @Override
    public final void sendImage(String imagePath, boolean group,String name) {
        sendImage(imagePath, group, name,this);
    }

    @Override
    public final void sendVideo(String videoPath, String thumPath, int length, String name, boolean group) {
        sendVideo(videoPath, thumPath, length, name, group, this);
    }

    @Override
    public final void sendVoice(String voicPath, int length, String name, boolean group) {
        sendVoice(voicPath, length, name, group, this);
    }

    @Override
    public final void sendText(String context, String name, boolean group) {
        sendText(context, name, group, this);
    }

    @Override
    public final void sendMesage(T o, String name, boolean group) {
        sendMesage(o, name, group, this);
    }

    @Override
    public final void sendCmd(String name, String action, Map<String, Object> params) {
        sendCmd(name, action, params, this);
    }

    @Override
    public void onSuccess() {
        Log.e(TAG, "onSuccess");
    }

    @Override
    public void onError(int i, String msg) {
        Log.e(TAG, String.format("onError,%d,%s", i, msg));
    }

    @Override
    public void onProgress(int i, String msg) {
        Log.e(TAG, String.format("onProgress,%d,%s", i, msg));
    }

    @Override
    public void logout() {
        this.pass = null;
        this.name = null;
    }

    @Override
    public void login(String name, String pass) {
        this.name = name;
        this.pass = pass;
    }

    protected boolean hasNetwork() {
        if (Build.VERSION.SDK_INT >= 23 && context.checkSelfPermission(Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED)
            return false;
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager
                .getActiveNetworkInfo();
        return (networkInfo.isConnected() && networkInfo.getState() == NetworkInfo.State.CONNECTED);
    }
}
