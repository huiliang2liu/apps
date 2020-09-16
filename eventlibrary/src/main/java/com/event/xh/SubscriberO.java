package com.event.xh;


import android.os.Handler;

import java.lang.ref.SoftReference;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

class SubscriberO {
    private static final String TAG = "SubscriberO";
    SoftReference softReference;
    List<SubscriberMethod> methodList = new ArrayList<>();

    public SubscriberO(Object o, Handler handler) {
        softReference = new SoftReference(o);
        Method methods[] = o.getClass().getDeclaredMethods();
        for (Method method : methods) {
            if (method.isAnnotationPresent(Subscriber.class)) {
                methodList.add(new SubscriberMethod(method, method.getAnnotation(Subscriber.class).threadMode(),handler));
            }
        }
    }

    public boolean invoke(Object... params) {
        Object o = softReference.get();
        if (o == null)
            return false;
        for (SubscriberMethod method : methodList)
            method.invoke(o, params);
        return true;
    }

}


