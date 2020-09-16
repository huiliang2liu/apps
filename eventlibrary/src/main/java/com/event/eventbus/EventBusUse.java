package com.event.eventbus;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class EventBusUse {
    {
        EventBus.getDefault().register(this);
    }

    public void sendEvent(Object o) {
        EventBus.getDefault().post(o);
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void posting(Object o) {
        //默认，表示事件处理函数的线程跟发布事件的线程在同一个线程。
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void main(Object o) {
        //表示事件处理函数的线程在主线程(UI)线程，因此在这里不能进行耗时操作
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void background(Object o) {
        //表示事件处理函数的线程在后台线程，因此不能进行UI操作。如果发布事件的线程是主线程(UI线程)，那么事件处理函数将会开启一个后台线程，如果果发布事件的线程是在后台线程，那么事件处理函数就使用该线程
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void async(Object o) {
        //表示无论事件发布的线程是哪一个，事件处理函数始终会新建一个子线程运行，同样不能进行UI操作
    }

    public void destory() {
        EventBus.getDefault().unregister(this);
    }
}
