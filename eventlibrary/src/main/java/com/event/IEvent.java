package com.event;


public interface IEvent {
    void register(Object o);

    void unregister(Object o);


    void post(Object... params);
}
