package com.plug.load;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import dalvik.system.BaseDexClassLoader;

class LoadDex extends ALoad {
    private static final String TAG = "LoadDex";
    private final static String DEX = "dex";
    private static Field dexElements;
    private Object objectDexElements;
    private File optimizedDirectory;

    LoadDex(Context context) {
        super(context);
        if (dexElements == null) {
            try {
                dexElements = objectPathList.getClass().getDeclaredField("dexElements");
                if (!dexElements.isAccessible())
                    dexElements.setAccessible(true);
            } catch (Exception e) {
                Log.i(TAG, "not found field dexElements");
                return;
            }
        }
        try {
            objectDexElements = dexElements.get(objectPathList);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        optimizedDirectory = context.getDir(DEX, Context.MODE_PRIVATE);
    }

    @Override
    public boolean load(String plugPath) {
        if (dexElements == null || objectDexElements == null)
            return false;
        BaseDexClassLoader baseDexClassLoader = new BaseDexClassLoader(plugPath,
                optimizedDirectory, null, classLoader);
        try {
            Object pathDexElements = dexElements.get(baseDexClassLoader);
            if (pathDexElements.getClass().isArray()) {//数组
                Log.i(TAG, "array");
                List list = new ArrayList();
                for (Object dexElement : (Object[]) objectDexElements) {
                    list.add(dexElement);
                }
                for (Object dexElement : (Object[]) pathDexElements) {
                    list.add(dexElement);
                }
                dexElements.set(objectPathList, list.toArray((Object[]) Array.newInstance((objectDexElements).getClass().getComponentType(), list.size())));
            } else {//列表
                Log.i(TAG, "list");
                List list = new ArrayList();
                for (Object dexElement : (List) objectDexElements) {
                    list.add(dexElement);
                }
                for (Object dexElement : (List) pathDexElements) {
                    list.add(dexElement);
                }
                dexElements.set(objectPathList, list);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
