package com.screen;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Application;
import android.app.Instrumentation;
import android.app.UiAutomation;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;
import android.os.PersistableBundle;
import android.os.TestLooperManager;
import android.os.UserHandle;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.KeyEvent;
import android.view.MotionEvent;

import com.screen.entities.ApkEntity;
import com.screen.iface.CancelAdapt;
import com.screen.iface.CustomAdapt;
import com.screen.placeholder.BehindActivity;
import com.screen.placeholder.DefaultActivity;
import com.screen.placeholder.LandscapeActivity;
import com.screen.placeholder.NosensorActivity;
import com.screen.placeholder.PortraitActivity;
import com.screen.placeholder.SLActivity;
import com.screen.placeholder.SPActivity;
import com.screen.placeholder.SensorActivity;
import com.screen.placeholder.UserActivity;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class PlugInstrumentation extends Instrumentation {
    private static Field mInflater;
    private static Method execStartActivity1;
    private static Method execStartActivity2;
    private static Method execStartActivity3;
    public static final String ORIGINAL_ACTIVITY = "original_activity";
    public static final String ORIGINL_PACKAGE = "original_package";
    private static final String TAG = "PlugInstrumentation";
    private InstrumentationListener listener;
    private ActivityLifeListener activityLifeListener;
    private Instrumentation instrumentation;
    private ScreenManager manager;


    static {
        try {
            mInflater = ContextThemeWrapper.class.getDeclaredField("mInflater");
            mInflater.setAccessible(true);
        } catch (Exception e) {
            Log.e(TAG, "获取mInflater字段错误");
        }
        Class cls = Instrumentation.class;
        try {
            execStartActivity1 = cls.getDeclaredMethod("execStartActivity", Context.class, IBinder.class, IBinder.class
                    , Activity.class, Intent.class, int.class, Bundle.class);
            execStartActivity1.setAccessible(true);
        } catch (Exception e) {
            Log.e(TAG, "获取execStartActivity1方法错误");
        }
        try {
            execStartActivity2 = cls.getDeclaredMethod("execStartActivity", Context.class, IBinder.class, IBinder.class
                    , String.class, Intent.class, int.class, Bundle.class);
            execStartActivity2.setAccessible(true);
        } catch (Exception e) {
            Log.e(TAG, "获取execStartActivity2方法错误");
        }
        if (Build.VERSION.SDK_INT > 16)
            try {
                execStartActivity3 = cls.getDeclaredMethod("execStartActivity", Context.class, IBinder.class, IBinder.class
                        , String.class, Intent.class, int.class, Bundle.class, UserHandle.class);
                execStartActivity3.setAccessible(true);
            } catch (Exception e) {
                Log.e(TAG, "获取execStartActivity3方法错误");
            }
    }

    public PlugInstrumentation(Instrumentation instrumentation, InstrumentationListener listener, ActivityLifeListener activityLifeListener) {
        this.instrumentation = instrumentation;
        this.listener = listener;
        this.activityLifeListener = activityLifeListener;
        this.manager = (ScreenManager) listener;
    }

    @Override
    public Activity newActivity(Class<?> clazz, Context context, IBinder token, Application application, Intent intent, ActivityInfo info, CharSequence title, Activity parent, String id, Object lastNonConfigurationInstance) throws IllegalAccessException, InstantiationException {
        String newclass = intent.getStringExtra(ORIGINAL_ACTIVITY);
        intent.removeExtra(ORIGINAL_ACTIVITY);
        if (newclass == null || newclass.isEmpty())
            newclass = clazz.getName();
        newclass = listener.transformNew(newclass);
        if (newclass != null && !newclass.isEmpty()) {
            Log.i(TAG, newclass);
            try {
                Class<?> newClass = Class.forName(newclass);
                clazz = newClass;
            } catch (Exception e) {
                Log.i(TAG, String.format("not found class %s", newclass));
            }
        }
        Activity activity = instrumentation.newActivity(clazz, context, token, application, intent, info, title, parent, id, lastNonConfigurationInstance);
        replaceLayout(activity);
        return activity;
    }

    @Override
    public Activity newActivity(ClassLoader cl, String className, Intent intent) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        String newclass = intent.getStringExtra(ORIGINAL_ACTIVITY);
        intent.removeExtra(ORIGINAL_ACTIVITY);
        if (newclass == null || newclass.isEmpty())
            newclass = className;
        newclass = listener.transformNew(newclass);
        Activity activity = null;
        if (newclass != null && !newclass.isEmpty()) {
            Log.i(TAG, newclass);
            try {
//                Class<?> newClass = Class.forName(newclass);
                className = newclass;
//                activity = (Activity) newClass.newInstance();
            } catch (Exception e) {
                Log.i(TAG, String.format("not found class %s", newclass));
            }
        }
        if (activity == null)
            activity = instrumentation.newActivity(cl, className, intent);
        replaceLayout(activity);
        return activity;
    }

    private void replaceLayout(Activity activity) {
        try {
            AppLayoutInflater layoutInflater = new AppLayoutInflater(activity);
            mInflater.set(activity, layoutInflater);
        } catch (Exception e) {
            Log.e(TAG, "替换layoutInflater出错了", e);
        }
    }

    @Override
    public void callActivityOnCreate(Activity activity, Bundle icicle) {
        activityLifeListener.onCreate(activity);
        instrumentation.callActivityOnCreate(activity, icicle);
    }

    @TargetApi(21)
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

    @TargetApi(21)
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

    @TargetApi(21)
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

    @TargetApi(26)
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

    @TargetApi(26)
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

    @TargetApi(28)
    @Override
    public Activity startActivitySync(Intent intent, Bundle options) {
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

    @TargetApi(21)
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

    @TargetApi(18)
    @Override
    public UiAutomation getUiAutomation() {
        return instrumentation.getUiAutomation();
    }

    @TargetApi(24)
    @Override
    public UiAutomation getUiAutomation(int flags) {
        return instrumentation.getUiAutomation(flags);
    }

    @TargetApi(26)
    @Override
    public TestLooperManager acquireLooperManager(Looper looper) {
        return instrumentation.acquireLooperManager(looper);
    }


    public ActivityResult execStartActivity(
            Context who, IBinder contextThread, IBinder token, Activity target,
            Intent intent, int requestCode, Bundle options) {
        Log.d(TAG, "execStartActivity1");
        replace(intent);
        try {
            return (ActivityResult) execStartActivity1.invoke(instrumentation, who, contextThread, token, target, intent, requestCode, options);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public ActivityResult execStartActivity(
            Context who, IBinder contextThread, IBinder token, String target,
            Intent intent, int requestCode, Bundle options) {
        Log.d(TAG, "execStartActivity2");
        replace(intent);
        try {
            return (ActivityResult) execStartActivity2.invoke(instrumentation, who, contextThread, token, target, intent, requestCode, options);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public ActivityResult execStartActivity(
            Context who, IBinder contextThread, IBinder token, String resultWho,
            Intent intent, int requestCode, Bundle options, UserHandle user) {
        Log.d(TAG, "execStartActivity3");
        replace(intent);
        try {
            return (ActivityResult) execStartActivity3.invoke(instrumentation, who, contextThread, token, resultWho, intent, requestCode, options, user);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private final static String LANDSCAPE = LandscapeActivity.class.getName();
    private final static String PORTRAIT = PortraitActivity.class.getName();
    private final static String USER = UserActivity.class.getName();
    private final static String BEHIND = BehindActivity.class.getName();
    private final static String SENSOR = SensorActivity.class.getName();
    private final static String NOSENSOR = NosensorActivity.class.getName();
    private final static String SENSOR_LANDSCAPE = SLActivity.class.getName();
    private final static String SENSOR_PORTRAIT = SPActivity.class.getName();
    private final static String DEFAULT = DefaultActivity.class.getName();

    private void replace(Intent intent) {
        Log.d(TAG, "replace");
        ResolveInfo resolveInfo = manager.packageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);
        if (resolveInfo != null) {
            Log.d(TAG, "不需要替换");
            return;
        }
        Log.d(TAG, "需要替换");
        List<ApkEntity> apkEntities = manager.apkEntities();
        if (apkEntities != null && apkEntities.size() >= 0) {
            for (ApkEntity apkEntity : apkEntities) {
                resolveInfo = apkEntity.packageManage.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);
                if (resolveInfo != null) {
                    intent.putExtra(ORIGINAL_ACTIVITY, resolveInfo.activityInfo.name);
                    int so = resolveInfo.activityInfo.screenOrientation;
                    if (ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE == so)
                        intent.setClassName(manager.packageInfo.packageName, LANDSCAPE);
                    else if (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT == so)
                        intent.setClassName(manager.packageInfo.packageName, PORTRAIT);
                    else if (ActivityInfo.SCREEN_ORIENTATION_USER == so)
                        intent.setClassName(manager.packageInfo.packageName, USER);
                    else if (ActivityInfo.SCREEN_ORIENTATION_BEHIND == so)
                        intent.setClassName(manager.packageInfo.packageName, BEHIND);
                    else if (ActivityInfo.SCREEN_ORIENTATION_SENSOR == so)
                        intent.setClassName(manager.packageInfo.packageName, SENSOR);
                    else if (ActivityInfo.SCREEN_ORIENTATION_NOSENSOR == so)
                        intent.setClassName(manager.packageInfo.packageName, NOSENSOR);
                    else if (ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE == so)
                        intent.setClassName(manager.packageInfo.packageName, SENSOR_LANDSCAPE);
                    else if (ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT == so)
                        intent.setClassName(manager.packageInfo.packageName, SENSOR_PORTRAIT);
                    else
                        intent.setClassName(manager.packageInfo.packageName, DEFAULT);
                    intent.putExtra(ORIGINL_PACKAGE, apkEntity.mPackageName);
                    Log.d(TAG, "替换成功");
                    return;
                }
            }
            Log.d(TAG, "没有找到替换的内容");
        }
        Log.e(TAG, "没注册的activity");
        ComponentName componentName = intent.getComponent();
        if (componentName != null) {
            intent.setClassName(componentName.getPackageName(), manager.placeholderActivity);
            intent.putExtra(ORIGINAL_ACTIVITY, componentName.getClassName());
        }
    }

    public interface InstrumentationListener {
        String transformNew(String newClass);
    }

    public interface ActivityLifeListener {
        void onCreate(Activity activity);

        void onNewIntent(Activity activity);
    }
}
