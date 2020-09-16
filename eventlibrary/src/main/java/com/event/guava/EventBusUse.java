package com.event.guava;


import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

public class EventBusUse {
    EventBus eventBus;

    {
        eventBus = new EventBus();
        eventBus.register(this);
    }

    public void sendEvent(Object o){
        eventBus.post(o);
    }


    @Subscribe
    public void background(Object o) {
        //表示事件处理函数的线程在后台线程，因此不能进行UI操作。如果发布事件的线程是主线程(UI线程)，那么事件处理函数将会开启一个后台线程，如果果发布事件的线程是在后台线程，那么事件处理函数就使用该线程
    }


    public void destory() {
        eventBus.unregister(this);
    }
}
