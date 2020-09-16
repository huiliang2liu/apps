package com.event.guava;

import android.util.Log;

import com.event.IEvent;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.SubscriberExceptionContext;
import com.google.common.eventbus.SubscriberExceptionHandler;

public class Event implements IEvent, SubscriberExceptionHandler {
    private static final String TAG = "guava.Event";
    EventBus eventBus = new EventBus(this);

    @Override
    public void register(Object o) {
        eventBus.register(o);
    }

    @Override
    public void unregister(Object o) {
        eventBus.unregister(o);
    }

    @Override
    public void post(Object... params) {
        if (params == null || params.length <= 0)
            return;
        eventBus.post(params[0]);
    }

    @Override
    public void handleException(Throwable exception, SubscriberExceptionContext context) {
        Log.e(TAG, "执行错误", exception);
    }
}
