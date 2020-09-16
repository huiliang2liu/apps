package com.event.xh;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.event.IEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import androidx.annotation.NonNull;

public class Event implements IEvent {
    private static final String TAG = "xh.Event";
    private List<SubscriberO> subscribers = new Vector<>();
    private Handler handler = new Handler(Looper.myLooper());

    public enum ThreadMode {
        MAIN("main"), CURRENT("current"), ASYNC("async");
        private String name;

        ThreadMode(String name) {
            this.name = name;
        }

        @NonNull
        @Override
        public String toString() {
            return name;
        }
    }

    public void register(Object o) {
        if (o == null) {
            Log.d(TAG, "观察者为空");
            return;
        }
        if (subscribers.indexOf(o) >= 0) {
            Log.d(TAG, "该观察者已经被注册");
        } else {
            subscribers.add(new SubscriberO(o, handler));
        }
    }

    public void unregister(Object o) {
        subscribers.remove(o);
    }

    public void post(Object... params) {
        List<SubscriberO> os = new ArrayList<>();
        for (SubscriberO subscriberO : subscribers) {
            if (!subscriberO.invoke(params))
                os.add(subscriberO);
        }
        subscribers.removeAll(os);
    }
}
