package com.plug;

import android.app.Activity;
import android.app.Application;
import android.app.Instrumentation;
import android.app.UiAutomation;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;
import android.os.PersistableBundle;
import android.os.TestLooperManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;

public class PlugInstrumentation extends Instrumentation {
    private static final String TAG = "PlugInstrumentation";
    private InstrumentationListener listener;
    private ActivityLifeListener activityLifeListener;
    private Instrumentation instrumentation;

    public PlugInstrumentation(Instrumentation instrumentation,InstrumentationListener listener,ActivityLifeListener activityLifeListener) {
        this.instrumentation = instrumentation;
        this.listener=listener;
        this.activityLifeListener=activityLifeListener;
    }

    @Override
    public Activity newActivity(Class<?> clazz, Context context, IBinder token, Application application, Intent intent, ActivityInfo info, CharSequence title, Activity parent, String id, Object lastNonConfigurationInstance) throws IllegalAccessException, InstantiationException {
        String newclass = listener.transformNew(clazz.getName());
        if (newclass != null && !newclass.isEmpty()) {
            Log.i(TAG, newclass);
            try {
                Class<?> newClass = Class.forName(newclass);
                clazz = newClass;
            } catch (Exception e) {
                Log.i(TAG, String.format("not found class %s", newclass));
            }
        }
        return instrumentation.newActivity(clazz, context, token, application, intent, info, title, parent, id, lastNonConfigurationInstance);
    }

    @Override
    public Activity newActivity(ClassLoader cl, String className, Intent intent) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        String newclass = listener.transformNew(className);
        if (newclass != null && !newclass.isEmpty()) {
            Log.i(TAG, newclass);
            try {
                Class<?> newClass = cl.loadClass(newclass);
                className = newclass;
            } catch (Exception e) {
                Log.i(TAG, String.format("not found class %s", newclass));
            }
        }
        return instrumentation.newActivity(cl, className, intent);
    }

    @Override
    public void callActivityOnCreate(Activity activity, Bundle icicle) {
        activityLifeListener.onCreate(activity);
        instrumentation.callActivityOnCreate(activity, icicle);
    }

    @Override
    public void callActivityOnCreate(Activity activity, Bundle icicle, PersistableBundle persistentState) {
        activityLifeListener.onCreate(activity);
        instrumentation.callActivityOnCreate(activity, icicle, persistentState);
    }

    @Override
    public void callActivityOnDestroy(Activity activity) {
        instrumentation.callActivityOnDestroy(activity);
    }

    @Override
    public void callActivityOnRestoreInstanceState(Activity activity, Bundle savedInstanceState) {
        instrumentation.callActivityOnRestoreInstanceState(activity, savedInstanceState);
    }

    @Override
    public void callActivityOnRestoreInstanceState(Activity activity, Bundle savedInstanceState, PersistableBundle persistentState) {
        instrumentation.callActivityOnRestoreInstanceState(activity, savedInstanceState, persistentState);
    }

    @Override
    public void callActivityOnNewIntent(Activity activity, Intent intent) {
        activityLifeListener.onNewIntent(activity);
        instrumentation.callActivityOnNewIntent(activity, intent);
    }

    @Override
    public void callActivityOnStart(Activity activity) {
        super.callActivityOnStart(activity);
    }

    @Override
    public void callActivityOnRestart(Activity activity) {
        instrumentation.callActivityOnRestart(activity);
    }

    @Override
    public void callActivityOnResume(Activity activity) {
        instrumentation.callActivityOnResume(activity);
    }

    @Override
    public void callActivityOnStop(Activity activity) {
        instrumentation.callActivityOnStop(activity);
    }

    @Override
    public void callActivityOnSaveInstanceState(Activity activity, Bundle outState) {
        instrumentation.callActivityOnSaveInstanceState(activity, outState);
    }

    @Override
    public void callActivityOnSaveInstanceState(Activity activity, Bundle outState, PersistableBundle outPersistentState) {
        instrumentation.callActivityOnSaveInstanceState(activity, outState, outPersistentState);
    }

    @Override
    public void callActivityOnPause(Activity activity) {
        instrumentation.callActivityOnPause(activity);
    }

    @Override
    public void onCreate(Bundle arguments) {
        instrumentation.onCreate(arguments);
    }

    @Override
    public void start() {
        instrumentation.start();
    }

    @Override
    public void onStart() {
        instrumentation.onStart();
    }

    @Override
    public boolean onException(Object obj, Throwable e) {
        return instrumentation.onException(obj, e);
    }

    @Override
    public void sendStatus(int resultCode, Bundle results) {
        instrumentation.sendStatus(resultCode, results);
    }

    @Override
    public void addResults(Bundle results) {
        instrumentation.addResults(results);
    }

    @Override
    public void finish(int resultCode, Bundle results) {
        instrumentation.finish(resultCode, results);
    }

    @Override
    public void setAutomaticPerformanceSnapshots() {
        instrumentation.setAutomaticPerformanceSnapshots();
    }

    @Override
    public void startPerformanceSnapshot() {
        instrumentation.startPerformanceSnapshot();
    }

    @Override
    public void endPerformanceSnapshot() {
        instrumentation.endPerformanceSnapshot();
    }

    @Override
    public void onDestroy() {
        instrumentation.onDestroy();
    }

    @Override
    public Context getContext() {
        return instrumentation.getContext();
    }

    @Override
    public ComponentName getComponentName() {
        return instrumentation.getComponentName();
    }

    @Override
    public Context getTargetContext() {
        return instrumentation.getTargetContext();
    }

    @Override
    public String getProcessName() {
        return instrumentation.getProcessName();
    }

    @Override
    public boolean isProfiling() {
        return instrumentation.isProfiling();
    }

    @Override
    public void startProfiling() {
        instrumentation.startProfiling();
    }

    @Override
    public void stopProfiling() {
        instrumentation.stopProfiling();
    }

    @Override
    public void setInTouchMode(boolean inTouch) {
        instrumentation.setInTouchMode(inTouch);
    }

    @Override
    public void waitForIdle(Runnable recipient) {
        instrumentation.waitForIdle(recipient);
    }

    @Override
    public void waitForIdleSync() {
        instrumentation.waitForIdleSync();
    }

    @Override
    public void runOnMainSync(Runnable runner) {
        instrumentation.runOnMainSync(runner);
    }

    @Override
    public Activity startActivitySync(Intent intent) {
        return instrumentation.startActivitySync(intent);
    }

    @Override
    public Activity startActivitySync( Intent intent,  Bundle options) {
        return instrumentation.startActivitySync(intent, options);
    }

    @Override
    public void addMonitor(ActivityMonitor monitor) {
        instrumentation.addMonitor(monitor);
    }

    @Override
    public ActivityMonitor addMonitor(IntentFilter filter, ActivityResult result, boolean block) {
        return instrumentation.addMonitor(filter, result, block);
    }

    @Override
    public ActivityMonitor addMonitor(String cls, ActivityResult result, boolean block) {
        return instrumentation.addMonitor(cls, result, block);
    }

    @Override
    public boolean checkMonitorHit(ActivityMonitor monitor, int minHits) {
        return instrumentation.checkMonitorHit(monitor, minHits);
    }

    @Override
    public Activity waitForMonitor(ActivityMonitor monitor) {
        return instrumentation.waitForMonitor(monitor);
    }

    @Override
    public Activity waitForMonitorWithTimeout(ActivityMonitor monitor, long timeOut) {
        return instrumentation.waitForMonitorWithTimeout(monitor, timeOut);
    }

    @Override
    public void removeMonitor(ActivityMonitor monitor) {
        instrumentation.removeMonitor(monitor);
    }

    @Override
    public boolean invokeMenuActionSync(Activity targetActivity, int id, int flag) {
        return instrumentation.invokeMenuActionSync(targetActivity, id, flag);
    }

    @Override
    public boolean invokeContextMenuAction(Activity targetActivity, int id, int flag) {
        return instrumentation.invokeContextMenuAction(targetActivity, id, flag);
    }

    @Override
    public void sendStringSync(String text) {
        instrumentation.sendStringSync(text);
    }

    @Override
    public void sendKeySync(KeyEvent event) {
        instrumentation.sendKeySync(event);
    }

    @Override
    public void sendKeyDownUpSync(int key) {
        instrumentation.sendKeyDownUpSync(key);
    }

    @Override
    public void sendCharacterSync(int keyCode) {
        instrumentation.sendCharacterSync(keyCode);
    }

    @Override
    public void sendPointerSync(MotionEvent event) {
        instrumentation.sendPointerSync(event);
    }

    @Override
    public void sendTrackballEventSync(MotionEvent event) {
        instrumentation.sendTrackballEventSync(event);
    }

    @Override
    public Application newApplication(ClassLoader cl, String className, Context context) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        return instrumentation.newApplication(cl, className, context);
    }

    @Override
    public void callApplicationOnCreate(Application app) {
        instrumentation.callApplicationOnCreate(app);
    }

    @Override
    public void callActivityOnPostCreate(Activity activity, Bundle icicle) {
        instrumentation.callActivityOnPostCreate(activity, icicle);
    }

    @Override
    public void callActivityOnPostCreate(Activity activity, Bundle icicle, PersistableBundle persistentState) {
        instrumentation.callActivityOnPostCreate(activity, icicle, persistentState);
    }

    @Override
    public void callActivityOnUserLeaving(Activity activity) {
        instrumentation.callActivityOnUserLeaving(activity);
    }

    @Override
    public void startAllocCounting() {
        instrumentation.startAllocCounting();
    }

    @Override
    public void stopAllocCounting() {
        instrumentation.stopAllocCounting();
    }

    @Override
    public Bundle getAllocCounts() {
        return instrumentation.getAllocCounts();
    }

    @Override
    public Bundle getBinderCounts() {
        return instrumentation.getBinderCounts();
    }

    @Override
    public UiAutomation getUiAutomation() {
        return instrumentation.getUiAutomation();
    }

    @Override
    public UiAutomation getUiAutomation(int flags) {
        return instrumentation.getUiAutomation(flags);
    }

    @Override
    public TestLooperManager acquireLooperManager(Looper looper) {
        return instrumentation.acquireLooperManager(looper);
    }

    public interface InstrumentationListener {
        String transformNew(String newClass);
    }

    public interface ActivityLifeListener {
        void onCreate(Activity activity);

        void onNewIntent(Activity activity);
    }
}
