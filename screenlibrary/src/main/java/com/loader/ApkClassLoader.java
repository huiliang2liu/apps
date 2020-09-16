package com.loader;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import dalvik.system.BaseDexClassLoader;

/**
 * User：liuhuiliang
 * Date：2019-12-20
 * Time：12:34
 * Descripotion：
 * {@hide}
 */
public class ApkClassLoader extends ClassLoader {
    private static final String TAG = "ApkClassLoader";
    private static Field mPackages;
    private static Method getClassLoader;
    private static Field mClassLoader;
    private static Method findLibrary;
    //    private static Field instance;
    private Object loadedApk;
    private List<String> soPaths = new Vector<>();


    static {
        try {
            Class cls = Class.forName("android.app.ActivityThread");
            mPackages = cls.getDeclaredField("mPackages");
            mPackages.setAccessible(true);
        } catch (Exception e) {
            Log.d(TAG, "获取字段mPackages错误", e);
        }
        try {
            Class loadedApkClass = Class.forName("android.app.LoadedApk");
            try {
                getClassLoader = loadedApkClass.getDeclaredMethod("getClassLoader");
                getClassLoader.setAccessible(true);
            } catch (Exception e) {
                Log.d(TAG, "获取方法getClassLoader错误", e);
            }
            try {
                mClassLoader = loadedApkClass.getDeclaredField("mClassLoader");
                mClassLoader.setAccessible(true);
            } catch (Exception e) {
                Log.d(TAG, "获取字段mClassLoader错误", e);
            }
        } catch (Exception e) {
            Log.d(TAG, "获取类LoadedApk错误", e);
        }
        try {
            findLibrary = ClassLoader.class.getDeclaredMethod("findLibrary", String.class);
            findLibrary.setAccessible(true);
        } catch (Exception e) {
            Log.d(TAG, "获取方法findLibrary错误", e);
        }
//        try {
//            Class bootClassLoader = Class.forName("java.lang.ClassLoader$BootClassLoader");
//            instance = bootClassLoader.getDeclaredField("instance");
//            instance.setAccessible(true);
//        } catch (Exception e) {
//            Log.d(TAG, "获取字段instance错误", e);
//        }
    }

    private ClassLoader mBase;
    private List<BaseDexClassLoader> classLoaders = new Vector<>();
    private Context context;

    public ApkClassLoader(Context context) {
        this.context = context;
        try {
            Class activityThreadClass = Class.forName("android.app.ActivityThread");
            Field sCurrentActivityThreadField = activityThreadClass.getDeclaredField("sCurrentActivityThread");
            if (!sCurrentActivityThreadField.isAccessible())
                sCurrentActivityThreadField.setAccessible(true);
            Object activityThread = sCurrentActivityThreadField.get(null);
            loadedApk = ((WeakReference) ((Map) mPackages.get(activityThread)).get(context.getPackageName())).get();
            mBase = (ClassLoader) getClassLoader.invoke(loadedApk);
            mClassLoader.set(loadedApk, this);
        } catch (Exception e) {
            e.printStackTrace();
            Log.i(TAG, "not found android.app.ActivityThread");
        }
        if (mBase == null)
            mBase = context.getClassLoader();
        Thread.currentThread().setContextClassLoader(this);
//        try {
//            instance.set(null,this);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    public void addClassLoader(BaseDexClassLoader classLoader) {
        if (classLoader != null)
            classLoaders.add(classLoader);
    }

    public void addSo(String... paths) {
        if (paths == null || paths.length <= 0)
            return;
        for (String path : paths)
            soPaths.add(path);
    }

    public void addDex(String... paths) {
        if (paths == null || paths.length <= 0)
            return;
        StringBuffer sb = new StringBuffer();
        for (String path : paths) {
            sb.append(path).append(":");
            addClassLoader(new DexLoader(sb.substring(0, sb.length() - 1), mBase, context));
        }
    }



    public void addApk(String... paths) {
        if (paths == null || paths.length <= 0)
            return;
        for (String path : paths)
            addClassLoader(new ApkLoader(path, mBase, context));
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        try {
            Class cl = mBase.loadClass(name);
            if (cl != null)
                return cl;
        } catch (Exception e) {

        }
        for (ClassLoader loader : classLoaders) {
            try {
                Class cls = loader.loadClass(name);
                if (cls != null)
                    return cls;
            } catch (Exception e) {
            }
        }
        throw new ClassNotFoundException(name);
    }

    @Override
    public String findLibrary(String libname) {
        Log.d(TAG, libname);
        String fileName = null;
        if (soPaths.size() > 0)
            for (String path : soPaths)
                if (path.endsWith(String.format("lib%s.so", libname))) {
                    Log.e(TAG, String.format("外部添加的so：%s", path));
                    return path;
                }
        if (classLoaders.size() > 0)
            for (BaseDexClassLoader dexClassLoader : classLoaders) {
                fileName = dexClassLoader.findLibrary(libname);
                if (fileName != null && !fileName.isEmpty()) {
                    Log.d(TAG, String.format("插件中的so：%s", fileName));
                    return fileName;
                }
            }
        try {
            fileName = (String) findLibrary.invoke(mBase, libname);
            if (fileName != null && !fileName.isEmpty()) {
                Log.d(TAG, String.format("so fileName ", fileName));
                return fileName;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fileName;
    }

}
