package com.plug;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Context;
import android.util.Log;

import com.plug.entities.ApkEntity;
import com.plug.load.ILoadApk;
import com.plug.load.LoadImpl;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class Plug implements ILoadApk, PlugInstrumentation.InstrumentationListener, PlugInstrumentation.ActivityLifeListener {
    private static final String TAG = "Plug";
    private LoadImpl loadApk;
    private Instrumentation instrumentation;
    private Object activityThread;
    private List<PlugListener> listeners;

    public Plug(Context context) {
        loadApk = new LoadImpl(context);
        listeners = new ArrayList<>();
        try {
            Class activityThreadClass = Class.forName("android.app.ActivityThread");
            Field sCurrentActivityThreadField = activityThreadClass.getDeclaredField("sCurrentActivityThread");
            if (!sCurrentActivityThreadField.isAccessible())
                sCurrentActivityThreadField.setAccessible(true);
            activityThread = sCurrentActivityThreadField.get(null);
            Field mInstrumentationField = activityThreadClass.getDeclaredField("mInstrumentation");
            if (!mInstrumentationField.isAccessible())
                mInstrumentationField.setAccessible(true);
            instrumentation = new PlugInstrumentation((Instrumentation) mInstrumentationField.get(activityThread), this, this);
            mInstrumentationField.set(activityThread, instrumentation);
        } catch (Exception e) {
            e.printStackTrace();
            Log.i(TAG, "not found android.app.ActivityThread");
        }
    }

    public void registerPlugListener(PlugListener listener) {
        if (listener != null)
            listeners.add(listener);
    }

    public void unRegisterPlugListener(PlugListener listener) {
        if (listener != null)
            listeners.remove(listener);
    }

    @Override
    public ApkEntity name2apkEntity(String packageName) {
        return loadApk.name2apkEntity(packageName);
    }

    @Override
    public boolean load(String plugPath) {
        return loadApk.load(plugPath);
    }

    public void load(String... paths) {
        loadApk.load(paths);
    }

    public void loadSo(String... sos) {
        loadApk.loadSo(sos);
    }

    @Override
    public String transformNew(String newClass) {
        for (PlugListener listener : listeners) {
            String nC = listener.transformNew(newClass);
            if (nC != null)
                return nC;
        }
        return newClass;
    }

    @Override
    public void onCreate(Activity activity) {
        for (PlugListener listener : listeners) {
            listener.onCreate(activity);
        }
    }

    @Override
    public void onNewIntent(Activity activity) {
        for (PlugListener listener : listeners) {
            listener.onNewIntent(activity);
        }
    }

    public interface PlugListener extends PlugInstrumentation.InstrumentationListener, PlugInstrumentation.ActivityLifeListener {

    }
}
