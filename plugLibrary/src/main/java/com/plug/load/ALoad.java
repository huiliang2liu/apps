package com.plug.load;

import android.content.Context;
import android.util.Log;

import java.lang.reflect.Field;

import dalvik.system.BaseDexClassLoader;

abstract class ALoad implements ILoad {
    private static final String TAG = "ALoad";
    protected static Field pathList;
    private final static Class<BaseDexClassLoader> CLS = BaseDexClassLoader.class;
    protected Object objectPathList;
    protected ClassLoader classLoader;

    {
        try {
            pathList = CLS.getDeclaredField("pathList");
            if (!pathList.isAccessible())
                pathList.setAccessible(true);
        } catch (Exception e) {
            Log.e(TAG, "not found field pathList");
        }
    }

    ALoad(Context context) {
        classLoader = context.getClassLoader();
        try {
            objectPathList = pathList.get(context.getClassLoader());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
