package com.loader;

import android.content.Context;
import android.util.Log;


import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import dalvik.system.BaseDexClassLoader;

class DexLoader extends BaseDexClassLoader {
    private static final String TAG = "DexLoader";
    private final static String DEX = "dex";
    private final static Class<BaseDexClassLoader> CLS = BaseDexClassLoader.class;
    private static Field pathList;
    private static Field dexElements;

    static {
        try {
            pathList = CLS.getDeclaredField("pathList");
            pathList.setAccessible(true);
        } catch (Exception e) {
            Log.e(TAG, "获取变量pathList错误", e);
        }
    }

    public DexLoader(String dexPath, ClassLoader parent, Context context) {
        super(dexPath, context.getDir(DEX, Context.MODE_PRIVATE), null, parent);
        try {
            Object basePathList = pathList.get(this);
            Object pathPathList = pathList.get(parent);
            if (dexElements == null) {
                dexElements = basePathList.getClass().getDeclaredField("dexElements");
                if (!dexElements.isAccessible())
                    dexElements.setAccessible(true);
            }
            Object baseDexElements = dexElements.get(basePathList);
            Object pathDexElements = dexElements.get(pathPathList);
            if (pathDexElements.getClass().isArray()) {//数组
                List list = new ArrayList();
                for (Object dexElement : (Object[]) baseDexElements) {
                    list.add(dexElement);
                }
                for (Object dexElement : (Object[]) pathDexElements) {
                    list.add(dexElement);
                }
                dexElements.set(pathPathList, list.toArray((Object[]) Array.newInstance((baseDexElements).getClass().getComponentType(), list.size())));
            } else {//列表
                ((List) pathDexElements).addAll((List) baseDexElements);
            }
        } catch (Exception e) {
            Log.e(TAG, "设置加载目录错误", e);
        }
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        return super.findClass(name);
    }

    @Override
    protected URL findResource(String name) {
        return super.findResource(name);
    }

    @Override
    protected Enumeration<URL> findResources(String name) {
        return super.findResources(name);
    }

    @Override
    public String findLibrary(String libname) {
        return null;
    }

    @Override
    protected synchronized Package getPackage(String name) {
        return super.getPackage(name);
    }

}
