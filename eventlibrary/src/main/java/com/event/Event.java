package com.event;

import android.util.Log;

public class Event implements IEvent {
    private static final String TAG = "Event";
    private static Event event;
    private IEvent iEvent;

    private static Event getEvent() {
        if (event == null) {
            synchronized (TAG) {
                if (event == null)
                    event = new Event();
            }
        }
        return event;
    }

    private Event() {
        try {
            Class.forName("com.google.common.eventbus.EventBus");
            iEvent = new com.event.guava.Event();
            Log.d(TAG, "com.event.guava.Event");
        } catch (Exception e) {
            try {
                Class.forName("org.greenrobot.eventbus.EventBus");
                iEvent = new com.event.eventbus.Event();
                Log.d(TAG, "com.event.eventbus.Event");
            } catch (Exception ex) {
                iEvent = new com.event.xh.Event();
                Log.d(TAG, "com.event.xh.Event");
            }
        }
    }

    @Override
    public void register(Object o) {
        iEvent.register(o);
    }

    @Override
    public void unregister(Object o) {
        iEvent.unregister(o);
    }

    @Override
    public void post(Object... params) {
        iEvent.post(params);
    }
}
