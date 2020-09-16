package com.event.xh;


import android.os.Handler;
import android.util.Log;

import java.lang.reflect.Method;

class SubscriberMethod {
    private static final String TAG = "SubscriberMethod";
    private Method method;
    private Class[] classes;
    private Event.ThreadMode mode;
    private Handler handler;

    public SubscriberMethod(Method method, Event.ThreadMode mode, Handler handler) {
        this.method = method;
        classes = method.getParameterTypes();
        this.mode = mode;
        this.handler=handler;
    }

    public void invoke(Object o, Object... params) {
        if (classes == null && params == null) {
            invoke1(o, params);
        } else if (classes != null && params != null && classes.length == params.length) {
            for (int i = 0; i < classes.length; i++) {
                Object o1 = params[i];
                if (o1 == null)
                    continue;
                if (!o1.getClass().isAssignableFrom(classes[i])) {
                    Log.d(TAG, "参数类型不匹配");
                    return;
                }
                invoke1(o, params);
            }
        } else {
            Log.d(TAG, "参数不匹配");
        }
    }

    private void invoke1(final Object o, final Object... params) {
        Log.d(TAG, String.format("执行方法%s", mode));
        switch (mode) {
            case MAIN:
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            method.invoke(o, params);
                        } catch (Exception e) {
                            Log.d(TAG, "方法执行错误", e);
                        }
                    }
                });
                break;
            case CURRENT:
                try {
                    method.invoke(o, params);
                } catch (Exception e) {
                    Log.d(TAG, "方法执行错误", e);
                }
                break;
            case ASYNC:
                new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        try {
                            method.invoke(o, params);
                        } catch (Exception e) {
                            Log.d(TAG, "方法执行错误", e);
                        }
                    }
                }.start();
                break;
        }

    }
}
