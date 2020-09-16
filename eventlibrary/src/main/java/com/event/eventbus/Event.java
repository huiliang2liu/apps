package com.event.eventbus;

import com.event.IEvent;

import org.greenrobot.eventbus.EventBus;

public class Event implements IEvent {
    private EventBus eventBus = EventBus.getDefault();

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
}
