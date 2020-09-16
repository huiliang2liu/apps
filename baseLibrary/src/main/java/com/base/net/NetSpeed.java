package com.base.net;

import android.content.Context;
import android.net.TrafficStats;

import com.base.thread.Handler;

public class NetSpeed implements Runnable {
    private Context context;
    private NetSpeedListener speedListener;
    private boolean stop = false;
    private long lastSpeed;
    private long lastTime;
    private Handler handler = new Handler();

    public NetSpeed(Context context) {
        this.context = context;
    }

    @Override
    public synchronized void run() {
        while (!stop) {
            long nowSpeed = getTotalRxBytes();
            long nowTime = System.currentTimeMillis();
            final double speed = ((nowSpeed - lastSpeed) * 1000.0d) / (nowTime - lastTime);
            lastSpeed = nowSpeed;
            lastTime = nowTime;
            handler.post(new Runnable() {
                @Override
                public void run() {
                    if (speedListener != null)
                        speedListener.onNetSpeed(speed);
                }
            });
            try {
                Thread.sleep(800);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void start(NetSpeedListener speedListener) {
        this.speedListener = speedListener;
        new Thread(this).start();
        lastSpeed = getTotalRxBytes();
        lastTime = System.currentTimeMillis();
    }

    private long getTotalRxBytes() {
        return TrafficStats.getUidRxBytes(context.getApplicationInfo().uid) == TrafficStats.UNSUPPORTED ? 0 : (TrafficStats.getTotalRxBytes() / 1024);//转为KB
    }

    public void stop() {
        stop = true;
        speedListener = null;
    }

    public interface NetSpeedListener {
        void onNetSpeed(double speed);
    }
}
